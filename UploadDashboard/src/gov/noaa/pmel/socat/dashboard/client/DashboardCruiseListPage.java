/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Main SOCAT upload dashboard page.  Shows uploaded cruise files
 * and their status.  Provides connections to upload data files,
 * describe the contents of these data files, and submit the data
 * for inclusion into SOCAT.
 * 
 * @author Karl Smith
 */
public class DashboardCruiseListPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String UPLOAD_TEXT = "Upload Cruise Data";
	private static final String UPLOAD_HOVER_HELP = 
			"upload cruise data to create a new cruise " +
			"or replace an existing cruise";

	private static final String DELETE_TEXT = "Delete Cruise";
	private static final String DELETE_HOVER_HELP =
			"delete the selected cruises, including the cruise data";

	private static final String DATA_CHECK_TEXT = "Check Data";
	private static final String DATA_CHECK_HOVER_HELP =
			"assign data column types and programmatically " +
			"check the data in the selected cruise";

	private static final String METADATA_TEXT = "Manage Metadata";
	private static final String METADATA_HOVER_HELP =
			"manage metadata files for the selected cruise(s)";

	/* 
	 * private static final String REVIEW_TEXT = "Review with LAS";
	 * private static final String REVIEW_HOVER_HELP =
	 * 		"examine the selected cruises in the cruise viewer " +
	 * 		"aside other SOCAT cruises";
	 */

	private static final String QC_SUBMIT_TEXT = "Submit for QC";
	private static final String QC_SUBMIT_HOVER_HELP =
			"submit the selected cruises to SOCAT for policy " +
			"(quality control) assessment ";

	static final String ARCHIVE_SUBMIT_TEXT = "Archive Cruise";
	private static final String ARCHIVE_SUBMIT_HOVER_HELP =
			"manage the archival of the selected cruise";

	private static final String ADD_TO_LIST_TEXT = 
			"Add Cruise to List";
	private static final String ADD_TO_LIST_HOVER_HELP = 
			"add an existing cruise to this list of cruises";

	private static final String REMOVE_FROM_LIST_TEXT = 
			"Remove Cruise from List";
	private static final String REMOVE_FROM_LIST_HOVER_HELP =
			"remove the selected cruises from this list of cruises; " +
			"this will NOT remove the cruise data";

	// Error messages when the request for the latest cruise list fails
	private static final String LOGIN_ERROR_MSG = 
			"Sorry, your login failed";
	private static final String GET_CRUISE_LIST_ERROR_MSG = 
			"Problems obtaining the latest cruise listing";

	private static final String NO_CRUISE_TO_DELETE_MSG = 
			"No cruises are selected which can be deleted " +
			"(cruises must be suspended or not submitted).";
	private static final String DELETE_CONFIRM_MSG = 
			": all cruise data for this cruise will be deleted.  " +
			"Do you wish to proceed?";
	private static final String DELETE_CRUISE_FAIL_MSG = 
			"Unable to delete the selected cruise(s)";

	private static final String ONLY_ONE_CRUISE_ALLOWED_MSG =
			"Exactly one cruise must be selected";

	private static final String EXPOCODE_TO_ADD_MSG = 
			"Enter the expocode of the cruise to wish to add " +
			"to your list of cruises";
	private static final String ADD_CRUISE_FAIL_MSG = 
			"Unable to add the specified cruise " +
			"to your list of cruises";

	private static final String NO_CRUISE_TO_REMOVE_MSG = 
			"No cruises are selected for removal ";
	private static final String REMOVE_CRUISE_CONFIRM_MSG = 
			": will be removed from your personal list of cruises; " +
			"the cruise data file will NOT be removed.  " +
			"Do you wish to proceed?";
	private static final String REMOVE_CRUISE_FAIL_MSG = 
			"Unable to remove the selected cruise(s) " +
			"from your list of cruises";

	private static final String NO_CRUISES_SELECTED_FOR_METADATA_MSG = 
			"No cruises are selected for managing metadata documents.  " +
			"Cruises submitted to SOCAT (and not suspended or failed) " +
			"were automatically removed from the list of cruises " +
			"considered.";

	private static final String NO_CRUISES_SELECTED_FOR_QC_SUBMIT_MSG =
			"No unsubmitted cruises are selected for submitting to SOCAT.  " +
			"Cruises already submitted to SOCAT (and not suspended or failed) " +
			"were automatically removed from the list of cruises to submit.";

	// Column header strings
	private static final String EXPOCODE_COLUMN_NAME = "Expocode";
	private static final String OWNER_COLUMN_NAME = "Owner";
	private static final String DATA_CHECK_COLUMN_NAME = "Data check";
	private static final String METADATA_COLUMN_NAME = "Metadata";
	private static final String SUBMITTED_COLUMN_NAME = "Submitted";
	private static final String ARCHIVED_COLUMN_NAME = "Archived";
	private static final String FILENAME_COLUMN_NAME = "Filename";

	// Replacement strings for empty or null values
	private static final String EMPTY_TABLE_TEXT = "No uploaded cruises";
	private static final String NO_EXPOCODE_STRING = "(unknown)";
	private static final String NO_OWNER_STRING = "(unknown)";
	private static final String NO_DATA_CHECK_STATUS_STRING = "(not checked)";
	private static final String NO_METADATA_STATUS_STRING = "(no metadata)";
	private static final String NO_QC_STATUS_STRING = "(not submitted)";
	private static final String NO_ARCHIVE_STATUS_STRING = "(not archived)";
	private static final String NO_UPLOAD_FILENAME_STRING = "(unknown)";

	interface DashboardCruiseListPageUiBinder 
			extends UiBinder<Widget, DashboardCruiseListPage> {
	}

	private static DashboardCruiseListPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseListPageUiBinder.class);

	private static DashboardCruiseListServiceAsync service = 
			GWT.create(DashboardCruiseListService.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField Button uploadButton;
	@UiField Button deleteButton;
	@UiField Button dataCheckButton;
	@UiField Button metadataButton;
	// @UiField Button reviewButton;
	@UiField Button qcSubmitButton;
	@UiField Button archiveSubmitButton;
	@UiField Button addToListButton;
	@UiField Button removeFromListButton;
	@UiField DataGrid<DashboardCruise> uploadsGrid;

	private String username;
	private ListDataProvider<DashboardCruise> listProvider;

	// The singleton instance of this page
	private static DashboardCruiseListPage singleton;

	/**
	 * Creates an empty cruise list page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page with the
	 * latest cruise list from the server. 
	 */
	private DashboardCruiseListPage() {
		initWidget(uiBinder.createAndBindUi(this));
		buildCruiseListTable();

		username = "";

		logoutButton.setText(LOGOUT_TEXT);

		uploadButton.setText(UPLOAD_TEXT);
		uploadButton.setTitle(UPLOAD_HOVER_HELP);

		deleteButton.setText(DELETE_TEXT);
		deleteButton.setTitle(DELETE_HOVER_HELP);

		dataCheckButton.setText(DATA_CHECK_TEXT);
		dataCheckButton.setTitle(DATA_CHECK_HOVER_HELP);

		metadataButton.setText(METADATA_TEXT);
		metadataButton.setTitle(METADATA_HOVER_HELP);

		/* 
		 * reviewButton.setText(REVIEW_TEXT);
		 * reviewButton.setTitle(REVIEW_HOVER_HELP);
		 */

		qcSubmitButton.setText(QC_SUBMIT_TEXT);
		qcSubmitButton.setTitle(QC_SUBMIT_HOVER_HELP);

		archiveSubmitButton.setText(ARCHIVE_SUBMIT_TEXT);
		archiveSubmitButton.setTitle(ARCHIVE_SUBMIT_HOVER_HELP);

		addToListButton.setText(ADD_TO_LIST_TEXT);
		addToListButton.setTitle(ADD_TO_LIST_HOVER_HELP);

		removeFromListButton.setText(REMOVE_FROM_LIST_TEXT);
		removeFromListButton.setTitle(REMOVE_FROM_LIST_HOVER_HELP);

		uploadButton.setFocus(true);
	}

	/**
	 * Display the cruise list page in the RootLayoutPanel 
	 * with the latest information from the server.
	 * Adds this page to the page history.
	 * 
	 * @param loggingIn
	 * 		is this request coming from a login request?  
	 * 		This is only used to select the error message 
	 * 		to show in a Window.alert when the request for
	 * 		the latest cruise information fails.
	 */
	static void showPage(boolean loggingIn) {
		// Select the appropriate error message if the request fails 
		final String errMsg;
		if ( loggingIn )
			errMsg = LOGIN_ERROR_MSG;
		else
			errMsg = GET_CRUISE_LIST_ERROR_MSG;
		// Request the latest cruise list
		service.updateCruiseList(DashboardLoginPage.getUsername(), 
								 DashboardLoginPage.getPasshash(),
								 DashboardUtils.REQUEST_CRUISE_LIST_ACTION, 
								 new HashSet<String>(),
								 new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruises) {
				if ( DashboardLoginPage.getUsername()
										.equals(cruises.getUsername()) ) {
					if ( singleton == null )
						singleton = new DashboardCruiseListPage();
					SocatUploadDashboard.get().updateCurrentPage(singleton);
					singleton.updateCruises(cruises);
					History.newItem(PagesEnum.CRUISE_LIST.name(), false);
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
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the current login username.
	 * 
	 * @param addToHistory 
	 * 		if true, adds this page to the page history 
	 */
	static void redisplayPage(boolean addToHistory) {
		// If never show before, or if the username does not match the 
		// current login username, show the login page instead
		if ( (singleton == null) || 
			 ! singleton.username.equals(DashboardLoginPage.getUsername()) ) {
			DashboardLoginPage.showPage(true);
		}
		else {
			SocatUploadDashboard.get().updateCurrentPage(singleton);
			if ( addToHistory )
				History.newItem(PagesEnum.CRUISE_LIST.name(), false);
		}
	}

	/**
	 * Updates the cruise list page with the current username and 
	 * with the cruises given in the argument.
	 * 
	 * @param cruises
	 * 		cruises to display
	 */
	private void updateCruises(DashboardCruiseList cruises) {
		// Update the username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);
		if ( cruises.isManager() ) {
			addToListButton.setVisible(true);
			removeFromListButton.setVisible(true);
		}
		else {
			addToListButton.setVisible(false);
			removeFromListButton.setVisible(false);
		}
		// Update the cruises shown by resetting the data in the data provider
		List<DashboardCruise> cruiseList = listProvider.getList();
		cruiseList.clear();
		if ( cruises != null ) {
			cruiseList.addAll(cruises.values());
		}
		uploadsGrid.setRowCount(cruiseList.size());
	}

	/**
	 * Update the currently displayed cruise list page with the latest 
	 * information from the server after performing the specified action.
	 * 
	 * @param action
	 * 		cruise list action to perform
	 * @param expocodes
	 * 		cruise expocodes to act upon (if appropriate)
	 * @param errMsg
	 * 		if fails, message to show, along with some explanation, 
	 * 		in a Window.alert
	 */
	private void updatePage(String action, HashSet<String> expocodes, 
													final String errMsg) {
		service.updateCruiseList(DashboardLoginPage.getUsername(), 
								 DashboardLoginPage.getPasshash(), 
								 action, expocodes,
								 new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruises) {
				if ( DashboardLoginPage.getUsername()
										.equals(cruises.getUsername()) ) {
					DashboardCruiseListPage.this.updateCruises(cruises);
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
	 * @param skipSubmitted
	 * 		if true, do not include cruises whose QC status 
	 * 		is one of the submitted or accepted types
	 * @return
	 * 		set of selected cruises fitting the desired criteria;
	 * 		will not be null, but may be empty. 
	 */
	private HashSet<DashboardCruise> getSelectedCruises(boolean skipSubmitted) {
		HashSet<DashboardCruise> cruiseSet = new HashSet<DashboardCruise>();
		for ( DashboardCruise cruise : listProvider.getList() ) {
			if ( cruise.isSelected() ) {
				if ( skipSubmitted ) {
					String status = cruise.getQcStatus();
					if ( ! ( status.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) || 
							 status.equals(DashboardUtils.QC_STATUS_UNACCEPTABLE) ||
							 status.equals(DashboardUtils.QC_STATUS_SUSPENDED) ||
							 status.equals(DashboardUtils.QC_STATUS_EXCLUDED) ) )
						continue;
				}
				cruiseSet.add(cruise);
			}
		}
		return cruiseSet;
	}

	/**
	 * @param skipSubmitted
	 * 		if true, do not include cruises whose QC status 
	 * 		is one of the submitted or accepted types
	 * @return
	 * 		set of expocodes of the selected cruises fitting the 
	 * 		desired criteria; will not be null, but may be empty. 
	 */
	private HashSet<String> getSelectedCruiseExpocodes(boolean skipSubmitted) {
		HashSet<String> expocodeSet = new HashSet<String>();
		for ( DashboardCruise cruise : 
			getSelectedCruises(skipSubmitted) ) {
			expocodeSet.add(cruise.getExpocode());
		}
		return expocodeSet;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("uploadButton")
	void uploadCruiseOnClick(ClickEvent event) {
		DashboardCruiseUploadPage.showPage();
	}

	@UiHandler("deleteButton")
	void deleteCruiseOnClick(ClickEvent event) {
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(true);
		if ( expocodeSet.size() == 0 ) {
			Window.alert(NO_CRUISE_TO_DELETE_MSG);
			return;
		}
		// Confirm each cruise 
		for ( String expocode : expocodeSet )
			if ( ! Window.confirm(expocode + DELETE_CONFIRM_MSG) ) 
				return;
		// Remove the cruises
		updatePage(DashboardUtils.REQUEST_CRUISE_DELETE_ACTION, 
						expocodeSet, DELETE_CRUISE_FAIL_MSG);
	}

	@UiHandler("dataCheckButton")
	void dataCheckOnClick(ClickEvent event) {
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(true);
		if ( expocodeSet.size() != 1 ) {
			Window.alert(ONLY_ONE_CRUISE_ALLOWED_MSG);
			return;
		}
		String expocode = expocodeSet.iterator().next();
		CruiseDataColumnSpecsPage.showPage(expocode);
	}

	@UiHandler("metadataButton")
	void metadataOnClick(ClickEvent event) {
		HashSet<DashboardCruise> cruises = getSelectedCruises(true);
		if ( cruises.size() < 1 ) {
			Window.alert(NO_CRUISES_SELECTED_FOR_METADATA_MSG);
			return;
		}
		else if ( cruises.size() == 1 ) {
			// Single cruise selected; go to the metadata manager page
			MetadataManagerPage.showPage(
					cruises.iterator().next().getExpocode());
		}
		else {
			// Multiple cruises selected; go to the metadata upload page
			MetadataUploadPage.showPage(cruises);
		}
	}

	/*
	 * @UiHandler("reviewButton")
	 * void reviewOnClick(ClickEvent event) {
	 * 	// TODO:
	 * 	Window.alert("Not yet implemented");
	 * }
	 */

	@UiHandler("qcSubmitButton")
	void qcSubmitOnClick(ClickEvent event) {
		HashSet<DashboardCruise> cruiseSet = getSelectedCruises(true);
		if ( cruiseSet.size() == 0 ) {
			Window.alert(NO_CRUISES_SELECTED_FOR_QC_SUBMIT_MSG);
			return;
		}
		CruiseAddToSocatPage.showPage(cruiseSet);
	}

	@UiHandler("archiveSubmitButton")
	void archiveSubmitOnClick(ClickEvent event) {
		HashSet<DashboardCruise> cruiseSet = getSelectedCruises(false);
		if ( cruiseSet.size() != 1 ) {
			Window.alert(ONLY_ONE_CRUISE_ALLOWED_MSG);
			return;
		}
		CruiseArchivePage.showPage(cruiseSet.iterator().next());
	}

	@UiHandler("addToListButton")
	void addToListOnClick(ClickEvent event) {
		String expocode = Window.prompt(EXPOCODE_TO_ADD_MSG, "");
		if ( expocode != null ) {
			expocode = expocode.trim().toUpperCase();
			// Quick local check if the expocode is obviously invalid
			boolean badExpo = false;
			String errMsg = ADD_CRUISE_FAIL_MSG;
			int expoLen = expocode.length();
			if ( (expoLen < DashboardUtils.MIN_EXPOCODE_LENGTH) ||
				 (expoLen > DashboardUtils.MAX_EXPOCODE_LENGTH) ) {
				badExpo = true;
				errMsg += " (Invalid cruise Expocode length)";
			}
			else {
				for (int k = 0; k < expoLen; k++) {
					if ( ! DashboardUtils.VALID_EXPOCODE_CHARACTERS
										 .contains(expocode.substring(k, k+1)) ) {
						badExpo = true;
						errMsg += " (Invalid characters in the cruise Expocode)";
						break;
					}
				}
			}
			if ( badExpo ) {
				Window.alert(errMsg);
			}
			else {
				HashSet<String> expocodeSet = new HashSet<String>();
				expocodeSet.add(expocode);
				updatePage(DashboardUtils.REQUEST_CRUISE_ADD_ACTION,
										expocodeSet, errMsg);
			}
		}
	}

	@UiHandler("removeFromListButton")
	void removeFromListOnClick(ClickEvent event) {
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(false);
		if ( expocodeSet.size() == 0 ) {
			Window.alert(NO_CRUISE_TO_REMOVE_MSG);
			return;
		}
		for ( String expocode : expocodeSet )
			if ( ! Window.confirm(expocode + REMOVE_CRUISE_CONFIRM_MSG) ) 
				return;
		updatePage(DashboardUtils.REQUEST_CRUISE_REMOVE_ACTION,
					expocodeSet, REMOVE_CRUISE_FAIL_MSG);
	}

	/**
	 * Creates the cruise data table columns.  The table will still need 
	 * to be populated using {@link #updateCruises(DashboardCruiseList)}.
	 */
	private void buildCruiseListTable() {
		
		// Create the columns for this table
		Column<DashboardCruise,Boolean> selectedColumn = buildSelectedColumn();
		TextColumn<DashboardCruise> expocodeColumn = buildExpocodeColumn();
		TextColumn<DashboardCruise> ownerColumn = buildOwnerColumn();
		TextColumn<DashboardCruise> dataCheckColumn = buildDataCheckColumn();
		TextColumn<DashboardCruise> metadataColumn = buildMetadataColumn();
		TextColumn<DashboardCruise> qcStatusColumn = buildQCStatusColumn();
		TextColumn<DashboardCruise> archiveStatusColumn = buildArchiveStatusColumn();
		TextColumn<DashboardCruise> filenameColumn = buildFilenameColumn();

		// Add the columns, with headers, to the table
		uploadsGrid.addColumn(selectedColumn, "");
		uploadsGrid.addColumn(expocodeColumn, EXPOCODE_COLUMN_NAME);
		uploadsGrid.addColumn(ownerColumn, OWNER_COLUMN_NAME);
		uploadsGrid.addColumn(dataCheckColumn, DATA_CHECK_COLUMN_NAME);
		uploadsGrid.addColumn(metadataColumn, METADATA_COLUMN_NAME);
		uploadsGrid.addColumn(qcStatusColumn, SUBMITTED_COLUMN_NAME);
		uploadsGrid.addColumn(archiveStatusColumn, ARCHIVED_COLUMN_NAME);
		uploadsGrid.addColumn(filenameColumn, FILENAME_COLUMN_NAME);

		// Set the minimum widths of the columns
		double tableWidth = 0.0;
		uploadsGrid.setColumnWidth(selectedColumn, 
				SocatUploadDashboard.CHECKBOX_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.CHECKBOX_COLUMN_WIDTH;
		uploadsGrid.setColumnWidth(expocodeColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		uploadsGrid.setColumnWidth(ownerColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		uploadsGrid.setColumnWidth(dataCheckColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		uploadsGrid.setColumnWidth(metadataColumn, 
				SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.FILENAME_COLUMN_WIDTH;
		uploadsGrid.setColumnWidth(qcStatusColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		uploadsGrid.setColumnWidth(archiveStatusColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		uploadsGrid.setColumnWidth(filenameColumn, 
				SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.FILENAME_COLUMN_WIDTH;

		// Set the minimum width of the full table
		uploadsGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardCruise>();
		listProvider.addDataDisplay(uploadsGrid);

		// Make the columns sortable
		selectedColumn.setSortable(true);
		expocodeColumn.setSortable(true);
		ownerColumn.setSortable(true);
		dataCheckColumn.setSortable(true);
		metadataColumn.setSortable(true);
		qcStatusColumn.setSortable(true);
		archiveStatusColumn.setSortable(true);
		filenameColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<DashboardCruise> columnSortHandler = 
				new ListHandler<DashboardCruise>(listProvider.getList());
		columnSortHandler.setComparator(selectedColumn,
				DashboardCruise.selectedComparator);
		columnSortHandler.setComparator(expocodeColumn, 
				DashboardCruise.expocodeComparator);
		columnSortHandler.setComparator(ownerColumn, 
				DashboardCruise.ownerComparator);
		columnSortHandler.setComparator(dataCheckColumn, 
				DashboardCruise.dataCheckComparator);
		columnSortHandler.setComparator(metadataColumn, 
				DashboardCruise.metadataFilenamesComparator);
		columnSortHandler.setComparator(qcStatusColumn, 
				DashboardCruise.qcStatusComparator);
		columnSortHandler.setComparator(archiveStatusColumn, 
				DashboardCruise.archiveStatusComparator);
		columnSortHandler.setComparator(filenameColumn, 
				DashboardCruise.filenameComparator);

		// Add the sort handler to the table, and sort by expocode by default
		uploadsGrid.addColumnSortHandler(columnSortHandler);
		uploadsGrid.getColumnSortList().push(expocodeColumn);

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
	private Column<DashboardCruise,Boolean> buildSelectedColumn() {
		Column<DashboardCruise,Boolean> selectedColumn = 
				new Column<DashboardCruise,Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(DashboardCruise cruise) {
				return cruise.isSelected();
			}
		};
		selectedColumn.setFieldUpdater(new FieldUpdater<DashboardCruise,Boolean>() {
			@Override
			public void update(int index, DashboardCruise cruise, Boolean value) {
				if ( value == null ) {
					cruise.setSelected(false);
				}
				else {
					cruise.setSelected(value);
				}
			}
		});
		return selectedColumn;
	}

	/**
	 * Creates the expocode column for the table
	 */
	private TextColumn<DashboardCruise> buildExpocodeColumn() {
		TextColumn<DashboardCruise> expocodeColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String expocode = cruise.getExpocode();
				if ( expocode.isEmpty() )
					expocode = NO_EXPOCODE_STRING;
				return expocode;
			}
		};
		return expocodeColumn;
	}

	/**
	 * Creates the owner column for the table
	 */
	private TextColumn<DashboardCruise> buildOwnerColumn() {
		TextColumn<DashboardCruise> ownerColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String owner = cruise.getOwner();
				if ( owner.isEmpty() )
					owner = NO_OWNER_STRING;
				return owner;
			}
		};
		return ownerColumn;
	}

	/**
	 * Creates the data-check status column for the table
	 */
	private TextColumn<DashboardCruise> buildDataCheckColumn() {
		TextColumn<DashboardCruise> dataCheckColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String status = cruise.getDataCheckStatus();
				if ( status.isEmpty() )
					status = NO_DATA_CHECK_STATUS_STRING;
				return status;
			}
		};
		return dataCheckColumn;
	}

	/**
	 * Creates the metadata files column for the table
	 */
	private TextColumn<DashboardCruise> buildMetadataColumn() {
		TextColumn<DashboardCruise> metaCheckColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				TreeSet<String> filenames = cruise.getMetadataFilenames();
				if ( filenames.size() == 0 )
					return NO_METADATA_STATUS_STRING;
				StringBuilder sb = new StringBuilder();
				boolean firstEntry = true;
				for ( String name : filenames ) {
					if ( firstEntry )
						firstEntry = false;
					else
						sb.append("; ");
					sb.append(name);
				}
				return sb.toString();
			}
		};
		return metaCheckColumn;
	}

	/**
	 * Creates the QC submission status column for the table
	 */
	private TextColumn<DashboardCruise> buildQCStatusColumn() {
		TextColumn<DashboardCruise> qcStatusColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String status = cruise.getQcStatus();
				if ( status.isEmpty() )
					status = NO_QC_STATUS_STRING;
				return status;
			}
		};
		return qcStatusColumn;
	}

	/**
	 * Creates the archive submission status column for the table
	 */
	private TextColumn<DashboardCruise> buildArchiveStatusColumn() {
		TextColumn<DashboardCruise> archiveStatusColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String status = cruise.getArchiveStatus();
				if ( status.isEmpty() )
					status = NO_ARCHIVE_STATUS_STRING;
				return status;
			}
		};
		return archiveStatusColumn;
	}

	/**
	 * Creates the filename column for the table
	 */
	private TextColumn<DashboardCruise> buildFilenameColumn() {
		TextColumn<DashboardCruise> filenameColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String uploadFilename = cruise.getUploadFilename();
				if ( uploadFilename.isEmpty() )
					uploadFilename = NO_UPLOAD_FILENAME_STRING;
				return uploadFilename;
			}
		};
		return filenameColumn;
	}

}
