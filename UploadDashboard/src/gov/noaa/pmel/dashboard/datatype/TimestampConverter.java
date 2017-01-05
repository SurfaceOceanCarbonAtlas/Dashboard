/**
 * 
 */
package gov.noaa.pmel.dashboard.datatype;

/**
 * @author Karl Smith
 */
public class TimestampConverter extends ValueConverter<String> {

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
