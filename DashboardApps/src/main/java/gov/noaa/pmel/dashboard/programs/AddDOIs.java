/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Adds DOIs for the SOCAT-enhanced as well as original-data documents.
 *
 * @author Karl Smith
 */
public class AddDOIs {

    private DataFileHandler dataHandler;

    /**
     * @param dataHandler
     *         data file handler for reading and update dataset information files
     */
    public AddDOIs(DataFileHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    /**
     * Update the DOIs for a dataset.  If the original-data DOI is given, mark the dataset as archived.
     * The changes are not committed to version control.
     *
     * @param expocode
     *         update the DOIs of the dataset with this ID
     * @param origDoi
     *         original-data DOI to use; if null or blank, no changes are made to the original-data DOI.
     *         If not null and not blank, the archive status of the dataset is set to the archived status.
     * @param enhancedDoi
     *         enhanced-data DOI to use; if null or blank, no changes are mode to the enhanced-data DOI
     *
     * @return message about what was done.
     *         If both the original-data and enhanced-doi DOIs are null or blank, null is returned.
     *
     * @throws IllegalArgumentException
     *         if the expocode is invalid,
     *         if the information file for this dataset does not exist, or
     *         if there are problems reading or updating the information file for this dataset.
     */
    public String updateDOIsForDataset(String expocode, String origDoi, String enhancedDoi)
            throws IllegalArgumentException {
        String upperExpo = DashboardServerUtils.checkDatasetID(expocode);
        DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(upperExpo);
        if ( cruise == null )
            throw new IllegalArgumentException("info file for " + upperExpo + "does not exist");
        String msg = null;
        if ( enhancedDoi != null ) {
            String doi = enhancedDoi.trim();
            if ( !doi.isEmpty() ) {
                cruise.setEnhancedDOI(doi);
                msg = "updated the SOCAT-enhanced DOI for " + upperExpo + " to " + doi;
            }
        }
        if ( origDoi != null ) {
            String doi = origDoi.trim();
            if ( !doi.isEmpty() ) {
                cruise.setSourceDOI(doi);
                cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_ARCHIVED);
                if ( msg != null )
                    msg += "\n";
                else
                    msg = "";
                msg += "updated the original-data DOI for " + upperExpo + " to " + doi + " and marking as archived";
            }
        }
        dataHandler.saveDatasetInfoToFile(cruise, null);
        return msg;
    }

    /**
     * Creates and returns a map of expocodes to DOIs in the specified file.
     *
     * @param expoDOIsFilename
     *         name of the file containing the expocode / DOI data
     *
     * @return map of expocodes to DOIs in the specified file;
     *         never null but may be empty
     *
     * @throws IllegalArgumentException
     *         if an invalid expocode / DOI data line is found
     * @throws IOException
     *         if opening or reading the expocode / DOI file throws one
     */
    private static TreeMap<String,String> readExpoDOIFile(String expoDOIsFilename)
            throws IllegalArgumentException, IOException {
        TreeMap<String,String> expoDOIMap = new TreeMap<String,String>();
        BufferedReader expoReader = new BufferedReader(new FileReader(expoDOIsFilename));
        try {
            String dataline = expoReader.readLine();
            while ( dataline != null ) {
                if ( !(dataline.isEmpty() || dataline.startsWith("#")) ) {
                    String[] expodoi = dataline.split("\t");
                    if ( expodoi.length < 2 )
                        throw new IllegalArgumentException("not a tab-separated pair: " + dataline.trim());
                    String upperExpo = DashboardServerUtils.checkDatasetID(expodoi[0]);
                    expoDOIMap.put(upperExpo, expodoi[1]);
                }
                dataline = expoReader.readLine();
            }
        } finally {
            expoReader.close();
        }
        return expoDOIMap;
    }

    /**
     * @param args
     *         Expo_SOCAT_DOI_File - file of expocodes with DOIs of the SOCAT-enhanced documents
     *         Expo_Orig_DOI_File - file of expocodes with DOIs of the original data documents
     */
    public static void main(String[] args) {
        if ( args.length != 2 ) {
            System.err.println();
            System.err.println("Arguments:  Expo_SOCAT_DOI.tsv  Expo_Orig_DOI.tsv");
            System.err.println();
            System.err.println("Updates the DOIs for the SOCAT-enhanced and original data documents. ");
            System.err.println("Each line in the files should be an expocode, a tab character, and the ");
            System.err.println("DOI to assign.  Blank lines or lines starting with a '#' are ignored. ");
            System.err.println("The default dashboard configuration is used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String socatDOIsFilename = args[0];
        String origDOIsFilename = args[1];

        // Create the expocode to SOCAT DOI map
        TreeMap<String,String> socatDOIMap = null;
        try {
            socatDOIMap = readExpoDOIFile(socatDOIsFilename);
        } catch ( Exception ex ) {
            System.err.println("Error reading " + socatDOIsFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        // Create the expocode to original DOI map
        TreeMap<String,String> origDOIMap = null;
        try {
            origDOIMap = readExpoDOIFile(origDOIsFilename);
        } catch ( Exception ex ) {
            System.err.println("Error reading " + origDOIsFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        TreeSet<String> exposSet = new TreeSet<String>();
        exposSet.addAll(socatDOIMap.keySet());
        exposSet.addAll(origDOIMap.keySet());
        if ( exposSet.size() == 0 ) {
            System.err.println("No valid expocode DOI data in " + socatDOIsFilename + " or " + origDOIsFilename);
            System.exit(1);
        }

        // Get the default dashboard configuration
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
            AddDOIs updater = new AddDOIs(configStore.getDataFileHandler());
            for (String expocode : exposSet) {
                try {
                    String msg = updater.updateDOIsForDataset(expocode,
                            origDOIMap.get(expocode), socatDOIMap.get(expocode));
                    if ( msg != null )
                        System.out.println(msg);
                } catch ( Exception ex ) {
                    System.err.println("Problems updating the DOIs for " + expocode + ": " + ex.getMessage());
                    success = false;
                    continue;
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

