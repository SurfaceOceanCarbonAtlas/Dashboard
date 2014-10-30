/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.beans;

import java.util.HashMap;

/**
 * Generic Map of named objects from an XML bean attribute named properties.
 * 
 */

public class Configuration {

	private HashMap<String, Object> properties;

	public HashMap<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, Object> properties) {
		this.properties = properties;
	}

}
