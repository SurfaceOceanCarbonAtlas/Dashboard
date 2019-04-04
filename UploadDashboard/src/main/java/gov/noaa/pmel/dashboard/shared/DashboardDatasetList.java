/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents a list of uploaded datasets for a user, mapped by dataset ID.
 * Also provides some other server-side information to the client.
 *
 * @author Karl Smith
 */
public class DashboardDatasetList extends HashMap<String,DashboardDataset> implements Serializable, IsSerializable {

    private static final long serialVersionUID = 232139378086273067L;

    protected String username;
    // The following indicates whether or not the above user has manager or admin privileges.
    // Currently not used anymore, but potentially could be used again.
    protected boolean manager;
    // The following gives the file extension for image files produced by the server
    // (".gif" for Ferret-generated images, ".png" for PyFerret-generated images).
    protected String imageExtension;

    /**
     * Creates without a user or any datasets
     */
    public DashboardDatasetList() {
        super();
        username = DashboardUtils.STRING_MISSING_VALUE;
        manager = false;
        imageExtension = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the username; never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *         the username to set; if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public void setUsername(String username) {
        if ( username == null )
            this.username = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.username = username;
    }

    /**
     * @return if this user is a manager/admin
     */
    public boolean isManager() {
        return manager;
    }

    /**
     * @param manager
     *         set if this user is a manager/admin
     */
    public void setManager(boolean manager) {
        this.manager = manager;
    }

    /**
     * @return the image filename extension (including the leading '.', if appropriate);
     *         never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getImageExtension() {
        return imageExtension;
    }

    /**
     * @param imageExtension
     *         the image filename extension (including the leading '.', if appropriate) to assign;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned.
     */
    public void setImageExtension(String imageExtension) {
        if ( imageExtension == null )
            this.imageExtension = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.imageExtension = imageExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = username.hashCode();
        result = result * prime + Boolean.valueOf(manager).hashCode();
        result = result * prime + imageExtension.hashCode();
        result = result * prime + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !(obj instanceof DashboardDatasetList) )
            return false;
        DashboardDatasetList other = (DashboardDatasetList) obj;

        if ( !username.equals(other.username) )
            return false;

        if ( manager != other.manager )
            return false;

        if ( !imageExtension.equals(other.imageExtension) )
            return false;

        if ( !super.equals(other) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        String repr = "DashboardDatasetList" +
                "[\n    username=" + username +
                ",\n    manager=" + Boolean.valueOf(manager).toString() +
                ",\n    imageExtension=" + imageExtension;
        for (String cruiseId : keySet()) {
            repr += ",\n    " + cruiseId + ":" + get(cruiseId).toString();
        }
        repr += "\n]";
        return repr;
    }

}
