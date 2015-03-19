/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SocatCrossover;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Checks for crossovers between cruises.
 * 
 * @author Karl Smith
 */
public class CrossoverChecker {

	private static final double MISSVAL_RTOLER = 1.0E-12;
	private static final double MISSVAL_ATOLER = 1.0E-5;

	private DsgNcFileHandler dsgHandler;

	/**
	 * Create a crossover checker which gets data from the full-data DSG files 
	 * using the given DSG data file handler.
	 * 
	 * @param dsgHandler
	 * 		the DSG data file handler to use 
	 */
	public CrossoverChecker(DsgNcFileHandler dsgHandler) {
		this.dsgHandler = dsgHandler;
	}

	/**
	 * Checks for high-quality crossovers between two cruises.
	 * Reads the data for both cruises, so for a one-time pair checks.
	 * If checking a cruise against many other cruises, better to
	 * read the data for the one cruise once and call 
	 * {@link #checkForCrossover(double[][], double[][], double[][], double[][], double[][])}
	 * method, then assign the expocodes and data miniminums and maximums.
	 * 
	 * @param expocodes
	 * 		the expocodes of the two cruises to examine
	 * @return
	 * 		null if no high-quality crossovers were found, or
	 * 		the closest high-quality crossover between the two cruises
	 * @throws IllegalArgumentException
	 * 		if either expocode is invalid
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file for either cruise is not found
	 * @throws IOException
	 * 		if problems reading from either full-data DSG file
	 */
	public SocatCrossover checkForCrossover(String[] expocodes) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		if ( (expocodes == null) || (expocodes.length != 2) || 
			 (expocodes[0] == null) || (expocodes[1] == null) )
			throw new IllegalArgumentException("Invalid expocodes given to checkForCrossover");
		String[] upperExpos = new String[] { DashboardServerUtils.checkExpocode(expocodes[0]),
											 DashboardServerUtils.checkExpocode(expocodes[1]) };
		// Check that the NODC codes are different - crossovers must be between different instruments
		if ( (upperExpos[0]).substring(0,4).equalsIgnoreCase((upperExpos[1]).substring(0,4)) )
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

		Long[] dataMinTimes = new Long[2];
		Long[] dataMaxTimes = new Long[2];
		for (int q = 0; q < 2; q++) {
			double[] minMaxVals = getMinMaxValidData(times[q]);
			if ( (minMaxVals[0] == SocatCruiseData.FP_MISSING_VALUE) ||
				 (minMaxVals[1] == SocatCruiseData.FP_MISSING_VALUE) )
				throw new IOException("No valid times for " + expocodes[q]);
			dataMinTimes[q] = Math.round(minMaxVals[0]);
			dataMaxTimes[q] = Math.round(minMaxVals[1]);
		}
		if ( (dataMaxTimes[0] + SocatCrossover.MAX_TIME_DIFF < dataMinTimes[1]) ||
			 (dataMaxTimes[1] + SocatCrossover.MAX_TIME_DIFF < dataMinTimes[0]) )
			return null;
			
		double[] dataMinLats = new double[2];
		double[] dataMaxLats = new double[2];
		for (int q = 0; q < 2; q++) {
			double[] minMaxVals = getMinMaxValidData(lats[q]);
			if ( (minMaxVals[0] == SocatCruiseData.FP_MISSING_VALUE) ||
				 (minMaxVals[1] == SocatCruiseData.FP_MISSING_VALUE) )
				throw new IOException("No valid latitudes for " + expocodes[q]);
			dataMinLats[q] = minMaxVals[0];
			dataMaxLats[q] = minMaxVals[1];
		}
		if ( (dataMaxLats[0] + SocatCrossover.MAX_LAT_DIFF < dataMinLats[1]) ||
			 (dataMaxLats[1] + SocatCrossover.MAX_LAT_DIFF < dataMinLats[0]) )
			return null;
		
		SocatCrossover crossover = checkForCrossover(lons, lats, times, ssts, fco2s);
		if ( crossover != null ) {
			crossover.setExpocodes(upperExpos);
			crossover.setCruiseMinTimes(dataMinTimes);
			crossover.setCruiseMaxTimes(dataMaxTimes);
		}

