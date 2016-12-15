/**
 * 
 */
package gov.noaa.pmel.dashboard.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
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

import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * Page for uploading new or updated cruise data files.
 * 
 * @author Karl Smith
 */
public class DataUploadPage extends CompositeWithUsername {

	private static final String TITLE_TEXT = "Upload Data Files";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String MORE_HELP_TEXT = "more help...";

	private static final String UPLOAD_FILE_DESCRIPTION_HTML = 
			"<p>A data file for upload has the general format: " +
			"<ul style=\"list-style-type:none\">" +
			"  <li>a header line of data column names,</li>" +
			"  <li>an optional header line of data column units,</li>" +
			"  <li>and for each sample, a line of data values.</li>" +
			"</ul></p>" +
			"<p>An appropriately-named column containing the dataset " +
			"or cruise name must be given; acceptable column names include: " +
			"<ul style=\"list-style-type:none\">" +
			"  <li>cruise</li>" +
			"  <li>cruise name</li>" +
			"  <li>cruise id</li>" +
			"  <li>dataset</li>" +
			"  <li>dataset name</li>" +
			"  <li>dataset id</li>" +
			"</ul>" +
			"The data of each a sample will be associated with the " +
			"dataset ID created from the name given in this column " +
			"(keeping only letters, which are capitalized, and numbers).</p>";

	private static final String MORE_HELP_HTML = 
			"<p>The first few lines of a comma-separated upload datafile " + 
			"may look something like the follow.  (Note that the data " +
			"lines were truncated to make it easier to see the format.) " +
			"<ul style=\"list-style-type:none\">" +
			"  <li>CruiseID, DATE_ddmmyyyy, TIME_hh:mm:ss, LAT_degN, LON_degE, ...<br /></li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:00:45, 12.638, -59.239, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:03:14, 12.633, -59.233, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:05:43, 12.628, -59.228, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:08:12, 12.622, -59.222, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:10:42, 12.617, -59.216, ...</li>" +
			"</ul>" +
			"The dataset ID for these samples will be <pre>ATLANTIS2001B</pre>" +
			"</p><p>" +
			"Identification of column types from names is case insensitive " +
			"and ignores any characters that are not letters or numbers. " +
			"Units for the columns can either follow the column name, as above, " +
			"or be given on a second column header line, such as the following:" +
			"<ul style=\"list-style-type:none\">" +
			"  <li>Cruise, Date, Time, Lat, Lon, ...<br /></li>" +
			"  <li> , ddmmyyyy, hh:mm:ss, deg N, deg E, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:00:45, 12.638, -59.239, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:03:14, 12.633, -59.233, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:05:43, 12.628, -59.228, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:08:12, 12.622, -59.222, ...</li>" +
			"  <li>Atlantis 20-01B, 19042012, 19:10:42, 12.617, -59.216, ...</li>" +
			"</ul></p>";

	private static final String SETTINGS_CAPTION_TEXT = "Settings";

	private static final String COMMA_FORMAT_TEXT = "file contains comma-separated values";
	private static final String SEMICOLON_FORMAT_TEXT = "file contains semicolon-separated values";
	private static final String TAB_FORMAT_TEXT = "file contains tab-separated values";

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
			"be seen by the dashboard using the given encoding.</li>" +
			"</ul>";
	private static final String ENCODING_TEXT = "File encoding:";
	private static final String[] KNOWN_ENCODINGS = {
		"ISO-8859-1", "ISO-8859-15", "UTF-8", "UTF-16", "Windows-1252"
	};
	private static final String PREVIEW_TEXT = "Preview Data File";
	private static final String NO_PREVIEW_HTML_MSG = "<p>(No file previewed)</p>";

	private static final String CREATE_TEXT = "only create new dataset(s)";
	private static final String CREATE_HOVER_HELP = 
			"the data uploaded must only create new datasets";

	private static final String OVERWRITE_TEXT = "create new or replace existing dataset(s)";
	private static final String OVERWRITE_HOVER_HELP = 
			"the data uploaded will replace any existing dataset(s)";

	private static final String APPEND_TEXT = "create new or append to existing dataset(s)";
	private static final String APPEND_HOVER_HELP = 
			"the data uploaded will be appended to any existing dataset(s)";

	private static final String SUBMIT_TEXT = "Upload";
	private static final String CANCEL_TEXT = "Cancel";

