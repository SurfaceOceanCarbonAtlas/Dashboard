/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;



/**
 * AbstractAuthor entity provides the base persistence definition of the Author entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractAuthor  implements java.io.Serializable {


    // Fields    

     private Long id;
     private String fullName;
     private String organization;
     private String address;
     private String phone;
     private String email;


    // Constructors

    /** default constructor */
    public AbstractAuthor() {
    }

    
    /** full constructor */
    public AbstractAuthor(String fullName, String organization, String address, String phone, String email) {
        this.fullName = fullName;
        this.organization = organization;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

   
    // Property accessors

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }   

    public String getOrganization() {
		return organization;
	}


	public void setOrganization(String organization) {
		this.organization = organization;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
