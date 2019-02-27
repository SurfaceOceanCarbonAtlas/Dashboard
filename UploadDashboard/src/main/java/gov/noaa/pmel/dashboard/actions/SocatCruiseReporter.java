/**
 *
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdDataArray;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.CdiacOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * @author Karl Smith
 */
public class SocatCruiseReporter {

    private static final String NOT_AVAILABLE_TAG = "N/A";
    private static final String SOCAT_ENHANCED_DOI_TAG = "SOCATENHANCEDDOI";
    private static final String SOCAT_ENHANCED_HREF_PREFIX = "http://doi.pangaea.de/";

    // SOCAT main DOI, DOI HRef, and publication citation
    private static final String SOCAT_MAIN_DOI = "10.1594/PANGAEA.890974";
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

    private static final SimpleDateFormat TIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
    private static final SimpleDateFormat DATETIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Jan 1, 1940 - reasonable lower limit on data dates
     */
    private static final Date EARLIEST_DATE;

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        TIMESTAMPER.setTimeZone(utc);
        DATETIMESTAMPER.setTimeZone(utc);
        try {
            EARLIEST_DATE = DATETIMESTAMPER.parse("1940-01-01 00:00:00");
        } catch ( ParseException ex ) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Class for collecting and sorting time/lat/lon/fco2rec data
     */
    private static class DataPoint implements Comparable<DataPoint> {
        final static Double MISSING_VALUE = -999.0;
        final Date datetime;
        final Double latitude;
        final Double longitude;
        final Double sst;
        final Double sal;
        final Double fco2rec;

        /**
         * @param expocode
         *         dataset expocode, only used for error message when raising exceptions
         * @param sectime
         *         measurement time in seconds since Jan 1, 1970 00:00:00; must be a valid value
         * @param latitude
         *         measurement latitude in decimal degrees north; must be a valid value
         * @param longitude
         *         measurment longitude in decimal degrees east in the range [-180,180]; must be a valid value
         * @param sst
         *         measurement SST in degrees C; if {@link DashboardUtils#FP_MISSING_VALUE}, {@link #MISSING_VALUE} is
         *         assigned
         * @param sal
         *         measurement salinity in PSU; if {@link DashboardUtils#FP_MISSING_VALUE}, {@link #MISSING_VALUE} is
         *         assigned
         * @param fco2rec
         *         measurement recommended fCO2; must be valid value
         *
         * @throws IllegalArgumentException
         *         if the sectime, latitude, longitude, sst, sal, or fco2rec values are invalid
         */
        DataPoint(String expocode, Double sectime, Double latitude, Double longitude,
                Double sst, Double sal, Double fco2rec) throws IllegalArgumentException {
            if ( sectime == null )
                throw new IllegalArgumentException("null time for " + expocode);
            this.datetime = new Date(Math.round(sectime * 1000.0));
            if ( this.datetime.before(EARLIEST_DATE) || this.datetime.after(new Date()) )
                throw new IllegalArgumentException("invalid time of " +
                        this.datetime.toString() + " for " + expocode);

            if ( latitude == null )
                throw new IllegalArgumentException("null latitude for " + expocode);
            if ( (latitude < -90.0) || (latitude > 90.0) )
                throw new IllegalArgumentException("invalid latitude of " +
                        Double.toString(latitude) + " for " + expocode);
            this.latitude = latitude;

            if ( longitude == null )
                throw new IllegalArgumentException("null longitude for " + expocode);
            if ( (longitude < -180.0) || (longitude > 180.0) )
                throw new IllegalArgumentException("invalid longitude of " +
                        Double.toString(longitude) + " for " + expocode);
            this.longitude = longitude;

            if ( fco2rec == null )
                throw new IllegalArgumentException("null fco2rec for " + expocode);
            if ( (fco2rec < 0.0) || (fco2rec > 100000.0) )
                throw new IllegalArgumentException("invalid fCO2rec of " +
                        Double.toString(fco2rec) + " for " + expocode);
            this.fco2rec = fco2rec;

            if ( sst == null )
                throw new IllegalArgumentException("null SST for " + expocode);
            if ( DashboardUtils.FP_MISSING_VALUE.equals(sst) ) {
                this.sst = MISSING_VALUE;
            }
            else {
                if ( (sst < -10.0) || (sst > 80.0) )
                    throw new IllegalArgumentException("invalid SST of " +
                            Double.toString(sst) + " for " + expocode);
                this.sst = sst;
            }

            if ( sal == null )
                throw new IllegalArgumentException("null salinity for " + expocode);
            if ( DashboardUtils.FP_MISSING_VALUE.equals(sal) ) {
                this.sal = MISSING_VALUE;
            }
            else {
                if ( (sal < -10.0) || (sal > 100.0) )
                    throw new IllegalArgumentException("invalid salinity of " +
                            Double.toString(sal) + " for " + expocode);
                this.sal = sal;
            }
        }

