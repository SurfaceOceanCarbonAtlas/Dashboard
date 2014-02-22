/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.DataSpecsService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import uk.ac.uea.socat.sanitychecker.Message;
import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.SanityChecker;
import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.InvalidColumnSpecException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DataSpecsService
 * @author Karl Smith
 */
public class DataSpecsServiceImpl extends RemoteServiceServlet
												implements DataSpecsService {

	private static final long serialVersionUID = -7106452856622957624L;

	@Override
	public DashboardCruiseWithData getCruiseDataColumnSpecs(String username,
			String passhash, String expocode) throws IllegalArgumentException {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected configuration error: " + ex.getMessage());
		}
		if ( ! dataStore.validateUser(username, passhash) )
			throw new IllegalArgumentException(
					"Invalid authentication credentials");

		// Get the cruise with the first maximum-needed number of rows
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
				.getCruiseDataFromFiles(expocode, 0, 
						DashboardUtils.MAX_ROWS_PER_GRID_PAGE);
		if ( cruiseData == null )
			throw new IllegalArgumentException(
					"cruise " + expocode + " does not exist");

		// Remove any metadata preamble to reduced data transmitted
		cruiseData.getPreamble().clear();

		// Return the cruise with the partial data
		return cruiseData;
	}

	@Override
	public ArrayList<ArrayList<String>> getCruiseData(String username,
				String passhash, String expocode, int firstRow, int numRows)
											throws IllegalArgumentException {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected configuration error: " + ex.getMessage());
		}
		if ( ! dataStore.validateUser(username, passhash) )
			throw new IllegalArgumentException(
					"Invalid authentication credentials");

		// Get the cruise data with exactly the data rows desired
		DashboardCruiseWithData cruiseWithData = 
				dataStore.getCruiseFileHandler()
						 .getCruiseDataFromFiles(expocode, firstRow, numRows);
		if ( cruiseWithData == null )
			throw new IllegalArgumentException(
					"cruise " + expocode + " does not exist");
		ArrayList<ArrayList<String>> cruiseDataRows = cruiseWithData.getDataValues();
		if ( cruiseDataRows.size() != numRows )
			throw new IllegalArgumentException(
					"invalid requested row numbers: " + 
					firstRow + " - " + (firstRow + numRows));
		return cruiseDataRows;
	}

	@Override
	public DashboardCruiseWithData updateCruiseDataColumnSpecs(String username,
									String passhash, DashboardCruise newSpecs)
											throws IllegalArgumentException {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected configuration error: " + ex.getMessage());
		}
		if ( ! dataStore.validateUser(username, passhash) )
			throw new IllegalArgumentException(
					"Invalid authentication credentials");

		// Retrieve all the current cruise data
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
						.getCruiseDataFromFiles(newSpecs.getExpocode(), 0, -1);
		// Revise the cruise data column types and units 
		if ( newSpecs.getDataColTypes().size() != 
				cruiseData.getDataColTypes().size() )
			throw new IllegalArgumentException(
					"Unexpected number of data columns (" +
					newSpecs.getDataColTypes().size() + " instead of " + 
					cruiseData.getDataColTypes().size());
		cruiseData.setDataColTypes(newSpecs.getDataColTypes());
		cruiseData.setDataColUnits(newSpecs.getDataColUnits());
		cruiseData.setMissingValues(newSpecs.getMissingValues());

		// Run the SanityCheck on the updated cruise.
		// Assigns the data check status and the WOCE-3 and WOCE-4 data flags.
		Output output = checkCruise(cruiseData);

		// Update the reports of any issues found
		dataStore.getCruiseFileHandler().saveCruiseMessages(
				cruiseData.getExpocode(), output.getMessages().getMessages());

		// Save and commit the updated cruise columns
		dataStore.getCruiseFileHandler().saveCruiseInfoToFile(cruiseData, 
				"Cruise data column types, units, and missing values for " + 
				cruiseData.getExpocode() + " updated by " + username);
		// Update the user-specific data column names to types, units, and missing values 
		dataStore.getUserFileHandler()
				 .updateUserDataColumnTypes(cruiseData, username);
		
		// Remove all but the first maximum-needed number of rows of cruise data 
		// to minimize the payload of the returned cruise data
		int numRows = cruiseData.getNumDataRows();
		if ( numRows > DashboardUtils.MAX_ROWS_PER_GRID_PAGE )
			cruiseData.getDataValues()
					  .subList(DashboardUtils.MAX_ROWS_PER_GRID_PAGE, numRows)
					  .clear();

		// Return the updated truncated cruise data for redisplay 
		// in the DataColumnSpecsPage
		return cruiseData;
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
	private Output checkCruise(DashboardCruiseWithData cruiseData) 
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
			if ( colType == DataColumnType.UNKNOWN ) {
				// Might happen in multiple file upload
				throw new IllegalArgumentException(
						"Data type not defined for column " + Integer.toString(k+1) + 
						": " + cruiseData.getUserColNames().get(k));
			}
			else if ( colType == DataColumnType.TIMESTAMP ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.SINGLE_DATE_TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Set the date format
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = DashboardUtils.CHECKER_DATA_UNITS.get(colType).get(idx);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.DATE ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.DATE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Set the date format
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				dateFormat = DashboardUtils.CHECKER_DATA_UNITS.get(colType).get(idx);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.YEAR ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.YEAR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.MONTH ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.MONTH_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.DAY ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.DAY_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.TIME ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.TIME_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.HOUR ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.HOUR_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.MINUTE ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.MINUTE_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( colType == DataColumnType.SECOND ) {
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.SECOND_ELEMENT);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				timestampElement.addContent(userElement);
				// Save the index of this date/time column for message processing
				ambiguousColumnIndices.add(k);
			}
			else if ( (colType == DataColumnType.LONGITUDE) || 
					  (colType == DataColumnType.LATITUDE) || 
					  (colType == DataColumnType.SAMPLE_DEPTH) || 
					  (colType == DataColumnType.SALINITY) || 
					  (colType == DataColumnType.EQUILIBRATOR_TEMPERATURE) || 
					  (colType == DataColumnType.SEA_SURFACE_TEMPERATURE) || 
					  (colType == DataColumnType.EQUILIBRATOR_PRESSURE) || 
					  (colType == DataColumnType.SEA_LEVEL_PRESSURE) || 
					  (colType == DataColumnType.XCO2WATER_EQU) ||
					  (colType == DataColumnType.XCO2WATER_SST) ||
					  (colType == DataColumnType.PCO2WATER_EQU) ||
					  (colType == DataColumnType.PCO2WATER_SST) ||
					  (colType == DataColumnType.FCO2WATER_EQU) ||
					  (colType == DataColumnType.FCO2WATER_SST) ) {
				// Element specifying the units of the column
				Element unitsElement = new Element(ColumnSpec.INPUT_UNITS_ELEMENT_NAME);
				int idx = DashboardUtils.STD_DATA_UNITS.get(colType).indexOf(
						cruiseData.getDataColUnits().get(k));
				unitsElement.setText(
						DashboardUtils.CHECKER_DATA_UNITS.get(colType).get(idx));
				// Element specifying the index and user name of the column
				Element userElement = new Element(ColumnSpec.INPUT_COLUMN_ELEMENT_NAME);
				userElement.setAttribute(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE, 
											Integer.toString(k+1));
				userElement.setText(cruiseData.getUserColNames().get(k));
				// Standard SOCAT column name for the checker
				Element columnElement = new Element(ColumnSpec.SOCAT_COLUMN_ELEMENT); 
				columnElement.setAttribute(ColumnSpec.SOCAT_COLUMN_NAME_ATTRIBUTE, 
						DashboardUtils.CHECKER_NAMES.get(colType));
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
			else if ( (colType == DataColumnType.EXPOCODE) || 
					  (colType == DataColumnType.CRUISE_NAME) || 
					  (colType == DataColumnType.XCO2_ATM) || 
					  (colType == DataColumnType.PCO2_ATM) || 
					  (colType == DataColumnType.FCO2_ATM) || 
					  (colType == DataColumnType.SHIP_SPEED) || 
					  (colType == DataColumnType.SHIP_DIRECTION) || 
					  (colType == DataColumnType.WIND_SPEED_TRUE) || 
					  (colType == DataColumnType.WIND_SPEED_RELATIVE) || 
					  (colType == DataColumnType.WIND_DIRECTION_TRUE) || 
					  (colType == DataColumnType.WIND_DIRECTION_RELATIVE) || 
					  (colType == DataColumnType.DELTA_PCO2) || 
					  (colType == DataColumnType.DELTA_FCO2) || 
					  (colType == DataColumnType.WOA_SALINITY) || 
					  (colType == DataColumnType.NCEP_SEA_LEVEL_PRESSURE) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_TEQ_PEQ_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_SST_PEQ_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_PCO2_TEQ_PEQU_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_PCO2_SST_PEQ_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_FCO2_TEQ_PEQ_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_FCO2_SST_PEQ_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_PCO2_TEQ_NCEP_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_PCO2_SST_NCEP_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_TEQ_PEQ_WOA) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_SST_PEQ_WOA) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_TEQ_NCEP_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_SST_NCEP_SAL) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_TEQ_NCEP_WOA) || 
					  (colType == DataColumnType.FCO2REC_FROM_XCO2_SST_NCEP_WOA) || 
					  (colType == DataColumnType.FCO2REC) || 
					  (colType == DataColumnType.FCO2REC_SOURCE) || 
					  (colType == DataColumnType.DELTA_TEMPERATURE) || 
					  (colType == DataColumnType.REGION_ID) || 
					  (colType == DataColumnType.SECONDS_1970) || 
					  (colType == DataColumnType.DAYS_1970) || 
					  (colType == DataColumnType.DAY_OF_YEAR) || 
					  (colType == DataColumnType.CALC_SHIP_SPEED) || 
					  (colType == DataColumnType.ETOPO2) || 
					  (colType == DataColumnType.GVCO2) || 
					  (colType == DataColumnType.DISTANCE_TO_LAND) || 
					  (colType == DataColumnType.FCO2REC_WOCE_FLAG) ) {
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

}
