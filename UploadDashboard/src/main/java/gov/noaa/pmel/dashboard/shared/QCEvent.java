/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents QC of a data variable, in general, for a dataset. Note that the id field is ignored in the hashCode and
 * equals methods.
 *
 * @author Karl Smith
 */
public class QCEvent implements Serializable, IsSerializable {

    private static final long serialVersionUID = 3499225143460826432L;

    protected Long id;
    protected Date flagDate;
    protected String flagName;
    protected String flagValue;
    protected String datasetId;
    protected String regionId;
    protected String version;
    protected String username;
    protected String realname;
    protected String comment;

    /**
     * Creates an empty QC event where id is zero, the flag date is {@link DashboardUtils#DATE_MISSING_VALUE}, the
     * region ID is {@link DashboardUtils#GLOBAL_REGION_ID}, and all other values (strings) are {@link
     * DashboardUtils#STRING_MISSING_VALUE}.
     */
    public QCEvent() {
        id = 0L;
        flagDate = DashboardUtils.DATE_MISSING_VALUE;
        flagName = DashboardUtils.STRING_MISSING_VALUE;
        flagValue = DashboardUtils.STRING_MISSING_VALUE;
        datasetId = DashboardUtils.STRING_MISSING_VALUE;
        regionId = DashboardUtils.GLOBAL_REGION_ID;
        version = DashboardUtils.STRING_MISSING_VALUE;
        username = DashboardUtils.STRING_MISSING_VALUE;
        realname = DashboardUtils.STRING_MISSING_VALUE;
        comment = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the id; never null, but may be zero if not assigned
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *         the id to set; if null, zero is assigned
     */
    public void setId(Long id) {
        if ( id == null )
            this.id = 0L;
        else
            this.id = id;
    }

    /**
     * @return the date of the QC flag; never null but may be {@link DashboardUtils#DATE_MISSING_VALUE}
     */
    public Date getFlagDate() {
        return flagDate;
    }

    /**
     * @param flagDate
     *         the date of the QC flag to set; if null, {@link DashboardUtils#DATE_MISSING_VALUE} is assigned
     */
    public void setFlagDate(Date flagDate) {
        if ( flagDate == null )
            this.flagDate = DashboardUtils.DATE_MISSING_VALUE;
        else
            this.flagDate = flagDate;
    }

    /**
     * @return the QC flag name; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getFlagName() {
        return flagName;
    }

    /**
     * @param flagName
     *         the QC flag name to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setFlagName(String flagName) {
        if ( flagName == null )
            this.flagName = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.flagName = flagName;
    }

    /**
     * @return the QC flag value; never null, but may be {@link DashboardUtils#CHAR_MISSING_VALUE}
     */
    public String getFlagValue() {
        return flagValue;
    }

    /**
     * @param flagValue
     *         the QC flag value to set; if null, {@link DashboardUtils#CHAR_MISSING_VALUE} is assigned
     */
    public void setFlagValue(String flagValue) {
        if ( flagValue == null )
            this.flagValue = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.flagValue = flagValue;
    }

    /**
     * @return the ID of the dataset; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @param datasetId
     *         the ID of the dataset to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDatasetId(String datasetId) {
        if ( datasetId == null )
            this.datasetId = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.datasetId = datasetId;
    }

    /**
     * @return the region ID; never null
     */
    public String getRegionId() {
        return regionId;
    }

    /**
     * @param regionId
     *         the regionId to set; if null, {@link DashboardUtils#GLOBAL_REGION_ID} is assigned
     */
    public void setRegionId(String regionId) {
        if ( regionId == null )
            this.regionId = DashboardUtils.GLOBAL_REGION_ID;
        else
            this.regionId = regionId;
    }

    /**
     * @return the data collection version; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *         the data collection version to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setVersion(String version) {
        if ( version == null )
            this.version = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.version = version;
    }

    /**
     * @return the reviewer username; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *         the reviewer username to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setUsername(String username) {
        if ( username == null )
            this.username = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.username = username;
    }

    /**
     * @return the reviewer's actual name; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getRealname() {
        return realname;
    }

    /**
     * @param realname
     *         the reviewer's actual name to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setRealname(String realname) {
        if ( realname == null )
            this.realname = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.realname = realname;
    }

    /**
     * @return the comment; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *         the comment to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setComment(String comment) {
        if ( comment == null )
            this.comment = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.comment = comment;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = flagDate.hashCode();
        result = result * prime + flagName.hashCode();
        result = result * prime + flagValue.hashCode();
        result = result * prime + datasetId.hashCode();
        result = result * prime + regionId.hashCode();
        result = result * prime + version.hashCode();
        result = result * prime + username.hashCode();
        result = result * prime + realname.hashCode();
        result = result * prime + comment.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !(obj instanceof QCEvent) )
            return false;
        QCEvent other = (QCEvent) obj;

        if ( !flagDate.equals(other.flagDate) )
            return false;
        if ( !flagName.equals(other.flagName) )
            return false;
        if ( !flagValue.equals(other.flagValue) )
            return false;
        if ( !datasetId.equals(other.datasetId) )
            return false;
        if ( !regionId.equals(other.regionId) )
            return false;
        if ( !version.equals(other.version) )
            return false;
        if ( !username.equals(other.username) )
            return false;
        if ( !realname.equals(other.realname) )
            return false;
        if ( !comment.equals(other.comment) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "QCEvent" +
                "[\n    id=" + id.toString() +
                ";\n    flagDate=" + flagDate.toString() +
                ";\n    flagName=" + flagName +
                ";\n    flagValue=" + flagValue +
                ";\n    datasetId=" + datasetId +
                ";\n    regionId=" + regionId +
                ";\n    version=" + version +
                ";\n    username=" + username +
                ";\n    realname=" + realname +
                ";\n    comment=" + comment +
                "\n]";
    }

}
