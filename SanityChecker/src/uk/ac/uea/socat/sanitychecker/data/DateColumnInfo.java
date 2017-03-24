package uk.ac.uea.socat.sanitychecker.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeParseException;
import uk.ac.uea.socat.sanitychecker.data.datetime.MissingDateTimeElementException;

/**
 * An object to hold details of the date columns in an input file
 */
public class DateColumnInfo {

	public static final int MINIMUM_YEAR = 1900;
	
	/**
	 * Indicates that a single column contains both the date and time
	 */
	public static final int SINGLE_ELEMENT_TYPE = 1;

	/**
	 * Indicates that two columns are used for the date and time
	 */
	public static final int DATE_TIME_ELEMENT_TYPE = 2;
	
	/**
	 * Indicates that separate elements are used for each element of the date and time
	 */
	public static final int INDIVIDUAL_ELEMENTS_TYPE = 3;
	
	/**
	 * Indicates that the date is specified as year, day, second
	 */
	public static final int YEAR_DAY_SECOND_TYPE = 4;

	/**
	 * Indicates that the date is specified as year, month, day, time
	 */
	public static final int DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE = 5;
	
	/**
	 * Indicates that the date is specified as year, decimal jdate
	 */
	public static final int YEAR_DECIMAL_JDATE_TYPE = 6;
	
	/**
	 * Indicates which type of date column setup is being used
	 */
	private int itsElementType;

	/**
	 * Column info for a combined date/time element
	 */
	private ColInfo itsSingleElementInfo = null;
	
	/**
	 * Column info for a separate date element
	 */
	private ColInfo itsDateInfo = null;
	
	/**
	 * Column info for a separate time element
	 */
	private ColInfo itsTimeInfo = null;
	
	/**
	 * Column info for a separate year element
	 */
	private ColInfo itsYearInfo = null;
	
	/**
	 * Column info for a separate month element
	 */
	private ColInfo itsMonthInfo = null;
	
	/**
	 * Column info for a separate day element
	 */
	private ColInfo itsDayInfo = null;
	
	/**
	 * Column info for a separate hour element
	 */
	private ColInfo itsHourInfo = null;
	
	/**
	 * Column info for a separate minute element
	 */
	private ColInfo itsMinuteInfo = null;
	
	/**
	 * Column info for a separate second element
	 */
	private ColInfo itsSecondInfo = null;
	
	/**
	 * Indicates which day index to use as 1st January
	 */
	private int itsJanFirstIndex = 0;
	
	/**
	 * Defines the various sets of date XML structures that are supported, with the names of the methods that handle them.
	 */
	private String[][] dateElementStructures = new String[][] {
			{"processSingleElement", ColumnSpec.SINGLE_DATE_TIME_ELEMENT},
			{"processDateTimeElements", ColumnSpec.DATE_ELEMENT, ColumnSpec.TIME_ELEMENT},
			{"processIndividualColumnElements", ColumnSpec.YEAR_ELEMENT, ColumnSpec.MONTH_ELEMENT, ColumnSpec.DAY_ELEMENT, ColumnSpec.HOUR_ELEMENT, ColumnSpec.MINUTE_ELEMENT, ColumnSpec.SECOND_ELEMENT},
			{"processIndividualColumnElements", ColumnSpec.YEAR_ELEMENT, ColumnSpec.MONTH_ELEMENT, ColumnSpec.DAY_ELEMENT, ColumnSpec.HOUR_ELEMENT, ColumnSpec.MINUTE_ELEMENT},
			{"processYearDaySecondElements", ColumnSpec.YDS_YEAR_ELEMENT, ColumnSpec.YDS_DAY_ELEMENT, ColumnSpec.YDS_SECOND_ELEMENT, ColumnSpec.JAN_FIRST_INDEX_ELEMENT},
			{"processYearJDateElements", ColumnSpec.YDJD_YEAR_ELEMENT, ColumnSpec.YDJD_DECIMAL_JDATE_ELEMENT, ColumnSpec.YDJD_JAN_FIRST_INDEX_ELEMENT},
			{"processYearColsSingleTimeElements", ColumnSpec.YMDT_YEAR_ELEMENT, ColumnSpec.YMDT_MONTH_ELEMENT, ColumnSpec.YMDT_DAY_ELEMENT, ColumnSpec.YMDT_TIME_ELEMENT}	
	};
	
	/**
	 * Takes an XML column spec date element and parses it.
	 * @param dateElement The date element.
	 * @param logger The program logger
	 */
	public DateColumnInfo(Element dateElement, Logger logger) throws Exception {
		processDateElement(dateElement, logger);
	}
	
