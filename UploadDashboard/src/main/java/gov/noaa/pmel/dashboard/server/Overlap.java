/**
 *
 */
package gov.noaa.pmel.dashboard.server;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Overlaps are duplications of location and time values either within a dataset or between any two datasets.  Extensive
 * overlaps are very likely to be erroneous duplication of data, although there is the rare possibility of two
 * instruments on the same platform.
 *
 * @author Karl Smith
 */
public class Overlap implements Serializable, IsSerializable, Comparable<Overlap> {

    private static final long serialVersionUID = 7268526718169557931L;

    protected String[] expocodes;
    protected ArrayList<Integer>[] rowNums;
    protected ArrayList<Double> lons;
    protected ArrayList<Double> lats;
    protected ArrayList<Double> times;

    /**
     * Creates an overlap with no information (all empty).
     */
    public Overlap() {
        setExpocodes(null);
        setRowNums(null);
        setLons(null);
        setLats(null);
        setTimes(null);
    }

    /**
     * Creates an empty overlap for the two expocodes.
     *
     * @param firstExpo
     *         expocode of the first dataset
     * @param secondExpo
     *         expocode of the second dataset
     */
    public Overlap(String firstExpo, String secondExpo) {
        this();
        if ( firstExpo != null )
            expocodes[0] = firstExpo;
        else
            expocodes[0] = "";
        if ( secondExpo != null )
            expocodes[1] = secondExpo;
        else
            expocodes[1] = "";
    }

    /**
     * @return the two expocodes of the overlapping datasets; always an array of two Strings, but each String may be
     * empty. The actual array in this instance is returned.
     */
    public String[] getExpocodes() {
        return expocodes;
    }

    /**
     * @param expocodes
     *         the two expocodes of the overlapping datasets to set. If null, an array of two empty Strings is assigned;
     *         otherwise an array of two Strings must be given. If null is given as a String, an empty String is
     *         assigned.
     */
    public void setExpocodes(String[] expocodes) {
        if ( expocodes == null ) {
            this.expocodes = new String[] { "", "" };
        }
        else {
            if ( expocodes.length != 2 )
                throw new IllegalArgumentException("expocodes array not length 2");
            if ( expocodes[0] != null )
                this.expocodes[0] = expocodes[0];
            else
                this.expocodes[0] = "";
            if ( expocodes[1] != null )
                this.expocodes[1] = expocodes[1];
            else
                this.expocodes[1] = "";
        }
    }

    /**
     * @return the dataset row numbers (starts with one) of the overlap; always an array of two ArrayLists, but the
     * ArrayLists may be empty.  The actual array in this instance is returned.
     */
    public ArrayList<Integer>[] getRowNums() {
        return rowNums;
    }

    /**
     * @param rowNums
     *         the dataset row numbers (starts with one) of the overlap. If null, an array of two empty ArrayLists is
     *         assigned; otherwise an array of two Integer ArrayLists of the same length must be given.
     */
    @SuppressWarnings("unchecked")
    public void setRowNums(ArrayList<Integer>[] rowNums) {
        if ( rowNums == null ) {
            this.rowNums = new ArrayList[] { new ArrayList<Integer>(), new ArrayList<Integer>() };
        }
        else {
            if ( rowNums.length != 2 )
                throw new IllegalArgumentException("rowNums array not length 2");
            if ( rowNums[0].size() != rowNums[1].size() )
                throw new IllegalArgumentException("rowNums arrays not same length");
            this.rowNums[0] = new ArrayList<Integer>(rowNums[0]);
            this.rowNums[1] = new ArrayList<Integer>(rowNums[1]);
        }
    }

    /**
     * @return the longitudes of the overlap; never null but may be empty. The actual ArrayList in this instance is
     * returned.
     */
    public ArrayList<Double> getLons() {
        return lons;
    }

    /**
     * @param lons
     *         the longitudes of the overlap to set. If null, an empty ArrayList is assigned.
     */
    public void setLons(ArrayList<Double> lons) {
        if ( lons == null ) {
            this.lons = new ArrayList<Double>();
        }
        else {
            this.lons = new ArrayList<Double>(lons);
        }
    }

    /**
     * @return the latitudes of the overlap; never null but may be empty. The actual ArrayList in this instance is
     * returned.
     */
    public ArrayList<Double> getLats() {
        return lats;
    }

    /**
     * @param lats
     *         the latitudes of the overlap to set. If null, an empty ArrayList is assigned.
     */
    public void setLats(ArrayList<Double> lats) {
        if ( lats == null ) {
            this.lats = new ArrayList<Double>();
        }
        else {
            this.lats = new ArrayList<Double>(lats);
        }
    }

    /**
     * @return the times, in seconds since 1 JAN 1970 00:00:00, of the overlap; never null but may be empty. The actual
     * ArrayList in this instance is returned.
     */
    public ArrayList<Double> getTimes() {
        return times;
    }

