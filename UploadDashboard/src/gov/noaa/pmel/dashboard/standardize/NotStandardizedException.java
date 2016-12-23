/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import java.util.Collection;
import java.util.HashSet;

import gov.noaa.pmel.dashboard.server.DashDataType;

/**
 * Exception thrown when standardization fails or if a standardized value 
 * is unavailable.  A set of data types required to perform the standardization,
 * if known, is included with this exception.
 * 
 * @author Karl Smith
 */
public class NotStandardizedException extends Exception {

	private static final long serialVersionUID = 8951929703125609580L;

	HashSet<DashDataType> requiredTypes;

	/**
	 * Exception with a message but without any data types required 
	 * for standardization specified.
	 * 
	 * @param message
	 * 		message associated with the exception (can be null)
	 */
	public NotStandardizedException(String message) {
		super(message);
		requiredTypes = null;
	}

	/**
	 * Exception with a message and a collection of data types required
	 * to perform standardization.
	 * 
	 * @param message
	 * 		message associated with the exception (can be null)
	 * @param requiredTypes
	 * 		data types required for standardization (can be null)
	 */
	public NotStandardizedException(String message, Collection<DashDataType> requiredTypes) {
		this(message);
		if ( requiredTypes != null )
			this.requiredTypes = new HashSet<DashDataType>(requiredTypes);
	}

	/**
	 * @return
	 * 		the set of data types required to to perform standardization;
	 * 		may be null
	 */
	public HashSet<DashDataType> getRequiredTypes() {
		return requiredTypes;
	}

	@Override
	public String toString() {
		String repr = "NotStandardizedException: " + getMessage() + 
					  "\n    requiredTypes=[";
		if ( requiredTypes != null ) {
			boolean first = true;
			for ( DashDataType dtype : requiredTypes ) {
				if ( first )
					first = false;
				else
					repr += ", ";
				repr += dtype.getVarName();
			}
		}
		repr += "]";
		return repr;
	}

}
