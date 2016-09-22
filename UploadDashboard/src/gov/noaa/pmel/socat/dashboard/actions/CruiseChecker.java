/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.socat.dashboard.server.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import uk.ac.uea.socat.omemetadata.BadEntryNameException;
import uk.ac.uea.socat.omemetadata.InvalidConflictException;
import uk.ac.uea.socat.omemetadata.OmeMetadata;
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
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageException;

/**
 * Class for working with the SanityChecker 
 * 
 * @author Karl Smith
 */
public class CruiseChecker {

	/**
	 * Indices of user-provided data columns. 
	 */
	private class ColumnIndices {
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
		int woceCO2WaterIndex = -1;
		int woceCO2AtmIndex = -1;
	}

	/**
	 * Creates and returns a ColumnIndices assigned with the indices
	 * of the given data column types.
	 * 
	 * @param columnTypes
	 * 		data column types to use
	 * @return
	 * 		assigned data column indices
	 */
	private ColumnIndices getColumnIndices(ArrayList<DataColumnType> columnTypes) {
		// Save indices of data columns for assigning WOCE flags 
		ColumnIndices colIndcs = new ColumnIndices();

		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( colType.typeEquals(KnownDataTypes.TIMESTAMP) )
				colIndcs.timestampIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.DATE) )
				colIndcs.dateIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.YEAR) )
				colIndcs.yearIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.MONTH_OF_YEAR) )
				colIndcs.monthIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.DAY_OF_MONTH) )
				colIndcs.dayIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.TIME_OF_DAY) )
				colIndcs.timeIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.HOUR_OF_DAY) )
				colIndcs.hourIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.MINUTE_OF_HOUR) )
				colIndcs.minuteIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.SECOND_OF_MINUTE) )
				colIndcs.secondIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.DAY_OF_YEAR) )
				colIndcs.dayOfYearIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.SECOND_OF_DAY) )
				colIndcs.secondOfDayIndex = k;

			else if ( colType.typeEquals(KnownDataTypes.LONGITUDE) )
				colIndcs.longitudeIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.LATITUDE) )
				colIndcs.latitudeIndex = k;

			else if ( colType.typeEquals(KnownDataTypes.WOCE_CO2_WATER) )
				colIndcs.woceCO2WaterIndex = k;
			else if ( colType.typeEquals(KnownDataTypes.WOCE_CO2_ATM) )
				colIndcs.woceCO2AtmIndex = k;
		}
		return colIndcs;
	}

	private enum DateTimeType {
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
	private static final EnumMap<DataColumnType,ArrayList<String>> CHECKER_DATA_UNITS = 
			new EnumMap<DataColumnType,ArrayList<String>>(DataColumnType.class);
	static {
		final ArrayList<String> checkerTimestampDateUnits = 
				new ArrayList<String>(KnownDataTypes.TIMESTAMP_UNITS.size());
		for ( String fmt : KnownDataTypes.TIMESTAMP_UNITS ) 
			checkerTimestampDateUnits.add(fmt.split(" ", 2)[0]);
		final ArrayList<String> checkerLongitudeUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees_east", "decimal_degrees_west"));
		final ArrayList<String> checkerLatitudeUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees_north", "decimal_degrees_south"));
		final ArrayList<String> checkerSalinityUnits = 
				new ArrayList<String>(Arrays.asList("psu"));
		final ArrayList<String> checkerTemperatureUnits = 
				new ArrayList<String>(Arrays.asList("degC"));
		final ArrayList<String> checkerXCO2Units = 
				new ArrayList<String>(Arrays.asList("ppm"));
		final ArrayList<String> checkerDirectionUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees"));

		// UNKNOWN should not be processed by the sanity checker

		CHECKER_DATA_UNITS.put(KnownDataTypes.EXPOCODE, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.DATASET_NAME, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.VESSEL_NAME, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.ORGANIZATION_NAME, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.INVESTIGATOR_NAMES, DataColumnType.NO_UNITS);

		CHECKER_DATA_UNITS.put(KnownDataTypes.TIMESTAMP, checkerTimestampDateUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.DATE, KnownDataTypes.DATE_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.YEAR, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.MONTH_OF_YEAR, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.DAY_OF_MONTH, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.TIME_OF_DAY, KnownDataTypes.TIME_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.HOUR_OF_DAY, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.MINUTE_OF_HOUR, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SECOND_OF_MINUTE, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.DAY_OF_YEAR, KnownDataTypes.DAY_OF_YEAR_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SECOND_OF_DAY, DataColumnType.NO_UNITS);

		CHECKER_DATA_UNITS.put(KnownDataTypes.LONGITUDE, checkerLongitudeUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.LATITUDE, checkerLatitudeUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SAMPLE_DEPTH, KnownDataTypes.DEPTH_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SALINITY, checkerSalinityUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.EQUILIBRATOR_TEMPERATURE, checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SEA_SURFACE_TEMPERATURE, checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.ATMOSPHERIC_TEMPERATURE, checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.EQUILIBRATOR_PRESSURE, DashboardUtils.PRESSURE_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SEA_LEVEL_PRESSURE, DashboardUtils.PRESSURE_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.XH2O_EQU, DashboardUtils.XH2O_UNITS);

		CHECKER_DATA_UNITS.put(KnownDataTypes.XCO2_WATER_TEQU_DRY, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(KnownDataTypes.XCO2_WATER_SST_DRY, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(KnownDataTypes.XCO2_WATER_TEQU_WET, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(KnownDataTypes.XCO2_WATER_SST_WET, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(KnownDataTypes.PCO2_WATER_TEQU_WET, KnownDataTypes.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.PCO2_WATER_SST_WET, KnownDataTypes.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.FCO2_WATER_TEQU_WET, KnownDataTypes.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.FCO2_WATER_SST_WET, KnownDataTypes.PCO2_UNITS);

		CHECKER_DATA_UNITS.put(KnownDataTypes.XCO2_ATM_DRY_ACTUAL, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(KnownDataTypes.XCO2_ATM_DRY_INTERP, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(KnownDataTypes.PCO2_ATM_WET_ACTUAL, KnownDataTypes.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.PCO2_ATM_WET_INTERP, KnownDataTypes.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.FCO2_ATM_WET_ACTUAL, KnownDataTypes.FCO2_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.FCO2_ATM_WET_INTERP, KnownDataTypes.FCO2_UNITS);

		CHECKER_DATA_UNITS.put(KnownDataTypes.DELTA_XCO2, checkerXCO2Units);
		CHECKER_DATA_UNITS.put(KnownDataTypes.DELTA_PCO2, KnownDataTypes.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.DELTA_FCO2, KnownDataTypes.FCO2_UNITS);

		CHECKER_DATA_UNITS.put(KnownDataTypes.RELATIVE_HUMIDITY, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SPECIFIC_HUMIDITY, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SHIP_SPEED, KnownDataTypes.SHIP_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.SHIP_DIRECTION, checkerDirectionUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.WIND_SPEED_TRUE, KnownDataTypes.WIND_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.WIND_SPEED_RELATIVE, KnownDataTypes.WIND_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.WIND_DIRECTION_TRUE, checkerDirectionUnits);
		CHECKER_DATA_UNITS.put(KnownDataTypes.WIND_DIRECTION_RELATIVE, checkerDirectionUnits);

		CHECKER_DATA_UNITS.put(KnownDataTypes.WOCE_CO2_WATER, DataColumnType.NO_UNITS);
		CHECKER_DATA_UNITS.put(KnownDataTypes.WOCE_CO2_ATM, DataColumnType.NO_UNITS);

		// COMMENT... and OTHER should not be processed by the sanity checker
	}

	private CheckerMessageHandler msgHandler;
	private MetadataFileHandler metadataHandler;
	private boolean lastCheckProcessedOkay;
	private boolean lastCheckHadGeopositionErrors;

	/**
	 * Initializes the SanityChecker using the configuration files names
	 * in the given properties files.
	 * 
	 * @param configFile
	 * 		properties file giving the names of the configuration files 
	 * 		for each SanityChecker component
	 * @param checkerMsgHandler
	 * 		handler for SanityChecker messages
	 * @param metaFileHandler
	 * 		handler for dashboard OME metadata files
	 * @throws IOException
	 * 		If the SanityChecker has problems with a configuration file
	 */
	public CruiseChecker(File configFile, CheckerMessageHandler checkerMsgHandler, 
			MetadataFileHandler metaFileHandler) throws IOException {
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
		if ( checkerMsgHandler == null )
			throw new NullPointerException(
					"CheckerMsgHandler passed to CruiseChecker is null");
		msgHandler = checkerMsgHandler;
		if ( metaFileHandler == null )
			throw new NullPointerException(
					"MetadataFileHandler passed to CruiseChecker is null");
		metadataHandler = metaFileHandler;
		lastCheckProcessedOkay = false;
		lastCheckHadGeopositionErrors = false;
	}

	/**
	 * Runs the SanityChecker on the given cruise.  Saves the SanityChecker
	 * messages, and assigns the data check status and the WOCE-3 and WOCE-4 
	 * data flags from the SanityChecker output.
	 * 
	 * @param cruiseData
	 * 		cruise to check
	 * @return
	 * 		if the SanityChecker ran successfully
	 * @throws IllegalArgumentException
	 * 		if a data column type is unknown, 
	 * 		if an existing OME XML file is corrupt, or
	 * 		if the sanity checker throws an exception
	 */
	public boolean checkCruise(DashboardCruiseWithData cruiseData) 
												throws IllegalArgumentException {
		Output output = checkCruiseAndReturnOutput(cruiseData);
		return output.processedOK();
	}

	/**
	 * Runs the SanityChecker on the given cruise.  Saves the SanityChecker
	 * messages, and assigns the data check status and the WOCE-3 and WOCE-4 
	 * data flags from the SanityChecker output.
	 * 
	 * @param cruiseData
	 * 		cruise to check
	 * @return
	 * 		the returned Output from {@link SanityChecker#process()}
	 * @throws IllegalArgumentException
	 * 		if a data column type is unknown, 
	 * 		if an existing OME XML file is corrupt, or
	 * 		if the sanity checker throws an exception
	 */
	private Output checkCruiseAndReturnOutput(DashboardCruiseWithData cruiseData) 
												throws IllegalArgumentException {
		String expocode = cruiseData.getExpocode();

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
		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();
		ColumnIndices colIndcs = getColumnIndices(columnTypes);

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
					"measurement is not completely specified in a way known " +
					"to the automated data checker");

		// Specify the columns in this cruise data
		Element rootElement = new Element("Expocode_" + cruiseData.getExpocode());
		Element[] timestampElements = new Element[] { null, null, null, null, null, null };
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( colType.typeEquals(DataColumnType.UNKNOWN) ) {
				// Might happen in multiple file upload
				throw new IllegalArgumentException(
						"Data type not defined for column " + Integer.toString(k+1) + 
						": " + cruiseData.getUserColNames().get(k));
			}
			// DATETIME_TIMESTAMP
			else if ( colType.typeEquals(KnownDataTypes.TIMESTAMP) && 
					  timeSpec.equals(DateTimeType.DATETIME_TIMESTAMP) ) {
				Element userElement = new Element(ColumnSpec.SINGLE_DATE_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = CHECKER_DATA_UNITS.get(colType).get(idx);
			}
			// DATETIME_DATA_TIME
			else if ( colType.typeEquals(KnownDataTypes.DATE) && 
					  timeSpec.equals(DateTimeType.DATETIME_DATE_TIME) ) {
				Element userElement = new Element(ColumnSpec.DATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = CHECKER_DATA_UNITS.get(colType).get(idx);
			}
			else if ( colType.typeEquals(KnownDataTypes.TIME_OF_DAY) && 
					  timeSpec.equals(DateTimeType.DATETIME_DATE_TIME) ) {
				Element userElement = new Element(ColumnSpec.TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			// DATETIME_YEAR_DAY_SEC
			else if ( colType.typeEquals(KnownDataTypes.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DAY_SEC) ) {
				Element userElement = new Element(ColumnSpec.YDS_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.DAY_OF_YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DAY_SEC) ) {
				Element userElement = new Element(ColumnSpec.YDS_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
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
				timestampElements[3] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.SECOND_OF_DAY) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DAY_SEC) ) {
				Element userElement = new Element(ColumnSpec.YDS_SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			// DATETIME_YEAR_DECIMAL_DAY
			else if ( colType.typeEquals(KnownDataTypes.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DECIMAL_DAY) ) {
				Element userElement = new Element(ColumnSpec.YDJD_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.DAY_OF_YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_DECIMAL_DAY) ) {
				Element userElement = new Element(ColumnSpec.YDJD_DECIMAL_JDATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
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
				timestampElements[2] = userElement;
			}
			// DATETIME_YEAR_MON_DAY_HR_MIN_SEC
			else if ( colType.typeEquals(KnownDataTypes.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.MONTH_OF_YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.DAY_OF_MONTH) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.HOUR_OF_DAY) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.HOUR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[3] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.MINUTE_OF_HOUR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.MINUTE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[4] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.SECOND_OF_MINUTE) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC) ) {
				Element userElement = new Element(ColumnSpec.SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[5] = userElement;
			}
			// DATETIME_YEAR_MON_DAY_TIME
			else if ( colType.typeEquals(KnownDataTypes.YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.MONTH_OF_YEAR) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.DAY_OF_MONTH) &&
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			else if ( colType.typeEquals(KnownDataTypes.TIME_OF_DAY) && 
					  timeSpec.equals(DateTimeType.DATETIME_YEAR_MON_DAY_TIME) ) {
				Element userElement = new Element(ColumnSpec.YMDT_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[3] = userElement;
			}
			// Not involved with date/time specification
			else if ( colType.typeEquals(KnownDataTypes.EXPOCODE) || 
					  colType.typeEquals(KnownDataTypes.CRUISE_NAME) || 
					  colType.typeEquals(KnownDataTypes.SHIP_NAME) || 
					  colType.typeEquals(KnownDataTypes.GROUP_NAME) || 
					  colType.typeEquals(KnownDataTypes.INVESTIGATOR_NAMES) || 

					  colType.typeEquals(KnownDataTypes.TIMESTAMP) ||
					  colType.typeEquals(KnownDataTypes.DATE) ||
					  colType.typeEquals(KnownDataTypes.YEAR) ||
					  colType.typeEquals(KnownDataTypes.MONTH_OF_YEAR) ||
					  colType.typeEquals(KnownDataTypes.DAY_OF_MONTH) ||
					  colType.typeEquals(KnownDataTypes.TIME_OF_DAY) ||
					  colType.typeEquals(KnownDataTypes.HOUR_OF_DAY) ||
					  colType.typeEquals(KnownDataTypes.MINUTE_OF_HOUR) ||
					  colType.typeEquals(KnownDataTypes.SECOND_OF_MINUTE) ||
					  colType.typeEquals(KnownDataTypes.DAY_OF_YEAR) ||
					  colType.typeEquals(KnownDataTypes.SECOND_OF_DAY) ||

					  colType.typeEquals(KnownDataTypes.LONGITUDE) || 
					  colType.typeEquals(KnownDataTypes.LATITUDE) || 
					  colType.typeEquals(KnownDataTypes.SAMPLE_DEPTH) || 
					  colType.typeEquals(KnownDataTypes.SALINITY) || 
					  colType.typeEquals(KnownDataTypes.EQUILIBRATOR_TEMPERATURE) || 
					  colType.typeEquals(KnownDataTypes.SEA_SURFACE_TEMPERATURE) || 
					  colType.typeEquals(KnownDataTypes.ATMOSPHERIC_TEMPERATURE) || 
					  colType.typeEquals(KnownDataTypes.EQUILIBRATOR_PRESSURE) || 
					  colType.typeEquals(KnownDataTypes.SEA_LEVEL_PRESSURE) || 
					  colType.typeEquals(KnownDataTypes.XH2O_EQU) || 

					  colType.typeEquals(KnownDataTypes.XCO2_WATER_TEQU_DRY) ||
					  colType.typeEquals(KnownDataTypes.XCO2_WATER_SST_DRY) ||
					  colType.typeEquals(KnownDataTypes.XCO2_WATER_TEQU_WET) ||
					  colType.typeEquals(KnownDataTypes.XCO2_WATER_SST_WET) ||
					  colType.typeEquals(KnownDataTypes.PCO2_WATER_TEQU_WET) ||
					  colType.typeEquals(KnownDataTypes.PCO2_WATER_SST_WET) ||
					  colType.typeEquals(KnownDataTypes.FCO2_WATER_TEQU_WET) ||
					  colType.typeEquals(KnownDataTypes.FCO2_WATER_SST_WET) || 

					  colType.typeEquals(KnownDataTypes.XCO2_ATM_DRY_ACTUAL) || 
					  colType.typeEquals(KnownDataTypes.XCO2_ATM_DRY_INTERP) || 
					  colType.typeEquals(KnownDataTypes.PCO2_ATM_WET_ACTUAL) || 
					  colType.typeEquals(KnownDataTypes.PCO2_ATM_WET_INTERP) || 
					  colType.typeEquals(KnownDataTypes.FCO2_ATM_WET_ACTUAL) || 
					  colType.typeEquals(KnownDataTypes.FCO2_ATM_WET_INTERP) || 

					  colType.typeEquals(KnownDataTypes.DELTA_XCO2) || 
					  colType.typeEquals(KnownDataTypes.DELTA_PCO2) || 
					  colType.typeEquals(KnownDataTypes.DELTA_FCO2) || 

					  colType.typeEquals(KnownDataTypes.RELATIVE_HUMIDITY) || 
					  colType.typeEquals(KnownDataTypes.SPECIFIC_HUMIDITY) || 
					  colType.typeEquals(KnownDataTypes.SHIP_SPEED) || 
					  colType.typeEquals(KnownDataTypes.SHIP_DIRECTION) || 
					  colType.typeEquals(KnownDataTypes.WIND_SPEED_TRUE) || 
					  colType.typeEquals(KnownDataTypes.WIND_SPEED_RELATIVE) || 
					  colType.typeEquals(KnownDataTypes.WIND_DIRECTION_TRUE) || 
					  colType.typeEquals(KnownDataTypes.WIND_DIRECTION_RELATIVE) ||

					  colType.typeEquals(KnownDataTypes.WOCE_CO2_WATER) ||
					  colType.typeEquals(KnownDataTypes.WOCE_CO2_ATM) ) {
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
			else if ( colType.typeEquals(KnownDataTypes.COMMENT_WOCE_CO2_WATER) ||
					  colType.typeEquals(KnownDataTypes.COMMENT_WOCE_CO2_ATM) ||
					  colType.typeEquals(DataColumnType.OTHER) ) {
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
		// Add the ordered complete timestamp element to the root element
		Element timestampElement = new Element(ColumnSpec.DATE_COLUMN_ELEMENT);
		for (int k = 0; k < 6; k++) {
			if ( timestampElements[k] == null )
				break;
			timestampElement.addContent(timestampElements[k]);
		}
		rootElement.addContent(timestampElement);
		// Create the cruise column specifications document
		Document cruiseDoc = new Document(rootElement);

		// Create the column specifications object for the sanity checker
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

		// Get the OME metadata for this cruise
		Document oldOmeDoc;
		OmeMetadata oldOmeMData;
		File omeFile = metadataHandler.getMetadataFile(expocode, DashboardMetadata.OME_FILENAME);
		if ( omeFile.exists() ) {
			try {
				oldOmeDoc = (new SAXBuilder()).build(omeFile);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Problems reading the OME XML " + 
						omeFile.getName() + "\n    " + ex.getMessage());
			}
			oldOmeMData = new OmeMetadata(expocode);
			try {
				oldOmeMData.assignFromOmeXmlDoc(oldOmeDoc);
			} catch (BadEntryNameException | InvalidConflictException ex) {
				throw new IllegalArgumentException("Unknown entry in the OME XML " + 
						omeFile.getName() + "\n    " + ex.getMessage());
			}
		}
		else {
			oldOmeDoc = null;
			oldOmeMData = new OmeMetadata(expocode);
		}

		// Create the SanityChecker for this cruise
		SanityChecker checker;
		try {
			checker = new SanityChecker(expocode, oldOmeMData, colSpec, 
										cruiseData.getDataValues(), dateFormat);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Sanity Checker Exception: " + ex.getMessage());
		}

		// Run the SanityChecker on this data and get the results
		Output output = checker.process();

		// Get the OME metadata that was updated from the data
		OmeMetadata updatedOmeMData = output.getMetadata();
		// Set the expocode to force the assignment of other fields associated with the expocode
		updatedOmeMData.setExpocode(expocode);
		updatedOmeMData.setDraft( ! updatedOmeMData.isAcceptable() );

		// Check if this OME metadata has any changes
		boolean saveOmeMData;
		if ( oldOmeDoc != null ) {
			Document updatedOmeDoc = updatedOmeMData.createOmeXmlDoc();
			// Document.equals is just "==", so useless; instead compare XML strings from the Documents
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLOutputter xmlWriter = new XMLOutputter(Format.getCompactFormat());
			try {
				try {
					xmlWriter.output(oldOmeDoc, out);
					String oldXmlString = out.toString();
					out.reset();
					xmlWriter.output(updatedOmeDoc, out);
					String updatedXmlString = out.toString();
					if ( oldXmlString.equals(updatedXmlString) ) {
						saveOmeMData = false;
					}
					else {
						saveOmeMData = true;
					}
				} finally {
					out.close();
				}
			} catch (IOException e) {
				throw new RuntimeException("Unexpected IOException writing to a ByteArrayOutputStream");
			}
		}
		else {
			saveOmeMData = true;
		}

		if ( saveOmeMData ) {
			// Save the updated OME metadata
			String timestamp = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z").print(new DateTime());
			DashboardOmeMetadata dashOmeMData = new DashboardOmeMetadata(updatedOmeMData, 
					timestamp, cruiseData.getOwner(), cruiseData.getVersion());

			String message = "Update of OME metadata from cruise checker";
			metadataHandler.saveMetadataInfo(dashOmeMData, message, false);
			metadataHandler.saveAsOmeXmlDoc(dashOmeMData, message);
		}

		// Save the SanityChecker messages and assign WOCE flags in cruiseData
		msgHandler.saveCruiseMessages(cruiseData, output);

		// Count the rows of data with errors and only warnings, check if there 
		// were lon/lat/date/time problems and assign the data check status
		countWoceFlagsAndAssignStatus(cruiseData, colIndcs, output);

		return output;
	}

	/**
	 * Sanity-checks and standardizes the units in the data values,
	 * stored as strings, in the given cruise after removing any data
	 * rows known problems (ones which the PI has marked with a WOCE-4 
	 * flag).  The year, month, day, hour, minute, and second data 
	 * columns are appended to each data measurement (row, outer array) 
	 * if not already present.
	 *  
	 * @param cruiseData
	 * 		cruise data to be standardized
	 * @return
	 * 		true if the SanityChecker ran successfully and data had no geoposition errors.
	 * 		If no valid data after removing rows with known problems (PI WOCE-4), 
	 * 		false is returned and cruiseData.getNumDataRows will be zero.
	 */
	public boolean standardizeCruiseData(DashboardCruiseWithData cruiseData) {
		removeKnownProblemRows(cruiseData);
		// If all data removed, return false
		if ( cruiseData.getNumDataRows() < 1 )
			return false;

		// Run the SanityChecker to get the standardized data
		Output output;
		try {
			output = checkCruiseAndReturnOutput(cruiseData);
		} catch (IllegalArgumentException ex) {
			lastCheckProcessedOkay = false;
			return false;
		}
		if ( lastCheckHadGeopositionErrors || ! lastCheckProcessedOkay )
			return false;
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
		int woceCO2WaterColumnIndex = -1;
		int k = -1;
		for ( DataColumnType colType : dataColTypes ) {
			k++;
			if ( colType.typeEquals(KnownDataTypes.YEAR) ) {
				hasYearColumn = true;
			}
			else if ( colType.typeEquals(KnownDataTypes.MONTH_OF_YEAR) ) {
				hasMonthColumn = true;
			}
			else if ( colType.typeEquals(KnownDataTypes.DAY_OF_MONTH) ) {
				hasDayColumn = true;
			}
			else if ( colType.typeEquals(KnownDataTypes.HOUR_OF_DAY) ) {
				hasHourColumn = true;
			}
			else if ( colType.typeEquals(KnownDataTypes.MINUTE_OF_HOUR) ) {
				hasMinuteColumn = true;
			}
			else if ( colType.typeEquals(KnownDataTypes.SECOND_OF_MINUTE) ) {
				hasSecondColumn = true;
			}
			else if ( colType.typeEquals(KnownDataTypes.WOCE_CO2_WATER) ) {
				woceCO2WaterColumnIndex = k;
			}
		}

		// Add any missing time columns; 
		// directly modify the lists in the cruise data object
		ArrayList<String> userColNames = cruiseData.getUserColNames();
		ArrayList<String> dataColUnits = cruiseData.getDataColUnits();
		ArrayList<String> missingValues = cruiseData.getMissingValues();
		ArrayList<HashSet<Integer>> woceThreeRowIndices = cruiseData.getWoceThreeRowIndices();
		ArrayList<HashSet<Integer>> woceFourRowIndices = cruiseData.getWoceFourRowIndices();
		if ( ! hasYearColumn ) {
			dataColTypes.add(KnownDataTypes.YEAR);
			userColNames.add("Year");
			dataColUnits.add("");
			missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasMonthColumn ) {
			dataColTypes.add(KnownDataTypes.MONTH_OF_YEAR);
			userColNames.add("Month");
			dataColUnits.add("");
			missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasDayColumn ) {
			dataColTypes.add(KnownDataTypes.DAY_OF_MONTH);
			userColNames.add("Day");
			dataColUnits.add("");
			missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasHourColumn ) {
			dataColTypes.add(KnownDataTypes.HOUR_OF_DAY);
			userColNames.add("Hour");
			dataColUnits.add("");
			missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasMinuteColumn ) {
			dataColTypes.add(KnownDataTypes.MINUTE_OF_HOUR);
			userColNames.add("Minute");
			dataColUnits.add("");
			missingValues.add(Integer.toString(SocatCruiseData.INT_MISSING_VALUE));
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasSecondColumn ) {
			dataColTypes.add(KnownDataTypes.SECOND_OF_MINUTE);
			userColNames.add("Second");
			dataColUnits.add("");
			missingValues.add(Double.toString(SocatCruiseData.FP_MISSING_VALUE));
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( woceCO2WaterColumnIndex < 0 ) {
			dataColTypes.add(KnownDataTypes.WOCE_CO2_WATER);
			userColNames.add("WOCE FLag");
			dataColUnits.add("");
			missingValues.add("");
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
			Double longitude;
			Double latitude;
			Integer year;
			Integer month;
			Integer day;
			Integer hour;
			Integer minute;
			Double second;
			try {
				// Verify the longitude, latitude, and time are given
				longitude = stdVals.getLongitude();
				latitude = stdVals.getLatitude();
				if ( longitude.isNaN() || latitude.isNaN() )
					throw new IllegalArgumentException();
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
				// Problem with the lon/lat/date/time of this record
				// Should have been caught earlier, but just in case....
				lastCheckHadGeopositionErrors = true;
				return false;
			}
			// Fill in any supplemental date/time columns
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
			if ( woceCO2WaterColumnIndex < 0 )
				rowData.add("");
		}
		if ( woceCO2WaterColumnIndex < 0 )
			woceCO2WaterColumnIndex = dataColTypes.size() - 1;

		// Go through each row, converting data as needed
		stdRowIter = stdRowVals.iterator();
		for ( ArrayList<String> rowData : dataVals ) {
			SocatDataRecord stdVals;
			try {
				stdVals = stdRowIter.next();
			} catch ( NoSuchElementException ex ) {
				throw new IllegalArgumentException(
						"Unexpected mismatch in the number of rows of " +
						"original data and standardized data");
			}
			k = -1;
			for ( DataColumnType colType : dataColTypes ) {
				k++;
				if ( colType.typeEquals(KnownDataTypes.EXPOCODE) || 
					 colType.typeEquals(KnownDataTypes.CRUISE_NAME) || 
					 colType.typeEquals(KnownDataTypes.SHIP_NAME) || 
					 colType.typeEquals(KnownDataTypes.GROUP_NAME) || 
					 colType.typeEquals(KnownDataTypes.INVESTIGATOR_NAMES) || 

					 colType.typeEquals(KnownDataTypes.TIMESTAMP) || 
					 colType.typeEquals(KnownDataTypes.DATE) || 
					 colType.typeEquals(KnownDataTypes.YEAR) || 
					 colType.typeEquals(KnownDataTypes.MONTH_OF_YEAR) || 
					 colType.typeEquals(KnownDataTypes.DAY_OF_MONTH) || 
					 colType.typeEquals(KnownDataTypes.TIME_OF_DAY) ||  
					 colType.typeEquals(KnownDataTypes.HOUR_OF_DAY) || 
					 colType.typeEquals(KnownDataTypes.MINUTE_OF_HOUR) || 
					 colType.typeEquals(KnownDataTypes.SECOND_OF_MINUTE) ||

					 colType.typeEquals(KnownDataTypes.COMMENT_WOCE_CO2_WATER) ||
					 colType.typeEquals(KnownDataTypes.COMMENT_WOCE_CO2_ATM) ||
					 colType.typeEquals(DataColumnType.OTHER) ) {
					// Do not change
					;
				}
				else if ( colType.typeEquals(KnownDataTypes.LONGITUDE) ) {
					rowData.set(k, Double.toString(stdVals.getLongitude()));
				}
				else if ( colType.typeEquals(KnownDataTypes.LATITUDE) ) {
					rowData.set(k, Double.toString(stdVals.getLatitude()));
				}
				else if ( colType.typeEquals(KnownDataTypes.DAY_OF_YEAR) || 
						  colType.typeEquals(KnownDataTypes.SECOND_OF_DAY) || 
						  colType.typeEquals(KnownDataTypes.SAMPLE_DEPTH) || 
						  colType.typeEquals(KnownDataTypes.SALINITY) || 
						  colType.typeEquals(KnownDataTypes.EQUILIBRATOR_TEMPERATURE) || 
						  colType.typeEquals(KnownDataTypes.SEA_SURFACE_TEMPERATURE) || 
						  colType.typeEquals(KnownDataTypes.ATMOSPHERIC_TEMPERATURE) || 
						  colType.typeEquals(KnownDataTypes.EQUILIBRATOR_PRESSURE) || 
						  colType.typeEquals(KnownDataTypes.SEA_LEVEL_PRESSURE) || 

						  colType.typeEquals(KnownDataTypes.XCO2_WATER_TEQU_DRY) || 
						  colType.typeEquals(KnownDataTypes.XCO2_WATER_SST_DRY) || 
						  colType.typeEquals(KnownDataTypes.XCO2_WATER_TEQU_WET) || 
						  colType.typeEquals(KnownDataTypes.XCO2_WATER_SST_WET) || 
						  colType.typeEquals(KnownDataTypes.PCO2_WATER_TEQU_WET) || 
						  colType.typeEquals(KnownDataTypes.PCO2_WATER_SST_WET) || 
						  colType.typeEquals(KnownDataTypes.FCO2_WATER_TEQU_WET) || 
						  colType.typeEquals(KnownDataTypes.FCO2_WATER_SST_WET) || 

						  colType.typeEquals(KnownDataTypes.XCO2_ATM_DRY_ACTUAL) || 
						  colType.typeEquals(KnownDataTypes.XCO2_ATM_DRY_INTERP) || 
						  colType.typeEquals(KnownDataTypes.PCO2_ATM_WET_ACTUAL) || 
						  colType.typeEquals(KnownDataTypes.PCO2_ATM_WET_INTERP) || 
						  colType.typeEquals(KnownDataTypes.FCO2_ATM_WET_ACTUAL) || 
						  colType.typeEquals(KnownDataTypes.FCO2_ATM_WET_INTERP) || 

						  colType.typeEquals(KnownDataTypes.DELTA_XCO2) || 
						  colType.typeEquals(KnownDataTypes.DELTA_PCO2) || 
						  colType.typeEquals(KnownDataTypes.DELTA_FCO2) || 

						  colType.typeEquals(KnownDataTypes.XH2O_EQU) || 
						  colType.typeEquals(KnownDataTypes.RELATIVE_HUMIDITY) || 
						  colType.typeEquals(KnownDataTypes.SPECIFIC_HUMIDITY) || 
						  colType.typeEquals(KnownDataTypes.SHIP_SPEED) || 
						  colType.typeEquals(KnownDataTypes.SHIP_DIRECTION) || 
						  colType.typeEquals(KnownDataTypes.WIND_SPEED_TRUE) || 
						  colType.typeEquals(KnownDataTypes.WIND_SPEED_RELATIVE) || 
						  colType.typeEquals(KnownDataTypes.WIND_DIRECTION_TRUE) || 
						  colType.typeEquals(KnownDataTypes.WIND_DIRECTION_RELATIVE) ||

						  colType.typeEquals(KnownDataTypes.WOCE_CO2_WATER) || 
						  colType.typeEquals(KnownDataTypes.WOCE_CO2_ATM) ) {
					String chkName = DashboardUtils.STD_HEADER_NAMES.get(colType);
					SocatDataColumn stdCol = stdVals.getColumn(chkName);
					if ( stdCol == null )
						throw new IllegalArgumentException("SocatDataColumn not found for " + 
								chkName + " (column type " + colType + ")");
					String value = stdCol.getValue();
					rowData.set(k, value);
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

		// Get SanityChecker WOCE flags
		HashSet<Integer> errRows = new HashSet<Integer>();
		HashSet<Integer> warnRows = new HashSet<Integer>();
		getErrorAndWarnRows(errRows, warnRows, cruiseData);

		// Only add SanityChecker WOCE-4 flags for now 
		// (here and in the middle of CheckerMessageHandler.generateWoceEvents)
		for ( int rowIdx : errRows ) {
			dataVals.get(rowIdx).set(woceCO2WaterColumnIndex, "4");
		}

		return true;
	}

	/**
	 * Removes rows of data which have known problems 
	 * (those with a WOCE-4 flag assigned by the PI).
	 * 
	 * @param cruiseData
	 * 		cruise data to modify
	 */
	private void removeKnownProblemRows(DashboardCruiseWithData cruiseData) {
		ColumnIndices colIndcs = getColumnIndices(cruiseData.getDataColTypes());
		// Directly modify the data rows and data row indices for the WOCE flags
		ArrayList<ArrayList<String>> dataVals = cruiseData.getDataValues();
		int k = 0;
		int numRows = cruiseData.getNumDataRows();
		while ( k < numRows ) {
			ArrayList<String> dataRow = dataVals.get(k);
			// Remove the row if the PI has marked it as bad
			if ( colIndcs.woceCO2WaterIndex >= 0 ) {
				try {
					int woceFlag = Integer.parseInt(dataRow.get(colIndcs.woceCO2WaterIndex));
					if ( woceFlag == 4 ) {
						dataVals.remove(k);
						numRows--;
						continue;
					}
				} catch (Exception ex) {
					// Assume a missing value
				}
			}
			if ( colIndcs.woceCO2AtmIndex >= 0 ) {
				try {
					int woceFlag = Integer.parseInt(dataRow.get(colIndcs.woceCO2AtmIndex));
					if ( woceFlag == 4 ) {
						dataVals.remove(k);
						numRows--;
						continue;
					}
				} catch (Exception ex) {
					// Assume a missing value
				}
			}

			// Row looks fine, so move on to the next row
			k++;
		}
		// Reset the record number of data rows
		cruiseData.setNumDataRows(numRows);
	}

	/**
	 * Counts and assigns the number of data rows with errors and with only 
	 * warnings.  Sets lastCheckProcessedOkay and lastCheckHadGeopostionErrors.
	 * Assigns the data check status.
	 * 
	 * @param cruise
	 * 		cruise to use
	 * @param colIndcs
	 * 		data column indices for the cruise
	 * @param processedOK
	 * 		did the SanityCheck run successfully?
	 */
	private void countWoceFlagsAndAssignStatus(DashboardCruise cruise, 
								ColumnIndices colIndcs, Output output) {
		lastCheckProcessedOkay = output.processedOK();
		lastCheckHadGeopositionErrors = false;
		ArrayList<HashSet<Integer>> woceFourSets = cruise.getWoceFourRowIndices();
		if ( (colIndcs.longitudeIndex < 0) ||
			 (colIndcs.latitudeIndex < 0) ||
			 ( ! woceFourSets.get(colIndcs.longitudeIndex).isEmpty() ) ||
			 ( ! woceFourSets.get(colIndcs.latitudeIndex).isEmpty() ) ||
			 ( (colIndcs.timestampIndex >= 0) && 
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
				! woceFourSets.get(colIndcs.secondOfDayIndex).isEmpty() ) ) {
			lastCheckHadGeopositionErrors = true;
		}
		if ( ! lastCheckHadGeopositionErrors ) {
			// Date/time errors may not be associated with any column
			// so check the sanity checker messages
			try {
				for ( Message msg : output.getMessages().getMessages() ) {
					String colName = msg.getColumnName();
					String summary = msg.getMessageType().getSummaryMessage(colName);
					if ( "Date/time could not be parsed".equals(summary) ||
						 "Times out of order".equals(summary) ) {
						lastCheckHadGeopositionErrors = true;
					}
				}
			} catch ( MessageException ex ) {
				throw new RuntimeException(ex);
			}
		}

		// Assign the number of data rows the SanityChecker found having 
		// errors but not marked as bad by the PI.
		// Assign the number of data rows the SanityChecker found having  
		// only warnings but not marked as bad or questionable by the PI
		HashSet<Integer> errRows = new HashSet<Integer>();
		HashSet<Integer> warnRows = new HashSet<Integer>();
		getErrorAndWarnRows(errRows, warnRows, cruise);
		int numErrorRows = errRows.size();
		int numWarnRows = warnRows.size();
		cruise.setNumErrorRows(numErrorRows);
		cruise.setNumWarnRows(numWarnRows);

		// Assign the data-check status message using the results of the sanity check
		if ( ! lastCheckProcessedOkay ) {
			cruise.setDataCheckStatus(DashboardUtils.CHECK_STATUS_UNACCEPTABLE);
		}
		else if ( lastCheckHadGeopositionErrors ) {
			cruise.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX +
					Integer.toString(numErrorRows) + " errors " + DashboardUtils.GEOPOSITION_ERRORS_MSG);
		}
		else if ( numErrorRows > 0 ) {
			cruise.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX +
					Integer.toString(numErrorRows) + " errors");
		}
		else if ( numWarnRows > 0 ) {
			cruise.setDataCheckStatus(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX +
					Integer.toString(numWarnRows) + " warnings");
		}
		else {
			cruise.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ACCEPTABLE);
		}
	}

	/**
	 * Assigns the indices of rows with SanityChecker errors not marked as
	 * bad by the PI, and the indices of rows with SanityChecker warnings, 
	 * but not errors, and not marked as bad or questionable by the PI.
	 * 
	 * @param errRows
	 * 		set to assign with indices of rows with SanityChecker errors 
	 * 		and not marked as bad by the PI
	 * @param warnRows
	 * 		set to assign with indices of rows with SanityChecker warnings 
	 * 		but not errors, and not marked as bad or questionable by the PI
	 * @param cruise
	 * 		SanityChecked cruise data to examine
	 */
	private void getErrorAndWarnRows(HashSet<Integer> errRows, 
			HashSet<Integer> warnRows, DashboardCruise cruise) {
		// Assign the number of data rows the SanityChecker found having 
		// errors but not marked as bad by the PI
		errRows.clear();
		for ( HashSet<Integer> rowIdxSet : cruise.getWoceFourRowIndices() )
			errRows.addAll(rowIdxSet);
		errRows.addAll(cruise.getNoColumnWoceFourRowIndices());
		errRows.removeAll(cruise.getUserWoceFourRowIndices());

		// Assign the number of data rows the SanityChecker found having  
		// only warnings but not marked as bad or questionable by the PI
		warnRows.clear();
		for ( HashSet<Integer> rowIdxSet : cruise.getWoceThreeRowIndices() )
			warnRows.addAll(rowIdxSet);
		warnRows.addAll(cruise.getNoColumnWoceThreeRowIndices());
		warnRows.removeAll(errRows);
		warnRows.removeAll(cruise.getUserWoceFourRowIndices());
		warnRows.removeAll(cruise.getUserWoceThreeRowIndices());
	}

	/**
	 * @return
	 * 		true if the last cruise check ran successfully
	 */
	public boolean checkProcessedOkay() {
		return lastCheckProcessedOkay;
	}

	/**
	 * @return
	 * 		true if the cruise last checked had longitude, latitude, or date/time errors
	 */
	public boolean hadGeopositionErrors() {
		return lastCheckHadGeopositionErrors;
	}

}
