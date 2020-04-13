package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;
import gov.noaa.pmel.socatmetadata.shared.core.SocatMetadata;

/**
 * Page for preparing to view and edit standard SOCAT metadata.
 *
 * @author Karl Smith
 */
public class EditSocatMetadataPage extends CompositeWithUsername {

    private static final String TITLE_TEXT = "Edit SOCAT Metadata";
    private static final String WELCOME_INTRO = "Logged in as ";
    private static final String LOGOUT_TEXT = "Logout";

    private static final String RETRIEVING_SOCAT_METADATA_MSG = "Retrieving the SOCAT Metadata for ";
    private static final String RETRIEVE_METADATA_FAIL_MSG = "Failed to retrieve the SOCAT Metadata for ";

    private static final String SAVE_TEXT = "Save and Continue";
    private static final String DONE_TEXT = "Save and Exit";
    private static final String CANCEL_TEXT = "Exit Without Saving";

    private static final String HTML_INTRO_PROLOGUE = "<p>Dataset: ";
    private static final String HTML_INTRO_EPILOGUE = "</p>";

    private static final String SUBMITTER_TAB_TEXT = "Submitter";
    private static final String INVESTIGATOR_TAB_TEXT = "Investigators";
    private static final String PLATFORM_TAB_TEXT = "Platform";
    private static final String COVERAGE_TAB_TEXT = "Coverage";
    private static final String VARIABLES_TAB_TEXT = "Data fields";
    private static final String INSTRUMENTS_TAB_TEXT = "Instruments";
    private static final String MISC_INFO_TAB_TEXT = "Information";

    private static final String SUBMITTER_TAB_HELP = "Contact information for the submitter of this dataset";
    private static final String INVESTIGATOR_TAB_HELP = "Information about each of the principal investigator of this dataset";
    private static final String PLATFORM_TAB_HELP = "Information about the platform (ship, mooring, etc.) used in this dataset";
    private static final String COVERAGE_TAB_HELP = "Information about the spatial and time coverage of this dataset";
    private static final String VARIABLES_TAB_HELP = "Information about each data field reported in this dataset";
    private static final String INSTRUMENTS_TAB_HELP = "Information about each instrument used in this dataset";
    private static final String MISC_INFO_TAB_HELP = "Various information about this dataset not describe elsewhere";

    private static final String ADD_TEXT = "Add another";
    private static final String REMOVE_TEXT = "Remove current";

    private static final String ADD_INVESTIGATOR_HELP = "Adds a new principal investigator description to the list";
    private static final String REMOVE_INVESTIGATOR_HELP = "Removes the currently displayed principal investigator description";

    private static final String ADD_VARIABLE_HELP = "Adds a new data field description to the list";
    private static final String REMOVE_VARIABLE_HELP = "Removes the currently displayed data field description";

    private static final String ADD_INSTRUMENT_HELP = "Adds a new instrument description to the list";
    private static final String REMOVE_INSTRUMENT_HELP = "Removes the currently displayed instrument description";

    interface EditSocatMetadataPageUiBinder extends UiBinder<Widget,EditSocatMetadataPage> {
    }

    private static EditSocatMetadataPageUiBinder uiBinder =
            GWT.create(EditSocatMetadataPageUiBinder.class);

    private static DashboardServicesInterfaceAsync service =
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
    HTML submitterHtml;
    @UiField
    FlowPanel submitterPanel;

    @UiField
    HTML pisHtml;
    @UiField
    Button pisAddButton;
    @UiField
    Button pisRemoveButton;
    @UiField
    StackLayoutPanel pisPanel;

    @UiField
    HTML platformHtml;
    @UiField
    FlowPanel platformPanel;

    @UiField
    HTML coverageHtml;
    @UiField
    FlowPanel coveragePanel;

    @UiField
    HTML varsHtml;
    @UiField
    Button varsAddButton;
    @UiField
    Button varsRemoveButton;
    @UiField
    StackLayoutPanel varsPanel;

    @UiField
    HTML instsHtml;
    @UiField
    Button instsAddButton;
    @UiField
    Button instsRemoveButton;
    @UiField
    StackLayoutPanel instsPanel;

    @UiField
    HTML miscInfoHtml;
    @UiField
    FlowPanel miscInfoPanel;

    @UiField
    Button saveButton;
    @UiField
    Button doneButton;
    @UiField
    Button cancelButton;

    private DashboardDataset dataset;
    private SocatMetadata metadata;

    // Singleton instance of this page
    private static EditSocatMetadataPage singleton;

