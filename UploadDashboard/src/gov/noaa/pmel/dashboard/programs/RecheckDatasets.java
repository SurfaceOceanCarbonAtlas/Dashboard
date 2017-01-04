/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

import gov.noaa.pmel.dashboard.actions.DatasetChecker;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;

/**
 * Rechecks datasets with the automated data checker and regenerates the messages files.
 * 
 * @author Karl Smith
 */
public class RecheckDatasets {

	/**
	 * @param args
	 * 		IDsFile - a file containing IDs of the datasets to recheck
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  IDsFile");
			System.err.println();
			System.err.println("IDsFile");
			System.err.println("    is a file containing dataset IDs, one per line, to recheck with the ");
			System.err.println("    automated data checker and regenerate the messages files.");
			System.err.println();
			System.exit(1);
		}
		String idsFilename = args[0];

		TreeSet<String> idsSet = new TreeSet<String>();
		try {
			BufferedReader idsReader = new BufferedReader(new FileReader(idsFilename));
			try {
				String dataline = idsReader.readLine();
				while ( dataline != null ) {
					dataline = dataline.trim();
					if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
						idsSet.add(dataline);
					dataline = idsReader.readLine();
				}
			} finally {
				idsReader.close();
			}
		} catch (Exception ex) {
			System.err.println("Error reading dataset IDs from " + idsFilename + ": " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		int retVal = 0;
		try {

			DataFileHandler dataHandler = configStore.getDataFileHandler();
			DatasetChecker dataChecker = configStore.getDashboardDatasetChecker();

			for ( String datasetId : idsSet ) {
				// Get all the data for this dataset
				DashboardDatasetData dataset;
				try {
					dataset = dataHandler.getDatasetDataFromFiles(datasetId, 0, -1);
				} catch ( Exception ex ) {
					System.err.println("Error - " + datasetId + " - problems obtaining dataset data");
					retVal = 1;
					continue;
				}
				// Check the dataset as if this was to be submitted.
				// This will regenerate the automated data checker messages file.
				dataChecker.standardizeDataset(dataset, null);
				System.err.println("Done - " + datasetId);
			}

		} finally {
			DashboardConfigStore.shutdown();
		}

		// Done - return zero if no problems
		System.exit(retVal);
	}

}