	private static final String NO_FILE_ERROR_MSG = 
			"Please select a data file to upload";
	private static final String UNEXPLAINED_FAIL_MSG = 
			"<h3>Upload failed.</h3>" + 
			"<p>Unexpectedly, no explanation of the failure was given</p>";
	private static final String FAIL_MSG_START = 
			"<h3>";
	private static final String EXPLAINED_FAIL_MSG_START =
			"<br />Upload failed.</h3>" +
			"<p><pre>\n";
	private static final String EXPLAINED_FAIL_MSG_END = 
			"</pre></p>";
	private static final String NO_DATASET_NAME_FAIL_MSG = 
			"<br />Dataset/Cruise name column not found.</h3>" +
			"<p>The data file needs to contain a data column specifying " +
			"the dataset or cruise name for each measurement.  Please " +
			"verify that an appropriately-named column exists for the " +
			"dataset or cruise name, and appropriate values are given for " +
			"each sample.</p>";
	private static final String DATASET_EXISTS_FAIL_MSG_START = 
			"<br />A dataset exists with a given dataset ID.</h3>";
	private static final String DATASET_EXISTS_FAIL_MSG_END = 
			"<p>Either you specified that this data upload file should " +
			"only create new datasets, or you do not have permission " +
			"to modify a dataset for this data.</p>";

	// Remove javascript added by the firewall
	private static final String JAVASCRIPT_START = "<script language=\"javascript\">";
	private static final String JAVASCRIPT_CLOSE = "</script>";

	interface DashboardCruiseUploadPageUiBinder extends UiBinder<Widget, DataUploadPage> {
	}

	private static DashboardCruiseUploadPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseUploadPageUiBinder.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField Anchor moreHelpAnchor;
	@UiField FormPanel uploadForm;
	@UiField HTML dataUpload;
	@UiField Hidden timestampToken;
	@UiField Hidden actionToken;
	@UiField Hidden encodingToken;
	@UiField Hidden formatToken;
	@UiField CaptionPanel settingsCaption;
	@UiField RadioButton commaRadio;
	@UiField RadioButton semicolonRadio;
	@UiField RadioButton tabRadio;
	@UiField DisclosurePanel advancedPanel;
	@UiField HTML advancedHtml;
	@UiField Label encodingLabel;
	@UiField ListBox encodingListBox;
	@UiField Button previewButton;
	@UiField HTML previewHtml;
	@UiField RadioButton createRadio;
	@UiField RadioButton appendRadio;
	@UiField RadioButton overwriteRadio;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	private DashboardInfoPopup moreHelpPopup;
	private Element uploadElement;

	// Singleton instance of this page
	private static DataUploadPage singleton = null;

