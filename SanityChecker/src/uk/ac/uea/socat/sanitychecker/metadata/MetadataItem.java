package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;

import org.apache.logging.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.SanityCheckerException;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;

/**
 * An object representing a metadata entry for a data file
 */
public abstract class MetadataItem {
	
	/**
	 * The configuration object corresponding to this metadata item
	 */
	protected MetadataConfigItem itsConfigItem;
	
	/**
	 * A logger
	 */
	protected Logger itsLogger;
	
	/**
	 * The metadata item can be a boolean, an integer or a string. It is stored
	 * as a MetadataItemValue for simplifying the data structures.
	 */
	private String itsValue;
	
	/**
	 * Stores the line number of the file on which this metadata item was found
	 */
	private int itsLine;
	
	/**
	 * Flag to indicate whether or not this metadata item can
	 * be generated from the file data
	 */
	protected boolean itCanGenerate;
	
	/**
	 * Flag to indicate whether or not this metadata item can
	 * be generated from a single record
	 */
	protected boolean itCanGenerateFromOneRecord;
		
	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param value The value of the metadata item
	 * @param line The line number of the file on which this item occurs. If the item has come from the command line, pass {@code -1}.
	 * @oaram messages the set of output message
	 * @param logger The system logger
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public MetadataItem(MetadataConfigItem config, int line, Logger logger) {
		itsConfigItem = config;
		itsLine = line;
		itsValue = null;
		itsLogger = logger;
	}
	
	/**
	 * Sets the value for this metadata item. The value is passed in as a String, and converted to
	 * the appropriate format for the item for storage.
	 * @param value The value to be stored
	 * @throws DateTimeException If a passed in value for a date item cannot be parsed
	 */
	public void setValue(String value) throws SanityCheckerException {
		itsLogger.trace("Setting metadata value '" + itsConfigItem.getName() + "' to '" + value + "'");
		itsValue = value;
	}
		
	/**
	 * Returns the value of this metadata item as a String, regardless of its actual type.
	 * @return The value of the metadata item
	 */
	public String getValue() {
		String result = null;
		if (null != itsValue) {
			result = itsValue;
		}
		
		return result;
	}
	
	/**
	 * Generates the value for this metadata item from the data file and/or system data
	 */
	public abstract void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException, SanityCheckerException;
	
	/**
	 * Processes a single data record to extract information for generating this metadata value.
	 * @param metadataSet The currently extracted metadata
	 * @param record The data record
	 */
	public abstract void processRecordForValue(SocatDataRecord record) throws MetadataException;
	
	/**
	 * Indicates whether or not this metadata item can be generated from the file data
	 * @return {@code true} if the item can be generated; {@code false} if it cannot
	 */
	public boolean canGenerate() {
		return itCanGenerate;
	}
	
	/**
	 * Indicates whether or not this metadata item can be generated from a single data record
	 * @return {@code true} if the item can be generated from a single data record; {@code false} if all records are required.
	 */
	public boolean canGenerateFromOneRecord() {
		return itCanGenerateFromOneRecord;
	}
	
	/**
	 * Returns the name of this metadata item.
	 * @return The name of this metadata item.
	 */
	public String getName() {
		return itsConfigItem.getName();
	}
	
	public int getLine() {
		return itsLine;
	}
}

