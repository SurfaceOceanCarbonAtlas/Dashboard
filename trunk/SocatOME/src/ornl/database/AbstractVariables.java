/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * AbstractVariables entity provides the base persistence definition of the
 * Variables entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractVariables implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;
	private String description;

	// Constructors

	/** default constructor */
	public AbstractVariables() {
	}
	
	/** full constructor */
	public AbstractVariables( String name, String description) {
		this.name = name;
		this.description = description;
		
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
