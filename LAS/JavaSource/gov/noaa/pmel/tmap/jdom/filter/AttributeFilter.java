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
 * This is a filter class that will find any element that has an attribute with particular name and value.
 * @author rhs
 *
 */
public class AttributeFilter implements Filter {
    String name;
    String value;
    String element_name;
	public AttributeFilter(String element_name, String name, String value) {
		this.element_name = element_name;
		this.name = name;
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see org.jdom.filter.Filter#matches(java.lang.Object)
	 */
	public boolean matches(Object obj) {
		Element element=null;
		if (obj instanceof Element) {
			element = (Element) obj;
		} else {
			return false;
		}
		if ( element.getName().equals(element_name) ) {
			String att = element.getAttributeValue(name);
			if (att != null && att.equals(value)) {
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
