/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.HashSet;
import java.util.List;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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
	private static final String MORE_INFO_TEXT = "more explanation";

	private static final String AGREE_SHARE_TEXT = 
			"I give permission for my cruises to be shared for policy (QC) assessment.";
	private static final String AGREE_SHARE_INFO_HTML =
			"By checking this box I am giving permission for my uploaded cruise files " +
			"to be shared for purposes of policy (QC) assessment.  I understand that " +
			"data so-released will be used only for that narrow purpose and will not " +
			"be further distributed until the next official publication of SOCAT if " +
			"the cruise was deemed acceptable. ";

	private static final String AGREE_ARCHIVE_TEXT = 
			"I give permission for my cruises to be automatically archived at CDIAC.  ";
	private static final String AGREE_ARCHIVE_INFO_HTML = 
			"By checking this box I am giving permission for my uploaded cruise files " +
			"and metadata to be archived at CDIAC.  This will occur, if the cruise was " +
			"deemed acceptable, at the time of the next SOCAT public release, after " +
			"which the files will be made accessible to the public through the CDIAC " +
			"Web site. " +
			"<br /><br /> " +
			"<em>Note that declining permission here implies an obligation on my part to " +
			"ensure that these data will be made accessible via another data center.</em>";

	private static final String UPLOAD_TEXT = "Upload New Cruise Data";
	private static final String UPLOAD_HOVER_HELP = 
			"upload cruise data to create a new cruise or replace an existing cruise";

	private static final String DELETE_TEXT = "Delete Cruise";
	private static final String DELETE_HOVER_HELP =
			"delete the selected cruises, including the cruise data, from SOCAT";

	private static final String DATA_CHECK_TEXT = "Check Data";
	private static final String DATA_CHECK_HOVER_HELP =
			"assign data column types and programmatically check the data in the selected cruise";

	private static final String META_CHECK_TEXT = "Check Metadata";
	private static final String META_CHECK_HOVER_HELP =
			"programmatically check the metadata in the selected cruise";

	private static final String REVIEW_TEXT = "Review with LAS";
	private static final String REVIEW_HOVER_HELP =
			"examine the selected cruises in the cruise viewer " +
			"aside other SOCAT cruises";

	private static final String QC_SUBMIT_TEXT = "Submit for QC";
	private static final String QC_SUBMIT_HOVER_HELP =
			"submit the selected cruises to SOCAT for quality assessment";

	private static final String ARCHIVE_SUBMIT_TEXT = "Archive Now";
	private static final String ARCHIVE_SUBMIT_HOVER_HELP =
			"report the archival of the selected cruises, " +
			"or submit the selected cruises for immediate archival";

	private static final String ADD_TO_LIST_TEXT = "Add Cruise";
	private static final String ADD_TO_LIST_HOVER_HELP = 
			"add an existing cruise to this list of cruises";

	private static final String REMOVE_FROM_LIST_TEXT = "Remove Cruise";
	private static final String REMOVE_FROM_LIST_HOVER_HELP =
			"remove the selected cruises from this list of cruises; " +
			"this will NOT remove the cruise or cruise data from SOCAT";

	private static final String NO_CRUISE_TO_DELETE_MSG = 
			"No cruises are selected which can be deleted " +
			"(cruises must be suspended or not submitted).";
	private static final String DELETE_CONFIRM_MSG = 
			": this cruise, including all cruise data, will be deleted from " +
			"SOCAT.  You will be able to do this only if the cruise belongs " +
			"to you or to someone in a group you manage.  Do you wish to " +
			"proceed?";
	private static final String DELETE_CRUISE_FAIL_MSG = 
			"Unable to delete the selected cruise(s)";

	private static final String ONLY_ONE_CRUISE_ALLOWED_MSG =
			"Exactly one cruise must be selected";

	private static final String EXPOCODE_TO_ADD_MSG = 
			"Enter the expocode of the cruise to wish to add to your cruise list";
	private static final String ADD_CRUISE_FAIL_MSG = 
			"Unable to add the specified cruise to your list of cruises";

	private static final String NO_CRUISE_TO_REMOVE_MSG = 
			"No cruises are selected for removal ";
	private static final String REMOVE_CRUISE_CONFIRM_MSG = 
			": this cruise will be removed from your personal list of cruises; " +
			"the cruise data file will NOT be removed from SOCAT.  Do you wish " +
			"to proceed?";
	private static final String REMOVE_CRUISE_FAIL_MSG = 
			"Unable to remove the selected cruise(s) from your list of cruises";

	// Column header strings
	private static final String EXPOCODE_COLUMN_NAME = "Expocode";
	private static final String OWNER_COLUMN_NAME = "Owner";
	private static final String DATA_CHECK_COLUMN_NAME = "Data check";
	private static final String META_CHECK_COLUMN_NAME = "Meta check";
	private static final String SUBMITTED_COLUMN_NAME = "Submitted";
	private static final String ARCHIVED_COLUMN_NAME = "Archived";
	private static final String FILENAME_COLUMN_NAME = "Filename";

	// Replacement strings for empty or null values
	private static final String EMPTY_TABLE_TEXT = "No uploaded cruises";
	private static final String NO_EXPOCODE_STRING = "(unknown)";
	private static final String NO_OWNER_STRING = "(unknown)";
	private static final String NO_CHECK_STATUS_STRING = "(not checked)";
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
	@UiField CheckBox agreeShareCheckBox;
	@UiField Button agreeShareInfoButton;
	@UiField CheckBox agreeArchiveCheckBox;
	@UiField Button agreeArchiveInfoButton;
	@UiField Button uploadButton;
	@UiField Button deleteButton;
	@UiField Button dataCheckButton;
	@UiField Button metaCheckButton;
	@UiField Button reviewButton;
	@UiField Button qcSubmitButton;
	@UiField Button archiveSubmitButton;
	@UiField Button addToListButton;
	@UiField Button removeFromListButton;
	@UiField DataGrid<DashboardCruise> uploadsGrid;

	private ListDataProvider<DashboardCruise> listProvider;
	private DashboardInfoPopup agreeSharePopup;
	private DashboardInfoPopup agreeArchivePopup;

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

		logoutButton.setText(LOGOUT_TEXT);

		agreeShareCheckBox.setText(AGREE_SHARE_TEXT);
		agreeShareCheckBox.setValue(true);
		agreeShareInfoButton.setText(MORE_INFO_TEXT);
		agreeSharePopup = null;

		agreeArchiveCheckBox.setText(AGREE_ARCHIVE_TEXT);
		agreeArchiveCheckBox.setValue(true);
		agreeArchiveInfoButton.setText(MORE_INFO_TEXT);
		agreeArchivePopup = null;

		uploadButton.setText(UPLOAD_TEXT);
		uploadButton.setTitle(UPLOAD_HOVER_HELP);

		deleteButton.setText(DELETE_TEXT);
		deleteButton.setTitle(DELETE_HOVER_HELP);

		dataCheckButton.setText(DATA_CHECK_TEXT);
		dataCheckButton.setTitle(DATA_CHECK_HOVER_HELP);

		metaCheckButton.setText(META_CHECK_TEXT);
		metaCheckButton.setTitle(META_CHECK_HOVER_HELP);

		reviewButton.setText(REVIEW_TEXT);
		reviewButton.setTitle(REVIEW_HOVER_HELP);

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
	 * with the latest information from the server
	 * 
	 * @param currentPage
	 * 		currently displayed page in the RootLayoutPanel
	 * 		to be removed when cruise list page is available
	 * @param errMsg
	 * 		message to show, along with some explanation, 
	 * 		in a Window.alert if unable to obtain the cruise
	 * 		list from the server
	 */
	static void showPage(final Composite currentPage, final String errMsg) {
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
					RootLayoutPanel.get().remove(currentPage);
					RootLayoutPanel.get().add(singleton);
					singleton.updateCruises(cruises);
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
	 * Updates the cruise list page with the current username and 
	 * with the cruises given in the argument.
	 * 
	 * @param cruises
	 * 		cruises to display
	 */
	private void updateCruises(DashboardCruiseList cruises) {
		// Update the username
		userInfoLabel.setText(WELCOME_INTRO + DashboardLoginPage.getUsername());
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
	 * @param skipArchived
	 * 		if true, do not include cruises whose archive status 
	 * 		is one of the submitted or assigned types 
	 * @return
	 * 		set of expocodes of the selected cruises fitting the desired criteria;
	 * 		will not be null, but may be empty. 
	 */
	private HashSet<String> getSelectedCruiseExpocodes(boolean skipSubmitted, 
											   boolean skipArchived) {
		HashSet<String> expocodeSet = new HashSet<String>();
		for ( DashboardCruise cruise : listProvider.getList() ) {
			if ( cruise.isSelected() ) {
				if ( skipSubmitted ) {
					String status = cruise.getQcStatus();
					if ( ! ( status.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) || 
							 status.equals(DashboardUtils.QC_STATUS_AUTOFAIL) ||
							 status.equals(DashboardUtils.QC_STATUS_UNACCEPTABLE) ||
							 status.equals(DashboardUtils.QC_STATUS_SUSPENDED) ||
							 status.equals(DashboardUtils.QC_STATUS_EXCLUDED) ) )
						continue;
				}
				if ( skipArchived ) {
					if ( ! cruise.getArchiveStatus().equals(
							DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED) )
						continue;
				}
				expocodeSet.add(cruise.getExpocode());
			}
		}
		return expocodeSet;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardCruiseListPage.this);
		DashboardLogoutPage.showPage();
	}

	@UiHandler("agreeShareInfoButton")
	void agreeShareInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeSharePopup == null ) {
			agreeSharePopup = new DashboardInfoPopup();
			agreeSharePopup.setInfoMessage(AGREE_SHARE_INFO_HTML);
		}
		// Show the popup over the info button
		agreeSharePopup.showAtPosition(
				agreeShareInfoButton.getAbsoluteLeft(),
				agreeShareInfoButton.getAbsoluteTop());
	}

	@UiHandler("agreeArchiveInfoButton")
	void agreeArchiveInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeArchivePopup == null ) {
			agreeArchivePopup = new DashboardInfoPopup();
			agreeArchivePopup.setInfoMessage(AGREE_ARCHIVE_INFO_HTML);
		}
		// Show the popup over the info button
		agreeArchivePopup.showAtPosition(
				agreeArchiveInfoButton.getAbsoluteLeft(),
				agreeArchiveInfoButton.getAbsoluteTop());
	}

	@UiHandler("uploadButton")
	void uploadCruiseOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardCruiseListPage.this);
		DashboardCruiseUploadPage.showPage();
	}

	@UiHandler("deleteButton")
	void deleteCruiseOnClick(ClickEvent event) {
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(true, false);
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
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(false, false);
		if ( expocodeSet.size() != 1 ) {
			Window.alert(ONLY_ONE_CRUISE_ALLOWED_MSG);
			return;
		}
		String expocode = expocodeSet.iterator().next();
		CruiseDataColumnSpecsPage.showPage(expocode, DashboardCruiseListPage.this);
	}

	@UiHandler("metaCheckButton")
	void metadataCheckOnClick(ClickEvent event) {
		Window.alert("Not yet implemented");
	}

	@UiHandler("reviewButton")
	void reviewOnClick(ClickEvent event) {
		Window.alert("Not yet implemented");
	}

	@UiHandler("qcSubmitButton")
	void qcSubmitOnClick(ClickEvent event) {
		Window.alert("Not yet implemented");
	}

	@UiHandler("archiveSubmitButton")
	void archiveSubmitOnClick(ClickEvent event) {
		Window.alert("Not yet implemented");
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
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(false, false);
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
		TextColumn<DashboardCruise> metaCheckColumn = buildMetaCheckColumn();
		TextColumn<DashboardCruise> qcStatusColumn = buildQCStatusColumn();
		TextColumn<DashboardCruise> archiveStatusColumn = buildArchiveStatusColumn();
		TextColumn<DashboardCruise> filenameColumn = buildFilenameColumn();

		// Add the columns, with headers, to the table
		uploadsGrid.addColumn(selectedColumn, "");
		uploadsGrid.addColumn(expocodeColumn, EXPOCODE_COLUMN_NAME);
		uploadsGrid.addColumn(ownerColumn, OWNER_COLUMN_NAME);
		uploadsGrid.addColumn(dataCheckColumn, DATA_CHECK_COLUMN_NAME);
		uploadsGrid.addColumn(metaCheckColumn, META_CHECK_COLUMN_NAME);
		uploadsGrid.addColumn(qcStatusColumn, SUBMITTED_COLUMN_NAME);
		uploadsGrid.addColumn(archiveStatusColumn, ARCHIVED_COLUMN_NAME);
		uploadsGrid.addColumn(filenameColumn, FILENAME_COLUMN_NAME);

		// Set the minimum widths of the columns
		double tableWidth = 0.0;
		uploadsGrid.setColumnWidth(selectedColumn, 2.5, Style.Unit.EM);
		tableWidth += 2.5;
		uploadsGrid.setColumnWidth(expocodeColumn, 8.0, Style.Unit.EM);
		tableWidth += 8.0;
		uploadsGrid.setColumnWidth(ownerColumn, 8.0, Style.Unit.EM);
		tableWidth += 8.0;
		uploadsGrid.setColumnWidth(dataCheckColumn, 8.0, Style.Unit.EM);
		tableWidth += 8.0;
		uploadsGrid.setColumnWidth(metaCheckColumn, 8.0, Style.Unit.EM);
		tableWidth += 8.0;
		uploadsGrid.setColumnWidth(qcStatusColumn, 8.0, Style.Unit.EM);
		tableWidth += 8.0;
		uploadsGrid.setColumnWidth(archiveStatusColumn, 8.0, Style.Unit.EM);
		tableWidth += 8.0;
		uploadsGrid.setColumnWidth(filenameColumn, 10.0, Style.Unit.EM);
		tableWidth += 10.0;

		// Set the minimum width of the full table
		uploadsGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardCruise>();
		listProvider.addDataDisplay(uploadsGrid);

		// Make some of the columns sortable
		selectedColumn.setSortable(true);
		expocodeColumn.setSortable(true);
		ownerColumn.setSortable(true);
		dataCheckColumn.setSortable(true);
		metaCheckColumn.setSortable(true);
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
		columnSortHandler.setComparator(metaCheckColumn, 
				DashboardCruise.metadataCheckComparator);
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
					status = NO_CHECK_STATUS_STRING;
				return status;
			}
		};
		return dataCheckColumn;
	}

	/**
	 * Creates the metadata-check status column for the table
	 */
	private TextColumn<DashboardCruise> buildMetaCheckColumn() {
		TextColumn<DashboardCruise> metaCheckColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String status = cruise.getMetadataCheckStatus();
				if ( status.isEmpty() )
					status = NO_CHECK_STATUS_STRING;
				return status;
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
