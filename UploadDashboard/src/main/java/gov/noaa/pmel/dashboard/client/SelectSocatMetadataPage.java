package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.client.metadata.EditSocatMetadataPage;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Page for selecting the standard SOCAT metadata to view and edit.
 *
 * @author Karl Smith
 */
public class SelectSocatMetadataPage extends CompositeWithUsername {

    private static final String TITLE_TEXT = "Select SOCAT Metadata Source";
    private static final String WELCOME_INTRO = "Logged in as ";
    private static final String LOGOUT_TEXT = "Logout";
    private static final String SOURCE_CAPTION_TITLE = "Source of SOCAT Metadata";
    private static final String EDIT_TEXT = "View/Edit";
    private static final String CANCEL_TEXT = "Cancel";

    private static final String HTML_INTRO_PROLOGUE = "<p>Dataset: <ul><li>";
    private static final String HTML_INTRO_EPILOGUE = "</li></ul></p>";

    private static final String EXISTING_RADIO_TEXT_PROLOGUE = " Use existing SOCAT metadata (";
    private static final String EXISTING_RADIO_TEXT_EPILOGUE = ")";
    private static final String EXISTING_IS_STUB_TEXT = "minimal metadata derived from data";
    private static final String EXISTING_LAST_MODIFIED = "last modified ";

    private static final String COPY_RADIO_TEXT = " Derive from SOCAT metadata for dataset: ";

    private static final String GET_DATASET_IDS_FAILURE_MSG = "Unexpected failure to retrieve the list of expocodes";
    private static final String NO_COPY_SELECTION_MADE = "Please select an expocode from which to copy metadata";

    private static final String NO_SELECTION_MSG = "Please select a source of the metadata to view";

    private static final String UPLOAD_RADIO_TEXT = " Generate from uploaded metadata XML file: ";

    private static final String NO_FILE_ERROR_MSG = "Please select a metadata XML file to upload";

    private static final String OVERWRITE_WARNING_MSG = "<h3>The SOCAT metadata for this dataset will be overwritten./h3>" +
            "<p>Do you wish to proceed?</p>";
    private static final String OVERWRITE_YES_TEXT = "Yes";
    private static final String OVERWRITE_NO_TEXT = "No";

    private static final String COPY_FAILURE_MSG = "Copy of metadata failed: ";

    private static final String UNEXPLAINED_FAIL_MSG = "<h3>Upload failed.</h3>" +
            "<p>Unexpectedly, no explanation of the failure was given</p>";
    private static final String EXPLAINED_FAIL_MSG_START = "<h3>Upload failed.</h3><p><pre>\n";
    private static final String EXPLAINED_FAIL_MSG_END = "</pre></p>";

    interface SelectSocatMetadataPageUiBinder extends UiBinder<Widget,SelectSocatMetadataPage> {
    }

    private static final SelectSocatMetadataPageUiBinder uiBinder =
            GWT.create(SelectSocatMetadataPageUiBinder.class);

    private static final DashboardServicesInterfaceAsync service =
            GWT.create(DashboardServicesInterface.class);

    @UiField
    InlineLabel titleLabel;
    @UiField
    InlineLabel userInfoLabel;
    @UiField
    Button logoutButton;
    @UiField
    HTML introHtml;
    @UiField
    CaptionPanel sourceCaption;
    @UiField
    RadioButton existingRadio;
    @UiField
    RadioButton copyRadio;
    @UiField
    ListBox metadataListBox;
    @UiField
    RadioButton uploadRadio;
    @UiField
    FormPanel uploadForm;
    @UiField
    FileUpload xmlUpload;
    @UiField
    Hidden timestampToken;
    @UiField
    Hidden datasetIdsToken;
    @UiField
    Hidden omeToken;
    @UiField
    Button editButton;
    @UiField
    Button cancelButton;

    private DashboardDataset cruise;
    private String lastDatasetId;
    private DashboardAskPopup askOverwritePopup;
    private ArrayList<String> datasetIdsList;


    // Singleton instance of this page
    private static SelectSocatMetadataPage singleton;

    SelectSocatMetadataPage() {
        initWidget(uiBinder.createAndBindUi(this));
        singleton = this;

        setUsername(null);
        cruise = null;
        lastDatasetId = "";
        askOverwritePopup = null;

        titleLabel.setText(TITLE_TEXT);
        logoutButton.setText(LOGOUT_TEXT);

        sourceCaption.setCaptionText(SOURCE_CAPTION_TITLE);
        copyRadio.setText(COPY_RADIO_TEXT);
        uploadRadio.setText(UPLOAD_RADIO_TEXT);

        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.setMethod(FormPanel.METHOD_POST);
        uploadForm.setAction(GWT.getModuleBaseURL() + "MetadataUploadService");

        datasetIdsList = null;
        clearTokens();

        editButton.setText(EDIT_TEXT);
        cancelButton.setText(CANCEL_TEXT);
    }

