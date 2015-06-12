package uk.ac.uea.socat.sanitychecker.data.conversion;

import java.util.ArrayList;

/**
 * Performs temperature conversions
 */
public class LongitudeConverter extends SpecifiedUnitsConverter {

	public LongitudeConverter() {
		itsSupportedUnits = new ArrayList<String>();
		itsSupportedUnits.add("decimal_degrees_east");
		itsSupportedUnits.add("decimal_degrees_west");
	}
	
	@Override
	public String convert(String value, String units) throws ConversionException {
		
		String result;
		// SOCAT uses negative longitudes for degrees west
		try {
			Double sourceLon = Double.parseDouble(value);
			if ( units.equalsIgnoreCase("decimal_degrees_west") ) {
				sourceLon *= -1.0;
			}
			while (sourceLon <= -180.0 ) {
				sourceLon += 360.0;
			}
			while (sourceLon > 180.0) {
				sourceLon -= 360.0;
			}
			result = Double.toString(sourceLon);
		} catch (NumberFormatException ex) {
			throw new ConversionException("Invalid longitude '" + value + "'");
		}
		
		return result;
	}
}
