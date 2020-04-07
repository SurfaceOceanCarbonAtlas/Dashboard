package gov.noaa.pmel.dashboard.client;

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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Main upload dashboard page.  Shows uploaded cruise files and their status.  Provides connections to upload data
 * files, describe the contents of these data files, and submit the data for QC.
 *
 * @author Karl Smith
 */
public class DatasetListPage extends CompositeWithUsername {

    private static final String TITLE_TEXT = "My Datasets";
    private static final String WELCOME_INTRO = "Logged in as ";
    private static final String LOGOUT_TEXT = "Logout";

    private static final String UPLOAD_TEXT = "Upload Datasets";
    private static final String UPLOAD_HOVER_HELP =
            "upload data to create a new dataset " +
                    "or replace an existing dataset";

    private static final String VIEW_DATA_TEXT = "Identify Columns";
    private static final String VIEW_DATA_HOVER_HELP =
            "review and modify data column type assignments for the " +
                    "selected dataset; identify issues in the data";

    static final String SOCAT_METADATA_TEXT = "Edit SOCAT Metadata";
    private static final String SOCAT_METADATA_HOVER_HELP =
            "edit the SOCAT metadata for the selected datasets";

    private static final String ADDL_DOCS_TEXT = "Supplemental Documents";
    private static final String ADDL_DOCS_HOVER_HELP =
            "manage supplemental documents for the selected datasets";

    private static final String QC_SUBMIT_TEXT = "Submit for QC";
    private static final String QC_SUBMIT_HOVER_HELP =
            "submit the selected datasets for quality control assessment";

    private static final String SUSPEND_TEXT = "Suspend Dataset";
    private static final String SUSPEND_HOVER_HELP =
            "suspend the selected datasets from quality control assessment to allow updates";

    private static final String REVIEW_TEXT = "Preview Dataset";
    private static final String REVIEW_HOVER_HELP =
            "examine various plots of data given in the selected dataset";

    private static final String SHOW_DATASETS_TEXT =
            "Show Datasets";
    private static final String SHOW_DATASETS_HOVER_HELP =
            "show existing datasets in your list of displayed datasets";

    private static final String HIDE_DATASETS_TEXT =
            "Hide Datasets";
    private static final String HIDE_DATASETS_HOVER_HELP =
            "hides the selected datasets from your list of displayed datasets; " +
                    "this will NOT delete the datasets from the system";

    private static final String CHANGE_OWNER_TEXT =
            "Change Datasets Owner";
    private static final String CHANGE_OWNER_HOVER_HELP =
            "change the owner of the selected datasets to a dashboard user you specify";

    private static final String DELETE_TEXT = "Delete Datasets";
    private static final String DELETE_HOVER_HELP =
            "delete the selected datasets from the system";

    // Error message when the request for the latest cruise list fails
    private static final String GET_DATASET_LIST_ERROR_MSG =
            "Problems obtaining the latest dataset listing";

    // Starts of error messages for improper cruise selections
    private static final String SUBMITTED_DATASETS_SELECTED_ERR_START =
            "Only datasets which have not been submitted for QC, " +
                    "or which have been suspended or excluded, may be selected ";
    private static final String ARCHIVED_DATASETS_SELECTED_ERR_START =
            "Only datasets which have not been archived may be selected ";
    private static final String NO_DATASET_SELECTED_ERR_START =
            "No dataset is selected ";
    private static final String MANY_DATASETS_SELECTED_ERR_START =
            "Only one dataset may be selected ";

    // Ends of error messages for improper cruise selections
    private static final String FOR_REVIEWING_ERR_END =
            "for reviewing data.";
    private static final String FOR_METADATA_ERR_END =
            "for managing metadata.";
    private static final String FOR_ADDL_DOCS_ERR_END =
            "for managing supplemental documents.";
    private static final String FOR_PREVIEW_ERR_END =
            "for dataset preview.";
    private static final String FOR_QC_SUBMIT_ERR_END =
            "for submitting for QC and archival.";
    private static final String FOR_DELETE_ERR_END =
            "for deletion from the system.";
    private static final String FOR_HIDE_ERR_END =
            "for hiding from your list of displayed datasets.";
    private static final String FOR_CHANGE_OWNER_ERR_END =
            "for changing ownership";

    private static final String NO_DATASETS_TO_SUSPEND_ERR_MSG =
            "All datasets selected (if any) can be modified and, thus, do not need to be suspended.";

    private static final String CANNOT_PREVIEW_UNCHECKED_ERRMSG =
            "Preview plots cannot be generated for datasets " +
                    "with unidentified columns or unchecked data.";
    private static final String CANNOT_PREVIEW_WITH_SERIOUS_ERRORS_ERRMSG =
            "Preview plots cannot be generated for datasets " +
                    "with longitude, latitude, or time errors.";

    private static final String NO_METADATA_HTML_PROLOGUE =
            "The following datasets do not have appropriate metadata: <ul>";
    private static final String NO_METADATA_HTML_EPILOGUE =
            "</ul> Appropriate metadata needs to be uploaded " +
                    "for these datasets before submitting them for QC or archival. ";
    private static final String CANNOT_SUBMIT_HTML_PROLOGUE =
            "The following datasets have not been checked, or have very " +
                    "serious errors detected by the automated data checker: <ul>";
    private static final String CANNOT_SUBMIT_HTML_EPILOGUE =
            "</ul> These datasets cannot be submitted for QC or archival " +
                    "until these problems have been resolved.";
    private static final String DATA_AUTOFAIL_HTML_PROLOGUE =
            "The following datasets have errors detected " +
                    "by the automated data checker: <ul>";
    private static final String AUTOFAIL_HTML_EPILOGUE =
            "</ul> These dataset can be submitted for QC and archival, " +
                    "but datsets with a large number of error will <em>probably</em> " +
                    "be suspended by reviewers.<br />" +
                    "Do you want to continue? ";
    private static final String AUTOFAIL_YES_TEXT = "Yes";
    private static final String AUTOFAIL_NO_TEXT = "No";

    private static final String SUSPEND_HTML_PROLOGUE =
            "The following datasets will be suspended from QC to allow updates: <ul>";
    private static final String SUSPEND_HTML_EPILOGUE =
            "</ul> Do you want to proceed?";
    private static final String SUSPEND_YES_TEXT = "Yes";
    private static final String SUSPEND_NO_TEXT = "No";
    private static final String SUSPEND_FAIL_MSG =
            "Problems suspending the selected datasets";

    private static final String DATASETS_TO_SHOW_MSG =
            "Enter the ID, possibly with wildcards * and ?, of the dataset(s) " +
                    "you want to show in your list of displayed datasets";
    private static final String SHOW_DATASET_FAIL_MSG =
            "Unable to show the specified datasets " +
                    "in your personal list of displayed datasets";

