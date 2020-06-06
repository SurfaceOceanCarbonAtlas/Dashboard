package gov.noaa.pmel.tmap.las.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
	private final static Pattern ID_PATTERN = Pattern.compile("[A-Za-z0-9._:/-]+");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String ID = "http://strider.weathertopconsulting.com:8282/baker::-::coads_climatology_cdf";
		if ( validateIdValue(new String[]{ID})) {
			System.out.println("The ID="+ID+" is valid");
		} else {
			System.out.println("The ID="+ID+" is not valid");
		}

	}
	private static boolean validateIdValue(String[] value) {
		if ( value == null || value.length == 0 ) return true;
		// More than one is allowed so don't test the length
		Matcher m = ID_PATTERN.matcher(value[0]);
		return m.matches();
	}
}
