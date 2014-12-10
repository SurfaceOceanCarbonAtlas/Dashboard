/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
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
public class CruiseListPage extends Composite {

	private static final String TITLE_TEXT_PROLOGUE = "My SOCAT Version ";
	private static final String TITLE_TEXT_EPILOGUE = " Datasets";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String UPLOAD_TEXT = "Upload Datasets";
	private static final String UPLOAD_HOVER_HELP = 
			"upload data to create a new data set " +
			"or replace an existing data set";

	private static final String VIEW_DATA_TEXT = "Identify Columns";
	private static final String VIEW_DATA_HOVER_HELP =
			"review and modify data column type assignments for the " +
			"selected data set; identify issues in the data";

	static final String OME_METADATA_TEXT = "Edit Metadata";
	private static final String OME_METADATA_HOVER_HELP =
			"edit the metadata for the selected data set";

	private static final String ADDL_DOCS_TEXT = "Supplemental Documents";
	private static final String ADDL_DOCS_HOVER_HELP =
			"manage supplemental documents for the selected data sets";

	private static final String QC_SUBMIT_TEXT = "Submit for QC";
	private static final String QC_SUBMIT_HOVER_HELP =
			"submit the selected data sets for quality control assessment";

	private static final String REVIEW_TEXT = "Preview Dataset";
	private static final String REVIEW_HOVER_HELP =
			"examine the selected data set in the data set viewer " +
			"aside other SOCAT data sets";

	private static final String DELETE_TEXT = "Delete Datasets";
	private static final String DELETE_HOVER_HELP =
			"delete the selected data sets, " +
			"including its metadata and supplemental documents";

	private static final String ADD_TO_LIST_TEXT = 
			"Add to List";
	private static final String ADD_TO_LIST_HOVER_HELP = 
			"add existing data sets to this list of data sets";

	private static final String REMOVE_FROM_LIST_TEXT = 
			"Remove from List";
	private static final String REMOVE_FROM_LIST_HOVER_HELP =
			"remove the selected data sets from this list of data sets; " +
			"this will NOT delete the data set from the system";

	// Error messages when the request for the latest cruise list fails
	private static final String LOGIN_ERROR_MSG = 
			"Sorry, your login failed";
	private static final String GET_DATASET_LIST_ERROR_MSG = 
			"Problems obtaining the latest data set listing";

	// Starts of error messages for improper cruise selections
	private static final String SUBMITTED_DATASETS_SELECTED_ERR_START = 
			"Only data sets which have not been submitted for QC, " +
			"or which have been suspended or failed, may be selected ";
	private static final String ARCHIVED_DATASETS_SELECTED_ERR_START =
			"Only data sets which have not been archived may be selected ";
	private static final String NO_DATASET_SELECTED_ERR_START = 
			"No data set is selected ";
	private static final String MANY_DATASETS_SELECTED_ERR_START = 
			"Only one data set may be selected ";

	// Ends of error messages for improper cruise selections
	private static final String FOR_REVIEWING_ERR_END = 
			"for reviewing data.";
	private static final String FOR_OME_ERR_END =
			"for managing metadata.";
	private static final String FOR_ADDL_DOCS_ERR_END = 
			"for managing supplemental documents.";
	private static final String FOR_QC_SUBMIT_ERR_END =
			"for submitting for QC and archival.";
	private static final String FOR_DELETE_ERR_END = 
			"for deletion from the system.";
	private static final String FOR_REMOVE_ERR_END = 
			"for removal from your personal list of datasets.";

	private static final String NO_METADATA_HTML_PROLOGUE = 
			"The following data sets do not have appropriate metadata " +
			"and cannot be submitted for QC: <ul>";
	private static final String NO_METADATA_HTML_EPILOGUE = 
			"</ul>";
	private static final String CANNOT_SUBMIT_HTML_PROLOGUE = 
			"The following data sets have not been checked, or have very " +
			"serious errors detected by the automated data checker: <ul>";
	private static final String CANNOT_SUBMIT_HTML_EPILOGUE =
			"</ul> These data sets cannot be submitted for QC " +
			"until these problems have been resolved.";
	private static final String DATA_AUTOFAIL_HTML_PROLOGUE = 
			"The following data sets have errors detected " +
			"by the automated data checker: <ul>";
	private static final String AUTOFAIL_HTML_EPILOGUE = 
			"</ul> These data sets can be submitted for QC, " +
			"but because of the number of errors, a QC flag of " + 
			SocatQCEvent.QC_UNACCEPTABLE_FLAG + " (unacceptable) " +
			"will <em>probably</em> be assigned by reviewers.<br />" +
			"Do you want to continue? ";
	private static final String AUTOFAIL_YES_TEXT = "Yes";
	private static final String AUTOFAIL_NO_TEXT = "No";

	private static final String DELETE_DATASET_HTML_PROLOGUE = 
			"All data will be deleted for the following data sets: <ul>";
	private static final String DELETE_DATASET_HTML_EPILOGUE =
			"</ul> Do you want to proceed?";
	private static final String DELETE_YES_TEXT = "Yes";
	private static final String DELETE_NO_TEXT = "No";
	private static final String DELETE_DATASET_FAIL_MSG = 
			"Unable to delete the data sets";

	private static final String EXPOCODE_TO_ADD_MSG = 
			"Enter the expocode, possibly with wildcards * and ?, of the " +
			"data set(s) you want to add to your personal list of data sets";
	private static final String ADD_DATASET_FAIL_MSG = 
			"Unable to add the specified data set(s) " +
			"to your personal list of data sets";

	private static final String REMOVE_DATASET_HTML_PROLOGUE = 
			"The following data sets will be removed from your personal " +
			"list of data sets; the data, metadata, and supplemental " +
			"documents will <b>not</b> be removed: <ul>";
	private static final String REMOVE_DATASET_HTML_EPILOGUE = 
			"</ul> Do you want to proceed?";
	private static final String REMOVE_YES_TEXT = "Yes";
	private static final String REMOVE_NO_TEXT = "No";
	private static final String REMOVE_DATASET_FAIL_MSG = 
			"Unable to remove the selected data sets " +
			"from your personal list of data sets";

	private static final String UNEXPECTED_INVALID_DATESET_LIST_MSG = 
			" (unexpected invalid data set list returned)";

	// Select options
	private static final String MIXED_SELECTION_OPTION = " ";
	private static final String ALL_SELECTION_OPTION = "All";
	private static final String EDITABLE_SELECTION_OPTION = "Editable";
	private static final String SUBMITTED_SELECTION_OPTION = "Submitted";
	private static final String PUBLISHED_SELECTION_OPTION = "Published";
	private static final String CLEAR_SELECTION_OPTION = "Clear";

