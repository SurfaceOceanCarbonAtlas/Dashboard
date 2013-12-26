package uk.ac.uea.socat.sanitychecker.data.conversion;

import java.util.List;

/**
 * Base implementation of the Converter interface.
 * This implements the {@code checkHandledUnits} method, which simply
 * checks a list of known handled units to see if there's a match.
 * 
 * Implementing classes can populate the {@code itsSupportedUnits} variable
 * with a list of all the handled units. Note that they must all be in lower
 * case, and must also include the final output units. For example, we support
 * conversion of temperatures from degrees Kelvin, and the output units are in
 * degrees Celsius. Both must be in the list.
 *
 */
public abstract class SpecifiedUnitsConverter implements Converter{

	protected List<String> itsSupportedUnits;
	
	@Override
	public boolean checkHandledUnits(String units) {
		boolean result = false;
		
		if (null != itsSupportedUnits) {
			result = itsSupportedUnits.contains(units.toLowerCase());
		}

		return result;
	}
}