        @Override
        public int compareTo(DataPoint other) {
            // the primary sort must be on datetime
            int result = this.datetime.compareTo(other.datetime);
            if ( result != 0 )
                return result;
            result = this.latitude.compareTo(other.latitude);
            if ( result != 0 )
                return result;
            result = this.longitude.compareTo(other.longitude);
            if ( result != 0 )
                return result;
            result = this.fco2rec.compareTo(other.fco2rec);
            if ( result != 0 )
                return result;
            result = this.sst.compareTo(other.sst);
            if ( result != 0 )
                return result;
            result = this.sal.compareTo(other.sal);
            if ( result != 0 )
                return result;
            return 0;
        }

        @Override
        public int hashCode() {
            final int prime = 37;
            int result = 1;
            result = prime * result + datetime.hashCode();
            result = prime * result + latitude.hashCode();
            result = prime * result + longitude.hashCode();
            result = prime * result + fco2rec.hashCode();
            result = prime * result + sst.hashCode();
            result = prime * result + sal.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( !(obj instanceof DataPoint) )
                return false;
            DataPoint other = (DataPoint) obj;
            if ( !datetime.equals(other.datetime) )
                return false;
            if ( !latitude.equals(other.latitude) )
                return false;
            if ( !longitude.equals(other.longitude) )
                return false;
            if ( !fco2rec.equals(other.fco2rec) )
                return false;
            if ( !sst.equals(other.sst) )
                return false;
            if ( !sal.equals(other.sal) )
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "[ datetime=" + DATETIMESTAMPER.format(datetime) +
                    ", latitude=" + String.format("%#.6f", latitude) +
                    ", longitude=" + String.format("%#.6f", longitude) +
                    ", fco2rec=" + String.format("%#.6f", fco2rec) +
                    ", sst =" + String.format("%#.6f", sst) +
                    ", sal =" + String.format("%#.6f", sal) +
                    " ]";
        }

    }

    private DataFileHandler dataHandler;
    private MetadataFileHandler metadataHandler;
    private DsgNcFileHandler dsgFileHandler;
    private KnownDataTypes knownMetadataTypes;
    private KnownDataTypes knownDataFileTypes;

    /**
     * For generating cruise reports from the data provided by the given dashboard configuration. DsgNcFileHandler.
     *
     * @param configStore
     *         dashboard configuration to use
     */
    public SocatCruiseReporter(DashboardConfigStore configStore) {
        dataHandler = configStore.getDataFileHandler();
        metadataHandler = configStore.getMetadataFileHandler();
        dsgFileHandler = configStore.getDsgNcFileHandler();
        knownMetadataTypes = configStore.getKnownMetadataTypes();
        knownDataFileTypes = configStore.getKnownDataFileTypes();
    }

