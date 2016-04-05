package uk.ac.uea.socat.sanitychecker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import uk.ac.exeter.QCRoutines.config.ConfigException;
import uk.ac.exeter.QCRoutines.config.RoutinesConfig;
import uk.ac.exeter.QCRoutines.data.DataRecord;
import uk.ac.exeter.QCRoutines.data.NoSuchColumnException;
import uk.ac.exeter.QCRoutines.messages.Message;
import uk.ac.exeter.QCRoutines.messages.MessageException;
import uk.ac.exeter.QCRoutines.routines.Routine;
import uk.ac.exeter.QCRoutines.routines.RoutineException;
import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.omemetadata.OmeMetadataException;
import uk.ac.uea.socat.sanitychecker.config.BaseConfig;
import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatConfigException;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfig;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.metadata.MetadataException;
import uk.ac.uea.socat.sanitychecker.metadata.MetadataItem;

/**
 * Startup class for the SOCAT Sanity Checker.
 * 
 * The Sanity Checker is used to perform basic validation and error
 * checking on data files submitted to the SOCAT database.
 * 
 * This class contains the startup method for the Sanity Checker, and also supplies
 * globally-accessible constants for such things as exit codes.
 * 
 */
public class SanityChecker {
	
	/**
	 * The standard output output format for dates
	 */
	public static final DateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	/**
	 * The logger for this instance of the Sanity Checker
	 */
	private Logger itsLogger;
	
	/**
	 * The passed in data fields
	 */
	private ArrayList<ArrayList<String>> itsInputData;
	
	/**
	 * The column specification for the passed in data
	 */
	private ColumnSpec itsColumnSpec;
	
	/**
	 * Utility object for handling dates and times
	 */
	private DateTimeHandler itsDateTimeHandler;
	
	/**
	 * The object containing details for the XML output from the Sanity Checker
	 */
	private Output itsOutput;
	
	/**
	 * Initialise the configuration of the Sanity Checker
	 * 
	 * @param filename The location of the {@link BaseConfig} file. 
	 */
	public static void initConfig(String filename) throws SocatConfigException, SanityCheckerException {
		Logger initLogger = Logger.getLogger("SanityChecker");
		BaseConfig.init(filename, initLogger);
		
		// Two-digit dates are handled, but only up to a certain time point. After that we must
		// fail. At the time of writing, we start failing 1 year before the program will actually
		// stop working reliably, which gives someone enough time to remove this check and work out a fix.
		// See you in 30 years or so...
		//
		// The relevant code for two-digit year handling is in the DateTimeHandler class
		//
		DateTime now = new DateTime(DateTimeZone.UTC);
		if (now.getYear() >= DateTimeHandler.FINAL_RUN_YEAR) {
			initLogger.fatal("The Sanity Checker will no longer run, because two-digit years cannot be supported.");
			throw new SanityCheckerException("The Sanity Checker will no longer run, because two-digit years cannot be supported.");
		}

		// Retrieve instances of the other configurations. This will
		// ensure that they are valid and set up properly before
		// we start trying to process files.
		BaseConfig.getInstance();
		MetadataConfig.getInstance();
		ColumnConversionConfig.getInstance();
		try {
			SocatColumnConfig.getInstance();
			RoutinesConfig.getInstance();
		} catch (uk.ac.exeter.QCRoutines.config.ConfigException e) {
			throw new SocatConfigException(filename, e.getMessage(), e);
		}
	}
	
	/**
	 * Base constructor for an instance of the Sanity Checker. This must be called for each
	 * data file individually.
	 * 
	 * The {@code dateTimeFormat} parameter is required to specify the format of dates in the metadata, and also
	 * in the data fields if the date is specified as a single date or date/time field. In the unlikely
	 * event that the data provider has given dates in different formats in the metadata and the data,
	 * one or the other will not be parsed properly and errors will be raised. 
	 * 
	 *  
	 * @param filename The name of the data file. Used for referencing purposes
	 * @param metadataInput The set of metadata values, as extracted from the file header and maybe elsewhere
	 * @param colSpec The column specification of the data file.
	 * @param dataInput The data values from the input file
	 * @param dateTimeFormat The date format to be used for parsing date strings. Do not include the time specification!
	 * @throws SocatConfigException If the base configuration has not been initialised
	 */
	public SanityChecker(String filename, OmeMetadata metadata, ColumnSpec colSpec, 
			ArrayList<ArrayList<String>> dataInput, String dateFormat) throws SanityCheckerException {
		
		itsLogger = Logger.getLogger("Sanity Checker - " + filename);
		itsLogger.trace("SanityChecker initialised");
		
		// Make sure the the base configuration is set up. Otherwise we can't do anything!
		if (!BaseConfig.isInitialised()) {
			itsLogger.fatal("Base Configuration has not been initialised - call initBaseConfig first!");
			throw new SanityCheckerException("Base Configuration has not been initialised - call initBaseConfig first!");
		}
		
		try {
			// Initialise the output object
			itsInputData = dataInput;
			itsColumnSpec = colSpec;
			itsDateTimeHandler = new DateTimeHandler(dateFormat);
			itsOutput = new Output(filename, metadata, dataInput.size(), itsColumnSpec, itsLogger);
			
		} catch (Exception e) {
			itsLogger.fatal("Error initialising Sanity Checker instance", e);
			throw new SanityCheckerException("Error initialising Sanity Checker instance", e);
		}
	}

