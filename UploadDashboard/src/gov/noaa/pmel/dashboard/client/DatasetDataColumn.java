/**
 * 
 */
package gov.noaa.pmel.dashboard.client;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;

import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Class for creating a CompositeCell Header for a cruise data column.
 * The cell includes a selection box for specifying the column type 
 * with units, and a text input for specifying a missing value.
 * 
 * @author Karl Smith
 */
public class DatasetDataColumn {

	static final String DEFAULT_MISSING_VALUE = "(default missing values)";

	// List of all known user data column types and selected units
	private ArrayList<DataColumnType> knownTypeUnitList;
	// List of "<name> [ <unit> ]" strings for all the known user data column types and selected units
	private ArrayList<String> typeUnitStringList;
	// Dataset associated with this instance
	private DashboardDataset cruise;
	// Dataset data column index associated with this instance
	private int columnIndex;
	// Header associated with this instance
	private Header<DatasetDataColumn> columnHeader;
	// Flag that something in the column header has hasChanged
	private boolean hasChanged;

	/**
	 * Specifies a data column of a DashboardDataset.
	 * 
	 * @param knownUserTypes
	 * 		list of all known data column types
	 * @param cruise
	 * 		cruise to associate with this instance
	 * @param column
	 * 		index of the cruise data column to associate with this instance
	 */
	DatasetDataColumn(ArrayList<DataColumnType> knownUserTypes, DashboardDataset cruise, int columnIndex) {
		knownTypeUnitList = new ArrayList<DataColumnType>(2 * knownUserTypes.size());
		for ( DataColumnType dataType : knownUserTypes ) {
			for (int k = 0; k < dataType.getUnits().size(); k++) {
				DataColumnType dctype = dataType.duplicate();
				dctype.setSelectedUnitIndex(k);
				knownTypeUnitList.add(dctype);
			}
		}
		typeUnitStringList = new ArrayList<String>(knownTypeUnitList.size());
		for ( DataColumnType dctype : knownTypeUnitList ) {
			String displayName = dctype.getDisplayName();
			String unit = dctype.getUnits().get(dctype.getSelectedUnitIndex());
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(unit) ) {
				typeUnitStringList.add(displayName);
			}
			else {
				typeUnitStringList.add(displayName + " [ " + unit + " ]");
			}
		}
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
	Header<DatasetDataColumn> getHeader() {
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
	private Header<DatasetDataColumn> createHeader() {

		// Create the TextCell giving the column name given by the user
		HasCell<DatasetDataColumn,String> userNameCell = new HasCell<DatasetDataColumn,String>() {
			@Override
			public TextCell getCell() {
				// Return a TextCell which is rendered as a block-level element
				return new TextCell() {
					@Override
					public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
						super.render(context, value, sb);
						sb.appendHtmlConstant("<br />");
					}
				};
			}
			@Override
			public FieldUpdater<DatasetDataColumn,String> getFieldUpdater() {
				return null;
			}
			@Override
			public String getValue(DatasetDataColumn dataCol) {
				return dataCol.cruise.getUserColNames().get(dataCol.columnIndex);
			}
		};

		// Create the SelectionCell listing the known standard headers
		HasCell<DatasetDataColumn,String> stdNameCell = new HasCell<DatasetDataColumn,String>() {
			@Override
			public SelectionCell getCell() {
				// Create a list of all the standard column headers with units;
				// render as a block-level element
				return new SelectionCell(typeUnitStringList) {
					@Override
					public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
						super.render(context, value, sb);
						sb.appendHtmlConstant("<br />");
					}
				};
			}
			@Override
			public FieldUpdater<DatasetDataColumn,String> getFieldUpdater() {
				return new FieldUpdater<DatasetDataColumn,String>() {
					@Override
					public void update(int index, DatasetDataColumn dataCol, String value) {
						// Note: index is the row index of the cell in a table 
						// column where it is normally used; not of use here.

						// Ignore this callback if value is null
						if ( value == null )
							return;

						// Find the data type and units corresponding to header
						int idx = typeUnitStringList.indexOf(value);
						// Ignore this callback if value is not found - should not happen
						if ( idx < 0 )
							return;
						DataColumnType newType = knownTypeUnitList.get(idx).duplicate();

						// Assign the data type and units
						ArrayList<DataColumnType> cruiseColTypes = dataCol.cruise.getDataColTypes();
						DataColumnType oldType = cruiseColTypes.get(dataCol.columnIndex);
						newType.setSelectedMissingValue(oldType.getSelectedMissingValue());
						if ( newType.equals(oldType) )
							return;
						hasChanged = true;
						cruiseColTypes.set(dataCol.columnIndex, newType);
					}
				};
			}
			@Override
			public String getValue(DatasetDataColumn dataCol) {
				// Find this column type with units
				DataColumnType dctype = dataCol.cruise.getDataColTypes().get(dataCol.columnIndex);
				// Ignore the missing value for this comparison
				String missVal = dctype.getSelectedMissingValue();
				dctype.setSelectedMissingValue(null);
				int idx = knownTypeUnitList.indexOf(dctype);
				dctype.setSelectedMissingValue(missVal);
				if ( idx < 0 ) {
					// Not a recognized column type with units; set to unknown
					idx = knownTypeUnitList.indexOf(DashboardUtils.UNKNOWN);
					if ( idx < 0 )
						throw new RuntimeException("Unexpected failure to find the UNASSIGNED data column");
				}
				// Return the header for this column type with units
				return typeUnitStringList.get(idx);
			}
		};

		// Create the TextInputCell allowing the user to specify the missing value
		HasCell<DatasetDataColumn,String> missValCell = new HasCell<DatasetDataColumn,String>() {
			@Override
			public TextInputCell getCell() {
				return new TextInputCell();
				// TODO: capture start-edit events to erase DEFAULT_MISSING_VALUE
			}
			@Override
			public FieldUpdater<DatasetDataColumn,String> getFieldUpdater() {
				return new FieldUpdater<DatasetDataColumn,String>() {
					@Override
					public void update(int index, DatasetDataColumn dataCol, String value) {
						if ( value == null ) {
							// ignore this callback if the value is null
							return;
						}
						// Set this missing value
						value = value.trim();
						if ( value.equals(DEFAULT_MISSING_VALUE) )
							value = "";
						DataColumnType dctype = dataCol.cruise.getDataColTypes().get(dataCol.columnIndex);
						String oldValue = dctype.getSelectedMissingValue();
						if ( value.equals(oldValue) )
							return;
						dctype.setSelectedMissingValue(value);
						hasChanged = true;
					}
				};
			}
			@Override
			public String getValue(DatasetDataColumn dataCol) {
				DataColumnType dctype = dataCol.cruise.getDataColTypes().get(dataCol.columnIndex);
				String value = dctype.getSelectedMissingValue();
				if ( (value == null) || value.isEmpty() )
					value = DEFAULT_MISSING_VALUE;
				return value;
			}
		};
		// Create the CompositeCell to be used for the header				
		CompositeCell<DatasetDataColumn> compCell = 
			new CompositeCell<DatasetDataColumn>(
				new ArrayList<HasCell<DatasetDataColumn,?>>(
					Arrays.asList(userNameCell, stdNameCell, missValCell)));

		// Create and return the Header
		Header<DatasetDataColumn> headerCell = new Header<DatasetDataColumn>(compCell) {
			@Override
			public DatasetDataColumn getValue() {
				return DatasetDataColumn.this;
			}
		};
		return headerCell;
	}

}
