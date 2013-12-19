/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;

/**
 * Class for creating a CompositeCell Header for a cruise data column.
 * The cell includes a selection box for specifying the column type 
 * including the units.
 * 
 * @author Karl Smith
 */
public class CruiseDataColumn {

	// Class to deal with pairs of data column types and units
	private static class TypeUnits {
		CruiseDataColumnType type;
		String units;
		TypeUnits(CruiseDataColumnType type, String units) {
			if ( type != null )
				this.type = type;
			else
				this.type = CruiseDataColumnType.UNKNOWN;
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
	}
	// Static list of standard data column types with units
	private static final ArrayList<TypeUnits> STD_TYPE_UNITS = 
												new ArrayList<TypeUnits>();
	static {
		// Move SUPPLEMENTAL from the tail to the head of the list.
		// Do not move SUPPLEMENTAL in the enum list, 
		// since we want that data to be at the end when sorted.
		STD_TYPE_UNITS.add(
				new TypeUnits(CruiseDataColumnType.SUPPLEMENTAL, ""));
		// Add everything else in the enumerated type order
		for ( Entry<CruiseDataColumnType,ArrayList<String>> entry : 
								DashboardUtils.STD_DATA_UNITS.entrySet() ) {
			CruiseDataColumnType type = entry.getKey();
			if ( type != CruiseDataColumnType.SUPPLEMENTAL ) {
				for ( String units : entry.getValue() ) {
					STD_TYPE_UNITS.add(new TypeUnits(type, units));
				}
			}
		}
	}
	// Static list of headers for the standard data column types with units
	private static final ArrayList<String> STD_TYPE_UNITS_HEADERS = 
								new ArrayList<String>(STD_TYPE_UNITS.size());
	static {
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
	}

	/**
	 * Creates a Header for with the cruise data column associated 
	 * with this instance.  This Header is a CompositeCell consisting 
	 * of a TextCell, for displaying the user-provided column name, 
	 * and a SelectionCell, for selecting the standard column type.
	 * 
	 * @param cruise
	 * 		cruise to associate with this header
	 * @param columnIndex
	 * 		index of the data column in the given cruise to associate 
	 * 		with this header
	 */
	Header<CruiseDataColumn> createHeader() {

		// Create the TextCell giving the column name given by the user
		HasCell<CruiseDataColumn,String> userNameCell = 
				new HasCell<CruiseDataColumn,String>() {
			// Create a TextCell which is rendered as a block-level element
			TextCell userTextCell = new TextCell() {
				@Override
				public void render(Cell.Context context, String value, 
									SafeHtmlBuilder sb) {
					sb.appendHtmlConstant("<div>");
					super.render(context, value, sb);
					sb.appendHtmlConstant("</div>");
				}
			};
			@Override
			public TextCell getCell() {
				return userTextCell;
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
				// Create a list of all the standard column headers with units
				return new SelectionCell(STD_TYPE_UNITS_HEADERS);
			}
			@Override
			public FieldUpdater<CruiseDataColumn,String> getFieldUpdater() {
				return new FieldUpdater<CruiseDataColumn,String>() {
					@Override
					public void update(int index, 
										CruiseDataColumn dataCol, String value) {
						// Note: index is the row index of the cell in a table 
						// column where it is normally used; not of use here.

						// Find the data type and units corresponding to header
						int idx = STD_TYPE_UNITS_HEADERS.indexOf(value);
						if ( idx < 0 ) {
							// Ignore this callback if value is not found, 
							// probably because value is null
							return;
						}
						TypeUnits descr = STD_TYPE_UNITS.get(idx);
						// Assign the data type and units directly in the lists 
						// for the cruise instance
						dataCol.cruise.getDataColTypes()
									  .set(dataCol.columnIndex, descr.type);
						dataCol.cruise.getDataColUnits()
									  .set(dataCol.columnIndex, descr.units);
					}
				};
			}
			@Override
			public String getValue(CruiseDataColumn dataCol) {
				// Find this column type with units
				CruiseDataColumnType type = 
						dataCol.cruise.getDataColTypes().get(dataCol.columnIndex);
				String units = 
						dataCol.cruise.getDataColUnits().get(dataCol.columnIndex);
				int idx = STD_TYPE_UNITS.indexOf(new TypeUnits(type, units));
				if ( idx < 0 ) {
					// Not a recognized column type with units; set to unknown
					idx = STD_TYPE_UNITS.indexOf(
							new TypeUnits(CruiseDataColumnType.UNKNOWN, ""));
				}
				// Return the header for this column type with units
				return STD_TYPE_UNITS_HEADERS.get(idx);
			}
		};

		// Create the CompositeCell to be used for the header
		ArrayList<HasCell<CruiseDataColumn,?>> cellList = 
				new ArrayList<HasCell<CruiseDataColumn,?>>(2);
		cellList.add(userNameCell);
		cellList.add(stdNameCell);
		CompositeCell<CruiseDataColumn> compCell =
				new CompositeCell<CruiseDataColumn>(cellList);

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
