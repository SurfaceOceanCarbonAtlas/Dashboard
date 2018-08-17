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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

/**
 * Class for the one special metadata file per cruise that must be present, has a known format, and contains
 * user-provided values needed by the dashboard. Extends DashboardMetadata, but uses {@link OmeMetadataInterface}
 * to work with the actual metadata.
 *
 * @author Karl Smith
 */
public class DashboardOmeMetadata extends DashboardMetadata {

    private static final long serialVersionUID = 2349022349952348949L;

    /**
     * String separating each PI listed in scienceGroup, each organization listed in organizations, and each additional
     * document filename listed in addlDocs.  This is cannot be a semicolon due to Ferret issues.
     */
    private static final String NAMES_SEPARATOR = " : ";

    private static final SimpleDateFormat DATE_STAMPER;

    static {
        DATE_STAMPER = new SimpleDateFormat("yyyy-MM-dd");
        DATE_STAMPER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private OmeMetadataInterface omeMData;

    /**
     * Creates from the contents of the OME XML file specified by the given DashboardMetadata.
     *
     * @param omeClass
     *         class of OME object to use in this instance
     * @param mdata
     *         OME XML file to read.  The dataset ID, upload timestamp, and owner are copied from mdata,
     *         and the file it specifies is read to populate this OME object used by this instance.
     * @param mdataHandler
     *         MetadataFileHandler to use to get the given OME XML file
     *
     * @throws IllegalArgumentException
     *         if mdata is null,
     *         if the information in the DashboardMetadata is invalid,
     *         if the contents of the metadata document are not valid, or
     *         if omeClass cannot be instantiated
     */
    public DashboardOmeMetadata(Class<? extends OmeMetadataInterface> omeClass,
            DashboardMetadata mdata, MetadataFileHandler mdataHandler)
            throws IllegalArgumentException {
        // Initialize to an empty OME metadata document with the standard OME filename
        super();
        this.filename = DashboardUtils.OME_FILENAME;
        if ( mdata == null )
            throw new IllegalArgumentException("No metadata file given");

        // Copy the dataset ID, upload timestamp, owner, and version from the given DashboardMetadata object
        setDatasetId(mdata.getDatasetId());
        setUploadTimestamp(mdata.getUploadTimestamp());
        setOwner(mdata.getOwner());
        setVersion(mdata.getVersion());

        // Read the XML given in the file specified by the given DashboardMetadata
        File mdataFile = mdataHandler.getMetadataFile(this.datasetId, mdata.getFilename());
        Document omeDoc;
        try {
            omeDoc = (new SAXBuilder()).build(mdataFile);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems interpreting the OME XML contents in " + mdataFile.getPath() +
                    "\n    " + ex.getMessage());
        }

        // Create the OME object for this instance
        try {
            this.omeMData = omeClass.newInstance();
            this.omeMData.setDatasetId(mdata.getDatasetId());
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Unable to create an instance of " + omeClass.getSimpleName() +
                    ": " + ex.getMessage());
        }
        // Populate the OME object from the OME XML contents
        try {
            this.omeMData.assignFromDocument(omeDoc);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problem with " + mdataFile.getPath() +
                    "\n    " + ex.getMessage(), ex);
        }
        setConflicted(!this.omeMData.isAcceptable());
    }

    /**
     * Creates using the given OME object.  The dataset ID is obtained from the OME object.
     *
     * @param omeMeta
     *         the OME object to use in this instance.  All appropriate fields in this OME object
     *         are assumed to be assigned.  This OME object, not a copy of it, is used in this instance.
     * @param timestamp
     *         the upload timestamp for this metadata
     * @param owner
     *         the owner of this metadata
     * @param version
     *         the SOCAT version of this metadata
     */
    public DashboardOmeMetadata(OmeMetadataInterface omeMeta, String timestamp, String owner, String version) {
        super();
        setFilename(DashboardUtils.OME_FILENAME);
        setDatasetId(DashboardServerUtils.checkDatasetID(omeMeta.getDatasetId()));
        setUploadTimestamp(timestamp);
        setOwner(owner);
        setVersion(version);
        this.omeMData = omeMeta;
        setConflicted(!this.omeMData.isAcceptable());
    }

    /**
     * Creates from an OME object resulting from appropriately merging the content of a secondary OME object into
     * the primary OME object.  The dataset ID, upload timestamp, owner, version, and fields derived from the data
     * are copied from the primary OME object (first) and values in the secondary OME object (second) are ignored.
     *
     * @param first
     *         start with a copy of this content
     * @param second
     *         merge in this content where appropriate
     *
     * @throws IllegalArgumentException
     *         if the merge method of the primary OME object using with the secondary OME object as an argument
     *         raises an exception
     */
    public DashboardOmeMetadata(DashboardOmeMetadata first, DashboardOmeMetadata second)
            throws IllegalArgumentException {
        super();
        // Copy the upload timestamp, owner, and version from the primary DashboardMetadata object
        setFilename(DashboardUtils.OME_FILENAME);
        setDatasetId(DashboardServerUtils.checkDatasetID(first.getDatasetId()));
        setUploadTimestamp(first.getUploadTimestamp());
        setOwner(first.getOwner());
        setVersion(first.getVersion());

        // Create the OME object for this instance
        try {
            this.omeMData = first.omeMData.merge(second.omeMData);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Unable to merge OME documents: " + ex.getMessage(), ex);
        }

        // Some fields should not have been merged; reset to the values in first
        this.omeMData.setWesternLongitude(first.omeMData.getWesternLongitude());
        this.omeMData.setEasternLongitude(first.omeMData.getEasternLongitude());
        this.omeMData.setSouthernLatitude(first.omeMData.getSouthernLatitude());
        this.omeMData.setNorthernLatitude(first.omeMData.getNorthernLatitude());
        this.omeMData.setDataStartTime(first.omeMData.getDataStartTime());
        this.omeMData.setDataEndTime(first.omeMData.getDataEndTime());

        setConflicted(!this.omeMData.isAcceptable());
    }

    /**
     * Create a DsgMetadata object from the data in this object.  Any PI or platform names will be anglicized.
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
        scMData.setDatasetName(omeMData.getDatasetName());

        // Anglicize the platform name for NetCDF/LAS
        String platformName = omeMData.getPlatformName();
        scMData.setPlatformName(SpellingHandler.anglicizeName(platformName));

        // Set the platform type - could be missing
        String platformType = omeMData.getPlatformType();
        if ( (platformType == null) || platformType.trim().isEmpty() )
            platformType = DashboardServerUtils.guessPlatformType(this.datasetId, platformName);
        scMData.setPlatformType(platformType);

        scMData.setWestmostLongitude(omeMData.getWesternLongitude());
        scMData.setEastmostLongitude(omeMData.getEasternLongitude());
        scMData.setSouthmostLatitude(omeMData.getSouthernLatitude());
        scMData.setNorthmostLatitude(omeMData.getNorthernLatitude());
        scMData.setBeginTime(omeMData.getDataStartTime());
        scMData.setEndTime(omeMData.getDataEndTime());

        StringBuilder piNames = new StringBuilder();
        for (String investigator : omeMData.getInvestigators()) {
            if ( piNames.length() > 0 )
                piNames.append(NAMES_SEPARATOR);
            // Anglicize investigator names for NetCDF/LAS
            piNames.append(SpellingHandler.anglicizeName(investigator));
        }
        scMData.setInvestigatorNames(piNames.toString());

        HashSet<String> usedOrganizations = new HashSet<String>();
        StringBuilder orgGroup = new StringBuilder();
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
     * Assigns the dataset ID associated with this DashboardMetadata as well as
     * the dataset ID stored in the OME information represented by this DashboardMetadata.
     *
     * @param newId
     *         new dataset ID to use
     */
    public void changeDatasetID(String newId) {
        omeMData.setDatasetId(newId);
        setDatasetId(newId);
    }

    /**
     * Generate a document that contains the contents of the data contained in this OME metadata object.
     *
     * @return the generated OME XML document
     */
    public Document createDocument() {
        return omeMData.createDocument();
    }

    /**
     * @return the westernmost longitude, in the range (-180,180]
     *
     * @throws IllegalArgumentException
     *         if the value is not given or invalid
     */
    public double getWestmostLongitude() throws IllegalArgumentException {
        Double value = omeMData.getWesternLongitude();
        if ( (value == null) || value.isNaN() || value.isInfinite() || (value <= -180.0) || (value > 180.0) )
            throw new IllegalArgumentException("Invalid westmost longitude: " + value);
        return value;
    }

    /**
     * @return the easternmost longitude, in the range (-180,180]
     *
     * @throws IllegalArgumentException
     *         if the value is not given or invalid
     */
    public double getEastmostLongitude() throws IllegalArgumentException {
        Double value = omeMData.getEasternLongitude();
        if ( (value == null) || value.isNaN() || value.isInfinite() || (value <= -180.0) || (value > 180.0) )
            throw new IllegalArgumentException("Invalid eastmost longitude: " + value);
        return value;
    }

    /**
     * @return the southernmost latitude
     *
     * @throws IllegalArgumentException
     *         if the value is not given or invalid
     */
    public double getSouthmostLatitude() throws IllegalArgumentException {
        Double value = omeMData.getSouthernLatitude();
        if ( (value == null) || value.isNaN() || value.isInfinite() || (value < -90.0) || (value > 90.0) )
            throw new IllegalArgumentException("Invalid southmost latitude: " + value);
        return value;
    }

    /**
     * @return the northernmost latitude
     *
     * @throws IllegalArgumentException
     *         if the value is not given or invalid
     */
    public double getNorthmostLatitude() throws IllegalArgumentException {
        Double value = omeMData.getNorthernLatitude();
        if ( (value == null) || value.isNaN() || value.isInfinite() || (value < -90.0) || (value > 90.0) )
            throw new IllegalArgumentException("Invalid northmost latitude: " + value);
        return value;
    }

    /**
     * @return the UTC date, as a String in "yyyy-MM-dd" format, of the earliest data measurement
     *
     * @throws IllegalArgumentException
     *         if the value is not given or invalid
     */
    public String getBeginDatestamp() throws IllegalArgumentException {
        Double value = omeMData.getDataStartTime();
        if ( (value == null) || value.isNaN() || value.isInfinite() )
            throw new IllegalArgumentException("Invalid data start time: " + value);
        Date beginTime = new Date(Math.round(value * 1000.0));
        return DATE_STAMPER.format(beginTime);
    }

    /**
     * @return the UTC date, as a String in "yyyy-MM-dd" format, of the latest data measurement
     *
     * @throws IllegalArgumentException
     *         if the value is not given or invalid
     */
    public String getEndDatestamp() throws IllegalArgumentException {
        Double value = omeMData.getDataEndTime();
        if ( (value == null) || value.isNaN() || value.isInfinite() )
            throw new IllegalArgumentException("Invalid data end time: " + value);
        Date endTime = new Date(Math.round(value * 1000.0));
        return DATE_STAMPER.format(endTime);
    }

    /**
     * @return the name given by the PI for this dataset; never null but may be empty
     */
    public String getDatasetName() {
        String cruiseName = omeMData.getDatasetName();
        if ( cruiseName == null )
            return "";
        return cruiseName;
    }

    /**
     * @return the platform name for this dataset; never null but may be empty
     */
    public String getPlatformName() {
        String platformName = omeMData.getPlatformName();
        if ( platformName == null )
            return "";
        return platformName;
    }

    public void setPlatformName(String platformName) {
        omeMData.setPlatformName(platformName);
    }

    /**
     * @return the semicolon-separated list of PI names for this dataset; never null but may be empty
     */
    public String getPINames() {
        ArrayList<String> investigators = omeMData.getInvestigators();
        if ( investigators == null )
            return "";
        StringBuilder piNames = new StringBuilder();
        boolean isFirst = true;
        for (String name : investigators) {
            if ( isFirst )
                isFirst = false;
            else
                piNames.append("; ");
            piNames.append(name);
        }
        return piNames.toString();
    }

    /**
     * @return PI names in citation order
     */
    public ArrayList<String> getInvestigators() {
        return omeMData.getInvestigators();
    }

    /**
     * @return associated organization for each PI
     */
    public ArrayList<String> getOrganizations() {
        return omeMData.getOrganizations();
    }

    /**
     * @param piNames
     *         the PI names, in citation order, to assign
     * @param piOrgs
     *         associated organization for each PI to assign
     */
    public void setInvestigatorsAndOrganizations(ArrayList<String> piNames, ArrayList<String> piOrgs) {
        omeMData.setInvestigatorsAndOrganizations(piNames, piOrgs);
    }

    /**
     * @return the DOI(s) for the source dataset; never null but may be empty
     */
    public String getDatasetDOI() {
        String dataSetDOI = omeMData.getDatasetDOI();
        if ( dataSetDOI == null )
            dataSetDOI = "";
        return dataSetDOI;
    }

    /**
     * @return the reference(s) for the source dataset; never null but may be empty
     */
    public String getDatasetRefs() {
        String dataSetRefs = omeMData.getDatasetRefs();
        if ( dataSetRefs == null )
            dataSetRefs = "";
        return dataSetRefs;
    }

}
