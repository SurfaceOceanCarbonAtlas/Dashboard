package socat.sanitychecker.data;

import socat.sanitychecker.data.conversion.ConversionException;
import socat.sanitychecker.data.conversion.Converter;

/**
 * An object that describes how a SOCAT output columns should be retrieved from the input file.
 * This includes the name and index of the column in the input file, and a {@code Converter} to
 * be used to convert the input data to the required output units.
 */
public class StandardColumnInfo {
	
	/**
	 * The SOCAT output file column that this object refers to.
	 */
	private String itsSocatColumn;
	
	/**
	 * The index of the input column
	 */
	private int itsInputColumnIndex;
	
	/**
	 * The name of the input column
	 */
	private String itsInputColumnName;
	
	/**
	 * The units of the input column
	 */
	private String itsInputUnits;
	
	/**
	 * The Converter object used to convert the input value to the
	 * required SOCAT units. If this is {@code null}, no conversion
	 * will be performed.
	 */
	private Converter itsConverter;

	/**
	 * Build the column info object
	 * @param socatColumn The SOCAT output column
	 * @param inputColumnIndex The input column index
	 * @param inputColumnName The input column name
	 * @param inputUnits The input column units
	 * @param converter The converter to be used to produce the SOCAT output value
	 * @throws ConversionException If the converter cannot work with the specified input units.
	 */
	public StandardColumnInfo(String socatColumn, int inputColumnIndex, String inputColumnName, String inputUnits, Converter converter) throws ConversionException {
		itsSocatColumn = socatColumn;
		itsInputColumnIndex = inputColumnIndex;
		itsInputColumnName = inputColumnName;
		itsInputUnits = inputUnits;
		itsConverter = converter;
		
		if (null != converter && !converter.checkHandledUnits(inputUnits)) {
			throw new ConversionException("Cannot convert data of units " + inputUnits + " (column " + inputColumnIndex + " '" + inputColumnName + "')");
		}
	}
	
	/**
	 * Returns the name of the SOCAT column that this info object refers to.
	 * @return The name of the SOCAT column.
	 */
	public String getSocatColumn() {
		return itsSocatColumn;
	}
	
	/**
	 * Returns the index of the column in the input file where this column can be found.
	 * Column indexes are 1-based.
	 * 
	 * @return The index of the column in the input file.
	 */
	public int getInputColumnIndex() {
		return itsInputColumnIndex;
	}
	
	/**
	 * Returns the name of the column in the input file that this info object refers to.
	 * @return The name of the column in the input file.
	 */
	public String getInputColumnName() {
		return itsInputColumnName;
	}
	
	/**
	 * Convert an input value to the required units for the SOCAT output,
	 * using the converter specified when this information object was created.
	 * If no converter was specified, the original value will be returned with no conversion performed.
	 * 
	 * @param value The input value
	 * @return The converted value
	 */
	public String convert(String value) throws ConversionException {
		String result = value;
		
		if (null != itsConverter) {
			result = itsConverter.convert(value, itsInputUnits);
		}
		
		return result;
	}
}
