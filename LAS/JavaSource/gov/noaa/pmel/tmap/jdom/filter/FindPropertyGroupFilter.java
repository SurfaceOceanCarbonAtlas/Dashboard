package gov.noaa.pmel.tmap.jdom.filter;

import org.jdom.filter.Filter;
import org.jdom.Element;
/**
 * A filter that helps locate a property groups that are stored in the "new" style:
 * <p>
 * &lt;properties&gt;<br>
 * &nbsp;&nbsp;&lt;property_group type=GROUP&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;property&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp&lt;name&gt;NAME&lt;/name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp&lt;value&gt;VALUE&lt;/value&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;/property&gt;<br>
 * &nbsp;&nbsp;&lt;/property_group&gt;<br>
 * &lt;properties&gt;<br> 
 * @author Roland Schweitzer
 *
 */
public class FindPropertyGroupFilter implements Filter {
    
    protected String type = null;
    /**
     * Construct the filter by passing in the name of the group you want to find.
     * @param type - the name of the property group.
     */
    public FindPropertyGroupFilter (String type) {
        this.type = type;
    }
    /**
     * If the element is named property_group and the type is the same as what you're looking for it's a match.
     */
    public boolean matches(Object node) {
        Element element;
        if ( node instanceof Element) {
            element = (Element) node;
        } else {
            return false;
        }
        
        if ( element.getName().equals("property_group") && 
             element.getAttributeValue("type").equals(type)) {
            return true;
        }
        
        return false;
    }

}
