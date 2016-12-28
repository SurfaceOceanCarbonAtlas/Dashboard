/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

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

import gov.noaa.pmel.dashboard.datatype.CharDashDataType;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

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
 * Class for working with the automated data checker 
 * 
 * @author Karl Smith
 */
public class DatasetChecker {

	/**
	 * Indices of data columns types of interest.
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
		int sampleDepthIndex = -1;
	}

	/**
	 * Returns a ColumnIndices assigned with the indices
	 * of the data column types of interest in the given list.
	 * 
	 * @param columnTypes
	 * 		data column types to use
	 * @return
	 * 		ColumnIndices with indices of data column types of interest
	 */
	private ColumnIndices getColumnIndices(ArrayList<DataColumnType> columnTypes) {
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
			else if ( DashboardServerUtils.SAMPLE_DEPTH.typeNameEquals(colType) )
				colIndcs.sampleDepthIndex = k;
		}
		return colIndcs;
	}

	/**
	 * Enumerated type indicating the source column type(s) for determining 
	 * the date and time of sample measurements.
	 */
	private enum DateTimeType {
		DATETIME_TIMESTAMP,
		DATETIME_DATE_TIME,
		DATETIME_YEAR_DAY_SEC,
		DATETIME_YEAR_DECIMAL_DAY,
		DATETIME_YEAR_MON_DAY_HR_MIN_SEC,
		DATETIME_YEAR_MON_DAY_TIME,
	}

	/**
	 * Data units used by the automated checker which are different  
	 * but equivalent to those already specified for the type.
	 */
	private static final HashMap<String,ArrayList<String>> CHECKER_DATA_UNITS ;
	static {
		// Checker timestamp units only specify the format of the date
		final ArrayList<String> checkerTimestampDateUnits = 
				new ArrayList<String>(DashboardUtils.TIMESTAMP_UNITS.size());
		for ( String fmt : DashboardUtils.TIMESTAMP_UNITS ) 
			checkerTimestampDateUnits.add(fmt.split(" ", 2)[0]);
		// Longitude and latitude units are prefixed with "decimal_"
		final ArrayList<String> checkerLongitudeUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees_east", "decimal_degrees_west"));
		final ArrayList<String> checkerLatitudeUnits = 
				new ArrayList<String>(Arrays.asList("decimal_degrees_north", "decimal_degrees_south"));
		// Create the map of renamed units
		CHECKER_DATA_UNITS = new HashMap<String,ArrayList<String>>();
		CHECKER_DATA_UNITS.put(DashboardServerUtils.TIMESTAMP.getVarName(), checkerTimestampDateUnits);
		CHECKER_DATA_UNITS.put(DashboardServerUtils.LONGITUDE.getVarName(), checkerLongitudeUnits);
		CHECKER_DATA_UNITS.put(DashboardServerUtils.LATITUDE.getVarName(), checkerLatitudeUnits);
	}

	private CheckerMessageHandler msgHandler;
	private MetadataFileHandler metadataHandler;
	private boolean lastCheckProcessedOkay;
	private boolean lastCheckHadGeopositionErrors;

	/**
	 * Initializes the automated data checker using the configuration files 
	 * named in the given properties files.
	 * 
	 * @param configFile
	 * 		properties file giving the names of the configuration files 
	 * 		for each automated data checker component
	 * @param checkerMsgHandler
	 * 		handler for automated data checker messages
	 * @param metaFileHandler
	 * 		handler for dashboard OME metadata files
	 * @throws IOException
	 * 		if the automated data checker has problems with a configuration file
	 */
	public DatasetChecker(File configFile, CheckerMessageHandler checkerMsgHandler, 
			MetadataFileHandler metaFileHandler) throws IOException {
		try {
			// Clear any previous configuration
			SanityCheckConfig.destroy();
			SocatColumnConfig.destroy();
			ColumnConversionConfig.destroy();
			MetadataConfig.destroy();
			BaseConfig.destroy();
			// Initialize the automated data checker from the configuration file
			SanityChecker.initConfig(configFile.getAbsolutePath());
		} catch ( Exception ex ) {
			throw new IOException("Invalid automated data checker configuration values specified in " + 
					configFile.getPath() + "\n    " + ex.getMessage());
		}
		if ( checkerMsgHandler == null )
			throw new NullPointerException("CheckerMsgHandler passed to DatasetChecker is null");
		msgHandler = checkerMsgHandler;
		if ( metaFileHandler == null )
			throw new NullPointerException("MetadataFileHandler passed to DatasetChecker is null");
		metadataHandler = metaFileHandler;
		lastCheckProcessedOkay = false;
		lastCheckHadGeopositionErrors = false;
	}

	/**
	 * Runs the automated data checker on the given dataset.  Saves the data check
	 * messages, sets the data check status, and assigned the data checker flags 
	 * from the results.
	 * 
	 * @param dataset
	 * 		dataset to check
	 * @return
	 * 		if the automcated data checker ran successfully
	 * @throws IllegalArgumentException
	 * 		if a data column type is unknown, 
	 * 		if an existing OME XML file is corrupt, or
	 * 		if the automated data checker throws an exception
	 */
	public boolean checkDataset(DashboardDatasetData dataset) throws IllegalArgumentException {
		ColumnIndices colIndcs = getColumnIndices(dataset.getDataColTypes());
		Output output = checkDatasetAndReturnOutput(dataset, colIndcs);
		return output.processedOK();
	}

	/**
	 * Runs the automated data checker on the given dataset.  Saves the data check
	 * messages, sets the data check status, and assigned the data checker flags 
	 * from the results.
	 * 
	 * @param dataset
	 * 		dataset to check
	 * @param colIndcs
	 * 		column indices of interest for this dataset
	 * @return
	 * 		the returned output from the automated data checker
	 * @throws IllegalArgumentException
	 * 		if a data column type is unknown, 
	 * 		if an existing OME XML file is corrupt, or
	 * 		if the automated data checker throws an exception
	 */
	private Output checkDatasetAndReturnOutput(DashboardDatasetData dataset, 
			ColumnIndices colIndcs) throws IllegalArgumentException {
		String datasetId = dataset.getDatasetId();

		// Get the data column units conversion object
		ColumnConversionConfig convConfig;
		try {
			convConfig = ColumnConversionConfig.getInstance();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unexpected ColumnConversionConfig exception: " + 
					ex.getMessage());
		}

		// Specify the default date format used in this cruise
		String dateFormat = "YYYY-MM-DD";

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

		ArrayList<DataColumnType> columnTypes = dataset.getDataColTypes();

		KnownDataTypes knownUserTypes;
		try {
			DashboardConfigStore configStore = DashboardConfigStore.get(false);
			knownUserTypes = configStore.getKnownUserDataTypes();
		} catch (IOException ex) {
			throw  new IllegalArgumentException("Unexpected error retrieving the dashboard configuration");
		}

		// Specify the columns in this cruise data
		Element rootElement = new Element("Expocode_" + dataset.getDatasetId());
		Element[] timestampElements = new Element[] { null, null, null, null, null, null };
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType dctype = columnTypes.get(k);
			DashDataType<?> colType = knownUserTypes.getDataType(dctype);
			if ( DashboardServerUtils.UNKNOWN.typeNameEquals(colType) ) {
				// Might happen in multiple file upload
				throw new IllegalArgumentException("Data type not defined for column " + 
						Integer.toString(k+1) + ": " + dataset.getUserColNames().get(k));
			}
			else if ( DashboardServerUtils.OTHER.typeNameEquals(colType) ||
					  colType.isCommentType() ) {
				// Unchecked data 
				;
			}
			// DATETIME_TIMESTAMP
			else if ( DashboardServerUtils.TIMESTAMP.typeNameEquals(colType) && 
					  DateTimeType.DATETIME_TIMESTAMP.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.SINGLE_DATE_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[0] = userElement;
				int idx = dctype.getSelectedUnitIndex();
				dateFormat = CHECKER_DATA_UNITS.get(colType.getVarName()).get(idx);
			}
			// DATETIME_DATA_TIME
			else if ( DashboardServerUtils.DATE.typeNameEquals(colType) && 
					  DateTimeType.DATETIME_DATE_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.DATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[0] = userElement;
				int idx = dctype.getSelectedUnitIndex();
				dateFormat = dctype.getUnits().get(idx);
			}
			else if ( DashboardServerUtils.TIME_OF_DAY.typeNameEquals(colType) && 
					  DateTimeType.DATETIME_DATE_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			// DATETIME_YEAR_DAY_SEC
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DAY_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDS_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DAY_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDS_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[1] = userElement;
				// assign the value for Jan 1 
				userElement = new Element(ColumnSpec.JAN_FIRST_INDEX_ELEMENT);
				String units = dctype.getUnits().get(dctype.getSelectedUnitIndex());
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
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			// DATETIME_YEAR_DECIMAL_DAY
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DECIMAL_DAY.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDJD_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_DECIMAL_DAY.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YDJD_DECIMAL_JDATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[1] = userElement;
				// assign the value for Jan 1 
				userElement = new Element(ColumnSpec.YDJD_JAN_FIRST_INDEX_ELEMENT);
				String units = dctype.getUnits().get(dctype.getSelectedUnitIndex());
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
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			else if ( DashboardServerUtils.HOUR_OF_DAY.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.HOUR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[3] = userElement;
			}
			else if ( DashboardServerUtils.MINUTE_OF_HOUR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.MINUTE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[4] = userElement;
			}
			else if ( DashboardServerUtils.SECOND_OF_MINUTE.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_HR_MIN_SEC.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[5] = userElement;
			}
			// DATETIME_YEAR_MON_DAY_TIME
			else if ( DashboardServerUtils.YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[0] = userElement;
			}
			else if ( DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[1] = userElement;
			}
			else if ( DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(colType) &&
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[2] = userElement;
			}
			else if ( DashboardServerUtils.TIME_OF_DAY.typeNameEquals(colType) && 
					  DateTimeType.DATETIME_YEAR_MON_DAY_TIME.equals(timeSpec) ) {
				Element userElement = new Element(ColumnSpec.YMDT_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				timestampElements[3] = userElement;
			}
			// Not involved with date/time specification
			else {
				// Element specifying the units of the column
				Element unitsElement = new Element(ColumnSpec.INPUT_UNITS_ELEMENT_NAME);
				int idx = dctype.getSelectedUnitIndex();
				// See if there are alternate unit strings for the checker for this data type
				ArrayList<String> checkerUnits = CHECKER_DATA_UNITS.get(colType.getVarName());
				String chkUnit;
				if ( checkerUnits == null ) {
					chkUnit = dctype.getUnits().get(idx);
					// Some hacks to avoid specific SOCAT types; ideally the automated data checker 
					// should be reconfigured to accept the units used
					if ( "PSU".equals(chkUnit) ) {
						// salinity
						chkUnit = "psu";
					}
					else if ( "degrees C".equals(chkUnit) ) {
						// temperature
						chkUnit = "degC";
					}
					else if ( "degrees".equals(chkUnit) ) {
						// direction
						chkUnit = "decimal_degrees";
					}
					else if ( (idx == 0) && "umol/mol".equals(chkUnit) ) {
						// xCO2 (but NOT xH2O which has umol/mol as second unit option)
						chkUnit = "ppm";
					}
				}	
				else {
					chkUnit = checkerUnits.get(idx);
				}
				unitsElement.setText(chkUnit);
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.INPUT_COLUMN_ELEMENT_NAME);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, Integer.toString(k+1));
				userElement.setText(dataset.getUserColNames().get(k));
				// Standard column name for the checker
				Element columnElement = new Element(ColumnSpec.SOCAT_COLUMN_ELEMENT); 
				columnElement.setAttribute(ColumnSpec.SOCAT_COLUMN_NAME_ATTRIBUTE, colType.getVarName());
				// Add the index and user name element, and the units element
				columnElement.addContent(userElement);
				columnElement.addContent(unitsElement);
				// Add the missing value if specified
				String missValue = dctype.getSelectedMissingValue();
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
		Logger logger = Logger.getLogger("Sanity Checker - " + datasetId);
		if ( Level.DEBUG.isGreaterOrEqual(logger.getEffectiveLevel()) ) {
			logger.debug("cruise columns specifications document:\n" + 
					(new XMLOutputter(Format.getPrettyFormat())).outputString(cruiseDoc));
		}
		ColumnSpec colSpec;
		try {
			colSpec = new ColumnSpec(new File(datasetId), cruiseDoc, convConfig, logger);
		} catch (InvalidColumnSpecException ex) {
			throw new IllegalArgumentException("Unexpected ColumnSpec exception: " + ex.getMessage());
		};

		// Get the OME metadata for this cruise
		Document oldOmeDoc;
		OmeMetadata oldOmeMData;
		File omeFile = metadataHandler.getMetadataFile(datasetId, DashboardUtils.OME_FILENAME);
		if ( omeFile.exists() ) {
			try {
				oldOmeDoc = (new SAXBuilder()).build(omeFile);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Problems reading the OME XML " + 
						omeFile.getName() + "\n    " + ex.getMessage());
			}
			oldOmeMData = new OmeMetadata(datasetId);
			try {
				oldOmeMData.assignFromOmeXmlDoc(oldOmeDoc);
			} catch (BadEntryNameException | InvalidConflictException ex) {
				throw new IllegalArgumentException("Unknown entry in the OME XML " + 
						omeFile.getName() + "\n    " + ex.getMessage());
			}
		}
		else {
			oldOmeDoc = null;
			oldOmeMData = new OmeMetadata(datasetId);
		}

		// Create the SanityChecker for this cruise
		SanityChecker checker;
		try {
			checker = new SanityChecker(datasetId, oldOmeMData, colSpec, 
										dataset.getDataValues(), dateFormat);
		} catch (Exception ex) {
			throw new IllegalArgumentException("automated data checker exception: " + ex.getMessage());
		}

		// Run the SanityChecker on this data and get the results
		Output output = checker.process();

		// Get the OME metadata that was updated from the data
		OmeMetadata updatedOmeMData = output.getMetadata();
		// Set the dataset ID to force the assignment of other fields associated with the dataset
		updatedOmeMData.setExpocode(datasetId);
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
					timestamp, dataset.getOwner(), dataset.getVersion());

			String message = "Update of OME metadata from cruise checker";
			metadataHandler.saveMetadataInfo(dashOmeMData, message, false);
			metadataHandler.saveAsOmeXmlDoc(dashOmeMData, message);
		}

		// Create/update the messages file and the data checker flags
		msgHandler.processCheckerMessages(dataset, output);

		// Count the rows of data with errors and with only warnings, check if there 
		// were lon/lat/depth/time problems and assign the data check status
		countWoceFlagsAndAssignStatus(dataset, colIndcs, output);

		return output;
	}

	/**
	 * Checks and standardizes the units in the data values, stored as strings, 
	 * in the given dataset.  The year, month, day, hour, minute, and second 
	 * data columns are appended to each data measurement (row, outer array) 
	 * if not already present.
	 *  
	 * @param dataset
	 * 		dataset whose data is to be standardized
	 * @return
	 * 		true if the automated data checker ran successfully and 
	 * 		data had no lon/lat/depth/time errors.
	 */
	public boolean standardizeDatasetData(DashboardDatasetData dataset) {
		if ( dataset.getNumDataRows() < 1 )
			return false;

		ColumnIndices colIndcs = getColumnIndices(dataset.getDataColTypes());

		// Run the automated data checker to get the standardized data
		Output output;
		try {
			output = checkDatasetAndReturnOutput(dataset, colIndcs);
		} catch (IllegalArgumentException ex) {
			lastCheckProcessedOkay = false;
			return false;
		}
		if ( lastCheckHadGeopositionErrors || ! lastCheckProcessedOkay )
			return false;
		List<SocatDataRecord> stdRowVals = output.getRecords();

		// Directly modify the lists in the cruise data object
		ArrayList<DataColumnType> dataColTypes = dataset.getDataColTypes();
		ArrayList<ArrayList<String>> dataVals = dataset.getDataValues();

		// Standardized data for generating a DsgData object must have 
		// separate year, month, day, hour, minute, and second columns
		boolean hasYearColumn = ( colIndcs.yearIndex >= 0 );
		boolean hasMonthColumn = ( colIndcs.monthIndex >= 0 );
		boolean hasDayColumn = ( colIndcs.dayIndex >= 0 );
		boolean hasHourColumn = ( colIndcs.hourIndex >= 0 );
		boolean hasMinuteColumn = ( colIndcs.minuteIndex >= 0 );
		boolean hasSecondColumn = ( colIndcs.secondIndex >= 0 );

		// Add any missing time columns.
		// !! Directly modify the lists in the cruise data object !!
		ArrayList<String> userColNames = dataset.getUserColNames();
		if ( ! hasYearColumn ) {
			DataColumnType dctype = DashboardServerUtils.YEAR.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Year");
		}
		if ( ! hasMonthColumn ) {
			DataColumnType dctype = DashboardServerUtils.MONTH_OF_YEAR.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Month");
		}
		if ( ! hasDayColumn ) {
			DataColumnType dctype = DashboardServerUtils.DAY_OF_MONTH.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Day");
		}
		if ( ! hasHourColumn ) {
			DataColumnType dctype = DashboardServerUtils.HOUR_OF_DAY.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Hour");
		}
		if ( ! hasMinuteColumn ) {
			DataColumnType dctype = DashboardServerUtils.MINUTE_OF_HOUR.duplicate();
			dctype.setSelectedMissingValue(Integer.toString(DashboardUtils.INT_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Minute");
		}
		if ( ! hasSecondColumn ) {
			DataColumnType dctype = DashboardServerUtils.SECOND_OF_MINUTE.duplicate();
			dctype.setSelectedMissingValue(Double.toString(DashboardUtils.FP_MISSING_VALUE));
			dataColTypes.add(dctype);
			userColNames.add("Second");
		}

		Iterator<SocatDataRecord> stdRowIter = stdRowVals.iterator();
		for ( ArrayList<String> rowData : dataVals ) {
			SocatDataRecord stdVals;
			try {
				stdVals = stdRowIter.next();
			} catch ( NoSuchElementException ex ) {
				throw new IllegalArgumentException("Unexpected mismatch in the " +
						"number of rows of original data and standardized data");
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
				// Verify the longitude, latitude, depth, and time are given
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
		}

		KnownDataTypes knownUserTypes;
		try {
			DashboardConfigStore configStore = DashboardConfigStore.get(false);
			knownUserTypes = configStore.getKnownUserDataTypes();
		} catch (IOException ex) {
			throw  new IllegalArgumentException("Unexpected error retrieving the dashboard configuration");
		}
		ArrayList<DashDataType<?>> dataTypes = new ArrayList<DashDataType<?>>(dataVals.size());
		for ( DataColumnType colType : dataColTypes ) {
			DashDataType<?> dtype = knownUserTypes.getDataType(colType);
			dataTypes.add(dtype);
		}

		// Go through each row, converting data as needed
		stdRowIter = stdRowVals.iterator();
		for ( ArrayList<String> rowData : dataVals ) {
			SocatDataRecord stdVals;
			try {
				stdVals = stdRowIter.next();
			} catch ( NoSuchElementException ex ) {
				throw new IllegalArgumentException("Unexpected mismatch in the " +
						"number of rows of original data and standardized data");
			}
			int k = 0;
			for ( DataColumnType colType : dataColTypes ) {
				DashDataType<?> dtype = dataTypes.get(k);
				if ( ( dtype instanceof StringDashDataType ) ||
					 ( dtype instanceof CharDashDataType ) ||
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
					rowData.set(k, Double.toString(stdVals.getLongitude()));
				}
				else if ( DashboardServerUtils.LATITUDE.typeNameEquals(colType) ) {
					rowData.set(k, Double.toString(stdVals.getLatitude()));
				}
				else {
					String chkName = colType.getVarName();
					SocatDataColumn stdCol = stdVals.getColumn(chkName);
					if ( stdCol == null )
						throw new IllegalArgumentException("Checker data column not found for " + 
								chkName + " (column type " + colType + ")");
					String value = stdCol.getValue();
					if ( DashboardServerUtils.SAMPLE_DEPTH.typeNameEquals(colType) ) {
						// Sample depth not a special column for the automated data checker,
						// but a valid value must be given for this dashboard.
						try {
							Double depth = Double.parseDouble(value);
							if ( depth.isNaN() || depth.isInfinite() )
								throw new IllegalArgumentException();
						} catch (Exception ex) {
							lastCheckHadGeopositionErrors = true;
							return false;
						}
					}
					rowData.set(k, value);
				}
				k++;
			}
		}

		return true;
	}

	/**
	 * Counts and assigns the number of data rows with errors and with only 
	 * warnings.  Sets lastCheckProcessedOkay and lastCheckHadGeopostionErrors.
	 * Assigns the data check status.
	 * 
	 * @param dataset
	 * 		dataset to use
	 * @param colIndcs
	 * 		data column indices for the cruise
	 * @param processedOK
	 * 		did the SanityCheck run successfully?
	 */
	private void countWoceFlagsAndAssignStatus(DashboardDataset dataset, 
								ColumnIndices colIndcs, Output output) {
		lastCheckProcessedOkay = output.processedOK();
		lastCheckHadGeopositionErrors = false;

		// Get the indices of data rows the PI marked as bad.
		HashSet<Integer> userErrRows = new HashSet<Integer>();
		for ( QCFlag wtype : dataset.getUserFlags() ) {
			if ( Severity.BAD.equals(wtype.getSeverity()) )
				userErrRows.add(wtype.getRowIndex());
		}
		// Get the indices of data rows the PI marked as questionable 
		// in some QC flag, but did not mark as bad in any QC flag.
		HashSet<Integer> userWarnRows = new HashSet<Integer>();
		for ( QCFlag wtype : dataset.getUserFlags() ) {
			if ( Severity.QUESTIONABLE.equals(wtype.getSeverity()) ) {
				Integer rowIdx = wtype.getRowIndex();
				if ( ! userErrRows.contains(rowIdx) )
					userWarnRows.add(rowIdx);
			}
		}
		// Get the indices of data rows the automated data checker 
		// found having errors but not marked as bad by the PI.
		HashSet<Integer> errRows = new HashSet<Integer>();
		for ( QCFlag wtype : dataset.getCheckerFlags() ) {
			if ( Severity.BAD.equals(wtype.getSeverity()) ) {
				Integer rowIdx = wtype.getRowIndex();
				if ( ! userErrRows.contains(rowIdx) )
					errRows.add(rowIdx);
			}
		}
		// Get the indices of data rows the automated data checker found having 
		// only warnings but not marked as bad or questionable by the PI.
		HashSet<Integer> warnRows = new HashSet<Integer>();
		for ( QCFlag wtype : dataset.getCheckerFlags() ) {
			if ( Severity.QUESTIONABLE.equals(wtype.getSeverity()) ) {
				Integer rowIdx = wtype.getRowIndex();
				if ( ! ( userErrRows.contains(rowIdx) ||
						 userWarnRows.contains(rowIdx) ||
						 errRows.contains(rowIdx) ) )
					warnRows.add(rowIdx);
			}
		}
		// Get the indices of data column that the automated data checker 
		// found errors in rows not marked as bad by the PI
		HashSet<Integer> errCols = new HashSet<Integer>();
		for ( QCFlag wtype : dataset.getCheckerFlags() ) {
			if ( Severity.BAD.equals(wtype.getSeverity()) ) {
				if ( ! userErrRows.contains(wtype.getRowIndex()) )
					errCols.add(wtype.getColumnIndex());
			}
		}

		if ( (colIndcs.longitudeIndex < 0) || errCols.contains(colIndcs.longitudeIndex) ||
			 (colIndcs.latitudeIndex < 0) || errCols.contains(colIndcs.latitudeIndex) ||
			 (colIndcs.sampleDepthIndex < 0) || errCols.contains(colIndcs.sampleDepthIndex) ||
			 ( (colIndcs.timestampIndex >= 0) && errCols.contains(colIndcs.timestampIndex) ) ||
			 ( (colIndcs.dateIndex >= 0) && errCols.contains(colIndcs.dateIndex) ) ||
			 ( (colIndcs.yearIndex >= 0) && errCols.contains(colIndcs.yearIndex) ) ||
			 ( (colIndcs.monthIndex >= 0) && errCols.contains(colIndcs.monthIndex) ) ||
			 ( (colIndcs.dayIndex >= 0) && errCols.contains(colIndcs.dayIndex) ) ||
			 ( (colIndcs.timeIndex >= 0) && errCols.contains(colIndcs.timeIndex) ) ||
			 ( (colIndcs.hourIndex >= 0) && errCols.contains(colIndcs.hourIndex) ) ||
			 ( (colIndcs.minuteIndex >= 0) && errCols.contains(colIndcs.minuteIndex) ) ||
			 ( (colIndcs.secondIndex >= 0) && errCols.contains(colIndcs.secondIndex) ) ||
			 ( (colIndcs.dayOfYearIndex >= 0) && errCols.contains(colIndcs.dayOfYearIndex) ) ||
			 ( (colIndcs.secondOfDayIndex >= 0) && errCols.contains(colIndcs.secondOfDayIndex) ) ) {
			lastCheckHadGeopositionErrors = true;
		}
		if ( ! lastCheckHadGeopositionErrors ) {
			// Date/time errors may not be associated with any column
			// so check the automated data checker messages
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

		int numErrorRows = errRows.size();
		int numWarnRows = warnRows.size();

		dataset.setNumErrorRows(numErrorRows);
		dataset.setNumWarnRows(numWarnRows);

		// Assign the data-check status message using the results of the sanity check
		if ( ! lastCheckProcessedOkay ) {
			dataset.setDataCheckStatus(DashboardUtils.CHECK_STATUS_UNACCEPTABLE);
		}
		else if ( lastCheckHadGeopositionErrors ) {
			dataset.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX +
					Integer.toString(numErrorRows) + " errors " + DashboardUtils.GEOPOSITION_ERRORS_MSG);
		}
		else if ( numErrorRows > 0 ) {
			dataset.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX +
					Integer.toString(numErrorRows) + " errors");
		}
		else if ( numWarnRows > 0 ) {
			dataset.setDataCheckStatus(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX +
					Integer.toString(numWarnRows) + " warnings");
		}
		else {
			dataset.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ACCEPTABLE);
		}
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
