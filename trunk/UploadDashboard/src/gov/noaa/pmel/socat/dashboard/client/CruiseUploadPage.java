/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page for uploading new or updated cruise data files.
 * 
 * @author Karl Smith
 */
public class CruiseUploadPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String CREATE_TEXT = "Add Cruise";
	private static final String CREATE_HOVER_HELP = 
			"upload the data as a new cruise";
	private static final String OVERWRITE_TEXT = "Update Cruise";
	private static final String OVERWRITE_HOVER_HELP = 
			"upload the data as revised data for an existing cruise";
	private static final String CANCEL_TEXT = "Cancel";
	private static final String CANCEL_HOVER_HELP =
			"returns to the list of cruises";
	private static final String HIDE_ADVANCED_TEXT = "Hide Advanced Settings";
	private static final String SHOW_ADVANCED_TEXT = "Show Advanced Settings";

	private static final String INTRO_HTML_MSG = 
			"<b>Upload Cruise Data</b><br />" +
			"Select a cruise data file to upload.";

	private static final String FORMAT_TEXT = "Cruise data format:";

	private static final String ADVANCED_HTML_MSG = 
			"<b>Select a character set encoding for this file.</b>" +
			"<ul>" +
			"<li>If you are unsure of the encoding, UTF-8, or either of the ISO " +
			"encodings, should work fine.  The main differences in the UTF-8 " +
			"and ISO encodings are in the \"extended\" characters.</li>" +
			"<li>Use UTF-16 only if you know your file is encoded in that format, " +
			"but be aware that only Western European characters can be " +
			"properly handled.  Use the Window encoding only for files " +
			"produced by older Window programs. </li>" +
			"<li>The preview button will show the beginning of the file as it will " +
			"be seen by SOCAT using the given encoding.  Note that this uploads " +
			"the entire file only for the purpose of creating the preview.</li>" +
			"</ul>";
	private static final String ENCODING_TEXT = "File encoding:";
	private static final String[] KNOWN_ENCODINGS = {
		"ISO-8859-1", "ISO-8859-15", "UTF-8", "UTF-16", "Windows-1252"
	};
	private static final String PREVIEW_TEXT = "Preview Cruise File";
	private static final String NO_PREVIEW_HTML_MSG = "<p>(No file previewed)</p>";

	private static final String NO_FILE_ERROR_MSG = 
			"Please select a cruise data file to upload";
	private static final String UNKNOWN_FAIL_MSG = 
			"<b>Upload failed.</b>  See the preview on the page for more information.";
	private static final String NO_EXPOCODE_FAIL_MSG = 
			"<b>No cruise expocode found.</b>  The data file needs to contain the " +
			"cruise expocode in the metadata preamble to the data as a line that " +
			"looks like \"expocode = \" followed by the expocode.  The expocode is " +
			"the NODC code for the ship follow by the year, month, and day of " +
			"departure for the cruise. " +
			"<br /><br />" +
			"The preview on the page contains the beginning of the file as it " +
			"appears to SOCAT.  If the contents look very strange, you might need " +
			"to change the character encoding in the advanced settings.";
	private static final String FILE_EXISTS_FAIL_HTML = 
			"<b>A cruise already exists with this expocode.</b>  The preview contains " +
			"the beginning of the existing cruise data.  Use the <em>" + 
			OVERWRITE_TEXT + "</em> button if this is an update of the existing " +
			"cruise.";
	private static final String CANNOT_OVERWRITE_FAIL_MSG = 
			"<b>A cruise with this expocode already exists which has been submitted " +
			"to SOCAT or does not below to you.</b>  The preview on this page " +
			"contains the beginning of the existing cruise data.";
	private static final String FILE_DOES_NOT_EXIST_FAIL_HTML = 
			"<b>A cruise with this expocode does not exist.</b>  If the expocode in " +
			"the cruise file is correct, use the <em>" + CREATE_TEXT + 
			"</em> button to create a new cruise.";

	interface DashboardCruiseUploadPageUiBinder 
			extends UiBinder<Widget, CruiseUploadPage> {
	}

	private static DashboardCruiseUploadPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseUploadPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField FormPanel uploadForm;
	@UiField FileUpload cruiseUpload;
	@UiField Label formatLabel;
	@UiField ListBox formatListBox;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden timestampToken;
	@UiField Hidden actionToken;
	@UiField Hidden encodingToken;
	@UiField Button advancedButton;
	@UiField Button createButton;
	@UiField Button overwriteButton;
	@UiField Button cancelButton;
	@UiField FlowPanel advancedPanel;
	@UiField HTML advancedHtml;
	@UiField Label encodingLabel;
	@UiField ListBox encodingListBox;
	@UiField ScrollPanel previewPanel;
	@UiField Button previewButton;
	@UiField HTML previewHtml;

	private String username;
	private boolean advancedShown; 

	// Singleton instance of this page
	private static CruiseUploadPage singleton = null;

	/**
	 * Creates an empty cruise upload page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page. 
	 */
	private CruiseUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));

		username = "";

		logoutButton.setText(LOGOUT_TEXT);

		introHtml.setHTML(INTRO_HTML_MSG);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "CruiseUploadService");

		formatLabel.setText(FORMAT_TEXT);
		formatListBox.addItem(DashboardUtils.CRUISE_FORMAT_TAB);
		formatListBox.addItem(DashboardUtils.CRUISE_FORMAT_COMMA);

		createButton.setText(CREATE_TEXT);
		createButton.setTitle(CREATE_HOVER_HELP);
		overwriteButton.setText(OVERWRITE_TEXT);
		overwriteButton.setTitle(OVERWRITE_HOVER_HELP);
		cancelButton.setText(CANCEL_TEXT);
		cancelButton.setTitle(CANCEL_HOVER_HELP);

		advancedHtml.setHTML(ADVANCED_HTML_MSG);
		encodingLabel.setText(ENCODING_TEXT);
		encodingListBox.setVisibleItemCount(1);
		for ( String encoding : KNOWN_ENCODINGS )
			encodingListBox.addItem(encoding);
		previewButton.setText(PREVIEW_TEXT);
	}

	/**
	 * Display the cruise upload page in the RootLayoutPanel
	 * after clearing as much of the page as possible.  
	 * The upload filename cannot be cleared. 
	 * Adds this page to the page history.
	 */
	static void showPage() {
		if ( singleton == null )
			singleton = new CruiseUploadPage();
		singleton.username = DashboardLoginPage.getUsername();
		singleton.userInfoLabel.setText(WELCOME_INTRO + 
				singleton.username);
		singleton.usernameToken.setValue("");
		singleton.passhashToken.setValue("");
		singleton.timestampToken.setValue("");
		singleton.encodingToken.setValue("");
		singleton.actionToken.setValue("");
		singleton.previewHtml.setHTML(NO_PREVIEW_HTML_MSG);
		singleton.encodingListBox.setSelectedIndex(2);
		singleton.hideAdvancedOptions();
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.CRUISE_UPLOAD.name(), false);
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
				History.newItem(PagesEnum.CRUISE_UPLOAD.name(), false);
		}
	}

	/**
	 * Hides the advanced options on the page.
	 */
	private void hideAdvancedOptions() {
		advancedButton.setText(SHOW_ADVANCED_TEXT);
		advancedPanel.setVisible(false);
		previewPanel.setVisible(false);
		advancedShown = false;
	}

	/**
	 * Shows the advanced options on the page.
	 */
	private void showAdvancedOptions() {
		advancedButton.setText(HIDE_ADVANCED_TEXT);
		advancedPanel.setVisible(true);
		previewPanel.setVisible(true);
		advancedShown = true;
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("advancedButton")
	void showAdvancedOnClick(ClickEvent event) {
		if ( advancedShown )
			hideAdvancedOptions();
		else
			showAdvancedOptions();
	}

	@UiHandler("previewButton") 
	void previewButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		String localTimestamp = 
				DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z")
							  .format(new Date());
		timestampToken.setValue(localTimestamp);
		encodingToken.setValue(
				KNOWN_ENCODINGS[encodingListBox.getSelectedIndex()]);
		actionToken.setValue(DashboardUtils.REQUEST_PREVIEW_TAG);
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("createButton") 
	void createButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		String localTimestamp = 
				DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z")
							  .format(new Date());
		timestampToken.setValue(localTimestamp);
		encodingToken.setValue(
				KNOWN_ENCODINGS[encodingListBox.getSelectedIndex()]);
		actionToken.setValue(DashboardUtils.REQUEST_NEW_CRUISE_TAG);
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("overwriteButton") 
	void overwriteButtonOnClick(ClickEvent event) {
		// Assign the "hidden" values
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		String localTimestamp = 
				DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z")
							  .format(new Date());
		timestampToken.setValue(localTimestamp);
		encodingToken.setValue(
				KNOWN_ENCODINGS[encodingListBox.getSelectedIndex()]);
		actionToken.setValue(DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG);
		// Submit the form
		uploadForm.submit();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the cruise list page after updating the cruise list
		CruiseListPage.showPage(false);
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String cruiseFilename = cruiseUpload.getFilename();
		if ( (cruiseFilename == null) || cruiseFilename.trim().isEmpty() ) {
			SocatUploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			event.cancel();
		}
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		// Clear the "hidden" values
		usernameToken.setValue("");
		passhashToken.setValue("");
		timestampToken.setValue("");
		encodingToken.setValue("");
		actionToken.setValue("");

		// Check the returned results
		String resultMsg = event.getResults();
		if ( resultMsg == null ) {
			SocatUploadDashboard.showMessage(
					"Unexpected null result from submit complete");
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
			previewPanel.setVisible(true);
			previewHtml.setHTML(previewMsg);
			SocatUploadDashboard.showMessage(UNKNOWN_FAIL_MSG);
		}
		else if ( tagMsg[0].equals(DashboardUtils.FILE_PREVIEW_HEADER_TAG) ) {
			// preview file; show partial file contents in the preview
			String previewMsg;
			if ( tagMsg[1].contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			// Advanced options must already be visible to have sent this request
			previewHtml.setHTML(previewMsg);
		}
		else if ( tagMsg[0].equals(DashboardUtils.NO_EXPOCODE_HEADER_TAG) ) {
			// no expocode found; show uploaded file partial contents
			String previewMsg;
			if ( tagMsg[1].contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewPanel.setVisible(true);
			previewHtml.setHTML(previewMsg);
			SocatUploadDashboard.showMessage(NO_EXPOCODE_FAIL_MSG);
		}
		else if ( tagMsg[0].equals(DashboardUtils.FILE_EXISTS_HEADER_TAG) ) {
			// cruise file exists and not overwrite; 
			// show existing file partial contents in the preview
			String previewMsg;
			if ( tagMsg[1].contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewPanel.setVisible(true);
			previewHtml.setHTML(previewMsg);
			SocatUploadDashboard.showMessage(FILE_EXISTS_FAIL_HTML);
		}
		else if ( tagMsg[0].equals(DashboardUtils.CANNOT_OVERWRITE_HEADER_TAG) ) {
			// cruise file exists and not permitted to overwrite; 
			// show existing file partial contents in the preview
			String previewMsg;
			if ( tagMsg[1].contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewPanel.setVisible(true);
			previewHtml.setHTML(previewMsg);
			SocatUploadDashboard.showMessage(CANNOT_OVERWRITE_FAIL_MSG);
		}
		else if ( tagMsg[0].equals(DashboardUtils.NO_FILE_HEADER_TAG) ) {
			// cruise file does not exist and overwrite; 
			// show partial file contents in preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewPanel.setVisible(true);
			previewHtml.setHTML(previewMsg);
			SocatUploadDashboard.showMessage(FILE_DOES_NOT_EXIST_FAIL_HTML);
		}
		else if ( tagMsg[0].startsWith(DashboardUtils.FILE_CREATED_HEADER_TAG) ) {
			String expocode = tagMsg[0].substring(
					DashboardUtils.FILE_CREATED_HEADER_TAG.length()).trim();
			// cruise file created or updated
			SocatUploadDashboard.showMessage(SafeHtmlUtils.htmlEscape(tagMsg[1]));
			// go to the data column specifications page
			DataColumnSpecsPage.showPage(expocode, true);
		}
		else if ( tagMsg[0].startsWith(DashboardUtils.FILE_UPDATED_HEADER_TAG) ) {
			String expocode = tagMsg[0].substring(
					DashboardUtils.FILE_UPDATED_HEADER_TAG.length()).trim();
			// cruise file created or updated
			SocatUploadDashboard.showMessage(SafeHtmlUtils.htmlEscape(tagMsg[1]));
			// go to the data column specifications page
			DataColumnSpecsPage.showPage(expocode, true);
		}
		else {
			// Unknown response with a newline, display the whole message in the preview
			String previewMsg;
			if ( resultMsg.contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(resultMsg) + "</pre>";
			else
				previewMsg = "<pre>" + resultMsg + "</pre>";
			previewPanel.setVisible(true);
			previewHtml.setHTML(previewMsg);
			SocatUploadDashboard.showMessage(UNKNOWN_FAIL_MSG);
		}
	}

}
