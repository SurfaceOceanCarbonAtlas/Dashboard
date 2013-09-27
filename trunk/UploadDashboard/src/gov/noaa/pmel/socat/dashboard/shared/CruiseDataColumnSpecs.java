/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Used for specifying column types.  Contains data column headers 
 * and only some of the cruise data for all data columns.
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnSpecs implements Serializable {

	private static final long serialVersionUID = -5011728457014905569L;

	String expocode;
	int numRowsTotal;
	ArrayList<ArrayList<String>> dataValues;
	ArrayList<CruiseDataColumnType> columnTypes;

	/**
	 * Creates with no cruise data or column types
	 */
	public CruiseDataColumnSpecs() {
		expocode = "";
		numRowsTotal = 0;
		dataValues = new ArrayList<ArrayList<String>>();
		columnTypes = new ArrayList<CruiseDataColumnType>();
	}

	/**
	 * @return 
	 * 		the cruise expocode; never null
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the cruise expocode to set;
	 * 		if null, an empty string will be assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = "";
		else
			this.expocode = expocode;
	}

	/**
	 * @return 
	 * 		the number of rows of data in the complete data set
	 * 		for the cruise; never negative.
	 */
	public int getNumRowsTotal() {
		return numRowsTotal;
	}

	/**
	 * @param numRowsTotal 
	 * 		the number of rows of data in the complete data set
	 * 		to set for the cruise; cannot be negative.
	 */
	public void setNumRowsTotal(int numRowsTotal) {
		if ( numRowsTotal < 0 )
			throw new IllegalArgumentException(
					"total number of rows cannot be negative");
		this.numRowsTotal = numRowsTotal;
	}

	/**
	 * @return 
	 * 		the data values; may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<ArrayList<String>> getDataValues() {
		return dataValues;
	}

	/**
	 * @param dataValues 
	 * 		the list of data values to set.  The list in this object 
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
	 * @return 
	 * 		the data column types; may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<CruiseDataColumnType> getColumnTypes() {
		return columnTypes;
	}

	/**
	 * @param columnTypes 
	 * 		the list of data column types to set.  The list in 
	 * 		this object is cleared and all the contents of the
	 * 		given list, if not null, are added. 
	 */
	public void setColumnTypes(ArrayList<CruiseDataColumnType> columnTypes) {
		this.columnTypes.clear();
		if ( columnTypes != null )
			this.columnTypes.addAll(columnTypes);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = numRowsTotal;
		result = result * prime + expocode.hashCode();
		result = result * prime + columnTypes.hashCode();
		result = result * prime + dataValues.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof CruiseDataColumnSpecs) )
			return false;
		CruiseDataColumnSpecs other = (CruiseDataColumnSpecs) obj;

		if ( numRowsTotal != other.numRowsTotal )
			return false;
		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! columnTypes.equals(other.columnTypes) )
			return false;
		if ( ! dataValues.equals(other.dataValues) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CruiseDataColumnSpecs" +
				"[ expocode=" + expocode +
				", numRowsTotal=" + numRowsTotal + 
				", columnTypes=" + columnTypes.toString() +
				", dataValues=" + dataValues.toString() + 
				" ]";
	}

}
