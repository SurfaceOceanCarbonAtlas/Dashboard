package uk.ac.uea.socat.metadata.MetadataChecker;

/**
 * Contains the results of a metadata check.
 * 
 * @author uuk07qzu
 *
 */
public class MetadataCheckResult {
	
	private boolean itsCheckPassed = false;
	
	/**
	 * Basic constructor. Does nothing
	 */
	protected MetadataCheckResult() {
		
	}

	/**
	 * Set the passed flag on this result
	 * @param passed The passed flag
	 */
	protected void setCheckPassed(boolean passed) {
		itsCheckPassed = passed;
	}
	
	/**
	 * Determines whether or not this result represents a passed check
	 * @return {@code true} if the check has passed; {@code false} if it has not.
	 */
	public boolean checkPassed() {
		return itsCheckPassed;
	}
}
