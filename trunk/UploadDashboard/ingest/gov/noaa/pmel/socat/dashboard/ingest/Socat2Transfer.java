/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.ingest;

import gov.noaa.pmel.socat.SocatCruises;
import gov.noaa.pmel.socat.SocatDataValues;
import gov.noaa.pmel.socat.SocatLogFiles;
import gov.noaa.pmel.socat.SocatMetaValues;
import gov.noaa.pmel.socat.SocatUtils;
import gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * @author Karl Smith
 */
public class Socat2Transfer {

	SocatLogFiles logFiles;
	SocatCruises socat2Cruises;
	SimpleDateFormat timestamper;
	CruiseFileHandler cruiseHandler;
	MetadataFileHandler metadataHandler;

	/**
	 * @param catConn
	 * 		connection to a SOCAT v2 database
	 * @param logFiles
	 * 		log messages here
	 * @throws SQLException
	 * 		if unable to create a PreparedStatement using this connection 
	 * @throws IOException 
	 * 		if unable to obtain the Dashboard data store
	 */
	public Socat2Transfer(Connection catConn, SocatLogFiles logFiles) 
										throws SQLException, IOException {
		this.logFiles = logFiles;
		socat2Cruises = new SocatCruises(catConn, "metadata", "data", "WOCE_flags");
		timestamper = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		DashboardDataStore dataStore = DashboardDataStore.get();
		cruiseHandler = dataStore.getCruiseFileHandler();
		metadataHandler = dataStore.getMetadataFileHandler();
	}

