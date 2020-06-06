/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Roland Schweitzer
 *
 */
public interface AnalysisAxisInterface {
	/*
	  <analysis label="Average AIR TEMPERATURE">
        <axis type="y" lo="-89" hi="89" op="ave"/>
      </analysis>
	 
	 */
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
    
    // Additional helper methods for the Analysis:
    
    public abstract String getType();
    public abstract void setType(String type);
    public abstract String getHi();
    public abstract void setHi(String hi);
    public abstract String getLo();
    public abstract void setLo(String lo);
    public abstract String getOp();
    public abstract void setOp(String op);
   
}
