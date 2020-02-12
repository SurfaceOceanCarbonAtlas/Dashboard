package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.Overlap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Checks for overlaps between data sets.  Overlaps are duplications of location and time values either within a data
 * set or between any two data sets. Extensive overlaps are very likely to be erroneous duplication of data, although
 * there is the rare possibility of two instruments on the same platform.
 *
 * @author Karl Smith
 */
public class OverlapChecker {

    /**
     * Cutoff time window, in seconds, for still considering data points - to allow some time disorder
     */
    public static final double TIME_WINDOW = (7.0 * 24.0 * 60.0 * 60.0);

    private DsgNcFileHandler dsgHandler;

    /**
     * Create a overlap checker which gets data from the full-data DSG files using the given DSG data file handler.
     *
     * @param dsgHandler
     *         the DSG data file handler to use
     */
    public OverlapChecker(DsgNcFileHandler dsgHandler) {
        this.dsgHandler = dsgHandler;
    }

    /**
     * Checks for overlaps between a data set and a collection of other data sets.  Reads the data once
     * for the primary data set, then reads the data as needed for the other data sets.  Ignores any
     * data points with a {@link SocatTypes#WOCE_CO2_WATER} value of {@link DashboardServerUtils#WOCE_BAD}
     * or where the fCO2_rec value is missing.
     *
     * @param expocode
     *         the expocode of the primary data set to examine
     * @param checkExpos
     *         datasetIds of data set to check for overlaps with the primary data set
     * @param progressPrinter
     *         if not null, progress messages with timings are printed using this PrintStream
     * @param progStartMilliTime
     *         System.getCurrentTimeMillis() start time of the program for reporting times to progressWriter;
     *         only used if progressPrinter is not null
     *
     * @return the list of overlaps found; never null but may be empty.
     *
     * @throws IllegalArgumentException
     *         if any expocode is invalid
     * @throws FileNotFoundException
     *         if the the full-data DSG file for any data set is not found
     * @throws IOException
     *         if problems reading from any full-data DSG file
     */
    public ArrayList<Overlap> getOverlaps(String expocode, Iterable<String> checkExpos,
            PrintStream progressPrinter, long progStartMilliTime)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        ArrayList<Overlap> overlapList = new ArrayList<Overlap>();

        String[] upperExpos = new String[2];
        double[][] lons = new double[2][];
        double[][] lats = new double[2][];
        double[][] times = new double[2][];
        boolean[][] ignores = new boolean[2][];

        // Get the data for the primary cruise
        upperExpos[0] = DashboardServerUtils.checkDatasetID(expocode);
        if ( progressPrinter != null ) {
            double deltaMinutes = (System.currentTimeMillis() - progStartMilliTime) / (60.0 * 1000.0);
            progressPrinter.format("%.2fm - reading data for %s\n", deltaMinutes, upperExpos[0]);
            progressPrinter.flush();
        }
        double[][] dataVals = getMaskedLonLatTimeSstFco2Vals(upperExpos[0]);
        lons[0] = dataVals[0];
        lats[0] = dataVals[1];
        times[0] = dataVals[2];
        // Ignore any data point that does not have an fCO2_rec value (or WOCE-4 due to masking)
        ignores[0] = new boolean[dataVals[4].length];
        for (int k = 0; k < dataVals[4].length; k++) {
            ignores[0][k] = DashboardUtils.closeTo(dataVals[4][k], DashboardUtils.FP_MISSING_VALUE,
                    DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR);
        }

