package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Represents an uploaded dataset and its current status.
 *
 * @author Karl Smith
 */
public class DashboardDataset implements Serializable, IsSerializable {

    private static final long serialVersionUID = 1136055198038962864L;

    protected boolean selected;
    protected String version;
    protected String owner;
    protected String datasetId;
    protected String dataCheckStatus;
    protected String omeTimestamp;
    protected TreeSet<String> addlDocs;
    protected DatasetQCStatus submitStatus;
    protected String archiveStatus;
    protected ArrayList<String> archiveTimestamps;
    protected String uploadFilename;
    protected String uploadTimestamp;
    protected String sourceDOI;
    protected String sourceURL;
    protected String enhancedDOI;
    protected String enhancedURL;
    protected int numDataRows;
    protected int numErrorRows;
    protected int numWarnRows;
    protected ArrayList<String> userColNames;
    // For each data column, a DataColumnType with type, unit, and missing value
    protected ArrayList<DataColumnType> dataColTypes;
    // Checker-generated QC flags without comments
    protected TreeSet<DataQCFlag> checkerFlags;
    // PI-provided QC flags without comments
    protected TreeSet<DataQCFlag> userFlags;

    /**
     * Create an empty dashboard dataset
     */
    public DashboardDataset() {
        selected = false;
        version = DashboardUtils.STRING_MISSING_VALUE;
        owner = DashboardUtils.STRING_MISSING_VALUE;
        datasetId = DashboardUtils.STRING_MISSING_VALUE;
        dataCheckStatus = DashboardUtils.CHECK_STATUS_NOT_CHECKED;
        omeTimestamp = DashboardUtils.STRING_MISSING_VALUE;
        addlDocs = new TreeSet<String>();
        submitStatus = new DatasetQCStatus();
        archiveStatus = DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED;
        archiveTimestamps = new ArrayList<String>(1);
        uploadFilename = DashboardUtils.STRING_MISSING_VALUE;
        uploadTimestamp = DashboardUtils.STRING_MISSING_VALUE;
        sourceDOI = DashboardUtils.STRING_MISSING_VALUE;
        sourceURL = DashboardUtils.STRING_MISSING_VALUE;
        enhancedDOI = DashboardUtils.STRING_MISSING_VALUE;
        enhancedURL = DashboardUtils.STRING_MISSING_VALUE;
        numDataRows = 0;
        numErrorRows = 0;
        numWarnRows = 0;
        userColNames = new ArrayList<String>();
        dataColTypes = new ArrayList<DataColumnType>();
        checkerFlags = new TreeSet<DataQCFlag>();
        userFlags = new TreeSet<DataQCFlag>();
    }

    /**
     * @return Boolean.TRUE if the dataset is suspended, excluded, or not submitted;
     *         Boolean.FALSE if the dataset is submitted or acceptable but not published;
     *         null if the dataset is (acceptable and) published.
     */
    public Boolean isEditable() {
        // true for datasets that are suspended, excluded, or not yet submitted
        if ( submitStatus.isEditable() )
            return Boolean.TRUE;
        // null for published datasets
        if ( DashboardUtils.ARCHIVE_STATUS_ARCHIVED.equals(archiveStatus) )
            return null;
        // false for submitted or QC-ed but not published
        return Boolean.FALSE;
    }

    /**
     * @return if the dataset is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *         set if the dataset is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the dataset version;
     *         never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *         the dataset version (after trimming) to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setVersion(String version) {
        if ( version == null )
            this.version = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.version = version.trim();
    }

    /**
     * @return the owner for this dataset;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner
     *         the dataset owner (after trimming) to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setOwner(String owner) {
        if ( owner == null )
            this.owner = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.owner = owner.trim();
    }

    /**
     * @return the dataset ID;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @param datasetId
     *         the dataset ID to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDatasetId(String datasetId) {
        if ( datasetId == null )
            this.datasetId = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.datasetId = datasetId;
    }

    /**
     * @return the data check status; never null
     */
    public String getDataCheckStatus() {
        return dataCheckStatus;
    }

    /**
     * @param dataCheckStatus
     *         the data check status to set;
     *         if null, sets to {@link DashboardUtils#CHECK_STATUS_NOT_CHECKED}
     */
    public void setDataCheckStatus(String dataCheckStatus) {
        if ( dataCheckStatus == null )
            this.dataCheckStatus = DashboardUtils.CHECK_STATUS_NOT_CHECKED;
        else
            this.dataCheckStatus = dataCheckStatus;
    }

