package uk.ac.uea.socat.sanitychecker.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.sanitychecks.SanityCheck;
import uk.ac.uea.socat.sanitychecker.sanitychecks.SanityCheckException;

/**
 * Represents the configuration of the sanity checkers to be run against
 * input files.
 */
public class SanityCheckConfig {
	
	/**
	 * The name of the package in which all sanity checker classes will be stored
	 */
	private static final String SANITY_CHECK_CLASS_ROOT = "uk.ac.uea.socat.sanitychecker.sanitychecks.";

	/**
	 * All sanity check class names must end with the same text
	 */
	private static final String SANITY_CHECK_CLASS_TAIL = "SanityCheck";

	/**
	 * The list of sanity checker objects. All records in the input file will be passed to
	 * each of these in turn.
	 */
	private List<CheckerInitData> itsSanityCheckClasses;
	
	/**
	 * The name of the configuration file for the sanity checks
	 */
	private static String itsConfigFilename = null;
	
	/** 
	 * A logger instance
	 */
	private static Logger itsLogger = null;
	
	/**
	 * The singleton instance of this class
	 */
	private static SanityCheckConfig sanityCheckConfigInstance = null;
	
	/**
	 * Initialises the sanity checker configuration. This cannot be run
	 * until {@link SanityCheckConfig#init(String, Logger)} has been called.
	 * @throws ConfigException If the configuration cannot be loaded
	 */
	public SanityCheckConfig() throws ConfigException {
		if (itsConfigFilename == null) {
			throw new ConfigException(null, "SanityCheckConfig filename has not been set - must run init first");
		}
		
		itsSanityCheckClasses = new ArrayList<CheckerInitData>();
		readFile();
	}
	
	/**
	 * Initialise the variables required to bootstrap the SanityCheckerConfig
	 * @param filename The name of the configuration file
	 * @param logger A logger instance
	 */
	public static void init(String filename, Logger logger) {
		itsConfigFilename = filename;
		itsLogger = logger;
	}
	
	/**
	 * Retrieves the singleton instance of the SanityCheckerConfig, creating it if it
	 * does not exist
	 * @return An instance of the SanityCheckerConfig
	 * @throws ConfigException If the configuration cannot be loaded
	 */
	public static SanityCheckConfig getInstance() throws ConfigException {
		if (null == sanityCheckConfigInstance) {
			sanityCheckConfigInstance = new SanityCheckConfig();
		}
		
		return sanityCheckConfigInstance;
	}
	
	/**
	 * Destroys the singleton instance of the SanityCheckerConfig
	 */
	public static void destroy() {
		sanityCheckConfigInstance = null;
	}
	
	/**
	 * Read and parse the configuration file
	 * @throws ConfigException If the configuration cannot be loaded
	 */
	private void readFile() throws ConfigException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(itsConfigFilename));
			try {
				String line = reader.readLine();
				int lineCount = 1;
				
				while (null != line) {
					if (!CheckerUtils.isComment(line)) {
						List<String> fields = Arrays.asList(line.split(","));
						fields = CheckerUtils.trimList(fields);
						
						// The first field is the class name. Grab it and remove
						// it from the list, so what's left is the parameters.
						String className = fields.remove(0);
						String fullClassName = SANITY_CHECK_CLASS_ROOT + className + SANITY_CHECK_CLASS_TAIL;

						if (className.equalsIgnoreCase("")) {
							throw new ConfigException(itsConfigFilename, lineCount, "Sanity Check class name cannot be empty");
						} else {
							try {
								// Instantiate the class and call the initialise method
								// to make sure everything's OK.
								itsLogger.trace("Initialising Sanity Check class " + fullClassName);
								Class<?> checkClass = Class.forName(fullClassName);
								SanityCheck checkInstance = (SanityCheck) checkClass.newInstance();
								checkInstance.initialise(fields);
								
								// Add the checker class to the list of all known checkers.
								// These will be instantiated in the getInstances() method.
								itsSanityCheckClasses.add(new CheckerInitData(checkClass, fields));
							} catch(ClassNotFoundException e) {
								throw new ConfigException(itsConfigFilename, lineCount, "Sanity check class '" + fullClassName + "' does not exist");
							} catch(Exception e) {
								throw new ConfigException(itsConfigFilename, lineCount, "Error creating Sanity check class", e);
							}
						}
					}
					
					line = reader.readLine();
					lineCount++;
				}
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			throw new ConfigException(itsConfigFilename, "I/O Error while reading file", e);
		}
		itsLogger.info("read SanityCheckConfig configuration file " + itsConfigFilename);
	}
	
	/**
	 * Returns a list containing fresh instances of all the configured sanity checker classes
	 * @return A list containing fresh instances of all the configured sanity checker classes
	 */
	public List<SanityCheck> getCheckers() throws SanityCheckException {
		List<SanityCheck> checkers = new ArrayList<SanityCheck>(itsSanityCheckClasses.size());
		
		try {
			for (CheckerInitData checkerData: itsSanityCheckClasses) {
				SanityCheck checkInstance = (SanityCheck) checkerData.checkerClass.newInstance();
				checkInstance.initialise(checkerData.params);
				checkers.add(checkInstance);
			}
		} catch (Exception e) {
			if (e instanceof SanityCheckException) {
				throw (SanityCheckException) e;
			} else {
				throw new SanityCheckException("Error initialising sanity checker instance", e);
			}
		}
		
		return checkers;
	}
	
	/**
	 * A helper class to hold details of a given sanity checker.
	 * A new instance of each sanity checker is created for each file,
	 * and this class contains the details required to construct it.
	 */
	private class CheckerInitData {
		
		/**
		 * The class of the sanity checker
		 */
		private Class<?> checkerClass;
		
		/**
		 * The parameters for the sanity checker
		 */
		private List<String> params;
		
		/**
		 * Builds an object containing all the details required to initialise
		 * a given sanity checker.
		 * @param checkerClass The class of the sanity checker
		 * @param params The parameters for the sanity checker
		 */
		private CheckerInitData(Class<?> checkerClass, List<String> params) {
			this.checkerClass = checkerClass;
			this.params = params;
		}
	}
}

