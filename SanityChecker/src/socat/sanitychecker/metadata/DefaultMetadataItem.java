package socat.sanitychecker.metadata;

import java.text.ParseException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import socat.sanitychecker.config.MetadataConfigItem;
import socat.sanitychecker.data.SocatDataRecord;
import socat.sanitychecker.data.datetime.DateTimeException;


/**
 * Default implementation of the {@code MetadataItem} class.
 * Does not allow values to be generated. 
 */
public class DefaultMetadataItem extends MetadataItem {

	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param value The value of the metadata item
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public DefaultMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException, DateTimeException {
		super(config, line, logger);
		itCanGenerate = false;
		itCanGenerateFromOneRecord = false;
	}

	/**
	 * This implementation of the {@code MetadataItem} class
	 * cannot generate values, so an exception is always thrown. 
	 */
	@Override
	public void generateValue() throws MetadataException {
		throw new MetadataException("This metadata value cannot be automatically generated");
	}

	/**
	 * This implementation of the {@code MetadataItem} class
	 * cannot generate values, so an exception is always thrown. 
	 */
	@Override
	public void processRecordForValue(HashMap<String, MetadataItem> metadataSet, SocatDataRecord record) throws MetadataException {
		throw new MetadataException("This metadata value cannot be automatically generated");
	}
}