    private static final String HIDE_DATASET_HTML_PROLOGUE =
            "The following datasets will be hidden from your " +
                    "list of displayed datasets; the data, metadata, and " +
                    "supplemental documents will <b>not</b> be removed: <ul>";
    private static final String HIDE_DATASET_HTML_EPILOGUE =
            "</ul> Do you want to proceed?";
    private static final String HIDE_YES_TEXT = "Yes";
    private static final String HIDE_NO_TEXT = "No";
    private static final String HIDE_DATASET_FAIL_MSG =
            "Unable to hide the selected datasets from " +
                    "your list of displayed datasets";

    private static final String CHANGE_OWNER_HTML_PROLOGUE =
            "The owner of the following datasets will be " +
                    "changed to the new owner you specify below: <ul>";
    private static final String CHANGE_OWNER_HTML_EPILOGUE =
            "</ul>";
    private static final String CHANGE_OWNER_INPUT_TEXT = "New Owner:";
    private static final String CHANGE_OWNER_YES_TEXT = "Proceed";
    private static final String CHANGE_OWNER_NO_TEXT = "Cancel";
    private static final String CHANGE_OWNER_FAIL_MSG =
            "An error occurred when changing ownership of these datasets";

    private static final String DELETE_DATASET_HTML_PROLOGUE =
            "All data will be deleted for the following datasets: <ul>";
    private static final String DELETE_DATASET_HTML_EPILOGUE =
            "</ul> Do you want to proceed?";
    private static final String DELETE_YES_TEXT = "Yes";
    private static final String DELETE_NO_TEXT = "No";
    private static final String DELETE_DATASET_FAIL_MSG =
            "Unable to delete the datasets";

    private static final String UNEXPECTED_INVALID_DATESET_LIST_MSG =
            " (unexpected invalid datasets list returned)";

    // Select options
    private static final String SELECTION_OPTION_LABEL = "Select...";
    private static final String ALL_SELECTION_OPTION = "All";
    private static final String EDITABLE_SELECTION_OPTION = "Editable";
    private static final String SUBMITTED_SELECTION_OPTION = "Submitted";
    private static final String PUBLISHED_SELECTION_OPTION = "Published";
    private static final String CLEAR_SELECTION_OPTION = "None";

    // Column header strings
    private static final String DATASET_ID_COLUMN_NAME = "Dataset ID";
    private static final String TIMESTAMP_COLUMN_NAME = "Upload Date";
    private static final String DATA_CHECK_COLUMN_NAME = "Data Status";
    private static final String SOCAT_METADATA_COLUMN_NAME = "SOCAT Metadata";
    private static final String ADDL_DOCS_COLUMN_NAME = "Supplemental<br />Documents";
    private static final String VERSION_COLUMN_NAME = "Version";
    private static final String SUBMITTED_COLUMN_NAME = "QC Status";
    private static final String ARCHIVED_COLUMN_NAME = "Archival";
    private static final String FILENAME_COLUMN_NAME = "Filename";
    private static final String OWNER_COLUMN_NAME = "Owner";

    // Replacement strings for empty or null values
    private static final String EMPTY_TABLE_TEXT = "(no uploaded datasets)";
    private static final String NO_DATASET_ID_STRING = "(unknown)";
    private static final String NO_TIMESTAMP_STRING = "(unknown)";
    private static final String NO_DATA_CHECK_STATUS_STRING = "Not checked";
    private static final String NO_SOCAT_METADATA_STATUS_STRING = "(no metadata)";
    private static final String NO_ARCHIVE_STATUS_STRING = "Not specified";
    private static final String NO_UPLOAD_FILENAME_STRING = "(unknown)";
    private static final String NO_ADDL_DOCS_STATUS_STRING = "(no documents)";
    private static final String NO_OWNER_STRING = "(unknown)";

    interface DatasetListPageUiBinder extends UiBinder<Widget,DatasetListPage> {
    }

    private static DatasetListPageUiBinder uiBinder =
            GWT.create(DatasetListPageUiBinder.class);

    private static DashboardServicesInterfaceAsync service =
            GWT.create(DashboardServicesInterface.class);

    @UiField
    Label titleLabel;
    @UiField
    Image titleImage;
    @UiField
    InlineLabel userInfoLabel;
    @UiField
    Button logoutButton;
    @UiField
    Button uploadButton;
    @UiField
    Button viewDataButton;
    @UiField
    Button omeMetadataButton;
    @UiField
    Button addlDocsButton;
    @UiField
    Button reviewButton;
    @UiField
    Button qcSubmitButton;
    @UiField
    Button suspendDatasetButton;
    @UiField
    Label firstSeparator;
    @UiField
    Button showDatasetButton;
    @UiField
    Button hideDatasetButton;
    @UiField
    Button changeOwnerButton;
    @UiField
    Label secondSeparator;
    @UiField
    Button deleteButton;
    @UiField
    DataGrid<DashboardDataset> datasetsGrid;

    private ListDataProvider<DashboardDataset> listProvider;
    private DashboardAskPopup askSuspendPopup;
    private DashboardAskPopup askDeletePopup;
    private DashboardAskPopup askRemovePopup;
    private DashboardInputPopup changeOwnerPopup;
    private DashboardDatasetList datasetsSet;
    private DashboardDatasetList checkSet;
    private TreeSet<String> datasetIdsSet;
    private DashboardAskPopup askDataAutofailPopup;
    private boolean isManager;
    private String imageExtension;
    private TextColumn<DashboardDataset> timestampColumn;
    private TextColumn<DashboardDataset> expocodeColumn;

    // The singleton instance of this page
    private static DatasetListPage singleton;

    /**
     * Creates an empty dataset list page.  Do not call this constructor; instead use the showPage static method to show
     * the singleton instance of this page with the latest dataset list from the server.
     */
    DatasetListPage() {
        initWidget(uiBinder.createAndBindUi(this));
        singleton = this;

        buildDatasetListTable();

        setUsername(null);

        datasetsSet = new DashboardDatasetList();
        checkSet = new DashboardDatasetList();
        datasetIdsSet = new TreeSet<String>();

        titleLabel.setText(TITLE_TEXT);
        titleImage.setResource(UploadDashboard.resources.getSocatCatPng());
        logoutButton.setText(LOGOUT_TEXT);

        uploadButton.setText(UPLOAD_TEXT);
        uploadButton.setTitle(UPLOAD_HOVER_HELP);

        viewDataButton.setText(VIEW_DATA_TEXT);
        viewDataButton.setTitle(VIEW_DATA_HOVER_HELP);

        omeMetadataButton.setText(SOCAT_METADATA_TEXT);
        omeMetadataButton.setTitle(SOCAT_METADATA_HOVER_HELP);

        addlDocsButton.setText(ADDL_DOCS_TEXT);
        addlDocsButton.setTitle(ADDL_DOCS_HOVER_HELP);

        reviewButton.setText(REVIEW_TEXT);
        reviewButton.setTitle(REVIEW_HOVER_HELP);

        qcSubmitButton.setText(QC_SUBMIT_TEXT);
        qcSubmitButton.setTitle(QC_SUBMIT_HOVER_HELP);

        suspendDatasetButton.setText(SUSPEND_TEXT);
        suspendDatasetButton.setTitle(SUSPEND_HOVER_HELP);

        showDatasetButton.setText(SHOW_DATASETS_TEXT);
        showDatasetButton.setTitle(SHOW_DATASETS_HOVER_HELP);

        hideDatasetButton.setText(HIDE_DATASETS_TEXT);
        hideDatasetButton.setTitle(HIDE_DATASETS_HOVER_HELP);

        changeOwnerButton.setText(CHANGE_OWNER_TEXT);
        changeOwnerButton.setTitle(CHANGE_OWNER_HOVER_HELP);

        deleteButton.setText(DELETE_TEXT);
        deleteButton.setTitle(DELETE_HOVER_HELP);

        // managerButtonsShown = true;
        askDeletePopup = null;
        askRemovePopup = null;
        askDataAutofailPopup = null;
        uploadButton.setFocus(true);
    }

