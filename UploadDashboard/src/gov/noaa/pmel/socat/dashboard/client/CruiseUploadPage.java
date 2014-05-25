/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page for uploading new or updated cruise data files.
 * 
 * @author Karl Smith
 */
public class CruiseUploadPage extends Composite {

	private static final String TITLE_TEXT = "Upload Data Files";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String SETTINGS_CAPTION_TEXT = "Settings";

	private static final String COMMA_FORMAT_HELP = 
			"the data file starts with lines of metadata, " +
			"then have a line of comma-separated column headers, and finally " +
			"a line of comma-separated data values for each data sample";
	private static final String TAB_FORMAT_HELP =
			"the data file starts with lines of metadata, " +
			"then have a line of tab-separated column headers, and finally " +
			"a line of tab-separated data values for each data sample";

	private static final String ADVANCED_HTML_MSG = 
			"Select a character set encoding for this file." +
			"<ul>" +
			"<li>If you are unsure of the encoding, UTF-8 should work fine.</li>" +
			"<li>The main differences in UTF-8 and ISO encodings are the " +
			"\"extended\" characters.</li>" +
			"<li>Use UTF-16 only if you know your file is encoded in that format, " +
			"but be aware that only Western European characters can be " +
			"properly handled.</li>" +
			"<li>Use the Window encoding only for files produced by older " +
			"Window programs. </li>" +
			"<li>The preview button will show the beginning of the file as it will " +
			"be seen by SOCAT using the given encoding.</li>" +
			"</ul>";
	private static final String ENCODING_TEXT = "File encoding:";
	private static final String[] KNOWN_ENCODINGS = {
		"ISO-8859-1", "ISO-8859-15", "UTF-8", "UTF-16", "Windows-1252"
	};
	private static final String PREVIEW_TEXT = "Preview Data File";
	private static final String NO_PREVIEW_HTML_MSG = "<p>(No file previewed)</p>";

	private static final String CREATE_TEXT = "create a new dataset";
	private static final String CREATE_HOVER_HELP = 
			"the data uploaded must create a new dataset to be successful";
	private static final String OVERWRITE_TEXT = "update an existing dataset";
	private static final String OVERWRITE_HOVER_HELP = 
			"the data uploaded must replace an existing dataset to be successful";

	private static final String SUBMIT_TEXT = "Upload";
	private static final String CANCEL_TEXT = "Cancel";

	private static final String NO_FILE_ERROR_MSG = 
			"Please select a data file to upload";
	private static final String FAIL_MSG_HEADER = 
			"<h3>";
	private static final String UNEXPLAINED_FAIL_MSG = 
			"<h3>Upload failed.</h3>" + 
			"<p>Unexpectedly, no explanation of the failure was given</p>";
	private static final String EXPLAINED_FAIL_MSG_START =
			"<br />Upload failed.</h3>" +
			"<p><pre>\n";
	private static final String EXPLAINED_FAIL_MSG_END = 
			"</pre></p>";
	private static final String NO_EXPOCODE_FAIL_MSG = 
			"<br />No expocode found.</h3>" +
			"<p>The data file needs to contain the dataset expocode in the lines " +
			"of metadata preceding the data, or in an expocode data column.  " +
			"The expocode metadata line should look something like<br />" +
			"&nbsp;&nbsp;&nbsp;&nbsp;expocode&nbsp;=&nbsp;49P120101218<br />" +
			"The 12 character expocode is the NODC code for the vessel carrying " +
			"the instrumentation followed by the numeric year, month, and day of " +
			"departure or initial measurement.  For example, 49P120101218 indicates " +
			"a cruise on the Japanese (49) ship of opportunity Pyxis (P1) with the " +
			"first day of the cruise on 18 December 2010.<br />" +
			"You might need use the preview and change the character encoding " +
			"given under the advanced settings.</p>";
	private static final String CANNOT_OVERWRITE_FAIL_MSG_START = 
			"<br />A dataset already exists with this expocode.</h3>" +
			"<p>";
	private static final String CANNOT_OVERWRITE_FAIL_MSG_END = 
			"<br />Either you specified that this file should create a new " +
			"dataset, or the existing dataset with this expocode cannot be " +
			"overwritten.  Datasets cannot be overwritten if they have been " +
			"submitted for QC, or if they do not belong to you.</p>";
	private static final String FILE_DOES_NOT_EXIST_FAIL_MSG = 
			"<br />A dataset with this expocode does not exist.</h3>" +
			"<p>You specified that this file should update an existing " +
			"dataset; however, no dataset exists with this expocode</p>";

