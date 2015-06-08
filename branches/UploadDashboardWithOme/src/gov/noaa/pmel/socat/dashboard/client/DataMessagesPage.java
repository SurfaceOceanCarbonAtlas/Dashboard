/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * @author Karl Smith
 */
public class DataMessagesPage extends CompositeWithUsername {

	private static final String TITLE_TEXT = "Data Errors and Warnings";

	private static final String INTRO_HTML_PROLOGUE = 
			"Dataset: <ul><li>";
	private static final String INTRO_HTML_EPILOGUE = 
			"</li></ul>";

	private static final String DISMISS_BUTTON_TEXT = "Back";

	private static final String SEVERITY_COLUMN_NAME = "Type";
	private static final String COLUMN_NUMBER_COLUMN_NAME = "Col.";
	private static final String COLUMN_NAME_COLUMN_NAME = "Column Name";
	private static final String ROW_NUMBER_COLUMN_NAME = "Row";
	private static final String TIMESTAMP_COLUMN_NAME = "Time";
	private static final String LONGITUDE_COLUMN_NAME = "Lon.";
	private static final String LATITUDE_COLUMN_NAME = "Lat.";
	private static final String EXPLANATION_COLUMN_NAME = "Explanation";

	private static final String ERROR_SEVERITY_TEXT = "Error";
	private static final String WARNING_SEVERITY_TEXT = "Warning";
	private static final String UNKNOWN_SEVERITY_TEXT = "Unknown";

	private static final String EMPTY_TABLE_TEXT = "No problems detected!";
	
	interface DataMessagesPageUiBinder extends UiBinder<Widget, DataMessagesPage> {
	}

	private static DataMessagesPageUiBinder uiBinder = 
			GWT.create(DataMessagesPageUiBinder.class);

	private static DashboardServicesInterfaceAsync service = 
			GWT.create(DashboardServicesInterface.class);

	@UiField InlineLabel titleLabel;
	@UiField HTML introHtml;
	@UiField DataGrid<SCMessage> messagesGrid;
	@UiField Button dismissButton;
	@UiField SimplePager messagesPager;
	
	private ListDataProvider<SCMessage> listProvider;

	// The singleton instance of this page
	private static DataMessagesPage singleton;

	/**
	 * Creates an empty data messages page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page with the
	 * latest data messages for a cruise from the server. 
	 */
	DataMessagesPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		singleton.setUsername(null);
		titleLabel.setText(TITLE_TEXT);
		buildMessageListTable();
		dismissButton.setText(DISMISS_BUTTON_TEXT);

