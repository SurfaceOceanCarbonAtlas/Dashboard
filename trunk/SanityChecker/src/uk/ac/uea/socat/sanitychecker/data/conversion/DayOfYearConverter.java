package uk.ac.uea.socat.sanitychecker.data.conversion;

import java.util.ArrayList;

/**
 * Performs day-of-year conversions
 */
public class DayOfYearConverter extends SpecifiedUnitsConverter {

	public DayOfYearConverter() {
		itsSupportedUnits = new ArrayList<String>();
		itsSupportedUnits.add("jan1=1.0");
		itsSupportedUnits.add("jan1=0.0");
	}
	
	@Override
	public String convert(String value, String units) throws ConversionException {
		String result = value;
		
		if (units.equalsIgnoreCase("jan1=0.0")) {
			try {
				Double doubleValue = Double.parseDouble(value);
				result = Double.toString(doubleValue + 1.0);
			} catch (NumberFormatException e) {
				throw new ConversionException("Cannot parse value");
			}
		}
		
		return result;
	}

}
