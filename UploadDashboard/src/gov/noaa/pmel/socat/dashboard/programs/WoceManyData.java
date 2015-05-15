/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

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
			if ( ! (woceFlag.equals(SocatWoceEvent.WOCE_BAD) || 
					woceFlag.equals(SocatWoceEvent.WOCE_QUESTIONABLE)) )
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
			configStore = DashboardConfigStore.get();
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
				regionIDs = dsgFile.readCharVarDataValues(Constants.SHORT_NAMES.get(Constants.regionID_VARNAME));
			} catch (Exception ex) {
				System.err.println("Problem reading the region IDs from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			double[] longitudes = null;
			try {
				longitudes = dsgFile.readDoubleVarDataValues(Constants.SHORT_NAMES.get(Constants.longitude_VARNAME));
			} catch (Exception ex) {
				System.err.println("Problem reading longitudes from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			double[] latitudes = null;
			try {
				latitudes = dsgFile.readDoubleVarDataValues(Constants.SHORT_NAMES.get(Constants.latitude_VARNAME));
			} catch (Exception ex) {
				System.err.println("Problem reading latitudes from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			double[] times = null;
			try {
				times = dsgFile.readDoubleVarDataValues(Constants.SHORT_NAMES.get(Constants.time_VARNAME));
			} catch (Exception ex) {
				System.err.println("Problem reading times from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			String dataVarName = Constants.SHORT_NAMES.get(Constants.fCO2Rec_VARNAME);
			double[] fco2Rec = null;
			try {
				fco2Rec = dsgFile.readDoubleVarDataValues(dataVarName);
			} catch (Exception ex) {
				System.err.println("Problem reading fco2_recommended values from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			char[] woceFlags = null;
			try {
				woceFlags = dsgFile.readCharVarDataValues(Constants.SHORT_NAMES.get(Constants.woceCO2Water_VARNAME));
			} catch (Exception ex) {
				System.err.println("Problem reading the WOCE flags from the DSG file");
				ex.printStackTrace();
				System.exit(1);
			}
			int numRows = woceFlags.length;

			// Create the WOCE event
			SocatWoceEvent woceEvent = new SocatWoceEvent();
			woceEvent.setComment(woceComment);
			woceEvent.setDataVarName(dataVarName);
			woceEvent.setExpocode(expocode);
			woceEvent.setFlag(woceFlag);
			woceEvent.setFlagDate(new Date());
			woceEvent.setSocatVersion(configStore.getSocatQCVersion());
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
			configStore.shutdown();
		}

		System.exit(0);
	}

}