	/**
	 * Process the date XML element into a format that can be used by the logic
	 * for the data conversion. Note that the XML has already been validated so we
	 * don't need to do it here.
	 * 
	 * @param dateElement The date XML element
	 */
	private void processDateElement(Element dateElement, Logger logger) throws Exception {
		
		boolean matchedDateElementStructure = false;
		
		for (int i = 0; i < dateElementStructures.length; i++) {
			
			String[] structure = dateElementStructures[i];
			if (elementMatchesStructure(dateElement, structure)) {
				
				Method processorMethod = this.getClass().getDeclaredMethod(structure[0], Element.class, Logger.class);
				processorMethod.invoke(this, dateElement, logger);
				
				matchedDateElementStructure = true;
			}
			
		}
		
		if (!matchedDateElementStructure) {
			// Something has gone horrifically wrong. ABORT!!!
			throw new Exception("FATAL ERROR: Bad element sequence in date column specification");
		}
	}
	
	/**
	 * Determines whether or not the structure of a given Date XML element (from the
	 * per-file column configuration XML) matches a given structure as defined in
	 * {@link DateColumnInfo#dateElementStructures}.
	 * @param dateElement The date XML element
	 * @param structure The structure against which the element will be tested
	 * @return {@code true} if the XML element matches the element structure's configuration; {@code false} otherwise
	 */
	private boolean elementMatchesStructure(Element dateElement, String[] structure) {
		boolean matches = true;
		
		List<Element> children = dateElement.getChildren();
		if (children.size() != (structure.length - 1)) {
			matches = false;
		} else {
			int count = 0;
			for (Element child : children) {
				count++;
				if (!child.getName().equalsIgnoreCase(structure[count])) {
					matches = false;
				}
			}
		}
		
		return matches;
	}
	
