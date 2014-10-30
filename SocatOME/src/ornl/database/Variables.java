/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * Variables entity. @author MyEclipse Persistence Tools
 */
public class Variables extends AbstractVariables implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public Variables() {
	}
	

	/** full constructor */
	public Variables( String name, String description) {
		super( name, description);
	}

}
