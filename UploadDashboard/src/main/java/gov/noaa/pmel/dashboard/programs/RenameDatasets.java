/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import gov.noaa.pmel.dashboard.actions.DatasetModifier;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;

/**
 * Renames datasets (changes the dataset IDs).  All files will be moved to the
 * new locations specified by the new ID.  If appropriate, file contents
 * are updated for the new Ids.
 * 
 * @author Karl Smith
 */
public class RenameDatasets {

	/**
	 * @param args
	 * 		Username - name of the dashboard admin user requesting this update.
	 * 		IDsFile - file of old and new IDs (one pair per line) for the datasets
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  Username  IDsFile");
			System.err.println();
			System.err.println("Renames datasets (changes the IDs).  All files will be moved, ");
			System.err.println("and updated if appropriate.");
			System.err.println();
			System.err.println("Username is the dashboard admin requesting this update.");
			System.err.println("IDsFile is a file of old and new IDs, one pair per line for the datasets.");
			System.err.println();
			System.exit(1);
		}

		String username = args[0];
		File idsFile = new File(args[1]);

		TreeMap<String,String> oldNewIdsMap = new TreeMap<String,String>();
		try {
			BufferedReader idsReader = new BufferedReader(new FileReader(idsFile));
			try {
				String dataline = idsReader.readLine();
				while ( dataline != null ) {
					if ( dataline.isEmpty() || dataline.startsWith("#") )
						continue;
					String[] expoPair = dataline.split("\\s+");
					if ( expoPair.length != 2 )
						throw new IllegalArgumentException("not a pair of IDs: '" + dataline.trim() + "'");
					oldNewIdsMap.put(expoPair[0], expoPair[1]);
					dataline = idsReader.readLine();
				}
			} finally {
				idsReader.close();
			}
			if ( oldNewIdsMap.isEmpty() )
				throw new IOException("file is empty");
		} catch (Exception ex) {
			System.err.println("Problems reading the old and new dataset IDs from " + idsFile.getPath());
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
		try {

			if ( ! configStore.isAdmin(username) ) {
				System.err.println(username + " is not an admin for the dashboard");
				System.exit(1);
			}
			DatasetModifier renamer = new DatasetModifier(configStore);
			for ( Entry<String, String> idsEntry: oldNewIdsMap.entrySet() ) {
				String oldId = idsEntry.getKey();
				String newId = idsEntry.getValue();
				try {
					renamer.renameDataset(oldId, newId, username);
				} catch (Exception ex) {
					System.err.println("Error renaming " + oldId + " to " + newId);
					ex.printStackTrace();
				}
			}

		} finally {
			DashboardConfigStore.shutdown();
		}

		System.exit(0);
	}

}
