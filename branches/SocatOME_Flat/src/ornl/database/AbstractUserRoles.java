/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;



/**
 * AbstractUserRoles entity provides the base persistence definition of the UserRoles entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractUserRoles  implements java.io.Serializable {


    // Fields    

     private Integer userRoleId;
     private Users users;
     private String authority;


    // Constructors

    /** default constructor */
    public AbstractUserRoles() {
    }

    
    /** full constructor */
    public AbstractUserRoles(Integer userRoleId, Users users, String authority) {
        this.userRoleId = userRoleId;
        this.users = users;
        this.authority = authority;
    }

   
    // Property accessors

    public Integer getUserRoleId() {
        return this.userRoleId;
    }
    
    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public Users getUsers() {
        return this.users;
    }
    
    public void setUsers(Users users) {
        this.users = users;
    }

    public String getAuthority() {
        return this.authority;
    }
    
    public void setAuthority(String authority) {
        this.authority = authority;
    }
   








}
