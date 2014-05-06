/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for a SOCAT WOCE flag.
 * 
 * @author Karl Smith
 */
public class SocatWoceFlag extends SocatQCFlag 
							implements Serializable, IsSerializable {

	private static final long serialVersionUID = -2064190756274447892L;

	Integer rowNumber;
	Double longitude;
	Double latitude;
	Date dataDate;
	DataColumnType dataType;
	String columnName;
	Double dataValue;

	/**
	 * Creates an empty flag.
	 */
	public SocatWoceFlag() {
		super();
		rowNumber = SocatCruiseData.INT_MISSING_VALUE;
		longitude = SocatCruiseData.FP_MISSING_VALUE;
		latitude = SocatCruiseData.FP_MISSING_VALUE;
		dataDate = SocatMetadata.DATE_MISSING_VALUE;
		dataType = DataColumnType.UNKNOWN;
		columnName = "";
		dataValue = SocatCruiseData.FP_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the data row number; 
	 * 		never null but may be {@link SocatCruiseData#INT_MISSING_VALUE}
	 */
	public Integer getRowNumber() {
		return rowNumber;
	}

	/**
	 * @param rowNumber 
	 * 		the data row number to set;
	 * 		if null, {@link SocatCruiseData#INT_MISSING_VALUE} is assigned
	 */
	public void setRowNumber(Integer rowNumber) {
		if ( rowNumber == null )
			this.rowNumber = SocatCruiseData.INT_MISSING_VALUE;
		else
			this.rowNumber = rowNumber;
	}

	/**
	 * @return 
	 * 		the longitude in the range [-180.0, 180.0)
	 * 		never null but may be {@link SocatCruiseData#FP_MISSING_VALUE}
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude 
	 * 		the longitude to set, which will be adjust the range [-180.0, 180.0);
	 * 		if null, {@link SocatCruiseData#FP_MISSING_VALUE} is assigned.
	 */
	public void setLongitude(Double longitude) {
		if ( longitude == null ) {
			this.longitude = SocatCruiseData.FP_MISSING_VALUE;
		}
		else {
			this.longitude = longitude;
			while ( this.longitude >= 180.0 )
				this.longitude -= 180.0;
			while ( this.longitude < -180.0 )
				this.longitude += 180.0;
		}
	}

	/**
	 * @return 
	 * 		the latitude;
	 * 		never null but may be {@link SocatCruiseData#FP_MISSING_VALUE}
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude 
	 * 		the latitude to set;
	 * 		if null, {@link SocatCruiseData#FP_MISSING_VALUE} is assigned.
	 */
	public void setLatitude(Double latitude) {
		if ( latitude == null )
			this.latitude = SocatCruiseData.FP_MISSING_VALUE;
		else
			this.latitude = latitude;
	}

	/**
	 * @return 
	 * 		the data date;
	 * 		never null but may be {@link SocatMetadata#DATE_MISSING_VALUE}
	 */
	public Date getDataDate() {
		return dataDate;
	}

	/**
	 * @param dataDate 
	 * 		the data date to set;
	 * 		if null, {@link SocatMetadata#DATE_MISSING_VALUE} is assigned.
	 */
	public void setDataDate(Date dataDate) {
		if ( dataDate == null )
			this.dataDate = SocatMetadata.DATE_MISSING_VALUE;
		else
			this.dataDate = dataDate;
	}

	/**
	 * @return 
	 * 		the data type; 
	 * 		never null but may be {@link DataColumnType#UNKNOWN}
	 */
	public DataColumnType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType 
	 * 		the data type to set;
	 * 		if null, {@link DataColumnType#UNKNOWN} is assigned.
	 */
	public void setDataType(DataColumnType dataType) {
		if ( dataType == null )
			this.dataType = DataColumnType.UNKNOWN;
		else
			this.dataType = dataType;
	}

	/**
	 * @return 
	 * 		the data column name;
	 * 		never null but may be empty
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName 
	 * 		the data column name to set;
	 * 		if null, an empty string is assigned.
	 */
	public void setColumnName(String columnName) {
		if ( columnName == null )
			this.columnName = "";
		else
			this.columnName = columnName;
	}

	/**
	 * @return 
	 * 		the data value;
	 * 		never null but may be {@link SocatCruiseData#FP_MISSING_VALUE}
	 */
	public Double getDataValue() {
		return dataValue;
	}

	/**
	 * @param dataValue 
	 * 		the data value to set;
	 * 		if null, {@link SocatCruiseData#FP_MISSING_VALUE} is assigned.
	 */
	public void setDataValue(Double dataValue) {
		if ( dataValue == null )
			this.dataValue = SocatCruiseData.FP_MISSING_VALUE;
		else
			this.dataValue = dataValue;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + rowNumber.hashCode();
		// Ignore floating point as they do not have to be exactly the same for equals
		result = result * prime + dataDate.hashCode();
		result = result * prime + dataType.hashCode();
		result = result * prime + columnName.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatWoceFlag) )
			return false;
		SocatWoceFlag other = (SocatWoceFlag) obj;

		if ( ! super.equals(other) )
			return false;
		if ( ! rowNumber.equals(other.rowNumber) )
			return false;
		if ( ! dataDate.equals(other.dataDate) )
			return false;
		if ( ! dataType.equals(other.dataType) )
			return false;
		if ( ! columnName.equals(other.columnName) )
			return false;

		if ( ! DashboardUtils.closeTo(dataValue, other.dataValue, SocatCruiseData.MAX_RELATIVE_ERROR, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(latitude, other.latitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;
		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		if ( ! DashboardUtils.closeTo(longitude, other.longitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			if ( ! DashboardUtils.closeTo(longitude + 360.0, other.longitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
				if ( ! DashboardUtils.closeTo(longitude, other.longitude + 360.0, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
					return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatWoceFlag" +
				"[\n    flag='" + flag.toString() + "'" +
				",\n    expocode=" + expocode + 
				",\n    socatVersion=" + socatVersion.toString() + 
				",\n    regionID='" + regionID.toString() + "'" + 
				",\n    rowNumber=" + rowNumber.toString() + 
				",\n    longitude=" + longitude.toString() + 
				",\n    latitude=" + latitude.toString() + 
				",\n    dataDate=" + dataDate.toString() + 
				",\n    dataType=" + dataType.toString() + 
				",\n    columnName=" + columnName + 
				",\n    dataValue=" + dataValue.toString() + 
				",\n    flagDate=" + flagDate.toString() + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
