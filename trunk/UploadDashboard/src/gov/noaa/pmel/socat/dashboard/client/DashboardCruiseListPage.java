/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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

	final static String COLUMN_NAME_EXPOCODE = "Expocode";
	final static String COLUMN_NAME_FILENAME = "Filename";
	final static String COLUMN_NAME_DATA_CHECK = "Data check";
	final static String COLUMN_NAME_META_CHECK = "Meta check";
	final static String COLUMN_NAME_QC_STATUS = "QC";
	final static String COLUMN_NAME_ARCHIVED = "CDIAC";
	final static int MAX_UPLOADS_SHOWN = 10;

	protected static String logoutText = "Log out";
	protected static String welcomeIntro = "Logged in as: ";
	protected static String uploadText = "Upload Data";
	protected static String moreInfoText = "-- more explanation --";
	protected static String agreeShareText = 
			"I give permission for my cruises to be shared for policy assessment.";
	protected static String agreeShareInfoHtml =
			"By checking this box I am giving permission for my uploaded cruise files " +
			"to be shared for purposes of policy assessment.  I understand that data " +
			"so-released will be used only for that narrow purpose and will not be " +
			"further distributed until the next official publication of SOCAT.";
	protected static String agreeArchiveText = 
			"I give permission for my cruises to be archived at CDIAC";
	protected static String agreeArchiveInfoHtml = 
			"By checking this box I am giving permission for my uploaded cruise files " +
			"and metadata to be archived at CDIAC.  This will occur at the time of the " +
			"next SOCAT public release, after which the files will be made accessible " +
			"to the public through the CDIAC Web site. <br /> " +
			"<em>Note that declining permission here implies an obligation on my part to " +
			"ensure that these data will be made accessible via another data center.</em>";
	protected static String dismissText = "Dismiss";
	protected static String reviewText = "Review with LAS";
	protected static String qcSubmitText = "Submit for QC";
	protected static String archiveSubmitText = "Submit to CDIAC";
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
	@UiField Button reviewButton;
	@UiField Button qcSubmitButton;
	@UiField Button archiveSubmitButton;
	@UiField(provided=true) SimplePager pager;
	@UiField(provided=true) DataGrid<DashboardCruise> uploadsGrid;

	TextColumn<DashboardCruise> expocodeColumn;
	TextColumn<DashboardCruise> filenameColumn;
	TextColumn<DashboardCruise> dataCheckColumn;
	TextColumn<DashboardCruise> metaCheckColumn;
	TextColumn<DashboardCruise> qcStatusColumn;
	TextColumn<DashboardCruise> archiveStatusColumn;

	ListDataProvider<DashboardCruise> listProvider;

	PopupPanel agreeSharePopupPanel;
	PopupPanel agreeArchivePopupPanel;

	/**
	 * Creates an empty cruise list page.  
	 * Use {@link #updateCruises(DashboardCruiseListing)}
	 * to update the cruises displayed on this page.
	 */
	public DashboardCruiseListPage() {
		Label emptyTableLabel = new Label(emptyTableText);
		emptyTableLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		uploadsGrid = new DataGrid<DashboardCruise>();
		uploadsGrid.setEmptyTableWidget(emptyTableLabel);
		pager = new SimplePager();
		pager.setDisplay(uploadsGrid);
		
		initWidget(uiBinder.createAndBindUi(this));

		logoutButton.setText(logoutText);

		uploadButton.setText(uploadText);
		agreeShareCheckBox.setText(agreeShareText);
		agreeShareCheckBox.setValue(true);
		agreeShareInfoButton.setText(moreInfoText);
		agreeSharePopupPanel = null;

		agreeArchiveCheckBox.setText(agreeArchiveText);
		agreeArchiveCheckBox.setValue(true);
		agreeArchiveInfoButton.setText(moreInfoText);
		agreeArchivePopupPanel = null;

		reviewButton.setText(reviewText);
		qcSubmitButton.setText(qcSubmitText);
		archiveSubmitButton.setText(archiveSubmitText);

		// Following recommended to improve efficiency for IE
		uploadsGrid.setSkipRowHoverCheck(false);
		uploadsGrid.setSkipRowHoverFloatElementCheck(false);
		uploadsGrid.setSkipRowHoverStyleUpdate(false);

		// Build the table and the data provider for the table
		buildCruiseListTable();
	}

	/**
	 * Updates the cruise list page with the username and cruises
	 * given in the argument.
	 * 
	 * @param cruises
	 * 		username and cruises to display
	 */
	public void updateCruises(DashboardCruiseListing cruises) {
		// Update the username
		userInfoLabel.setText(
				SafeHtmlUtils.htmlEscape(welcomeIntro + cruises.getUsername()));
		// Update the cruises shown by resetting the data in the data provider
		List<DashboardCruise> cruiseList = listProvider.getList();
		cruiseList.clear();
		if ( cruises != null ) {
			cruiseList.addAll(cruises.getCruises());
		}
		uploadsGrid.setRowCount(cruiseList.size());
		uploadsGrid.setPageSize(MAX_UPLOADS_SHOWN);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogout logoutPage = 
				DashboardPageFactory.getPage(DashboardLogout.class);
		RootPanel.get().remove(this);
		RootPanel.get().add(logoutPage);
		logoutPage.doLogout();
	}

	@UiHandler("agreeShareInfoButton")
	void agreeShareInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeSharePopupPanel == null ) {
			// Create the message
			HTML message = new HTML(agreeShareInfoHtml);
			message.setWidth("20em");
			message.setWordWrap(true);
			message.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
			// Create the dismiss button
			Button dismissButton = new Button(dismissText);
			dismissButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					agreeSharePopupPanel.hide();
				}
			});
			// Vertical panel to contain the label and button
			VerticalPanel vpanel = new VerticalPanel();
			vpanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			vpanel.add(message);
			vpanel.add(dismissButton);
			// Create the popup panel with its one widget
			agreeSharePopupPanel = new PopupPanel();
			agreeSharePopupPanel.add(vpanel);
			agreeSharePopupPanel.setAutoHideEnabled(true);
		}
		// Show the popup over the info button
		agreeSharePopupPanel.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int infoButtonLeft = agreeShareInfoButton.getAbsoluteLeft();
				int infoButtonTop = agreeShareInfoButton.getAbsoluteTop();
				agreeSharePopupPanel.setPopupPosition(infoButtonLeft, infoButtonTop);
			}
		});
	}

	@UiHandler("agreeArchiveInfoButton")
	void agreeArchiveInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeArchivePopupPanel == null ) {
			// Create the message
			HTML message = new HTML(agreeArchiveInfoHtml);
			message.setWidth("20em");
			message.setWordWrap(true);
			message.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
			// Create the dismiss button
			Button dismissButton = new Button(dismissText);
			dismissButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					agreeArchivePopupPanel.hide();
				}
			});
			// Vertical panel to contain the label and button
			VerticalPanel vpanel = new VerticalPanel();
			vpanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			vpanel.add(message);
			vpanel.add(dismissButton);
			// Create the popup panel with its one widget
			agreeArchivePopupPanel = new PopupPanel();
			agreeArchivePopupPanel.add(vpanel);
			agreeArchivePopupPanel.setAutoHideEnabled(true);
		}
		// Show the popup over the info button
		agreeArchivePopupPanel.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int infoButtonLeft = agreeArchiveInfoButton.getAbsoluteLeft();
				int infoButtonTop = agreeArchiveInfoButton.getAbsoluteTop();
				agreeArchivePopupPanel.setPopupPosition(infoButtonLeft, infoButtonTop);
			}
		});
	}

	/**
	 * Create the cruise data table.  The table will need to be populated
	 * using {@link #updateCruises(DashboardCruiseListing)}
	 */
	void buildCruiseListTable() {
		// Create the columns for this table
		buildExpocodeColumn();
		buildFilenameColumn();
		buildDataCheckColumn();
		buildMetaCheckColumn();
		buildQCStatusColumn();
		buildArchiveStatusColumn();

		// Add the columns, with headers, to the table
		uploadsGrid.addColumn(expocodeColumn, COLUMN_NAME_EXPOCODE);
		uploadsGrid.addColumn(filenameColumn, COLUMN_NAME_FILENAME);
		uploadsGrid.addColumn(dataCheckColumn, COLUMN_NAME_DATA_CHECK);
		uploadsGrid.addColumn(metaCheckColumn, COLUMN_NAME_META_CHECK);
		uploadsGrid.addColumn(qcStatusColumn, COLUMN_NAME_QC_STATUS);
		uploadsGrid.addColumn(archiveStatusColumn, COLUMN_NAME_ARCHIVED);

		// Set the widths of the columns
		uploadsGrid.setColumnWidth(expocodeColumn, 6.0, Unit.EM);
		uploadsGrid.setColumnWidth(filenameColumn, 10.0, Unit.EM);
		uploadsGrid.setColumnWidth(dataCheckColumn, 5.0, Unit.EM);
		uploadsGrid.setColumnWidth(metaCheckColumn, 5.0, Unit.EM);
		uploadsGrid.setColumnWidth(qcStatusColumn, 3.0, Unit.EM);
		uploadsGrid.setColumnWidth(archiveStatusColumn, 3.0, Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<DashboardCruise>();
		listProvider.addDataDisplay(uploadsGrid);

		// Make some of the columns sortable
		expocodeColumn.setSortable(true);
		filenameColumn.setSortable(true);
		dataCheckColumn.setSortable(true);
		metaCheckColumn.setSortable(true);
		qcStatusColumn.setSortable(true);
		archiveStatusColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<DashboardCruise> columnSortHandler = 
				new ListHandler<DashboardCruise>(listProvider.getList());
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
	}

	/**
	 * Creates the expocode column for the table
	 */
	void buildExpocodeColumn() {
		expocodeColumn = new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getExpocode();
			}
		};
		expocodeColumn.setDataStoreName(COLUMN_NAME_EXPOCODE);
	}

	/**
	 * Creates the filename column for the table
	 */
	void buildFilenameColumn() {
		filenameColumn = new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getUploadFilename();
			}
		};
		filenameColumn.setDataStoreName(COLUMN_NAME_FILENAME);
	}

	/**
	 * Creates the data-check date-string column for the table
	 */
	void buildDataCheckColumn() {
		dataCheckColumn = new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				Date checkDate = cruise.getDataCheckDate();
				//TODO: create a date string in the desired format
				String checkStr = checkDate.toString();
				return checkStr;
			}
		};
		dataCheckColumn.setDataStoreName(COLUMN_NAME_DATA_CHECK);
	}

	/**
	 * Creates the metadata-check date-string column for the table
	 */
	void buildMetaCheckColumn() {
		metaCheckColumn = new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				Date checkDate = cruise.getMetaCheckDate();
				//TODO: create a date string in the desired format
				String checkStr = checkDate.toString();
				return checkStr;
			}
		};
		metaCheckColumn.setDataStoreName(COLUMN_NAME_META_CHECK);
	}

	/**
	 * Creates the QC submission status column for the table
	 */
	void buildQCStatusColumn() {
		qcStatusColumn = new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getQCStatus();
			}
		};
		qcStatusColumn.setDataStoreName(COLUMN_NAME_QC_STATUS);
	}

	/**
	 * Creates the archive submission status column for the table
	 */
	void buildArchiveStatusColumn() {
		archiveStatusColumn = new TextColumn<DashboardCruise> () {
			@Override
			public String getValue(DashboardCruise cruise) {
				return cruise.getArchiveStatus();
			}
		};
		archiveStatusColumn.setDataStoreName(COLUMN_NAME_QC_STATUS);
	}

}
