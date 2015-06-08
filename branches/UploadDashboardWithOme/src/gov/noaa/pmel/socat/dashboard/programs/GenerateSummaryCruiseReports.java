/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Generates a summary of the cruises specified in ExpocodesFile, 
 * or all cruises if ExpocodesFile is '-'. The default dashboard 
 * configuration is used for this process.
 * 
 * @author Karl Smith
 */
public class GenerateSummaryCruiseReports {

	/**
	 * @param args 
	 * 		ExpocodesFile - generate summaries of the cruises with these expocodes,
	 * 				or all cruises if '-'
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  [ - | ExpocodesFile ]");
			System.err.println();
			System.err.println("Generates a summary of the cruises specified in ExpocodesFile, ");
			System.err.println("or all cruises if ExpocodesFile is '-'. The default dashboard ");
			System.err.println("configuration is used for this process. ");
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];
		if ( "-".equals(expocodesFilename) )
			expocodesFilename = null;

		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			SocatCruiseReporter summaryReporter = new SocatCruiseReporter(configStore);

			// Get the expocode of the cruises to report
			TreeSet<String> allExpocodes = null; 
			if ( expocodesFilename != null ) {
				allExpocodes = new TreeSet<String>();
				try {
					BufferedReader expoReader = 
							new BufferedReader(new FileReader(expocodesFilename));
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
			} 
			else {
				try {
					allExpocodes = new TreeSet<String>(
							configStore.getCruiseFileHandler().getMatchingExpocodes("*"));
				} catch (Exception ex) {
					System.err.println("Error getting all expocodes: " + ex.getMessage());
					ex.printStackTrace();
					System.exit(1);
				}
			}

			// Generate the report
			summaryReporter.printSummaryHeader(System.out);
			for ( String expocode : allExpocodes ) {
				try {
					summaryReporter.printCruiseSummary(expocode, System.out);
				} catch (Exception ex) {
					System.err.println("Error reporting on " + expocode + " : " + ex.getMessage());
					success = false;
				}
			}

		} finally {
			configStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
