/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.AddToSocatService;
import gov.noaa.pmel.socat.dashboard.shared.AddToSocatServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Page for submitting cruises to be incorporated into the SOCAT collection.
 * 
 * @author Karl Smith
 */
public class AddToSocatPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String MORE_INFO_TEXT = "more explanation";

	private static final String ADD_TO_SOCAT_FIRST_INFO_HTML =
			"This page will submit the cruises listed in the table below to SOCAT " +
			"for examination and quality assessment.  You can withdraw cruises from " +
			"SOCAT after this submission if you need to make changes." +
			"<br /><br />" +
			"<b>Required</b>:";

	private static final String AGREE_SHARE_TEXT = 
			"I give permission for my cruises to be shared for policy (QC) assessment.";
	private static final String AGREE_SHARE_INFO_HTML =
			"By checking this box I am giving permission for my uploaded cruise files " +
			"to be shared for purposes of policy (QC) assessment.  I understand that " +
			"data so-released will be used only for that narrow purpose and will not " +
			"be further distributed until the next official publication of SOCAT if " +
			"the cruise was deemed acceptable. ";

	private static final String ADD_TO_SOCAT_SECOND_INFO_HTML =
			"Highly recommended:";

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

	private static final String METADATA_AUTOFAIL_HTML_PROLOGUE = 
			"The following cruises do not have any metadata documents: <ul>";
	private static final String DATA_AUTOFAIL_HTML_PROLOGUE = 
			"The following cruises have data with serious problems detected " +
			"by the automated data checking software: <ul>";
	private static final String AUTOFAIL_HTML_EPILOGUE = 
			"</ul> These cruises can be added to SOCAT, but will be automatically be " +
			"given the QC status '" + DashboardUtils.QC_STATUS_UNACCEPTABLE + 
			"'.  Do you want to continue? ";
	private static final String AUTOFAIL_YES_TEXT = "Yes";
	private static final String AUTOFAIL_NO_TEXT = "No";

	private static final String SUBMIT_FAILURE_MSG = 
			"Unexpected failure submitting cruises to SOCAT: ";

	private static final String SUBMIT_TEXT = "Add to SOCAT";
	private static final String CANCEL_TEXT = "Cancel";

	// Column header strings
	private static final String EXPOCODE_COLUMN_NAME = "Expocode";
	private static final String OWNER_COLUMN_NAME = "Owner";
	private static final String DATA_CHECK_COLUMN_NAME = "Data check";
	private static final String METADATA_COLUMN_NAME = "Metadata";
	private static final String FILENAME_COLUMN_NAME = "Filename";

	// Replacement strings for empty or null values
	private static final String NO_EXPOCODE_STRING = "(unknown)";
	private static final String NO_OWNER_STRING = "(unknown)";
	private static final String NO_DATA_CHECK_STATUS_STRING = "(not checked)";
	private static final String NO_METADATA_STATUS_STRING = "(no metadata)";
	private static final String NO_UPLOAD_FILENAME_STRING = "(unknown)";

	interface AddCruiseToSocatPageUiBinder 
			extends UiBinder<Widget, AddToSocatPage> {
	}

	private static AddCruiseToSocatPageUiBinder uiBinder = 
			GWT.create(AddCruiseToSocatPageUiBinder.class);

	private static AddToSocatServiceAsync service = 
			GWT.create(AddToSocatService.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML firstInfoHtml;
	@UiField CheckBox agreeShareCheckBox;
	@UiField Button agreeShareInfoButton;
	@UiField HTML secondInfoHtml;
	@UiField CheckBox agreeArchiveCheckBox;
	@UiField Button agreeArchiveInfoButton;
	@UiField Button submitButton;
	@UiField Button cancelButton;
	@UiField DataGrid<DashboardCruise> cruisesGrid;

	private String username;
	private DashboardInfoPopup agreeSharePopup;
	private DashboardInfoPopup agreeArchivePopup;
	private ListDataProvider<DashboardCruise> listProvider;
	private DashboardAskPopup askMetaAutofailPopup;
	private DashboardAskPopup askDataAutofailPopup;
	private HashSet<String> cruiseExpocodes;

	// The singleton instance of this page
	private static AddToSocatPage singleton;

	private AddToSocatPage() {
		initWidget(uiBinder.createAndBindUi(this));
		buildCruiseListTable();

		username = "";

		logoutButton.setText(LOGOUT_TEXT);

		firstInfoHtml.setHTML(ADD_TO_SOCAT_FIRST_INFO_HTML);

		agreeShareCheckBox.setText(AGREE_SHARE_TEXT);
		agreeShareInfoButton.setText(MORE_INFO_TEXT);
		agreeSharePopup = null;

		secondInfoHtml.setHTML(ADD_TO_SOCAT_SECOND_INFO_HTML);

		agreeArchiveCheckBox.setText(AGREE_ARCHIVE_TEXT);
		agreeArchiveInfoButton.setText(MORE_INFO_TEXT);
		agreeArchivePopup = null;

		askMetaAutofailPopup = null;
		askDataAutofailPopup = null;
		cruiseExpocodes = new HashSet<String>();

		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display this page in the RootLayoutPanel showing the
	 * given cruises.  Adds this page to the page history.
	 */
	static void showPage(HashSet<DashboardCruise> cruises) {
		if ( singleton == null )
			singleton = new AddToSocatPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		singleton.updateCruises(cruises);
		History.newItem(PagesEnum.ADD_TO_SOCAT.name(), false);
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
			SocatUploadDashboard.updateCurrentPage(singleton);
			if ( addToHistory )	
				History.newItem(PagesEnum.ADD_TO_SOCAT.name(), false);
		}
	}

	/**
	 * Updates the username on this page using the login page username,
	 * and updates the listing of cruises on this page with those given 
	 * in the argument.
	 * 
	 * @param cruises
	 * 		cruises to display
	 */
	private void updateCruises(HashSet<DashboardCruise> cruises) {
		// Update the username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);
		// Reselect the check boxes
		agreeShareCheckBox.setValue(true);
		agreeArchiveCheckBox.setValue(true);
		// Update the cruises shown by resetting the data in the data provider
		List<DashboardCruise> cruiseList = listProvider.getList();
		cruiseList.clear();
		if ( cruises != null ) {
			cruiseList.addAll(cruises);
		}
		cruisesGrid.setRowCount(cruiseList.size());
		// Enable the submit button since the agreeShareCheckBox is checked
		submitButton.setEnabled(true);
		// Reset the focus on the submit button
		submitButton.setFocus(true);
	}

	@UiHandler("agreeShareInfoButton")
	void agreeShareInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeSharePopup == null ) {
			agreeSharePopup = new DashboardInfoPopup();
			agreeSharePopup.setInfoMessage(AGREE_SHARE_INFO_HTML);
		}
		// Show the popup over the info button
		agreeSharePopup.showRelativeTo(agreeShareInfoButton);
	}

	@UiHandler("agreeArchiveInfoButton")
	void agreeArchiveInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeArchivePopup == null ) {
			agreeArchivePopup = new DashboardInfoPopup();
			agreeArchivePopup.setInfoMessage(AGREE_ARCHIVE_INFO_HTML);
		}
		// Show the popup over the info button
		agreeArchivePopup.showRelativeTo(agreeArchiveInfoButton);
	}

	@UiHandler("agreeShareCheckBox")
	void agreeShareCheckBoxOnValueChange(ValueChangeEvent<Boolean> event) {
		// submitButton is enabled if and only if agreeShareCheckBox is checked
		submitButton.setEnabled(agreeShareCheckBox.getValue());
	}

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Return to the cruise list page exactly as it was
		CruiseListPage.redisplayPage(true);
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		// Get the expocodes of cruises to be submitted,
		// checking if they have metadata documents
		cruiseExpocodes.clear();
		String message = METADATA_AUTOFAIL_HTML_PROLOGUE;
		boolean willAutofail = false;
		for ( DashboardCruise cruise : listProvider.getList() ) {
			String expocode = cruise.getExpocode();
			cruiseExpocodes.add(expocode);

			TreeSet<String> metaNames = cruise.getMetadataFilenames();
			if ( metaNames.size() < 1 ) {
				message += "<li>" + SafeHtmlUtils.htmlEscape(expocode) + "</li>";
				willAutofail = true;
			}
		}

		// If missing metadata, ask to continue
		if ( willAutofail ) {
			message += AUTOFAIL_HTML_EPILOGUE;
			if ( askMetaAutofailPopup == null ) {
				askMetaAutofailPopup = new DashboardAskPopup(AUTOFAIL_YES_TEXT,
						AUTOFAIL_NO_TEXT, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						// Only proceed if yes; ignore if no or null
						if ( result == true )
							continueSubmit();
					}
					@Override
					public void onFailure(Throwable ex) {
						// Never called
						;
					}
				});
			}
			askMetaAutofailPopup.askQuestion(message);
			return;
		}

		// All cruises have metadata; continue on
		continueSubmit();
	}

	/**
	 * Continues the submit process from the submitOnClick callback,
	 * checking the data check status.
	 */
	private void continueSubmit() {
		// Check that the cruise data is checked and reasonable
		String message = DATA_AUTOFAIL_HTML_PROLOGUE;
		boolean willAutofail = false;
		for ( DashboardCruise cruise : listProvider.getList() ) {
			String status = cruise.getDataCheckStatus();
			if ( ! ( DashboardUtils.CHECK_STATUS_ACCEPTABLE.equals(status) ||
					 DashboardUtils.CHECK_STATUS_QUESTIONABLE.equals(status) ) ) {
				message += "<li>" + 
					 SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + "</li>";
				willAutofail = true;
			}
		}

		// If unreasonable data, ask to continue
		if ( willAutofail ) {
			message += AUTOFAIL_HTML_EPILOGUE;
			if ( askDataAutofailPopup == null ) {
				askDataAutofailPopup = new DashboardAskPopup(AUTOFAIL_YES_TEXT,
						AUTOFAIL_NO_TEXT, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						// Only proceed if yes; ignore if no or null
						if ( result == true )
							furtherContinueSubmit();
					}
					@Override
					public void onFailure(Throwable ex) {
						// Never called
						;
					}
				});
			}
			askDataAutofailPopup.askQuestion(message);
			return;
		}

		// All cruises have reasonable data; continue on
		furtherContinueSubmit();
	}

	/**
	 * Further continues the submit process, actually performing the
	 * submit to the server.  Assumes cruiseExpocodes has been assigned 
	 * the expocodes of the cruises to submit to SOCAT.
	 */
	private void furtherContinueSubmit() {
		// Get the default status for cruises without DOI's
		String archiveStatus;
		if ( agreeArchiveCheckBox.getValue() )
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT;
		else
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE;
		service.addCruisesToSocat(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), cruiseExpocodes, 
				archiveStatus, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// Success - go back to the cruise list page
				CruiseListPage.showPage(false);
			}
			@Override
			public void onFailure(Throwable ex) {
				// Unexpected problems - show the error message
				SocatUploadDashboard.showMessage(SUBMIT_FAILURE_MSG + 
						SafeHtmlUtils.htmlEscape(ex.getMessage()));
			}
		});
	}

	/**
	 * Creates the cruise data table columns.  The table will still need 
	 * to be populated using {@link #updateCruises(DashboardCruiseList)}.
	 */
	private void buildCruiseListTable() {
		
		// Create the columns for this table
		TextColumn<DashboardCruise> expocodeColumn = buildExpocodeColumn();
		TextColumn<DashboardCruise> ownerColumn = buildOwnerColumn();
		TextColumn<DashboardCruise> dataCheckColumn = buildDataCheckColumn();
		TextColumn<DashboardCruise> metadataColumn = buildMetadataColumn();
		TextColumn<DashboardCruise> filenameColumn = buildFilenameColumn();

		// Add the columns, with headers, to the table
		cruisesGrid.addColumn(expocodeColumn, EXPOCODE_COLUMN_NAME);
		cruisesGrid.addColumn(ownerColumn, OWNER_COLUMN_NAME);
		cruisesGrid.addColumn(dataCheckColumn, DATA_CHECK_COLUMN_NAME);
		cruisesGrid.addColumn(metadataColumn, METADATA_COLUMN_NAME);
		cruisesGrid.addColumn(filenameColumn, FILENAME_COLUMN_NAME);

		// Set the minimum widths of the columns
		double tableWidth = 0.0;
		cruisesGrid.setColumnWidth(expocodeColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		cruisesGrid.setColumnWidth(ownerColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		cruisesGrid.setColumnWidth(dataCheckColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		cruisesGrid.setColumnWidth(metadataColumn, 
				SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.FILENAME_COLUMN_WIDTH;
		cruisesGrid.setColumnWidth(filenameColumn, 
				SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.FILENAME_COLUMN_WIDTH;

		// Set the minimum width of the full table
		cruisesGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardCruise>();
		listProvider.addDataDisplay(cruisesGrid);

		// Make the columns sortable
		expocodeColumn.setSortable(true);
		ownerColumn.setSortable(true);
		dataCheckColumn.setSortable(true);
		metadataColumn.setSortable(true);
		filenameColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<DashboardCruise> columnSortHandler = 
				new ListHandler<DashboardCruise>(listProvider.getList());
		columnSortHandler.setComparator(expocodeColumn, 
				DashboardCruise.expocodeComparator);
		columnSortHandler.setComparator(ownerColumn, 
				DashboardCruise.ownerComparator);
		columnSortHandler.setComparator(dataCheckColumn, 
				DashboardCruise.dataCheckComparator);
		columnSortHandler.setComparator(metadataColumn, 
				DashboardCruise.metadataFilenamesComparator);
		columnSortHandler.setComparator(filenameColumn, 
				DashboardCruise.filenameComparator);

		// Add the sort handler to the table, and sort by expocode by default
		cruisesGrid.addColumnSortHandler(columnSortHandler);
		cruisesGrid.getColumnSortList().push(expocodeColumn);

		// Set the contents if there are no rows
		cruisesGrid.setEmptyTableWidget(new Label("No cruises???"));

		// Following recommended to improve efficiency with IE
		cruisesGrid.setSkipRowHoverCheck(false);
		cruisesGrid.setSkipRowHoverFloatElementCheck(false);
		cruisesGrid.setSkipRowHoverStyleUpdate(false);
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
