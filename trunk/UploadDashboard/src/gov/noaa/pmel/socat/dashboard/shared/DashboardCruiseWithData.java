/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;

/**
 * Represents the data given an uploaded cruise data file
 * 
 * @author Karl Smith
 */
public class DashboardCruiseWithData extends DashboardCruise {

	private static final long serialVersionUID = -7383174672122766457L;

	String version;
	ArrayList<String> preamble;
	ArrayList<ArrayList<String>> dataValues;

	/**
	 * Creates with no cruise data
	 */
	public DashboardCruiseWithData() {
		version = "";
		preamble = new ArrayList<String>();
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
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<String> getPreamble() {
		return preamble;
	}

	/**
	 * @param preamble 
	 * 		the metadata preamble strings to assign.  The list in 
	 * 		this object is cleared and all the contents of the
	 * 		given list, if not null, are added. 
	 */
	public void setPreamble(ArrayList<String> preamble) {
		this.preamble.clear();
		if ( preamble != null )
			this.preamble.addAll(preamble);
	}

	/**
	 * @return 
	 * 		the list of data string lists; 
	 * 		may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<ArrayList<String>> getDataValues() {
		return dataValues;
	}

	/**
	 * @param dataValues 
	 * 		the lists of data values to assign.  The list in this object
	 * 		is cleared and all the contents of the given list, if not 
	 * 		null, are added.  Note that this is a shallow copy; the 
	 * 		lists in the given list are not copied but used directly.
	 */
	public void setDataValues(ArrayList<ArrayList<String>> dataValues) {
		this.dataValues.clear();
		if ( dataValues != null )
			this.dataValues.addAll(dataValues);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + version.hashCode();
		result = result * prime + preamble.hashCode();
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
		if ( ! dataValues.equals(other.dataValues) ) 
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "DashboardCruiseWithData" +
					  "[ version = " + version +
					  ";\n    " + super.toString() +
					  ";\n    preamble = " + preamble.toString() +
					  ";\n    dataValues = " + dataValues.toString() +
					  " ]";
		return repr;
	}

}