	/**
	 * Processes the data file.
	 * @return The output of the processing.
	 */
	public Output process() {

		try {
			itsLogger.debug("Processing data records");
			int recordCount = 0;
			for (List<String> record: itsInputData) {
				recordCount++;
				itsLogger.trace("Processing record " + recordCount);
				SocatDataRecord socatRecord = new SocatDataRecord(recordCount, SocatColumnConfig.getInstance(), record, itsColumnSpec, itsOutput.getMetadata(), itsDateTimeHandler); 
				itsOutput.addRecord(socatRecord);
				itsOutput.addMessages(socatRecord.getMessages());
			}
			
			// Check for missing/out-of-range values
			checkDataValues();
			
			// Generate metadata from data
			generateMetadataFromData();
			
			// Run data sanity checks
			runRoutines();
		} catch (Exception e) {
			itsLogger.fatal("Unhandled exception encountered", e);
			
			Message message = new InternalErrorMessage(e);
			try {
				itsOutput.addMessage(message);
				itsOutput.setExitFlag(Output.INTERNAL_ERROR_FLAG);
				itsOutput.clear(true);
			} catch (Exception e2) {
				itsLogger.fatal("Error while storing exception in output messages", e2);
			}
		}

		return itsOutput;
	}

	/**
	 * Runs the individual sanity check modules over the processed data.
	 * @throws SocatConfigException If the Sanity Checker modules are badly configured.
	 * @throws SanityCheckException If errors are encountered while performing the sanity checks.
	 * @throws MessageException If errors occur while generating and storing messages
	 */
	private void runRoutines() throws ConfigException, RoutineException, MessageException {
		
		List<Routine> routines = RoutinesConfig.getInstance().getRoutines();
		
		for (Routine routine : routines) {
			@SuppressWarnings("unchecked")
			List<DataRecord> records = (List<DataRecord>)(List<?>) itsOutput.getRecords();
			
			
			routine.processRecords(records);
			itsOutput.addMessages(routine.getMessages());
		}
	}
	
	/**
	 * Generates metadata values from the file data.
	 * Any conflicting metadata items already present will be overwritten
	 * @param metadataSet The metadata that has been extracted thus far. New metadata will be added to this.
	 */
	private void generateMetadataFromData() throws SanityCheckerException, SocatConfigException, MetadataException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, DateTimeException, OmeMetadataException {
		
		List<MetadataItem> allRecordItems = new ArrayList<MetadataItem>();
		
		// Loop through every item in the metadata config
		for (String metadataName : MetadataConfig.getInstance().getConfigItemNames()) {
			MetadataConfigItem metadataItemConfig = MetadataConfig.getInstance().get(metadataName);

			Class<?> c = metadataItemConfig.getItemClass();
			Class<? extends MetadataItem> itemClass = c.asSubclass(MetadataItem.class);
			Constructor<? extends MetadataItem> itemConstructor = itemClass.getConstructor(MetadataConfigItem.class, Integer.TYPE, Logger.class);
			MetadataItem item = itemConstructor.newInstance(metadataItemConfig, -1, itsLogger);
			
			// See if the item's value can be generated. If so...
			if (item.canGenerate()) {
				
				// If it can't be generated from one record, add it to the list of items for later processing
				if (!item.canGenerateFromOneRecord()) {
					allRecordItems.add(item);
				} else {
					item.processRecordForValue(itsOutput.getRecords().get(0));
					item.generateValue(itsDateTimeHandler);
				}
			}
		}
		
		// Assuming there's more than one metadata item in the list for later processing...
		
		// Loop through all the records from the file, and pass each record to each metadata entry in turn.
		for (SocatDataRecord record: itsOutput.getRecords()) {
			for (MetadataItem item : allRecordItems) {
				item.processRecordForValue(record);
			}
		}
		
		// After that, call generateValue on each metadata item to set its final value,
		// and add it to the metadata set.
		for (MetadataItem item: allRecordItems) {
			item.generateValue(itsDateTimeHandler);
			itsOutput.addMetadataValue(item.getName(), item.getValue(), item.getLine());
		}
	}
	
