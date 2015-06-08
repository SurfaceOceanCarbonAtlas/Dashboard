/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;

import java.util.ArrayList;
import java.util.ListIterator;
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
import com.google.gwt.user.client.ui.TextBox;
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
			"with the indicated content and moves to the next dataset";

	private static final String PREVIOUS_TEXT = "Previous";
	private static final String PREVIOUS_TOOLTIP_HELP = "Moves to the previous dataset without opening the OME";

	private static final String NEXT_TEXT = "Next";
	private static final String NEXT_TOOLTIP_HELP = "Moves to the next dataset without opening the OME";

	private static final String DONE_TEXT = "Done";
	private static final String DONE_TOOLTIP_HELP = "Returns to your list of displayed datasets";

	private static final String CRUISE_HTML_INTRO_PROLOGUE = 
			"<p>Sequentially open the online metadata editor (OME) " +
			"for each of the following datasets: <ul>";
	private static final String CRUISE_HTML_INTRO_EPILOGUE = 
			"</ul>" +
			"(Note: you will need to allow popups from this site.)</p>";

	private static final String CRUISE_HTML_ACTIVE_PROLOGUE = "Open the OME for <b>";
	private static final String CRUISE_HTML_ACTIVE_EPILOGUE = "</b>:";
	private static final String CRUISE_HTML_DONE_MSG = "Select Done when finished editing in the OME";

	private static final String EDIT_EXISTING_RADIO_TEXT = "with any existing metadata for ";
	private static final String EDIT_MERGED_RADIO_TEXT = "copying applicable metadata from ";
	private static final String EDIT_MERGED_TEXT_HOVER_HELP = "expocode of the dataset from which to copy/merge applicable metadata values";
	private static final String EDIT_UPLOAD_RADIO_TEXT = "using contents of your locally-saved OME XML file";

	private static final String NO_EXPOCODE_ERRMSG = "No expocode is given for the dataset from which to copy/merge applicable metadata values";

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
	@UiField RadioButton editExistingRadio;
	@UiField RadioButton editMergedRadio;
	@UiField TextBox editMergedText;
	@UiField RadioButton editUploadRadio;
	@UiField Button editButton;
	@UiField Button previousButton;
	@UiField Button nextButton;
	@UiField Button doneButton;

	// Singleton instance of this page
	private static OmeManagerPage singleton;

	// Ordered set of unique expocodes to work with
	private ArrayList<String> expocodes;

	// Iterator over the list of expocodes;
	private ListIterator<String> exposIter;

	// Current expocode
	private String activeExpocode;

	// Previously active expocode
	private String priorActiveExpocode;

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
		expocodes = new ArrayList<String>();
		exposIter = expocodes.listIterator();
		activeExpocode = "";
		priorActiveExpocode = "";

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		editExistingRadio.setText(EDIT_EXISTING_RADIO_TEXT);
		editMergedRadio.setText(EDIT_MERGED_RADIO_TEXT);
		editMergedText.setText("");
		editMergedText.setTitle(EDIT_MERGED_TEXT_HOVER_HELP);
		editUploadRadio.setText(EDIT_UPLOAD_RADIO_TEXT);

		editButton.setText(EDIT_TEXT);
		editButton.setTitle(EDIT_TOOLTIP_HELP);
		previousButton.setText(PREVIOUS_TEXT);
		previousButton.setTitle(PREVIOUS_TOOLTIP_HELP);
		nextButton.setText(NEXT_TEXT);
		nextButton.setTitle(NEXT_TOOLTIP_HELP);
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
		}
	}

	/**
	 * Updates this page with the username and the cruises in the given set of cruise.
	 * 
	 * @param cruises
	 * 		open the OME with the metadata for this set of cruises
	 */
	private void updateCruise(DashboardCruiseList cruises) {
		setUsername(cruises.getUsername());

		TreeSet<String> exposSet = new TreeSet<String>();
		for (String expo : cruises.keySet() ) {
			exposSet.add(expo);
		}
		expocodes.clear();
		expocodes.addAll(exposSet);
		exposIter = expocodes.listIterator();
		try {
			activeExpocode = exposIter.next();
		} catch (Exception ex) {
			activeExpocode = "";
		}
		priorActiveExpocode = "";

		userInfoLabel.setText(WELCOME_INTRO + getUsername());
		editExistingRadio.setValue(true);
		updateIntro();
	}

	/**
	 * Updates the HTML intro message to reflect the current set of
	 * expocodes and the currently active expocode.
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

		editMergedText.setText(SafeHtmlUtils.htmlEscape(priorActiveExpocode));

		if ( activeExpocode.isEmpty() ) {
			// Gone through the list
			editExistingRadio.setText(EDIT_EXISTING_RADIO_TEXT);
			editExistingRadio.setEnabled(false);
			editMergedRadio.setEnabled(false);
			editUploadRadio.setEnabled(false);
			editButton.setEnabled(false);
		}
		else {
			// Start or middle of the list
			editExistingRadio.setText(EDIT_EXISTING_RADIO_TEXT + SafeHtmlUtils.htmlEscape(activeExpocode));
			editExistingRadio.setEnabled(true);
			editMergedRadio.setEnabled(true);
			editUploadRadio.setEnabled(true);
			editButton.setEnabled(true);
		}

		try {
			if ( activeExpocode.equals(expocodes.get(0)) )
				previousButton.setEnabled(false);
			else
				previousButton.setEnabled(true);
		} catch (Exception ex) {
			// No expocodes in the list - should not happen
			previousButton.setEnabled(false);
		}

		nextButton.setEnabled(exposIter.hasNext());
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("previousButton")
	void previousButtonOnClick(ClickEvent event) {
		priorActiveExpocode = activeExpocode;
		if ( activeExpocode.isEmpty() ) {
			// list iterator is at the end so get the final expocode
			try {
				// Move the list iterator back before the final expocode
				exposIter.previous();
				// Get the final expocode and move the list iterator to the end
				activeExpocode = exposIter.next();
			} catch (Exception ex) {
				// No expocodes in the list - should not happen
				activeExpocode = "";
			}
		}
		else {
			try {
				// Move the list iterator back before the current expocode
				exposIter.previous();
				// Move the list iterator back before the previous expocode
				exposIter.previous();
			} catch (Exception ex) {
				// Already at the start of the list - should not happen but continue on
				;
			}
			try {
				// Get the expocode and move the list iterator after it
				activeExpocode = exposIter.next();
			} catch (Exception ex) {
				// No expocodes in the list - should not happen
				activeExpocode = "";
			}
		}
		updateIntro();
	}

	@UiHandler("nextButton")
	void nextButtonOnClick(ClickEvent event) {
		priorActiveExpocode = activeExpocode;
		try {
			activeExpocode = exposIter.next();
		} catch (Exception ex) {
			// At the end
			activeExpocode = "";
		}
		updateIntro();
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
		if ( editMergedRadio.getValue() ) {
			prevExpo = editMergedText.getText().trim();
			if ( prevExpo.isEmpty() ) {
				SocatUploadDashboard.showMessage(NO_EXPOCODE_ERRMSG);
				return;
			}
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
				priorActiveExpocode = activeExpocode;
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
				priorActiveExpocode = activeExpocode;
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
