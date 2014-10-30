/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.beans;

import java.util.HashMap;

public class Statistics {
	private HashMap<String, String> stats;

	public HashMap<String, String> getStats() {
		return stats;
	}

	public void setStats(HashMap<String, String> stats) {
		this.stats = stats;
	}
}
