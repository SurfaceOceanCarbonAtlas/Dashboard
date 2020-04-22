package gov.noaa.pmel.dashboard.client;

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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
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
    SimpleLayoutPanel submitterSLPanel;

    @UiField
    HTML investigatorsHtml;
    @UiField
    Button investigatorAddButton;
    @UiField
    Button investigatorRemoveButton;
    @UiField
    TabLayoutPanel investigatorsTLPanel;

    @UiField
    HTML platformHtml;
    @UiField
    SimpleLayoutPanel platformSLPanel;

    @UiField
    HTML coverageHtml;
    @UiField
    SimpleLayoutPanel coverageSLPanel;

    @UiField
    HTML varsHtml;
    @UiField
    Button varAddButton;
    @UiField
    Button varRemoveButton;
    @UiField
    TabLayoutPanel varsTLPanel;

    @UiField
    HTML instsHtml;
    @UiField
    Button instAddButton;
    @UiField
    Button instRemoveButton;
    @UiField
    TabLayoutPanel instsTLPanel;

    @UiField
    HTML miscInfoHtml;
    @UiField
    SimpleLayoutPanel miscInfoSLPanel;

    @UiField
    Button saveButton;
    @UiField
    Button doneButton;
    @UiField
    Button cancelButton;

    private DashboardDataset dataset;
    private SubmitterPanel submitterPanel;
    private final ArrayList<InvestigatorPanel> piPanels;
    private PlatformPanel platformPanel;
    private CoveragePanel coveragePanel;
    private final ArrayList<VariablePanel> varPanels;
    private final ArrayList<InstrumentPanel> instPanels;
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

        // Assign the HTML for the tabs
        submitterHtml.setHTML(SUBMITTER_TAB_TEXT);
        investigatorsHtml.setHTML(INVESTIGATOR_TAB_TEXT);
        platformHtml.setHTML(PLATFORM_TAB_TEXT);
        coverageHtml.setHTML(COVERAGE_TAB_TEXT);
        varsHtml.setHTML(VARIABLES_TAB_TEXT);
        instsHtml.setHTML(INSTRUMENTS_TAB_TEXT);
        miscInfoHtml.setHTML(MISC_INFO_TAB_TEXT);

        // Assign the hover helps for the tabs
        submitterHtml.setTitle(SUBMITTER_TAB_HELP);
        investigatorsHtml.setTitle(INVESTIGATOR_TAB_HELP);
        platformHtml.setTitle(PLATFORM_TAB_HELP);
        coverageHtml.setTitle(COVERAGE_TAB_HELP);
        varsHtml.setTitle(VARIABLES_TAB_HELP);
        instsHtml.setTitle(INSTRUMENTS_TAB_HELP);
        miscInfoHtml.setTitle(MISC_INFO_TAB_HELP);

        // Assign the labels and hover helps for add and remove buttons in the stacks panel tabs
        investigatorAddButton.setHTML(ADD_TEXT);
        investigatorAddButton.setTitle(ADD_INVESTIGATOR_HELP);
        investigatorRemoveButton.setHTML(REMOVE_TEXT);
        investigatorRemoveButton.setTitle(REMOVE_INVESTIGATOR_HELP);

        varAddButton.setHTML(ADD_TEXT);
        varAddButton.setTitle(ADD_VARIABLE_HELP);
        varRemoveButton.setHTML(REMOVE_TEXT);
        varRemoveButton.setTitle(REMOVE_VARIABLE_HELP);

        instAddButton.setHTML(ADD_TEXT);
        instAddButton.setTitle(ADD_INSTRUMENT_HELP);
        instRemoveButton.setHTML(REMOVE_TEXT);
        instRemoveButton.setTitle(REMOVE_INSTRUMENT_HELP);

        // Create the panel ArrayList
        piPanels = new ArrayList<InvestigatorPanel>();
        varPanels = new ArrayList<VariablePanel>();
        instPanels = new ArrayList<InstrumentPanel>();
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

        submitterPanel = new SubmitterPanel(metadata.getSubmitter());
        submitterSLPanel.setWidget(submitterPanel);

        // Make sure there is at least one PI specified, even if it is completely blank
        ArrayList<Investigator> investigators = metadata.getInvestigators();
        if ( investigators.isEmpty() )
            investigators.add(new Investigator());

        piPanels.clear();
        investigatorsTLPanel.clear();
        for (Investigator pi : investigators) {
            Label header = new Label();
            InvestigatorPanel panel = new InvestigatorPanel(pi, header);
            piPanels.add(panel);
            investigatorsTLPanel.add(panel, header);
        }
        investigatorsTLPanel.selectTab(0);

        platformPanel = new PlatformPanel(metadata.getPlatform());
        platformSLPanel.setWidget(platformPanel);

        coveragePanel = new CoveragePanel(metadata.getCoverage(), today);
        coverageSLPanel.setWidget(coveragePanel);

        // Make sure there is at least one variable specified, even if it is completely blank
        ArrayList<Variable> variables = metadata.getVariables();
        if ( variables.isEmpty() )
            variables.add(new Variable());

        varPanels.clear();
        varsTLPanel.clear();
        for (Variable var : variables) {
            Label header = new Label();
            VariablePanel panel = new VariablePanel(var, header);
            varPanels.add(panel);
            varsTLPanel.add(panel, header);
        }
        varsTLPanel.selectTab(0);

        // Make sure there is at least one instrument specified, even if it is completely blank
        ArrayList<Instrument> instruments = metadata.getInstruments();
        if ( instruments.isEmpty() )
            instruments.add(new Instrument());

        instPanels.clear();
        instsTLPanel.clear();
        for (Instrument inst : instruments) {
            Label header = new Label();
            InstrumentPanel panel = new InstrumentPanel(inst, header);
            instPanels.add(panel);
            instsTLPanel.add(panel, header);
        }
        instsTLPanel.selectTab(0);

        miscInfoPanel = new MiscInfoPanel(metadata.getMiscInfo());
        miscInfoSLPanel.setWidget(miscInfoPanel);
    }

    @UiHandler("investigatorAddButton")
    void addPiOnClick(ClickEvent event) {
        // Copy the last investigator information as a first guess, but remove the name and ID
        int numPanels = piPanels.size();
        Investigator pi = (Investigator) (piPanels.get(numPanels - 1).getUpdatedInvestigator().duplicate(null));
        pi.setLastName(null);
        pi.setFirstName(null);
        pi.setMiddle(null);
        pi.setId(null);
        Label header = new Label();
        InvestigatorPanel panel = new InvestigatorPanel(pi, header);
        piPanels.add(panel);
        investigatorsTLPanel.add(panel, header);
        investigatorsTLPanel.selectTab(numPanels);
    }

    @UiHandler("investigatorRemoveButton")
    void removePiOnClick(ClickEvent event) {
        if ( piPanels.size() < 2 ) {
            UploadDashboard.showMessage("Cannot have less than one principal investigator description");
            return;
        }
        int idx = investigatorsTLPanel.getSelectedIndex();
        if ( idx < 0 ) {
            UploadDashboard.showMessage("No principal investigator description selected");
            return;
        }
        piPanels.remove(idx);
        investigatorsTLPanel.remove(idx);
        // show what was the next panel if it exists; otherwise the last panel
        if ( idx == piPanels.size() )
            idx--;
        investigatorsTLPanel.selectTab(idx);
    }

    @UiHandler("varAddButton")
    void addVarOnClick(ClickEvent event) {
        // Copy the last variable information as a first guess, but remove the name
        int numPanels = varPanels.size();
        Variable var = (Variable) (varPanels.get(numPanels - 1).getUpdatedVariable().duplicate(null));
        var.setColName(null);
        Label header = new Label();
        VariablePanel panel = new VariablePanel(var, header);
        varPanels.add(panel);
        varsTLPanel.add(panel, header);
        varsTLPanel.selectTab(numPanels);
    }

    @UiHandler("varRemoveButton")
    void removeVarOnClick(ClickEvent event) {
        if ( varPanels.size() < 2 ) {
            UploadDashboard.showMessage("Cannot have less than one data field description");
            return;
        }
        int idx = varsTLPanel.getSelectedIndex();
        if ( idx < 0 ) {
            UploadDashboard.showMessage("No data field description selected");
            return;
        }
        varPanels.remove(idx);
        varsTLPanel.remove(idx);
        // show what was the next panel if it exists; otherwise the last panel
        if ( idx == varPanels.size() )
            idx--;
        varsTLPanel.selectTab(idx);
    }

    @UiHandler("instAddButton")
    void addInstOnClick(ClickEvent event) {
        // Copy the last instrument information as a first guess, but remove the name
        int numPanels = instPanels.size();
        Instrument inst = (Instrument) (instPanels.get(numPanels - 1).getUpdatedInstrument().duplicate(null));
        inst.setName(null);
        Label header = new Label();
        InstrumentPanel panel = new InstrumentPanel(inst, header);
        instPanels.add(panel);
        instsTLPanel.add(panel, header);
        instsTLPanel.selectTab(numPanels);
    }

    @UiHandler("instRemoveButton")
    void removeInstOnClick(ClickEvent event) {
        if ( instPanels.size() < 2 ) {
            UploadDashboard.showMessage("Cannot have less than one instrument description");
            return;
        }
        int idx = instsTLPanel.getSelectedIndex();
        if ( idx < 0 ) {
            UploadDashboard.showMessage("No instrument description selected");
            return;
        }
        instPanels.remove(idx);
        instsTLPanel.remove(idx);
        // show what was the next panel if it exists; otherwise the last panel
        if ( idx == instPanels.size() )
            idx--;
        instsTLPanel.selectTab(idx);
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
        ArrayList<Investigator> investigators = new ArrayList<Investigator>(piPanels.size());
        for (InvestigatorPanel panel : piPanels) {
            investigators.add(panel.getUpdatedInvestigator());
        }
        metadata.setInvestigators(investigators);
        metadata.setPlatform(platformPanel.getUpdatedPlatform());
        metadata.setCoverage(coveragePanel.getUpdatedCoverage());
        ArrayList<Variable> variables = new ArrayList<Variable>(varPanels.size());
        for (VariablePanel panel : varPanels) {
            variables.add(panel.getUpdatedVariable());
        }
        metadata.setVariables(variables);
        ArrayList<Instrument> instruments = new ArrayList<Instrument>(instPanels.size());
        for (InstrumentPanel panel : instPanels) {
            instruments.add(panel.getUpdatedInstrument());
        }
        metadata.setInstruments(instruments);
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
