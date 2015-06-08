/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Generates the decimated DSG file from the full-data DSG file.  
 * The default dashboard configuration is used for this process. 
 * 
 * @author Karl Smith
 */
public class DecimateCruises {

	/**
	 * @param args
	 * 		ExpocodesFile - file of expocodes of cruises to decimate
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  [ - | ExpocodesFile ]");
			System.err.println();
			System.err.println("Generates the decimated DSG file from the full-data DSG file for ");
			System.err.println("cruises specified in ExpocodesFile, or all cruises if ExpocodesFile ");
			System.err.println("is '-'. The default dashboard configuration is used for this process. ");
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
			DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

			// Get the expocode of the cruises to decimate
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

			// decimate each of these cruises
			for ( String expocode : allExpocodes ) {
				try {
					dsgHandler.decimateCruise(expocode);
				} catch (Exception ex) {
					System.err.println("Error decimating " + expocode + " : " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
				}
			}

			// Flag ERDDAP that (only) the decimated files have been updated
			dsgHandler.flagErddap(false, true);
		} finally {
			configStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
