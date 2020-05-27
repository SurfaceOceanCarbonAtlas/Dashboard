package gov.noaa.pmel.dashboard.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import gov.noaa.pmel.dashboard.client.CompositeWithUsername;
import gov.noaa.pmel.dashboard.client.DashboardLogoutPage;
import gov.noaa.pmel.dashboard.client.DatasetListPage;
import gov.noaa.pmel.dashboard.client.SelectSocatMetadataPage;
import gov.noaa.pmel.dashboard.client.UploadDashboard;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.client.metadata.instpanels.InstrumentsTabPanel;
import gov.noaa.pmel.dashboard.client.metadata.pipanels.InvestigatorsTabPanel;
import gov.noaa.pmel.dashboard.client.metadata.pipanels.SubmitterPanel;
import gov.noaa.pmel.dashboard.client.metadata.varpanels.VariablesTabPanel;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.SocatMetadata;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Page for preparing to view and edit standard SOCAT metadata.
 *
 * @author Karl Smith
 */
public class EditSocatMetadataPage extends CompositeWithUsername {

    private static final String TITLE_TEXT = "Edit SOCAT Metadata";
    private static final String WELCOME_INTRO = "Logged in as ";
    private static final String LOGOUT_TEXT = "Logout";

    private static final String RETRIEVE_METADATA_FAIL_MSG = "Failed to retrieve the SOCAT Metadata for ";
    private static final String SAVE_METADATA_FAIL_MSG = "Failed to save the SOCAT Metadata for ";
    private static final String SAVE_METADATA_SUCCESS_MSG = "Successfully saved the SOCAT Metadata for ";

    private static final String SAVE_TEXT = "Save and Continue";
    private static final String DONE_TEXT = "Save and Exit";
    private static final String CANCEL_TEXT = "Exit Without Saving";

    private static final String HTML_INTRO_PROLOGUE = "<p>Dataset: ";
    private static final String HTML_INTRO_EPILOGUE = "</p>";

    public static final String SUBMITTER_TAB_TEXT = "Submitter";
    public static final String INVESTIGATOR_TAB_TEXT = "Investigators";
    public static final String PLATFORM_TAB_TEXT = "Platform";
    public static final String COVERAGE_TAB_TEXT = "Coverage";
    public static final String INSTRUMENTS_TAB_TEXT = "Instruments";
    public static final String VARIABLES_TAB_TEXT = "Data fields";
    public static final String MISC_INFO_TAB_TEXT = "Information";

    private static final String SUBMITTER_TAB_HELP = "Contact information for the submitter of this dataset";
    private static final String INVESTIGATOR_TAB_HELP = "Information about each of the principal investigator of this dataset";
    private static final String PLATFORM_TAB_HELP = "Information about the platform (ship, mooring, etc.) used in this dataset";
    private static final String COVERAGE_TAB_HELP = "Information about the spatial and time coverage of this dataset";
    private static final String INSTRUMENTS_TAB_HELP = "Information about each instrument used in this dataset";
    private static final String VARIABLES_TAB_HELP = "Information about each data field (variable) reported in this dataset";
    private static final String MISC_INFO_TAB_HELP = "Various information about this dataset not describe elsewhere";

    interface EditSocatMetadataPageUiBinder extends UiBinder<Widget,EditSocatMetadataPage> {
    }

    private static final EditSocatMetadataPageUiBinder uiBinder =
            GWT.create(EditSocatMetadataPageUiBinder.class);

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
    Button submitterButton;
    @UiField
    Button investigatorsButton;
    @UiField
    Button platformButton;
    @UiField
    Button coverageButton;
    @UiField
    Button instrumentsButton;
    @UiField
    Button variablesButton;
    @UiField
    Button miscInfoButton;

    @UiField
    DeckLayoutPanel mainPanel;

    @UiField
    Button saveButton;
    @UiField
    Button doneButton;
    @UiField
    Button cancelButton;

