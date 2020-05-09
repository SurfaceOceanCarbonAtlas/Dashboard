package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

/**
 * Generates the original data file bundles for the specified dataset IDs
 * without e-mailing them out.  Intended for archival on release.
 *
 * @author Karl Smith
 */
public class GenerateOrigFileBundles {

    /**
     * Generates the original data file bundles for the specified dataset IDs.
     * These bundles are added to the version control bundles directory,
     * but not e-mailed to anyone.
     *
     * @param args
     *         IDsFile
     *         <p>
     *         where IDsFile is a file of IDs of datasets to generating original data file bundles for.
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println("Arguments:  IDsFile");
            System.err.println();
            System.err.println("Generates original data file bundles for the dataset IDs specified in IDsFile. ");
            System.err.println("These file bundles are added to the version control bundles directory, but are ");
            System.err.println("not emailed to anyone.  The default dashboard configuration specified by the ");
            System.err.println("environment variable UPLOAD_DASHBOARD_SERVER_NAME is used for this process. ");
            System.err.println();
            System.exit(1);
        }
        String idsFilename = args[0];

        TreeSet<String> idsSet = new TreeSet<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(idsFilename));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {
                    dataline = dataline.trim();
                    if ( !(dataline.isEmpty() || dataline.startsWith("#")) )
                        idsSet.add(dataline);
                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Error reading dataset IDs from " + idsFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        boolean success = true;
        try {

            ArchiveFilesBundler filesBundler = configStore.getArchiveFilesBundler();
            DataFileHandler dataHandler = configStore.getDataFileHandler();
            String archiveStatus = DashboardUtils.ARCHIVE_STATUS_SENT_TO_START + "OCADS";
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm Z").format(new Date());
            for (String datasetId : idsSet) {
                String commitMsg = "Automated generation of the original data files bundle for " + datasetId;
                try {
                    String resultMsg = filesBundler.sendOrigFilesBundle(datasetId, commitMsg,
                            DashboardServerUtils.NOMAIL_USER_REAL_NAME, DashboardServerUtils.NOMAIL_USER_EMAIL);
                    // Output the (success) message from the archival
                    System.out.println(datasetId + " : " + resultMsg);
                    // Update the archived timestamp
                    DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(datasetId);
                    cruise.setArchiveStatus(archiveStatus);
                    cruise.getArchiveTimestamps().add(timestamp);
                    dataHandler.saveDatasetInfoToFile(cruise, commitMsg);
                } catch ( IllegalArgumentException | IOException ex ) {
                    System.out.println(datasetId + " : " + "failed - " + ex.getMessage());
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