	// Column header strings
	private static final String EXPOCODE_COLUMN_NAME = "Expocode";
	private static final String TIMESTAMP_COLUMN_NAME = "Upload Date";
	private static final String DATA_CHECK_COLUMN_NAME = "Data Status";
	private static final String OME_METADATA_COLUMN_NAME = "Metadata";
	private static final String SUBMITTED_COLUMN_NAME = "QC Status";
	private static final String ARCHIVED_COLUMN_NAME = "Archival";
	private static final String FILENAME_COLUMN_NAME = "Filename";
	private static final String ADDL_DOCS_COLUMN_NAME = "Supplemental<br />Documents";
	private static final String OWNER_COLUMN_NAME = "Owner";

	// Replacement strings for empty or null values
	private static final String EMPTY_TABLE_TEXT = "(no uploaded datasets)";
	private static final String NO_EXPOCODE_STRING = "(unknown)";
	private static final String NO_TIMESTAMP_STRING = "(unknown)";
	private static final String NO_DATA_CHECK_STATUS_STRING = "Not checked";
	private static final String NO_OME_METADATA_STATUS_STRING = "(no metadata)";
	private static final String NO_QC_STATUS_STRING = "Private";
	private static final String NO_ARCHIVE_STATUS_STRING = "Not specified";
	private static final String NO_UPLOAD_FILENAME_STRING = "(unknown)";
	private static final String NO_ADDL_DOCS_STATUS_STRING = "(no documents)";
	private static final String NO_OWNER_STRING = "(unknown)";

	interface CruiseListPageUiBinder extends UiBinder<Widget, CruiseListPage> {
	}

	private static CruiseListPageUiBinder uiBinder = 
			GWT.create(CruiseListPageUiBinder.class);

	private static DashboardListServiceAsync service = 
			GWT.create(DashboardListService.class);

	@UiField Label titleLabel;
	@UiField Image titleImage;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField Button uploadButton;
	@UiField Button viewDataButton;
	@UiField Button omeMetadataButton;
	@UiField Button addlDocsButton;
	@UiField Button reviewButton;
	@UiField Button qcSubmitButton;
	@UiField Button deleteButton;
	@UiField Button addToListButton;
	@UiField Button removeFromListButton;
	@UiField DataGrid<DashboardCruise> datasetsGrid;

	private String username;
	private ListDataProvider<DashboardCruise> listProvider;
	private DashboardAskPopup askDeletePopup;
	private DashboardAskPopup askRemovePopup;
	private HashSet<DashboardCruise> cruiseSet;
	private HashSet<DashboardCruise> checkSet;
	private TreeSet<String> expocodeSet;
	private DashboardAskPopup askDataAutofailPopup;
	private boolean managerButtonsShown;
	TextColumn<DashboardCruise> timestampColumn;
	TextColumn<DashboardCruise> expocodeColumn;

	// The singleton instance of this page
	private static CruiseListPage singleton;

	/**
	 * Creates an empty cruise list page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page with the
	 * latest cruise list from the server. 
	 */
	CruiseListPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		buildCruiseListTable();

		username = "";

		cruiseSet = new HashSet<DashboardCruise>();
		checkSet = new HashSet<DashboardCruise>();
		expocodeSet = new TreeSet<String>();

		titleImage.setResource(SocatUploadDashboard.resources.getSocatCatPng());
		logoutButton.setText(LOGOUT_TEXT);

		uploadButton.setText(UPLOAD_TEXT);
		uploadButton.setTitle(UPLOAD_HOVER_HELP);

		viewDataButton.setText(VIEW_DATA_TEXT);
		viewDataButton.setTitle(VIEW_DATA_HOVER_HELP);

		omeMetadataButton.setText(OME_METADATA_TEXT);
		omeMetadataButton.setTitle(OME_METADATA_HOVER_HELP);

		addlDocsButton.setText(ADDL_DOCS_TEXT);
		addlDocsButton.setTitle(ADDL_DOCS_HOVER_HELP);

		reviewButton.setText(REVIEW_TEXT);
		reviewButton.setTitle(REVIEW_HOVER_HELP);

		qcSubmitButton.setText(QC_SUBMIT_TEXT);
		qcSubmitButton.setTitle(QC_SUBMIT_HOVER_HELP);

		deleteButton.setText(DELETE_TEXT);
		deleteButton.setTitle(DELETE_HOVER_HELP);

		addToListButton.setText(ADD_TO_LIST_TEXT);
		addToListButton.setTitle(ADD_TO_LIST_HOVER_HELP);

		removeFromListButton.setText(REMOVE_FROM_LIST_TEXT);
		removeFromListButton.setTitle(REMOVE_FROM_LIST_HOVER_HELP);

		managerButtonsShown = true;
		askDeletePopup = null;
		askRemovePopup = null;
		askDataAutofailPopup = null;
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
		final String errMsg;
		final boolean clearExpocodes;
		if ( loggingIn ) {
			errMsg = LOGIN_ERROR_MSG;
			clearExpocodes = true;
		}
		else {
			errMsg = GET_DATASET_LIST_ERROR_MSG;
			clearExpocodes = false;
		}
		SocatUploadDashboard.showWaitCursor();
		// Request the latest cruise list
		service.getCruiseList(DashboardLoginPage.getUsername(), 
								 DashboardLoginPage.getPasshash(),
								 new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruises) {
				if ( DashboardLoginPage.getUsername()
										.equals(cruises.getUsername()) ) {
					if ( singleton == null ) {
						singleton = new CruiseListPage();
					}
					else if ( clearExpocodes ) {
						singleton.expocodeSet.clear();
					}
					SocatUploadDashboard.updateCurrentPage(singleton);
					singleton.updateCruises(cruises);
					History.newItem(PagesEnum.SHOW_DATASETS.name(), false);
				}
				else {
					SocatUploadDashboard.showMessage(errMsg + 
							UNEXPECTED_INVALID_DATESET_LIST_MSG);
				}
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(errMsg, ex);
				SocatUploadDashboard.showAutoCursor();
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
		// If never shown before, or if the username does not match the 
		// current login username, show the login page instead
		if ( (singleton == null) || 
			 ! singleton.username.equals(DashboardLoginPage.getUsername()) ) {
			DashboardLoginPage.showPage(true);
		}
		else {
			SocatUploadDashboard.updateCurrentPage(singleton);
			if ( addToHistory )
				History.newItem(PagesEnum.SHOW_DATASETS.name(), false);
		}
	}

	/**
	 * Add a cruise to the selected list when the page is displayed.
	 * This should be called just prior to calling showPage(false).  
	 * If no cruise with the given expocode exists in the updated 
	 * list of cruises, this expocode will be ignored. 
	 * 
	 * @param expocode
	 * 		select the cruise with this expocode
	 */
	static void addSelectedCruise(String expocode) {
		if ( singleton == null )
			singleton = new CruiseListPage();
		singleton.expocodeSet.add(expocode);
	}

