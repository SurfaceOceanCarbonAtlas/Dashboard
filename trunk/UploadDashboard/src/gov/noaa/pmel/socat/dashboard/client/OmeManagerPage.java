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
	private static final String EDIT_TEXT = "Open OME";
	private static final String DONE_TEXT = "Done";

	private static final String CRUISE_HTML_INTRO_PROLOGUE = 
			"<p>" +
			"This form is used to open the online metadata editor (OME) " +
			"for the following datasets.  " +
			"</p><p>" +
			"Datasets: <ul>";
	private static final String CRUISE_HTML_INTRO_EPILOGUE = "</ul>";
	private static final String CRUISE_HTML_ACTIVE_PROLOGUE = "<p>Open OME for <b>";
	private static final String CRUISE_HTML_ACTIVE_EPILOGUE = "</b> using:</p>";
	private static final String CRUISE_HTML_DONE_MSG = "<p>Select Done when finished editing</p>";

	private static final String EDIT_NEW_RADIO_TEXT = "existing metadata for this dataset (if any)";
	private static final String EDIT_PREVIOUS_RADIO_TEXT = "applicable metadata from the previous dataset";
	private static final String EDIT_UPLOAD_RADIO_TEXT = "contents of your locally-saved OME XML file";

	private static final String OPEN_OME_FAIL_MSG = "Opening the metadata editor failed";

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
	@UiField RadioButton editNewRadio;
	@UiField RadioButton editPreviousRadio;
	@UiField RadioButton editUploadRadio;
	@UiField Button editButton;
	@UiField Button doneButton;

	// Singleton instance of this page
	private static OmeManagerPage singleton;

	// Ordered set of expocodes to work with
	private TreeSet<String> expocodes;

	// First expocode in the ordered set
	private String firstExpocode;

	// Open the OME with metadata for this expocode
	private String activeExpocode;

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

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		editNewRadio.setText(EDIT_NEW_RADIO_TEXT);
		editPreviousRadio.setText(EDIT_PREVIOUS_RADIO_TEXT);
		editUploadRadio.setText(EDIT_UPLOAD_RADIO_TEXT);
		editButton.setText(EDIT_TEXT);
		doneButton.setText(DONE_TEXT);
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
					singleton.activeExpocode = singleton.expocodes.first();
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
		try {
			firstExpocode = expocodes.first();
		} catch ( Exception ex ) {
			// Should not happen as the list should not be empty
			firstExpocode = "";
		}
		activeExpocode = firstExpocode;

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
		if ( activeExpocode.isEmpty() ) {
			introMsg += CRUISE_HTML_DONE_MSG;
		}
		else {
			introMsg += CRUISE_HTML_ACTIVE_PROLOGUE;
			introMsg += SafeHtmlUtils.htmlEscape(activeExpocode);
			introMsg += CRUISE_HTML_ACTIVE_EPILOGUE;
		}
		introHtml.setHTML(introMsg);

		// Enable/Disable the radio buttons and set the default selection
		if ( activeExpocode.isEmpty() ) {
			editNewRadio.setEnabled(false);
			editPreviousRadio.setEnabled(false);
			editUploadRadio.setEnabled(false);
			editButton.setEnabled(false);
		}
		else if ( activeExpocode.equals(firstExpocode) ) {
			editNewRadio.setEnabled(true);
			editPreviousRadio.setEnabled(false);
			editUploadRadio.setEnabled(true);
			editNewRadio.setValue(true);
			editButton.setEnabled(true);
		}
		else {
			editNewRadio.setEnabled(true);
			editPreviousRadio.setEnabled(true);
			editUploadRadio.setEnabled(true);
			editPreviousRadio.setValue(true);
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
		String previousExpocode = "";
		boolean editUpload = false;
		if ( editPreviousRadio.getValue() ) {
			try {
				previousExpocode = expocodes.headSet(activeExpocode).last();
			} catch ( Exception ex ) {
				// Should never happen as this should not be checked if the first - leave empty
			}
		}
		else if ( editUploadRadio.getValue() ) {
			editUpload = true;
		}

		// Show the wait cursor
		SocatUploadDashboard.showWaitCursor();

		// Open the OME for the active expocode
		service.openOME(activeExpocode, previousExpocode, editUpload, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean success) {
				if ( success ) {
					try {
						Iterator<String> iter = expocodes.tailSet(activeExpocode).iterator();
						iter.next();
						activeExpocode = iter.next();
						// Another expocode found - make it active and update the page message
						updateIntro();
					} catch ( Exception ex ) {
						// No more expocodes
						activeExpocode = "";
						updateIntro();
					}
				}
				else {
					SocatUploadDashboard.showMessage(OPEN_OME_FAIL_MSG);
				}
				// Show the normal cursor
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(OPEN_OME_FAIL_MSG, ex);
				// Show the normal cursor
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

}
