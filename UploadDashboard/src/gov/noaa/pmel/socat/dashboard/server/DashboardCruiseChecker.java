/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;

import uk.ac.uea.socat.sanitychecker.Message;
import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.SanityChecker;
import uk.ac.uea.socat.sanitychecker.config.BaseConfig;
import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfig;
import uk.ac.uea.socat.sanitychecker.config.SanityCheckConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.InvalidColumnSpecException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

/**
 * Class for interfacing with the SanityChecker 
 * 
 * @author Karl Smith
 */
public class DashboardCruiseChecker {

	/**
	 * Indices of user-provided data columns that could have WOCE flags. 
	 */
	class ColumnIndices {
		int timestampIndex = -1;
		int dateIndex = -1;
		int yearIndex = -1;
		int monthIndex = -1;
		int dayIndex = -1;
		int timeIndex = -1;
		int hourIndex = -1;
		int minuteIndex = -1;
		int secondIndex = -1;
		int dayOfYearIndex = -1;
		int secondOfDayIndex = -1;
		int longitudeIndex = -1;
		int latitudeIndex = -1;
		int sampleDepthIndex = -1;
		int salinityIndex = -1;
		int tEquIndex = -1;
		int sstIndex = -1;
		int tAtmIndex = -1;
		int pEquIndex = -1;
		int slpIndex = -1;
		int xH2OEquIndex = -1;
		int xCO2WaterTEquDryIndex = -1;
		int xCO2WaterSstDryIndex = -1;
		int xCO2WaterTEquWetIndex = -1;
		int xCO2WaterSstWetIndex = -1;
		int pCO2WaterTEquWetIndex = -1;
		int pCO2WaterSstWetIndex = -1;
		int fCO2WaterTEquWetIndex = -1;
		int fCO2WaterSstWetIndex = -1;
		int xCO2AtmDryActualIndex = -1;
		int xCO2AtmDryInterpIndex = -1;
		int pCO2AtmDryActualIndex = -1;
		int pCO2AtmDryInterpIndex = -1;
		int fCO2AtmDryActualIndex = -1;
		int fCO2AtmDryInterpIndex = -1;
		int deltaXCO2Index = -1;
		int deltaPCO2Index = -1;
		int deltaFCO2Index = -1;
		int relativeHumidityIndex = -1;
		int specificHumidityIndex = -1;
		int shipSpeedIndex = -1;
		int shipDirIndex = -1;
		int windSpeedTrueIndex = -1;
		int windSpeedRelIndex = -1;
		int windDirTrueIndex = -1;
		int windDirRelIndex = -1;
	}

	enum DateTimeType {
		DATETIME_TIMESTAMP,
		DATETIME_DATE_TIME,
		DATETIME_YEAR_DAY_SEC,
		DATETIME_YEAR_DECIMAL_DAY,
		DATETIME_YEAR_MON_DAY_HR_MIN_SEC,
		DATETIME_YEAR_MON_DAY_TIME,
	}

	/**
	 * Data units used by the sanity checker corresponding to {@link #STD_DATA_UNITS}
	 */
	public static final EnumMap<DataColumnType,ArrayList<String>> CHECKER_DATA_UNITS = 
			new EnumMap<DataColumnType,ArrayList<String>>(DataColumnType.class);
	static {
		final ArrayList<String> checkerTimestampDateUnits = 
				new ArrayList<String>(DashboardUtils.TIMESTAMP_UNITS.size());
		for ( String fmt : DashboardUtils.TIMESTAMP_UNITS ) 
			checkerTimestampDateUnits.add(fmt.split(" ", 2)[0]);
		final ArrayList<String> checkerLongitudeUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees"));
		final ArrayList<String> checkerLatitudeUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees"));
		final ArrayList<String> checkerSalinityUnits = 
				new ArrayList<String>(Arrays.asList("psu"));
		final ArrayList<String> checkerTemperatureUnits = 
				new ArrayList<String>(Arrays.asList("degC"));
		final ArrayList<String> checkerXCO2Units = 
				new ArrayList<String>(Arrays.asList("ppm"));
		final ArrayList<String> checkerDirectionUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees"));

		// UNKNOWN should not be processed by the sanity checker

		CHECKER_DATA_UNITS.put(DataColumnType.EXPOCODE, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.CRUISE_NAME, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SHIP_NAME, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.GROUP_NAME, DashboardUtils.NO_UNITS);

		CHECKER_DATA_UNITS.put(DataColumnType.TIMESTAMP, checkerTimestampDateUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.DATE, DashboardUtils.DATE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.YEAR, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.MONTH, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.DAY, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.TIME, DashboardUtils.TIME_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.HOUR, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.MINUTE, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SECOND, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.DAY_OF_YEAR, DashboardUtils.DAY_OF_YEAR_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SECOND_OF_DAY, DashboardUtils.NO_UNITS);

		CHECKER_DATA_UNITS.put(DataColumnType.LONGITUDE, checkerLongitudeUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.LATITUDE, checkerLatitudeUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH, DashboardUtils.DEPTH_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SALINITY, checkerSalinityUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.ATMOSPHERIC_TEMPERATURE, checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE, DashboardUtils.PRESSURE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE, DashboardUtils.PRESSURE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.XH2O_EQU, checkerXCO2Units);

		CHECKER_DATA_UNITS.put(DataColumnType.XCO2_WATER_TEQU_DRY, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2_WATER_SST_DRY, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2_WATER_TEQU_WET, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2_WATER_SST_WET, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2_WATER_TEQU_WET, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2_WATER_SST_WET, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2_WATER_TEQU_WET, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2_WATER_SST_WET, DashboardUtils.PCO2_UNITS);

		CHECKER_DATA_UNITS.put(DataColumnType.XCO2_ATM_DRY_ACTUAL, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2_ATM_DRY_INTERP, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2_ATM_DRY_ACTUAL, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2_ATM_DRY_INTERP, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2_ATM_DRY_ACTUAL, DashboardUtils.FCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2_ATM_DRY_INTERP, DashboardUtils.FCO2_UNITS);

		CHECKER_DATA_UNITS.put(DataColumnType.DELTA_XCO2, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(DataColumnType.DELTA_PCO2, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.DELTA_FCO2, DashboardUtils.FCO2_UNITS);

		CHECKER_DATA_UNITS.put(DataColumnType.RELATIVE_HUMIDITY, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SPECIFIC_HUMIDITY, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SHIP_SPEED, DashboardUtils.SHIP_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SHIP_DIRECTION, checkerDirectionUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_SPEED_TRUE, DashboardUtils.WIND_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_SPEED_RELATIVE, DashboardUtils.WIND_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_TRUE, checkerDirectionUnits);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_RELATIVE, checkerDirectionUnits);

		CHECKER_DATA_UNITS.put(DataColumnType.WOCE_CO2_WATER, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.WOCE_CO2_ATM, DashboardUtils.NO_UNITS);

		// COMMENT... and OTHER should not be processed by the sanity checker
	}