        for (String otherExpo : checkExpos) {
            upperExpos[1] = DashboardServerUtils.checkDatasetID(otherExpo);
            if ( upperExpos[0].equals(upperExpos[1]) ) {
                lons[1] = lons[0];
                lats[1] = lats[0];
                times[1] = times[0];
                ignores[1] = ignores[0];
            }
            else {
                if ( progressPrinter != null ) {
                    double deltaMinutes = (System.currentTimeMillis() - progStartMilliTime) / (60.0 * 1000.0);
                    progressPrinter.format("%.2fm - reading data for %s\n", deltaMinutes, upperExpos[1]);
                    progressPrinter.flush();
                }
                dataVals = getMaskedLonLatTimeSstFco2Vals(upperExpos[1]);
                lons[1] = dataVals[0];
                lats[1] = dataVals[1];
                times[1] = dataVals[2];
                // Ignore any data point that does not have an fCO2_rec value
                ignores[1] = new boolean[dataVals[4].length];
                for (int k = 0; k < dataVals[4].length; k++) {
                    ignores[1][k] = DashboardUtils.closeTo(dataVals[4][k], DashboardUtils.FP_MISSING_VALUE,
                            DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR);
                }
            }

            long checkStartMilliTime = System.currentTimeMillis();
            if ( progressPrinter != null ) {
                double deltaMinutes = (checkStartMilliTime - progStartMilliTime) / (60.0 * 1000.0);
                progressPrinter.format("%.2fm - examining %s and %s: ", deltaMinutes, upperExpos[0], upperExpos[1]);
                progressPrinter.flush();
            }

            // Check for an overlap
            Overlap oerlap = checkForOverlaps(upperExpos, lons, lats, times, ignores);
            if ( oerlap != null ) {
                overlapList.add(oerlap);
                if ( progressPrinter != null ) {
                    double checkDeltaSecs = (System.currentTimeMillis() - checkStartMilliTime) / 1000.0;
                    progressPrinter.format("%.2fs - overlap found: %s\n", checkDeltaSecs, oerlap.toString());
                    progressPrinter.flush();
                }
            }
            else if ( progressPrinter != null ) {
                double checkDeltaSecs = (System.currentTimeMillis() - checkStartMilliTime) / 1000.0;
                System.err.format("%.2fs - no overlaps\n", checkDeltaSecs);
                progressPrinter.flush();
            }
        }

