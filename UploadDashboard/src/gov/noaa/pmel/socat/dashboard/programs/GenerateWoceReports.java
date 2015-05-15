/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFlagsHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Generates WOCE flag reports for cruises.
 * 
 * @author Karl Smith
 */
public class GenerateWoceReports {

	/**
	 * @param args
	 * 		ExpocodesFile - file containing expocodes of the cruises to report WOCE flags
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("ExpocodesFile");
			System.err.println("    is a file containing expocodes, one per line, of cruises for which ");
			System.err.println("    to generate WOCE flags report files from the current WOCE flags in ");
			System.err.println("    the database. ");
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
			DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();
			CruiseFlagsHandler flagsHandler = configStore.getCruiseFlagsHandler();

			for ( String expo : expocodes ) {
				// Generate the WOCE flags report file from the summary messages and current WOCE flags
				try {
					flagsHandler.generateWoceFlagMsgsFile(expo, dbHandler);
				} catch ( Exception ex ) {
					System.err.println("Error - " + expo + " - problems getting WOCE flags");
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
