/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        if ( args.length != 2 ) {
            System.err.println("Arguments:  IDsFile  Use_Bagit");
            System.err.println();
            System.err.println("Generates original data file bundles for the dataset IDs specified ");
            System.err.println("in IDsFile.  These file bundles are added to the version control ");
            System.err.println("bundles directory, but are not emailed to anyone.  The default ");
            System.err.println("dashboard configuration is used for this process. ");
            System.err.println();
            System.err.println("If Use_Bagit is 'True' or 'T' (case insensitive), uses bagit instead of zip");
            System.err.println();
            System.exit(1);
        }
        String idsFilename = args[0];
        boolean useBagit;
        if ( "True".equalsIgnoreCase(args[1]) || "T".equalsIgnoreCase(args[1]) )
            useBagit = true;
        else
            useBagit = false;

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
            for (String datasetId : idsSet) {
                String commitMsg = "Automated generation of the original data files bundle for " + datasetId;
                try {
                    if ( useBagit ) {
                        File bagFile = filesBundler.createBagitFilesBundle(datasetId, commitMsg);
                        System.out.println(datasetId + " bagit file: " + bagFile.getPath());
                    }
                    else {
                        String resultMsg = filesBundler.sendOrigFilesBundle(datasetId, commitMsg,
                                DashboardServerUtils.NOMAIL_USER_REAL_NAME, DashboardServerUtils.NOMAIL_USER_EMAIL);
                        System.out.println(datasetId + " : " + resultMsg);
                    }
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
