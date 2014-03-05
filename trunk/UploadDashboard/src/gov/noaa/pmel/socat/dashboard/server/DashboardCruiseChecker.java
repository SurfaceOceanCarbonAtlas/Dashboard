/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

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
	 * Column names used by the sanity checker corresponding the {@link #STD_HEADER_NAMES}
	 */
	public static final EnumMap<DataColumnType,String> CHECKER_NAMES = 
			new EnumMap<DataColumnType,String>(DataColumnType.class);
	static {
		CHECKER_NAMES.put(DataColumnType.UNKNOWN, "");
		CHECKER_NAMES.put(DataColumnType.EXPOCODE, "EXPOCode");
		CHECKER_NAMES.put(DataColumnType.CRUISE_NAME, "cruise_name");
		CHECKER_NAMES.put(DataColumnType.TIMESTAMP, "date_time");
		CHECKER_NAMES.put(DataColumnType.DATE, "date");
		CHECKER_NAMES.put(DataColumnType.YEAR, "year");
		CHECKER_NAMES.put(DataColumnType.MONTH, "month");
		CHECKER_NAMES.put(DataColumnType.DAY, "day");
		CHECKER_NAMES.put(DataColumnType.TIME, "time");
		CHECKER_NAMES.put(DataColumnType.HOUR, "hour");
		CHECKER_NAMES.put(DataColumnType.MINUTE, "minute");
		CHECKER_NAMES.put(DataColumnType.SECOND, "second");
		CHECKER_NAMES.put(DataColumnType.LONGITUDE, "longitude");
		CHECKER_NAMES.put(DataColumnType.LATITUDE, "latitude");
		CHECKER_NAMES.put(DataColumnType.SAMPLE_DEPTH, "depth");
		CHECKER_NAMES.put(DataColumnType.SALINITY, "sal");
		CHECKER_NAMES.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "temperature_equi");
		CHECKER_NAMES.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "temp");
		CHECKER_NAMES.put(DataColumnType.EQUILIBRATOR_PRESSURE, "pressure_equi");
		CHECKER_NAMES.put(DataColumnType.SEA_LEVEL_PRESSURE, "pressure_atm");
		CHECKER_NAMES.put(DataColumnType.XCO2WATER_EQU, "xco2water_equ_dry");
		CHECKER_NAMES.put(DataColumnType.XCO2WATER_SST, "xco2water_sst_dry");
		CHECKER_NAMES.put(DataColumnType.PCO2WATER_EQU, "pco2water_equ_wet");
		CHECKER_NAMES.put(DataColumnType.PCO2WATER_SST, "pco2water_sst_wet");
		CHECKER_NAMES.put(DataColumnType.FCO2WATER_EQU, "fco2water_equ_wet");
		CHECKER_NAMES.put(DataColumnType.FCO2WATER_SST, "fco2water_sst_wet");
		CHECKER_NAMES.put(DataColumnType.XCO2_ATM, "xco2_air");
		CHECKER_NAMES.put(DataColumnType.PCO2_ATM, "pco2_air");
		CHECKER_NAMES.put(DataColumnType.FCO2_ATM, "fco2_air");
		CHECKER_NAMES.put(DataColumnType.SHIP_SPEED, "ship_speed");
		CHECKER_NAMES.put(DataColumnType.SHIP_DIRECTION, "ship_dir");
		CHECKER_NAMES.put(DataColumnType.WIND_SPEED_TRUE, "wind_speed_true");
		CHECKER_NAMES.put(DataColumnType.WIND_SPEED_RELATIVE, "wind_speed_rel");
		CHECKER_NAMES.put(DataColumnType.WIND_DIRECTION_TRUE, "wind_dir_true");
		CHECKER_NAMES.put(DataColumnType.WIND_DIRECTION_RELATIVE, "wind_dir_rel");
	}

	/*
	 * SanityChecker version of the strings for above data units when not the same strings
	 */
	private static final ArrayList<String> CHECKER_LONGITUDE_UNITS = new ArrayList<String>(Arrays.asList("decimal_degrees"));
	private static final ArrayList<String> CHECKER_LATITUDE_UNITS = new ArrayList<String>(Arrays.asList("decimal_degrees"));
	private static final ArrayList<String> CHECKER_SALINITY_UNITS = new ArrayList<String>(Arrays.asList("psu"));
	private static final ArrayList<String> CHECKER_TEMPERATURE_UNITS = new ArrayList<String>(Arrays.asList("degC", "Kelvin", "degF"));
	private static final ArrayList<String> CHECKER_XCO2_UNITS = new ArrayList<String>(Arrays.asList("ppm"));
	private static final ArrayList<String> CHECKER_DIRECTION_UNITS = new ArrayList<String>(Arrays.asList("decimal_degrees"));

	/**
	 * Data units used by the sanity checker corresponding to {@link #STD_DATA_UNITS}
	 */
	public static final EnumMap<DataColumnType,ArrayList<String>> CHECKER_DATA_UNITS = 
			new EnumMap<DataColumnType,ArrayList<String>>(DataColumnType.class);
	static {
		CHECKER_DATA_UNITS.put(DataColumnType.UNKNOWN, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.EXPOCODE, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.CRUISE_NAME, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.TIMESTAMP, new ArrayList<String>(Arrays.asList(
				"YYYY-MM-DD", "MM/DD/YYYY", "DD/MM/YYYY")));
		CHECKER_DATA_UNITS.put(DataColumnType.DATE, new ArrayList<String>(Arrays.asList(
				"YYYY-MM-DD", "MM/DD/YYYY", "DD/MM/YYYY")));
		CHECKER_DATA_UNITS.put(DataColumnType.YEAR, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.MONTH, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.DAY, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.TIME, new ArrayList<String>(Arrays.asList("HH:MM:SS")));
		CHECKER_DATA_UNITS.put(DataColumnType.HOUR, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.MINUTE, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SECOND, DashboardUtils.NO_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.LONGITUDE, CHECKER_LONGITUDE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.LATITUDE, CHECKER_LATITUDE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH, DashboardUtils.DEPTH_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SALINITY, CHECKER_SALINITY_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, CHECKER_TEMPERATURE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, CHECKER_TEMPERATURE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE, DashboardUtils.PRESSURE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE, DashboardUtils.PRESSURE_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2WATER_EQU, CHECKER_XCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2WATER_SST, CHECKER_XCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2WATER_EQU, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2WATER_SST, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2WATER_EQU, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2WATER_SST, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2_ATM, DashboardUtils.XCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2_ATM, DashboardUtils.PCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2_ATM, DashboardUtils.FCO2_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SHIP_SPEED, DashboardUtils.SHIP_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.SHIP_DIRECTION, CHECKER_DIRECTION_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_SPEED_TRUE, DashboardUtils.WIND_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_SPEED_RELATIVE, DashboardUtils.WIND_SPEED_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_TRUE, CHECKER_DIRECTION_UNITS);
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_RELATIVE, CHECKER_DIRECTION_UNITS);
	}

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
		String dateFormat = "YYYY-MM-DD ";

		// Column indices which are assigned issues with ambiguous sources;
		// most likely to be date/time issues.
		HashSet<Integer> ambiguousColumnIndices = new HashSet<Integer>();

		// Specify the columns in this cruise data
		Element rootElement = new Element("Expocode_" + cruiseData.getExpocode());
		Element timestampElement = new Element(ColumnSpec.DATE_COLUMN_ELEMENT);
		int k = -1;
		for ( DataColumnType colType : cruiseData.getDataColTypes() ) {
			k++;
			if ( colType.equals(DataColumnType.UNKNOWN) ) {
				// Might happen in multiple file upload
				throw new IllegalArgumentException(
						"Data type not defined for column " + Integer.toString(k+1) + 
						": " + cruiseData.getUserColNames().get(k));
			}
			else if ( colType.equals(DataColumnType.TIMESTAMP) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.SINGLE_DATE_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Set the date format
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = CHECKER_DATA_UNITS.get(colType).get(idx);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.DATE) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.DATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Set the date format
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = CHECKER_DATA_UNITS.get(colType).get(idx);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.YEAR) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.MONTH) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.DAY) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.TIME) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.HOUR) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.HOUR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.MINUTE) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.MINUTE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.SECOND) ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType.equals(DataColumnType.LONGITUDE) || 
					  colType.equals(DataColumnType.LATITUDE) || 
					  colType.equals(DataColumnType.SAMPLE_DEPTH) || 
					  colType.equals(DataColumnType.SALINITY) || 
					  colType.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) || 
					  colType.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) || 
					  colType.equals(DataColumnType.EQUILIBRATOR_PRESSURE) || 
					  colType.equals(DataColumnType.SEA_LEVEL_PRESSURE) || 
					  colType.equals(DataColumnType.XCO2WATER_EQU) ||
					  colType.equals(DataColumnType.XCO2WATER_SST) ||
					  colType.equals(DataColumnType.PCO2WATER_EQU) ||
					  colType.equals(DataColumnType.PCO2WATER_SST) ||
					  colType.equals(DataColumnType.FCO2WATER_EQU) ||
					  colType.equals(DataColumnType.FCO2WATER_SST) || 
					  colType.equals(DataColumnType.XCO2_ATM) || 
					  colType.equals(DataColumnType.PCO2_ATM) || 
					  colType.equals(DataColumnType.FCO2_ATM) || 
					  colType.equals(DataColumnType.SHIP_SPEED) || 
					  colType.equals(DataColumnType.SHIP_DIRECTION) || 
					  colType.equals(DataColumnType.WIND_SPEED_TRUE) || 
					  colType.equals(DataColumnType.WIND_SPEED_RELATIVE) || 
					  colType.equals(DataColumnType.WIND_DIRECTION_TRUE) || 
					  colType.equals(DataColumnType.WIND_DIRECTION_RELATIVE) ) {
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
				// Standard SOCAT column name for the checker
				Element columnElement = new Element(ColumnSpec.SOCAT_COLUMN_ELEMENT); 
				columnElement.setAttribute(ColumnSpec.SOCAT_COLUMN_NAME_ATTRIBUTE, 
						CHECKER_NAMES.get(colType));
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
				// Element specifying the missing value of the column
				// Add this column description to the root element
				rootElement.addContent(columnElement);
			}
			else if ( colType.equals(DataColumnType.EXPOCODE) || 
					  colType.equals(DataColumnType.CRUISE_NAME) || 
					  colType.equals(DataColumnType.DELTA_PCO2) || 
					  colType.equals(DataColumnType.DELTA_FCO2) || 
					  colType.equals(DataColumnType.WOA_SALINITY) || 
					  colType.equals(DataColumnType.NCEP_SEA_LEVEL_PRESSURE) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_PEQ_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_PEQ_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_PCO2_TEQ_PEQ_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_PCO2_SST_PEQ_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_FCO2_TEQ_PEQ_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_FCO2_SST_PEQ_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_PCO2_TEQ_NCP_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_PCO2_SST_NCP_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_PEQ_WOA) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_PEQ_WOA) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_NCP_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_NCP_SAL) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_NCP_WOA) || 
					  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_NCP_WOA) || 
					  colType.equals(DataColumnType.FCO2_REC) || 
					  colType.equals(DataColumnType.FCO2_REC_SOURCE) || 
					  colType.equals(DataColumnType.DELTA_TEMPERATURE) || 
					  colType.equals(DataColumnType.REGION_ID) || 
					  colType.equals(DataColumnType.SECONDS_1970) || 
					  colType.equals(DataColumnType.DAYS_1970) || 
					  colType.equals(DataColumnType.DAY_OF_YEAR) || 
					  colType.equals(DataColumnType.CALC_SHIP_SPEED) || 
					  colType.equals(DataColumnType.ETOPO2) || 
					  colType.equals(DataColumnType.GVCO2) || 
					  colType.equals(DataColumnType.DISTANCE_TO_LAND) || 
					  colType.equals(DataColumnType.FCO2_REC_WOCE_FLAG) ) {
				// Unchecked column types at this time - just ignore their presence
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
		File name = new File(cruiseData.getExpocode());
		Logger logger = Logger.getLogger("Sanity Checker - " + 
				cruiseData.getExpocode());
		if ( Level.DEBUG.isGreaterOrEqual(logger.getEffectiveLevel()) ) {
			logger.debug("cruise columns specifications document:\n" + 
					(new XMLOutputter(Format.getPrettyFormat()))
					.outputString(cruiseDoc));
		}
		ColumnSpec colSpec;
		try {
			colSpec = new ColumnSpec(name, cruiseDoc, convConfig, logger);
		} catch (InvalidColumnSpecException ex) {
			throw new IllegalArgumentException(
					"Unexpected ColumnSpec exception: " + ex.getMessage());
		};

		// Create the SanityChecker for this cruise
		SanityChecker checker;
		try {
			checker = new SanityChecker(cruiseData.getExpocode(), metadataInput, 
					colSpec, cruiseData.getDataValues(), dateFormat);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Sanity Checker Exception: " + ex.getMessage());
		}

		// Run the SanityChecker on this data and get the results
		Output output = checker.process();
		if ( ! output.processedOK() ) {
			cruiseData.setNumErrorMsgs(0);
			cruiseData.setNumWarnMsgs(0);
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_UNACCEPTABLE);
		}
		else if ( output.hasErrors() ) {
			int numErrors = 0;
			int numWarns = 0;
			for ( Message msg : output.getMessages().getMessages() ) {
				if ( msg.isError() )
					numErrors++;
				else if ( msg.isWarning() )
					numWarns++;
			}
			cruiseData.setNumErrorMsgs(numErrors);
			cruiseData.setNumWarnMsgs(numWarns);
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX +
					Integer.toString(numErrors) + " errors");
		}
		else if ( output.hasWarnings() ) {
			int numWarns = 0;
			for ( Message msg : output.getMessages().getMessages() )
				if ( msg.isWarning() )
					numWarns++;
			cruiseData.setNumErrorMsgs(0);
			cruiseData.setNumWarnMsgs(numWarns);
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX +
					Integer.toString(numWarns) + " warnings");
		}
		else {
			cruiseData.setNumErrorMsgs(0);
			cruiseData.setNumWarnMsgs(0);
			cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ACCEPTABLE);
		}

		// Clear all WOCE flags, then set those from the current set of message
		for ( HashSet<Integer> rowIdxSet : cruiseData.getWoceThreeRowIndices() )
			rowIdxSet.clear();
		for ( HashSet<Integer> rowIdxSet : cruiseData.getWoceFourRowIndices() )
			rowIdxSet.clear();
		for ( Message msg : output.getMessages().getMessages() )
			processMessage(cruiseData, msg, ambiguousColumnIndices);

		return output;
	}
	
	/**
	 * Assigns the WOCE-3 or WOCE-4 flag associated with this message 
	 * to the cruise.
	 * 
	 * @param cruiseData
	 * 		cruise with data to assign
	 * @param msg
	 * 		SanityChecker message
	 * @param ambiguousColumnIndices
	 * 		column indices to use for messages with a negative (invalid) 
	 * 		column number, indicating an ambiguous source of the issue
	 */
	private void processMessage(DashboardCruiseWithData cruiseData, 
			Message msg, HashSet<Integer> ambiguousColumnIndices) {
		int rowIdx = msg.getLineIndex();
		if ( (rowIdx <= 0) || (rowIdx > cruiseData.getNumDataRows()) )
			throw new RuntimeException("Unexpected row number of " + 
					Integer.toString(rowIdx) + " in the sanity checker message\n" +
					"    " + msg.toString());
		// Change row number to row index
		rowIdx--;
		int colIdx = msg.getInputItemIndex();
		if ( (colIdx == 0) || (colIdx > cruiseData.getDataColTypes().size()) )
			throw new RuntimeException("Unexpected input column number of " + 
					Integer.toString(rowIdx) + " in the sanity checker message\n" +
					"    " + msg.toString());
		// Change column number to column index; 
		// negative numbers indicate an ambiguous source of error
		if ( colIdx > 0 )
			colIdx--;

		if ( msg.isError() ) {
			// Erroneous data value
			if ( colIdx < 0 ) {
				// Associate ambiguous errors with ambiguous column indices
				for ( int timeColIdx : ambiguousColumnIndices )
					cruiseData.getWoceFourRowIndices().get(timeColIdx).add(rowIdx);
			}
			else {
				cruiseData.getWoceFourRowIndices().get(colIdx).add(rowIdx);
			}
		}
		else if ( msg.isWarning() ) {
			// Questionable data value
			if ( colIdx < 0 ) {
				// If the message is concerning the timestamp, it may not 
				// have a column index.  So associate the WOCE flag with 
				// all timestamp columns.
				for ( int timeColIdx : ambiguousColumnIndices )
					cruiseData.getWoceThreeRowIndices().get(timeColIdx).add(rowIdx);
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

	public Output standardizeCruiseData(DashboardCruiseWithData cruiseData) 
											throws IllegalArgumentException {
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
				else if ( colType.equals(DataColumnType.SAMPLE_DEPTH) || 
						  colType.equals(DataColumnType.SALINITY) || 
						  colType.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) || 
						  colType.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) || 
						  colType.equals(DataColumnType.EQUILIBRATOR_PRESSURE) || 
						  colType.equals(DataColumnType.SEA_LEVEL_PRESSURE) || 
						  colType.equals(DataColumnType.XCO2WATER_EQU) || 
						  colType.equals(DataColumnType.XCO2WATER_SST) || 
						  colType.equals(DataColumnType.PCO2WATER_EQU) || 
						  colType.equals(DataColumnType.PCO2WATER_SST) || 
						  colType.equals(DataColumnType.FCO2WATER_EQU) || 
						  colType.equals(DataColumnType.FCO2WATER_SST) || 
						  colType.equals(DataColumnType.XCO2_ATM) || 
						  colType.equals(DataColumnType.PCO2_ATM) || 
						  colType.equals(DataColumnType.FCO2_ATM) || 
						  colType.equals(DataColumnType.SHIP_SPEED) || 
						  colType.equals(DataColumnType.SHIP_DIRECTION) || 
						  colType.equals(DataColumnType.WIND_SPEED_TRUE) || 
						  colType.equals(DataColumnType.WIND_SPEED_RELATIVE) || 
						  colType.equals(DataColumnType.WIND_DIRECTION_TRUE) || 
						  colType.equals(DataColumnType.WIND_DIRECTION_RELATIVE) ) {
					String chkName = CHECKER_NAMES.get(colType);
					SocatDataColumn stdCol = stdVals.getColumn(chkName);
					if ( stdCol == null )
						throw new NullPointerException("SocatDataColumn not found for " + chkName + " (column type " + colType + ")");
					String value = stdCol.getValue();
					rowData.set(k, value);
				}
				else if ( colType.equals(DataColumnType.EXPOCODE) || 
						  colType.equals(DataColumnType.CRUISE_NAME) || 
						  colType.equals(DataColumnType.DELTA_PCO2) || 
						  colType.equals(DataColumnType.DELTA_FCO2) || 
						  colType.equals(DataColumnType.WOA_SALINITY) || 
						  colType.equals(DataColumnType.NCEP_SEA_LEVEL_PRESSURE) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_PEQ_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_PEQ_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_PCO2_TEQ_PEQ_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_PCO2_SST_PEQ_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_FCO2_TEQ_PEQ_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_FCO2_SST_PEQ_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_PCO2_TEQ_NCP_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_PCO2_SST_NCP_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_PEQ_WOA) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_PEQ_WOA) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_NCP_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_NCP_SAL) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_TEQ_NCP_WOA) || 
						  colType.equals(DataColumnType.FCO2_FROM_XCO2_SST_NCP_WOA) || 
						  colType.equals(DataColumnType.FCO2_REC) || 
						  colType.equals(DataColumnType.FCO2_REC_SOURCE) || 
						  colType.equals(DataColumnType.DELTA_TEMPERATURE) || 
						  colType.equals(DataColumnType.REGION_ID) || 
						  colType.equals(DataColumnType.SECONDS_1970) || 
						  colType.equals(DataColumnType.DAYS_1970) || 
						  colType.equals(DataColumnType.DAY_OF_YEAR) || 
						  colType.equals(DataColumnType.CALC_SHIP_SPEED) || 
						  colType.equals(DataColumnType.ETOPO2) || 
						  colType.equals(DataColumnType.GVCO2) || 
						  colType.equals(DataColumnType.DISTANCE_TO_LAND) || 
						  colType.equals(DataColumnType.FCO2_REC_WOCE_FLAG) ) {
					// Unchecked column types at this time so they have not changed
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
