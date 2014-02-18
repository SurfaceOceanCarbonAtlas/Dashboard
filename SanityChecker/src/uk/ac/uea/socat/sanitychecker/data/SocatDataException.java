package uk.ac.uea.socat.sanitychecker.data;

/**
 * An exception for program errors encountered while processing data.
 * These errors should never occur in a well-running program, and are
 * not synonymous with bad input data.
 */
public class SocatDataException extends Exception {
	
	private static final long serialVersionUID = 6539512783276005987L;

	/**
	 * The line number of the input file that was being processed when the error occurred.
	 */
	private int itsLineNumber;
	
	/**
	 * The column index of the input file that was being processed when the error occurred
	 */
	private int itsInputColumn;
	
	/**
	 * The name of the destination SOCAT output column
	 */
	private String itsOutputColumn;

	/**
	 * Constructor for a message and an error
	 * @param lineNumber The line number of the input file that was being processed when the error occurred
	 * @param inputColumn The column index of the input file that was being processed when the error occurred
	 * @param outputColumn The name of the destination SOCAT output column
	 * @param message The error message
	 * @param cause The root cause of the exception
	 */
	public SocatDataException(int lineNumber, int inputColumn, String outputColumn, String message, Throwable cause) {
		super(message, cause);
		itsLineNumber = lineNumber;
		itsInputColumn = inputColumn;
		itsOutputColumn = outputColumn;
	}
	
	/**
	 * Constructor for a message and an error
	 * @param lineNumber The line number of the input file that was being processed when the error occurred
	 * @param inputColumn The column index of the input file that was being processed when the error occurred
	 * @param outputColumn The name of the destination SOCAT output column
	 * @param message The error message
	 */
	public SocatDataException(int lineNumber, int inputColumn, String outputColumn, String message) {
		super(message);
		itsLineNumber = lineNumber;
		itsInputColumn = inputColumn;
		itsOutputColumn = outputColumn;
	}
	
	/**
	 * Constructor for a an error only
	 * @param lineNumber The line number of the input file that was being processed when the error occurred
	 * @param inputColumn The column index of the input file that was being processed when the error occurred
	 * @param outputColumn The name of the destination SOCAT output column
	 * @param cause The root cause of the exception
	 */
	public SocatDataException(int lineNumber, int inputColumn, String outputColumn, Throwable cause) {
		super(cause);
		itsLineNumber = lineNumber;
		itsInputColumn = inputColumn;
		itsOutputColumn = outputColumn;
	}

	@Override
	public String getMessage() {
		return "SocatDataException, Line = " + itsLineNumber + ", Input col = " + itsInputColumn + ", Output col = " + itsOutputColumn + "\n" + super.getMessage();
	}
}
