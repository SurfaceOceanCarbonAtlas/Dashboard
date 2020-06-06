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
 * Tool class which defines a method for getting path to the resources this tool uses.
 * @author Roland Schweitzer
 *
 */

public class Tool {
    /**
     * Resolve the full path name for the location of the resource used by this tool.
     * @param resource The 
     * @return the fully qualified path of the requested resource
     */
    public String getResourcePath(String resource) {
        
        return JDOMUtils.getResourcePath(this, resource);
        
    }
}
