/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.jdom.filter;

import org.jdom.Element;
import org.jdom.filter.Filter;
/**
 * A filter that helps locate properties that are stored in the "old" style:
 * <p>
 * <pre>
 * &lt;properties&gt;<br>
 * &nbsp;&nbsp;&lt;GROUP&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;NAME&gt;VALUE&lt;/NAME&gt;<br>
 * &nbsp;&nbsp;&lt;/GROUP&gt;<br>
 * &lt;properties&gt;<br> 
 * </pre>
 * @author Roland Schweitzer
 *
 */
public class FindOldPropertyFilter implements Filter {
    
    protected String name = null;
    /**
     * Construct the filter by passing in the name of the property you're looking for.
     * @param name - the property name.
     */
    public FindOldPropertyFilter(String name) {
        this.name = name;
    }
    /**
     * The implementation of the matches method which returns true of the JDOM element has the same name as the property.
     * @param obj the object to be tested
     */
    public boolean matches(Object node) {
        Element element=null;
        if (node instanceof Element) {
            element = (Element) node;
        } else {
            return false;
        }
        
        if (element.getName().equals(name) ) {
            return true;
        } else {
            return false;
        }
    }
}