    /**
     * Display the dataset list page in the RootLayoutPanel with the latest information from the server. Adds this page
     * to the page history.
     */
    static void showPage() {
        UploadDashboard.showWaitCursor();
        // Request the latest cruise list
        service.getDatasetList(new AsyncCallback<DashboardDatasetList>() {
            @Override
            public void onSuccess(DashboardDatasetList cruises) {
                if ( singleton == null )
                    singleton = new DatasetListPage();
                UploadDashboard.updateCurrentPage(singleton);
                singleton.updateDatasets(cruises);
                History.newItem(PagesEnum.SHOW_DATASETS.name(), false);
                UploadDashboard.showAutoCursor();
            }

            @Override
            public void onFailure(Throwable ex) {
                UploadDashboard.showFailureMessage(GET_DATASET_LIST_ERROR_MSG, ex);
                UploadDashboard.showAutoCursor();
            }
        });
    }

    /**
     * Redisplays the last version of this page if the username associated with this page matches the given username.
     */
    static void redisplayPage(String username) {
        if ( (username == null) || username.isEmpty() ||
                (singleton == null) || !singleton.getUsername().equals(username) )
            DatasetListPage.showPage();
        else
            UploadDashboard.updateCurrentPage(singleton);
    }

    /**
     * Add a dataset to the selected list when the page is displayed. This should be called just prior to calling
     * showPage(). If no dataset with the given ID exists in the updated list of datasets, this ID will be ignored.
     *
     * @param datasetId
     *         select the dataset with this ID
     */
    static void addSelectedDataset(String datasetId) {
        if ( singleton == null )
            singleton = new DatasetListPage();
        singleton.datasetIdsSet.add(datasetId);
    }

    /**
     * Resorts the cruise list table first by upload timestamp in descending order, then by dataset in ascending order.
     */
    static void resortTable() {
        if ( singleton == null )
            singleton = new DatasetListPage();
        ColumnSortList sortList = singleton.datasetsGrid.getColumnSortList();
        sortList.push(new ColumnSortInfo(singleton.expocodeColumn, true));
        sortList.push(new ColumnSortInfo(singleton.timestampColumn, false));
        ColumnSortEvent.fire(singleton.datasetsGrid, sortList);
    }

    /**
     * Updates the dataset list page with the current username and with the datasets given in the argument.
     *
     * @param newList
     *         datasets to display
     */
    private void updateDatasets(DashboardDatasetList newList) {
        // Update the username
        setUsername(newList.getUsername());
        userInfoLabel.setText(WELCOME_INTRO + getUsername());
        // Update the cruises shown by resetting the data in the data provider
        List<DashboardDataset> providerList = listProvider.getList();
        providerList.clear();
        if ( newList != null ) {
            providerList.addAll(newList.values());
            isManager = newList.isManager();
            imageExtension = newList.getImageExtension();
        }
        for (DashboardDataset dataset : providerList) {
            if ( datasetIdsSet.contains(dataset.getDatasetId()) )
                dataset.setSelected(true);
            else
                dataset.setSelected(false);
        }
        datasetsGrid.setRowCount(providerList.size());
        datasetsGrid.setVisibleRange(0, providerList.size());
        // Make sure the table is sorted according to the last specification
        ColumnSortEvent.fire(datasetsGrid, datasetsGrid.getColumnSortList());
    }

    /**
     * Selects dataset of the given type in the dataset list, supplementing the currently selected datasets, or clears
     * all selected datasets.
     *
     * @param option
     *         string indicating the cruise types to select;  one of: <br /> SELECTION_OPTION_LABEL - does nothing; <br
     *         /> ALL_SELECTION_OPTION - selects all cruises; <br /> EDITABLE_SELECTION_OPTION - selects editable
     *         cruises; <br /> SUBMITTED_SELECTION_OPTION - selects submitted cruises; <br /> PUBLISHED_SELECTION_OPTION
     *         - selects published cruises; or <br /> CLEAR_SELECTION_OPTION - clears all selected cruises.
     */
    private void setDatasetSelection(String option) {
        // Do nothing is SELECTION_OPTION_LABEL is given
        if ( SELECTION_OPTION_LABEL.equals(option) )
            return;
        // Modify the dataset selection
        List<DashboardDataset> providerList = listProvider.getList();
        if ( ALL_SELECTION_OPTION.equals(option) ) {
            for (DashboardDataset dataset : providerList) {
                dataset.setSelected(true);
            }
        }
        else if ( EDITABLE_SELECTION_OPTION.equals(option) ) {
            for (DashboardDataset dataset : providerList) {
                if ( Boolean.TRUE.equals(dataset.isEditable()) )
                    dataset.setSelected(true);
                else
                    dataset.setSelected(false);
            }
        }
        else if ( SUBMITTED_SELECTION_OPTION.equals(option) ) {
            for (DashboardDataset dataset : providerList) {
                if ( Boolean.FALSE.equals(dataset.isEditable()) )
                    dataset.setSelected(true);
                else
                    dataset.setSelected(false);
            }
        }
        else if ( PUBLISHED_SELECTION_OPTION.equals(option) ) {
            for (DashboardDataset dataset : providerList) {
                if ( null == dataset.isEditable() )
                    dataset.setSelected(true);
                else
                    dataset.setSelected(false);
            }
        }
        else if ( CLEAR_SELECTION_OPTION.equals(option) ) {
            for (DashboardDataset dataset : providerList) {
                dataset.setSelected(false);
            }
        }
        else {
            throw new RuntimeException("Unexpected option given the setDatasetSelection: " + option);
        }
        datasetsGrid.setRowCount(providerList.size());
        datasetsGrid.setVisibleRange(0, providerList.size());
        // Make sure the table is sorted according to the last specification
        ColumnSortEvent.fire(datasetsGrid, datasetsGrid.getColumnSortList());
    }

