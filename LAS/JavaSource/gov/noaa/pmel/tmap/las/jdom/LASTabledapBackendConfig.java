package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * @author Roland Schweitzer
 * @author Bob Simons (bob.simons@noaa.gov)
 *
 */
public class LASTabledapBackendConfig extends LASDocument {
    
    /**
     * @param name
     * @return
     * @throws JDOMException
     */
    public String getURL(String name) throws JDOMException  {
        Element server = getElementByXPath("/tabledap_servers/tabledap[@name='"+name+"']");
        if ( server == null ) {
            return "";
        } else {
            String url = server.getAttributeValue("url");
            if ( url == null ) {
                return "";
            } else {
                return url;
            }
        }
    }

}
