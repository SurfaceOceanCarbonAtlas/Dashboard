/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.SocatFilesBundler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

/**
 * Generates the original data file bundles for the specified expocodes 
 * without e-mailing them out.  Intended for archival on release of SOCAT.
 * 
 * @author Karl Smith
 */
public class GenerateOrigFileBundles {

	/**
	 * Generates the original data file bundles for the specified expocodes.
	 * These bundles are added to the version control bundles directory, 
	 * but not e-mailed to anyone.
	 * 
	 * @param args
	 * 		ExpocodesFile
	 * 
	 * where ExpocodesFile is a file of expocodes for generating original data file bundles.
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Generates original data file bundles for the expocodes ");
			System.err.println("specified in ExpocodesFile.  These file bundles are added ");
			System.err.println("to version control bundles directory, but not emailed to ");
			System.err.println("anyone.  The default dashboard configuration is used for ");
			System.err.println("this process. ");
			System.err.println();
			System.exit(1);
		}
		String exposFilename = args[0];

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
			System.err.println("Problems reading the file of expocodes '" + 
					exposFilename + "': " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		boolean success = true;
		try {
			SocatFilesBundler filesBundler = configStore.getCdiacFilesBundler();
			for ( String expo : expocodes ) {
				String commitMsg = "Automated generation of the original data files bundle for " + expo;
				try {
					String resultMsg = filesBundler.sendOrigFilesBundle(expo, commitMsg, 
							DashboardServerUtils.NOMAIL_USER_REAL_NAME, DashboardServerUtils.NOMAIL_USER_EMAIL);
					System.out.println(expo + " : " + resultMsg); 
				} catch ( IllegalArgumentException | IOException ex ) {
					System.out.println(expo + " : " + "failed - " + ex.getMessage());
					success = false;
				}
			}
		} finally {
			DashboardConfigStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
