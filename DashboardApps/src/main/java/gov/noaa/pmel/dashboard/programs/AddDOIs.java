/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
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
                    String[] expodoi = dataline.split("\\s+");
                    if ( expodoi.length < 2 )
                        throw new IllegalArgumentException("Less than two words in " + dataline.trim());
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
            System.err.println("Arguments:  Expo_SOCAT_DOI_File  Expo_Orig_DOI_File");
            System.err.println();
            System.err.println("Updates the DOIs for the SOCAT-enhanced and original data documents. ");
            System.err.println(
                    "Each line in the files should be an expocode followed (whitespace separated) by a DOI. ");
            System.err.println("Blank lines or lines starting with a '#' are ignored. ");
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
            gov.noaa.pmel.dashboard.handlers.DataFileHandler cruiseHandler = configStore.getDataFileHandler();
            for (String expocode : exposSet) {
                gov.noaa.pmel.dashboard.shared.DashboardDataset cruise = null;
                try {
                    cruise = cruiseHandler.getDatasetFromInfoFile(expocode);
                    if ( cruise == null )
                        throw new NullPointerException("no such dataset");
                } catch ( Exception ex ) {
                    System.err.println(
                            "Problems reading the cruise data properties for " + expocode + ": " + ex.getMessage());
                    success = false;
                    continue;
                }
                String doi = socatDOIMap.get(expocode);
                if ( doi != null ) {
                    cruise.setEnhancedDOI(doi);
                    System.out.println("Updating the SOCAT-enhanced DOI for " + expocode + " to " + doi);
                }
                doi = origDOIMap.get(expocode);
                if ( doi != null ) {
                    cruise.setSourceDOI(doi);
                    cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_ARCHIVED);
                    System.out.println(
                            "Updating the original-data DOI for " + expocode + " to " + doi + " and marking as archived");
                }
                try {
                    cruiseHandler.saveDatasetInfoToFile(cruise, null);
                } catch ( Exception ex ) {
                    System.err.println(
                            "Problems saving the cruise data properties for " + expocode + ": " + ex.getMessage());
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
