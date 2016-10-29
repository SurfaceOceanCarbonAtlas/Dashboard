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
public class WoceType implements Serializable, IsSerializable, Comparable<WoceType> {

	private static final long serialVersionUID = 267650346106336479L;

	private Integer columnIndex;
	private Integer rowIndex;
	private String woceName;

	/**
	 * Create with a 
	 * 		columnIndex of {@link DashboardUtils#INT_MISSING_VALUE}
	 * 		rowIndex of {@link DashboardUtils#INT_MISSING_VALUE}
	 * 		woceName of {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public WoceType() {
		columnIndex = DashboardUtils.INT_MISSING_VALUE;
		rowIndex = DashboardUtils.INT_MISSING_VALUE;
		woceName = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * Create with given rowIndex and woceName.  Equivalent to calling
	 * 		{@link #setRColumnIndex(Integer)} with columnIndex and 
	 * 		{@link #setRowIndex(Integer)} with rowIndex and 
	 * 		{@link #setWoceName(String)} with woceName.
	 */
	public WoceType(Integer columnIndex, Integer rowIndex, String woceName) {
		setColumnIndex(columnIndex);
		setRowIndex(rowIndex);
		setWoceName(woceName);
	}

	/**
	 * @return 
	 * 		the index of the row for this WOCE flag;
	 * 		never null, but may be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @param columnIndex 
	 * 		the index of the column to set for this WOCE flag;
	 * 		if null {@link DashboardUtils#INT_MISSING_VALUE} will be assigned
	 */
	public void setColumnIndex(Integer columnIndex) {
		if ( columnIndex != null )
			this.columnIndex = columnIndex;
		else
			this.columnIndex = DashboardUtils.INT_MISSING_VALUE;
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
	public int compareTo(WoceType other) {
		int result = this.columnIndex.compareTo(other.columnIndex);
		if ( result != 0 )
			return result;
		result = this.rowIndex.compareTo(other.rowIndex);
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
		int result = columnIndex.hashCode();
		result = prime * result + rowIndex.hashCode();
		result = prime * result + woceName.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( ! (obj instanceof WoceType) )
			return false;

		WoceType other = (WoceType) obj;
		if ( ! columnIndex.equals(other.columnIndex) )
			return false;
		if ( ! rowIndex.equals(other.rowIndex) )
			return false;
		if ( ! woceName.equals(other.woceName) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WoceType[" +
				"columnIndex = " + columnIndex.toString() + ", " + 
				"rowIndex=" + rowIndex.toString() + ", " + 
				"woceName=" + woceName + "]";
	}

}
