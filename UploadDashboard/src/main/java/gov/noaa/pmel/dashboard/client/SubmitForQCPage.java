/**
 *
 */
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Page for submitting cruises for QC.
 *
 * @author Karl Smith
 */
public class SubmitForQCPage extends CompositeWithUsername {

    private static final String TITLE_TEXT = "Submit Datasets for QC / Manage Archival";
    private static final String WELCOME_INTRO = "Logged in as ";
    private static final String LOGOUT_TEXT = "Logout";
    private static final String MORE_INFO_TEXT = "more ...";

    private static final String INTRO_HTML_PROLOGUE =
            "Datasets: <ul>";
    private static final String INTRO_HTML_EPILOGUE =
            "</ul>";

    private static final String CRUISE_INFO_PROLOGUE =
            "&nbsp;&nbsp;&nbsp;&nbsp;<em>(";
    private static final String QC_STATUS_INTRO =
            "QC status: ";
    private static final String ARCHIVE_STATUS_INTRO =
            "Archive request sent on ";
    private static final String CRUISE_INFO_EPILOGUE =
            ")</em>";

    private static final String ARCHIVE_PLAN_INTRO =
            "Archival plan for the uploaded files for these datasets: <br />" +
                    "<small><em>(this option can be modified on submitted datasets without affecting QC)</em></small>";

    private static final String ARCHIVE_LATER_TEXT =
            "delay archiving at this time";
    private static final String ARCHIVE_LATER_ADDN_HTML =
            "<em>(if not archived before the next public release, archive at OCADS)</em>";
    private static final String ARCHIVE_LATER_INFO_HTML =
            "By selecting this option I wish to delay archival at this time.  " +
                    "If another archive option has not been selected before the next " +
                    "public release, I am giving permission for my uploaded files for these " +
                    "datasets, if deemed acceptable, to be archived at OCADS (formerly CDIAC) " +
                    "at the time of the next public release, after which the files will " +
                    "be made accessible to the public through the OCADS web site.";

    private static final String ARCHIVE_NOW_TEXT =
            "archive at OCADS (formerly CDIAC) now";
    private static final String ARCHIVE_NOW_ADDN_HTML =
            "<em>(an e-mail with the data and metadata files will be sent to OCADS)</em>";
    private static final String ARCHIVE_NOW_INFO_HTML =
            "By selecting this option I am requesting that my uploaded files for " +
                    "these datasets be archived at OCADS (formerly CDIAC) as soon as possible.  " +
                    "When OCADS provides a DOI, or other reference, for these archived files, " +
                    "please verify these references are in the metadata for these datasets.";

    private static final String OWNER_ARCHIVE_TEXT =
            "already archived or I will manage archival";
    private static final String OWNER_ARCHIVE_ADDN_HTML =
            "<em>(and I understand it is my responsibility to include DOIs in the submitted metadata)</em>";
    private static final String OWNER_ARCHIVE_INFO_HTML =
            "By selecting this option I am agreeing the uploaded files for these " +
                    "datasets are archived or will be archived at a data center of my choice " +
                    "before the public release containing these datasets.  If a DOI or other " +
                    "reference for these archived files is provided, I will include these " +
                    "references in the metadata supplied for these datasets.";

    private static final String ALREADY_ARCHIVED_HTML =
            "<h3>WARNING</h3>" +
                    "<p>The files for some or all of these dataset were earlier sent to OCADS " +
                    "(formerly CDIAC) for archival.  Normally you do not want to change the " +
                    "archival option for these datasets. </p>" +
                    "<p>If you are working with a mix of datasets that have and have not been " +
                    "sent to OCADS, we strongly recommend you cancel this action and work with " +
                    "datasets already sent to OCADS separately from those not sent.</p>";

    private static final String REARCHIVE_QUESTION =
            "<p>Some or all of these datasets were earlier sent to OCADS (formerly CDIAC) " +
                    "for archival.  Do you want to send the files for these datasets <b>again</b>?</p>" +
                    "<p><em>If you send the files for these datasets again, you should contact " +
                    "OCADS to explain the reason for this repeated request for archival.</em></p>";
    private static final String YES_RESEND_TEXT = "Yes, send";
    private static final String NO_CANCEL_TEXT = "No, cancel";

