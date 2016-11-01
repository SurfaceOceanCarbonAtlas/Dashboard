/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a WOCE flag.
 * 
 * @author Karl Smith
 */
public class WoceType implements Serializable, IsSerializable, Comparable<WoceType> {

	private static final long serialVersionUID = 6339363961788956859L;

	protected String woceName;
	protected Integer columnIndex;
	protected Integer rowIndex;

	/**
	 * Create with a 
	 * 		woceName of {@link DashboardUtils#STRING_MISSING_VALUE}.
	 * 		columnIndex of {@link DashboardUtils#INT_MISSING_VALUE},
	 * 		rowIndex of {@link DashboardUtils#INT_MISSING_VALUE}, and
	 */
	public WoceType() {
		woceName = DashboardUtils.STRING_MISSING_VALUE;
		columnIndex = DashboardUtils.INT_MISSING_VALUE;
		rowIndex = DashboardUtils.INT_MISSING_VALUE;
	}

	/**
	 * Create with given column index, row index, and woceName.
	 * Equivalent to calling
	 * 		{@link #setWoceName(String)} with woceName,
	 * 		{@link #setColumnIndex(Integer)} with columnIndex, and
	 * 		{@link #setRowIndex(Integer)} with rowIndex.
	 */
	public WoceType(String woceName, Integer columnIndex, Integer rowIndex) {
		setWoceName(woceName);
		setColumnIndex(columnIndex);
		setRowIndex(rowIndex);
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

	@Override
	public int compareTo(WoceType other) {
		int result = this.woceName.compareTo(other.woceName);
		if ( result != 0 )
			return result;
		result = this.columnIndex.compareTo(other.columnIndex);
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
		int result =  woceName.hashCode();
		result = prime * result + columnIndex.hashCode();
		result = prime * result + rowIndex.hashCode();
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
		if ( ! woceName.equals(other.woceName) )
			return false;
		if ( ! columnIndex.equals(other.columnIndex) )
			return false;
		if ( ! rowIndex.equals(other.rowIndex) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WoceType[" +
				"woceName=" + woceName + 
				"columnIndex = " + columnIndex.toString() + ", " + 
				"rowIndex=" + rowIndex.toString() + ", " + 
				"]";
	}

}
