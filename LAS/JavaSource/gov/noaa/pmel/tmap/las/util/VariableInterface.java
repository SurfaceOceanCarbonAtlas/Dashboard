package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.HashMap;

public interface VariableInterface {
    /* These are supplied by the Container class in my implementation. */
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
    /* End of methods supplied by the Container class. */
    
    /* These are supplied by the Variable class which implements this interface. */
    public abstract String getDSID();
    public abstract void setDSID(String dsid);
    public abstract String getGridID();
    public abstract void setGridID(String gridID);
    public abstract String getXPath();

    /**
     * This is a convenience method which can be used to set the value of the URL attribute in the attribute collection for this variable.
     * @param string
     */
    public abstract void setURL(String var_url);
    public abstract String getUnits();

}