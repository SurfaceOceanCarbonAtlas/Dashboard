/**
 *
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.SpellingHandler;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import uk.ac.uea.socat.omemetadata.OmeMetadata;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

/**
 * Class for the one special metadata file per cruise that must be present, has a known format, and contains
 * user-provided values needed by the dashboard. Extends DashboardMetadata, but uses {@link
 * uk.ac.uea.socat.omemetadata.OmeMetadata} to work with the actual metadata.
 *
 * @author Karl Smith
 */
public class DashboardOmeMetadata extends DashboardMetadata {

    private static final long serialVersionUID = 6970740109331521539L;

    /**
     * String separating each PI listed in scienceGroup, each organization listed in organizations, and each additional
     * document filename listed in addlDocs.  This is cannot be a semicolon due to Ferret issues.
     */
    private static final String NAMES_SEPARATOR = " : ";

    private static final SimpleDateFormat TIMEPARSER = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat TIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd");

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        TIMEPARSER.setTimeZone(utc);
        TIMESTAMPER.setTimeZone(utc);
    }

    private OmeMetadata omeMData;

    /**
     * Creates from the contents of the OME XML file specified in the DashboardMetadata given.
     *
     * @param mdata
     *         OME XML file to read.  The dataset, upload timestamp, and owner are copied from this object,
     *         and the file specified is read to populate the OmeMetadata member of this object.
     * @param mdataHandler
     *         MetadataFileHandler to use to get the given OME XML file
     *
     * @throws IllegalArgumentException
     *         if mdata is null, if the information in the DashboardMetadata is invalid, or
     *         if the contents of the metadata document are not valid
     */
    public DashboardOmeMetadata(DashboardMetadata mdata, MetadataFileHandler mdataHandler)
            throws IllegalArgumentException {
        // Initialize to an empty OME metadata document with the standard OME filename
        super();
        this.filename = DashboardUtils.OME_FILENAME;

        if ( mdata == null )
            throw new IllegalArgumentException("No metadata file given");

        // Copy the dataset, uploadTimestamp, owner, and version
        // from the given DashboardMetadata object
        setDatasetId(mdata.getDatasetId());
        setUploadTimestamp(mdata.getUploadTimestamp());
        setOwner(mdata.getOwner());
        setVersion(mdata.getVersion());

        File mdataFile = mdataHandler.getMetadataFile(this.datasetId, mdata.getFilename());
        Document omeDoc;
        try {
            omeDoc = (new SAXBuilder()).build(mdataFile);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems interpreting " +
                    "the OME XML contents in " + mdataFile.getPath() +
                    "\n    " + ex.getMessage());
        }

        // Create the OmeMetadata object associated with this instance
        // from the OME XML contents
        try {
            omeMData = new OmeMetadata(this.datasetId);
            omeMData.assignFromOmeXmlDoc(omeDoc);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problem with " + mdataFile.getPath() +
                    "\n    " + ex.getMessage(), ex);
        }
        // If conflicted or incomplete for DSG files, set the conflicted flags in DsgMetadata
        setConflicted(!omeMData.isAcceptable());
    }

    /**
     * Creates using the given OmeMetadata.  The dataset ID is obtained from the OmeMetadata.
     *
     * @param omeMeta
     *         the OmeMetadata contents of this metadata
     * @param timestamp
     *         the upload timestamp for this metadata
     * @param owner
     *         the owner of this metadata
     * @param version
     *         the SOCAT version of this metadata
     */
    public DashboardOmeMetadata(OmeMetadata omeMeta, String timestamp, String owner, String version) {
        super();
        setFilename(DashboardUtils.OME_FILENAME);
        setDatasetId(DashboardServerUtils.checkDatasetID(omeMeta.getExpocode()));
        setUploadTimestamp(timestamp);
        setOwner(owner);
        setVersion(version);
        omeMData = omeMeta;
        // If conflicted or incomplete for DSG files, set the conflicted flags in DsgMetadata
        setConflicted(!omeMData.isAcceptable());
    }

    /**
     * Create a DsgMetadata object from the data in this object. Any PI or platform names will be anglicized.
     * The version status, QC flag, enhanced DOI, and string of all region IDs are not assigned.
     *
     * @param metadataTypes
     *         known DSG file metadata types
     *
     * @return created DsgMetadata object
     */
    public DsgMetadata createDsgMetadata(KnownDataTypes metadataTypes) throws IllegalArgumentException {

        // a DsgMetadata object cannot be created if there are conflicts
        if ( isConflicted() )
            throw new IllegalArgumentException("The metadata contains conflicts");

        DsgMetadata scMData = new DsgMetadata(metadataTypes);

        scMData.setDatasetId(this.datasetId);
        scMData.setDatasetName(omeMData.getExperimentName());

        // Anglicize the platform name for NetCDF/LAS
        String platformName = omeMData.getVesselName();
        scMData.setPlatformName(SpellingHandler.anglicizeName(platformName));

        // Set the platform type - could be missing
        String platformType;
        try {
            platformType = omeMData.getValue(OmeMetadata.PLATFORM_TYPE_STRING);
        } catch ( Exception ex ) {
            platformType = null;
        }
        if ( (platformType == null) || platformType.trim().isEmpty() )
            platformType = DashboardServerUtils.guessPlatformType(this.datasetId, platformName);
        scMData.setPlatformType(platformType);

        try {
            scMData.setWestmostLongitude(Double.parseDouble(omeMData.getWestmostLongitude()));
        } catch ( NumberFormatException | NullPointerException ex ) {
            scMData.setWestmostLongitude(null);
        }

        try {
            scMData.setEastmostLongitude(Double.parseDouble(omeMData.getEastmostLongitude()));
        } catch ( NumberFormatException | NullPointerException ex ) {
            scMData.setEastmostLongitude(null);
        }

        try {
            scMData.setSouthmostLatitude(Double.parseDouble(omeMData.getSouthmostLatitude()));
        } catch ( NumberFormatException | NullPointerException ex ) {
            scMData.setSouthmostLatitude(null);
        }

        try {
            scMData.setNorthmostLatitude(Double.parseDouble(omeMData.getNorthmostLatitude()));
        } catch ( NumberFormatException | NullPointerException ex ) {
            scMData.setNorthmostLatitude(null);
        }

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            scMData.setBeginTime(
                    dateParser.parse(omeMData.getTemporalCoverageStartDate() + " 00:00:00").getTime() / 1000.0);
        } catch ( ParseException ex ) {
            scMData.setBeginTime(null);
        }
        try {
            scMData.setEndTime(
                    dateParser.parse(omeMData.getTemporalCoverageEndDate() + " 23:59:59").getTime() / 1000.0);
        } catch ( ParseException ex ) {
            scMData.setEndTime(null);
        }

        StringBuffer piNames = new StringBuffer();
        for (String investigator : omeMData.getInvestigators()) {
            if ( piNames.length() > 0 )
                piNames.append(NAMES_SEPARATOR);
            // Anglicize investigator names for NetCDF/LAS
            piNames.append(SpellingHandler.anglicizeName(investigator));
        }
        scMData.setInvestigatorNames(piNames.toString());

        HashSet<String> usedOrganizations = new HashSet<String>();
        StringBuffer orgGroup = new StringBuffer();
        for (String org : omeMData.getOrganizations()) {
            if ( org == null )
                continue;
            org = org.trim();
            if ( org.isEmpty() )
                continue;
            if ( usedOrganizations.add(org) ) {
                if ( orgGroup.length() > 0 )
                    orgGroup.append(NAMES_SEPARATOR);
                // Anglicize organizations names for NetCDF/LAS
                orgGroup.append(SpellingHandler.anglicizeName(org));
            }
        }
        String allOrgs = orgGroup.toString().trim();
        if ( allOrgs.isEmpty() )
            allOrgs = "unassigned";
        scMData.setOrganizationName(allOrgs);

        return scMData;
    }

    /**
     * Assigns the dataset ID associated with this DashboardMetadata as well as the dataset ID stored in the OME
     * information represented by this DashboardMetadata.
     *
     * @param newId
     *         new dataset ID to use
     */
    public void changeDatasetID(String newId) {
        omeMData.setExpocode(newId);
        setDatasetId(newId);
    }

    /**
     * Generated an OME XML document that contains the contents of the data contained in this OME metadata object.
     *
     * @return the generated OME XML document
     */
    public Document createOmeXmlDoc() {
        return omeMData.createOmeXmlDoc();
    }

    /**
     * Creates a new DashboardOmeMetadata from merging, where appropriate, the OME content of this instance
     * with the OME content of other. The datasetIds in other must be the same as in this instance. Fields
     * derived from the data are the same as those in this instance.
     *
     * @param other
     *         merge with this OME content
     *
     * @return new DasboardOmeMetadata with merged content, where appropriate
     *
     * @throws IllegalArgumentException
     *         if the datasetIds in this instance and other do not match
     */
    public DashboardOmeMetadata mergeModifiable(DashboardOmeMetadata other)
            throws IllegalArgumentException {
        OmeMetadata mergedOmeMData;
        try {
            // Merge the OmeMetadata documents - requires the datasetIds be the same
            mergedOmeMData = OmeMetadata.merge(this.omeMData, other.omeMData);

            // Some fields should not have been merged; reset to the values in this instance
            // setExpcode sets
            //   cruise ID = dataset ID = dataset,
            //   vessel ID = NODC code from dataset,
            //   cruise start date = start date from dataset
            mergedOmeMData.setExpocode(this.datasetId);

            String value = this.omeMData.getValue(OmeMetadata.END_DATE_STRING);
            if ( !OmeMetadata.CONFLICT_STRING.equals(value) )
                mergedOmeMData.replaceValue(OmeMetadata.END_DATE_STRING, value, -1);

            value = this.omeMData.getValue(OmeMetadata.TEMP_START_DATE_STRING);
            if ( !OmeMetadata.CONFLICT_STRING.equals(value) )
                mergedOmeMData.replaceValue(OmeMetadata.TEMP_START_DATE_STRING, value, -1);

            value = this.omeMData.getValue(OmeMetadata.TEMP_END_DATE_STRING);
            if ( !OmeMetadata.CONFLICT_STRING.equals(value) )
                mergedOmeMData.replaceValue(OmeMetadata.TEMP_END_DATE_STRING, value, -1);

            value = this.omeMData.getValue(OmeMetadata.WEST_BOUND_STRING);
            if ( !OmeMetadata.CONFLICT_STRING.equals(value) )
                mergedOmeMData.replaceValue(OmeMetadata.WEST_BOUND_STRING, value, -1);

            value = this.omeMData.getValue(OmeMetadata.EAST_BOUND_STRING);
            if ( !OmeMetadata.CONFLICT_STRING.equals(value) )
                mergedOmeMData.replaceValue(OmeMetadata.EAST_BOUND_STRING, value, -1);

            value = this.omeMData.getValue(OmeMetadata.SOUTH_BOUND_STRING);
            if ( !OmeMetadata.CONFLICT_STRING.equals(value) )
                mergedOmeMData.replaceValue(OmeMetadata.SOUTH_BOUND_STRING, value, -1);

            value = this.omeMData.getValue(OmeMetadata.NORTH_BOUND_STRING);
            if ( !OmeMetadata.CONFLICT_STRING.equals(value) )
                mergedOmeMData.replaceValue(OmeMetadata.NORTH_BOUND_STRING, value, -1);

            mergedOmeMData.setDraft(!mergedOmeMData.isAcceptable());
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Unable to merge OME documents: " + ex.getMessage(), ex);
        }
        return new DashboardOmeMetadata(mergedOmeMData, this.uploadTimestamp, this.owner, this.version);
    }

    /**
     * @return the westernmost longitude, in the range (-180,180]
     *
     * @throws IllegalArgumentException
     *         if the westernmost longitude is invalid
     */
    public double getWestmostLongitude() throws IllegalArgumentException {
        double westLon;
        try {
            westLon = Double.parseDouble(omeMData.getWestmostLongitude());
            if ( (westLon < -540.0) || (westLon > 540.0) )
                throw new IllegalArgumentException("not in [-540,540]");
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid westmost longitude: " + ex.getMessage());
        }
        while ( westLon <= -180.0 ) {
            westLon += 360.0;
        }
        while ( westLon > 180.0 ) {
            westLon -= 360.0;
        }
        return westLon;
    }

    /**
     * @return the easternmost longitude, in the range (-180,180]
     *
     * @throws IllegalArgumentException
     *         if the easternmost longitude is invalid
     */
    public double getEastmostLongitude() throws IllegalArgumentException {
        double eastLon;
        try {
            eastLon = Double.parseDouble(omeMData.getEastmostLongitude());
            if ( (eastLon < -540.0) || (eastLon > 540.0) )
                throw new IllegalArgumentException("not in [-540,540]");
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid eastmost longitude: " + ex.getMessage());
        }
        while ( eastLon <= -180.0 ) {
            eastLon += 360.0;
        }
        while ( eastLon > 180.0 ) {
            eastLon -= 360.0;
        }
        return eastLon;
    }

    /**
     * @return the southernmost latitude
     *
     * @throws IllegalArgumentException
     *         if the southernmost latitude is invalid
     */
    public double getSouthmostLatitude() throws IllegalArgumentException {
        double southLat;
        try {
            southLat = Double.parseDouble(omeMData.getSouthmostLatitude());
            if ( (southLat < -90.0) || (southLat > 90.0) )
                throw new IllegalArgumentException("not in [-90,90]");
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid southmost latitude: " + ex.getMessage());
        }
        return southLat;
    }

    /**
     * @return the northernmost latitude
     *
     * @throws IllegalArgumentException
     *         if the northernmost latitude is invalid
     */
    public double getNorthmostLatitude() throws IllegalArgumentException {
        double northLat;
        try {
            northLat = Double.parseDouble(omeMData.getNorthmostLatitude());
            if ( (northLat < -90.0) || (northLat > 90.0) )
                throw new IllegalArgumentException("not in [-90,90]");
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid northmost latitude: " + ex.getMessage());
        }
        return northLat;
    }

    /**
     * @return the date stamp of the earliest data measurement
     *
     * @throws IllegalArgumentException
     *         if the date of the earliest data measurement is invalid
     */
    public String getBeginDatestamp() throws IllegalArgumentException {
        String beginTimestamp;
        try {
            Date beginTime = TIMEPARSER.parse(omeMData.getTemporalCoverageStartDate());
            beginTimestamp = TIMESTAMPER.format(beginTime);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid begin time: " + ex.getMessage());
        }
        return beginTimestamp;
    }

    /**
     * @return the date stamp of the latest data measurement
     *
     * @throws IllegalArgumentException
     *         if the date of the latest data measurement is invalid
     */
    public String getEndDatestamp() throws IllegalArgumentException {
        String endTimestamp;
        try {
            Date endTime = TIMEPARSER.parse(omeMData.getTemporalCoverageEndDate());
            endTimestamp = TIMESTAMPER.format(endTime);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid begin time: " + ex.getMessage());
        }
        return endTimestamp;
    }

    /**
     * @return the name given by the PI for this dataset; never null but may be empty
     */
    public String getDatasetName() {
        String cruiseName = omeMData.getExperimentName();
        if ( cruiseName == null )
            return "";
        return cruiseName;
    }

    /**
     * @return the platform name for this dataset; never null but may be empty
     */
    public String getPlatformName() {
        String platformName = omeMData.getVesselName();
        if ( platformName == null )
            return "";
        return platformName;
    }

    /**
     * @return the semicolon-separated list of PI names for this dataset; never null but may be empty
     */
    public String getPINames() {
        ArrayList<String> investigators = omeMData.getInvestigators();
        if ( investigators == null )
            return "";
        String piNames = "";
        boolean isFirst = true;
        for (String name : investigators) {
            if ( isFirst )
                isFirst = false;
            else
                piNames += "; ";
            piNames += name;
        }
        return piNames;
    }

    /**
     * @return the DOI(s) for the source dataset; never null but may be empty
     *         Currently this is stubbed and always returns an empty string.
     */
    public String getDatasetDOI() {
        // TODO: add the source dataset DOI to the OME metadata
        return "";
    }

    /**
     * @return the reference(s) for the source dataset; never null but may be empty
     */
    public String getDatasetRefs() {
        String dataSetRefs;
        try {
            dataSetRefs = omeMData.getValue(OmeMetadata.DATA_SET_REFS_STRING);
            if ( dataSetRefs == null )
                dataSetRefs = "";
        } catch ( Exception ex ) {
            // Should never happen
            dataSetRefs = "";
        }
        return dataSetRefs;
    }

}
