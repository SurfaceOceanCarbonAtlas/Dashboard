package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.HashMap;

public interface OperationInterface {
    /* All of these methods are supplied by the Container class in my implementation. */
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
    
    /* These methods are supplied by the implementing class (Operation) */
    public abstract ArrayList<Option> getOptions();
    public abstract void setOptions(ArrayList<Option> options);

}