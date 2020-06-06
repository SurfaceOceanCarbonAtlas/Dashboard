package gov.noaa.pmel.tmap.jdom.filter;

import org.jdom.Element;
import org.jdom.filter.Filter;
/**
 * A filter that helps locate properties that are stored in the "new" style:
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
public class FindPropertyFilter implements Filter {
   
    protected String name = null;
    /**
     * Construct the filter by passing in the name of property you want to find.
     * @param name - the name of the property.
     */
    public FindPropertyFilter(String name) {
        this.name = name;
    }
    /**
     * Determines if the Element matches, by finding if the text of a child element called name of an element called property matches.
     */
    public boolean matches(Object node) {
        Element element=null;
        if (node instanceof Element) {
            element = (Element) node;
        } else {
            return false;
        }
        
        if (element.getName().equals("property")) {
            if (element.getChildText("name").equals(name)) {
                return true;
            }
            else {
                return false;
            }
        } else {
            return false;
        }

    }

}
