/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;

/**
 * Describes the type of a cruise data column
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnType implements Serializable, 
										Comparable<CruiseDataColumnType> {

	private static final long serialVersionUID = 6338855177727993770L;

	int userColumnNum;
	int stdColumnNum;
	String dataType;
	String unit;
	String userHeaderName;
	String stdHeaderName;
	String description;

	/**
	 * Creates a column type with no names, no type, no unit, and has 
	 * column numbers set to the unknown data standard column number.
	 */
	public CruiseDataColumnType() {
		userColumnNum = DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM;
		stdColumnNum = DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM;
		dataType = "";
		unit = "";
		userHeaderName = "";
		stdHeaderName = "";
		description = "";
	}

	/**
	 * @return 
	 * 		the column number of this data 
	 * 		in the user-provided data file
	 */
	public int getUserColumnNum() {
		return userColumnNum;
	}

	/**
	 * @param userColumnNum 
	 * 		set the column number of this data 
	 * 		in the user-provided data file to this value
	 */
	public void setUserColumnNum(int userColumnNum) {
		this.userColumnNum = userColumnNum;
	}

	/**
	 * @return 
	 * 		the standard column number of this data
	 */
	public int getStdColumnNum() {
		return stdColumnNum;
	}

	/**
	 * @param stdColumnNum 
	 * 		set the standard column number of this data 
	 * 		to this value
	 */
	public void setStdColumnNum(int stdColumnNum) {
		this.stdColumnNum = stdColumnNum;
	}

	/**
	 * @return 
	 * 		the type of this data; never null, 
	 * 		but may be empty
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType 
	 * 		set the type of this data to this value;
	 * 		if null, an empty string is assigned
	 */
	public void setDataType(String type) {
		if ( type == null )
			this.dataType = "";
		else
			this.dataType = type;
	}

	/**
	 * @return 
	 * 		the unit of this data; never null, 
	 * 		but may be empty
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit 
	 * 		set the unit of this data to this value;
	 * 		if null, an empty string is assigned
	 */
	public void setUnit(String unit) {
		if ( unit == null )
			this.unit = "";
		else
			this.unit = unit;
	}

	/**
	 * @return
	 * 		the user-provided name (data column header) 
	 * 		of this data; never null, but may be empty.
	 */
	public String getUserHeaderName() {
		return userHeaderName;
	}

	/**
	 * @param userHeaderName
	 * 		set the user-provided name (data column header) 
	 * 		of this data to this value; if null, an empty 
	 * 		string is assigned. 
	 */
	public void setUserHeaderName(String userHeaderName) {
		if ( userHeaderName == null )
			this.userHeaderName = "";
		else
			this.userHeaderName = userHeaderName;
	}

	/**
	 * @return 
	 * 		the standard name (data column header) 
	 * 		of this data; never null, but may be empty
	 */
	public String getStdHeaderName() {
		return stdHeaderName;
	}

	/**
	 * @param stdHeaderName 
	 * 		set the standard name (data column header) 
	 * 		of this data to this value; if null, an empty 
	 * 		string is assigned
	 */
	public void setStdHeaderName(String stdHeaderName) {
		if ( stdHeaderName == null )
			this.stdHeaderName = "";
		else
			this.stdHeaderName = stdHeaderName;
	}

	/**
	 * @return 
	 * 		the full description of this data; 
	 * 		never null, but may be empty
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description 
	 * 		set the full description of the data to this value; 
	 * 		if null, an empty string is assigned
	 */
	public void setDescription(String description) {
		if ( description == null )
			this.description = "";
		else
			this.description = description;
	}

	/**
	 * Gives a natural ascending ordering from lowest standard column 
	 * number to largest standard column number.  With the same standard 
	 * column number, the ordering is then alphabetical using the data 
	 * type, the standard name, the user-provided name, and the unit.
	 * Finally the column number in the user-provided file and the 
	 * data description is used.  
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
		value = this.stdHeaderName.compareTo(other.stdHeaderName);
		if ( value != 0 )
			return value;
		value = this.userHeaderName.compareTo(other.userHeaderName);
		if ( value != 0 )
			return value;
		value = this.unit.compareTo(other.unit);
		if ( value != 0 )
			return value;
		if ( this.userColumnNum < other.userColumnNum )
			return -1;
		if ( this.userColumnNum > other.userColumnNum )
			return 1;
		value = this.description.compareTo(other.description);
		if ( value != 0 )
			return value;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = stdColumnNum;
		result = result * prime + dataType.hashCode();
		result = result * prime + stdHeaderName.hashCode();
		result = result * prime + userHeaderName.hashCode();
		result = result * prime + unit.hashCode();
		result = result * prime + userColumnNum;
		result = result * prime + description.hashCode();
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
		if ( ! stdHeaderName.equals(other.stdHeaderName) )
			return false;
		if ( ! userHeaderName.equals(other.userHeaderName) )
			return false;
		if ( ! unit.equals(other.unit) )
			return false;
		if ( userColumnNum != other.userColumnNum )
			return false;
		if ( ! description.equals(other.description) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CruiseDataColumnType" +
				"[ stdColumnNum=" + stdColumnNum + 
				", dataType=" + dataType +
				", stdHeaderName=" + stdHeaderName + 
				", userHeaderName=" + userHeaderName +
				", unit=" + unit +
				", userColumnNum=" + userColumnNum +
				", description=" + description +
				" ]";
	}

}
