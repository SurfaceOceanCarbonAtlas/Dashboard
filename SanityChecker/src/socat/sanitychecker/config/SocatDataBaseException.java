package socat.sanitychecker.config;

/**
 * Exception for errors in manipulating SOCAT data objects.
 * This is the base exception, and should be wrapped in a
 * SocatDataException to give it proper context.
 */
public class SocatDataBaseException extends Exception {
	
	/**
	 * The name of the column where the error occurred
	 */
	private String itsColumnName;
	
	/**
	 * Basic constructor - just a message.
	 * @param columnName The name of the column where the error occurred
	 * @param message The error message.
	 */
	public SocatDataBaseException(String columnName, String message) {
		super(message);
		itsColumnName = columnName;
	}
	
	/**
	 * Returns the name of the column where the error occurred
	 * @return The name of the column where the error occurred
	 */
	public String getColumnName() {
		return itsColumnName;
	}
	
	@Override
	public String getMessage() {
		return "Exception processing column name " + itsColumnName + ":\n" + super.getMessage();
	}
}
