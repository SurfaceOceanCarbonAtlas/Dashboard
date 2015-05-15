/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;

import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 */
public class OmeManagerPage extends CompositeWithUsername {

	private static final String TITLE_TEXT = "Edit Metadata";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String EDIT_TEXT = "Open OME ...";
	private static final String EDIT_TOOLTIP_HELP = "Opens the OME in another window " +
			"with the indicated content and advances this page to the next dataset";
	private static final String DONE_TEXT = "Done";
	private static final String DONE_TOOLTIP_HELP = "Returns to your list of displayed datasets";

	private static final String CRUISE_HTML_INTRO_PROLOGUE = 
			"Sequentially open the online metadata editor (OME) " +
			"for each of the following datasets: <ul>";
	private static final String CRUISE_HTML_INTRO_EPILOGUE = "</ul>";

	private static final String CRUISE_HTML_ACTIVE_PROLOGUE = "Open the OME for <b>";
	private static final String CRUISE_HTML_ACTIVE_EPILOGUE = "</b>:";
	private static final String CRUISE_HTML_DONE_MSG = "Select Done when finished editing in the OME";

	private static final String EDIT_NEW_RADIO_TEXT = "with any existing metadata for ";
	private static final String EDIT_PREVIOUS_RADIO_TEXT = "copying applicable metadata from ";
	private static final String EDIT_UPLOAD_RADIO_TEXT = "using contents of your locally-saved OME XML file";

	private static final String OPEN_OME_FAIL_MSG = "Opening the metadata editor failed";

	private static final String WINDOW_FEATURES = "resizeable,scrollbars,status";

	// URL for OME file-upload page; fileURI is actually ignored at this time, but keep it in there
	private static final String OME_UPLOAD_FILE_URL = "/SocatOME/editor.htm?fileURI=file://";
	// URL for OME to show the current XML file on the server
	private static final String OME_SHOW_FILE_URL = "/SocatOME/show.htm?fileURI=file://";

	interface OmeManagerPageUiBinder extends UiBinder<Widget, OmeManagerPage> {
	}

	private static DashboardServicesInterfaceAsync service = 
			GWT.create(DashboardServicesInterface.class);

	private static OmeManagerPageUiBinder uiBinder = 
			GWT.create(OmeManagerPageUiBinder.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField HTML cruiseNameHtml;
	@UiField RadioButton editNewRadio;
	@UiField RadioButton editPreviousRadio;
	@UiField RadioButton editUploadRadio;
	@UiField Button editButton;
	@UiField Button doneButton;

	// Singleton instance of this page
	private static OmeManagerPage singleton;

	// Ordered set of expocodes to work with
	private TreeSet<String> expocodes;

	// Iterator over the above set of expocodes
	private Iterator<String> exposIter;

	// First expocode in the ordered set
	private String firstExpocode;

	// Open the OME with metadata for this expocode
	private String activeExpocode;

	// Expocode of the datasets immediately prior to the active expocode
	private String previousExpocode;

	/**
	 * Creates an empty OME manager page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page with the
	 * specified set of cruises. 
	 */
	OmeManagerPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		setUsername(null);
		expocodes = new TreeSet<String>();
		firstExpocode = "";
		activeExpocode = "";
		previousExpocode = "";
		exposIter = expocodes.iterator();

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		editNewRadio.setText(EDIT_NEW_RADIO_TEXT);
		editPreviousRadio.setText(EDIT_PREVIOUS_RADIO_TEXT);
		editUploadRadio.setText(EDIT_UPLOAD_RADIO_TEXT);
		editButton.setText(EDIT_TEXT);
		editButton.setTitle(EDIT_TOOLTIP_HELP);
		doneButton.setText(DONE_TEXT);
		doneButton.setTitle(DONE_TOOLTIP_HELP);
	}

	/**
	 * Display the OME manager page in the RootLayoutPanel for the
	 * given set of cruises.  Adds this page to the page history.
	 * 
	 * @param cruises
	 * 		open the OME with the metadata for these cruises 
	 */
	static void showPage(DashboardCruiseList cruises) {
		if ( singleton == null )
			singleton = new OmeManagerPage();
		singleton.updateCruise(cruises);
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.EDIT_METADATA.name(), false);
	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the given username.
	 */
	static void redisplayPage(String username) {
		if ( (username == null) || username.isEmpty() || 
			 (singleton == null) || ! singleton.getUsername().equals(username) ) {
			CruiseListPage.showPage();
		}
		else {
			SocatUploadDashboard.updateCurrentPage(singleton);
			// If was complete, reset to the start
			if ( singleton.activeExpocode.isEmpty() ) {
				try {
					singleton.exposIter = singleton.expocodes.iterator();
					singleton.activeExpocode = singleton.exposIter.next();
					singleton.previousExpocode = "";
					singleton.updateIntro();
				} catch ( Exception ex ) {
					// Should never happen - leave empty
				}
			}
		}
	}

