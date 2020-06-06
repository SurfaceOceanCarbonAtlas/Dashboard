package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class Message extends Container {

    public Message(Element element) {
        super(element);
    }

    public String getType() {
        return element.getAttributeValue("type");
    }

    public String getKey() {
        return element.getChild("key").getTextTrim();
    }

    public String getText() {
        Element text = element.getChild("text");
        if ( text != null ) {
            return text.getTextTrim();
        } else {
            return null;
        }
    }
}
