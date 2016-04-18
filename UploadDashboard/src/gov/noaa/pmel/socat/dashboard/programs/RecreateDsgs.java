/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseChecker;
import gov.noaa.pmel.socat.dashboard.actions.CruiseModifier;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Recreates the DSG files, including the SanityChecker messages files, using the current 
 * cruise data file.  WOCE flags that still match are restored in the DSG files.
 * Does not make any QC or WOCE flag changes in the database.  This was written to fix
 * column types that are not involved in the calculation of fCO2_rec.
 * 
 * @author Karl Smith
 */
public class RecreateDsgs {

	DashboardConfigStore confStore;
	CruiseFileHandler cruiseHandler;
	CruiseChecker cruiseChecker;
	DsgNcFileHandler dsgHandler;
	MetadataFileHandler metaHandler;
	DatabaseRequestHandler dbHandler;
	CruiseModifier cruiseModifier;

	/**
	 * Regenerate DSG files using the given configuration data.
	 * 
	 * @param configStore
	 * 		configuration data to use
	 */
	public RecreateDsgs(DashboardConfigStore configStore) {
		confStore = configStore;
		cruiseHandler = configStore.getCruiseFileHandler();
		cruiseChecker = configStore.getDashboardCruiseChecker();
		metaHandler = configStore.getMetadataFileHandler();
		dbHandler = configStore.getDatabaseRequestHandler();
		dsgHandler = configStore.getDsgNcFileHandler();
		cruiseModifier = new CruiseModifier();
	}

	/**
	 * Recreate the DSG files for the given dataset.
	 * 
	 * @param expocode
	 * 		regenerate the DSG files the the dataset with this expocode
	 * @throws IllegalArgumentException
	 * 		if there was a problem recreating the DSG files
	 */
	public void recreateDsgFiles(String expocode) throws IllegalArgumentException {
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);

		// Get all the data for this cruise
		DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(upperExpo, 0, -1);
		// Check the cruise and standardize the data values.
		// This will regenerate the SanityChecker messages file.
		if ( ! cruiseChecker.standardizeCruiseData(cruiseData) )
			throw new IllegalArgumentException("Problems standardizing cruise data for " + upperExpo);
			
		// Get the OME metadata for this cruise
		DashboardMetadata omeInfo = metaHandler.getMetadataInfo(expocode, DashboardMetadata.OME_FILENAME);
		DashboardOmeMetadata omeMData = new DashboardOmeMetadata(omeInfo, metaHandler);
		String socatVersionStatus;
		try {
			socatVersionStatus = dbHandler.getSocatVersionStatus(expocode);
			if ( socatVersionStatus.isEmpty() )
				throw new IllegalArgumentException("no global N/U QC flags");
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems getting the SOCAT version status for " + 
					upperExpo + ": " + ex.getMessage());
		}
		Character flag;
		try {
			flag = dbHandler.getQCFlag(upperExpo);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems getting the SOCAT QC flag for " + 
					upperExpo + ": " + ex.getMessage());
		}
		// Generate the NetCDF DSG file, enhanced by Ferret, for this 
		// possibly modified and WOCEd cruise data
		dsgHandler.saveCruise(omeMData, cruiseData, socatVersionStatus, flag.toString());
		// Generate the decimated-data DSG file from the full-data DSG file
		dsgHandler.decimateCruise(expocode);
		// Restore any WOCE flags from the database - redecimates if necessary
		try {
			cruiseModifier.restoreWoceFlags(confStore, upperExpo);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems restoring the WOCE flags for " + 
					upperExpo + ": " + ex.getMessage());
		}

	}

	/**
	 * Flag ERDDAP that the full-data and decimated-data DSG files have changed
	 */
	public void flagErddap() {
		dsgHandler.flagErddap(true, true);
	}

	/**
	 * @param args
	 * 		ExpocodesFile - recreate DSG files of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Recreates the full-data DSG files with the current data values in the ");
			System.err.println("cruise data file and OME XML metadata file.  The QC flag, as well as ");
			System.err.println("any matching WOCE flags, are restored from the database.  No changes ");
			System.err.println("are made to the database.  The decimated DSG files are then regenerated ");
			System.err.println("from the full-data DSG file.  The default dashboard configuration is ");
			System.err.println("used for this process.  This program was written to fix data column ");
			System.err.println("types that are not involved in the calculation of fCO2_rec. ");
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

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
			System.err.println("Error getting expocodes from " + expocodesFilename + ": " + ex.getMessage());
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
		RecreateDsgs regenerator = new RecreateDsgs(configStore);

		boolean success = true;
		try {
			// update each of the datasets
			for ( String expocode : allExpocodes ) {
				try {
					regenerator.recreateDsgFiles(expocode);
					System.err.println("Recreated the DSG files for " + expocode);
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
					success = false;
				}
			}
			regenerator.flagErddap();
		} finally {
			DashboardConfigStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
