/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseModifier;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Renames cruises (changes the expocodes).  All files will be moved to the
 * new locations specified by the new expocode.  If appropriate, file contents
 * are updated for the new expocodes.  If a cruise has been submitted for QC,
 * QC and WOCE flags are updated and rename events are added.
 * 
 * @author Karl Smith
 */
public class RenameCruises {

	/**
	 * @param args
	 * 		Username - name of the dashboard admin user requesting this update.
	 * 		ExpocodesFile - file of old and new expocodes (one pair per line) for the cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  Username  ExpocodesFile");
			System.err.println();
			System.err.println("Renames cruise (changes the expocodes).  All files will be moved, ");
			System.err.println("and updated if appropriate.  If the cruise had been submitted for ");
			System.err.println("QC, the QC and WOCE flags will be updated and rename events will ");
			System.err.println("be added. ");
			System.err.println();
			System.err.println("Username is the dashboard admin requesting this update.");
			System.err.println("ExpocodesFile is a file of old and new expocode pairs, one pair per line");
			System.err.println();
			System.exit(1);
		}

		String username = args[0];
		File exposFile = new File(args[1]);

		TreeMap<String,String> oldNewExpoMap = new TreeMap<String,String>();
		try {
			BufferedReader exposReader = new BufferedReader(new FileReader(exposFile));
			try {
				String dataline = exposReader.readLine();
				while ( dataline != null ) {
					String[] expoPair = dataline.split("\\s+");
					if ( expoPair.length != 2 )
						throw new IllegalArgumentException("not an expocode pair: '" + dataline.trim() + "'");
					oldNewExpoMap.put(expoPair[0], expoPair[1]);
					dataline = exposReader.readLine();
				}
			} finally {
				exposReader.close();
			}
			if ( oldNewExpoMap.isEmpty() )
				throw new IOException("file is empty");
		} catch (Exception ex) {
			System.err.println("Problems reading the old and new expocodes from " + exposFile.getPath());
			System.exit(1);
		}

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
			if ( ! configStore.isAdmin(username) ) {
				System.err.println(username + " is not an admin for the dashboard");
				System.exit(1);
			}
			CruiseModifier renamer = new CruiseModifier(configStore);
			for ( Entry<String, String> expoEntry: oldNewExpoMap.entrySet() ) {
				String oldExpocode = expoEntry.getKey();
				String newExpocode = expoEntry.getValue();
				try {
					renamer.renameCruise(oldExpocode, newExpocode, username);
				} catch (Exception ex) {
					System.err.println("Error renaming " + oldExpocode + " to " + newExpocode);
					ex.printStackTrace();
				}
			}
		} finally {
			configStore.shutdown();
		}
		System.exit(0);
	}

}