    EditSocatMetadataPage() {
        initWidget(uiBinder.createAndBindUi(this));
        singleton = this;

        setUsername(null);
        dataset = null;

        titleLabel.setText(TITLE_TEXT);
        logoutButton.setText(LOGOUT_TEXT);

        saveButton.setText(SAVE_TEXT);
        doneButton.setText(DONE_TEXT);
        cancelButton.setText(CANCEL_TEXT);

        // Assign the HTML for the tabs
        submitterHtml.setHTML(SUBMITTER_TAB_TEXT);
        pisHtml.setHTML(INVESTIGATOR_TAB_TEXT);
        platformHtml.setHTML(PLATFORM_TAB_TEXT);
        coverageHtml.setHTML(COVERAGE_TAB_TEXT);
        varsHtml.setHTML(VARIABLES_TAB_TEXT);
        instsHtml.setHTML(INSTRUMENTS_TAB_TEXT);
        miscInfoHtml.setHTML(MISC_INFO_TAB_TEXT);

        // Assign the hover helps for the tabs
        submitterHtml.setTitle(SUBMITTER_TAB_HELP);
        pisHtml.setTitle(INVESTIGATOR_TAB_HELP);
        platformHtml.setTitle(PLATFORM_TAB_HELP);
        coverageHtml.setTitle(COVERAGE_TAB_HELP);
        varsHtml.setTitle(VARIABLES_TAB_HELP);
        instsHtml.setTitle(INSTRUMENTS_TAB_HELP);
        miscInfoHtml.setTitle(MISC_INFO_TAB_HELP);

        // Assign the labels and hover helps for add and remove buttons in the stacks panel tabs
        pisAddButton.setHTML(ADD_TEXT);
        pisAddButton.setTitle(ADD_INVESTIGATOR_HELP);
        pisRemoveButton.setHTML(REMOVE_TEXT);
        pisRemoveButton.setTitle(REMOVE_INVESTIGATOR_HELP);

        varsAddButton.setHTML(ADD_TEXT);
        varsAddButton.setTitle(ADD_VARIABLE_HELP);
        varsRemoveButton.setHTML(REMOVE_TEXT);
        varsRemoveButton.setTitle(REMOVE_VARIABLE_HELP);

        instsAddButton.setHTML(ADD_TEXT);
        instsAddButton.setTitle(ADD_INSTRUMENT_HELP);
        instsRemoveButton.setHTML(REMOVE_TEXT);
        instsRemoveButton.setTitle(REMOVE_INSTRUMENT_HELP);
    }

    /**
     * Display the EditSocatMetadataPage in the RootLayoutPanel for the given dataset.
     * Adds this page to the page history.
     *
     * @param datasetList
     *         ask about editing the metadata for the first dataset in this list
     */
    static void showPage(DashboardDatasetList datasetList) {
        if ( singleton == null )
            singleton = new EditSocatMetadataPage();
        singleton.updateDataset(datasetList);
        UploadDashboard.updateCurrentPage(singleton);
        History.newItem(PagesEnum.EDIT_SOCAT_METADATA.name(), false);
    }

    /**
     * Redisplays the last version of this page if the username associated with this page matches the given
     * username.
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

    /**
     * Updates this page with the username and the first dataset in the given dataset list.
     *
     * @param datasetList
     *         ask about editing the metadata for the first dataset in this list
     */
    private void updateDataset(DashboardDatasetList datasetList) {
        // Update the current username
        setUsername(datasetList.getUsername());
        userInfoLabel.setText(WELCOME_INTRO + getUsername());

        // Update the cruise associated with this page
        dataset = datasetList.values().iterator().next();

        // Update the HTML intro naming the cruise
        String expo = SafeHtmlUtils.htmlEscape(dataset.getDatasetId());
        introHtml.setHTML(HTML_INTRO_PROLOGUE + expo + HTML_INTRO_EPILOGUE);

        UploadDashboard.showWaitCursor();
        UploadDashboard.showMessage(RETRIEVING_SOCAT_METADATA_MSG + expo);
        service.getSocatMetadata(getUsername(), dataset.getDatasetId(), new AsyncCallback<SocatMetadata>() {
            @Override
            public void onSuccess(SocatMetadata result) {
                UploadDashboard.showAutoCursor();
                metadata = (SocatMetadata) (result.duplicate(null));
                showSocatMetadata();
            }

            @Override
            public void onFailure(Throwable ex) {
                UploadDashboard.showAutoCursor();
                UploadDashboard.showFailureMessage(RETRIEVE_METADATA_FAIL_MSG + expo, ex);
                SelectSocatMetadataPage.showPage(datasetList);
            }
        });
    }

    @UiHandler("logoutButton")
    void logoutOnClick(ClickEvent event) {
        DashboardLogoutPage.showPage();
    }

    @UiHandler("saveButton")
    void saveButtonOnClick(ClickEvent event) {
        // Save the current SocatMetadata on the server but continue working
        saveSocatMetadata();
    }

    @UiHandler("doneButton")
    void doneButtonOnClick(ClickEvent event) {
        // Save the current SocatMetadata on the server and exit this page
        saveSocatMetadata();
        DatasetListPage.showPage();
    }

    @UiHandler("cancelButton")
    void cancelButtonOnClick(ClickEvent event) {
        // Return to the cruise list page without save changes
        DatasetListPage.showPage();
    }

    private void showSocatMetadata() {
        //TODO: implement
    }

    private void saveSocatMetadata() {
        //TODO: implement
    }

}
