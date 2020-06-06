package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class Institution extends Container {

    public Institution(Element element) {
        super(element);

    }
    public String getName() {
        return element.getAttributeValue("name");
    }
    
    public String getURL() {
        return element.getAttributeValue("url");
    }
    
    public String getContact() {
        return element.getAttributeValue("contact");
    }
}
