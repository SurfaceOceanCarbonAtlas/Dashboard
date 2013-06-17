package socat.sanitychecker.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import socat.sanitychecker.CheckerUtils;
import socat.sanitychecker.data.SocatDataColumn;
import socat.sanitychecker.data.SocatDataRecord;
import socat.sanitychecker.data.calculate.DataCalculator;

/**
 * Holds the SOCAT Column configuration for the Sanity Checker
 */
public class SocatColumnConfig {
	
	/**
	 * The index of the column containing the column name
	 */
	private static final int NAME_COL = 0;
	
	/**
	 * The index of the column that indicates whether this entry is required
	 */
	private static final int REQUIRED_COL = 1;
	
	/**
	 * Indicates the column in which collectively required fields are defined.
	 * (i.e. one of the fields in each group is required).
	 */
	private static final int REQUIRED_GROUP_COL = 2;
	
	/**
	 * The index of the column that indicates the data source
	 */
	private static final int SOURCE_COL = 3;
	
	/**
	 * The index of the column containing the metadata source name
	 */
	private static final int METADATA_COL = 4;
	
	/**
	 * The index of the column containing the name of the column calculation method
	 */
	private static final int CALC_METHOD_COL = 5;
	
	/**
	 * The index of the column indicating whether the entry is numeric
	 */
	private static final int NUMERIC_COL = 6;
	
	/**
	 * The index of the column containing the minimum value of the entry's possible range
	 */
	private static final int RANGE_MIN_COL = 7;
	
	/**
	 * The index of the column containing the maximum value of the entry's possible range
	 */
	private static final int RANGE_MAX_COL = 8;
	
	/**
	 * The index of the column indicating whether or not this field has a flag
	 */
	private static final int FLAG_COL = 9;
	
	/**
	 * The index of the column indicating which flag to assign to missing values
	 */
	private static final int MISSING_FLAG_COL = 10;
	
	/**
	 * The index of the column indicating which flag to assign out-of-range values
	 */
	private static final int RANGE_FLAG_COL = 11;
	
	/**
	 * The name of the package in which classes and methods for calculating data values are defined 
	 */
	private static final String CALCULATOR_PACKAGE = "socat.sanitychecker.data.calculate";
	
	/**
	 * The name of the data calculator method
	 */
	private static final String CALCULATOR_METHOD_NAME = "calculateDataValue";
	
	/**
	 * The name of the interface that all calculator classes must implement
	 */
	private static final String CALCULATOR_INTERFACE_NAME = "socat.sanitychecker.data.calculate.DataCalculator";
	
	/**
	 * The list of SOCAT columns in the order in which they appear in the config file
	 */
	private List<String> itsColumns;
	
	/**
	 * The set of configuration items for the SOCAT columns
	 */
	private Map<String, SocatColumnConfigItem> itsColumnConfig;

	/**
	 * Reads the SOCAT column configuration.
	 * @param configFile The file containing the configuration 
	 * @param logger The logger
	 * @throws IOException If the file cannot be read
	 * @throws ConfigException If the contents of the file are invalid
	 */
	public SocatColumnConfig(String configFile, Logger logger) throws IOException, ConfigException {
		itsColumns = new ArrayList<String>();
		itsColumnConfig = new HashMap<String, SocatColumnConfigItem>();
		readFile(configFile, logger);
	}
	
