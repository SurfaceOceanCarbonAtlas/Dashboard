/**
 *
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.Crossover;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Checks for high-quality crossovers between datasets.  High-quality crossovers are desirable coincidental
 * near-overlaps of location, time, and some other properties found in datasets from different platforms
 * (different NODC codes).
 *
 * @author Karl Smith
 */
public class CrossoverChecker {

    private DsgNcFileHandler dsgHandler;

    /**
     * Create a crossover checker which gets data from the full-data DSG files using the given DSG data file handler.
     *
     * @param dsgHandler
     *         the DSG data file handler to use
     */
    public CrossoverChecker(DsgNcFileHandler dsgHandler) {
        this.dsgHandler = dsgHandler;
    }

    /**
     * Checks for high-quality crossovers between two datasets.  Always reads the data for both cruises, so intended
     * only for a one-time pair check; use other methods to efficiently check a dataset against many other datasets.
     *
     * @param expocodes
     *         the IDs of the two datasets to examine
     *
     * @return the closest high-quality crossover between the two datasets, or
     *         null if not high-quality crossover was found.
     *         The crossover returned will be fully assigned.
     *
     * @throws IllegalArgumentException
     *         if either expocode is invalid
     * @throws FileNotFoundException
     *         if the full-data DSG file for either dataset is not found
     * @throws IOException
     *         if problems reading from either full-data DSG file
     */
    public Crossover checkForCrossover(String[] expocodes)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        if ( (expocodes == null) || (expocodes.length != 2) ||
                (expocodes[0] == null) || (expocodes[1] == null) )
            throw new IllegalArgumentException("Invalid expcodes argument given to checkForCrossover");
        String[] upperExpos = new String[] { DashboardServerUtils.checkDatasetID(expocodes[0]),
                DashboardServerUtils.checkDatasetID(expocodes[1]) };
        // Check that the NODC codes are different - crossovers must be between different instruments
        if ( (upperExpos[0]).substring(0, 4).equals((upperExpos[1]).substring(0, 4)) )
            return null;

        double[][] lons = new double[2][];
        double[][] lats = new double[2][];
        double[][] times = new double[2][];
        double[][] ssts = new double[2][];
        double[][] fco2s = new double[2][];

        double[][] dataVals = dsgHandler.readLonLatTimeSstFco2DataValues(upperExpos[0]);
        lons[0] = dataVals[0];
        lats[0] = dataVals[1];
        times[0] = dataVals[2];
        ssts[0] = dataVals[3];
        fco2s[0] = dataVals[4];

        dataVals = dsgHandler.readLonLatTimeSstFco2DataValues(upperExpos[1]);
        lons[1] = dataVals[0];
        lats[1] = dataVals[1];
        times[1] = dataVals[2];
        ssts[1] = dataVals[3];
        fco2s[1] = dataVals[4];

        // Check for any possibility of time overlap
        Long[] dataMinTimes = new Long[2];
        Long[] dataMaxTimes = new Long[2];
        for (int q = 0; q < 2; q++) {
            double[] minMaxVals = DashboardServerUtils.getMinMaxValidData(times[q]);
            if ( (minMaxVals[0] == DashboardUtils.FP_MISSING_VALUE) ||
                    (minMaxVals[1] == DashboardUtils.FP_MISSING_VALUE) )
                throw new IOException("No valid times for " + expocodes[q]);
            dataMinTimes[q] = Math.round(minMaxVals[0]);
            dataMaxTimes[q] = Math.round(minMaxVals[1]);
        }
        if ( (dataMaxTimes[0] + DashboardServerUtils.MAX_TIME_DIFF < dataMinTimes[1]) ||
                (dataMaxTimes[1] + DashboardServerUtils.MAX_TIME_DIFF < dataMinTimes[0]) )
            return null;

        // Check for any possibility of latitude overlap
        double[] dataMinLats = new double[2];
        double[] dataMaxLats = new double[2];
        for (int q = 0; q < 2; q++) {
            double[] minMaxVals = DashboardServerUtils.getMinMaxValidData(lats[q]);
            if ( (minMaxVals[0] == DashboardUtils.FP_MISSING_VALUE) ||
                    (minMaxVals[1] == DashboardUtils.FP_MISSING_VALUE) )
                throw new IOException("No valid latitudes for " + expocodes[q]);
            dataMinLats[q] = minMaxVals[0];
            dataMaxLats[q] = minMaxVals[1];
        }
        if ( (dataMaxLats[0] + DashboardServerUtils.MAX_LAT_DIFF < dataMinLats[1]) ||
                (dataMaxLats[1] + DashboardServerUtils.MAX_LAT_DIFF < dataMinLats[0]) )
            return null;

