package gov.noaa.pmel.tmap.jdom.filter;

import org.jdom.Element;
import org.jdom.filter.Filter;
/**
 * A filter that helps locate a property group that are stored in the "old" style:
 * <p>
 * &lt;properties&gt;<br>
 * &nbsp;&nbsp;&lt;GROUP&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;NAME&gt;VALUE&lt;/NAME&gt;<br>
 * &nbsp;&nbsp;&lt;/GROUP&gt;<br>
 * &lt;properties&gt;<br> 
 * @author Roland Schweitzer
 */
public class FindOldPropertyGroupFilter implements Filter {

    protected String type;
    
    /**
     * Construct the filter by passing in the group name you're looking for.
     * @param type - the group name you want to find.
     */
    public FindOldPropertyGroupFilter (String type) {
        this.type = type;
    }
    /**
     * Implementation of the matches method that returns true if it's a JDOM Element with the same name as the group you're looking for.
     */
    public boolean matches(Object node) {
        Element element;
        if ( node instanceof Element) {
            element = (Element) node;
        } else {
            return false;
        }
        
        if ( element.getName().equals(type) ) {
            return true;
        }
        
        return false;
    }
}
