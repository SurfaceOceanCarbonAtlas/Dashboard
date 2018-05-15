/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Set of sanity checker ADCMessages for a dataset, along with some dataset information.
 *
 * @author Karl Smith
 */
public class ADCMessageList extends HashSet<ADCMessage> implements Serializable, IsSerializable {

    private static final long serialVersionUID = 2922125729137984943L;

    protected String username;
    protected String datasetId;
    protected ArrayList<String> summaries;

    public ADCMessageList() {
        super();
        username = DashboardUtils.STRING_MISSING_VALUE;
        datasetId = DashboardUtils.STRING_MISSING_VALUE;
        summaries = new ArrayList<String>();
    }

    /**
     * @return the username; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *         the username to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setUsername(String username) {
        if ( username == null )
            this.username = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.username = username;
    }

    /**
     * @return the dataset ID; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @param datasetId
     *         the dataset to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDatasetId(String datasetId) {
        if ( datasetId == null )
            this.datasetId = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.datasetId = datasetId;
    }

    /**
     * @return the summary messages; never null but may be empty. The actual list contained in this object is returned.
     */
    public ArrayList<String> getSummaries() {
        return summaries;
    }

    /**
     * @param summaries
     *         the summary messages to assign.  The current list of summary messages is cleared, and then the contents
     *         of this list, if not null, are added to the list.
     */
    public void setSummaries(ArrayList<String> summaries) {
        this.summaries.clear();
        if ( summaries != null )
            this.summaries.addAll(summaries);
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + datasetId.hashCode();
        result = result * prime + username.hashCode();
        result = result * prime + summaries.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !(obj instanceof ADCMessageList) )
            return false;
        ADCMessageList other = (ADCMessageList) obj;

        if ( !datasetId.equals(other.datasetId) )
            return false;
        if ( !username.equals(other.username) )
            return false;
        if ( !summaries.equals(other.summaries) )
            return false;
        return super.equals(other);
    }

    @Override
    public String toString() {
        return "ADCMessageList" +
                "[\n    username=" + username +
                ",\n    datasetId=" + datasetId +
                ",\n    summaries=" + summaries.toString() +
                ",\n    " + super.toString() +
                " \n]";
    }

}