	private boolean lastCruiseCheckHadGeopositionErrors;

	/**
	 * Initializes the SanityChecker using the configuration files names
	 * in the given properties files.
	 * 
	 * @param configFile
	 * 		properties file giving the names of the configuration files 
	 * 		for each SanityChecker component
	 * @throws IOException
	 * 		If the SanityChecker has problems with a configuration file
	 */
	public DashboardCruiseChecker(File configFile) throws IOException {
		try {
			// Clear any previous configuration
			SanityCheckConfig.destroy();
			SocatColumnConfig.destroy();
			ColumnConversionConfig.destroy();
			MetadataConfig.destroy();
			BaseConfig.destroy();
			// Initialize the SanityChecker from the configuration file
			SanityChecker.initConfig(configFile.getAbsolutePath());
		} catch ( Exception ex ) {
			throw new IOException("Invalid SanityChecker configuration" + 
					" values specified in " + configFile.getPath() + 
					"\n    " + ex.getMessage());
		}
		lastCruiseCheckHadGeopositionErrors = false;
	}

	/**
	 * Runs the SanityChecker on the given cruise.  
	 * Assigns the data check status and the WOCE-3 and WOCE-4 
	 * data flags from the SanityChecker output.
	 * 
	 * @param cruiseData
	 * 		cruise to check
	 * @return
	 * 		output from the SanityChecker.
	 * @throws IllegalArgumentException
	 * 		if a data column type is unknown, or
	 * 		if the sanity checker throws an exception
	 */
	public Output checkCruise(DashboardCruiseWithData cruiseData) 
											throws IllegalArgumentException {
		// Create the metadata properties of this cruise for the sanity checker
		Properties metadataInput = new Properties();
		metadataInput.setProperty("EXPOCode", cruiseData.getExpocode());

		// Get the data column units conversion object
		ColumnConversionConfig convConfig;
		try {
			convConfig = ColumnConversionConfig.getInstance();
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Unexpected ColumnConversionConfig exception: " + 
							ex.getMessage());
		}

		// Specify the default date format used in this cruise
		String dateFormat = "YYYY-MM-DD";

		// Save indices of data columns for assigning WOCE flags 
		ColumnIndices colIndcs = new ColumnIndices();

		// determine how the date/time is specified - in the process assign colIndcs
		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( colType.equals(DataColumnType.TIMESTAMP) )
				colIndcs.timestampIndex = k;
			else if ( colType.equals(DataColumnType.DATE) )
				colIndcs.dateIndex = k;
			else if ( colType.equals(DataColumnType.YEAR) )
				colIndcs.yearIndex = k;
			else if ( colType.equals(DataColumnType.MONTH) )
				colIndcs.monthIndex = k;
			else if ( colType.equals(DataColumnType.DAY) )
				colIndcs.dayIndex = k;
			else if ( colType.equals(DataColumnType.TIME) )
				colIndcs.timeIndex = k;
			else if ( colType.equals(DataColumnType.HOUR) )
				colIndcs.hourIndex = k;
			else if ( colType.equals(DataColumnType.MINUTE) )
				colIndcs.minuteIndex = k;
			else if ( colType.equals(DataColumnType.SECOND) )
				colIndcs.secondIndex = k;
			else if ( colType.equals(DataColumnType.DAY_OF_YEAR) )
				colIndcs.dayOfYearIndex = k;
			else if ( colType.equals(DataColumnType.SECOND_OF_DAY) )
				colIndcs.secondOfDayIndex = k;

