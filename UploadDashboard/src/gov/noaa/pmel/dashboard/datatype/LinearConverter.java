/**
 * 
 */
package gov.noaa.pmel.dashboard.datatype;

import java.util.TreeMap;

/**
 * @author ksmith
 *
 */
public class LinearConverter extends ValueConverter<Double> {

	private static final TreeMap<String,Double> SLOPES_MAP;
	private static final TreeMap<String,Double> INTERCEPTS_MAP;
	static {
		SLOPES_MAP = new TreeMap<String,Double>(String.CASE_INSENSITIVE_ORDER);
		INTERCEPTS_MAP = new TreeMap<String,Double>(String.CASE_INSENSITIVE_ORDER);
		String key;

		// Depth
		key = "from \"km\" to \"m\"";
		SLOPES_MAP.put(key, 1000.0);
		INTERCEPTS_MAP.put(key, 0.0);

		// Distance
		key = "from \"m\" to \"km\"";
		SLOPES_MAP.put(key, 0.001);
		INTERCEPTS_MAP.put(key, 0.0);

		// Temperature
		key = "from \"K\" to \"degC\"";
		SLOPES_MAP.put(key, 1.0);
		INTERCEPTS_MAP.put(key, -273.15);

		// Pressure
		key = "from \"hPa\" to \"db\"";
		SLOPES_MAP.put(key, 0.01);
		INTERCEPTS_MAP.put(key, 0.0);
		key = "from \"mmHg\" to \"db\"";
		SLOPES_MAP.put(key, 0.013332239);
		INTERCEPTS_MAP.put(key, 0.0);
		key = "from \"PSI\" to \"db\"";
		SLOPES_MAP.put(key, 0.689475728);
		INTERCEPTS_MAP.put(key, 0.0);

		// TODO: add more linear unit conversions
	}

	Double slope;
	Double intercept;

	/**
	 * Interprets string representations of floating-point values using 
	 * {@link Double#valueOf(String)} after checking for missing values.
	 * Performs linear conversions between known units if the units are
	 * not equal.
	 * 
	 * @param inputUnit
	 * 		unit of the values represented by the input strings, or 
	 * 		null for unitless values
	 * @param outputUnit
	 * 		unit for the values to returned, or null for unitless values
	 * @param missingValue
	 * 		missing value string, or null for default missing values
	 * @throws IllegalArgumentException
	 * 		if unable to perform the unit conversion
	 * @throws IllegalStateException
	 * 		never thrown; does not perform any conversions that require
	 * 		standardized data from other data columns
	 */
	public LinearConverter(String inputUnit, String outputUnit, String missingValue) 
			throws IllegalArgumentException, IllegalStateException {
		super(inputUnit, outputUnit, missingValue);

		if ( ( (fromUnit == null) && (toUnit == null) ) || 
			 ( (fromUnit != null) && fromUnit.equals(toUnit) ) ) {
			slope = 1.0;
			intercept = 0.0;
			return;
		}

		String conversionKey = "from \"" + fromUnit + "\" to \"" + toUnit + "\"";
		Double slope = SLOPES_MAP.get(conversionKey);
		Double intercept = INTERCEPTS_MAP.get(conversionKey);
		if ( (slope == null) || (intercept == null) )
			throw new IllegalArgumentException("conversion " + conversionKey + " is not supported");
	}

	@Override
	public Double convertValueOf(String valueString) 
			throws IllegalArgumentException, IllegalStateException {
		if ( (slope == null) || (intercept == null) )
			throw new IllegalArgumentException("conversion from \"" + 
					fromUnit + "\" to \"" + toUnit + "\" is not supported");
		if ( isMissingValue(valueString, true) )
			return null;
		Double dataVal;
		try {
			dataVal = Double.valueOf(valueString);
		} catch ( NumberFormatException ex ) {
			dataVal = Double.NaN;
		}
		if ( dataVal.isNaN() || dataVal.isInfinite() )
			throw new IllegalArgumentException("not a valid floating-point value");
		dataVal *= slope;
		dataVal += intercept;
		return dataVal;
	}

}
