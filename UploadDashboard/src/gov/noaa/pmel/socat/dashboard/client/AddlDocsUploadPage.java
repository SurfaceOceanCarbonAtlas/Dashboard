/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Metadata document upload page, for either a single cruise or multiple
 * cruises.  The introduction is modified to be appropriate for these
 * two cases.
 * 
 * @author Karl Smith
 */
public class AddlDocsUploadPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String UPLOAD_TEXT = "Upload";
	private static final String CANCEL_TEXT = "Cancel";

	private static final String MULTI_CRUISE_HTML_INTRO_PROLOGUE = 
			"<b>Upload an Additional Document</b>" +
			"<br /><br />" +
			"Because multiple cruises were selected, you can only upload " +
			"additional documents.  The uploaded document will be duplicated " +
			"to uniquely associate a copy to each cruise selected, either " +
			"as a new document or replacing an existing document. " +
			"<br /><br />" +
			"Upload additional documents for the cruises: <b>";
	private static final String SINGLE_CRUISE_HTML_INTRO_PROLOGUE = 
			"<b>Upload an Additional Document</b>" +
			"<br /><br />" +
			"Upload an additional document for the cruise: <b>";
	private static final String CRUISE_HTML_INTRO_EPILOGUE = "</b>";

	private static final String NO_FILE_ERROR_MSG = 
			"Please select a document to upload";

	private static final String OVERWRITE_WARNING_MSG_PROLOGUE = 
			"The following documents for this cruise will be " +
			"overwritten: <ul>";
	private static final String OVERWRITE_WARNING_MSG_EPILOGUE =
			"</ul> Do you wish to proceed?";
	private static final String OVERWRITE_YES_TEXT = "Yes";
	private static final String OVERWRITE_NO_TEXT = "No";

	interface AddlDocsUploadPageUiBinder extends UiBinder<Widget, AddlDocsUploadPage> {
	}

	private static AddlDocsUploadPageUiBinder uiBinder = 
			GWT.create(AddlDocsUploadPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField FormPanel uploadForm;
	@UiField FileUpload docUpload;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden timestampToken;
	@UiField Hidden expocodesToken;
	@UiField Button uploadButton;
	@UiField Button cancelButton;

	private String username;
	private HashSet<DashboardCruise> cruises;
	private TreeSet<String> expocodes;
	private DashboardAskPopup askOverwritePopup;
	private boolean okayToOverwrite;

	// Singleton instance of this page
	private static AddlDocsUploadPage singleton = null;

	AddlDocsUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));
		username = "";
		cruises = new HashSet<DashboardCruise>();
		expocodes = new TreeSet<String>();
		askOverwritePopup = null;

		logoutButton.setText(LOGOUT_TEXT);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "MetadataUploadService");

		uploadButton.setText(UPLOAD_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display the additional documents upload page in the RootLayoutPanel
	 * for a set of given cruises.
	 * Adds this page to the page history.
	 * 
	 * @param cruises
	 * 		add/replace the additional documents in these cruises 
	 */
	static void showPage(HashSet<DashboardCruise> cruises) {
		if ( singleton == null )
			singleton = new AddlDocsUploadPage();
		singleton.updateCruises(cruises);
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.ADDL_DOCS_UPLOAD.name(), false);
	}

	/**
	 * Display the additional documents upload page in the RootLayoutPanel
	 * for the given cruise.  Adds this page to the page history.
	 * 
	 * @param cruiseExpocode
	 * 		add/replace the additional documents for this cruise
	 * @param addlDocNames
	 * 		set of additional document filenames currently
	 * 		associated with this cruise
	 */
	static void showPage(String cruiseExpocode, TreeSet<String> addlDocNames) {
		// Create a DashboardCruise using the above information
		DashboardCruise cruise = new DashboardCruise();
		cruise.setExpocode(cruiseExpocode);
		cruise.setAddlDocNames(addlDocNames);
		// Create a set containing just this cruise
		HashSet<DashboardCruise> cruiseSet = new HashSet<DashboardCruise>();
		cruiseSet.add(cruise);
		// Show the page using this cruise set
		showPage(cruiseSet);
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
				History.newItem(PagesEnum.ADDL_DOCS_UPLOAD.name(), false);
		}
	}

	/**
	 * Updates this page with the latest username from DashboardLoginPage
	 * and the cruises from the given set of cruises.  The introduction 
	 * is modified to reflect whether one or multiple cruises are given.
	 * 
	 * @param cruises
	 * 		associated the uploaded metadata document to these cruises
	 */
	private void updateCruises(HashSet<DashboardCruise> cruises) {
		// Update the current username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);

		// Update the cruises associated with this page
		this.cruises.clear();
		this.cruises.addAll(cruises);
		expocodes.clear();
		for ( DashboardCruise cruz : cruises )
			expocodes.add(cruz.getExpocode());

		// Update the HTML intro naming the cruises
		StringBuilder sb = new StringBuilder();
		if ( expocodes.size() > 1 )
			sb.append(MULTI_CRUISE_HTML_INTRO_PROLOGUE);
		else
			sb.append(SINGLE_CRUISE_HTML_INTRO_PROLOGUE);
		boolean first = true;
		for ( String expo : expocodes ) {
			if ( first )
				first = false;
			else
				sb.append(",  ");
			sb.append(SafeHtmlUtils.htmlEscape(expo));
		}
		sb.append(CRUISE_HTML_INTRO_EPILOGUE);
		introHtml.setHTML(sb.toString());

		// Clear the hidden tokens just to be safe
		usernameToken.setValue("");
		passhashToken.setValue("");
		timestampToken.setValue("");
		expocodesToken.setValue("");

		// Set to ask about any overwrites
		okayToOverwrite = false;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		if ( expocodes.size() == 1 ) {
			// Return to the additional documents manager page exactly as it was
			AddlDocsManagerPage.redisplayPage(true);
		}
		else {
			// Return to the cruise list page exactly as it was
			CruiseListPage.redisplayPage(true);
		}
	}

	@UiHandler("uploadButton") 
	void uploadButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		String localTimestamp = 
				DateTimeFormat.getFormat("yyyy-MM-dd HH:mm")
							  .format(new Date());
		timestampToken.setValue(localTimestamp);
		expocodesToken.setValue(
				DashboardUtils.encodeStringArrayList(
						new ArrayList<String>(expocodes)));
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String uploadFilename = DashboardUtils.baseName(docUpload.getFilename());
		if ( uploadFilename.isEmpty() ) {
			event.cancel();
			usernameToken.setValue("");
			passhashToken.setValue("");
			timestampToken.setValue("");
			expocodesToken.setValue("");
			okayToOverwrite = false;
			SocatUploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}

		// If this is a resubmit with overwriting, let the submit go through
		// (event is not cancelled)
		if ( okayToOverwrite ) {
			okayToOverwrite = false;
			return;
		}

		// Check for any overwrites that will happen
		String message = OVERWRITE_WARNING_MSG_PROLOGUE;
		boolean willOverwrite = false;
		for ( DashboardCruise cruz : cruises ) {
			if ( cruz.getOmeFilename().equals(uploadFilename) ) {
				message += "<li>" + SafeHtmlUtils.htmlEscape(uploadFilename) + 
						" <em>OME metadata for cruise " + SafeHtmlUtils.htmlEscape(cruz.getExpocode()) + "</em></li>";
				willOverwrite = true;
			}
			else if ( cruz.getAddlDocNames().contains(uploadFilename) ) {
				message += "<li>" + SafeHtmlUtils.htmlEscape(uploadFilename) + 
						" <em>for cruise " + SafeHtmlUtils.htmlEscape(cruz.getExpocode()) + "</em></li>";
				willOverwrite = true;
			}
		}

		// If an overwrite will occur, cancel this submit and ask for confirmation
		if ( willOverwrite ) {
			event.cancel();
			message += OVERWRITE_WARNING_MSG_EPILOGUE;
			if ( askOverwritePopup == null ) {
				askOverwritePopup = new DashboardAskPopup(OVERWRITE_YES_TEXT, 
						OVERWRITE_NO_TEXT, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						// Resubmit only if yes; clear tokens if no or null
						if ( result == true ) {
							okayToOverwrite = true;
							uploadForm.submit();
						}
						else {
							usernameToken.setValue("");
							passhashToken.setValue("");
							timestampToken.setValue("");
							expocodesToken.setValue("");
							okayToOverwrite = false;
						}
					}
					@Override
					public void onFailure(Throwable ex) {
						// Never called
						;
					}
				});
			}
			askOverwritePopup.askQuestion(message);
			return;
		}

		// Nothing overwritten, let the submit continue
		// (event not cancelled)
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		usernameToken.setValue("");
		passhashToken.setValue("");
		timestampToken.setValue("");
		expocodesToken.setValue("");
		okayToOverwrite = false;

		// Check the result returned
		String resultMsg = event.getResults();
		if ( resultMsg == null ) {
			SocatUploadDashboard.showMessage(
					"Unexpected null result from submit complete");
			return;
		}

		String[] tagMsg = resultMsg.split("\n", 2);
		if ( tagMsg.length < 2 ) {
			// probably an error response; just display the entire message
			SocatUploadDashboard.showMessage(SafeHtmlUtils.htmlEscape(resultMsg));
		}
		else if ( DashboardUtils.FILE_CREATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file(s) created or updated
			if ( expocodes.size() == 1 ) {
				// return to the metadata manager, having it
				// request the updated cruise from the server 
				AddlDocsManagerPage.showPage(expocodes.first());
			}
			else {
				// return to the cruise list, having it request 
				// the updated cruises for the user from the server
				CruiseListPage.showPage(false);
			}
		}
		else {
			// Unknown response with a newline, just display the entire message
			SocatUploadDashboard.showMessage(SafeHtmlUtils.htmlEscape(resultMsg));
		}
	}

}
