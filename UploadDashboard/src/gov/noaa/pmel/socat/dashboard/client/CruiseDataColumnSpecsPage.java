/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecsService;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecsServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
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

	// Username associated with this page
	private String username;
	// Cruise associated with and updated by this page
	private DashboardCruise cruise;
	// List of CruiseDataColumn objects associated with the column Headers
	private ArrayList<CruiseDataColumn> cruiseDataCols;
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
		username = "";
		logoutButton.setText(LOGOUT_TEXT);
		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);
		cruise = new DashboardCruise();
		cruiseDataCols = new ArrayList<CruiseDataColumn>();
		// Create the asynchronous data provider for the data grid
		dataProvider = new AsyncDataProvider<ArrayList<String>>() {
			@Override
			protected void onRangeChanged(HasData<ArrayList<String>> display) {
				// Ignore the call if there is no expocode assigned
				if ( cruise.getExpocode().isEmpty() )
					return;
				// Get the data for the cruise from the server
				final Range range = display.getVisibleRange();
				service.getCruiseData(DashboardLoginPage.getUsername(), 
						DashboardLoginPage.getPasshash(), 
						cruise.getExpocode(), range.getStart(), range.getLength(), 
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
	 * Add this page to the page history list.
	 * 
	 * @param expocode
	 * 		show the specifications for this cruise
	 */
	static void showPage(String expocode) {
		service.getCruiseDataColumnSpecs(DashboardLoginPage.getUsername(), 
								DashboardLoginPage.getPasshash(), expocode, 
								new AsyncCallback<DashboardCruiseWithData>() {
			@Override
			public void onSuccess(DashboardCruiseWithData cruiseSpecs) {
				if ( cruiseSpecs != null ) {
					if ( singleton == null )
						singleton = new CruiseDataColumnSpecsPage();
					SocatUploadDashboard.get().updateCurrentPage(singleton);
					singleton.updateCruiseColumnSpecs(cruiseSpecs);
					History.newItem(PagesEnum.DATA_COLUMN_SPECS.name(), false);
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
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the current login username.
	 * Does not add this page to the page history list.
	 */
	static void redisplayPage() {
		// If never show before, or if the username does not match the 
		// current login username, show the login page instead
		if ( (singleton == null) || 
			 ! singleton.username.equals(DashboardLoginPage.getUsername()) )
			DashboardLoginPage.showPage();
		else
			SocatUploadDashboard.get().updateCurrentPage(singleton);
	}

	/**
	 * Updates the data column specification page with the given
	 * column types and data.
	 * 
	 * @param cruiseSpecs
	 * 		current cruise data column type specifications and
	 * 		initial cruise data for display
	 */
	private void updateCruiseColumnSpecs(DashboardCruiseWithData cruiseSpecs) {
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);

		// Clear the expocode in case the data provider gets called while clearing
		cruise.setExpocode(null);

		// Delete any existing columns and headers
		int k = dataGrid.getColumnCount();
		while ( k > 0 ) {
			k--;
			dataGrid.removeColumn(k);
		}
		// Clear the list of CruiseDataColumns
		cruiseDataCols.clear();

		// Assign the new cruise information needed by this page
		cruise.setNumDataRows(cruiseSpecs.getNumDataRows());
		cruise.setDataColTypes(cruiseSpecs.getDataColTypes());
		cruise.setUserColIndices(cruiseSpecs.getUserColIndices());
		cruise.setUserColNames(cruiseSpecs.getUserColNames());
		cruise.setDataColUnits(cruiseSpecs.getDataColUnits());
		cruise.setDataColDescriptions(cruiseSpecs.getDataColDescriptions());

		cruise.setExpocode(cruiseSpecs.getExpocode());
		cruiseLabel.setText(CRUISE_INTRO + cruise.getExpocode());

		// Rebuild the data grid using the provided CruiseDataColumnSpecs
		if ( cruise.getDataColTypes().size() < 4 )
			throw new IllegalArgumentException(
					"Unexpected small number of data columns: " + 
					cruise.getDataColTypes().size());
		int minTableWidth = 2;
		for (k = 0; k < cruise.getDataColTypes().size(); k++) {
			// TextColumn for displaying the data strings for this column
			ArrayListTextColumn dataColumn = new ArrayListTextColumn(k);

			// CruiseDataColumn for creating the Header cell for this column
			CruiseDataColumn cruiseColumn = new CruiseDataColumn(cruise, k);
			// Maintain a reference to the CruiseDataColumn object
			cruiseDataCols.add(cruiseColumn);

			// Add this data column and the header to the grid
			dataGrid.addColumn(dataColumn, cruiseColumn.createHeader());

			// Get the width for this column
			int colWidth = 15;
			int len = cruise.getUserColNames().get(k).length();
			if ( colWidth < len )
				colWidth = len;
			len = DashboardUtils.STD_HEADER_NAMES.get(
					cruise.getDataColTypes().get(k)).length();
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
			if ( colWidth > 24 )
				colWidth = 24;
			// Set the width of this column
			dataGrid.setColumnWidth(dataColumn, 0.75 * colWidth, Style.Unit.EM);
			// Add this width to the minimum table width
			minTableWidth += colWidth;
		}
		// Make sure the table has some reasonable minimum width
		if ( minTableWidth < 60 )
			minTableWidth = 60;
		// Set the minimum table width
		dataGrid.setMinimumTableWidth(0.75 * minTableWidth, Style.Unit.EM);
		// Update the data provider with the data in the CruiseDataColumnSpecs
		dataProvider.updateRowCount(cruise.getNumDataRows(), true);
		dataProvider.updateRowData(0, cruiseSpecs.getDataValues());
		// Set the number of data rows to display in the grid.
		// This will refresh the view.
		dataGrid.setPageSize(NUM_ROWS_PER_GRID_PAGE);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page, which may  
		// have been updated from previous actions on this page.
		DashboardCruiseListPage.showPage(false);
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		// Submit the updated data column types to the server.
		// This update invokes the SanityChecker on the data and
		// the results are then reported back to this page.
		service.updateCruiseDataColumnSpecs(DashboardLoginPage.getUsername(), 
								DashboardLoginPage.getPasshash(), cruise, 
								new AsyncCallback<DashboardCruiseWithData>() {
			@Override
			public void onSuccess(DashboardCruiseWithData specs) {
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