	/**
	 * Resorts the cruise list table first by upload timestamp 
	 * in descending order, then by expocode in ascending order.
	 */
	static void resortTable() {
		if ( singleton == null )
			singleton = new CruiseListPage();
		ColumnSortList sortList = singleton.datasetsGrid.getColumnSortList();
		sortList.push(new ColumnSortInfo(singleton.expocodeColumn, true));
		sortList.push(new ColumnSortInfo(singleton.timestampColumn, false));
		ColumnSortEvent.fire(singleton.datasetsGrid, sortList);
	}

	/**
	 * Updates the cruise list page with the current username and 
	 * with the cruises given in the argument.
	 * 
	 * @param cruises
	 * 		cruises to display
	 */
	private void updateCruises(DashboardCruiseList cruises) {
		// Update the title and username
		titleLabel.setText(TITLE_TEXT_PROLOGUE + 
				cruises.getSocatVersion() + TITLE_TEXT_EPILOGUE);
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);
		if ( cruises.isManager() ) {
			if ( ! managerButtonsShown ) {
				// Add manager-specific buttons - currently no manager-specific buttons
				managerButtonsShown = true;
			}
		}
		else {
			if ( managerButtonsShown ) {
				// Remove manager-specific buttons - currently no manager-specific buttons
				managerButtonsShown = false;
			}
		}
		// Update the cruises shown by resetting the data in the data provider
		List<DashboardCruise> cruiseList = listProvider.getList();
		cruiseList.clear();
		if ( cruises != null ) {
			cruiseList.addAll(cruises.values());
		}
		for ( DashboardCruise cruise : cruiseList ) {
			if ( expocodeSet.contains(cruise.getExpocode()) )
				cruise.setSelected(true);
			else
				cruise.setSelected(false);
		}
		datasetsGrid.setRowCount(cruiseList.size());
		datasetsGrid.setVisibleRange(0, cruiseList.size());
		// Make sure the table is sorted according to the last specification
		ColumnSortEvent.fire(datasetsGrid, datasetsGrid.getColumnSortList());
	}

	/**
	 * @param cruise
	 * 		cruise to check
	 * @return
	 * 		null if the cruise is published (previous SOCAT version),
	 * 		false if the cruise is submitted, or
	 * 		true if the cruise is not submitted.
	 */
	private Boolean isEditableCruise(DashboardCruise cruise) {
		// null for published cruises
		String status = cruise.getArchiveStatus();
		if ( ! ( status.equals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED) ||
				 status.equals(DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT) ||
				 status.equals(DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC) ||
				 status.equals(DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE) ) ) 
			return null;
		// false for submitted cruises
		status = cruise.getQcStatus();
		if ( ! ( status.equals(SocatQCEvent.QC_STATUS_NOT_SUBMITTED) || 
				 status.equals(SocatQCEvent.QC_STATUS_UNACCEPTABLE) ||
				 status.equals(SocatQCEvent.QC_STATUS_SUSPENDED) ||
				 status.equals(SocatQCEvent.QC_STATUS_EXCLUDED) )  ) 
			return Boolean.FALSE;
		// true for unsubmitted cruises
		return Boolean.TRUE;
	}

	/**
	 * Selects cruises of the given type in the cruise list, supplementing the currently
	 * selected cruises, or clears all selected cruises.
	 * 
	 * @param option
	 * 		string indicating the cruise types to select;  one of: <br />
	 * 		MIXED_SELECTION_OPTION - does nothing; <br />
	 * 		ALL_SELECTION_OPTION - selects all cruises; <br />
	 * 		EDITABLE_SELECTION_OPTION - selects editable cruises; <br />
	 * 		SUBMITTED_SELECTION_OPTION - selects submitted cruises; <br />
	 * 		PUBLISHED_SELECTION_OPTION - selects published cruises; or <br />
	 * 		CLEAR_SELECTION_OPTION - clears all selected cruises.
	 */
	private void setCruiseSelection(String option) {
		// Do nothing is MIXED_SELECTION_OPTION is given
		if ( MIXED_SELECTION_OPTION.equals(option) )
			return;
		// Modify the cruise selection
		List<DashboardCruise> cruiseList = listProvider.getList();
		if ( ALL_SELECTION_OPTION.equals(option) ) {
			for ( DashboardCruise cruise : cruiseList )
				cruise.setSelected(true);
		}
		else if ( EDITABLE_SELECTION_OPTION.equals(option) ) {
			for ( DashboardCruise cruise : cruiseList )
				if ( Boolean.TRUE.equals(isEditableCruise(cruise)) )
					cruise.setSelected(true);
		}
		else if ( SUBMITTED_SELECTION_OPTION.equals(option) ) {
			for ( DashboardCruise cruise : cruiseList )
				if ( Boolean.FALSE.equals(isEditableCruise(cruise)) )
					cruise.setSelected(true);
		}
		else if ( PUBLISHED_SELECTION_OPTION.equals(option) ) {
			for ( DashboardCruise cruise : cruiseList )
				if ( null == isEditableCruise(cruise) )
					cruise.setSelected(true);
		}
		else if ( CLEAR_SELECTION_OPTION.equals(option) ) {
			for ( DashboardCruise cruise : cruiseList )
				cruise.setSelected(false);
		}
		else {
			throw new RuntimeException("Unexpected option given the setCruiseSelection: " + option);
		}
		datasetsGrid.setRowCount(cruiseList.size());
		datasetsGrid.setVisibleRange(0, cruiseList.size());
		// Make sure the table is sorted according to the last specification
		ColumnSortEvent.fire(datasetsGrid, datasetsGrid.getColumnSortList());
	}

	/**
	 * @return
	 * 		the ..._SELECTION_OPTION String that describes the currently selected cruises;
	 * 		one of MIXED_SELECTION_OPTION, ALL_SELECTION_OPTION, EDITABLE_SELECTION_OPTION, 
	 * 		SUBMITTED_SELECTION_OPTION, PUBLISHED_SELECTION_OPTION, CLEAR_SELECTION_OPTION
	 * - unused at this time, thus commented out
	private String getSelectedCruisesType() {
		List<DashboardCruise> cruiseList = listProvider.getList();
		boolean isCleared = true;
		boolean isAll = true;
		Boolean isAllEditable = null;
		Boolean isAllSubmitted = null;
		Boolean isAllPublished = null;
		for ( DashboardCruise cruise : cruiseList ) {
			Boolean editable = isEditableCruise(cruise);
			if ( cruise.isSelected() ) {
				isCleared = false;
				if ( null == editable ) {
					// Published cruise selected
					if ( null == isAllPublished )
						isAllPublished = true;
					isAllEditable = false;
					isAllSubmitted = false;
				}
				else if ( ! editable ) {
					// Submitted cruise selected
					if ( null == isAllSubmitted )
						isAllSubmitted = true;
					isAllEditable = false;
					isAllPublished = false;
				}
				else {
					// Editable cruise selected
					if ( null == isAllEditable )
						isAllEditable = true;
					isAllSubmitted = false;
					isAllPublished = false;
				}
			}
			else {
				isAll = false;
				if ( null == editable ) {
					// Published cruise not selected
					isAllPublished = false;
				}
				else if ( ! editable ) {
					// Submitted cruise not selected
					isAllSubmitted = false;
				}
				else {
					// Editable cruise not selected
					isAllEditable = false;
				}
			}
		}
		String selectType;
		if ( isCleared )
			selectType = CLEAR_SELECTION_OPTION;
		else if ( isAll )
			selectType = ALL_SELECTION_OPTION;
		else if ( Boolean.TRUE.equals(isAllEditable) )
			selectType = EDITABLE_SELECTION_OPTION;
		else if ( Boolean.TRUE.equals(isAllSubmitted) )
			selectType = SUBMITTED_SELECTION_OPTION;
		else if ( Boolean.TRUE.equals(isAllPublished) )
			selectType = PUBLISHED_SELECTION_OPTION;
		else
			selectType = MIXED_SELECTION_OPTION;
		return selectType;
	}
	*/

	/**
	 * Assigns cruiseSet with the set of selected cruises, and 
	 * expocodeSet with the expocodes of these cruises. 
	 *  
	 * @param onlyEditable
	 * 		if true, fails if a submitted or published cruise is selected;
	 * 		if false, fails if a published cruise is selected;
	 * 		if null, always succeeds.
	 * @return
	 * 		if successful
	 */
	private boolean getSelectedCruises(Boolean onlyEditable) {
		cruiseSet.clear();
		expocodeSet.clear();
		for ( DashboardCruise cruise : listProvider.getList() ) {
			if ( cruise.isSelected() ) {
				if ( onlyEditable != null ) {
					Boolean editable = isEditableCruise(cruise);
					// check if from a previous SOCAT version
					if ( editable == null )
						return false;
					// check if editable, if requested
					if ( onlyEditable && ! editable )
						return false;
				}
				cruiseSet.add(cruise);
				expocodeSet.add(cruise.getExpocode());
			}
		}
		return true;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("uploadButton")
	void uploadCruiseOnClick(ClickEvent event) {
		// Save the expocodes of the currently selected cruises
		getSelectedCruises(null);
		// Go to the cruise upload page
		CruiseUploadPage.showPage();
	}

	@UiHandler("viewDataButton")
	void dataCheckOnClick(ClickEvent event) {
		if ( ! getSelectedCruises(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_DATASETS_SELECTED_ERR_START + FOR_REVIEWING_ERR_END);
			return;
		}
		if ( expocodeSet.size() < 1 ) {
			SocatUploadDashboard.showMessage(
					NO_DATASET_SELECTED_ERR_START + FOR_REVIEWING_ERR_END);
			return;
		}
		DataColumnSpecsPage.showPage(new ArrayList<String>(expocodeSet));
	}

	@UiHandler("omeMetadataButton")
	void omeOnClick(ClickEvent event) {
		if ( ! getSelectedCruises(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_DATASETS_SELECTED_ERR_START + FOR_OME_ERR_END);
			return;
		}
		if ( cruiseSet.size() < 1 ) {
			SocatUploadDashboard.showMessage(
					NO_DATASET_SELECTED_ERR_START + FOR_OME_ERR_END);
			return;
		}
		if ( cruiseSet.size() > 1 ) {
			SocatUploadDashboard.showMessage(
					MANY_DATASETS_SELECTED_ERR_START + FOR_OME_ERR_END);
			return;
		}
		OmeManagerPage.showPage(cruiseSet.iterator().next());
	}

	@UiHandler("addlDocsButton")
	void addlDocsOnClick(ClickEvent event) {
		if ( ! getSelectedCruises(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_DATASETS_SELECTED_ERR_START + FOR_ADDL_DOCS_ERR_END);
			return;
		}
		if ( cruiseSet.size() < 1 ) {
			SocatUploadDashboard.showMessage(
					NO_DATASET_SELECTED_ERR_START + FOR_ADDL_DOCS_ERR_END);
			return;
		}
		AddlDocsManagerPage.showPage(cruiseSet);
	}

	@UiHandler("reviewButton")
	void reviewOnClick(ClickEvent event) {
		// TODO: implement
		SocatUploadDashboard.showMessage("Not yet implemented");
		return;
	}

	@UiHandler("qcSubmitButton")
	void qcSubmitOnClick(ClickEvent event) {
		if ( ! getSelectedCruises(false) ) {
			SocatUploadDashboard.showMessage(
					ARCHIVED_DATASETS_SELECTED_ERR_START + FOR_QC_SUBMIT_ERR_END);
			return;
		}
		if ( cruiseSet.size() == 0 ) {
			SocatUploadDashboard.showMessage(
					NO_DATASET_SELECTED_ERR_START + FOR_QC_SUBMIT_ERR_END);
			return;
		}
		checkSet.clear();
		checkSet.addAll(cruiseSet);
		checkCruisesForSocat();
	}

	@UiHandler("deleteButton")
	void deleteCruiseOnClick(ClickEvent event) {
		if ( ! getSelectedCruises(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_DATASETS_SELECTED_ERR_START + FOR_DELETE_ERR_END);
			return;
		}
		if ( expocodeSet.size() == 0 ) {
			SocatUploadDashboard.showMessage(
					NO_DATASET_SELECTED_ERR_START + FOR_DELETE_ERR_END);
			return;
		}
		// Confirm cruises to be deleted
		String message = DELETE_DATASET_HTML_PROLOGUE;
		for ( String expocode : expocodeSet )
			message += "<li>" + SafeHtmlUtils.htmlEscape(expocode) + "</li>";
		message += DELETE_DATASET_HTML_EPILOGUE;
		if ( askDeletePopup == null ) {
			askDeletePopup = new DashboardAskPopup(DELETE_YES_TEXT, 
					DELETE_NO_TEXT, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean okay) {
					// Only proceed only if yes button was selected
					if ( okay ) {
						// never delete the metadata or supplemental documents
						continueDeleteCruises(false);
					}
				}
				@Override
				public void onFailure(Throwable ex) {
					// Never called
					;
				}
			});
		}
		askDeletePopup.askQuestion(message);
	}

	/**
	 * Makes the request to delete the currently selected cruises,
	 * and processes the results.
	 */
	private void continueDeleteCruises(Boolean deleteMetadata) {
		SocatUploadDashboard.showWaitCursor();
		service.deleteCruises(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), expocodeSet,
				deleteMetadata, new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruises) {
				if ( DashboardLoginPage.getUsername()
						.equals(cruises.getUsername()) ) {
					CruiseListPage.this.updateCruises(cruises);
				}
				else {
					SocatUploadDashboard.showMessage(DELETE_DATASET_FAIL_MSG + 
							UNEXPECTED_INVALID_DATESET_LIST_MSG);
				}
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(DELETE_DATASET_FAIL_MSG, ex);
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

	@UiHandler("addToListButton")
	void addToListOnClick(ClickEvent event) {
		String wildExpocode = Window.prompt(EXPOCODE_TO_ADD_MSG, "");
		if ( (wildExpocode != null) && ! wildExpocode.trim().isEmpty() ) {
			SocatUploadDashboard.showWaitCursor();
			// Save the currently selected cruises
			getSelectedCruises(null);
			service.addCruisesToList(DashboardLoginPage.getUsername(), 
					DashboardLoginPage.getPasshash(), wildExpocode, 
					new AsyncCallback<DashboardCruiseList>() {
				@Override
				public void onSuccess(DashboardCruiseList cruises) {
					if ( DashboardLoginPage.getUsername()
							.equals(cruises.getUsername()) ) {
						CruiseListPage.this.updateCruises(cruises);
					}
					else {
						SocatUploadDashboard.showMessage(ADD_DATASET_FAIL_MSG + 
								UNEXPECTED_INVALID_DATESET_LIST_MSG);
					}
					SocatUploadDashboard.showAutoCursor();
				}
				@Override
				public void onFailure(Throwable ex) {
					SocatUploadDashboard.showFailureMessage(ADD_DATASET_FAIL_MSG, ex);
					SocatUploadDashboard.showAutoCursor();
				}
			});
		}
	}

	@UiHandler("removeFromListButton")
	void removeFromListOnClick(ClickEvent event) {
		getSelectedCruises(null);
		if ( expocodeSet.size() == 0 ) {
			SocatUploadDashboard.showMessage(
					NO_DATASET_SELECTED_ERR_START + FOR_REMOVE_ERR_END);
			return;
		}
		// Confirm cruises to be removed
		String message = REMOVE_DATASET_HTML_PROLOGUE;
		for ( String expocode : expocodeSet )
			message += "<li>" + SafeHtmlUtils.htmlEscape(expocode) + "</li>";
		message += REMOVE_DATASET_HTML_EPILOGUE;
		if ( askRemovePopup == null ) {
			askRemovePopup = new DashboardAskPopup(REMOVE_YES_TEXT, 
					REMOVE_NO_TEXT, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					// Only proceed if yes; ignore if no or null
					if ( result == true )
						continueRemoveCruisesFromList();
				}
				@Override
				public void onFailure(Throwable ex) {
					// Never called
					;
				}
			});
		}
		askRemovePopup.askQuestion(message);
	}

	/**
	 * Makes the request to remove cruises from a user's list,
	 * and processes the results.
	 */
	private void continueRemoveCruisesFromList() {
		SocatUploadDashboard.showWaitCursor();
		service.removeCruisesFromList(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), expocodeSet, 
				new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruises) {
				if ( DashboardLoginPage.getUsername()
						.equals(cruises.getUsername()) ) {
					CruiseListPage.this.updateCruises(cruises);
				}
				else {
					SocatUploadDashboard.showMessage(REMOVE_DATASET_FAIL_MSG + 
							UNEXPECTED_INVALID_DATESET_LIST_MSG);
				}
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(REMOVE_DATASET_FAIL_MSG, ex);
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

	/**
	 * Creates the cruise data table columns.  The table will still need 
	 * to be populated using {@link #updateCruises(DashboardCruiseList)}.
	 */
	private void buildCruiseListTable() {
		Header<String> selectedHeader = buildSelectionHeader();

		// Create the columns for this table
		TextColumn<DashboardCruise> rowNumColumn = buildRowNumColumn();
		Column<DashboardCruise,Boolean> selectedColumn = buildSelectedColumn();
		expocodeColumn = buildExpocodeColumn();
		Column<DashboardCruise,String> dataCheckColumn = buildDataCheckColumn();
		Column<DashboardCruise,String> omeMetadataColumn = buildOmeMetadataColumn();
		Column<DashboardCruise,String> qcStatusColumn = buildQCStatusColumn();
		Column<DashboardCruise,String> archiveStatusColumn = buildArchiveStatusColumn();
		timestampColumn = buildTimestampColumn();
		TextColumn<DashboardCruise> filenameColumn = buildFilenameColumn();
		Column<DashboardCruise,String> addlDocsColumn = buildAddnDocsColumn();
		TextColumn<DashboardCruise> ownerColumn = buildOwnerColumn();

		// Add the columns, with headers, to the table
		datasetsGrid.addColumn(rowNumColumn, "");
		datasetsGrid.addColumn(selectedColumn, selectedHeader);
		datasetsGrid.addColumn(expocodeColumn, 
				SafeHtmlUtils.fromSafeConstant(EXPOCODE_COLUMN_NAME));
		datasetsGrid.addColumn(timestampColumn, 
				SafeHtmlUtils.fromSafeConstant(TIMESTAMP_COLUMN_NAME));
		datasetsGrid.addColumn(dataCheckColumn, 
				SafeHtmlUtils.fromSafeConstant(DATA_CHECK_COLUMN_NAME));
		datasetsGrid.addColumn(omeMetadataColumn, 
				SafeHtmlUtils.fromSafeConstant(OME_METADATA_COLUMN_NAME));
		datasetsGrid.addColumn(qcStatusColumn, 
				SafeHtmlUtils.fromSafeConstant(SUBMITTED_COLUMN_NAME));
		datasetsGrid.addColumn(archiveStatusColumn, 
				SafeHtmlUtils.fromSafeConstant(ARCHIVED_COLUMN_NAME));
		datasetsGrid.addColumn(filenameColumn, 
				SafeHtmlUtils.fromSafeConstant(FILENAME_COLUMN_NAME));
		datasetsGrid.addColumn(addlDocsColumn, 
				SafeHtmlUtils.fromSafeConstant(ADDL_DOCS_COLUMN_NAME));
		datasetsGrid.addColumn(ownerColumn, 
				SafeHtmlUtils.fromSafeConstant(OWNER_COLUMN_NAME));

		// Set the minimum widths of the columns
		double minTableWidth = 0.0;
		datasetsGrid.setColumnWidth(rowNumColumn, 
				SocatUploadDashboard.CHECKBOX_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.CHECKBOX_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(selectedColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(expocodeColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(timestampColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(dataCheckColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(omeMetadataColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(qcStatusColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(archiveStatusColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(filenameColumn, 
				SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.FILENAME_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(addlDocsColumn, 
				SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.FILENAME_COLUMN_WIDTH;
		datasetsGrid.setColumnWidth(ownerColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;

		// Set the minimum width of the full table
		datasetsGrid.setMinimumTableWidth(minTableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardCruise>();
		listProvider.addDataDisplay(datasetsGrid);

		// Make the columns sortable
		expocodeColumn.setSortable(true);
		timestampColumn.setSortable(true);
		dataCheckColumn.setSortable(true);
		omeMetadataColumn.setSortable(true);
		qcStatusColumn.setSortable(true);
		archiveStatusColumn.setSortable(true);
		filenameColumn.setSortable(true);
		addlDocsColumn.setSortable(true);
		ownerColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<DashboardCruise> columnSortHandler = 
				new ListHandler<DashboardCruise>(listProvider.getList());
		columnSortHandler.setComparator(expocodeColumn, 
				DashboardCruise.expocodeComparator);
		columnSortHandler.setComparator(timestampColumn, 
				DashboardCruise.timestampComparator);
		columnSortHandler.setComparator(dataCheckColumn, 
				DashboardCruise.dataCheckComparator);
		columnSortHandler.setComparator(omeMetadataColumn, 
				DashboardCruise.omeTimestampComparator);
		columnSortHandler.setComparator(qcStatusColumn, 
				DashboardCruise.qcStatusComparator);
		columnSortHandler.setComparator(archiveStatusColumn, 
				DashboardCruise.archiveStatusComparator);
		columnSortHandler.setComparator(filenameColumn, 
				DashboardCruise.filenameComparator);
		columnSortHandler.setComparator(addlDocsColumn, 
				DashboardCruise.addlDocsComparator);
		columnSortHandler.setComparator(ownerColumn, 
				DashboardCruise.ownerComparator);

		// Add the sort handler to the table, and set the default sort order
		datasetsGrid.addColumnSortHandler(columnSortHandler);
		resortTable();

		// Set the contents if there are no rows
		datasetsGrid.setEmptyTableWidget(new Label(EMPTY_TABLE_TEXT));

		// Following recommended to improve efficiency with IE
		datasetsGrid.setSkipRowHoverCheck(false);
		datasetsGrid.setSkipRowHoverFloatElementCheck(false);
		datasetsGrid.setSkipRowHoverStyleUpdate(false);
	}

	/**
	 * @return the row number column for the table
	 */
	private TextColumn<DashboardCruise> buildRowNumColumn() {
		TextColumn<DashboardCruise> rowNumColumn = new TextColumn<DashboardCruise>() {
			@Override
			public String getValue(DashboardCruise cruise) {
				String expocode = cruise.getExpocode();
				List<DashboardCruise> cruiseList = listProvider.getList();
				int k = 0;
				while ( k < cruiseList.size() ) {
					// Only check expocodes since they should be unique
					if ( expocode.equals(cruiseList.get(k).getExpocode()) )
						break;
					k++;
				}
				return Integer.toString(k+1);
			}
			@Override
			public void render(Cell.Context ctx, DashboardCruise cruise, 
													SafeHtmlBuilder sb) {
				String msg = getValue(cruise);
				sb.appendHtmlConstant("<div style=\"color: " + 
						SocatUploadDashboard.ROW_NUMBER_COLOR + ";\">");
				for (int k = msg.length(); k < 4; k++)
					sb.appendHtmlConstant("&nbsp;");
				sb.appendEscaped(msg);
				sb.appendHtmlConstant("</div>");
			}
		};
		return rowNumColumn;
	}

	/**
	 * @return the selection header for the table
	 */
	private Header<String> buildSelectionHeader() {
		SelectionCell selectCell = new SelectionCell(Arrays.asList(
				MIXED_SELECTION_OPTION, 
				ALL_SELECTION_OPTION, 
				EDITABLE_SELECTION_OPTION, 
				SUBMITTED_SELECTION_OPTION, 
				PUBLISHED_SELECTION_OPTION, 
				CLEAR_SELECTION_OPTION));
		Header<String> selectedHeader = new Header<String>(selectCell) {
			@Override
			public String getValue() {
				return MIXED_SELECTION_OPTION;
			}
		};
		selectedHeader.setUpdater(new ValueUpdater<String>() {
			@Override
			public void update(String option) {
				if ( option == null )
					return;
				setCruiseSelection(option);
			}
		});
		return selectedHeader;
	}

	/**
	 * @return the selection column for the table
	 */
	private Column<DashboardCruise,Boolean> buildSelectedColumn() {
		Column<DashboardCruise,Boolean> selectedColumn = 
				new Column<DashboardCruise,Boolean>(new CheckboxCell(true, true)) {
			@Override
			public Boolean getValue(DashboardCruise cruise) {
				return cruise.isSelected();
			}
		};
		selectedColumn.setFieldUpdater(new FieldUpdater<DashboardCruise,Boolean>() {
			@Override
			public void update(int index, DashboardCruise cruise, Boolean value) {
				if ( ! value ) {
					cruise.setSelected(false);
				}
				else {
					cruise.setSelected(true);
				}
			}
		});
		return selectedColumn;
	}

	/**
	 * @return the expocode column for the table
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
	 * @return the timestamp column for the table
	 */
	private TextColumn<DashboardCruise> buildTimestampColumn() {
		TextColumn<DashboardCruise> timestampColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String timestamp = cruise.getUploadTimestamp();
				if ( timestamp.isEmpty() )
					timestamp = NO_TIMESTAMP_STRING;
				return timestamp;
			}
		};
		return timestampColumn;
	}

	/**
	 * @return the data-check status column for the table
	 */
	private Column<DashboardCruise,String> buildDataCheckColumn() {
		Column<DashboardCruise,String> dataCheckColumn = 
				new Column<DashboardCruise,String> (new ClickableTextCell()) {
			@Override
			public String getValue(DashboardCruise cruise) { 
				String status = cruise.getDataCheckStatus();
				if ( status.isEmpty() ) {
					status = NO_DATA_CHECK_STATUS_STRING;
				}
				else if ( status.startsWith( 
						DashboardUtils.CHECK_STATUS_ERRORS_PREFIX) ) { 
					status = status.substring(
							DashboardUtils.CHECK_STATUS_ERRORS_PREFIX.length());
				}
				else if ( status.startsWith(
						DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX) ) {
					status = status.substring(
							DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX.length());
				}
				return status;
			}
			@Override
			public void render(Cell.Context ctx, DashboardCruise cruise, 
													SafeHtmlBuilder sb) {
				Boolean editable = isEditableCruise(cruise);
				String msg = getValue(cruise);
				if ( msg.equals(DashboardUtils.CHECK_STATUS_ACCEPTABLE) ) {
					// No problems - use normal background
					if ( Boolean.TRUE.equals(editable) ) {
						sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
						sb.appendEscaped(msg);
						sb.appendHtmlConstant("</em></u></div>");
					}
					else {
						sb.appendHtmlConstant("<div>");
						sb.appendEscaped(msg);
						sb.appendHtmlConstant("</div>");
					}
				}
				else if ( msg.contains("warnings") || (msg.contains("errors") && 
						(cruise.getNumErrorRows() <= DashboardUtils.MAX_ACCEPTABLE_ERRORS)) ) {
					// Only warnings or a few errors - use warning background color
					if ( Boolean.TRUE.equals(editable) ) {
						sb.appendHtmlConstant("<div style=\"cursor:pointer; background-color:" +
								SocatUploadDashboard.WARNING_COLOR + ";\"><u><em>");
						sb.appendEscaped(msg);
						sb.appendHtmlConstant("</em></u></div>");
					}
					else {
						sb.appendHtmlConstant("<div style=\"background-color:" +
								SocatUploadDashboard.WARNING_COLOR + ";\">");
						sb.appendEscaped(msg);
						sb.appendHtmlConstant("</div>");
					}
				}
				else {
					// Many errors, unacceptable, or not checked - use error background color
					if ( Boolean.TRUE.equals(editable) ) {
						sb.appendHtmlConstant("<div style=\"cursor:pointer; background-color:" +
								SocatUploadDashboard.ERROR_COLOR + ";\"><u><em>");
						sb.appendEscaped(msg);
						sb.appendHtmlConstant("</em></u></div>");
					}
					else {
						sb.appendHtmlConstant("<div style=\"background-color:" +
								SocatUploadDashboard.ERROR_COLOR + ";\">");
						sb.appendEscaped(msg);
						sb.appendHtmlConstant("</div>");
					}
				}
			}
		};
		dataCheckColumn.setFieldUpdater(new FieldUpdater<DashboardCruise,String>() {
			@Override
			public void update(int index, DashboardCruise cruise, String value) {
				// Respond only for cruises that have not been submitted
				Boolean editable = isEditableCruise(cruise);
				if ( Boolean.TRUE.equals(editable) ) {
					// Save the currently selected cruises
					getSelectedCruises(null);
					// Open the data column specs page for this one cruise
					ArrayList<String> expocodes = new ArrayList<String>(1);
					expocodes.add(cruise.getExpocode());
					DataColumnSpecsPage.showPage(expocodes);
				}
			}
		});
		return dataCheckColumn;
	}

	/**
	 * @return the OME metadata filename column for the table
	 */
	private Column<DashboardCruise,String> buildOmeMetadataColumn() {
		Column<DashboardCruise,String> omeMetadataColumn = 
				new Column<DashboardCruise,String> (new ClickableTextCell()) {
			@Override
			public String getValue(DashboardCruise cruise) {
				String omeTimestamp = cruise.getOmeTimestamp();
				if ( omeTimestamp.isEmpty() )
					omeTimestamp = NO_OME_METADATA_STATUS_STRING;
				return omeTimestamp;
			}
			@Override
			public void render(Cell.Context ctx, DashboardCruise cruise, 
													SafeHtmlBuilder sb) {
				Boolean editable = isEditableCruise(cruise);
				if ( Boolean.TRUE.equals(editable) ) {
					sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
					sb.appendEscaped(getValue(cruise));
					sb.appendHtmlConstant("</em></u></div>");
				}
				else {
					sb.appendHtmlConstant("<div>");
					sb.appendEscaped(getValue(cruise));
					sb.appendHtmlConstant("</div>");
				}
			}
		};
		omeMetadataColumn.setFieldUpdater(new FieldUpdater<DashboardCruise,String>() {
			@Override
			public void update(int index, DashboardCruise cruise, String value) {
				// Respond only for cruises that have not been submitted
				Boolean editable = isEditableCruise(cruise);
				if ( Boolean.TRUE.equals(editable) ) {
					// Save the currently selected cruises
					getSelectedCruises(null);
					// Show the OME metadata manager page for this one cruise
					OmeManagerPage.showPage(cruise);
				}
			}
		});
		return omeMetadataColumn;
	}

	/**
	 * @return the QC submission status column for the table
	 */
	private Column<DashboardCruise,String> buildQCStatusColumn() {
		Column<DashboardCruise,String> qcStatusColumn = 
				new Column<DashboardCruise,String> (new ClickableTextCell()) {
			@Override
			public String getValue(DashboardCruise cruise) {
				String status = cruise.getQcStatus();
				if ( status.isEmpty() )
					status = NO_QC_STATUS_STRING;
				return status;
			}
			@Override
			public void render(Cell.Context ctx, DashboardCruise cruise, 
													SafeHtmlBuilder sb) {
				Boolean editable = isEditableCruise(cruise);
				if ( editable != null ) {
					sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
					sb.appendEscaped(getValue(cruise));
					sb.appendHtmlConstant("</em></u></div>");
				}
				else {
					sb.appendHtmlConstant("<div>");
					sb.appendEscaped(getValue(cruise));
					sb.appendHtmlConstant("</div>");
				}
			}
		};
		qcStatusColumn.setFieldUpdater(new FieldUpdater<DashboardCruise,String>() {
			@Override
			public void update(int index, DashboardCruise cruise, String value) {
				// Respond only for cruises in this version
				Boolean editable = isEditableCruise(cruise);
				if ( editable != null ) {
					// Save the currently selected cruises (in expocodeSet)
					getSelectedCruises(null);
					// Go to the QC page after performing the client-side checks on this one cruise
					checkSet.clear();
					checkSet.add(cruise);
					checkCruisesForSocat();
				}
			}
		});
		return qcStatusColumn;
	}

	/**
	 * @return the archive submission status column for the table
	 */
	private Column<DashboardCruise,String> buildArchiveStatusColumn() {
		Column<DashboardCruise,String> archiveStatusColumn = 
				new Column<DashboardCruise,String> (new ClickableTextCell()) {
			@Override
			public String getValue(DashboardCruise cruise) {
				String status = cruise.getArchiveStatus();
				if ( status.isEmpty() )
					status = NO_ARCHIVE_STATUS_STRING;
				return status;
			}
			@Override
			public void render(Cell.Context ctx, DashboardCruise cruise, 
													SafeHtmlBuilder sb) {
				Boolean editable = isEditableCruise(cruise);
				if ( editable != null ) {
					sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
					sb.appendEscaped(getValue(cruise));
					sb.appendHtmlConstant("</em></u></div>");
				}
				else {
					sb.appendHtmlConstant("<div>");
					sb.appendEscaped(getValue(cruise));
					sb.appendHtmlConstant("</div>");
				}
			}
		};
		archiveStatusColumn.setFieldUpdater(new FieldUpdater<DashboardCruise,String>() {
			@Override
			public void update(int index, DashboardCruise cruise, String value) {
				// Respond only for cruises in this version
				Boolean editable = isEditableCruise(cruise);
				if ( editable != null ) {
					// Save the currently selected cruises (in expocodeSet)
					getSelectedCruises(null);
					// Go to the QC page after performing the client-side checks on this one cruise
					checkSet.clear();
					checkSet.add(cruise);
					checkCruisesForSocat();
				}
			}
		});
		return archiveStatusColumn;
	}

	/**
	 * @return the filename column for the table
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

	/**
	 * @return the additional metadata files column for the table
	 */
	private Column<DashboardCruise,String> buildAddnDocsColumn() {
		Column<DashboardCruise,String> addnDocsColumn = 
				new Column<DashboardCruise,String>(new ClickableTextCell()) {
			@Override
			public String getValue(DashboardCruise cruise) {
				TreeSet<String> addlDocTitles = cruise.getAddlDocs();
				if ( addlDocTitles.size() == 0 )
					return NO_ADDL_DOCS_STATUS_STRING;
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				boolean firstEntry = true;
				for ( String title : addlDocTitles ) {
					if ( firstEntry )
						firstEntry = false;
					else
						sb.appendHtmlConstant("<br />");
					String[] pieces = DashboardMetadata.splitAddlDocsTitle(title);
					sb.appendEscaped(pieces[0]);
					sb.appendHtmlConstant("<br /><small>&nbsp;&nbsp;(");
					sb.appendEscaped(pieces[1]);
					sb.appendHtmlConstant(")</small>");
				}
				return sb.toSafeHtml().asString();
			}
			@Override
			public void render(Cell.Context ctx, DashboardCruise cruise, 
													SafeHtmlBuilder sb) {
				Boolean editable = isEditableCruise(cruise);
				if ( Boolean.TRUE.equals(editable) ) {
					sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
					sb.appendHtmlConstant(getValue(cruise));
					sb.appendHtmlConstant("</em></u></div>");
				}
				else {
					sb.appendHtmlConstant("<div>");
					sb.appendHtmlConstant(getValue(cruise));
					sb.appendHtmlConstant("</div>");
				}
			}
		};
		addnDocsColumn.setFieldUpdater(new FieldUpdater<DashboardCruise,String>() {
			@Override
			public void update(int index, DashboardCruise cruise, String value) {
				// Respond only for cruises that have not been submitted
				Boolean editable = isEditableCruise(cruise);
				if ( Boolean.TRUE.equals(editable) ) {
					// Save the currently selected cruises (in expocodeSet)
					getSelectedCruises(null);
					// Go to the additional docs page with just this one cruise
					// Go to the QC page after performing the client-side checks on this one cruise
					checkSet.clear();
					checkSet.add(cruise);
					AddlDocsManagerPage.showPage(checkSet);
				}
			}
		});
		return addnDocsColumn;
	}

	/**
	 * @return the owner column for the table
	 */
	 private TextColumn<DashboardCruise> buildOwnerColumn() {
	 	TextColumn<DashboardCruise> myOwnerColumn = 
	 			new TextColumn<DashboardCruise> () {
	 		@Override
	 		public String getValue(DashboardCruise cruise) {
	 			String owner = cruise.getOwner();
	 			if ( owner.isEmpty() )
	 				owner = NO_OWNER_STRING;
	 			return owner;
	 		}
	 	};
	 	return myOwnerColumn;
	 }

	/**
	 * Checks the cruises given in checkSet in this instance for metadata 
	 * compatibility for adding to SOCAT.  At this time this only checks 
	 * that an OME metadata document is associated with each cruise.
	 * 
	 * Then checks the cruises given in checkSet in this instance for data 
	 * compatibility for adding to SOCAT.  If the data has not been checked 
	 * or is unacceptable, this method presents an error message and returns.  
	 * If the data has serious issues to cause an automatic F flag, asks the 
	 * user if the submit should be continued.  If the answer is yes, or if 
	 * there were no serious data issues, continues the submission to SOCAT 
	 * by calling {@link AddToSocatPage#showPage(java.util.HashSet)}.
	 */
	private void checkCruisesForSocat() {
		// Check if the cruises have metadata documents
		String errMsg = NO_METADATA_HTML_PROLOGUE;
		boolean cannotSubmit = false;
		for ( DashboardCruise cruise : checkSet ) {
			// At this time, just check that a metadata file exists
			// and do not worry about the contents
			if ( cruise.getOmeTimestamp().isEmpty() ) {
				errMsg += "<li>" + 
						SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + "</li>";
				cannotSubmit = true;
			}
		}

		// If missing metadata, cannot submit
		if ( cannotSubmit ) {
			errMsg += NO_METADATA_HTML_EPILOGUE;
			SocatUploadDashboard.showMessage(errMsg);
			return;
		}

		// Check that the cruise data is checked and reasonable
		errMsg = CANNOT_SUBMIT_HTML_PROLOGUE;
		String warnMsg = DATA_AUTOFAIL_HTML_PROLOGUE;
		boolean willAutofail = false;
		for ( DashboardCruise cruise : checkSet ) {
			String status = cruise.getDataCheckStatus();
			if ( DashboardUtils.CHECK_STATUS_NOT_CHECKED.equals(status) ||
				 DashboardUtils.CHECK_STATUS_UNACCEPTABLE.equals(status) ) {
				errMsg += "<li>" + 
						 SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + "</li>";
				cannotSubmit = true;
			}
			else if ( status.equals(DashboardUtils.CHECK_STATUS_ACCEPTABLE) ||
					  status.startsWith(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX) ||
					  ( status.startsWith(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX) &&
						(cruise.getNumErrorRows() <= DashboardUtils.MAX_ACCEPTABLE_ERRORS) ) ) {
				// Acceptable
			}
			else {
				warnMsg += "<li>" + 
					 SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + "</li>";
				willAutofail = true;
			}
		}

		// If unchecked or very serious data issues, put up error message and stop
		if ( cannotSubmit ) {
			errMsg += CANNOT_SUBMIT_HTML_EPILOGUE;
			SocatUploadDashboard.showMessage(errMsg);
			return;
		}

		// If unreasonable data, ask to continue
		if ( willAutofail ) {
			warnMsg += AUTOFAIL_HTML_EPILOGUE;
			if ( askDataAutofailPopup == null ) {
				askDataAutofailPopup = new DashboardAskPopup(AUTOFAIL_YES_TEXT,
						AUTOFAIL_NO_TEXT, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean okay) {
						// Only proceed if yes; ignore if no or null
						if ( okay )
							AddToSocatPage.showPage(checkSet);
					}
					@Override
					public void onFailure(Throwable ex) {
						// Never called
						;
					}
				});
			}
			askDataAutofailPopup.askQuestion(warnMsg);
			return;
		}
		// No problems; continue on
		AddToSocatPage.showPage(checkSet);
	}

}
