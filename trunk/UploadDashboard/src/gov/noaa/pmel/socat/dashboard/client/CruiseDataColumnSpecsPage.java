/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecsService;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecsServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

/**
 * Page for specifying the data column types in a DashboardCruiseWithData.
 * A new page needs to be created each time since the number of data columns
 * can vary with each cruise. 
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnSpecsPage extends Composite {

	private static final int NUM_ROWS_PER_GRID_PAGE = 15;

	private static final String LOGOUT_TEXT = "Logout";
	private static final String SUBMIT_TEXT = "Submit Column Types";
	private static final String CANCEL_TEXT = "Return to Cruise List";

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String CRUISE_INTRO = "Cruise: ";

	private static final String GET_COLUMN_SPECS_FAIL_MSG = 
			"Unable to obtaining the cruise column types";
	private static final String SUBMIT_FAIL_MSG = 
			"Problems updating the cruise column types";
	private static final String MORE_DATA_FAIL_MSG = 
			"Problems obtaining more cruise data";
	private static final String GET_CRUISE_LIST_FAIL_MSG = 
			"Problems obtaining the latest cruise listing";
	private static final String SUBMIT_SUCCESS_MSG = 
			"Columns specifications updated for cruise: ";

	interface CruiseDataColumnSpecsPageUiBinder 
			extends UiBinder<Widget, CruiseDataColumnSpecsPage> {
	}

	private static CruiseDataColumnSpecsPageUiBinder uiBinder = 
			GWT.create(CruiseDataColumnSpecsPageUiBinder.class);

	private static CruiseDataColumnSpecsServiceAsync service = 
			GWT.create(CruiseDataColumnSpecsService.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField Label cruiseLabel;
	@UiField DataGrid<ArrayList<String>> dataGrid;
	@UiField Button submitButton;
	@UiField Button cancelButton;
	@UiField SimplePager gridPager;

	// Expocode of cruise associated with this page
	private String expocode;
	// These CruiseDataColumnType objects will be updated
	// by the user interacting with the header SelectionCell
	private ArrayList<CruiseDataColumnType> dataColTypes;
	// This maintains references to the CruiseDataColumnHeader 
	// objects which contain the mechanism for updating data types
	private ArrayList<CruiseDataColumnHeader> dataColHeaders;
	// Asynchronous data provider for the data grid 
	private AsyncDataProvider<ArrayList<String>> dataProvider;

	// Singleton instance of this page
	private static CruiseDataColumnSpecsPage singleton = null;

	/**
	 * Creates an empty cruise data column specification page.  
	 * Allows the user to update the data column types for a
	 * cruise when populated.
	 */
	private CruiseDataColumnSpecsPage() {
		initWidget(uiBinder.createAndBindUi(this));
		logoutButton.setText(LOGOUT_TEXT);
		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);
		expocode = "";
		dataColTypes = new ArrayList<CruiseDataColumnType>();
		dataColHeaders = new ArrayList<CruiseDataColumnHeader>();
		// Create the asynchronous data provider for the data grid
		dataProvider = new AsyncDataProvider<ArrayList<String>>() {
			@Override
			protected void onRangeChanged(HasData<ArrayList<String>> display) {
				// Ignore the call if there is no expocode assigned
				if ( expocode.isEmpty() )
					return;
				// Get the data for the cruise from the server
				final Range range = display.getVisibleRange();
				service.getCruiseData(DashboardLoginPage.getUsername(), 
						DashboardLoginPage.getPasshash(), 
						expocode, range.getStart(), range.getLength(), 
						new AsyncCallback<ArrayList<ArrayList<String>>>() {
					@Override
					public void onSuccess(ArrayList<ArrayList<String>> newData) {
						updateRowData(range.getStart(), newData);
					}
					@Override
					public void onFailure(Throwable ex) {
						Window.alert(MORE_DATA_FAIL_MSG + 
								" (" + ex.getMessage() + ")");
					}
				});
			}
		};
		dataProvider.addDataDisplay(dataGrid);
		// Assign the pager controlling which rows of the the data grid are shown
		gridPager.setDisplay(dataGrid);
	}

	/**
	 * Display the cruise data column specifications page for a cruise
	 * with the latest cruise data column specifications from the server.
	 * 
	 * @param expocode
	 * 		show the specifications for this cruise
	 * @param currentPage
	 * 		current page on the RootLayoutPanel to be removed when
	 * 		the cruise data column specifications page is available
	 */
	static void showPage(String expocode, final Composite currentPage) {
		service.getCruiseDataColumnSpecs(DashboardLoginPage.getUsername(), 
								DashboardLoginPage.getPasshash(), expocode, 
								new AsyncCallback<CruiseDataColumnSpecs>() {
			@Override
			public void onSuccess(CruiseDataColumnSpecs cruiseSpecs) {
				if ( cruiseSpecs != null ) {
					RootLayoutPanel.get().remove(currentPage);
					if ( singleton == null )
						singleton = new CruiseDataColumnSpecsPage();
					RootLayoutPanel.get().add(singleton);
					singleton.updateCruiseColumnSpecs(cruiseSpecs);
				}
				else {
					Window.alert(GET_COLUMN_SPECS_FAIL_MSG + 
						" (unexpected null cruise column specificiations)");
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(GET_COLUMN_SPECS_FAIL_MSG + 
						" (" + ex.getMessage() + ")");
			}
		});
	}

	/**
	 * Updates the data column specification page with the given
	 * column types and data.
	 * 
	 * @param cruiseSpecs
	 * 		current cruise data column type specifications and
	 * 		initial cruise data for display
	 */
	private void updateCruiseColumnSpecs(CruiseDataColumnSpecs cruiseSpecs) {
		userInfoLabel.setText(WELCOME_INTRO + DashboardLoginPage.getUsername());

		// Clear the expocode in case the data provider gets called while clearing
		expocode = "";
		// Delete any existing columns, headers, and types
		int k = dataGrid.getColumnCount();
		while ( k > 0 ) {
			k--;
			dataGrid.removeColumn(k);
		}
		dataColHeaders.clear();
		dataColTypes.clear();

		// Assign the new cruise information
		expocode = cruiseSpecs.getExpocode();
		cruiseLabel.setText(CRUISE_INTRO + expocode);

		// Rebuild the data grid using the provided CruiseDataColumnSpecs
		if ( cruiseSpecs.getColumnTypes().size() < 4 )
			throw new IllegalArgumentException(
					"Unexpected small number of data columns: " + 
					dataColTypes.size());
		dataColTypes.addAll(cruiseSpecs.getColumnTypes());
		dataColHeaders.ensureCapacity(dataColTypes.size());
		int minTableWidth = 0;
		for (k = 0; k < dataColTypes.size(); k++) {
			// TextColumn for displaying the data strings for this column
			ArrayListTextColumn dataColumn = new ArrayListTextColumn(k);

			// Object creating the Header cell for this data column
			CruiseDataColumnType colType = dataColTypes.get(k);
			CruiseDataColumnHeader dataHeader = 
							new CruiseDataColumnHeader(colType);
			// Maintain a reference to the CruiseDataColumnHeader object
			dataColHeaders.add(dataHeader);

			// Add this data column and the header to the grid
			dataGrid.addColumn(dataColumn, dataHeader.getHeader());

			// Get the width for this column
			int colWidth = 3;
			int len = colType.getUserHeaderName().length();
			if ( colWidth < len )
				colWidth = len;
			len = colType.getStdHeaderName().length();
			if ( colWidth < len )
				colWidth = len;
			for ( ArrayList<String> dataRow : cruiseSpecs.getDataValues() ) {
				if ( dataRow != null ) {
					String val = dataRow.get(k);
					if ( val != null ) {
						len = val.length();
						if ( colWidth < len )
							colWidth = len;

					}
				}
			}
			// Limit the column width in case URLs or filenames are given
			if ( colWidth > 16 )
				colWidth = 16;
			// Set the width of this column
			dataGrid.setColumnWidth(dataColumn, (double) colWidth, Style.Unit.EM);
			// Add this width to the minimum table width
			minTableWidth += colWidth;
		}
		// Make sure the table has some reasonable minimum width
		if ( minTableWidth < 45 )
			minTableWidth = 45;
		// Set the minimum table width
		dataGrid.setMinimumTableWidth((double) minTableWidth, Style.Unit.EM);
		// Update the data provider with the data in the CruiseDataColumnSpecs
		dataProvider.updateRowCount(cruiseSpecs.getNumRowsTotal(), true);
		dataProvider.updateRowData(0, cruiseSpecs.getDataValues());
		// Set the number of data rows to display in the grid.
		// This will refresh the view.
		dataGrid.setPageSize(NUM_ROWS_PER_GRID_PAGE);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(CruiseDataColumnSpecsPage.this);
		DashboardLogoutPage.showPage();
	}

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page and discard this page. 
		// (These pages are not kept by DashboardPageFactory.)  The cruise
		// listing may have been updated from previous actions on this page.
		DashboardCruiseListPage.showPage(
				CruiseDataColumnSpecsPage.this, GET_CRUISE_LIST_FAIL_MSG);
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		// Submit the updated data column types to the server.
		// This update invokes the SanityChecker on the data and
		// the results are then reported back to this page.
		CruiseDataColumnSpecs newSpecs = new CruiseDataColumnSpecs();
		newSpecs.setExpocode(expocode);
		newSpecs.setColumnTypes(dataColTypes);
		service.updateCruiseDataColumnSpecs(DashboardLoginPage.getUsername(), 
								DashboardLoginPage.getPasshash(), newSpecs, 
								new AsyncCallback<CruiseDataColumnSpecs>() {
			@Override
			public void onSuccess(CruiseDataColumnSpecs specs) {
				if ( specs != null ) {
					updateCruiseColumnSpecs(specs);
					Window.alert(SUBMIT_SUCCESS_MSG + specs.getExpocode());
				}
				else {
					Window.alert(SUBMIT_FAIL_MSG + 
							" (unexpected null cruise column specificiations)");
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(SUBMIT_FAIL_MSG + " (" + ex.getMessage() + ")");
			}
		});
	}

	/**
	 * TextColumn for displaying the value at a given index 
	 * of an ArrayList of Strings 
	 */
	private class ArrayListTextColumn extends TextColumn<ArrayList<String>> {
		private int colNum;
		/**
		 * Creates a TextColumn for an ArrayList of Strings that 
		 * displays the value at the given index in the ArrayList.
		 * @param colNum
		 * 		display data at this index of the ArrayList
		 */
		ArrayListTextColumn(int colNum) {
			super();
			this.colNum = colNum;
		}
		@Override
		public String getValue(ArrayList<String> dataRow) {
			if ( (dataRow != null) && (dataRow.size() > colNum) )
				return dataRow.get(colNum);
			else
				return "";
		}
	}

}
