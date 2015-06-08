/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Provides information about the crossover of two cruises.
 * 
 * @author Karl Smith
 */
public class SocatCrossover implements Serializable, IsSerializable {

	private static final long serialVersionUID = -1480644418805338101L;

	/** Max "distance", in kilometers, still considered a crossover */
	public static final double MAX_CROSSOVER_DIST = 80.0;
	/** "Distance" contribution, in kilometers, for every 24h time difference */
	public static final double SEAWATER_SPEED = 30.0;
	/** Maximum difference in FCO2_rec for a high-quality crossover */
	public static final double MAX_FCO2_DIFF = 5.0;
	/** Maximum difference in SST for a high-quality crossover */
	public static final double MAX_TEMP_DIFF = 0.3;

	/** Authalic radius, in kilometers, of Earth */
	public static final double EARTH_AUTHALIC_RADIUS = 6371.007;
	/** Max allowable difference in time, in seconds, between two crossover data points */
	public static final double MAX_TIME_DIFF = Math.ceil(24.0 * 60.0 * 60.0 * MAX_CROSSOVER_DIST / SEAWATER_SPEED);
	/** Max allowable difference in latitude, in degrees, between two crossover data points */
	public static final double MAX_LAT_DIFF = (MAX_CROSSOVER_DIST / EARTH_AUTHALIC_RADIUS) * (180.0 / Math.PI);

	String[] expocodes;
	Double minDistance;
	Integer[] rowNumsAtMin;
	Double[] lonsAtMin;
	Double[] latsAtMin;
	Long[] timesAtMin;
	Long[] cruiseMinTimes;
	Long[] cruiseMaxTimes;

	/**
	 * Creates an crossover with no information (all null).
	 */
	public SocatCrossover() {
		setExpocodes(null);
		setMinDistance(null);
		setRowNumsAtMin(null);
		setLonsAtMin(null);
		setLatsAtMin(null);
		setTimesAtMin(null);
		setCruiseMinTimes(null);
		setCruiseMaxTimes(null);
	}

	/**
	 * @return
	 * 		the two expocodes of the crossover cruises;
	 * 		always an array of two Strings, but each String may be null.
	 * 		The actual array in this instance is returned.
	 */
	public String[] getExpocodes() {
		return expocodes;
	}

	/**
	 * @param expocodes
	 * 		the two expocodes of the crossover cruises to set.
	 * 		If null, an array of two nulls is assigned; 
	 * 		otherwise an array of two Strings must be given.
	 * 		The values in the array, but not the array itself, are used.
	 */
	public void setExpocodes(String[] expocodes) {
		if ( expocodes == null ) {
			this.expocodes = new String[] { null, null };
		}
		else {
			if ( expocodes.length != 2 )
				throw new IllegalArgumentException("expocodes array not length 2");
			this.expocodes[0] = expocodes[0];
			this.expocodes[1] = expocodes[1];
		}
	}

	/**
	 * @return 
	 * 		the "distance" of the crossover in kilometers; may be null.
	 */
	public Double getMinDistance() {
		return minDistance;
	}

	/**
	 * @param minDistance
	 * 		the "distance" of the crossover in kilometers to set.
	 */
	public void setMinDistance(Double minDistance) {
		this.minDistance = minDistance;
	}

	/**
	 * @return
	 * 		the data row numbers (starts with one) of the two cruises at the crossover;
	 * 		always an array of two Integers, but each Integer may be null.
	 * 		The actual array in this instance is returned.
	 */
	public Integer[] getRowNumsAtMin() {
		return rowNumsAtMin;
	}

	/**
	 * @param rowNumsAtMin
	 * 		the data row numbers (starts with one) of the two cruises at the crossover to set.
	 * 		If null, an array of two nulls is assigned; 
	 * 		otherwise an array of two Integers must be given.
	 * 		The values in the array, but not the array itself, are used.
	 */
	public void setRowNumsAtMin(Integer[] rowNumsAtMin) {
		if ( rowNumsAtMin == null ) {
			this.rowNumsAtMin = new Integer[] { null, null };
		}
		else {
			if ( rowNumsAtMin.length != 2 )
				throw new IllegalArgumentException("rowNumsAtMin array not length 2");
			this.rowNumsAtMin[0] = rowNumsAtMin[0];
			this.rowNumsAtMin[1] = rowNumsAtMin[1];
		}
	}

