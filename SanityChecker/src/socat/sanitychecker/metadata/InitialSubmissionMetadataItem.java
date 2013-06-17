package socat.sanitychecker.metadata;

import java.text.ParseException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import socat.sanitychecker.config.MetadataConfigItem;
import socat.sanitychecker.data.SocatDataRecord;
import socat.sanitychecker.data.datetime.DateTimeException;


/**
 * Implementation of the {@code MetadataItem} class
 * to handle the Initial Submission Date entry
 */
public class InitialSubmissionMetadataItem extends MetadataItem {

	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param value The value of the metadata item
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public InitialSubmissionMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException, DateTimeException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = true;
	}

	@Override
	public void generateValue() throws MetadataException {
		// If there's already an initial submission date, we do nothing.
		// Otherwise we set today's date.
		try {
			if (null == getValue()) {
				setValue(new DateMidnight());
			}
		} catch (DateTimeException e) {
			throw new MetadataException(getName(), "A value already exists for the initial submission date, but it couldn't be retrieved");
		}
	}

	@Override
	public void processRecordForValue(
			HashMap<String, MetadataItem> metadataSet, SocatDataRecord record)
			throws MetadataException {
		// TODO Auto-generated method stub
		
	}

}
