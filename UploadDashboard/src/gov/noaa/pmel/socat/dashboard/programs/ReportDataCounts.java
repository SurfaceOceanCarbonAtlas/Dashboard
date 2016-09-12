/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Report data point counts of valid fCO2_rec values grouped by year and QC flag.
 *  
 * @author Karl Smith
 */
public class ReportDataCounts {

	/**
	 * @param args
	 * 		ExpocodesFile  ReportFile  [ RegionID ]
	 * 
	 * ExpocodesFile: use data points from data sets with the expocodes in this file
	 * 
	 * RegionID: consider only data points in the region with this ID; if not given
	 *           or empty, no regional restriction is applied
	 */
	public static void main(String[] args) {
		if ( (args.length < 2) || (args.length > 3) ) {
			System.err.println("Arguments:  ExpocodesFile  ReportFile  [ RegionID ]");
			System.err.println();
			System.err.println("ExpocodesFile");
			System.err.println("    file containing expocodes, one per line,");
			System.err.println("    of the data sets to consider.");
			System.err.println("ReportFile");
			System.err.println("    file to contain the data counts report");
			System.err.println("RegionID");
			System.err.println("    consider only data points in the region with this ID;");
			System.err.println("    if not given or empty, no regional restriction is applied.");
			System.exit(1);
		}
		String exposFilename = args[0];
		String reportFilename = args[1];
		Character regionID = null;
		if ( args.length > 2 ) {
			String val = args[2].trim();
			if ( val.isEmpty() )
				regionID = null;
			else
				regionID = val.charAt(0);
		}

		TreeSet<String> expocodes = new TreeSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(exposFilename));
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
			System.err.println("Problems reading the file of expocodes '" + exposFilename + "': " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			SocatCruiseReporter reporter = new SocatCruiseReporter(configStore);
			try {
				ArrayList<String> warnMsgs = reporter.printDataCounts(expocodes, regionID, new File(reportFilename));
				if ( warnMsgs.size() > 0 ) {
					System.err.println("Warnings: ");
					for ( String msg : warnMsgs )
						System.err.println(msg);
				}
			} catch (Exception ex) {
				System.err.println("Problems generating the data count report: " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}
		} finally {
			DashboardConfigStore.shutdown();
		}

		System.exit(0);
	}

}
