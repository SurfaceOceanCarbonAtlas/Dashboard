/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.beans;

import java.util.LinkedHashMap;

public class MapBean {

	private LinkedHashMap<String, String> fgdc;

	public LinkedHashMap<String, String> getFgdc() {
		return fgdc;
	}

	public void setFgdc(LinkedHashMap<String, String> fgdc) {
		this.fgdc = fgdc;
	}

}
