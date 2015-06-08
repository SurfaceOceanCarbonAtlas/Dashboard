/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;

/**
 * Class for creating a CompositeCell Header for a cruise data column.
 * The cell includes a selection box for specifying the column type 
 * with units, and a text input for specifying a missing value.
 * 
 * @author Karl Smith
 */
public class CruiseDataColumn {

	static final String DEFAULT_MISSING_VALUE = "(default missing values)";

	// Class to deal with a data column type and unit pair
	private static class TypeUnits {
		DataColumnType type;
		String units;
		TypeUnits(DataColumnType type, String units) {
			if ( type != null )
				this.type = type;
			else
				this.type = DataColumnType.UNKNOWN;
			if ( units != null )
				this.units = units;
			else
				this.units = "";
		}
		@Override
		public int hashCode() {
			int result = 37 * type.hashCode() + units.hashCode();
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if ( this == obj )
				return true;
			if ( obj == null )
				return false;
			if ( ! (obj instanceof TypeUnits) )
				return false;
			TypeUnits other = (TypeUnits) obj;
			if ( type != other.type )
				return false;
			if ( ! units.equals(other.units) )
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "TypeUnits[type=" + type.name() + ",units=" + units + "]";
		}
	}

	// Static list of standard data column types with units
	private static final ArrayList<TypeUnits> STD_TYPE_UNITS = 
												new ArrayList<TypeUnits>();

	// Static list of headers for the standard data column types with units
	private static final ArrayList<String> STD_TYPE_UNITS_HEADERS = 
												new ArrayList<String>();

	static {
		// Generate the standard data column types with units
		for ( Entry<DataColumnType,ArrayList<String>> entry : 
								DashboardUtils.STD_DATA_UNITS.entrySet() ) {
			DataColumnType type = entry.getKey();
			for ( String units : entry.getValue() ) {
				STD_TYPE_UNITS.add(new TypeUnits(type, units));
			}
		}

		// Generate the headers from the standard data column types with units
		for ( TypeUnits descr : STD_TYPE_UNITS ) {
			String header = DashboardUtils.STD_HEADER_NAMES.get(descr.type);
			if ( ! descr.units.isEmpty() )
				header += " [ " + descr.units + " ]";
			STD_TYPE_UNITS_HEADERS.add(header);
		}
	}

	// Cruise associated with this instance
	private DashboardCruise cruise;
	// Cruise data column index associated with this instance
	private int columnIndex;
	// Header associated with this instance
	private Header<CruiseDataColumn> columnHeader;
	// Flag that something in the column header has hasChanged
	private boolean hasChanged;

	/**
	 * Specifies a data column of a DashboardCruise.
	 * 
	 * @param cruise
	 * 		cruise to associate with this instance
	 * @param columnIndex
	 * 		index of the cruise data column to associate with this instance
	 */
	CruiseDataColumn(DashboardCruise cruise, int columnIndex) {
		this.cruise = cruise;
		this.columnIndex = columnIndex;
		this.columnHeader = createHeader();
		this.hasChanged = false;
	}

	/**
	 * Returns the header for this cruise data column.  This header is a 
	 * CompositeCell consisting of a TextCell, for displaying the user-
	 * provided column name, a SelectionCell, for selecting the standard 
	 * column type, a TextCell, and a TextInputCell, for the user to 
	 * specify a missing value.
	 */
	Header<CruiseDataColumn> getHeader() {
		return columnHeader;
	}

	/**
	 * @return
	 * 		if the values shown in this header have changed
	 */
	boolean hasChanged() {
		return hasChanged;
	}

