/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * Files entity. @author MyEclipse Persistence Tools
 */
public class Files extends AbstractFiles implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public Files() {
	}

	/** full constructor */
	public Files(String id, String fileLocation, String creatorEmail,
			String fileStatus, String updateDate) {
		super(id, fileLocation, creatorEmail, fileStatus, updateDate);
	}

}
