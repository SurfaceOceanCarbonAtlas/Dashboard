/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.CruiseListService;
import gov.noaa.pmel.socat.dashboard.shared.CruiseListServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
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

	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String TITLE_TEXT = "List of Datasets";
	
	private static final String UPLOAD_TEXT = "Upload Datasets";
	private static final String UPLOAD_HOVER_HELP = 
			"upload data to create a new data set " +
			"or replace an existing data set";

	private static final String VIEW_DATA_TEXT = "Identify Columns";
	private static final String VIEW_DATA_HOVER_HELP =
			"review and modify data column type assignments for the " +
			"selected data set; highlights issues in the data";

	static final String OME_METADATA_TEXT = "Edit Metadata";
	private static final String OME_METADATA_HOVER_HELP =
			"edit the metadata for the selected data set";

	private static final String ADDL_DOCS_TEXT = "Manage Documents";
	private static final String ADDL_DOCS_HOVER_HELP =
			"manage ancillary documents for the selected data set";

	private static final String QC_SUBMIT_TEXT = "Submit for QC";
	private static final String QC_SUBMIT_HOVER_HELP =
			"submit the selected data sets for quality control assessment";

	private static final String REVIEW_TEXT = "Preview Dataset";
	private static final String REVIEW_HOVER_HELP =
			"examine the selected data sets in the data set viewer " +
			"aside other SOCAT data sets";

	private static final String DELETE_TEXT = "Delete Datasets";
	private static final String DELETE_HOVER_HELP =
			"delete the selected data set, " +
			"including its metadata and ancillary documents";

	private static final String ADD_TO_LIST_TEXT = 
			"Add to List";
	private static final String ADD_TO_LIST_HOVER_HELP = 
			"add an existing data set to this list of data sets";

	private static final String REMOVE_FROM_LIST_TEXT = 
			"Remove from List";
	private static final String REMOVE_FROM_LIST_HOVER_HELP =
			"remove the selected data sets from this list of data sets; " +
			"this will NOT delete the data set from the system";

	// Error messages when the request for the latest cruise list fails
	private static final String LOGIN_ERROR_MSG = 
			"Sorry, your login failed";
	private static final String GET_CRUISE_LIST_ERROR_MSG = 
			"Problems obtaining the latest data set listing";

	// Starts of error messages for improper cruise selections
	private static final String SUBMITTED_CRUISES_SELECTED_ERR_START = 
			"Only data sets which have not been submitted for QC, " +
			"or which have been suspended or failed, may be selected ";
	private static final String NO_CRUISE_SELECTED_ERR_START = 
			"No data set is select ";
	private static final String MANY_CRUISES_SELECTED_ERR_START = 
			"Only one data set may be selected ";

	// Ends of error messages for improper cruise selections
	private static final String FOR_REVIEWING_ERR_END = 
			"for reviewing data.";
	private static final String FOR_OME_ERR_END =
			"for managing metadata.";
	private static final String FOR_ADDL_DOCS_ERR_END = 
			"for managing ancillary documents.";
	private static final String FOR_QC_SUBMIT_ERR_END =
			"for submitting for QC.";
	private static final String FOR_DELETE_ERR_END = 
			"for deletion from the system.";
	private static final String FOR_REMOVE_ERR_END = 
			"for removal from your personal list of cruises.";

	private static final String NO_METADATA_HTML_PROLOGUE = 
			"The following data sets do not have appropriate metadata" +
			"and thus cannot be submitted for QC: <ul>";
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
			"but will be given a QC Flag F when added.  " +
			"Do you want to continue? ";
	private static final String AUTOFAIL_YES_TEXT = "Yes";
	private static final String AUTOFAIL_NO_TEXT = "No";

	private static final String DELETE_CRUISE_HTML_PROLOGUE = 
			"All data, metadata, and ancillary docuemnts will be deleted " +
			"for the following data sets: <ul>";
	private static final String DELETE_CRUISE_HTML_EPILOGUE =
			"</ul> Do you wish to proceed?";
	private static final String DELETE_YES_TEXT = "Yes";
	private static final String DELETE_NO_TEXT = "No";
	private static final String DELETE_CRUISE_FAIL_MSG = 
			"Unable to delete the data sets";

	private static final String EXPOCODE_TO_ADD_MSG = 
			"Enter the expocode of the data set to wish to add " +
			"to your personal list of data sets";
	private static final String ADD_CRUISE_FAIL_MSG = 
			"Unable to add the specified data set " +
			"to your personal list of data sets";

	private static final String REMOVE_CRUISE_HTML_PROLOGUE = 
			"The following data sets will be removed from your personal " +
			"list of data sets; the data, metadata, and ancillary documents " +
			"will <b>not</b> be removed: <ul>";
	private static final String REMOVE_CRUISE_HTML_EPILOGUE = 
			"</ul> Do you wish to proceed?";
	private static final String REMOVE_YES_TEXT = "Yes";
	private static final String REMOVE_NO_TEXT = "No";
	private static final String REMOVE_CRUISE_FAIL_MSG = 
			"Unable to remove the selected data sets " +
			"from your personal list of data sets";

	// Column header strings
	private static final String EXPOCODE_COLUMN_NAME = "Expocode";
	private static final String TIMESTAMP_COLUMN_NAME = "Upload Date";
	private static final String DATA_CHECK_COLUMN_NAME = "Data status";
	private static final String OME_METADATA_COLUMN_NAME = "Metadata";
	private static final String SUBMITTED_COLUMN_NAME = "Status";
	private static final String ARCHIVED_COLUMN_NAME = "Archival";
	private static final String FILENAME_COLUMN_NAME = "Filename";
	private static final String ADDL_DOCS_COLUMN_NAME = "Addl Documents";
	private static final String OWNER_COLUMN_NAME = "Owner";

	// Replacement strings for empty or null values
	private static final String EMPTY_TABLE_TEXT = "(no uploaded cruises)";
	private static final String NO_EXPOCODE_STRING = "(unknown)";
	private static final String NO_TIMESTAMP_STRING = "(unknown)";
	static final String NO_DATA_CHECK_STATUS_STRING = "(not checked)";
	private static final String NO_OME_METADATA_STATUS_STRING = "(no metadata)";
	private static final String NO_QC_STATUS_STRING = "(private)";
	private static final String NO_ARCHIVE_STATUS_STRING = "(not specified)";
	private static final String NO_UPLOAD_FILENAME_STRING = "(unknown)";
	private static final String NO_ADDL_DOCS_STATUS_STRING = "(no documents)";
	private static final String NO_OWNER_STRING = "(unknown)";

	interface DashboardCruiseListPageUiBinder extends UiBinder<Widget, CruiseListPage> {
	}

	private static DashboardCruiseListPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseListPageUiBinder.class);

	private static CruiseListServiceAsync service = 
			GWT.create(CruiseListService.class);

	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField Label titleLabel;
	@UiField Button uploadButton;
	@UiField Button viewDataButton;
	@UiField Button omeMetadataButton;
	@UiField Button addlDocsButton;
	@UiField Button qcSubmitButton;
	@UiField Button reviewButton;
	@UiField Button deleteButton;
	@UiField Button addToListButton;
	@UiField Button removeFromListButton;
	@UiField DataGrid<DashboardCruise> datasetsGrid;

	private String username;
	private ListDataProvider<DashboardCruise> listProvider;
	private DashboardAskPopup askDeletePopup;
	private DashboardAskPopup askRemovePopup;
	private HashSet<DashboardCruise> cruiseSet;
	private HashSet<String> expocodeSet;
	private DashboardAskPopup askDataAutofailPopup;
	private TextColumn<DashboardCruise> ownerColumn;
	private boolean ownerColumnShown;
	private double minTableWidth;

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
		expocodeSet = new HashSet<String>();

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		uploadButton.setText(UPLOAD_TEXT);
		uploadButton.setTitle(UPLOAD_HOVER_HELP);

		viewDataButton.setText(VIEW_DATA_TEXT);
		viewDataButton.setTitle(VIEW_DATA_HOVER_HELP);

		omeMetadataButton.setText(OME_METADATA_TEXT);
		omeMetadataButton.setTitle(OME_METADATA_HOVER_HELP);

		addlDocsButton.setText(ADDL_DOCS_TEXT);
		addlDocsButton.setTitle(ADDL_DOCS_HOVER_HELP);

		qcSubmitButton.setText(QC_SUBMIT_TEXT);
		qcSubmitButton.setTitle(QC_SUBMIT_HOVER_HELP);

		reviewButton.setText(REVIEW_TEXT);
		reviewButton.setTitle(REVIEW_HOVER_HELP);

		deleteButton.setText(DELETE_TEXT);
		deleteButton.setTitle(DELETE_HOVER_HELP);

		addToListButton.setText(ADD_TO_LIST_TEXT);
		addToListButton.setTitle(ADD_TO_LIST_HOVER_HELP);

		removeFromListButton.setText(REMOVE_FROM_LIST_TEXT);
		removeFromListButton.setTitle(REMOVE_FROM_LIST_HOVER_HELP);

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
						singleton = new CruiseListPage();
					SocatUploadDashboard.updateCurrentPage(singleton);
					singleton.updateCruises(cruises);
					History.newItem(PagesEnum.SHOW_DATASETS.name(), false);
				}
				else {
					SocatUploadDashboard.showMessage(errMsg + 
							" (unexpected invalid cruise list)");
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(errMsg, ex);
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
			if ( ! ownerColumnShown ) {
				// Add manager-specific buttons and column
				addToListButton.setVisible(true);
				removeFromListButton.setVisible(true);
				datasetsGrid.addColumn(ownerColumn, OWNER_COLUMN_NAME);
				datasetsGrid.setColumnWidth(ownerColumn, 
						SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
				minTableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
				datasetsGrid.setMinimumTableWidth(minTableWidth, Style.Unit.EM);
				ownerColumnShown = true;
			}
		}
		else {
			if ( ownerColumnShown ) {
				// Remove manager-specific buttons and column
				addToListButton.setVisible(false);
				removeFromListButton.setVisible(false);
				datasetsGrid.removeColumn(ownerColumn);
				minTableWidth -= SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
				datasetsGrid.setMinimumTableWidth(minTableWidth, Style.Unit.EM);
				ownerColumnShown = false;
			}
		}
		// Update the cruises shown by resetting the data in the data provider
		List<DashboardCruise> cruiseList = listProvider.getList();
		cruiseList.clear();
		if ( cruises != null ) {
			cruiseList.addAll(cruises.values());
		}
		datasetsGrid.setRowCount(cruiseList.size());
		// Make sure the table is sorted according to the last specification
		ColumnSortEvent.fire(datasetsGrid, datasetsGrid.getColumnSortList());
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
	 * 		if fails, safe HTML message to show, along with some explanation 
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
					CruiseListPage.this.updateCruises(cruises);
				}
				else {
					SocatUploadDashboard.showMessage(errMsg + 
							" (unexpected invalid data set list)");
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(errMsg, ex);
			}
		});
	}

	/**
	 * Assigns cruiseSet in this instance with the set of selected cruises. 
	 * Succeeds only if all the cruises fit the desired criterion.
	 *  
	 * @param unsubmitted
	 * 		if true, fails if a submitted cruise is selected;
	 * 		if false, fails if an unsubmitted cruise is selected;
	 * 		if null, no restriction and always succeeds.
	 * @return
	 * 		if successful
	 */
	private boolean getSelectedCruises(Boolean unsubmitted) {
		cruiseSet.clear();
		for ( DashboardCruise cruise : listProvider.getList() ) {
			if ( cruise.isSelected() ) {
				cruiseSet.add(cruise);
				if ( unsubmitted != null ){
					String status = cruise.getQcStatus();
					if ( status.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) || 
						 status.equals(DashboardUtils.QC_STATUS_UNACCEPTABLE) ||
						 status.equals(DashboardUtils.QC_STATUS_SUSPENDED) ||
						 status.equals(DashboardUtils.QC_STATUS_EXCLUDED) ) {
						if ( unsubmitted == false )
							return false;
					}
					else {
						if ( unsubmitted == true )
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Assigns cruiseSet in this instance with the set of selected cruises, 
	 * then assigns expocodeSet in this instance with the expocodes of these 
	 * cruises.  Succeeds only if all the cruises fit the desired criterion.
	 *  
	 * @param unsubmitted
	 * 		if true, fails if a submitted cruise is selected;
	 * 		if false, fails if an unsubmitted cruise is selected;
	 * 		if null, no restriction and always succeeds.
	 * @return
	 * 		if successful
	 */
	private boolean getSelectedCruiseExpocodes(Boolean unsubmitted) {
		boolean success = getSelectedCruises(unsubmitted);
		if ( ! success )
			return false;
		expocodeSet.clear();
		for ( DashboardCruise cruise : cruiseSet )
			expocodeSet.add(cruise.getExpocode());
		return true;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("uploadButton")
	void uploadCruiseOnClick(ClickEvent event) {
		CruiseUploadPage.showPage();
	}

	@UiHandler("viewDataButton")
	void dataCheckOnClick(ClickEvent event) {
		if ( ! getSelectedCruiseExpocodes(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_CRUISES_SELECTED_ERR_START + FOR_REVIEWING_ERR_END);
			return;
		}
		if ( expocodeSet.size() < 1 ) {
			SocatUploadDashboard.showMessage(
					NO_CRUISE_SELECTED_ERR_START + FOR_REVIEWING_ERR_END);
			return;
		}
		if ( expocodeSet.size() > 1 ) {
			SocatUploadDashboard.showMessage(
					MANY_CRUISES_SELECTED_ERR_START + FOR_REVIEWING_ERR_END);
			return;
		}
		String expocode = expocodeSet.iterator().next();
		DataColumnSpecsPage.showPage(expocode);
	}

	@UiHandler("omeMetadataButton")
	void omeOnClick(ClickEvent event) {
		if ( ! getSelectedCruises(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_CRUISES_SELECTED_ERR_START + FOR_OME_ERR_END);
			return;
		}
		if ( cruiseSet.size() < 1 ) {
			SocatUploadDashboard.showMessage(
					NO_CRUISE_SELECTED_ERR_START + FOR_OME_ERR_END);
			return;
		}
		if ( cruiseSet.size() > 1 ) {
			SocatUploadDashboard.showMessage(
					MANY_CRUISES_SELECTED_ERR_START + FOR_OME_ERR_END);
		}
		OmeManagerPage.showPage(cruiseSet.iterator().next());
	}

	@UiHandler("addlDocsButton")
	void addlDocsOnClick(ClickEvent event) {
		if ( ! getSelectedCruises(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_CRUISES_SELECTED_ERR_START + FOR_ADDL_DOCS_ERR_END);
			return;
		}
		if ( cruiseSet.size() < 1 ) {
			SocatUploadDashboard.showMessage(
					NO_CRUISE_SELECTED_ERR_START + FOR_ADDL_DOCS_ERR_END);
			return;
		}
		if ( cruiseSet.size() == 1 ) {
			// Single cruise selected; go to the additional documents manager page
			AddlDocsManagerPage.showPage(
					cruiseSet.iterator().next().getExpocode());
		}
		else {
			// Multiple cruises selected; go to the additional documents upload page
			AddlDocsUploadPage.showPage(cruiseSet);
		}
	}

	@UiHandler("qcSubmitButton")
	void qcSubmitOnClick(ClickEvent event) {
		// With null argument, getSelectedCruises always returns true
		getSelectedCruises(null);
		if ( cruiseSet.size() == 0 ) {
			SocatUploadDashboard.showMessage(
					NO_CRUISE_SELECTED_ERR_START + FOR_QC_SUBMIT_ERR_END);
			return;
		}
		checkCruisesForSOCAT();
	}

	@UiHandler("reviewButton")
	void reviewOnClick(ClickEvent event) {
		// TODO:
		SocatUploadDashboard.showMessage("Not yet implemented");
		return;
	}

	@UiHandler("deleteButton")
	void deleteCruiseOnClick(ClickEvent event) {
		if ( ! getSelectedCruiseExpocodes(true) ) {
			SocatUploadDashboard.showMessage(
					SUBMITTED_CRUISES_SELECTED_ERR_START + FOR_DELETE_ERR_END);
			return;
		}
		if ( expocodeSet.size() == 0 ) {
			SocatUploadDashboard.showMessage(
					NO_CRUISE_SELECTED_ERR_START + FOR_DELETE_ERR_END);
			return;
		}
		// Confirm cruises to be deleted
		String message = DELETE_CRUISE_HTML_PROLOGUE;
		for ( String expocode : expocodeSet )
			message += "<li>" + SafeHtmlUtils.htmlEscape(expocode) + "</li>";
		message += DELETE_CRUISE_HTML_EPILOGUE;
		if ( askDeletePopup == null ) {
			askDeletePopup = new DashboardAskPopup(DELETE_YES_TEXT, 
					DELETE_NO_TEXT, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean okay) {
					// Only proceed if okay
					if ( okay ) {
						updatePage(DashboardUtils.REQUEST_CRUISE_DELETE_ACTION, 
								expocodeSet, DELETE_CRUISE_FAIL_MSG);
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
				errMsg += " (Invalid Expocode length)";
			}
			else {
				for (int k = 0; k < expoLen; k++) {
					if ( ! DashboardUtils.VALID_EXPOCODE_CHARACTERS
										 .contains(expocode.substring(k, k+1)) ) {
						badExpo = true;
						errMsg += " (Invalid characters in the Expocode)";
						break;
					}
				}
			}
			if ( badExpo ) {
				SocatUploadDashboard.showMessage(errMsg);
			}
			else {
				expocodeSet.clear();
				expocodeSet.add(expocode);
				updatePage(DashboardUtils.REQUEST_CRUISE_ADD_ACTION,
										expocodeSet, errMsg);
			}
		}
	}

	@UiHandler("removeFromListButton")
	void removeFromListOnClick(ClickEvent event) {
		getSelectedCruiseExpocodes(null);
		if ( expocodeSet.size() == 0 ) {
			SocatUploadDashboard.showMessage(
					NO_CRUISE_SELECTED_ERR_START + FOR_REMOVE_ERR_END);
			return;
		}
		// Confirm cruises to be removed
		String message = REMOVE_CRUISE_HTML_PROLOGUE;
		for ( String expocode : expocodeSet )
			message += "<li>" + SafeHtmlUtils.htmlEscape(expocode) + "</li>";
		message += REMOVE_CRUISE_HTML_EPILOGUE;
		if ( askRemovePopup == null ) {
			askRemovePopup = new DashboardAskPopup(REMOVE_YES_TEXT, 
					REMOVE_NO_TEXT, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					// Only proceed if yes; ignore if no or null
					if ( result == true )
						updatePage(DashboardUtils.REQUEST_CRUISE_REMOVE_ACTION,
								expocodeSet, REMOVE_CRUISE_FAIL_MSG);
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
	 * Creates the cruise data table columns.  The table will still need 
	 * to be populated using {@link #updateCruises(DashboardCruiseList)}.
	 */
	private void buildCruiseListTable() {
		
		// Create the columns for this table
		Column<DashboardCruise,Boolean> selectedColumn = buildSelectedColumn();
		TextColumn<DashboardCruise> expocodeColumn = buildExpocodeColumn();
		TextColumn<DashboardCruise> dataCheckColumn = buildDataCheckColumn();
		TextColumn<DashboardCruise> omeMetadataColumn = buildOmeMetadataColumn();
		TextColumn<DashboardCruise> qcStatusColumn = buildQCStatusColumn();
		TextColumn<DashboardCruise> archiveStatusColumn = buildArchiveStatusColumn();
		TextColumn<DashboardCruise> timestampColumn = buildTimestampColumn();
		TextColumn<DashboardCruise> filenameColumn = buildFilenameColumn();
		Column<DashboardCruise,SafeHtml> addlDocsColumn = buildAddnDocsColumn();
		ownerColumn = buildOwnerColumn();

		// Add the columns, with headers, to the table
		datasetsGrid.addColumn(selectedColumn, "");
		datasetsGrid.addColumn(expocodeColumn, EXPOCODE_COLUMN_NAME);
		datasetsGrid.addColumn(timestampColumn, TIMESTAMP_COLUMN_NAME);
		datasetsGrid.addColumn(dataCheckColumn, DATA_CHECK_COLUMN_NAME);
		datasetsGrid.addColumn(omeMetadataColumn, OME_METADATA_COLUMN_NAME);
		datasetsGrid.addColumn(qcStatusColumn, SUBMITTED_COLUMN_NAME);
		datasetsGrid.addColumn(archiveStatusColumn, ARCHIVED_COLUMN_NAME);
		datasetsGrid.addColumn(filenameColumn, FILENAME_COLUMN_NAME);
		datasetsGrid.addColumn(addlDocsColumn, ADDL_DOCS_COLUMN_NAME);
		datasetsGrid.addColumn(ownerColumn, OWNER_COLUMN_NAME);
		ownerColumnShown = true;

		// Set the minimum widths of the columns
		minTableWidth = 0.0;
		datasetsGrid.setColumnWidth(selectedColumn, 
				SocatUploadDashboard.CHECKBOX_COLUMN_WIDTH, Style.Unit.EM);
		minTableWidth += SocatUploadDashboard.CHECKBOX_COLUMN_WIDTH;
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
		selectedColumn.setSortable(true);
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
		columnSortHandler.setComparator(selectedColumn,
				DashboardCruise.selectedComparator);
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

		// Add the sort handler to the table, and sort by expocode by default
		datasetsGrid.addColumnSortHandler(columnSortHandler);
		datasetsGrid.getColumnSortList().push(expocodeColumn);

		// Set the contents if there are no rows
		datasetsGrid.setEmptyTableWidget(new Label(EMPTY_TABLE_TEXT));

		// Following recommended to improve efficiency with IE
		datasetsGrid.setSkipRowHoverCheck(false);
		datasetsGrid.setSkipRowHoverFloatElementCheck(false);
		datasetsGrid.setSkipRowHoverStyleUpdate(false);
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
	 * Creates the timestamp column for the table
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
	 * Creates the data-check status column for the table
	 */
	private TextColumn<DashboardCruise> buildDataCheckColumn() {
		TextColumn<DashboardCruise> dataCheckColumn = 
				new TextColumn<DashboardCruise> () {
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
				String msg = getValue(cruise);
				if ( msg.equals(NO_DATA_CHECK_STATUS_STRING) ||
					 msg.equals(DashboardUtils.CHECK_STATUS_ACCEPTABLE) ) {
					// No problems, or not checked (so no messages)
					// render as plain text as usual
					sb.appendHtmlConstant("<div>");
					sb.appendEscaped(msg);
					sb.appendHtmlConstant("</div>");
				}
				else if ( msg.contains("warning") ) {
					// Only warnings - use warning background color
					sb.appendHtmlConstant("<div style=\"background-color:" +
							SocatUploadDashboard.WARNING_COLOR + ";\">");
					sb.appendEscaped(msg);
					sb.appendHtmlConstant("</div>");
				}
				else {
					// Errors or unacceptable - use error background color
					sb.appendHtmlConstant("<div style=\"background-color:" +
							SocatUploadDashboard.ERROR_COLOR + ";\">");
					sb.appendEscaped(msg);
					sb.appendHtmlConstant("</div>");
				}
			}
		};
		return dataCheckColumn;
	}

	/**
	 * Creates the OME metadata filename column for the table
	 */
	private TextColumn<DashboardCruise> buildOmeMetadataColumn() {
		TextColumn<DashboardCruise> omeMetadataColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				String omeTimestamp = cruise.getOmeTimestamp();
				if ( omeTimestamp.isEmpty() )
					omeTimestamp = NO_OME_METADATA_STATUS_STRING;
				return omeTimestamp;
			}
		};
		return omeMetadataColumn;
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

	/**
	 * Creates the additional metadata files column for the table
	 */
	private Column<DashboardCruise,SafeHtml> buildAddnDocsColumn() {
		Column<DashboardCruise,SafeHtml> addnDocsColumn = 
				new Column<DashboardCruise,SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(DashboardCruise cruise) {
				TreeSet<String> addlDocTitles = cruise.getAddlDocs();
				if ( addlDocTitles.size() == 0 )
					return SafeHtmlUtils.fromSafeConstant(NO_ADDL_DOCS_STATUS_STRING);
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
				return sb.toSafeHtml();
			}
		};
		return addnDocsColumn;
	}

	/**
	 * Creates the owner column for the table
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
	 * Checks the cruises given in cruiseSet in this instance for metadata 
	 * compatibility for adding to SOCAT.  At this time this only checks 
	 * that an OME metadata document is associated with each cruise.
	 * 
	 * Then checks the cruises given in cruiseSet in this instance for data 
	 * compatibility for adding to SOCAT.  If the data has not been checked 
	 * or is unacceptable, this method presents an error message and returns.  
	 * If the data has serious issues to cause an automatic F flag, asks the 
	 * user if the submit should be continued.  If the answer is yes, or if 
	 * there were no serious data issues, continues the submission to SOCAT 
	 * by calling {@link AddToSocatPage#showPage(java.util.HashSet)}.
	 */
	private void checkCruisesForSOCAT() {
		// Check if the cruises have metadata documents
		String errMsg = NO_METADATA_HTML_PROLOGUE;
		boolean cannotSubmit = false;
		for ( DashboardCruise cruise : cruiseSet ) {
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
		for ( DashboardCruise cruise : cruiseSet ) {
			String status = cruise.getDataCheckStatus();
			if ( DashboardUtils.CHECK_STATUS_NOT_CHECKED.equals(status) ||
				 DashboardUtils.CHECK_STATUS_UNACCEPTABLE.equals(status) ) {
				errMsg += "<li>" + 
						 SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + "</li>";
				cannotSubmit = true;
			}
			else if ( ! ( status.equals(DashboardUtils.CHECK_STATUS_ACCEPTABLE) ||
						  status.startsWith(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX) ) ) {
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
					public void onSuccess(Boolean result) {
						// Only proceed if yes; ignore if no or null
						if ( result == true )
							AddToSocatPage.showPage(cruiseSet);
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
		AddToSocatPage.showPage(cruiseSet);
	}

}
