/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.AddToSocatService;
import gov.noaa.pmel.socat.dashboard.shared.AddToSocatServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page for submitting cruises to be incorporated into the SOCAT collection.
 * 
 * @author Karl Smith
 */
public class AddToSocatPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String MORE_INFO_TEXT = "more ...";

	private static final String INTRO_HTML_PROLOGUE =
			"<b>Submit Cruises to the QC System / Manage Archival</b>" +
			"<br /><br />" +
			"Add the following cruises to the SOCAT collection for policy (QC) " +
			"assessment, or modify the archival of already-submitted cruises." +
			"<ul>";
	private static final String INTRO_HTML_EPILOGUE = 
			"</ul>";

	private static final String CRUISE_INFO_PROLOGUE = 
			"&nbsp;&nbsp;&nbsp;&nbsp;<em>(";
	private static final String QC_STATUS_INTRO =
			"QC status: ";
	private static final String ARCHIVE_STATUS_INTRO =
			"CDIAC archive request sent on ";
	private static final String CRUISE_INFO_EPILOGUE =
			")</em>";

	private static final String ARCHIVE_PLAN_INTRO = 
			"Archival plan for the uploaded data and metadata of these cruises: ";
	private static final String SOCAT_ARCHIVE_TEXT = 
			"archive at CDIAC at the time of the next SOCAT public release";
	private static final String SOCAT_ARCHIVE_INFO_HTML = 
			"By selecting this option I am giving permission for my uploaded cruise " +
			"files and metadata for these cruises to be archived at CDIAC.  This will " +
			"occur, for cruises deemed acceptable, at the time of the next SOCAT public " +
			"release, after which the files will be made accessible to the public " +
			"through the CDIAC Web site.";

	private static final String CDIAC_ARCHIVE_TEXT = 
			"archive at CDIAC as soon as possible";
	private static final String CDIAC_ARCHIVE_INFO_HTML =
			"By selecting this option I am requesting that my uploaded cruise files " +
			"and metadata for these cruise be archived at CDIAC as soon as possible.  " +
			"When CDIAC provides a DOI, or other reference, for these archived files, " +
			"I will insure these references are in the metadata supplied to SOCAT for " +
			"the cruises.";

	private static final String OWNER_ARCHIVE_TEXT =
			"do not archive at CDIAC; I will manage archival myself";
	private static final String OWNER_ARCHIVE_ADDN_HTML =
			"<em>(and I understand it is my responsibility to include DOIs " +
			"in SOCAT metadata)</em>";
	private static final String OWNER_ARCHIVE_INFO_HTML = 
			"By selecting this option I am agreeing to archive the uploaded cruise " +
			"files and metadata for these cruises at a data center of my choice before " +
			"the SOCAT public release containing these cruises.  If I am provided a " +
			"DOI or other reference for these archived files, I will include these " +
			"references in the metadata supplied to SOCAT for the cruises.";

	private static final String ALREADY_SENT_CDIAC_HTML =
			"<b>WARNING</b>" +
			"<br /><br />" +
			"Some or all of these cruises were earlier sent to CDIAC for archival. " +
			"Normally you do not want to change the archival option for these cruises. " +
			"<br /><br />" +
			"If you are managing the archival of a mix of cruises that have and have " +
			"not been sent to CDIAC, we strongly recommend you cancel this action and " +
			"manage only those cruises that have not been sent to CDIAC.";

	private static final String RESEND_CDIAC_QUESTION = 
			"Some or all of these cruises were earlier sent to CDIAC for archival.  " +
			"Do you want to send these cruises <em>again<em>?" +
			"<br /><br />" +
			"<em>If you send these cruises again, you should contact CDIAC to " +
			"explain the reason for this repeated request for archival.</em>";
	private static final String YES_RESEND_TEXT = "Yes, send";
	private static final String NO_CANCEL_TEXT = "No, cancel";

	private static final String AGREE_SHARE_TEXT = 
			"I give permission for these cruises to be shared for policy (QC) assessment.";
	private static final String AGREE_SHARE_INFO_HTML =
			"By checking this box I am giving permission for my uploaded cruise files " +
			"to be shared for purposes of policy (QC) assessment.  I understand that " +
			"data so-released will be used only for that narrow purpose and will not " +
			"be further distributed until the next official publication of SOCAT, if " +
			"the cruise was deemed acceptable. ";

	private static final String AGREE_SHARE_REQUIRED_MSG =
			"You must give permission to shared the cruise data for policy " +
			"(QC) assessment before the cruises can be added to the QC system.";

	private static final String SUBMIT_FAILURE_MSG = 
			"Unexpected failure with submitting cruises to SOCAT: ";

	private static final String SUBMIT_TEXT = "OK";
	private static final String CANCEL_TEXT = "Cancel";

	interface AddCruiseToSocatPageUiBinder extends UiBinder<Widget, AddToSocatPage> {
	}

	private static AddCruiseToSocatPageUiBinder uiBinder = 
			GWT.create(AddCruiseToSocatPageUiBinder.class);

	private static AddToSocatServiceAsync service = 
			GWT.create(AddToSocatService.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField Label archivePlanLabel;
	@UiField RadioButton socatRadio;
	@UiField Button socatInfoButton;
	@UiField RadioButton cdiacRadio;
	@UiField Button cdiacInfoButton;
	@UiField RadioButton ownerRadio;
	@UiField Button ownerInfoButton;
	@UiField HTML ownerAddnHtml;
	@UiField CheckBox agreeShareCheckBox;
	@UiField Button agreeShareInfoButton;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	private String username;
	private HashSet<String> expocodes;
	private boolean hasSentCruise;
	private DashboardInfoPopup socatArchivePopup;
	private DashboardInfoPopup cdiacInfoPopup;
	private DashboardInfoPopup ownerArchivePopup;
	private DashboardInfoPopup agreeSharePopup;
	private DashboardAskPopup resubmitAskPopup;

	// The singleton instance of this page
	private static AddToSocatPage singleton;

	/**
	 * Creates an empty AddToSocat page.  Do not use this constructor;
	 * instead use the static showPage or redisplayPage method.
	 */
	AddToSocatPage() {
		initWidget(uiBinder.createAndBindUi(this));

		username = "";
		expocodes = new HashSet<String>();
		hasSentCruise = false;

		logoutButton.setText(LOGOUT_TEXT);

		archivePlanLabel.setText(ARCHIVE_PLAN_INTRO);
		socatRadio.setText(SOCAT_ARCHIVE_TEXT);
		socatInfoButton.setText(MORE_INFO_TEXT);
		socatArchivePopup = null;

		cdiacRadio.setText(CDIAC_ARCHIVE_TEXT);
		cdiacInfoButton.setText(MORE_INFO_TEXT);
		cdiacInfoPopup = null;

		ownerRadio.setText(OWNER_ARCHIVE_TEXT);
		ownerInfoButton.setText(MORE_INFO_TEXT);
		ownerAddnHtml.setHTML(OWNER_ARCHIVE_ADDN_HTML);
		ownerArchivePopup = null;

		agreeShareCheckBox.setText(AGREE_SHARE_TEXT);
		agreeShareInfoButton.setText(MORE_INFO_TEXT);
		agreeSharePopup = null;

		resubmitAskPopup = null;
		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display this page in the RootLayoutPanel showing the
	 * given cruises.  Adds this page to the page history.
	 */
	static void showPage(HashSet<DashboardCruise> cruises) {
		if ( singleton == null )
			singleton = new AddToSocatPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		singleton.updateCruises(cruises);
		History.newItem(PagesEnum.ADD_TO_SOCAT.name(), false);
	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the current login username.
	 * 
	 * @param addToHistory
	 * 		if true, adds this page to the page history 
	 */
	static void redisplayPage(boolean addToHistory) {
		// If never show before, or if the username does not match the 
		// current login username, show the login page instead
		if ( (singleton == null) || 
			 ! singleton.username.equals(DashboardLoginPage.getUsername()) ) {
			DashboardLoginPage.showPage(true);
		}
		else {
			SocatUploadDashboard.updateCurrentPage(singleton);
			if ( addToHistory )	
				History.newItem(PagesEnum.ADD_TO_SOCAT.name(), false);
		}
	}

	/**
	 * Updates the username on this page using the login page username,
	 * and updates the listing of cruises on this page with those given 
	 * in the argument.
	 * 
	 * @param cruises
	 * 		cruises to display
	 */
	private void updateCruises(HashSet<DashboardCruise> cruisesSet) {
		// Update the username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);

		expocodes.clear();
		hasSentCruise = false;
		int numSocat = 0;
		int numOwner = 0;
		int numCdiac = 0;
		TreeSet<String> cruiseIntros = new TreeSet<String>();
		for ( DashboardCruise cruise : cruisesSet ) {
			String expo = cruise.getExpocode();
			// Add the expocode of this cruise to the list for the server 
			expocodes.add(expo);
			// Add the status of this cruise to the counts 
			String archiveStatus = cruise.getArchiveStatus();
			if ( archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT) ) {
				// Archive with next SOCAT release
				numSocat++;
			}
			else if ( archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC) ) {
				// Archive at CDIAC now
				numCdiac++;
			}
			else if ( archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE) ) {
				// Owner will archive
				numOwner++;
			}
			else if ( ! archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED) ) {
				// unknown status - should not happen
				Window.alert("Unexpected archive status: " + archiveStatus);
			}
			// Add this cruise to the intro list
			String submitStatus = cruise.getQcStatus();
			String cdiacDate = cruise.getCdiacDate();
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
				hasSentCruise = true;
				cruiseIntros.add("<li>" + SafeHtmlUtils.htmlEscape(expo) + 
						CRUISE_INFO_PROLOGUE + ARCHIVE_STATUS_INTRO +
						cdiacDate + CRUISE_INFO_EPILOGUE + "</li>");								
			}
			else {
				hasSentCruise = true;
				cruiseIntros.add("<li>" + SafeHtmlUtils.htmlEscape(expo) + 
						CRUISE_INFO_PROLOGUE + QC_STATUS_INTRO +
						submitStatus + "; " + ARCHIVE_STATUS_INTRO +
						cdiacDate + CRUISE_INFO_EPILOGUE + "</li>");								
			}
		}

		// Create the intro using the ordered expocodes
		String introMsg = INTRO_HTML_PROLOGUE;
		for ( String introItem : cruiseIntros ) {
			introMsg += introItem;
		}
		introMsg += INTRO_HTML_EPILOGUE;
		introHtml.setHTML(introMsg);

		// Check the appropriate radio button
		if ( (numOwner == 0) && (numCdiac == 0) ) {
			// All "with next SOCAT" or not assigned
			socatRadio.setValue(true, true);
		}
		else if ( (numSocat == 0) && (numOwner == 0) ) {
			// All "sent to CDIAC", so keep that setting
			cdiacRadio.setValue(true, true);
		}
		else if ( (numSocat == 0) && (numCdiac == 0) ) {
			// All "owner will archive", so keep that setting
			ownerRadio.setValue(true, true);
		}
		else {
			// A mix, so set to "with next SOCAT" and let the user decide
			socatRadio.setValue(true, true);
		}

		// Select the agree-to-share check boxes
		agreeShareCheckBox.setValue(true, true);

		// Reset the focus on the submit button
		submitButton.setFocus(true);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler({"socatRadio","ownerRadio"})
	void radioOnClick(ClickEvent event) {
		// If there is a cruise sent to CDIAC, warn if another selection is made
		if ( hasSentCruise ) {
			SocatUploadDashboard.showMessage(ALREADY_SENT_CDIAC_HTML);
		}
	}

	@UiHandler("socatInfoButton")
	void socatInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( socatArchivePopup == null ) {
			socatArchivePopup = new DashboardInfoPopup();
			socatArchivePopup.setInfoMessage(SOCAT_ARCHIVE_INFO_HTML);
		}
		// Show the popup over the info button
		socatArchivePopup.showRelativeTo(socatInfoButton);
	}

	@UiHandler("cdiacInfoButton")
	void cdiacInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( cdiacInfoPopup == null ) {
			cdiacInfoPopup = new DashboardInfoPopup();
			cdiacInfoPopup.setInfoMessage(CDIAC_ARCHIVE_INFO_HTML);
		}
		// Show the popup over the info button
		cdiacInfoPopup.showRelativeTo(cdiacInfoButton);
	}

	@UiHandler("ownerInfoButton")
	void ownerInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( ownerArchivePopup == null ) {
			ownerArchivePopup = new DashboardInfoPopup();
			ownerArchivePopup.setInfoMessage(OWNER_ARCHIVE_INFO_HTML);
		}
		// Show the popup over the info button
		ownerArchivePopup.showRelativeTo(ownerInfoButton);
	}

	@UiHandler("agreeShareInfoButton")
	void agreeShareInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( agreeSharePopup == null ) {
			agreeSharePopup = new DashboardInfoPopup();
			agreeSharePopup.setInfoMessage(AGREE_SHARE_INFO_HTML);
		}
		// Show the popup over the info button
		agreeSharePopup.showRelativeTo(agreeShareInfoButton);
	}

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Return to the cruise list page exactly as it was
		CruiseListPage.redisplayPage(true);
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		if ( ! agreeShareCheckBox.getValue() ) {
			SocatUploadDashboard.showMessageAt(
					AGREE_SHARE_REQUIRED_MSG, agreeShareCheckBox);
			return;
		}
		if ( hasSentCruise && cdiacRadio.getValue() ) {
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
			resubmitAskPopup.askQuestion(RESEND_CDIAC_QUESTION);
		}
		else {
			// Either no cruises sent to CDIAC, or not a send to CDIAC now request. 
			// Continue setting the archive status.
			continueSubmit();
		}
	}

	/**
	 * Submits cruises and updated archival selection to SOCAT.
	 */
	void continueSubmit() {
		String localTimestamp = 
				DateTimeFormat.getFormat("yyyy-MM-dd HH:mm")
							  .format(new Date());
		String archiveStatus;
		if ( socatRadio.getValue() ) {
			// Archive with the next release of SOCAT
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT;
		}
		else if ( cdiacRadio.getValue() ) {
			// Tell CDIAC to archive now
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC;
		}
		else if ( ownerRadio.getValue() ) {
			// Owner will archive
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE;
		}
		else {
			// Should never happen
			Window.alert("Unexpect state where no radio buttons are selected");
			return;
		}

		boolean repeatSend = true;
		// Add the cruises to SOCAT
		service.addCruisesToSocat(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), expocodes, 
				archiveStatus, localTimestamp, repeatSend,
				new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// Success - go back to the cruise list page
				CruiseListPage.showPage(false);
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(SUBMIT_FAILURE_MSG, ex);
			}
		});
	}

}
