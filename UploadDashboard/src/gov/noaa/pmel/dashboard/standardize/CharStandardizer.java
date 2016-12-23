/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * @author Karl Smith
 */
public class CharStandardizer extends ValueStandardizer {

	public CharStandardizer(DataColumnType dtype) throws NotStandardizedException {
		super(dtype);
		// TODO:
	}

	@Override
	public Character getStandardValue(String strVal) throws NotStandardizedException {
		// TODO:
		throw new NotStandardizedException("not yet implemented");
	}

}