    private static final String AGREE_SHARE_TEXT =
            "I give permission for these datasets to be shared for QC assessment " +
                    "and archived as indicated above.";
    private static final String AGREE_SHARE_INFO_HTML =
            "By checking this box I am giving permission for my uploaded files for " +
                    "these datasets to be shared for purposes of assessing data quality.  " +
                    "I understand that data so-released will be used only for that narrow " +
                    "purpose and will not be further distributed except as indicated in the " +
                    "above selected archival option.";

    private static final String AGREE_SHARE_REQUIRED_MSG =
            "You must give permission to share the dataset(s) for QC " +
                    "assessment before the dataset(s) can be submitted for QC.";

    private static final String ARCHIVE_PLAN_REQUIRED_MSG =
            "You must select an archival option for the uploaded data and metadata " +
                    "files before the dataset(s) can be submitted for QC.";

    private static final String SUBMIT_FAILURE_MSG =
            "Unexpected failure with submitting datasets for QC: ";

    private static final String SUBMIT_TEXT = "OK";
    private static final String CANCEL_TEXT = "Cancel";

    interface SubmitForQCPageUIBinder extends UiBinder<Widget,SubmitForQCPage> {
    }

    private static SubmitForQCPageUIBinder uiBinder =
            GWT.create(SubmitForQCPageUIBinder.class);

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
    HTML archivePlanHtml;
    @UiField
    RadioButton laterRadio;
    @UiField
    Anchor laterInfoAnchor;
    @UiField
    HTML laterAddnHtml;
    @UiField
    RadioButton nowRadio;
    @UiField
    Anchor nowInfoAnchor;
    @UiField
    HTML nowAddnHtml;
    @UiField
    RadioButton ownerRadio;
    @UiField
    Anchor ownerInfoAnchor;
    @UiField
    HTML ownerAddnHtml;
    @UiField
    CheckBox agreeShareCheckBox;
    @UiField
    Anchor agreeShareInfoAnchor;
    @UiField
    Button submitButton;
    @UiField
    Button cancelButton;

    private TreeSet<String> expocodes;
    private boolean hasSentDataset;
    private DashboardInfoPopup laterArchivePopup;
    private DashboardInfoPopup nowInfoPopup;
    private DashboardInfoPopup ownerArchivePopup;
    private DashboardInfoPopup agreeSharePopup;
    private DashboardAskPopup resubmitAskPopup;

    // The singleton instance of this page
    private static SubmitForQCPage singleton;

    /**
     * Creates an empty SubmitForQC page.  Do not use this constructor; instead use the static showPage or redisplayPage
     * method.
     */
    SubmitForQCPage() {
        initWidget(uiBinder.createAndBindUi(this));
        singleton = this;

        setUsername(null);
        expocodes = new TreeSet<String>();
        hasSentDataset = false;

        titleLabel.setText(TITLE_TEXT);
        logoutButton.setText(LOGOUT_TEXT);

        archivePlanHtml.setHTML(ARCHIVE_PLAN_INTRO);

        laterRadio.setText(ARCHIVE_LATER_TEXT);
        laterInfoAnchor.setText(MORE_INFO_TEXT);
        laterAddnHtml.setHTML(ARCHIVE_LATER_ADDN_HTML);
        laterArchivePopup = null;

        nowRadio.setText(ARCHIVE_NOW_TEXT);
        nowInfoAnchor.setText(MORE_INFO_TEXT);
        nowAddnHtml.setHTML(ARCHIVE_NOW_ADDN_HTML);
        nowInfoPopup = null;

        ownerRadio.setText(OWNER_ARCHIVE_TEXT);
        ownerInfoAnchor.setText(MORE_INFO_TEXT);
        ownerAddnHtml.setHTML(OWNER_ARCHIVE_ADDN_HTML);
        ownerArchivePopup = null;

        agreeShareCheckBox.setText(AGREE_SHARE_TEXT);
        agreeShareInfoAnchor.setText(MORE_INFO_TEXT);
        agreeSharePopup = null;

        resubmitAskPopup = null;
        submitButton.setText(SUBMIT_TEXT);
        cancelButton.setText(CANCEL_TEXT);
    }

