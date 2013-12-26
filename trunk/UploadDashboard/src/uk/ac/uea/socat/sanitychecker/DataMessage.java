package uk.ac.uea.socat.sanitychecker;

public class DataMessage extends MetadataMessage {

	@SuppressWarnings("unused")
	private int itsColumnIndex;
	
	@SuppressWarnings("unused")
	private String itsColumnName;
	
	public DataMessage(int severity, int line, int column, String columnName, String message) {
		super(severity, line, message);
		itsColumnIndex = column;
		itsColumnName = columnName;
	}
}
