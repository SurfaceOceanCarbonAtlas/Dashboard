/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Updates the full-data and decimated-data DSG files 
 * with the current values in the OME metadata XML files.
 * 
 * @author Karl Smith
 */
public class UpdateDsgMetadata {

	/**
	 * @param args
	 * 		ExpocodesFile - update dashboard status of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Updates the metadata in the full-data and decimated-data DSG files ");
			System.err.println("with the current values in the OME metadata XML files for cruises "); 
			System.err.println("specified in ExpocodesFile.  The default dashboard configuration ");
			System.err.println("is used for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

		boolean success = true;
		boolean changed = false;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			// Get the expocode of the cruises to update
			TreeSet<String> allExpocodes = new TreeSet<String>();
			try {
				BufferedReader expoReader = new BufferedReader(new FileReader(expocodesFilename));
				try {
					String dataline = expoReader.readLine();
					while ( dataline != null ) {
						dataline = dataline.trim();
						if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
							allExpocodes.add(dataline);
						dataline = expoReader.readLine();
					}
				} finally {
					expoReader.close();
				}
			} catch (Exception ex) {
				System.err.println("Error getting expocodes from " + 
						expocodesFilename + ": " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
			DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
			MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
			FerretConfig ferretConfig = configStore.getFerretConfig();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				try {

					// Get just the filenames from the set of addition document
					DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
					TreeSet<String> addlDocs = new TreeSet<String>();
					for ( String docInfo : cruise.getAddlDocs() ) {
						addlDocs.add(DashboardMetadata.splitAddlDocsTitle(docInfo)[0]);
					}

					// Read the current metadata in the full-data DSG file
					CruiseDsgNcFile fullDataDsg = dsgHandler.getDsgNcFile(expocode);
					ArrayList<String> missing = fullDataDsg.read(true);
					if ( ! missing.isEmpty() )
						throw new RuntimeException("Unexpected values missing from the DSG file: " + missing);
					SocatMetadata fullDataMeta = fullDataDsg.getMetadata();

					// Get the metadata in the OME XML file
					DashboardOmeMetadata omeMData = new DashboardOmeMetadata(
							metaHandler.getMetadataInfo(expocode, DashboardMetadata.OME_FILENAME), metaHandler);
					SocatMetadata updatedMeta = omeMData.createSocatMetadata(
							fullDataMeta.getSocatVersion(), addlDocs, fullDataMeta.getQcFlag());

					// Check if there are any changes in the metadata
					if ( ! fullDataMeta.equals(updatedMeta) ) {
						// Just re-create the DSG file with the updated metadata
						missing = fullDataDsg.read(false);
						if ( ! missing.isEmpty() )
							throw new RuntimeException("Unexpected values missing from the DSG file: " + missing);
						try {
							ArrayList<SocatCruiseData> dataVals = fullDataDsg.getDataList();
							fullDataDsg.create(updatedMeta, dataVals);
							// Call Ferret to add lon360 and tmonth (calculated data should be the same)
							SocatTool tool = new SocatTool(ferretConfig);
							ArrayList<String> scriptArgs = new ArrayList<String>(1);
							scriptArgs.add(fullDataDsg.getPath());
							tool.init(scriptArgs, expocode, FerretConfig.Action.COMPUTE);
							tool.run();
							if ( tool.hasError() )
								throw new IllegalArgumentException(expocode + 
										": Failure adding computed variables: " + tool.getErrorMessage());
							// Re-create the decimated-data DSG file 
							dsgHandler.decimateCruise(expocode);
							System.out.println("Updated metadata in the DSG files for " + expocode);
							changed = true;
						} catch ( Exception ex ) {
							fullDataDsg.delete();
							System.err.println("Problems re-creating the DSG files for " + 
									expocode + ": " + ex.getMessage());
							success = false;
							continue;
						}
					}

				} catch ( Exception ex ) {
					System.err.println("Problems working with the full-data DSG file for " + 
							expocode + ": " + ex.getMessage());
					success = false;
					continue;
				}
			}
			if ( changed ) {
				dsgHandler.flagErddap(true, true);
			}
		} finally {
			configStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
