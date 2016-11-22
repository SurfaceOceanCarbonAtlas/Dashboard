/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a QC event giving a flag (or just a comment) on a region of a cruise. 
 * Note that the inherited id field is ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class QCEvent extends DashboardEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = 5810939408562187279L;

	protected Character flag;

	/**
	 * Creates an empty QC flag as a comment in the global region 
	 */
	public QCEvent() {
		super();
		flag = DashboardUtils.QC_COMMENT;
	}

	/**
	 * @return 
	 * 		the flag; never null
	 */
	public Character getFlag() {
		return flag;
	}

	/**
	 * @param flag 
	 * 		the flag to set; if null {@link DashboardUtils#QC_COMMENT} is assigned
	 */
	public void setFlag(Character flag) {
		if ( flag == null )
			this.flag = DashboardUtils.QC_COMMENT;
		else
			this.flag = flag;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + flag.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof QCEvent) )
			return false;
		QCEvent other = (QCEvent) obj;

		if ( ! super.equals(other) )
			return false;
		if ( ! flag.equals(other.flag) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "QCEvent" +
				"[\n    id=" + id.toString() +
				",\n    flag='" + flag.toString() + "'" +
				",\n    flagDate=" + flagDate.toString() + 
				",\n    expocode=" + expocode + 
				",\n    version=" + version.toString() + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
