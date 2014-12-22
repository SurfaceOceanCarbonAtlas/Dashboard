/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.beans;


public class Editor {

	String aUser;
	String profile;	
	


	String adminUser;
	
	
	String fgdcText;
	String homePath;
	// textbox
	String userName;
	String[] files;
	String[] users;
	public String[] getUsers() {
		return users;
	}

	public void setUsers(String[] users) {
		this.users = users;
	}


	String mdFile;	
	
	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	
	
	

	
	
	public String getadminUser() {
		return adminUser;
	}

	public void setadminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getaUser() {
		return aUser;
	}

	public void setaUser(String aUser) {
		this.aUser = aUser;
	}

	
	Metadata_Editor med;

	public Metadata_Editor getMed() {
		return med;
	}

	public void setMed(Metadata_Editor med) {
		this.med = med;
	}

	

	public String getHomePath() {
		return homePath;
	}

	public void setHomePath(String homePath) {
		this.homePath = homePath;
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}

	

	public String getMdFile() {
		return mdFile;
	}

	public void setMdFile(String mdFile) {
		this.mdFile = mdFile;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFgdcText() {
		return fgdcText;
	}

	public void setFgdcText(String fgdcText) {
		this.fgdcText = fgdcText;
	}

	

	

	
}
