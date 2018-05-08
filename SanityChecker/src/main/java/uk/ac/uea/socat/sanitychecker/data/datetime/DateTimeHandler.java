package uk.ac.uea.socat.sanitychecker.data.datetime;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * A utility class for parsing dates and times from strings.
 * 
 * The date format is defined for each input file (and therefore each run of the
 * sanity checker).
 * 
 * Date format strings must contain Ys, Ms, Ds, and maybe some of ' /-'.
 * Any other characters will be rejected.
 * 
 * We support two formats of date/time string:
 * 
 * &lt;Date&gt; HHMMSS
 * &lt;Date&gt; HH:MM:SS
 * 
 * Any date/time string will be parsed against the three possible formats above.
 * 
 */
public class DateTimeHandler {
	
	/**
	 * The final year in which the Sanity Checker can run without adjustments
	 * being required for handling two-digit years.
	 */
	public static final int FINAL_RUN_YEAR = 2048;
	
	/**
	 * The pivot year used for two digit years. This gives us a range of
	 * 1950-2049. The program will be written so that it won't run after 31st Dec 2048.
	 */
	public static final Integer PIVOT_YEAR = Integer.valueOf(2000);
	
	/**
	 * The output format for dates
	 */
	private static final String DATE_OUTPUT_FORMAT = "YYYYMMdd";
	
	/**
	 * The output format for dates and times
	 */
	private static final String DATE_TIME_OUTPUT_FORMAT = "YYYYMMddHHmmss.SSS";
	
	/**
	 * The formatter used to parse dates from input data and metadata
	 */
	private List<DateTimeFormatter> itsDateOnlyFormatters = null;
	
	/**
	 * The set of possible date-time formats that can be used to parse dates and times
	 * from input data and metadata
	 * See the main class comment for details.
	 */
	private List<DateTimeFormatter> itsDateTimeFormatters = null;
	
	/**
	 * The formatter used for output dates
	 */
	private DateTimeFormatter itsOutputDateFormatter = null;
	
	/**
	 * The formatter used for output date/times
	 */
	private DateTimeFormatter itsOutputDateTimeFormatter = null;
	
