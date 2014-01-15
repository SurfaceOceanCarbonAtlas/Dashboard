package uk.ac.uea.socat.sanitychecker;

public class DataMessage extends MetadataMessage {

	private int itsColumnIndex;
	
	private String itsColumnName;
	
	public DataMessage(int severity, int line, int column, String columnName, String message) {
		super(severity, line, message);
		itsColumnIndex = column;
		itsColumnName = columnName;
	}

	public String toString() {
		StringBuffer output = new StringBuffer(super.toString());

		output.append("  Column index " + itsColumnIndex + ", name '" + itsColumnName + "'");

		return output.toString();
	}
}
