/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the data given an uploaded cruise data file
 * 
 * @author Karl Smith
 */
public class DashboardCruiseData implements Serializable {

	private static final long serialVersionUID = -263336885454727722L;

	String expocode;
	ArrayList<String> preamble;
	String[] columnNames;
	ArrayList<String[]> dataValues;

	/**
	 * Creates with no cruise data
	 */
	public DashboardCruiseData() {
		expocode = "";
		preamble = new ArrayList<String>();
		columnNames = new String[0];
		dataValues = new ArrayList<String[]>();
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
	 * 		if null an empty string is assigned
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
	 * 		may be empty, but never null
	 * 		The actual list in the instance is returned.
	 */
	public ArrayList<String> getPreamble() {
		return preamble;
	}

	/**
	 * @param preamble 
	 * 		the list of metadata preamble string to set;
	 * 		if null the existing list is cleared.
	 */
	public void setPreamble(ArrayList<String> preamble) {
		if ( preamble == null )
			this.preamble.clear();
		else
			this.preamble = preamble;
	}

	/**
	 * @return 
	 * 		the column names array; may be empty but never null
	 * 		The actual list contained in the instance is returned.
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnNames 
	 * 		the column names array to set
	 */
	public void setColumnNames(String[] columnNames) {
		if ( columnNames == null )
			this.columnNames = new String[0];
		else
			this.columnNames = columnNames;
	}

	/**
	 * @return 
	 * 		the list of data arrays; may be empty but never null
	 * 		The actual list in the instance is returned.
	 */
	public ArrayList<String[]> getDataValues() {
		return dataValues;
	}

	/**
	 * @param dataValues 
	 * 		the list of data arrays to set;
	 * 		if null the existing list is cleared
	 */
	public void setDataValues(ArrayList<String[]> dataValues) {
		if ( dataValues == null )
			this.dataValues.clear();
		else
			this.dataValues = dataValues;
	}

}
