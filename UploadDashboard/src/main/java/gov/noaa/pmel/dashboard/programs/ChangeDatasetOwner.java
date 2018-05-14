/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

import gov.noaa.pmel.dashboard.actions.DatasetModifier;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

/**
 * Changes the owner of data and metadata files for datasets.
 *
 * @author Karl Smith
 */
public class ChangeDatasetOwner {

    /**
     * @param args
     *         IDsOwnersFile - file of dataset IDs and new owners
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println();
            System.err.println("Arguments:  IDsOwnersFile");
            System.err.println();
            System.err.println("Each line of IDsOwnersFile is a space-separated dataset ID and dashboard username. ");
            System.err.println("For each, changes the owner of the data and metadata files for the dataset to the ");
            System.err.println("specified user.  The datasets are added to the listing of the new owner.  The dataset ");
            System.err.println("will be removed from old owners list, if appropriate, by the automatic update when ");
            System.err.println("the old owners list is next read. ");
            System.err.println("The default dashboard configuration is used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String idOwnerFilename = args[0];

        boolean success = true;

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

            // Get the IDs and new owners of the datasets to update
            TreeMap<String,String> idOwnerMap = new TreeMap<String,String>();
            try {
                BufferedReader idOwnerReader = new BufferedReader(new FileReader(idOwnerFilename));
                try {
                    String dataline = idOwnerReader.readLine();
                    while ( dataline != null ) {
                        dataline = dataline.trim();
                        if ( dataline.isEmpty() || dataline.startsWith("#") )
                            continue;
                        String[] tokens = dataline.split("\\s+");
                        if ( tokens.length != 2 ) {
                            System.err.println("Unable to get dataset ID and new owner from: " + dataline);
                            System.exit(1);
                        }
                        if ( ! configStore.validateUser(tokens[1]) ) {
                            System.err.println("Invalid dashboard username given in: " + dataline);
                            System.exit(1);
                        }
                        try {
                            String stdId = DashboardServerUtils.checkDatasetID(tokens[0]);
                            if ( idOwnerMap.put(stdId, tokens[1]) != null ) {
                                System.err.println("More than one owner specified for " + stdId);
                                System.exit(1);
                            }
                        } catch (Exception ex) {
                            System.err.println("Invalid dataset ID given in: " + dataline);
                            System.exit(1);
                        }
                        dataline = idOwnerReader.readLine();
                    }
                } finally {
                    idOwnerReader.close();
                }
            } catch (Exception ex) {
                System.err.println("Error getting dataset IDs and new owners from " +
                        idOwnerFilename + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }

            DatasetModifier modifier = new DatasetModifier(configStore);
            for ( Map.Entry<String,String> idOwner : idOwnerMap.entrySet() ) {
                String datasetId = idOwner.getKey();
                String newOwner = idOwner.getValue();
                try {
                    modifier.changeDatasetOwner(datasetId, newOwner);
                } catch (Exception ex) {
                    System.err.println("Problems changing the owner of " + datasetId +
                            " to " + newOwner + ": " + ex.getMessage());
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
