package uk.ac.uea.socat.sanitychecker.data;

import uk.ac.exeter.QCRoutines.data.DataRecordException;

/**
 * An exception for program errors encountered while processing data.
 * These errors should never occur in a well-running program, and are
 * not synonymous with bad input data.
 */
public class SocatDataException extends DataRecordException {
	
	private static final long serialVersionUID = 8052582544581014828L;

	/**
	 * The column index of the input file that was being processed when the error occurred
	 */
	private int inputColumn;
	
	/**
	 * Constructor for a message and an error
	 * @param lineNumber The line number of the input file that was being processed when the error occurred
	 * @param inputColumn The column index of the input file that was being processed when the error occurred
	 * @param outputColumn The destination SOCAT output column
	 * @param message The error message
	 * @param cause The root cause of the exception
	 */
	public SocatDataException(int lineNumber, int inputColumn, SocatDataColumn outputColumn, String message, Throwable cause) {
		super(lineNumber, outputColumn, message, cause);
		this.inputColumn = inputColumn;
	}
	
	/**
	 * Constructor for a message and an error
	 * @param lineNumber The line number of the input file that was being processed when the error occurred
	 * @param inputColumn The column index of the input file that was being processed when the error occurred
	 * @param outputColumn The destination SOCAT output column
	 * @param message The error message
	 */
	public SocatDataException(int lineNumber, int inputColumn, SocatDataColumn outputColumn, String message) {
		super(lineNumber, outputColumn, message);
		this.inputColumn = inputColumn;
	}
	
	/**
	 * Constructor for a an error only
	 * @param lineNumber The line number of the input file that was being processed when the error occurred
	 * @param inputColumn The column index of the input file that was being processed when the error occurred
	 * @param outputColumn The destination SOCAT output column
	 * @param cause The root cause of the exception
	 */
	public SocatDataException(int lineNumber, int inputColumn, SocatDataColumn outputColumn, Throwable cause) {
		super(lineNumber, outputColumn, "An unknown error occurred", cause);
		this.inputColumn = inputColumn;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + "(Input column index " + inputColumn + ")";
	}
}
