/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Page for managing supplemental documents for a cruise.  
 *  
 * @author Karl Smith
 */
public class AddlDocsManagerPage extends Composite {

	private static final String TITLE_TEXT = "Supplemental Documents";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String INTRO_HTML_PROLOGUE = 
			"Supplemental documents associated with the datasets: <ul>";
	private static final String INTRO_HTML_EPILOGUE = 
			"</ul>";

	private static final String UPLOAD_TEXT = "Upload";
	private static final String UPLOAD_HOVER_HELP = 
			"upload a file that will be added as a new supplemental document, " +
			"or replace an existing supplemental document, for the datasets";

	private static final String DISMISS_TEXT = "Done";

	private static final String NO_FILE_ERROR_MSG = 
			"Please select a document to upload";

	private static final String NO_OME_OVERWRITE_ERROR_MSG =
			"Documents with the name " + DashboardMetadata.OME_FILENAME + 
			" cannot to uploaded as supplemental documents.  Please upload " +
			"the file under a different name.";

	private static final String ADDL_DOCS_LIST_FAIL_MSG = 
			"Unexpected problems obtaining the updated supplemental " +
			"documents for the datasets";

	private static final String OVERWRITE_WARNING_MSG_PROLOGUE = 
			"This will overwrite the supplemental documents: <ul>";
	private static final String OVERWRITE_WARNING_MSG_EPILOGUE =
			"</ul> Do you wish to proceed?";
	private static final String OVERWRITE_YES_TEXT = "Yes";
	private static final String OVERWRITE_NO_TEXT = "No";

	private static final String DELETE_BUTTON_TEXT = "Delete";

	private static final String DELETE_DOC_HTML_PROLOGUE =
			"This will deleted the supplemental document: <ul><li>";
	private static final String DELETE_DOC_HTML_EPILOGUE =
			"</li></ul> Do you wish to proceed?";
	private static final String DELETE_YES_TEXT = "Yes";
	private static final String DELETE_NO_TEXT = "No";

	private static final String DELETE_DOCS_FAIL_MSG =
			"Problems deleting supplemental document";

	// Replacement strings for empty or null values
	private static final String EMPTY_TABLE_TEXT = 
			"No supplemental documents";

	// Column header strings
	private static final String FILENAME_COLUMN_NAME = "Filename";
	private static final String UPLOAD_TIME_COLUMN_NAME = "Upload date";
	private static final String EXPOCODE_COLUMN_NAME = "Dataset";

	interface AddlDocsManagerPageUiBinder extends UiBinder<Widget, AddlDocsManagerPage> {
	}

	private static AddlDocsManagerPageUiBinder uiBinder = 
			GWT.create(AddlDocsManagerPageUiBinder.class);

	private static DashboardListServiceAsync service = 
			GWT.create(DashboardListService.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml; 
	@UiField DataGrid<DashboardMetadata> addlDocsGrid;
	@UiField FormPanel uploadForm;
	@UiField FileUpload docUpload;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden timestampToken;
	@UiField Hidden expocodesToken;
	@UiField Hidden omeToken;
	@UiField Button uploadButton;
	@UiField Button dismissButton;

	private String username;
	private ListDataProvider<DashboardMetadata> listProvider;
	private HashSet<DashboardCruise> cruises;
	private TreeSet<String> expocodes;
	private DashboardAskPopup askOverwritePopup;
	private boolean okayToOverwrite;

	// The singleton instance of this page
	private static AddlDocsManagerPage singleton;

	/**
	 * Creates an empty metadata list page.  Do not call this constructor; 
	 * instead use the one of the showPage static methods to show the 
	 * singleton instance of this page with the additional documents for 
	 * a cruise. 
	 */
	AddlDocsManagerPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		buildMetadataListTable();

		username = "";
		cruises = new HashSet<DashboardCruise>();
		expocodes = new TreeSet<String>();
		askOverwritePopup = null;
		okayToOverwrite = false;

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "MetadataUploadService");

		uploadButton.setText(UPLOAD_TEXT);
		uploadButton.setTitle(UPLOAD_HOVER_HELP);

