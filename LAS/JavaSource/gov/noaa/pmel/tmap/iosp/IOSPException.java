/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.iosp;
/**
 * An exception class so that any Exception our code throws will be of this type and
 * can be recognized as something we threw rather than something thrown by a system or library class.
 * @author Roland Schweitzer
 *
 */

public class IOSPException extends Exception {
    /*
	 * Any number that uniquely identifies the version of this class' code.  
	 * The Eclipse IDE will generate it automatically for you.  We do not depend on this
	 * since we do not serialize our code across the wire.
	 */
    private static final long serialVersionUID = -9084893017702988427L;
    /**
     * Construct an Exception with our message.
     * @param message
     */
    public IOSPException(String message) {
        super(message);
    }
}
