package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import uk.ac.uea.socat.sanitychecker.SanityCheckerException;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;

/**
 * Implementation of the {@code MetadataItem} class
 * to handle the Initial Submission Date entry
 */
public class RevisedSubmissionMetadataItem extends MetadataItem {

	
	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param line The line number of the file on which this item occurs. If the item has come from the command line, pass {@code -1}.
 	 * @param messages the set of output message
	 * @param logger The system logger
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public RevisedSubmissionMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = true;
	}

	@Override
	public void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException, SanityCheckerException {
		// Set to today's date
		DateTime date = new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay();
		setValue(dateTimeHandler.formatDate(date));
	}

	@Override
	public void processRecordForValue(SocatDataRecord record) throws MetadataException {
		// No action required
	}
}