	/**
	 * Creates the header for this cruise data column.
	 */
	private Header<CruiseDataColumn> createHeader() {
		// Create the TextCell giving the column name given by the user
		HasCell<CruiseDataColumn,String> userNameCell = 
									new HasCell<CruiseDataColumn,String>() {
			@Override
			public TextCell getCell() {
				// Return a TextCell which is rendered as a block-level element
				return new TextCell() {
					@Override
					public void render(Cell.Context context, String value, 
										SafeHtmlBuilder sb) {
						super.render(context, value, sb);
						sb.appendHtmlConstant("<br />");
					}
				};
			}
			@Override
			public FieldUpdater<CruiseDataColumn,String> getFieldUpdater() {
				return null;
			}
			@Override
			public String getValue(CruiseDataColumn dataCol) {
				return dataCol.cruise.getUserColNames()
									 .get(dataCol.columnIndex);
			}
		};

		// Create the SelectionCell listing the known standard headers
		HasCell<CruiseDataColumn,String> stdNameCell = 
									new HasCell<CruiseDataColumn,String>() {
			@Override
			public SelectionCell getCell() {
				// Create a list of all the standard column headers with units;
				// render as a block-level element
				return new SelectionCell(STD_TYPE_UNITS_HEADERS) {
					@Override
					public void render(Cell.Context context, String value,
										SafeHtmlBuilder sb) {
						super.render(context, value, sb);
						sb.appendHtmlConstant("<br />");
					}
				};
			}
			@Override
			public FieldUpdater<CruiseDataColumn,String> getFieldUpdater() {
				return new FieldUpdater<CruiseDataColumn,String>() {
					@Override
					public void update(int index, 
										CruiseDataColumn dataCol, String value) {
						// Note: index is the row index of the cell in a table 
						// column where it is normally used; not of use here.

						// Ignore this callback if value is null
						if ( value == null )
							return;

						// Find the data type and units corresponding to header
						int idx = STD_TYPE_UNITS_HEADERS.indexOf(value);
						// Ignore this callback if value is not found - should not happen
						if ( idx < 0 )
							return;

						TypeUnits descr = STD_TYPE_UNITS.get(idx);
						// Assign the data type and units directly in the lists 
						// for the cruise instance
						DataColumnType oldType = dataCol.cruise.getDataColTypes()
								.set(dataCol.columnIndex, descr.type);
						if ( ! descr.type.equals(oldType) )
							hasChanged = true;
						String oldUnits = dataCol.cruise.getDataColUnits()
								.set(dataCol.columnIndex, descr.units);
						if ( ! descr.units.equals(oldUnits) )
							hasChanged = true;
					}
				};
			}
			@Override
			public String getValue(CruiseDataColumn dataCol) {
				// Find this column type with units
				DataColumnType type = 
						dataCol.cruise.getDataColTypes().get(dataCol.columnIndex);
				String units = 
						dataCol.cruise.getDataColUnits().get(dataCol.columnIndex);
				int idx = STD_TYPE_UNITS.indexOf(new TypeUnits(type, units));
				if ( idx < 0 ) {
					// Not a recognized column type with units; set to unknown
					idx = STD_TYPE_UNITS.indexOf(
							new TypeUnits(DataColumnType.UNKNOWN, ""));
					if ( idx < 0 )
						throw new RuntimeException(
								"Unexpected invalid index for DataColumnType.UNKNOWN");
				}
				// Return the header for this column type with units
				return STD_TYPE_UNITS_HEADERS.get(idx);
			}
		};

		// Create the TextInputCell allowing the user to specify the missing value
		HasCell<CruiseDataColumn,String> missValCell = 
									new HasCell<CruiseDataColumn,String>() {
			@Override
			public TextInputCell getCell() {
				return new TextInputCell();
				// TODO: capture start-edit events to erase DEFAULT_MISSING_VALUE
			}
			@Override
			public FieldUpdater<CruiseDataColumn,String> getFieldUpdater() {
				return new FieldUpdater<CruiseDataColumn,String>() {
					@Override
					public void update(int index, 
									   CruiseDataColumn dataCol, String value) {
						if ( value == null ) {
							// ignore this callback if the value is null
							return;
						}
						// Set this missing value
						value = value.trim();
						if ( value.equals(DEFAULT_MISSING_VALUE) )
							value = "";
						String oldValue = dataCol.cruise.getMissingValues()
									  .set(dataCol.columnIndex, value);
						if ( ! value.equals(oldValue) )
							hasChanged = true;
					}
				};
			}
			@Override
			public String getValue(CruiseDataColumn dataCol) {
				String value = dataCol.cruise.getMissingValues()
											 .get(dataCol.columnIndex);
				if ( (value == null) || value.isEmpty() )
					value = DEFAULT_MISSING_VALUE;
				return value;
			}
		};
		// Create the CompositeCell to be used for the header				
		CompositeCell<CruiseDataColumn> compCell = 
			new CompositeCell<CruiseDataColumn>(
				new ArrayList<HasCell<CruiseDataColumn,?>>(
					Arrays.asList(userNameCell, stdNameCell, missValCell)));

		// Create and return the Header
		Header<CruiseDataColumn> headerCell = new Header<CruiseDataColumn>(compCell) {
			@Override
			public CruiseDataColumn getValue() {
				return CruiseDataColumn.this;
			}
		};
		return headerCell;
	}

}
