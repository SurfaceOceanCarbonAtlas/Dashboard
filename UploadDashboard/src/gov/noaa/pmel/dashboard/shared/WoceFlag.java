/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a WOCE flag with the flag and the comment.
 * Primarily to combine and order all WOCE flags for a dataset.
 * 
 * @author Karl Smith
 */
public class WoceFlag extends WoceType implements Serializable, IsSerializable {

	private static final long serialVersionUID = 5479920975233735408L;

	protected Character flag;
	protected String comment;

	/**
	 * Create with a 
	 * 		flag of {@link DashboardUtils#CHAR_MISSING_VALUE},
	 * 		comment of {@link DashboardUtils#STRING_MISSING_VALUE},
	 * 		woceName of {@link DashboardUtils#STRING_MISSING_VALUE},
	 * 		columnIndex of {@link DashboardUtils#INT_MISSING_VALUE}, and
	 * 		rowIndex of {@link DashboardUtils#INT_MISSING_VALUE}.
	 */
	public WoceFlag() {
		super();
		flag = DashboardUtils.CHAR_MISSING_VALUE;
		comment = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * Create with given column index, row index, and woceName.  
	 * The flag is set to {@link DashboardUtils#CHAR_MISSING_VALUE} and
	 * the comment is set to {@link DashboardUtils#STRING_MISSING_VALUE}.
	 * Equivalent to calling
	 * 		{@link #setWoceName(String)} with woceName,
	 * 		{@link #setColumnIndex(Integer)} with columnIndex, and
	 * 		{@link #setRowIndex(Integer)} with rowIndex.
	 */
	public WoceFlag(String woceName, Integer columnIndex, Integer rowIndex) {
		super(woceName, columnIndex, rowIndex);
		flag = DashboardUtils.CHAR_MISSING_VALUE;
		comment = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the WOCE flag;
	 * 		never null but may be {@link DashboardUtils#CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getFlag() {
		return flag;
	}

	/**
	 * @param flag 
	 * 		the WOCE flag to set;
	 * 		if null, {@link DashboardUtils#CHAR_MISSING_VALUE} will be assigned
	 */
	public void setFlag(Character flag) {
		if ( flag != null )
			this.flag = flag;
		else
			this.flag = DashboardUtils.CHAR_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the WOCE comment;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment 
	 * 		the WOCE comment to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setComment(String comment) {
		if ( comment != null )
			this.comment = comment;
		else
			this.comment = DashboardUtils.STRING_MISSING_VALUE;
	}

	@Override
	public int compareTo(WoceType obj) {
		WoceFlag other;
		if ( obj instanceof WoceFlag )
			other = (WoceFlag) obj;
		else
			other = new WoceFlag(obj.woceName, obj.columnIndex, obj.rowIndex);

		int result = this.woceName.compareTo(other.woceName);
		if ( result != 0 )
			return result;
		result = this.flag.compareTo(other.flag);
		if ( result != 0 )
			return result;
		result = this.columnIndex.compareTo(other.columnIndex);
		if ( result != 0 )
			return result;
		result = this.comment.compareTo(other.comment);
		if ( result != 0 )
			return result;
		result = this.rowIndex.compareTo(other.rowIndex);
		if ( result != 0 )
			return result;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = prime * result + comment.hashCode();
		result = prime * result + flag.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( ! super.equals(obj) )
			return false;
		if ( ! (obj instanceof WoceFlag) )
			return false;

		WoceFlag other = (WoceFlag) obj;
		if ( ! comment.equals(other.comment) )
			return false;
		if ( ! flag.equals(other.flag) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WoceFlag[" +
			   "woceName=" + woceName + ", " +
			   "flag='" + flag.toString() + "', " +
			   "columnIndex=" + columnIndex.toString() + ", " +
			   "rowIndex=" + rowIndex.toString() + 
			   "comment=\"" + comment + "\", " +
			   "]";
	}
	
}
