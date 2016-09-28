/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Smith
 */
public class DashboardServerUtils {

	/** mapping from old unit names to new unit names */
	public static final HashMap<String,String> RENAMED_UNITS;
	static {
		RENAMED_UNITS = new HashMap<String,String>();
		RENAMED_UNITS.put("deg.E", "degrees_east");
		RENAMED_UNITS.put("deg.W", "degrees_west");
		RENAMED_UNITS.put("deg.N", "degrees_north");
		RENAMED_UNITS.put("deg.S", "degrees_south");
		RENAMED_UNITS.put("deg.C", "degrees C");
		RENAMED_UNITS.put("deg.clk.N", "degrees");
	}

	/** Pattern for getKeyForName */
	private static final Pattern stripPattern = Pattern.compile("[^a-z0-9]+");

	/**
	 * Computes a key for the given name which is case-insensitive and ignores 
	 * non-alphanumeric characters.  The value returned is equivalent to 
	 * <pre>name.toLowerCase().replaceAll("[^a-z0-9]+", "")</pre>
	 * 
	 * @param name
	 * 		name to use
	 * @return
	 * 		key for the name
	 */
	public static String getKeyForName(String name) {
		return stripPattern.matcher(name.toLowerCase()).replaceAll("");
	}

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

	/**
	 * Checks the validity of the given "NODC code" (first four characters of a standard expocode).
	 * This does not actually check that the value is listed in the NODC registry of ships.
	 * 
	 * @param nodccode
	 * 		expocode start to check
	 * @return
	 * 		false if nodccode is not exactly four characters from 
	 * 		{@link DashboardUtils#VALID_EXPOCODE_CHARACTERS};
	 * 		otherwise true
	 */
	public static boolean isLikeNODCCode(String nodccode) {
		if ( (nodccode == null) || (nodccode.length() != 4) )
			return false;
		Matcher mat = invalidExpocodePattern.matcher(nodccode);
		if ( mat.find() )
			return false;
		return true;
	}

}
