/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Regenerates the full-data DSG files with the current data values 
 * in the DSG files but with the current metadata values in the OME 
 * XML files.  The decimated DSG files are then regenerated from the
 * full-data DSG file.
 *  
 * @author Karl Smith
 */
public class RegenerateDsgs {

	CruiseFileHandler cruiseHandler;
	DsgNcFileHandler dsgHandler;
	MetadataFileHandler metaHandler;
	DatabaseRequestHandler dbHandler;
	FerretConfig ferretConfig;

	/**
	 * Regenerate DSG files using the given configuration data.
	 * 
	 * @param configStore
	 * 		configuration data to use
	 */
	public RegenerateDsgs(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getCruiseFileHandler();
		dsgHandler = configStore.getDsgNcFileHandler();
		metaHandler = configStore.getMetadataFileHandler();
		dbHandler = configStore.getDatabaseRequestHandler();
		ferretConfig = configStore.getFerretConfig();
	}

	/**
	 * Regenerate the DSG files for the given dataset.
	 * 
	 * @param expocode
	 * 		regenerate the DSG files the the dataset with this expocode
	 * @param forceIt
	 * 		if true, always regenerate the DSG files;
	 * 		if false, regenerate the DSG files only if the metadata has changed
	 * @return
	 * 		if the DSG files were regenerated
	 * @throws IllegalArgumentException
	 * 		if there was a problem regenerating the DSG files
	 */
	public boolean regenerateDsgFiles(String expocode, boolean forceIt) throws IllegalArgumentException {
		boolean updateIt = forceIt;
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		CruiseDsgNcFile fullDataDsg;
		ArrayList<SocatCruiseData> dataVals;
		SocatMetadata updatedMeta;
		try {
			// Get just the filenames from the set of addition document
			DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(upperExpo);
			TreeSet<String> addlDocs = new TreeSet<String>();
			for ( String docInfo : cruise.getAddlDocs() ) {
				addlDocs.add(DashboardMetadata.splitAddlDocsTitle(docInfo)[0]);
			}

			// Read the current metadata in the full-data DSG file
			fullDataDsg = dsgHandler.getDsgNcFile(upperExpo);
			ArrayList<String> missing = fullDataDsg.read(false);
			if ( ! missing.isEmpty() )
				throw new IllegalArgumentException("Unexpected values missing from the DSG file: " + missing);
			SocatMetadata fullDataMeta = fullDataDsg.getMetadata();
			dataVals = fullDataDsg.getDataList();

			// Get the QC flag and SOCAT version from the database
			Character qcFlag = dbHandler.getQCFlag(upperExpo);
			String qcStatus = SocatQCEvent.FLAG_STATUS_MAP.get(qcFlag);
			String socatVersionStatus = dbHandler.getSocatVersionStatus(upperExpo);
			if ( socatVersionStatus.isEmpty() )
				throw new IllegalArgumentException("Unable to get the version and status from the database");
			String socatVersion = socatVersionStatus.substring(0, socatVersionStatus.length() - 1);

			// Update (but do not commit) the cruise info version number and QC status if not correct 
			if ( ! ( socatVersion.equals(cruise.getVersion()) && qcStatus.equals(cruise.getQcStatus()) ) ) {
				cruise.setVersion(socatVersion);
				cruise.setQcStatus(qcStatus);
				cruiseHandler.saveCruiseInfoToFile(cruise, null);
			}

			// Get the metadata in the OME XML file
			DashboardOmeMetadata omeMData = new DashboardOmeMetadata(
					metaHandler.getMetadataInfo(upperExpo, DashboardMetadata.OME_FILENAME), metaHandler);
			// Update (but do not commit) the metadata info version number if not correct
			if ( ! socatVersion.equals(omeMData.getVersion()) ) {
				omeMData.setVersion(socatVersion);
				metaHandler.saveMetadataInfo(omeMData, null);
			}
			updatedMeta = omeMData.createSocatMetadata(socatVersionStatus, addlDocs, qcFlag.toString());
			if ( ! fullDataMeta.equals(updatedMeta) )
				updateIt = true;
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems reading the dataset " + upperExpo + ": " + ex.getMessage());
		}

		if ( updateIt ) {
			try {
				// Change date/time of old dataset problem points to something valid, but WOCE them as bad
				if ( "49K619871028".equals(upperExpo) ) {
					for ( SocatCruiseData data : dataVals ) {
						if ( (data.getMonth() == 11) && (data.getDay() > 30) ) {
							// 11-31 in 49K619871028
							data.setMonth(12);
							data.setDay(1);
							data.setWoceCO2Water(SocatWoceEvent.WOCE_BAD);
						}
					}
				}
				else if ( "49NB19881228".equals(upperExpo) ) {
					for ( SocatCruiseData data : dataVals ) {
						if ( (data.getMonth() == 2) && (data.getDay() > 29) ) {
							// 2-31 in 49NB19881228
							data.setMonth(3);
							data.setDay(1);
							data.setWoceCO2Water(SocatWoceEvent.WOCE_BAD);
						}
					}
				}
				else if ( "74JC20061024".equals(upperExpo) ) {
					for ( SocatCruiseData data : dataVals ) {
						if ( data.getMinute() > 59 ) {
							// 12:99 in 74JC20061024
							data.setMinute(59);
							data.setWoceCO2Water(SocatWoceEvent.WOCE_BAD);
						}
					}
				}
				else if ( "77FF20020226".equals(upperExpo) ) {
					for ( SocatCruiseData data : dataVals ) {
						if ( data.getSecond() >= 60.0 ) {
							// 13:54:60 in 77FF20020226
							data.setSecond(59.99);
							data.setWoceCO2Water(SocatWoceEvent.WOCE_BAD);
						}
					}
				}
				// Regenerate the DSG file with the updated metadata
				fullDataDsg.create(updatedMeta, dataVals);
				// Call Ferret to add lon360 and tmonth (calculated data should be the same)
				SocatTool tool = new SocatTool(ferretConfig);
				ArrayList<String> scriptArgs = new ArrayList<String>(1);
				scriptArgs.add(fullDataDsg.getPath());
				tool.init(scriptArgs, upperExpo, FerretConfig.Action.COMPUTE);
				tool.run();
				if ( tool.hasError() )
					throw new IllegalArgumentException("Failure in adding computed variables: " + 
							tool.getErrorMessage());
			} catch ( Exception ex ) {
				throw new IllegalArgumentException("Problems regenerating the full-data DSG files for " + 
							upperExpo + ": " + ex.getMessage());
			}
			try {
				// Regenerate the decimated-data DSG file 
				dsgHandler.decimateCruise(upperExpo);
			} catch ( Exception ex ) {
				throw new IllegalArgumentException("Problems regenerating the decimated-data DSG files for " + 
							upperExpo + ": " + ex.getMessage());
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
	 * 		ExpocodesFile - update DSG files of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  ExpocodesFile  Always");
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

		String expocodesFilename = args[0];
		boolean always = false;
		if ( "T".equals(args[1]) || "True".equals(args[1]) )
			always = true;

		// Get the expocodes of the datasets to update
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
			for ( String expocode : allExpocodes ) {
				try {
					if ( regenerator.regenerateDsgFiles(expocode, always) ) {
						System.err.println("Regenerated the DSG files for " + expocode);
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
			configStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