    /**
     * Generates a single-cruise-format data report (includes WOCE-3 and WOCE-4 data and original-data CO2
     * measurements). If successful, any warnings about the generated report are returned.
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
        DashboardMetadata metadata = metadataHandler.getMetadataInfo(upperExpo, DashboardUtils.OME_FILENAME);
        DashboardOmeMetadata omeMeta = new DashboardOmeMetadata(CdiacOmeMetadata.class, metadata, metadataHandler);

        // Get the list of additional document filenames associated with this cruise.
        // Use what the QC-ers see - the directory listing.
        TreeSet<String> addlDocs = new TreeSet<String>();
        for (DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo)) {
            if ( !mdata.getFilename().equals(DashboardUtils.OME_FILENAME) ) {
                addlDocs.add(mdata.getFilename());
            }
        }

        // Get the SOCAT-enhanced data document DOI for this cruise
        DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(upperExpo);
        String socatDOI = cruise.getEnhancedDOI();
        if ( socatDOI.isEmpty() )
            socatDOI = SOCAT_ENHANCED_DOI_TAG;

        // Get the computed values of time in seconds since 1970-01-01 00:00:00
        double[] sectimes = dsgFile.readDoubleVarDataValues(DashboardServerUtils.TIME.getVarName());

        // Generate the report
        PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
        try {
            ArrayList<String> msgs = printMetadataPreamble(omeMeta, socatVersion, socatDOI, qcFlag, addlDocs, report);
            warnMsgs.addAll(msgs);
            printDataTableHeader(report, false);
            printDataStrings(report, dsgFile.getStdDataArray(), upperExpo, socatVersion, socatDOI, qcFlag, null, false);
        } finally {
            report.close();
        }

        return warnMsgs;
    }

    /**
     * Generates a multi-cruise-format data report (does not include WOCE-3 and WOCE-4 data nor original-data CO2
     * measurements). If successful, any warnings about the generated report are returned.
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
        ArrayList<String> socatDOIList = new ArrayList<String>(numDatasets);
        ArrayList<String> qcFlagList = new ArrayList<String>(numDatasets);
        ArrayList<String> warnMsgs = new ArrayList<String>(numDatasets);

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
                    if ( regionID.equals((String) dataVals.getStdVal(j, regionColIdx)) &&
                            DashboardServerUtils.WOCE_ACCEPTABLE.equals((String) dataVals.getStdVal(j, woceColIdx)) ) {
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
                DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(upperExpo);
                String socatDOI = cruise.getEnhancedDOI();
                if ( DashboardUtils.STRING_MISSING_VALUE.equals(socatDOI) )
                    socatDOI = SOCAT_ENHANCED_DOI_TAG;
                socatDOIList.add(socatDOI);
                upperExpoList.add(upperExpo);
            }
        }

        // Get the rest of the metadata info from the OME XML
        ArrayList<DashboardOmeMetadata> omeMetaList = new ArrayList<DashboardOmeMetadata>();
        for (String upperExpo : upperExpoList) {
            DashboardMetadata metadata = metadataHandler.getMetadataInfo(upperExpo, DashboardUtils.OME_FILENAME);
            omeMetaList.add(new DashboardOmeMetadata(CdiacOmeMetadata.class, metadata, metadataHandler));
        }

        // Get the list of additional document filenames associated with this cruise.
        // Use what the QC-ers see - the directory listing.
        ArrayList<TreeSet<String>> addlDocsList = new ArrayList<TreeSet<String>>();
        for (String upperExpo : upperExpoList) {
            TreeSet<String> addlDocs = new TreeSet<String>();
            for (DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo)) {
                // Ignore the OME.xml stub at this time, but include any PI_OME.xml since that is what was uploaded
                if ( !mdata.getFilename().equals(DashboardUtils.OME_FILENAME) ) {
                    addlDocs.add(mdata.getFilename());
                }
            }
            addlDocsList.add(addlDocs);
        }

        PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
        try {
            ArrayList<String> msgs = printMetadataPreamble(regionName, omeMetaList,
                    socatVersionList, socatDOIList, qcFlagList, addlDocsList, report);
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
                        socatDOIList.get(k), qcFlagList.get(k), regionID, true);
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
     * @param socatDOI
     *         DOI for this SOCAT-enhanced data file
     * @param qcFlag
     *         QC flag to report in the preamble
     * @param addlDocs
     *         filenames of additional documents to report in the preamble
     * @param report
     *         print with this PrintWriter
     *
     * @return list of warnings about the generated preamble; never null but may be empty
     */
    private ArrayList<String> printMetadataPreamble(DashboardOmeMetadata omeMeta,
            String socatVersion, String socatDOI, String qcFlag, TreeSet<String> addlDocs, PrintWriter report) {
        String upperExpo = omeMeta.getDatasetId();
        ArrayList<String> warnMsgs = new ArrayList<String>();

        report.println("SOCAT data report created: " + TIMESTAMPER.format(new Date()));
        report.println("Expocode: " + upperExpo);
        report.println("version: " + socatVersion);
        report.println("Dataset Name: " + omeMeta.getDatasetName());
        report.println("Platform Name: " + omeMeta.getPlatformName());
        report.println("Principal Investigator(s): " + omeMeta.getPINames());
        report.println("DOI for the original data: " + omeMeta.getDatasetDOI());
        report.println("    or see: " + omeMeta.getDatasetLink());
        report.println("DOI of this SOCAT-enhanced data: " + socatDOI);
        report.println("    or see: " + SOCAT_ENHANCED_HREF_PREFIX + socatDOI);
        report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
        report.println("    or see: " + SOCAT_ENHANCED_HREF_PREFIX + SOCAT_MAIN_DOI);
        report.println();

        // Additional references - add expocode suffix for clarity
        report.println("Supplemental documentation reference(s):");
        for (String filename : addlDocs) {
            report.println("    " + filename);
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
     * @param socatDOIList
     *         list of DOIs, one per dataset, for the SOCAT-enhanced data files
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
            ArrayList<String> socatVersionList, ArrayList<String> socatDOIList, ArrayList<String> qcFlagList,
            ArrayList<TreeSet<String>> addlDocsList, PrintWriter report) {
        ArrayList<String> warnMsgs = new ArrayList<String>();

        report.println("SOCAT data report created: " + TIMESTAMPER.format(new Date()));
        report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
        report.println("    or see: " + SOCAT_ENHANCED_HREF_PREFIX + SOCAT_MAIN_DOI);
        if ( regionName == null )
            report.println("SOCAT data for the following data sets:");
        else
            report.println("SOCAT data in SOCAT region \"" +
                    regionName + "\" for the following data sets:");
        report.println("Expocode\t" +
                "version\t" +
                "Dataset Name\t" +
                "Platform Name\t" +
                "PI(s)\t" +
                "Original Data DOI\t" +
                "Original Data Reference\t" +
                "SOCAT DOI\t" +
                "SOCAT Reference\t" +
                "Westmost Longitude\t" +
                "Eastmost Longitude\t" +
                "Southmost Latitude\t" +
                "Northmost Latitude\t" +
                "Start Time\t" +
                "End Time\t" +
                "QC Flag\t" +
                "Additional Metadata Document(s)");
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

            String origDOI = omeMeta.getDatasetDOI();
            if ( origDOI.isEmpty() )
                origDOI = NOT_AVAILABLE_TAG;
            report.print(origDOI);
            report.print("\t");

            String origHRef = omeMeta.getDatasetLink();
            if ( origHRef.isEmpty() )
                origHRef = NOT_AVAILABLE_TAG;
            report.print(origHRef);
            report.print("\t");

            String socatDOI = socatDOIList.get(k);
            report.print(socatDOI);
            report.print("\t");

            String socatHRef = SOCAT_ENHANCED_HREF_PREFIX + socatDOI;
            report.print(socatHRef);
            report.print("\t");

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
                report.print(filename);
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
     * Prints the data table header.  This includes the explanation of the data columns as well as the SOCAT reference
     * and, if appropriate, the salinity used if WOA_SSS is needed but missing.
     *
     * @param report
     *         print to this PrintWriter
     * @param multicruise
     *         is this header for a multi-cruise report (only data with fCO2_rec given and WOCE-flag 2 or not given, do
     *         not include original-data CO2 measurement columns) ?
     */
    private void printDataTableHeader(PrintWriter report, boolean multicruise) {
        report.println("Explanation of data columns:");
        if ( multicruise ) {
            for (int k = 0; k < MULTI_CRUISE_DATA_REPORT_EXPLANATIONS.length; k++) {
                report.println(MULTI_CRUISE_DATA_REPORT_EXPLANATIONS[k]);
            }
        }
        else {
            for (int k = 0; k < SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS.length; k++) {
                report.println(SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS[k]);
            }
        }
        report.println();
        report.println("The quality assessments given by the QC flag and fCO2rec_flag only apply to");
        report.println("the fCO2rec value.  For more information about the recomputed fCO2 value and");
        report.println("the meaning of the QC flag, fCO2rec_src, and fCO2rec_flag values, see:");
        for (int k = 0; k < SOCAT_MAIN_CITATION.length; k++) {
            report.println(SOCAT_MAIN_CITATION[k]);
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
            "SOCAT_DOI: DOI for this SOCAT-enhanced data",
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
            "    1979-01-01 to 2018-01-01 surface CO2 data (see: https://www.esrl.noaa.gov/gmd/ccgg/mbl/data.php)",
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
            "SOCAT_DOI\t" +
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
            "SOCAT_DOI: DOI for this SOCAT-enhanced data",
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
            "    1979-01-01 to 2018-01-01 surface CO2 data (see: https://www.esrl.noaa.gov/gmd/ccgg/mbl/data.php)",
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
            "SOCAT_DOI\t" +
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
     * @param socatDOI
     *         DOI for this SOCAT-enhanced dataset
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
            String socatDOI, String qcFlag, String regionID, boolean multicruise) throws IOException {
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
        Integer sectimeIdx;
        Integer regionIdx;
        Integer xco2WaterTEquIdx;
        Integer xco2WaterSSTIdx;
        Integer pco2WaterTEquIdx;
        Integer pco2WaterSSTIdx;
        Integer fco2WaterTEquIdx;
        Integer fco2WaterSSTIdx;
        TreeSet<DataPoint> prevDatPts;
        if ( multicruise ) {
            // Need time to tests for duplicates in multicruise reports
            sectimeIdx = dataVals.getIndexOfType(DashboardServerUtils.TIME);
            if ( sectimeIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + DashboardServerUtils.TIME.getVarName());
            // These values are not reported in multicruise reports
            xco2WaterTEquIdx = null;
            xco2WaterSSTIdx = null;
            pco2WaterTEquIdx = null;
            pco2WaterSSTIdx = null;
            fco2WaterTEquIdx = null;
            fco2WaterSSTIdx = null;
            regionIdx = null;
            prevDatPts = null;

        }
        else {
            // Duplicates not checked in single-cruise reports
            sectimeIdx = null;
            // These values are reported in single-cruise reports
            xco2WaterTEquIdx = dataVals.getIndexOfType(SocatTypes.XCO2_WATER_TEQU_DRY);
            if ( xco2WaterTEquIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + SocatTypes.XCO2_WATER_TEQU_DRY.getVarName());

            xco2WaterSSTIdx = dataVals.getIndexOfType(SocatTypes.XCO2_WATER_SST_DRY);
            if ( xco2WaterSSTIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + SocatTypes.XCO2_WATER_SST_DRY.getVarName());

            pco2WaterTEquIdx = dataVals.getIndexOfType(SocatTypes.PCO2_WATER_TEQU_WET);
            if ( pco2WaterTEquIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + SocatTypes.PCO2_WATER_TEQU_WET.getVarName());

            pco2WaterSSTIdx = dataVals.getIndexOfType(SocatTypes.PCO2_WATER_SST_WET);
            if ( pco2WaterSSTIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + SocatTypes.PCO2_WATER_SST_WET.getVarName());

            fco2WaterTEquIdx = dataVals.getIndexOfType(SocatTypes.FCO2_WATER_TEQU_WET);
            if ( fco2WaterTEquIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + SocatTypes.FCO2_WATER_TEQU_WET.getVarName());

            fco2WaterSSTIdx = dataVals.getIndexOfType(SocatTypes.FCO2_WATER_SST_WET);
            if ( fco2WaterSSTIdx == null )
                throw new IOException("The DSG file for " + expocode +
                        " does not contain the variable " + SocatTypes.FCO2_WATER_SST_WET.getVarName());

            if ( regionID != null ) {
                regionIdx = dataVals.getIndexOfType(DashboardServerUtils.REGION_ID);
                if ( regionIdx == null )
                    throw new IOException("The DSG file for " + expocode +
                            " does not contain the variable " + DashboardServerUtils.REGION_ID.getVarName());
            }
            else
                regionIdx = null;

            prevDatPts = new TreeSet<DataPoint>();
        }

        for (int j = 0; j < dataVals.getNumSamples(); j++) {
            if ( multicruise ) {
                // First check if this data sample should even be considered
                if ( (regionID != null) && !regionID.equals((String) dataVals.getStdVal(j, regionIdx)) )
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
            fmtr.format("%s\t", socatDOI);
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

            if ( !multicruise ) {
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
            }

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

                report.println(fmtr.toString());
            }
            fmtr.close();
        }
    }

    /**
     * Tab-separated data column names for the data printed by {@link #generateDataFileForGrids(TreeSet, File)}
     */
    private static final String GENERATE_DATA_FILE_FOR_GRIDS_HEADER = "data_id\t" +
            "latitude\t" +
            "longitude\t" +
            "datetime\t" +
            "expocode\t" +
            "fCO2rec\t" +
            "SST\t" +
            "salinity";


    /**
     * Print the data needed to generate the gridded-data NetCDF files. Only WOCE-2 data with valid fCO2rec values are
     * printed for the given datasets.  Data is printed in order of datasetIds as they are given and the in increasing
     * time order.  Only one copy of any data points in a dataset with identical valid values for latitude, longitude,
     * time, fCO2rec, and WOCE flag are printed.
     *
     * @param expocodes
     *         report the data in the datasets with these IDs
     * @param outputFile
     *         print the data to this File
     *
     * @throws IllegalArgumentException
     *         if an dataset ID is invalid, or if the full-data DSG file for a dataset is invalid
     * @throws IOException
     *         if reading from a DSG file throws one, or
     *         if creating or writing to the output file throws one
     */
    public void generateDataFileForGrids(TreeSet<String> expocodes, File outputFile)
            throws IllegalArgumentException, IOException {
        PrintWriter report = new PrintWriter(outputFile);
        report.println(GENERATE_DATA_FILE_FOR_GRIDS_HEADER);
        long dataID = 0L;
        try {
            // Read and report the data for one cruise at a time
            for (String expo : expocodes) {
                // Read the data for this cruise
                String upperExpo = DashboardServerUtils.checkDatasetID(expo);
                DsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
                ArrayList<String> unknownVars = dsgFile.readData(knownDataFileTypes);
                if ( unknownVars.size() > 0 ) {
                    String msg = upperExpo + " unassigned data variables: ";
                    for (String var : unknownVars) {
                        msg += var + "; ";
                    }
                    throw new IllegalArgumentException(msg);
                }
                StdDataArray dataVals = dsgFile.getStdDataArray();

                Integer longitudeIdx = dataVals.getIndexOfType(DashboardServerUtils.LONGITUDE);
                if ( longitudeIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + DashboardServerUtils.LONGITUDE.getVarName());

                Integer latitudeIdx = dataVals.getIndexOfType(DashboardServerUtils.LATITUDE);
                if ( latitudeIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + DashboardServerUtils.LATITUDE.getVarName());

                Integer sectimeIdx = dataVals.getIndexOfType(DashboardServerUtils.TIME);
                if ( sectimeIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + DashboardServerUtils.TIME.getVarName());

                Integer fco2RecIdx = dataVals.getIndexOfType(SocatTypes.FCO2_REC);
                if ( fco2RecIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.FCO2_REC.getVarName());

                Integer salIdx = dataVals.getIndexOfType(SocatTypes.SALINITY);
                if ( salIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.SALINITY.getVarName());

                Integer sstIdx = dataVals.getIndexOfType(SocatTypes.SST);
                if ( sstIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.SST.getVarName());

                Integer woceWaterIdx = dataVals.getIndexOfType(SocatTypes.WOCE_CO2_WATER);
                if ( woceWaterIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.WOCE_CO2_WATER.getVarName());

                // Collect and sort the acceptable data for this cruise
                // Any duplicates are eliminated in this process
                TreeSet<DataPoint> datSet = new TreeSet<DataPoint>();
                for (int j = 0; j < dataVals.getNumSamples(); j++) {
                    Double fco2rec = (Double) dataVals.getStdVal(j, fco2RecIdx);
                    if ( fco2rec == null )
                        continue;
                    String woceFlag = (String) dataVals.getStdVal(j, woceWaterIdx);
                    if ( !((woceFlag == null) || woceFlag.isEmpty() ||
                            DashboardServerUtils.WOCE_ACCEPTABLE.equals(woceFlag)) )
                        continue;
                    DataPoint datpt = new DataPoint(upperExpo, (Double) dataVals.getStdVal(j, sectimeIdx),
                            (Double) dataVals.getStdVal(j, latitudeIdx),
                            (Double) dataVals.getStdVal(j, longitudeIdx),
                            (Double) dataVals.getStdVal(j, sstIdx), (Double) dataVals.getStdVal(j, salIdx),
                            fco2rec);
                    if ( !datSet.add(datpt) )
                        System.err
                                .println("Ignored duplicate datapoint for " + upperExpo + ": " + datpt.toString());
                }
                // Print the sorted data for this cruise
                for (DataPoint datPt : datSet) {
                    dataID++;
                    String datetime = DATETIMESTAMPER.format(datPt.datetime);
                    report.format("%d\t%.6f\t%.6f\t%s\t%s\t%.6f\t%.3f\t%.3f\n",
                            Long.valueOf(dataID), datPt.latitude, datPt.longitude,
                            datetime, upperExpo, datPt.fco2rec, datPt.sst, datPt.sal);
                }
            }
        } finally {
            report.close();
        }
    }

}
