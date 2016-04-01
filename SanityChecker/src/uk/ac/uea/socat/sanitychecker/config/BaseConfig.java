package uk.ac.uea.socat.sanitychecker.config;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.exeter.QCRoutines.config.RoutinesConfig;

/**
 * The base configuration for the Sanity Checker.
 * 
 * This configuration contains basic configuration for the Sanity Checker, such as logging information
 * and the location of other more detailed configuration files for the program. 
 *
 */
public class BaseConfig extends Properties {

	/**
	 * Version string for the BaseConfig class. This should be changed if the
	 * class is ever updated and becomes incompatible with previous versions.
	 */
	private static final long serialVersionUID = 10001002L;
	
	/**
	 * The config value containing the location of the metadata processing configuration.
	 */
	public static final String METADATA_CONFIG_FILE = "MetadataConfigFile";
	
	/**
	 * The config value containing the location of the SOCAT data format configuration.
	 */
	public static final String SOCAT_CONFIG_FILE = "SocatConfigFile";
	
	/**
	 * The config value containing the location of the column specification XML Schema file.
	 */
	public static final String COLUMN_SPEC_SCHEMA_FILE = "ColumnSpecSchemaFile";
	
	/**
	 * The config value containing the location of the column conversion configuration.
	 */
	public static final String COLUMN_CONVERSION_FILE = "ColumnConversionFile";
	
	public static final String SANITY_CHECK_CONFIG_FILE = "SanityChecksFile";
	
	/**
	 * The file containing the configuration.
	 * Must be set via {@link #init(String, Logger)} before
	 * calling {@link #getInstance()}.
	 */
	private static String itsConfigFile = null;
	
	/**
	 * The logger to be used for all configuration files.
	 */
	private static Logger itsLogger = null;
	
	/**
	 * The singleton instance of the base configuration
	 */
	private static BaseConfig baseConfigInstance = null;
	

	/**
	 * Constructs an empty {@link BaseConfig} object.
	 * This only exists to ensure that {@link BaseConfig}
	 * objects cannot be created except through the {@link #getInstance()}
	 * method. 
	 */
	private BaseConfig() {
		super();
	}
	
	/**
	 * Retrieves an instance of the {@link BaseConfig}. If it doesn't exist, create it.
	 * @return The {@link BaseConfig} instance
	 * @throws ConfigException If an error occurs while loading the configuration
	 */
	public static BaseConfig getInstance() throws ConfigException {
		if (baseConfigInstance == null) {
			loadConfig();
		}
		
		return baseConfigInstance;
	}
	
	/**
	 * Destroy the current instance of the base config.
	 */
	public static void destroy() {
		baseConfigInstance = null;
	}
	
	/**
	 * Set the location of the {@link BaseConfig} file.
	 * @param filenameSet the location of the {@link BaseConfig} file
	 * @param logger A logger instance
	 */
	public static void init(String filename, Logger logger) {
		itsConfigFile = filename;
		itsLogger = logger;
	}
	
	/**
	 * Checks to see if the {@link BaseConfig} has been initialised with a filename.
	 * This does not guarantee that the configuration is valid.
	 * 
	 * @return {@code true} if the {@link BaseConfig} has been initialised; {@code false} if it hasn't.
	 */
	public static boolean isInitialised() {
		return (itsConfigFile != null);
	}
		
	/**
	 * Load a configuration from the specified file location.
	 * @param configFile The name of the file containing the configuration.
	 * @return The loaded configuration.
	 * @throws ConfigException If the configuration cannot be loaded or is invalid.
	 */
	private static void loadConfig() throws ConfigException {
		if (itsConfigFile == null) {
			throw new ConfigException(null, "Base config file location has not been set");
		}
		
		BaseConfig loadedConfig = new BaseConfig();
		
		// Make sure the file exists and we can read it.
		File fileCheck = new File(itsConfigFile);
		if (!fileCheck.exists()) {
			throw new ConfigException(itsConfigFile, "File does not exist");
		} else if (!fileCheck.canRead()) {
			throw new ConfigException(itsConfigFile, "Cannot access file for reading");
		}
		
		// Load the file.
		try {
			Reader configReader = new FileReader(itsConfigFile);
			loadedConfig.load(configReader);
		} catch (Exception e) {
			throw new ConfigException(itsConfigFile, "Error reading base configuration file", e);
		}
		
		// Validate the file.
		loadedConfig.validate();
		
		// Log the config details
		itsLogger.trace("Base configuration loaded.");
		itsLogger.trace("Metadata config file = " + loadedConfig.getMetadataConfigFile());
		itsLogger.trace("Column specification schema file = " + loadedConfig.getColumnSpecSchemaFile());
		itsLogger.trace("Column conversion config file = " + loadedConfig.getColumnConversionConfigFile());
		itsLogger.trace("SOCAT data config file = " + loadedConfig.getSocatConfigFile());
		itsLogger.trace("Sanity Check config file = " + loadedConfig.getSanityCheckConfigFile());

		// Initialise the configurations specified in the base config
		MetadataConfig.init(loadedConfig.getMetadataConfigFile(), itsLogger);
		ColumnConversionConfig.init(loadedConfig.getColumnConversionConfigFile(), itsLogger);
		SocatColumnConfig.init(loadedConfig.getSocatConfigFile(), itsLogger);
		
		try {
			RoutinesConfig.init(loadedConfig.getSanityCheckConfigFile());
		} catch (uk.ac.exeter.QCRoutines.config.ConfigException e) {
			throw new ConfigException(loadedConfig.getSanityCheckConfigFile(), e.getMessage(), e);
		}
		
		// Store the loaded configuration as the singleton instance
		baseConfigInstance = loadedConfig;
	}
	
