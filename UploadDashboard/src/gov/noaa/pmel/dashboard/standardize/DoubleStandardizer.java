/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import java.util.HashMap;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Standardizer for double values.
 * 
 * @author Karl Smith
 */
public class DoubleStandardizer extends ValueStandardizer {

	/**
	 * Factors for performing linear conversions.
	 */
	private static class Factor {
		private double m;
		private double b;

		/**
		 * Factor for performing linear conversions
		 * 	y = m * x + b 
		 * where x in the input value and y is the converted value.
		 *  
		 * @param m
		 * 		slope of the linear conversion
		 * @param b
		 * 		intercept of the linear conversion
		 */
		private Factor(double m, double b) {
			this.m = m;
			this.b = b;
		}

		/**
		 * @param value
		 * 		input value
		 * @return
		 * 		converted value
		 */
		private double convert(double value) {
			return (m * value) + b;
		}
	}

	private static final HashMap<String,Factor> CONVERSION_FACTORS;
	static {
		CONVERSION_FACTORS = new HashMap<String,Factor>(16);
		// Depth
		CONVERSION_FACTORS.put("from \"km\" to \"m\"", new Factor(1000.0, 0.0));
		// Distance
		CONVERSION_FACTORS.put("from \"m\" to \"km\"", new Factor(0.001, 0.0));
		// Temperature
		CONVERSION_FACTORS.put("from \"K\" to \"degC\"", new Factor(1.0, -273.15));
		// Pressure
		CONVERSION_FACTORS.put("from \"hPa\" to \"db\"", new Factor(0.01, 0.0));
		CONVERSION_FACTORS.put("from \"mmHg\" to \"db\"", new Factor(0.013332239, 0.0));
		CONVERSION_FACTORS.put("from \"PSI\" to \"db\"", new Factor(0.689475728, 0.0));
		// Conversions requiring density (another data column)
		CONVERSION_FACTORS.put("from \"mg/L\" to \"umol/kg\"", null);
		CONVERSION_FACTORS.put("from \"mL/L\" to \"umol/kg\"", null);
	}

	private Factor conversionFactor;

	/**
	 * Create a standardizer for a Double data column type.  This standardizer returns 
	 * null for missing value strings; otherwise parses strings as floating point values 
	 * and performs any unit conversion required. 
	 *  
	 * @param dtype
	 * 		Double data column type to standardize
	 * @throws IllegalArgumentException
	 * 		if the data column type is not a Double type, or
	 * 		if the unit conversion is not supported
	 * @throws IllegalStateException
	 * 		if the unit conversion needs standardized data from another column
	 */
	public DoubleStandardizer(DataColumnType dtype) 
			throws IllegalArgumentException, IllegalStateException {
		super(dtype);
		if ( ! DashboardUtils.DOUBLE_DATA_CLASS_NAME.equals(dataType.getDataClassName()) )
			throw new IllegalArgumentException("data class name not " + DashboardUtils.DOUBLE_DATA_CLASS_NAME);
		if ( ( (fromUnit == null) && (toUnit == null) ) || 
			 ( (fromUnit != null) && fromUnit.equals(toUnit) ) ) {
			conversionFactor = new Factor(1.0, 0.0);
			return;
		}
		String conversionKey = "from \"" + fromUnit + "\" to \"" + toUnit + "\"";
		conversionFactor = CONVERSION_FACTORS.get(conversionKey);
		if ( conversionFactor == null ) {
			 if ( CONVERSION_FACTORS.containsKey(conversionKey) ) 
				 throw new IllegalStateException("conversion " + conversionKey + " needs more data");
			 throw new IllegalArgumentException("conversion " + conversionKey + " is not supported");
		}
	}

	/**
	 * Standardized the given string representation of a floating-point value by first 
	 * identifying missing values.  If not a missing value, the string is parsed as a 
	 * floating-point values (see {@link Double#valueOf(String)}) and any unit conversion 
	 * is performed.
	 * 
	 * @param strVal
	 * 		the given string representation of the floating-point value
	 * @return
	 * 		null if the string matches (case insensitive, trimmed) a missing value for this type; 
	 * 		otherwise the unit-converted floating-point value represented by the string
	 * @throws IllegalArgumentException
	 * 		if null is given, or 
	 * 		if the string represents neither a missing value nor a floating-point value
	 * @throws IllegalStateException
	 * 		if unit conversion cannot be accomplished.
	 */
	@Override
	public Double getStandardValue(String strVal) 
			throws IllegalArgumentException, IllegalStateException {
		if ( strVal == null )
			throw new IllegalArgumentException("null string given");
		if ( isMissingValue(strVal) )
			return null;
		Double value;
		try {
			value = Double.valueOf(strVal);
		} catch ( Exception ex ) {
			value = null;
		}
		if ( value == null )
			throw new IllegalArgumentException("\"" + strVal + "\" is neither an float-point nor a missing value");
		if ( conversionFactor == null )
			throw new IllegalStateException("Unable to perform unit conversion");
		return conversionFactor.convert(value);
	}

}
