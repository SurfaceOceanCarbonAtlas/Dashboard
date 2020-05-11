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
import java.util.regex.Pattern;

/**
 * Adds references for the SOCAT-enhanced as well as original-data documents.
 *
 * @author Karl Smith
 */
public class AddDatasetRefs {

    private static final Pattern DOI_PATTERN = Pattern.compile("[0-9]+\\.[0-9]+/[A-Z0-9/_.-]+");

    private final DataFileHandler dataHandler;

    /**
     * @param dataHandler
     *         data file handler for updating DOIs, landing pages, and archival status in the data properties file
     */
    public AddDatasetRefs(DataFileHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    /**
     * Update the original-data as well as the SOCAT-enhanced DOIs and landing pages for a dataset.
     * If an original-data DOI or landing page is given, mark the dataset as archived.
     * The changes are not committed to version control.
     *
     * @param expocode
     *         update the DOIs of the dataset with this ID
     * @param origDoi
     *         original-data DOI to use; if null or blank, no changes are made to the original-data DOI.
     *         If not null and not blank, the archive status of the dataset is set to the archived status.
     * @param origUrl
     *         original-data landing page URL to use; if null or blank, no changes are made to the original-data URL.
     *         If not null and not blank, the archive status of the dataset is set to the archived status.
     * @param enhancedDoi
     *         enhanced-data DOI to use; if null or blank, no changes are mode to the enhanced-data DOI
     * @param enhancedUrl
     *         enhanced-data landing page URL to use;
     *         if null or blank, no changes are mode to the enhanced-data landing URL
     * @param replaceOld
     *         replace existing information?
     *
     * @return message about what was done.
     *         If all DOIs and URLs are null or blank, null is returned.
     */
    public String updateReferencesForDataset(String expocode, String origDoi, String origUrl,
            String enhancedDoi, String enhancedUrl, boolean replaceOld) {
        String upperExpo;
        try {
            upperExpo = DashboardServerUtils.checkDatasetID(expocode);
        } catch ( IllegalArgumentException ex ) {
            return "invalid expocode " + expocode + ": " + ex.getMessage();
        }
        DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(upperExpo);
        if ( cruise == null )
            return "dataset " + upperExpo + " does not exist";

        String msg = null;
        boolean proceed = (replaceOld || (cruise.getSourceDOI().isEmpty() && cruise.getSourceURL().isEmpty()));
        if ( !proceed ) {
            msg = "existing original-data references for " + upperExpo + " was not changed";
        }
        else {
            if ( origDoi != null ) {
                String doi = origDoi.trim();
                if ( !doi.isEmpty() ) {
                    cruise.setSourceDOI(doi);
                    cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_ARCHIVED);
                    msg = "updated the original-data DOI for " + upperExpo + " to " + doi + " and marking as archived";
                }
            }
            if ( origUrl != null ) {
                String url = origUrl.trim();
                if ( !url.isEmpty() ) {
                    cruise.setSourceURL(url);
                    if ( msg != null )
                        msg += "\n";
                    else
                        msg = "";
                    msg += "updated the original-data URL for " + upperExpo + " to " + url + " and marking as archived";
                }
            }
        }

        proceed = (replaceOld || (cruise.getEnhancedDOI().isEmpty() && cruise.getEnhancedURL().isEmpty()));
        if ( !proceed ) {
            if ( msg != null )
                msg += "\n";
            else
                msg = "";
            msg += "existing SOCAT-enhanced references for " + upperExpo + " was not changed";
        }
        else {
            if ( enhancedDoi != null ) {
                String doi = enhancedDoi.trim();
                if ( !doi.isEmpty() ) {
                    cruise.setEnhancedDOI(doi);
                    if ( msg != null )
                        msg += "\n";
                    else
                        msg = "";
                    msg += "updated the SOCAT-enhanced DOI for " + upperExpo + " to " + doi;
                }
            }
            if ( enhancedUrl != null ) {
                String url = enhancedUrl.trim();
                if ( !url.isEmpty() ) {
                    cruise.setEnhancedURL(url);
                    if ( msg != null )
                        msg += "\n";
                    else
                        msg = "";
                    msg += "updated the SOCAT-enhanced URL for " + upperExpo + " to " + url;
                }
            }
        }

        dataHandler.saveDatasetInfoToFile(cruise, null);
        return msg;
    }

