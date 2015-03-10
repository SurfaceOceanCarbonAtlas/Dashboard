package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import uk.ac.uea.socat.sanitychecker.SanityCheckerException;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * An object representing a metadata entry for a data file
 */
public abstract class MetadataItem {
	
	private static final String METADATA_RANGE_ID = "METADATA_RANGE";
	
	private static MessageType itsMetadataRangeType;
	
	/**
	 * Indicates that the metadata item is a String
	 */
	public static final int STRING_TYPE = 1;
	
	/**
	 * Indicates that the metadata item is a boolean
	 */
	public static final int BOOLEAN_TYPE = 2;
	
	/**
	 * Indicates that the metadata item is an integer
	 */
	public static final int INTEGER_TYPE = 3;
	
	/**
	 * Indicates that the metadata item is a Real
	 */
	public static final int REAL_TYPE = 4;
	
	/**
	 * Indicates that the metadata item is a Date
	 */
	public static final int DATE_TYPE = 5;
	
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
	private MetadataValue itsValue;
	
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
		
		if (null == itsMetadataRangeType) {
			itsMetadataRangeType = new MessageType(METADATA_RANGE_ID, "Metadata item/value '" + MessageType.FIELD_VALUE_IDENTIFIER + "' should be in the range " + MessageType.VALID_VALUE_IDENTIFIER, "Metadata value out of range");
		}
	}
	
	/**
	 * Sets a boolean value for a metadata item
	 * @param value The value to be set
	 * @throws MetadataException If this metadata item does not store a boolean
	 */
	public void setValue(boolean value) throws MetadataException {
		if (!(itsConfigItem.getType() == BOOLEAN_TYPE)) {
			throw new MetadataException("Attempt to set a boolean value for a non-boolean metadata item");
		} else {
			itsValue = new MetadataValue(value);
			itsLogger.trace("Setting metadata value '" + itsConfigItem.getName() + "' to '" + value + "'");
		}
	}
	
	/**
	 * Sets a date value for a metadata item
	 * @param value The value to be set
	 * @throws MetadataException If this metadata item does not store a date
	 */
	public void setValue(DateTime value) throws MetadataException {
		if (!(itsConfigItem.getType() == DATE_TYPE)) {
			throw new MetadataException("Attempt to set a date value for a non-date metadata item");
		} else {
			itsValue = new MetadataValue(value);
			itsLogger.trace("Setting metadata value '" + itsConfigItem.getName() + "' to '" + value + "'");
		}
	}
	
	/**
	 * Sets a long value for a metadata item
	 * @param value The value to be set
	 */
	public void setValue(double value) {
		itsValue = new MetadataValue(value);
		itsLogger.trace("Setting metadata value '" + itsConfigItem.getName() + "' to '" + value + "'");
	}
	
	/**
	 * Sets the value for this metadata item. The value is passed in as a String, and converted to
	 * the appropriate format for the item for storage.
	 * @param value The value to be stored
	 * @throws DateTimeException If a passed in value for a date item cannot be parsed
	 */
	public void setValue(String value, DateTimeHandler dateTimeHandler) throws SanityCheckerException {
		itsLogger.trace("Setting metadata value '" + itsConfigItem.getName() + "' to '" + value + "'");

		switch(itsConfigItem.getType()) {
		case STRING_TYPE:
		{
			itsValue = new MetadataValue(value);
			break;
		}
		case BOOLEAN_TYPE:
		{
			itsValue = new MetadataValue(Boolean.parseBoolean(value));
			break;
		}
		case INTEGER_TYPE:
		{
			itsValue = new MetadataValue(Integer.parseInt(value));
			break;
		}
		case REAL_TYPE:
		{
			itsValue = new MetadataValue(Double.parseDouble(value));
			break;
		}
		case DATE_TYPE:
		{
			try {
				DateTime parsedDate = dateTimeHandler.parseDate(value);
				itsValue = new MetadataValue(parsedDate);
			} catch (DateTimeException e) {
				throw new SanityCheckerException("Invalid date format in metadata item '" + itsConfigItem.getName() + "'", e);
			}
			break;
		}
		default:
		{
			itsValue = new MetadataValue(value);
		}
		}
	}
	
	/**
	 * Validate the supplied metadata value.
	 * @param logger A logger to send messages to.
	 * @return A message for the Sanity Checker's output. If the message is {@code null}, this indicates that validation was successful.
	 */
	public Message validate(Logger logger) {
		Message result = null;
		
		// No validation is performed on strings
		switch (itsConfigItem.getType()) {
		case STRING_TYPE:
		case BOOLEAN_TYPE:
		case DATE_TYPE:
		{
			// No validation can be performed on these.
			// Note that dates will have been validated when the value was first read.
			break;
		}
		case INTEGER_TYPE:
		{
			/*
			 *  Make sure the value is in the valid range, if it's been specified
			 *  in the configuration.
			 */
			if (itsConfigItem.hasRange()) {
				int min = itsConfigItem.getRange().getIntMin();
				int max = itsConfigItem.getRange().getIntMax();
				
				if (itsValue.getInt() >= min && itsValue.getInt() <= max) {
					result = null;
				} else {
					logger.warn("Metadata value '" + itsConfigItem.getName() + "' is out of range");

					StringBuffer itemValueString = new StringBuffer(itsConfigItem.getName());
					itemValueString.append(" (");
					itemValueString.append(itsValue);
					itemValueString.append(")");
					
				    StringBuffer validRangeString = new StringBuffer(itsConfigItem.getRange().getIntMin());
				    validRangeString.append(':');
				    validRangeString.append(itsConfigItem.getRange().getIntMax());

				    result = new Message(Message.NO_COLUMN_INDEX, null, itsMetadataRangeType, Message.WARNING, itsLine, itemValueString.toString(), validRangeString.toString());
				}
			}
			
			break;
		}
		case REAL_TYPE:
		{
			/*
			 *  Make sure the value is in the valid range, if it's been specified
			 *  in the configuration.
			 */
			if (itsConfigItem.hasRange()) {
				double min = itsConfigItem.getRange().getRealMin();
				double max = itsConfigItem.getRange().getRealMax();
				
				if (itsValue.getReal() >= min && itsValue.getReal() <= max) {
					result = null;
				} else {
					logger.warn("Metadata value '" + itsConfigItem.getName() + "' is out of range");

					StringBuffer itemValueString = new StringBuffer(itsConfigItem.getName());
					itemValueString.append(" (");
					itemValueString.append(itsValue);
					itemValueString.append(")");
					
				    StringBuffer validRangeString = new StringBuffer();
				    validRangeString.append(itsConfigItem.getRange().getRealMin());
				    validRangeString.append(':');
				    validRangeString.append(itsConfigItem.getRange().getRealMax());

				    result = new Message(Message.NO_COLUMN_INDEX, null, itsMetadataRangeType, Message.WARNING, Message.NO_LINE_NUMBER, itemValueString.toString(), validRangeString.toString());
				}
			}
			
			break;
		}
		
		}
		
		return result;
	}
	
	/**
	 * Converts a string indicating the data type of a metadata item into
	 * the corresponding data type flag
	 * @param type The data type to be converted
	 * @return The data type flag
	 */
	public static int getTypeFlag(String type) throws MetadataException {
		int result = -1;
		
		if (type.equalsIgnoreCase("string")) {
			result = STRING_TYPE;
		} else if (type.equalsIgnoreCase("date")) {
			result = DATE_TYPE;
		} else if (type.equalsIgnoreCase("boolean")) {
			result = BOOLEAN_TYPE;
		} else if (type.equalsIgnoreCase("integer")) {
			result = INTEGER_TYPE;
		} else if (type.equalsIgnoreCase("real")) {
			result = REAL_TYPE;
		} else {
			throw new MetadataException("Unrecognised data type " + type);
		}
		
		return result;
	}
	
	/**
	 * Returns the value of this metadata item as a String, regardless of its actual type.
	 * @return The value of the metadata item
	 */
	public String getValue(DateTimeHandler dateTimeHandler) throws DateTimeException {
		String result = null;
		if (null != itsValue) {
			result = itsValue.getValue(dateTimeHandler);
		}
		
		return result;
	}
	
	/**
	 * Generates the value for this metadata item from the data file and/or system data
	 */
	public abstract void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException;
	
	/**
	 * Processes a single data record to extract information for generating this metadata value.
	 * @param metadataSet The currently extracted metadata
	 * @param record The data record
	 */
	public abstract void processRecordForValue(SocatDataRecord record) throws MetadataException;
	
	/**
	 * Internal class for holding a metadata value. This handles the fact that
	 * a value might be one of various data types.
	 */
	class MetadataValue {
		
		/**
		 * Contains the value if this is a String
		 */
		private String itsStringValue;
		
		/**
		 * Contains the value if this is a Boolean
		 */
		private boolean itsBooleanValue;
		
		/**
		 * Contains the value if this is an integer
		 */
		private int itsIntValue;
		
		/**
		 * Contains the value if this is a real
		 */
		private double itsRealValue;
		
		/**
		 * Contains the value if this is a date
		 */
		private DateTime itsDateValue;
		
		/**
		 * The data type of the value
		 */
		private int itsType;
		
		/**
		 * Constructor for a string value
		 * @param value The value
		 */
		MetadataValue(String value) {
			itsType = STRING_TYPE;
			itsStringValue = value;
		}
		
		/**
		 * Constructor for a boolean value
		 * @param value The value
		 */
		MetadataValue(boolean value) {
			itsType = BOOLEAN_TYPE;
			itsBooleanValue = value;
		}
		
		/**
		 * Constructor for an integer value
		 * @param value The value
		 */
		MetadataValue(int value) {
			itsType = INTEGER_TYPE;
			itsIntValue = value;
		}
		
		/**
		 * Constructor for a real value
		 * @param value The value
		 */
		MetadataValue(double value) {
			itsType = REAL_TYPE;
			itsRealValue = value;
		}
		
		/**
		 * Constructor for a date value
		 * @param value The value
		 */
		MetadataValue(DateTime value) {
			itsType = DATE_TYPE;
			itsDateValue = value;
		}
		
		/**
		 * Returns a string representation of the value, regardless of its actual
		 * data type.
		 * @return A string representation of the value
		 */
		String getValue(DateTimeHandler dateTimeHandler) throws DateTimeException {
			String result = null;
			
			switch (itsType) {
			case MetadataItem.BOOLEAN_TYPE:
			{
				result = String.valueOf(itsBooleanValue);
				break;
			}
			case MetadataItem.INTEGER_TYPE:
			{
				result = String.valueOf(itsIntValue);
				break;
			}
			case MetadataItem.REAL_TYPE:
			{
				result = String.valueOf(itsRealValue);
				break;
			}
			case MetadataItem.STRING_TYPE:
			{
				result = itsStringValue;
				break;
			}
			case MetadataItem.DATE_TYPE:
			{
				result = dateTimeHandler.formatDate(itsDateValue);
				break;
			}
			}
			
			return result;
		}
		
		/**
		 * Returns the integer value of this item
		 * @return The integer value of this item
		 */
		int getInt() {
			return itsIntValue;
		}
		
		/**
		 * Returns the real value of this item
		 * @return The real value of this item
		 */
		double getReal() {
			return itsRealValue;
		}
		
		/**
		 * Returns the boolean value of this item
		 * @return The boolean value of this item
		 */
		boolean getBoolean() {
			return itsBooleanValue;
		}
			
		
		/**
		 * Returns the date value of this item
		 * @return The date value of this item
		 */
		DateTime getDate() {
			return itsDateValue;
		}
		
		/**
		 * Returns the string value of this item
		 * @return The string value of this item
		 */
		String getString() {
			return itsStringValue;
		}
	}
	
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

