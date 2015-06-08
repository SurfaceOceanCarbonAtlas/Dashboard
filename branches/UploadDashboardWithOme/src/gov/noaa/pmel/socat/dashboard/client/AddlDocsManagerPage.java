/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;
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
public class AddlDocsManagerPage extends CompositeWithUsername {

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

	private static final String UNEXPLAINED_FAIL_MSG = 
			"<h3>Upload failed.</h3>" + 
			"<p>Unexpectedly, no explanation of the failure was given</p>";
	private static final String EXPLAINED_FAIL_MSG_START = 
			"<h3>Upload failed.</h3>" +
			"<p><pre>\n";
	private static final String EXPLAINED_FAIL_MSG_END = 
			"</pre></p>";

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

	private static DashboardServicesInterfaceAsync service = 
			GWT.create(DashboardServicesInterface.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml; 
	@UiField DataGrid<DashboardMetadata> addlDocsGrid;
	@UiField FormPanel uploadForm;
	@UiField FileUpload docUpload;
	@UiField Hidden timestampToken;
	@UiField Hidden expocodesToken;
	@UiField Hidden omeToken;
	@UiField Button uploadButton;
	@UiField Button dismissButton;

	private ListDataProvider<DashboardMetadata> listProvider;
	private HashSet<DashboardCruise> cruiseSet;
	private TreeSet<String> expocodes;
	private DashboardAskPopup askOverwritePopup;

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

		setUsername(null);
		cruiseSet = new HashSet<DashboardCruise>();
		expocodes = new TreeSet<String>();
		askOverwritePopup = null;

		clearTokens();

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
	 * @param cruiseList
	 * 		cruises to use 
	 */
	static void showPage(DashboardCruiseList cruiseList) {
		if ( singleton == null )
			singleton = new AddlDocsManagerPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		singleton.updateAddlDocs(cruiseList);
		History.newItem(PagesEnum.MANAGE_DOCUMENTS.name(), false);
	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the given username.
	 */
	static void redisplayPage(String username) {
		if ( (username == null) || username.isEmpty() || 
			 (singleton == null) || ! singleton.getUsername().equals(username) ) {
			CruiseListPage.showPage();
		}
		else {
			SocatUploadDashboard.updateCurrentPage(singleton);
		}
	}

	/**
	 * Updates the this page with the given cruises and their 
	 * supplemental documents.
	 * 
	 * @param cruiseSet
	 * 		set of cruises to use 
	 */
	private void updateAddlDocs(DashboardCruiseList cruises) {
		// Update the username
		setUsername(cruises.getUsername());
		userInfoLabel.setText(WELCOME_INTRO + getUsername());

		// Update the cruises associated with this page
		cruiseSet.clear();
		cruiseSet.addAll(cruises.values());
		expocodes.clear();
		expocodes.addAll(cruises.keySet());

		// Update the HTML intro naming the cruises
		StringBuilder sb = new StringBuilder();
		sb.append(INTRO_HTML_PROLOGUE);
		for ( String expo : expocodes )
			sb.append("<li>" + SafeHtmlUtils.htmlEscape(expo) + "</li>");
		sb.append(INTRO_HTML_EPILOGUE);
		introHtml.setHTML(sb.toString());

		// Clear the hidden tokens just to be safe
		clearTokens();

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

	/**
	 * Clears all the Hidden tokens on the page.
	 */
	private void clearTokens() {
		timestampToken.setValue("");
		expocodesToken.setValue("");
		omeToken.setValue("");
	}

	/**
	 * Assigns all the Hidden tokens on the page.
	 */
	private void assignTokens() {
		String localTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z").format(new Date());
		timestampToken.setValue(localTimestamp);
		expocodesToken.setValue(DashboardUtils.encodeStringArrayList(new ArrayList<String>(expocodes)));
		omeToken.setValue("false");
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("dismissButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page.
		CruiseListPage.showPage();
	}

	@UiHandler("uploadButton") 
	void uploadButtonOnClick(ClickEvent event) {
		// Make sure a file was selected
		String uploadFilename = DashboardUtils.baseName(docUpload.getFilename());
		if ( uploadFilename.isEmpty() ) {
			SocatUploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}

		// Disallow any overwrite of an OME file
		if ( uploadFilename.equals(DashboardMetadata.OME_FILENAME) ) {
			SocatUploadDashboard.showMessage(NO_OME_OVERWRITE_ERROR_MSG);
			return;
		}

		// Check for any overwrites that will happen
		String message = OVERWRITE_WARNING_MSG_PROLOGUE;
		boolean willOverwrite = false;
		for ( DashboardCruise cruz : cruiseSet ) {
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

		// If an overwrite will occur, ask for confirmation
		if ( willOverwrite ) {
			message += OVERWRITE_WARNING_MSG_EPILOGUE;
			if ( askOverwritePopup == null ) {
				askOverwritePopup = new DashboardAskPopup(OVERWRITE_YES_TEXT, 
						OVERWRITE_NO_TEXT, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						// Submit only if yes
						if ( result == true ) {
							assignTokens();
							uploadForm.submit();
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

		assignTokens();
		uploadForm.submit();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		SocatUploadDashboard.showWaitCursor();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		clearTokens();
		// Process the returned message
		processResultMsg(event.getResults());
		// Contact the server to obtain the latest set 
		// of supplemental documents for the current cruises
		service.getUpdatedCruises(getUsername(), expocodes, 
				new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruiseList) {
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
	 * Process the message returned from the upload of a dataset.
	 * 
	 * @param resultMsg
	 * 		message returned from the upload of a dataset
	 */
	private void processResultMsg(String resultMsg) {
		if ( resultMsg == null ) {
			SocatUploadDashboard.showMessage(UNEXPLAINED_FAIL_MSG);
			return;
		}
		resultMsg = resultMsg.trim();
		if ( resultMsg.startsWith(DashboardUtils.FILE_CREATED_HEADER_TAG) ) {
			// Do not show any messages on success;
			// depend on the updated list of documents to show success
			;
		}
		else {
			// Unknown response, just display the entire message
			SocatUploadDashboard.showMessage(EXPLAINED_FAIL_MSG_START + 
					SafeHtmlUtils.htmlEscape(resultMsg) + EXPLAINED_FAIL_MSG_END);
		}
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
	 * @return the upload filename column for the table
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
	 * @return the upload timestamp column for the table
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
	 * @return the upload timestamp column for the table
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

	/**
	 * @return the delete column for the tables
	 */
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
						if ( result == true ) {
							continueDelete(deleteFilename, deleteExpocode);
						}
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

	/**
	 * Calls the server to delete an ancillary document from a cruise.
	 * 
	 * @param deleteFilename
	 * 		upload name of the document to delete
	 * @param deleteExpocode
	 * 		delete the document from the cruise with this expocode
	 */
	private void continueDelete(String deleteFilename, String deleteExpocode) {
		// Send the request to the server
		SocatUploadDashboard.showWaitCursor();
		service.deleteAddlDoc(getUsername(), deleteFilename, deleteExpocode, 
				expocodes, new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruiseList) {
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