    /**
     * Creates and returns maps of expocodes to URLs and expocodes to DOIs in the specified file.
     * If multiple entries for an expocode are present, the URLs or DOIs are concatenated with
     * semicolons.  Ignored blank lines and lines that start with a #
     *
     * @param refsFilename
     *         name of the file containing the expocode / URL / DOI data
     * @param urlMap
     *         map of expocode to URL(s) to assign
     * @param doiMap
     *         map of expocode to DOI(s) to assign
     *
     * @throws IllegalArgumentException
     *         if a dataline (that is not blank and does not start with a #) does not
     *         contain a tab character, or if an invalid expocode, URL, or DOI is given
     * @throws IOException
     *         if opening or reading the expocode / DOI file throws one
     */
    private static void readRefsFromFile(String refsFilename, TreeMap<String,String> urlMap,
            TreeMap<String,String> doiMap) throws IllegalArgumentException, IOException {
        BufferedReader refsReader = new BufferedReader(new FileReader(refsFilename));
        try {
            String dataline = refsReader.readLine();
            while ( dataline != null ) {
                if ( !(dataline.trim().isEmpty() || dataline.startsWith("#")) ) {
                    String[] refs = dataline.split("\t");
                    if ( refs.length < 2 )
                        throw new IllegalArgumentException(dataline + ": not tab-separated values");

                    String upperExpo;
                    try {
                        upperExpo = DashboardServerUtils.checkDatasetID(refs[0].trim());
                    } catch ( IllegalArgumentException ex ) {
                        throw new IllegalArgumentException(dataline + ": invalid expocode - " + ex.getMessage());
                    }

                    String url = refs[1].trim();
                    if ( !url.isEmpty() ) {
                        if ( !url.startsWith("http") )
                            throw new IllegalArgumentException(dataline + ": invalid landing page URL");
                        String val = urlMap.get(upperExpo);
                        if ( val != null )
                            val += " ; " + url;
                        else
                            val = url;
                        urlMap.put(upperExpo, val);
                    }

                    if ( refs.length > 2 ) {
                        String doi = refs[2].trim().toUpperCase();
                        if ( !doi.isEmpty() ) {
                            if ( !DOI_PATTERN.matcher(doi).matches() )
                                throw new IllegalArgumentException(dataline + ": invalid DOI");
                            String val = doiMap.get(upperExpo);
                            if ( val != null )
                                val += " ; " + doi;
                            else
                                val = doi;
                            doiMap.put(upperExpo, val);
                        }
                    }
                }

                dataline = refsReader.readLine();
            }
        } finally {
            refsReader.close();
        }
    }

    /**
     * @param args
     *         SOCAT_Refs_File - file of expocodes with DOIs of the SOCAT-enhanced documents
     *         Orig_Refs_File - file of expocodes with DOIs of the original data documents
     */
    public static void main(String[] args) {
        if ( args.length != 3 ) {
            System.err.println();
            System.err.println("Arguments:  Orig_Refs.tsv  SOCAT_Refs.tsv  ReplaceOld");
            System.err.println();
            System.err.println("Updates the DOIs for the original and SOCAT-enhanced data documents.  Each ");
            System.err.println("line in the files should be an expocode, a tab character, the landing page URL ");
            System.err.println("for the dataset to assign, a tab character, and the DOI for the dataset to ");
            System.err.println("assign.  Blank lines or lines starting with a '#' are ignored.  Any blank URL ");
            System.err.println("or DOI values are ignored.  If ReplaceOld is True or T (case insensitive), ");
            System.err.println("then any existing information is replaced; otherwise, information is assigned ");
            System.err.println("only where no information is already present.");
            System.err.println();
            System.err.println("The default dashboard configuration (specified by the UPLOAD_DASHBOARD_SERVER_NAME ");
            System.err.println("environment variable) is used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String origDOIsFilename = args[0];
        String socatDOIsFilename = args[1];
        boolean replaceOld = ("True".equalsIgnoreCase(args[2]) || "T".equalsIgnoreCase(args[2]));


        // Create the maps of expocode to the original-data URL and original-data DOI
        TreeMap<String,String> origDOIMap = new TreeMap<String,String>();
        TreeMap<String,String> origURLMap = new TreeMap<String,String>();
        try {
            readRefsFromFile(origDOIsFilename, origURLMap, origDOIMap);
        } catch ( Exception ex ) {
            System.err.println("Error reading " + origDOIsFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        // Create the maps of expocode to the SOCAT URL and SOCAT DOI
        TreeMap<String,String> socatDOIMap = new TreeMap<String,String>();
        TreeMap<String,String> socatURLMap = new TreeMap<String,String>();
        try {
            readRefsFromFile(socatDOIsFilename, socatURLMap, socatDOIMap);
        } catch ( Exception ex ) {
            System.err.println("Error reading " + socatDOIsFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        TreeSet<String> exposSet = new TreeSet<String>();
        exposSet.addAll(origURLMap.keySet());
        exposSet.addAll(origDOIMap.keySet());
        exposSet.addAll(socatURLMap.keySet());
        exposSet.addAll(socatDOIMap.keySet());
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

        try {
            AddDatasetRefs updater = new AddDatasetRefs(configStore.getDataFileHandler());
            for (String expocode : exposSet) {
                String msg = updater.updateReferencesForDataset(expocode, origDOIMap.get(expocode),
                        origURLMap.get(expocode), socatDOIMap.get(expocode), socatURLMap.get(expocode),
                        replaceOld);
                if ( msg != null )
                    System.out.println(msg);
            }
        } finally {
            DashboardConfigStore.shutdown();
        }

        System.exit(0);
    }

}

