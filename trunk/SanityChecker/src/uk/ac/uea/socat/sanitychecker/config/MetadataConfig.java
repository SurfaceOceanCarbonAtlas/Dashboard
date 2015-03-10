package uk.ac.uea.socat.sanitychecker.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.metadata.MetadataItem;

/**
 * Holds the metadata configuration information for the Sanity Checker
 */
public class MetadataConfig {

	/**
	 * The index of the column that contains the metadata name
	 */
	private static final int NAME_COL = 0;
	
	/**
	 * The index of the column that contains the metadata data type
	 */
	private static final int TYPE_COL = 1;
	
	/**
	 * The index of the column that states whether the item is required
	 */
	private static final int REQUIRED_COL = 2;
	
	/**
	 * The index of the column that defines the mutual group of required values that
	 * pertains to the item
	 */
	private static final int REQUIRED_GROUP_COL = 3;
	
	/**
	 * The index of the column that states whether the Sanity Checker should generate the metadata value
	 */
	private static final int GENERATE_COL = 4;
	
	/**
	 * The index of the column that contains the metadata item's minimum value
	 */
	private static final int MIN_COL = 5;
	
	/**
	 * The index of the column that contains the metadata item's maximum value
	 */
	private static final int MAX_COL = 6;
	
	/**
	 * The index of the column that contains name of the class to use for the metadata item 
	 */
	private static final int ITEM_CLASS_COL = 7;
	
	/**
	 * The index of the column that contains the parameter to be passed to the generator function
	 */
	private static final int GENERATOR_PARAM_COL = 8;
	
	/**
	 * The root of all metadata item class names
	 */
	private static final String ITEM_CLASS_ROOT = "uk.ac.uea.socat.sanitychecker.metadata";
	
	/**
	 * The default metadata item class to use if it's not specified
	 * in the config file. Note that the value of {@code ITEM_CLASS_TAIL}
	 * will be appended to this.
	 */
	private static final String DEFAULT_ITEM_CLASS = "Default";
	
	/**
	 * All metadata item class names end with this string.
	 * In the case of the default item class, this is the full name. 
	 */
	private static final String ITEM_CLASS_TAIL = "MetadataItem";
	
	/**
	 * Holds the configuration details for each metadata item
	 */
	private HashMap<String, MetadataConfigItem> itsConfigItems;
	
	/**
	 * Holds the list of metadata entry names which act as a group where
	 * at least one of the group must be specified
	 */
	private MetadataConfigRequiredGroups itsRequiredGroups;
	
	
	/**
	 * The location of the metadata config file.
	 * Must be set via {@link #init(String, Logger)} before calling
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
	private static MetadataConfig metadataConfigInstance = null;

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
	 * Reads the metadata configuration from the specified file.
	 * @param filename The name of the file where the configuration is stored
	 * @throws IOException If the file cannot be accessed
	 */
	private MetadataConfig() throws ConfigException {
		
		if (itsConfigFilename == null) {
			throw new ConfigException(null, "Config filename has not been set - must run init() first");
		}
		
		itsConfigItems = new HashMap<String, MetadataConfigItem>(200);
		itsRequiredGroups = new MetadataConfigRequiredGroups();
		readFile();
	}
	
	/**
	 * Retrieves the singleton instance of this class, creating it
	 * if necessary.
	 * 
	 * @return The singleton instance of this class
	 */
	public static MetadataConfig getInstance() throws ConfigException {
		if (metadataConfigInstance == null) {
			metadataConfigInstance = new MetadataConfig();
		}
		
		return metadataConfigInstance;
	}
	
	/**
	 * Destroy the singleton instance of this class
	 */
	public static void destroy() {
		metadataConfigInstance = null;
	}
	
	/**
	 * Read and process the configuration file.
	 * 
	 * @param filename The name of the config file
	 * @param logger The logger to which message should be sent
	 * @throws IOException If an error occurs in accessing the file
	 * @throws ConfigException If an error occurs while processing the config data
	 */
	private void readFile() throws ConfigException {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(itsConfigFilename));
			try {
				String line = reader.readLine().trim();
				int lineCount = 1;

				while (null != line) {
					if (!CheckerUtils.isComment(line)) {
						List<String> fields = Arrays.asList(line.split(","));

						String name = fields.get(NAME_COL).toLowerCase();

						if (contains(name)) {
							throw new ConfigException(itsConfigFilename, name, lineCount, "Item is configured more than once");
						}

						try {

							// Extract all values from the config line
							// All the simple ones are at the top!
							boolean itemRequired = CheckerUtils.parseBoolean(fields.get(REQUIRED_COL));
							boolean mustGenerate = CheckerUtils.parseBoolean(fields.get(GENERATE_COL));

							// Get the class of the final metadata item
							Class<? extends MetadataItem> itemClass = null;
							String fullClassName = null;
							try {
								String itemClassName = fields.get(ITEM_CLASS_COL);
								if (null == itemClassName) {
									itemClassName = DEFAULT_ITEM_CLASS;
								}

								fullClassName = ITEM_CLASS_ROOT + "." + itemClassName + ITEM_CLASS_TAIL;
								Class <?> c = Class.forName(fullClassName);
								itemClass = c.asSubclass(MetadataItem.class);
							} catch (ClassNotFoundException e) {
								throw new Exception("Cannot find metadata item class " + fullClassName);
							} catch (ClassCastException e) {
								throw new Exception("Class '" + fullClassName + "' is not a subclass of MetadataItem");
							}

							/*
							 * Get the generator parameter if it's present.
							 * 
							 * Java's string splitter isn't very good with an empty last column.
							 * Therefore we must explicitly check whether or not the column exists
							 * before we try to access it.
							 */
							String generatorParameter = null;
							if (fields.size() > GENERATOR_PARAM_COL) {
								generatorParameter = fields.get(GENERATOR_PARAM_COL);
							}

							// Now we have all the values, construct an object for them
							MetadataConfigItem configItem = new MetadataConfigItem(name, 
									itemRequired, mustGenerate, itemClass, generatorParameter, itsLogger);

							// And add it to the configuration list
							itsConfigItems.put(name, configItem);

						} catch (Exception e) {
							itsLogger.error("Error processing Metadata config", e);
							throw new ConfigException(itsConfigFilename, name, lineCount, e.getMessage());
						}
					}

					// Read the next line
					line = reader.readLine();
					lineCount++;
				}
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			throw new ConfigException(itsConfigFilename, "I/O Error while reading from file", e);
		}
		itsLogger.info("read MetadataConfig configuration file " + itsConfigFilename);
	}
	
	/**
	 * Determines whether or not the specified metadata name is present in the configuration.
	 * @param metadataName The metadata name
	 * @return {@code true} if the name exists in the configuration; {@code false} otherwise
	 */
	public boolean contains(String metadataName) {
		return itsConfigItems.containsKey(metadataName.toLowerCase());
	}
	
	/**
	 * Retrieve the configuration for the specified metadata item.
	 * @param metadataName The name of the metadata item
	 * @return The configuration for the metadata item.
	 */
	public MetadataConfigItem get(String metadataName) {
		return itsConfigItems.get(metadataName.toLowerCase());
	}
	
	/**
	 * Returns an iterator for all the configured metadata item names
	 * @return The iterator
	 */
	public Set<String> getConfigItemNames() {
		return itsConfigItems.keySet();
	}
	
	/**
	 * Returns the set of Required Groups for this metadata configuration
	 * @return The set of Required Groups
	 */
	public MetadataConfigRequiredGroups getRequiredGroups() {
		return itsRequiredGroups;
	}
}
