/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

import java.util.HashSet;
import java.util.Set;


/**
 * AbstractUsers entity provides the base persistence definition of the Users entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractUsers  implements java.io.Serializable {


    // Fields    

     private Integer userId;
     private String username;
     private String password;
     private Boolean enabled;
     private String email;
     private Set userRoleses = new HashSet(0);


    // Constructors

    /** default constructor */
    public AbstractUsers() {
    }

	/** minimal constructor */
    public AbstractUsers(Integer userId, String username, String password, Boolean enabled, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.email = email;
    }
    
    /** full constructor */
    public AbstractUsers(Integer userId, String username, String password, Boolean enabled, String email, Set userRoleses) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.email = email;
        this.userRoleses = userRoleses;
    }

   
    // Property accessors

    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public Set getUserRoleses() {
        return this.userRoleses;
    }
    
    public void setUserRoleses(Set userRoleses) {
        this.userRoleses = userRoleses;
    }
   








}
