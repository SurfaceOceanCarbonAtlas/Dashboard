/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.serializable;


import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author rhs
 * 
 */
public class Serializable implements IsSerializable {
    String name;
    String ID;
    Map<String, String> attributes;
    Map<String, Map<String, String>> properties;
	/**
	 * 
	 */
	public Serializable() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @return the properties
	 */
	public Map<String, Map<String, String>> getProperties() {
		return properties;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param id the iD to set
	 */
	public void setID(String id) {
		ID = id;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, Map<String,String>> properties) {
		this.properties = properties;
	}

}
