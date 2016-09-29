/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.server.CruiseDsgNcFile;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Apply a WOCE flag against fCO2_recommended to data points of a cruise.
 * Used to WOCE many data points of a cruise where the web interface becomes too slow.
 * (Normally one should never WOCE that many data points.)
 * 
 * @author Karl Smith
 */
public class WoceManyData {

	/**
	 * @param args
	 * 		expocode  start_timestamp  end_timestamp  user_name  woce_flag  woce_comment
	 */
	public static void main(String[] args) {
		if ( args.length != 6 ) {
			System.err.println("args:");
			System.err.println("    expocode  start_timestamp  end_timestamp  user_name  woce_flag  woce_comment");
			System.err.println("");
			System.err.println("timestamps are UTC with format: yyyy-MM-dd HH:mm:ss");
			System.err.println("After running, still need to decimate and notify ERDDAP");
			System.exit(1);
		}
		String expocode = args[0];
		String startTimestamp = args[1];
		String endTimestamp = args[2];
		String username = args[3];
		Character woceFlag = null;
		try {
			if ( args[4].length() != 1 )
				throw new IllegalArgumentException("not a single character");
			woceFlag = args[4].charAt(0);
			if ( ! (woceFlag.equals(DashboardUtils.WOCE_BAD) || 
					woceFlag.equals(DashboardUtils.WOCE_QUESTIONABLE)) )
				throw new IllegalArgumentException("unrecognized flag");
		} catch ( Exception ex ) {
			System.err.println("Problems with WOCE flag '" + args[4] + "': " + ex.getMessage());
			System.exit(1);
		}
		String woceComment = args[5];

		SimpleDateFormat timestamper = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timestamper.setTimeZone(TimeZone.getTimeZone("UTC"));
		double startTime = Double.NaN;
		double endTime = Double.NaN;
		try {
			startTime = timestamper.parse(startTimestamp).getTime() / 1000.0;
			endTime = timestamper.parse(endTimestamp).getTime() / 1000.0;
		} catch (ParseException ex) {
			System.err.println("Unexpected problems parsing timestamp");
			ex.printStackTrace();
			System.exit(1);
		}
		if ( endTime < startTime ) {
			System.err.println("end_timestamp before start_timestamp");
			System.exit(1);
		}

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (IOException ex) {
			System.err.println("Problem getting the default DashboardConfigStore");
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			CruiseDsgNcFile dsgFile = null;
			try {
				dsgFile = configStore.getDsgNcFileHandler().getDsgNcFile(expocode);
			} catch (Exception ex) {
				System.err.println("Problems getting the DSG file for " + expocode);
				ex.printStackTrace();
				System.exit(1);
			}
			char[] regionIDs = null;
			try {
				regionIDs = dsgFile.readCharVarDataValues(SocatTypes.REGION_ID.getVarName());
			} catch (Exception ex) {
				System.err.println("Problem reading the region IDs from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			double[] longitudes = null;
			double[] latitudes = null;
			double[] times = null;
			try {
				double[][] lonlattimes = dsgFile.readLonLatTimeDataValues();
				longitudes = lonlattimes[0];
				latitudes = lonlattimes[1];
				times = lonlattimes[2];
			} catch (Exception ex) {
				System.err.println("Problem reading longitudes/latitude/times from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			double[] fco2Rec = null;
			try {
				fco2Rec = dsgFile.readDoubleVarDataValues(SocatTypes.FCO2_REC.getVarName());
			} catch (Exception ex) {
				System.err.println("Problem reading fco2_recommended values from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			char[] woceFlags = null;
			try {
				woceFlags = dsgFile.readCharVarDataValues(SocatTypes.WOCE_CO2_WATER.getVarName());
			} catch (Exception ex) {
				System.err.println("Problem reading the WOCE flags from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			int numRows = woceFlags.length;

			// Create the WOCE event
			WoceEvent woceEvent = new WoceEvent();
			woceEvent.setComment(woceComment);
			woceEvent.setVarName(SocatTypes.FCO2_REC.getVarName());
			woceEvent.setExpocode(expocode);
			woceEvent.setFlag(woceFlag);
			woceEvent.setFlagDate(new Date());
			woceEvent.setVersion(configStore.getSocatQCVersion());
			woceEvent.setUsername(username);

			// Directly modify the data locations list in this event
			ArrayList<DataLocation> locList = woceEvent.getLocations();
			for (int rowIdx = 0; rowIdx < numRows; rowIdx++) {
				if ( times[rowIdx] < startTime )
					continue;
				if ( times[rowIdx] > endTime )
					break;
				DataLocation dataLoc = new DataLocation();
				dataLoc.setDataDate(new Date(Math.round(times[rowIdx] * 1000.0)));
				dataLoc.setDataValue(fco2Rec[rowIdx]);
				dataLoc.setLatitude(latitudes[rowIdx]);
				dataLoc.setLongitude(longitudes[rowIdx]);
				dataLoc.setRegionID(regionIDs[rowIdx]);
				dataLoc.setRowNumber(rowIdx+1);
				locList.add(dataLoc);
			}

			// Submit this WOCE Event to the database
			try {
				configStore.getDatabaseRequestHandler().addWoceEvent(woceEvent);
			} catch (SQLException ex) {
				System.err.println("Problem adding the WOCE event to the database");
				ex.printStackTrace();
				System.exit(1);
			}

			// Update the WOCE flags in the DSG file
			try {
				ArrayList<String> results = dsgFile.assignWoceFlags(woceEvent);
				if ( ! results.isEmpty() ) {
					System.err.println("Errors returned when assigning the WOCE flags in the DSG file");
					for ( String msg : results ) {
						System.err.println(msg);
					}
					System.exit(1);
				}
			} catch (Exception ex) {
				System.err.println("Problem assigning the WOCE flags in the DSG files");
				ex.printStackTrace();
				System.exit(1);
			}

			// TODO: redecimate - done by hand
			// TODO: flag ERDDAP - done by hand
		} finally {
			DashboardConfigStore.shutdown();
		}

		System.exit(0);
	}

}
