package socat.sanitychecker.metadata;

import java.text.ParseException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import socat.sanitychecker.config.MetadataConfigItem;
import socat.sanitychecker.data.SocatDataRecord;
import socat.sanitychecker.data.datetime.DateTimeException;

/**
 * Implementation of the {@code MetadataItem} class
 * to handle the Vessel Name entry
 */
public class VesselLookupMetadataItem extends MetadataItem {

	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param value The value of the metadata item
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public VesselLookupMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException, DateTimeException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = true;
	}

	/**
	 * If a vessel name was supplied in the metadata,
	 * we'll use that. Otherwise we'll look it up using the
	 * NODC code.
	 */
	@Override
	public void generateValue() throws MetadataException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processRecordForValue(HashMap<String, MetadataItem> metadataSet,
			SocatDataRecord record) throws MetadataException {
		// TODO Auto-generated method stub
		
	}

}
