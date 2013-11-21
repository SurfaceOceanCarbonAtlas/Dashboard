/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 */
public class DashboardCruiseUploadPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String INTRO_HTML_MSG = 
			"Select a cruise file to upload, and select the character set " +
			"encoding for that file.  Standard ASCII text files can use " +
			"either of the ISO, or the UTF-8, encodings.  Only use UTF-16 " +
			"if you know your file is in that encoding, but be aware that " +
			"only Western European characters can be properly handled.  " +
			"Use the Window encoding for files produced by older Window " +
			"programs.  Finally upload the file (or cancel) using the buttons " +
			"at the bottom of the page. " +
			"<br /><br /> " +
			"If you are unsure of the encoding, use the preview button to " +
			"show the beginning of the file as it will be used for SOCAT " +
			"ingestion.  Note that this uploads the entire file only for " +
			"the purpose of creating the preview. ";
	private static final String ENCODING_TEXT = "File encoding:";
	private static final String[] KNOWN_ENCODINGS = {
		"ISO-8859-1", "ISO-8859-15", "UTF-8", "UTF-16", "Windows-1252"
	};
	private static final String CREATE_TEXT = "Create Cruise";
	private static final String OVERWRITE_TEXT = "Update Cruise";
	private static final String CANCEL_TEXT = "Return to Cruise List";
	private static final String BUTTONS_HTML_MSG = 
			"The <em>" + CREATE_TEXT + "</em> button will upload the selected " +
			"file as a new cruise; it will fail if a cruise exists with the " +
			"same expocode as this cruise.  The <em>" + OVERWRITE_TEXT + "</em> " +
			"button will upload the selected file as a revised cruise; it will " +
			"fail if a cruise does not exist with the same expocode as this " +
			"cruise.  <b>Only use <em>" + OVERWRITE_TEXT + "</em> if you are " +
			"absolutely sure this is an update of the existing cruise.</b>";
	private static final String PREVIEW_TEXT = "Preview Cruise File";
	private static final String NO_PREVIEW_HTML_MSG = "<p>(No file previewed)</p>";
	private static final String NO_FILE_ERROR_MSG = 
			"Please select a cruise data file to upload";
	private static final String UNKNOWN_FAIL_MSG = 
			"Upload failed for some unexpected reason";
	private static final String NO_EXPOCODE_FAIL_MSG = 
			"Unable to obtain a cruise expocode from the uploaded file contents";
	private static final String FILE_EXISTS_FAIL_MSG = 
			"A cruise already exists with this expocode.  The preview contains " +
			"the (partial) contents of the existing cruise data.  Use the " +
			OVERWRITE_TEXT + " button if this is an update of the existing cruise " +
			"after verifying the expocode for this cruise and the contents of the " +
			"existing cruise.";
	private static final String CANNOT_OVERWRITE_FAIL_MSG = 
			"A cruise already exists with this expocode which does not belong to " +
			"you or to someone in a group you manage.  The preview contains the " +
			"(partial) contents of the existing cruise data.";
	private static final String FILE_DOES_NOT_EXIST_FAIL_MSG = 
			"A cruise with this expocode does not exist.  Use the " + CREATE_TEXT + 
			" button to create a new cruise after verifying the expocode for this cruise.";
	private static final String GET_CRUISE_LIST_FAIL_MSG = 
			"Unable to obtain the cruise list for some unexpected reason";
	private static final String LIST_UPDATE_FAIL_MSG =
			"Updating of the cruise list failed for some unexpected reason";

	interface DashboardCruiseUploadPageUiBinder 
			extends UiBinder<Widget, DashboardCruiseUploadPage> {
	}

	private static DashboardCruiseUploadPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseUploadPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField FormPanel uploadForm;
	@UiField FileUpload cruiseUpload;
	@UiField Label encodingLabel;
	@UiField ListBox encodingListBox;
	@UiField Button previewButton;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden actionToken;
	@UiField HTML buttonsHtml;
	@UiField Button createButton;
	@UiField Button overwriteButton;
	@UiField Button cancelButton;
	@UiField HTML previewHtml;

	// Singleton instance of this page
	private static DashboardCruiseUploadPage singleton = null;

	/**
	 * Creates an empty cruise upload page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page. 
	 */
	private DashboardCruiseUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));

		logoutButton.setText(LOGOUT_TEXT);

		introHtml.setHTML(INTRO_HTML_MSG);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "cruiseUploadService");

		encodingLabel.setText(ENCODING_TEXT);

		encodingListBox.setVisibleItemCount(1);
		for ( String encoding : KNOWN_ENCODINGS ) {
			encodingListBox.addItem(encoding);
		}

		previewButton.setText(PREVIEW_TEXT);

		buttonsHtml.setHTML(BUTTONS_HTML_MSG);
		createButton.setText(CREATE_TEXT);
		overwriteButton.setText(OVERWRITE_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display the cruise upload page in the RootLayoutPanel
	 * after clearing as much of the page as possible.  
	 * The upload filename cannot be cleared. 
	 */
	static void showPage() {
		if ( singleton == null )
			singleton = new DashboardCruiseUploadPage();
		singleton.userInfoLabel.setText(WELCOME_INTRO + 
				DashboardLoginPage.getUsername());
		singleton.usernameToken.setValue("");
		singleton.passhashToken.setValue("");
		singleton.actionToken.setValue("");
		singleton.previewHtml.setHTML(NO_PREVIEW_HTML_MSG);
		RootLayoutPanel.get().add(singleton);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardCruiseUploadPage.this);
		DashboardLogoutPage.showPage();
	}

	@UiHandler("previewButton") 
	void previewButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		actionToken.setValue(DashboardUtils.REQUEST_PREVIEW_TAG);
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("createButton") 
	void createButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		actionToken.setValue(DashboardUtils.REQUEST_NEW_CRUISE_TAG);
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("overwriteButton") 
	void overwriteButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		actionToken.setValue(DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG);
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the cruise list page
		DashboardCruiseListPage.showPage(DashboardCruiseUploadPage.this, 
											GET_CRUISE_LIST_FAIL_MSG);
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String cruiseFilename = cruiseUpload.getFilename();
		if ( (cruiseFilename == null) || cruiseFilename.trim().isEmpty() ) {
			Window.alert(NO_FILE_ERROR_MSG);
			event.cancel();
		}
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		// Clear the "hidden" values
		usernameToken.setValue("");
		passhashToken.setValue("");
		actionToken.setValue("");

		// Check the returned results
		String resultMsg = event.getResults();
		if ( resultMsg == null ) {
			Window.alert("Unexpected null result from submit complete");
			return;
		}

		String[] tagMsg = resultMsg.split("\n", 2);
		if ( tagMsg.length < 2 ) {
			// probably an error response; display the message in the preview
			String previewMsg;
			if ( resultMsg.contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(resultMsg) + "</pre>";
			else
				previewMsg = "<pre>" + resultMsg + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(UNKNOWN_FAIL_MSG);
		}
		else if ( DashboardUtils.FILE_PREVIEW_HEADER_TAG.equals(tagMsg[0]) ) {
			// preview file; show partial file contents in the preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
		}
		else if ( DashboardUtils.NO_EXPOCODE_HEADER_TAG.equals(tagMsg[0]) ) {
			// no expocode found; show uploaded file partial contents
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(NO_EXPOCODE_FAIL_MSG);
		}
		else if ( DashboardUtils.FILE_EXISTS_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file exists and not overwrite; 
			// show existing file partial contents in the preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(FILE_EXISTS_FAIL_MSG);
		}
		else if ( DashboardUtils.CANNOT_OVERWRITE_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file exists and not permitted to overwrite; 
			// show existing file partial contents in the preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(CANNOT_OVERWRITE_FAIL_MSG);
		}
		else if ( DashboardUtils.NO_FILE_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file does not exist and overwrite; 
			// show partial file contents in preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(FILE_DOES_NOT_EXIST_FAIL_MSG);
		}
		else if ( DashboardUtils.FILE_CREATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file created
			Window.alert(tagMsg[1]);
			// return to the updated cruise list
			DashboardCruiseListPage.showPage(
					DashboardCruiseUploadPage.this, LIST_UPDATE_FAIL_MSG);
		}
		else if ( DashboardUtils.FILE_UPDATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file updated
			Window.alert(tagMsg[1]);
			// return to the updated cruise list
			DashboardCruiseListPage.showPage(
					DashboardCruiseUploadPage.this, LIST_UPDATE_FAIL_MSG);
		}
		else {
			// Unknown response with a newline, display the whole message in the preview
			String previewMsg;
			if ( resultMsg.contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(resultMsg) + "</pre>";
			else
				previewMsg = "<pre>" + resultMsg + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(UNKNOWN_FAIL_MSG);
		}
	}

}
