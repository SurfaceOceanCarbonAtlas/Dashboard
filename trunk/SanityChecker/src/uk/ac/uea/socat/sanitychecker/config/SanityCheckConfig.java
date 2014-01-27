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

public class SanityCheckConfig {
	
	private static final String SANITY_CHECK_CLASS_ROOT = "uk.ac.uea.socat.sanitychecker.sanitychecks.";

	private static final String SANITY_CHECK_CLASS_TAIL = "SanityCheck";

	
	private List<SanityCheck> itsSanityChecks;
	
	private static String itsConfigFilename = null;
	
	private static Logger itsLogger = null;
	
	private static SanityCheckConfig sanityCheckConfigInstance = null;
	
	
	public SanityCheckConfig() throws ConfigException {
		if (itsConfigFilename == null) {
			throw new ConfigException(null, "SanityCheckConfig filename has not been set - must run init first");
		}
		
		itsSanityChecks = new ArrayList<SanityCheck>();
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
								itsLogger.trace("Initialising Sanity Check class " + fullClassName);
								Class<?> checkClass = Class.forName(fullClassName);
								SanityCheck checkInstance = (SanityCheck) checkClass.newInstance();
								checkInstance.initialise(fields);
								itsSanityChecks.add(checkInstance);
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
	}
	
	public List<SanityCheck> getCheckers() {
		return itsSanityChecks;
	}
}