	/**
	 * Build a {@link DateTime} object from a set of date fields.
	 * The contents of the fields are converted into a date/time string that can be parsed
	 * by the supplied {@link DateTimeHandler}.
	 * @param dataFields The set of date fields
	 * @param dateTimeHandler The utility object for handling dates and times
	 * @return The constructed {@link DateTime} object
	 * @throws DateTimeException If the date fields could not be parsed into a {@link DateTime} object.
	 */
	public DateTime makeDateTime(List<String> dataFields, DateTimeHandler dateTimeHandler) throws DateTimeException {

		DateTime result = null;

		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE: {
			
			String value = dataFields.get(itsSingleElementInfo.index - 1);
			
			if (CheckerUtils.isEmpty(value)) {
				throw new MissingDateTimeElementException();
			} else {
				result = dateTimeHandler.parseDateTime(value);
			}
			
			break;
		}
		
		case DATE_TIME_ELEMENT_TYPE: {
			
			String dateValue = dataFields.get(itsDateInfo.index - 1);
			String timeValue = dataFields.get(itsTimeInfo.index - 1);
			
			if (CheckerUtils.isEmpty(dateValue) || CheckerUtils.isEmpty(timeValue)) {
				throw new MissingDateTimeElementException();
			} else {
				String dateTimeString = dateValue + " " + timeValue;
				result = dateTimeHandler.parseDateTime(dateTimeString);
			}
			
			break;
		}
		
		case DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE: {
			String yearString = dataFields.get(getYearColumnIndex() - 1);
			String monthString = dataFields.get(getMonthColumnIndex() - 1);
			String dayString = dataFields.get(getDayColumnIndex() - 1);
			String timeString = dataFields.get(itsTimeInfo.index - 1);
			
			if (CheckerUtils.isEmpty(yearString, monthString, dayString, timeString)) {
				throw new MissingDateTimeElementException();
			} else {
				try {
					DateTimeHandler handler = new DateTimeHandler("YYYYMMDD");
					int month = Integer.parseInt(monthString);
					int day = Integer.parseInt(dayString);
					String fullDateTimeString = yearString + String.format("%02d", month) + String.format("%02d", day) + " " + timeString;
					result = handler.parseDateTime(fullDateTimeString);

				} catch (NumberFormatException e) {
					throw new DateTimeParseException("Invalid date " + yearString + "/" + monthString + "/" + dayString + " " + timeString);
				} catch (IllegalFieldValueException e) {
					throw new DateTimeParseException("Invalid date " + yearString + "/" + monthString + "/" + dayString + " " + timeString);
				} catch (Exception e) {
					throw new DateTimeParseException(e);
				}
			}
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			String yearString = dataFields.get(getYearColumnIndex() - 1);
			String monthString = dataFields.get(getMonthColumnIndex() - 1);
			String dayString = dataFields.get(getDayColumnIndex() - 1);
			String hourString = dataFields.get(getHourColumnIndex() - 1);
			String minuteString = dataFields.get(getMinuteColumnIndex() - 1);
			String secondString;
			int idx = getSecondColumnIndex();
			if ( idx > 0 )
				secondString = dataFields.get(idx - 1);
			else
				secondString = "0";
			
			if (CheckerUtils.isEmpty(yearString, monthString, dayString, hourString, minuteString, secondString)) {
				throw new MissingDateTimeElementException();
			} else {
				
				try {
					int year = Integer.parseInt(yearString);
					int month = Integer.parseInt(monthString);
					int day = Integer.parseInt(dayString);
					int hour = Integer.parseInt(hourString);
					int minute = Integer.parseInt(minuteString);
					
					double second = Double.parseDouble(secondString);
					int wholeSecond = (int) Math.floor(second);
					int millisecond = (int) Math.floor((second - wholeSecond) * 1000);
					
				
					result = new DateTime(year, month, day, hour, minute, wholeSecond, millisecond, DateTimeZone.UTC);
				} catch (NumberFormatException e) {
					throw new DateTimeParseException("Invalid date " + yearString + "/" + monthString + "/" + dayString + " " + hourString + ":" + minuteString + ":" + secondString);
				} catch (IllegalFieldValueException e) {
					throw new DateTimeParseException("Invalid date " + yearString + "/" + monthString + "/" + dayString + " " + hourString + ":" + minuteString + ":" + secondString);
				} catch (Exception e) {
					throw new DateTimeParseException(e);
				}
			}
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			
			String yearString = dataFields.get(getYearColumnIndex() - 1);
			String dayString = dataFields.get(getDayColumnIndex() - 1);
			String secondString = dataFields.get(getSecondColumnIndex() - 1);
			
			String invalidDateMessage = "Invalid date " + yearString + " " + dayString + " " + secondString;
			
			if (CheckerUtils.isEmpty(yearString, dayString, secondString)) {
				throw new MissingDateTimeElementException();
			} else {
				try {
					int year = Integer.parseInt(yearString);
					int day = Integer.parseInt(dayString);
					int second = Integer.parseInt(secondString);

					// Set the 1st Jan to day zero
					day = day - itsJanFirstIndex;
					
					// Build the date, checking values as we go
					DateTime calculationDate = new DateTime(year, 1, 1, 0, 0, 0, DateTimeZone.UTC);
					
					if (day < 0) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (calculationDate.year().isLeap() && day > 366) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (day > 365) {
						throw new DateTimeParseException(invalidDateMessage);
					}
					
					calculationDate = calculationDate.plusDays(day);
					
					if (second < 0) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (second > 86400) {
						throw new DateTimeParseException(invalidDateMessage);
					}
					
					calculationDate = calculationDate.plusSeconds(second);
					
					result = calculationDate;
					
					
				} catch (NumberFormatException e) {
					throw new DateTimeParseException(invalidDateMessage);
				} catch (IllegalFieldValueException e) {
					throw new DateTimeParseException(invalidDateMessage);
				} catch (Exception e) {
					throw new DateTimeParseException(e);
				}
			}
			
			break;
		}
		case YEAR_DECIMAL_JDATE_TYPE: {
			
			String yearString = dataFields.get(itsYearInfo.index - 1);
			String jdateString = dataFields.get(itsDayInfo.index - 1);
			
			String invalidDateMessage = "Invalid date " + yearString + " " + jdateString;
			
			if (CheckerUtils.isEmpty(yearString, jdateString)) {
				throw new MissingDateTimeElementException();
			} else {
				try {
					
					int year = Integer.parseInt(yearString);
					double jdate = Double.parseDouble(jdateString);
					jdate = jdate - (double) itsJanFirstIndex;
					
					// Create a new date at Jan 1st in the specified year
					DateTime calculationDate = new DateTime(year, 1, 1, 0, 0, 0, DateTimeZone.UTC);

					if (jdate < 0) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (calculationDate.year().isLeap() && jdate > 366) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (jdate > 365) {
						throw new DateTimeParseException(invalidDateMessage);
					}
					
					/*
					 * The jdate is a double in <day>.<dayFraction> format
					 * 
					 * The day is therefore just the integer floor value
					 */
					int days = (int) Math.floor(jdate);
					
					/*
					 * The number of seconds is the dayFraction * 86400 (number of seconds in a day)
					 * This gives a double of the form <seconds>.<secondsFraction>
					 * 
					 * So the seconds is the integer floor value
					 */
					double dayFraction = jdate - (double) days;
					int seconds = (int) Math.floor(dayFraction * 86400);
					
					/*
					 * And the milliseconds will be the first three digits after
					 * the decimal place
					 */
					double secondsFraction = dayFraction - ((double)seconds / 86400.0);
					int milliseconds = (int) Math.floor(secondsFraction * 1000.0);
					
					// Now add all the parts to the year
					calculationDate = calculationDate.plusDays(days);
					calculationDate = calculationDate.plusSeconds(seconds);
					calculationDate = calculationDate.plusMillis(milliseconds);
					
					result = calculationDate;
					
				} catch (NumberFormatException e) {
					throw new DateTimeParseException(invalidDateMessage);
				} catch (IllegalFieldValueException e) {
					throw new DateTimeParseException(invalidDateMessage);
				} catch (Exception e) {
					throw new DateTimeParseException(e);
				}
			}

			break;
		}
		}
		