    /**
     * Display this page in the RootLayoutPanel showing the given cruises.  Adds this page to the page history.
     */
    static void showPage(DashboardDatasetList cruises) {
        if ( singleton == null )
            singleton = new SubmitForQCPage();
        UploadDashboard.updateCurrentPage(singleton);
        singleton.updateDatasets(cruises);
        History.newItem(PagesEnum.SUBMIT_FOR_QC.name(), false);
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
     * Updates the username on this page using the login page username, and updates the listing of cruises on this page
     * with those given in the argument.
     *
     * @param cruises
     *         cruises to display
     */
    private void updateDatasets(DashboardDatasetList cruises) {
        // Update the username
        setUsername(cruises.getUsername());
        userInfoLabel.setText(WELCOME_INTRO + getUsername());

        expocodes.clear();
        hasSentDataset = false;
        int numDelay = 0;
        int numOwner = 0;
        int numCdiac = 0;
        TreeSet<String> cruiseIntros = new TreeSet<String>();
        for (DashboardDataset cruise : cruises.values()) {
            String expo = cruise.getDatasetId();
            // Add the status of this cruise to the counts
            String archiveStatus = cruise.getArchiveStatus();
            if ( archiveStatus.equals(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE) ) {
                // Archive with next release
                numDelay++;
            }
            else if ( archiveStatus.startsWith(DashboardUtils.ARCHIVE_STATUS_SENT_TO_START) ) {
                // Archive now - in future there might be more than one place
                numCdiac++;
            }
            else if ( archiveStatus.equals(DashboardUtils.ARCHIVE_STATUS_OWNER_TO_ARCHIVE) ) {
                // Owner will archive
                numOwner++;
            }
            expocodes.add(expo);

            // Add this cruise to the intro list
            String submitStatus = cruise.getSubmitStatus();
            String cdiacDate = cruise.getArchiveDate();
            if ( submitStatus.isEmpty() && cdiacDate.isEmpty() ) {
                cruiseIntros.add("<li>" + SafeHtmlUtils.htmlEscape(expo) +
                        "</li>");
            }
            else if ( cdiacDate.isEmpty() ) {
                cruiseIntros.add("<li>" + SafeHtmlUtils.htmlEscape(expo) +
                        CRUISE_INFO_PROLOGUE + QC_STATUS_INTRO +
                        submitStatus + CRUISE_INFO_EPILOGUE + "</li>");
            }
            else if ( submitStatus.isEmpty() ) {
                hasSentDataset = true;
                cruiseIntros.add("<li>" + SafeHtmlUtils.htmlEscape(expo) +
                        CRUISE_INFO_PROLOGUE + ARCHIVE_STATUS_INTRO +
                        cdiacDate + CRUISE_INFO_EPILOGUE + "</li>");
            }
            else {
                hasSentDataset = true;
                cruiseIntros.add("<li>" + SafeHtmlUtils.htmlEscape(expo) +
                        CRUISE_INFO_PROLOGUE + QC_STATUS_INTRO +
                        submitStatus + "; " + ARCHIVE_STATUS_INTRO +
                        cdiacDate + CRUISE_INFO_EPILOGUE + "</li>");
            }
        }

        // Create the intro using the ordered datasetIds
        String introMsg = INTRO_HTML_PROLOGUE;
        for (String introItem : cruiseIntros) {
            introMsg += introItem;
        }
        introMsg += INTRO_HTML_EPILOGUE;
        introHtml.setHTML(introMsg);

        // Check the appropriate radio button
        int numDatasets = cruises.size();
        if ( numDelay == numDatasets ) {
            // All "with next release", so keep that setting
            laterRadio.setValue(true, true);
        }
        else if ( numCdiac == numDatasets ) {
            // All "sent for archival", so keep that setting
            nowRadio.setValue(true, true);
        }
        else if ( numOwner == numDatasets ) {
            // All "owner will archive", so keep that setting
            ownerRadio.setValue(true, true);
        }
        else {
            // A mix, so unset all and make the user decide
            laterRadio.setValue(false, true);
            nowRadio.setValue(false, true);
            ownerRadio.setValue(false, true);
        }

        // Unselect the agree-to-share check box
        agreeShareCheckBox.setValue(false, true);

        // Reset the focus on the agree-to-share check box
        agreeShareCheckBox.setFocus(true);
    }

    @UiHandler("logoutButton")
    void logoutOnClick(ClickEvent event) {
        DashboardLogoutPage.showPage();
    }

    @UiHandler({ "laterRadio", "ownerRadio" })
    void radioOnClick(ClickEvent event) {
        // If there is a cruise sent to CDIAC, warn if another selection is made
        if ( hasSentDataset ) {
            UploadDashboard.showMessage(ALREADY_ARCHIVED_HTML);
        }
    }

    @UiHandler("laterInfoAnchor")
    void laterInfoOnClick(ClickEvent event) {
        // Create the popup only when needed and if it does not exist
        if ( laterArchivePopup == null ) {
            laterArchivePopup = new DashboardInfoPopup();
            laterArchivePopup.setInfoMessage(ARCHIVE_LATER_INFO_HTML);
        }
        // Show the popup over the info anchor
        laterArchivePopup.showRelativeTo(laterInfoAnchor);
    }

    @UiHandler("nowInfoAnchor")
    void nowInfoOnClick(ClickEvent event) {
        // Create the popup only when needed and if it does not exist
        if ( nowInfoPopup == null ) {
            nowInfoPopup = new DashboardInfoPopup();
            nowInfoPopup.setInfoMessage(ARCHIVE_NOW_INFO_HTML);
        }
        // Show the popup over the info anchor
        nowInfoPopup.showRelativeTo(nowInfoAnchor);
    }

    @UiHandler("ownerInfoAnchor")
    void ownerInfoOnClick(ClickEvent event) {
        // Create the popup only when needed and if it does not exist
        if ( ownerArchivePopup == null ) {
            ownerArchivePopup = new DashboardInfoPopup();
            ownerArchivePopup.setInfoMessage(OWNER_ARCHIVE_INFO_HTML);
        }
        // Show the popup over the info anchor
        ownerArchivePopup.showRelativeTo(ownerInfoAnchor);
    }

    @UiHandler("agreeShareInfoAnchor")
    void agreeShareInfoOnClick(ClickEvent event) {
        // Create the popup only when needed and if it does not exist
        if ( agreeSharePopup == null ) {
            agreeSharePopup = new DashboardInfoPopup();
            agreeSharePopup.setInfoMessage(AGREE_SHARE_INFO_HTML);
        }
        // Show the popup over the info anchor
        agreeSharePopup.showRelativeTo(agreeShareInfoAnchor);
    }

    @UiHandler("cancelButton")
    void cancelOnClick(ClickEvent event) {
        // Return to the list of cruises which could have been modified by this page
        DatasetListPage.showPage();
    }

    @UiHandler("submitButton")
    void submitOnClick(ClickEvent event) {
        if ( !agreeShareCheckBox.getValue() ) {
            UploadDashboard.showMessageAt(AGREE_SHARE_REQUIRED_MSG, agreeShareCheckBox);
            return;
        }
        if ( hasSentDataset && nowRadio.getValue() ) {
            // Asking to submit to CDIAC now, but has a cruise already sent
            if ( resubmitAskPopup == null ) {
                resubmitAskPopup = new DashboardAskPopup(YES_RESEND_TEXT,
                        NO_CANCEL_TEXT, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean okay) {
                        // Continue setting the archive status (and thus,
                        // sending the request to CDIAC) only if user okays it
                        if ( okay ) {
                            continueSubmit();
                        }
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        // Never called
                        ;
                    }
                });
            }
            resubmitAskPopup.askQuestion(REARCHIVE_QUESTION);
        }
        else {
            // Either no cruises sent to CDIAC, or not a send to CDIAC now request.
            // Continue setting the archive status.
            continueSubmit();
        }
    }

    /**
     * Submits cruises and updated archival selection
     */
    void continueSubmit() {
        String localTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z").format(new Date());
        String archiveStatus;
        if ( laterRadio.getValue() ) {
            // Archive with the next release
            archiveStatus = DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE;
        }
        else if ( nowRadio.getValue() ) {
            // Archive now - in future there might be more than one place
            archiveStatus = DashboardUtils.ARCHIVE_STATUS_SENT_TO_START + "OCADS";
        }
        else if ( ownerRadio.getValue() ) {
            // Owner will archive
            archiveStatus = DashboardUtils.ARCHIVE_STATUS_OWNER_TO_ARCHIVE;
        }
        else {
            // Archive option not selected - fail
            UploadDashboard.showMessageAt(ARCHIVE_PLAN_REQUIRED_MSG, archivePlanHtml);
            return;
        }

        boolean repeatSend = true;
        // Submit the dataset
        UploadDashboard.showWaitCursor();
        service.submitDatasetsForQC(getUsername(), expocodes, archiveStatus,
                localTimestamp, repeatSend, new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Success - go back to the cruise list page
                        DatasetListPage.showPage();
                        UploadDashboard.showAutoCursor();
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        // Failure, so show fail message
                        // But still go back to the cruise list page since some may have succeeded
                        UploadDashboard.showFailureMessage(SUBMIT_FAILURE_MSG, ex);
                        DatasetListPage.showPage();
                        UploadDashboard.showAutoCursor();
                    }
                });
    }

}