		dismissButton.setText(DISMISS_TEXT);
	}

	/**
	 * Display this page in the RootLayoutPanel with the list of supplemental 
	 * documents in the given cruises.  Note that any uploaded documents 
	 * are added to all the cruises by replicating the documents.  
	 * Adds this page to the page history list.
	 * 
	 * @param cruiseSet
	 * 		set of cruises to use 
	 */
	static void showPage(HashSet<DashboardCruise> cruiseSet) {
		if ( singleton == null )
			singleton = new AddlDocsManagerPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		singleton.updateAddlDocs(cruiseSet);
		History.newItem(PagesEnum.MANAGE_DOCUMENTS.name(), false);
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
				History.newItem(PagesEnum.MANAGE_DOCUMENTS.name(), false);
		}
	}

	/**
	 * Updates the this page with the given cruises and their 
	 * supplemental documents.
	 * 
	 * @param cruiseSet
	 * 		set of cruises to use 
	 */
	private void updateAddlDocs(HashSet<DashboardCruise> cruiseSet) {
		// Update the username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);

		// Update the cruises associated with this page
		cruises.clear();
		cruises.addAll(cruiseSet);
		expocodes.clear();
		for ( DashboardCruise cruz : cruiseSet )
			expocodes.add(cruz.getExpocode());

		// Update the HTML intro naming the cruises
		StringBuilder sb = new StringBuilder();
		sb.append(INTRO_HTML_PROLOGUE);
		for ( String expo : expocodes )
			sb.append("<li>" + SafeHtmlUtils.htmlEscape(expo) + "</li>");
		sb.append(INTRO_HTML_EPILOGUE);
		introHtml.setHTML(sb.toString());

		// Clear the hidden tokens just to be safe
		usernameToken.setValue("");
		passhashToken.setValue("");
		timestampToken.setValue("");
		expocodesToken.setValue("");
		omeToken.setValue("");

		// Set to ask about any overwrites
		okayToOverwrite = false;

		// Update the metadata shown by resetting the data in the data provider
		List<DashboardMetadata> addlDocsList = listProvider.getList();
		addlDocsList.clear();
		for ( DashboardCruise cruz : cruiseSet ) {
			for ( String docTitle : cruz.getAddlDocs() ) {
				String[] nameDate = DashboardMetadata.splitAddlDocsTitle(docTitle);
				DashboardMetadata mdata = new DashboardMetadata();
				mdata.setExpocode(cruz.getExpocode());
				mdata.setFilename(nameDate[0]);
				mdata.setUploadTimestamp(nameDate[1]);
				addlDocsList.add(mdata);
			}
		}
		addlDocsGrid.setRowCount(addlDocsList.size(), true);
		// Make sure the table is sorted according to the last specification
		ColumnSortEvent.fire(addlDocsGrid, addlDocsGrid.getColumnSortList());
		// No pager (not needed); just set the page size and refresh the view
		addlDocsGrid.setPageSize(DashboardUtils.MAX_ROWS_PER_GRID_PAGE);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("dismissButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page.
		CruiseListPage.showPage(false);
	}

	@UiHandler("uploadButton") 
	void uploadButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		String localTimestamp = 
				DateTimeFormat.getFormat("yyyy-MM-dd HH:mm")
							  .format(new Date());
		timestampToken.setValue(localTimestamp);
		expocodesToken.setValue(
				DashboardUtils.encodeStringArrayList(
						new ArrayList<String>(expocodes)));
		omeToken.setValue("false");
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String uploadFilename = DashboardUtils.baseName(docUpload.getFilename());
		if ( uploadFilename.isEmpty() ) {
			event.cancel();
			usernameToken.setValue("");
			passhashToken.setValue("");
			timestampToken.setValue("");
			expocodesToken.setValue("");
			omeToken.setValue("");
			okayToOverwrite = false;
			SocatUploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}

		// Disallow any overwrite of an OME file
		if ( uploadFilename.equals(DashboardMetadata.OME_FILENAME) ) {
			event.cancel();
			usernameToken.setValue("");
			passhashToken.setValue("");
			timestampToken.setValue("");
			expocodesToken.setValue("");
			omeToken.setValue("");
			okayToOverwrite = false;
			SocatUploadDashboard.showMessage(NO_OME_OVERWRITE_ERROR_MSG);
			return;
		}

		// If this is a resubmit with overwriting, let the submit go through
		// (event is not cancelled)
		if ( okayToOverwrite ) {
			okayToOverwrite = false;
			return;
		}

		// Check for any overwrites that will happen
		String message = OVERWRITE_WARNING_MSG_PROLOGUE;
		boolean willOverwrite = false;
		for ( DashboardCruise cruz : cruises ) {
			for ( String addlDocTitle : cruz.getAddlDocs() ) {
				String[] nameTime = DashboardMetadata.splitAddlDocsTitle(addlDocTitle);
				if ( uploadFilename.equals(nameTime[0]) ) {
					message += "<li>" + SafeHtmlUtils.htmlEscape(nameTime[0]) + 
							"<br />&nbsp;&nbsp;(uploaded " + SafeHtmlUtils.htmlEscape(nameTime[1]) + 
							")<br />&nbsp;&nbsp;for dataset " + 
							SafeHtmlUtils.htmlEscape(cruz.getExpocode()) + "</li>";
					willOverwrite = true;
				}
			}
		}

		// If an overwrite will occur, cancel this submit and ask for confirmation
		if ( willOverwrite ) {
			event.cancel();
			message += OVERWRITE_WARNING_MSG_EPILOGUE;
			if ( askOverwritePopup == null ) {
				askOverwritePopup = new DashboardAskPopup(OVERWRITE_YES_TEXT, 
						OVERWRITE_NO_TEXT, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						// Resubmit only if yes; clear tokens if no or null
						if ( result == true ) {
							okayToOverwrite = true;
							uploadForm.submit();
						}
						else {
							usernameToken.setValue("");
							passhashToken.setValue("");
							timestampToken.setValue("");
							expocodesToken.setValue("");
							omeToken.setValue("");
							okayToOverwrite = false;
						}
					}
					@Override
					public void onFailure(Throwable ex) {
						// Never called
						;
					}
				});
			}
			askOverwritePopup.askQuestion(message);
			return;
		}

		// Nothing overwritten, let the submit continue
		// (event not cancelled)
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		usernameToken.setValue("");
		passhashToken.setValue("");
		timestampToken.setValue("");
		expocodesToken.setValue("");
		omeToken.setValue("");
		okayToOverwrite = false;

		// Check the result returned
		String resultMsg = event.getResults();
		if ( resultMsg == null ) {
			SocatUploadDashboard.showMessage(
					"Unexpected null result from upload of an supplemental document");
			return;
		}

		String[] tagMsg = resultMsg.split("\n", 2);
		if ( tagMsg.length < 2 ) {
			// probably an error response; just display the entire message
			SocatUploadDashboard.showMessage(SafeHtmlUtils.htmlEscape(resultMsg));
		}
		else if ( DashboardUtils.FILE_CREATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// Do not show any messages on success;
			// depend on the updated list of documents to show success
			;
		}
		else {
			// Unknown response with a newline, just display the entire message
			SocatUploadDashboard.showMessage(SafeHtmlUtils.htmlEscape(resultMsg));
		}
		// Contact the server to obtain the latest set 
		// of supplemental documents for the current cruises
		// Send the request to the server
		SocatUploadDashboard.showWaitCursor();
		service.getUpdatedCruises(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), expocodes, 
				new AsyncCallback<HashSet<DashboardCruise>>() {
			@Override
			public void onSuccess(HashSet<DashboardCruise> cruiseList) {
				// Update the list shown in this page
				updateAddlDocs(cruiseList);
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(ADDL_DOCS_LIST_FAIL_MSG, ex);
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

	/**
	 * Creates the table of selectable metadata documents
	 */
	private void buildMetadataListTable() {
		// Create the columns for this table
		Column<DashboardMetadata,String> deleteColumn = buildDeleteColumn();
		TextColumn<DashboardMetadata> filenameColumn = buildFilenameColumn();
		TextColumn<DashboardMetadata> uploadTimeColumn = buildUploadTimeColumn();
		TextColumn<DashboardMetadata> expocodeColumn = buildExpocodeColumn();
		
		// Add the columns, with headers, to the table
		addlDocsGrid.addColumn(deleteColumn, "");
		addlDocsGrid.addColumn(filenameColumn, FILENAME_COLUMN_NAME);
		addlDocsGrid.addColumn(uploadTimeColumn, UPLOAD_TIME_COLUMN_NAME);
		addlDocsGrid.addColumn(expocodeColumn, EXPOCODE_COLUMN_NAME);

		// Set the minimum widths of the columns
		double tableWidth = 0.0;
		addlDocsGrid.setColumnWidth(deleteColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		addlDocsGrid.setColumnWidth(filenameColumn, 
				SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.FILENAME_COLUMN_WIDTH;
		addlDocsGrid.setColumnWidth(uploadTimeColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		addlDocsGrid.setColumnWidth(expocodeColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;

		// Set the minimum width of the full table
		addlDocsGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardMetadata>();
		listProvider.addDataDisplay(addlDocsGrid);

		// Make the columns sortable
		deleteColumn.setSortable(false);
		filenameColumn.setSortable(true);
		uploadTimeColumn.setSortable(true);
		expocodeColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<DashboardMetadata> columnSortHandler = 
				new ListHandler<DashboardMetadata>(listProvider.getList());
		columnSortHandler.setComparator(filenameColumn, 
				DashboardMetadata.filenameComparator);
		columnSortHandler.setComparator(uploadTimeColumn, 
				DashboardMetadata.uploadTimestampComparator);
		columnSortHandler.setComparator(expocodeColumn, 
				DashboardMetadata.expocodeComparator);

		// Add the sort handler to the table, and sort by filename, then expocode by default
		addlDocsGrid.addColumnSortHandler(columnSortHandler);
		addlDocsGrid.getColumnSortList().push(expocodeColumn);
		addlDocsGrid.getColumnSortList().push(filenameColumn);

		// Set the contents if there are no rows
		addlDocsGrid.setEmptyTableWidget(new Label(EMPTY_TABLE_TEXT));

		// Following recommended to improve efficiency with IE
		addlDocsGrid.setSkipRowHoverCheck(false);
		addlDocsGrid.setSkipRowHoverFloatElementCheck(false);
		addlDocsGrid.setSkipRowHoverStyleUpdate(false);
	}

	/**
	 * Creates the upload filename column for the table
	 */
	private TextColumn<DashboardMetadata> buildFilenameColumn() {
		TextColumn<DashboardMetadata> filenameColumn = 
						new TextColumn<DashboardMetadata> () {
			@Override
			public String getValue(DashboardMetadata mdata) {
				return mdata.getFilename();
			}
		};
		return filenameColumn;
	}

	/**
	 * Creates the upload timestamp column for the table
	 */
	private TextColumn<DashboardMetadata> buildUploadTimeColumn() {
		TextColumn<DashboardMetadata> uploadTimeColumn = 
						new TextColumn<DashboardMetadata> () {
			@Override
			public String getValue(DashboardMetadata mdata) {
				return mdata.getUploadTimestamp();
			}
		};
		return uploadTimeColumn;
	}

	/**
	 * Creates the upload timestamp column for the table
	 */
	private TextColumn<DashboardMetadata> buildExpocodeColumn() {
		TextColumn<DashboardMetadata> expocodeColumn = 
						new TextColumn<DashboardMetadata> () {
			@Override
			public String getValue(DashboardMetadata mdata) {
				return mdata.getExpocode();
			}
		};
		return expocodeColumn;
	}

	private Column<DashboardMetadata,String> buildDeleteColumn() {
		Column<DashboardMetadata,String> deleteColumn =
				new Column<DashboardMetadata,String>(new ButtonCell()) {
			@Override
			public String getValue(DashboardMetadata object) {
				return DELETE_BUTTON_TEXT;
			}
		};
		deleteColumn.setFieldUpdater(new FieldUpdater<DashboardMetadata,String>() {
			@Override
			public void update(int index, DashboardMetadata mdata, String value) {
				// Show the document name and have the user confirm the delete 
				final String deleteFilename = mdata.getFilename();
				final String deleteExpocode = mdata.getExpocode();
				String message = DELETE_DOC_HTML_PROLOGUE + 
						SafeHtmlUtils.htmlEscape(deleteFilename) + 
						"<br />&nbsp;&nbsp;(uploaded " + 
						SafeHtmlUtils.htmlEscape(mdata.getUploadTimestamp()) + 
						")<br />&nbsp;&nbsp;for dataset " + 
						SafeHtmlUtils.htmlEscape(deleteExpocode) + 
						DELETE_DOC_HTML_EPILOGUE;
				new DashboardAskPopup(DELETE_YES_TEXT, DELETE_NO_TEXT, 
						new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						// Only continue if yes returned; ignore if no or null
						if ( result == true )
							continueDelete(deleteFilename, deleteExpocode);
					}
					@Override
					public void onFailure(Throwable caught) {
						// Never called
						;
					}
				}).askQuestion(message);
			}
		});
		return deleteColumn;
	}

	private void continueDelete(String deleteFilename, String deleteExpocode) {
		// Send the request to the server
		SocatUploadDashboard.showWaitCursor();
		service.deleteAddlDoc(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(),
				deleteFilename, deleteExpocode, expocodes, 
				new AsyncCallback<HashSet<DashboardCruise>>() {
			@Override
			public void onSuccess(HashSet<DashboardCruise> cruiseList) {
				// Update the list shown in this page
				updateAddlDocs(cruiseList);
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(DELETE_DOCS_FAIL_MSG, ex);
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

}