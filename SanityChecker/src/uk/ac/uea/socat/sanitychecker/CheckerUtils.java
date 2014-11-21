package uk.ac.uea.socat.sanitychecker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Miscellaneous utility methods
 */
public class CheckerUtils {

	/**
	 * Set of default missing values as trimmed lower-case strings.
	 */
	public static final Set<String> DEFAULT_MISSING_VALUE_STRINGS;
	static {
		HashSet<String> defaultsSet = new HashSet<String>();
		defaultsSet.add("");
		defaultsSet.add("na");
		defaultsSet.add("n/a");
		defaultsSet.add("nan");
		defaultsSet.add("-999");
		defaultsSet.add("-999.");
		defaultsSet.add("-999.0");
		defaultsSet.add("-999.9");
		defaultsSet.add("-9999");
		defaultsSet.add("-9999.");
		defaultsSet.add("-9999.0");
		defaultsSet.add("-9999.9");
		DEFAULT_MISSING_VALUE_STRINGS = Collections.unmodifiableSet(defaultsSet);
	}

	/**
	 * Processes all items in a list of strings, trimming them and converting them
	 * to lower case
	 * @param source The strings to be converted 
	 * @return The converted strings
	 */
	public static List<String> trimAndLowerList(List<String> source) {
		
		List<String> result = new ArrayList<String>(source.size());
		
		for (int i = 0; i < source.size(); i++) {
			result.add(source.get(i).trim().toLowerCase());
		}
		
		return result;
	}
	
	/**
	 * Trims all items in a list of strings
	 * @param source The strings to be converted 
	 * @return The converted strings
	 */
	public static List<String> trimList(List<String> source) {
		
		List<String> result = new ArrayList<String>(source.size());
		
		for (int i = 0; i < source.size(); i++) {
			result.add(source.get(i).trim());
		}
		
		return result;
	}
	
	/**
	 * Convert a list of strings to a single list, separated by spaces.
	 * @param stringList The list of strings
	 * @return The converted string
	 */
	public static String listToString(List<String> stringList) {
		StringBuffer result = new StringBuffer();
		for (String string : stringList) {
			result.append(string);
			result.append(' ');
		}
		
		return result.toString().trim();
	}
	
	/**
	 * Determines whether or not a line is a comment, signified by it starting with {@code #} or {@code !} or {@code //}
	 * @param line The line to be checked
	 * @return {@code true} if the line is a comment; {@code false} otherwise.
	 */
	public static boolean isComment(String line) {
		String trimmedLine = line.trim();
		return trimmedLine.length() == 0 || trimmedLine.charAt(0) == '#' || trimmedLine.charAt(0) == '!' || trimmedLine.startsWith("//", 0);
	}
	
	/**
	 * Parse a string into a boolean value. Accept Y/T and N/F.
	 * @param value The string to be parsed
	 * @return {@code true} if the string contents are {@code Y} or {@code T}; {@code false} if the contents are {@code N} or {@code F}.
	 * @throws ParseException If the string does not contain a recognised boolean value.
	 */
	public static boolean parseBoolean(String value) throws ParseException {
		boolean result = false;
		
		if (value.equalsIgnoreCase("Y") || value.equalsIgnoreCase("T")) {
			result = true;
		} else if (value.equalsIgnoreCase("N") || value.equalsIgnoreCase("F")) {
			result = false;
		} else {
			throw new ParseException("Invalid boolean value (must be Y/N or T/F)", 0);
		}
		
		return result;
	}
	
	/**
	 * Determines whether or not a String value contains a number
	 * @param value The value to be checked
	 * @return {@code true} if the value is numeric; {@code false} otherwise.
	 */
	public static boolean isNumeric(String value) {
		boolean result = true;
		
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Determines whether or not all of a list of string values is empty.  
	 * A null or default missing value is considered empty.
	 * @param values The values to be checked.
	 * @return {@code true} if the values are all empty; {@code false} otherwise.
	 */
	public static boolean isEmpty(List<String> values) {
		boolean result = true;

		for (String value: values) {
			if ( (null != value) && 
				 ! DEFAULT_MISSING_VALUE_STRINGS.contains(value.trim().toLowerCase()) ) {
				result = false;
			}
		}

		return result;
	}
	
	/**
	 * Determines whether or not all of a set of string values is empty.
	 * A null or default missing value is considered empty.
	 * @param values The values to be checked.
	 * @return {@code true} if the values are all empty; {@code false} otherwise.
	 */
	public static boolean isEmpty(String... values) {
		return isEmpty(Arrays.asList(values));
	}
}
