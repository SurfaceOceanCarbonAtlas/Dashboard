/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Overlaps are duplications of location and time values either within a dataset 
 * or between any two datasets.  Extensive overlaps are very likely to be erroneous 
 * duplication of data, although there is the rare possibility of two instruments 
 * on the same platform.
 * 
 * @author Karl Smith
 */
public class Overlap implements Serializable, IsSerializable {

	private static final long serialVersionUID = 3781721342737853060L;

	protected String[] expocodes;
	protected Integer[][] rowNums;
	protected Double[] lons;
	protected Double[] lats;
	protected Long[] times;

	/**
	 * Creates an crossover with no information (all null).
	 */
	public Overlap() {
		setExpocodes(null);
		setRowNums(null);
		setLons(null);
		setLats(null);
		setTimes(null);
	}

	/**
	 * @return
	 * 		the two expocodes of the crossover cruises; always 
	 * 		an array of two Strings, but each String may be null. 
	 * 		The actual array in this instance is returned. 
	 */
	public String[] getExpocodes() {
		return expocodes;
	}

	/**
	 * @param expocodes
	 * 		the two expocodes of the crossover cruises to set. 
	 * 		If null, an array of two nulls is assigned; otherwise 
	 * 		an array of two Strings must be given. 
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
	 * 		the dataset row numbers (starts with one) of the overlap; 
	 * 		always an array of two Integer arrays, but the Integer arrays 
	 * 		may be empty.  The actual array in this instance is returned. 
	 */
	public Integer[][] getRowNums() {
		return rowNums;
	}

	/**
	 * @param rowNums
	 * 		the dataset row numbers (starts with one) of the overlap. 
	 * 		If null, an array of two empty arrays is assigned; otherwise an 
	 * 		array of two Integer arrays of the same length must be given. 
	 */
	public void setRowNums(Integer[][] rowNums) {
		if ( rowNums == null ) {
			this.rowNums = new Integer[][] { new Integer[0], new Integer[0] };
		}
		else {
			if ( rowNums.length != 2 )
				throw new IllegalArgumentException("rowNums array not length 2");
			int numRows = rowNums[0].length;
			if ( rowNums[1].length != numRows )
				throw new IllegalArgumentException("rowNums arrays not same length");
			this.rowNums[0] = rowNums[0].clone();
			this.rowNums[1] = rowNums[1].clone();
		}
	}

	/**
	 * @return
	 * 		the longitudes of the overlap; never null but may be empty. 
	 * 		The actual array in this instance is returned.
	 */
	public Double[] getLons() {
		return lons;
	}

	/**
	 * @param lons
	 * 		the longitudes of the overlap to set. 
	 * 		If null, an empty array is assigned.
	 */
	public void setLons(Double[] lons) {
		if ( lons == null ) {
			this.lons = new Double[0];
		}
		else {
			this.lons = lons.clone();
		}
	}

	/**
	 * @return
	 * 		the latitudes of the overlap; never null but may be empty. 
	 * 		The actual array in this instance is returned.
	 */
	public Double[] getLats() {
		return lats;
	}

	/**
	 * @param lats
	 * 		the latitudes of the overlap to set.
	 * 		If null, an empty array is assigned.
	 */
	public void setLats(Double[] lats) {
		if ( lats == null ) {
			this.lats = new Double[0];
		}
		else {
			this.lats = lats.clone();
		}
	}

	/**
	 * @return
	 * 		the times, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of the overlap; never null but may be empty.
	 * 		The actual array in this instance is returned.
	 */
	public Long[] getTimes() {
		return times;
	}

	/**
	 * @param times
	 * 		the times, in seconds with Jan 1, 1970 00:00:00, 
	 * 		of the overlap to set.  If null, an empty array assigned.
	 */
	public void setTimes(Long[] times) {
		if ( times == null ) {
			this.times = new Long[0];
		}
		else {
			this.times = times.clone();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Arrays.deepHashCode(expocodes);
		result = prime * result + Arrays.deepHashCode(rowNums);
		result = prime * result + Arrays.deepHashCode(times);
		// Do not include floating point values, as they do not have to be exact to match
		// but do include the number of floating point values as those do have to match
		result = prime * result + Integer.hashCode(lats.length);
		result = prime * result + Integer.hashCode(lons.length);

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( ! (obj instanceof Overlap) ) {
			return false;
		}

		Overlap other = (Overlap) obj;

		if ( ! Arrays.deepEquals(expocodes, other.expocodes) )
			return false;
		if ( ! Arrays.deepEquals(rowNums, other.rowNums) )
			return false;
		if ( ! Arrays.deepEquals(times, other.times) )
			return false;

		if ( lats.length != other.lats.length )
			return false;
		if ( lons.length != other.lons.length )
			return false;

		for (int k = 0; k < lats.length; k++) {
			if ( ! DashboardUtils.closeTo(lats[k], other.lats[k], 
					0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
				return false;
			}
		}

		for (int k = 0; k < lons.length; k++) {
			if ( ! DashboardUtils.longitudeCloseTo(lons[k], other.lons[k], 
					0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
				return false;
			}			
		}

		return true;
	}

	@Override
	public String toString() {
		return "Overlap" + 
				"[ expocodes=" + Arrays.toString(expocodes) + 
				", rowNums=" + Arrays.toString(rowNums) + 
				", lons=" + Arrays.toString(lons) + 
				", lats=" + Arrays.toString(lats) + 
				", times=" +Arrays.toString(times) + 
				"]";
	}

}
