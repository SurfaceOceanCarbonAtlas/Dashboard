/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;

/**
 * Describes the data dataType of a data column
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnType implements Serializable, Comparable<CruiseDataColumnType> {

	private static final long serialVersionUID = -2477993828796593230L;

	int stdColumnNum;
	String fullName;
	String labelName;
	String dataType;

	/**
	 * Creates an spec with no names, no type, and has the 
	 * standard column number for an unknown column (zero).
	 */
	public CruiseDataColumnType() {
		stdColumnNum = 0;
		fullName = "";
		labelName = "";
		dataType = "";
	}

	/**
	 * Creates an empty CruiseDataColumnType and then 
	 * assigns each of the given value using the "set" methods.
	 */
	public CruiseDataColumnType(int stdColNum, String dataType, 
							String labelName, String fullName) {
		setStdColumnNum(stdColNum);
		setDataType(dataType);
		setLabelName(labelName);
		setFullName(fullName);
	}

	/**
	 * @return 
	 * 		the standard column number
	 */
	public int getStdColumnNum() {
		return stdColumnNum;
	}

	/**
	 * @param stdColumnNum 
	 * 		the standard column number to set
	 */
	public void setStdColumnNum(int stdColumnNum) {
		this.stdColumnNum = stdColumnNum;
	}

	/**
	 * @return 
	 * 		the full standard name; never null
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName 
	 * 		the full standard name to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setFullName(String fullName) {
		if ( fullName == null )
			this.fullName = "";
		else
			this.fullName = fullName;
	}

	/**
	 * @return 
	 * 		the standard label name; never null
	 */
	public String getLabelName() {
		return labelName;
	}

	/**
	 * @param labelName 
	 * 		the standard label name to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setLabelName(String labelName) {
		if ( labelName == null )
			this.labelName = "";
		else
			this.labelName = labelName;
	}

	/**
	 * @return 
	 * 		the standard name of the data type; never null
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType 
	 * 		the standard name of the data type to set;
	 * 		if null, an empty string is assigned
	 */
	public void setDataType(String type) {
		if ( type == null )
			this.dataType = "";
		else
			this.dataType = type;
	}

	/**
	 * Gives a natural ascending ordering from lowest standard column 
	 * number to largest standard column number.  With the same standard 
	 * column number, the ordering is alphabetically starting with the 
	 * data type, then the label name, and the finally full name.
	 */
	@Override
	public int compareTo(CruiseDataColumnType other) {
		if ( this.stdColumnNum < other.stdColumnNum )
			return -1;
		if ( this.stdColumnNum > other.stdColumnNum )
			return 1;
		int value = this.dataType.compareTo(other.dataType);
		if ( value != 0 )
			return value;
		value = this.labelName.compareTo(other.labelName);
		if ( value != 0 )
			return value;
		value = this.fullName.compareTo(other.fullName);
		if ( value != 0 )
			return value;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = stdColumnNum;
		result = result * prime + dataType.hashCode();
		result = result * prime + labelName.hashCode();
		result = result * prime + fullName.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof CruiseDataColumnType) )
			return false;
		CruiseDataColumnType other = (CruiseDataColumnType) obj;

		if ( stdColumnNum != other.stdColumnNum )
			return false;
		if ( ! dataType.equals(other.dataType) )
			return false;
		if ( ! labelName.equals(other.labelName) )
			return false;
		if ( ! fullName.equals(other.fullName) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CruiseDataColumnType" +
				"[ stdColumnNum=" + stdColumnNum + 
				", dataType=" + dataType +
				", labelName=" + labelName + 
				", fullName=" + fullName + 
				" ]";
	}

}