	interface DashboardCruiseUploadPageUiBinder extends UiBinder<Widget, CruiseUploadPage> {
	}

	private static DashboardCruiseUploadPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseUploadPageUiBinder.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField FormPanel uploadForm;
	@UiField HTML cruiseUpload;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden timestampToken;
	@UiField Hidden actionToken;
	@UiField Hidden encodingToken;
	@UiField Hidden formatToken;
	@UiField CaptionPanel settingsCaption;
	@UiField RadioButton commaRadio;
	@UiField RadioButton tabRadio;
	@UiField DisclosurePanel advancedPanel;
	@UiField HTML advancedHtml;
	@UiField Label encodingLabel;
	@UiField ListBox encodingListBox;
	@UiField Button previewButton;
	@UiField HTML previewHtml;
	@UiField RadioButton createRadio;
	@UiField RadioButton overwriteRadio;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	private String username;
	private Element uploadElement;

	// Singleton instance of this page
	private static CruiseUploadPage singleton = null;

	/**
	 * Creates an empty cruise upload page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page. 
	 */
	CruiseUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		username = "";

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "CruiseUploadService");
		// Create the HTML5 multiple-file upload
		cruiseUpload.setHTML("<input type=\"file\" name=\"cruisedata\" id=\"cruisedata\" multiple \\>");
		// Get the multiple file input element within the HTML <div>
		uploadElement = cruiseUpload.getElement();
		for (int k = 0; k < uploadElement.getChildCount(); k++) {
			Element childElem = (Element) uploadElement.getChild(k);
			if ( "cruisedata".equals(childElem.getId()) ) {
				uploadElement = childElem;
				break;
			}
		}

		clearTokens();

		settingsCaption.setCaptionText(SETTINGS_CAPTION_TEXT);

		commaRadio.setText(DashboardUtils.CRUISE_FORMAT_COMMA);
		commaRadio.setTitle(COMMA_FORMAT_HELP);
		tabRadio.setText(DashboardUtils.CRUISE_FORMAT_TAB);
		tabRadio.setTitle(TAB_FORMAT_HELP);
		commaRadio.setValue(false, false);
		tabRadio.setValue(true, false);

		createRadio.setText(CREATE_TEXT);
		createRadio.setTitle(CREATE_HOVER_HELP);
		overwriteRadio.setText(OVERWRITE_TEXT);
		overwriteRadio.setTitle(OVERWRITE_HOVER_HELP);
		overwriteRadio.setValue(false, false);
		createRadio.setValue(true, false);

		submitButton.setText(SUBMIT_TEXT);
		cancelButton.setText(CANCEL_TEXT);

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
		singleton.clearTokens();
		singleton.previewHtml.setHTML(NO_PREVIEW_HTML_MSG);
		singleton.encodingListBox.setSelectedIndex(2);
		singleton.advancedPanel.setOpen(false);
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.UPLOAD_DATASETS.name(), false);
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
				History.newItem(PagesEnum.UPLOAD_DATASETS.name(), false);
		}
	}

	/**
	 * Assigns the values of the Hidden tokens on the page.
	 * 
	 * @param cruiseAction
	 * 		value to assign to the actionToken
	 */
	private void assignTokens(String cruiseAction) {
		String localTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm")
											  .format(new Date());
		String encoding = KNOWN_ENCODINGS[encodingListBox.getSelectedIndex()];
		String format;
		if ( commaRadio.getValue() )
			format = DashboardUtils.CRUISE_FORMAT_COMMA;
		else
			format = DashboardUtils.CRUISE_FORMAT_TAB;
		
		usernameToken.setValue(DashboardLoginPage.getUsername());
		passhashToken.setValue(DashboardLoginPage.getPasshash());
		timestampToken.setValue(localTimestamp);
		actionToken.setValue(cruiseAction);
		encodingToken.setValue(encoding);
		formatToken.setValue(format); 
	}

	/**
	 * Clears the values of the Hidden tokens on the page.
	 */
	private void clearTokens() {
		usernameToken.setValue("");
		passhashToken.setValue("");
		timestampToken.setValue("");
		actionToken.setValue("");
		encodingToken.setValue("");
		formatToken.setValue(""); 
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
		// Make sure the normal cursor is shown
		SocatUploadDashboard.showAutoCursor();
	}

	/**
	 * @param input
	 * 		multiple file input HTML element
	 * @return
	 * 		a " ; "-separated list of the filenames given 
	 * 		in the multiple file input HTML element
	 */
	private static native String getInputFileNames(Element input) /*-{
        var namesString = "";

        // Just in case not multiple
        if ( typeof (input.files) == 'undefined' || 
             typeof (input.files.length) == 'undefined') {
            return input.value;
        }

        for (var k = 0; k < input.files.length; k++) {
            if ( k > 0 ) {
                namesString += " ; ";
            }
            namesString += input.files[k].name;
        }
        return namesString;
	}-*/;

	@UiHandler("previewButton") 
	void previewButtonOnClick(ClickEvent event) {
		String namesString = getInputFileNames(uploadElement).trim();
		if (  namesString.isEmpty() ) {
			SocatUploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}
		assignTokens(DashboardUtils.REQUEST_PREVIEW_TAG);
		uploadForm.submit();
	}

	@UiHandler("submitButton") 
	void createButtonOnClick(ClickEvent event) {
		String namesString = getInputFileNames(uploadElement).trim();
		if (  namesString.isEmpty() ) {
			SocatUploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}
		if ( overwriteRadio.getValue() )
			assignTokens(DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG);
		else
			assignTokens(DashboardUtils.REQUEST_NEW_CRUISE_TAG);
		uploadForm.submit();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the cruise list page after updating the cruise list
		CruiseListPage.showPage(false);
		// Make sure the normal cursor is shown
		SocatUploadDashboard.showAutoCursor();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		SocatUploadDashboard.showWaitCursor();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		clearTokens();
		processResultMsg(event.getResults());
		SocatUploadDashboard.showAutoCursor();
	}

	/**
	 * Process the message returned from the upload of a dataset.
	 * 
	 * @param resultMsg
	 * 		message returned from the upload of a dataset
	 */
	private void processResultMsg(String resultMsg) {
		// Check the returned results
		if ( resultMsg == null ) {
			SocatUploadDashboard.showMessage(UNEXPLAINED_FAIL_MSG);
			return;
		}
		resultMsg = resultMsg.trim();

		// Preview is a special case - the start of the first file
		if ( resultMsg.startsWith(DashboardUtils.FILE_PREVIEW_HEADER_TAG) ) {
			resultMsg = resultMsg.substring(DashboardUtils.FILE_PREVIEW_HEADER_TAG.length()).trim();
			// preview file; show partial file contents in the preview
			String previewMsg;
			previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(resultMsg) + "</pre>";
			advancedPanel.setOpen(true);
			previewHtml.setHTML(previewMsg);
			return;
		}

		ArrayList<String> expocodes = new ArrayList<String>();
		ArrayList<String> errMsgs = new ArrayList<String>();
		while ( resultMsg.length() > 0 ) {			
			if ( resultMsg.startsWith(DashboardUtils.FILE_CREATED_HEADER_TAG) ) {
				// Success
				resultMsg = resultMsg.substring(DashboardUtils.FILE_CREATED_HEADER_TAG.length());
				String[] splitMsg = resultMsg.split("\n", 2);
				expocodes.add(splitMsg[0].trim());
				if ( splitMsg.length > 1 )
					resultMsg = splitMsg[1].trim();
				else
					resultMsg = "";
			}
			else if ( resultMsg.startsWith(DashboardUtils.FILE_INVALID_HEADER_TAG) ) {
				// An exception was thrown while processing the input file
				resultMsg = resultMsg.substring(DashboardUtils.FILE_INVALID_HEADER_TAG.length());
				String[] splitMsg = resultMsg.split("\n", 2);
				String filename = splitMsg[0].trim();
				String failMsg = FAIL_MSG_HEADER + SafeHtmlUtils.htmlEscape(filename) + 
						EXPLAINED_FAIL_MSG_START;
				if ( splitMsg.length > 1 )  {
					resultMsg = splitMsg[1].trim();
					while ( ! resultMsg.startsWith(DashboardUtils.END_OF_ERROR_MESSAGE_TAG) ) {
						splitMsg = resultMsg.split("\n", 2);
						String exceptMsg = splitMsg[0].trim();
						failMsg += SafeHtmlUtils.htmlEscape(exceptMsg) + "\n";
						if ( splitMsg.length > 1 ) {
							resultMsg = splitMsg[1].trim();
						}
						else {
							resultMsg = DashboardUtils.END_OF_ERROR_MESSAGE_TAG;
							break;
						}
					}
					resultMsg = resultMsg.substring(DashboardUtils.END_OF_ERROR_MESSAGE_TAG.length());
					resultMsg = resultMsg.trim();
				}
				else
					resultMsg = "";
				errMsgs.add(failMsg + EXPLAINED_FAIL_MSG_END);
			}
			else if ( resultMsg.startsWith(DashboardUtils.NO_EXPOCODE_HEADER_TAG) ) {
				// No expocode was found in the file
				resultMsg = resultMsg.substring(DashboardUtils.NO_EXPOCODE_HEADER_TAG.length());
				String[] splitMsg = resultMsg.split("\n", 2);
				String filename = splitMsg[0].trim();
				errMsgs.add(FAIL_MSG_HEADER + SafeHtmlUtils.htmlEscape(filename) + NO_EXPOCODE_FAIL_MSG);
				if ( splitMsg.length > 1 )
					resultMsg = splitMsg[1].trim();
				else
					resultMsg = "";
			}
			else if ( resultMsg.startsWith(DashboardUtils.CANNOT_OVERWRITE_HEADER_TAG) ) {
				// Cruise file exists and not permitted to overwrite; 
				resultMsg = resultMsg.substring(DashboardUtils.CANNOT_OVERWRITE_HEADER_TAG.length()).trim();
				String[] splitMsg = resultMsg.split("\n", 2);
				String[] info = splitMsg[0].split(" ; ", 3);
				String failMsg = FAIL_MSG_HEADER + SafeHtmlUtils.htmlEscape(info[0]);
				if ( info.length > 1 ) 
					failMsg += " ( " + SafeHtmlUtils.htmlEscape(info[1]) + " )";
				failMsg += CANNOT_OVERWRITE_FAIL_MSG_START;
				if ( info.length > 2 )
					failMsg += "&nbsp;&nbsp;&nbsp;&nbsp;owner = " + SafeHtmlUtils.htmlEscape(info[2]);
				errMsgs.add(failMsg + CANNOT_OVERWRITE_FAIL_MSG_END); 
				if ( splitMsg.length > 1 )
					resultMsg = splitMsg[1].trim();
				else
					resultMsg = "";
			}
			else if ( resultMsg.startsWith(DashboardUtils.NO_DATASET_HEADER_TAG) ) {
				// cruise file does not exist and request was to overwrite
				resultMsg = resultMsg.substring(DashboardUtils.NO_DATASET_HEADER_TAG.length()).trim();
				String[] splitMsg = resultMsg.split("\n", 2);
				String[] info = splitMsg[0].split(" ; ", 2);
				String failMsg = FAIL_MSG_HEADER + SafeHtmlUtils.htmlEscape(info[0]);
				if ( info.length > 1 ) 
					failMsg += " ( " + SafeHtmlUtils.htmlEscape(info[1]) + " )";
				errMsgs.add(failMsg + FILE_DOES_NOT_EXIST_FAIL_MSG);
				if ( splitMsg.length > 1 )
					resultMsg = splitMsg[1].trim();
				else
					resultMsg = "";
			}
			else if ( resultMsg.startsWith("<script language=\"javascript\">") ) {
				// added javascript from passing through the socat firewall - ignore it
				while ( ! resultMsg.contains("</script>") ) {
					String[] splitMsg = resultMsg.split("\n", 2);
					if ( splitMsg.length > 1 ) {
						resultMsg = splitMsg[1].trim();
					}
					else {
						resultMsg = "";
						break;
					}
				}
			}
			else {
				//  some other error message, display the whole message and be done with it
				errMsgs.add("<pre>" + SafeHtmlUtils.htmlEscape(resultMsg) + "</pre>");
				resultMsg = "";
			}
		}
		// Display any error messages from the upload
		if ( errMsgs.size() > 0 ) {
			String errors = "";
			for ( String msg : errMsgs ) 
				errors += msg;
			SocatUploadDashboard.showMessage(errors);
		}
		// If any successes, go on to the data column identification page
		if ( ! expocodes.isEmpty() ) {
			DataColumnSpecsPage.showPage(expocodes);
		}
	}

}
