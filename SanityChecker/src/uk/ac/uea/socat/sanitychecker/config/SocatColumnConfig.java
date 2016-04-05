package uk.ac.uea.socat.sanitychecker.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.exeter.QCRoutines.config.ColumnConfig;
import uk.ac.exeter.QCRoutines.config.ColumnConfigItem;
import uk.ac.exeter.QCRoutines.config.ConfigException;
import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.InvalidFlagException;
import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;

/**
 * Holds the SOCAT Column configuration for the Sanity Checker
 */
public class SocatColumnConfig extends ColumnConfig {
	
	/**
	 * The column name for the year
	 */
	public static final String YEAR_COLUMN_NAME = "yr";
	
	/**
	 * The column name for the month
	 */
	public static final String MONTH_COLUMN_NAME = "mon";
	
	/**
	 * The column name for the day
	 */
	public static final String DAY_COLUMN_NAME = "day";
	
	/**
	 * The column name for the hour
	 */
	public static final String HOUR_COLUMN_NAME = "hh";
	
	/**
	 * The column name for the minute
	 */
	public static final String MINUTE_COLUMN_NAME = "mm";
	
	/**
	 * The column name for the second
	 */
	public static final String SECOND_COLUMN_NAME = "ss";

	/**
	 * The column name for the ISO date
	 */
	public static final String ISO_DATE_COLUMN_NAME = "iso_date";
	
	/**
	 * The column name for the latitude
	 */
	public static final String LATITUDE_COLUMN_NAME = "latitude";
	
	/**
	 * The column name for the longitude
	 */
	public static final String LONGITUDE_COLUMN_NAME = "longitude";
	

	/**
	 * The list of date column headers. These columns will always return {@code false}
	 * to the {@link uk.ac.uea.socat.sanitychecker.data.ColumnSpec#isRequired(String)} function.
	 */
	public static final String DATE_COLUMN_NAMES[] = {"yr", "mon", "day", "hh", "mm", "ss", "iso_date"};
	
	/**
	 * Indicates the column in which collectively required fields are defined.
	 * (i.e. one of the fields in each group is required).
	 */
	private static final int COL_REQUIRED_GROUP = 4;
	
	/**
	 * The index of the column that indicates the data source
	 */
	private static final int COL_DATA_SOURCE = 5;
	
	/**
	 * The index of the column containing the metadata source name
	 */
	private static final int COL_METADATA_NAME = 6;
	
	/**
	 * The index of the column containing the name of the column calculation method
	 */
	private static final int COL_CALC_CLASS = 7;
	
	/**
	 * The index of the column indicating whether the entry is numeric
	 */
	private static final int COL_MISSING_FLAG = 8;
	
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
	 * The singleton instance of this class.
	 */
	private static SocatColumnConfig instance = null;
	
	/**
	 * The set of configuration items for the columns
	 */
	private Map<String, SocatColumnConfigItem> columnConfig;

	/**
	 * Set the required data for building the singleton instance of this class
	 * 
	 * @param filename The name of the file containing the configuration
	 * @param logger The logger to be used
	 */
	public static void init(String filename) {
		configFilename = filename;
	}

	/**
	 * Initialises the column configuration config.
	 * This cannot be called until after {@link SocatColumnConfig#init(String, Logger)} has been called.
	 * @throws ConfigException If the configuration cannot be loaded
	 */
	private SocatColumnConfig() throws ConfigException {
		super();
	}
	
	/**
	 * Retrieve the singleton instance of this class, creating it if
	 * it doesn't exist.
	 * 
	 * @return The singleton instance of this class
	 * @throws ConfigException If the configuration is invalid
	 */
	public static SocatColumnConfig getInstance() throws ConfigException {
		if (instance == null) {
			instance = new SocatColumnConfig();
		}
		
		return instance;
	}
	
	/**
	 * Destroys the singleton instance of this class
	 */
	public static void destroy() {
		instance = null;
	}
	
	@Override
	protected ColumnConfigItem createColumnConfigItem(int lineCount, int entryCount) {
		return new SocatColumnConfigItem(lineCount, entryCount);
	}
	
	@Override
	protected void parseLine(int lineCount, List<String> fields, ColumnConfigItem columnConfigItem) throws ConfigException {

		super.parseLine(lineCount, fields, columnConfigItem);
		
		SocatColumnConfigItem item = (SocatColumnConfigItem) columnConfigItem;
		
		String requiredGroup = null;
		if (item.getRequired()) {
			requiredGroup = fields.get(COL_REQUIRED_GROUP);
			item.setRequiredGroup(requiredGroup);
		}

		try {
			item.setDataSource(fields.get(COL_DATA_SOURCE));
		} catch (InvalidDataSourceException e) {
			throw new ConfigException(configFilename, item.getColumnName(), lineCount, "Invalid Data Source", e);
		}
		
		item.setMetadataName(fields.get(COL_METADATA_NAME));
		
		String calcClassName = null;
		if (item.getDataSource() == SocatColumnConfigItem.CALCULATION_SOURCE) {
			try {
				calcClassName = fields.get(COL_CALC_CLASS);

				// Check that the class and method exist.
				Class<?> calcClass = Class.forName(CALCULATOR_PACKAGE + "." + calcClassName);
				List<Class<?>> interfaces = Arrays.asList(calcClass.getInterfaces());
				Class<?> interfaceClass = Class.forName(CALCULATOR_INTERFACE_NAME);
				if (!interfaces.contains(interfaceClass)) {
					throw new ConfigException(configFilename, item.getColumnName(), lineCount, "Specified calculator method does not implement the correct interface");
				}

				DataCalculator calcObject = (DataCalculator) calcClass.newInstance();
				Method calcMethod = item.getCalculatorObject().getClass().getDeclaredMethod(CALCULATOR_METHOD_NAME, OmeMetadata.class, SocatDataRecord.class, int.class, String.class, DateTimeHandler.class);
				item.setCalculatorDetails(calcObject, calcMethod);
			}
			catch (ClassNotFoundException e) {
				throw new ConfigException(configFilename, item.getColumnName(), lineCount, "The specified calculator class does not exist");
			}
			catch (NoSuchMethodException e) {
				throw new ConfigException(configFilename, item.getColumnName(), lineCount, "The specified calculator class does not contain the required method");
			}
			catch (ConfigException e) {
				throw e;
			}
			catch (Exception e) {
				throw new ConfigException(configFilename, item.getColumnName(), lineCount, "Unhandled exception while checking calculator method", e);
			}
		}
		
		if (item.getRequired()) {
			if (fields.size() < COL_MISSING_FLAG) {
				throw new ConfigException(configFilename, item.getColumnName(), lineCount, "Missing Flag value not set");
			} else {
				try {
					item.setMissingFlag(new Flag(Integer.parseInt(fields.get(COL_MISSING_FLAG))));
				} catch (NumberFormatException e) {
					throw new ConfigException(configFilename, item.getColumnName(), lineCount, "Missing Flag value must be numeric", e);
				} catch (InvalidFlagException e) {
					throw new ConfigException(configFilename, item.getColumnName(), lineCount, "Missing Flag value is invalid", e);
				}
			}
		}
	}
	
	@Override
	protected void storeConfigItem(ColumnConfigItem item) {
		columnConfig.put(item.getColumnName(), (SocatColumnConfigItem) item);
	}
 
	public static boolean isDateColumn(String columnName) {
		boolean result = false;
		for (int i = 0; i < DATE_COLUMN_NAMES.length && !result; i++) {
			if (columnName.equalsIgnoreCase(DATE_COLUMN_NAMES[i])) {
				result = true;;
			}
		}

		return result;
	}
}
