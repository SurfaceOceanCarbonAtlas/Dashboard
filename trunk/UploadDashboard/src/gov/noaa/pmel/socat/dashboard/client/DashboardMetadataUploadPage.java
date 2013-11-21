/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 */
public class DashboardMetadataUploadPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String OK_TEXT = "OK";
	private static final String CANCEL_TEXT = "Cancel";
	private static final String UPLOAD_NEW_HTML_INTRO = 
			"Select a file that will be uploaded as a new metadata " + 
			"document for the cruise expocode (and possibly others):<br />";
	private static final String UPLOAD_UPDATE_HTML_INTRO =
			"Select a file that will be uploaded as a replacement " + 
			"document for the cruise metadata file: <br />";
	private static final String GET_METADATA_LIST_FAIL_MSG = 
			"Unable to obtain the cruise list for some unexpected reason";
	private static final String NO_FILE_ERROR_MSG = 
			"Please select a metadata file to upload";

	interface DashboardMetadataUploadPageUiBinder 
			extends UiBinder<Widget, DashboardMetadataUploadPage> {
	}

	private static DashboardMetadataUploadPageUiBinder uiBinder = 
			GWT.create(DashboardMetadataUploadPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField FormPanel uploadForm;
	@UiField HTML expocodeHtml;
	@UiField FileUpload metadataUpload;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden expocodeToken;
	@UiField Hidden overwriteToken;
	@UiField Button okButton;
	@UiField Button cancelButton;

	String metadataFilename;
	TreeSet<String> cruiseExpocodes;

	// Singleton instance of this page
	private static DashboardMetadataUploadPage singleton = null;

	private DashboardMetadataUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));

		logoutButton.setText(LOGOUT_TEXT);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "metadataUploadService");

		okButton.setText(OK_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	static void showPage(TreeSet<String> cruiseExpocodes, String metadataFilename) {
		if ( singleton == null )
			singleton = new DashboardMetadataUploadPage();
		singleton.userInfoLabel.setText(WELCOME_INTRO + 
				DashboardLoginPage.getUsername());
		singleton.cruiseExpocodes = cruiseExpocodes;
		singleton.metadataFilename = metadataFilename;
		if ( metadataFilename == null ) {
			singleton.expocodeHtml.setHTML(
					SafeHtmlUtils.fromSafeConstant(UPLOAD_NEW_HTML_INTRO +
					SafeHtmlUtils.htmlEscape(cruiseExpocodes.first())));
		}
		else {
			singleton.expocodeHtml.setHTML(
					SafeHtmlUtils.fromSafeConstant(UPLOAD_UPDATE_HTML_INTRO +
					SafeHtmlUtils.htmlEscape(metadataFilename)));
		}
		singleton.usernameToken.setValue("");
		singleton.passhashToken.setValue("");
		singleton.expocodeToken.setValue("");
		singleton.overwriteToken.setValue("");
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardMetadataUploadPage.this);
		DashboardLogoutPage.showPage();
	}

	@UiHandler("okButton") 
	void createButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		if ( metadataFilename == null ) {
			expocodeToken.setValue(cruiseExpocodes.first());
			overwriteToken.setValue("false");
		}
		else {
			expocodeToken.setValue(metadataFilename);
			overwriteToken.setValue("true");
		}
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the metadata list page
		DashboardMetadataListPage.showPage(cruiseExpocodes, 
				DashboardMetadataUploadPage.this, GET_METADATA_LIST_FAIL_MSG);
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String metadataFilename = metadataUpload.getFilename();
		if ( (metadataFilename == null) || metadataFilename.trim().isEmpty() ) {
			Window.alert(NO_FILE_ERROR_MSG);
			event.cancel();
		}
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		// Clear the "hidden" values
		usernameToken.setValue("");
		passhashToken.setValue("");
		singleton.expocodeToken.setValue("");
		singleton.overwriteToken.setValue("");

		// Check the result returned
		String resultMsg = event.getResults();
		if ( resultMsg == null ) {
			Window.alert("Unexpected null result from submit complete");
			return;
		}

		String[] tagMsg = resultMsg.split("\n", 2);
		if ( tagMsg.length < 2 ) {
			// probably an error response; just display the entire message
			Window.alert(resultMsg);
		}
		else if ( DashboardUtils.FILE_CREATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file created
			Window.alert(tagMsg[1]);
			// return to the updated metadata list
			DashboardMetadataListPage.showPage(cruiseExpocodes, 
					DashboardMetadataUploadPage.this, GET_METADATA_LIST_FAIL_MSG);
		}
		else if ( DashboardUtils.FILE_UPDATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file updated
			Window.alert(tagMsg[1]);
			// return to the updated metadata list
			DashboardMetadataListPage.showPage(cruiseExpocodes, 
					DashboardMetadataUploadPage.this, GET_METADATA_LIST_FAIL_MSG);
		}
		else {
			// Unknown response with a newline, just display the whole message
			Window.alert(resultMsg);
		}
	}

}