    /**
     * Assigns datasetsSet with the selected cruises, and datasetIdsSet with the datasetIds of these cruises.
     *
     * @param onlyEditable
     *         if true, fails if a submitted or published cruise is selected; if false, fails if a published cruise is
     *         selected; if null, always succeeds.
     *
     * @return if successful
     */
    private boolean getSelectedDatasets(Boolean onlyEditable) {
        datasetIdsSet.clear();
        datasetsSet.clear();
        datasetsSet.setUsername(getUsername());
        datasetsSet.setManager(isManager);
        datasetsSet.setImageExtension(imageExtension);
        for (DashboardDataset dataset : listProvider.getList()) {
            if ( dataset.isSelected() ) {
                if ( onlyEditable != null ) {
                    Boolean editable = dataset.isEditable();
                    // check if from a previous version
                    if ( editable == null )
                        return false;
                    // check if editable, if requested
                    if ( onlyEditable && !editable )
                        return false;
                }
                String expocode = dataset.getDatasetId();
                datasetIdsSet.add(expocode);
                datasetsSet.put(expocode, dataset);
            }
        }
        return true;
    }

    @UiHandler("logoutButton")
    void logoutOnClick(ClickEvent event) {
        DashboardLogoutPage.showPage();
    }

    @UiHandler("uploadButton")
    void uploadDatasetOnClick(ClickEvent event) {
        // Save the IDs of the currently selected datasets
        getSelectedDatasets(null);
        // Go to the dataset upload page
        DataUploadPage.showPage(getUsername());
    }