	/**
	 * Returns the name of the file containing the configuration for metadata processing.
	 * @return The name of the file containing the configuration for metadata processing.
	 */
	public String getMetadataConfigFile() {
		return getProperty(METADATA_CONFIG_FILE);
	}
	
	/**
	 * Returns the name of the file containing the configuration of the SOCAT output files
	 * @return The name of the file containing the configuration of the SOCAT output files
	 */
	public String getSocatConfigFile() {
		return getProperty(SOCAT_CONFIG_FILE);
	}
	
	/**
	 * Returns the name of the file containing the configuration of the Sanity Checkers
	 * @return The name of the file containing the configuration of the Sanity Checkers
	 */
	public String getSanityCheckConfigFile() {
		return getProperty(SANITY_CHECK_CONFIG_FILE);
	}
	
	/**
	 * Returns the {@code File} object for the column specification XML schema file.
	 * @return The {@code File} object for the column specification XML schema file.
	 */
	public String getColumnSpecSchemaFile() {
		return getProperty(COLUMN_SPEC_SCHEMA_FILE);
	}
	
	/**
	 * Returns the {@code File} object for the column conversion configuration file.
	 * @return The {@code File} object for the column conversion configuration file.
	 */
	public String getColumnConversionConfigFile() {
		return getProperty(COLUMN_CONVERSION_FILE);
	}

	/**
	 * Ensures that the configuration file contains all the required values, and that they are valid.
	 * @throws ConfigException If any of the configuration entries fails validation.
	 */
	private void validate() throws ConfigException {
		File columnSpecSchemaFile = new File(getProperty(COLUMN_SPEC_SCHEMA_FILE));
		if (!columnSpecSchemaFile.exists()) {
			throw new ConfigException(itsConfigFile, "Specified " + COLUMN_SPEC_SCHEMA_FILE + " does not exist");
		} else if (!columnSpecSchemaFile.isFile()) {
			throw new ConfigException(itsConfigFile, "Specified " + COLUMN_SPEC_SCHEMA_FILE + " is not a file");
		} else if (!columnSpecSchemaFile.canRead()) {
			throw new ConfigException(itsConfigFile, "No permission to read specified " + COLUMN_SPEC_SCHEMA_FILE);
		}
		
		validateMetadataConfig();
		validateColumnConversionConfig();
		
		// The SocatColumnConfig is validated in SanityChecker.initConfig, by calling its getInstance() method.
	}

	/**
	 * Ensure the metadata config file location has been configured correctly, and that the Sanity Checker
	 * can access it.
	 * @throws ConfigException If the file isn't specified or can't be accessed.
	 */
	private void validateMetadataConfig() throws ConfigException {
		
		// Get the log file value. If it's missing, throw an exception.
		String configFile = getMetadataConfigFile();
		if (null == configFile) {
			throw new ConfigException(itsConfigFile, "Missing config value " + METADATA_CONFIG_FILE);
		}
		
		File theFile = new File(configFile);
		
		// Make sure the file exists
		if (!theFile.exists()) {
			throw new ConfigException(itsConfigFile, "The specified " + METADATA_CONFIG_FILE + " does not exist");
		}
		
		// Make sure we can read it
		if (!theFile.isFile()) {
			throw new ConfigException(itsConfigFile, "The specified " + METADATA_CONFIG_FILE + " is not a regular file");
		}
		
		if (!theFile.canRead()) {
			throw new ConfigException(itsConfigFile, "Cannot access specified " + METADATA_CONFIG_FILE);
		}
	}

	/**
	 * Ensure the data config file location has been configured correctly, and that the Sanity Checker
	 * can access it.
	 * @throws ConfigException If the file isn't specified or can't be accessed.
	 */
	private void validateColumnConversionConfig() throws ConfigException {
		
		// Get the log file value. If it's missing, throw an exception.
		String configFile = getColumnConversionConfigFile();
		if (null == configFile) {
			throw new ConfigException(itsConfigFile, "Missing config value " + COLUMN_CONVERSION_FILE);
		}
		
		File theFile = new File(configFile);
		
		// Make sure the file exists
		if (!theFile.exists()) {
			throw new ConfigException(itsConfigFile, "The specified " + COLUMN_CONVERSION_FILE + " does not exist");
		}
		
		// Make sure we can read it
		if (!theFile.isFile()) {
			throw new ConfigException(itsConfigFile, "The specified " + COLUMN_CONVERSION_FILE + " is not a regular file");
		}

		if (!theFile.canRead()) {
			throw new ConfigException(itsConfigFile, "Cannot access specified " + COLUMN_CONVERSION_FILE);
		}
	}
}
