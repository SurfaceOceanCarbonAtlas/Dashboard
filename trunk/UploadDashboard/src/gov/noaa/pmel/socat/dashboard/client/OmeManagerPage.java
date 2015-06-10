/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
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
public class OmeManagerPage extends CompositeWithUsername {

	private static final String TITLE_TEXT = "Edit OME Metadata";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String UPLOAD_TEXT = "Upload";
	private static final String CANCEL_TEXT = "Cancel";

	private static final String CRUISE_HTML_INTRO_PROLOGUE = 
			"<p>At this time, the system only uploads SOCAT OME XML metadata files.</p>" +
			"<p>To generate a SOCAT OME XML metadata file to upload: <ul>" +
			"<li>Go to the CDIAC OME site " +
			"<a href=\"http://mercury-ops2.ornl.gov/socatome/newForm.htm\" target=\"_blank\">" +
			"http://mercury-ops2.ornl.gov/socatome/newForm.htm</a></li>" +
			"<li>Fill in the appropriate metadata</li>" +
			"<li>Save a local copy (preferrably with validation)</li>" +
			"</ul>" +
			"This will creates a SOCAT OME XML metadata file on your system that can be uploaded here. " +
			"</p><p>" +
			"Dataset: <ul><li>";
	private static final String CRUISE_HTML_INTRO_EPILOGUE = "</li></ul></p>";

	private static final String NO_FILE_ERROR_MSG = 
			"Please select an SOCAT OME XML metadata file to upload";

	private static final String OVERWRITE_WARNING_MSG = 
			"The SOCAT OME XML metadata for this dataset will be overwritten.  Do you wish to proceed?";
	private static final String OVERWRITE_YES_TEXT = "Yes";
	private static final String OVERWRITE_NO_TEXT = "No";

	private static final String UNEXPLAINED_FAIL_MSG = 
			"<h3>Upload failed.</h3>" + 
			"<p>Unexpectedly, no explanation of the failure was given</p>";
	private static final String EXPLAINED_FAIL_MSG_START = 
			"<h3>Upload failed.</h3>" +
			"<p><pre>\n";
	private static final String EXPLAINED_FAIL_MSG_END = 
			"</pre></p>";

	interface OmeManagerPageUiBinder extends UiBinder<Widget, OmeManagerPage> {
	}

	private static OmeManagerPageUiBinder uiBinder = 
			GWT.create(OmeManagerPageUiBinder.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField FormPanel uploadForm;
	@UiField FileUpload omeUpload;
	@UiField Hidden timestampToken;
	@UiField Hidden expocodesToken;
	@UiField Hidden omeToken;
	@UiField Button uploadButton;
	@UiField Button cancelButton;

	private DashboardCruise cruise;
	private DashboardAskPopup askOverwritePopup;

	// Singleton instance of this page
	private static OmeManagerPage singleton;
	
	OmeManagerPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		setUsername(null);
		cruise = null;
		askOverwritePopup = null;

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "MetadataUploadService");

		clearTokens();

		uploadButton.setText(UPLOAD_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display the OME metadata upload page in the RootLayoutPanel
	 * for the given cruise.  Adds this page to the page history.
	 * 
	 * @param cruises
	 * 		add/replace the OME metadata for the cruise in this list 
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
	 * Updates this page with the username and the cruise in the given set of cruise.
	 * 
	 * @param cruises
	 * 		associate the uploaded OME metadata to the cruise in this set of cruises
	 */
	private void updateCruise(DashboardCruiseList cruises) {
		// Update the current username
		setUsername(cruises.getUsername());
		userInfoLabel.setText(WELCOME_INTRO + getUsername());

		// Update the cruise associated with this page
		cruise = cruises.values().iterator().next();

		// Update the HTML intro naming the cruise
		introHtml.setHTML(CRUISE_HTML_INTRO_PROLOGUE + 
				SafeHtmlUtils.htmlEscape(cruise.getExpocode()) + 
				CRUISE_HTML_INTRO_EPILOGUE);

		// Clear the hidden tokens just to be safe
		clearTokens();
	}

	/**
	 * Clears all the Hidden tokens on the page. 
	 */
	private void clearTokens() {
		timestampToken.setValue("");
		expocodesToken.setValue("");
		omeToken.setValue("");
	}

	/**
	 * Assigns all the Hidden tokens on the page. 
	 */
	private void assignTokens() {
		String localTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z").format(new Date());
		timestampToken.setValue(localTimestamp);
		expocodesToken.setValue("[ \"" + cruise.getExpocode() + "\" ]");
		omeToken.setValue("true");
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the cruise list page which might have been updated
		CruiseListPage.showPage();
	}

	@UiHandler("uploadButton") 
	void uploadButtonOnClick(ClickEvent event) {
		// Make sure a file was selected
		String uploadFilename = DashboardUtils.baseName(omeUpload.getFilename());
		if ( uploadFilename.isEmpty() ) {
			SocatUploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}

		// If an overwrite will occur, ask for confirmation
		if ( ! cruise.getOmeTimestamp().isEmpty() ) {
			if ( askOverwritePopup == null ) {
				askOverwritePopup = new DashboardAskPopup(OVERWRITE_YES_TEXT, 
						OVERWRITE_NO_TEXT, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						// Submit only if yes
						if ( result == true ) {
							assignTokens();
							uploadForm.submit();
						}
					}
					@Override
					public void onFailure(Throwable ex) {
						// Never called
						;
					}
				});
			}
			askOverwritePopup.askQuestion(OVERWRITE_WARNING_MSG);
			return;
		}

		// Nothing overwritten, submit the form
		assignTokens();
		uploadForm.submit();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		SocatUploadDashboard.showWaitCursor();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		clearTokens();
		processResultMsg(event.getResults());
		// Restore the usual cursor
		SocatUploadDashboard.showAutoCursor();
	}

	/**
	 * Process the message returned from the upload of a dataset.
	 * 
	 * @param resultMsg
	 * 		message returned from the upload of a dataset
	 */
	private void processResultMsg(String resultMsg) {
		if ( resultMsg == null ) {
			SocatUploadDashboard.showMessage(UNEXPLAINED_FAIL_MSG);
			return;
		}
		resultMsg = resultMsg.trim();
		if ( resultMsg.startsWith(DashboardUtils.FILE_CREATED_HEADER_TAG) ) {
			// cruise file created or updated; return to the cruise list, 
			// having it request the updated cruises for the user from the server
			CruiseListPage.showPage();
		}
		else {
			// Unknown response, just display the entire message
			SocatUploadDashboard.showMessage(EXPLAINED_FAIL_MSG_START + 
					SafeHtmlUtils.htmlEscape(resultMsg) + EXPLAINED_FAIL_MSG_END);
		}
	}

}
