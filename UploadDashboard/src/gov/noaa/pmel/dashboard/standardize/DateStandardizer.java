/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import java.util.Date;

import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * @author Karl Smith
 */
public class DateStandardizer extends ValueStandardizer {

	public DateStandardizer(DataColumnType dtype) throws NotStandardizedException {
		super(dtype);
		// TODO:
	}

	@Override
	public Date getStandardValue(String strVal) throws NotStandardizedException {
		// TODO:
		throw new NotStandardizedException("not yet implemented");
	}

}
