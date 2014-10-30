/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * Vessel entity. @author MyEclipse Persistence Tools
 */
public class Vessel extends AbstractVessel implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public Vessel() {
	}

	/** full constructor */
	public Vessel(String vesselName, String vesselId, String country,
			String vesselOwner) {
		super(vesselName, vesselId, country, vesselOwner);
	}

}
