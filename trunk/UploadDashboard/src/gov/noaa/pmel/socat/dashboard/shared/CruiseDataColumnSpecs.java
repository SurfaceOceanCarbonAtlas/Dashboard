/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for specifying column types.  Contains data column headers 
 * and only some of the cruise data for all data columns.
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnSpecs implements Serializable {

	private static final long serialVersionUID = -850895025884813130L;

	String expocode;
	int numRowsTotal;
	int firstDataRowIndex;
	ArrayList<String> columnNames;
	ArrayList<ArrayList<String>> dataValues;

	/**
	 * Creates with no cruise data
	 */
	public CruiseDataColumnSpecs() {
		expocode = "";
		numRowsTotal = 0;
		firstDataRowIndex = 0;
		columnNames = new ArrayList<String>();
		dataValues = new ArrayList<ArrayList<String>>();
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
	 * 		for the cruise; never negative
	 */
	public int getNumRowsTotal() {
		return numRowsTotal;
	}

	/**
	 * @param numRowsTotal 
	 * 		the number of rows of data in the complete data set
	 * 		to set for the cruise; cannot be negative
	 */
	public void setNumRowsTotal(int numRowsTotal) {
		if ( numRowsTotal < 0 )
			throw new IllegalArgumentException(
					"total number of rows cannot be negative");
		this.numRowsTotal = numRowsTotal;
	}

	/**
	 * @return 
	 * 		the row index in the complete data set of the
	 * 		first row of provided data; never negative
	 */
	public int getFirstDataRowIndex() {
		return firstDataRowIndex;
	}

	/**
	 * @param firstDataRowIndex 
	 * 		the row index in the complete data set of the 
	 * 		first row of provided data; cannot be negative
	 */
	public void setFirstDataRowIndex(int firstDataRowIndex) {
		if ( firstDataRowIndex < 0 )
			throw new IllegalArgumentException(
					"index of first row of provided data cannot be negative");
		this.firstDataRowIndex = firstDataRowIndex;
	}

	/**
	 * @return 
	 * 		the column names; may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnNames 
	 * 		the column names to set; 
	 * 		if null, the list is cleared
	 */
	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames.clear();
		if ( columnNames != null )
			this.columnNames.addAll(columnNames);
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
	 * 		the dataValues to set;
	 * 		if null, the list is cleared
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
		int result = numRowsTotal;
		result = result * prime + firstDataRowIndex;
		result = result * prime + expocode.hashCode();
		result = result * prime + columnNames.hashCode();
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
		if ( firstDataRowIndex != other.firstDataRowIndex )
			return false;
		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! columnNames.equals(other.columnNames) )
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
				", firstDataRowIndex=" + firstDataRowIndex + 
				", columnNames=" + columnNames + 
				", dataValues=" + dataValues + 
				" ]";
	}

}
