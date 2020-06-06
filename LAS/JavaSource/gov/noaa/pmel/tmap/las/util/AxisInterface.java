package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.HashMap;

public interface AxisInterface {
    
    /* These methods are supplied by the Container class in my implementation. */
    public abstract String getID();
    public abstract void setID(String ID);
    public abstract String getName();
    public abstract void setName(String name);
    public abstract HashMap<String, ArrayList<NameValuePair>> getProperties();
    public abstract void setProperties(HashMap<String, ArrayList<NameValuePair>> properties);
    public abstract ArrayList<NameValuePair> getAttributes();
    public abstract void setAttributes(ArrayList<NameValuePair> attributes);
    public abstract String getAttributeValue(String name);
    
    /**
     * @return the type
     */
    public abstract String getType();

    /**
     * @param type the type to set
     */
    public abstract void setType(String type);

    /**
     * @return the verticies
     */
    public abstract ArrayList<NameValuePair> getVerticies();

    /**
     * @param the ArrayList of verticies
     */
    public abstract void setVerticies(ArrayList<NameValuePair> verticies);

    /**
     * @return the arange
     */
    public abstract Arange getArange();

    /**
     * @param arange the arange to set
     */
    public abstract void setArange(Arange arange);

    public abstract boolean hasV();

}