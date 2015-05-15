/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.RowNumSet;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

/**
 * Reports missing longitude, latitude, or time values in DSG files, 
 * which should not have any missing longitude, latitude, or time values.
 * 
 * @author Karl Smith
 */
public class ReportMissing {

	// Names of the variables in the DSG files
	private static final String LONGITUDE_NCVAR_NAME = Constants.SHORT_NAMES.get(Constants.longitude_VARNAME);
	private static final String LATITUDE_NCVAR_NAME = Constants.SHORT_NAMES.get(Constants.latitude_VARNAME);
	private static final String TIME_NCVAR_NAME = Constants.SHORT_NAMES.get(Constants.time_VARNAME);
	private static final String REGION_NCVAR_NAME = Constants.SHORT_NAMES.get(Constants.regionID_VARNAME);
	private static final double REL_TOLER = 1.0E-12;
	private static final double ABS_TOLER = 1.0E-6;

	/**
	 * Reports to System.out any missing longitudes, latitudes, times, and region IDs in the given DSG file.
	 * 
	 * @param dsgFile
	 * 		DSG file to examine
	 * @param expocode
	 * 		dataset expocode of this DSG file
	 * @param typeName
	 * 		type of this DSG file "full" or "dec." - for the messages 
	 * @throws IOException
	 * 		if there is a problem opening, or reading data from, the DSG file
	 * @throws IllegalArgumentException
	 * 		if the longitude, latitude, or time variables are not found in the DSG file
	 */
	private static void checkMissing(CruiseDsgNcFile dsgFile, String expocode, 
			String typeName) throws IOException, IllegalArgumentException {
		double[] longitudes = dsgFile.readDoubleVarDataValues(LONGITUDE_NCVAR_NAME);
		double[] latitudes = dsgFile.readDoubleVarDataValues(LATITUDE_NCVAR_NAME);
		double[] times = dsgFile.readDoubleVarDataValues(TIME_NCVAR_NAME);
		char[] regionIDs = dsgFile.readCharVarDataValues(REGION_NCVAR_NAME);
		int numObs = longitudes.length;
		if ( latitudes.length != numObs )
			throw new IllegalArgumentException("number of latitudes (" + 
					Integer.toString(latitudes.length) + 
					") does not match the number of longitudes (" + 
					Integer.toString(numObs) + ")");
		if ( times.length != numObs )
			throw new IllegalArgumentException("number of times (" + 
					Integer.toString(times.length) + 
					") does not match the number of longitudes (" + 
					Integer.toString(numObs) + ")");
		if ( regionIDs.length != numObs )
			throw new IllegalArgumentException("number of regionIDs (" + 
					Integer.toString(regionIDs.length) + 
					") does not match the number of longitudes (" + 
					Integer.toString(numObs) + ")");

		RowNumSet missingLons = new RowNumSet();
		for (int k = 0; k < numObs; k++)
			if ( DashboardUtils.closeTo(longitudes[k], SocatCruiseData.FP_MISSING_VALUE, REL_TOLER, ABS_TOLER) )
				missingLons.add(k+1);
		RowNumSet missingLats = new RowNumSet();
		for (int k = 0; k < numObs; k++)
			if ( DashboardUtils.closeTo(latitudes[k], SocatCruiseData.FP_MISSING_VALUE, REL_TOLER, ABS_TOLER) )
				missingLats.add(k+1);
		RowNumSet missingTimes = new RowNumSet();
		for (int k = 0; k < numObs; k++)
			if ( DashboardUtils.closeTo(times[k], SocatCruiseData.FP_MISSING_VALUE, REL_TOLER, ABS_TOLER) )
				missingTimes.add(k+1);
		RowNumSet missingRegionIDs = new RowNumSet();
		for (int k = 0; k < numObs; k++)
			if ( regionIDs[k] == ' ' )
				missingRegionIDs.add(k+1);

		if ( ! missingLons.isEmpty() )
			System.out.println(expocode + " " + typeName + " DSG: missing longitudes for rows " + 
					missingLons.toString());
		if ( ! missingLats.isEmpty() )
			System.out.println(expocode + " " + typeName + " DSG: missing latitudes for rows " + 
					missingLats.toString());
		if ( ! missingTimes.isEmpty() )
			System.out.println(expocode + " " + typeName + " DSG: missing times for rows " + 
					missingTimes.toString());
		if ( ! missingRegionIDs.isEmpty() )
			System.out.println(expocode + " " + typeName + " DSG: missing region IDs for rows " + 
					missingRegionIDs.toString());
	}

	/**
	 * @param args
	 * 		ExpocodesFile - report missing longitudes, latitudes, times, or 
	 * 						region IDs in these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println();
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Reports any missing longitudes, latitudes, times, or region IDs ");
			System.err.println("in DSG and decimated DSG files (which should not have any missing "); 
			System.err.println("longitudes, latitudes, times, or region IDs) for the indicated "); 
			System.err.println("cruises.  The default dashboard configuration is used for this ");
			System.err.println("process. ");
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			// Get the expocode of the cruises to update
			TreeSet<String> allExpocodes = new TreeSet<String>();
			try {
				BufferedReader expoReader = new BufferedReader(new FileReader(expocodesFilename));
				try {
					String dataline = expoReader.readLine();
					while ( dataline != null ) {
						dataline = dataline.trim();
						if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
							allExpocodes.add(dataline);
						dataline = expoReader.readLine();
					}
				} finally {
					expoReader.close();
				}
			} catch (Exception ex) {
				System.err.println("Error getting expocodes from " + 
						expocodesFilename + ": " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
			for ( String expocode : allExpocodes ) {
				try {
					CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
					checkMissing(dsgFile, expocode, "full");
				} catch (Exception ex) {
					System.err.println("Problems working with full DSG for " + expocode);
					ex.printStackTrace();
					success = false;
				}				
				try {
					CruiseDsgNcFile dsgFile = dsgHandler.getDecDsgNcFile(expocode);
					checkMissing(dsgFile, expocode, "dec.");
				} catch (Exception ex) {
					System.err.println("Problems working with decimated DSG for " + expocode);
					ex.printStackTrace();
					success = false;
				}				
			}
		} finally {
			configStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
