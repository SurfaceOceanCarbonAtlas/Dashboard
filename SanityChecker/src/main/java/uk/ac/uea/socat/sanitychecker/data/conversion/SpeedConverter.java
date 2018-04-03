package uk.ac.uea.socat.sanitychecker.data.conversion;

import java.util.ArrayList;

/**
 * Performs speed conversions to knots (for ships; air speeds should be in m/s)
 */
public class SpeedConverter extends SpecifiedUnitsConverter {

	public SpeedConverter() {
		itsSupportedUnits = new ArrayList<String>();
		itsSupportedUnits.add("knots");
		itsSupportedUnits.add("km/h");
		itsSupportedUnits.add("m/s");
		itsSupportedUnits.add("mph");
	}
	
	@Override
	public String convert(String value, String units) throws ConversionException {
		String result = value;
		
		if (units.equalsIgnoreCase("km/h")) {
			result = convertKilometersPerHour(value);
		}
		else if (units.equalsIgnoreCase("m/s")) {
			result = convertMetersPerSecond(value);
		}
		else if (units.equalsIgnoreCase("mph")) {
			result = convertMilesPerHour(value);
		}
		
		return result;
	}

	/**
	 * Convert km/h to knots
	 * @param value The input value in km/h
	 * @return The converted knots value
	 * @throws ConversionException If the number cannot be parsed
	 */
	private String convertKilometersPerHour(String value) throws ConversionException {
		String result = value;
		try {
			Double doubleValue = Double.parseDouble(value);
			result = Double.toString(doubleValue / 1.852);
		} catch (NumberFormatException e) {
			throw new ConversionException("Cannot parse value");
		}
		
		return result;
	}

	/**
	 * Convert m/s to knots
	 * @param value The input value in m/s
	 * @return The converted knots value
	 * @throws ConversionException If the number cannot be parsed
	 */
	private String convertMetersPerSecond(String value) throws ConversionException {
		String result = value;
		try {
			Double doubleValue = Double.parseDouble(value);
			result = Double.toString(doubleValue * 3.6 / 1.852);
		} catch (NumberFormatException e) {
			throw new ConversionException("Cannot parse value");
		}
		
		return result;
	}

	/**
	 * Convert mph to knots
	 * @param value The input value in mph
	 * @return The converted knots value
	 * @throws ConversionException If the number cannot be parsed
	 */
	private String convertMilesPerHour(String value) throws ConversionException {
		String result = value;
		try {
			Double doubleValue = Double.parseDouble(value);
			result = Double.toString(doubleValue * 1.609344 / 1.852);
		} catch (NumberFormatException e) {
			throw new ConversionException("Cannot parse value");
		}
		
		return result;
	}

}