        // Check for a crossover
        Crossover crossover = checkForCrossover(lons, lats, times, ssts, fco2s);
        if ( crossover != null ) {
            // crossover found; add the datasetIds, dataMinTimes, and dataMaxTimes
            crossover.setDatasetIds(upperExpos);
            crossover.setDatasetMinTimes(dataMinTimes);
            crossover.setDatasetMaxTimes(dataMaxTimes);
        }

        return crossover;
    }

    /**
     * Checks for high-quality crossovers between a dataset and a set of other datasets.  Reads the data once
     * for the primary dataset, then reads the data as needed for the other datasets.  Assumes the datasets
     * to check against are those whose time and latitude minimums and maximums are such that a crossover is a
     * possibility, so this check is not performed.  The cruiseMinTimes and cruiseMaxTimes are neither computed
     * nor assigned in the crossovers since these presumably have already been computed elsewhere.
     *
     * @param expocode
     *         the ID of the primary dataset to examine
     * @param checkExpos
     *         IDs of datasets to check for crossovers with the primary dataset
     * @param progressPrinter
     *         if not null, progress messages with timings are printed here
     * @param progStartMilliTime
     *         start time of the program for reporting times to progressWriter;
     *         only used if progressPrinter is not null
     *
     * @return the list of crossovers found; never null but may empty.
     *         The crossovers in the list will not have the cruiseMinTimes and cruiseMaxTimes assigned.
     *
     * @throws IllegalArgumentException
     *         if any expocode is invalid
     * @throws FileNotFoundException
     *         if the the full-data DSG file for any dataset is not found
     * @throws IOException
     *         if problems reading from any full-data DSG file
     */
    public ArrayList<Crossover> getCrossovers(String expocode, Iterable<String> checkExpos, PrintStream progressPrinter,
            long progStartMilliTime) throws IllegalArgumentException, FileNotFoundException, IOException {
        ArrayList<Crossover> crossList = new ArrayList<Crossover>();

        String[] upperExpos = new String[2];
        double[][] lons = new double[2][];
        double[][] lats = new double[2][];
        double[][] times = new double[2][];
        double[][] ssts = new double[2][];
        double[][] fco2s = new double[2][];

        // Get the data for the primary cruise
        upperExpos[0] = DashboardServerUtils.checkDatasetID(expocode);
        if ( progressPrinter != null ) {
            double deltaMinutes = (System.currentTimeMillis() - progStartMilliTime) / (60.0 * 1000.0);
            progressPrinter.format("%.2fm - reading data for %s\n", deltaMinutes, upperExpos[0]);
            progressPrinter.flush();
        }
        double[][] dataVals = dsgHandler.readLonLatTimeSstFco2DataValues(upperExpos[0]);
        lons[0] = dataVals[0];
        lats[0] = dataVals[1];
        times[0] = dataVals[2];
        ssts[0] = dataVals[3];
        fco2s[0] = dataVals[4];

        for (String otherExpo : checkExpos) {
            upperExpos[1] = DashboardServerUtils.checkDatasetID(otherExpo);
            // Check that the NODC codes are different - crossovers must be between different instruments
            if ( (upperExpos[0]).substring(0, 4).equals((upperExpos[1]).substring(0, 4)) )
                continue;

            if ( progressPrinter != null ) {
                double deltaMinutes = (System.currentTimeMillis() - progStartMilliTime) / (60.0 * 1000.0);
                progressPrinter.format("%.2fm - reading data for %s\n", deltaMinutes, upperExpos[1]);
                progressPrinter.flush();
            }

            dataVals = dsgHandler.readLonLatTimeSstFco2DataValues(upperExpos[1]);
            lons[1] = dataVals[0];
            lats[1] = dataVals[1];
            times[1] = dataVals[2];
            ssts[1] = dataVals[3];
            fco2s[1] = dataVals[4];

            long checkStartMilliTime = System.currentTimeMillis();
            if ( progressPrinter != null ) {
                double deltaMinutes = (checkStartMilliTime - progStartMilliTime) / (60.0 * 1000.0);
                progressPrinter.format("%.2fm - examining %s and %s: ", deltaMinutes, upperExpos[0], upperExpos[1]);
                progressPrinter.flush();
            }

            // Check for a crossover
            Crossover crossover = checkForCrossover(lons, lats, times, ssts, fco2s);
            if ( crossover != null ) {
                // crossover found; add the datasetIds (only the values in the array are used)
                crossover.setDatasetIds(upperExpos);
                crossList.add(crossover);
                if ( progressPrinter != null ) {
                    double checkDeltaSecs = (System.currentTimeMillis() - checkStartMilliTime) / 1000.0;
                    progressPrinter.format("%.2fs - crossover found: %s\n", checkDeltaSecs, crossover.toString());
                    progressPrinter.flush();
                }
            }
            else if ( progressPrinter != null ) {
                double checkDeltaSecs = (System.currentTimeMillis() - checkStartMilliTime) / 1000.0;
                System.err.format("%.2fs - no crossover\n", checkDeltaSecs);
                progressPrinter.flush();
            }
        }

        return crossList;
    }

    /**
     * Checks for high-quality crossovers in a set of dataset with another set of datasets.
     * Computes the min and max times and latitudes for all datasets beforehand to efficiently check
     * if a crossover is a possibility before actually reading data from each dataset to search
     * for crossovers.  The crossovers returned are all fully assigned.
     * <p>
     * The keys of the returned map are the dataset IDs from reportExpos, if there was a crossover
     * found for that dataset.  The map values are the crossovers of that dataset with datasets having
     * an ID in checkExpos.  The crossovers are ordered with closest crossovers first.
     *
     * @param reportExpos
     *         report any crossovers of datasets with these IDs
     * @param checkExpos
     *         search for crossovers in datasets with these IDs
     * @param progressPrinter
     *         if not null, print progress messages here
     * @param startTimeMillis
     *         start time of the program for reporting times to progressWriter;
     *         only used if progressPrinter is not null
     *
     * @return the map of crossovers found; never null but may empty.
     *
     * @throws IllegalArgumentException
     *         if any dataset ID is invalid
     * @throws FileNotFoundException
     *         if the the full-data DSG file for any dataset is not found
     * @throws IOException
     *         if problems reading from any full-data DSG file
     */
    public TreeMap<String,TreeSet<Crossover>> findCrossovers(Collection<String> reportExpos,
            Collection<String> checkExpos, PrintStream progressPrinter, long startTimeMillis)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        // Get all the unique expocodes to analyze data only once
        TreeSet<String> allExpos = new TreeSet<String>(reportExpos);
        allExpos.addAll(checkExpos);

        TreeMap<String,double[]> timeMinMaxMap = new TreeMap<String,double[]>();
        TreeMap<String,double[]> latMinMaxMap = new TreeMap<String,double[]>();
        for (String expo : allExpos) {
            if ( progressPrinter != null ) {
                double timeDiff = (System.currentTimeMillis() - startTimeMillis) / (60.0 * 1000.0);
                progressPrinter.format("%.2fm - getting data limits for %s\n", timeDiff, expo);
            }
            double[][] dataVals = dsgHandler.readLonLatTimeDataValues(expo);
            double[] timeMinMaxVals = DashboardServerUtils.getMinMaxValidData(dataVals[2]);
            if ( (timeMinMaxVals[0] == DashboardUtils.FP_MISSING_VALUE) ||
                    (timeMinMaxVals[1] == DashboardUtils.FP_MISSING_VALUE) )
                throw new IllegalArgumentException("No valid times for " + expo);
            timeMinMaxMap.put(expo, timeMinMaxVals);
            double[] latMinMaxVals = DashboardServerUtils.getMinMaxValidData(dataVals[1]);
            if ( (latMinMaxVals[0] == DashboardUtils.FP_MISSING_VALUE) ||
                    (latMinMaxVals[1] == DashboardUtils.FP_MISSING_VALUE) )
                throw new IllegalArgumentException("No valid latitudes for " + expo);
            latMinMaxMap.put(expo, latMinMaxVals);
        }

        TreeMap<String,TreeSet<Crossover>> crossoversMap = new TreeMap<String,TreeSet<Crossover>>();
        for (String firstExpo : allExpos) {
            double[] firstTimeMinMax = timeMinMaxMap.get(firstExpo);
            double[] firstLatMinMax = latMinMaxMap.get(firstExpo);
            // Get the list of possibly-crossing cruises to check
            TreeSet<String> possibleExpos = new TreeSet<String>();
            for (String secondExpo : allExpos) {
                // Only check datasets preceding the first one so not doing two checks on a report pair
                if ( firstExpo.equals(secondExpo) )
                    break;
                // Must be difference intstruments == different NODC codes at this time
                if ( firstExpo.substring(0, 4).equals(secondExpo.substring(0, 4)) )
                    continue;
                // One of the datasets must be from the report set, one from the check set
                if ( ! ( (reportExpos.contains(firstExpo) && checkExpos.contains(secondExpo)) ||
                         (reportExpos.contains(secondExpo) && checkExpos.contains(firstExpo)) ) )
                    continue;

                // Check that there is some overlap in time
                double[] secondTimeMinMax = timeMinMaxMap.get(secondExpo);
                if ( (firstTimeMinMax[1] + DashboardServerUtils.MAX_TIME_DIFF < secondTimeMinMax[0]) ||
                        (secondTimeMinMax[1] + DashboardServerUtils.MAX_TIME_DIFF < firstTimeMinMax[0]) )
                    continue;
                // Check that there is some overlap in latitude
                double[] secondLatMinMax = latMinMaxMap.get(secondExpo);
                if ( (firstLatMinMax[1] + DashboardServerUtils.MAX_TIME_DIFF < secondLatMinMax[0]) ||
                        (secondLatMinMax[1] + DashboardServerUtils.MAX_TIME_DIFF < firstLatMinMax[0]) )
                    continue;
                possibleExpos.add(secondExpo);
            }
            // Find any crossovers of this dataset with any of the selected set of datasets
            if ( possibleExpos.size() > 0 ) {
                ArrayList<Crossover> crossList = getCrossovers(firstExpo, possibleExpos,
                        progressPrinter, startTimeMillis);
                for (Crossover cross : crossList) {
                    String[] expos = cross.getDatasetIds();
                    Long[] minTimes = new Long[2];
                    Long[] maxTimes = new Long[2];
                    for (int q = 0; q < 2; q++) {
                        double[] timeMinMax = timeMinMaxMap.get(expos[q]);
                        minTimes[q] = Math.round(timeMinMax[0]);
                        maxTimes[q] = Math.round(timeMinMax[1]);
                    }
                    cross.setDatasetMinTimes(minTimes);
                    cross.setDatasetMaxTimes(maxTimes);
                    for (int q = 0; q < 2; q++) {
                        if ( reportExpos.contains(expos[q]) ) {
                            TreeSet<Crossover> crossovers = crossoversMap.get(expos[q]);
                            if ( crossovers == null ) {
                                crossovers = new TreeSet<Crossover>();
                                crossoversMap.put(expos[q], crossovers);
                            }
                            crossovers.add(cross);
                        }
                    }
                }
            }
        }
        return crossoversMap;
    }

    /**
     * Checks for high-quality crossovers in the data of a pair of datasets.
     *
     * @param longitudes
     *         the longitudes for the two datasets
     * @param latitudes
     *         the latitudes for the two datasets
     * @param times
     *         the times, in seconds since Jan 1, 1970 00:00:00, of the two datasets
     * @param ssts
     *         the SSTs values for the two datasets
     * @param fco2s
     *         the fCO2_recommended values of the two datasets
     *
     * @return the closest high-quality crossover between the two datasets, or
     *         null if no high-quality crossovers were found.  The dataset IDs, cruiseMinTimes,
     *         and cruiseMaxTimes will not have been assigned in the returned crossover.
     *
     * @throws IllegalArgumentException
     *         if any of the arguments do not have length 2,
     *         if any of the arguments or argument array values are null, or
     *         if the number of longitude, latitude, time, SST, or fCO2_recommended data values for a dataset differ
     */
    public static Crossover checkForCrossover(double[][] longitudes, double[][] latitudes, double[][] times,
            double[][] ssts, double[][] fco2s) throws IllegalArgumentException {
        if ( (longitudes == null) || (longitudes.length != 2) || (longitudes[0] == null) || (longitudes[1] == null) )
            throw new IllegalArgumentException("Invalid longitudes given to checkForCrossover");
        if ( (latitudes == null) || (latitudes.length != 2) || (latitudes[0] == null) || (latitudes[1] == null) )
            throw new IllegalArgumentException("Invalid latitudes given to checkForCrossover");
        if ( (times == null) || (times.length != 2) || (times[0] == null) || (times[1] == null) )
            throw new IllegalArgumentException("Invalid times given to checkForCrossover");
        if ( (ssts == null) || (ssts.length != 2) || (ssts[0] == null) || (ssts[1] == null) )
            throw new IllegalArgumentException("Invalid ssts given to checkForCrossover");
        if ( (fco2s == null) || (fco2s.length != 2) || (fco2s[0] == null) || (fco2s[1] == null) )
            throw new IllegalArgumentException("Invalid fco2s given to checkForCrossover");

        int[] numRows = new int[] { longitudes[0].length, longitudes[1].length };
        if ( (latitudes[0].length != numRows[0]) || (latitudes[1].length != numRows[1]) )
            throw new IllegalArgumentException("Sizes of longitudes and latitudes arrays do not match");
        if ( (times[0].length != numRows[0]) || (times[1].length != numRows[1]) )
            throw new IllegalArgumentException("Sizes of longitudes and times arrays do not match");
        if ( (ssts[0].length != numRows[0]) || (ssts[1].length != numRows[1]) )
            throw new IllegalArgumentException("Sizes of longitudes and ssts arrays do not match");
        if ( (fco2s[0].length != numRows[0]) || (fco2s[1].length != numRows[1]) )
            throw new IllegalArgumentException("Sizes of longitudes and fco2s arrays do not match");

        double minDistance = DashboardServerUtils.MAX_CROSSOVER_DIST;
        Crossover crossover = null;
        for (int j = 0; j < numRows[0]; j++) {
            // Skip this point if any missing values
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, longitudes[0][j],
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, latitudes[0][j],
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, times[0][j],
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, ssts[0][j],
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, fco2s[0][j],
                    0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;

            for (int k = 0; k < numRows[1]; k++) {
                // Skip this point if any missing values
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, longitudes[1][k],
                        0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, latitudes[1][k],
                        0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, times[1][k],
                        0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, ssts[1][k],
                        0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;
                if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, fco2s[1][k],
                        0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                    continue;

                if ( times[1][k] > times[0][j] + DashboardServerUtils.MAX_TIME_DIFF ) {
                    // The rest of the second cruise occurred far later than the point of first cruise.
                    // Go on to the next point of the first cruise.
                    break;
                }
                if ( times[1][k] < times[0][j] - DashboardServerUtils.MAX_TIME_DIFF ) {
                    // This point of the second cruise occurred far earlier than the point of the first cruise.
                    // Go on to the next point of the second cruise.
                    continue;
                }
                if ( Math.abs(ssts[1][k] - ssts[0][j]) > DashboardServerUtils.MAX_TEMP_DIFF ) {
                    // SST difference too large.
                    // Go on to the next point of the second cruise.
                    continue;
                }
                if ( Math.abs(fco2s[1][k] - fco2s[0][j]) > DashboardServerUtils.MAX_FCO2_DIFF ) {
                    // fCO2 difference too large.
                    // Go on to the next point of the second cruise.
                    continue;
                }
                if ( Math.abs(latitudes[1][k] - latitudes[0][j]) > DashboardServerUtils.MAX_LAT_DIFF ) {
                    // Differences in latitudes are too large.
                    // Go on to the next point of the second cruise.
                    continue;
                }

                double locTimeDist = DashboardServerUtils.distanceBetween(
                        longitudes[0][j], latitudes[0][j], times[0][j],
                        longitudes[1][k], latitudes[1][k], times[1][k]);
                if ( locTimeDist < minDistance ) {
                    // Update this minimum distance and record the crossover
                    minDistance = locTimeDist;
                    crossover = new Crossover();
                    crossover.setMinDistance(minDistance);
                    crossover.setRowNumsAtMin(new Integer[] { j + 1, k + 1 });
                    crossover.setLonsAtMin(new Double[] { longitudes[0][j], longitudes[1][k] });
                    crossover.setLatsAtMin(new Double[] { latitudes[0][j], latitudes[1][k] });
                    crossover.setTimesAtMin(new Long[] { Math.round(times[0][j]), Math.round(times[1][k]) });
                }
            }
        }

        return crossover;
    }

}