	/**
	 * Initialises all the date formats
	 */
	public DateTimeHandler(String dateFormat) throws DateTimeException {
		itsDateOnlyFormatters = new ArrayList<DateTimeFormatter>(3);
		String hyphenDateString = validateDateFormatString(dateFormat, '-');
		itsDateOnlyFormatters.add(DateTimeFormat.forPattern(hyphenDateString).withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		String slashDateString = validateDateFormatString(dateFormat, '/');
		itsDateOnlyFormatters.add(DateTimeFormat.forPattern(slashDateString).withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		String dotDateString = validateDateFormatString(dateFormat, '.');
		itsDateOnlyFormatters.add(DateTimeFormat.forPattern(dotDateString).withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		String noSepDateString = validateDateFormatString(dateFormat, null);
		itsDateOnlyFormatters.add(DateTimeFormat.forPattern(noSepDateString).withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));

		// The date-time formats can come from the sanity checker combining separate date and time fields
		itsDateTimeFormatters = new ArrayList<DateTimeFormatter>(21);
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(hyphenDateString + " HH:mm:ss.SSS").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(hyphenDateString + " HH:mm:ss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(hyphenDateString + " HH:mm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(hyphenDateString + " HHmmss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(hyphenDateString + " HHmm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(slashDateString + " HH:mm:ss.SSS").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(slashDateString + " HH:mm:ss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(slashDateString + " HH:mm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(slashDateString + " HHmmss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(slashDateString + " HHmm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(dotDateString + " HH:mm:ss.SSS").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(dotDateString + " HH:mm:ss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(dotDateString + " HH:mm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(dotDateString + " HHmmss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(dotDateString + " HHmm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(noSepDateString + " HH:mm:ss.SSS").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(noSepDateString + " HH:mm:ss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(noSepDateString + " HH:mm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(noSepDateString + " HHmmss").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(noSepDateString + " HHmm").withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR));

		itsOutputDateFormatter = DateTimeFormat.forPattern(DATE_OUTPUT_FORMAT).withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR);
		itsOutputDateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_OUTPUT_FORMAT).withZone(DateTimeZone.UTC).withPivotYear(PIVOT_YEAR);
	}
	
	/**
	 * Ensures that a supplied date format string complies with
	 * the requirements of this program and the JODA data library.
	 * 
	 * Specifically, it must contain only 'y', 'Y', 'm', 'M', 'd', 'D', '-', '/', and '.' characters.
	 * Any other character will throw an exception.
	 * 
	 * @param format The supplied format
	 * @param sep replace any separators with this character; if null, separators are removed
	 * @return The format adjusted for use with the JODA library
	 * @throws DateTimeException If any parts of the supplied format string are not supported
	 */
	private String validateDateFormatString(String format, Character sep) throws DateTimeException {

		StringBuffer outputFormat = new StringBuffer();
		
		for (int i = 0; i < format.length(); i++) {
		    switch (format.charAt(i)) {
		    case 'y': case 'Y':
		    {
		    	outputFormat.append('Y');
		    	break;
		    }
		    case 'm': case 'M':
		    {
		    	outputFormat.append('M');
		    	break;
		    }
		    case 'd': case 'D':
		    {
		    	outputFormat.append('d');
		    	break;
		    }
		    case '/': case '-': case '.':
		    {
		    	if ( sep != null )
		    		outputFormat.append(sep);
		    	break;
		    }
		    default:
		    {
		    	throw new DateTimeException("Unsupported character in date format string: '" + format.charAt(i) + "'");
		    }
		    }
		}
		
		return outputFormat.toString();
	}
	
	/**
	 * Parse a date string into a date object
	 * @param date The date string
	 * @return The date object
	 * @throws DateTimeException If the string does not conform to one of the expected formats
	 */
	public DateTime parseDate(String date) throws DateTimeParseException {
		
		DateTime parsedDate = null;

		for (DateTimeFormatter formatter : itsDateOnlyFormatters) {
			try {
				DateTime tempParsedDate = formatter.parseDateTime(date);
				parsedDate = tempParsedDate.withTimeAtStartOfDay();
				break;
			} catch (IllegalArgumentException e) {
				continue;
			}
		}
		if (null == parsedDate)
			throw new DateTimeParseException("Unable to parse date string '" + 
					parsedDate + "': It is not in a supported format");
		return parsedDate;
	}
	
	/**
	 * Parse a date-time string into a DateTime object. The date must be in the format
	 * supplied in the input data to the sanity checker. The string as a whole must be in
	 * one of the following formats: 
	 * <pre>
	 * &lt;Date&gt; HH:MM:SS.S
	 * &lt;Date&gt; HH:MM:SS
	 * &lt;Date&gt; HH:MM
	 * &lt;Date&gt; HHMMSS
	 * &lt;Date&gt; HHMM
	 * </pre>
	 * where &lt;Date&gt; is any of:
	 * <pre>
	 * the date format string with hyphen separators 
	 * the date format string with slash separators 
	 * the date format string without separators
	 * </pre>
	 * 
	 * @param dateTime The string to be parsed
	 * @return The parsed date-time object
	 * @throws DateTimeException If the string cannot be parsed.
	 */
	public DateTime parseDateTime(String dateTime) throws DateTimeParseException {
		
		DateTime parsedDateTime = null;

		for (DateTimeFormatter formatter : itsDateTimeFormatters) {
			try {
				parsedDateTime = formatter.parseDateTime(dateTime);
				break;
			} catch (IllegalArgumentException e) {
				continue;
			}
		}

		if (null == parsedDateTime) {
			throw new DateTimeParseException("Unable to parse date-time string '" + 
					dateTime + "': It is not in a supported format");
		}
		
		return parsedDateTime;
	}
	
	/**
	 * Output a date object formatted as a YYYY-MM-DD string
	 * @param date The date to be formatted
	 * @return The formatted date
	 */
	public String formatDate(BaseDateTime date) {
		return itsOutputDateFormatter.print(date);
	}

	/**
	 * Output a date/time object as a string (YYYY-MM-DD HH:mm:ss)
	 * @param date The date to be formatted
	 * @return The formatted date/time
	 */
	public String formatDateTime(BaseDateTime date) {
		return itsOutputDateTimeFormatter.print(date);
	}
}
