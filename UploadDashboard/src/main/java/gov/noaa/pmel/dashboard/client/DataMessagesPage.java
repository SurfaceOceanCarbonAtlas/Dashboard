/**
 *
 */
package gov.noaa.pmel.dashboard.client;

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
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.ADCMessageList;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

import java.util.List;

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

    interface DataMessagesPageUiBinder extends UiBinder<Widget,DataMessagesPage> {
    }

    private static DataMessagesPageUiBinder uiBinder =
            GWT.create(DataMessagesPageUiBinder.class);

    private static DashboardServicesInterfaceAsync service =
            GWT.create(DashboardServicesInterface.class);

    @UiField
    InlineLabel titleLabel;
    @UiField
    HTML introHtml;
    @UiField
    DataGrid<ADCMessage> messagesGrid;
    @UiField
    Button dismissButton;
    @UiField
    SimplePager messagesPager;

    private ListDataProvider<ADCMessage> listProvider;

    // The singleton instance of this page
    private static DataMessagesPage singleton;

    /**
     * Creates an empty data messages page.  Do not call this constructor; instead use the showPage static method to
     * show the singleton instance of this page with the latest data messages for a cruise from the server.
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
     * Display this page in the RootLayoutPanel showing the messages for the specified dataset.  Adds this page to the
     * page history.
     *
     * @param username
     *         user requesting the page
     * @param datasetId
     *         ID of the dataset to use
     */
    static void showPage(String username, String datasetId) {
        UploadDashboard.showWaitCursor();
        service.getDataMessages(username, datasetId, new AsyncCallback<ADCMessageList>() {
            @Override
            public void onSuccess(ADCMessageList msgList) {
                if ( msgList == null ) {
                    UploadDashboard.showMessage("Unexpected list of data problems returned");
                    UploadDashboard.showAutoCursor();
                    return;
                }
                if ( singleton == null )
                    singleton = new DataMessagesPage();
                UploadDashboard.updateCurrentPage(singleton);
                singleton.updateMessages(msgList);
                History.newItem(PagesEnum.SHOW_DATA_MESSAGES.name(), false);
                UploadDashboard.showAutoCursor();
            }

            @Override
            public void onFailure(Throwable ex) {
                UploadDashboard.showFailureMessage(
                        "Unexpected failure obtaining the list of data problems", ex);
                UploadDashboard.showAutoCursor();
            }
        });

    }

    /**
     * Redisplays the last version of this page if the username associated with this page matches the given username.
     */
    static void redisplayPage(String username) {
        if ( (username == null) || username.isEmpty() ||
                (singleton == null) || !singleton.getUsername().equals(username) ) {
            DatasetListPage.showPage();
        }
        else {
            UploadDashboard.updateCurrentPage(singleton);
        }
    }

    @UiHandler("dismissButton")
    void dismissOnClick(ClickEvent event) {
        DataColumnSpecsPage.redisplayPage(getUsername());
    }

    /**
     * Update the automated data checker messages with that given in the provided ADCMessageList.
     *
     * @param msgList
     *         cruise dataset and set of messages to show
     */
    private void updateMessages(ADCMessageList msgs) {
        // Assign the username and introduction message
        setUsername(msgs.getUsername());
        introHtml.setHTML(INTRO_HTML_PROLOGUE +
                SafeHtmlUtils.htmlEscape(msgs.getDatasetId()) +
                INTRO_HTML_EPILOGUE);
        // Update the table by resetting the data in the data provider
        List<ADCMessage> msgList = listProvider.getList();
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
        TextColumn<ADCMessage> severityColumn = buildSeverityColumn();
        TextColumn<ADCMessage> colNumColumn = buildColNumColumn();
        TextColumn<ADCMessage> colNameColumn = buildColNameColumn();
        TextColumn<ADCMessage> rowNumColumn = buildRowNumColumn();
        TextColumn<ADCMessage> timestampColumn = buildTimestampColumn();
        TextColumn<ADCMessage> longitudeColumn = buildLongitudeColumn();
        TextColumn<ADCMessage> latitudeColumn = buildLatitudeColumn();
        TextColumn<ADCMessage> explanationColumn = buildExplanationColumn();

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
                UploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += UploadDashboard.NARROW_COLUMN_WIDTH;
        messagesGrid.setColumnWidth(colNumColumn,
                UploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += UploadDashboard.NARROW_COLUMN_WIDTH;
        messagesGrid.setColumnWidth(colNameColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        messagesGrid.setColumnWidth(rowNumColumn,
                UploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += UploadDashboard.NARROW_COLUMN_WIDTH;
        messagesGrid.setColumnWidth(timestampColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        messagesGrid.setColumnWidth(longitudeColumn,
                UploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += UploadDashboard.NARROW_COLUMN_WIDTH;
        messagesGrid.setColumnWidth(latitudeColumn,
                UploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += UploadDashboard.NARROW_COLUMN_WIDTH;
        messagesGrid.setColumnWidth(explanationColumn,
                2 * UploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
        tableWidth += 2 * UploadDashboard.FILENAME_COLUMN_WIDTH;

        // Set the minimum width of the full table
        messagesGrid.setMinimumTableWidth(tableWidth, Style.Unit.EM);

        // Create the data provider for this table
        listProvider = new ListDataProvider<ADCMessage>();
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
        ListHandler<ADCMessage> columnSortHandler =
                new ListHandler<ADCMessage>(listProvider.getList());
        columnSortHandler.setComparator(severityColumn,
                ADCMessage.severityComparator);
        columnSortHandler.setComparator(colNumColumn,
                ADCMessage.colNumComparator);
        columnSortHandler.setComparator(colNameColumn,
                ADCMessage.colNameComparator);
        columnSortHandler.setComparator(rowNumColumn,
                ADCMessage.rowNumComparator);
        columnSortHandler.setComparator(timestampColumn,
                ADCMessage.timestampComparator);
        columnSortHandler.setComparator(longitudeColumn,
                ADCMessage.longitudeComparator);
        columnSortHandler.setComparator(latitudeColumn,
                ADCMessage.latitudeComparator);
        columnSortHandler.setComparator(explanationColumn,
                ADCMessage.explanationComparator);

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

    private TextColumn<ADCMessage> buildSeverityColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( msg == null )
                    return UNKNOWN_SEVERITY_TEXT;
                Severity severity = msg.getSeverity();
                if ( severity == Severity.WARNING )
                    return WARNING_SEVERITY_TEXT;
                if ( severity == Severity.ERROR )
                    return ERROR_SEVERITY_TEXT;
                return UNKNOWN_SEVERITY_TEXT;
            }
        };
    }

    private TextColumn<ADCMessage> buildColNumColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( (msg == null) || (msg.getColNumber() <= 0) )
                    return " --- ";
                return INT_NUMBER_FORMAT.format(msg.getColNumber());
            }
        };
    }

    private TextColumn<ADCMessage> buildColNameColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( (msg == null) || msg.getColName().isEmpty() )
                    return " --- ";
                return msg.getColName();
            }
        };
    }

    private TextColumn<ADCMessage> buildRowNumColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( (msg == null) || (msg.getRowNumber() <= 0) )
                    return "";
                return INT_NUMBER_FORMAT.format(msg.getRowNumber());
            }
        };
    }

    private TextColumn<ADCMessage> buildTimestampColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( msg == null )
                    return "";
                return msg.getTimestamp();
            }
        };
    }

    private TextColumn<ADCMessage> buildLongitudeColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( (msg == null) || Double.isNaN(msg.getLongitude()) )
                    return "";
                return FLT_NUMBER_FORMAT.format(msg.getLongitude());
            }
        };
    }

    private TextColumn<ADCMessage> buildLatitudeColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( (msg == null) || Double.isNaN(msg.getLatitude()) )
                    return "";
                return FLT_NUMBER_FORMAT.format(msg.getLatitude());
            }
        };
    }

    private TextColumn<ADCMessage> buildExplanationColumn() {
        return new TextColumn<ADCMessage>() {
            @Override
            public String getValue(ADCMessage msg) {
                if ( msg == null )
                    return "";
                return msg.getDetailedComment();
            }
        };
    }

}