    /**
     * Display the SelectSocatMetadataPage in the RootLayoutPanel for the given dataset.
     * Adds this page to the page history.
     *
     * @param cruises
     *         ask about editing the metadata for the first dataset in this set
     */
    public static void showPage(DashboardDatasetList cruises) {
        if ( singleton == null )
            singleton = new SelectSocatMetadataPage();
        singleton.updateDataset(cruises);
        UploadDashboard.updateCurrentPage(singleton);
        History.newItem(PagesEnum.SELECT_SOCAT_METADATA.name(), false);
    }

    /**
     * Re-displays the last version of this page if the username associated with this page matches the given username.
     */
    public static void redisplayPage(String username) {
        if ( (username == null) || username.isEmpty() || (singleton == null) ||
                !singleton.getUsername().equals(username) ) {
            DatasetListPage.showPage();
        }
        else {
            UploadDashboard.updateCurrentPage(singleton);
        }
    }

    /**
     * Updates this page with the username and the first dataset in the given dataset set.
     *
     * @param cruises
     *         ask about editing the metadata for the first dataset in this set
     */
    private void updateDataset(DashboardDatasetList cruises) {
        // Update the current username
        setUsername(cruises.getUsername());
        userInfoLabel.setText(WELCOME_INTRO + getUsername());

        // Record the expocode of the last dataset as a first guess for copying metadata.
        if ( cruise != null )
            lastDatasetId = cruise.getDatasetId();

        // Update the cruise associated with this page
        cruise = cruises.values().iterator().next();

        // Update the HTML intro naming the cruise
        introHtml.setHTML(HTML_INTRO_PROLOGUE + SafeHtmlUtils.htmlEscape(cruise.getDatasetId()) + HTML_INTRO_EPILOGUE);

        // Briefly describe existing metadata
        String omeTimestamp = cruise.getOmeTimestamp();
        if ( omeTimestamp.isEmpty() )
            existingRadio.setText(EXISTING_RADIO_TEXT_PROLOGUE +
                    EXISTING_IS_STUB_TEXT + EXISTING_RADIO_TEXT_EPILOGUE);
        else
            existingRadio.setText(EXISTING_RADIO_TEXT_PROLOGUE +
                    EXISTING_LAST_MODIFIED + omeTimestamp + EXISTING_RADIO_TEXT_EPILOGUE);

        // Initialize the list box of dataset IDs for coping metadata, but only if the list of dataset IDs exist.
        boolean selectionMade = selectCopyMetadataDatasetId(lastDatasetId);

        // Use existing metadata unless only have data-derived metadata and a selection was made for copying metadata
        if ( selectionMade && omeTimestamp.isEmpty() )
            copyRadio.setValue(true, true);
        else
            existingRadio.setValue(true, true);

        // Clear the hidden tokens just to be safe
        clearTokens();
    }