			else if ( colType.equals(DataColumnType.LONGITUDE) )
				colIndcs.longitudeIndex = k;
			else if ( colType.equals(DataColumnType.LATITUDE) )
				colIndcs.latitudeIndex = k;
			else if ( colType.equals(DataColumnType.SAMPLE_DEPTH) )
				colIndcs.sampleDepthIndex = k;
			else if ( colType.equals(DataColumnType.SALINITY) )
				colIndcs.salinityIndex = k;
			else if ( colType.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) )
				colIndcs.tEquIndex = k;
			else if ( colType.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) )
				colIndcs.sstIndex = k;
			else if ( colType.equals(DataColumnType.ATMOSPHERIC_TEMPERATURE) )
				colIndcs.tAtmIndex = k;
			else if ( colType.equals(DataColumnType.EQUILIBRATOR_PRESSURE) )
				colIndcs.pEquIndex = k;
			else if ( colType.equals(DataColumnType.SEA_LEVEL_PRESSURE) )
				colIndcs.slpIndex = k;

			else if ( colType.equals(DataColumnType.XCO2_WATER_TEQU_DRY) )
				colIndcs.xCO2WaterTEquDryIndex = k;
			else if ( colType.equals(DataColumnType.XCO2_WATER_SST_DRY) )
				colIndcs.xCO2WaterSstDryIndex = k;
			else if ( colType.equals(DataColumnType.XCO2_WATER_TEQU_WET) )
				colIndcs.xCO2WaterTEquWetIndex = k;
			else if ( colType.equals(DataColumnType.XCO2_WATER_SST_WET) )
				colIndcs.xCO2WaterSstWetIndex = k;
			else if ( colType.equals(DataColumnType.PCO2_WATER_TEQU_WET) )
				colIndcs.pCO2WaterTEquWetIndex = k;
			else if ( colType.equals(DataColumnType.PCO2_WATER_SST_WET) )
				colIndcs.pCO2WaterSstWetIndex = k;
			else if ( colType.equals(DataColumnType.FCO2_WATER_TEQU_WET) )
				colIndcs.fCO2WaterTEquWetIndex = k;
			else if ( colType.equals(DataColumnType.FCO2_WATER_SST_WET) )
				colIndcs.fCO2WaterSstWetIndex = k;

			else if ( colType.equals(DataColumnType.XCO2_ATM_DRY_ACTUAL) )
				colIndcs.xCO2AtmDryActualIndex = k;
			else if ( colType.equals(DataColumnType.XCO2_ATM_DRY_INTERP) )
				colIndcs.xCO2AtmDryInterpIndex = k;
			else if ( colType.equals(DataColumnType.PCO2_ATM_DRY_ACTUAL) )
				colIndcs.pCO2AtmDryActualIndex = k;
			else if ( colType.equals(DataColumnType.PCO2_ATM_DRY_INTERP) )
				colIndcs.pCO2AtmDryInterpIndex = k;
			else if ( colType.equals(DataColumnType.FCO2_ATM_DRY_ACTUAL) )
				colIndcs.fCO2AtmDryActualIndex = k;
			else if ( colType.equals(DataColumnType.FCO2_ATM_DRY_INTERP) )
				colIndcs.fCO2AtmDryInterpIndex = k;

			else if ( colType.equals(DataColumnType.DELTA_XCO2) )
				colIndcs.deltaXCO2Index = k;
			else if ( colType.equals(DataColumnType.DELTA_PCO2) )
				colIndcs.deltaPCO2Index = k;
			else if ( colType.equals(DataColumnType.DELTA_FCO2) )
				colIndcs.deltaFCO2Index = k;

			else if ( colType.equals(DataColumnType.XH2O_EQU) )
				colIndcs.xH2OEquIndex = k;
			else if ( colType.equals(DataColumnType.RELATIVE_HUMIDITY) )
				colIndcs.relativeHumidityIndex = k;
			else if ( colType.equals(DataColumnType.SPECIFIC_HUMIDITY) )
				colIndcs.specificHumidityIndex = k;
			else if ( colType.equals(DataColumnType.SHIP_SPEED) )
				colIndcs.shipSpeedIndex = k;
			else if ( colType.equals(DataColumnType.SHIP_DIRECTION) )
				colIndcs.shipDirIndex = k;
			else if ( colType.equals(DataColumnType.WIND_SPEED_TRUE) )
				colIndcs.windSpeedTrueIndex = k;
			else if ( colType.equals(DataColumnType.WIND_SPEED_RELATIVE) )
				colIndcs.windSpeedRelIndex = k;
			else if ( colType.equals(DataColumnType.WIND_DIRECTION_TRUE) )
				colIndcs.windDirTrueIndex = k;
			else if ( colType.equals(DataColumnType.WIND_DIRECTION_RELATIVE) )
				colIndcs.windDirRelIndex = k;
		}

		// Decide where to get the date and time for each measurement
		DateTimeType timeSpec;
		if ( (colIndcs.yearIndex >= 0) && (colIndcs.monthIndex >= 0) && 
			 (colIndcs.dayIndex >= 0) && (colIndcs.hourIndex >= 0) &&
			 (colIndcs.minuteIndex >= 0) ) {
			timeSpec = DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC;
		}
		else if ( (colIndcs.yearIndex >= 0) && (colIndcs.monthIndex >= 0) && 
			 (colIndcs.dayIndex >= 0) && (colIndcs.timeIndex >= 0) ) {
			timeSpec = DateTimeType.DATETIME_YEAR_MON_DAY_TIME;
		}
		else if ( (colIndcs.yearIndex >= 0) && (colIndcs.dayOfYearIndex >= 0) ) {
			if ( colIndcs.secondOfDayIndex >= 0 )
				timeSpec = DateTimeType.DATETIME_YEAR_DAY_SEC;
			else
				timeSpec = DateTimeType.DATETIME_YEAR_DECIMAL_DAY;
		}
		else if ( (colIndcs.dateIndex >= 0) && (colIndcs.timeIndex >= 0) ) {
			timeSpec = DateTimeType.DATETIME_DATE_TIME;
		}
		else if ( colIndcs.timestampIndex >= 0 ) {
			timeSpec = DateTimeType.DATETIME_TIMESTAMP;
		}
		else 
			throw new IllegalArgumentException("The date and/or time of each " +
					"measurement is not completely specified in the data columns");

		// Specify the columns in this cruise data
		Element rootElement = new Element("Expocode_" + cruiseData.getExpocode());
		Element timestampElement = new Element(ColumnSpec.DATE_COLUMN_ELEMENT);
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( colType.equals(DataColumnType.UNKNOWN) ) {
				// Might happen in multiple file upload
				throw new IllegalArgumentException(
						"Data type not defined for column " + Integer.toString(k+1) + 
						": " + cruiseData.getUserColNames().get(k));
			}
			// DATETIME_TIMESTAMP
			else if ( colType.equals(DataColumnType.TIMESTAMP) && 
					  timeSpec.equals(DateTimeType.DATETIME_TIMESTAMP) ) {
				Element userElement = new Element(ColumnSpec.SINGLE_DATE_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = CHECKER_DATA_UNITS.get(colType).get(idx);
			}
			// DATETIME_DATA_TIME
			else if ( colType.equals(DataColumnType.DATE) && 
					  timeSpec.equals(DateTimeType.DATETIME_DATE_TIME) ) {
				Element userElement = new Element(ColumnSpec.DATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = CHECKER_DATA_UNITS.get(colType).get(idx);
			}
			else if ( colType.equals(DataColumnType.TIME) && 
					  timeSpec.equals(DateTimeType.DATETIME_DATE_TIME) ) {
				Element userElement = new Element(ColumnSpec.TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			// DATETIME_YEAR_DAY_SEC
			else if ( colType.equals(DataColumnType.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DAY_SEC) ) {
				Element userElement = new Element(ColumnSpec.YDS_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.DAY_OF_YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DAY_SEC) ) {
				Element userElement = new Element(ColumnSpec.YDS_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// assign the value for Jan 1 
				userElement = new Element(ColumnSpec.JAN_FIRST_INDEX_ELEMENT);
				String units = cruiseData.getDataColUnits().get(k);
				if ( "Jan1=1.0".equals(units) )
					userElement.setText("1");
				else if ( "Jan1=0.0".equals(units) )
					userElement.setText("0");
				else
					throw new IllegalArgumentException("Unexpected \"units\" of '" +
							units + "' for day-of-year");
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.SECOND_OF_DAY) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DAY_SEC) ) {
				Element userElement = new Element(ColumnSpec.YDS_SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			// DATETIME_YEAR_DECIMAL_DAY
			else if ( colType.equals(DataColumnType.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DECIMAL_DAY) ) {
				Element userElement = new Element(ColumnSpec.YDJD_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.DAY_OF_YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DECIMAL_DAY) ) {
				Element userElement = new Element(ColumnSpec.YDJD_DECIMAL_JDATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// assign the value for Jan 1 
				userElement = new Element(ColumnSpec.YDJD_JAN_FIRST_INDEX_ELEMENT);
				String units = cruiseData.getDataColUnits().get(k);
				if ( "Jan1=1.0".equals(units) )
					userElement.setText("1");
				else if ( "Jan1=0.0".equals(units) )
					userElement.setText("0");
				else
					throw new IllegalArgumentException("Unexpected \"units\" of '" +
							units + "' for day-of-year");
				timestampElement.addContent(userElement);
			}
			// DATETIME_YEAR_MON_DAY_HR_MIN_SEC
			else if ( colType.equals(DataColumnType.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.MONTH) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.DAY) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.HOUR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.HOUR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.MINUTE) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.MINUTE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.SECOND) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			// DATETIME_YEAR_MON_DAY_TIME
			else if ( colType.equals(DataColumnType.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.MONTH) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.DAY) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			else if ( colType.equals(DataColumnType.TIME) && 
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
			}
			// Not involved with date/time specification
			else if ( colType.equals(DataColumnType.EXPOCODE) || 
					  colType.equals(DataColumnType.CRUISE_NAME) || 
					  colType.equals(DataColumnType.SHIP_NAME) || 
					  colType.equals(DataColumnType.GROUP_NAME) || 

					  colType.equals(DataColumnType.TIMESTAMP) ||
					  colType.equals(DataColumnType.DATE) ||
					  colType.equals(DataColumnType.YEAR) ||
					  colType.equals(DataColumnType.MONTH) ||
					  colType.equals(DataColumnType.DAY) ||
					  colType.equals(DataColumnType.TIME) ||
					  colType.equals(DataColumnType.HOUR) ||
					  colType.equals(DataColumnType.MINUTE) ||
					  colType.equals(DataColumnType.SECOND) ||
					  colType.equals(DataColumnType.DAY_OF_YEAR) ||
					  colType.equals(DataColumnType.SECOND_OF_DAY) ||

					  colType.equals(DataColumnType.LONGITUDE) || 
					  colType.equals(DataColumnType.LATITUDE) || 
					  colType.equals(DataColumnType.SAMPLE_DEPTH) || 
					  colType.equals(DataColumnType.SALINITY) || 
					  colType.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) || 
					  colType.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) || 
					  colType.equals(DataColumnType.ATMOSPHERIC_TEMPERATURE) || 
					  colType.equals(DataColumnType.EQUILIBRATOR_PRESSURE) || 
					  colType.equals(DataColumnType.SEA_LEVEL_PRESSURE) || 
					  colType.equals(DataColumnType.XH2O_EQU) || 

					  colType.equals(DataColumnType.XCO2_WATER_TEQU_DRY) ||
					  colType.equals(DataColumnType.XCO2_WATER_SST_DRY) ||
					  colType.equals(DataColumnType.XCO2_WATER_TEQU_WET) ||
					  colType.equals(DataColumnType.XCO2_WATER_SST_WET) ||
					  colType.equals(DataColumnType.PCO2_WATER_TEQU_WET) ||
					  colType.equals(DataColumnType.PCO2_WATER_SST_WET) ||
					  colType.equals(DataColumnType.FCO2_WATER_TEQU_WET) ||
					  colType.equals(DataColumnType.FCO2_WATER_SST_WET) || 

					  colType.equals(DataColumnType.XCO2_ATM_DRY_ACTUAL) || 
					  colType.equals(DataColumnType.XCO2_ATM_DRY_INTERP) || 
					  colType.equals(DataColumnType.PCO2_ATM_DRY_ACTUAL) || 
					  colType.equals(DataColumnType.PCO2_ATM_DRY_INTERP) || 
					  colType.equals(DataColumnType.FCO2_ATM_DRY_ACTUAL) || 
					  colType.equals(DataColumnType.FCO2_ATM_DRY_INTERP) || 

					  colType.equals(DataColumnType.DELTA_XCO2) || 
					  colType.equals(DataColumnType.DELTA_PCO2) || 
					  colType.equals(DataColumnType.DELTA_FCO2) || 

					  colType.equals(DataColumnType.RELATIVE_HUMIDITY) || 
					  colType.equals(DataColumnType.SPECIFIC_HUMIDITY) || 
					  colType.equals(DataColumnType.SHIP_SPEED) || 
					  colType.equals(DataColumnType.SHIP_DIRECTION) || 
					  colType.equals(DataColumnType.WIND_SPEED_TRUE) || 
					  colType.equals(DataColumnType.WIND_SPEED_RELATIVE) || 
					  colType.equals(DataColumnType.WIND_DIRECTION_TRUE) || 
					  colType.equals(DataColumnType.WIND_DIRECTION_RELATIVE) ||

					  colType.equals(DataColumnType.WOCE_CO2_WATER) ||
					  colType.equals(DataColumnType.WOCE_CO2_ATM) ) {
				// Element specifying the units of the column
				Element unitsElement = new Element(ColumnSpec.INPUT_UNITS_ELEMENT_NAME);
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				unitsElement.setText(CHECKER_DATA_UNITS.get(colType).get(idx));
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.INPUT_COLUMN_ELEMENT_NAME);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				// Standard column name for the checker
				Element columnElement = new Element(ColumnSpec.SOCAT_COLUMN_ELEMENT); 
				columnElement.setAttribute(ColumnSpec.SOCAT_COLUMN_NAME_ATTRIBUTE, 
						DashboardUtils.STD_HEADER_NAMES.get(colType));
				// Add the index and user name element, and the units element
				columnElement.addContent(userElement);
				columnElement.addContent(unitsElement);
				// Add the missing value if specified
				String missValue = cruiseData.getMissingValues().get(k);
				if ( ! missValue.isEmpty() ) {
					Element missValElement = new Element(ColumnSpec.MISSING_VALUE_ELEMENT_NAME);
					missValElement.setText(missValue);
					columnElement.addContent(missValElement);
				}
				// Add this column description to the root element
				rootElement.addContent(columnElement);
			}
			else if ( colType.equals(DataColumnType.COMMENT_WOCE_CO2_WATER) ||
					  colType.equals(DataColumnType.COMMENT_WOCE_CO2_ATM) ||
					  colType.equals(DataColumnType.OTHER) ) {
				// Unchecked data 
				;
			}
			else {
				// Should never happen
				throw new IllegalArgumentException(
						"Unexpected data column of type " +	colType + "\n" +
						"    for column " + Integer.toString(k+1) + ": " + 
						cruiseData.getUserColNames().get(k));
			}
		}
		// Add the completed timestamp element to the root element
		rootElement.addContent(timestampElement);
		// Create the cruise column specifications document
		Document cruiseDoc = new Document(rootElement);

		// Create the column specifications object for the sanity checker
		String expocode = cruiseData.getExpocode();
		Logger logger = Logger.getLogger("Sanity Checker - " + expocode);
		if ( Level.DEBUG.isGreaterOrEqual(logger.getEffectiveLevel()) ) {
			logger.debug("cruise columns specifications document:\n" + 
					(new XMLOutputter(Format.getPrettyFormat()))
					.outputString(cruiseDoc));
		}
		ColumnSpec colSpec;
		try {
			colSpec = new ColumnSpec(new File(expocode), cruiseDoc, convConfig, logger);
		} catch (InvalidColumnSpecException ex) {
			throw new IllegalArgumentException(
					"Unexpected ColumnSpec exception: " + ex.getMessage());
		};

		// Create the SanityChecker for this cruise
		SanityChecker checker;
		try {
			checker = new SanityChecker(expocode, metadataInput, colSpec, 
									cruiseData.getDataValues(), dateFormat);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Sanity Checker Exception: " + ex.getMessage());
		}

		// Run the SanityChecker on this data and get the results
		Output output = checker.process();

		// Clear all WOCE flags, then set those from the current set of messages
		for ( HashSet<Integer> rowIdxSet : cruiseData.getWoceThreeRowIndices() )
			rowIdxSet.clear();
		for ( HashSet<Integer> rowIdxSet : cruiseData.getWoceFourRowIndices() )
			rowIdxSet.clear();
		for ( Message msg : output.getMessages().getMessages() )
			processMessage(cruiseData, msg, colIndcs);

		// Assign the number of data rows with errors and with only warnings from the sanity checker
		HashSet<Integer> errRows = new HashSet<Integer>();
		for ( HashSet<Integer> rowIdxSet : cruiseData.getWoceFourRowIndices() )
			errRows.addAll(rowIdxSet);
		cruiseData.setNumErrorRows(errRows.size());
		HashSet<Integer> warnRows = new HashSet<Integer>();
		for ( HashSet<Integer> rowIdxSet : cruiseData.getWoceThreeRowIndices() )
			warnRows.addAll(rowIdxSet);
		warnRows.removeAll(errRows);
		cruiseData.setNumWarnRows(warnRows.size());

		// Assign the data-check status message using the results of the sanity check
		if ( ! output.processedOK() ) {
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_UNACCEPTABLE);
		}
		else if ( output.hasErrors() ) {
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX +
					Integer.toString(cruiseData.getNumErrorRows()) + " errors");
		}
		else if ( output.hasWarnings() ) {
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX +
					Integer.toString(cruiseData.getNumWarnRows()) + " warnings");
		}
		else {
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ACCEPTABLE);
		}

		// Add any user-provided WOCE-3 and WOCE-4 flags
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( colType.equals(DataColumnType.WOCE_CO2_WATER) ||
				 colType.equals(DataColumnType.WOCE_CO2_ATM) ) {
				for (int rowIdx = 0; rowIdx < cruiseData.getNumDataRows(); rowIdx++) {
					ArrayList<HashSet<Integer>> woceFlags;
					try {
						int value = Integer.parseInt(cruiseData.getDataValues().get(rowIdx).get(k));
						if ( value == 4 )
							woceFlags = cruiseData.getWoceFourRowIndices();
						else if ( value == 3 )
							woceFlags = cruiseData.getWoceThreeRowIndices();
						else
							woceFlags = null;
					} catch (NumberFormatException ex) {
						woceFlags = null;
					}
					if ( woceFlags != null ) {
						if ( colType.equals(DataColumnType.WOCE_CO2_WATER) ) {
							if ( colIndcs.xCO2WaterTEquDryIndex >= 0 )
								woceFlags.get(colIndcs.xCO2WaterTEquDryIndex).add(rowIdx);
							if ( colIndcs.xCO2WaterSstDryIndex >= 0 )
								woceFlags.get(colIndcs.xCO2WaterSstDryIndex).add(rowIdx);
							if ( colIndcs.xCO2WaterTEquWetIndex >= 0 )
								woceFlags.get(colIndcs.xCO2WaterTEquWetIndex).add(rowIdx);
							if ( colIndcs.xCO2WaterSstWetIndex >= 0 )
								woceFlags.get(colIndcs.xCO2WaterSstWetIndex).add(rowIdx);
							if ( colIndcs.pCO2WaterTEquWetIndex >= 0 )
								woceFlags.get(colIndcs.pCO2WaterTEquWetIndex).add(rowIdx);
							if ( colIndcs.pCO2WaterSstWetIndex >= 0 )
								woceFlags.get(colIndcs.pCO2WaterSstWetIndex).add(rowIdx);
							if ( colIndcs.fCO2WaterTEquWetIndex >= 0 )
								woceFlags.get(colIndcs.fCO2WaterTEquWetIndex).add(rowIdx);
							if ( colIndcs.fCO2WaterSstWetIndex >= 0 )
								woceFlags.get(colIndcs.fCO2WaterSstWetIndex).add(rowIdx);
						}
						else if ( colType.equals(DataColumnType.WOCE_CO2_ATM) ) {
							if ( colIndcs.xCO2AtmDryActualIndex >= 0 )
								woceFlags.get(colIndcs.xCO2AtmDryActualIndex).add(rowIdx);
							if ( colIndcs.xCO2AtmDryInterpIndex >= 0 )
								woceFlags.get(colIndcs.xCO2AtmDryInterpIndex).add(rowIdx);
							if ( colIndcs.pCO2AtmDryActualIndex >= 0 )
								woceFlags.get(colIndcs.pCO2AtmDryActualIndex).add(rowIdx);
							if ( colIndcs.pCO2AtmDryInterpIndex >= 0 )
								woceFlags.get(colIndcs.pCO2AtmDryInterpIndex).add(rowIdx);
							if ( colIndcs.fCO2AtmDryActualIndex >= 0 )
								woceFlags.get(colIndcs.fCO2AtmDryActualIndex).add(rowIdx);
							if ( colIndcs.fCO2AtmDryInterpIndex >= 0 )
								woceFlags.get(colIndcs.fCO2AtmDryInterpIndex).add(rowIdx);
						}
					}
				}
			}
		}

		ArrayList<HashSet<Integer>> woceThreeSets = cruiseData.getWoceThreeRowIndices();
		ArrayList<HashSet<Integer>> woceFourSets = cruiseData.getWoceFourRowIndices();

		// Remove any WOCE-3 flags on data values that also have a WOCE-4 flag
		for (int k = 0; k < woceThreeSets.size(); k++) {
			woceThreeSets.get(k).removeAll(woceFourSets.get(k));
		}

		// Set the lastCruiseCheckHadGeopositionErrors flag indicating 
		// if there are any date/time, lat, or lon WOCE-4 flags
		if ( ( (colIndcs.timestampIndex >= 0) && 
				! woceFourSets.get(colIndcs.timestampIndex).isEmpty() ) ||
			 ( (colIndcs.dateIndex >= 0) && 
				! woceFourSets.get(colIndcs.dateIndex).isEmpty() ) ||
			 ( (colIndcs.yearIndex >= 0) &&
				! woceFourSets.get(colIndcs.yearIndex).isEmpty() ) ||
			 ( (colIndcs.monthIndex >= 0) &&
				! woceFourSets.get(colIndcs.monthIndex).isEmpty() ) ||
			 ( (colIndcs.dayIndex >= 0) &&
				! woceFourSets.get(colIndcs.dayIndex).isEmpty() ) ||
			 ( (colIndcs.timeIndex >= 0) &&
				! woceFourSets.get(colIndcs.timeIndex).isEmpty() ) ||
			 ( (colIndcs.hourIndex >= 0) &&
				! woceFourSets.get(colIndcs.hourIndex).isEmpty() ) ||
			 ( (colIndcs.minuteIndex >= 0) &&
				! woceFourSets.get(colIndcs.minuteIndex).isEmpty() ) ||
			 ( (colIndcs.secondIndex >= 0) &&
				! woceFourSets.get(colIndcs.secondIndex).isEmpty() ) ||
			 ( (colIndcs.dayOfYearIndex >= 0) &&
				! woceFourSets.get(colIndcs.dayOfYearIndex).isEmpty() ) ||
			 ( (colIndcs.secondOfDayIndex >= 0) &&
				! woceFourSets.get(colIndcs.secondOfDayIndex).isEmpty() ) ||
			 ( (colIndcs.longitudeIndex < 0) ||
				! woceFourSets.get(colIndcs.longitudeIndex).isEmpty() ) ||
			 ( (colIndcs.latitudeIndex < 0) ||
				! woceFourSets.get(colIndcs.latitudeIndex).isEmpty() ) ) {
			lastCruiseCheckHadGeopositionErrors = true;
		}
		else {
			lastCruiseCheckHadGeopositionErrors = false;
		}

		return output;
	}

	/**
	 * @return
	 * 		true if the cruise last checked had geoposition errors
	 */
	public boolean hadGeopositionErrors() {
		return lastCruiseCheckHadGeopositionErrors;
	}

	/**
	 * Assigns the WOCE-3 or WOCE-4 flag associated with this message 
	 * to the cruise.
	 * 
	 * @param cruiseData
	 * 		cruise with data to assign
	 * @param msg
	 * 		SanityChecker message
	 * @param colIndcs
	 * 		user-provided-data column indices that could receive WOCE flags
	 */
	private void processMessage(DashboardCruiseWithData cruiseData, 
								Message msg, ColumnIndices colIndcs) {
		int rowIdx = msg.getLineNumber();
		if ( (rowIdx <= 0) || (rowIdx > cruiseData.getNumDataRows()) )
			throw new RuntimeException("Unexpected row number of " + 
					Integer.toString(rowIdx) + " in the sanity checker message\n" +
					"    " + msg.toString());
		// Change row number to row index
		rowIdx--;

		int colIdx = msg.getInputItemIndex();
		if ( (colIdx == 0) || (colIdx > cruiseData.getDataColTypes().size()) )
			throw new RuntimeException("Unexpected input column number of " + 
					Integer.toString(colIdx) + " in the sanity checker message\n" +
					"    " + msg.toString());
		// Change column number to column index; 
		// negative numbers indicate an ambiguous source of error
		if ( colIdx > 0 )
			colIdx--;

		if ( msg.isError() ) {
			// Erroneous data value
			if ( colIdx < 0 ) {
				// TODO: Disambiguate errors with no column index
				// Associate ambiguous errors with time indices; 
				// could be timestamp issue or calculated speed issue
				ArrayList<HashSet<Integer>> woceFlags = cruiseData.getWoceFourRowIndices();
				if ( colIndcs.timestampIndex >= 0 )
					woceFlags.get(colIndcs.timestampIndex).add(rowIdx);
				if ( colIndcs.timeIndex >= 0 )
					woceFlags.get(colIndcs.timeIndex).add(rowIdx);
				if ( colIndcs.yearIndex >= 0 )
					woceFlags.get(colIndcs.yearIndex).add(rowIdx);
				if ( colIndcs.monthIndex >= 0 )
					woceFlags.get(colIndcs.monthIndex).add(rowIdx);
				if ( colIndcs.dayIndex >= 0 )
					woceFlags.get(colIndcs.dayIndex).add(rowIdx);
				if ( colIndcs.timeIndex >= 0 )
					woceFlags.get(colIndcs.timeIndex).add(rowIdx);
				if ( colIndcs.hourIndex >= 0 )
					woceFlags.get(colIndcs.hourIndex).add(rowIdx);
				if ( colIndcs.minuteIndex >= 0 )
					woceFlags.get(colIndcs.minuteIndex).add(rowIdx);
				if ( colIndcs.secondIndex >= 0 )
					woceFlags.get(colIndcs.secondIndex).add(rowIdx);
				if ( colIndcs.dayOfYearIndex >= 0 )
					woceFlags.get(colIndcs.dayOfYearIndex).add(rowIdx);
			}
			else {
				cruiseData.getWoceFourRowIndices().get(colIdx).add(rowIdx);
			}
		}
		else if ( msg.isWarning() ) {
			// Questionable data value
			if ( colIdx < 0 ) {
				// TODO: Disambiguate warnings with no column index
				// Associate ambiguous warnings with time indices; 
				// could be timestamp issue or calculated speed issue
				ArrayList<HashSet<Integer>> woceFlags = cruiseData.getWoceThreeRowIndices();
				if ( colIndcs.timestampIndex >= 0 )
					woceFlags.get(colIndcs.timestampIndex).add(rowIdx);
				if ( colIndcs.timeIndex >= 0 )
					woceFlags.get(colIndcs.timeIndex).add(rowIdx);
				if ( colIndcs.yearIndex >= 0 )
					woceFlags.get(colIndcs.yearIndex).add(rowIdx);
				if ( colIndcs.monthIndex >= 0 )
					woceFlags.get(colIndcs.monthIndex).add(rowIdx);
				if ( colIndcs.dayIndex >= 0 )
					woceFlags.get(colIndcs.dayIndex).add(rowIdx);
				if ( colIndcs.timeIndex >= 0 )
					woceFlags.get(colIndcs.timeIndex).add(rowIdx);
				if ( colIndcs.hourIndex >= 0 )
					woceFlags.get(colIndcs.hourIndex).add(rowIdx);
				if ( colIndcs.minuteIndex >= 0 )
					woceFlags.get(colIndcs.minuteIndex).add(rowIdx);
				if ( colIndcs.secondIndex >= 0 )
					woceFlags.get(colIndcs.secondIndex).add(rowIdx);
				if ( colIndcs.dayOfYearIndex >= 0 )
					woceFlags.get(colIndcs.dayOfYearIndex).add(rowIdx);
			}
			else {
				cruiseData.getWoceThreeRowIndices().get(colIdx).add(rowIdx);
			}
		}
		else {
			// Should never happen
			throw new IllegalArgumentException(
					"Unexpected message that is neither an error nor a warning:\n" +
					"    " + msg.toString());
		}
	}

	/**
	 * Sanity-checks and standardizes the units in the data values,
	 * stored as strings, in the given cruise.  The year, month, day,
	 * hour, minute, and second data columns are appended to each 
	 * data measurement (row, outer array) if not already present.
	 *  
	 * @param cruiseData
	 * 		cruise data to be standardized
	 * @return
	 * 		standardized cruise data
	 */
	public Output standardizeCruiseData(DashboardCruiseWithData cruiseData) {
		// Run the SanityChecker to get the standardized data
		Output output = checkCruise(cruiseData);
		List<SocatDataRecord> stdRowVals = output.getRecords();

		// Directly modify the lists in the cruise data object
		ArrayList<DataColumnType> dataColTypes = cruiseData.getDataColTypes();
		ArrayList<ArrayList<String>> dataVals = cruiseData.getDataValues();

		// Standardized data for generating a SocatCruiseData object must have 
		// separate year, month, day, hour, minute, and second columns
		boolean hasYearColumn = false;
		boolean hasMonthColumn = false;
		boolean hasDayColumn = false;
		boolean hasHourColumn = false;
		boolean hasMinuteColumn = false;
		boolean hasSecondColumn = false;
		for ( DataColumnType colType : dataColTypes ) {
			if ( colType.equals(DataColumnType.YEAR) ) {
				hasYearColumn = true;
			}
			else if ( colType.equals(DataColumnType.MONTH) ) {
				hasMonthColumn = true;
			}
			else if ( colType.equals(DataColumnType.DAY) ) {
				hasDayColumn = true;
			}
			else if ( colType.equals(DataColumnType.HOUR) ) {
				hasHourColumn = true;
			}
			else if ( colType.equals(DataColumnType.MINUTE) ) {
				hasMinuteColumn = true;
			}
			else if ( colType.equals(DataColumnType.SECOND) ) {
				hasSecondColumn = true;
			}
		}

		if ( ! ( hasYearColumn && hasMonthColumn && hasDayColumn &&
				 hasHourColumn && hasMinuteColumn && hasSecondColumn ) ) {
			// Add missing time columns; 
			// directly modify the lists in the cruise data object
			ArrayList<String> userColNames = cruiseData.getUserColNames();
			ArrayList<String> dataColUnits = cruiseData.getDataColUnits();
			ArrayList<String> missingValues = cruiseData.getMissingValues();
			ArrayList<HashSet<Integer>> woceThreeRowIndices = cruiseData.getWoceThreeRowIndices();
			ArrayList<HashSet<Integer>> woceFourRowIndices = cruiseData.getWoceFourRowIndices();
			if ( ! hasYearColumn ) {
				dataColTypes.add(DataColumnType.YEAR);
				userColNames.add("Year");
				dataColUnits.add("");
				missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
				woceThreeRowIndices.add(new HashSet<Integer>());
				woceFourRowIndices.add(new HashSet<Integer>());
			}
			if ( ! hasMonthColumn ) {
				dataColTypes.add(DataColumnType.MONTH);
				userColNames.add("Month");
				dataColUnits.add("");
				missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
				woceThreeRowIndices.add(new HashSet<Integer>());
				woceFourRowIndices.add(new HashSet<Integer>());
			}
			if ( ! hasDayColumn ) {
				dataColTypes.add(DataColumnType.DAY);
				userColNames.add("Day");
				dataColUnits.add("");
				missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
				woceThreeRowIndices.add(new HashSet<Integer>());
				woceFourRowIndices.add(new HashSet<Integer>());
			}
			if ( ! hasHourColumn ) {
				dataColTypes.add(DataColumnType.HOUR);
				userColNames.add("Hour");
				dataColUnits.add("");
				missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
				woceThreeRowIndices.add(new HashSet<Integer>());
				woceFourRowIndices.add(new HashSet<Integer>());
			}
			if ( ! hasMinuteColumn ) {
				dataColTypes.add(DataColumnType.MINUTE);
				userColNames.add("Minute");
				dataColUnits.add("");
				missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
				woceThreeRowIndices.add(new HashSet<Integer>());
				woceFourRowIndices.add(new HashSet<Integer>());
			}
			if ( ! hasSecondColumn ) {
				dataColTypes.add(DataColumnType.SECOND);
				userColNames.add("Second");
				dataColUnits.add("");
				missingValues.add(Double.toString(SocatCruiseData.FP_MISSING_VALUE));
				woceThreeRowIndices.add(new HashSet<Integer>());
				woceFourRowIndices.add(new HashSet<Integer>());
			}
			Iterator<SocatDataRecord> stdRowIter = stdRowVals.iterator();
			for ( ArrayList<String> rowData : dataVals ) {
				SocatDataRecord stdVals;
				try {
					stdVals = stdRowIter.next();
				} catch ( NoSuchElementException ex ) {
					throw new IllegalArgumentException(
							"Unexpected mismatch in the number of rows of " +
							"original data and standardized data");
				}
				Integer year;
				Integer month;
				Integer day;
				Integer hour;
				Integer minute;
				Double second;
				try {
					DateTime timestamp = stdVals.getTime();
					year = timestamp.getYear();
					month = timestamp.getMonthOfYear();
					day = timestamp.getDayOfMonth();
					hour = timestamp.getHourOfDay();
					minute = timestamp.getMinuteOfHour();
					int isecond = timestamp.getSecondOfMinute();
					int msecs = timestamp.getMillisOfSecond();
					second = isecond + (double) msecs / 1000.0;
				} catch ( Exception ex ) {
					year = SocatCruiseData.INT_MISSING_VALUE;
					month = SocatCruiseData.INT_MISSING_VALUE;
					day = SocatCruiseData.INT_MISSING_VALUE;
					hour = SocatCruiseData.INT_MISSING_VALUE;
					minute = SocatCruiseData.INT_MISSING_VALUE;
					second = SocatCruiseData.FP_MISSING_VALUE;
				}
				if ( ! hasYearColumn )
					rowData.add(year.toString());
				if ( ! hasMonthColumn )
					rowData.add(month.toString());
				if ( ! hasDayColumn )
					rowData.add(day.toString());
				if ( ! hasHourColumn )
					rowData.add(hour.toString());
				if ( ! hasMinuteColumn )
					rowData.add(minute.toString());
				if ( ! hasSecondColumn )
					rowData.add(second.toString());
			}
		}

		// Go through each row, converting data as needed
		Iterator<SocatDataRecord> stdRowIter = stdRowVals.iterator();
		for ( ArrayList<String> rowData : dataVals ) {
			SocatDataRecord stdVals;
			try {
				stdVals = stdRowIter.next();
			} catch ( NoSuchElementException ex ) {
				throw new IllegalArgumentException(
						"Unexpected mismatch in the number of rows of " +
						"original data and standardized data");
			}
			int k = -1;
			for ( DataColumnType colType : dataColTypes ) {
				k++;
				if ( colType.equals(DataColumnType.TIMESTAMP) || 
					 colType.equals(DataColumnType.DATE) || 
					 colType.equals(DataColumnType.YEAR) || 
					 colType.equals(DataColumnType.MONTH) || 
					 colType.equals(DataColumnType.DAY) || 
					 colType.equals(DataColumnType.TIME) ||  
					 colType.equals(DataColumnType.HOUR) || 
					 colType.equals(DataColumnType.MINUTE) || 
					 colType.equals(DataColumnType.SECOND) ) {
					// Already handled
					;
				}
				else if ( colType.equals(DataColumnType.LONGITUDE) ) {
					rowData.set(k, Double.toString(stdVals.getLongitude()));
				}
				else if ( colType.equals(DataColumnType.LATITUDE) ) {
					rowData.set(k, Double.toString(stdVals.getLatitude()));
				}
				else if ( colType.equals(DataColumnType.DAY_OF_YEAR) || 
						  colType.equals(DataColumnType.SECOND_OF_DAY) || 
						  colType.equals(DataColumnType.SAMPLE_DEPTH) || 
						  colType.equals(DataColumnType.SALINITY) || 
						  colType.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) || 
						  colType.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) || 
						  colType.equals(DataColumnType.ATMOSPHERIC_TEMPERATURE) || 
						  colType.equals(DataColumnType.EQUILIBRATOR_PRESSURE) || 
						  colType.equals(DataColumnType.SEA_LEVEL_PRESSURE) || 

						  colType.equals(DataColumnType.XCO2_WATER_TEQU_DRY) || 
						  colType.equals(DataColumnType.XCO2_WATER_SST_DRY) || 
						  colType.equals(DataColumnType.XCO2_WATER_TEQU_WET) || 
						  colType.equals(DataColumnType.XCO2_WATER_SST_WET) || 
						  colType.equals(DataColumnType.PCO2_WATER_TEQU_WET) || 
						  colType.equals(DataColumnType.PCO2_WATER_SST_WET) || 
						  colType.equals(DataColumnType.FCO2_WATER_TEQU_WET) || 
						  colType.equals(DataColumnType.FCO2_WATER_SST_WET) || 

						  colType.equals(DataColumnType.XCO2_ATM_DRY_ACTUAL) || 
						  colType.equals(DataColumnType.XCO2_ATM_DRY_INTERP) || 
						  colType.equals(DataColumnType.PCO2_ATM_DRY_ACTUAL) || 
						  colType.equals(DataColumnType.PCO2_ATM_DRY_INTERP) || 
						  colType.equals(DataColumnType.FCO2_ATM_DRY_ACTUAL) || 
						  colType.equals(DataColumnType.FCO2_ATM_DRY_INTERP) || 

						  colType.equals(DataColumnType.DELTA_XCO2) || 
						  colType.equals(DataColumnType.DELTA_PCO2) || 
						  colType.equals(DataColumnType.DELTA_FCO2) || 

						  colType.equals(DataColumnType.XH2O_EQU) || 
						  colType.equals(DataColumnType.RELATIVE_HUMIDITY) || 
						  colType.equals(DataColumnType.SPECIFIC_HUMIDITY) || 
						  colType.equals(DataColumnType.SHIP_SPEED) || 
						  colType.equals(DataColumnType.SHIP_DIRECTION) || 
						  colType.equals(DataColumnType.WIND_SPEED_TRUE) || 
						  colType.equals(DataColumnType.WIND_SPEED_RELATIVE) || 
						  colType.equals(DataColumnType.WIND_DIRECTION_TRUE) || 
						  colType.equals(DataColumnType.WIND_DIRECTION_RELATIVE) ) {
					String chkName = DashboardUtils.STD_HEADER_NAMES.get(colType);
					SocatDataColumn stdCol = stdVals.getColumn(chkName);
					if ( stdCol == null )
						throw new IllegalArgumentException("SocatDataColumn not found for " + 
								chkName + " (column type " + colType + ")");
					String value = stdCol.getValue();
					rowData.set(k, value);
				}
				else if ( colType.equals(DataColumnType.EXPOCODE) || 
						  colType.equals(DataColumnType.CRUISE_NAME) || 
						  colType.equals(DataColumnType.SHIP_NAME) || 
						  colType.equals(DataColumnType.GROUP_NAME) || 

						  colType.equals(DataColumnType.WOCE_CO2_WATER) || 
						  colType.equals(DataColumnType.WOCE_CO2_ATM) || 
						  colType.equals(DataColumnType.COMMENT_WOCE_CO2_WATER) ||
						  colType.equals(DataColumnType.COMMENT_WOCE_CO2_ATM) ||
						  colType.equals(DataColumnType.OTHER) ) {
					// Column types that are never modified by the sanity checker
					// They may or may not have been checked.
					;
				}
				else {
					// Should never happen
					throw new IllegalArgumentException(
							"Unexpected data column of type " +	colType + "\n" +
									"    for column " + Integer.toString(k+1) + ": " + 
									cruiseData.getUserColNames().get(k));
				}
			}
		}
		return output;
	}

}