    @UiHandler("viewDataButton")
    void dataCheckOnClick(ClickEvent event) {
        if ( !getSelectedDatasets(true) ) {
            UploadDashboard.showMessage(
                    SUBMITTED_DATASETS_SELECTED_ERR_START + FOR_REVIEWING_ERR_END);
            return;
        }
        if ( datasetIdsSet.size() < 1 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_REVIEWING_ERR_END);
            return;
        }
        DataColumnSpecsPage.showPage(getUsername(), new ArrayList<String>(datasetIdsSet));
    }

    @UiHandler("omeMetadataButton")
    void omeOnClick(ClickEvent event) {
        getSelectedDatasets(null);
        if ( datasetsSet.size() < 1 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_METADATA_ERR_END);
            return;
        }
        // Until the SOCAT Metadata is in place, only accept one cruise
        if ( datasetsSet.size() > 1 ) {
            UploadDashboard.showMessage(
                    MANY_DATASETS_SELECTED_ERR_START + FOR_METADATA_ERR_END);
            return;
        }
        EditMetadataPage.showPage(datasetsSet);
    }

    @UiHandler("addlDocsButton")
    void addlDocsOnClick(ClickEvent event) {
        getSelectedDatasets(null);
        if ( datasetsSet.size() < 1 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_ADDL_DOCS_ERR_END);
            return;
        }
        AddlDocsManagerPage.showPage(datasetsSet);
    }

    @UiHandler("reviewButton")
    void reviewOnClick(ClickEvent event) {
        getSelectedDatasets(null);
        if ( datasetsSet.size() < 1 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_PREVIEW_ERR_END);
            return;
        }
        if ( datasetsSet.size() > 1 ) {
            UploadDashboard.showMessage(
                    MANY_DATASETS_SELECTED_ERR_START + FOR_PREVIEW_ERR_END);
            return;
        }
        for (DashboardDataset dataset : datasetsSet.values()) {
            String status = dataset.getDataCheckStatus();
            if ( status.equals(DashboardUtils.CHECK_STATUS_NOT_CHECKED) ) {
                UploadDashboard.showMessage(CANNOT_PREVIEW_UNCHECKED_ERRMSG);
                return;
            }
            else if ( status.contains(DashboardUtils.GEOPOSITION_ERRORS_MSG) ) {
                UploadDashboard.showMessage(CANNOT_PREVIEW_WITH_SERIOUS_ERRORS_ERRMSG);
                return;
            }
        }
        DatasetPreviewPage.showPage(datasetsSet);
        return;
    }

    @UiHandler("qcSubmitButton")
    void qcSubmitOnClick(ClickEvent event) {
        if ( !getSelectedDatasets(false) ) {
            UploadDashboard.showMessage(
                    ARCHIVED_DATASETS_SELECTED_ERR_START + FOR_QC_SUBMIT_ERR_END);
            return;
        }
        if ( datasetsSet.size() == 0 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_QC_SUBMIT_ERR_END);
            return;
        }
        checkSet.clear();
        checkSet.setUsername(getUsername());
        checkSet.setManager(isManager);
        checkSet.setImageExtension(imageExtension);
        checkSet.putAll(datasetsSet);
        checkDatasetsForSubmitting();
    }

    @UiHandler("suspendDatasetButton")
    void suspendDatasetOnClick(ClickEvent event) {
        getSelectedDatasets(null);
        // remove any datasets that are editable
        datasetIdsSet.clear();
        for (DashboardDataset dset : datasetsSet.values()) {
            if ( !Boolean.TRUE.equals(dset.isEditable()) )
                datasetIdsSet.add(dset.getDatasetId());
        }
        if ( datasetIdsSet.size() == 0 ) {
            UploadDashboard.showMessage(NO_DATASETS_TO_SUSPEND_ERR_MSG);
            return;
        }
        // Confirm datasets to be suspended
        String message = SUSPEND_HTML_PROLOGUE;
        for (String datasetId : datasetIdsSet) {
            message += "<li>" + SafeHtmlUtils.htmlEscape(datasetId) + "</li>";
        }
        message += SUSPEND_HTML_EPILOGUE;
        if ( askSuspendPopup == null ) {
            askSuspendPopup = new DashboardAskPopup(SUSPEND_YES_TEXT,
                    SUSPEND_NO_TEXT, new AsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean okay) {
                    // Only proceed only if yes button was selected
                    if ( okay ) {
                        continueSuspendDatasets();
                    }
                }

                @Override
                public void onFailure(Throwable ex) {
                    // Never called
                    ;
                }
            });
        }
        askSuspendPopup.askQuestion(message);
    }

    /**
     * Makes the request to suspend the currently selected datasets that are not editable, and processes the results.
     */
    private void continueSuspendDatasets() {
        String username = getUsername();
        UploadDashboard.showWaitCursor();
        service.suspendDatasets(username, datasetIdsSet, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                DatasetListPage.showPage();
                UploadDashboard.showAutoCursor();
            }

            @Override
            public void onFailure(Throwable ex) {
                UploadDashboard.showFailureMessage(SUSPEND_FAIL_MSG, ex);
                UploadDashboard.showAutoCursor();
            }
        });
    }

    @UiHandler("deleteButton")
    void deleteDatasetOnClick(ClickEvent event) {
        if ( !getSelectedDatasets(true) ) {
            UploadDashboard.showMessage(
                    SUBMITTED_DATASETS_SELECTED_ERR_START + FOR_DELETE_ERR_END);
            return;
        }
        if ( datasetIdsSet.size() == 0 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_DELETE_ERR_END);
            return;
        }
        // Confirm cruises to be deleted
        String message = DELETE_DATASET_HTML_PROLOGUE;
        for (String datasetId : datasetIdsSet) {
            message += "<li>" + SafeHtmlUtils.htmlEscape(datasetId) + "</li>";
        }
        message += DELETE_DATASET_HTML_EPILOGUE;
        if ( askDeletePopup == null ) {
            askDeletePopup = new DashboardAskPopup(DELETE_YES_TEXT,
                    DELETE_NO_TEXT, new AsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean okay) {
                    // Only proceed only if yes button was selected
                    if ( okay ) {
                        // never delete the metadata or supplemental documents
                        continueDeleteDatasets(false);
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
     * Makes the request to delete the currently selected cruises, and processes the results.
     */
    private void continueDeleteDatasets(Boolean deleteMetadata) {
        UploadDashboard.showWaitCursor();
        service.deleteDatasets(getUsername(), datasetIdsSet, deleteMetadata,
                new AsyncCallback<DashboardDatasetList>() {
                    @Override
                    public void onSuccess(DashboardDatasetList datasetList) {
                        if ( getUsername().equals(datasetList.getUsername()) ) {
                            DatasetListPage.this.updateDatasets(datasetList);
                        }
                        else {
                            UploadDashboard.showMessage(DELETE_DATASET_FAIL_MSG +
                                    UNEXPECTED_INVALID_DATESET_LIST_MSG);
                        }
                        UploadDashboard.showAutoCursor();
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        UploadDashboard.showFailureMessage(DELETE_DATASET_FAIL_MSG, ex);
                        UploadDashboard.showAutoCursor();
                    }
                });
    }

    @UiHandler("showDatasetButton")
    void addToListOnClick(ClickEvent event) {
        String wildDatasetId = Window.prompt(DATASETS_TO_SHOW_MSG, "");
        if ( (wildDatasetId != null) && !wildDatasetId.trim().isEmpty() ) {
            UploadDashboard.showWaitCursor();
            // Save the currently selected cruises
            getSelectedDatasets(null);
            service.addDatasetsToList(getUsername(), wildDatasetId,
                    new AsyncCallback<DashboardDatasetList>() {
                        @Override
                        public void onSuccess(DashboardDatasetList cruises) {
                            if ( getUsername().equals(cruises.getUsername()) ) {
                                DatasetListPage.this.updateDatasets(cruises);
                            }
                            else {
                                UploadDashboard.showMessage(SHOW_DATASET_FAIL_MSG +
                                        UNEXPECTED_INVALID_DATESET_LIST_MSG);
                            }
                            UploadDashboard.showAutoCursor();
                        }

                        @Override
                        public void onFailure(Throwable ex) {
                            UploadDashboard.showFailureMessage(SHOW_DATASET_FAIL_MSG, ex);
                            UploadDashboard.showAutoCursor();
                        }
                    });
        }
    }

    @UiHandler("hideDatasetButton")
    void removeFromListOnClick(ClickEvent event) {
        getSelectedDatasets(null);
        if ( datasetIdsSet.size() == 0 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_HIDE_ERR_END);
            return;
        }
        // Confirm cruises to be removed
        String message = HIDE_DATASET_HTML_PROLOGUE;
        for (String expocode : datasetIdsSet) {
            message += "<li>" + SafeHtmlUtils.htmlEscape(expocode) + "</li>";
        }
        message += HIDE_DATASET_HTML_EPILOGUE;
        if ( askRemovePopup == null ) {
            askRemovePopup = new DashboardAskPopup(HIDE_YES_TEXT,
                    HIDE_NO_TEXT, new AsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    // Only proceed if yes; ignore if no or null
                    if ( result == true )
                        continueRemoveDatasetsFromList();
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
     * Makes the request to remove cruises from a user's list, and processes the results.
     */
    private void continueRemoveDatasetsFromList() {
        UploadDashboard.showWaitCursor();
        service.removeDatasetsFromList(getUsername(), datasetIdsSet,
                new AsyncCallback<DashboardDatasetList>() {
                    @Override
                    public void onSuccess(DashboardDatasetList cruises) {
                        if ( getUsername().equals(cruises.getUsername()) ) {
                            DatasetListPage.this.updateDatasets(cruises);
                        }
                        else {
                            UploadDashboard.showMessage(HIDE_DATASET_FAIL_MSG +
                                    UNEXPECTED_INVALID_DATESET_LIST_MSG);
                        }
                        UploadDashboard.showAutoCursor();
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        UploadDashboard.showFailureMessage(HIDE_DATASET_FAIL_MSG, ex);
                        UploadDashboard.showAutoCursor();
                    }
                });
    }

    @UiHandler("changeOwnerButton")
    void changeOwnerOnClick(ClickEvent event) {
        getSelectedDatasets(null);
        if ( datasetIdsSet.size() == 0 ) {
            UploadDashboard.showMessage(
                    NO_DATASET_SELECTED_ERR_START + FOR_CHANGE_OWNER_ERR_END);
            return;
        }
        // Confirm cruises to be removed
        String message = CHANGE_OWNER_HTML_PROLOGUE;
        for (String expocode : datasetIdsSet) {
            message += "<li>" + SafeHtmlUtils.htmlEscape(expocode) + "</li>";
        }
        message += CHANGE_OWNER_HTML_EPILOGUE;
        if ( changeOwnerPopup == null ) {
            changeOwnerPopup = new DashboardInputPopup(CHANGE_OWNER_INPUT_TEXT,
                    CHANGE_OWNER_YES_TEXT, CHANGE_OWNER_NO_TEXT,
                    new AsyncCallback<String>() {
                        @Override
                        public void onSuccess(String newOwner) {
                            if ( newOwner != null ) {
                                continueChangeOwner(newOwner);
                            }
                        }

                        @Override
                        public void onFailure(Throwable ex) {
                            // Never called
                            ;
                        }
                    });
        }
        changeOwnerPopup.askForInput(message);
    }

    /**
     * Makes the request to remove cruises from a user's list, and processes the results.
     */
    private void continueChangeOwner(String newOwner) {
        UploadDashboard.showWaitCursor();
        service.changeDatasetOwner(getUsername(), datasetIdsSet, newOwner,
                new AsyncCallback<DashboardDatasetList>() {
                    @Override
                    public void onSuccess(DashboardDatasetList cruises) {
                        if ( getUsername().equals(cruises.getUsername()) ) {
                            DatasetListPage.this.updateDatasets(cruises);
                        }
                        else {
                            UploadDashboard.showMessage(CHANGE_OWNER_FAIL_MSG +
                                    UNEXPECTED_INVALID_DATESET_LIST_MSG);
                        }
                        UploadDashboard.showAutoCursor();
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        UploadDashboard.showFailureMessage(CHANGE_OWNER_FAIL_MSG, ex);
                        UploadDashboard.showAutoCursor();
                    }
                });
    }

    /**
     * Creates the cruise data table columns.  The table will still need to be populated using {@link
     * #updateDatasets(DashboardDatasetList)}.
     */
    private void buildDatasetListTable() {
        Header<String> selectHeader = buildSelectionHeader();

        // Create the columns for this table
        TextColumn<DashboardDataset> rowNumColumn = buildRowNumColumn();
        Column<DashboardDataset,Boolean> selectedColumn = buildSelectedColumn();
        expocodeColumn = buildDatasetIdColumn();
        timestampColumn = buildTimestampColumn();
        Column<DashboardDataset,String> dataCheckColumn = buildDataCheckColumn();
        Column<DashboardDataset,String> omeMetadataColumn = buildOmeMetadataColumn();
        Column<DashboardDataset,String> addlDocsColumn = buildAddnDocsColumn();
        TextColumn<DashboardDataset> versionColumn = buildVersionColumn();
        Column<DashboardDataset,String> qcStatusColumn = buildQCStatusColumn();
        Column<DashboardDataset,String> archiveStatusColumn = buildArchiveStatusColumn();
        TextColumn<DashboardDataset> filenameColumn = buildFilenameColumn();
        TextColumn<DashboardDataset> ownerColumn = buildOwnerColumn();

        // Add the columns, with headers, to the table
        datasetsGrid.addColumn(rowNumColumn, "");
        datasetsGrid.addColumn(selectedColumn, selectHeader);
        datasetsGrid.addColumn(expocodeColumn,
                SafeHtmlUtils.fromSafeConstant(DATASET_ID_COLUMN_NAME));
        datasetsGrid.addColumn(timestampColumn,
                SafeHtmlUtils.fromSafeConstant(TIMESTAMP_COLUMN_NAME));
        datasetsGrid.addColumn(dataCheckColumn,
                SafeHtmlUtils.fromSafeConstant(DATA_CHECK_COLUMN_NAME));
        datasetsGrid.addColumn(omeMetadataColumn,
                SafeHtmlUtils.fromSafeConstant(SOCAT_METADATA_COLUMN_NAME));
        datasetsGrid.addColumn(addlDocsColumn,
                SafeHtmlUtils.fromSafeConstant(ADDL_DOCS_COLUMN_NAME));
        datasetsGrid.addColumn(versionColumn,
                SafeHtmlUtils.fromSafeConstant(VERSION_COLUMN_NAME));
        datasetsGrid.addColumn(qcStatusColumn,
                SafeHtmlUtils.fromSafeConstant(SUBMITTED_COLUMN_NAME));
        datasetsGrid.addColumn(archiveStatusColumn,
                SafeHtmlUtils.fromSafeConstant(ARCHIVED_COLUMN_NAME));
        datasetsGrid.addColumn(filenameColumn,
                SafeHtmlUtils.fromSafeConstant(FILENAME_COLUMN_NAME));
        datasetsGrid.addColumn(ownerColumn,
                SafeHtmlUtils.fromSafeConstant(OWNER_COLUMN_NAME));

        // Set the minimum widths of the columns
        double minTableWidth = 0.0;
        datasetsGrid.setColumnWidth(rowNumColumn,
                UploadDashboard.CHECKBOX_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.CHECKBOX_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(selectedColumn,
                UploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NARROW_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(expocodeColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(timestampColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(dataCheckColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(omeMetadataColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(addlDocsColumn,
                UploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.FILENAME_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(versionColumn,
                UploadDashboard.NARROW_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NARROW_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(qcStatusColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(archiveStatusColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(filenameColumn,
                UploadDashboard.FILENAME_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.FILENAME_COLUMN_WIDTH;
        datasetsGrid.setColumnWidth(ownerColumn,
                UploadDashboard.NORMAL_COLUMN_WIDTH, Style.Unit.EM);
        minTableWidth += UploadDashboard.NORMAL_COLUMN_WIDTH;

        // Set the minimum width of the full table
        datasetsGrid.setMinimumTableWidth(minTableWidth, Style.Unit.EM);

        // Create the data provider for this table
        listProvider = new ListDataProvider<DashboardDataset>();
        listProvider.addDataDisplay(datasetsGrid);

        // Make the columns sortable
        expocodeColumn.setSortable(true);
        timestampColumn.setSortable(true);
        dataCheckColumn.setSortable(true);
        omeMetadataColumn.setSortable(true);
        addlDocsColumn.setSortable(true);
        versionColumn.setSortable(true);
        qcStatusColumn.setSortable(true);
        archiveStatusColumn.setSortable(true);
        filenameColumn.setSortable(true);
        ownerColumn.setSortable(true);

        // Add a column sorting handler for these columns
        ListHandler<DashboardDataset> columnSortHandler = new ListHandler<DashboardDataset>(listProvider.getList());
        columnSortHandler.setComparator(expocodeColumn, DashboardUtils.dataDatasetIdComparator);
        columnSortHandler.setComparator(timestampColumn, DashboardUtils.dataTimestampComparator);
        columnSortHandler.setComparator(dataCheckColumn, DashboardUtils.dataCheckComparator);
        columnSortHandler.setComparator(omeMetadataColumn, DashboardUtils.omeTimestampComparator);
        columnSortHandler.setComparator(addlDocsColumn, DashboardUtils.addlDocsComparator);
        columnSortHandler.setComparator(versionColumn, DashboardUtils.versionComparator);
        columnSortHandler.setComparator(qcStatusColumn, DashboardUtils.submitStatusComparator);
        columnSortHandler.setComparator(archiveStatusColumn, DashboardUtils.archiveStatusComparator);
        columnSortHandler.setComparator(filenameColumn, DashboardUtils.dataFilenameComparator);
        columnSortHandler.setComparator(ownerColumn, DashboardUtils.dataOwnerComparator);

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
    private TextColumn<DashboardDataset> buildRowNumColumn() {
        TextColumn<DashboardDataset> rowNumColumn = new TextColumn<DashboardDataset>() {
            @Override
            public String getValue(DashboardDataset cruise) {
                String expocode = cruise.getDatasetId();
                List<DashboardDataset> cruiseList = listProvider.getList();
                int k = 0;
                while ( k < cruiseList.size() ) {
                    // Only check datasetIds since they should be unique
                    if ( expocode.equals(cruiseList.get(k).getDatasetId()) )
                        break;
                    k++;
                }
                return Integer.toString(k + 1);
            }

            @Override
            public void render(Cell.Context ctx, DashboardDataset cruise,
                    SafeHtmlBuilder sb) {
                String msg = getValue(cruise);
                sb.appendHtmlConstant("<div style=\"color: " +
                        UploadDashboard.ROW_NUMBER_COLOR + ";\">");
                for (int k = msg.length(); k < 4; k++) {
                    sb.appendHtmlConstant("&nbsp;");
                }
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
        SelectionCell selectHeaderCell = new SelectionCell(Arrays.asList(
                SELECTION_OPTION_LABEL,
                ALL_SELECTION_OPTION,
                EDITABLE_SELECTION_OPTION,
                SUBMITTED_SELECTION_OPTION,
                PUBLISHED_SELECTION_OPTION,
                CLEAR_SELECTION_OPTION));
        Header<String> selectHeader = new Header<String>(selectHeaderCell) {
            @Override
            public String getValue() {
                return SELECTION_OPTION_LABEL;
            }
        };
        selectHeader.setUpdater(new ValueUpdater<String>() {
            @Override
            public void update(String option) {
                if ( option == null )
                    return;
                setDatasetSelection(option);
            }
        });
        return selectHeader;
    }

    /**
     * @return the selection column for the table
     */
    private Column<DashboardDataset,Boolean> buildSelectedColumn() {
        Column<DashboardDataset,Boolean> selectedColumn =
                new Column<DashboardDataset,Boolean>(new CheckboxCell(true, true)) {
                    @Override
                    public Boolean getValue(DashboardDataset cruise) {
                        return cruise.isSelected();
                    }
                };
        selectedColumn.setFieldUpdater(new FieldUpdater<DashboardDataset,Boolean>() {
            @Override
            public void update(int index, DashboardDataset cruise, Boolean value) {
                if ( !value ) {
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
     * @return the dataset column for the table
     */
    private TextColumn<DashboardDataset> buildDatasetIdColumn() {
        TextColumn<DashboardDataset> expocodeColumn =
                new TextColumn<DashboardDataset>() {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        String expocode = cruise.getDatasetId();
                        if ( expocode.isEmpty() )
                            expocode = NO_DATASET_ID_STRING;
                        return expocode;
                    }
                };
        return expocodeColumn;
    }

    /**
     * @return the timestamp column for the table
     */
    private TextColumn<DashboardDataset> buildTimestampColumn() {
        TextColumn<DashboardDataset> timestampColumn =
                new TextColumn<DashboardDataset>() {
                    @Override
                    public String getValue(DashboardDataset cruise) {
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
    private Column<DashboardDataset,String> buildDataCheckColumn() {
        Column<DashboardDataset,String> dataCheckColumn =
                new Column<DashboardDataset,String>(new ClickableTextCell()) {
                    @Override
                    public String getValue(DashboardDataset cruise) {
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
                    public void render(Cell.Context ctx, DashboardDataset cruise,
                            SafeHtmlBuilder sb) {
                        String msg = getValue(cruise);
                        if ( msg.equals(DashboardUtils.CHECK_STATUS_ACCEPTABLE) ) {
                            // No problems - use normal background
                            sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
                            sb.appendEscaped(msg);
                            sb.appendHtmlConstant("</em></u></div>");
                        }
                        else if ( msg.contains("warnings") ||
                                (msg.contains("errors") &&
                                        (!msg.contains(DashboardUtils.GEOPOSITION_ERRORS_MSG)) &&
                                        (cruise.getNumErrorRows() <= DashboardUtils.MAX_ACCEPTABLE_ERRORS)) ) {
                            // Only warnings or a few minor errors - use warning background color
                            sb.appendHtmlConstant("<div style=\"cursor:pointer; background-color:" +
                                    UploadDashboard.CHECKER_WARNING_COLOR + ";\"><u><em>");
                            sb.appendEscaped(msg);
                            sb.appendHtmlConstant("</em></u></div>");
                        }
                        else {
                            // Many errors, unacceptable, or not checked - use error background color
                            sb.appendHtmlConstant("<div style=\"cursor:pointer; background-color:" +
                                    UploadDashboard.CHECKER_ERROR_COLOR + ";\"><u><em>");
                            sb.appendEscaped(msg);
                            sb.appendHtmlConstant("</em></u></div>");
                        }
                    }
                };
        dataCheckColumn.setFieldUpdater(new FieldUpdater<DashboardDataset,String>() {
            @Override
            public void update(int index, DashboardDataset cruise, String value) {
                // Save the currently selected cruises
                getSelectedDatasets(null);
                // Open the data column specs page for this one cruise
                ArrayList<String> expocodes = new ArrayList<String>(1);
                expocodes.add(cruise.getDatasetId());
                DataColumnSpecsPage.showPage(getUsername(), expocodes);
            }
        });
        return dataCheckColumn;
    }

    /**
     * @return the SOCAT Metadata filename column for the table
     */
    private Column<DashboardDataset,String> buildOmeMetadataColumn() {
        Column<DashboardDataset,String> omeMetadataColumn =
                new Column<DashboardDataset,String>(new ClickableTextCell()) {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        String omeTimestamp = cruise.getOmeTimestamp();
                        if ( omeTimestamp.isEmpty() )
                            omeTimestamp = NO_SOCAT_METADATA_STATUS_STRING;
                        return omeTimestamp;
                    }

                    @Override
                    public void render(Cell.Context ctx, DashboardDataset cruise,
                            SafeHtmlBuilder sb) {
                        sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
                        sb.appendEscaped(getValue(cruise));
                        sb.appendHtmlConstant("</em></u></div>");
                    }
                };
        omeMetadataColumn.setFieldUpdater(new FieldUpdater<DashboardDataset,String>() {
            @Override
            public void update(int index, DashboardDataset cruise, String value) {
                // Save the currently selected cruises
                getSelectedDatasets(null);
                // Show the SOCAT metadata manager page for this one cruise
                checkSet.clear();
                checkSet.setUsername(getUsername());
                checkSet.setManager(isManager);
                checkSet.setImageExtension(imageExtension);
                checkSet.put(cruise.getDatasetId(), cruise);
                EditMetadataPage.showPage(checkSet);
            }
        });
        return omeMetadataColumn;
    }

    /**
     * @return the additional metadata files column for the table
     */
    private Column<DashboardDataset,String> buildAddnDocsColumn() {
        Column<DashboardDataset,String> addnDocsColumn =
                new Column<DashboardDataset,String>(new ClickableTextCell()) {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        TreeSet<String> addlDocTitles = cruise.getAddlDocs();
                        if ( addlDocTitles.size() == 0 )
                            return NO_ADDL_DOCS_STATUS_STRING;
                        SafeHtmlBuilder sb = new SafeHtmlBuilder();
                        boolean firstEntry = true;
                        for (String title : addlDocTitles) {
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
                    public void render(Cell.Context ctx, DashboardDataset cruise,
                            SafeHtmlBuilder sb) {
                        sb.appendHtmlConstant("<div style=\"cursor:pointer;\"><u><em>");
                        sb.appendHtmlConstant(getValue(cruise));
                        sb.appendHtmlConstant("</em></u></div>");
                    }
                };
        addnDocsColumn.setFieldUpdater(new FieldUpdater<DashboardDataset,String>() {
            @Override
            public void update(int index, DashboardDataset cruise, String value) {
                // Save the currently selected cruises (in datasetIdsSet)
                getSelectedDatasets(null);
                // Go to the additional docs page with just this one cruise
                // Go to the QC page after performing the client-side checks on this one cruise
                checkSet.clear();
                checkSet.setUsername(getUsername());
                checkSet.setManager(isManager);
                checkSet.setImageExtension(imageExtension);
                checkSet.put(cruise.getDatasetId(), cruise);
                AddlDocsManagerPage.showPage(checkSet);
            }
        });
        return addnDocsColumn;
    }

    /**
     * @return the version number column for the table
     */
    private TextColumn<DashboardDataset> buildVersionColumn() {
        TextColumn<DashboardDataset> versionColumn =
                new TextColumn<DashboardDataset>() {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        return cruise.getVersion();
                    }
                };
        return versionColumn;
    }

    /**
     * @return the QC submission status column for the table
     */
    private Column<DashboardDataset,String> buildQCStatusColumn() {
        Column<DashboardDataset,String> qcStatusColumn =
                new Column<DashboardDataset,String>(new ClickableTextCell()) {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        return cruise.getSubmitStatus().statusString();
                    }

                    @Override
                    public void render(Cell.Context ctx, DashboardDataset cruise,
                            SafeHtmlBuilder sb) {
                        Boolean editable = cruise.isEditable();
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
        qcStatusColumn.setFieldUpdater(new FieldUpdater<DashboardDataset,String>() {
            @Override
            public void update(int index, DashboardDataset cruise, String value) {
                // Respond only for cruises in this version
                Boolean editable = cruise.isEditable();
                if ( editable != null ) {
                    // Save the currently selected cruises (in datasetIdsSet)
                    getSelectedDatasets(null);
                    // Go to the QC page after performing the client-side checks on this one cruise
                    checkSet.clear();
                    checkSet.setUsername(getUsername());
                    checkSet.setManager(isManager);
                    checkSet.setImageExtension(imageExtension);
                    checkSet.put(cruise.getDatasetId(), cruise);
                    checkDatasetsForSubmitting();
                }
            }
        });
        return qcStatusColumn;
    }

    /**
     * @return the archive submission status column for the table
     */
    private Column<DashboardDataset,String> buildArchiveStatusColumn() {
        Column<DashboardDataset,String> archiveStatusColumn =
                new Column<DashboardDataset,String>(new ClickableTextCell()) {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        String status = cruise.getArchiveStatus();
                        if ( status.isEmpty() )
                            status = NO_ARCHIVE_STATUS_STRING;
                        return status;
                    }

                    @Override
                    public void render(Cell.Context ctx, DashboardDataset cruise,
                            SafeHtmlBuilder sb) {
                        Boolean editable = cruise.isEditable();
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
        archiveStatusColumn.setFieldUpdater(new FieldUpdater<DashboardDataset,String>() {
            @Override
            public void update(int index, DashboardDataset cruise, String value) {
                // Respond only for cruises in this version
                Boolean editable = cruise.isEditable();
                if ( editable != null ) {
                    // Save the currently selected cruises (in datasetIdsSet)
                    getSelectedDatasets(null);
                    // Go to the QC page after performing the client-side checks on this one cruise
                    checkSet.clear();
                    checkSet.setUsername(getUsername());
                    checkSet.setManager(isManager);
                    checkSet.setImageExtension(imageExtension);
                    checkSet.put(cruise.getDatasetId(), cruise);
                    checkDatasetsForSubmitting();
                }
            }
        });
        return archiveStatusColumn;
    }

    /**
     * @return the filename column for the table
     */
    private TextColumn<DashboardDataset> buildFilenameColumn() {
        TextColumn<DashboardDataset> filenameColumn =
                new TextColumn<DashboardDataset>() {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        String uploadFilename = cruise.getUploadFilename();
                        if ( uploadFilename.isEmpty() )
                            uploadFilename = NO_UPLOAD_FILENAME_STRING;
                        return uploadFilename;
                    }
                };
        return filenameColumn;
    }

    /**
     * @return the owner column for the table
     */
    private TextColumn<DashboardDataset> buildOwnerColumn() {
        TextColumn<DashboardDataset> myOwnerColumn =
                new TextColumn<DashboardDataset>() {
                    @Override
                    public String getValue(DashboardDataset cruise) {
                        String owner = cruise.getOwner();
                        if ( owner.isEmpty() )
                            owner = NO_OWNER_STRING;
                        return owner;
                    }
                };
        return myOwnerColumn;
    }

    /**
     * Checks the cruises given in checkSet in this instance for metadata compatibility for submitting for QC.
     * At this time this only checks that an SOCAT metadata document or an additional document is associated with
     * each cruise.
     * <p>
     * Then checks the cruises given in checkSet in this instance for data compatibility for submitting for QC.
     * If the data has not been checked or is unacceptable, this method presents an error message and returns.
     * If the data has many serious issues, asks the user if the submit should be continued.
     * If the answer is yes, or if there were no serious data issues, continues submitting for QC by calling
     * {@link SubmitForQCPage#showPage(DashboardDatasetList)}.
     */
    private void checkDatasetsForSubmitting() {
        // Check if the cruises have metadata documents
        String errMsg = NO_METADATA_HTML_PROLOGUE;
        boolean cannotSubmit = false;
        for (DashboardDataset cruise : checkSet.values()) {
            // At this time, just check that some metadata file exists
            // and do not worry about the contents
            if ( cruise.getOmeTimestamp().isEmpty() &&
                    cruise.getAddlDocs().isEmpty() ) {
                errMsg += "<li>" +
                        SafeHtmlUtils.htmlEscape(cruise.getDatasetId()) + "</li>";
                cannotSubmit = true;
            }
        }

        // If no metadata documents, cannot submit
        if ( cannotSubmit ) {
            errMsg += NO_METADATA_HTML_EPILOGUE;
            UploadDashboard.showMessage(errMsg);
            return;
        }

        // Check that the cruise data is checked and reasonable
        errMsg = CANNOT_SUBMIT_HTML_PROLOGUE;
        String warnMsg = DATA_AUTOFAIL_HTML_PROLOGUE;
        boolean willAutofail = false;
        for (DashboardDataset cruise : checkSet.values()) {
            String status = cruise.getDataCheckStatus();
            if ( status.equals(DashboardUtils.CHECK_STATUS_NOT_CHECKED) ||
                    status.equals(DashboardUtils.CHECK_STATUS_UNACCEPTABLE) ||
                    status.contains(DashboardUtils.GEOPOSITION_ERRORS_MSG) ) {
                errMsg += "<li>" +
                        SafeHtmlUtils.htmlEscape(cruise.getDatasetId()) + "</li>";
                cannotSubmit = true;
            }
            else if ( status.equals(DashboardUtils.CHECK_STATUS_ACCEPTABLE) ||
                    status.startsWith(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX) ||
                    (status.startsWith(DashboardUtils.CHECK_STATUS_ERRORS_PREFIX) &&
                            (cruise.getNumErrorRows() <= DashboardUtils.MAX_ACCEPTABLE_ERRORS)) ) {
                // Acceptable
                ;
            }
            else {
                warnMsg += "<li>" +
                        SafeHtmlUtils.htmlEscape(cruise.getDatasetId()) + "</li>";
                willAutofail = true;
            }
        }

        // If unchecked or very serious data issues, put up error message and stop
        if ( cannotSubmit ) {
            errMsg += CANNOT_SUBMIT_HTML_EPILOGUE;
            UploadDashboard.showMessage(errMsg);
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
                            SubmitForQCPage.showPage(checkSet);
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
        SubmitForQCPage.showPage(checkSet);
    }

}