    private DashboardDataset dataset;
    private SubmitterPanel submitterPanel;
    private InvestigatorsTabPanel investigatorsPanel;
    private PlatformPanel platformPanel;
    private CoveragePanel coveragePanel;
    private InstrumentsTabPanel instrumentsPanel;
    private VariablesTabPanel variablesPanel;
    private MiscInfoPanel miscInfoPanel;

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

        // Assign the generic text for the category buttons
        submitterButton.setText(SUBMITTER_TAB_TEXT);
        investigatorsButton.setText(INVESTIGATOR_TAB_TEXT);
        platformButton.setText(PLATFORM_TAB_TEXT);
        coverageButton.setText(COVERAGE_TAB_TEXT);
        instrumentsButton.setText(INSTRUMENTS_TAB_TEXT);
        variablesButton.setText(VARIABLES_TAB_TEXT);
        miscInfoButton.setText(MISC_INFO_TAB_TEXT);

        // Assign the hover helps for the category buttons
        submitterButton.setTitle(SUBMITTER_TAB_HELP);
        investigatorsButton.setTitle(INVESTIGATOR_TAB_HELP);
        platformButton.setTitle(PLATFORM_TAB_HELP);
        coverageButton.setTitle(COVERAGE_TAB_HELP);
        instrumentsButton.setTitle(INSTRUMENTS_TAB_HELP);
        variablesButton.setTitle(VARIABLES_TAB_HELP);
        miscInfoButton.setTitle(MISC_INFO_TAB_HELP);

