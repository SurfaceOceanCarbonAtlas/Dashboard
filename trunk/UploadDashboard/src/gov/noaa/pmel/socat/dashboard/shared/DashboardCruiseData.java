/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data given an uploaded cruise data file
 * 
 * @author Karl Smith
 */
public class DashboardCruiseData implements Serializable {

	private static final long serialVersionUID = 6974324789113457449L;

	String username;
	String filename;
	String version;
	String expocode;
	ArrayList<String> preamble;
	ArrayList<String> columnNames;
	ArrayList<ArrayList<String>> dataValues;

	/**
	 * Creates with no cruise data
	 */
	public DashboardCruiseData() {
		username = "";
		filename = "";
		version = "";
		expocode = "";
		preamble = new ArrayList<String>();
		columnNames = new ArrayList<String>();
		dataValues = new ArrayList<ArrayList<String>>();
	}

	/**
	 * @return the username; may be empty, but never null
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the username to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setUsername(String username) {
		if ( username == null )
			this.username = "";
		else
			this.username = username;
	}

	/**
	 * @return the filename; may be empty, but never null
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename 
	 * 		the filename to set;
	 * 		if null, an empty string is assigned
	 */
	public void setFilename(String filename) {
		if ( filename == null )
			this.filename = "";
		else
			this.filename = filename;
	}

	/**
	 * @return 
	 * 		the cruise version; may be empty, but never null
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version 
	 * 		the cruise version to set;
	 * 		if null, an empty string is assigned
	 */
	public void setVersion(String version) {
		if ( version == null )
			this.version = "";
		else
			this.version = version;
	}

	/**
	 * @return 
	 * 		the cruise expocode; may be empty, but never null
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the cruise expocode to set, after
	 * 		converting to upper-case;
	 * 		if null, an empty string is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = "";
		else
			this.expocode = expocode.toUpperCase();
	}

	/**
	 * @return 
	 * 		the list of metadata preamble strings;
	 * 		may be empty, but never null.
	 * 		The actual list in the instance is returned.
	 */
	public ArrayList<String> getPreamble() {
		return preamble;
	}

	/**
	 * @param preamble 
	 * 		the metadata preamble strings to assign;
	 * 		if null, the list is cleared.
	 */
	public void setPreamble(ArrayList<String> preamble) {
		this.preamble.clear();
		if ( preamble != null )
			this.preamble.addAll(preamble);
	}

	/**
	 * @return 
	 * 		the column names list; may be empty but never null.
	 * 		The actual list contained in the instance is returned.
	 */
	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnNames 
	 * 		the column names to assign;
	 * 		if null, the list is cleared.
	 */
	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames.clear();
		if ( columnNames != null )
			this.columnNames.addAll(columnNames);
	}

	/**
	 * @return 
	 * 		the list of data string lists; 
	 * 		may be empty but never null
	 * 		The actual list in the instance is returned.
	 */
	public ArrayList<ArrayList<String>> getDataValues() {
		return dataValues;
	}

	/**
	 * @param dataValues 
	 * 		the lists of data values to assign;
	 * 		if null the list is cleared
	 */
	public void setDataValues(ArrayList<ArrayList<String>> dataValues) {
		this.dataValues.clear();
		if ( dataValues != null )
			for ( List<String> datalist : dataValues )
				if ( datalist != null )
					this.dataValues.add(new ArrayList<String>(datalist));
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = username.hashCode();
		result = result * prime + filename.hashCode();
		result = result * prime + version.hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + preamble.hashCode();
		result = result * prime + columnNames.hashCode();
		result = result * prime + dataValues.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if (obj == null)
			return false;

		if ( ! ( obj instanceof DashboardCruiseData ) )
			return false;
		DashboardCruiseData other = (DashboardCruiseData) obj;

		if ( ! username.equals(other.username) ) 
			return false;
		if ( ! filename.equals(other.filename) ) 
			return false;
		if ( ! version.equals(other.version) ) 
			return false;
		if ( ! expocode.equals(other.expocode) ) 
			return false;
		if ( ! preamble.equals(other.preamble) ) 
			return false;
		if ( ! columnNames.equals(other.columnNames) )
			return false;
		if ( ! dataValues.equals(other.dataValues) ) 
			return false;

		return true;
	}

}
