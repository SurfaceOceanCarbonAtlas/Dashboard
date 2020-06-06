/**
 * 
 */
package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;

import gov.noaa.pmel.tmap.las.client.serializable.RegionSerializable;
import gov.noaa.pmel.tmap.las.ui.Util;

import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author rhs
 *
 */
public class Region extends Container {

	/**
	 * @param element
	 */
	public Region(Element element) {
		super(element);
		// TODO Auto-generated constructor stub
	}
	public JSONObject toJSON() throws JSONException {
		ArrayList<String> asArrays = new ArrayList<String>();
		asArrays.add("region");
		return Util.toJSON(element, asArrays);
	}
	public RegionSerializable getRegionSerializable() {
		RegionSerializable wire_region = new RegionSerializable();
		/*
		 *      The XML looks like this...
		 *          
		        region.setAttribute("name", name);
    			region.setAttribute("ID", name);
    			region.setAttribute("values", values);
    			String[] corners = values.split(",");
    			region.setAttribute("xhi", corners[0]);
    			region.setAttribute("xlo", corners[1]);
    			region.setAttribute("yhi", corners[2]);
    			region.setAttribute("ylo", corners[3]);
		 */
		wire_region.setName(getAttributeValue("name"));
		String values = getAttributeValue("values");
		String[] corners = values.split(",");
		wire_region.setWestLon(Double.valueOf(corners[0].trim()));
		wire_region.setEastLon(Double.valueOf(corners[1].trim()));
		wire_region.setSouthLat(Double.valueOf(corners[2].trim()));
		wire_region.setNorthLat(Double.valueOf(corners[3].trim()));
		return wire_region;
	}

}
