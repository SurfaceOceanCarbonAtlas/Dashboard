package socat.sanitychecker.config;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Contains the data configuration for the Sanity Checker
 *
 */
public class DataConfig extends Properties {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -6570135024656573304L;

	/**
	 * The name of the configuration file used to build this configuration
	 */
	private String itsConfigFile;
	
	private static final String INPUT_DIR = "InputDir";
	
	private static final String OUTPUT_DIR = "OutputDir";
	
	private DataConfig(String configFile) {
		super();
		itsConfigFile = configFile;
	}
	
	/**
	 * Load and set up a new data configuration for the Sanity Checker
	 * @param configFile The name of the configuration file to read
	 * @param logger The logger
	 * @return The loaded configuration
	 */
	public static DataConfig loadDataConfig(String configFile, Logger logger) throws ConfigException {
		DataConfig loadedConfig = new DataConfig(configFile);
		
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
			throw new ConfigException(configFile, "Error reading data configuration file", e);
		}
		
		// Validate the file.
		loadedConfig.validate();
		
		return loadedConfig;
	}
	
	/**
	 * Returns the directory from which input files will be read.
	 * @return The directory from which input files will be read.
	 */
	public File getInputDir() {
		return new File(getInputDirName());
	}
	
	/**
	 * Returns the directory from which input files will be read as a string path.
	 * @return The directory from which input files will be read as a string path.
	 */
	public String getInputDirName() {
		return getProperty(INPUT_DIR);
	}
	
	/**
	 * Returns the directory in which output files will be stored
	 * @return The directory in which output files will be stored
	 */
	public File getOutputDir() {
		return new File(getOutputDirName());
	}
	
	/**
	 * Returns the directory in which output files will be stored as a string path
	 * @return The directory in which output files will be stored as a string path
	 */
	public String getOutputDirName() {
		return getProperty(OUTPUT_DIR);
	}

	/**
	 * Validate the loaded configuration
	 * @throws ConfigException If the configuration fails validation
	 */
	private void validate() throws ConfigException {
		if (null == getProperty(INPUT_DIR)) {
			throw new ConfigException(itsConfigFile, INPUT_DIR + " not specified in file");
		}
		
		if (null == getProperty(OUTPUT_DIR)) {
			throw new ConfigException(itsConfigFile, OUTPUT_DIR + " not specified in file");
		}
		
		checkDir(INPUT_DIR, getInputDirName(), true, false);
		checkDir(OUTPUT_DIR, getOutputDirName(), false, true);
	}
	
	/**
	 * Check a directory's properties, including read/write access as required.
	 * @param name The name of the directory
	 * @param checkRead If read access should be checked
	 * @param checkWrite If write access should be checked
	 * @throws ConfigException If any of the checks fails
	 */
	private void checkDir(String dirRef, String name, boolean checkRead, boolean checkWrite) throws ConfigException {
		File dir = new File(name);
		if (!dir.exists()) {
			throw new ConfigException(itsConfigFile, dirRef + " " + name + " does not exist");
		} else if (!dir.isDirectory()) {
			throw new ConfigException(itsConfigFile, dirRef + " " + name + " is not a directory");
		}
		
		if (checkRead) {
			if (!dir.canRead()) {
				throw new ConfigException(itsConfigFile, "Cannot read from " + dirRef + " " + name);
			}
		}
		
		if (checkWrite) {
			if (!dir.canWrite()) {
				throw new ConfigException(itsConfigFile, "Cannot write to " + dirRef + " " + name);
			}
		}
	}
}
