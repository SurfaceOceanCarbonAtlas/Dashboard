package gov.noaa.pmel.tmap.addxml;

import org.jdom.Element;
import org.jdom.filter.Filter;

public class CatalogRefFilter implements Filter {
    
	@Override
	public boolean matches(Object object) {
		if ( object instanceof Element ) {
			Element e = (Element) object;
			if ( e. getName().equals("catalogRef") ) return true;
		}
		return false;
	}
	
}
