/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
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
		if ( args.length != 2 ) {
			System.err.println("Arguments:  ExpocodesFile  SummaryFile");
			System.err.println();
			System.err.println("Generates a summary of the cruises specified in ExpocodesFile ");
			System.err.println("which is written to SummaryFile. The default dashboard ");
			System.err.println("configuration is used for this process. ");
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];
		String summaryFilename = args[1];

		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			TreeSet<String> allExpocodes = new TreeSet<String>();
			try {
				// Get the expocode of the cruises to report
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
				System.err.println("Problems reading the file of expocodes: " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			// Generate the report
			PrintWriter report = null;
			try {
				report = new PrintWriter(summaryFilename, "ISO-8859-1");
			} catch (Exception ex) {
				System.err.println("Problems opening the summary file for writing: " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}
			SocatCruiseReporter summaryReporter = new SocatCruiseReporter(configStore);
			try {
				summaryReporter.printSummaryHeader(report);
				for ( String expocode : allExpocodes ) {
					try {
						summaryReporter.printCruiseSummary(expocode, report);
					} catch (Exception ex) {
						System.err.println("Error reporting on " + expocode + " : " + ex.getMessage());
						success = false;
					}
				}
			} finally {
				report.close();
			}
		} finally {
			DashboardConfigStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
