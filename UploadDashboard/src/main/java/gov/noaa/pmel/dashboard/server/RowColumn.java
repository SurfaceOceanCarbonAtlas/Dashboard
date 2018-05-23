package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.actions.DatasetChecker;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * General purpose row/column index pair.
 * Ordering is such that rows with the same column number are next to each other (so first on column, then on row).
 */
public class RowColumn implements Comparable<RowColumn> {
    Integer row;
    Integer column;

    /**
     * @param rowIndex
     *  row index to use; if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
     * @param columnIndex
     *  column index to use; if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
     */
    public RowColumn(Integer rowIndex, Integer columnIndex) {
        if ( rowIndex == null )
            row = DashboardUtils.INT_MISSING_VALUE;
        else
            row = rowIndex;
        if ( columnIndex == null )
            column = DashboardUtils.INT_MISSING_VALUE;
        else
            column = columnIndex;
    }

    @Override
    public int compareTo(RowColumn other) {
        int result = column.compareTo(other.column);
        if ( result != 0 )
            return result;
        result = row.compareTo(other.row);
        if ( result != 0 )
            return result;
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = column.hashCode();
        result = prime * result + row.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !(obj instanceof RowColumn) )
            return false;
        RowColumn other = (RowColumn) obj;
        if ( ! column.equals(other.column) )
            return false;
        if ( ! row.equals(other.row) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RowColumn[row=" + row.toString() + ", column=" + column.toString() + "]";
    }

}
