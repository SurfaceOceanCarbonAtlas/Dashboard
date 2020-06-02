package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdDataArray;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.zip.ZipOutputStream;

/**
 * Generates full cruise reports for public consumption.
 *
 * @author Karl Smith
 */
public class GenerateCruiseReports {

    private static final String ENHANCED_REPORT_NAME_EXTENSION = "_SOCAT_enhanced.tsv";
    private static final String NOT_AVAILABLE_TAG = "N/A";

    // SOCAT main DOI, DOI HRef, and publication citation
    private static final String SOCAT_MAIN_DOI = "10.1594/PANGAEA.905654";
    private static final String SOCAT_MAIN_URL = "https://doi.pangaea.de/10.1594/PANGAEA.905654";
    private static final String[] SOCAT_MAIN_CITATION = {
            "  D.C.E. Bakker, B. Pfeil, C.S. Landa, et. al.  \"A multi-decade ",
            "record of high quality fCO2 data in version 3 of the Surface Ocean ",
            "CO2 Atlas (SOCAT)\"  Earth System Science Data, 8(2), 383-413, 2016 ",
            "doi:10.5194/essd-8-383-2106"
    };

    // Expocodes for cruises missing hours and minutes (hour:minute time is 00:00)
    private static final TreeSet<String> DAY_RESOLUTION_CRUISE_EXPOCODES =
            new TreeSet<String>(Arrays.asList(
                    "06AQ19911114",
                    "06AQ19911210",
                    "06MT19920510",
                    "06MT19970106",
                    "06P119910616",
                    "06P119950901",
                    "316N19971005"
            ));

    private final DataFileHandler dataHandler;
    private final MetadataFileHandler metadataHandler;
    private final DsgNcFileHandler dsgFileHandler;
    private final KnownDataTypes knownMetadataTypes;
    private final KnownDataTypes knownDataFileTypes;
    private final String dateStamp;

    /**
     * For generating cruise reports from the data provided by the given dashboard configuration.
     *
     * @param configStore
     *         dashboard configuration to use
     */
    public GenerateCruiseReports(DashboardConfigStore configStore) {
        dataHandler = configStore.getDataFileHandler();
        metadataHandler = configStore.getMetadataFileHandler();
        dsgFileHandler = configStore.getDsgNcFileHandler();
        knownMetadataTypes = configStore.getKnownMetadataTypes();
        knownDataFileTypes = configStore.getKnownDataFileTypes();
        SimpleDateFormat timeStamper = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
        timeStamper.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateStamp = timeStamper.format(new Date());
    }

    public File getEnhancedZipBundleFile(String expocode, File filesDir) {
        if ( !filesDir.isDirectory() )
            throw new IllegalArgumentException(filesDir.getPath() + " is not a directory");
        String stdId = DashboardServerUtils.checkDatasetID(expocode);
        File parentFile = new File(filesDir, stdId.substring(0, 4));
        if ( !parentFile.isDirectory() ) {
            if ( parentFile.exists() )
                throw new IllegalArgumentException("File exists but is not a directory: " + parentFile.getPath());
            if ( !parentFile.mkdir() )
                throw new IllegalArgumentException("Problems creating the directory: " + parentFile.getPath());
        }
        return new File(parentFile, stdId + "_enhanced.zip");
    }

    /**
     * Generates a single-cruise enhanced data file, then bundles that report with all the metadata
     * documents for that dataset.  Use {@link #getEnhancedZipBundleFile(String, File)} to get the
     * virtual File of the created bundle.
     *
     * @param expocode
     *         create the bundle for the dataset with this ID
     * @param outputDir
     *         create the bundles under subdirectories of the directory
     *
     * @return the warning messages from generating the single-cruise enhanced data file
     *
     * @throws IllegalArgumentException
     *         if the expoocode is invalid
     * @throws IOException
     *         if unable to read the default DashboardConfigStore,
     *         if unable to create the enhanced data file, or
     *         in unable to create the bundle file
     */
    public ArrayList<String> createEnhancedFilesBundle(String expocode, File outputDir) throws
            IllegalArgumentException, IOException {
        // Generate the single-cruise SOCAT-enhanced data file
        File bundleFile = getEnhancedZipBundleFile(expocode, outputDir);
        File enhancedDataFile = new File(bundleFile.getParent(), expocode + ENHANCED_REPORT_NAME_EXTENSION);
        ArrayList<String> warnings = generateReport(expocode, enhancedDataFile);

        // Get the list of metadata documents to be bundled with this data file
        ArrayList<File> addlDocs = new ArrayList<File>();
        for (DashboardMetadata mdata : metadataHandler.getMetadataFiles(expocode)) {
            // Exclude the (expocode)/OME.xml document at this time;
            // do include the (expocode)/PI_OME.xml
            String filename = mdata.getFilename();
            if ( !filename.equals(DashboardServerUtils.OME_FILENAME) ) {
                addlDocs.add(metadataHandler.getMetadataFile(expocode, filename));
            }
        }

        // Generate the bundle as a zip file
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(bundleFile));
        try {
            ArchiveFilesBundler.copyFileToZipBundle(zipOut, expocode, enhancedDataFile);
            for (File metaFile : addlDocs) {
                ArchiveFilesBundler.copyFileToZipBundle(zipOut, expocode, metaFile);
            }
        } finally {
            zipOut.close();
        }
        // Leave the SOCAT-enhanced data file for sending just those files
        // enhancedDataFile.delete();

