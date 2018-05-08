package uk.ac.uea.socat.sanitychecker.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;

import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.data.conversion.ConversionException;

/**
 * A Java representation of the passed in column specification
 */
public class ColumnSpec {
	
	/**
	 * The name of an XML element containing standard column information
	 */
	public static final String SOCAT_COLUMN_ELEMENT = "socat_column";
	
	/**
	 * The name of an XML element containing date column information
	 */
	public static final String DATE_COLUMN_ELEMENT = "date_columns";
	
	/**
	 * The name of the attribute containing the SOCAT column name
	 */
	public static final String SOCAT_COLUMN_NAME_ATTRIBUTE = "name";
	
	/**
	 * The name of the element containing details of the input column
	 */
	public static final String INPUT_COLUMN_ELEMENT_NAME = "input_column";
	
	/**
	 * The name of the attribute containing the index of the input column
	 */
	public static final String INPUT_COLUMN_INDEX_ATTRIBUTE = "index";
	
	/**
	 * The name of the element containing the units of the input column
	 */
	public static final String INPUT_UNITS_ELEMENT_NAME = "input_units";
	
	/**
	 * The name of the element containing the (optional) missing value of the input column
	 */
	public static final String MISSING_VALUE_ELEMENT_NAME = "missing_value";
	
	/**
	 * The name of the element containing the details for a combined date and time column
	 */
	public static final String SINGLE_DATE_TIME_ELEMENT = "date_time";
	
	/**
	 * The name of the element containing the details for a separate date column
	 */
	public static final String DATE_ELEMENT = "date";
	
	/**
	 * The name of the element containing the details for a separate time column
	 */
	public static final String TIME_ELEMENT = "time";
	
	/**
	 * The name of the element containing the details for a separate year column
	 */
	public static final String YEAR_ELEMENT = "year";
	
	/**
	 * The name of the element containing the details for a separate month column
	 */
	public static final String MONTH_ELEMENT = "month";
	
	/**
	 * The name of the element containing the details for a separate day column
	 */
	public static final String DAY_ELEMENT = "day";
	
	/**
	 * The name of the element containing the details for a separate hour column
	 */
	public static final String HOUR_ELEMENT = "hour";
	
	/**
	 * The name of the element containing the details for a separate minute column
	 */
	public static final String MINUTE_ELEMENT = "minute";
	
	/**
	 * The name of the element containing the details for a separate second column
	 */
	public static final String SECOND_ELEMENT = "second";
	
	/**
	 * The name of the element containing the details for the year column in a year/day/second setup
	 */
	public static final String YDS_YEAR_ELEMENT = "yds_year";

	/**
	 * The name of the element containing the details for the day column in a year/day/second setup
	 */
	public static final String YDS_DAY_ELEMENT = "yds_day";

	/**
	 * The name of the element containing the details for the year column in a year/day/second setup
	 */
	public static final String YDS_SECOND_ELEMENT = "yds_second";
	
	/**
	 * The name of the element that states which number represents the 1st January
	 */
	public static final String JAN_FIRST_INDEX_ELEMENT = "jan_first_index";

	/**
	 * The name of the element containing the details for the year column in a year/jdate setup
	 */
	public static final String YDJD_YEAR_ELEMENT = "ydjd_year";
	
	/**
	 * The name of the element containing the details for the date column in a year/jdate setup
	 */
	public static final String YDJD_DECIMAL_JDATE_ELEMENT = "ydjd_decimal_jdate";

	/**
	 * The name of the element that states which number represents the 1st January
	 */
	public static final String YDJD_JAN_FIRST_INDEX_ELEMENT = "ydjd_jan_first_index";

	/**
	 * The name of the element containing the details for a separate year column
	 */
	public static final String YMDT_YEAR_ELEMENT = "ymdt_year";
	
	/**
	 * The name of the element containing the details for a separate month column
	 */
	public static final String YMDT_MONTH_ELEMENT = "ymdt_month";
	
	/**
	 * The name of the element containing the details for a separate day column
	 */
	public static final String YMDT_DAY_ELEMENT = "ymdt_day";
	
	/**
	 * The name of the element containing the details for a separate time column
	 */
	public static final String YMDT_TIME_ELEMENT = "ymdt_time";
	
	

	/**
	 * Lookup table of column specifications for non-date columns
	 */
	private HashMap<String, StandardColumnInfo> itsStandardColumnInfo;
	
	/**
	 * Column specifications for date columns
	 */
	private DateColumnInfo itsDateColumnInfo;
	
	/**
	 * Construct the ColumnSpec object from XML
	 * @param specFile The original XML file
	 * @param xml The parsed column spec XML
	 * @param dataConversionConfig The data conversion configuration
	 * @param logger The logger
	 * @throws InvalidColumnSpecException
	 */
	public ColumnSpec(File specFile, Document xml, ColumnConversionConfig dataConversionConfig, Logger logger) throws InvalidColumnSpecException {
		
		itsStandardColumnInfo = new HashMap<String, StandardColumnInfo>();
		
		Element root = xml.getRootElement();
		List<Element> columnElements = root.getChildren();
		
		try {
			Iterator<Element> iterator = columnElements.iterator();
			
			while (iterator.hasNext()) {
				Element element = iterator.next();
				String elementName = element.getName();
				if (elementName.equalsIgnoreCase(SOCAT_COLUMN_ELEMENT)) {
					StandardColumnInfo columnInfo = processStandardColumnElement(element, dataConversionConfig, logger);
					itsStandardColumnInfo.put(columnInfo.getSocatColumn(), columnInfo);
				} else if (elementName.equalsIgnoreCase(DATE_COLUMN_ELEMENT)) {
					itsDateColumnInfo = processDateColumnInfo(element, logger);
				}
			}
		} catch (Exception e) {
			throw new InvalidColumnSpecException(specFile, e.getMessage(), e);
		}
	}
	
