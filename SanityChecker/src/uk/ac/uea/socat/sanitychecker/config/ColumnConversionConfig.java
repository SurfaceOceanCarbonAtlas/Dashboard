package uk.ac.uea.socat.sanitychecker.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.data.conversion.Converter;

/**
 * Base utility methods for the Sanity Checker's data conversion functionality.
 * 
 * Calling the {@code get} method with a String column name will retrieve the
 * converter for that column. This isn't specifically coded because
 * {@code HashMap}'s {@code get} method does the work for us.
 */
public class ColumnConversionConfig extends HashMap<String, Converter> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6014268696105078128L;

	/**
	 * The name of the package that contains all the converter classes
	 */
	private static final String CONVERTER_CLASS_PREFIX = "uk.ac.uea.socat.sanitychecker.data.conversion.";
	
	/**
	 * The name of the configuration file.
	 * Must be set via {@link #init(String, Logger)} before calling
	 * {@link #getInstance()}.
	 */
	private static String itsFilename = null;
	
	/**
	 * The logger to be used by the singleton instance of this class
	 */
	private static Logger itsLogger = null;
	
	/**
	 * The singleton instance of this class
	 */
	private static ColumnConversionConfig columnConversionConfigInstance = null;
	
	/**
	 * Set the required data for building the singleton instance of this class
	 * 
	 * @param filename The name of the file containing the configuration
	 * @param logger The logger to be used
	 */
	public static void init(String filename, Logger logger) {
		itsFilename = filename;
		itsLogger = logger;
	}
	
	/**
	 * Create the data conversion configuration
	 * @param conversionConfig The data conversion configuration file
	 * @throws FileNotFoundException If the file doesn't exist.
	 * @throws IOException If an error occurs while reading the file
	 */
	private ColumnConversionConfig() throws SocatConfigException {
		
		if (itsFilename == null) {
			throw new SocatConfigException(null, "ColumnConversionConfig filename has not been set - must run init first");
		}
		
		try {
			FileReader reader = new FileReader(new File(itsFilename));
			try {
				Properties config = new Properties();
				config.load(reader);
				buildMap(config, itsLogger);
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			throw new SocatConfigException(itsFilename, e.getMessage(), e);
		}
		itsLogger.info("read ColumnConversionConfig configuration file " + itsFilename);
	}
	
	/**
	 * Retrieves the singleton instance of this class
	 * 
	 * @return The singleton instance of this class
	 * @throws SocatConfigException
	 */
	public static ColumnConversionConfig getInstance() throws SocatConfigException {
		if (columnConversionConfigInstance == null) {
			columnConversionConfigInstance = new ColumnConversionConfig();
		}
		
		return columnConversionConfigInstance;
	}
	
	/**
	 * Destroy the singleton instance of this class
	 */
	public static void destroy() {
		columnConversionConfigInstance = null;
	}
	
	
	/**
	 * Build the lookup table of data conversion classes for the Sanity Checker
	 * @param config The data conversion configuration
	 * @throws ClassNotFoundException If the specified conversion class does not exist
	 * @throws InstantiationException If an error occurs while instantiating the data conversion class
	 * @throws IllegalAccessException If an error occurs while instantiating the data conversion class
	 */
	private void buildMap(Properties config, Logger logger) throws SocatConfigException {
		Enumeration<?> columns = config.propertyNames();
		while (columns.hasMoreElements()) {
			String column = (String) columns.nextElement();
			String className = config.getProperty(column);
			
			if (null != className && !className.equalsIgnoreCase("NULL") && !className.equalsIgnoreCase("")) {
				try {
					className = CONVERTER_CLASS_PREFIX + className;
					Class<?> converterClass = Class.forName(className);
					put(column, (Converter) converterClass.newInstance());
					
					logger.trace("Added converter for SOCAT column " + column + ": " + className);
				} catch(ClassNotFoundException e) {
					throw new SocatConfigException(itsFilename, "Error processing configuration for column '" + column + "', converter class '" + className + "': Cannot find class " + e.getMessage());
				} catch(Exception e) {
					throw new SocatConfigException(itsFilename, "Error processing configuration for column '" + column + "', converter class '" + className + "': " + e.getMessage());
				}
			} else {
				throw new SocatConfigException(itsFilename, "Invalid configuration for column '" + column + "', converter class '" + className + "'");
			}
		}
	}
}
