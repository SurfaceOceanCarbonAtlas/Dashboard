package socat.sanitychecker.config;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

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
	private static final long serialVersionUID = 10001001L;
	
	/**
	 * The default location of the Sanity Checker's configuration file.
	 */
	private static final String DEFAULT_CONFIG_FILE = "./config.properties";

	/**
	 * The config value containing the location of the metadata processing configuration.
	 */
	private static final String METADATA_CONFIG_FILE = "MetadataConfigFile";
	
	/**
	 * The config value containing the location of the SOCAT data format configuration.
	 */
	private static final String SOCAT_CONFIG_FILE = "SocatConfigFile";
	
	/**
	 * The config value containing the location of the data processing configuration.
	 */
	private static final String DATA_CONFIG_FILE = "DataConfigFile";
	
	/**
	 * The config value containing the location of the column specification XML Schema file.
	 */
	private static final String COLUMN_SPEC_SCHEMA_FILE = "ColumnSpecSchemaFile";
	
	/**
	 * The config value containing the location of the column conversion configuration.
	 */
	private static final String COLUMN_CONVERSION_FILE = "ColumnConversionFile";
	
	/**
	 * The file containing the configuration.
	 */
	private String itsConfigFile;
	
	/**
	 * Constructs an empty {@code BaseConfig} object. 
	 */
	private BaseConfig(String configFile) {
		super();
		itsConfigFile = configFile;
	}
		
	/**
	 * Load a configuration file from the system default location.
	 * 
	 * @return The loaded configuration.
	 * @throws ConfigException If the configuration cannot be loaded or is invalid.
	 * 
	 */
	public static BaseConfig loadConfig() throws ConfigException {
		return loadConfig(DEFAULT_CONFIG_FILE);
	}
	
	/**
	 * Load a configuration from the specified file location.
	 * @param configFile The name of the file containing the configuration.
	 * @return The loaded configuration.
	 * @throws ConfigException If the configuration cannot be loaded or is invalid.
	 */
	public static BaseConfig loadConfig(String configFile) throws ConfigException {
		BaseConfig loadedConfig = new BaseConfig(configFile);
		
		// Make sure the file exists and we can read it.
		File fileCheck = new File(configFile);
		if (!fileCheck.exists()) {
			throw new ConfigException(configFile, "File does not exist");
		} else if (!fileCheck.canRead()) {
			throw new ConfigException(configFile, "Cannot access file for reading");
		}
		
		// Load the file.
		try {
			Reader configReader = new FileReader(configFile);
			loadedConfig.load(configReader);
		} catch (Exception e) {
			throw new ConfigException(configFile, "Error reading base configuration file", e);
		}
		
		// Validate the file.
		loadedConfig.validate();
		
		return loadedConfig;
	}
	
	/**
	 * Returns the name of the file containing the configuration for metadata processing.
	 * @return The name of the file containing the configuration for metadata processing.
	 */
	public String getMetadataConfigFile() {
		return getProperty(METADATA_CONFIG_FILE);
	}
	
	/**
	 * Returns the name of the file containing the configuration for data processing.
	 * @return The name of the file containing the configuration for data processing.
	 */
	public String getDataConfigFile() {
		return getProperty(DATA_CONFIG_FILE);
	}
	
	/**
	 * Returns the name of the file containing the configuration of the SOCAT output files
	 * @return The name of the file containing the configuration of the SOCAT output files
	 */
	public String getSocatConfigFile() {
		return getProperty(SOCAT_CONFIG_FILE);
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
		validateDataConfig();
		validateColumnConversionConfig();
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
	private void validateDataConfig() throws ConfigException {
		
		// Get the log file value. If it's missing, throw an exception.
		String configFile = getDataConfigFile();
		if (null == configFile) {
			throw new ConfigException(itsConfigFile, "Missing config value " + DATA_CONFIG_FILE);
		}
		
		File theFile = new File(configFile);
		
		// Make sure the file exists
		if (!theFile.exists()) {
			throw new ConfigException(itsConfigFile, "The specified " + DATA_CONFIG_FILE + " does not exist");
		}
		
		// Make sure we can read it
		if (!theFile.isFile()) {
			throw new ConfigException(itsConfigFile, "The specified " + DATA_CONFIG_FILE + " is not a regular file");
		}

		if (!theFile.canRead()) {
			throw new ConfigException(itsConfigFile, "Cannot access specified " + DATA_CONFIG_FILE);
		}
	}

	/**
	 * Ensure the data config file location has been configured correctly, and that the Sanity Checker
	 * can access it.
	 * @throws ConfigException If the file isn't specified or can't be accessed.
	 */
	private void validateColumnConversionConfig() throws ConfigException {
		
		// Get the log file value. If it's missing, throw an exception.
		String configFile = getDataConfigFile();
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
