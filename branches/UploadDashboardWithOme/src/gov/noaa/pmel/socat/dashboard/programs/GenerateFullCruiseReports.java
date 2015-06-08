/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.socat.dashboard.handlers.SocatFilesBundler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Generates full cruise reports for public consumption.
 * 
 * @author Karl Smith
 */
public class GenerateFullCruiseReports {

	/**
	 * @param args
	 * 		ExpocodesFile  Destination  [ RegionID ]
	 * 
	 * where:
	 * 
	 * ExpocodesFile is a file containing expocodes of the cruises to report;
	 * 
	 * Destination is the name of the directory to contain the single-cruise 
	 * reports, or the name of the file to contain the multi-cruise report;
	 *
	 * RegionID is the region ID restriction for the multi-cruise report;
	 * if not given, single-cruise reports will be generated; to generate
	 * a multi-cruise report without a region ID restriction, provide an
	 * empty string '' for this argument. 
	 */
	public static void main(String[] args) {
		if ( (args.length < 2) || (args.length > 3) ) {
			System.err.println("Arguments:  ExpocodesFile  Destination  [ RegionID ]");
			System.err.println();
			System.err.println("ExpocodesFile");
			System.err.println("    is a file containing expocodes, one per line, to report on");
			System.err.println("Destination");
			System.err.println("    the name of the directory to contain the single-cruise reports,");
			System.err.println("    or the name of the file to contain the multi-cruise report");
			System.err.println("RegionID");
			System.err.println("    the region ID restriction for the multi-cruise report; if not");
			System.err.println("    given, single-cruise reports will be generated; to generate a");
			System.err.println("    multi-cruise report without a region ID restriction, provide");
			System.err.println("    an empty string '' for this argument");
			System.exit(1);
		}
		String exposFilename = args[0];
		String destName = args[1];
		boolean multicruise;
		Character regionID;
		if ( args.length > 2 ) {
			multicruise = true;
			if ( args[2].trim().isEmpty() )
				regionID = null;
			else
				regionID = args[2].charAt(0);
		}
		else {
			multicruise = false;
			regionID = null;
		}

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
		try {
			if ( multicruise ) {
				SocatCruiseReporter reporter = new SocatCruiseReporter(configStore);
				try {
					ArrayList<String> warnMsgs = 
							reporter.generateReport(expocodes, regionID, new File(destName));
					if ( warnMsgs.size() > 0 ) {
						System.err.println("Warnings: ");
						for ( String msg : warnMsgs )
							System.err.println(msg);
					}
				} catch (Exception ex) {
					System.err.println("Problems generating the multi-cruise report: " + 
							ex.getMessage());
					ex.printStackTrace();
					System.exit(1);
				}
			}
			else {
				SocatFilesBundler bundler = new SocatFilesBundler(destName);
				for ( String expo : expocodes ) {
					try {
						ArrayList<String> warnMsgs = bundler.createSocatEnhancedFilesBundle(expo);
						System.err.println("Created single-cruise enhanced-data files bundle " + 
								bundler.getBundleFile(expo).getPath());
						if ( warnMsgs.size() > 0 ) {
							System.err.println("Warnings for " + expo + ": ");
							for ( String msg : warnMsgs )
								System.err.println(expo + ": " + msg);
						}
					} catch (Exception ex) {
						System.err.println("Problems generating the single-cruise enhanced-data " +
								"files bundle for " + expo + ": " + ex.getMessage());
						ex.printStackTrace();
						System.exit(1);
					}
				}
			}
		} finally {
			configStore.shutdown();
		}

		System.exit(0);
	}

}
