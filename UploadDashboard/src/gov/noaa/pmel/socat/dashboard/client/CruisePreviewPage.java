/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page showing various plots of cruise data.
 * These plots are to be examined by a user 
 * to catch errors prior to submitting for QC.
 * 
 * @author Karl Smith
 */
public class CruisePreviewPage extends CompositeWithUsername {

	private static final String TITLE_TEXT = "Preview Dataset";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String INTRO_HTML_PROLOGUE = 
			"Examine data plots for the dataset: ";

	private static final String DISMISS_TEXT = "Done";

	interface CruisePreviewPageUiBinder extends UiBinder<Widget, CruisePreviewPage> {
	}

	private static CruisePreviewPageUiBinder uiBinder = 
			GWT.create(CruisePreviewPageUiBinder.class);

	private static DashboardServicesInterfaceAsync service = 
			GWT.create(DashboardServicesInterface.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml; 
	@UiField Button dismissButton;

	String expocode;

	// The singleton instance of this page
	private static CruisePreviewPage singleton;

	public CruisePreviewPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		setUsername(null);
		expocode = "";

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		dismissButton.setText(DISMISS_TEXT);
	}

	/**
	 * Display the preview page in the RootLayoutPanel with data plots  
	 * for the indicated cruise.  Adds this page to the page history.
	 */
	static void showPage(String expocode, String username) {
		if ( singleton == null )
			singleton = new CruisePreviewPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		singleton.updatePreviewPlots(expocode, username);
		History.newItem(PagesEnum.PREVIEW_CRUISE.name(), false);
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
	 * Updates the this page with the plots for the indicated cruise.
	 * 
	 * @param expocode
	 * 		cruises to use
	 * @param username
	 * 		user requesting these plots 
	 */
	private void updatePreviewPlots(String expocode, String username) {
		// Update the username
		setUsername(username);
		userInfoLabel.setText(WELCOME_INTRO + getUsername());

		this.expocode = expocode;
		introHtml.setHTML(INTRO_HTML_PROLOGUE + SafeHtmlUtils.htmlEscape(expocode));
		
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("dismissButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page.
		CruiseListPage.showPage();
	}

}
