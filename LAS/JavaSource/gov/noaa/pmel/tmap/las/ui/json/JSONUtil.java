package gov.noaa.pmel.tmap.las.ui.json;

import gov.noaa.pmel.tmap.las.util.Container;
import gov.noaa.pmel.tmap.las.util.NameValuePair;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Some utility methods to make filling up JSONArrays easier.
 * 
 * @author Roland Schweitzer
 * @see org.json.JSONArray
 *
 */
public class JSONUtil {

    /**
     * Take a list of LAS Containers and use them to fill a JSONArray.
     * @param containers
     * @param array
     * @throws JSONException
     */
    public static void fill (ArrayList containers, JSONObject hash) throws JSONException {

        for (Iterator conIt = containers.iterator(); conIt.hasNext();) {
            JSONObject attributes = new JSONObject();
            Object con = conIt.next();
            if (con instanceof Container) {
                Container lcon = (Container) con;               
                attributes.put("name", lcon.getName());
                attributes.put("ID", lcon.getID());
                ArrayList<NameValuePair> attrs = lcon.getAttributes();
                for (Iterator atIt = attrs.iterator(); atIt.hasNext();) {
                    NameValuePair attr = (NameValuePair) atIt.next();
                    attributes.put(attr.getName(), attr.getValue());
                }
                            
                HashMap<String, ArrayList<NameValuePair>> properties = lcon.getProperties();
                if ( properties.size() > 0 ) {
                    JSONObject json_groups = new JSONObject();
                    for (Iterator groups = properties.keySet().iterator(); groups.hasNext();) {
                        String key = (String) groups.next();
                        ArrayList<NameValuePair> group_props = properties.get(key);
                        JSONObject group = new JSONObject();
                        for (Iterator grpPropIt = group_props.iterator(); grpPropIt.hasNext();) {
                            NameValuePair property = (NameValuePair) grpPropIt.next();
                            group.put(property.getName(), property.getValue());
                        }
                        json_groups.put(key, group);  
                    }
                    attributes.put("properties",json_groups);
                }
                hash.put(lcon.getID(), attributes);  
            }
        }
    }
    public static void better_fill (ArrayList containers, JSONArray array) throws JSONException {

        for (Iterator conIt = containers.iterator(); conIt.hasNext();) {
            JSONObject attributes = new JSONObject();
            Object con = conIt.next();
            if (con instanceof Container) {
                Container lcon = (Container) con;               
                attributes.put("name", lcon.getName());
                attributes.put("ID", lcon.getID());
                ArrayList<NameValuePair> attrs = lcon.getAttributes();
                for (Iterator atIt = attrs.iterator(); atIt.hasNext();) {
                    NameValuePair attr = (NameValuePair) atIt.next();
                    attributes.put(attr.getName(), attr.getValue());
                }
                            
                HashMap<String, ArrayList<NameValuePair>> properties = lcon.getProperties();
                if ( properties.size() > 0 ) {
                    JSONObject json_groups = new JSONObject();
                    for (Iterator groups = properties.keySet().iterator(); groups.hasNext();) {
                        String key = (String) groups.next();
                        ArrayList<NameValuePair> group_props = properties.get(key);
                        JSONObject group = new JSONObject();
                        JSONObject values = new JSONObject();
                        for (Iterator grpPropIt = group_props.iterator(); grpPropIt.hasNext();) {
                            NameValuePair property = (NameValuePair) grpPropIt.next();
                            values.put(property.getName(), property.getValue());
                        }
                        group.put("name", key);
                        group.put("values", values);
                        json_groups.put("property_group_"+key, group);  
                    }
                    attributes.put("properties",json_groups);
                }
                array.put(attributes);  
            }
        }
    }
}
