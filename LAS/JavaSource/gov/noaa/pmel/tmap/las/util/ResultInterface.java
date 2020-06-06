package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.HashMap;

public interface ResultInterface {
	// From the container class
    public abstract String getID();
    public abstract void setID(String ID);
    public abstract String getName();
    public abstract void setName(String name);
    public abstract HashMap<String, ArrayList<NameValuePair>> getProperties();
    public abstract void setProperties(HashMap<String, ArrayList<NameValuePair>> properties);
    public abstract ArrayList<NameValuePair> getAttributes();
    public abstract void setAttributes(ArrayList<NameValuePair> attributes);
    public abstract String toXML();
    public abstract String getAttributeValue(String name);
    // From the result implementation
    public abstract String getURL();
    public abstract String getFile();
    public abstract String getType();
}
