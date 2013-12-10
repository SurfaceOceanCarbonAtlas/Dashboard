/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 */
public class CruiseArchivePage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String MORE_INFO_TEXT = "more explanation";

	private static final String INTRO_HTML_PROLOGUE = 
			"Select an archive option for the uploaded cruise data and metadata " +
			"files.  We highly recommend permitting us to automatically archive " +
			"the cruise at CDIAC.  <em>If you wish to archive these files at " +
			"another data center, you will need to provide the DOI for these files " +
			"before the next SOCAT release for the cruise to be included in that SOCAT " +
			"release.</em> " +
			"<br /><br />" +
			"<large>For the cruise: ";
	private static final String INTRO_HTML_EPILOGUE = "</large>";

	private static final String SOCAT_BUTTON_TEXT = 
			"I give permission for my cruise to be automatically archived at CDIAC";
	private static final String SOCAT_INFO_HTML = 
			"By selecting this option I am giving permission for my uploaded " +
			"cruise file and metadata to be archived at CDIAC.  This will occur, " +
			"if the cruise was deemed acceptable, at the time of the next SOCAT " +
			"public release, after which the files will be made accessible to " +
			"the public through the CDIAC Web site. ";

	private static final String CDIAC_BUTTON_TEXT = 
			"I wish to archive my cruise at CDIAC as soon as possible";
	private static final String CDIAC_INFO_HTML =
			"By selecting this option I am giving permission for my uploaded " +
			"cruise file and metadata to be archived at CDIAC as soon as possible. " +
			"I understand this indicates I will not be making any modifications " +
			"to these files that may result from the SOCAT policy (QC) assessment. " +
			"<br /><br />" +
			"When this archiving has been performed, the files will be made " +
			"accessible to the public through the CDIAC Web site, and this option " +
			"will change to the archived option below with the new CDIAC DOI";

	private static final String OWNER_BUTTON_TEXT =
			"I will archive my cruise at a data center, but do not yet have a DOI";
	private static final String OWNER_INFO_HTML = 
			"By selecting this option I am agreeing to archive the uploaded " +
			"cruise file and metadata at a data center, but do not have a DOI " +
			"for these archived files at this time.  I understand I will need " +
			"to provide a DOI for these files before the cruise can be included " +
			"in a SOCAT release. " +
			"<br /><br />" +
			"When a DOI for these files is available, select the archived option " +
			"below and provide the new DOI.";

	private static final String DOI_BUTTON_TEXT = 
			"The cruise is archived at a data center with the DOI";

	private static final String SUBMIT_TEXT = "OK";
	private static final String CANCEL_TEXT = "Cancel";

	interface CruiseArchivePageUiBinder 
			extends UiBinder<Widget, CruiseArchivePage> {
	}

	private static CruiseArchivePageUiBinder uiBinder = 
			GWT.create(CruiseArchivePageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField RadioButton socatRadio;
	@UiField Button socatInfoButton;
	@UiField RadioButton cdiacRadio;
	@UiField Button cdiacInfoButton;
	@UiField RadioButton ownerRadio;
	@UiField Button ownerInfoButton;
	@UiField RadioButton doiRadio;
	@UiField TextBox doiTextBox;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	private String username;
	private DashboardInfoPopup socatInfoPopup;
	private DashboardInfoPopup cdiacInfoPopup;
	private DashboardInfoPopup ownerInfoPopup;

	// The singleton instance of this page
	private static CruiseArchivePage singleton;

	private CruiseArchivePage() {
		initWidget(uiBinder.createAndBindUi(this));

		username = "";
		socatInfoPopup = null;
		cdiacInfoPopup = null;
		ownerInfoPopup = null;

		logoutButton.setText(LOGOUT_TEXT);

		socatRadio.setText(SOCAT_BUTTON_TEXT);
		socatInfoButton.setText(MORE_INFO_TEXT);

		cdiacRadio.setText(CDIAC_BUTTON_TEXT);
		cdiacInfoButton.setText(MORE_INFO_TEXT);

		ownerRadio.setText(OWNER_BUTTON_TEXT);
		ownerInfoButton.setText(MORE_INFO_TEXT);

		doiRadio.setText(DOI_BUTTON_TEXT);
		doiTextBox.setVisibleLength(32);

		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display this page in the RootLayoutPanel showing the
	 * given cruises.  Adds this page to the page history.
	 */
	static void showPage(DashboardCruise cruise) {
		if ( singleton == null )
			singleton = new CruiseArchivePage();
		SocatUploadDashboard.get().updateCurrentPage(singleton);
		singleton.update(cruise);
		History.newItem(PagesEnum.ARCHIVE.name(), false);
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
			SocatUploadDashboard.get().updateCurrentPage(singleton);
			if ( addToHistory ) 
				History.newItem(PagesEnum.ARCHIVE.name(), false);
		}
	}

	/**
	 * Updates the username on this page using the login page username,
	 * and updates the archive status with that from the given cruise.
	 *  
	 * @param cruise
	 * 		show the archive status from this cruise
	 */
	private void update(DashboardCruise cruise) {
		// Update the username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);

		introHtml.setHTML(INTRO_HTML_PROLOGUE + 
				SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + 
				INTRO_HTML_EPILOGUE);

		// Check the appropriate radio button
		String archiveStatus = cruise.getArchiveStatus();
		if ( archiveStatus.equals(
				DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED)  ) {
			// Nothing assigned yet; default to "with next SOCAT"
			doiTextBox.setText("");
			doiTextBox.setEnabled(false);
			socatRadio.setValue(true, true);
		}
		else if ( archiveStatus.equals(
				DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT) ) {
			doiTextBox.setText("");
			doiTextBox.setEnabled(false);
			socatRadio.setValue(true, true);
		}
		else if ( archiveStatus.equals(
				DashboardUtils.ARCHIVE_STATUS_SUBMIT_CDIAC) ) {
			doiTextBox.setText("");
			doiTextBox.setEnabled(false);
			cdiacRadio.setValue(true, true);
		}
		else if ( archiveStatus.equals(
				DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE) ) {
			doiTextBox.setText("");
			doiTextBox.setEnabled(false);
			ownerRadio.setValue(true, true);
		}
		else if ( archiveStatus.startsWith(
				DashboardUtils.ARCHIVE_STATUS_ARCHIVED_PREFIX) ) {
			// Get the DOI out of the status string
			String doi = archiveStatus.substring(
					DashboardUtils.ARCHIVE_STATUS_ARCHIVED_PREFIX.length()
					).trim();
			doiTextBox.setText(doi);
			doiTextBox.setEnabled(true);
			doiTextBox.setFocus(true);
			doiRadio.setValue(true, true);
		}
		else {
			// Should not happen
			Window.alert("Unexpected archive status: " + archiveStatus);
			doiTextBox.setText("");
			doiTextBox.setEnabled(false);
			socatRadio.setValue(true, true);
		}
	}

	@UiHandler({"socatRadio","cdiacRadio","ownerRadio"})
	void noDoiRadioOnValueChange(ValueChangeEvent<Boolean> event) {
		if ( event.getValue() ) {
			// Disable the DOI text box
			doiTextBox.setEnabled(false);
		}
	}

	@UiHandler("doiRadio")
	void doiRadioOnValueChange(ValueChangeEvent<Boolean> event) {
		if ( event.getValue() ) {
			// Enable the DOI text box, and set the focus on this text box
			doiTextBox.setEnabled(true);
			doiTextBox.setFocus(true);
		}
	}

	@UiHandler("socatInfoButton")
	void socatInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( socatInfoPopup == null ) {
			socatInfoPopup = new DashboardInfoPopup();
			socatInfoPopup.setInfoMessage(SOCAT_INFO_HTML);
		}
		// Show the popup over the info button
		socatInfoPopup.showAtPosition(
				socatInfoButton.getAbsoluteLeft(),
				socatInfoButton.getAbsoluteTop());
	}

	@UiHandler("cdiacInfoButton")
	void cdiacInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( cdiacInfoPopup == null ) {
			cdiacInfoPopup = new DashboardInfoPopup();
			cdiacInfoPopup.setInfoMessage(CDIAC_INFO_HTML);
		}
		// Show the popup over the info button
		cdiacInfoPopup.showAtPosition(
				cdiacInfoButton.getAbsoluteLeft(),
				cdiacInfoButton.getAbsoluteTop());
	}

	@UiHandler("ownerInfoButton")
	void selfInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( ownerInfoPopup == null ) {
			ownerInfoPopup = new DashboardInfoPopup();
			ownerInfoPopup.setInfoMessage(OWNER_INFO_HTML);
		}
		// Show the popup over the info button
		ownerInfoPopup.showAtPosition(
				ownerInfoButton.getAbsoluteLeft(),
				ownerInfoButton.getAbsoluteTop());
	}

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Return to the cruise list page exactly as it was
		DashboardCruiseListPage.redisplayPage(true);
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		// TODO: tell the server of the archive status for this cruise
		Window.alert("Not yet implemented");
	}

}
