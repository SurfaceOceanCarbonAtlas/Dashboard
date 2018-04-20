/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.actions.CruiseModifier;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Changes the owner of data and metadata files for datasets.
 *
 * @author Karl Smith
 */
public class ChangeCruiseOwner {

    /**
     * @param args
     *         ExpocodesOwnersFile - file of expocodes and new owners
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println();
            System.err.println("Arguments:  ExpocodesOwnersFile");
            System.err.println();
            System.err.println("Changes the owner of the data and metadata files for the datasets and ");
            System.err.println("owners specified in ExpocodesOwnersFile.  Each line of ExpocodesOwnersFile ");
            System.err.println("has a space-separated expocode and new owner dashboard username.  The ");
            System.err.println("datasets are added to the listing of the new owner.  The update-on-read ");
            System.err.println("of dataset listings will remove datasets that should no longer be seen ");
            System.err.println("by old owners, and others, due to the change.  The default dashboard ");
            System.err.println("configuration is used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String exposOwnersFilename = args[0];

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
            // Get the expocode of the cruises to update
            TreeMap<String,String> exposOwners = new TreeMap<String,String>();
            try {
                BufferedReader exposOwnersReader = new BufferedReader(new FileReader(exposOwnersFilename));
                try {
                    String dataline = exposOwnersReader.readLine();
                    while ( dataline != null ) {
                        dataline = dataline.trim();
                        if ( dataline.isEmpty() || dataline.startsWith("#") )
                            continue;
                        String[] tokens = dataline.split("\\s+");
                        if ( tokens.length != 2 ) {
                            System.err.println("Unable to get expocode and owner from: " + dataline);
                            System.exit(1);
                        }
                        if ( !configStore.validateUser(tokens[1]) ) {
                            System.err.println("Invalid dashboard user given in: " + dataline);
                            System.exit(1);
                        }
                        try {
                            String upperExpo = DashboardServerUtils.checkExpocode(tokens[0]);
                            if ( exposOwners.put(upperExpo, tokens[1]) != null ) {
                                System.err.println("More than one owner specified for " + upperExpo);
                                System.exit(1);
                            }
                        } catch (Exception ex) {
                            System.err.println("Invalid expocode given in: " + dataline);
                            System.exit(1);
                        }
                        dataline = exposOwnersReader.readLine();
                    }
                } finally {
                    exposOwnersReader.close();
                }
            } catch (Exception ex) {
                System.err.println("Error getting expocodes and owners from " +
                                           exposOwnersFilename + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }

            CruiseModifier modifier = new CruiseModifier(configStore);
            for (Map.Entry<String,String> expoOwner : exposOwners.entrySet()) {
                String expocode = expoOwner.getKey();
                String newOwner = expoOwner.getValue();
                try {
                    modifier.changeCruiseOwner(expocode, newOwner);
                } catch (Exception ex) {
                    System.err.println("Problems changing the owner of " + expocode +
                                               " to " + newOwner + ": " + ex.getMessage());
                    success = false;
                }
            }
        } finally {
            DashboardConfigStore.shutdown();
        }
        if ( !success )
            System.exit(1);
        System.exit(0);
    }

}
