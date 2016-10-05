/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import uk.ac.exeter.QCRoutines.data.DataColumn;
import uk.ac.exeter.QCRoutines.data.DataRecordException;
import uk.ac.exeter.QCRoutines.messages.Message;
import uk.ac.exeter.QCRoutines.messages.ParsingMessages.DateTimeMessage;
import uk.ac.uea.socat.omemetadata.BadEntryNameException;
import uk.ac.uea.socat.omemetadata.InvalidConflictException;
import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.SanityChecker;
import uk.ac.uea.socat.sanitychecker.config.BaseConfig;
import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.InvalidColumnSpecException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

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
			if ( DashboardServerUtils.TIMESTAMP.typeNameEquals(colType) )
				colIndcs.timestampIndex = k;
			else if ( DashboardServerUtils.DATE.typeNameEquals(colType) )
				colIndcs.dateIndex = k;
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) )
				colIndcs.yearIndex = k;
			else if ( DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(colType) )
				colIndcs.monthIndex = k;
			else if ( DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(colType) )
				colIndcs.dayIndex = k;
			else if ( DashboardServerUtils.TIME_OF_DAY.typeNameEquals(colType) )
				colIndcs.timeIndex = k;
			else if ( DashboardServerUtils.HOUR_OF_DAY.typeNameEquals(colType) )
				colIndcs.hourIndex = k;
			else if ( DashboardServerUtils.MINUTE_OF_HOUR.typeNameEquals(colType) )
				colIndcs.minuteIndex = k;
			else if ( DashboardServerUtils.SECOND_OF_MINUTE.typeNameEquals(colType) )
				colIndcs.secondIndex = k;
			else if ( DashboardServerUtils.DAY_OF_YEAR.typeNameEquals(colType) )
				colIndcs.dayOfYearIndex = k;
			else if ( DashboardServerUtils.SECOND_OF_DAY.typeNameEquals(colType) )
				colIndcs.secondOfDayIndex = k;

			else if ( DashboardServerUtils.LONGITUDE.typeNameEquals(colType) )
				colIndcs.longitudeIndex = k;
			else if ( DashboardServerUtils.LATITUDE.typeNameEquals(colType) )
				colIndcs.latitudeIndex = k;

			else if ( SocatTypes.WOCE_CO2_WATER.typeNameEquals(colType) )
				colIndcs.woceCO2WaterIndex = k;
			else if ( SocatTypes.WOCE_CO2_ATM.typeNameEquals(colType) )
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
	 * Data units used by the sanity checker where they do not match
	 * those already specified for the type.
	 */
	private static final HashMap<String,ArrayList<String>> CHECKER_DATA_UNITS = 
			new HashMap<String,ArrayList<String>>();
	static {
		final ArrayList<String> checkerTimestampDateUnits = 
				new ArrayList<String>(DashboardUtils.TIMESTAMP_UNITS.size());
		for ( String fmt : DashboardUtils.TIMESTAMP_UNITS ) 
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

		CHECKER_DATA_UNITS.put(DashboardServerUtils.TIMESTAMP.getVarName(), checkerTimestampDateUnits);

		CHECKER_DATA_UNITS.put(DashboardServerUtils.LONGITUDE.getVarName(), checkerLongitudeUnits);

		CHECKER_DATA_UNITS.put(DashboardServerUtils.LATITUDE.getVarName(), checkerLatitudeUnits);

		CHECKER_DATA_UNITS.put(SocatTypes.SALINITY.getVarName(), checkerSalinityUnits);

		CHECKER_DATA_UNITS.put(SocatTypes.TEQU.getVarName(), checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(SocatTypes.SST.getVarName(), checkerTemperatureUnits);
		CHECKER_DATA_UNITS.put(SocatTypes.TATM.getVarName(), checkerTemperatureUnits);

		CHECKER_DATA_UNITS.put(SocatTypes.XCO2_WATER_TEQU_DRY.getVarName(), checkerXCO2Units);
		CHECKER_DATA_UNITS.put(SocatTypes.XCO2_WATER_SST_DRY.getVarName(), checkerXCO2Units);
		CHECKER_DATA_UNITS.put(SocatTypes.XCO2_WATER_TEQU_WET.getVarName(), checkerXCO2Units);
		CHECKER_DATA_UNITS.put(SocatTypes.XCO2_WATER_SST_WET.getVarName(), checkerXCO2Units);
		CHECKER_DATA_UNITS.put(SocatTypes.XCO2_ATM_DRY_ACTUAL.getVarName(), checkerXCO2Units);
		CHECKER_DATA_UNITS.put(SocatTypes.XCO2_ATM_DRY_INTERP.getVarName(), checkerXCO2Units);
		CHECKER_DATA_UNITS.put(SocatTypes.DELTA_XCO2.getVarName(), checkerXCO2Units);

		CHECKER_DATA_UNITS.put(SocatTypes.SHIP_DIRECTION.getVarName(), checkerDirectionUnits);
		CHECKER_DATA_UNITS.put(SocatTypes.WIND_DIRECTION_TRUE.getVarName(), checkerDirectionUnits);
		CHECKER_DATA_UNITS.put(SocatTypes.WIND_DIRECTION_RELATIVE.getVarName(), checkerDirectionUnits);
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
			if ( DashboardServerUtils.UNKNOWN.typeNameEquals(colType) ) {
				// Might happen in multiple file upload
				throw new IllegalArgumentException(
						"Data type not defined for column " + Integer.toString(k+1) + 
						": " + cruiseData.getUserColNames().get(k));
			}
			else if ( DashboardServerUtils.OTHER.typeNameEquals(colType) ||
					  colType.getVarName().toLowerCase().startsWith("comment") ) {
				// Unchecked data 
				;
			}
			// DATETIME_TIMESTAMP
			else if ( DashboardServerUtils.TIMESTAMP.typeNameEquals(colType) && 
					DateTimeType.DATETIME_TIMESTAMP.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.SINGLE_DATE_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
				int idx = colType.getSelectedUnitIndex();
				dateFormat = CHECKER_DATA_UNITS.get(colType.getVarName()).get(idx);
			}
			// DATETIME_DATA_TIME
			else if ( DashboardServerUtils.DATE.typeNameEquals(colType) && 
					  DateTimeType.DATETIME_DATE_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.DATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
				int idx = colType.getSelectedUnitIndex();
				dateFormat = colType.getUnits().get(idx);
			}
			else if ( DashboardServerUtils.TIME_OF_DAY.typeNameEquals(colType) && 
					  DateTimeType.DATETIME_DATE_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			// DATETIME_YEAR_DAY_SEC
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DAY_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDS_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DAY_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDS_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
				// assign the value for Jan 1 
				userElement = new Element(ColumnSpec.JAN_FIRST_INDEX_ELEMENT);
				String units = colType.getUnits().get(colType.getSelectedUnitIndex());
				if ( "Jan1=1.0".equals(units) )
					userElement.setText("1");
				else if ( "Jan1=0.0".equals(units) )
					userElement.setText("0");
				else
					throw new IllegalArgumentException("Unexpected \"units\" of '" +
							units + "' for day-of-year");
				timestampElements[3] = userElement;
			}
			else if ( DashboardServerUtils.SECOND_OF_DAY.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DAY_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDS_SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			// DATETIME_YEAR_DECIMAL_DAY
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DECIMAL_DAY.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDJD_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DECIMAL_DAY.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDJD_DECIMAL_JDATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
				// assign the value for Jan 1 
				userElement = new Element(ColumnSpec.YDJD_JAN_FIRST_INDEX_ELEMENT);
				String units = colType.getUnits().get(colType.getSelectedUnitIndex());
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
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			else if ( DashboardServerUtils.HOUR_OF_DAY.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.HOUR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[3] = userElement;
			}
			else if ( DashboardServerUtils.MINUTE_OF_HOUR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.MINUTE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[4] = userElement;
			}
			else if ( DashboardServerUtils.SECOND_OF_MINUTE.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[5] = userElement;
			}
			// DATETIME_YEAR_MON_DAY_TIME
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			else if ( DashboardServerUtils.TIME_OF_DAY.typeNameEquals(colType) && 
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElements[3] = userElement;
			}
			// Not involved with date/time specification
			else {
				// Element specifying the units of the column
				Element unitsElement = new Element(ColumnSpec.INPUT_UNITS_ELEMENT_NAME);
				int idx = colType.getSelectedUnitIndex();
				// See if there are alternate unit strings for the checker for this data type
				ArrayList<String> checkerUnits = CHECKER_DATA_UNITS.get(colType.getVarName());
				if ( checkerUnits == null )
					checkerUnits = colType.getUnits();
				unitsElement.setText(checkerUnits.get(idx));
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.INPUT_COLUMN_ELEMENT_NAME);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				// Standard column name for the checker
				Element columnElement = new Element(ColumnSpec.SOCAT_COLUMN_ELEMENT); 
				columnElement.setAttribute(ColumnSpec.SOCAT_COLUMN_NAME_ATTRIBUTE, colType.getVarName());
				// Add the index and user name element, and the units element
				columnElement.addContent(userElement);
				columnElement.addContent(unitsElement);
				// Add the missing value if specified
				String missValue = colType.getSelectedMissingValue();
				if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(missValue) ) {
					Element missValElement = new Element(ColumnSpec.MISSING_VALUE_ELEMENT_NAME);
					missValElement.setText(missValue);
					columnElement.addContent(missValElement);
				}
				// Add this column description to the root element
				rootElement.addContent(columnElement);
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
		File omeFile = metadataHandler.getMetadataFile(expocode, DashboardUtils.OME_FILENAME);
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
			if ( DashboardServerUtils.YEAR.typeNameEquals(colType) ) {
				hasYearColumn = true;
			}
			else if ( DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(colType) ) {
				hasMonthColumn = true;
			}
			else if ( DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(colType) ) {
				hasDayColumn = true;
			}
			else if ( DashboardServerUtils.HOUR_OF_DAY.typeNameEquals(colType) ) {
				hasHourColumn = true;
			}
			else if ( DashboardServerUtils.MINUTE_OF_HOUR.typeNameEquals(colType) ) {
				hasMinuteColumn = true;
			}
			else if ( DashboardServerUtils.SECOND_OF_MINUTE.typeNameEquals(colType) ) {
				hasSecondColumn = true;
			}
			else if ( SocatTypes.WOCE_CO2_WATER.typeNameEquals(colType) ) {
				woceCO2WaterColumnIndex = k;
			}
		}

		// Add any missing time columns; 
		// directly modify the lists in the cruise data object
		ArrayList<String> userColNames = cruiseData.getUserColNames();
		ArrayList<HashSet<Integer>> woceThreeRowIndices = cruiseData.getWoceThreeRowIndices();
		ArrayList<HashSet<Integer>> woceFourRowIndices = cruiseData.getWoceFourRowIndices();
		if ( ! hasYearColumn ) {
			DataColumnType dctype = DashboardServerUtils.YEAR.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Year");
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasMonthColumn ) {
			DataColumnType dctype = DashboardServerUtils.MONTH_OF_YEAR.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Month");
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasDayColumn ) {
			DataColumnType dctype = DashboardServerUtils.DAY_OF_MONTH.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Day");
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasHourColumn ) {
			DataColumnType dctype = DashboardServerUtils.HOUR_OF_DAY.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Hour");
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasMinuteColumn ) {
			DataColumnType dctype = DashboardServerUtils.MINUTE_OF_HOUR.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Minute");
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( ! hasSecondColumn ) {
			DataColumnType dctype = DashboardServerUtils.SECOND_OF_MINUTE.duplicate();
			dctype.setSelectedMissingValue(Double.toString(DashboardUtils.FP_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Second");
			woceThreeRowIndices.add(new HashSet<Integer>());
			woceFourRowIndices.add(new HashSet<Integer>());
		}
		if ( woceCO2WaterColumnIndex < 0 ) {
			dataColTypes.add(SocatTypes.WOCE_CO2_WATER.duplicate());
			userColNames.add("WOCE flag");
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
				if ( colType.getDataClassName().equals(DashboardUtils.STRING_DATA_CLASS_NAME) ||
					 colType.getDataClassName().equals(DashboardUtils.CHAR_DATA_CLASS_NAME) ||
					 DashboardServerUtils.OTHER.typeNameEquals(colType) ||
					 DashboardServerUtils.TIMESTAMP.typeNameEquals(colType) ||   // just in case it changes away from String 
					 DashboardServerUtils.DATE.typeNameEquals(colType) ||   // just in case it changes away from String
					 DashboardServerUtils.YEAR.typeNameEquals(colType) || 
					 DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(colType) || 
					 DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(colType) || 
					 DashboardServerUtils.TIME_OF_DAY.typeNameEquals(colType) ||   //just in case it changes away from String
					 DashboardServerUtils.HOUR_OF_DAY.typeNameEquals(colType) || 
					 DashboardServerUtils.MINUTE_OF_HOUR.typeNameEquals(colType) || 
					 DashboardServerUtils.SECOND_OF_MINUTE.typeNameEquals(colType) ) {
					 // Do not change
					;
				}
				else if ( DashboardServerUtils.LONGITUDE.typeNameEquals(colType) ) {
					double longitude;
					try {
						longitude = stdVals.getLongitude();
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("Unable to get the longitude from the SanityChecker: " + 
								ex.getMessage(), ex);
					}
					rowData.set(k, Double.toString(longitude));
				}
				else if ( DashboardServerUtils.LATITUDE.typeNameEquals(colType) ) {
					double latitude;
					try {
						latitude = stdVals.getLatitude();
					} catch ( DataRecordException ex ) {
						throw new IllegalArgumentException("Unable to get the latitude from the SanityChecker: " + 
								ex.getMessage(), ex);
					}
					rowData.set(k, Double.toString(latitude));
				}
				else {
					String chkName = colType.getVarName();
					DataColumn stdCol;
					try {
						stdCol = stdVals.getColumn(chkName);
					} catch ( Exception ex ) {
						stdCol = null;
					}
					if ( stdCol == null )
						throw new IllegalArgumentException("Checker data column not found for " + 
								chkName + " (column type " + colType + ")");
					String value = stdCol.getValue();
					rowData.set(k, value);
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
			for ( Message msg : output.getMessages() ) {
				if ( msg instanceof DateTimeMessage ) {
					lastCheckHadGeopositionErrors = true;
					break;
				}
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
