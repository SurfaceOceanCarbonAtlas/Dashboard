package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;

/**
 * Implementation of the {@code MetadataItem} class
 * to handle the Initial Submission Date entry
 */
@SuppressWarnings("deprecation")
public class RevisedSubmissionMetadataItem extends MetadataItem {

	
	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param value The value of the metadata item
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public RevisedSubmissionMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException, DateTimeException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = true;
	}

	@Override
	public void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException {
		// Set to today's date
		setValue(new DateMidnight());

	}

	@Override
	public void processRecordForValue(Map<String, MetadataItem> metadataSet, SocatDataRecord record) throws MetadataException {
		// TODO Auto-generated method stub
		
	}

}
