package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.las.ui.Util;

import java.util.ArrayList;

import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class View extends Container implements ViewInterface {
    public View(Element element) {
        super(element);
    }
    public String getValue() {
        String value = element.getAttributeValue("value");
        if ( value != null ) {
            return value;
        } else {
            return "";
        }
    }
    public JSONObject toJSON() throws JSONException {
        ArrayList<String> asArrays = new ArrayList<String>();
        asArrays.add("view");
        return Util.toJSON(element, asArrays);
    }
}

