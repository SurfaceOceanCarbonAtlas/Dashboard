package uk.ac.uea.socat.metadata.OmeMetadata;

public class OmeMetadataException extends Exception {

	public OmeMetadataException(String message) {
		super(message);
	}
	
	public OmeMetadataException(int line, String message) {
		super("Line " + line + ": " + message);
	}
}