        submitterPanel = null;
        investigatorsPanel = null;
        platformPanel = null;
        coveragePanel = null;
        instrumentsPanel = null;
        variablesPanel = null;
        miscInfoPanel = null;
    }

    /**
     * Display the EditSocatMetadataPage in the RootLayoutPanel for the given dataset.
     * Adds this page to the page history.
     *
     * @param datasetList
     *         ask about editing the metadata for the first dataset in this list
     */
    public static void showPage(DashboardDatasetList datasetList) {
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
    public static void redisplayPage(String username) {
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
        service.getSocatMetadata(getUsername(), dataset.getDatasetId(), new AsyncCallback<SocatMetadata>() {
            @Override
            public void onSuccess(SocatMetadata result) {
                UploadDashboard.showAutoCursor();
                showSocatMetadata(result);

            }

            @Override
            public void onFailure(Throwable ex) {
                UploadDashboard.showAutoCursor();
                UploadDashboard.showFailureMessage(RETRIEVE_METADATA_FAIL_MSG + expo, ex);
                SelectSocatMetadataPage.showPage(datasetList);
            }
        });
    }

    /**
     * Recreate all the panels appropriately for the given SOCAT metadata
     */
    private void showSocatMetadata(SocatMetadata metadata) {
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy MM dd HH mm ss");
        TimeZone timeZone = TimeZone.createTimeZone(0);
        String[] pieces = dateTimeFormat.format(new Date(), timeZone).split(" ");
        Datestamp today = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);


        // Make sure there is at least one PI specified, even if it is completely blank
        ArrayList<Investigator> investigators = metadata.getInvestigators();
        if ( investigators.isEmpty() )
            investigators.add(new Investigator());

        // Make sure there is at least one instrument specified, even if it is completely blank
        ArrayList<Instrument> instruments = metadata.getInstruments();
        if ( instruments.isEmpty() )
            instruments.add(new Instrument());

        // Make sure there is at least one variable specified, even if it is completely blank
        ArrayList<Variable> variables = metadata.getVariables();
        if ( variables.isEmpty() )
            variables.add(new Variable());

        submitterPanel = new SubmitterPanel(dataset, metadata.getSubmitter());
        investigatorsPanel = new InvestigatorsTabPanel(dataset, investigators);
        platformPanel = new PlatformPanel(dataset, metadata.getPlatform());
        coveragePanel = new CoveragePanel(dataset, metadata.getCoverage(), today);
        instrumentsPanel = new InstrumentsTabPanel(dataset, instruments);
        variablesPanel = new VariablesTabPanel(dataset, variables);
        miscInfoPanel = new MiscInfoPanel(dataset, metadata.getMiscInfo());

        mainPanel.clear();
        mainPanel.add(submitterPanel);
        mainPanel.add(investigatorsPanel);
        mainPanel.add(platformPanel);
        mainPanel.add(coveragePanel);
        mainPanel.add(instrumentsPanel);
        mainPanel.add(variablesPanel);
        mainPanel.add(miscInfoPanel);

        investigatorsPanel.showPanel(0);
        instrumentsPanel.showPanel(0);
        variablesPanel.showPanel(0);

        // Highlight the submitterButton and show the submitterPanel
        submitterButton.setFocus(true);
        mainPanel.showWidget(0);
    }

    @UiHandler("submitterButton")
    void submitterButtonOnClick(ClickEvent event) {
        mainPanel.showWidget(0);
    }

    @UiHandler("investigatorsButton")
    void setInvestigatorsButtonOnClick(ClickEvent event) {
        mainPanel.showWidget(1);
    }

    @UiHandler("platformButton")
    void platformButtonOnClick(ClickEvent event) {
        mainPanel.showWidget(2);
    }

    @UiHandler("coverageButton")
    void coverageButtonOnClick(ClickEvent event) {
        mainPanel.showWidget(3);
    }

    @UiHandler("instrumentsButton")
    void instrumentsButtonOnClick(ClickEvent event) {
        mainPanel.showWidget(4);
    }

    @UiHandler("variablesButton")
    void variablesButtonOnClick(ClickEvent event) {
        mainPanel.showWidget(5);
    }

    @UiHandler("miscInfoButton")
    void miscInfoButtonOnClick(ClickEvent event) {
        mainPanel.showWidget(6);
    }

    @UiHandler("logoutButton")
    void logoutOnClick(ClickEvent event) {
        DashboardLogoutPage.showPage();
    }

    @UiHandler("saveButton")
    void saveButtonOnClick(ClickEvent event) {
        // Save the current SocatMetadata on the server but continue working
        saveSocatMetadata(false);
    }

    @UiHandler("doneButton")
    void doneButtonOnClick(ClickEvent event) {
        // Save the current SocatMetadata on the server and exit this page if successful
        saveSocatMetadata(true);
    }

    @UiHandler("cancelButton")
    void cancelButtonOnClick(ClickEvent event) {
        // Return to the cruise list page without save changes
        DatasetListPage.showPage();
    }

    /**
     * Update the metadata from all the associated panels, then send to the server to be saved
     */
    private void saveSocatMetadata(boolean exitOnSuccess) {
        SocatMetadata metadata = new SocatMetadata();
        metadata.setSubmitter(submitterPanel.getUpdatedSumitter());
        metadata.setInvestigators(investigatorsPanel.getUpdatedInvestigators());
        metadata.setPlatform(platformPanel.getUpdatedPlatform());
        metadata.setCoverage(coveragePanel.getUpdatedCoverage());
        metadata.setInstruments(instrumentsPanel.getUpdatedInstruments());
        metadata.setVariables(variablesPanel.getUpdatedVariables());
        metadata.setMiscInfo(miscInfoPanel.getUpdatedMiscInfo());

        UploadDashboard.showWaitCursor();
        String expo = SafeHtmlUtils.htmlEscape(dataset.getDatasetId());
        service.saveSocatMetadata(getUsername(), dataset.getDatasetId(), metadata, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                UploadDashboard.showAutoCursor();
                UploadDashboard.showMessage(SAVE_METADATA_SUCCESS_MSG + expo);
                if ( exitOnSuccess )
                    DatasetListPage.showPage();
            }

            @Override
            public void onFailure(Throwable ex) {
                UploadDashboard.showAutoCursor();
                UploadDashboard.showFailureMessage(SAVE_METADATA_FAIL_MSG + expo, ex);
            }
        });
    }

}
