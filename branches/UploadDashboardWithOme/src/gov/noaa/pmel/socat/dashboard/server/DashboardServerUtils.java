/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Smith
 *
 */
public class DashboardServerUtils {

	// Pattern for checking for invalid characters in the expocode
	private static final Pattern invalidExpocodePattern = 
			Pattern.compile("[^" + DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]");

	/**
	 * Checks and standardized a given expocode.
	 * 
	 * @param expocode
	 * 		expocode to check
	 * @return
	 * 		standardized (uppercase) expocode
	 * @throws IllegalArgumentException
	 * 		if the expocode is unreasonable
	 * 		(invalid characters, too short, too long)
	 */
	public static String checkExpocode(String expocode) throws IllegalArgumentException {
		if ( expocode == null )
			throw new IllegalArgumentException("Expocode not given");
		// Do some automatic clean-up
		String upperExpo = expocode.trim().toUpperCase();
		// Make sure it is the proper length
		if ( (upperExpo.length() < DashboardUtils.MIN_EXPOCODE_LENGTH) || 
			 (upperExpo.length() > DashboardUtils.MAX_EXPOCODE_LENGTH) )
			throw new IllegalArgumentException(
					"Invalid Expocode length");
		// Make sure there are no invalid characters
		Matcher mat = invalidExpocodePattern.matcher(upperExpo);
		if ( mat.find() )
			throw new IllegalArgumentException(
					"Invalid characters in the Expocode");
		return upperExpo;
	}

}
