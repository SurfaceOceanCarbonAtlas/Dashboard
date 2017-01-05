/**
 * 
 */
package gov.noaa.pmel.dashboard.datatype;

/**
 * @author Karl Smith
 */
public class TimestampConverter extends ValueConverter<String> {

	/* various separators: / - : space or nothing
	 * seconds can be floating point
					"yyyy-mm-dd hh:mm:ss", 
					"mm-dd-yyyy hh:mm:ss", 
					"dd-mm-yyyy hh:mm:ss", 
					"mm-dd-yy hh:mm:ss", 
					"dd-mm-yy hh:mm:ss"));

					"yyyy-mm-dd", 
					"mm-dd-yyyy", 
					"dd-mm-yyyy", 
					"mm-dd-yy", 
					"dd-mm-yy"));

					"hh:mm:ss"
	 */
	public TimestampConverter(String inputUnit, String outputUnit, String missingValue)
			throws IllegalArgumentException, IllegalStateException {
		super(inputUnit, outputUnit, missingValue);
		throw new IllegalArgumentException("Not yet supported");
	}

	@Override
	public String convertValueOf(String valueString) throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("Not yet supported");
	}

}
