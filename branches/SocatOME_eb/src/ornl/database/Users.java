/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

import java.util.Set;


/**
 * Users entity. @author MyEclipse Persistence Tools
 */
public class Users extends AbstractUsers implements java.io.Serializable {

    // Constructors

    /** default constructor */
    public Users() {
    }

	/** minimal constructor */
    public Users(Integer userId, String username, String password, Boolean enabled, String email) {
        super(userId, username, password, enabled, email);        
    }
    
    /** full constructor */
    public Users(Integer userId, String username, String password, Boolean enabled, String email, Set userRoleses) {
        super(userId, username, password, enabled, email, userRoleses);        
    }
   
}