    /**
     * Select the given dataset ID in the list box of dataset IDs for copying metadata.
     * If the list of dataset IDs does not yet exist, false is returned; this method
     * does not trigger retrieval of the list of dataset IDs.
     *
     * @param datasetId
     *         dataset ID to select; cannot be null but can be empty
     *
     * @return true if the dataset ID was found in the list of dataset IDs; otherwise false
     */
    private boolean selectCopyMetadataDatasetId(String datasetId) {
        if ( (datasetIdsList != null) && !datasetId.isEmpty() ) {
            int idx = datasetIdsList.indexOf(lastDatasetId);
            if ( idx >= 0 ) {
                metadataListBox.setSelectedIndex(idx);
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all the Hidden tokens on the page.
     */
    private void clearTokens() {
        timestampToken.setValue("");
        datasetIdsToken.setValue("");
        omeToken.setValue("");
    }


    /**
     * Assigns all the Hidden tokens on the page.
     */
    private void assignTokens() {
        String localTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z").format(new Date());
        timestampToken.setValue(localTimestamp);
        datasetIdsToken.setValue("[ \"" + cruise.getDatasetId() + "\" ]");
        omeToken.setValue("true");
    }

    @UiHandler("copyRadio")
    void copyRadioOnClick(ClickEvent event) {
        if ( datasetIdsList == null ) {
            // Make the call to the server to retrieve the complete list of dataset IDs
            UploadDashboard.showWaitCursor();
            service.getAllDatasetIdsForMetadata(getUsername(), new AsyncCallback<ArrayList<String>>() {
                @Override
                public void onSuccess(ArrayList<String> result) {
                    UploadDashboard.showAutoCursor();
                    datasetIdsList = new ArrayList<String>(result);
                    // Now that the list of dataset IDs exists, try to initialize the list box to the last dataset ID
                    selectCopyMetadataDatasetId(lastDatasetId);
                }

                @Override
                public void onFailure(Throwable ex) {
                    UploadDashboard.showAutoCursor();
                    UploadDashboard.showFailureMessage(GET_DATASET_IDS_FAILURE_MSG, ex);
                    existingRadio.setValue(true, true);
                }
            });
        }
    }

    @UiHandler("logoutButton")
    void logoutOnClick(ClickEvent event) {
        DashboardLogoutPage.showPage();
    }

    @UiHandler("cancelButton")
    void cancelButtonOnClick(ClickEvent event) {
        // Return to the cruise list page which might have been updated
        DatasetListPage.showPage();
    }

    @UiHandler("editButton")
    void editButtonOnClick(ClickEvent event) {
        if ( existingRadio.getValue() ) {
            showMetadataForDataset();
        }
        else if ( copyRadio.getValue() ) {
            // If an overwrite will occur, ask for confirmation
            if ( !cruise.getOmeTimestamp().isEmpty() ) {
                if ( askOverwritePopup == null ) {
                    askOverwritePopup = new DashboardAskPopup(OVERWRITE_YES_TEXT, OVERWRITE_NO_TEXT,
                            new AsyncCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    // Submit only if yes
                                    if ( result == Boolean.TRUE ) {
                                        copyMetadataFromSelectedIdAndShow();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable ex) {
                                    // Never called
                                }
                            });
                }
                askOverwritePopup.askQuestion(OVERWRITE_WARNING_MSG);
                return;
            }
            copyMetadataFromSelectedIdAndShow();
        }
        else if ( uploadRadio.getValue() ) {
            // Make sure a file was selected
            String uploadFilename = DashboardUtils.baseName(xmlUpload.getFilename());
            if ( uploadFilename.isEmpty() ) {
                UploadDashboard.showMessage(NO_FILE_ERROR_MSG);
                return;
            }

            // If an overwrite will occur, ask for confirmation
            if ( !cruise.getOmeTimestamp().isEmpty() ) {
                if ( askOverwritePopup == null ) {
                    askOverwritePopup = new DashboardAskPopup(OVERWRITE_YES_TEXT, OVERWRITE_NO_TEXT,
                            new AsyncCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    // Submit only if yes
                                    if ( result == Boolean.TRUE ) {
                                        assignTokens();
                                        uploadForm.submit();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable ex) {
                                    // Never called
                                }
                            });
                }
                askOverwritePopup.askQuestion(OVERWRITE_WARNING_MSG);
                return;
            }

            // Nothing overwritten, submit the form
            assignTokens();
            uploadForm.submit();
        }
        else {
            UploadDashboard.showMessage(NO_SELECTION_MSG);
        }
    }

    /**
     * Request that the SocatMetadata for the selected ID be copied to as the SocatMetadata for the current ID.
     * If successful, show this new SocatMetadata for the current ID.
     */
    private void copyMetadataFromSelectedIdAndShow() {
        String selectedDatasetId = metadataListBox.getSelectedItemText();
        if ( selectedDatasetId == null ) {
            UploadDashboard.showMessage(NO_COPY_SELECTION_MADE);
            return;
        }
        UploadDashboard.showWaitCursor();
        service.copySocatMetadata(getUsername(), cruise.getDatasetId(), selectedDatasetId, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                UploadDashboard.showAutoCursor();
                showMetadataForDataset();
            }

            @Override
            public void onFailure(Throwable ex) {
                UploadDashboard.showAutoCursor();
                UploadDashboard.showFailureMessage(COPY_FAILURE_MSG, ex);
            }
        });
    }

    @UiHandler("uploadForm")
    void uploadFormOnSubmit(SubmitEvent event) {
        UploadDashboard.showWaitCursor();
    }

    @UiHandler("uploadForm")
    void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
        UploadDashboard.showAutoCursor();
        clearTokens();
        processResultMsg(event.getResults());
    }

    /**
     * Process the message returned from the upload of a dataset.
     *
     * @param resultMsg
     *         message returned from the upload of a dataset
     */
    private void processResultMsg(String resultMsg) {
        if ( resultMsg == null ) {
            UploadDashboard.showMessage(UNEXPLAINED_FAIL_MSG);
            return;
        }
        resultMsg = resultMsg.trim();
        if ( resultMsg.startsWith(DashboardUtils.SUCCESS_HEADER_TAG) ) {
            // Metadata file successfully uploaded
            // Display the SocatMetadata derived from the XML
            showMetadataForDataset();
        }
        else {
            // Unknown response, just display the entire message
            UploadDashboard.showMessage(EXPLAINED_FAIL_MSG_START +
                    SafeHtmlUtils.htmlEscape(resultMsg) + EXPLAINED_FAIL_MSG_END);
        }
    }

    /**
     * Show the pages for viewing and editing the SocatMetadata for the current dataset.
     */
    private void showMetadataForDataset() {
        DashboardDatasetList datasetList = new DashboardDatasetList();
        datasetList.setUsername(getUsername());
        datasetList.put(cruise.getDatasetId(), cruise);
        EditSocatMetadataPage.showPage(datasetList);
    }

}
