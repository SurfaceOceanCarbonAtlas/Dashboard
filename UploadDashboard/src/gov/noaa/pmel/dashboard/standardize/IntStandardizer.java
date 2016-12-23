/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * @author Karl Smith
 */
public class IntStandardizer extends ValueStandardizer {

	public IntStandardizer(DataColumnType dtype) throws NotStandardizedException {
		super(dtype);
		// TODO:
	}

	@Override
	public Integer getStandardValue(String strVal) throws NotStandardizedException {
		// TODO:
		throw new NotStandardizedException("not yet implemented");
	}

}
