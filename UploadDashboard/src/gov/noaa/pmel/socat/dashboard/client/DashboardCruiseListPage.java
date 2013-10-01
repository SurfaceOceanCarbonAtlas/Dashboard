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

	// Replacement strings for empty or null values
	protected static String noExpocodeString = "(unknown)";
	protected static String noOwnerString = "(unknown)";
	protected static String noUploadFilenameString = "(unknown)";
	protected static String noCheckStatusString = "(not checked)";
	protected static String noQCStatusString = "(not submitted)";
	protected static String noArchiveStatusString = "(not archived)";

	protected static String welcomeIntro = "Logged in as: ";
	protected static String logoutText = "Logout";
	protected static String agreeShareText = 
			"I give permission for my cruises to be shared for policy (QC) assessment.";
	protected static String agreeShareInfoHtml =
			"By checking this box I am giving permission for my uploaded cruise files " +
			"to be shared for purposes of policy (QC) assessment.  I understand that " +
			"data so-released will be used only for that narrow purpose and will not " +
			"be further distributed until the next official publication of SOCAT if " +
			"the cruise was deemed acceptable. ";
	protected static String agreeArchiveText = 
			"I give permission for my cruises to be automatically archived at CDIAC.  ";
	protected static String agreeArchiveInfoHtml = 
			"By checking this box I am giving permission for my uploaded cruise files " +
			"and metadata to be archived at CDIAC.  This will occur, if the cruise was " +
			"deemed acceptable, at the time of the next SOCAT public release, after " +
			"which the files will be made accessible to the public through the CDIAC " +
			"Web site. " +
			"<br /><br /> " +
			"<em>Note that declining permission here implies an obligation on my part to " +
			"ensure that these data will be made accessible via another data center.</em>";
	protected static String moreInfoText = "more explanation";

	protected static String uploadText = "Upload New Cruise Data";
	protected static String uploadHoverHelp = 
			"upload cruise data to create a new cruise or replace an existing cruise";

	protected static String deleteText = "Delete Cruise";
	protected static String deleteHoverHelp =
			"delete the selected cruises, including the cruise data, from SOCAT";

	protected static String dataCheckText = "Check Data";
	protected static String dataCheckHoverHelp =
			"assign data column types and programmatically check the data in the selected cruise";

	protected static String metaCheckText = "Check Metadata";
	protected static String metaCheckHoverHelp =
			"programmatically check the metadata in the selected cruise";

	protected static String reviewText = "Review with LAS";
	protected static String reviewHoverHelp =
			"examine the selected cruises in the cruise viewer " +
			"aside other SOCAT cruises";

	protected static String qcSubmitText = "Submit for QC";
	protected static String qcSubmitHoverHelp =
			"submit the selected cruises to SOCAT for quality assessment";

	protected static String archiveSubmitText = "Archive Now";
	protected static String archiveSubmitHoverHelp =
			"report the archival of the selected cruises, " +
			"or submit the selected cruises for immediate archival";

	protected static String addToListText = "Add Cruise";
	protected static String addToListHoverHelp = 
			"add an existing cruise to this list of cruises";

	protected static String removeFromListText = "Remove Cruise";
	protected static String removeFromListHoverHelp =
			"remove the selected cruises from this list of cruises; " +
			"this will NOT remove the cruise or cruise data from SOCAT";

	protected static String columnNameExpocode = "Expocode";
	protected static String columnNameOwner = "Owner";
	protected static String columnNameFilename = "Filename";
	protected static String columnNameDataCheck = "Data check";
	protected static String columnNameMetaCheck = "Meta check";
	protected static String columnNameSubmitted = "Submitted";
	protected static String columnNameArchived = "Archived";
	protected static String emptyTableText = "No uploaded cruises";

	protected static String noCruisesToDeleteMsg = 
			"No cruises are selected which can be deleted " +
			"(cruises must be suspended or not submitted).";
	protected static String deleteConfirmMsg = 
			": this cruise, including all cruise data, will be deleted from " +
			"SOCAT.  You will be able to do this only if the cruise belongs " +
			"to you or to someone in a group you manage.  Do you wish to " +
			"proceed?";
	protected static String deleteCruiseFailMsg = 
			"Unable to delete the selected cruise(s)";

	protected static String onlyOneCruiseAllowedMsg =
			"Exactly one cruise must be selected";

	protected static String cruiseDataCheckFailMsg = 
			"Unable to generate the cruise data column specifications page";
	
	protected static String expocodeToAddMsg = 
			"Enter the expocode of the cruise to wish to add to your cruise list";
	protected static String addCruiseFailMsg = 
			"Unable to add the specified cruise to your list of cruises";

	protected static String noCruisesToRemoveMsg = 
			"No cruises are selected for removal ";
	protected static String removeCruiseConfirmMsg = 
			": this cruise will be removed from your personal list of cruises; " +
			"the cruise data file will NOT be removed from SOCAT.  Do you wish " +
			"to proceed?";
	protected static String removeCruiseFailMsg = 
			"Unable to remove the selected cruise(s) from your list of cruises";

	interface DashboardCruiseListPageUiBinder extends
			UiBinder<Widget, DashboardCruiseListPage> {
	}

	private static DashboardCruiseListPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseListPageUiBinder.class);

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

	/**
	 * Creates an empty cruise list page.  
	 * Call {@link #updateCruises(DashboardCruiseList)}
	 * to update the cruises displayed on this page.
	 */
	DashboardCruiseListPage() {
		initWidget(uiBinder.createAndBindUi(this));
		buildCruiseListTable();

		logoutButton.setText(logoutText);

		agreeShareCheckBox.setText(agreeShareText);
		agreeShareCheckBox.setValue(true);
		agreeShareInfoButton.setText(moreInfoText);
		agreeSharePopup = null;

		agreeArchiveCheckBox.setText(agreeArchiveText);
		agreeArchiveCheckBox.setValue(true);
		agreeArchiveInfoButton.setText(moreInfoText);
		agreeArchivePopup = null;

		uploadButton.setText(uploadText);
		uploadButton.setTitle(uploadHoverHelp);

		deleteButton.setText(deleteText);
		deleteButton.setTitle(deleteHoverHelp);

		dataCheckButton.setText(dataCheckText);
		dataCheckButton.setTitle(dataCheckHoverHelp);

		metaCheckButton.setText(metaCheckText);
		metaCheckButton.setTitle(metaCheckHoverHelp);

		reviewButton.setText(reviewText);
		reviewButton.setTitle(reviewHoverHelp);

		qcSubmitButton.setText(qcSubmitText);
		qcSubmitButton.setTitle(qcSubmitHoverHelp);

		archiveSubmitButton.setText(archiveSubmitText);
		archiveSubmitButton.setTitle(archiveSubmitHoverHelp);

		addToListButton.setText(addToListText);
		addToListButton.setTitle(addToListHoverHelp);

		removeFromListButton.setText(removeFromListText);
		removeFromListButton.setTitle(removeFromListHoverHelp);

		uploadButton.setFocus(true);
	}

	/**
	 * Updates the cruise list page with the current username and 
	 * with the cruises given in the argument.
	 * 
	 * @param cruises
	 * 		cruises to display
	 */
	void updateCruises(DashboardCruiseList cruises) {
		// Update the username
		userInfoLabel.setText(welcomeIntro + DashboardPageFactory.getUsername());
		// Update the cruises shown by resetting the data in the data provider
		List<DashboardCruise> cruiseList = listProvider.getList();
		cruiseList.clear();
		if ( cruises != null ) {
			cruiseList.addAll(cruises.values());
		}
		uploadsGrid.setRowCount(cruiseList.size());
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
	HashSet<String> getSelectedCruiseExpocodes(boolean skipSubmitted, 
											   boolean skipArchived) {
		HashSet<String> expocodeSet = new HashSet<String>();
		for ( DashboardCruise cruise : listProvider.getList() ) {
			if ( cruise.isSelected() ) {
				if ( skipSubmitted ) {
					String status = cruise.getQCStatus();
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
		DashboardLogout logoutPage = 
				DashboardPageFactory.getPage(DashboardLogout.class);
		RootLayoutPanel.get().remove(DashboardCruiseListPage.this);
		RootLayoutPanel.get().add(logoutPage);
		logoutPage.doLogout();
	}

	@UiHandler("agreeShareInfoButton")
	void agreeShareInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeSharePopup == null ) {
			agreeSharePopup = new DashboardInfoPopup();
			agreeSharePopup.setInfoMessage(agreeShareInfoHtml);
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
			agreeArchivePopup.setInfoMessage(agreeArchiveInfoHtml);
		}
		// Show the popup over the info button
		agreeArchivePopup.showAtPosition(
				agreeArchiveInfoButton.getAbsoluteLeft(),
				agreeArchiveInfoButton.getAbsoluteTop());
	}

	@UiHandler("uploadButton")
	void uploadCruiseOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardCruiseListPage.this);
		DashboardCruiseUploadPage newCruisePage = 
				DashboardPageFactory.getPage(DashboardCruiseUploadPage.class);
		RootLayoutPanel.get().add(newCruisePage);
		newCruisePage.updatePageContents();
	}

	@UiHandler("deleteButton")
	void deleteCruiseOnClick(ClickEvent event) {
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(true, false);
		if ( expocodeSet.size() == 0 ) {
			Window.alert(noCruisesToDeleteMsg);
			return;
		}
		// Confirm each cruise 
		for ( String expocode : expocodeSet )
			if ( ! Window.confirm(expocode + deleteConfirmMsg) ) 
				return;
		// Remove the cruises
		updateCruiseListPage(DashboardUtils.REQUEST_CRUISE_DELETE_ACTION, 
						expocodeSet, deleteCruiseFailMsg);
	}

	@UiHandler("dataCheckButton")
	void dataCheckOnClick(ClickEvent event) {
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(false, false);
		if ( expocodeSet.size() != 1 ) {
			Window.alert(onlyOneCruiseAllowedMsg);
			return;
		}
		String expocode = expocodeSet.iterator().next();
		CruiseDataColumnSpecsPage.showCruiseDataColumnSpecsPage(expocode, 
				DashboardCruiseListPage.this, cruiseDataCheckFailMsg);
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
		String expocode = Window.prompt(expocodeToAddMsg, "");
		if ( expocode != null ) {
			expocode = expocode.trim().toUpperCase();
			// Quick local check if the expocode is obviously invalid
			boolean badExpo = false;
			String errMsg = addCruiseFailMsg;
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
				updateCruiseListPage(DashboardUtils.REQUEST_CRUISE_ADD_ACTION,
										expocodeSet, errMsg);
			}
		}
	}

	@UiHandler("removeFromListButton")
	void removeFromListOnClick(ClickEvent event) {
		HashSet<String> expocodeSet = getSelectedCruiseExpocodes(false, false);
		if ( expocodeSet.size() == 0 ) {
			Window.alert(noCruisesToRemoveMsg);
			return;
		}
		for ( String expocode : expocodeSet )
			if ( ! Window.confirm(expocode + removeCruiseConfirmMsg) ) 
				return;
		updateCruiseListPage(DashboardUtils.REQUEST_CRUISE_REMOVE_ACTION,
					expocodeSet, removeCruiseFailMsg);
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
		uploadsGrid.addColumn(expocodeColumn, columnNameExpocode);
		uploadsGrid.addColumn(ownerColumn, columnNameOwner);
		uploadsGrid.addColumn(dataCheckColumn, columnNameDataCheck);
		uploadsGrid.addColumn(metaCheckColumn, columnNameMetaCheck);
		uploadsGrid.addColumn(qcStatusColumn, columnNameSubmitted);
		uploadsGrid.addColumn(archiveStatusColumn, columnNameArchived);
		uploadsGrid.addColumn(filenameColumn, columnNameFilename);

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
		uploadsGrid.setEmptyTableWidget(new Label(emptyTableText));

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
					expocode = noExpocodeString;
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
					owner = noOwnerString;
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
					status = noCheckStatusString;
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
					status = noCheckStatusString;
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
				String status = cruise.getQCStatus();
				if ( status.isEmpty() )
					status = noQCStatusString;
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
					status = noArchiveStatusString;
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
					uploadFilename = noUploadFilenameString;
				return uploadFilename;
			}
		};
		return filenameColumn;
	}

	/**
	 * Display the cruise list page with the latest information from the server
	 * 
	 * @param currentPage
	 * 		currently displayed page to be removed when cruise list page
	 * 		is available
	 * @param errMsg
	 * 		if fails, message to show, along with some explanation, in a Window.alert
	 */
	static void showCruiseListPage(final Composite currentPage, final String errMsg) {
		DashboardCruiseListServiceAsync service = 
				GWT.create(DashboardCruiseListService.class);
		service.updateCruiseList(DashboardPageFactory.getUsername(), 
								 DashboardPageFactory.getPasshash(),
								 DashboardUtils.REQUEST_CRUISE_LIST_ACTION, 
								 new HashSet<String>(),
								 new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruises) {
				if ( DashboardPageFactory.getUsername().equals(cruises.getUsername()) ) {
					RootLayoutPanel.get().remove(currentPage);
					DashboardCruiseListPage cruiseListPage = 
							DashboardPageFactory.getPage(DashboardCruiseListPage.class);
					RootLayoutPanel.get().add(cruiseListPage);
					cruiseListPage.updateCruises(cruises);
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
	void updateCruiseListPage(String action, HashSet<String> expocodes, 
			final String errMsg) {
		DashboardCruiseListServiceAsync service = 
				GWT.create(DashboardCruiseListService.class);
		service.updateCruiseList(DashboardPageFactory.getUsername(), 
				DashboardPageFactory.getPasshash(), action, expocodes,
				new AsyncCallback<DashboardCruiseList>() {
			@Override
			public void onSuccess(DashboardCruiseList cruises) {
				if ( DashboardPageFactory.getUsername()
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

}
