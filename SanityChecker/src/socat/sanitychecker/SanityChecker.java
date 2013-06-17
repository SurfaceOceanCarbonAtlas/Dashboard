package socat.sanitychecker;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import socat.sanitychecker.config.BaseConfig;
import socat.sanitychecker.config.ColumnConversionConfig;
import socat.sanitychecker.config.ConfigException;
import socat.sanitychecker.config.DataConfig;
import socat.sanitychecker.config.MetadataConfig;
import socat.sanitychecker.config.MetadataConfigItem;
import socat.sanitychecker.config.MetadataConfigRequiredGroups;
import socat.sanitychecker.config.SocatColumnConfig;
import socat.sanitychecker.config.SocatColumnConfigItem;
import socat.sanitychecker.config.SocatDataBaseException;
import socat.sanitychecker.data.ColumnSpec;
import socat.sanitychecker.data.DateColumnInfo;
import socat.sanitychecker.data.InvalidColumnSpecException;
import socat.sanitychecker.data.SocatDataColumn;
import socat.sanitychecker.data.SocatDataRecord;
import socat.sanitychecker.data.StandardColumnInfo;
import socat.sanitychecker.data.datetime.DateTimeException;
import socat.sanitychecker.data.datetime.DateTimeHandler;
import socat.sanitychecker.metadata.MetadataException;
import socat.sanitychecker.metadata.MetadataItem;

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
	 * Exit code flag indicating that no errors or warnings occurred.
	 * This is the default exit code if everything works smoothly.
	 * 
	 * This code is also used by some functions to indicate a NO_ERROR state.
	 */
	public static final int NO_ERROR_FLAG = 0;
	
	/**
	 * Exit code flag indicating that no output files were generated from
	 * processing the input file.
	 */
	public static final int NO_OUTPUT_FLAG = 1;

	/**
	 * Exit code flag indicating that warnings were generated while processing
	 * the input file.
	 */
	public static final int WARNINGS_FLAG = 1 << 1;

	/**
	 * Exit code flag indicating that errors were generated while processing the
	 * input file.
	 */
	public static final int ERRORS_FLAG = 1 << 2;

	/**
	 * Exit code flag indicating that the input data supplied to the Sanity Checker was
	 * invalid.
	 */
	public static final int INVALID_INPUT_FLAG = 1 << 3;

	/**
	 * Exit code flag indicating that the Sanity Checker's configuration is invalid
	 */
	public static final int INVALID_CONFIG_FLAG = 1 << 4;
	
	/**
	 * Exit code flag indicating that an internal error occurred.
	 */
	public static final int INTERNAL_ERROR_FLAG = 1 << 5;

	/**
	 * Filename base argument flag
	 */
	public static final String OUTPUT_FILENAME_BASE_ARG = "-outputfilebase";
	
	/**
	 * The standard output output format for dates
	 */
	public static final DateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	/**
	 * The Sanity Checker will output a number of files. Each file will begin with contents
	 * of this field, plus a hyphen '{@code -}'.
	 * 
	 * This value is also used to identify specific runs of the Sanity Checker in its log file.
	 */
	private String itsOutputBase = null;
	
	/**
	 * The base configuration for the Sanity Checker
	 */
	private BaseConfig itsBaseConfig;
	
	/**
	 * The metadata configuration for the Sanity Checker
	 */
	private MetadataConfig itsMetadataConfig;
	
	/**
	 * The data configuration for the Sanity Checker
	 */
	private DataConfig itsDataConfig;

	/**
	 * The column conversion configuration for the Sanity Checker
	 */
	private ColumnConversionConfig itsColumnConversionConfig;
	
	/**
	 * The configuration of the output SOCAT columns
	 */
	private SocatColumnConfig itsSocatColumnConfig;
	
	/**
	 * A map storing any metadata values passed in on the command line
	 */
	private HashMap<String, MetadataItem> itsPassedMetadataValues;
	
	/**
	 * The file containing the column configuration for the data file
	 */
	private File itsColumnConfigFile;
	
	/**
	 * Lookup table of the data conversion classes used to convert input data
	 * to the required SOCAT data formats
	 */
	private ColumnConversionConfig itsDataConversionConfig;
	
	/**
	 * The data file
	 */
	private File itsDataFile;
	
	/**
	 * The logger for this instance of the Sanity Checker
	 */
	private Logger itsLogger;
	
	/**
	 * The exit code returned by the program.
	 */
	private int itsExitCode = 0;
	
	/**
	 * Stores the current line in the file being processed.
	 */
	private int itsCurrentLine = 0;
	
	/**
	 * The object containing details for the XML output from the Sanity Checker
	 */
	private Output itsOutput;
	
	private List<SocatDataRecord> itsDataRecords;
	
	/**
	 * Base constructor for the central Sanity Checker object.
	 */
	private SanityChecker() {
		itsPassedMetadataValues = new HashMap<String, MetadataItem>(10);
		itsOutput = new Output();
		itsDataRecords = new ArrayList<SocatDataRecord>();
	}
	
	/**
	 * This is the startup method for the Sanity Checker. It does very little
	 * itself, other than call methods to read configuration and
	 * actually do the processing.
	 * 
	 * The command line arguments to the Sanity Checker are as follows: 
	 * TODO Fill in the command line arguments
	 * @param args Command line arguments to the Sanity Checker.
	 * 
	 */
	public static void main(String[] args) {
		
		try {
			boolean ok = true;

			// Instantiate a main program object. This will be passed around so
			// the main processing parts of the program can access information
			// and set exit flags.
			SanityChecker programInstance = new SanityChecker();
			
			Logger logger = Logger.getLogger("Sanity Checker_INIT");
			programInstance.setLogger(logger);
			logger.trace("Logging initialised");
			
			
			// Two-digit dates are handled, but only up to a certain time point. After that we must
			// fail. At the time of writing, we start failing 1 year before the program will actually
			// stop working reliably, which gives someone enough time to remove this check and work out a fix.
			DateTime now = new DateTime();
			if (now.getYear() >= DateTimeHandler.FINAL_RUN_YEAR) {
				programInstance.getLogger().fatal("This program will no longer run, because two-digit years cannot be supported.");
				programInstance.setExitFlag(INTERNAL_ERROR_FLAG);
				ok = false;
			}
			
			// Load the base configuration and process the command line
			if (ok) {
				try {
					logger.debug("Reading base configuration");
					programInstance.setBaseConfig(BaseConfig.loadConfig());
					logger.trace("Base configuration loaded.");
					logger.trace("Metadata config file = " + programInstance.getBaseConfig().getMetadataConfigFile());
					logger.trace("Data config file = " + programInstance.getBaseConfig().getDataConfigFile());
					logger.trace("Column specification schema file = " + programInstance.getBaseConfig().getColumnSpecSchemaFile());
					logger.trace("Column conversion config file = " + programInstance.getBaseConfig().getColumnConversionConfigFile());
					logger.trace("SOCAT data config file = " + programInstance.getBaseConfig().getSocatConfigFile());
					
					// Set up the metadata configuration
					programInstance.setMetadataConfig(new MetadataConfig(programInstance.getBaseConfig().getMetadataConfigFile(), programInstance.getLogger()));
					
					// Set up the data configuration
					programInstance.setDataConfig(DataConfig.loadDataConfig(programInstance.getBaseConfig().getDataConfigFile(), programInstance.getLogger()));
					
					// Set up data conversion routines
					programInstance.setColumnConversionConfig(new ColumnConversionConfig(new File(programInstance.getBaseConfig().getColumnConversionConfigFile()), programInstance.getLogger()));
					
					// Set up the SOCAT column configuration
					programInstance.setSocatColumnConfig(new SocatColumnConfig(programInstance.getBaseConfig().getSocatConfigFile(), programInstance.getLogger()));
					
				
					// Read and process the command line arguments
					logger.debug("Processing command line arguments");
					programInstance.processCommandLine(args);	
					logger.trace("Output filename base = " + programInstance.getFilenameBase());
					
					// Switch the logger to log for this instance using the output filename base
					logger = Logger.getLogger(programInstance.itsOutputBase);
					programInstance.setLogger(logger);
					logger.debug("Logger switched for instance " + programInstance.getFilenameBase());
					
					// The config is loaded and the command line processed. Let's do some stuff!
					programInstance.processFile();

				} catch (ConfigException e) {
					programInstance.getLogger().fatal(e);
					programInstance.setExitFlag(INVALID_CONFIG_FLAG);
					ok = false;
				} catch (CommandLineException e) {
					programInstance.getLogger().fatal(e);
					programInstance.setExitFlag(INVALID_INPUT_FLAG);
					ok = false;
				}
			}
			
			// Finish the program. Print out the exit code.
			// If the config or command line were invalid,
			// make sure the "No Output Files" flag is set.
			int exitCode = programInstance.getExitCode();
			if ((INVALID_INPUT_FLAG & exitCode) > 0
					|| (INVALID_CONFIG_FLAG & exitCode) > 0) {
				programInstance.setExitFlag(NO_OUTPUT_FLAG);
			}
			
			/*
			 * Tidying up - destroy all singleton classes
			 */
			DateTimeHandler.destroy();
			
			System.out.println(programInstance.getExitCode());
		} catch (Exception e) {

			/*
			 * No exception should ever get this far. If it does, print the
			 * INVALID_CONFIG_FLAG exit code and all the exception details
			 * to the console.
			 * 
			 * If things have gone this badly wrong we can't rely on the
			 * logging mechanism being in a working state.
			 */
			int exitCode = INVALID_CONFIG_FLAG | NO_OUTPUT_FLAG;
			System.out.println(exitCode);
			System.out.println("**** UNCAUGHT EXCEPTION ****");
			System.out.println("An uncaught exception has occurred. This should never happen!");
			System.out.println("");
			System.out.println("------------  STACK TRACE ------------");
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Store the metadata configuration for the Sanity Checker
	 * @param metadataConfig The configuration
	 */
	private void setMetadataConfig(MetadataConfig metadataConfig) {
		itsMetadataConfig = metadataConfig;
	}
	
	/**
	 * Store the data configuration for the Sanity Checker
	 * @param dataConfig The configuration
	 */
	private void setDataConfig(DataConfig dataConfig) {
		itsDataConfig = dataConfig;
	}
	
	/**
	 * Store the column conversion configuration for the Sanity Checker
	 * @param config The configuration
	 */
	private void setColumnConversionConfig(ColumnConversionConfig config) {
		itsColumnConversionConfig = config;
	}
	
	/**
	 * Store the SOCAT column configuration for the Sanity Checker
	 * @param config The configuration
	 */
	private void setSocatColumnConfig(SocatColumnConfig config) {
		itsSocatColumnConfig = config;
	}

	/**
	 * Set a flag on the Sanity Checker's exit code. Any flag set will be added to the
	 * existing flags. Note that once a flag has been set it cannot be un-set.
	 * 
	 * @param flag The flag to be set.
	 */
	public void setExitFlag(int flag) {
		itsExitCode = itsExitCode | flag;
	}

	/**
	 * Returns the current state of the Sanity Checker's exit code.
	 * 
	 * @return The current state of the exit code.
	 */
	public int getExitCode() {
		return itsExitCode;
	}

	/**
	 * Concatenates the output directory and the output filename base to give
	 * the standardised start portion of the output filenames to be used by the
	 * Sanity Checker.
	 * 
	 * @return The filename base for all output files from the Sanity Checker.
	 */
	public String getOutputBase() {
		return itsDataConfig.getOutputDirName() + System.getProperty("file.separator") + itsOutputBase + "-";
	}

	/**
	 * Process the command line arguments
	 * 
	 * @param args The command line arguments
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	private void processCommandLine(String[] args) throws CommandLineException, DateTimeException, SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

		// Read the command line arguments
		readCommandArgs(args);

		if (null == itsOutputBase) {
			throw new CommandLineException("Missing argument " + OUTPUT_FILENAME_BASE_ARG);
		}
		
		if (null == itsColumnConfigFile) {
			throw new CommandLineException("Missing argument -C for column spec XML file");
		}
		
		if (null == itsDataFile) {
			throw new CommandLineException("Missing argument -D for data file");
		}
	}

	/**
	 * Locate and read the date format specification from the command line arguments
	 * @param args The command line arguments
	 * @throws CommandLineException If no specification is present, or the specification is invalid
	 */
	private void readDateFormatArg(String[] args) throws CommandLineException {
		int currentPos = 0;
		boolean foundDateFormat = false;
		
		while (!foundDateFormat && currentPos < args.length) {
			if (args[currentPos].startsWith("-F")) {
				String dateFormat = args[currentPos].substring(2);
				try {
					DateTimeHandler.initialise(dateFormat);
					foundDateFormat = true;
				} catch (DateTimeException e) {
					throw new CommandLineException("Invalid date format specified: " + e.getMessage());
				}
			}
			
			currentPos++;
		}
		
		if (!foundDateFormat) {
			throw new CommandLineException("Missing mandatory date format specification (-F)");
		}
	}
	
	/**
	 * Read arguments from the command line
	 * 
	 * @param args The command line arguments
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 */
	private void readCommandArgs(String[] args) throws CommandLineException, DateTimeException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		// First of all we extract the date format specification - this may be required
		// for other command line arguments
		readDateFormatArg(args);
		
		// Now read the rest of the command line arguments
		int currentPos = 0;

		// Walk through all arguments
		while (currentPos < args.length) {

			// The root of all output filenames (and the name given to log messages)
			if (args[currentPos].equalsIgnoreCase(OUTPUT_FILENAME_BASE_ARG)) {
				// Get the next argument
				currentPos++;
				if (currentPos < args.length) {
					String argValue = args[currentPos];
					itsOutputBase = checkCommandArgValue(OUTPUT_FILENAME_BASE_ARG, argValue);
				}
			// Passed in metadata parameters
			} else if (args[currentPos].startsWith("-M")) {
				
				// Extract the metadata item name
				String metadataItemName = args[currentPos].substring(2);
				
				// Get the item value
				currentPos++;
				String metadataValue = checkCommandArgValue("-M" + metadataItemName, args[currentPos]);
				
				if (!itsMetadataConfig.contains(metadataItemName)) {
					itsLogger.warn("Unrecognised metadata command line argument " + metadataItemName + " - ignoring");
				} else if (itsPassedMetadataValues.containsKey(metadataItemName)) {
					throw new CommandLineException("Command line metadata item " + metadataItemName + " specified more than once");
				} else {
					itsLogger.trace("Metadata item passed in on command line: " + metadataItemName + " = " + metadataValue);
					
					// Create the metadata item and store it
					try {
						MetadataItem item = createMetadataItem(metadataItemName, metadataValue, -1);
					
						itsPassedMetadataValues.put(metadataItemName, item);
					} catch (InvocationTargetException e) {
						if (e.getCause() instanceof ParseException || e.getCause() instanceof NumberFormatException) {
							throw new CommandLineException("Could not parse metadata value for " + metadataItemName + ": invalid data type");
						} else if (e.getCause() instanceof DateTimeException) {
							throw new CommandLineException("Could not parse metadata value for " + metadataItemName + ": invalid date format");
						} else {
							throw e;
						}
					}
				}
				
			} else if (args[currentPos].startsWith("-C")) {
				String colFileName = args[currentPos].substring(2);
				
				itsColumnConfigFile = new File(itsDataConfig.getInputDir(), colFileName);
				if (!itsColumnConfigFile.exists()) {
					throw new CommandLineException("Specified column spec XML file doesn't exist");
				}
				if (!itsColumnConfigFile.canRead()) {
					throw new CommandLineException("Cannot read specified column spec XML file");
				}
				
				itsLogger.trace("Column spec XML file is " + colFileName);
			} else if (args[currentPos].startsWith("-D")) {
				String dataFileName = args[currentPos].substring(2);
				
				itsDataFile = new File(itsDataConfig.getInputDir(), dataFileName);
				if (!itsDataFile.exists()) {
					throw new CommandLineException("Specified column spec XML file doesn't exist");
				}
				if (!itsDataFile.canRead()) {
					throw new CommandLineException("Cannot read specified column spec XML file");
				}
				
				itsLogger.trace("Data file is " + dataFileName);
			
			// Date format flags are ignored
			} else if (!args[currentPos].startsWith("-F")) {
				itsLogger.warn("Unrecognised command line flag " + args[currentPos] + " - ignoring");
			}

			// Move to the next argument
			currentPos++;
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
	private MetadataItem createMetadataItem(String metadataItemName, String metadataValue, int line) throws DateTimeException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		MetadataConfigItem metadataItemConfig = itsMetadataConfig.get(metadataItemName);
		@SuppressWarnings("unchecked")
		Class<? extends MetadataItem> itemClass = metadataItemConfig.getItemClass();
		Constructor<? extends MetadataItem> itemConstructor = itemClass.getConstructor(MetadataConfigItem.class, Integer.TYPE, Logger.class);
		MetadataItem item = itemConstructor.newInstance(metadataItemConfig, line, itsLogger);
		item.setValue(metadataValue);
		return item;
	}
	
	/**
	 * Ensure that a command line argument has a value associated with it
	 * @param argument The argument
	 * @param argValue The value to the argument
	 * @return The value to the argument
	 * @throws CommandLineException If no value is supplied to the argument
	 */
	private String checkCommandArgValue(String argument, String argValue) throws CommandLineException {
		if (argValue.charAt(0) == '-') {
			throw new CommandLineException("Command option " + argument + " does not have a value");
		}
		
		return argValue;
	}
	
	/**
	 * Set the Sanity Checker's base configuration
	 * @param config The configuration object
	 */
	private void setBaseConfig(BaseConfig config) {
		itsBaseConfig = config;
	}
	
	/**
	 * Returns the logger for this instance of the Sanity Checker
	 * @return The logger for this instance of the Sanity Checker 
	 */
	public Logger getLogger() {
		return itsLogger;
	}
	
	/**
	 * Sets the Sanity Checker's logger.
	 * @param logger The logger
	 */
	private void setLogger(Logger logger) {
		itsLogger = logger;
	}
	
	/**
	 * Returns the base of the output filenames
	 * @return The base of the output filenames
	 */
	private String getFilenameBase() {
		return itsOutputBase;
	}
	
	/**
	 * Returns the Sanity Checker's base configuration
	 * @return The Sanity Checker's base configuration
	 */
	private BaseConfig getBaseConfig() {
		return itsBaseConfig;
	}
	
	/**
	 * The main data processing method.
	 * @throws FileNotFoundException If the data file disappears between running the Sanity Checker and processing the data
	 */
	private void processFile() {
		itsLogger.info("Processing data file " + itsDataFile.getName());
		
		boolean ok = true;
		
		ColumnSpec colSpec = null;
		BufferedReader reader = null;
		HashMap<String, MetadataItem> extractedMetadata = null;
		
		try {
			// Load the column specification file
			try {
				itsLogger.debug("Processing column specification");
				colSpec = ColumnSpec.importSpec(itsColumnConfigFile, new File(itsBaseConfig.getColumnSpecSchemaFile()), itsColumnConversionConfig, itsLogger);
			} catch (InvalidColumnSpecException e) {
				itsLogger.fatal(e);
				setExitFlag(INVALID_INPUT_FLAG);
				ok = false;
			}
			
			if (ok) {
				// Load data file
				reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(itsDataFile))));
				
				// Process metadata header
				itsLogger.debug("Extracting metadata from header");
				
				/**
				 * Extract the metadata. If this comes back as null, then we can't continue
				 */
				extractedMetadata = extractMetadata(reader);
				if (null == extractedMetadata) {
					setExitFlag(NO_OUTPUT_FLAG);
					ok = false;
				}
			}
			
			if (ok) {
				
				// Merge the read in metadata with the values passed on the command line
				itsLogger.debug("Merging metadata from command line");
				mergeMetadata(extractedMetadata, itsPassedMetadataValues);
				
				// Validate the metadata items from the header.
				// If required fields are missing, we will fail.
				// Other validation failures are allowed through with errors.
				itsLogger.debug("Validating metadata");
				boolean metadataPassed = validateMetadata(extractedMetadata);
				if (!metadataPassed) {
					setExitFlag(NO_OUTPUT_FLAG);
					ok = false;
				}
				
			}
			
			if (ok) {
				ok = validateColHeaders(reader, colSpec);
				if (!ok) {
					setExitFlag(NO_OUTPUT_FLAG);
					setExitFlag(ERRORS_FLAG);
				}
			}
			
			if (ok) {
				// Read the remainder of the file, processing each line into a SOCAT data record
				itsLogger.debug("Reading data records");
				String line = getNextLine(reader);
				while (null != line) {
					
					itsLogger.trace("Processing line " + itsCurrentLine);
					SocatDataRecord record = new SocatDataRecord(line, itsCurrentLine, colSpec, itsSocatColumnConfig, itsColumnConversionConfig, extractedMetadata, itsLogger);
					
					itsDataRecords.add(record);
					line = getNextLine(reader);
				}
				
				// Close the input file
				reader.close();
				itsLogger.debug("Read " + itsDataRecords.size() + " records from file");
				
				// Check for missing/out-of-range values
				checkDataValues();
				
				// Generate metadata from data
				generateMetadataFromData(extractedMetadata);
				
				// Write output files
			}
			
			// Make sure the input file has been closed
			if (null != reader) {
				reader.close();
			}
		} catch (Exception e) {
			itsLogger.fatal("Unexpected exception", e);
			setExitFlag(INTERNAL_ERROR_FLAG);
			
			//TODO Delete output files
			setExitFlag(NO_OUTPUT_FLAG);
		}
	}
	
	/**
	 * Generates metadata values from the file data.
	 * Any conflicting metadata items already present will be overwritten
	 * @param metadataSet The metadata that has been extracted thus far. New metadata will be added to this.
	 */
	private void generateMetadataFromData(HashMap<String, MetadataItem> metadataSet) throws MetadataException, DateTimeException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		List<MetadataItem> allRecordItems = new ArrayList<MetadataItem>();
		
		// Loop through every item in the metadata config
		for (String metadataName : itsMetadataConfig.getConfigItemNames()) {
			MetadataConfigItem metadataItemConfig = itsMetadataConfig.get(metadataName);

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
					item.processRecordForValue(metadataSet, itsDataRecords.get(0));
					item.generateValue();
				}
			}
		}
		
		// Assuming there's more than one metadata item in the list for later processing...
		
		// Loop through all the records from the file, and pass each record to each metadata entry in turn.
		for (SocatDataRecord record: itsDataRecords) {
			for (MetadataItem item : allRecordItems) {
				item.processRecordForValue(metadataSet, record);
			}
		}
		
		// After that, call generateValue on each metadata item to set its final value,
		// and add it to the metadata set.
		for (MetadataItem item: allRecordItems) {
			item.generateValue();
			metadataSet.put(item.getName(), item);
		}
	}
	
	/**
	 * Checks the read in data for missing, invalid and out-of-range values
	 */
	private void checkDataValues() throws SocatDataBaseException {
		/*
		 * Loop through all the numeric columns, searching for candidates for
		 * values that indicate missing data. For each column this is the
		 * most common out-of-range value.
		 * 
		 * Any such values are replaced with an empty string.
		 */
		for (String columnName : itsSocatColumnConfig.getColumnList()) {
			SocatColumnConfigItem columnConfig = itsSocatColumnConfig.getColumnConfig(columnName);
			
			// If the column is not required and not numeric, we don't do any checks
			if (!columnConfig.isRequired() && !columnConfig.isNumeric()) {
				continue;
			}
			
			itsLogger.trace("Checking values in column '" + columnName + "'");

			if (columnConfig.isNumeric()) {
			
				// Loop through all records looking for candidates
				// for values that indicate missing data.
				MissingValuesCandidates missingValues = new MissingValuesCandidates();
	
				for (SocatDataRecord record : itsDataRecords) {
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
					for (SocatDataRecord record: itsDataRecords) {
						SocatDataColumn column = record.getColumn(columnName);
						if (column.getValue().equalsIgnoreCase(missingValue)) {
							column.setValue("");
						}
					}
				}
			}
		}
		
		/*
		 * Now we can go through all the data looking for required values that are
		 * missing, and numeric values that are out of range. 
		 */
		for (String columnName : itsSocatColumnConfig.getColumnList()) {
			SocatColumnConfigItem columnConfig = itsSocatColumnConfig.getColumnConfig(columnName);

			for (SocatDataRecord record : itsDataRecords) {
				SocatDataColumn column = record.getColumn(columnName);
				
				// If the column is empty, see if it's required and set the flag if it is.
				if (column.isEmpty()) {
					if (columnConfig.isRequired()) {
						
						// Check the Required Group
						if (CheckerUtils.isEmpty(columnConfig.getRequiredGroup())) {
							column.setFlag(columnConfig.getMissingFlag());
							itsLogger.trace("Missing required value on line " + record.getLineNumber() + ", column '" + columnName + "'");
							itsOutput.addDataMessage(new DataMessage(DataMessage.ERROR, record.getLineNumber(), columnConfig.getIndex(), columnName, "Missing required value"));
						} else {
							List<String> requiredGroupValues = record.getRequiredGroupValues(columnConfig.getRequiredGroup());
							if (CheckerUtils.isEmpty(requiredGroupValues)) {
								itsLogger.trace("Missing required value on line " + record.getLineNumber() + ", column '" + columnName + "'");
								column.setFlag(columnConfig.getMissingFlag());
							}
						}
					}
				} else if (columnConfig.isNumeric()) {
					
					// If it's supposed to be numeric and isn't, this is always bad!
					if (!CheckerUtils.isNumeric(column.getValue())) {
						itsLogger.trace("Non-parseable numeric value on line " + record.getLineNumber() + ", column '" + columnName + "'");
						column.setFlag(SocatColumnConfigItem.BAD_FLAG);
					} else {
						if (!columnConfig.isInRange(Double.parseDouble(column.getValue()))) {
							itsLogger.trace("Value is out of range on line " + record.getLineNumber() + ", column '" + columnName + "'");
							column.setFlag(columnConfig.getRangeFlag());
						}
					}
				}
			}
		}
	}

	/**
	 * Extracts the metadata from the file header.
	 * @param reader The reader object for the data file
	 * @return The extracted metadata
	 * @throws IOException If an error occurs while reading from the file
	 * @throws InvocationTargetException If an error occurs while constructing metadata item objects
	 * @throws IllegalAccessException If an error occurs while constructing metadata item objects
	 * @throws InstantiationException If an error occurs while constructing metadata item objects
	 * @throws NoSuchMethodException If an error occurs while constructing metadata item objects
	 */
	private HashMap<String, MetadataItem> extractMetadata(BufferedReader reader) throws IOException, DateTimeException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		HashMap<String, MetadataItem> metadata = new HashMap<String, MetadataItem>();
		
		// Read lines from the file until we find a slash-star on its own.
		String line = getNextLine(reader);
		if (!line.equalsIgnoreCase("/*")) {
			itsLogger.trace("File does not begin with metadata section");
			itsOutput.addMetadataMessage(new MetadataMessage(MetadataMessage.ERROR, itsCurrentLine, "File does not begin with metadata section"));
			setExitFlag(ERRORS_FLAG);
			metadata = null;
		} else {
			
			// Keep reading lines until we find a slash-star or fall off the end of the file
			boolean metadataEnded = false;
			
			while (!metadataEnded) {
				line = getNextLine(reader);
				if (null == line) {
					// We've fallen off the end of the file. This is bad.
					itsLogger.trace("Metadata section has no end '*/'");
					itsOutput.addMetadataMessage(new MetadataMessage(MetadataMessage.ERROR, itsCurrentLine, "Metadata section has no end '*/'"));
					setExitFlag(ERRORS_FLAG);
					metadata = null;
					metadataEnded = true;
				} else if (line.equalsIgnoreCase("*/")) {
					// We've found the end of the metadata header. We can stop now!
					metadataEnded = true;
				} else {
					// Find the = sign, and split the line into name=value
					int equalsPos = line.indexOf("=");
					
					// Make sure the equals sign is in a sensible place. We allow the equals at the end of the line
					// to signify an empty value, which may be possible. The metadata checker routines will decide about that later.
					if (equalsPos == -1 || equalsPos == 0) {
						itsLogger.trace("Malformed metadata line (" + itsCurrentLine + ")");
						itsOutput.addMetadataMessage(new MetadataMessage(MetadataMessage.WARNING, itsCurrentLine, "Malformed metadata line"));
						setExitFlag(WARNINGS_FLAG);
					} else {
					
						String name = line.substring(0, equalsPos).trim();
						String value = line.substring(equalsPos + 1).trim();
						
						MetadataConfigItem config = itsMetadataConfig.get(name);
						if (null == config) {
							itsLogger.trace("Unrecognised metadata item '" + name + "' (" + itsCurrentLine + ")");
							
							MetadataMessage message = new MetadataMessage(MetadataMessage.WARNING, itsCurrentLine, name, "Unrecognised metadata item");
							setExitFlag(WARNINGS_FLAG);
							itsOutput.addMetadataMessage(message);
							itsLogger.warn("Unrecognised metadata item " + name);
						} else {
							itsLogger.trace("Metadata item: " + name + " = " + value);
							
							try {
								metadata.put(name, createMetadataItem(name, value, itsCurrentLine));
								
							} catch (InvocationTargetException e) {
								if (e.getCause() instanceof DateTimeException) {
									MetadataMessage message = new MetadataMessage(MetadataMessage.ERROR, itsCurrentLine, name, "Invalid date format");
									setExitFlag(ERRORS_FLAG);
									itsOutput.addMetadataMessage(message);
									itsLogger.error("Invalid date format for metadata item " + name);
								} else {
									throw e;
								}
							}
						}
					}
				}
			}
		}
		
		return metadata;
	}
	
	/**
	 * Read a line from a file, ignoring blank lines and commented lines.
	 * @param reader The file reader object
	 * @return The next non-blank, non-comment line, or {@code null} if the end of the file is reached.
	 * @throws IOException If an error occurs while reading from the file
	 */
	private String getNextLine(BufferedReader reader) throws IOException {
		String nextLine = null;
		boolean foundLine = false;
		
		while (!foundLine) {
			nextLine = reader.readLine();
			itsCurrentLine++;
			if (null == nextLine) {
				// We've reached the end of the file. The calling function must deal with that.
				foundLine = true;
			} else {
				
				// Empty lines are ignored
				nextLine = nextLine.trim();
				if (nextLine.length() > 0) {
					
					// See if there's a comment indicator.
					// If it's at the start of the line, we ignore the whole line.
					// Otherwise just strip it off and return the rest of the line.
					int commentIndex = nextLine.indexOf("//");
					
					if (commentIndex > 0) {
						nextLine = nextLine.substring(0, commentIndex).trim();
						foundLine = true;
					} else if (commentIndex == -1) {
						foundLine = true;
					}
				}
			}
		}
		
		return nextLine;
	}
	
	/**
	 * Add a set of metadata to a pre-existing set. Additions that are already present in the metadata are ignored.
	 * @param metadata The set of metadata
	 * @param additions The metadata to be added
	 */
	private void mergeMetadata(HashMap<String, MetadataItem> metadata, HashMap<String, MetadataItem> additions) {
		if (null != additions) {
			Iterator<String> additionIterator = additions.keySet().iterator();
			while (additionIterator.hasNext()) {
				String additionName = additionIterator.next();
				if (!metadata.containsKey(additionName)) {
					metadata.put(additionName, additions.get(additionName));
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
	private boolean validateMetadata(HashMap<String, MetadataItem> metadata) {
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
			MetadataMessage validateResult = value.validate(itsLogger);

			if (validateResult != null) {
				switch (validateResult.getSeverity()) {
				case MetadataMessage.WARNING:
				{
					setExitFlag(WARNINGS_FLAG);
					break;
				}
				case MetadataMessage.ERROR:
				{
					setExitFlag(ERRORS_FLAG);
					validatedOK = false;
					break;
				}
				}
				
				itsOutput.addMetadataMessage(validateResult);
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
	private boolean checkRequiredMetadata(HashMap<String, MetadataItem> metadata) {
		boolean ok = true;

		for (String metadataName : itsMetadataConfig.getConfigItemNames()) {
			MetadataConfigItem configuredItem = itsMetadataConfig.get(metadataName);
			
			// Things in required groups are processed separately
			if (!configuredItem.isInRequiredGroup()) {
				if (configuredItem.isRequired() && !configuredItem.autoGenerated()) {
					if (!metadata.containsKey(metadataName)) {
						itsLogger.error("Required metadata item '" + metadataName + "' is missing");
						ok = false;
						setExitFlag(ERRORS_FLAG);

						MetadataMessage message = new MetadataMessage(MetadataMessage.ERROR, -1, metadataName, "Required metadata value is missng");
						itsOutput.addMetadataMessage(message);
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
	private boolean checkRequiredMetadataGroups(HashMap<String, MetadataItem> metadata) {
		boolean ok = true;
		
		MetadataConfigRequiredGroups requiredGroups = itsMetadataConfig.getRequiredGroups();
		for (String groupName : requiredGroups.getGroupNames()) {
			boolean groupOK = false;
			
			List<String> groupedItemNames = requiredGroups.getGroupedItemNames(groupName);
			for (String groupedItemName : groupedItemNames) {
				MetadataConfigItem configItem = itsMetadataConfig.get(groupedItemName);
				if (metadata.get(groupedItemName) != null || configItem.autoGenerated()) {
					groupOK = true;
				}
			}
			
			if (!groupOK) {
				itsLogger.error("At least one of " + groupedItemNames + " must be present in the metadata");
				setExitFlag(ERRORS_FLAG);
				ok = false;
				
				MetadataMessage message = new MetadataMessage(MetadataMessage.ERROR, -1, groupedItemNames.toString(), "At least one of these metadata items must be present");
				itsOutput.addMetadataMessage(message);
			}
		}
		return ok;
	}

	/**
	 * After the metadata section, the next line must contain the column headers
	 * This method checks that all headings specified in the column specification are present.
	 * If any are missing, the file is rejected.
	 * @param reader The file reader object
	 * @param colSpec The column specification for the file
	 * @return {@code true} if the column header line is found and matches the column spec; {@code false} otherwise.
	 */
	private boolean validateColHeaders(BufferedReader reader, ColumnSpec colSpec) throws IOException {
		itsLogger.debug("Validating data column headers");
		boolean colHeadersOK = true;
		
		String line = getNextLine(reader);
		List<String> inputColumns = CheckerUtils.trimAndLowerList(Arrays.asList(line.split(",")));
		itsLogger.trace("Columns in input file:" + inputColumns);
		
		for (String requiredColumn : colSpec.getRequiredInputColumnNames()) {
			itsLogger.trace("Checking for required column '" + requiredColumn + "'");
			if (!inputColumns.contains(requiredColumn.toLowerCase())) {
				itsLogger.error("Data file missing specified column '" + requiredColumn + "'");
				DataMessage message = new DataMessage(DataMessage.ERROR, itsCurrentLine, -1, requiredColumn, "Specified column name not in data file");
				itsOutput.addDataMessage(message);
				colHeadersOK = false;
			}
		}		
		
		return colHeadersOK;
	}
}