		return crossover; 
	}

	/**
	 * Checks for high-quality crossovers between the two given sets of cruise data.
	 * 
	 * @param longitudes
	 * 		the longitudes of the the data for the two cruises
	 * @param latitudes
	 * 		the latitudes of the the data for the two cruises
	 * @param times
	 * 		the times, in seconds since Jan 1, 1970 00:00:00, of the data for the two cruises
	 * @param ssts
	 * 		the SSTs values of the data for the two cruises
	 * @param fco2s
	 * 		the fCO2_recommended values of the data for the two cruises
	 * @return
	 * 		null if no high-quality crossovers were found, or
	 * 		the closest high-quality crossover between the two cruises;
	 * 		the expocodes, cruiseMinTimes, and cruiseMaxTimes will not
	 * 		have been assigned in the returned crossover.
	 * @throws IllegalArgumentException
	 * 		if any of the arguments do not have length 2,
	 * 		if any of the arguments or argument array values are null, or
	 * 		if the number of longitude, latitude, time, SST, or 
	 * 		fCO2_recommended data differ for a cruise 
	 */
	public static SocatCrossover checkForCrossover(double[][] longitudes, double[][] latitudes, 
			double[][] times, double[][] ssts, double[][] fco2s) throws IllegalArgumentException {
		if ( (longitudes == null) || (longitudes.length != 2) || 
			 (longitudes[0] == null) || (longitudes[1] == null) )
			throw new IllegalArgumentException("Invalid longitudes given to checkForCrossover");
		if ( (latitudes == null) || (latitudes.length != 2) ||
			 (latitudes[0] == null) || (latitudes[1] == null) )
			throw new IllegalArgumentException("Invalid latitudes given to checkForCrossover");
		if ( (times == null) || (times.length != 2) ||
			 (times[0] == null) || (times[1] == null) )
			throw new IllegalArgumentException("Invalid times given to checkForCrossover");
		if ( (ssts == null) || (ssts.length != 2) ||
			 (ssts[0] == null) || (ssts[1] == null) )
			throw new IllegalArgumentException("Invalid ssts given to checkForCrossover");
		if ( (fco2s == null) || (fco2s.length != 2) ||
			 (fco2s[0] == null) || (fco2s[1] == null) )
			throw new IllegalArgumentException("Invalid fco2s given to checkForCrossover");

		int[] numRows = new int[] {longitudes[0].length, longitudes[1].length};
		if ( (latitudes[0].length != numRows[0]) || (latitudes[1].length != numRows[1]) )
			throw new IllegalArgumentException("Sizes of longitudes and latitudes arrays do not match");
		if ( (times[0].length != numRows[0]) || (times[1].length != numRows[1]) )
			throw new IllegalArgumentException("Sizes of longitudes and times arrays do not match");
		if ( (ssts[0].length != numRows[0]) || (ssts[1].length != numRows[1]) )
			throw new IllegalArgumentException("Sizes of longitudes and ssts arrays do not match");
		if ( (fco2s[0].length != numRows[0]) || (fco2s[1].length != numRows[1]) )
			throw new IllegalArgumentException("Sizes of longitudes and fco2s arrays do not match");

		double minDistance = SocatCrossover.MAX_CROSSOVER_DIST;
		SocatCrossover crossover = null;
		for (int j = 0; j < numRows[0]; j++) {
			// Skip this point if any missing values
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, longitudes[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, latitudes[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, times[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, ssts[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, fco2s[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;

			for (int k = 0; k < numRows[1]; k++) {
				// Skip this point if any missing values
				if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, longitudes[1][k], MISSVAL_RTOLER, MISSVAL_ATOLER) )
					continue;
				if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, latitudes[1][k], MISSVAL_RTOLER, MISSVAL_ATOLER) )
					continue;
				if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, times[1][k], MISSVAL_RTOLER, MISSVAL_ATOLER) )
					continue;
				if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, ssts[1][k], MISSVAL_RTOLER, MISSVAL_ATOLER) )
					continue;
				if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, fco2s[1][k], MISSVAL_RTOLER, MISSVAL_ATOLER) )
					continue;

				if ( times[1][k] > times[0][j] + SocatCrossover.MAX_TIME_DIFF ) {
					/* 
					 * The rest of the second cruise occurred far 
					 * later than the point of first cruise.  
					 * Go on to the next point of the first cruise.
					 */
					 break;
				}
				if ( times[1][k] < times[0][j] - SocatCrossover.MAX_TIME_DIFF ) {
					/* 
					 * This point of the second cruise occurred far 
					 * earlier than the point of the first cruise.
					 * Go on to the next point of the second cruise.
					 */
					continue;
				}
				if ( Math.abs(ssts[1][k] - ssts[0][j]) > SocatCrossover.MAX_TEMP_DIFF ) {
					// SST difference too large.  
					// Go on to the next point of the second cruise.
					continue;
				}
				if ( Math.abs(fco2s[1][k] - fco2s[0][j]) > SocatCrossover.MAX_FCO2_DIFF ) {
					// fCO2 difference too large.
					// Go on to the next point of the second cruise.
					continue;
				}
				if ( Math.abs(latitudes[1][k] - latitudes[0][j]) > SocatCrossover.MAX_LAT_DIFF ) {
					/*
					 * Differences in latitudes are too large.
					 * Go on to the next point of the second cruise. 
					 */
					continue;
				}

				double locTimeDist = distanceTo(longitudes[0][j], latitudes[0][j], times[0][j], 
												longitudes[1][k], latitudes[1][k], times[1][k]);
				if ( locTimeDist < minDistance ) {
					// Update this minimum distance and record the crossover
					minDistance = locTimeDist;
					crossover = new SocatCrossover();
					crossover.setMinDistance(minDistance);
					crossover.setRowNumsAtMin(new Integer[] {j+1, k+1});
					crossover.setLonsAtMin(new Double[] {longitudes[0][j], longitudes[1][k]});
					crossover.setLatsAtMin(new Double[] {latitudes[0][j], latitudes[1][k]});
					crossover.setTimesAtMin(new Long[] {Math.round(times[0][j]), Math.round(times[1][k])});
				}
			}
		}

		return crossover;
	}

	/**
	 * Returns the location-time "distance" to another location-time point.
	 * Uses {@link SocatCrossover#SEAWATER_SPEED} for converting differences 
	 * in time into distance.  Uses the haversine formula, and 
	 * {@link SocatCrossover#EARTH_AUTHALIC_RADIUS} for the radius of a 
	 * spherical Earth, to compute the great circle distance from the 
	 * longitudes and latitudes.
	 * 
	 * @param lon
	 * 		longitude, in degrees, of the first data location
	 * @param lat
	 * 		latitude, in degrees, of the first data location
	 * @param time
	 * 		time, in seconds since Jan 1, 1970 00:00:00, of the first data location
	 * @param otherlon
	 * 		longitude, in degrees, of the other data location
	 * @param otherlat
	 * 		latitude, in degrees, of the other data location
	 * @param othertime
	 * 		time, in seconds since Jan 1, 1970 00:00:00, of the other data location
	 * @return
	 *      the location-time distance between this location-time point
	 *      and other in kilometers
	 */
	public static double distanceTo(double lon, double lat, double time, 
							 double otherLon, double otherLat, double otherTime) {
		// Convert longitude and latitude degrees to radians
		double lat1 = lat * Math.PI / 180.0;
		double lat2 = otherLat * Math.PI / 180.0;
		double lon1 = lon * Math.PI / 180.0;
		double lon2 = otherLon * Math.PI / 180.0;
		/*
		 * Use the haversine formula to compute the great circle distance, 
		 * in radians, between the two (longitude, latitude) points. 
		 */
		double dellat = Math.sin(0.5 * (lat2 - lat1));
		dellat *= dellat;
		double dellon = Math.sin(0.5 * (lon2 - lon1));
		dellon *= dellon * Math.cos(lat1) * Math.cos(lat2);
		double distance = 2.0 * Math.asin(Math.sqrt(dellon + dellat));
		// Convert the great circle distance from radians to kilometers
		distance *= SocatCrossover.EARTH_AUTHALIC_RADIUS;

		// Get the time difference in days (24 hours)
		double deltime = (otherTime - time) / (24.0 * 60.0 * 60.0);
		// Convert to the time difference to kilometers
		deltime *= SocatCrossover.SEAWATER_SPEED;
		// Combine the time distance with the surface distance
		distance = Math.sqrt(distance * distance + deltime * deltime);

		return distance;
	}

	/**
	 * Returns the minimum and maximum valid values from the given data array.
	 * Missing values (those very close to {@link SocatCruiseData#FP_MISSING_VALUE})
	 * are ignored.
	 * 
	 * @param data
	 * 		find the minimum and maximum valid values of this data
	 * @return
	 * 		(minVal, maxVal) where minVal is the minimum, maxVal is the maximum, or
	 * 		({@link SocatCruiseData#FP_MISSING_VALUE}, {@link SocatCruiseData#FP_MISSING_VALUE})
	 * 		if all data is missing.
	 */
	public static double[] getMinMaxValidData(double[] data) {
		double maxVal = SocatCruiseData.FP_MISSING_VALUE;
		double minVal = SocatCruiseData.FP_MISSING_VALUE;
		for ( double val : data ) {
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, val, MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( (maxVal == SocatCruiseData.FP_MISSING_VALUE) ||
				 (minVal == SocatCruiseData.FP_MISSING_VALUE) ) {
				maxVal = val;
				minVal = val;
			}
			else if ( maxVal < val ) {
				maxVal = val;
			}
			else if ( minVal > val ) {
				minVal = val;
			}
		}
		return new double[] {minVal, maxVal};
	}

}
