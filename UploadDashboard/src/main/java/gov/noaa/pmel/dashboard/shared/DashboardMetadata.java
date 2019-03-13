/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Represents an uploaded metadata file of unknown contents.
 *
 * @author Karl Smith
 */
public class DashboardMetadata implements Serializable, IsSerializable {

    private static final long serialVersionUID = 8947621857406378016L;

    /**
     * Separator between the filename and the upload timestamp in additional document titles.
     */
    private static final String TITLE_SEPARATOR = " ; ";

    protected boolean selected;
    protected String datasetId;
    protected String filename;
    protected String uploadTimestamp;
    protected String owner;
    protected boolean conflicted;
    protected String version;

    /**
     * Creates an empty metadata document record
     */
    public DashboardMetadata() {
        selected = false;
        datasetId = DashboardUtils.STRING_MISSING_VALUE;
        filename = DashboardUtils.STRING_MISSING_VALUE;
        uploadTimestamp = DashboardUtils.STRING_MISSING_VALUE;
        owner = DashboardUtils.STRING_MISSING_VALUE;
        conflicted = false;
        version = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * Returns the additional documents title for this metadata.
     * Normally this title is the filename, followed by a space, a semicolon, another space, and the upload timestamp.
     * If the filename is empty, this title is empty.  If the upload timestamp is empty, the title is just the filename.
     */
    public String getAddlDocsTitle() {
        if ( DashboardUtils.STRING_MISSING_VALUE.equals(filename) )
            return "";
        if ( DashboardUtils.STRING_MISSING_VALUE.equals(uploadTimestamp) )
            return filename;
        return filename + TITLE_SEPARATOR + uploadTimestamp;
    }

    /**
     * Returns the metadata filename and the upload timestamp given in the document title.
     *
     * @param docTitle
     *         document title to parse
     *
     * @return string array of length two with the filename as the first string
     *         and the timestamp as the second filename.  If the title is empty,
     *         both strings in the returned array will be empty. If the title does not
     *         have a timestamp, the timestamp in the returned array will be empty.
     */
    public static String[] splitAddlDocsTitle(String docTitle) {
        String[] pieces = docTitle.split(TITLE_SEPARATOR, 2);
        if ( pieces.length == 1 )
            pieces = new String[] { docTitle, "" };
        return pieces;
    }

    /**
     * @return if the metadata document is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *         set whether this metadata document is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the dataset ID; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @param datasetId
     *         the dataset ID to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDatasetId(String datasetId) {
        if ( datasetId != null )
            this.datasetId = datasetId;
        else
            this.datasetId = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the filename; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename
     *         the filename to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setFilename(String filename) {
        if ( filename != null )
            this.filename = filename;
        else
            this.filename = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the upload timestamp; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getUploadTimestamp() {
        return uploadTimestamp;
    }

    /**
     * @param uploadTimestamp
     *         the upload timestamp to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setUploadTimestamp(String uploadTimestamp) {
        if ( uploadTimestamp != null )
            this.uploadTimestamp = uploadTimestamp;
        else
            this.uploadTimestamp = DashboardUtils.STRING_MISSING_VALUE;
        ;
    }

    /**
     * @return the owner; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner
     *         the owner to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setOwner(String owner) {
        if ( owner != null )
            this.owner = owner;
        else
            this.owner = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return true if conflicts have been detected in the metadata; false otherwise.
     */
    public boolean isConflicted() {
        return conflicted;
    }

    /**
     * @param conflicted
     *         any conflicts detected in the metadata object?
     */
    public void setConflicted(boolean conflicted) {
        this.conflicted = conflicted;
    }

    /**
     * @return the version; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *         the version to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setVersion(String version) {
        if ( version != null )
            this.version = version;
        else
            this.version = DashboardUtils.STRING_MISSING_VALUE;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = Boolean.valueOf(selected).hashCode();
        result = result * prime + datasetId.hashCode();
        result = result * prime + filename.hashCode();
        result = result * prime + uploadTimestamp.hashCode();
        result = result * prime + owner.hashCode();
        result = result * prime + Boolean.valueOf(conflicted).hashCode();
        result = result * prime + version.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !(obj instanceof DashboardMetadata) )
            return false;
        DashboardMetadata other = (DashboardMetadata) obj;

        if ( selected != other.selected )
            return false;
        if ( !datasetId.equals(other.datasetId) )
            return false;
        if ( !filename.equals(other.filename) )
            return false;
        if ( !uploadTimestamp.equals(other.uploadTimestamp) )
            return false;
        if ( !owner.equals(other.owner) )
            return false;
        if ( conflicted != other.conflicted )
            return false;
        if ( !version.equals(other.version) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DashboardMetadata" +
                "[ selected=" + Boolean.toString(selected) +
                ",\n  datasetId=" + datasetId +
                ",\n  filename=" + filename +
                ",\n  uploadTimestamp=" + uploadTimestamp +
                ",\n  owner=" + owner +
                ",\n  conflicted=" + Boolean.toString(conflicted) +
                ",\n  version=" + version +
                " ]";
    }

}
