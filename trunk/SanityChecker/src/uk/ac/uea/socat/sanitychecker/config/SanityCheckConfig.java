package uk.ac.uea.socat.sanitychecker.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.sanitychecks.SanityCheck;
import uk.ac.uea.socat.sanitychecker.sanitychecks.SanityCheckException;

public class SanityCheckConfig {
	
	private static final String SANITY_CHECK_CLASS_ROOT = "uk.ac.uea.socat.sanitychecker.sanitychecks.";

	private static final String SANITY_CHECK_CLASS_TAIL = "SanityCheck";

	
	private List<CheckerInitData> itsSanityCheckClasses;
	
	private static String itsConfigFilename = null;
	
	private static Logger itsLogger = null;
	
	private static SanityCheckConfig sanityCheckConfigInstance = null;
	
	public SanityCheckConfig() throws ConfigException {
		if (itsConfigFilename == null) {
			throw new ConfigException(null, "SanityCheckConfig filename has not been set - must run init first");
		}
		
		itsSanityCheckClasses = new ArrayList<CheckerInitData>();
		readFile();
	}
	
	public static void init(String filename, Logger logger) {
		itsConfigFilename = filename;
		itsLogger = logger;
	}
	
	public static SanityCheckConfig getInstance() throws ConfigException {
		if (null == sanityCheckConfigInstance) {
			sanityCheckConfigInstance = new SanityCheckConfig();
		}
		
		return sanityCheckConfigInstance;
	}
	
	public static void destroy() {
		sanityCheckConfigInstance = null;
	}
	
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
								// These will be instantiated in the @code{getInstances()} method.
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
	
	private class CheckerInitData {
		
		private Class<?> checkerClass;
		private List<String> params;
		
		private CheckerInitData(Class<?> checkerClass, List<String> params) {
			this.checkerClass = checkerClass;
			this.params = params;
		}
	}
}