    /**
     * @return the OME metadata timestamp;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getOmeTimestamp() {
        return omeTimestamp;
    }

    /**
     * @param omeTimestamp
     *         the OME metadata timestamp to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setOmeTimestamp(String omeTimestamp) {
        if ( omeTimestamp == null )
            this.omeTimestamp = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.omeTimestamp = omeTimestamp;
    }

    /**
     * @return the additional document "filename ; timestamp" strings associated with this dataset;
     *         never null but may be empty. The actual set of strings in this object is returned.
     */
    public TreeSet<String> getAddlDocs() {
        return addlDocs;
    }

    /**
     * @param addlDocs
     *         the set of additional document "filename ; timestamp" strings for this dataset.
     *         The set in this object is cleared and all the contents of the given set, if not null, are added.
     */
    public void setAddlDocs(TreeSet<String> addlDocs) {
        this.addlDocs.clear();
        if ( addlDocs != null )
            this.addlDocs.addAll(addlDocs);
    }

    /**
     * @return the submission status; never null
     */
    public DatasetQCStatus getSubmitStatus() {
        return new DatasetQCStatus(submitStatus);
    }

    /**
     * @param submitStatus
     *         the submission status (after trimming) to set;
     *         if null, a default DatasetQCStatus (all {@link DatasetQCStatus.Status#PRIVATE}) is assigned
     */
    public void setSubmitStatus(DatasetQCStatus submitStatus) {
        this.submitStatus = new DatasetQCStatus(submitStatus);
    }

    /**
     * @return the archive submission status;
     *         never null but may be {@link DashboardUtils#ARCHIVE_STATUS_NOT_SUBMITTED} if not assigned
     */
    public String getArchiveStatus() {
        return archiveStatus;
    }

    /**
     * @param archiveStatus
     *         the archive submission status (after trimming) to set;
     *         if null, {@link DashboardUtils#ARCHIVE_STATUS_NOT_SUBMITTED} is assigned
     */
    public void setArchiveStatus(String archiveStatus) {
        if ( archiveStatus == null )
            this.archiveStatus = DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED;
        else
            this.archiveStatus = archiveStatus.trim();
    }

    /**
     * @return the list of archive submission timestamps; never null but may be empty.
     *         Any values given in the list will not be blank.
     *         The actual list used in this object is returned.
     */
    public ArrayList<String> getArchiveTimestamps() {
        return archiveTimestamps;
    }

    /**
     * @param archiveTimestamps
     *         the list of archive timestamps to assign.
     *         The internal list is cleared and the contents of the given list, if not null, are added.
     */
    public void setArchiveTimestamps(ArrayList<String> archiveTimestamps) {
        this.archiveTimestamps.clear();
        if ( archiveTimestamps != null ) {
            for (String stamp : archiveTimestamps) {
                if ( stamp == null )
                    continue;
                stamp = stamp.trim();
                if ( !stamp.isEmpty() )
                    this.archiveTimestamps.add(stamp);
            }
        }
    }

    /**
     * @return the uploaded data filename;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getUploadFilename() {
        return uploadFilename;
    }

    /**
     * @param uploadFilename
     *         the uploaded data filename (after trimming) to set;
     *         if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public void setUploadFilename(String uploadFilename) {
        if ( uploadFilename == null )
            this.uploadFilename = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.uploadFilename = uploadFilename.trim();
    }

    /**
     * @return the uploaded data timestamp;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getUploadTimestamp() {
        return uploadTimestamp;
    }

    /**
     * @param uploadTimestamp
     *         the uploaded data timestamp (after trimming) to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setUploadTimestamp(String uploadTimestamp) {
        if ( uploadTimestamp == null )
            this.uploadTimestamp = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.uploadTimestamp = uploadTimestamp.trim();
    }

    /**
     * @return the DOI of the source dataset document;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getSourceDOI() {
        return sourceDOI;
    }

    /**
     * @param sourceDOI
     *         the DOI (after trimming) of the source dataset document to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setSourceDOI(String sourceDOI) {
        if ( sourceDOI == null )
            this.sourceDOI = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.sourceDOI = sourceDOI.trim();
    }

    /**
     * @return the landing page URL of the source dataset document;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getSourceURL() {
        return sourceURL;
    }

    /**
     * @param sourceURL
     *         the landing page URL (after trimming) of the source dataset document to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setSourceURL(String sourceURL) {
        if ( sourceURL == null )
            this.sourceURL = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.sourceURL = sourceURL.trim();
    }

    /**
     * @return the DOI of the enhanced data document;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getEnhancedDOI() {
        return enhancedDOI;
    }

    /**
     * @param enhancedDOI
     *         the DOI (after trimming) of the enhanced data document to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setEnhancedDOI(String enhancedDOI) {
        if ( enhancedDOI == null )
            this.enhancedDOI = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.enhancedDOI = enhancedDOI.trim();
    }

    /**
     * @return the landing page URL of the enhanced data document;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getEnhancedURL() {
        return enhancedURL;
    }

    /**
     * @param enhancedURL
     *         the landing page URL (after trimming) of the enhanced data document to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setEnhancedURL(String enhancedURL) {
        if ( enhancedURL == null )
            this.enhancedURL = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.enhancedURL = enhancedURL.trim();
    }

    /**
     * @return the total number of data measurements (data rows) for the dataset
     */
    public int getNumDataRows() {
        return numDataRows;
    }

