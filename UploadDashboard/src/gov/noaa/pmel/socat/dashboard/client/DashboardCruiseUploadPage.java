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

	protected static String welcomeIntro = "Logged in as: ";
	protected static String logoutText = "Logout";
	protected static String introHtmlMsg = 
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
	protected static String encodingText = "File encoding:";
	protected static String[] knownEncodings = {
		"ISO-8859-1", "ISO-8859-15", "UTF-8", "UTF-16", "Windows-1252"
	};
	protected static String createText = "Create Cruise";
	protected static String overwriteText = "Update Cruise";
	protected static String cancelText = "Return to Cruise List";
	protected static String buttonsHtmlMsg = 
			"The <em>" + createText + "</em> button will upload the selected " +
			"file as a new cruise; it will fail if a cruise exists with the " +
			"same expocode as this cruise.  The <em>" + overwriteText + "</em> " +
			"button will upload the selected file as a revised cruise; it will " +
			"fail if a cruise does not exist with the same expocode as this " +
			"cruise.  <b>Only use <em>" + overwriteText + "</em> if you are " +
			"absolutely sure this is an update of the existing cruise.</b>";
	protected static String previewText = "Preview Cruise File";
	protected static String noPreviewMsg = "<p>(No file previewed)</p>";
	protected static String noFileErrorMsg = 
			"Please select a cruise data file to upload";
	protected static String unknownFailureMsg = 
			"Upload failed for some unexpected reason";
	protected static String noExpocodeFailureMsg = 
			"Unable to obtain a cruise expocode from the uploaded file contents";
	protected static String fileExistsFailureMsg = 
			"A cruise already exists with this expocode.  The preview contains " +
			"the (partial) contents of the existing cruise data.  Use the " +
			overwriteText + " button if this is an update of the existing cruise " +
			"after verifying the expocode for this cruise and the contents of the " +
			"existing cruise.";
	protected static String cannotOverwriteFailureMsg = 
			"A cruise already exists with this expocode which does not belong to " +
			"you or to someone in a group you manage.  The preview contains the " +
			"(partial) contents of the existing cruise data.";
	protected static String fileDoesNotExistFailureMsg = 
			"A cruise with this expocode does not exist.  Use the " + createText + 
			" button to create a new cruise after verifying the expocode for this cruise.";
	protected static String listUpdateFailedMsg =
			"Updating of the cruise list failed for some unexpected reason";

	interface DashboardNewCruisePageUiBinder extends
			UiBinder<Widget, DashboardCruiseUploadPage> {
	}

	private static DashboardNewCruisePageUiBinder uiBinder = 
			GWT.create(DashboardNewCruisePageUiBinder.class);

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

	DashboardCruiseUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));

		logoutButton.setText(logoutText);

		introHtml.setHTML(introHtmlMsg);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "cruiseUploadService");

		encodingLabel.setText(encodingText);

		encodingListBox.setVisibleItemCount(1);
		for ( String encoding : knownEncodings ) {
			encodingListBox.addItem(encoding);
		}

		previewButton.setText(previewText);

		buttonsHtml.setHTML(buttonsHtmlMsg);
		createButton.setText(createText);
		overwriteButton.setText(overwriteText);
		cancelButton.setText(cancelText);
	}

	void updatePageContents() {
		userInfoLabel.setText(welcomeIntro + 
				SafeHtmlUtils.htmlEscape(DashboardPageFactory.getUsername()));
		usernameToken.setValue("");
		passhashToken.setValue("");
		actionToken.setValue("");
		previewHtml.setHTML(noPreviewMsg);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogout logoutPage = 
				DashboardPageFactory.getPage(DashboardLogout.class);
		RootLayoutPanel.get().remove(DashboardCruiseUploadPage.this);
		RootLayoutPanel.get().add(logoutPage);
		logoutPage.doLogout();
	}

	@UiHandler("previewButton") 
	void previewButtonOnClick(ClickEvent event) {
		usernameToken.setValue(DashboardPageFactory.getUsername());
		passhashToken.setValue(DashboardPageFactory.getPasshash());
		actionToken.setValue(DashboardUtils.REQUEST_PREVIEW_TAG);
		uploadForm.submit();
	}

	@UiHandler("createButton") 
	void createButtonOnClick(ClickEvent event) {
		usernameToken.setValue(DashboardPageFactory.getUsername());
		passhashToken.setValue(DashboardPageFactory.getPasshash());
		actionToken.setValue(DashboardUtils.REQUEST_NEW_CRUISE_TAG);
		uploadForm.submit();
	}

	@UiHandler("overwriteButton") 
	void overwriteButtonOnClick(ClickEvent event) {
		usernameToken.setValue(DashboardPageFactory.getUsername());
		passhashToken.setValue(DashboardPageFactory.getPasshash());
		actionToken.setValue(DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG);
		uploadForm.submit();
	}

	@UiHandler("cancelButton")
	void cancelButtonOnClick(ClickEvent event) {
		RootLayoutPanel.get().remove(DashboardCruiseUploadPage.this);
		DashboardCruiseListPage page = 
				DashboardPageFactory.getPage(DashboardCruiseListPage.class);
		RootLayoutPanel.get().add(page);
		// Current contents should still be valid
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String cruiseFilename = cruiseUpload.getFilename();
		if ( (cruiseFilename == null) || cruiseFilename.trim().isEmpty() ) {
			Window.alert(noFileErrorMsg);
			event.cancel();
		}
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		usernameToken.setValue("");
		passhashToken.setValue("");
		actionToken.setValue("");

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
			Window.alert(unknownFailureMsg);
			return;
		}

		if ( DashboardUtils.FILE_PREVIEW_HEADER_TAG.equals(tagMsg[0]) ) {
			// preview file; show partial file contents in the preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			return;
		}

		if ( DashboardUtils.NO_EXPOCODE_HEADER_TAG.equals(tagMsg[0]) ) {
			// no expocode found; show uploaded file partial contents
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(noExpocodeFailureMsg);
			return;
		}

		if ( DashboardUtils.FILE_EXISTS_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file exists and not overwrite; 
			// show existing file partial contents in the preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(fileExistsFailureMsg);
			return;
		}

		if ( DashboardUtils.CANNOT_OVERWRITE_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file exists and not permitted to overwrite; 
			// show existing file partial contents in the preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(cannotOverwriteFailureMsg);
			return;
		}

		if ( DashboardUtils.NO_FILE_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file does not exist and overwrite; 
			// show partial file contents in preview
			String previewMsg;
			if ( (tagMsg[1]).contains("</pre>") )
				previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(tagMsg[1]) + "</pre>";
			else
				previewMsg = "<pre>" + tagMsg[1] + "</pre>";
			previewHtml.setHTML(previewMsg);
			Window.alert(fileDoesNotExistFailureMsg);
			return;
		}

		if ( DashboardUtils.FILE_CREATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file created
			Window.alert(tagMsg[1]);
			// return to the updated cruise list
			DashboardCruiseListPage.showCruiseListPage(
					DashboardCruiseUploadPage.this, listUpdateFailedMsg);
			return;
		}

		if ( DashboardUtils.FILE_UPDATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file updated
			Window.alert(tagMsg[1]);
			// return to the updated cruise list
			DashboardCruiseListPage.showCruiseListPage(
					DashboardCruiseUploadPage.this, listUpdateFailedMsg);
			return;
		}

		// Unknown response with a newline, display the whole message in the preview
		String previewMsg;
		if ( resultMsg.contains("</pre>") )
			previewMsg = "<pre>" + SafeHtmlUtils.htmlEscape(resultMsg) + "</pre>";
		else
			previewMsg = "<pre>" + resultMsg + "</pre>";
		previewHtml.setHTML(previewMsg);
		Window.alert(unknownFailureMsg);
		return;

	}

}