        return overlapList;
    }

    /**
     * Reads and returns the longitudes, latitudes, times, SSTs, and fCO2s for the data points
     * in a data set.  The values for any data points with a {@link SocatTypes#WOCE_CO2_WATER}
     * value of {@link DashboardServerUtils#WOCE_BAD} are set to {@link DashboardUtils#FP_MISSING_VALUE}.
     *
     * @param upperExpo
     *         get the data from the dataset with this expocode
     *
     * @return the array { longitudes, latitudes, times, SSTs, fCO2s } for the data set
     *
     * @throws IllegalArgumentException
     *         if the expocode is invalid
     * @throws FileNotFoundException
     *         if the DSG file for this data set does not exist
     * @throws IOException
     *         if there are problems opening or reading the DSG file
     */
    public double[][] getMaskedLonLatTimeSstFco2Vals(String upperExpo)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        double[][] dataVals = dsgHandler.readLonLatTimeSstFco2DataValues(upperExpo);
        String[] dataflags = dsgHandler.readStringVarDataValues(upperExpo,
                SocatTypes.WOCE_CO2_WATER.getVarName());
        for (int k = 0; k < dataflags.length; k++) {
            if ( DashboardServerUtils.WOCE_BAD.equals(dataflags[k]) ) {
                dataVals[0][k] = DashboardUtils.FP_MISSING_VALUE;
                dataVals[1][k] = DashboardUtils.FP_MISSING_VALUE;
                dataVals[2][k] = DashboardUtils.FP_MISSING_VALUE;
                dataVals[3][k] = DashboardUtils.FP_MISSING_VALUE;
                dataVals[4][k] = DashboardUtils.FP_MISSING_VALUE;
            }
        }
        return dataVals;
    }

    /**
     * Checks for overlaps between two datasets. If the two datasetIds are the same, this detects overlaps within a
     * dataset (excludes matching a data point with itself). Assumes the data points are ordered in increasing time.
     *
     * @param expocodes
     *         datasetIds of the two datasets
     * @param longitudes
     *         longitudes of the the data for the two datasets
     * @param latitudes
     *         latitudes of the the data for the two datasets
     * @param times
     *         times, in seconds since Jan 1, 1970 00:00:00, of the data for the two datasets
     * @param ignore
     *         if true for a data point, any overlaps with that data point is ignored
     *
     * @return the overlap found between the two datasets, or null if no overlaps were found
     *
     * @throws IllegalArgumentException
     *         if any of the arguments or argument array values is null, if any of the arguments is not an array of two
     *         objects, or if there is not the same number of longitudes, latitudes, and times for a dataset
     */
    private Overlap checkForOverlaps(String[] expocodes, double[][] longitudes,
            double[][] latitudes, double[][] times, boolean[][] ignore)
            throws IllegalArgumentException {
        if ( (expocodes == null) || (expocodes.length != 2) ||
                (expocodes[0] == null) || (expocodes[1] == null) )
            throw new IllegalArgumentException("Invalid datasetIds given to checkForOverlaps");
        if ( (longitudes == null) || (longitudes.length != 2) ||
                (longitudes[0] == null) || (longitudes[1] == null) )
            throw new IllegalArgumentException("Invalid longitudes given to checkForOverlaps");
        if ( (latitudes == null) || (latitudes.length != 2) ||
                (latitudes[0] == null) || (latitudes[1] == null) )
            throw new IllegalArgumentException("Invalid latitudes given to checkForOverlaps");
        if ( (times == null) || (times.length != 2) ||
                (times[0] == null) || (times[1] == null) )
            throw new IllegalArgumentException("Invalid times given to checkForOverlaps");
        if ( (ignore == null) || (ignore.length != 2) ||
                (ignore[0] == null) || (ignore[1] == null) )
            throw new IllegalArgumentException("Invalid ignore given to checkForOverlaps");

        int[] numRows = new int[] { longitudes[0].length, longitudes[1].length };
        if ( (latitudes[0].length != numRows[0]) || (latitudes[1].length != numRows[1]) )
            throw new IllegalArgumentException("Sizes of longitudes and latitudes arrays do not match");
        if ( (times[0].length != numRows[0]) || (times[1].length != numRows[1]) )
            throw new IllegalArgumentException("Sizes of longitudes and times arrays do not match");
        if ( (ignore[0].length != numRows[0]) || (ignore[1].length != numRows[1]) )
            throw new IllegalArgumentException("Sizes of longitudes and ignore arrays do not match");

        // Always make the first expocode the earlier one for reporting
        Overlap oerlap;
        boolean swapped;
        if ( expocodes[0].compareTo(expocodes[1]) > 0 ) {
            oerlap = new Overlap(expocodes[1], expocodes[0]);
            swapped = true;
        }
        else {
            oerlap = new Overlap(expocodes[0], expocodes[1]);
            swapped = false;
        }

        boolean sameExpo = expocodes[0].equals(expocodes[1]);
        int kStart = 0;
        for (int j = 0; j < numRows[0]; j++) {
            // Skip any points already WOCE-4 or with missing fCO2_rec
            if ( ignore[0][j] )
                continue;
            // Skip this point if missing lon, lat, or time value
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, longitudes[0][j],
                    DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, latitudes[0][j],
                    DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, times[0][j],
                    DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;

            if ( sameExpo )
                kStart = j + 1;

            for (int k = kStart; k < numRows[1]; k++) {
                // Skip any points already WOCE-4 or with missing fCO2_rec
                if ( ignore[1][k] )
                    continue;
                // Skip this point if missing lon, lat, or time value
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, longitudes[1][k],
                        DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, latitudes[1][k],
                        DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, times[1][k],
                        DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;

                if ( times[1][k] >= times[0][j] + TIME_WINDOW ) {
                    /*
                     * The rest of the second dataset occurred much later than
                     * the point of first dataset (allowing for some disorder
                     * in time).  Go on to the next point of the first dataset.
                     */
                    break;
                }
                if ( times[1][k] <= times[0][j] - TIME_WINDOW ) {
                    /*
                     * The point of the second dataset occurred much earlier than
                     * the point of the first dataset (allowing for some disorder
                     * in time).  Update the start point for the second dataset and
                     * go on to the next point of the second dataset.
                     */
                    kStart = k + 1;
                    continue;
                }

                if ( DashboardUtils.closeTo(times[0][j], times[1][k], 0.0, DsgNcFile.MIN_TIME_DIFF) &&
                        DashboardUtils.closeTo(latitudes[0][j], latitudes[1][k], 0.0, DsgNcFile.MIN_LAT_DIFF) &&
                        DashboardUtils.longitudeCloseTo(longitudes[0][j], longitudes[1][k],
                                0.0, DsgNcFile.MIN_LON_DIFF) ) {
                    if ( swapped ) {
                        // swap row number to match datasetIds above
                        // different expocodes; report first expocode's data point for WOCE-4
                        oerlap.addDuplicatePoint(k + 1, j + 1, longitudes[0][k], latitudes[0][k], times[0][k]);
                    }
                    else if ( sameExpo ) {
                        // internal overlap; report second occurrence for WOCE-4
                        oerlap.addDuplicatePoint(j + 1, k + 1, longitudes[0][k], latitudes[0][k], times[0][k]);
                    }
                    else {
                        // different expocodes; report first expocode's data point for WOCE-4
                        oerlap.addDuplicatePoint(j + 1, k + 1, longitudes[0][j], latitudes[0][j], times[0][j]);
                    }
                }
            }
        }

        if ( oerlap.getLons().isEmpty() )
            return null;

        return oerlap;
    }

}
