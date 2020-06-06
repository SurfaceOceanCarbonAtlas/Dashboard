package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

public interface TimeAxisInterface {

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
    
    public abstract String getType();
    
    public abstract ArrayList<NameValuePair> getVerticies();
      
    public abstract boolean isDayNeeded();

    public abstract void setDayNeeded(boolean dayNeeded);

    public abstract String getDisplay_type();

    public abstract void setDisplay_type(String display_type);

    public abstract String getHi();

    public abstract void setHi(String hi);

    public abstract boolean isHourNeeded();

    public abstract void setHourNeeded(boolean hourNeeded);

    public abstract String getLo();

    public abstract void setLo(String lo);

    public abstract double getMinuteInterval();

    public abstract void setMinuteInterval(double minuteInterval);

    public abstract boolean isMonthNeeded();

    public abstract void setMonthNeeded(boolean monthNeeded);

    public abstract boolean isYearNeeded();

    public abstract void setYearNeeded(boolean yearNeeded);

}