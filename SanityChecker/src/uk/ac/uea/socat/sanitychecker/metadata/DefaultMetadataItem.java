package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;

import org.apache.logging.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;


/**
 * Default implementation of the {@code MetadataItem} class.
 * Does not allow values to be generated. 
 */
public class DefaultMetadataItem extends MetadataItem {

	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param line The line number of the file on which this item occurs. If the item has come from the command line, pass {@code -1}.
 	 * @param messages the set of output message
	 * @param logger The system logger
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public DefaultMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException {
		super(config, line, logger);
		itCanGenerate = false;
		itCanGenerateFromOneRecord = false;
	}

	/**
	 * This implementation of the {@code MetadataItem} class
	 * cannot generate values, so an exception is always thrown. 
	 */
	@Override
	public void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException {
		throw new MetadataException("This metadata value cannot be automatically generated");
	}

	/**
	 * This implementation of the {@code MetadataItem} class
	 * cannot generate values, so an exception is always thrown. 
	 */
	@Override
	public void processRecordForValue(SocatDataRecord record) throws MetadataException {
		throw new MetadataException("This metadata value cannot be automatically generated");
	}
}