	/**
	 * @return
	 * 		the longitudes of the two cruises at the crossover;
	 * 		always an array of two Doubles, but each Double may be null.
	 * 		The actual array in this instance is returned.
	 */
	public Double[] getLonsAtMin() {
		return lonsAtMin;
	}

	/**
	 * @param lonsAtMin
	 * 		the longitudes of the two cruises at the crossover to set.
	 * 		If null, an array of two nulls is assigned; 
	 * 		otherwise an array of two Doubles must be given.
	 * 		The values in the array, but not the array itself, are used.
	 */
	public void setLonsAtMin(Double[] lonsAtMin) {
		if ( lonsAtMin == null ) {
			this.lonsAtMin = new Double[] { null, null };
		}
		else {
			if ( lonsAtMin.length != 2 )
				throw new IllegalArgumentException("lonsAtMin array not length 2");
			this.lonsAtMin[0] = lonsAtMin[0];
			this.lonsAtMin[1] = lonsAtMin[1];
		}
	}

	/**
	 * @return
	 * 		the latitudes of the two cruises at the crossover;
	 * 		always an array of two Doubles, but each Double may be null.
	 * 		The actual array in this instance is returned.
	 */
	public Double[] getLatsAtMin() {
		return latsAtMin;
	}

	/**
	 * @param latsAtMin
	 * 		the latitudes of the two cruises at the crossover to set.
	 * 		If null, an array of two nulls is assigned; 
	 * 		otherwise an array of two Doubles must be given.
	 * 		The values in the array, but not the array itself, are used.
	 */
	public void setLatsAtMin(Double[] latsAtMin) {
		if ( latsAtMin == null ) {
			this.latsAtMin = new Double[] { null, null };
		}
		else {
			if ( latsAtMin.length != 2 )
				throw new IllegalArgumentException("latsAtMin array not length 2");
			this.latsAtMin[0] = latsAtMin[0];
			this.latsAtMin[1] = latsAtMin[1];
		}
	}

	/**
	 * @return
	 * 		the times, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of the two cruises at the crossover;
	 * 		always an array of two Dates, but each Date may be null.
	 * 		The actual array in this instance is returned.
	 */
	public Long[] getTimesAtMin() {
		return timesAtMin;
	}

	/**
	 * @param timesAtMin
	 * 		the times, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of the two cruises at the crossover to set.
	 * 		If null, an array of two nulls is assigned; 
	 * 		otherwise an array of two Dates must be given.
	 * 		The values in the array, but not the array itself, are used.
	 */
	public void setTimesAtMin(Long[] timesAtMin) {
		if ( timesAtMin == null ) {
			this.timesAtMin = new Long[] { null, null };
		}
		else {
			if ( timesAtMin.length != 2 )
				throw new IllegalArgumentException("timesAtMin array not length 2");
			this.timesAtMin[0] = timesAtMin[0];
			this.timesAtMin[1] = timesAtMin[1];
		}
	}

	/**
	 * @return
	 * 		the minimum time, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of all data for each cruise;
	 * 		always an array of two Dates, but each Date may be null.
	 * 		The actual array in this instance is returned.
	 */
	public Long[] getCruiseMinTimes() {
		return cruiseMinTimes;
	}

	/**
	 * @param cruiseMinTimes 
	 * 		the minimum time, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of all data for each cruise to set.
	 * 		If null, an array of two nulls is assigned; 
	 * 		otherwise an array of two Dates must be given.
	 * 		The values in the array, but not the array itself, are used.
	 */
	public void setCruiseMinTimes(Long[] cruiseMinTimes) {
		if ( cruiseMinTimes == null ) {
			this.cruiseMinTimes = new Long[] { null, null };
		}
		else {
			if ( cruiseMinTimes.length != 2 )
				throw new IllegalArgumentException("cruiseMinTimes array not length 2");
			this.cruiseMinTimes[0] = cruiseMinTimes[0];
			this.cruiseMinTimes[1] = cruiseMinTimes[1];
		}
	}

