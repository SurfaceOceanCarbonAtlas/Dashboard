/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * @author Karl Smith
 */
public class DoubleStandardizer extends ValueStandardizer {

	public DoubleStandardizer(DataColumnType dtype) throws NotStandardizedException {
		super(dtype);
		// TODO:
	}

	@Override
	public Double getStandardValue(String strVal) throws NotStandardizedException {
		// TODO:
		throw new NotStandardizedException("not yet implemented");
	}

}
