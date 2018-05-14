/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * High-quality crossovers are desirable coincidental near-overlaps of location,
 * time, and some other properties found in datasets from different platforms
 * (different NODC codes).
 *
 * @author Karl Smith
 */
public class Crossover implements Serializable, IsSerializable {

    private static final long serialVersionUID = -1068460517910734247L;

    protected String[] datasetIds;
    protected Double minDistance;
    protected Integer[] rowNumsAtMin;
    protected Double[] lonsAtMin;
    protected Double[] latsAtMin;
    protected Long[] timesAtMin;
    protected Long[] datasetMinTimes;
    protected Long[] datasetMaxTimes;

    /**
     * Creates an crossover with no information (all null).
     */
    public Crossover() {
        setDatasetIds(null);
        setMinDistance(null);
        setRowNumsAtMin(null);
        setLonsAtMin(null);
        setLatsAtMin(null);
        setTimesAtMin(null);
        setDatasetMinTimes(null);
        setDatasetMaxTimes(null);
    }

    /**
     * @return the two IDs of the crossover datasets;
     * always an array of two Strings, but each String may be null.
     * The actual array in this instance is returned.
     */
    public String[] getDatasetIds() {
        return datasetIds;
    }

    /**
     * @param datasetIds
     *         the two IDs of the crossover datasets to set.
     *         If null, an array of two nulls is assigned;
     *         otherwise an array of two Strings must be given.
     *         The values in the array, but not the array itself, are used.
     */
    public void setDatasetIds(String[] datasetIds) {
        if ( datasetIds == null ) {
            this.datasetIds = new String[] { null, null };
        }
        else {
            if ( datasetIds.length != 2 )
                throw new IllegalArgumentException("datasetIds array not length 2");
            this.datasetIds[0] = datasetIds[0];
            this.datasetIds[1] = datasetIds[1];
        }
    }

    /**
     * @return the "distance" of the crossover in kilometers; may be null.
     */
    public Double getMinDistance() {
        return minDistance;
    }

