/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.qc.QCEvent;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Updates the QC flags in the full-data and decimated-data DSG files for cruises
 * to the flag obtained from the database.
 *
 * @author Karl Smith
 */
public class UpdateQCFlags {

    /**
     * @param args
     *         ExpocodesFile - update QC flags of these cruises
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println("Arguments:  ExpocodesFile");
            System.err.println();
            System.err.println("Updates the QC flags in the full-data and decimated-data DSG ");
            System.err.println("files for cruises specified in ExpocodesFile to the flag obtained ");
            System.err.println("from the database.  The default dashboard configuration is ");
            System.err.println("used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String expocodesFilename = args[0];

        boolean success = true;
        boolean updated = false;

        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            // Get the expocode of the cruises to update
            TreeSet<String> allExpocodes = new TreeSet<String>();
            try {
                BufferedReader expoReader =
                        new BufferedReader(new FileReader(expocodesFilename));
                try {
                    String dataline = expoReader.readLine();
                    while ( dataline != null ) {
                        dataline = dataline.trim();
                        if ( !(dataline.isEmpty() || dataline.startsWith("#")) )
                            allExpocodes.add(dataline);
                        dataline = expoReader.readLine();
                    }
                } finally {
                    expoReader.close();
                }
            } catch ( Exception ex ) {
                System.err.println("Error getting expocodes from " + expocodesFilename + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }

            DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();
            DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

            // update each of these cruises
            for (String expocode : allExpocodes) {
                String qcFlag;
                String version;
                try {
                    qcFlag = dbHandler.getDatasetQCFlag(expocode);
                    String versionStatus = dbHandler.getVersionStatus(expocode);
                    // Remove the 'N' or 'U' on the end to get just the version number
                    version = versionStatus.substring(0, versionStatus.length() - 1);
                } catch ( Exception ex ) {
                    System.err.println("Error getting the database QC flag or version for " +
                            expocode + " : " + ex.getMessage());
                    success = false;
                    continue;
                }
                String[] oldFlagVersion;
                try {
                    oldFlagVersion = dsgHandler.getDatasetQCFlagAndVersion(expocode);
                } catch ( Exception ex ) {
                    System.err.println("Error reading the current DSG QC flag or version for " +
                            expocode + " : " + ex.getMessage());
                    success = false;
                    continue;
                }
                try {
                    if ( !(qcFlag.equals(oldFlagVersion[0]) && version.equals(oldFlagVersion[1])) ) {
                        QCEvent event = new QCEvent();
                        event.setDatasetId(expocode);
                        event.setFlagValue(qcFlag);
                        event.setVersion(version);
                        // Update the QC flag in the DSG files
                        dsgHandler.updateDatasetQCFlagAndVersion(event);
                        System.out.println("Updated QC flag for " + expocode + " from '" + oldFlagVersion[0] +
                                "', v" + oldFlagVersion[1] + " to '" + qcFlag + "', v" + version);

                        updated = true;
                    }
                } catch ( Exception ex ) {
                    System.err.println("Error updating the QC flag in the DSG files for " +
                            expocode + " : " + ex.getMessage());
                    success = false;
                }
            }
            if ( updated ) {
                dsgHandler.flagErddap(true, true);
            }
        } finally {
            DashboardConfigStore.shutdown();
        }
        if ( !success )
            System.exit(1);
        System.exit(0);
    }

}
