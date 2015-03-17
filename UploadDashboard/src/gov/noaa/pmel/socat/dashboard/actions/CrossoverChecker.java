/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import java.io.FileNotFoundException;
import java.io.IOException;

import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SocatCrossover;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;

/**
 * Checks for crossovers between cruises.
 * 
 * @author Karl Smith
 */
public class CrossoverChecker {

	private static final double MISSVAL_RTOLER = 1.0E-12;
	private static final double MISSVAL_ATOLER = 1.0E-5;

	DsgNcFileHandler dsgHandler;

	/**
	 * Creates a checker for high-quality crossovers.
	 * 
	 * @param dsgHandler
	 * 		handler to obtain full-data DSG files for cruises
	 */
	public CrossoverChecker(DsgNcFileHandler dsgHandler) {
		this.dsgHandler = dsgHandler;
	}

	/**
	 * Checks for high-quality crossovers between two cruises
	 * 
	 * @param expocodes
	 * 		the expocodes of the two cruises to examine
	 * @return
	 * 		the closest high-quality crossover between the two cruises, or
	 * 		null if no high-quality crossovers were found
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
			 (expocodes[0] == null) || (expocodes[1] == null) ||
			 (expocodes[0]).equals(expocodes[1]) )
			throw new IllegalArgumentException("Invalid expocodes given to checkForCrossover");

		// Check that the NODC codes are different - crossovers must be between different instruments
		if ( (expocodes[0]).substring(0,4).equals((expocodes[1]).substring(0,4)) )
			return null;

		int[] numRows = new int[2];
		double[][] lons = new double[2][];
		double[][] lats = new double[2][];
		double[][] times = new double[2][];
		double[][] ssts = new double[2][];
		double[][] fco2s = new double[2][];

		double[][] dataVals = dsgHandler.readLonLatTimeSstFco2DataValues(expocodes[0]);
		numRows[0] = (dataVals[0]).length;
		lons[0] = dataVals[0];
		lats[0] = dataVals[1];
		times[0] = dataVals[2];
		ssts[0] = dataVals[3];
		fco2s[0] = dataVals[4];

		dataVals = dsgHandler.readLonLatTimeSstFco2DataValues(expocodes[1]);
		numRows[1] = (dataVals[0]).length;
		lons[1] = dataVals[0];
		lats[1] = dataVals[1];
		times[1] = dataVals[2];
		ssts[1] = dataVals[3];
		fco2s[1] = dataVals[4];

		double minDistance = SocatCrossover.MAX_CROSSOVER_DIST + 1.0;
		SocatCrossover crossover = null;
		Long[] dataMinTimes = null;
		Long[] dataMaxTimes = null;
		for (int j = 0; j < numRows[0]; j++) {
			// Skip this point if any missing values
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, lons[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, lats[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, times[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, ssts[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;
			if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, fco2s[0][j], MISSVAL_RTOLER, MISSVAL_ATOLER) )
				continue;

			for (int k = 0; k < numRows[1]; k++) {
				// Skip this point if any missing values
				if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, lons[1][k], MISSVAL_RTOLER, MISSVAL_ATOLER) )
					continue;
				if ( DashboardUtils.closeTo(SocatCruiseData.FP_MISSING_VALUE, lats[1][k], MISSVAL_RTOLER, MISSVAL_ATOLER) )
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
				if ( Math.abs(lats[1][k] - lats[0][j]) > SocatCrossover.MAX_LAT_DIFF ) {
					/*
					 * Differences in latitudes are too large.
					 * Go on to the next point of the second cruise. 
					 */
					continue;
				}

				double locTimeDist = distanceTo(lons[0][j], lats[0][j], times[0][j], 
												lons[1][j], lats[1][j], times[1][j]);
				if ( (locTimeDist < minDistance) && (locTimeDist <= SocatCrossover.MAX_CROSSOVER_DIST) ) {

					// Get the min and max data times for the cruises if not done earlier
					if ( (dataMaxTimes == null) || (dataMinTimes == null) ) {
						dataMinTimes = new Long[] {null, null};
						dataMaxTimes = new Long[] {null, null};
						for (int q = 0; q < 2; q++) {
							double[] minMaxVals = getMinMaxValid(times[q]);
							if ( (minMaxVals[0] == SocatCruiseData.FP_MISSING_VALUE) ||
								 (minMaxVals[1] == SocatCruiseData.FP_MISSING_VALUE) )
								throw new IOException("No valid times for " + expocodes[q]);
							dataMinTimes[q] = Math.round(minMaxVals[0]);
							dataMaxTimes[q] = Math.round(minMaxVals[1]);
						}
					}

					// Update this minimum distance and record the crossover
					minDistance = locTimeDist;
					crossover = new SocatCrossover();
					crossover.setExpocodes(expocodes);
					crossover.setMinDistance(minDistance);
					crossover.setRowNumsAtMin(new Integer[] {j+1, k+1});
					crossover.setLonsAtMin(new Double[] {lons[0][j], lons[1][k]});
					crossover.setLatsAtMin(new Double[] {lats[0][j], lats[1][k]});
					crossover.setTimesAtMin(new Long[] {Math.round(times[0][j]), Math.round(times[1][k])});
					crossover.setCruiseMinTimes(dataMinTimes);
					crossover.setCruiseMaxTimes(dataMaxTimes);
				}
			}
		}

		return crossover;
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
	private double[] getMinMaxValid(double[] data) {
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
	private double distanceTo(double lon, double lat, double time, 
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

}
