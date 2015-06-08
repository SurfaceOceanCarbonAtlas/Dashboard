/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The location of a data point with a data value at that location.
 * Used for indicating locations for WOCE flag events which describes 
 * the data set and data column for this location and value.
 * 
 * @author Karl Smith
 */
public class DataLocation implements Serializable, IsSerializable {

	private static final long serialVersionUID = -6320586997492783178L;

	public static final Character GLOBAL_REGION_ID = 'G';
	public static final Character NORTH_PACIFIC_REGION_ID = 'N';
	public static final Character TROPICAL_PACIFIC_REGION_ID = 'T';
	public static final Character NORTH_ATLANTIC_REGION_ID = 'A';
	public static final Character TROPICAL_ATLANTIC_REGION_ID = 'Z';
	public static final Character INDIAN_REGION_ID = 'I';
	public static final Character COASTAL_REGION_ID = 'C';
	public static final Character SOUTHERN_OCEANS_REGION_ID = 'O';
	public static final Character ARCTIC_REGION_ID = 'R';

	public static final HashMap<Character,String> REGION_NAMES;
	static {
		REGION_NAMES = new HashMap<Character,String>();
		REGION_NAMES.put(GLOBAL_REGION_ID, "Global");
		REGION_NAMES.put(NORTH_PACIFIC_REGION_ID, "North Pacific");
		REGION_NAMES.put(TROPICAL_PACIFIC_REGION_ID, "Tropical Pacific");
		REGION_NAMES.put(NORTH_ATLANTIC_REGION_ID, "North Atlantic");
		REGION_NAMES.put(TROPICAL_ATLANTIC_REGION_ID, "Tropical Atlantic");
		REGION_NAMES.put(INDIAN_REGION_ID, "Indian");
		REGION_NAMES.put(COASTAL_REGION_ID, "Coastal");
		REGION_NAMES.put(SOUTHERN_OCEANS_REGION_ID, "Southern Oceans");
		REGION_NAMES.put(ARCTIC_REGION_ID, "Artic");
	}

	Character regionID;
	Integer rowNumber;
	Date dataDate;
	Double longitude;
	Double latitude;
	Double dataValue;

	/**
	 * Creates an empty location with a global region ID
	 */
	public DataLocation() {
		regionID = GLOBAL_REGION_ID;
		rowNumber = SocatCruiseData.INT_MISSING_VALUE;
		dataDate = SocatMetadata.DATE_MISSING_VALUE;
		longitude = SocatCruiseData.FP_MISSING_VALUE;
		latitude = SocatCruiseData.FP_MISSING_VALUE;
		dataValue = SocatCruiseData.FP_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the region ID for this WOCE flag; never null
	 */
	public Character getRegionID() {
		return regionID;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set for this WOCE flag; 
	 * 		if null, {@link #GLOBAL_REGION_ID} is assigned
	 */
	public void setRegionID(Character regionID) {
		if ( regionID == null )
			this.regionID = GLOBAL_REGION_ID;
		else
			this.regionID = regionID;
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
				this.longitude -= 360.0;
			while ( this.longitude < -180.0 )
				this.longitude += 360.0;
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
		int result = regionID.hashCode();
		result = result * prime + rowNumber.hashCode();
		result = result * prime + dataDate.hashCode();
		// Ignore floating point values as they do not have to be exactly the same for equals
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DataLocation) )
			return false;
		DataLocation other = (DataLocation) obj;

		if ( ! regionID.equals(other.regionID) )
			return false;
		if ( ! rowNumber.equals(other.rowNumber) )
			return false;
		if ( ! dataDate.equals(other.dataDate) )
			return false;

		if ( ! DashboardUtils.closeTo(dataValue, other.dataValue, SocatCruiseData.MAX_RELATIVE_ERROR, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(latitude, other.latitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.longitudeCloseTo(longitude, other.longitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DataLocation" +
				"[ regionID='" + regionID.toString() + "'" + 
				", rowNumber=" + rowNumber.toString() + 
				", dataTime=" + Long.toString(Math.round((dataDate.getTime()/1000.0))) + 
				", longitude=" + longitude.toString() + 
				", latitude=" + latitude.toString() + 
				", dataValue=" + dataValue.toString() + 
				"]";
	}

}
