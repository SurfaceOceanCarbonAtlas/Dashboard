package uk.ac.uea.socat.sanitychecker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import uk.ac.uea.socat.sanitychecker.config.BaseConfig;
import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfig;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigRequiredGroups;
import uk.ac.uea.socat.sanitychecker.config.SanityCheckConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.metadata.MetadataException;
import uk.ac.uea.socat.sanitychecker.metadata.MetadataItem;
import uk.ac.uea.socat.sanitychecker.sanitychecks.SanityCheck;

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
	 * The passed in metadata name/value pairs
	 */
	private Properties itsInputMetadata;
	
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
	 * A counter for the number of records processed
	 */
	private int itsRecordCount = 0;
	
	/**
	 * Initialise the configuration of the Sanity Checker
	 * 
	 * @param filename The location of the @code{BaseConfig} file. 
	 */
	public static void initConfig(String filename) throws ConfigException, SanityCheckerException {
		Logger initLogger = Logger.getLogger("SanityChecker - Configuration init");
		BaseConfig.init(filename, initLogger);
		
		// Two-digit dates are handled, but only up to a certain time point. After that we must
		// fail. At the time of writing, we start failing 1 year before the program will actually
		// stop working reliably, which gives someone enough time to remove this check and work out a fix.
		// See you in 30 years or so...
		//
		// The relevant code for two-digit year handling is in the DateTimeHandler class
		//
		DateTime now = new DateTime();
		if (now.getYear() >= DateTimeHandler.FINAL_RUN_YEAR) {
			initLogger.fatal("The Sanity Checker will no longer run, because two-digit years cannot be supported.");
			throw new SanityCheckerException("The Sanity Checker will no longer run, because two-digit years cannot be supported.");
		}

		// Retrieve instances of the other configurations. This will
		// ensure that they are valid and set up properly before
		// we start trying to process files.
		BaseConfig.getInstance();
		MetadataConfig.getInstance();
		SocatColumnConfig.getInstance();
		SanityCheckConfig.getInstance();
	}
	
	/**
	 * Base constructor for an instance of the Sanity Checker. This must be called for each
	 * data file individually.
	 * 
	 * The @code{dateFormat} parameter is required to specify the format of dates in the metadata, and also
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
	 * @throws ConfigException If the base configuration has not been initialised
	 */
	public SanityChecker(String filename, Properties metadataInput, ColumnSpec colSpec, 
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
			itsInputMetadata = metadataInput;
			itsInputData = dataInput;
			itsColumnSpec = colSpec;
			itsDateTimeHandler = new DateTimeHandler(dateFormat);
			itsOutput = new Output(filename, metadataInput.size(), dataInput.size(), itsLogger);
			
		} catch (Exception e) {
			itsLogger.fatal("Error initialising Sanity Checker instance", e);
			throw new SanityCheckerException("Error initialising Sanity Checker instance", e);
		}
	}

	public Output process() {

		try {
			boolean ok = true;
			
			// Extract the metadata. If this comes back as null, then we can't continue
			itsLogger.debug("Processing metadata");
			processInputMetadata();
			
			// Validate the metadata items from the header.
			// If required fields are missing, we will fail.
			// Other validation failures are allowed through with errors.
			itsLogger.debug("Validating metadata");
			boolean metadataPassed = validateMetadata(itsOutput.getMetadata());
			if (!metadataPassed) {
				itsOutput.clear(true);
				ok = false;
			}
			
			if (ok) {
				itsLogger.debug("Processing data records");
				
				for (List<String> record: itsInputData) {
					itsRecordCount++;
					itsLogger.trace("Processing record " + itsRecordCount);
					SocatDataRecord socatRecord = new SocatDataRecord(record, itsRecordCount, itsColumnSpec, itsOutput.getMetadata(), itsDateTimeHandler, itsLogger, itsOutput);
					itsOutput.addRecord(socatRecord);
					itsOutput.addMessages(socatRecord.getMessages());
				}
				
				// Check for missing/out-of-range values
				checkDataValues();
				
				// Generate metadata from data
				generateMetadataFromData();
				
				// Run data sanity checks
				runSanityChecks();
			}
		} catch (Exception e) {
			itsLogger.fatal("Unhandled exception encountered", e);
			
			Message message = new Message(Message.DATA_MESSAGE, Message.ERROR, itsRecordCount, "Unhandled exception encountered");
			message.addProperty("Error", e.getMessage());
			
			itsOutput.addMessage(message);
			itsOutput.setExitFlag(Output.INTERNAL_ERROR_FLAG);
			itsOutput.clear(true);
		}

		return itsOutput;
	}

	private void runSanityChecks() throws ConfigException {
		
		List<SanityCheck> checkers = SanityCheckConfig.getInstance().getCheckers();
		
		// Loop through all the records
		for (SocatDataRecord record : itsOutput.getRecords()) {
			
			// Loop through all known sanity checkers, and pass the record to them
			for (SanityCheck checker : checkers) {
				checker.processRecord(record);
			}
		}
		
		// Call the final check method for all the checkers,
		// then get any messages and add them to the output
		for (SanityCheck checker : checkers) {
			checker.performFinalCheck();
			itsOutput.addMessages(checker.getMessages());
		}
	}
	

	/**
	 * Generates a MetadataItem object for a given piece of metadata
	 * @param metadataItemName The name of the metadata item
	 * @param metadataValue The value of the metadata
	 * @return An item representing the metadata and its associated options and methods
	 * @throws NoSuchMethodException If an error occurs setting up the MetadataItem object
	 * @throws InstantiationException If an error occurs setting up the MetadataItem object
	 * @throws IllegalAccessException If an error occurs setting up the MetadataItem object
	 * @throws InvocationTargetException If an error occurs setting up the MetadataItem object
	 */
	private MetadataItem createMetadataItem(String metadataItemName, String metadataValue, int line) throws ConfigException, SanityCheckerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		MetadataConfigItem metadataItemConfig = MetadataConfig.getInstance().get(metadataItemName);
		@SuppressWarnings("unchecked")
		Class<? extends MetadataItem> itemClass = metadataItemConfig.getItemClass();
		Constructor<? extends MetadataItem> itemConstructor = itemClass.getConstructor(MetadataConfigItem.class, Integer.TYPE, Logger.class);
		MetadataItem item = itemConstructor.newInstance(metadataItemConfig, line, itsLogger);
		item.setValue(metadataValue, itsDateTimeHandler);
		return item;
	}
	
	/**
	 * Generates metadata values from the file data.
	 * Any conflicting metadata items already present will be overwritten
	 * @param metadataSet The metadata that has been extracted thus far. New metadata will be added to this.
	 */
	private void generateMetadataFromData() throws ConfigException, MetadataException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		List<MetadataItem> allRecordItems = new ArrayList<MetadataItem>();
		Map<String, MetadataItem> metadataSet = itsOutput.getMetadata();
		
		// Loop through every item in the metadata config
		for (String metadataName : MetadataConfig.getInstance().getConfigItemNames()) {
			MetadataConfigItem metadataItemConfig = MetadataConfig.getInstance().get(metadataName);

			@SuppressWarnings("unchecked")
			Class<? extends MetadataItem> itemClass = metadataItemConfig.getItemClass();
			Constructor<? extends MetadataItem> itemConstructor = itemClass.getConstructor(MetadataConfigItem.class, Integer.TYPE, Logger.class);
			MetadataItem item = itemConstructor.newInstance(metadataItemConfig, -1, itsLogger);
			
			// See if the item's value can be generated. If so...
			if (item.canGenerate()) {
				
				// If it can't be generated from one record, add it to the list of items for later processing
				if (!item.canGenerateFromOneRecord()) {
					allRecordItems.add(item);
				} else {
					item.processRecordForValue(metadataSet, itsOutput.getRecords().get(0));
					item.generateValue(itsDateTimeHandler);
				}
			}
		}
		
		// Assuming there's more than one metadata item in the list for later processing...
		
		// Loop through all the records from the file, and pass each record to each metadata entry in turn.
		for (SocatDataRecord record: itsOutput.getRecords()) {
			for (MetadataItem item : allRecordItems) {
				item.processRecordForValue(metadataSet, record);
			}
		}
		
		// After that, call generateValue on each metadata item to set its final value,
		// and add it to the metadata set.
		for (MetadataItem item: allRecordItems) {
			item.generateValue(itsDateTimeHandler);
			metadataSet.put(item.getName(), item);
		}
	}
	
	/**
	 * Checks the read in data for missing, invalid and out-of-range values
	 */
	private void checkDataValues() throws ConfigException, SocatDataBaseException {
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
			SocatColumnConfigItem columnConfig = SocatColumnConfig.getInstance().getColumnConfig(columnName);

			int currentRecord = 0;
			for (SocatDataRecord record : itsOutput.getRecords()) {
				currentRecord++;
				SocatDataColumn column = record.getColumn(columnName);
				
				// If the column is empty, see if it's required and set the flag if it is.
				if (column.isEmpty()) {
					if (columnConfig.isRequired()) {
						
						// Check the Required Group
						if (CheckerUtils.isEmpty(columnConfig.getRequiredGroup())) {
							// No required group, so we just report the missing value
							itsLogger.trace("Missing required value on line " + currentRecord + ", column '" + columnName + "'");
							column.setFlag(columnConfig.getMissingFlag(), messages, currentRecord, "Missing required value");
						} else {
							// We're in a required group, so check the values from the other fields.
							// Only if they're all missing do we set the missing flag
							List<String> requiredGroupValues = record.getRequiredGroupValues(columnConfig.getRequiredGroup());
							if (CheckerUtils.isEmpty(requiredGroupValues)) {
								
								// Only report required group columns that are in the input file
								if (null != column.getInputColumnName()) {
									itsLogger.trace("Missing required value on line " + currentRecord + ", column '" + columnName + "'");
									column.setFlag(columnConfig.getMissingFlag(), messages, currentRecord, "Missing required value");
								}
							}
						}
					}
				} else if (columnConfig.isNumeric()) {
					
					// If it's supposed to be numeric and isn't, this is always bad!
					if (!CheckerUtils.isNumeric(column.getValue())) {
						itsLogger.trace("Non-parseable numeric value on line " + record.getLineNumber() + ", column '" + columnName + "'");
						column.setFlag(SocatColumnConfigItem.BAD_FLAG, messages, currentRecord, "Non-numeric value");
					} else {
						int rangeCheckFlag = columnConfig.checkRange(Double.parseDouble(column.getValue()));

						if (SocatColumnConfigItem.GOOD_FLAG != rangeCheckFlag) {
							String message = "Value is out of range on line " + record.getLineNumber() + ", column '" + columnName + "'";
							itsLogger.trace(message);
							column.setFlag(rangeCheckFlag, messages, currentRecord, "Value out of range");
						}
					}
				}
			}
		}
		
		itsOutput.addMessages(messages);
	}

	/**
	 * Processes the passed in metadata and adds the results to the output.
	 * @throws ConfigException If an error occurs while retrieving the configuration (this shouldn't happen!)
	 * @throws Exception If any exceptions occur while creating metadata item objects (most likely to be Refelction issues)
	 */
	private void processInputMetadata() throws ConfigException, Exception {
		for (String name : itsInputMetadata.stringPropertyNames()) {
			String value = itsInputMetadata.getProperty(name);
			
			MetadataConfigItem config = MetadataConfig.getInstance().get(name);
			if (null == config) {
				itsLogger.trace("Unrecognised metadata item '" + name);
				
				Message message = new Message(Message.METADATA_MESSAGE, Message.WARNING, -1, "Unrecognised metadata item");
				itsOutput.setExitFlag(Output.WARNINGS_FLAG);
				itsOutput.addMessage(message);
				itsLogger.warn("Unrecognised metadata item " + name);
			} else {
				itsLogger.trace("Metadata item: " + name + " = " + value);
				
				try {
					itsOutput.addMetadataItem(createMetadataItem(name, value, -1));					
				} catch (Exception e) {
					if (e.getCause() instanceof DateTimeException) {
						Message message = new Message(Message.METADATA_MESSAGE, Message.ERROR, -1, name, "Invalid date format");
						itsOutput.setExitFlag(Output.ERRORS_FLAG);
						itsOutput.addMessage(message);
						itsLogger.error("Invalid date format for metadata item " + name);
					} else {
						// Other exceptions are internal Java issues from Reflection. There's not much
						// we can do about them
						throw e;
					}
				}
			}
		}
	}
	
	/**
	 * Validates all metadata items in a given set of metadata. Warnings and errors are logged as required.
	 * If any metadata items are required, but are not configured to be generated from the data, the method
	 * returns {@code false}. In all other cases it returns {@code true}.
	 * @param metadata The metadata to be validated
	 * @return {@code false} if required items are missing but cannot be generated from the data; {@code true} otherwise.
	 */
	private boolean validateMetadata(Map<String, MetadataItem> metadata) throws ConfigException {
		boolean validatedOK = true;

		// Ensure that all required values are present
		validatedOK = checkRequiredMetadata(metadata);
		
		// Ensure that all grouped value requirements are met
		boolean requiredGroupsOK = checkRequiredMetadataGroups(metadata);
		if (!requiredGroupsOK)
			validatedOK = false;
		
		
		// Finally, validate each value individually.
		// The severity of any validation failure is determined by the validation method.
		for (String metadataName : metadata.keySet()) {
			MetadataItem value = metadata.get(metadataName);
			Message validateResult = value.validate(itsLogger);

			if (validateResult != null) {
				if (validateResult.getSeverity() == Message.ERROR) {
					validatedOK = false;
				}
				
				itsOutput.addMessage(validateResult);
			}
		}
		return validatedOK;
	}

	/**
	 * Check that all required metadata items are present.
	 * Note that if the metadata item in question is generated automatically
	 * then we don't expect it to be there.
	 * @param metadata The metadata to be checked
	 * @return {@code true} if all required metadata entries are present; {@code false} otherwise.
	 */
	private boolean checkRequiredMetadata(Map<String, MetadataItem> metadata) throws ConfigException {
		boolean ok = true;

		for (String metadataName : MetadataConfig.getInstance().getConfigItemNames()) {
			MetadataConfigItem configuredItem = MetadataConfig.getInstance().get(metadataName);
			
			// Things in required groups are processed separately
			if (!configuredItem.isInRequiredGroup()) {
				if (configuredItem.isRequired() && !configuredItem.autoGenerated()) {
					if (!metadata.containsKey(metadataName)) {
						itsLogger.error("Required metadata item '" + metadataName + "' is missing");
						ok = false;
						itsOutput.setExitFlag(Output.ERRORS_FLAG);

						Message message = new Message(Message.METADATA_MESSAGE, Message.ERROR, -1, metadataName, "Required metadata value is missng");
						itsOutput.addMessage(message);
					}
				}
			}
		}
		return ok;
	}

	/**
	 * Check that at least one representative item is present for each metadata group.
	 * If one of the entries is auto-generated, then the check will pass.
	 * @param metadata
	 * @return
	 */
	private boolean checkRequiredMetadataGroups(Map<String, MetadataItem> metadata) throws ConfigException {
		boolean ok = true;
		
		MetadataConfigRequiredGroups requiredGroups = MetadataConfig.getInstance().getRequiredGroups();
		for (String groupName : requiredGroups.getGroupNames()) {
			boolean groupOK = false;
			
			List<String> groupedItemNames = requiredGroups.getGroupedItemNames(groupName);
			for (String groupedItemName : groupedItemNames) {
				MetadataConfigItem configItem = MetadataConfig.getInstance().get(groupedItemName);
				if (metadata.get(groupedItemName) != null || configItem.autoGenerated()) {
					groupOK = true;
				}
			}
			
			if (!groupOK) {
				itsLogger.error("At least one of " + groupedItemNames + " must be present in the metadata");
				itsOutput.setExitFlag(Output.ERRORS_FLAG);
				ok = false;
				
				Message message = new Message(Message.METADATA_MESSAGE, Message.ERROR, -1, groupedItemNames.toString(), "At least one of these metadata items must be present");
				itsOutput.addMessage(message);
			}
		}
		return ok;
	}
}
