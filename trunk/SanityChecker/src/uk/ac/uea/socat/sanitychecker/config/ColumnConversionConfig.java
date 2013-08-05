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
	 * The name of the configuration file
	 */
	private String itsFilename;
	
	/**
	 * Create the data conversion configuration
	 * @param conversionConfig The data conversion configuration file
	 * @throws FileNotFoundException If the file doesn't exist.
	 * @throws IOException If an error occurs while reading the file
	 */
	public ColumnConversionConfig(File conversionConfig, Logger logger) throws ConfigException {
		itsFilename = conversionConfig.getName();
		try {
			Properties config = new Properties();
			config.load(new FileReader(conversionConfig));
			buildMap(config, logger);
		} catch (IOException e) {
			throw new ConfigException(conversionConfig.getName(), e.getMessage(), e);
		}
	}
	
	/**
	 * Build the lookup table of data conversion classes for the Sanity Checker
	 * @param config The data conversion configuration
	 * @throws ClassNotFoundException If the specified conversion class does not exist
	 * @throws InstantiationException If an error occurs while instantiating the data conversion class
	 * @throws IllegalAccessException If an error occurs while instantiating the data conversion class
	 */
	private void buildMap(Properties config, Logger logger) throws ConfigException {
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
					throw new ConfigException(itsFilename, "Error processing configuration for column '" + column + "', converter class '" + className + "': Cannot find class " + e.getMessage());
				} catch(Exception e) {
					throw new ConfigException(itsFilename, "Error processing configuration for column '" + column + "', converter class '" + className + "': " + e.getMessage());
				}
			} else {
				throw new ConfigException(itsFilename, "Invalid configuration for column '" + column + "', converter class '" + className + "'");
			}
		}
	}
}