	/**
	 * Checks the read in data for missing, invalid and out-of-range values.
	 * 
	 * There is code here that attempts to auto-detect the value that indicates
	 * missing data (e.g. -999), but it is currently disabled. It may be reinstated
	 * at a later date if automatically detecting this is better than asking users
	 * to specify it.
	 * @throws ConfigException 
	 * @throws NoSuchColumnException 
	 */
	private void checkDataValues() throws SocatConfigException, MessageException, NoSuchColumnException, ConfigException {
		/*
		 * Loop through all the numeric columns, searching for candidates for
		 * values that indicate missing data. For each column this is the
		 * most common out-of-range value.
		 * 
		 * Any such values are replaced with an empty string.
		 */

		
/* 
		 * This method of auto-detecting 'missing' value denoters has been removed.
		 * It may be reinstated at a later date, but needs careful testing for false positives/negatives.
		 * 
		 * Also note that this section has not been updated to use the new QC_Routines API.
		 * 
		 * 
		for (String columnName : SocatColumnConfig.getInstance().getColumnList()) {
			SocatColumnConfigItem columnConfig = SocatColumnConfig.getInstance().getColumnConfig(columnName);
			
			// If the column is not required and not numeric, we don't do any checks
			if (!columnConfig.isRequired() && !columnConfig.isNumeric()) {
				continue;
			}
			
			itsLogger.trace("Checking values in column '" + columnName + "'");

			if (columnConfig.isNumeric()) {
			
				// Loop through all records looking for candidates
				// for values that indicate missing data.
				MissingValuesCandidates missingValues = new MissingValuesCandidates();
	
				for (SocatDataRecord record : itsOutput.getRecords()) {
					SocatDataColumn column = record.getColumn(columnName);
					
					if (!column.isEmpty()) {
						String value = column.getValue();
						if (CheckerUtils.isNumeric(value)) {
							if (!columnConfig.isInRange(Double.parseDouble(value))) {
								missingValues.add(value);
							}
						}
					}
				}
				
				// If we've found a candidate for the missing value,
				// go back through the records and replace those with empty strings.
				String missingValue = missingValues.getBestCandidate();
				if (null != missingValue) {
					for (SocatDataRecord record: itsOutput.getRecords()) {
						SocatDataColumn column = record.getColumn(columnName);
						if (column.getValue().equalsIgnoreCase(missingValue)) {
							column.setValue("");
						}
					}
				}
			}
		}
*/
		/*
		 * Now we can go through all the data looking for required values that are
		 * missing, and numeric values that are out of range. 
		 */
		
		// A store for any generated messages
		List<Message> messages = new ArrayList<Message>();
		
		for (String columnName : SocatColumnConfig.getInstance().getColumnList()) {
			SocatColumnConfigItem columnConfig = (SocatColumnConfigItem) SocatColumnConfig.getInstance().getColumnConfig(columnName);

			int currentRecord = 0;
			for (SocatDataRecord record : itsOutput.getRecords()) {
				currentRecord++;
				SocatDataColumn column = (SocatDataColumn) record.getColumn(columnName);
				
				// If the column is empty, see if it's required and set the flag if it is.
				if (column.isEmpty()) {
					if (column.isRequired()) {
						
						// Check the Required Group
						if (CheckerUtils.isEmpty(columnConfig.getRequiredGroup())) {
							// No required group, so we just report the missing value
							itsLogger.trace("Missing required value on line " + currentRecord + ", column '" + columnName + "'");
							record.addMessage(new MissingValueMessage(record.getLineNumber(), column, columnConfig.getMissingFlag()));
						} else {
							// We're in a required group, so check the values from the other fields.
							// Only if they're all missing do we set the missing flag
							List<String> requiredGroupValues = record.getRequiredGroupValues(columnConfig.getRequiredGroup());
							if (CheckerUtils.isEmpty(requiredGroupValues)) {
								
								// Only report required group columns that are in the input file
								if (null != column.getInputColumnName()) {
									itsLogger.trace("Missing required value on line " + currentRecord + ", column '" + columnName + "'");
									record.addMessage(new MissingValueMessage(record.getLineNumber(), column, columnConfig.getMissingFlag()));
								}
							}
						}
					}
				}
			}
		}
		
		itsOutput.addMessages(messages);
	}
}