    /**
     * @param numDataRows
     *         the total number of data measurements (data rows) to set for the dataset
     */
    public void setNumDataRows(int numDataRows) {
        this.numDataRows = numDataRows;
    }

    /**
     * @return the number of data rows with error messages from the automated data checker
     */
    public int getNumErrorRows() {
        return numErrorRows;
    }

    /**
     * @param numErrorRows
     *         the number of data rows with error messages from the automated data checker to set
     */
    public void setNumErrorRows(int numErrorRows) {
        this.numErrorRows = numErrorRows;
    }

    /**
     * @return the number of data rows with warning messages from the automated data checker
     */
    public int getNumWarnRows() {
        return numWarnRows;
    }

    /**
     * @param numWarnRows
     *         the number of data rows with warning messages from the automated data checker to set
     */
    public void setNumWarnRows(int numWarnRows) {
        this.numWarnRows = numWarnRows;
    }

    /**
     * @return the list of data column header names as they appeared in the original data file for this dataset;
     *         never null but may be empty.  The actual list in this object is returned.
     */
    public ArrayList<String> getUserColNames() {
        return userColNames;
    }

    /**
     * @param userColNames
     *         the list of data column header names as they appeared in the original data file for this dataset.
     *         The list in this object is cleared and all the contents of the given list, if not null, are added.
     */
    public void setUserColNames(ArrayList<String> userColNames) {
        this.userColNames.clear();
        if ( userColNames != null )
            this.userColNames.addAll(userColNames);
    }

    /**
     * @return the list of data column types for this dataset; may be empty but never null.
     *         The actual list in this object is returned.
     */
    public ArrayList<DataColumnType> getDataColTypes() {
        return dataColTypes;
    }

    /**
     * @param dataColTypes
     *         the list of data column types for this dataset.
     *         The list in this object is cleared and all the contents of the given list, if not null, are added.
     *         Note that this is a shallow copy; the given DataColumnType objects are reused.
     */
    public void setDataColTypes(ArrayList<DataColumnType> dataColTypes) {
        this.dataColTypes.clear();
        if ( dataColTypes != null )
            this.dataColTypes.addAll(dataColTypes);
    }

    /**
     * @return the set of automated data checker data QC flags; never null but may be empty.
     *         The actual set in this object is returned.
     */
    public TreeSet<DataQCFlag> getCheckerFlags() {
        return checkerFlags;
    }

    /**
     * @param checkerFlags
     *         the set of automated data checker data QC flags to assign.
     *         The set in this object is cleared and all the contents of the given collection, if not null, are added.
     *         Note that this is a shallow copy; the given DataQCFlag objects are reused.
     */
    public void setCheckerFlags(TreeSet<DataQCFlag> checkerFlags) {
        this.checkerFlags.clear();
        if ( checkerFlags != null )
            this.checkerFlags.addAll(checkerFlags);
    }

    /**
     * @return The set of user-provided QC flags; never null but may be empty.
     *         The actual set in this object is returned.
     */
    public TreeSet<DataQCFlag> getUserFlags() {
        return userFlags;
    }