	/**
	 * Build a column information object for a given {@code SOCAT_COLUMN} element in the
	 * column specification XML file.
	 * 
	 * @param element The element to be processed
	 * @param columnConversionConfig The configuration object detailing which data converters are to be used for different columns.
	 * @return An object describing a SOCAT output column, which input column corresponds to it, and how it should be converted.
	 */
	private StandardColumnInfo processStandardColumnElement(Element element, ColumnConversionConfig columnConversionConfig, Logger logger) throws ConversionException {
		String socatColumn = element.getAttributeValue(SOCAT_COLUMN_NAME_ATTRIBUTE);
		Element inputColumn = element.getChild(INPUT_COLUMN_ELEMENT_NAME, element.getNamespace());
		int inputColumnIndex = Integer.parseInt(inputColumn.getAttribute(INPUT_COLUMN_INDEX_ATTRIBUTE).getValue());
		String inputColumnName = inputColumn.getText();
		String inputUnits = element.getChild(INPUT_UNITS_ELEMENT_NAME, element.getNamespace()).getTextTrim();
		
		Double missingValue = null;
		Element missingValueElement = element.getChild(MISSING_VALUE_ELEMENT_NAME, element.getNamespace());
		if (null != missingValueElement) {
			missingValue = Double.parseDouble(missingValueElement.getTextTrim());
		}
		
		logger.trace("Column spec: SOCAT = " + socatColumn + ", Input = '" + inputColumnName + "' (" + inputColumnIndex + ")");
		return new StandardColumnInfo(socatColumn, inputColumnIndex, inputColumnName, inputUnits, missingValue, columnConversionConfig.get(socatColumn));		
	}
	
	/**
	 * Build an object detailing the column information for date fields in an input file.
	 * @param dateElement The element containing the date details
	 * @return An object describing the date columns.
	 */
	private DateColumnInfo processDateColumnInfo(Element dateElement, Logger logger) throws Exception {
		// This is really quite complicated, so all the logic is in the DateColumnInfo class.
		return new DateColumnInfo(dateElement, logger);
	}
	
	/**
	 * Imports a column spec file and converts it to a Java object for use by the rest of the Sanity Checker. 
	 * @param specFile The column spec file
	 * @return A Java object representing the column spec
	 * @throws InvalidColumnSpecException If the column spec is invalid
	 */
	public static ColumnSpec importSpec(File specFile, File schemaFile, ColumnConversionConfig dataConversionConfig, Logger logger) throws InvalidColumnSpecException {
		try {
			Document xml = loadXML(specFile, schemaFile);
			return new ColumnSpec(specFile, xml, dataConversionConfig, logger);
		}	
		catch (Exception e) {
			throw new InvalidColumnSpecException(specFile, "Error reading column spec XML: " + e.getMessage());
		}
	}
	
	/**
	 * Load the column spec XML file and validate it against the XML schema.
	 * @param specFile The XML file
	 * @param schemaFile The schema file.
	 * @return
	 * @throws JDOMException If the XML is invalid
	 * @throws IOException If the file cannot be read
	 */
	private static Document loadXML(File specFile, File schemaFile) throws JDOMException, IOException {
		XMLReaderJDOMFactory schemaFactory = new XMLReaderXSDFactory(schemaFile);
		SAXBuilder builder = new SAXBuilder(schemaFactory);
		return builder.build(specFile);
	}
	
	/**
	 * Returns the column info for a given SOCAT column. Note that date/time columns are handled
	 * differently, and the info for them should be acquired using the appropriate method. 
	 * @param socatColumn The name of the SOCAT column
	 * @return The column information for the specified SOCAT column.
	 */
	public StandardColumnInfo getColumnInfo(String socatColumn) {
		return itsStandardColumnInfo.get(socatColumn);
	}
	
	/**
	 * Returns the column information for the date fields
	 * @return The column information for the date fields
	 */
	public DateColumnInfo getDateColumnInfo() {
		return itsDateColumnInfo;
	}
	
	/**
	 * Returns the list of all column names required in an input file
	 * to allow conversion to the SOCAT data format.
	 * @return The list of required input column names
	 */
	public List<String> getRequiredInputColumnNames() {
		List<String> columnNames = new ArrayList<String>();
		
		for (String column : itsStandardColumnInfo.keySet()) {
			columnNames.add(itsStandardColumnInfo.get(column).getInputColumnName());
		}
		
		columnNames.addAll(itsDateColumnInfo.getDateTimeInputColumns());
		
		return columnNames;
	}
	
	/**
	 * Returns the list of all input column names in this specification
	 * @return The list of input column names
	 */
	public Set<String> getColumnNames() {
		return itsStandardColumnInfo.keySet();
	}
	
	public String getInputColumnName(int columnIndex) {
		String result = null;
		
		for (String columnName : getColumnNames()) {
			StandardColumnInfo colInfo = itsStandardColumnInfo.get(columnName);
			if (colInfo.getInputColumnIndex() == columnIndex) {
				result = colInfo.getInputColumnName();
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Determines whether or not the specified input column name is required or not
	 * @param columnName The input column name
	 * @return {@code true} if the column is required; {@code false} otherwise
	 */
	public boolean isRequired(String columnName) {
		return getRequiredInputColumnNames().contains(columnName);
	}
}