	/**
	 * Creates an empty cruise upload page.  Do not call this 
	 * constructor; instead use the showPage static method 
	 * to show the singleton instance of this page. 
	 */
	DataUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		setUsername(null);

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);
		introHtml.setHTML(UPLOAD_FILE_DESCRIPTION_HTML);
		moreHelpAnchor.setText(MORE_HELP_TEXT);
		moreHelpPopup = null;

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "DataUploadService");
		// Create the HTML5 multiple-file upload in the HTML <div>
		dataUpload.setHTML("<input type=\"file\" name=\"datafiles\" " +
				"id=\"datafiles\" style=\"width: 100%;\" multiple />");
		// Get the multiple file input element within the HTML <div>
		uploadElement = dataUpload.getElement();
		for (int k = 0; k < uploadElement.getChildCount(); k++) {
			Element childElem = (Element) uploadElement.getChild(k);
			if ( "datafiles".equals(childElem.getId()) ) {
				uploadElement = childElem;
				break;
			}
		}

		clearTokens();

		settingsCaption.setCaptionText(SETTINGS_CAPTION_TEXT);

		commaRadio.setText(COMMA_FORMAT_TEXT);
		semicolonRadio.setText(SEMICOLON_FORMAT_TEXT);
		tabRadio.setText(TAB_FORMAT_TEXT);
		commaRadio.setValue(true, false);
		semicolonRadio.setValue(false, false);
		tabRadio.setValue(false, false);

		createRadio.setText(CREATE_TEXT);
		createRadio.setTitle(CREATE_HOVER_HELP);
		appendRadio.setText(APPEND_TEXT);
		appendRadio.setTitle(APPEND_HOVER_HELP);
		overwriteRadio.setText(OVERWRITE_TEXT);
		overwriteRadio.setTitle(OVERWRITE_HOVER_HELP);
		createRadio.setValue(true, false);
		appendRadio.setValue(false, false);
		overwriteRadio.setValue(false, false);

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
	static void showPage(String username) {
		if ( singleton == null )
			singleton = new DataUploadPage();
		singleton.setUsername(username);
		singleton.userInfoLabel.setText(WELCOME_INTRO + singleton.getUsername());
		singleton.clearTokens();
		singleton.previewHtml.setHTML(NO_PREVIEW_HTML_MSG);
		singleton.encodingListBox.setSelectedIndex(2);
		singleton.advancedPanel.setOpen(false);
		UploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.UPLOAD_DATA.name(), false);
	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the given username.
	 */
	static void redisplayPage(String username) {
		if ( (username == null) || username.isEmpty() || 
			 (singleton == null) || ! singleton.getUsername().equals(username) ) {
			DatasetListPage.showPage();
		}
		else {
			UploadDashboard.updateCurrentPage(singleton);
		}
	}

	/**
	 * Assigns the values of the Hidden tokens on the page.
	 * 
	 * @param requestAction
	 * 		action to request (value to assign to the actionToken)
	 */
	private void assignTokens(String requestAction) {
		String localTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z").format(new Date());
		String encoding = KNOWN_ENCODINGS[encodingListBox.getSelectedIndex()];
		String format;
		if ( commaRadio.getValue() )
			format = DashboardUtils.COMMA_FORMAT_TAG;
		else if ( semicolonRadio.getValue() )
			format = DashboardUtils.SEMICOLON_FORMAT_TAG;
		else
			format = DashboardUtils.TAB_FORMAT_TAG;
		
		timestampToken.setValue(localTimestamp);
		actionToken.setValue(requestAction);
		encodingToken.setValue(encoding);
		formatToken.setValue(format); 
	}

	/**
	 * Clears the values of the Hidden tokens on the page.
	 */
	private void clearTokens() {
		timestampToken.setValue("");
		actionToken.setValue("");
		encodingToken.setValue("");
		formatToken.setValue(""); 
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
		// Make sure the normal cursor is shown
		UploadDashboard.showAutoCursor();
	}

	@UiHandler("moreHelpAnchor")
	void moreHelpOnClick(ClickEvent event) {
		// Create the popup only when needed and if it does not exist
		if ( moreHelpPopup == null ) {
			moreHelpPopup = new DashboardInfoPopup();
			moreHelpPopup.setInfoMessage(MORE_HELP_HTML);
		}
		moreHelpPopup.showCentered();
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
			UploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}
		assignTokens(DashboardUtils.PREVIEW_REQUEST_TAG);
		uploadForm.submit();
	}

	@UiHandler("submitButton") 
	void createButtonOnClick(ClickEvent event) {
		String namesString = getInputFileNames(uploadElement).trim();
		if (  namesString.isEmpty() ) {
			UploadDashboard.showMessage(NO_FILE_ERROR_MSG);
			return;
		}
		if ( overwriteRadio.getValue() )
			assignTokens(DashboardUtils.OVERWRITE_DATASETS_REQUEST_TAG);
		else if ( appendRadio.getValue() )
			assignTokens(DashboardUtils.APPEND_DATASETS_REQUEST_TAG);
		else 
			assignTokens(DashboardUtils.NEW_DATASETS_REQUEST_TAG);
		uploadForm.submit();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		// Return to the cruise list page after updating the cruise list
		DatasetListPage.showPage();
		// Make sure the normal cursor is shown
		UploadDashboard.showAutoCursor();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		UploadDashboard.showWaitCursor();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		clearTokens();
		processResultMsg(event.getResults());
		UploadDashboard.showAutoCursor();
	}

	/**
	 * Process the message returned from the upload of a data file.
	 * 
	 * @param resultMsg
	 * 		message returned from the upload of a dataset
	 */
	private void processResultMsg(String resultMsg) {
		// Check the returned results
		if ( resultMsg == null ) {
			UploadDashboard.showMessage(UNEXPLAINED_FAIL_MSG);
			return;
		}
		String[] splitMsgs = resultMsg.trim().split("\n");

		// Preview is a special case - the start of the first file is returned
		if ( splitMsgs[0].startsWith(DashboardUtils.FILE_PREVIEW_HEADER_TAG) ) {
			// show partial file contents in the preview
			String previewMsg = "<pre>\n";
			for (int k = 1; k < splitMsgs.length; k++) {
				// Some clean-up: remove the javascript that is added by the firewall
				if ( splitMsgs[k].trim().startsWith(JAVASCRIPT_START) ) {
					do {
						k++;
						if ( k >= splitMsgs.length )
							break;
					} while ( ! splitMsgs[k].trim().startsWith(JAVASCRIPT_CLOSE) );
				}
				else {
					previewMsg += SafeHtmlUtils.htmlEscape(splitMsgs[k]) + "\n";
				}
			}
			previewMsg += "</pre>";
			advancedPanel.setOpen(true);
			previewHtml.setHTML(previewMsg);
			return;
		}

		ArrayList<String> cruiseIDs = new ArrayList<String>();
		ArrayList<String> errMsgs = new ArrayList<String>();
		for (int k = 0; k < splitMsgs.length; k++) {
			String header = splitMsgs[k].trim();
			if ( header.startsWith(DashboardUtils.SUCCESS_HEADER_TAG) ) {
				// Success
				cruiseIDs.add(header.substring(DashboardUtils.SUCCESS_HEADER_TAG.length()).trim());
			}
			else if ( header.startsWith(DashboardUtils.INVALID_FILE_HEADER_TAG) ) {
				// An exception was thrown while processing the input file
				String filename = header.substring(DashboardUtils.INVALID_FILE_HEADER_TAG.length()).trim();
				String failMsg = FAIL_MSG_START + SafeHtmlUtils.htmlEscape(filename) + EXPLAINED_FAIL_MSG_START;
				for (k++; k < splitMsgs.length; k++) {
					if ( splitMsgs[k].trim().startsWith(DashboardUtils.END_OF_ERROR_MESSAGE_TAG) )
						break;
					failMsg += SafeHtmlUtils.htmlEscape(splitMsgs[k]) + "\n";
				}
				errMsgs.add(failMsg + EXPLAINED_FAIL_MSG_END);
			}
			else if ( header.startsWith(DashboardUtils.DATASET_EXISTS_HEADER_TAG) ) {
				// Cruise file exists and not permitted to modify
				String[] info = header.substring(DashboardUtils.DATASET_EXISTS_HEADER_TAG.length()).trim().split(" ; ", 4);
				String failMsg = FAIL_MSG_START;
				if ( info.length > 1 ) 
					failMsg += SafeHtmlUtils.htmlEscape(info[1].trim()) + " - ";
				failMsg += SafeHtmlUtils.htmlEscape(info[0].trim());
				failMsg += DATASET_EXISTS_FAIL_MSG_START;
				if ( info.length > 2 )
					failMsg += "<p>&nbsp;&nbsp;&nbsp;&nbsp;Owner = " + SafeHtmlUtils.htmlEscape(info[2].trim()) + "</p>";
				if ( info.length > 3 )
					failMsg += "<p>&nbsp;&nbsp;&nbsp;&nbsp;Submit Status = " + SafeHtmlUtils.htmlEscape(info[3].trim()) + "</p>";
				errMsgs.add(failMsg + DATASET_EXISTS_FAIL_MSG_END); 
			}
			else if ( header.startsWith(JAVASCRIPT_START) ) {
				// ignore the added javascript from the firewall
				do {
					k++;
					if ( k >= splitMsgs.length )
						break;
				} while ( ! splitMsgs[k].trim().startsWith(JAVASCRIPT_CLOSE) );
			}
			else {
				//  some other error message, display the whole message and be done with it
				String failMsg = "<pre>";
				do {
					failMsg += SafeHtmlUtils.htmlEscape(splitMsgs[k]) + "\n";
					k++;
				} while ( k < splitMsgs.length );
				errMsgs.add(failMsg + "</pre>");
			}
		}

		// Display any error messages from the upload
		if ( errMsgs.size() > 0 ) {
			String errors = "";
			for ( String msg : errMsgs ) 
				errors += msg;
			UploadDashboard.showMessage(errors);
		}

		// Process any successes
		if ( ! cruiseIDs.isEmpty() ) {
			for ( String expo : cruiseIDs )
				DatasetListPage.addSelectedCruise(expo);
			DatasetListPage.resortTable();
			DataColumnSpecsPage.showPage(getUsername(), cruiseIDs);
		}
	}

}
