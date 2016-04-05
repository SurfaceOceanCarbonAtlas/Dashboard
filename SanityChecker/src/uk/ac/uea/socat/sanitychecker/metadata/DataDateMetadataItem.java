package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import uk.ac.exeter.QCRoutines.data.NoSuchColumnException;
import uk.ac.uea.socat.sanitychecker.SanityCheckerException;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;


/**
 * Implementation of the {@code MetadataItem} class
 * to extract dates from the data in the file
 */
public class DataDateMetadataItem extends MetadataItem {

	DateTime itsDate = null;
	
	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param line The line number of the file on which this item occurs. If the item has come from the command line, pass {@code -1}.
 	 * @param messages the set of output message
	 * @param logger The system logger
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public DataDateMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = false;
	}

	@Override
	public void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException, SanityCheckerException {
		setValue(dateTimeHandler.formatDate(itsDate));
	}

	@Override
	public void processRecordForValue(SocatDataRecord record) throws MetadataException {
		
		try {
			// Get the record's date
			if (!record.getColumn(SocatColumnConfig.YEAR_COLUMN_NAME).getValue().equalsIgnoreCase("NaN")) {
			
				int year = Integer.parseInt(record.getColumn(SocatColumnConfig.YEAR_COLUMN_NAME).getValue());
				int month = Integer.parseInt(record.getColumn(SocatColumnConfig.MONTH_COLUMN_NAME).getValue());
				int day = Integer.parseInt(record.getColumn(SocatColumnConfig.DAY_COLUMN_NAME).getValue());
				DateTime newDate = new DateTime(year, month, day, 0, 0, 0, DateTimeZone.UTC).withTimeAtStartOfDay();
				
				// If no date is currently set, then this record's date is be recorded
				if (null == itsDate) {
					itsDate = newDate;
				} else {
					
					/* If we're looking for the start date, only store this date if it's before
					   what we already have */ 
					if (itsConfigItem.getGeneratorParameter().equalsIgnoreCase("start")) {
						if (newDate.isBefore(itsDate)) {
							itsDate = newDate;
						}
					/* If we're looking for the end date, only store this date if it's after
					   what we already have */ 
					} else if (itsConfigItem.getGeneratorParameter().equalsIgnoreCase("end")) {
						if (newDate.isAfter(itsDate)) {
							itsDate = newDate;
						}
					}
				}
			}
		} catch (NoSuchColumnException e) {
			throw new MetadataException("Error while retrieving date values", e);
		}
	}
}