        return warnings;
    }

    /**
     * Generates a single-cruise-format data report (includes WOCE-3 and WOCE-4 data and
     * original-data CO2 measurements). If successful, any warnings about the generated
     * report are returned.
     *
     * @param expocode
     *         report the data of the cruise with this expocode
     * @param reportFile
     *         print the report to this file
     *
     * @return list of warnings about the generated report; never null but may be empty
     *
     * @throws IllegalArgumentException
     *         if the expocode is invalid,
     * @throws IOException
     *         if unable to read the DSG NC file, or if unable to create the cruise report file
     */
    public ArrayList<String> generateReport(String expocode, File reportFile)
            throws IllegalArgumentException, IOException {
        String upperExpo = DashboardServerUtils.checkDatasetID(expocode);
        ArrayList<String> warnMsgs = new ArrayList<String>();

        // Get the metadata and data from the DSG file
        DsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
        ArrayList<String> unknownVars = dsgFile.readMetadata(knownMetadataTypes);
        if ( unknownVars.size() > 0 ) {
            String msg = "Unassigned metadata variables: ";
            for (String var : unknownVars) {
                msg += var + "; ";
            }
            warnMsgs.add(msg);
        }
        unknownVars = dsgFile.readData(knownDataFileTypes);
        if ( unknownVars.size() > 0 ) {
            String msg = "Unassigned data variables: ";
            for (String var : unknownVars) {
                msg += var + "; ";
            }
            warnMsgs.add(msg);
        }

        // Get the SOCAT version and QC flag from the DSG metadata
        DsgMetadata socatMeta = dsgFile.getMetadata();
        String socatVersion = socatMeta.getVersion();
        String qcFlag = socatMeta.getDatasetQCFlag();

        // Get the rest of the metadata info from the OME XML
        DashboardOmeMetadata omeMeta = metadataHandler.getOmeFromFile(upperExpo, DashboardServerUtils.OME_FILENAME);

        // Get DOIs and URLs from data file properties
        DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(upperExpo);
        String origDoi = cruise.getSourceDOI();
        if ( DashboardUtils.STRING_MISSING_VALUE.equals(origDoi) )
            origDoi = NOT_AVAILABLE_TAG;
        String origUrl = cruise.getSourceURL();
        if ( DashboardUtils.STRING_MISSING_VALUE.equals(origUrl) )
            origUrl = NOT_AVAILABLE_TAG;
        /*
         * String socatDoi = cruise.getEnhancedDOI();
         * if ( DashboardUtils.STRING_MISSING_VALUE.equals(socatDoi) )
         *     socatDoi = NOT_AVAILABLE_TAG;
         * String socatUrl = cruise.getEnhancedURL();
         * if ( DashboardUtils.STRING_MISSING_VALUE.equals(socatUrl) )
         *     socatUrl = NOT_AVAILABLE_TAG;
         */

        // Get the list of additional document filenames associated with this cruise.
        // Use what the QC-ers see - the directory listing.
        TreeSet<String> addlDocs = new TreeSet<String>();
        for (DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo)) {
            if ( !mdata.getFilename().equals(DashboardServerUtils.OME_FILENAME) ) {
                addlDocs.add(mdata.getFilename());
            }
        }

        // Generate the report
        PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
        try {
            ArrayList<String> msgs = printMetadataPreamble(omeMeta, socatVersion,
                    origDoi, origUrl, qcFlag, addlDocs, report);
            warnMsgs.addAll(msgs);
            printDataTableHeader(report, false);
            printDataStrings(report, dsgFile.getStdDataArray(), upperExpo, socatVersion,
                    origDoi, qcFlag, null, false);
        } finally {
            report.close();
        }

        return warnMsgs;
    }

    /**
     * Generates a multi-cruise-format data report (does not include WOCE-3 and WOCE-4 data
     * nor original-data CO2 measurements).  If successful, any warnings about the generated
     * report are returned.
     *
     * @param expocodes
     *         report the data for the cruises with these datasetIds
     * @param regionID
     *         report only data in the region with this ID; if null, no region restriction is made on the data
     * @param reportFile
     *         print the report to this file
     *
     * @return list of warnings about the generated report; never null but may be empty
     *
     * @throws IllegalArgumentException
     *         if the region ID or a dataset ID is invalid,
     * @throws IOException
     *         if unable to read a DSG NC file, or if unable to create the cruise report file
     */
    public ArrayList<String> generateReport(TreeSet<String> expocodes, String regionID, File reportFile)
            throws IllegalArgumentException, IOException {
        int numDatasets = expocodes.size();
        ArrayList<String> upperExpoList = new ArrayList<String>(numDatasets);
        ArrayList<String> socatVersionList = new ArrayList<String>(numDatasets);
        ArrayList<String> origDoiList = new ArrayList<String>(numDatasets);
        ArrayList<String> origUrlList = new ArrayList<String>(numDatasets);
        /*
         * ArrayList<String> socatDoiList = new ArrayList<String>(numDatasets);
         * ArrayList<String> socatUrlList = new ArrayList<String>(numDatasets);
         */
        ArrayList<String> qcFlagList = new ArrayList<String>(numDatasets);
        ArrayList<String> warnMsgs = new ArrayList<String>(numDatasets);
        ArrayList<DashboardOmeMetadata> omeMetaList = new ArrayList<DashboardOmeMetadata>();

        String regionName;
        if ( regionID != null ) {
            regionName = DashboardServerUtils.REGION_NAMES.get(regionID);
            if ( regionName == null )
                throw new IllegalArgumentException("Unknown region ID of " + regionID);
        }
        else
            regionName = null;

        for (String expo : expocodes) {
            // Get the datasetIds, SOCAT version, and QC flags
            // of the datasets to report (checking region IDs, if appropriate)
            String upperExpo = DashboardServerUtils.checkDatasetID(expo);
            DsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
            ArrayList<String> unknownVars = dsgFile.readMetadata(knownMetadataTypes);
            if ( unknownVars.size() > 0 ) {
                String msg = upperExpo + " unknown metadata variables: ";
                for (String var : unknownVars) {
                    msg += var + "; ";
                }
                warnMsgs.add(msg);
            }
            boolean inRegion;
            if ( regionID != null ) {
                inRegion = false;
                dsgFile.readData(knownDataFileTypes);
                StdDataArray dataVals = dsgFile.getStdDataArray();
                Integer regionColIdx = dataVals.getIndexOfType(DashboardServerUtils.REGION_ID);
                if ( regionColIdx == null )
                    throw new IOException(DashboardServerUtils.REGION_ID.getVarName() +
                            " is not defined in the DSG file for " + upperExpo);
                Integer woceColIdx = dataVals.getIndexOfType(SocatTypes.WOCE_CO2_WATER);
                if ( woceColIdx == null )
                    throw new IOException(SocatTypes.WOCE_CO2_WATER.getVarName() +
                            " is not defined in the DSG file for " + upperExpo);
                for (int j = 0; j < dataVals.getNumSamples(); j++) {
                    // Ignore WOCE-3 and WOCE-4 as they are not reported
                    // and may be indicating invalid locations for this cruise
                    if ( regionID.equals(dataVals.getStdVal(j, regionColIdx)) &&
                            DashboardServerUtils.WOCE_ACCEPTABLE.equals(dataVals.getStdVal(j, woceColIdx)) ) {
                        inRegion = true;
                        break;
                    }
                }
            }
            else {
                inRegion = true;
            }
            if ( inRegion ) {
                DsgMetadata socatMeta = dsgFile.getMetadata();
                socatVersionList.add(socatMeta.getVersion());
                qcFlagList.add(socatMeta.getDatasetQCFlag());
                // get the DOIs and URLs from the data file properties
                DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(upperExpo);
                String value = cruise.getSourceDOI();
                if ( DashboardUtils.STRING_MISSING_VALUE.equals(value) )
                    value = NOT_AVAILABLE_TAG;
                origDoiList.add(value);
                value = cruise.getSourceURL();
                if ( DashboardUtils.STRING_MISSING_VALUE.equals(value) )
                    value = NOT_AVAILABLE_TAG;
                origUrlList.add(value);
                /*
                 * value = cruise.getEnhancedDOI();
                 * if ( DashboardUtils.STRING_MISSING_VALUE.equals(value) )
                 *     value = NOT_AVAILABLE_TAG;
                 * socatDoiList.add(value);
                 * value = cruise.getEnhancedURL();
                 * if ( DashboardUtils.STRING_MISSING_VALUE.equals(value) )
                 *     value = NOT_AVAILABLE_TAG;
                 * socatUrlList.add(value);
                 */
                omeMetaList.add(metadataHandler.getOmeFromFile(upperExpo, DashboardServerUtils.OME_FILENAME));
                upperExpoList.add(upperExpo);
            }
        }

        // Get the list of additional document filenames associated with this cruise.
        // Use what the QC-ers see - the directory listing.
        ArrayList<TreeSet<String>> addlDocsList = new ArrayList<TreeSet<String>>();
        for (String upperExpo : upperExpoList) {
            TreeSet<String> addlDocs = new TreeSet<String>();
            for (DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo)) {
                // Ignore the OME.xml stub at this time, but include any PI_OME.xml since that is what was uploaded
                if ( !mdata.getFilename().equals(DashboardServerUtils.OME_FILENAME) ) {
                    addlDocs.add(mdata.getFilename());
                }
            }
            addlDocsList.add(addlDocs);
        }

        PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
        try {
            ArrayList<String> msgs = printMetadataPreamble(regionName, omeMetaList, socatVersionList,
                    origDoiList, origUrlList, qcFlagList, addlDocsList, report);
            warnMsgs.addAll(msgs);
            printDataTableHeader(report, true);
            // Read and report the data for one dataset at a time
            for (int k = 0; k < upperExpoList.size(); k++) {
                String upperExpo = upperExpoList.get(k);
                DsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
                ArrayList<String> unknownVars = dsgFile.readData(knownDataFileTypes);
                if ( unknownVars.size() > 0 ) {
                    String msg = upperExpo + " unknown data variables: ";
                    for (String var : unknownVars) {
                        msg += var + "; ";
                    }
                    warnMsgs.add(msg);
                }
                printDataStrings(report, dsgFile.getStdDataArray(), upperExpo, socatVersionList.get(k),
                        origDoiList.get(k), qcFlagList.get(k), regionID, true);
            }
        } finally {
            report.close();
        }

        return warnMsgs;
    }

    /**
     * Prints the metadata preamble for a single-cruise report.
     * If successful, any warnings about the generated preamble are returned.
     *
     * @param omeMeta
     *         OME XML document with metadata values to report in the preamble
     * @param socatVersion
     *         SOCAT version to report in the preamble
     * @param origDoi
     *         DOI for the original data
     * @param origUrl
     *         URL for the landing page of the original data
     * @param qcFlag
     *         QC flag to report in the preamble
     * @param addlDocs
     *         filenames of additional documents to report in the preamble
     * @param report
     *         print with this PrintWriter
     *
     * @return list of warnings about the generated preamble; never null but may be empty
     */
    private ArrayList<String> printMetadataPreamble(DashboardOmeMetadata omeMeta, String socatVersion,
            String origDoi, String origUrl, String qcFlag, TreeSet<String> addlDocs, PrintWriter report) {
        String upperExpo = omeMeta.getDatasetId();
        ArrayList<String> warnMsgs = new ArrayList<String>();

        report.println("SOCAT data report created: " + dateStamp);
        report.println("Expocode: " + upperExpo);
        report.println("version: " + socatVersion);
        report.println("Dataset Name: " + omeMeta.getDatasetName());
        report.println("Platform Name: " + omeMeta.getPlatformName());
        report.println("Principal Investigator(s): " + omeMeta.getPINames());
        report.println("DOI for the original data: " + origDoi);
        report.println("    or see: " + origUrl);
        /*
         * report.println("DOI of this SOCAT-enhanced data: " + socatDoi);
         * report.println("    or see: " + socatUrl);
         */
        report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
        report.println("    or see: " + SOCAT_MAIN_URL);
        report.println();

        // Additional references - add expocode suffix for clarity
        report.println("Supplemental documentation reference(s):");
        for (String filename : addlDocs) {
            report.println("    " + upperExpo + "/" + filename);
        }
        report.println();

        // Longitude range in [180W,180E]
        double westLon;
        double eastLon;
        try {
            westLon = omeMeta.getWestmostLongitude();
            eastLon = omeMeta.getEastmostLongitude();
            if ( westLon < 0.0 )
                report.format("Longitude range: %#.2fW", -1.0 * westLon);
            else
                report.format("Longitude range: %#.2fE", westLon);
            if ( eastLon < 0.0 )
                report.format(" to %#.2fW", -1.0 * eastLon);
            else
                report.format(" to %#.2fE", eastLon);
            report.println();
        } catch ( Exception ex ) {
            warnMsgs.add(ex.getMessage());
        }

        // Latitude range in [90S,90N]
        double southLat;
        double northLat;
        try {
            southLat = omeMeta.getSouthmostLatitude();
            northLat = omeMeta.getNorthmostLatitude();
            if ( southLat < 0.0 )
                report.format("Latitude range: %#.2fS", -1.0 * southLat);
            else
                report.format("Latitude range: %#.2fN", southLat);
            if ( northLat < 0.0 )
                report.format(" to %#.2fS", -1.0 * northLat);
            else
                report.format(" to %#.2fN", northLat);
            report.println();
        } catch ( Exception ex ) {
            warnMsgs.add(ex.getMessage());
        }

        // Time range
        String startDatestamp;
        String endDatestamp;
        try {
            startDatestamp = omeMeta.getBeginDatestamp();
            endDatestamp = omeMeta.getEndDatestamp();
            report.println("Time range: " + startDatestamp + " to " + endDatestamp);
        } catch ( Exception ex ) {
            warnMsgs.add(ex.getMessage());
        }

        // Check if this is a day-resolution cruise whose hour, minutes, and seconds
        // were approximated and reset in the database
        if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(upperExpo) ) {
            report.println();
            report.println("Observation times were not provided to a resolution of hours;");
            report.println("the hours, minutes, and seconds given are artificially generated values");
            warnMsgs.add("Data set was marked as having artificial hours, minutes, and seconds");
        }
        report.println();
        report.println("Data set QC flag: " + qcFlag + " (see below)");
        report.println();

        return warnMsgs;
    }

    public static final String MULTI_CRUISE_METADATA_REPORT_HEADER = "Expocode\t" +
            "version\t" +
            "Dataset Name\t" +
            "Platform Name\t" +
            "PI(s)\t" +
            "Data Source DOI\t" +
            "Data Source Reference\t" +
            // "SOCAT DOI\t" +
            // "SOCAT Reference\t" +
            "Westmost Longitude\t" +
            "Eastmost Longitude\t" +
            "Southmost Latitude\t" +
            "Northmost Latitude\t" +
            "Start Time\t" +
            "End Time\t" +
            "QC Flag\t" +
            "Additional Metadata Document(s)";

    /**
     * Prints the metadata preamble for a multi-cruise report.
     * If successful, any warnings about the generated preamble are returned.
     *
     * @param regionName
     *         name of the region being reported; use null for no region
     * @param omeMetaList
     *         list of metadata values, one per dataset, to use for information to report
     * @param socatVersionList
     *         list of SOCAT version numbers, one per dataset, to report
     * @param origDoiList
     *         list of DOIs, one per dataset, for the original data
     * @param origUrlList
     *         list of landing page URLs, one per dataset, for the original data
     * @param qcFlagList
     *         list of QC flags, one per dataset, to report
     * @param addlDocsList
     *         list of additional documents, one set of names per dataset, to report
     * @param report
     *         print with this PrintWriter
     *
     * @return list of warnings about the generated preamble; never null but may be empty
     */
    private ArrayList<String> printMetadataPreamble(String regionName, ArrayList<DashboardOmeMetadata> omeMetaList,
            ArrayList<String> socatVersionList, ArrayList<String> origDoiList, ArrayList<String> origUrlList,
            ArrayList<String> qcFlagList, ArrayList<TreeSet<String>> addlDocsList, PrintWriter report) {
        ArrayList<String> warnMsgs = new ArrayList<String>();

        report.println("SOCAT data report created: " + dateStamp);
        report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
        report.println("    or see: " + SOCAT_MAIN_URL);
        if ( regionName == null )
            report.println("SOCAT data for the following data sets:");
        else
            report.println("SOCAT data in SOCAT region \"" + regionName + "\" for the following data sets:");
        report.println(MULTI_CRUISE_METADATA_REPORT_HEADER);
        boolean needsFakeHoursMsg = false;
        for (int k = 0; k < omeMetaList.size(); k++) {
            DashboardOmeMetadata omeMeta = omeMetaList.get(k);

            String upperExpo = omeMeta.getDatasetId();
            report.print(upperExpo);
            report.print("\t");

            report.print(socatVersionList.get(k));
            report.print("\t");

            String cruiseName = omeMeta.getDatasetName();
            if ( cruiseName.isEmpty() )
                cruiseName = NOT_AVAILABLE_TAG;
            report.print(cruiseName);
            report.print("\t");

            report.print(omeMeta.getPlatformName());
            report.print("\t");

            report.print(omeMeta.getPINames());
            report.print("\t");

            report.print(origDoiList.get(k));
            report.print("\t");

            report.print(origUrlList.get(k));
            report.print("\t");

            /*
             * report.print(socatDoiList.get(k));
             * report.print("\t");
             *
             * report.print(socatUrlList.get(k));
             * report.print("\t");
             */

            try {
                double westLon = omeMeta.getWestmostLongitude();
                if ( westLon < 0.0 ) {
                    report.format("%#.2fW", -1.0 * westLon);
                }
                else {
                    report.format("%#.2fE", westLon);
                }
            } catch ( Exception ex ) {
                // Leave blank
                warnMsgs.add("Invalid west-most longitude for " + upperExpo);
            }
            report.print("\t");

            try {
                double eastLon = omeMeta.getEastmostLongitude();
                if ( eastLon < 0.0 ) {
                    report.format("%#.2fW", -1.0 * eastLon);
                }
                else {
                    report.format("%#.2fE", eastLon);
                }
            } catch ( Exception ex ) {
                // Leave blank
                warnMsgs.add("Invalid east-most longitude for " + upperExpo);
            }
            report.print("\t");

            try {
                double southLat = omeMeta.getSouthmostLatitude();
                if ( southLat < 0.0 ) {
                    report.format("%#.2fS", -1.0 * southLat);
                }
                else {
                    report.format("%#.2fN", southLat);
                }
            } catch ( Exception ex ) {
                // Leave blank
                warnMsgs.add("Invalid south-most latitude for " + upperExpo);
            }
            report.print("\t");

            try {
                double northLat = omeMeta.getNorthmostLatitude();
                if ( northLat < 0.0 ) {
                    report.format("%#.2fS", -1.0 * northLat);
                }
                else {
                    report.format("%#.2fN", northLat);
                }
            } catch ( Exception ex ) {
                // Leave blank
                warnMsgs.add("Invalid north-most latitude for " + upperExpo);
            }
            report.print("\t");

            try {
                report.print(omeMeta.getBeginDatestamp());
            } catch ( Exception ex ) {
                // Leave blank
                warnMsgs.add("Invalid beginning date for " + upperExpo);
            }
            report.print("\t");

            try {
                report.print(omeMeta.getEndDatestamp());
            } catch ( Exception ex ) {
                // Leave blank
                warnMsgs.add("Invalid ending date for " + upperExpo);
            }
            report.print("\t");

            report.print(qcFlagList.get(k));
            report.print("\t");

            boolean isFirst = true;
            for (String filename : addlDocsList.get(k)) {
                if ( isFirst )
                    isFirst = false;
                else
                    report.print("; ");
                report.print(upperExpo + "/" + filename);
            }

            report.println();

            if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(upperExpo) )
                needsFakeHoursMsg = true;
        }

        if ( needsFakeHoursMsg ) {
            boolean isFirst = true;
            report.print("Note for data set(s): ");
            for (DashboardOmeMetadata omeMeta : omeMetaList) {
                String upperExpo = omeMeta.getDatasetId();
                if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(upperExpo) ) {
                    if ( isFirst )
                        isFirst = false;
                    else
                        report.print(", ");
                    report.print(upperExpo);
                    warnMsgs.add(upperExpo + " was marked as having artificial hours, minutes, and seconds");
                }
            }
            report.println("    Observation times were not provided to a resolution of hours;");
            report.println("    the hours, minutes, and seconds given are artificially generated values");
        }

        report.println();

        return warnMsgs;
    }

    /**
     * Prints the data table header.  This includes the explanation of the data columns as well as
     * the SOCAT reference and, if appropriate, the salinity used if WOA_SSS is needed but missing.
     *
     * @param report
     *         print to this PrintWriter
     * @param multicruise
     *         is this header for a multi-cruise report (only data with fCO2_rec given and
     *         WOCE-flag 2 or not given, do not include original-data CO2 measurement columns) ?
     */
    private void printDataTableHeader(PrintWriter report, boolean multicruise) {
        report.println("Explanation of data columns:");
        if ( multicruise ) {
            for (String explantion : MULTI_CRUISE_DATA_REPORT_EXPLANATIONS) {
                report.println(explantion);
            }
        }
        else {
            for (String explantion : SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS) {
                report.println(explantion);
            }
        }
        report.println();
        report.println("The quality assessments given by the QC flag and fCO2rec_flag only apply to");
        report.println("the fCO2rec value.  For more information about the recomputed fCO2 value and");
        report.println("the meaning of the QC flag, fCO2rec_src, and fCO2rec_flag values, see:");
        for (String info : SOCAT_MAIN_CITATION) {
            report.println(info);
        }
        if ( multicruise ) {
            report.println();
            report.println("This is a report of only data points with recomputed fCO2 values");
            report.println("which were deemed acceptable (WOCE flag 2). ");
        }
        else {
            report.println();
            report.println("This is a report of all data points, including those with missing");
            report.println("recomputed fCO2 values and those with a WOCE flag indicating questionable (3)");
            report.println("or bad (4) recomputed fCO2 values.");
        }
        report.println();
        report.println("Terms of use of this data are given in the SOCAT Fair Data Use Statement ");
        report.println("http://www.socat.info/SOCAT_fair_data_use_statement.htm");
        if ( !multicruise ) {
            report.println();
            report.println("All standard SOCAT data columns are reported in this file,");
            report.println("even if all values are missing ('NaN') for this data set.");
        }
        report.println();

        if ( multicruise )
            report.println(MULTI_CRUISE_DATA_REPORT_HEADER);
        else
            report.println(SINGLE_CRUISE_DATA_REPORT_HEADER);
    }

    /**
     * Explanation lines for the data columns given by the header string {@link #SINGLE_CRUISE_DATA_REPORT_HEADER}
     * and data printed by the single-cruise version of {@link #printDataStrings}
     */
    private static final String[] SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS = {
            "Expocode: unique identifier for the data set from which this data was obtained",
            "version: version of SOCAT where this enhanced data first appears",
            "Source_DOI: DOI for the data source (the original data)",
            // "SOCAT_DOI: DOI for this SOCAT-enhanced data",
            "QC_Flag: Data set QC flag",
            "yr: 4-digit year of the time (UTC) of the measurement",
            "mon: month of the time (UTC) of the measurement",
            "day: day of the time (UTC) of the measurement",
            "hh: hour of the time (UTC) of the measurement",
            "mm: minute of the time (UTC) of the measurement",
            "ss: second of the time (UTC) of the measurement (may include decimal places)",
            "longitude: measurement longitude, from zero to 360, in decimal degrees East",
            "latitude: measurement latitude in decimal degrees North",
            "sample_depth: water sampling depth in meters",
            "sal: measured sea surface salinity on the Practical Salinity Scale",
            "SST: measured sea surface temperature in degrees Celcius",
            "Tequ: equilibrator chamber temperature in degrees Celcius",
            "PPPP: measured atmospheric pressure in hectopascals",
            "Pequ: equilibrator chamber pressure in hectopascals",
            "WOA_SSS: sea surface salinity on the Practical Salinity Scale interpolated from the",
            "    World Ocean Atlas 2005 (see: http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)",
            "NCEP_SLP: sea level pressure in hectopascals interpolated from the NCEP/NCAR Reanalysis Project",
            "    (see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)",
            "ETOPO2_depth: bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded ",
            "    Global Relief Data (see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)",
            "dist_to_land: estimated distance to major land mass in kilometers (up to 1000 km)",
            "GVCO2: atmospheric xCO2 in micromole per mole interpolated from NOAA Greenhouse Gas Reference ",
            "    1979-01-01 to 2019-01-01 surface CO2 data (see: https://www.esrl.noaa.gov/gmd/ccgg/mbl/data.php)",
            "xCO2water_equ_dry: measured xCO2 (water) in micromole per mole at equilibrator temperature (dry air)",
            "xCO2water_SST_dry: measured xCO2 (water) in micromole per mole at sea surface temperature (dry air)",
            "pCO2water_equ_wet: measured pCO2 (water) in microatmospheres at equilibrator temperature (wet air)",
            "pCO2water_SST_wet: measured pCO2 (water) in microatmospheres at sea surface temperature (wet air)",
            "fCO2water_equ_wet: measured fCO2 (water) in microatmospheres at equilibrator temperature (wet air)",
            "fCO2water_SST_wet: measured fCO2 (water) in microatmospheres at sea surface temperature (wet air)",
            "fCO2rec: fCO2 in microatmospheres recomputed from the measured CO2 data (see below)",
            "fCO2rec_src: algorithm for generating fCO2rec from the measured CO2 data (0:not generated; 1-14, see below)",
            "fCO2rec_flag: WOCE flag for this fCO2rec value (2:good, 3:questionable, 4:bad, 9:not generated; see below)",
            "",
            "Missing values are indicated by 'NaN'"
    };

    /**
     * Tab-separated data column names for the data printed by the single-cruise version of
     * {@link #printDataStrings}.  Made public just for unit testing.
     */
    public static final String SINGLE_CRUISE_DATA_REPORT_HEADER = "Expocode\t" +
            "version\t" +
            "Source_DOI\t" +
            // "SOCAT_DOI\t" +
            "QC_Flag\t" +
            "yr\t" +
            "mon\t" +
            "day\t" +
            "hh\t" +
            "mm\t" +
            "ss\t" +
            "longitude [dec.deg.E]\t" +
            "latitude [dec.deg.N]\t" +
            "sample_depth [m]\t" +
            "sal\t" +
            "SST [deg.C]\t" +
            "Tequ [deg.C]\t" +
            "PPPP [hPa]\t" +
            "Pequ [hPa]\t" +
            "WOA_SSS\t" +
            "NCEP_SLP [hPa]\t" +
            "ETOPO2_depth [m]\t" +
            "dist_to_land [km]\t" +
            "GVCO2 [umol/mol]\t" +
            "xCO2water_equ_dry [umol/mol]\t" +
            "xCO2water_SST_dry [umol/mol]\t" +
            "pCO2water_equ_wet [uatm]\t" +
            "pCO2water_SST_wet [uatm]\t" +
            "fCO2water_equ_wet [uatm]\t" +
            "fCO2water_SST_wet [uatm]\t" +
            "fCO2rec [uatm]\t" +
            "fCO2rec_src\t" +
            "fCO2rec_flag";

    /**
     * Explanation lines for the data columns given by the header string {@link #MULTI_CRUISE_DATA_REPORT_HEADER}
     * and data printed by the multi-cruise version of {@link #printDataStrings}
     */
    private static final String[] MULTI_CRUISE_DATA_REPORT_EXPLANATIONS = {
            "Expocode: unique identifier for the data set from which this data was obtained",
            "version: version of SOCAT where this enhanced data first appears",
            "Source_DOI: DOI for the data source (the original data)",
            // "SOCAT_DOI: DOI for this SOCAT-enhanced data",
            "QC_Flag: Data set QC flag",
            "yr: 4-digit year of the time (UTC) of the measurement",
            "mon: month of the time (UTC) of the measurement",
            "day: day of the time (UTC) of the measurement",
            "hh: hour of the time (UTC) of the measurement",
            "mm: minute of the time (UTC) of the measurement",
            "ss: second of the time (UTC) of the measurement (may include decimal places)",
            "longitude: measurement longitude, from zero to 360, in decimal degrees East",
            "latitude: measurement latitude in decimal degrees North",
            "sample_depth: water sampling depth in meters",
            "sal: measured sea surface salinity on the Practical Salinity Scale",
            "SST: measured sea surface temperature in degrees Celcius",
            "Tequ: equilibrator chamber temperature in degrees Celcius",
            "PPPP: measured atmospheric pressure in hectopascals",
            "Pequ: equilibrator chamber pressure in hectopascals",
            "WOA_SSS: sea surface salinity on the Practical Salinity Scale interpolated from the",
            "    World Ocean Atlas 2005 (see: http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)",
            "NCEP_SLP: sea level pressure in hectopascals interpolated from the NCEP/NCAR Reanalysis Project",
            "    (see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)",
            "ETOPO2_depth: bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded ",
            "    Global Relief Data (see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)",
            "dist_to_land: estimated distance to major land mass in kilometers (up to 1000 km)",
            "GVCO2: atmospheric xCO2 in micromole per mole interpolated from NOAA Greenhouse Gas Reference ",
            "    1979-01-01 to 2019-01-01 surface CO2 data (see: https://www.esrl.noaa.gov/gmd/ccgg/mbl/data.php)",
            "xCO2water_equ_dry: measured xCO2 (water) in micromole per mole at equilibrator temperature (dry air)",
            "xCO2water_SST_dry: measured xCO2 (water) in micromole per mole at sea surface temperature (dry air)",
            "pCO2water_equ_wet: measured pCO2 (water) in microatmospheres at equilibrator temperature (wet air)",
            "pCO2water_SST_wet: measured pCO2 (water) in microatmospheres at sea surface temperature (wet air)",
            "fCO2water_equ_wet: measured fCO2 (water) in microatmospheres at equilibrator temperature (wet air)",
            "fCO2water_SST_wet: measured fCO2 (water) in microatmospheres at sea surface temperature (wet air)",
            "fCO2rec: fCO2 in microatmospheres recomputed from the raw data (see below)",
            "fCO2rec_src: algorithm for generating fCO2rec from the raw data (0:not generated; 1-14, see below)",
            "fCO2rec_flag: WOCE flag for this fCO2rec value (2:good, 3:questionable, 4:bad, 9:not generated; see below)",
            "",
            "Missing values are indicated by 'NaN'"
    };

    /**
     * Tab-separated data column names for the data printed by the multi-cruise version of
     * {@link #printDataStrings)}.  Made public just for unit testing.
     */
    public static final String MULTI_CRUISE_DATA_REPORT_HEADER = "Expocode\t" +
            "version\t" +
            "Source_DOI\t" +
            // "SOCAT_DOI\t" +
            "QC_Flag\t" +
            "yr\t" +
            "mon\t" +
            "day\t" +
            "hh\t" +
            "mm\t" +
            "ss\t" +
            "longitude [dec.deg.E]\t" +
            "latitude [dec.deg.N]\t" +
            "sample_depth [m]\t" +
            "sal\t" +
            "SST [deg.C]\t" +
            "Tequ [deg.C]\t" +
            "PPPP [hPa]\t" +
            "Pequ [hPa]\t" +
            "WOA_SSS\t" +
            "NCEP_SLP [hPa]\t" +
            "ETOPO2_depth [m]\t" +
            "dist_to_land [km]\t" +
            "GVCO2 [umol/mol]\t" +
            "xCO2water_equ_dry [umol/mol]\t" +
            "xCO2water_SST_dry [umol/mol]\t" +
            "pCO2water_equ_wet [uatm]\t" +
            "pCO2water_SST_wet [uatm]\t" +
            "fCO2water_equ_wet [uatm]\t" +
            "fCO2water_SST_wet [uatm]\t" +
            "fCO2rec [uatm]\t" +
            "fCO2rec_src\t" +
            "fCO2rec_flag";

    /**
     * @param report
     *         print the data strings here
     * @param dataVals
     *         all standardized values for this SOCAT-enhanced dataset
     * @param expocode
     *         ID for this dataset
     * @param version
     *         version for this SOCAT-enhanced dataset
     * @param origDoi
     *         DOI for the original dataset
     * @param qcFlag
     *         dataset QC flag value for this dataset
     * @param regionID
     *         if not null, restrict data reported to those in the region with this ID;
     *         ignored if multicruise is false
     * @param multicruise
     *         if true, print multi-cruise data strings (no original-data CO2 measurements,
     *         only WOCE-2 data points, eliminate duplicate data points);
     *         if false, print single-cruise data strings
     */
    private void printDataStrings(PrintWriter report, StdDataArray dataVals, String expocode, String version,
            String origDoi, String qcFlag, String regionID, boolean multicruise) throws IOException {
        // Indices to data columns that will be reported
        // All data columns should exist, although they may not have any valid values
        Integer longitudeIdx = dataVals.getIndexOfType(DashboardServerUtils.LONGITUDE);
        if ( longitudeIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.LONGITUDE.getVarName());

        Integer latitudeIdx = dataVals.getIndexOfType(DashboardServerUtils.LATITUDE);
        if ( latitudeIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.LATITUDE.getVarName());

        Integer yearIdx = dataVals.getIndexOfType(DashboardServerUtils.YEAR);
        if ( yearIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.YEAR.getVarName());

        Integer monthOfYearIdx = dataVals.getIndexOfType(DashboardServerUtils.MONTH_OF_YEAR);
        if ( monthOfYearIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.MONTH_OF_YEAR.getVarName());

        Integer dayOfMonthIdx = dataVals.getIndexOfType(DashboardServerUtils.DAY_OF_MONTH);
        if ( dayOfMonthIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.DAY_OF_MONTH.getVarName());

        Integer hourOfDayIdx = dataVals.getIndexOfType(DashboardServerUtils.HOUR_OF_DAY);
        if ( hourOfDayIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.HOUR_OF_DAY.getVarName());

        Integer minOfHourIdx = dataVals.getIndexOfType(DashboardServerUtils.MINUTE_OF_HOUR);
        if ( minOfHourIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.MINUTE_OF_HOUR.getVarName());

        Integer secOfMinIdx = dataVals.getIndexOfType(DashboardServerUtils.SECOND_OF_MINUTE);
        if ( secOfMinIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.SECOND_OF_MINUTE.getVarName());

        Integer salIdx = dataVals.getIndexOfType(SocatTypes.SALINITY);
        if ( salIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.SALINITY.getVarName());

        Integer woaSalIdx = dataVals.getIndexOfType(SocatTypes.WOA_SALINITY);
        if ( woaSalIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.WOA_SALINITY.getVarName());

        Integer tequIdx = dataVals.getIndexOfType(SocatTypes.TEQU);
        if ( tequIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.TEQU.getVarName());

        Integer sstIdx = dataVals.getIndexOfType(SocatTypes.SST);
        if ( sstIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.SST.getVarName());

        Integer pequIdx = dataVals.getIndexOfType(SocatTypes.PEQU);
        if ( pequIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.PEQU.getVarName());

        Integer patmIdx = dataVals.getIndexOfType(SocatTypes.PATM);
        if ( patmIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.PATM.getVarName());

        Integer ncepSLPIdx = dataVals.getIndexOfType(SocatTypes.NCEP_SLP);
        if ( ncepSLPIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.NCEP_SLP.getVarName());

        Integer depthIdx = dataVals.getIndexOfType(DashboardServerUtils.SAMPLE_DEPTH);
        if ( depthIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + DashboardServerUtils.SAMPLE_DEPTH.getVarName());

        Integer etopoDepthIdx = dataVals.getIndexOfType(SocatTypes.ETOPO2_DEPTH);
        if ( etopoDepthIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.ETOPO2_DEPTH.getVarName());

        Integer distToLandIdx = dataVals.getIndexOfType(SocatTypes.DIST_TO_LAND);
        if ( distToLandIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.DIST_TO_LAND.getVarName());

        Integer gvco2Idx = dataVals.getIndexOfType(SocatTypes.GVCO2);
        if ( gvco2Idx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.GVCO2.getVarName());

        Integer fco2RecIdx = dataVals.getIndexOfType(SocatTypes.FCO2_REC);
        if ( fco2RecIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.FCO2_REC.getVarName());

        Integer fco2SrcIdx = dataVals.getIndexOfType(SocatTypes.FCO2_SOURCE);
        if ( fco2SrcIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.FCO2_SOURCE.getVarName());

        Integer woceWaterIdx = dataVals.getIndexOfType(SocatTypes.WOCE_CO2_WATER);
        if ( woceWaterIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.WOCE_CO2_WATER.getVarName());

        // For multicruise reports, remove duplicate data points.
        // Region restrictions, if any, only apply to multicruise reports.
        TreeSet<DataPoint> prevDatPts = null;
        Integer sectimeIdx = null;
        Integer regionIdx = null;
        if ( multicruise ) {
            prevDatPts = new TreeSet<DataPoint>();

            // Need time to tests for duplicates in multicruise reports
            sectimeIdx = dataVals.getIndexOfType(DashboardServerUtils.TIME);
            if ( sectimeIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + DashboardServerUtils.TIME.getVarName());

            // Need region_id if region-specific multicruise report
            if ( regionID != null ) {
                regionIdx = dataVals.getIndexOfType(DashboardServerUtils.REGION_ID);
                if ( regionIdx == null )
                    throw new IOException("The DSG file for " + expocode +
                            " does not contain the variable " + DashboardServerUtils.REGION_ID.getVarName());
            }
            else
                regionIdx = null;
        }

        Integer xco2WaterTEquIdx = dataVals.getIndexOfType(SocatTypes.XCO2_WATER_TEQU_DRY);
        if ( xco2WaterTEquIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.XCO2_WATER_TEQU_DRY.getVarName());

        Integer xco2WaterSSTIdx = dataVals.getIndexOfType(SocatTypes.XCO2_WATER_SST_DRY);
        if ( xco2WaterSSTIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.XCO2_WATER_SST_DRY.getVarName());

        Integer pco2WaterTEquIdx = dataVals.getIndexOfType(SocatTypes.PCO2_WATER_TEQU_WET);
        if ( pco2WaterTEquIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.PCO2_WATER_TEQU_WET.getVarName());

        Integer pco2WaterSSTIdx = dataVals.getIndexOfType(SocatTypes.PCO2_WATER_SST_WET);
        if ( pco2WaterSSTIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.PCO2_WATER_SST_WET.getVarName());

        Integer fco2WaterTEquIdx = dataVals.getIndexOfType(SocatTypes.FCO2_WATER_TEQU_WET);
        if ( fco2WaterTEquIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.FCO2_WATER_TEQU_WET.getVarName());

        Integer fco2WaterSSTIdx = dataVals.getIndexOfType(SocatTypes.FCO2_WATER_SST_WET);
        if ( fco2WaterSSTIdx == null )
            throw new IOException("The DSG file for " + expocode +
                    " does not contain the variable " + SocatTypes.FCO2_WATER_SST_WET.getVarName());

        for (int j = 0; j < dataVals.getNumSamples(); j++) {
            if ( multicruise ) {
                // First check if this data sample should even be considered
                if ( (regionID != null) && !regionID.equals(dataVals.getStdVal(j, regionIdx)) )
                    continue;
                Double fco2Rec = (Double) dataVals.getStdVal(j, fco2RecIdx);
                if ( fco2Rec == null )
                    continue;
                String woceValue = (String) dataVals.getStdVal(j, woceWaterIdx);
                if ( !((woceValue == null) || woceValue.trim().isEmpty() ||
                        DashboardServerUtils.WOCE_ACCEPTABLE.equals(woceValue)) )
                    continue;
                // Valid for multicruise report; now check that it is not a duplicate
                DataPoint datpt = new DataPoint(expocode, (Double) dataVals.getStdVal(j, sectimeIdx),
                        (Double) dataVals.getStdVal(j, latitudeIdx), (Double) dataVals.getStdVal(j, longitudeIdx),
                        (Double) dataVals.getStdVal(j, sstIdx), (Double) dataVals.getStdVal(j, salIdx), fco2Rec);
                if ( !prevDatPts.add(datpt) ) {
                    System.err.println("Ignored duplicate datapoint for " + expocode + ": " + datpt.toString());
                    continue;
                }
            }

            // Generate the string for this data point
            Formatter fmtr = new Formatter();
            fmtr.format("%s\t", expocode);
            fmtr.format("%s\t", version);
            fmtr.format("%s\t", origDoi);
            // fmtr.format("%s\t", socatDoi);
            fmtr.format("%s\t", qcFlag);

            Object value;

            value = dataVals.getStdVal(j, yearIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%04d\t", (Integer) value);

            value = dataVals.getStdVal(j, monthOfYearIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%02d\t", (Integer) value);

            value = dataVals.getStdVal(j, dayOfMonthIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%02d\t", (Integer) value);

            value = dataVals.getStdVal(j, hourOfDayIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%02d\t", (Integer) value);

            value = dataVals.getStdVal(j, minOfHourIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%02d\t", (Integer) value);

            value = dataVals.getStdVal(j, secOfMinIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#03.0f\t", (Double) value);

            value = dataVals.getStdVal(j, longitudeIdx);
            if ( value == null ) {
                fmtr.format("NaN\t");
            }
            else {
                Double dblVal = (Double) value;
                while ( dblVal < 0.0 ) {
                    dblVal += 360.0;
                }
                while ( dblVal >= 360.0 ) {
                    dblVal -= 360.0;
                }
                fmtr.format("%#.5f\t", dblVal);
            }

            value = dataVals.getStdVal(j, latitudeIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.5f\t", (Double) value);

            value = dataVals.getStdVal(j, depthIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.0f\t", (Double) value);

            value = dataVals.getStdVal(j, salIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, sstIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, tequIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, patmIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, pequIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, woaSalIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, ncepSLPIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, etopoDepthIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.0f\t", (Double) value);

            value = dataVals.getStdVal(j, distToLandIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.0f\t", (Double) value);

            value = dataVals.getStdVal(j, gvco2Idx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, xco2WaterTEquIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, xco2WaterSSTIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, pco2WaterTEquIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, pco2WaterSSTIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, fco2WaterTEquIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, fco2WaterSSTIdx);
            if ( value == null )
                fmtr.format("NaN\t");
            else
                fmtr.format("%#.3f\t", (Double) value);

            value = dataVals.getStdVal(j, fco2RecIdx);
            if ( value == null ) {
                fmtr.format("NaN\t");
                // if fCO2_rec not given, always set source to zero
                fmtr.format("0\t");
                // if fCO2_rec not given, always set WOCE flag to nine ("bottle not sampled");
                fmtr.format("9");
            }
            else {
                fmtr.format("%#.3f\t", (Double) value);

                value = dataVals.getStdVal(j, fco2SrcIdx);
                if ( value == null )
                    fmtr.format("0\t");
                else
                    fmtr.format("%d\t", (Integer) value);

                value = dataVals.getStdVal(j, woceWaterIdx);
                if ( value == null ) {
                    fmtr.format("2");
                }
                else {
                    String woceVal = (String) value;
                    if ( woceVal.isEmpty() )
                        fmtr.format("2");
                    else
                        fmtr.format(woceVal);
                }

            }
            report.println(fmtr.toString());
            fmtr.close();
        }
    }


    /**
     * Generates full cruise reports for public consumption.
     *
     * @param args
     *         ExpocodesFile  Destination  [ RegionID ]
     *         <p>
     *         where:
     *         <p>
     *         ExpocodesFile is a file containing expocodes of the cruises to report;
     *         <p>
     *         Destination is the name of the directory to contain the single-cruise
     *         reports, or the name of the file to contain the multi-cruise report;
     *         <p>
     *         RegionID is the region ID restriction for the multi-cruise report;
     *         if not given, single-cruise reports will be generated; to generate
     *         a multi-cruise report without a region ID restriction, provide an
     *         empty string '' for this argument.
     */
    public static void main(String[] args) {
        if ( (args.length < 2) || (args.length > 3) ) {
            System.err.println("Arguments:  ExpocodesFile  Destination  [ RegionID ]");
            System.err.println();
            System.err.println("ExpocodesFile");
            System.err.println("    is a file containing expocodes, one per line, to report on");
            System.err.println("Destination");
            System.err.println("    the name of the directory to contain the single-cruise reports,");
            System.err.println("    or the name of the file to contain the multi-cruise report");
            System.err.println("RegionID");
            System.err.println("    the region ID restriction for the multi-cruise report; if not");
            System.err.println("    given, single-cruise reports will be generated; to generate a");
            System.err.println("    multi-cruise report without a region ID restriction, provide");
            System.err.println("    an empty string '' for this argument");
            System.exit(1);
        }
        String exposFilename = args[0];
        File destination = new File(args[1]);
        boolean multicruise;
        String regionID;
        if ( args.length > 2 ) {
            multicruise = true;
            if ( args[2].trim().isEmpty() )
                regionID = null;
            else
                regionID = args[2];
        }
        else {
            multicruise = false;
            regionID = null;
        }

        TreeSet<String> expocodes = new TreeSet<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(exposFilename));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {
                    dataline = dataline.trim().toUpperCase();
                    if ( !dataline.isEmpty() )
                        expocodes.add(dataline);
                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Problems reading the file of expocodes '" + exposFilename + "': " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems obtaining the default dashboard configuration: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            GenerateCruiseReports reporter = new GenerateCruiseReports(configStore);
            if ( multicruise ) {
                try {
                    ArrayList<String> warnMsgs = reporter.generateReport(expocodes, regionID, destination);
                    if ( warnMsgs.size() > 0 ) {
                        System.err.println("Warnings: ");
                        for (String msg : warnMsgs) {
                            System.err.println(msg);
                        }
                    }
                } catch ( Exception ex ) {
                    System.err.println("Problems generating the multi-cruise report: " + ex.getMessage());
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            else {
                for (String expo : expocodes) {
                    try {
                        ArrayList<String> warnMsgs = reporter.createEnhancedFilesBundle(expo, destination);
                        File enhancedZipFile = reporter.getEnhancedZipBundleFile(expo, destination);
                        System.err.println("Created single-cruise enhanced-data files bundle " +
                                enhancedZipFile.getPath());
                        if ( warnMsgs.size() > 0 ) {
                            System.err.println("Warnings for " + expo + ": ");
                            for (String msg : warnMsgs) {
                                System.err.println(expo + ": " + msg);
                            }
                        }
                    } catch ( Exception ex ) {
                        System.err.println("Problems generating the single-cruise enhanced-data files bundle for " +
                                expo + ": " + ex.getMessage());
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        } finally {
            DashboardConfigStore.shutdown();
        }

        System.exit(0);
    }

}