    /**
     * @param userFlags
     *         The set user-provided QC flags to assign.
     *         The set in this object is cleared and all the contents of the given Collection, if not null, are added.
     *         Note that this is a shallow copy; the given DataQCFlag objects are reused.
     */
    public void setUserFlags(TreeSet<DataQCFlag> userFlags) {
        this.userFlags.clear();
        if ( userFlags != null )
            this.userFlags.addAll(userFlags);
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = Boolean.valueOf(selected).hashCode();
        result = result * prime + version.hashCode();
        result = result * prime + owner.hashCode();
        result = result * prime + datasetId.hashCode();
        result = result * prime + dataCheckStatus.hashCode();
        result = result * prime + omeTimestamp.hashCode();
        result = result * prime + addlDocs.hashCode();
        result = result * prime + submitStatus.hashCode();
        result = result * prime + archiveStatus.hashCode();
        result = result * prime + archiveTimestamps.hashCode();
        result = result * prime + uploadFilename.hashCode();
        result = result * prime + uploadTimestamp.hashCode();
        result = result * prime + sourceDOI.hashCode();
        result = result * prime + sourceURL.hashCode();
        result = result * prime + enhancedDOI.hashCode();
        result = result * prime + enhancedURL.hashCode();
        result = result * prime + Integer.hashCode(numDataRows);
        result = result * prime + Integer.hashCode(numErrorRows);
        result = result * prime + Integer.hashCode(numWarnRows);
        result = result * prime + userColNames.hashCode();
        result = result * prime + dataColTypes.hashCode();
        result = result * prime + checkerFlags.hashCode();
        result = result * prime + userFlags.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !(obj instanceof DashboardDataset) )
            return false;
        DashboardDataset other = (DashboardDataset) obj;

        if ( selected != other.selected )
            return false;
        if ( !version.equals(other.version) )
            return false;
        if ( !owner.equals(other.owner) )
            return false;
        if ( !datasetId.equals(other.datasetId) )
            return false;
        if ( !dataCheckStatus.equals(other.dataCheckStatus) )
            return false;
        if ( !omeTimestamp.equals(other.omeTimestamp) )
            return false;
        if ( !addlDocs.equals(other.addlDocs) )
            return false;
        if ( !submitStatus.equals(other.submitStatus) )
            return false;
        if ( !archiveStatus.equals(other.archiveStatus) )
            return false;
        if ( !archiveTimestamps.equals(other.archiveTimestamps) )
            return false;
        if ( !uploadFilename.equals(other.uploadFilename) )
            return false;
        if ( !uploadTimestamp.equals(other.uploadTimestamp) )
            return false;
        if ( !sourceDOI.equals(other.sourceDOI) )
            return false;
        if ( !sourceURL.equals(other.sourceURL) )
            return false;
        if ( !enhancedDOI.equals(other.enhancedDOI) )
            return false;
        if ( !enhancedURL.equals(other.enhancedURL) )
            return false;
        if ( numDataRows != other.numDataRows )
            return false;
        if ( numErrorRows != other.numErrorRows )
            return false;
        if ( numWarnRows != other.numWarnRows )
            return false;
        if ( !userColNames.equals(other.userColNames) )
            return false;
        if ( !dataColTypes.equals(other.dataColTypes) )
            return false;
        if ( !checkerFlags.equals(other.checkerFlags) )
            return false;
        if ( !userFlags.equals(other.userFlags) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DashboardDataset" +
                "[\n    selected=" + Boolean.toString(selected) +
                ";\n    version = " + version +
                ";\n    owner=" + owner +
                ";\n    datasetId=" + datasetId +
                ";\n    dataCheckStatus=" + dataCheckStatus +
                ";\n    omeTimestamp=" + omeTimestamp +
                ";\n    addlDocs=" + addlDocs.toString() +
                ";\n    submitStatus=" + submitStatus.toString() +
                ";\n    archiveStatus=" + archiveStatus +
                ";\n    archiveTimestamps=" + archiveTimestamps +
                ";\n    uploadFilename=" + uploadFilename +
                ";\n    uploadTimestamp=" + uploadTimestamp +
                ";\n    sourceDOI=" + sourceDOI +
                ";\n    sourceURL=" + sourceURL +
                ";\n    enhancedDOI=" + enhancedDOI +
                ";\n    enhancedURL=" + enhancedURL +
                ";\n    numDataRows=" + Integer.toString(numDataRows) +
                ";\n    numErrorRows=" + Integer.toString(numErrorRows) +
                ";\n    numWarnRows=" + Integer.toString(numWarnRows) +
                ";\n    userColNames=" + userColNames.toString() +
                ";\n    dataColTypes=" + dataColTypes.toString() +
                ";\n    checkerFlags = " + checkerFlags.toString() +
                ";\n    userFlags = " + userFlags.toString() +
                "\n]";
    }

}
