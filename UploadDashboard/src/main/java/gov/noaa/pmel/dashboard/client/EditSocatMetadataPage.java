package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;

/**
 * Page for preparing to view and edit standard SOCAT metadata.
 *
 * @author Karl Smith
 */
public class EditSocatMetadataPage extends CompositeWithUsername {

    private static final String TITLE_TEXT = "Edit SOCAT Metadata";
    private static final String WELCOME_INTRO = "Logged in as ";
    private static final String LOGOUT_TEXT = "Logout";
    private static final String SAVE_TEXT = "Save and Continue";
    private static final String DONE_TEXT = "Save and Exit";
    private static final String CANCEL_TEXT = "Exit Without Saving";

    private static final String HTML_INTRO_PROLOGUE = "<p>" +
            "Dataset: <ul><li>";
    private static final String HTML_INTRO_EPILOGUE = "</li></ul></p>";

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
    TabLayoutPanel metaviewTabPanel;
    @UiField
    Button saveButton;
    @UiField
    Button doneButton;
    @UiField
    Button cancelButton;

    private DashboardDataset dataset;

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
        introHtml.setHTML(HTML_INTRO_PROLOGUE + SafeHtmlUtils.htmlEscape(dataset.getDatasetId()) + HTML_INTRO_EPILOGUE);
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

}
