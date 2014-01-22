package uk.ac.uea.socat.sanitychecker.data.datetime;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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
 * We support three formats of date/time string:
 * 
 * &lt;Date&gt;HHMMSS
 * &lt;Date&gt; HHMMSS
 * &lt;Date&gt; HH:MM:SS
 * 
 * Any date/time string will be parsed against the three possible formats above.
 * 
 */
@SuppressWarnings("deprecation")
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
	private DateTimeFormatter itsDateOnlyFormatter = null;
	
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
		String adjustedDateString = validateDateFormatString(dateFormat);
		itsDateOnlyFormatter = DateTimeFormat.forPattern(adjustedDateString);
		
		itsDateTimeFormatters = new ArrayList<DateTimeFormatter>(5);
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(adjustedDateString + "HHmmss.SSS").withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(adjustedDateString + "HHmmss").withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(adjustedDateString + " HH:mm:ss.SSS").withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(adjustedDateString + " HH:mm:ss").withPivotYear(PIVOT_YEAR));
		itsDateTimeFormatters.add(DateTimeFormat.forPattern(adjustedDateString + " HH:mm").withPivotYear(PIVOT_YEAR));
		
		itsOutputDateFormatter = DateTimeFormat.forPattern(DATE_OUTPUT_FORMAT).withPivotYear(PIVOT_YEAR);
		itsOutputDateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_OUTPUT_FORMAT).withPivotYear(PIVOT_YEAR);
	}
	
	/**
	 * Ensures that a supplied date format string complies with
	 * the requirements of this program and the JODA data library.
	 * 
	 * Specifically, it must contain capital YMD letters, and no others.
	 * Allowed separators are ' -/'. Any other character will throw an exception.
	 * 
	 * @param format The supplied format
	 * @return The format adjusted for use with the JODA library
	 * @throws DateTimeException If any parts of the supplied format string are not supported
	 */
	private String validateDateFormatString(String format) throws DateTimeException {

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
		    case ' ': case '/': case ':': case '-':
		    {
		    	outputFormat.append(format.charAt(i));
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
	 * Worker method for parseDate methods.
	 * @param date The date string
	 * @param formatter The formatter to use
	 * @return The date object
	 * @throws DateTimeException If the string does not conform to the expected format
	 */
	private DateMidnight parseDateWorker(String date, DateTimeFormatter formatter) throws DateTimeParseException {
		
		DateMidnight parsedDate;
		
		try {
			DateTime tempParsedDate = formatter.parseDateTime(date);
			parsedDate = new DateMidnight(tempParsedDate.getYear(), tempParsedDate.getMonthOfYear(), tempParsedDate.getDayOfMonth());
		} catch (IllegalArgumentException e) {
			throw new DateTimeParseException("Unable to parse date string '" + date + "': " + e.getMessage());
		}
		
		return parsedDate;
	}
	
	/**
	 * Parse a date string into a date object
	 * @param date The date string
	 * @return The date object
	 * @throws DateTimeException If the string does not conform to the expected format
	 */
	public DateMidnight parseDate(String date) throws DateTimeParseException {
		return parseDateWorker(date, itsDateOnlyFormatter);
	}
	
	/**
	 * Parse a date-time string into a DateTime object. The date must be in the format
	 * supplied in the input data to the sanity checker. The string as a whole must be in
	 * one of the following formats: 

	 * &lt;Date&gt;HHMMSS
	 * &lt;Date&gt; HHMMSS
	 * &lt;Date&gt; HH:MM:SS
	 * &lt;Date&gt; HH:MM:SS.S
	 * 
	 * @param dateTime The string to be parsed
	 * @return The parsed date-time object
	 * @throws DateTimeException If the string cannot be parsed.
	 */
	public DateTime parseDateTime(String dateTime) throws DateTimeParseException {
		
		DateTime parsedDateTime = null;

		for (DateTimeFormatter formatter : itsDateTimeFormatters) {
			// If the string has been successfully parsed, we don't need to keep trying
			if (null != parsedDateTime) {
				break;
			}
			
			try {
				parsedDateTime = formatter.parseDateTime(dateTime);
			} catch (IllegalArgumentException e) {
				; // Do nothing - we will try one of the other formatters
			}
		}

		if (null == parsedDateTime) {
			throw new DateTimeParseException("Unable to parse date-time string '" + dateTime + "': It is not in a supported format");
		}
		
		return parsedDateTime;
	}
	
	/**
	 * Output a date object formatted as a YYYYMMDD string
	 * @param date The date to be formatted
	 * @return The formatted date
	 */
	public String formatDate(BaseDateTime date) {
		return itsOutputDateFormatter.print(date);
	}

	/**
	 * Output a date/time object as a string (YYYYMMDDhhmmss)
	 * @param date The date to be formatted
	 * @return The formatted date/time
	 */
	public String formatDateTime(BaseDateTime date) {
		return itsOutputDateTimeFormatter.print(date);
	}
}