	/**
	 * The cruise with the given expocode is read from the v2 database and 
	 * is added to the SocatUploadDashboard as if its data and metadata files 
	 * had been uploaded into the dashboard by the given owner. 
	 * 
	 * @param expocode
	 * 		expocode of the cruise to transfer
	 * @param owner
	 * 		owner of this cruise
	 * @return 
	 * 		the cruise QC flag, or null if there was an error
	 * @throws SQLException
	 * 		if reading from the v2 database throws one
	 * @throws IllegalArgumentException 
	 * 		if there are problems saving the dashboard files
	 */
	public String transferV2DataToDashboard(String expocode, String owner) 
							throws SQLException, IllegalArgumentException {
		// Read the cruise data from the v2 database
		SocatMetaValues socat2Metadata;
		try {
			TreeSet<String> expocodesSet = new TreeSet<String>(Arrays.asList(expocode));
			socat2Metadata = socat2Cruises.readMetadata(expocodesSet, logFiles).first();
		} catch (Exception ex) {
			logFiles.logToSummary("Problems reading the v2 metadata for " + expocode);
			return null;
		}
		ArrayList<SocatDataValues> socat2DataList = socat2Cruises.getDatabaseData(socat2Metadata, logFiles);
		if ( socat2DataList.size() < 1 ) {
			logFiles.logToSummary("No data values read for " + expocode);
			return null;
		}

		String timestamp = timestamper.format(new Date());

		// Generate the minimal OmeMetadata from the v2 metadata
		OmeMetadata cruiseMetadata = new OmeMetadata();
		cruiseMetadata.setExpocode(expocode);
		cruiseMetadata.setOwner(owner);
		cruiseMetadata.setUploadTimestamp(timestamp);
		cruiseMetadata.setCruiseName(socat2Metadata.getCruiseName());
		cruiseMetadata.setVesselName(socat2Metadata.getVesselName());
		cruiseMetadata.setScienceGroup(socat2Metadata.getScienceGroup());
		cruiseMetadata.setOrigDataRef(socat2Metadata.getOrigDOI());
		String qcFlag = socat2Metadata.getFlag();

		// Generate the DashboardCruiseWithData from the v2 data
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		cruiseData.setVersion("2");
		cruiseData.setExpocode(expocode);
		cruiseData.setOwner(owner);
		cruiseData.setUploadFilename(expocode + "_from_v2.tsv");
		cruiseData.setUploadTimestamp(timestamp);
		cruiseData.setOmeTimestamp(timestamp);

		boolean hasSeconds = false;
		boolean hasDepth = false;
		boolean hasSalinity = false;
		boolean hasSst = false;
		boolean hasTEqu = false;
		boolean hasSlp = false;
		boolean hasPEqu = false;
		boolean hasxCO2WaterSst = false;
		boolean hasxCO2WaterTEqu = false;
		boolean haspCO2WaterSst = false;
		boolean haspCO2WaterTEqu = false;
		boolean hasfCO2WaterSst = false;
		boolean hasfCO2WaterTEqu = false;
		boolean hasHumidity = false;
		boolean hasxCO2Air = false;
		boolean haspCO2Air = false;
		boolean hasfCO2Air = false;
		boolean hasShipSpeed = false;
		boolean hasShipDir = false;
		boolean hasWindSpeedTrue = false;
		boolean hasWindSpeedRel = false;
		boolean hasWindDirTrue = false;
		boolean hasWindDirRel = false;
		for ( SocatDataValues dataVals : socat2DataList ) {
			if ( dataVals.getSSWasGiven() )
				hasSeconds = true;
			if ( ! dataVals.getDepth().isNaN() )
				hasDepth = true;
			if ( ! dataVals.getSal().isNaN() )
				hasSalinity = true;
			if ( ! dataVals.getTemp().isNaN() )
				hasSst = true;
			if ( ! dataVals.getTemperature_equi().isNaN() )
				hasTEqu = true;
			if ( ! dataVals.getPressure_atm().isNaN() )
				hasSlp = true;
			if ( ! dataVals.getPressure_equi().isNaN() )
				hasPEqu = true;
			if ( ! dataVals.getxCO2_water_sst_dry_ppm().isNaN() )
				hasxCO2WaterSst = true;
			if ( ! dataVals.getxCO2_water_equi_temp_dry_ppm().isNaN() )
				hasxCO2WaterTEqu = true;
			if ( ! dataVals.getpCO2_water_sst_100humidity_uatm().isNaN() )
				haspCO2WaterSst = true;
			if ( ! dataVals.getpCO2_water_equi_temp().isNaN() )
				haspCO2WaterTEqu = true;
			if ( ! dataVals.getfCO2_water_sst_100humidity_uatm().isNaN() )
				hasfCO2WaterSst = true;
			if ( ! dataVals.getfCO2_water_equi_uatm().isNaN() )
				hasfCO2WaterTEqu = true;
			if ( ! dataVals.getHumidity().isNaN() )
				hasHumidity = true;
			if ( ! dataVals.getxCO2_atm().isNaN() )
				hasxCO2Air = true;
			if ( ! dataVals.getpCO2_atm().isNaN() )
				haspCO2Air = true;
			if ( ! dataVals.getfCO2_atm().isNaN() )
				hasfCO2Air = true;
			if ( ! dataVals.getShip_speed().isNaN() )
				hasShipSpeed = true;
			if ( ! dataVals.getShip_direc().isNaN() )
				hasShipDir = true;
			if ( ! dataVals.getWind_speed_true().isNaN() )
				hasWindSpeedTrue = true;
			if ( ! dataVals.getWind_speed_rel().isNaN() )
				hasWindSpeedRel = true;
			if ( ! dataVals.getWind_direc_true().isNaN() )
				hasWindDirTrue = true;
			if ( ! dataVals.getWind_direc_rel().isNaN() )
				hasWindDirRel = true;
		}

		// Directly assign the lists given in the DashboardCruiseWithData
		cruiseData.getPreamble().addAll(Arrays.asList(
				"Cruise Expocode = " + expocode,
				"Generated from SOCAT v2 data on " + timestamp));

		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();
		columnTypes.add(DataColumnType.YEAR);
		columnTypes.add(DataColumnType.MONTH);
		columnTypes.add(DataColumnType.DAY);
		columnTypes.add(DataColumnType.HOUR);
		columnTypes.add(DataColumnType.MINUTE);
		if ( hasSeconds )
			columnTypes.add(DataColumnType.SECOND);
		columnTypes.add(DataColumnType.LONGITUDE);
		columnTypes.add(DataColumnType.LATITUDE);
		if ( hasDepth )
			columnTypes.add(DataColumnType.SAMPLE_DEPTH);
		if ( hasSalinity )
			columnTypes.add(DataColumnType.SALINITY);
		if ( hasSst )
			columnTypes.add(DataColumnType.SEA_SURFACE_TEMPERATURE);
		if ( hasTEqu )
			columnTypes.add(DataColumnType.EQUILIBRATOR_TEMPERATURE);
		if ( hasSlp )
			columnTypes.add(DataColumnType.SEA_LEVEL_PRESSURE);
		if ( hasPEqu )
			columnTypes.add(DataColumnType.EQUILIBRATOR_PRESSURE);
		if ( hasxCO2WaterSst )
			columnTypes.add(DataColumnType.XCO2WATER_SST);
		if ( hasxCO2WaterTEqu )
			columnTypes.add(DataColumnType.XCO2WATER_EQU);
		if ( haspCO2WaterSst )
			columnTypes.add(DataColumnType.PCO2WATER_SST);
		if ( haspCO2WaterTEqu )
			columnTypes.add(DataColumnType.PCO2WATER_EQU);
		if ( hasfCO2WaterSst )
			columnTypes.add(DataColumnType.FCO2WATER_SST);
		if ( hasfCO2WaterTEqu )
			columnTypes.add(DataColumnType.FCO2WATER_EQU);
		if ( hasHumidity )
			columnTypes.add(DataColumnType.HUMIDITY);
		if ( hasxCO2Air )
			columnTypes.add(DataColumnType.XCO2AIR);
		if ( haspCO2Air )
			columnTypes.add(DataColumnType.PCO2AIR);
		if ( hasfCO2Air )
			columnTypes.add(DataColumnType.FCO2AIR);
		if ( hasShipSpeed )
			columnTypes.add(DataColumnType.SHIP_SPEED);
		if ( hasShipDir )
			columnTypes.add(DataColumnType.SHIP_DIRECTION);
		if ( hasWindSpeedTrue )
			columnTypes.add(DataColumnType.WIND_SPEED_TRUE);
		if ( hasWindSpeedRel )
			columnTypes.add(DataColumnType.WIND_SPEED_RELATIVE);
		if ( hasWindDirTrue )
			columnTypes.add(DataColumnType.WIND_DIRECTION_TRUE);
		if ( hasWindDirRel )
			columnTypes.add(DataColumnType.WIND_DIRECTION_RELATIVE);
		columnTypes.add(DataColumnType.OVERALL_WOCE);

		ArrayList<String> columnNames = cruiseData.getUserColNames();
		ArrayList<String> columnUnits = cruiseData.getDataColUnits();
		ArrayList<String> missValues = cruiseData.getMissingValues();
		ArrayList<HashSet<Integer>> woceThreeRows = cruiseData.getWoceThreeRowIndices();
		ArrayList<HashSet<Integer>> woceFourRows = cruiseData.getWoceFourRowIndices();
		for ( DataColumnType type : columnTypes ) {
			columnNames.add(DashboardUtils.STD_HEADER_NAMES.get(type));
			columnUnits.add(DashboardUtils.STD_DATA_UNITS.get(type).get(0));
			missValues.add("");
			woceThreeRows.add(new HashSet<Integer>());
			woceFourRows.add(new HashSet<Integer>());
		}

		ArrayList<ArrayList<String>> dataLists = cruiseData.getDataValues();
		for ( SocatDataValues dataVals : socat2DataList ) {
			ArrayList<String> thisData = new ArrayList<String>(columnTypes.size());
			for ( DataColumnType type : columnTypes ) {
				if ( type.equals(DataColumnType.YEAR) ) {
					thisData.add(stringFromSocat2Int(dataVals.getYr()));
				}
				else if ( type.equals(DataColumnType.MONTH) ) {
					thisData.add(stringFromSocat2Int(dataVals.getMon()));
				}
				else if ( type.equals(DataColumnType.DAY) ) {
					thisData.add(stringFromSocat2Int(dataVals.getDay()));
				}
				else if ( type.equals(DataColumnType.HOUR) ) {
					thisData.add(stringFromSocat2Int(dataVals.getHh()));
				}
				else if ( type.equals(DataColumnType.MINUTE) ) {
					thisData.add(stringFromSocat2Int(dataVals.getMm()));
				}
				else if ( type.equals(DataColumnType.SECOND) ) {
					thisData.add(stringFromSocat2Double(dataVals.getSs()));
				}
				else if ( type.equals(DataColumnType.LONGITUDE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getLongitude()));
				}
				else if ( type.equals(DataColumnType.LATITUDE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getLatitude()));
				}
				else if ( type.equals(DataColumnType.SAMPLE_DEPTH) ) {
					thisData.add(stringFromSocat2Double(dataVals.getDepth()));
				}
				else if ( type.equals(DataColumnType.SALINITY) ) {
					thisData.add(stringFromSocat2Double(dataVals.getSal()));
				}
				else if ( type.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getTemp()));
				}
				else if ( type.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getTemperature_equi()));
				}
				else if ( type.equals(DataColumnType.SEA_LEVEL_PRESSURE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getPressure_atm()));
				}
				else if ( type.equals(DataColumnType.EQUILIBRATOR_PRESSURE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getPressure_equi()));
				}
				else if ( type.equals(DataColumnType.XCO2WATER_SST) ) {
					thisData.add(stringFromSocat2Double(dataVals.getxCO2_water_sst_dry_ppm()));
				}
				else if ( type.equals(DataColumnType.XCO2WATER_EQU) ) {
					thisData.add(stringFromSocat2Double(dataVals.getxCO2_water_equi_temp_dry_ppm()));
				}
				else if ( type.equals(DataColumnType.PCO2WATER_SST) ) {
					thisData.add(stringFromSocat2Double(dataVals.getpCO2_water_sst_100humidity_uatm()));
				}
				else if ( type.equals(DataColumnType.PCO2WATER_EQU) ) {
					thisData.add(stringFromSocat2Double(dataVals.getpCO2_water_equi_temp()));
				}
				else if ( type.equals(DataColumnType.FCO2WATER_SST) ) {
					thisData.add(stringFromSocat2Double(dataVals.getfCO2_water_sst_100humidity_uatm()));
				}
				else if ( type.equals(DataColumnType.FCO2WATER_EQU)) {
					thisData.add(stringFromSocat2Double(dataVals.getfCO2_water_equi_uatm()));
				}
				else if ( type.equals(DataColumnType.HUMIDITY) ) {
					thisData.add(stringFromSocat2Double(dataVals.getHumidity()));
				}
				else if ( type.equals(DataColumnType.XCO2AIR) ) {
					thisData.add(stringFromSocat2Double(dataVals.getxCO2_atm()));
				}
				else if ( type.equals(DataColumnType.PCO2AIR) ) {
					thisData.add(stringFromSocat2Double(dataVals.getpCO2_atm()));
				}
				else if ( type.equals(DataColumnType.FCO2AIR) ) {
					thisData.add(stringFromSocat2Double(dataVals.getfCO2_atm()));
				}
				else if ( type.equals(DataColumnType.SHIP_SPEED) ) {
					thisData.add(stringFromSocat2Double(dataVals.getShip_speed()));
				}
				else if ( type.equals(DataColumnType.SHIP_DIRECTION) ) {
					thisData.add(stringFromSocat2Double(dataVals.getShip_direc()));
				}
				else if ( type.equals(DataColumnType.WIND_SPEED_TRUE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getWind_speed_true()));
				}
				else if ( type.equals(DataColumnType.WIND_SPEED_RELATIVE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getWind_speed_rel()));
				}
				else if ( type.equals(DataColumnType.WIND_DIRECTION_TRUE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getWind_direc_true()));
				}
				else if ( type.equals(DataColumnType.WIND_DIRECTION_RELATIVE) ) {
					thisData.add(stringFromSocat2Double(dataVals.getWind_direc_rel()));
				}
				else if ( type.equals(DataColumnType.OVERALL_WOCE) ) {
					short woceFlag = dataVals.getWoceFlag();
					if ( woceFlag == 0 ) {
						thisData.add("");
					}
					else {
						thisData.add(Short.toString(woceFlag));
					}
				}
				else {
					throw new RuntimeException("Unexpected data column type of " + type.name());
				}
			}
			dataLists.add(thisData);
		}
		cruiseData.setNumDataRows(dataLists.size());

		// Save the cruise data and metadata to the dashboard repository
		String commitMessage = "Cruise added from SOCAT v2 database on " + timestamp; 
		cruiseHandler.saveCruiseInfoToFile(cruiseData, commitMessage);
		cruiseHandler.saveCruiseDataToFile(cruiseData, commitMessage);
		metadataHandler.saveMetadataInfo(cruiseMetadata, commitMessage);
		metadataHandler.saveAsMinimalOmeXmlDoc(cruiseMetadata, commitMessage);

		return qcFlag;
	}

	/**
	 * Returns a string representation of a Integer value from the SOCAT v2
	 * database.  If the value is null or -1, an empty string is returned.
	 *   
	 * @param socat2Val
	 * 		SOCAT v2 value to be used
	 * @return
	 * 		string representation of the value
	 */
	public static String stringFromSocat2Int(Integer socat2Val) {
		String valStr;
		if ( (socat2Val == null) || (socat2Val == -1) )
			valStr = "";
		else
			valStr = socat2Val.toString();
		return valStr;
	}

	/**
	 * Returns a string representation of a Double value from the SOCAT v2
	 * database.  If the value is null, NaN, or infinite, an empty string
	 * is returned.
	 *   
	 * @param socat2Val
	 * 		SOCAT v2 value to be used
	 * @return
	 * 		string representation of the value
	 */
	public static String stringFromSocat2Double(Double socat2Val) {
		String valStr;
		if ( (socat2Val == null) || socat2Val.isNaN() || socat2Val.isInfinite() )
			valStr = "";
		else
			valStr = String.format("%#.6f", socat2Val);
		return valStr;
	}

	/**
	 * @param args
	 * 		catalog  username  expocode  owner  logdir
	 * 
	 * The cruise with the given expocode is read from the v2 database and 
	 * is added to the SocatUploadDashboard as if its data and metadata files 
	 * had been uploaded into the dashboard by the given owner. 
	 * 
	 * You will be prompted for the database user password.  Results are 
	 * logged to files in logdir, a directory which will be created.
	 */
	public static void main(String[] args) {
		if ( args.length != 5 ) {
			System.err.println("\n" +
				"Arguments:  catalog  username  expocode  owner logdir \n" +
				"\n" +
				"The cruise with the given expocode is read from the v2 database and \n" + 
				"is added to the SocatUploadDashboard as if its data and metadata files \n" + 
				"had been uploaded into the dashboard by the given owner. \n" + 
				"\n" +
				"You will be prompted for the database user password.  Results are \n" +
				"logged to files in logdir, a directory which will be created. \n");
			System.exit(1);
		}

		String catalog = args[0];
		String username = args[1];
		String expocode = args[2];
		String owner = args[3];
		File logDir = new File(args[4]);

		SocatLogFiles logFiles = null;
		try {
			logFiles = new SocatLogFiles(logDir);
		} catch (IllegalArgumentException ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		} catch (FileNotFoundException ex) {
			System.err.println("Problems opening one of the standard log file: " + ex.getMessage());
			System.exit(1);
		}

		Connection catConn = null;
		try {
			catConn = SocatUtils.createSocatConnection(catalog, username);
			if ( catConn == null ) {
				// Error message already printed to System.err
				System.exit(1);
			}

			try {
				Socat2Transfer transfer = new Socat2Transfer(catConn, logFiles);
				String qcFlag = transfer.transferV2DataToDashboard(expocode, owner);
				if ( qcFlag == null ) {
					// Error message already printed to log files
					System.exit(1);
				}
			} catch (SQLException ex) {
				logFiles.logToSummary(ex.getMessage());
				logFiles.logToSummary("SQLException raised in Socat2Transfer for catalog " + catalog);
				System.exit(1);
			} catch (Exception ex) {
				logFiles.logStackTrace(ex);
				logFiles.logToSummary("Unexpected Exception raised");
				System.exit(1);
			}

		} finally {
			if ( catConn != null ) {
				try {
					catConn.close();
				} catch (SQLException ex) {
					// ignore
					;
				}
			}
			logFiles.closeLogFiles();
		}
		System.exit(0);
	}

}
