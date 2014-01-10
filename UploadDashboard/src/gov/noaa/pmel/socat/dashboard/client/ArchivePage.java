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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page for updating the archive status of cruises.
 * 
 * @author Karl Smith
 */
public class ArchivePage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String MORE_INFO_TEXT = "more explanation";

	private static final String INTRO_HTML_PROLOGUE = 
			"<b>Manage Archival for Cruises</b>" +
			"<br /><br />" +
			"Select an archive option for the uploaded data and metadata for " +
			"the cruises: <ul>";
	private static final String INTRO_HTML_EPILOGUE = "</ul>";

	static final String SOCAT_ARCHIVE_TEXT = 
			"I give permission for these cruises to be automatically archived at CDIAC.  ";
	static final String SOCAT_ARCHIVE_INFO_HTML = 
			"By selecting this option I am giving permission for my uploaded cruise " +
			"files and metadata for these cruises to be archived at CDIAC.  This will " +
			"occur, for cruises deemed acceptable, at the time of the next SOCAT public " +
			"release, after which the files will be made accessible to the public " +
			"through the CDIAC Web site.";
	static final String OWNER_ARCHIVE_TEXT =
			"I will archive these cruises at a data center of my choice.  ";
	static final String OWNER_ARCHIVE_INFO_HTML = 
			"By selecting this option I am agreeing to archive the uploaded cruise " +
			"files and metadata for these cruises at a data center of my choice before " +
			"the SOCAT public release containing these cruises.  If I am provided a " +
			"DOI or other reference for these archived files, I will include these " +
			"references in the metadata supplied to SOCAT for the cruises.";
	static final String CDIAC_ARCHIVE_TEXT = 
			"I wish to archive these cruises at CDIAC as soon as possible";
	static final String CDIAC_ARCHIVE_INFO_HTML =
			"By selecting this option I am giving permission for my uploaded cruise " +
			"files and metadata for these cruise to be archived at CDIAC as soon as " +
			"possible.  When CDIAC provides a DOI, or other reference, for these " +
			"archived files, I will include these references in the metadata supplied " +
			"to SOCAT for the cruises.";

	private static final String NO_CDIAC_ARCHIVALS = 
			"<em>None archived at CDIAC</em>";
	private static final String CDIAC_ARCHIVE_DATE_PROLOGUE =
			"<em>Request to archive at CDIAC sent on ";
	private static final String CDIAC_ARCHIVE_DATE_EPILOGUE =
			"</em>";
	private static final String CDIAC_ARCHIVE_MULTIPLE_DATES =
			"<em>Request to archive at CDIAC sent on various dates</em>";

	private static final String SUBMIT_TEXT = "OK";
	private static final String CANCEL_TEXT = "Cancel";

	private static final String UPDATE_FAILURE_MSG = 
			"Unexpected error updating the cruise archive status: ";

	interface CruiseArchivePageUiBinder extends UiBinder<Widget, ArchivePage> {
	}

	private static CruiseArchivePageUiBinder uiBinder = 
			GWT.create(CruiseArchivePageUiBinder.class);

	private static AddToSocatServiceAsync service = 
			GWT.create(AddToSocatService.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField RadioButton socatRadio;
	@UiField Button socatInfoButton;
	@UiField RadioButton ownerRadio;
	@UiField Button ownerInfoButton;
	@UiField RadioButton cdiacRadio;
	@UiField Button cdiacInfoButton;
	@UiField HTML cdiacDateHtml;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	private String username;
	private TreeSet<String> expocodes;
	private DashboardInfoPopup socatInfoPopup;
	private DashboardInfoPopup ownerInfoPopup;
	private DashboardInfoPopup cdiacInfoPopup;

	// The singleton instance of this page
	private static ArchivePage singleton;

	private ArchivePage() {
		initWidget(uiBinder.createAndBindUi(this));

		username = "";
		expocodes = new TreeSet<String>();
		socatInfoPopup = null;
		ownerInfoPopup = null;
		cdiacInfoPopup = null;

		logoutButton.setText(LOGOUT_TEXT);

		socatRadio.setText(SOCAT_ARCHIVE_TEXT);
		socatInfoButton.setText(MORE_INFO_TEXT);

		ownerRadio.setText(OWNER_ARCHIVE_TEXT);
		ownerInfoButton.setText(MORE_INFO_TEXT);

		cdiacRadio.setText(CDIAC_ARCHIVE_TEXT);
		cdiacInfoButton.setText(MORE_INFO_TEXT);
		cdiacDateHtml.setHTML(" ");

		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display this page in the RootLayoutPanel showing the
	 * archive options for the given cruises.  Adds this 
	 * page to the page history.
	 */
	static void showPage(HashSet<DashboardCruise> cruisesSet) {
		if ( singleton == null )
			singleton = new ArchivePage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		singleton.update(cruisesSet);
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
			SocatUploadDashboard.updateCurrentPage(singleton);
			if ( addToHistory ) 
				History.newItem(PagesEnum.ARCHIVE.name(), false);
		}
	}

	/**
	 * Updates the username on this page using the login page username,
	 * and updates the archive status with the consensus status of the 
	 * given cruises.
	 *  
	 * @param cruise
	 * 		show the archive status from this cruise
	 */
	private void update(HashSet<DashboardCruise> cruisesSet) {
		// Update the username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);

		// Get the ordered list of expocodes and count archival statuses
		expocodes.clear();
		int numSocat = 0;
		int numOwner = 0;
		int numCdiac = 0;
		String cdiacDate = null;
		for ( DashboardCruise cruise : cruisesSet ) {
			expocodes.add(cruise.getExpocode());
			String archiveStatus = cruise.getArchiveStatus();
			if ( archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED)  ) {
				// Nothing assigned yet
				;
			}
			else if ( archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT) ) {
				// Archive with next SOCAT release
				numSocat++;
			}
			else if ( archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE) ) {
				// Owner will archive
				numOwner++;
			}
			else if ( archiveStatus.equals(
					DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC) ) {
				numCdiac++;
			}
			else {
				// Should not happen
				Window.alert("Unexpected archive status: " + archiveStatus);
			}
			String dateStr = cruise.getCdiacDate();
			if ( ! dateStr.isEmpty() ) {
				if ( cdiacDate == null ) {
					// The one submission date (so far)
					cdiacDate = dateStr;
				}
				else if ( ! cdiacDate.equals(dateStr) ){
					// Multiple submission dates
					cdiacDate = "";
				}
			}
		}

		// Create the intro using the ordered expocodes
		String introMsg = INTRO_HTML_PROLOGUE;
		for ( String expo : expocodes ) {
			introMsg += "<li>" + SafeHtmlUtils.htmlEscape(expo) + "</li>";
		}
		introMsg += INTRO_HTML_EPILOGUE;
		introHtml.setHTML(introMsg);

		// Assign the CDIAC submission date label
		if ( cdiacDate == null ) {
			// No cruises submitted to CDIAC
			cdiacDateHtml.setHTML(NO_CDIAC_ARCHIVALS);
		}
		else if ( cdiacDate.isEmpty() ) {
			// Multiple submissions to CDIAC at different times
			cdiacDateHtml.setText(CDIAC_ARCHIVE_MULTIPLE_DATES);
		}
		else {
			cdiacDateHtml.setHTML(CDIAC_ARCHIVE_DATE_PROLOGUE + 
					SafeHtmlUtils.htmlEscape(cdiacDate) + 
					CDIAC_ARCHIVE_DATE_EPILOGUE);
		}

		// Check the appropriate radio button
		if ( (numOwner == 0) && (numCdiac == 0) ) {
			// All "with next SOCAT" or not assigned
			socatRadio.setValue(true, true);
		}
		else if ( (numSocat == 0) && (numCdiac == 0) ) {
			// All "owner will archive", so keep that setting
			ownerRadio.setValue(true, true);
		}
		else if ( (numSocat == 0) && (numOwner == 0) ) {
			// All "sent to CDIAC", so keep that setting
			cdiacRadio.setValue(true, true);
		}
		else {
			// A mix, so set to "with next SOCAT" and let the user decide
			socatRadio.setValue(true, true);
		}
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("socatInfoButton")
	void socatInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( socatInfoPopup == null ) {
			socatInfoPopup = new DashboardInfoPopup();
			socatInfoPopup.setInfoMessage(SOCAT_ARCHIVE_INFO_HTML);
		}
		// Show the popup over the info button
		socatInfoPopup.showRelativeTo(socatInfoButton);
	}

	@UiHandler("ownerInfoButton")
	void ownerInfoOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( ownerInfoPopup == null ) {
			ownerInfoPopup = new DashboardInfoPopup();
			ownerInfoPopup.setInfoMessage(OWNER_ARCHIVE_INFO_HTML);
		}
		// Show the popup over the info button
		ownerInfoPopup.showRelativeTo(ownerInfoButton);
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

	@UiHandler("cancelButton")
	void cancelOnClick(ClickEvent event) {
		// Return to the cruise list page exactly as it was
		CruiseListPage.redisplayPage(true);
	}

	@UiHandler("submitButton")
	void submitOnClick(ClickEvent event) {
		String localTimestamp = 
				DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z")
							  .format(new Date());
		String archiveStatus;
		if ( socatRadio.getValue() ) {
			// Archive with the next release of SOCAT
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT;
		}
		else if ( ownerRadio.getValue() ) {
			// Owner will archive
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE;
		}
		else if ( cdiacRadio.getValue() ) {
			// Tell CDIAC to archive now
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC;
		}
		else {
			// Should never happen
			archiveStatus = DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED;
		}
		service.setCruiseArchiveStatus(DashboardLoginPage.getUsername(), 
				DashboardLoginPage.getPasshash(), expocodes, archiveStatus, 
				localTimestamp, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// Success; show the updated cruise list page
				CruiseListPage.showPage(false);
			}
			@Override
			public void onFailure(Throwable ex) {
				// unexpected failure; show the error message
				Window.alert(UPDATE_FAILURE_MSG + ex.getMessage());
			}
		});
	}

}
