/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataListServiceAsync;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Page for associating metadata files to one or more cruises.  
 * Shows currently uploaded metadata files for a user and 
 * provides a connection for uploading new or updated metadata 
 * files.
 *  
 * @author Karl Smith
 */
public class DashboardMetadataListPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String CRUISES_HTML_INTRO = 
			"Select metadata documents to be associated with the cruise(s):<br />";

	private static final String UPLOAD_NEW_TEXT = "Upload New Metadata";
	private static final String UPLOAD_NEW_HOVER_HELP = 
			"upload a file that will be treated as a new metadata file";

	private static final String UPLOAD_UPDATE_TEXT = "Upload Updated Metadata";
	private static final String UPLOAD_UPDATE_HOVER_HELP = 
			"upload a file that will replace the selected metadata file";

	private static final String ONLY_ONE_METADATA_ALLOWED_MSG =
			"Exactly one metadata document must be selected";

	private static final String SUBMIT_TEXT = "Associate Selected Metadata";
	private static final String CANCEL_TEXT = "Cancel";

	private static final String GET_CRUISE_LIST_FAIL_MSG = 
			"Problems obtaining the latest cruise listing";
	private static final String SUBMIT_FAIL_MSG = 
			"Problems associating the metadata with the cruises";
	private static final String SUBMIT_SUCCESS_MSG = 
			"Metadata successfully associated with the cruises";

	// Replacement strings for empty or null values
	private static final String EMPTY_TABLE_TEXT = "No uploaded cruises";
	private static final String NO_OWNER_STRING = "(unknown)";
	private static final String NO_FILENAME_STRING = "(unknown)";

	// Column header strings
	private static final String OWNER_COLUMN_NAME = "Owner";
	private static final String UPLOAD_FILENAME_COLUMN_NAME = "User's Filename";
	private static final String EXPOCODE_FILENAME_COLUMN_NAME = "Expocode Filename";

	interface DashboardMetadataListPageUiBinder 
			extends UiBinder<Widget, DashboardMetadataListPage> {
	}

	private static DashboardMetadataListPageUiBinder uiBinder = 
			GWT.create(DashboardMetadataListPageUiBinder.class);

	private static DashboardMetadataListServiceAsync service = 
			GWT.create(DashboardMetadataListService.class);

	@UiField Label userInfoLabel;
	@UiField HTML cruiseNamesHtml; 
	@UiField Button logoutButton;
	@UiField Button uploadNewButton;
	@UiField Button uploadUpdateButton;
	@UiField DataGrid<DashboardMetadata> uploadsGrid;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	private ListDataProvider<DashboardMetadata> listProvider;
	private TreeSet<String> cruiseExpocodes;

	// The singleton instance of this page
	private static DashboardMetadataListPage singleton;

	/**
	 * Creates an empty metadata list page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page with the
	 * latest metadata list from the server. 
	 */
	private DashboardMetadataListPage() {
		initWidget(uiBinder.createAndBindUi(this));
		buildMetadataListTable();

		logoutButton.setText(LOGOUT_TEXT);
		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);

		uploadNewButton.setText(UPLOAD_NEW_TEXT);
		uploadNewButton.setTitle(UPLOAD_NEW_HOVER_HELP);

		uploadUpdateButton.setText(UPLOAD_UPDATE_TEXT);
		uploadUpdateButton.setTitle(UPLOAD_UPDATE_HOVER_HELP);

		uploadNewButton.setFocus(true);
	}

	/**
	 * Display the metadata list page in the RootLayoutPanel 
	 * with the latest information from the server
	 * 
	 * @param cruiseExpocodes
	 * 		associate metadata to the cruises with these expocodes 
	 * @param currentPage
	 * 		currently displayed page in the RootLayoutPanel
	 * 		to be removed when the metadata list page is available
	 * @param errMsg
	 * 		message to show, along with some explanation, 
	 * 		in a Window.alert if unable to obtain the metadata
	 * 		list from the server
	 */
	static void showPage(final TreeSet<String> cruiseExpocodes, 
						final Composite currentPage, final String errMsg) {
		service.getMetadataList(DashboardLoginPage.getUsername(), 
								 DashboardLoginPage.getPasshash(),
								 new AsyncCallback<DashboardMetadataList>() {
			@Override
			public void onSuccess(DashboardMetadataList mdataList) {
				if ( DashboardLoginPage.getUsername()
										.equals(mdataList.getUsername()) ) {
					if ( singleton == null )
						singleton = new DashboardMetadataListPage();
					RootLayoutPanel.get().remove(currentPage);
					RootLayoutPanel.get().add(singleton);
					singleton.updateMetadataList(cruiseExpocodes, mdataList);
				}
				else {
					Window.alert(errMsg + " (unexpected invalid cruise list)");
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(errMsg + " (" + ex.getMessage() + ")");
			}
		});
	}

	/**
	 * Updates the cruise list page with the current username, 
	 * cruise expocodes, and list of available metadata files.
	 * 
	 * @param cruiseExpocodes
	 * 		associate metadata to the cruises with these expocodes 
	 * @param mdataList
	 * 		metadata documents to display
	 */
	private void updateMetadataList(TreeSet<String> cruiseExpocodes, 
									DashboardMetadataList mdataList) {
		// Update the username
		userInfoLabel.setText(WELCOME_INTRO + 
				DashboardLoginPage.getUsername());
		// Update the cruises to be assigned by this page
		this.cruiseExpocodes.clear();
		if ( cruiseExpocodes != null ) {
			this.cruiseExpocodes.addAll(cruiseExpocodes);
		}
		// Update the HTML intro naming the cruises
		StringBuilder sb = new StringBuilder();
		sb.append(CRUISES_HTML_INTRO);
		boolean first = true;
		for ( String expo : this.cruiseExpocodes ) {
			if ( first )
				first = false;
			else
				sb.append("; ");
			sb.append("<b>");
			sb.append(SafeHtmlUtils.htmlEscape(expo));
			sb.append("</b>");
		}
		cruiseNamesHtml.setHTML(SafeHtmlUtils.fromTrustedString(sb.toString()));
		// Update the metadata shown by resetting the data 
		// in the data provider
		List<DashboardMetadata> metadataList = listProvider.getList();
		metadataList.clear();
		if ( mdataList != null ) {
			metadataList.addAll(mdataList);
		}
		uploadsGrid.setRowCount(metadataList.size());
	}

	/**
	 * @return
	 * 		set of metadata expocode filenames of the selected 
	 * 		metadata documents; will not be null, but may be empty. 
	 */
	private HashSet<String> getSelectedExpocodeFilenames() {
		HashSet<String> expoNameSet = new HashSet<String>();
		for ( DashboardMetadata mdata : listProvider.getList() ) {
			if ( mdata.isSelected() ) {
				expoNameSet.add(mdata.getExpocodeFilename());
			}
		}
		return expoNameSet;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardMetadataListPage.this);
		DashboardLogoutPage.showPage();
	}

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page.
		DashboardCruiseListPage.showPage(
				DashboardMetadataListPage.this, GET_CRUISE_LIST_FAIL_MSG);
	}

	@UiHandler("uploadNewButton")
	void uploadNewOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardMetadataListPage.this);
		DashboardMetadataUploadPage.showPage(cruiseExpocodes, null);
	}

	@UiHandler("uploadUpdateButton")
	void uploadUpdateOnClick(ClickEvent event) {
		HashSet<String> selectedFilenames = getSelectedExpocodeFilenames();
		if ( selectedFilenames.size() != 1 ) {
			Window.alert(ONLY_ONE_METADATA_ALLOWED_MSG);
			return;
		}
		String expoFilename = selectedFilenames.iterator().next();
		RootLayoutPanel.get().remove(DashboardMetadataListPage.this);
		DashboardMetadataUploadPage.showPage(cruiseExpocodes, expoFilename);
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		// Submit the selected metadata documents with the list of cruises
		// associated with this page
		HashSet<String> expoFilenames = getSelectedExpocodeFilenames();
		service.associateMetadata(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), cruiseExpocodes, 
				expoFilenames, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// Change to the latest cruise listing page.
				DashboardCruiseListPage.showPage(
						DashboardMetadataListPage.this, GET_CRUISE_LIST_FAIL_MSG);
				Window.alert(SUBMIT_SUCCESS_MSG);
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(SUBMIT_FAIL_MSG + " (" + ex.getMessage() + ")");
			}
		});
	}

	private void buildMetadataListTable() {
		// Create the columns for this table
		Column<DashboardMetadata,Boolean> selectedColumn = buildSelectedColumn();
		TextColumn<DashboardMetadata> uploadFilenameColumn = buildUploadFilenameColumn();
		TextColumn<DashboardMetadata> expocodeFilenameColumn = buildExpocodeFilenameColumn();
		TextColumn<DashboardMetadata> ownerColumn = buildOwnerColumn();
		
		// Add the columns, with headers, to the table
		uploadsGrid.addColumn(selectedColumn, "");
		uploadsGrid.addColumn(uploadFilenameColumn, UPLOAD_FILENAME_COLUMN_NAME);
		uploadsGrid.addColumn(expocodeFilenameColumn, EXPOCODE_FILENAME_COLUMN_NAME);
		uploadsGrid.addColumn(ownerColumn, OWNER_COLUMN_NAME);

		// Set the minimum widths of the columns
		double tableWidth = 0.0;
		uploadsGrid.setColumnWidth(selectedColumn, 2.5, Style.Unit.EM);
		tableWidth += 2.5;
		uploadsGrid.setColumnWidth(uploadFilenameColumn, 15.0, Style.Unit.EM);
		tableWidth += 15.0;
		uploadsGrid.setColumnWidth(expocodeFilenameColumn, 15.0, Style.Unit.EM);
		tableWidth += 15.0;
		uploadsGrid.setColumnWidth(ownerColumn, 8.0, Style.Unit.EM);
		tableWidth += 8.0;

		// Set the minimum width of the full table
		uploadsGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardMetadata>();
		listProvider.addDataDisplay(uploadsGrid);

		// Make the columns sortable
		selectedColumn.setSortable(true);
		uploadFilenameColumn.setSortable(true);
		expocodeFilenameColumn.setSortable(true);
		ownerColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<DashboardMetadata> columnSortHandler = 
				new ListHandler<DashboardMetadata>(listProvider.getList());
		columnSortHandler.setComparator(selectedColumn,
				DashboardMetadata.selectedComparator);
		columnSortHandler.setComparator(uploadFilenameColumn, 
				DashboardMetadata.uploadFilenameComparator);
		columnSortHandler.setComparator(expocodeFilenameColumn, 
				DashboardMetadata.expocodeFilenameComparator);
		columnSortHandler.setComparator(ownerColumn, 
				DashboardMetadata.ownerComparator);

		// Add the sort handler to the table, and sort by expocode by default
		uploadsGrid.addColumnSortHandler(columnSortHandler);
		uploadsGrid.getColumnSortList().push(expocodeFilenameColumn);

		// Set the contents if there are no rows
		uploadsGrid.setEmptyTableWidget(new Label(EMPTY_TABLE_TEXT));

		// Following recommended to improve efficiency with IE
		uploadsGrid.setSkipRowHoverCheck(false);
		uploadsGrid.setSkipRowHoverFloatElementCheck(false);
		uploadsGrid.setSkipRowHoverStyleUpdate(false);
	}

	/**
	 * Creates the selection column for the table
	 */
	private Column<DashboardMetadata,Boolean> buildSelectedColumn() {
		Column<DashboardMetadata,Boolean> selectedColumn = 
				new Column<DashboardMetadata,Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(DashboardMetadata mdata) {
				return mdata.isSelected();
			}
		};
		selectedColumn.setFieldUpdater(new FieldUpdater<DashboardMetadata,Boolean>() {
			@Override
			public void update(int index, DashboardMetadata mdata, Boolean value) {
				if ( value == null ) {
					mdata.setSelected(false);
				}
				else {
					mdata.setSelected(value);
				}
			}
		});
		return selectedColumn;
	}

	/**
	 * Creates the upload filename column for the table
	 */
	private TextColumn<DashboardMetadata> buildUploadFilenameColumn() {
		TextColumn<DashboardMetadata> filenameColumn = new TextColumn<DashboardMetadata> () {
			@Override
			public String getValue(DashboardMetadata mdata) {
				String uploadFilename = mdata.getUploadFilename();
				if ( uploadFilename.isEmpty() )
					uploadFilename = NO_FILENAME_STRING;
				return uploadFilename;
			}
		};
		return filenameColumn;
	}

	/**
	 * Creates the expocode filename column for the table
	 */
	private TextColumn<DashboardMetadata> buildExpocodeFilenameColumn() {
		TextColumn<DashboardMetadata> filenameColumn = new TextColumn<DashboardMetadata> () {
			@Override
			public String getValue(DashboardMetadata mdata) {
				String expocodeFilename = mdata.getExpocodeFilename();
				if ( expocodeFilename.isEmpty() )
					expocodeFilename = NO_FILENAME_STRING;
				return expocodeFilename;
			}
		};
		return filenameColumn;
	}

	/**
	 * Creates the owner column for the table
	 */
	private TextColumn<DashboardMetadata> buildOwnerColumn() {
		TextColumn<DashboardMetadata> ownerColumn = new TextColumn<DashboardMetadata> () {
			@Override
			public String getValue(DashboardMetadata mdata) {
				String owner = mdata.getOwner();
				if ( owner.isEmpty() )
					owner = NO_OWNER_STRING;
				return owner;
			}
		};
		return ownerColumn;
	}

}