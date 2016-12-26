/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.dsg.DsgData;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.ferret.SocatTool;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * Regenerates the full-data DSG files with the current data values 
 * in the DSG files but with the current metadata values in the OME 
 * XML files.  The decimated DSG files are then regenerated from the
 * full-data DSG file.
 *  
 * @author Karl Smith
 */
public class RegenerateDsgs {

	DataFileHandler dataHandler;
	DsgNcFileHandler dsgHandler;
	MetadataFileHandler metaHandler;
	KnownDataTypes knownMetadataTypes;
	KnownDataTypes knownDataFileTypes;
	DatabaseRequestHandler dbHandler;
	FerretConfig ferretConfig;

	/**
	 * Regenerate DSG files using the given configuration data.
	 * 
	 * @param configStore
	 * 		configuration data to use
	 */
	public RegenerateDsgs(DashboardConfigStore configStore) {
		dataHandler = configStore.getDataFileHandler();
		dsgHandler = configStore.getDsgNcFileHandler();
		metaHandler = configStore.getMetadataFileHandler();
		knownMetadataTypes = configStore.getKnownMetadataTypes();
		knownDataFileTypes = configStore.getKnownDataFileTypes();
		dbHandler = configStore.getDatabaseRequestHandler();
		ferretConfig = configStore.getFerretConfig();
	}

	/**
	 * Regenerate the DSG files for the given dataset.
	 * 
	 * @param datasetId
	 * 		regenerate the DSG files the the dataset with this ID
	 * @param forceIt
	 * 		if true, always regenerate the DSG files;
	 * 		if false, regenerate the DSG files only if the metadata has changed
	 * @return
	 * 		if the DSG files were regenerated
	 * @throws IllegalArgumentException
	 * 		if there was a problem regenerating the DSG files
	 */
	public boolean regenerateDsgFiles(String datasetId, boolean forceIt) throws IllegalArgumentException {
		boolean updateIt = forceIt;
		String stdId = DashboardServerUtils.checkDatasetID(datasetId);
		DsgNcFile fullDataDsg;
		ArrayList<DsgData> dataVals;

		DsgMetadata updatedMeta;
		try {
			// Read the current metadata in the full-data DSG file
			fullDataDsg = dsgHandler.getDsgNcFile(stdId);
			ArrayList<String> missing = fullDataDsg.readMetadata(knownMetadataTypes);
			if ( ! missing.isEmpty() )
				throw new IllegalArgumentException("Unexpected metadata fields missing from the DSG file: " + missing);
			missing = fullDataDsg.readData(knownDataFileTypes);
			if ( ! missing.isEmpty() )
				throw new IllegalArgumentException("Unexpected data fields missing from the DSG file: " + missing);
			DsgMetadata fullDataMeta = fullDataDsg.getMetadata();
			dataVals = fullDataDsg.getDataList();

			// Get the metadata in the OME XML file
			DashboardOmeMetadata omeMData = new DashboardOmeMetadata(
					metaHandler.getMetadataInfo(stdId, DashboardUtils.OME_FILENAME), metaHandler);
			updatedMeta = omeMData.createDsgMetadata();

			if ( ! fullDataMeta.equals(updatedMeta) )
				updateIt = true;
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems reading the dataset " + stdId + ": " + ex.getMessage());
		}

		if ( updateIt ) {
			try {
				// Regenerate the DSG file with the updated metadata
				fullDataDsg.create(updatedMeta, dataVals);
				// Call Ferret to add data variables
				SocatTool tool = new SocatTool(ferretConfig);
				ArrayList<String> scriptArgs = new ArrayList<String>(1);
				scriptArgs.add(fullDataDsg.getPath());
				tool.init(scriptArgs, stdId, FerretConfig.Action.COMPUTE);
				tool.run();
				if ( tool.hasError() )
					throw new IllegalArgumentException("Failure in adding computed variables for " + 
							stdId + ": " + tool.getErrorMessage());

			} catch ( Exception ex ) {
				throw new IllegalArgumentException("Problems regenerating the full-data DSG files for " + 
							stdId + ": " + ex.getMessage());
			}
			try {
				// Regenerate the decimated-data DSG file 
				dsgHandler.decimateCruise(stdId);
			} catch ( Exception ex ) {
				throw new IllegalArgumentException("Problems regenerating the decimated-data DSG files for " + 
							stdId + ": " + ex.getMessage());
			}
		}
		return updateIt;		
	}

	/**
	 * Flag ERDDAP that the full-data and decimated-data DSG files have changed
	 */
	public void flagErddap() {
		dsgHandler.flagErddap(true, true);
	}

	/**
	 * @param args
	 * 		IDsFile - update DSG files of the datasets with these IDs
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  IDsFile  Always");
			System.err.println();
			System.err.println("Regenerates the full-data DSG files with the current data values ");
			System.err.println("in the DSG files but with the current metadata values in the OME "); 
			System.err.println("XML files.  The decimated DSG files are then regenerated from the ");
			System.err.println("full-data DSG file.  The default dashboard configuration is used ");
			System.err.println("for this process.  If Always is T or True, this regeneration always "); 
			System.err.println("occurs; otherwise if only occurs if the metadata has changed. "); 
			System.err.println();
			System.exit(1);
		}

		String idsFilename = args[0];
		boolean always = false;
		if ( "T".equals(args[1]) || "True".equals(args[1]) )
			always = true;

		// Get the IDs of the datasets to update
		TreeSet<String> idsSet = new TreeSet<String>();
		try {
			BufferedReader idsReader = new BufferedReader(new FileReader(idsFilename));
			try {
				String dataline = idsReader.readLine();
				while ( dataline != null ) {
					dataline = dataline.trim();
					if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
						idsSet.add(dataline);
					dataline = idsReader.readLine();
				}
			} finally {
				idsReader.close();
			}
		} catch (Exception ex) {
			System.err.println("Error reading dataset IDs from " + idsFilename + ": " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		RegenerateDsgs regenerator = new RegenerateDsgs(configStore);

		boolean changed = false;
		boolean success = true;
		try {
			// update each of the datasets
			for ( String datasetId : idsSet ) {
				try {
					if ( regenerator.regenerateDsgFiles(datasetId, always) ) {
						System.err.println("Regenerated the DSG files for " + datasetId);
						changed = true;
					}
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
					success = false;
				}
			}
			if ( changed ) {
				regenerator.flagErddap();
			}
		} finally {
			DashboardConfigStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