	/**
	 * Reads and parses the contents of the SOCAT column config file
	 * @param configFile The config file
	 * @param logger The system logger
	 * @throws IOException If the file cannot be read
	 * @throws ConfigException If the configuration is invalid
	 */
	private void readFile(String configFile, Logger logger) throws IOException, ConfigException {
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		String line = reader.readLine();
		int lineCount = 1;
		
		while (null != line) {
			if (!CheckerUtils.isComment(line)) {
				List<String> fields = Arrays.asList(line.split(","));
				fields = CheckerUtils.trimList(fields);
				
				String columnName = fields.get(NAME_COL);
				logger.trace("Creating SOCAT column config item '" + columnName + "'");
				
				if (itsColumns.contains(columnName)) {
					throw new ConfigException(configFile, columnName, lineCount, "Item is configured more than once");
				}
				
				boolean required;
				try {
					required = CheckerUtils.parseBoolean(fields.get(REQUIRED_COL));
				} catch (ParseException e) {
					throw new ConfigException(configFile, columnName, lineCount, "Invalid boolean value");
				}
				
				String requiredGroup = null;
				if (required) {
					requiredGroup = fields.get(REQUIRED_GROUP_COL);
				}
				
				int dataSource = SocatColumnConfigItem.convertDataSourceString(configFile, columnName, lineCount, fields.get(SOURCE_COL));
				
				String metadataName = null;
				if (dataSource == SocatColumnConfigItem.METADATA_SOURCE) {
					metadataName = fields.get(METADATA_COL);
				}
				
				String calcClassName = null;
				DataCalculator calculatorObject = null;
				Method calculatorMethod = null;
				if (dataSource == SocatColumnConfigItem.CALCULATION_SOURCE) {
					try {
						calcClassName = fields.get(CALC_METHOD_COL);
						
						// Check that the class and method exist.
						Class<?> calcClass = Class.forName(CALCULATOR_PACKAGE + "." + calcClassName);
						List<Class<?>> interfaces = Arrays.asList(calcClass.getInterfaces());
						Class<?> interfaceClass = Class.forName(CALCULATOR_INTERFACE_NAME);
						if (!interfaces.contains(interfaceClass)) {
							throw new ConfigException(configFile, columnName, lineCount, "Specified calculator method does not implement the correct interface");
						}
						
						calculatorObject = (DataCalculator) calcClass.newInstance();
						calculatorMethod = calculatorObject.getClass().getDeclaredMethod(CALCULATOR_METHOD_NAME, HashMap.class, SocatDataRecord.class, int.class, String.class);
					}
					catch (ClassNotFoundException e) {
						throw new ConfigException(configFile, columnName, lineCount, "The specified calculator class does not exist");
					}
					catch (NoSuchMethodException e) {
						throw new ConfigException(configFile, columnName, lineCount, "The specified calculator class does not contain the required method");
					}
					catch (ConfigException e) {
						throw e;
					}
					catch (Exception e) {
						throw new ConfigException(configFile, columnName, lineCount, "Unhandled exception while checking calculator method", e);
					}
				}
				
				boolean isNumeric;
				try {
					isNumeric = CheckerUtils.parseBoolean(fields.get(NUMERIC_COL));
				} catch (ParseException e) {
					throw new ConfigException(configFile, columnName, lineCount, "Invalid boolean value");
				}
				
				boolean hasRange = false;
				double rangeMin = 0;
				double rangeMax = 0;
				
				if (fields.get(RANGE_MIN_COL).length() > 0 || fields.get(RANGE_MAX_COL).length() > 0) {
					hasRange = true;
					try {
						rangeMin = Double.parseDouble(fields.get(RANGE_MIN_COL));
						rangeMax = Double.parseDouble(fields.get(RANGE_MAX_COL));
					} catch (NumberFormatException e) {
						throw new ConfigException(configFile, columnName, lineCount, "Invalid range specification");
					}
				}
				
				int flagType = SocatColumnConfigItem.convertFlagTypeString(configFile, columnName, lineCount, fields.get(FLAG_COL));
				
				int missingFlag = SocatColumnConfigItem.GOOD_FLAG;
				int rangeFlag = SocatColumnConfigItem.GOOD_FLAG;
				if (flagType != SocatColumnConfigItem.NO_FLAG) {
					
					if (required) {
						if (fields.size() < MISSING_FLAG_COL) {
							throw new ConfigException(configFile, columnName, lineCount, "Missing flag values");
						} else {
							missingFlag = SocatColumnConfigItem.convertFlagValueString(configFile, columnName, lineCount, fields.get(MISSING_FLAG_COL));
						}
					}
					
					if (hasRange) {
						if (fields.size() <= RANGE_FLAG_COL) {
							throw new ConfigException(configFile, columnName, lineCount, "Range is specified, but no range flag is set");
						} else {
							rangeFlag = SocatColumnConfigItem.convertFlagValueString(configFile, columnName, lineCount, fields.get(RANGE_FLAG_COL));
						}
					}
				}
				
				SocatColumnConfigItem configItem = new SocatColumnConfigItem(columnName, lineCount, required, requiredGroup, dataSource, metadataName, calculatorObject, calculatorMethod, isNumeric, hasRange, rangeMin, rangeMax, flagType, missingFlag, rangeFlag);
				itsColumns.add(columnName);
				itsColumnConfig.put(columnName, configItem);
				
			}
			
			line = reader.readLine();
			lineCount++;
		}
	}
	
	/**
	 * Construct a set of empty SocatDataField objects ready to be populated
	 * @return A set of empty SocatDataField objects corresponding to this configuration
	 */
	public Map<String, SocatDataColumn> buildDataFields() {
		Map<String, SocatDataColumn> dataFields = new HashMap<String, SocatDataColumn>(itsColumns.size());
		
		for (String fieldName : itsColumns) {
			dataFields.put(fieldName, new SocatDataColumn(itsColumnConfig.get(fieldName)));
		}
		
		return dataFields;
	}
	
	/**
	 * Returns a list of the configured SOCAT data field names in file order.
	 * @return The list of SOCAT data field names
	 */
	public List<String> getColumnList() {
		return itsColumns;
	}
	
	/**
	 * Returns the column configuration for the specified column
	 * @param columnName The name of the column
	 * @return The column configuration for the specified column
	 */
	public SocatColumnConfigItem getColumnConfig(String columnName) {
		return itsColumnConfig.get(columnName);
	}
}