	/**
	 * @return
	 * 		maximum time, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of all data for each cruise;
	 * 		always an array of two Dates, but each Date may be null.
	 * 		The actual array in this instance is returned.
	 */
	public Long[] getCruiseMaxTimes() {
		return cruiseMaxTimes;
	}

	/**
	 * @param cruiseMaxTimes
	 * 		the maximum time, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of all data for each cruise to set.
	 * 		If null, an array of two nulls is assigned; 
	 * 		otherwise an array of two Dates must be given.
	 * 		The values in the array, but not the array itself, are used.
	 */
	public void setCruiseMaxTimes(Long[] cruiseMaxTimes) {
		if ( cruiseMaxTimes == null ) {
			this.cruiseMaxTimes = new Long[] { null, null };
		}
		else {
			if ( cruiseMaxTimes.length != 2 )
				throw new IllegalArgumentException("cruiseMaxTimes array not length 2");
			this.cruiseMaxTimes[0] = cruiseMaxTimes[0];
			this.cruiseMaxTimes[1] = cruiseMaxTimes[1];
		}
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Arrays.hashCode(expocodes);
		result = prime * result + Arrays.hashCode(rowNumsAtMin);
		result = prime * result + Arrays.hashCode(timesAtMin);
		result = prime * result + Arrays.hashCode(cruiseMinTimes);
		result = prime * result + Arrays.hashCode(cruiseMaxTimes);
		// Do not include floating point values, as they do not have to be exact to match

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		final double rtoler = 1.0E-8;
		final double atoler = 1.0E-4;

		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( ! (obj instanceof SocatCrossover) ) {
			return false;
		}

		SocatCrossover other = (SocatCrossover) obj;

		if ( ! Arrays.equals(expocodes, other.expocodes) )
			return false;
		if ( ! Arrays.equals(rowNumsAtMin, other.rowNumsAtMin) )
			return false;
		if ( ! Arrays.equals(cruiseMaxTimes, other.cruiseMaxTimes) )
			return false;
		if ( ! Arrays.equals(cruiseMinTimes, other.cruiseMinTimes) )
			return false;
		if ( ! Arrays.equals(timesAtMin, other.timesAtMin) )
			return false;

		if ( minDistance == null ) {
			if ( other.minDistance != null )
				return false;
		} 
		else if ( other.minDistance == null ) {
			return false;
		}
		else if ( ! DashboardUtils.closeTo(minDistance, other.minDistance, rtoler, atoler) ) {
			return false;
		}

		for (int k = 0; k < 2; k++) {
			if ( latsAtMin[k] == null ) {
				if ( other.latsAtMin[k] != null )
					return false;
			} 
			else if ( other.latsAtMin[k] == null ) {
				return false;
			}
			else if ( ! DashboardUtils.closeTo(latsAtMin[k], other.latsAtMin[k], rtoler, atoler) ) {
				return false;
			}
		}

		for (int k = 0; k < 2; k++) {
			if ( lonsAtMin[k] == null ) {
				if ( other.lonsAtMin[k] != null )
					return false;
			} 
			else if ( other.lonsAtMin[k] == null ) {
				return false;
			}
			else if ( ! DashboardUtils.longitudeCloseTo(lonsAtMin[k], other.lonsAtMin[k], rtoler, atoler) ) {
				return false;
			}			
		}

		return true;
	}

	@Override
	public String toString() {
		return "SocatCrossover" + 
				"[ expocodes=" + Arrays.toString(expocodes) + 
				", minDistance=" + minDistance + 
				", rowNumsAtMin=" + Arrays.toString(rowNumsAtMin) + 
				", lonsAtMin=" + Arrays.toString(lonsAtMin) + 
				", latsAtMin=" + Arrays.toString(latsAtMin) + 
				", timesAtMin=" +Arrays.toString(timesAtMin) + 
				", cruiseMinTimes=" + Arrays.toString(cruiseMinTimes) + 
				", cruiseMaxTimes=" + Arrays.toString(cruiseMaxTimes) + 
				"]";
	}

}
