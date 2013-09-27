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

	private static final String DATA_COLUMN_NAME_TAG = "DataColumn_";
	private static final int NUM_ROWS_PER_GRID_PAGE = 15;

	// These CruiseDataColumnType objects will be updated
	// by the user interacting with the header SelectionCell
	private ArrayList<CruiseDataColumnType> dataColTypes;
	// This maintains references to the CruiseDataColumnHeader 
	// objects which contain the mechanism for updating data types
	private ArrayList<CruiseDataColumnHeader> dataColHeaders;

	protected static String welcomeIntro = "Logged in as: ";
	protected static String logoutText = "Logout";
	protected static String cruiseIntro = "Cruise: ";
	protected static String submitText = "Submit Column Types";
	protected static String cancelText = "Return to Cruise List";

	protected String updateCruiseListFailMsg = 
			"Problems obtaining the latest cruise listing";

	interface CruiseDataColumnSpecsPageUiBinder extends	
			UiBinder<Widget, CruiseDataColumnSpecsPage> {
	}

	private static CruiseDataColumnSpecsPageUiBinder uiBinder = 
			GWT.create(CruiseDataColumnSpecsPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField Label cruiseLabel;
	@UiField DataGrid<ArrayList<String>> dataGrid;
	@UiField SimplePager gridPager;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	// Asynchronous provider of more data, if needed, for the cruise
	AsyncDataProvider<CruiseDataColumnSpecs> asyncCruiseDataProvider;

	/**
	 * Creates a cruise data column specification page for the 
	 * indicated cruise.  Allows the user to specify and submit
	 * the data column types.  On submission, the SanityChecker
	 * is run to validate the data type selection.
	 * 
	 * @param cruiseSpecs
	 * 		current cruise data column type specifications and
	 * 		initial cruise data for display
	 */
	CruiseDataColumnSpecsPage(CruiseDataColumnSpecs cruiseSpecs) {
		initWidget(uiBinder.createAndBindUi(this));

		userInfoLabel.setText(welcomeIntro + 
				DashboardPageFactory.getUsername());
		logoutButton.setText(logoutText);
		cruiseLabel.setText(cruiseIntro + cruiseSpecs.getExpocode());
		submitButton.setText(submitText);
		cancelButton.setText(cancelText);

		// Build the data grid using the provided CruiseDataColumnSpecs
		dataColTypes = cruiseSpecs.getColumnTypes();
		if ( dataColTypes.size() < 4 )
			throw new IllegalArgumentException(
					"Unexpected small number of data columns: " + 
					dataColTypes.size());
		dataColHeaders = 
				new ArrayList<CruiseDataColumnHeader>(dataColTypes.size());
		for (int k = 0; k < dataColTypes.size(); k++) {
			// TextColumn for displaying the data strings for this column
			ArrayListTextColumn dataColumn = new ArrayListTextColumn(k);
			dataColumn.setDataStoreName(DATA_COLUMN_NAME_TAG + k);

			// Object creating the Header cell for this data column
			CruiseDataColumnHeader dataHeader = 
					new CruiseDataColumnHeader(dataColTypes.get(k));
			// Maintain a reference to the CruiseDataColumnHeader object
			dataColHeaders.add(dataHeader);

			// Add this data column and the header to the grid
			dataGrid.addColumn(dataColumn, dataHeader.getHeader());
		}
		// Set the number of data rows to display
		dataGrid.setPageSize(NUM_ROWS_PER_GRID_PAGE);
		// Create the async data provider for this data grid 
		CruiseDataProvider dataProvider = 
				new CruiseDataProvider(cruiseSpecs.getExpocode());
		dataProvider.addDataDisplay(dataGrid);
		// Update the data provider with the data in the CruiseDataColumnSpecs
		dataProvider.updateRowCount(cruiseSpecs.getNumRowsTotal(), true);
		dataProvider.updateRowData(0, cruiseSpecs.getDataValues());
		// Set the pager to control the data grid
		gridPager.setDisplay(dataGrid);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		// Change to the logout page and discard this page.
		// (These pages are not kept by DashboardPageFactory.)
		RootLayoutPanel.get().remove(CruiseDataColumnSpecsPage.this);
		DashboardLogout logoutPage = 
				DashboardPageFactory.getPage(DashboardLogout.class);
		RootLayoutPanel.get().add(logoutPage);
		logoutPage.doLogout();
	}

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page and discard this page. 
		// (These pages are not kept by DashboardPageFactory.)  The cruise
		// listing may have been updated from previous actions on this page.
		DashboardCruiseListPage.showCruiseListPage(
				CruiseDataColumnSpecsPage.this, updateCruiseListFailMsg );
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		// Submit the updated data column types to the server.
		// This update invokes the SanityChecker on the data and
		// the results are then reported back to this page.
		// TODO:
		Window.alert("Not yet implemented");
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

	/**
	 * asynchronous provider of data from a given cruise 
	 */
	private class CruiseDataProvider extends AsyncDataProvider<ArrayList<String>> {
		private String expocode;
		private CruiseDataColumnSpecsServiceAsync service;

		CruiseDataProvider(String expocode) {
			this.expocode = expocode;
			service = GWT.create(CruiseDataColumnSpecsService.class);
		}

		@Override
		protected void onRangeChanged(HasData<ArrayList<String>> display) {
			final Range range = display.getVisibleRange();
			service.getCruiseData(DashboardPageFactory.getUsername(), 
					DashboardPageFactory.getPasshash(), 
					expocode, range.getStart(), range.getLength(), 
					new AsyncCallback<ArrayList<ArrayList<String>>>() {
				@Override
				public void onSuccess(ArrayList<ArrayList<String>> newData) {
					updateRowData(range.getStart(), newData);
				}
				@Override
				public void onFailure(Throwable ex) {
					Window.alert("Problems obtaining more cruise data" + 
							" (" + ex.getMessage() + ")");
				}
			});
		}
	}

	/**
	 * Display the cruise data column specifications page for a cruise
	 * with the latest cruise specifications from the server.
	 * 
	 * @param expocode
	 * 		show the specifications for this cruise
	 * @param currentPage
	 * 		current page on the RootLayoutPanel to be removed when
	 * 		the cruise data column specifications page is available
	 * @param errMsg
	 * 		error message to be shown, along with some explanation,
	 * 		in a Window.alert
	 */
	static void showCruiseDataColumnSpecsPage(String expocode, 
						final Composite currentPage, final String errMsg) {
		CruiseDataColumnSpecsServiceAsync service = 
				GWT.create(CruiseDataColumnSpecsService.class);
		service.getCruiseDataColumnSpecs(DashboardPageFactory.getUsername(), 
				DashboardPageFactory.getPasshash(), expocode, 
				new AsyncCallback<CruiseDataColumnSpecs>() {
			@Override
			public void onSuccess(CruiseDataColumnSpecs cruiseSpecs) {
				if ( cruiseSpecs != null ) {
					RootLayoutPanel.get().remove(currentPage);
					CruiseDataColumnSpecsPage cruiseSpecsPage = 
							new CruiseDataColumnSpecsPage(cruiseSpecs);
					RootLayoutPanel.get().add(cruiseSpecsPage);
				}
				else {
					Window.alert(errMsg + 
						" (unexpected null cruise column specificiations)");
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(errMsg + " (" + ex.getMessage() + ")");
			}
		});
	}
}