    /**
     * @param times
     *         the times, in seconds since 1 JAN 1970 00:00:00, of the overlap to set.  If null, an empty ArrayList
     *         assigned.
     */
    public void setTimes(ArrayList<Double> times) {
        if ( times == null ) {
            this.times = new ArrayList<Double>();
        }
        else {
            this.times = new ArrayList<Double>(times);
        }
    }

    /**
     * Adds the given duplicated data point to this overlap.
     *
     * @param firstRowNum
     *         row number (starts with one) of the overlap data point in the first dataset
     * @param secondRowNum
     *         row number (starts with one) of the overlap data point in the second dataset
     * @param longitude
     *         longitude of the overlap data point in the datasets
     * @param latitude
     *         latitude of the overlap data point in the datasets
     * @param time
     *         time, in seconds since 1 JAN 1970 00:00:00, of the overlap data point in the datasets
     */
    public void addDuplicatePoint(int firstRowNum, int secondRowNum,
            double longitude, double latitude, double time) {
        rowNums[0].add(firstRowNum);
        rowNums[1].add(secondRowNum);
        lons.add(longitude);
        lats.add(latitude);
        times.add(time);
    }

    @Override
    public int compareTo(Overlap other) {
        int result;

        // First separate internal overlaps from external overlaps
        boolean internal = expocodes[0].equals(expocodes[1]);
        boolean otherInternal = other.expocodes[0].equals(other.expocodes[1]);
        if ( internal && !otherInternal )
            return -1;
        if ( otherInternal && !internal )
            return 1;

        // Then order on the size of the overlap (number of data points); check all just in case
        result = Integer.compare(rowNums[0].size(), other.rowNums[0].size());
        if ( result != 0 )
            return result;
        result = Integer.compare(rowNums[1].size(), other.rowNums[1].size());
        if ( result != 0 )
            return result;
        result = Integer.compare(times.size(), other.times.size());
        if ( result != 0 )
            return result;
        result = Integer.compare(lats.size(), other.lats.size());
        if ( result != 0 )
            return result;
        result = Integer.compare(lons.size(), other.lons.size());
        if ( result != 0 )
            return result;

        // Finally order by expocodes
        result = expocodes[0].compareTo(other.expocodes[0]);
        if ( result != 0 )
            return result;
        result = expocodes[1].compareTo(other.expocodes[1]);
        if ( result != 0 )
            return result;

        // The rest is primarily to be compatible with equals
        for (int k = 0; k < rowNums[0].size(); k++) {
            result = rowNums[0].get(k).compareTo(other.rowNums[0].get(k));
            if ( result != 0 )
                return result;
        }
        for (int k = 0; k < rowNums[1].size(); k++) {
            result = rowNums[1].get(k).compareTo(other.rowNums[1].get(k));
            if ( result != 0 )
                return result;
        }
        for (int k = 0; k < times.size(); k++) {
            if ( !DashboardUtils.closeTo(times.get(k), other.times.get(k),
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                return times.get(k).compareTo(other.times.get(k));
            }
        }
        for (int k = 0; k < lats.size(); k++) {
            if ( !DashboardUtils.closeTo(lats.get(k), other.lats.get(k),
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                return lats.get(k).compareTo(other.lats.get(k));
            }
        }
        for (int k = 0; k < lons.size(); k++) {
            if ( !DashboardUtils.longitudeCloseTo(lons.get(k), other.lons.get(k),
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                return lons.get(k).compareTo(other.lons.get(k));
            }
        }

        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = Arrays.hashCode(expocodes);
        result = prime * result + Arrays.hashCode(rowNums);
        // Do not include floating point values, as they do not have to be exact to match
        // but do include the number of floating point values as those do have to match
        result = prime * result + Integer.hashCode(times.size());
        result = prime * result + Integer.hashCode(lats.size());
        result = prime * result + Integer.hashCode(lons.size());

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
        if ( !(obj instanceof Overlap) ) {
            return false;
        }

        Overlap other = (Overlap) obj;

        if ( times.size() != other.times.size() )
            return false;
        if ( lats.size() != other.lats.size() )
            return false;
        if ( lons.size() != other.lons.size() )
            return false;

        if ( !Arrays.equals(expocodes, other.expocodes) )
            return false;
        if ( !Arrays.equals(rowNums, other.rowNums) )
            return false;

        for (int k = 0; k < times.size(); k++) {
            if ( !DashboardUtils.closeTo(times.get(k), other.times.get(k),
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                return false;
            }
        }
        for (int k = 0; k < lats.size(); k++) {
            if ( !DashboardUtils.closeTo(lats.get(k), other.lats.get(k),
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                return false;
            }
        }
        for (int k = 0; k < lons.size(); k++) {
            if ( !DashboardUtils.longitudeCloseTo(lons.get(k), other.lons.get(k),
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
                ", lons=" + lons.toString() +
                ", lats=" + lats.toString() +
                ", times=" + times.toString() +
                "]";
    }

}
