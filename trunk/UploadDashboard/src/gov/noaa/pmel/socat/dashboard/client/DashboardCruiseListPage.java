/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;

import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
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

	final static String COLUMN_NAME_SELECTED = "Selected";
	final static String COLUMN_NAME_EXPOCODE = "Expocode";
	final static String COLUMN_NAME_FILENAME = "Filename";
	final static String COLUMN_NAME_DATA_CHECK = "Data check";
	final static String COLUMN_NAME_META_CHECK = "Meta check";
	final static String COLUMN_NAME_QC_STATUS = "Submitted";
	final static String COLUMN_NAME_ARCHIVED = "Archived";

	protected static String logoutText = "Log out";
	protected static String welcomeIntro = "Logged in as: ";
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
	protected static String uploadText = "Upload New Cruise";
	protected static String deleteText = "Delete Cruise";
	protected static String dataCheckText = "Check Data";
	protected static String metaCheckText = "Check Metadata";
	protected static String reviewText = "Review with LAS";
	protected static String qcSubmitText = "Submit for QC";
	protected static String archiveSubmitText = "Archive Now";
	protected static String emptyTableText = "No uploaded cruises";

	interface DashboardCruiseListPageUiBinder extends
			UiBinder<Widget, DashboardCruiseListPage> {
	}

	private static DashboardCruiseListPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseListPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField Button uploadButton;
	@UiField CheckBox agreeShareCheckBox;
	@UiField Button agreeShareInfoButton;
	@UiField CheckBox agreeArchiveCheckBox;
	@UiField Button agreeArchiveInfoButton;
	@UiField Button deleteButton;
	@UiField Button dataCheckButton;
	@UiField Button metaCheckButton;
	@UiField Button reviewButton;
	@UiField Button qcSubmitButton;
	@UiField Button archiveSubmitButton;
	@UiField DataGrid<DashboardCruise> uploadsGrid;

	private ListDataProvider<DashboardCruise> listProvider;
	private DashboardInfoPopup agreeSharePopup;
	private DashboardInfoPopup agreeArchivePopup;

	/**
	 * Creates an empty cruise list page.  
	 * Call {@link #updateCruises(DashboardCruiseListing)}
	 * to update the cruises displayed on this page.
	 */
	public DashboardCruiseListPage() {
		initWidget(uiBinder.createAndBindUi(this));
		buildCruiseListTable();

		logoutButton.setText(logoutText);

		uploadButton.setText(uploadText);
		agreeShareCheckBox.setText(agreeShareText);
		agreeShareCheckBox.setValue(true);
		agreeShareInfoButton.setText(moreInfoText);
		agreeSharePopup = null;

		agreeArchiveCheckBox.setText(agreeArchiveText);
		agreeArchiveCheckBox.setValue(true);
		agreeArchiveInfoButton.setText(moreInfoText);
		agreeArchivePopup = null;

		deleteButton.setText(deleteText);
		dataCheckButton.setText(dataCheckText);
		metaCheckButton.setText(metaCheckText);
		reviewButton.setText(reviewText);
		qcSubmitButton.setText(qcSubmitText);
		archiveSubmitButton.setText(archiveSubmitText);

		uploadButton.setFocus(true);
	}

	/**
	 * Updates the cruise list page with the username and cruises
	 * given in the argument.
	 * 
	 * @param cruises
	 * 		username and cruises to display
	 */
	public void updateCruises(DashboardCruiseListing cruises) {
		if ( (cruises != null) && (cruises.getUsername() != null) ) {
			userInfoLabel.setText(
					SafeHtmlUtils.htmlEscape(welcomeIntro + cruises.getUsername()));
		}
		else {
			userInfoLabel.setText(
					SafeHtmlUtils.htmlEscape(welcomeIntro));
		}
		// Update the cruises shown by resetting the data in the data provider
		List<DashboardCruise> cruiseList = listProvider.getList();
		cruiseList.clear();
		// Update the username
		if ( (cruises != null) && (cruises.getCruises() != null) ) {
			cruiseList.addAll(cruises.getCruises());
		}
		uploadsGrid.setRowCount(cruiseList.size());
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogout logoutPage = 
				DashboardPageFactory.getPage(DashboardLogout.class);
		RootLayoutPanel.get().remove(this);
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

	/**
	 * Creates the cruise data table columns.  The table will still need 
	 * to be populated using {@link #updateCruises(DashboardCruiseListing)}.
	 */
	void buildCruiseListTable() {
		
		// Create the columns for this table
		Column<DashboardCruise,Boolean> selectedColumn = buildSelectedColumn();
		TextColumn<DashboardCruise> expocodeColumn = buildExpocodeColumn();
		TextColumn<DashboardCruise> filenameColumn = buildFilenameColumn();
		TextColumn<DashboardCruise> dataCheckColumn = buildDataCheckColumn();
		TextColumn<DashboardCruise> metaCheckColumn = buildMetaCheckColumn();
		TextColumn<DashboardCruise> qcStatusColumn = buildQCStatusColumn();
		TextColumn<DashboardCruise> archiveStatusColumn = buildArchiveStatusColumn();

		// Add the columns, with headers, to the table
		uploadsGrid.addColumn(selectedColumn, "");
		uploadsGrid.addColumn(expocodeColumn, COLUMN_NAME_EXPOCODE);
		uploadsGrid.addColumn(filenameColumn, COLUMN_NAME_FILENAME);
		uploadsGrid.addColumn(dataCheckColumn, COLUMN_NAME_DATA_CHECK);
		uploadsGrid.addColumn(metaCheckColumn, COLUMN_NAME_META_CHECK);
		uploadsGrid.addColumn(qcStatusColumn, COLUMN_NAME_QC_STATUS);
		uploadsGrid.addColumn(archiveStatusColumn, COLUMN_NAME_ARCHIVED);

		// Set the widths of the columns
		uploadsGrid.setColumnWidth(selectedColumn, 2.0, Unit.EM);
		uploadsGrid.setColumnWidth(expocodeColumn, 5.0, Unit.EM);
		uploadsGrid.setColumnWidth(filenameColumn, 10.0, Unit.EM);
		uploadsGrid.setColumnWidth(dataCheckColumn, 5.0, Unit.EM);
		uploadsGrid.setColumnWidth(metaCheckColumn, 5.0, Unit.EM);
		uploadsGrid.setColumnWidth(qcStatusColumn, 5.0, Unit.EM);
		uploadsGrid.setColumnWidth(archiveStatusColumn, 5.0, Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardCruise>();
		listProvider.addDataDisplay(uploadsGrid);

		// Make some of the columns sortable
		selectedColumn.setSortable(true);
		expocodeColumn.setSortable(true);
		filenameColumn.setSortable(true);
		dataCheckColumn.setSortable(true);
		metaCheckColumn.setSortable(true);
		qcStatusColumn.setSortable(true);
		archiveStatusColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<DashboardCruise> columnSortHandler = 
				new ListHandler<DashboardCruise>(listProvider.getList());
		columnSortHandler.setComparator(selectedColumn,
				DashboardCruise.selectedComparator);
		columnSortHandler.setComparator(expocodeColumn, 
				DashboardCruise.expocodeComparator);
		columnSortHandler.setComparator(filenameColumn, 
				DashboardCruise.filenameComparator);
		columnSortHandler.setComparator(dataCheckColumn, 
				DashboardCruise.dataCheckComparator);
		columnSortHandler.setComparator(metaCheckColumn, 
				DashboardCruise.metaCheckComparator);
		columnSortHandler.setComparator(qcStatusColumn, 
				DashboardCruise.qcStatusComparator);
		columnSortHandler.setComparator(archiveStatusColumn, 
				DashboardCruise.archiveStatusComparator);

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
	Column<DashboardCruise,Boolean> buildSelectedColumn() {
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
				cruise.setSelected(value);
			}
		});
		selectedColumn.setDataStoreName(COLUMN_NAME_SELECTED);
		return selectedColumn;
	}

	/**
	 * Creates the expocode column for the table
	 */
	TextColumn<DashboardCruise> buildExpocodeColumn() {
		TextColumn<DashboardCruise> expocodeColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getExpocode();
			}
		};
		expocodeColumn.setDataStoreName(COLUMN_NAME_EXPOCODE);
		return expocodeColumn;
	}

	/**
	 * Creates the filename column for the table
	 */
	TextColumn<DashboardCruise> buildFilenameColumn() {
		TextColumn<DashboardCruise> filenameColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getUploadFilename();
			}
		};
		filenameColumn.setDataStoreName(COLUMN_NAME_FILENAME);
		return filenameColumn;
	}

	/**
	 * Creates the data-check date-string column for the table
	 */
	TextColumn<DashboardCruise> buildDataCheckColumn() {
		TextColumn<DashboardCruise> dataCheckColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				Date checkDate = cruise.getDataCheckDate();
				//TODO: create a date string in the desired format
				String checkStr = checkDate.toString();
				return checkStr;
			}
		};
		dataCheckColumn.setDataStoreName(COLUMN_NAME_DATA_CHECK);
		return dataCheckColumn;
	}

	/**
	 * Creates the metadata-check date-string column for the table
	 */
	TextColumn<DashboardCruise> buildMetaCheckColumn() {
		TextColumn<DashboardCruise> metaCheckColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				Date checkDate = cruise.getMetaCheckDate();
				//TODO: create a date string in the desired format
				String checkStr = checkDate.toString();
				return checkStr;
			}
		};
		metaCheckColumn.setDataStoreName(COLUMN_NAME_META_CHECK);
		return metaCheckColumn;
	}

	/**
	 * Creates the QC submission status column for the table
	 */
	TextColumn<DashboardCruise> buildQCStatusColumn() {
		TextColumn<DashboardCruise> qcStatusColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getQCStatus();
			}
		};
		qcStatusColumn.setDataStoreName(COLUMN_NAME_QC_STATUS);
		return qcStatusColumn;
	}

	/**
	 * Creates the archive submission status column for the table
	 */
	TextColumn<DashboardCruise> buildArchiveStatusColumn() {
		TextColumn<DashboardCruise> archiveStatusColumn = 
				new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getArchiveStatus();
			}
		};
		archiveStatusColumn.setDataStoreName(COLUMN_NAME_QC_STATUS);
		return archiveStatusColumn;
	}

}
