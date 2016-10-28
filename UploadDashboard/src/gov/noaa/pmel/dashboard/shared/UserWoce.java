/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a user-provide WOCE flag.
 * 
 * @author Karl Smith
 */
public class UserWoce implements Serializable, IsSerializable, Comparable<UserWoce> {

	private static final long serialVersionUID = 3885130943241122491L;

	private Integer rowIndex;
	private String woceName;

	/**
	 * Create with a 
	 * 		rowIndex of {@link DashboardUtils#INT_MISSING_VALUE}
	 * 		woceName of {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public UserWoce() {
		rowIndex = DashboardUtils.INT_MISSING_VALUE;
		woceName = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * Create with given rowIndex and woceName.  Equivalent to calling
	 * 		{@link #setRowIndex(Integer)} with rowIndex and 
	 * 		{@link #setWoceName(String)} with woceName.
	 */
	public UserWoce(Integer rowIndex, String woceName) {
		setRowIndex(rowIndex);
		setWoceName(woceName);
	}

	/**
	 * @return 
	 * 		the index of the row for this WOCE flag;
	 * 		never null, but may be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getRowIndex() {
		return rowIndex;
	}

	/**
	 * @param rowIndex 
	 * 		the index of the row to set for this WOCE flag;
	 * 		if null {@link DashboardUtils#INT_MISSING_VALUE} will be assigned
	 */
	public void setRowIndex(Integer rowIndex) {
		if ( rowIndex != null )
			this.rowIndex = rowIndex;
		else
			this.rowIndex = DashboardUtils.INT_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the WOCE variable name for this WOCE flag;
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getWoceName() {
		return woceName;
	}

	/**
	 * @param woceName 
	 * 		the WOCE variable name to set for this WOCE flag;
	 * 		if null {@link DashboardUtils#STRING_MISSING_VALUE} will be assigned
	 */
	public void setWoceName(String woceName) {
		if ( woceName != null )
			this.woceName = woceName;
		else
			this.woceName = DashboardUtils.STRING_MISSING_VALUE;
	}

	@Override
	public int compareTo(UserWoce other) {
		int result = this.rowIndex.compareTo(other.rowIndex);
		if ( result != 0 )
			return result;
		result = this.woceName.compareTo(other.woceName);
		if ( result != 0 )
			return result;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = rowIndex.hashCode();
		result = prime * result + woceName.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( ! (obj instanceof UserWoce) )
			return false;

		UserWoce other = (UserWoce) obj;
		if ( ! rowIndex.equals(other.rowIndex) )
			return false;
		if ( ! woceName.equals(other.woceName) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserWoce[rowIndex=" + rowIndex.toString() + ", woceName=" + woceName + "]";
	}

}
