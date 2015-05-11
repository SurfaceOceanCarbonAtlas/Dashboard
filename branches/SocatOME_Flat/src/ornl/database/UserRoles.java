/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * UserRoles entity. @author MyEclipse Persistence Tools
 */
public class UserRoles extends AbstractUserRoles implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public UserRoles() {
	}

	/** full constructor */
	public UserRoles(Integer userRoleId, Users users, String authority) {
		super(userRoleId, users, authority);
	}

}