		// Assign the pager controlling which rows of the the messages grid are shown
		messagesPager.setDisplay(messagesGrid);
	}

	/**
	 * Display this page in the RootLayoutPanel showing the
	 * messages for cruise with the provided expocode.
	 * Adds this page to the page history.
	 */
	static void showPage(String username, String cruiseExpocode) {
		SocatUploadDashboard.showWaitCursor();
		service.getDataMessages(username, cruiseExpocode, new AsyncCallback<SCMessageList>() {
			@Override
			public void onSuccess(SCMessageList msgList) {
				if ( msgList == null ) {
					SocatUploadDashboard.showMessage("Unexpected list of data problems returned");
					SocatUploadDashboard.showAutoCursor();
					return;
				}
				if ( singleton == null )
					singleton = new DataMessagesPage();
				SocatUploadDashboard.updateCurrentPage(singleton);
				singleton.updateMessages(msgList);
				History.newItem(PagesEnum.SHOW_DATA_MESSAGES.name(), false);
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(
						"Unexpected failure obtaining the list of data problems", ex);
				SocatUploadDashboard.showAutoCursor();
			}
		});

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

	@UiHandler("dismissButton")
	void dismissOnClick(ClickEvent event) {
		DataColumnSpecsPage.redisplayPage(getUsername());
	}

	/**
	 * Update the cruise expocode and sanity checker messages with 
	 * that given in the provided SCMessageList.
	 * 
	 * @param msgList
	 * 		cruise expocode and set of messages to show 
	 */
	private void updateMessages(SCMessageList msgs) {
		// Assign the username and introduction message
		setUsername(msgs.getUsername());
		introHtml.setHTML(INTRO_HTML_PROLOGUE + 
				SafeHtmlUtils.htmlEscape(msgs.getExpocode()) + 
				INTRO_HTML_EPILOGUE);
		// Update the table by resetting the data in the data provider
		List<SCMessage> msgList = listProvider.getList();
		msgList.clear();
		msgList.addAll(msgs);
		messagesGrid.setRowCount(msgList.size(), true);
		// Make sure the table is sorted according to the last specification
		ColumnSortEvent.fire(messagesGrid, messagesGrid.getColumnSortList());
		// Set the number of data rows to display in the grid.
		// This will refresh the view.
		messagesGrid.setPageSize(DashboardUtils.MAX_ROWS_PER_GRID_PAGE);
	}

	/**
	 * Creates the messages table for this page.
	 */
	private void buildMessageListTable() {
		TextColumn<SCMessage> severityColumn = buildSeverityColumn();
		TextColumn<SCMessage> colNumColumn = buildColNumColumn();
		TextColumn<SCMessage> colNameColumn = buildColNameColumn();
		TextColumn<SCMessage> rowNumColumn = buildRowNumColumn();
		TextColumn<SCMessage> timestampColumn = buildTimestampColumn();
		TextColumn<SCMessage> longitudeColumn = buildLongitudeColumn();
		TextColumn<SCMessage> latitudeColumn = buildLatitudeColumn();
		TextColumn<SCMessage> explanationColumn = buildExplanationColumn();

		messagesGrid.addColumn(severityColumn, SEVERITY_COLUMN_NAME);
		messagesGrid.addColumn(colNumColumn, COLUMN_NUMBER_COLUMN_NAME);
		messagesGrid.addColumn(colNameColumn, COLUMN_NAME_COLUMN_NAME);
		messagesGrid.addColumn(rowNumColumn, ROW_NUMBER_COLUMN_NAME);
		messagesGrid.addColumn(timestampColumn, TIMESTAMP_COLUMN_NAME);
		messagesGrid.addColumn(longitudeColumn, LONGITUDE_COLUMN_NAME);
		messagesGrid.addColumn(latitudeColumn, LATITUDE_COLUMN_NAME);
		messagesGrid.addColumn(explanationColumn, EXPLANATION_COLUMN_NAME);

		// Set the minimum widths of the columns
		double tableWidth = 0.0;
		messagesGrid.setColumnWidth(severityColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(colNumColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(colNameColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(rowNumColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(timestampColumn, 
				SocatUploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NORMAL_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(longitudeColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(latitudeColumn, 
				SocatUploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += SocatUploadDashboard.NARROW_COLUMN_WIDTH;
		messagesGrid.setColumnWidth(explanationColumn, 
				2 * SocatUploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
		tableWidth += 2 * SocatUploadDashboard.FILENAME_COLUMN_WIDTH;

		// Set the minimum width of the full table
		messagesGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

		// Create the data provider for this table
		listProvider = new ListDataProvider<SCMessage>();
		listProvider.addDataDisplay(messagesGrid);

		// Make the columns sortable
		severityColumn.setSortable(true);
		colNumColumn.setSortable(true);
		colNameColumn.setSortable(true);
		rowNumColumn.setSortable(true);
		timestampColumn.setSortable(true);
		longitudeColumn.setSortable(true);
		latitudeColumn.setSortable(true);
		explanationColumn.setSortable(true);

		// Add a column sorting handler for these columns
		ListHandler<SCMessage> columnSortHandler = 
				new ListHandler<SCMessage>(listProvider.getList());
		columnSortHandler.setComparator(severityColumn,
				SCMessage.severityComparator);
		columnSortHandler.setComparator(colNumColumn,
				SCMessage.colNumComparator);
		columnSortHandler.setComparator(colNameColumn,
				SCMessage.colNameComparator);
		columnSortHandler.setComparator(rowNumColumn,
				SCMessage.rowNumComparator);
		columnSortHandler.setComparator(timestampColumn,
				SCMessage.timestampComparator);
		columnSortHandler.setComparator(longitudeColumn,
				SCMessage.longitudeComparator);
		columnSortHandler.setComparator(latitudeColumn,
				SCMessage.latitudeComparator);
		columnSortHandler.setComparator(explanationColumn,
				SCMessage.explanationComparator);

		// Add the sort handler to the table, setting the default sorting
		// first by severity, then column number, and finally row number
		messagesGrid.addColumnSortHandler(columnSortHandler);
		messagesGrid.getColumnSortList().push(rowNumColumn);
		messagesGrid.getColumnSortList().push(colNumColumn);
		messagesGrid.getColumnSortList().push(severityColumn);

		// Set the contents if there are no rows
		messagesGrid.setEmptyTableWidget(new Label(EMPTY_TABLE_TEXT));

		// Following recommended to improve efficiency with IE
		messagesGrid.setSkipRowHoverCheck(false);
		messagesGrid.setSkipRowHoverFloatElementCheck(false);
		messagesGrid.setSkipRowHoverStyleUpdate(false);
	}

	private static final NumberFormat INT_NUMBER_FORMAT = NumberFormat.getFormat(
			NumberFormat.getDecimalFormat().getPattern()).overrideFractionDigits(0);
	private static final NumberFormat FLT_NUMBER_FORMAT = NumberFormat.getFormat(
			NumberFormat.getDecimalFormat().getPattern()).overrideFractionDigits(4);

	private TextColumn<SCMessage> buildSeverityColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( msg == null )
					return UNKNOWN_SEVERITY_TEXT;
				SCMsgSeverity severity = msg.getSeverity();
				if ( severity == SCMsgSeverity.WARNING )
					return WARNING_SEVERITY_TEXT;
				if ( severity == SCMsgSeverity.ERROR )
					return ERROR_SEVERITY_TEXT;
				return UNKNOWN_SEVERITY_TEXT;
			}
		};
	}

	private TextColumn<SCMessage> buildColNumColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( (msg == null) || (msg.getColNumber() <= 0) )
					return " --- ";
				return INT_NUMBER_FORMAT.format(msg.getColNumber());
			}
		};
	}

	private TextColumn<SCMessage> buildColNameColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( (msg == null) || msg.getColName().isEmpty() )
					return " --- ";
				return msg.getColName();
			}
		};
	}

	private TextColumn<SCMessage> buildRowNumColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( (msg == null) || (msg.getRowNumber() <= 0) )
					return "";
				return INT_NUMBER_FORMAT.format(msg.getRowNumber());
			}
		};
	}

	private TextColumn<SCMessage> buildTimestampColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( msg == null )
					return "";
				return msg.getTimestamp();
			}
		};
	}

	private TextColumn<SCMessage> buildLongitudeColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( (msg == null) || Double.isNaN(msg.getLongitude()) )
					return "";
				return FLT_NUMBER_FORMAT.format(msg.getLongitude());
			}
		};
	}

	private TextColumn<SCMessage> buildLatitudeColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( (msg == null) || Double.isNaN(msg.getLatitude()) )
					return "";
				return FLT_NUMBER_FORMAT.format(msg.getLatitude());
			}
		};
	}

	private TextColumn<SCMessage> buildExplanationColumn() {
		return new TextColumn<SCMessage>() {
			@Override
			public String getValue(SCMessage msg) {
				if ( msg == null )
					return "";
				return msg.getDetailedComment();
			}
		};
	}

}
