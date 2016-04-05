package uk.ac.uea.socat.sanitychecker.metadata;

public class MetadataException extends Exception {
	/**
	 * Version string for the MetadataException class. This should be changed if the
	 * class is ever updated and becomes incompatible with previous versions.
	 */
	private static final long serialVersionUID = 10004001L;

	/**
	 * The name of the metadata item in which the error occurred
	 */
	private String itsMetadataName = null;
	
	/**
	 * Basic constructor for an error that is not linked to a specific metadata item.
	 * Only an error message is required.
	 * @param message The error message
	 */
	public MetadataException(String message) {
		super(message);
	}
	
	/**
	 * Basic constructor for an error that is not linked to a specific metadata item.
	 * Only an error message is required.
	 * @param message The error message
	 */
	public MetadataException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructor for an error relating to a specific metadata item.
	 * @param name The name of the metadata item
	 * @param message The error message
	 */
	public MetadataException(String name, String message) {
		super(message);
		itsMetadataName = name;
	}
	
	/**
	 * Returns the details of the error
	 */
	public String getMessage() {
		String message;

		if (null == itsMetadataName) {
			message = super.getMessage();
		} else {
			message = "Error in metadata item" + itsMetadataName + ": " + super.getMessage();
		}
		
		return message;
	}
}
