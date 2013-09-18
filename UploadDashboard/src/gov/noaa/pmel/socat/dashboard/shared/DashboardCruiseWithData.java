/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data given an uploaded cruise data file
 * 
 * @author Karl Smith
 */
public class DashboardCruiseWithData extends DashboardCruise {

	private static final long serialVersionUID = 5819589890074395617L;

	String version;
	ArrayList<String> preamble;
	ArrayList<String> columnNames;
	ArrayList<ArrayList<String>> dataValues;

	/**
	 * Creates with no cruise data
	 */
	public DashboardCruiseWithData() {
		version = "";
		preamble = new ArrayList<String>();
		columnNames = new ArrayList<String>();
		dataValues = new ArrayList<ArrayList<String>>();
	}

	/**
	 * @return 
	 * 		the cruise version; never null
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version 
	 * 		the cruise version (after trimming) to set;
	 * 		if null, sets to an empty string
	 */
	public void setVersion(String version) {
		if ( version == null )
			this.version = "";
		else
			this.version = version.trim();
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
		int result = super.hashCode();
		result = result * prime + version.hashCode();
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

		if ( ! ( obj instanceof DashboardCruiseWithData ) )
			return false;
		DashboardCruiseWithData other = (DashboardCruiseWithData) obj;

		if ( ! super.equals(other) ) 
			return false;
		if ( ! version.equals(other.version) ) 
			return false;
		if ( ! preamble.equals(other.preamble) ) 
			return false;
		if ( ! columnNames.equals(other.columnNames) )
			return false;
		if ( ! dataValues.equals(other.dataValues) ) 
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "DashboardCruiseWithData" +
					  "[ " + super.toString() +
					  "; version = " + version +
					  "; preamble = " + preamble.toString() +
					  "; columnNames = " + columnNames.toString() +
					  "; dataValues = " + dataValues.toString() +
					  " ]";
		return repr;
	}

}
