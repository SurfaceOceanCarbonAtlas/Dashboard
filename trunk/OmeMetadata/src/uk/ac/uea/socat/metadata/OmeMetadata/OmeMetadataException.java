package uk.ac.uea.socat.metadata.OmeMetadata;

public class OmeMetadataException extends Exception {

	private static final long serialVersionUID = -6937865715432331699L;

	public OmeMetadataException(String message) {
		super(message);
	}
	
	public OmeMetadataException(int line, String message) {
		super("Line " + line + ": " + message);
	}
}
