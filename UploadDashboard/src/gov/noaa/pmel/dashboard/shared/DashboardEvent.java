/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Base class for QCEvent and WoceEvent.  
 * Note that the id field is ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class DashboardEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = 7627175617050451698L;

	Long id;
	Date flagDate;
	String expocode;
	String version;
	String username;
	String realname;
	String comment;

	/**
	 * Creates an empty flag
	 */
	public DashboardEvent() {
		id = 0L;
		flagDate = DashboardUtils.DATE_MISSING_VALUE;
		expocode = DashboardUtils.STRING_MISSING_VALUE;
		version = DashboardUtils.STRING_MISSING_VALUE;
		username = DashboardUtils.STRING_MISSING_VALUE;
		realname = DashboardUtils.STRING_MISSING_VALUE;
		comment = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the id; never null, 
	 * 		but may be zero if not assigned
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id 
	 * 		the id to set; 
	 * 		if null, zero is assigned
	 */
	public void setId(Long id) {
		if ( id == null )
			this.id = 0L;
		else
			this.id = id;
	}

	/**
	 * @return 
	 * 		the date of the flag; never null 
	 * 		but may be {@link DashboardUtils#DATE_MISSING_VALUE}
	 */
	public Date getFlagDate() {
		return flagDate;
	}

	/**
	 * @param flagDate 
	 * 		the date of the flag to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setFlagDate(Date flagDate) {
		if ( flagDate == null )
			this.flagDate = DashboardUtils.DATE_MISSING_VALUE;
		else
			this.flagDate = flagDate;
	}

	/**
	 * @return 
	 * 		the expocode; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the expocode to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.expocode = expocode;
	}

	/**
	 * @return 
	 * 		the data collection version; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version 
	 * 		the data collection version to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setVersion(String version) {
		if ( version == null )
			this.version = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.version = version;
	}

	/**
	 * @return 
	 * 		the reviewer username; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the reviewer username to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setUsername(String username) {
		if ( username == null )
			this.username = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.username = username;
	}

	/**
	 * @return 
	 * 		the reviewer's actual name; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getRealname() {
		return realname;
	}

	/**
	 * @param realname 
	 * 		the reviewer's actual name to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setRealname(String realname) {
		if ( realname == null )
			this.realname = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.realname = realname;
	}

	/**
	 * @return 
	 * 		the comment; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment 
	 * 		the comment to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setComment(String comment) {
		if ( comment == null )
			this.comment = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.comment = comment;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = flagDate.hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + version.hashCode();
		result = result * prime + username.hashCode();
		result = result * prime + realname.hashCode();
		result = result * prime + comment.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DashboardEvent) )
			return false;
		DashboardEvent other = (DashboardEvent) obj;

		if ( ! flagDate.equals(other.flagDate) )
			return false;
		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! version.equals(other.version) )
			return false;
		if ( ! username.equals(other.username) )
			return false;
		if ( ! realname.equals(other.realname) )
			return false;
		if ( ! comment.equals(other.comment) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DashboardEvent" +
				"[\n    id=" + id.toString() +
				",\n    flagDate=" + flagDate.toString() + 
				",\n    expocode=" + expocode + 
				",\n    version=" + version.toString() + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
