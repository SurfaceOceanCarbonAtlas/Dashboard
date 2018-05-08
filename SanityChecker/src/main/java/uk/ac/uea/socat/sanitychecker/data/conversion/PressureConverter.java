package uk.ac.uea.socat.sanitychecker.data.conversion;

import java.util.ArrayList;

/**
 * Performs pressure conversions
 */
public class PressureConverter extends SpecifiedUnitsConverter {

	/**
	 * Dummy constructor - nothing to be done here!
	 */
	public PressureConverter() {
		itsSupportedUnits = new ArrayList<String>();
		itsSupportedUnits.add("hpa");
		itsSupportedUnits.add("kpa");
		itsSupportedUnits.add("millibar");
		itsSupportedUnits.add("inches");
		itsSupportedUnits.add("mmhg");
		itsSupportedUnits.add("torr");
	}
	
	@Override
	public String convert(String value, String units) throws ConversionException {
		String result = value;
		
		if (units.equalsIgnoreCase("inches")) {
			result = convertInches(value);
		}
		else if (units.equalsIgnoreCase("kpa")) {
			result = convertKilopascal(value);
		}
		else if ( units.equalsIgnoreCase("mmhg") ||
				  units.equalsIgnoreCase("torr") ) {
			result = convertTorr(value);
		}
		
		return result;
	}

	/**
	 * Convert inches of mercury to hPa
	 * @param value The input value in inches
	 * @return The converted hPa value
	 * @throws ConversionException If the number cannot be parsed
	 */
	private String convertInches(String value) throws ConversionException {
		String result = value;
		try {
			Double doubleValue = Double.parseDouble(value);
			result = Double.toString(doubleValue * 0.029529983071);
		} catch (NumberFormatException e) {
			throw new ConversionException("Cannot parse value");
		}
		
		return result;
	}

	/**
	 * Convert kPa to hPa
	 * @param value The input value in kPa
	 * @return The converted hPa value
	 * @throws ConversionException If the number cannot be parsed
	 */
	private String convertKilopascal(String value) throws ConversionException {
		String result = value;
		try {
			Double doubleValue = Double.parseDouble(value);
			result = Double.toString(doubleValue * 10.0);
		} catch (NumberFormatException e) {
			throw new ConversionException("Cannot parse value");
		}
		
		return result;
	}

	/**
	 * Convert Torr (millimeters of mercury) to hPa
	 * @param value The input value in Torr (mmHg)
	 * @return The converted hPa value
	 * @throws ConversionException If the number cannot be parsed
	 */
	private String convertTorr(String value) throws ConversionException {
		String result = value;
		try {
			Double doubleValue = Double.parseDouble(value);
			result = Double.toString(doubleValue * 1.3332236842105);
		} catch (NumberFormatException e) {
			throw new ConversionException("Cannot parse value");
		}
		
		return result;
	}

}
