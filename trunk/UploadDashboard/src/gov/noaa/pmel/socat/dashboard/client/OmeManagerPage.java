/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.Date;

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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 */
public class OmeManagerPage extends Composite {

	private static final String WELCOME_INTRO = "Welcome ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String UPLOAD_TEXT = "Upload";
	private static final String CANCEL_TEXT = "Cancel";

	private static final String CRUISE_HTML_INTRO_PROLOGUE = 
			"<b>Manage OME Metadata</b>" +
			"<br /><br />" +
			"At this time we can only upload OME metadata files generated " +
			"from the CDIAC OME site.  Please: " +
			"<ul>" +
			"<li>in a new browser tab or window, go to the CDIAC OME site <br />" +
			"<a href=\"http://mercury-ops2.ornl.gov/OceanOME/newForm.htm\">" +
			"http://mercury-ops2.ornl.gov/OceanOME/newForm.htm</a></li>" +
			"<li>fill in the appropriate metadata</li>" +
			"<li><em>Save Locally</em> (the button above the CAPTCHA)</li>" +
			"</ul>" +
			"This will generate an OME metadata file on your system that " +
			"can be uploaded here. " +
			"<br /><br />" +
			"Upload OME metadata for the cruise: <b>";
	private static final String CRUISE_HTML_INTRO_EPILOGUE = "</b>";

	private static final String NO_FILE_ERROR_MSG = 
			"Please select an OME metadata file to upload";

	private static final String OVERWRITE_WARNING_MSG_PROLOGUE = 
			"The following documents for the cruise will be " +
			"removed or overwritten: <ul>";
	private static final String OVERWRITE_WARNING_MSG_EPILOGUE =
			"</ul> Do you wish to proceed?";
	private static final String OVERWRITE_YES_TEXT = "Yes";
	private static final String OVERWRITE_NO_TEXT = "No";

	interface OmeManagerPageUiBinder extends UiBinder<Widget, OmeManagerPage> {
	}

	private static OmeManagerPageUiBinder uiBinder = 
			GWT.create(OmeManagerPageUiBinder.class);

	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField FormPanel uploadForm;
	@UiField FileUpload omeUpload;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden timestampToken;
	@UiField Hidden expocodesToken;
	@UiField Hidden omeToken;
	@UiField Button uploadButton;
	@UiField Button cancelButton;

	private String username;
	private DashboardCruise cruise;
	private DashboardAskPopup askOverwritePopup;
	private boolean okayToOverwrite;

	// Singleton instance of this page
	private static OmeManagerPage singleton;
	
	OmeManagerPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		username = "";
		cruise = null;
		askOverwritePopup = null;
		okayToOverwrite = false;

		logoutButton.setText(LOGOUT_TEXT);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "MetadataUploadService");

		uploadButton.setText(UPLOAD_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display the OME metadata upload page in the RootLayoutPanel
	 * for the given cruise.  Adds this page to the page history.
	 * 
	 * @param cruises
	 * 		add/replace the OME metadata for this cruise 
	 */
	static void showPage(DashboardCruise cruise) {
		if ( singleton == null )
			singleton = new OmeManagerPage();
		singleton.updateCruise(cruise);
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.OME_MANAGER.name(), false);
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
				History.newItem(PagesEnum.OME_MANAGER.name(), false);
		}
	}

	/**
	 * Updates this page with the latest username from DashboardLoginPage
	 * and the given set of cruise.
	 * 
	 * @param cruise
	 * 		associate the uploaded OME metadata to this cruise
	 */
	private void updateCruise(DashboardCruise cruise) {
		// Update the current username
		username = DashboardLoginPage.getUsername();
		userInfoLabel.setText(WELCOME_INTRO + username);

		// Update the cruise associated with this page
		this.cruise = cruise;

		// Update the HTML intro naming the cruise
		introHtml.setHTML(CRUISE_HTML_INTRO_PROLOGUE + 
				SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + 
				CRUISE_HTML_INTRO_EPILOGUE);

		// Clear the hidden tokens just to be safe
		usernameToken.setValue("");
		passhashToken.setValue("");
		timestampToken.setValue("");
		expocodesToken.setValue("");
		omeToken.setValue("");

		// Set to ask about any overwrites
		okayToOverwrite = false;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the cruise list page exactly as it was
		CruiseListPage.redisplayPage(true);
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
		expocodesToken.setValue("[ \"" + cruise.getExpocode() + "\" ]");
		omeToken.setValue("true");
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String uploadFilename = DashboardUtils.baseName(omeUpload.getFilename());
		if ( uploadFilename.isEmpty() ) {
			event.cancel();
			usernameToken.setValue("");
			passhashToken.setValue("");
			timestampToken.setValue("");
			expocodesToken.setValue("");
			omeToken.setValue("");
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

		// Check for any overwrite that will happen
		String message = OVERWRITE_WARNING_MSG_PROLOGUE;
		boolean willOverwrite = false;
		if ( ! cruise.getOmeFilename().isEmpty() ) {
			message += "<li>" + SafeHtmlUtils.htmlEscape(cruise.getOmeFilename()) + 
					"<br />&nbsp;&nbsp;&nbsp;&nbsp;<em>OME metadata for cruise " + 
					SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + "</em></li>";
			willOverwrite = true;
		}
		if ( cruise.getAddlDocNames().contains(uploadFilename) ) {
			message += "<li>" + SafeHtmlUtils.htmlEscape(uploadFilename) + 
					"<br />&nbsp;&nbsp;&nbsp;&nbsp;<em>addl document for cruise " + 
					SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + "</em></li>";
			willOverwrite = true;
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
							omeToken.setValue("");
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
		omeToken.setValue("");
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
			// cruise file created or updated
			// return to the cruise list, having it request 
			// the updated cruises for the user from the server
			CruiseListPage.showPage(false);
		}
		else {
			// Unknown response with a newline, just display the entire message
			SocatUploadDashboard.showMessage(SafeHtmlUtils.htmlEscape(resultMsg));
		}
	}

}
