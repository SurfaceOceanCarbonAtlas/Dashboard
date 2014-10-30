/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;



/**
 * Author entity. @author MyEclipse Persistence Tools
 */
public class Author extends AbstractAuthor implements java.io.Serializable {

    // Constructors

    /** default constructor */
    public Author() {
    }

    
    /** full constructor */
    public Author(String fullName, String organization, String address, String phone, String email) {
        super(fullName, organization, address, phone, email);        
    }
   
}
