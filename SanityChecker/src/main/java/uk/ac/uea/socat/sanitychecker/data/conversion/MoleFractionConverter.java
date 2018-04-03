package uk.ac.uea.socat.sanitychecker.data.conversion;

import java.util.ArrayList;

/**
 * Performs conversions for mole fractions;
 * in particular, for xH2O since the default is mmol/mol
 */
public class MoleFractionConverter extends SpecifiedUnitsConverter {

	public MoleFractionConverter() {
		itsSupportedUnits = new ArrayList<String>();
		itsSupportedUnits.add("mmol/mol");
		itsSupportedUnits.add("umol/mol");
	}

	@Override
	public String convert(String value, String units) throws ConversionException {
		String result;
		if ( units.equalsIgnoreCase("umol/mol") ) {
			try {
				Double doubleValue = Double.parseDouble(value);
				result = Double.toString(doubleValue / 1000.0);
			} catch (NumberFormatException e) {
				throw new ConversionException("Cannot parse value");
			}
		}
		else {
			result = value;
		}

		return result;
	}

}
