package uk.ac.uea.socat.sanitychecker.config;

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

import org.apache.logging.log4j.Logger;

import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;

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
	private static final int QUESTIONABLE_RANGE_MIN_COL = 7;
	
	/**
	 * The index of the column containing the maximum value of the entry's possible range
	 */
	private static final int QUESTIONABLE_RANGE_MAX_COL = 8;
	
	/**
	 * The index of the column containing the minimum value of the entry's possible range
	 */
	private static final int BAD_RANGE_MIN_COL = 9;
	
	/**
	 * The index of the column containing the maximum value of the entry's possible range
	 */
	private static final int BAD_RANGE_MAX_COL = 10;
	
	/**
	 * The index of the column indicating whether or not this field has a flag
	 */
	private static final int FLAG_COL = 11;
	
	/**
	 * The index of the column indicating which flag to assign to missing values
	 */
	private static final int MISSING_FLAG_COL = 12;
	
	/**
	 * The name of the package in which classes and methods for calculating data values are defined 
	 */
	private static final String CALCULATOR_PACKAGE = "uk.ac.uea.socat.sanitychecker.data.calculate";
	
	/**
	 * The name of the data calculator method
	 */
	private static final String CALCULATOR_METHOD_NAME = "calculateDataValue";
	
	/**
	 * The name of the interface that all calculator classes must implement
	 */
	private static final String CALCULATOR_INTERFACE_NAME = "uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator";
	
	/**
	 * The list of SOCAT columns in the order in which they appear in the config file
	 */
	private List<String> itsColumns;
	
	/**
	 * The set of configuration items for the SOCAT columns
	 */
	private Map<String, SocatColumnConfigItem> itsColumnConfig;
	
	/**
	 * The location of the metadata config file.
	 * Must be set via {@link #init(String, Logger) before calling
	 * {@link #getInstance()}.
	 */
	private static String itsConfigFilename = null;

	/**
	 * The logger to be used by the config object.
	 */
	private static Logger itsLogger = null; 
	
	/**
	 * The singleton instance of this class.
	 */
	private static SocatColumnConfig socatColumnConfigInstance = null;

	/**
	 * Set the required data for building the singleton instance of this class
	 * 
	 * @param filename The name of the file containing the configuration
	 * @param logger The logger to be used
	 */
	public static void init(String filename, Logger logger) {
		itsConfigFilename = filename;
		itsLogger = logger;
	}

	/**
	 * Initialises the column configuration config.
	 * This cannot be called until after {@link SocatColumnConfig#init(String, Logger)} has been called.
	 * @throws ConfigException If the configuration cannot be loaded
	 */
	private SocatColumnConfig() throws ConfigException {
		if (itsConfigFilename == null) {
			throw new ConfigException(null, "SocatColumnConfig filename has not been set - must run init first");
		}
		
		itsColumns = new ArrayList<String>();
		itsColumnConfig = new HashMap<String, SocatColumnConfigItem>();
		readFile();
	}
	
	/**
	 * Retrieve the singleton instance of this class, creating it if
	 * it doesn't exist.
	 * 
	 * @return The singleton instance of this class
	 * @throws ConfigException If the configuration is invalid
	 */
	public static SocatColumnConfig getInstance() throws ConfigException {
		if (socatColumnConfigInstance == null) {
			socatColumnConfigInstance = new SocatColumnConfig();
		}
		
		return socatColumnConfigInstance;
	}
	
	/**
	 * Destroys the singleton instance of this class
	 */
	public static void destroy() {
		socatColumnConfigInstance = null;
	}
	
	/**
	 * Reads and parses the contents of the SOCAT column config file
	 * @param configFile The config file
	 * @param logger The system logger
	 * @throws IOException If the file cannot be read
	 * @throws ConfigException If the configuration is invalid
	 */
	private void readFile() throws ConfigException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(itsConfigFilename));
			try {
				String line = reader.readLine();
				int lineCount = 1;
				int entryCount = 0;				
				
				while (null != line) {
					if (!CheckerUtils.isComment(line)) {
						entryCount++;
						List<String> fields = Arrays.asList(line.split(","));
						fields = CheckerUtils.trimList(fields);

						String columnName = fields.get(NAME_COL);
						itsLogger.trace("Creating SOCAT column config item '" + columnName + "'");

						if (itsColumns.contains(columnName)) {
							throw new ConfigException(itsConfigFilename, columnName, lineCount, "Item is configured more than once");
						}

						boolean required;
						try {
							required = CheckerUtils.parseBoolean(fields.get(REQUIRED_COL));
						} catch (ParseException e) {
							throw new ConfigException(itsConfigFilename, columnName, lineCount, "Invalid boolean value");
						}

						String requiredGroup = null;
						if (required) {
							requiredGroup = fields.get(REQUIRED_GROUP_COL);
						}

						int dataSource = SocatColumnConfigItem.convertDataSourceString(itsConfigFilename, columnName, lineCount, fields.get(SOURCE_COL));

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
									throw new ConfigException(itsConfigFilename, columnName, lineCount, "Specified calculator method does not implement the correct interface");
								}

								calculatorObject = (DataCalculator) calcClass.newInstance();
								calculatorMethod = calculatorObject.getClass().getDeclaredMethod(CALCULATOR_METHOD_NAME, OmeMetadata.class, SocatDataRecord.class, int.class, String.class, DateTimeHandler.class);
							}
							catch (ClassNotFoundException e) {
								throw new ConfigException(itsConfigFilename, columnName, lineCount, "The specified calculator class does not exist");
							}
							catch (NoSuchMethodException e) {
								throw new ConfigException(itsConfigFilename, columnName, lineCount, "The specified calculator class does not contain the required method");
							}
							catch (ConfigException e) {
								throw e;
							}
							catch (Exception e) {
								throw new ConfigException(itsConfigFilename, columnName, lineCount, "Unhandled exception while checking calculator method", e);
							}
						}

						boolean isNumeric;
						try {
							isNumeric = CheckerUtils.parseBoolean(fields.get(NUMERIC_COL));
						} catch (ParseException e) {
							throw new ConfigException(itsConfigFilename, columnName, lineCount, "Invalid boolean value");
						}

						
						
						
						
						boolean hasQuestionableRange = false;
						boolean hasBadRange = false;
						double questionableRangeMin = 0;
						double questionableRangeMax = 0;
						double badRangeMin = 0;
						double badRangeMax = 0;

						if (fields.get(QUESTIONABLE_RANGE_MIN_COL).length() > 0 || fields.get(QUESTIONABLE_RANGE_MAX_COL).length() > 0) {
							hasQuestionableRange = true;
							try {
								questionableRangeMin = Double.parseDouble(fields.get(QUESTIONABLE_RANGE_MIN_COL));
								questionableRangeMax = Double.parseDouble(fields.get(QUESTIONABLE_RANGE_MAX_COL));
							} catch (NumberFormatException e) {
								throw new ConfigException(itsConfigFilename, columnName, lineCount, "Invalid range specification");
							}
						}
						
						
						if (fields.get(BAD_RANGE_MIN_COL).length() > 0 || fields.get(BAD_RANGE_MAX_COL).length() > 0) {
							hasBadRange = true;
							try {
								badRangeMin = Double.parseDouble(fields.get(BAD_RANGE_MIN_COL));
								badRangeMax = Double.parseDouble(fields.get(BAD_RANGE_MAX_COL));
							} catch (NumberFormatException e) {
								throw new ConfigException(itsConfigFilename, columnName, lineCount, "Invalid range specification");
							}
						}
						
						if (hasQuestionableRange && hasBadRange) {
							if (badRangeMin > questionableRangeMin || badRangeMax < questionableRangeMax) {
								throw new ConfigException(itsConfigFilename, columnName, lineCount, "Bad range must be larger than questionable range");
							}
						}

						int flagType = SocatColumnConfigItem.convertFlagTypeString(itsConfigFilename, columnName, lineCount, fields.get(FLAG_COL));

						int missingFlag = SocatColumnConfigItem.GOOD_FLAG;
						if (flagType != SocatColumnConfigItem.NO_FLAG) {
							if (required) {
								if (fields.size() < MISSING_FLAG_COL) {
									throw new ConfigException(itsConfigFilename, columnName, lineCount, "Missing flag values");
								} else {
									missingFlag = SocatColumnConfigItem.convertFlagValueString(itsConfigFilename, columnName, lineCount, fields.get(MISSING_FLAG_COL));
								}
							}
						}

						SocatColumnConfigItem configItem = new SocatColumnConfigItem(columnName, entryCount, required, requiredGroup, dataSource, metadataName, calculatorObject, calculatorMethod, isNumeric, hasQuestionableRange, questionableRangeMin, questionableRangeMax, hasBadRange, badRangeMin, badRangeMax, flagType, missingFlag);
						itsColumns.add(columnName);
						itsColumnConfig.put(columnName, configItem);
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
		itsLogger.info("read SocatColumnConfig configuration file " + itsConfigFilename);

	}
	
	/**
	 * Construct a set of empty SocatDataField objects ready to be populated
	 * @return A set of empty SocatDataField objects corresponding to this configuration
	 */
	public Map<String, SocatDataColumn> buildDataFields(ColumnSpec colSpec) {
		Map<String, SocatDataColumn> dataFields = new HashMap<String, SocatDataColumn>(itsColumns.size());
		
		for (String fieldName : itsColumns) {
			dataFields.put(fieldName, new SocatDataColumn(itsColumnConfig.get(fieldName), colSpec.getColumnInfo(fieldName)));
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
	
	/**
	 * Retrieves the column name for a given column index in the data file
	 * @param columnIndex The index of the column
	 * @return The name of the column
	 */
	public String getColumnName(int columnIndex) {
		
		String columnName = null;
		
		for (String searchColumn : itsColumnConfig.keySet()) {
			
			SocatColumnConfigItem columnConfig = itsColumnConfig.get(searchColumn);
			if (columnConfig.getIndex() == columnIndex) {
				columnName = searchColumn;
				break;
			}
		}
		
		return columnName;
	}
}
