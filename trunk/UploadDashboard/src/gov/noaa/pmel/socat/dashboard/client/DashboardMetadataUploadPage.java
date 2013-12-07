/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 */
public class DashboardMetadataUploadPage extends Composite {

	private static final String WELCOME_INTRO = "Logged in as: ";
	private static final String LOGOUT_TEXT = "Logout";
	private static final String UPLOAD_TEXT = "Upload";
	private static final String CANCEL_TEXT = "Cancel";
	private static final String UPLOAD_NEW_LABEL_INTRO = 
			"Select a file that will be uploaded as a new metadata " + 
			"document for the cruise expocode (and possibly others):  ";
	private static final String UPLOAD_UPDATE_LABEL_INTRO =
			"Select a file that will be uploaded as a replacement " + 
			"document for the cruise metadata file:  ";
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
	@UiField Label expocodeLabel;
	@UiField FileUpload metadataUpload;
	@UiField Hidden usernameToken;
	@UiField Hidden passhashToken;
	@UiField Hidden expocodeToken;
	@UiField Hidden overwriteToken;
	@UiField Button uploadButton;
	@UiField Button cancelButton;

	private String username;
	private TreeSet<String> cruiseExpocodes;
	private String metadataFilename;

	// Singleton instance of this page
	private static DashboardMetadataUploadPage singleton = null;

	private DashboardMetadataUploadPage() {
		initWidget(uiBinder.createAndBindUi(this));
		username = "";
		cruiseExpocodes = new TreeSet<String>();
		metadataFilename = null;

		logoutButton.setText(LOGOUT_TEXT);

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "MetadataUploadService");

		uploadButton.setText(UPLOAD_TEXT);
		cancelButton.setText(CANCEL_TEXT);
	}

	/**
	 * Display the metadata upload page in the RootLayoutPanel.
	 * Adds this page to the page history.
	 * 
	 * @param cruiseExpocodes
	 * 		cruises currently associated with the DashboardMetadataListPage
	 * @param metadataFilename
	 * 		metadata document to be updated, or null if a new metadata
	 * 		document is to be created
	 * 		
	 */
	static void showPage(TreeSet<String> cruiseExpocodes, String metadataFilename) {
		if ( singleton == null )
			singleton = new DashboardMetadataUploadPage();
		singleton.username = DashboardLoginPage.getUsername();
		singleton.userInfoLabel.setText(WELCOME_INTRO + singleton.username);
		singleton.cruiseExpocodes.clear();
		singleton.cruiseExpocodes.addAll(cruiseExpocodes);
		singleton.metadataFilename = metadataFilename;
		if ( metadataFilename == null ) {
			singleton.expocodeLabel.setText(UPLOAD_NEW_LABEL_INTRO +
					cruiseExpocodes.first());
		}
		else {
			singleton.expocodeLabel.setText(UPLOAD_UPDATE_LABEL_INTRO +
					metadataFilename);
		}
		singleton.usernameToken.setValue("");
		singleton.passhashToken.setValue("");
		singleton.expocodeToken.setValue("");
		singleton.overwriteToken.setValue("");
		SocatUploadDashboard.get().updateCurrentPage(singleton);
		History.newItem(PagesEnum.METADATA_UPLOAD.name(), false);
	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the current login username.
	 * Does not add this page to the page history.
	 */
	static void redisplayPage() {
		// If never show before, or if the username does not match the 
		// current login username, show the login page instead
		if ( (singleton == null) || 
			 ! singleton.username.equals(DashboardLoginPage.getUsername()) )
			DashboardLoginPage.showPage();
		else
			SocatUploadDashboard.get().updateCurrentPage(singleton);
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("uploadButton") 
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
		// Return to the metadata list page exactly as it was
		DashboardMetadataListPage.redisplayPage();
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmit(SubmitEvent event) {
		// Make sure a file was selected
		String uploadFilename = metadataUpload.getFilename();
		if ( (uploadFilename == null) || uploadFilename.trim().isEmpty() ) {
			Window.alert(NO_FILE_ERROR_MSG);
			event.cancel();
		}
	}

	@UiHandler("uploadForm")
	void uploadFormOnSubmitComplete(SubmitCompleteEvent event) {
		// Clear the "hidden" values
		usernameToken.setValue("");
		passhashToken.setValue("");
		expocodeToken.setValue("");
		overwriteToken.setValue("");

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
			DashboardMetadataListPage.showPage(cruiseExpocodes);
		}
		else if ( DashboardUtils.FILE_UPDATED_HEADER_TAG.equals(tagMsg[0]) ) {
			// cruise file updated
			Window.alert(tagMsg[1]);
			// return to the updated metadata list
			DashboardMetadataListPage.showPage(cruiseExpocodes);
		}
		else {
			// Unknown response with a newline, just display the whole message
			Window.alert(resultMsg);
		}
	}

}
