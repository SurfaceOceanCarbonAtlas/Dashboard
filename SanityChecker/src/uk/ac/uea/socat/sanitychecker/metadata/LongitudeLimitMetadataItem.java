package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.uea.socat.sanitychecker.SanityCheckerException;
import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;


/**
 * Implementation of the {@code MetadataItem} class
 * to extract geographical limits from the data.
 */
public class LongitudeLimitMetadataItem extends MetadataItem {

	/**
	 * The value that represents a missing longitude
	 */
	private static final String MISSING_VALUE = "-999";
	
	/**
	 * Indicates whether or not a value has been set
	 */
	private boolean hasValue = false;

	/**
	 * List of all longitude values
	 */
	private List<Double> allLons;
	
	/**
	 * Flag to indicate whether the cruise has crossed the 0 degree line
	 */
	private boolean crossesZero = false;
	
	/**
	 * The longitude from the previous record. Used determine if the cruise crosses the 0 degree line
	 */
	private double lastPosition = -9999.0;
	
	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param line The line number of the file on which this item occurs. If the item has come from the command line, pass {@code -1}.
 	 * @param messages the set of output message
	 * @param logger The system logger
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public LongitudeLimitMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = false;
		
		allLons = new ArrayList<Double>();
	}

	@Override
	public void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException, SanityCheckerException {
		// If we haven't had any data, set a dummy value
		if (!hasValue) {
			setValue(MISSING_VALUE);
		} else {
			String parameter = itsConfigItem.getGeneratorParameter();
			// Sort the list of all longitudes.
			Collections.sort(allLons);
			
			// If the cruise hasn't crossed zero, then things are easy;
			// The lowest value is the western limit, and the highest is the eastern limit
			if (!crossesZero) {
				if (parameter.equalsIgnoreCase("W")) {
					setValue(String.valueOf(allLons.get(0)));
				} else if (parameter.equalsIgnoreCase("E")) {
					setValue(String.valueOf(allLons.get(allLons.size() - 1)));
				}
			} else {
				
				/*
				 * Because we crossed zero, the limits are not simply the minimum and maximum -
				 * those will be the two points closes to zero.
				 * Instead, we look at the gaps between the sorted longitude positions. The largest
				 * gap indicates the portion of the globe *not* covered by the cruise, so the points
				 * either side of this gap indicate the eastern and western limits.
				 * 
				 * The low end of the gap is the eastern limit; the high point is the western limit.
				 */
				
				double largestGap = 0;
				int gapLower = 0;
				int gapUpper = 0;
				
				double gap;
				for (int i = 1; i < allLons.size(); i++) {
					gap = allLons.get(i) - allLons.get(i - 1);
					if (gap > largestGap) {
						largestGap = gap;
						gapLower = i - 1;
						gapUpper = i;
					}
				}
				
				if (parameter.equalsIgnoreCase("W")) {
					setValue(String.valueOf(allLons.get(gapUpper)));
				} else if (parameter.equalsIgnoreCase("E")) {
					setValue(String.valueOf(allLons.get(gapLower)));
				}
			}
		}
	}

	@Override
	public void processRecordForValue(SocatDataRecord record) throws MetadataException {

		SocatDataColumn longitudeColumn = record.getColumn(SocatColumnConfig.LONGITUDE_COLUMN_NAME);
		if (!longitudeColumn.getFlag().equals(Flag.BAD)) {

			// Get the longitude from the record - covert to 0-360 range
			Double position = Double.parseDouble(longitudeColumn.getValue());
			// Only allow out to [-540,540]
			if ( (position >= -540.0) && (position < -360.0) ) {
				position += 720.0;
			}
			else if ( (position >= -360.0) && (position < 0.0) ) {
				position += 360.0;
			}
			else if ( (position >= 360.0) && (position < 540.0) ) {
				position -= 360.0;
			}
			
			// Add the longitude to the list of all longitudes
			allLons.add(position);
			hasValue = true;
			
			// See if the cruise has crossed the zero line, if it hasn't already.
			if (!crossesZero) {
				if (lastPosition != -9999.0) {
					if (Math.abs(position - lastPosition) > 180) {
						crossesZero = true;
					}
				}
				lastPosition = position;
			}
		}
	}
}
