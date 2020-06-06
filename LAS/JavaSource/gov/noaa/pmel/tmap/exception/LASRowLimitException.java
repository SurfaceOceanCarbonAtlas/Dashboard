package gov.noaa.pmel.tmap.exception;

public class LASRowLimitException extends LASException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8243598707951213275L;

	/**
     * Construct an Exception with our message.
     * @param message
     */
    public LASRowLimitException(String message) {
        super(message);
    }
}
