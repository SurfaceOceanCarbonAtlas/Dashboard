package uk.ac.uea.socat.sanitychecker.data.conversion;

/**
 * Interface for data conversion routines.
 * One implementation of this interface should be created for each destination
 * data type for the final SOCAT output.
 */
public interface Converter {
	
	/**
	 * Convert the supplied value from the specified units to the destination
	 * units provided by the specific implementation of this interface.
	 * @param value The value to be converted
	 * @param units The units in which the value has been supplied
	 * @return The converted value.
	 */
	public String convert(String value, String units) throws ConversionException;
	
	/**
	 * Determines whether or not the converter can handle the specified units
	 * @param units The units that we want to use
	 * @return {@code true} If the units are supported; {@code false} if they are not.
	 */
	public boolean checkHandledUnits(String units);
}
