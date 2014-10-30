/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * AbstractFiles entity provides the base persistence definition of the Files
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractFiles implements java.io.Serializable {

	// Fields

	private String id;
	private String fileLocation;
	private String creatorEmail;
	private String fileStatus;
	private String updateDate;

	// Constructors

	/** default constructor */
	public AbstractFiles() {
	}

	/** full constructor */
	public AbstractFiles(String id, String fileLocation, String creatorEmail,
			String fileStatus, String updateDate) {
		this.id = id;
		this.fileLocation = fileLocation;
		this.creatorEmail = creatorEmail;
		this.fileStatus = fileStatus;
		this.updateDate = updateDate;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileLocation() {
		return this.fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getCreatorEmail() {
		return this.creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public String getFileStatus() {
		return this.fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

}
