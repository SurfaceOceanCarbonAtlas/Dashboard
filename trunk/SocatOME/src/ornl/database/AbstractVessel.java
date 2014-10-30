/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * AbstractVessel entity provides the base persistence definition of the Vessel
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractVessel implements java.io.Serializable {

	// Fields

	private Long id;
	private String vesselName;
	private String vesselId;
	private String country;
	private String vesselOwner;

	// Constructors

	/** default constructor */
	public AbstractVessel() {
	}

	/** full constructor */
	public AbstractVessel(String vesselName, String vesselId, String country,
			String vesselOwner) {
		this.vesselName = vesselName;
		this.vesselId = vesselId;
		this.country = country;
		this.vesselOwner = vesselOwner;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVesselName() {
		return this.vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getVesselId() {
		return this.vesselId;
	}

	public void setVesselId(String vesselId) {
		this.vesselId = vesselId;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getVesselOwner() {
		return this.vesselOwner;
	}

	public void setVesselOwner(String vesselOwner) {
		this.vesselOwner = vesselOwner;
	}

}
