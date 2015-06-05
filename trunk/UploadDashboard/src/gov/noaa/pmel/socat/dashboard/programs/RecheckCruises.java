/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseChecker;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Rechecks cruises with the SanityChecker and regenerates the SanityChecker messages files.
 * Does not make any changes to the WOCE flags in the database.
 * 
 * @author Karl Smith
 */
public class RecheckCruises {

	/**
	 * @param args
	 * 		ExpocodesFile - a file containing expocodes of the cruises to recheck
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("ExpocodesFile");
			System.err.println("    is a file containing expocodes, one per line, to recheck with the ");
			System.err.println("    SanityChecker and regenerate the SanityChecker messages files, but ");
			System.err.println("    does not make any changes to the WOCE flags in the database. ");
			System.err.println();
			System.exit(1);
		}
		String exposFilename = args[0];

		TreeSet<String> expocodes = new TreeSet<String>();
		try {
			BufferedReader reader = 
					new BufferedReader(new FileReader(exposFilename));
			try {
				String dataline = reader.readLine();
				while ( dataline != null ) {
					dataline = dataline.trim().toUpperCase();
					if ( ! dataline.isEmpty() )
						expocodes.add(dataline);
					dataline = reader.readLine();
				}
			} finally {
				reader.close();
			}
		} catch (Exception ex) {
			System.err.println("Problems reading the file of expocodes '" + 
					exposFilename + "': " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		int retVal = 0;
		try {
			CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
			CruiseChecker cruiseChecker = configStore.getDashboardCruiseChecker();

			for ( String expo : expocodes ) {
				// Get all the data for this cruise
				DashboardCruiseWithData cruiseData;
				try {
					cruiseData = cruiseHandler.getCruiseDataFromFiles(expo, 0, -1);
				} catch ( Exception ex ) {
					System.err.println("Error - " + expo + " - problems obtaining cruise data");
					retVal = 1;
					continue;
				}
				// Check the cruise as if this was to be submitted.
				// This will regenerate the SanityChecker messages file.
				if ( ! cruiseChecker.standardizeCruiseData(cruiseData) ) {
					System.err.println("Error - " + expo + " - problems standardizing cruise data");
					retVal = 1;
					continue;
				}
				System.err.println("Success - " + expo);
			}
		} finally {
			configStore.shutdown();
		}

		// Done - return zero if no problems
		System.exit(retVal);
	}

}
