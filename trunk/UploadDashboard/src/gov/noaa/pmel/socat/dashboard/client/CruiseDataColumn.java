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
 * 
 * @author Karl Smith
 */
public class CruiseDataColumn {

	DashboardCruise cruise;
	int columnIndex;

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
			// SelectionCell needs a List, not just a Collection
			SelectionCell typeSelectCell = new SelectionCell(new ArrayList<String>(
					DashboardUtils.STD_HEADER_NAMES.values()));
			@Override
			public SelectionCell getCell() {
				return typeSelectCell;
			}
			@Override
			public FieldUpdater<CruiseDataColumn,String> getFieldUpdater() {
				return new FieldUpdater<CruiseDataColumn,String>() {
					@Override
					public void update(int index, 
										CruiseDataColumn dataCol, String value) {
						// Note: index is the row index of the cell in a table 
						//       column where it is normally used.
						// Ignore this callback if a null String value is given
						if ( value == null )
							return;
						// Assign the data type corresponding to this String value
						for ( Entry<CruiseDataColumnType,String> stdNameEntry : 
							DashboardUtils.STD_HEADER_NAMES.entrySet() ) {
							if ( value.equals(stdNameEntry.getValue()) ) {
								dataCol.cruise.getDataColTypes().set(
										dataCol.columnIndex, stdNameEntry.getKey());
								return;
							}
						}
					}
				};
			}
			@Override
			public String getValue(CruiseDataColumn dataCol) {
				return DashboardUtils.STD_HEADER_NAMES.get(
						dataCol.cruise.getDataColTypes().get(dataCol.columnIndex));
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