    /**
     * @param minDistance
     *         the "distance" of the crossover in kilometers to set.
     */
    public void setMinDistance(Double minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * @return the data row numbers (starts with one) of the two datasets at the crossover;
     * always an array of two Integers, but each Integer may be null.
     * The actual array in this instance is returned.
     */
    public Integer[] getRowNumsAtMin() {
        return rowNumsAtMin;
    }

    /**
     * @param rowNumsAtMin
     *         the data row numbers (starts with one) of the two datasets at the crossover to set.
     *         If null, an array of two nulls is assigned;
     *         otherwise an array of two Integers must be given.
     *         The values in the array, but not the array itself, are used.
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
     * @return the longitudes of the two datasets at the crossover;
     * always an array of two Doubles, but each Double may be null.
     * The actual array in this instance is returned.
     */
    public Double[] getLonsAtMin() {
        return lonsAtMin;
    }

    /**
     * @param lonsAtMin
     *         the longitudes of the two datasets at the crossover to set.
     *         If null, an array of two nulls is assigned;
     *         otherwise an array of two Doubles must be given.
     *         The values in the array, but not the array itself, are used.
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
     * @return the latitudes of the two datasets at the crossover;
     * always an array of two Doubles, but each Double may be null.
     * The actual array in this instance is returned.
     */
    public Double[] getLatsAtMin() {
        return latsAtMin;
    }

    /**
     * @param latsAtMin
     *         the latitudes of the two datasets at the crossover to set.
     *         If null, an array of two nulls is assigned;
     *         otherwise an array of two Doubles must be given.
     *         The values in the array, but not the array itself, are used.
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
     * @return the times, in seconds with Jan 1, 1970 00:00:00,
     * of the two datasets at the crossover;
     * always an array of two Dates, but each Date may be null.
     * The actual array in this instance is returned.
     */
    public Long[] getTimesAtMin() {
        return timesAtMin;
    }

    /**
     * @param timesAtMin
     *         the times, in seconds with Jan 1, 1970 00:00:00,
     *         of the two datasets at the crossover to set.
     *         If null, an array of two nulls is assigned;
     *         otherwise an array of two Dates must be given.
     *         The values in the array, but not the array itself, are used.
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
     * @return the minimum time, in seconds with Jan 1, 1970 00:00:00,
     * of all data for each dataset;
     * always an array of two Dates, but each Date may be null.
     * The actual array in this instance is returned.
     */
    public Long[] getDatasetMinTimes() {
        return datasetMinTimes;
    }

    /**
     * @param datasetMinTimes
     *         the minimum time, in seconds with Jan 1, 1970 00:00:00,
     *         of all data for each dataset to set.
     *         If null, an array of two nulls is assigned;
     *         otherwise an array of two Dates must be given.
     *         The values in the array, but not the array itself, are used.
     */
    public void setDatasetMinTimes(Long[] datasetMinTimes) {
        if ( datasetMinTimes == null ) {
            this.datasetMinTimes = new Long[] { null, null };
        }
        else {
            if ( datasetMinTimes.length != 2 )
                throw new IllegalArgumentException("datasetMinTimes array not length 2");
            this.datasetMinTimes[0] = datasetMinTimes[0];
            this.datasetMinTimes[1] = datasetMinTimes[1];
        }
    }

    /**
     * @return maximum time, in seconds with Jan 1, 1970 00:00:00,
     * of all data for each dataset;
     * always an array of two Dates, but each Date may be null.
     * The actual array in this instance is returned.
     */
    public Long[] getDatasetMaxTimes() {
        return datasetMaxTimes;
    }

    /**
     * @param datasetMaxTimes
     *         the maximum time, in seconds with Jan 1, 1970 00:00:00,
     *         of all data for each dataset to set.
     *         If null, an array of two nulls is assigned;
     *         otherwise an array of two Dates must be given.
     *         The values in the array, but not the array itself, are used.
     */
    public void setDatasetMaxTimes(Long[] datasetMaxTimes) {
        if ( datasetMaxTimes == null ) {
            this.datasetMaxTimes = new Long[] { null, null };
        }
        else {
            if ( datasetMaxTimes.length != 2 )
                throw new IllegalArgumentException("datasetMaxTimes array not length 2");
            this.datasetMaxTimes[0] = datasetMaxTimes[0];
            this.datasetMaxTimes[1] = datasetMaxTimes[1];
        }
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = Arrays.hashCode(datasetIds);
        result = prime * result + Arrays.hashCode(rowNumsAtMin);
        result = prime * result + Arrays.hashCode(timesAtMin);
        result = prime * result + Arrays.hashCode(datasetMinTimes);
        result = prime * result + Arrays.hashCode(datasetMaxTimes);
        // Do not include floating point values, as they do not have to be exact to match

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
        if ( !( obj instanceof Crossover ) ) {
            return false;
        }

        Crossover other = (Crossover) obj;

        if ( !Arrays.equals(datasetIds, other.datasetIds) )
            return false;
        if ( !Arrays.equals(rowNumsAtMin, other.rowNumsAtMin) )
            return false;
        if ( !Arrays.equals(datasetMaxTimes, other.datasetMaxTimes) )
            return false;
        if ( !Arrays.equals(datasetMinTimes, other.datasetMinTimes) )
            return false;
        if ( !Arrays.equals(timesAtMin, other.timesAtMin) )
            return false;

        if ( minDistance == null ) {
            if ( other.minDistance != null )
                return false;
        }
        else if ( other.minDistance == null ) {
            return false;
        }
        else if ( !DashboardUtils.closeTo(minDistance, other.minDistance,
                                          0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
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
            else if ( !DashboardUtils.closeTo(latsAtMin[k], other.latsAtMin[k],
                                              0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
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
            else if ( !DashboardUtils.longitudeCloseTo(lonsAtMin[k], other.lonsAtMin[k],
                                                       0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "Crossover" +
                "[ datasetIds=" + Arrays.toString(datasetIds) +
                ", minDistance=" + minDistance +
                ", rowNumsAtMin=" + Arrays.toString(rowNumsAtMin) +
                ", lonsAtMin=" + Arrays.toString(lonsAtMin) +
                ", latsAtMin=" + Arrays.toString(latsAtMin) +
                ", timesAtMin=" + Arrays.toString(timesAtMin) +
                ", datasetMinTimes=" + Arrays.toString(datasetMinTimes) +
                ", datasetMaxTimes=" + Arrays.toString(datasetMaxTimes) +
                "]";
    }

}
