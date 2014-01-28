package uk.ac.uea.socat.sanitychecker;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Miscellaneous utility methods
 */
public class CheckerUtils {

	/**
	 * Processes all items in a list of strings, trimming them and converting them
	 * to lower case
	 * @param source The list of strings to be converted 
	 * @return The list of converted strings
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
	 * @param source The list of strings to be converted 
	 * @return The list of converted strings
	 */
	public static List<String> trimList(List<String> source) {
		
		List<String> result = new ArrayList<String>(source.size());
		
		for (int i = 0; i < source.size(); i++) {
			result.add(source.get(i).trim());
		}
		
		return result;
	}
	
	/**
	 * Determines whether or not a line is a comment, signified by it starting with @code{#} or @code{!} or @code{//}
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
	 * Determines whether or not all of a list of string values is empty. NA or NaN qualifies as empty.
	 * @param value The value to be checked.
	 * @return {@code true} if the value is empty; {@code false} otherwise.
	 */
	public static boolean isEmpty(List<String> values) {
		boolean result = true;

		for (String value: values) {
			if (null != value && !value.equals("") && !value.equalsIgnoreCase("nan") && !value.equalsIgnoreCase("na")) {
				result = false;
			}
		}

		return result;
	}
	
	/**
	 * Determines whether or not all of a set of string values is empty. NA or NaN qualifies as empty.
	 * @param value The value to be checked.
	 * @return {@code true} if the value is empty; {@code false} otherwise.
	 */
	public static boolean isEmpty(String... values) {
		return isEmpty(Arrays.asList(values));
	}
}
