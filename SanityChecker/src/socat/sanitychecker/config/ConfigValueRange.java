package socat.sanitychecker.config;

import java.text.ParseException;

import socat.sanitychecker.metadata.MetadataItem;

/**
 * Since data ranges can be either integers or reals,
 * this little helper class will help to work with the
 * different data types.
 */
public class ConfigValueRange {
	
	/**
	 * Integer minimum value
	 */
	private int minInt;
	
	/**
	 * Integer maximum value
	 */
	private int maxInt;
	
	/**
	 * Real minimum value
	 */
	private double minReal;
	
	/**
	 * Real maximum value
	 */
	private double maxReal;
	
	protected ConfigValueRange(int dataType, String min, String max) throws ParseException {
	
		switch (dataType) {
		case (MetadataItem.INTEGER_TYPE):
		{
			if (min.equalsIgnoreCase("NA")) {
				minInt = Integer.MIN_VALUE;
			} else {
				minInt = Integer.parseInt(min);
			}
			
			if (max.equalsIgnoreCase("NA")) {
				maxInt = Integer.MAX_VALUE;
			} else {
				maxInt = Integer.parseInt(max);
			}
			break;
		}
		case (MetadataItem.REAL_TYPE):
		{
			if (min.equalsIgnoreCase("NA")) {
				minReal = Double.MIN_VALUE;
			} else {
				minReal = Double.parseDouble(min);
			}
			
			if (max.equalsIgnoreCase("NA")) {
				maxReal = Double.MAX_VALUE;
			} else {
				maxReal = Double.parseDouble(max);
			}
			break;
		}
		default:
		{
			throw new ParseException("Cannot specify a range for non-number data type", 0);
		}
		}
	}
	
	/**
	 * Returns the integer minimum of the range
	 * @return The integer minimum of the range
	 */
	public int getIntMin() {
		return minInt;
	}
	
	/**
	 * Returns the integer maximum of the range
	 * @return The integer maximum of the range
	 */
	public int getIntMax() {
		return maxInt;
	}
	
	/**
	 * Returns the real minimum of the range
	 * @return The real minimum of the range
	 */
	public double getRealMin() {
		return minReal;
	}
	
	/**
	 * Returns the real maximum of the range
	 * @return The real maximum of the range
	 */
	public double getRealMax() {
		return maxReal;
	}
}
