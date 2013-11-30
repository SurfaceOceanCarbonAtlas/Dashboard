/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents the data given an uploaded cruise data file
 * 
 * @author Karl Smith
 */
public class DashboardCruiseWithData extends DashboardCruise 
						implements Serializable, IsSerializable {

	private static final long serialVersionUID = -8946792242247576598L;

	String version;
	ArrayList<String> preamble;
	ArrayList<ArrayList<String>> dataValues;
	ArrayList<HashSet<Integer>> woceThreeRowIndices;
	ArrayList<HashSet<Integer>> woceFourRowIndices;

	/**
	 * Creates with no cruise data
	 */
	public DashboardCruiseWithData() {
		version = "";
		preamble = new ArrayList<String>();
		dataValues = new ArrayList<ArrayList<String>>();
		woceThreeRowIndices = new ArrayList<HashSet<Integer>>();
		woceFourRowIndices = new ArrayList<HashSet<Integer>>();
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
	 * The outer list of the data values iterates over the data samples; 
	 * the rows of a table of data.  The inner list iterates over each 
	 * particular data value for that sample; an entry in the column 
	 * of a table of data.
	 * 
	 * @return 
	 * 		the list of data string lists; 
	 * 		may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<ArrayList<String>> getDataValues() {
		return dataValues;
	}

	/**
	 * The outer list of the data values iterates over the data samples; 
	 * the rows of a table of data.  The inner list iterates over each 
	 * particular data value for that sample; an entry in the column 
	 * of a table of data.
	 * 
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

	/**
	 * The list of sets of WOCE-3 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-3 
	 * (questionable) flag.  Presumably these sets will be small and 
	 * could be empty. 
	 * 
	 * @return 
	 * 		the list of sets of WOCE-3 data row indices; 
	 * 		may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<HashSet<Integer>> getWoceThreeRowIndices() {
		return woceThreeRowIndices;
	}

	/**
	 * The list of sets of WOCE-3 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-3 
	 * (questionable) flag.  Presumably these sets will be small and 
	 * could be empty. 
	 * 
	 * @param woceThreeRowIndices 
	 * 		the list of sets of WOCE-3 data row indices to assign. 
	 * 		The list in this object is cleared and all the contents 
	 * 		of the given list, if not null, are added.  Note that 
	 * 		this is a shallow copy; the sets in the given list are 
	 * 		not copied but used directly.
	 */
	public void setWoceThreeRowIndices(
					ArrayList<HashSet<Integer>> woceThreeRowIndices) {
		this.woceThreeRowIndices.clear();
		if ( woceThreeRowIndices != null )
			this.woceThreeRowIndices.addAll(woceThreeRowIndices);
	}

	/**
	 * The list of sets of WOCE-4 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-4 (bad)  
	 * flag.  Presumably these sets will be small and could be empty. 
	 * 
	 * @return 
	 * 		the list of sets of WOCE-4 data row indices; 
	 * 		may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<HashSet<Integer>> getWoceFourRowIndices() {
		return woceFourRowIndices;
	}

	/**
	 * The list of sets of WOCE-4 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-4 (bad)  
	 * flag.  Presumably these sets will be small and could be empty. 
	 * 
	 * @param woceFourRowIndices 
	 * 		the list of sets of WOCE-4 data row indices to assign. 
	 * 		The list in this object is cleared and all the contents 
	 * 		of the given list, if not null, are added.  Note that 
	 * 		this is a shallow copy; the sets in the given list are 
	 * 		not copied but used directly.
	 */
	public void setWoceFourRowIndices(
					ArrayList<HashSet<Integer>> woceFourRowIndices) {
		this.woceFourRowIndices.clear();
		if ( woceFourRowIndices != null )
			this.woceFourRowIndices.addAll(woceFourRowIndices);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + version.hashCode();
		result = result * prime + preamble.hashCode();
		result = result * prime + dataValues.hashCode();
		result = result * prime + woceThreeRowIndices.hashCode();
		result = result * prime + woceFourRowIndices.hashCode();
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
		if ( ! woceThreeRowIndices.equals(other.woceThreeRowIndices) ) 
			return false;
		if ( ! woceFourRowIndices.equals(other.woceFourRowIndices) ) 
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
					  ";\n    woceThreeRowIndices = " + woceThreeRowIndices.toString() +
					  ";\n    woceFourRowIndices = " + woceFourRowIndices.toString() +
					  " ]";
		return repr;
	}

}
