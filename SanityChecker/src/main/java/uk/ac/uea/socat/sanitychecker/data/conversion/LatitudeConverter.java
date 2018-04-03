package uk.ac.uea.socat.sanitychecker.data.conversion;

import java.util.ArrayList;

/**
 * Performs temperature conversions
 */
public class LatitudeConverter extends SpecifiedUnitsConverter {

	public LatitudeConverter() {
		itsSupportedUnits = new ArrayList<String>();
		itsSupportedUnits.add("decimal_degrees_north");
		itsSupportedUnits.add("decimal_degrees_south");
	}
	
	@Override
	public String convert(String value, String units) throws ConversionException {
		
		String result;
		try {
			Double sourceLat = Double.parseDouble(value);
			if ( units.equalsIgnoreCase("decimal_degrees_south") ) {
				sourceLat *= -1.0;
			}
			result = Double.toString(sourceLat);
		} catch (NumberFormatException ex) {
			throw new ConversionException("Invalid latitude '" + value + "'");
		}
		
		return result;
	}
}