		if (null == result) {
			throw new DateTimeException("Failed to parse date for unknown reason");
		} else if (result.getYear() < MINIMUM_YEAR) {
			throw new DateTimeParseException("Date is before " + MINIMUM_YEAR);
		} else {
			DateTime now = new DateTime(DateTimeZone.UTC);
			if (result.isAfter(now)) {
				throw new DateTimeParseException("Date is in the future");
			}
		}
		
		return result;
	}
	
	/**
	 * Process a combined date/time column specification
	 * @param parent The parent element
	 * @param logger A Logger instance
	 */
	@SuppressWarnings("unused")
	private void processSingleElement(Element parent, Logger logger) {
		itsElementType = SINGLE_ELEMENT_TYPE;
		
		Element child = parent.getChild(ColumnSpec.SINGLE_DATE_TIME_ELEMENT, parent.getNamespace());
		int columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		String columnName = child.getTextTrim();
		logger.trace("Date column spec: Single column '" + columnName + "' (" + columnIndex + ")");
		itsSingleElementInfo = new ColInfo(columnIndex, columnName);
	}
	
	/**
	 * Process separate date and time column specifications
	 * @param parent The parent element
	 * @param logger A Logger instance
	 */
	@SuppressWarnings("unused")
	private void processDateTimeElements(Element parent, Logger logger) {
		itsElementType = DATE_TIME_ELEMENT_TYPE;

		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.DATE_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Date column '" + columnName + "' (" + columnIndex + ")");
		itsDateInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.TIME_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Time column '" + columnName + "' (" + columnIndex + ")");
		itsTimeInfo = new ColInfo(columnIndex, columnName);
	}
	
	
	/**
	 * Process a column specification of Year/Day of Year/Second
	 * @param parent The parent element
	 * @param logger A Logger instance
	 */
	@SuppressWarnings("unused")
	private void processYearDaySecondElements(Element parent, Logger logger) {
		itsElementType = YEAR_DAY_SECOND_TYPE;
		
		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.YDS_YEAR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Year column '" + columnName + "' (" + columnIndex + ")");
		itsYearInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YDS_DAY_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Day of Year column '" + columnName + "' (" + columnIndex + ")");
		itsDayInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YDS_SECOND_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Second of Day column '" + columnName + "' (" + columnIndex + ")");
		itsSecondInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.JAN_FIRST_INDEX_ELEMENT, parent.getNamespace());
		itsJanFirstIndex = Integer.parseInt(child.getTextTrim());
	}
	
	/**
	 * Process a column specification of Year/Decimal JDate
	 * @param parent The parent element
	 * @param logger A Logger instance
	 */
	@SuppressWarnings("unused")
	private void processYearJDateElements(Element parent, Logger logger) {
		itsElementType = YEAR_DECIMAL_JDATE_TYPE;
		
		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.YDJD_YEAR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Year column '" + columnName + "' (" + columnIndex + ")");
		itsYearInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YDJD_DECIMAL_JDATE_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Decimal JDate column '" + columnName + "' (" + columnIndex + ")");
		itsDayInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YDJD_JAN_FIRST_INDEX_ELEMENT, parent.getNamespace());
		itsJanFirstIndex = Integer.parseInt(child.getTextTrim());
	}
	
	/**
	 * Process a column specification of Year/Month/Day/Time elements
	 * @param parent The parent element
	 * @param logger A Logger instance
	 */
	@SuppressWarnings("unused")
	private void processYearColsSingleTimeElements(Element parent, Logger logger) {
		itsElementType = DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE;

		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.YMDT_YEAR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Year column '" + columnName + "' (" + columnIndex + ")");
		itsYearInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YMDT_MONTH_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Month column '" + columnName + "' (" + columnIndex + ")");
		itsMonthInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YMDT_DAY_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Day column '" + columnName + "' (" + columnIndex + ")");
		itsDayInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YMDT_TIME_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Time column '" + columnName + "' (" + columnIndex + ")");
		itsTimeInfo = new ColInfo(columnIndex, columnName);
	}
	
	/**
	 * Process individual date and time component column specifications
	 * @param parent The parent element
	 */
	@SuppressWarnings("unused")
	private void processIndividualColumnElements(Element parent, Logger logger) {
		itsElementType = INDIVIDUAL_ELEMENTS_TYPE;

		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.YEAR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Year column '" + columnName + "' (" + columnIndex + ")");
		itsYearInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.MONTH_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Month column '" + columnName + "' (" + columnIndex + ")");
		itsMonthInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.DAY_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Day column '" + columnName + "' (" + columnIndex + ")");
		itsDayInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.HOUR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Hour column '" + columnName + "' (" + columnIndex + ")");
		itsHourInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.MINUTE_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Minute column '" + columnName + "' (" + columnIndex + ")");
		itsMinuteInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.SECOND_ELEMENT, parent.getNamespace());
		if ( child != null ) {
			columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
			columnName = child.getTextTrim();
			logger.trace("Date column spec: Second column '" + columnName + "' (" + columnIndex + ")");
			itsSecondInfo = new ColInfo(columnIndex, columnName);
		}
		else {
			logger.trace("Date column spec: Second column not specified");
			itsSecondInfo = null;
		}
	}
	
	/**
	 * Returns the index of the column containing the year	
	 * @return The index of the column containing the year
	 */
	public int getYearColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsYearInfo.index;
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			result = itsYearInfo.index;
			break;
		}
		case DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE: {
			result = itsYearInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the month	
	 * @return The index of the column containing the month
	 */
	public int getMonthColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsMonthInfo.index;
			break;
		}
		case DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE: {
			result = itsMonthInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the day	
	 * @return The index of the column containing the day
	 */
	public int getDayColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsDayInfo.index;
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			result = itsDayInfo.index;
			break;
		}
		case DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE: {
			result = itsDayInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the hour	
	 * @return The index of the column containing the hour
	 */
	public int getHourColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsHourInfo.index;
			break;
		}
		case DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the minute	
	 * @return The index of the column containing the minute
	 */
	public int getMinuteColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsMinuteInfo.index;
			break;
		}
		case DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the second	
	 * @return The index of the column containing the second
	 */
	public int getSecondColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			if ( itsSecondInfo != null )
				result = itsSecondInfo.index;
			else
				result = -1;
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			result = itsSecondInfo.index;
			break;
		}
		case DATE_COLS_SINGLE_TIME_ELEMENTS_TYPE: {
			result = itsTimeInfo.index;
			break;
		}

		}
		
		return result;
	}
	
	/**
	 * Returns a list of all the date/time columns required in the data file.
	 * The list will vary according to the specified format of the date/time columns.
	 * @return The list of all required date/time columns.
	 */
	public List<String> getDateTimeInputColumns() {
		List<String> columnNames = new ArrayList<String>();

		switch(itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			columnNames.add(itsSingleElementInfo.name);
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			columnNames.add(itsDateInfo.name);
			columnNames.add(itsTimeInfo.name);
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			columnNames.add(itsYearInfo.name);
			columnNames.add(itsMonthInfo.name);
			columnNames.add(itsDayInfo.name);
			columnNames.add(itsHourInfo.name);
			columnNames.add(itsMinuteInfo.name);
			columnNames.add(itsSecondInfo.name);
		}
		}
		
		return columnNames;
	}
	
	/**
	 * Inner class to store the index and name of an input column.
	 * This is only used here, so we're not going to bother with
	 * get methods. Just grab the values direct.
	 */
	class ColInfo {
	
		/**
		 * The input column index
		 */
		private int index;
		
		/**
		 * The input column name
		 */
		private String name;
		
		/**
		 * Constructor
		 * @param columnIndex Column index
		 * @param columnName Column name
		 */
		private ColInfo(int columnIndex, String columnName) {
			index = columnIndex;
			name = columnName;
		}
	}
}

