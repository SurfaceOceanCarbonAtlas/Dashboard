package uk.ac.uea.socat.sanitychecker.data.conversion;


/**
 * Base implementation of the Converter interface.
 * This implements the {@code checkHandledUnits} method, which
 * always returns true. This is for converters that don't care
 * about units (mainly for text-based converters).
 *
 */
public abstract class AnyUnitsConverter implements Converter{

	@Override
	public boolean checkHandledUnits(String units) {
		return true;
	}
}
