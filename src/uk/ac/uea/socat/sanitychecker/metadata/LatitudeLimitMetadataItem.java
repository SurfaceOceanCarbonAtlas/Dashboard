package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;


/**
 * Implementation of the {@code MetadataItem} class
 * to extract geographical limits from the data.
 */
public class LatitudeLimitMetadataItem extends MetadataItem {

	/**
	 * The value that represents a missing latitude
	 */
	private static final double MISSING_VALUE = -999.0;
	
	/**
	 * Defines the name of the Latitude column in data records
	 */
	private static final String LATITUDE_COLUMN = "latitude";
	
	/**
	 * Indicates whether or not a value has been set
	 */
	private boolean hasValue = false;
	
	/**
	 * The minimum limit of the geographical data
	 */
	private double min = 9999.0;
	
	/**
	 * The maximum limit of the geographical data
	 */
	private double max = -9999.0;
	
	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param value The value of the metadata item
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public LatitudeLimitMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException, DateTimeException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = false;
	}

	@Override
	public void generateValue() throws MetadataException {
		// If we haven't had any data, set a dummy value
		if (!hasValue) {
			setValue(MISSING_VALUE);
		} else {
			// The value is set according to the passed in parameter
			String parameter = itsConfigItem.getGeneratorParameter();
			if (parameter.equalsIgnoreCase("N")) {
				// The northern limit is the maximum value stored.
				setValue(max);
			} else if (parameter.equalsIgnoreCase("S")) {
				// The southern limit is the minimum value stored.
				setValue(min);
			}
		}
	}

	@Override
	public void processRecordForValue(HashMap<String, MetadataItem> metadataSet, SocatDataRecord record) throws MetadataException {
		double position = Double.parseDouble(record.getColumn(LATITUDE_COLUMN).getValue());
		updateLimits(position);
		hasValue = true;
	}
	
	/**
	 * Updates the geographical limits using the specified value.
	 * This method is used for both latitude and longitude values.
	 * @param position The value (in degrees) of the record's position.
	 */
	private void updateLimits(double position) {
		if (position > max) {
			max = position;
		}
		
		if (position < min) {
			min = position;
		}
		
		hasValue = true;
	}
}
