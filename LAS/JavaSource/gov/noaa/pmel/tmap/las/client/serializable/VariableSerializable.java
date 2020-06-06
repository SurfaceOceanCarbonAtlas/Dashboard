/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.serializable;


import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author rhs
 *
 */
public class VariableSerializable extends Serializable implements IsSerializable {
    String CATID;
    String DSID;
    String DSName;
    String shortname;
    GridSerializable grid;
    List<String> components;
	boolean vector;
	
	
	/**
	 * 
	 */
	public VariableSerializable() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @return The category id of the category hosting this data set that is hosting this variable.
	 */
	public String getCATID() {
        return CATID;
    }
	/**
	 * @return the dSID
	 */
	public String getDSID() {
		return DSID;
	}
	/**
	 * Set the category ID of the category hosting the data set that is hosting this variable.
	 * @param catid
	 */
	public void setCATID(String catid) {
	    CATID = catid;
	}
	/**
	 * @param dsid the dSID to set
	 */
	public void setDSID(String dsid) {
		DSID = dsid;
	}
	
	/**
	 * @return the dSName
	 */
	public String getDSName() {
		return DSName;
	}
	/**
	 * @param name the dSName to set
	 */
	public void setDSName(String name) {
		DSName = name;
	}
	/**
	 * @return the grid
	 */
	public GridSerializable getGrid() {
		return grid;
	}
	/**
	 * @param grid the grid to set
	 */
	public void setGrid(GridSerializable grid) {
		this.grid = grid;
	}
	public List<String> getComponents() {
		return components;
	}
	public void setComponents(List<String> components) {
		this.components = components;
	}
	public boolean isVector() {
		return vector;
	}
	public void setVector(boolean vector) {
		this.vector = vector;
	}
	public boolean isDiscrete() {
	    return getAttributes().get("grid_type").equals("trajectory") || 
	    		getAttributes().get("grid_type").equals("trajectoryprofile") ||
	            getAttributes().get("grid_type").equals("scattered") || 
	            getAttributes().get("grid_type").equals("point") ||
	            getAttributes().get("grid_type").equals("timeseries") ||
	            getAttributes().get("grid_type").equals("profile") ; //TODO   ||   OTHER DSG TYPES
	}
	public boolean isSubset() {
		if ( getAttributes().get("subset_variable") != null ) {
			return getAttributes().get("subset_variable").equals("true");
		} else {
			return false;
		}
	}
	public boolean isPoint() {
		return getAttributes().get("grid_type").equals("point");
	}
	public boolean isTimeSeries() {
		return getAttributes().get("grid_type").equals("timeseries");
	}
	public boolean isTrajectory() {
		return getAttributes().get("grid_type").equals("trajectory");
	}
    public String getShortname() {
        return shortname;
    }
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
	
}