	/**
	 * Updates this page with the username and 
	 * the cruises in the given set of cruise.
	 * 
	 * @param cruises
	 * 		open the OME with the metadata for this set of cruises
	 */
	private void updateCruise(DashboardCruiseList cruises) {
		// Update the current username
		setUsername(cruises.getUsername());
		expocodes.clear();
		for (String expo : cruises.keySet() ) {
			expocodes.add(expo);
		}
		exposIter = expocodes.iterator();
		try {
			firstExpocode = exposIter.next();
		} catch ( Exception ex ) {
			// Should not happen as the list should not be empty
			firstExpocode = "";
		}
		activeExpocode = firstExpocode;
		previousExpocode = "";

		userInfoLabel.setText(WELCOME_INTRO + getUsername());

		updateIntro();
	}

	/**
	 * Updates the HTML intro message to reflect the current set of
	 * expocodes and the currently active expocode.  Also enables
	 * or disables the radio buttons appropriately.
	 */
	private void updateIntro() {
		// Update the HTML intro
		String introMsg = CRUISE_HTML_INTRO_PROLOGUE;
		for ( String expo : expocodes ) {
			if ( activeExpocode.equals(expo) ) {
				introMsg += "<li><b>" + SafeHtmlUtils.htmlEscape(expo) + "</b></li>";
			}
			else {
				introMsg += "<li>" + SafeHtmlUtils.htmlEscape(expo) + "</li>";
			}
		}
		introMsg += CRUISE_HTML_INTRO_EPILOGUE;
		introHtml.setHTML(introMsg);

		String cruiseNameMsg;
		if ( activeExpocode.isEmpty() ) {
			cruiseNameMsg = CRUISE_HTML_DONE_MSG;
		}
		else {
			cruiseNameMsg  = CRUISE_HTML_ACTIVE_PROLOGUE;
			cruiseNameMsg += SafeHtmlUtils.htmlEscape(activeExpocode);
			cruiseNameMsg += CRUISE_HTML_ACTIVE_EPILOGUE;
		}
		cruiseNameHtml.setHTML(cruiseNameMsg);

		if ( activeExpocode.isEmpty() ) {
			// Gone through the list - only done button
			editNewRadio.setText(EDIT_NEW_RADIO_TEXT);
			editPreviousRadio.setText(EDIT_PREVIOUS_RADIO_TEXT);
			editNewRadio.setValue(true);
			editNewRadio.setEnabled(false);
			editPreviousRadio.setEnabled(false);
			editUploadRadio.setEnabled(false);
			editButton.setEnabled(false);
		}
		else if ( previousExpocode.isEmpty() ) {
			// First expocode in the list - no previous option
			editNewRadio.setText(EDIT_NEW_RADIO_TEXT +
					SafeHtmlUtils.htmlEscape(activeExpocode));
			editPreviousRadio.setText(EDIT_PREVIOUS_RADIO_TEXT);
			editNewRadio.setValue(true);
			editNewRadio.setEnabled(true);
			editPreviousRadio.setEnabled(false);
			editUploadRadio.setEnabled(true);
			editButton.setEnabled(true);
		}
		else {
			// Middle of the list - all options available
			editNewRadio.setText(EDIT_NEW_RADIO_TEXT +
					SafeHtmlUtils.htmlEscape(activeExpocode));
			editPreviousRadio.setText(EDIT_PREVIOUS_RADIO_TEXT + 
					SafeHtmlUtils.htmlEscape(previousExpocode));
			editPreviousRadio.setValue(true);
			editNewRadio.setEnabled(true);
			editPreviousRadio.setEnabled(true);
			editUploadRadio.setEnabled(true);
			editButton.setEnabled(true);
		}
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("doneButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the cruise list page which might have been updated
		CruiseListPage.showPage();
	}

	@UiHandler("editButton") 
	void editButtonOnClick(ClickEvent event) {
		// Get the selected option for opening the OME
		String prevExpo;
		if ( editPreviousRadio.getValue() ) {
			prevExpo = previousExpocode;
		}
		else {
			prevExpo = "";
		}

		final boolean editUpload;
		if ( editUploadRadio.getValue() ) {
			editUpload = true;
		}
		else {
			editUpload = false;
		}

		// Show the wait cursor
		SocatUploadDashboard.showWaitCursor();

		// Open the OME for the active expocode
		service.getOmeXmlPath(getUsername(), activeExpocode, prevExpo, 
				new AsyncCallback<String>() {
			@Override
			public void onSuccess(String fileAbsPath) {
				// Go on to the next dataset
				previousExpocode = activeExpocode;
				try {
					activeExpocode = exposIter.next();
				} catch ( Exception ex ) {
					// No more expocodes
					activeExpocode = "";
				}
				updateIntro();
				if ( editUpload ) {
					// Open a new window with the OME upload page
					Window.open(OME_UPLOAD_FILE_URL + fileAbsPath, 
							"OME for " + activeExpocode, WINDOW_FEATURES);
				}
				else {
					// Open a new window with the OME initialized from indicated file
					Window.open(OME_SHOW_FILE_URL + fileAbsPath, 
							"OME for " + activeExpocode, WINDOW_FEATURES);
				}
				// Show the normal cursor
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				// Go on to the next dataset
				previousExpocode = activeExpocode;
				try {
					activeExpocode = exposIter.next();
				} catch ( Exception ex1 ) {
					// No more expocodes
					activeExpocode = "";
				}
				updateIntro();
				// Show the failure message along with the exception message
				SocatUploadDashboard.showFailureMessage(OPEN_OME_FAIL_MSG, ex);
				// Show the normal cursor
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

}
