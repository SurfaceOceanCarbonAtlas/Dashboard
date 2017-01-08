/**
 * 
 */
package gov.noaa.pmel.dashboard.datatype;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * For converting various timestamp, date, and time-of-day strings (various 
 * orderings of year, month, and day as well as various separators to standard 
 * formats of yyyy-MM-dd HH:mm:ss.SSS for timestamps, yyyy-MM-dd for dates and 
 * HH:mm:ss.SSS for times-of-day.
 * 
 * @author Karl Smith
 */
public class TimestampConverter extends ValueConverter<String> {

	private static final GregorianCalendar STD_CALENDAR;
	private static final int CURRENT_YEAR;

	// TreeSet so can do case insensitive comparisons
	private static final TreeSet<String> SUPPORTED_FROM_UNITS;
	static {
		STD_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		STD_CALENDAR.setLenient(false);
		STD_CALENDAR.clear();
		STD_CALENDAR.setTime(new Date());
		CURRENT_YEAR = STD_CALENDAR.get(GregorianCalendar.YEAR);

		SUPPORTED_FROM_UNITS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		// timestamps
		SUPPORTED_FROM_UNITS.add("from \"yyyy-mm-dd hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"mm-dd-yyyy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mm-yyyy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"mon-dd-yyyy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mon-yyyy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"mm-dd-yy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mm-yy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"mon-dd-yy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mon-yy hh:mm:ss\" to \"yyyy-mm-dd hh:mm:ss\"");
		// date only
		SUPPORTED_FROM_UNITS.add("from \"yyyy-mm-dd\" to \"yyyy-mm-dd\"");
		SUPPORTED_FROM_UNITS.add("from \"mm-dd-yyyy\" to \"yyyy-mm-dd\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mm-yyyy\" to \"yyyy-mm-dd\"");
		SUPPORTED_FROM_UNITS.add("from \"mm-dd-yy\" to \"yyyy-mm-dd\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mm-yy\" to \"yyyy-mm-dd\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mon-yyyy\" to \"yyyy-mm-dd\"");
		SUPPORTED_FROM_UNITS.add("from \"dd-mon-yy\" to \"yyyy-mm-dd\"");
		// time only
		SUPPORTED_FROM_UNITS.add("from \"hh:mm:ss\" to \"hh:mm:ss\"");
	}

	private static final Pattern TIMESTAMP_SPLIT_PATTERN = Pattern.compile("[T ]+");
	private static final Pattern DATE_SPLIT_PATTERN = Pattern.compile("[/-, ]+");
	private static final Pattern TIME_SPLIT_PATTERN = Pattern.compile("[: ]+");

	public TimestampConverter(String inputUnit, String outputUnit, String missingValue)
			throws IllegalArgumentException, IllegalStateException {
		super(inputUnit, outputUnit, missingValue);
		String key = "from \"" + fromUnit + "\" to \"" + toUnit + "\"";
		if ( ! SUPPORTED_FROM_UNITS.contains(key) )
			throw new IllegalArgumentException("conversion " + key + " not supported");
	}

	@Override
	public String convertValueOf(String valueString) throws IllegalArgumentException, IllegalStateException {
		// Deal with missing values
		if ( isMissingValue(valueString, true) )
			return null;
		String stdVal;
		if ( "yyyy-mm-dd hh:mm:ss".equalsIgnoreCase(toUnit) ) {
			try {
				String[] pieces = TIMESTAMP_SPLIT_PATTERN.split(valueString, 0);
				if ( pieces.length != 2 ) {
					// TODO:
					throw new Exception();
				}
				String dateStdVal = standardizeDate(pieces[0]);
				String timeStdVal = standardizeTime(pieces[1]);
				stdVal = dateStdVal + " " + timeStdVal;
			} catch ( Exception ex ) {
				throw new IllegalArgumentException("not a valid timestamp value");
			}
		}
		else if ( "yyyy-mm-dd".equalsIgnoreCase(toUnit) ) {
			stdVal = standardizeDate(valueString);
		}
		else if ( "hh:mm:ss".equalsIgnoreCase(toUnit) ) {
			stdVal = standardizeTime(valueString);
		}
		else {
			throw new IllegalArgumentException("conversion to \"" + toUnit + "\" is not supported");
		}
		return stdVal;
	}

	/**
	 * Standardize a date value to yyyy-MM-dd
	 * 
	 * @param valueString
	 * 		date string in the format of fromUnit
	 * @return
	 * 		standardized date string
	 * @throws IllegalArgumentException
	 * 		if the fromUnit format is not recognized, or
	 * 		if the value is not a valid date string
	 */
	private String standardizeDate(String valueString) throws IllegalArgumentException {
		Integer year;
		Integer month;
		Integer day;
		if ( "yyyy-mm-dd".equalsIgnoreCase(fromUnit) ) {
			try {
				String[] pieces = DATE_SPLIT_PATTERN.split(valueString, 0);
				if ( pieces.length != 3 ) {
					if ( valueString.length() == 8 ) {
						pieces = new String[3];
						pieces[0] = valueString.substring(0, 4);
						pieces[1] = valueString.substring(4, 6);
						pieces[2] = valueString.substring(6);
					}
					else
						throw new Exception();
				}
				year = Integer.valueOf(pieces[0]);
				month = Integer.valueOf(pieces[1]);
				day = Integer.valueOf(pieces[2]);
			} catch ( Exception ex ) {
				year = -1;
				month = -1;
				day = -1;
			}
		}
		else if ( "mm-dd-yyyy".equalsIgnoreCase(fromUnit) ) {
			try {
				String[] pieces = DATE_SPLIT_PATTERN.split(valueString, 0);
				if ( pieces.length != 3 ) {
					if ( valueString.length() == 8 ) {
						pieces = new String[3];
						pieces[0] = valueString.substring(0, 2);
						pieces[1] = valueString.substring(2, 4);
						pieces[2] = valueString.substring(4);
					}
					else
						throw new Exception();
				}
				month = Integer.valueOf(pieces[0]);
				day = Integer.valueOf(pieces[1]);
				year = Integer.valueOf(pieces[2]);
			} catch ( Exception ex ) {
				month = -1;
				day = -1;
				year = -1;
			}
		}
		else if ( "dd-mm-yyyy".equalsIgnoreCase(fromUnit) ) {
			try {
				String[] pieces = DATE_SPLIT_PATTERN.split(valueString, 0);
				if ( pieces.length != 3 ) {
					if ( valueString.length() == 8 ) {
						pieces = new String[3];
						pieces[0] = valueString.substring(0, 2);
						pieces[1] = valueString.substring(2, 4);
						pieces[2] = valueString.substring(4);
					}
					else
						throw new Exception();
				}
				day = Integer.valueOf(pieces[0]);
				month = Integer.valueOf(pieces[1]);
				year = Integer.valueOf(pieces[2]);
			} catch ( Exception ex ) {
				day = -1;
				month = -1;
				year = -1;
			}
		}
		else if ( "dd-mon-yyyy".equalsIgnoreCase(fromUnit) ) {
			// TODO:
			throw new IllegalArgumentException("not yet implemented");
		}
		else if ( "mon-dd-yyyy".equalsIgnoreCase(fromUnit) ) {
			// TODO:
			throw new IllegalArgumentException("not yet implemented");
		}
		else if ( "mm-dd-yy".equalsIgnoreCase(fromUnit) ) {
			try {
				String[] pieces = DATE_SPLIT_PATTERN.split(valueString, 0);
				if ( pieces.length != 3 ) {
					if ( valueString.length() == 6 ) {
						pieces = new String[3];
						pieces[0] = valueString.substring(0, 2);
						pieces[1] = valueString.substring(2, 4);
						pieces[2] = valueString.substring(4);
					}
					else {
						throw new Exception();
					}
				}
				month = Integer.valueOf(pieces[0]);
				day = Integer.valueOf(pieces[1]);
				year = Integer.valueOf(pieces[2]);
				int century = CURRENT_YEAR / 100;
				year += century * 100;
				if ( year > CURRENT_YEAR )
					year -= 100;
			} catch ( Exception ex ) {
				day = -1;
				month = -1;
				year = -1;
			}
		}
		else if ( "dd-mm-yy".equalsIgnoreCase(fromUnit) ) {
			try {
				String[] pieces = DATE_SPLIT_PATTERN.split(valueString, 0);
				if ( pieces.length != 3 ) {
					if ( valueString.length() == 6 ) {
						pieces = new String[3];
						pieces[0] = valueString.substring(0, 2);
						pieces[1] = valueString.substring(2, 4);
						pieces[2] = valueString.substring(4);
					}
					else {
						throw new Exception();
					}
				}
				day = Integer.valueOf(pieces[0]);
				month = Integer.valueOf(pieces[1]);
				year = Integer.valueOf(pieces[2]);
				int century = CURRENT_YEAR / 100;
				year += century * 100;
				if ( year > CURRENT_YEAR )
					year -= 100;
			} catch ( Exception ex ) {
				day = -1;
				month = -1;
				year = -1;
			}
		}
		else if ( "dd-mon-yy".equalsIgnoreCase(fromUnit) ) {
			// TODO:
			throw new IllegalArgumentException("not yet implemented");
		}
		else if ( "mon-dd-yy".equalsIgnoreCase(fromUnit) ) {
			// TODO:
			throw new IllegalArgumentException("not yet implemented");
		}
		else {
			throw new IllegalArgumentException("conversion from \"" + fromUnit + "\" is not supported");
		}
		if ( (year == null) || (year < 1900) || (year > CURRENT_YEAR + 1) || 
			 (month == null) || (month < 1) || (month > 12) ||
			 (day == null) || (day < 1) || (day > 31) )
			throw new IllegalArgumentException("invalid date value");
		try {
			STD_CALENDAR.clear();
			STD_CALENDAR.set(year, GregorianCalendar.JANUARY + month - 1, day, 12, 0, 0);
			STD_CALENDAR.getTime();
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("invalid date value");
		}
		String stdVal = String.format("%04d-%02d-%02d", year, month, day);		
		return stdVal;
	}

	/**
	 * Standardized a time string to HH:mm:ss.SSS
	 * 
	 * @param valueString
	 * 		time string in the fromUnit format
	 * @return
	 * 		standardized time string
	 * @throws IllegalArgumentException
	 * 		if the fromUnit format is not recognized, or
	 * 		if the value is not a valid time string
	 */
	private String standardizeTime(String valueString) throws IllegalArgumentException {
		Integer hour;
		Integer minute;
		Double second;
		if ( "hh:mm:ss".equalsIgnoreCase(fromUnit) ) {
			try {
				String[] pieces = TIME_SPLIT_PATTERN.split(valueString,0);
				if ( (pieces.length < 2) || (pieces.length > 3) ) {
					int idx = valueString.indexOf('.');
					if ( (idx == 6) || ((idx < 0) && (valueString.length() >= 6)) ) {
						pieces = new String[3];
						pieces[0] = valueString.substring(0, 2);
						pieces[1] = valueString.substring(2, 4);
						pieces[2] = valueString.substring(4);
					}
					else if ( (idx < 0) && (valueString.length() == 4) ) {
						pieces = new String[2];
						pieces[0] = valueString.substring(0, 2);
						pieces[1] = valueString.substring(2, 4);
					}
					else
						throw new Exception();
				}
				hour = Integer.valueOf(pieces[0]);
				minute = Integer.valueOf(pieces[1]);
				if ( pieces.length == 3 ) {
					second = Double.valueOf(pieces[2]);
				}
				else {
					second = 0.0;
				}
			} catch ( Exception ex ) {
				hour = -1;
				minute = -1;
				second = -1.0;
			}
		}
		else {
			throw new IllegalArgumentException("conversion from \"" + fromUnit + "\" is not supported");
		}
		if ( (hour == null) || (hour < 0) || (hour >= 24) || 
			 (minute == null) || (minute < 0) || (minute >= 60) || 
			 (second == null) || second.isNaN() || (second < 0.0) || (second >= 60.0) )
			throw new IllegalArgumentException("invalid time value");
		String stdVal = String.format("%02d:%02d:%05.3f", hour, minute, second);
		return stdVal;
	}

}
