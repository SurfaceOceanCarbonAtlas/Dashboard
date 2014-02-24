/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.LogoutService;
import gov.noaa.pmel.socat.dashboard.shared.LogoutServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Logout page with buttons to login again and to go to socat.info
 * @author Karl Smith
 */
public class DashboardLogoutPage extends Composite {

	private static final String GOODBYE_MSG = 
			"<p>" +
			"Thank you for contributing cruise data to SOCAT.  Please consider, " +
			"if you are not already a member, joining our group of reviewers as " +
			"well as joining the SOCAT E-mail list (Google group).  Contact " +
			"socat.support@noaa.gov for more information on joining these groups. " +
			"</p><p>" +
			"If you experienced problems working with this product, please contact " +
			"socat.support@noaa.gov with details explaining what you were trying to " +
			"accomplish and the problem you encountered. " +
			"</p><p>" +
			"Goodbye." +
			"</p>";
	private static final String RELOGIN_TEXT = "Log in again";
	private static final String SOCAT_INFO_TEXT = "Return to socat.info";
	private static final String SOCAT_INFO_LINK = "http://www.socat.info";
	private static final String REQUEST_FAILED_MSG = 
			"Sorry, an error occurred with your logout request";

	interface DashboardLogoutPageUiBinder extends UiBinder<Widget, DashboardLogoutPage> {
	}

	private static DashboardLogoutPageUiBinder uiBinder = 
			GWT.create(DashboardLogoutPageUiBinder.class);

	private static LogoutServiceAsync service = 
			GWT.create(LogoutService.class);

	@UiField HTML goodbyeHTML;
	@UiField Button reloginButton;
	@UiField Anchor socatInfoAnchor;

	// The singleton instance of this page
	private static DashboardLogoutPage singleton = null;

	/**
	 * Creates a logout page.  Do not call this constructor; 
	 * instead use the showPage static method to show the 
	 * singleton instance of this page.
	 */
	DashboardLogoutPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		goodbyeHTML.setHTML(GOODBYE_MSG);
		reloginButton.setText(RELOGIN_TEXT);
		socatInfoAnchor.setText(SOCAT_INFO_TEXT);
		socatInfoAnchor.setHref(SOCAT_INFO_LINK);
	}

	/**
	 * Shows the logout page in the RootLayoutPanel 
	 * and logs out the user.  
	 * Adds this page to the page history.
	 */
	static void showPage() {
		if ( singleton == null )
			singleton = new DashboardLogoutPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.LOGOUT.name(), false);
		service.logoutUser(DashboardLoginPage.getUsername(),
						   DashboardLoginPage.getPasshash(),
						   new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean okay) {
				if ( okay ) {
					Cookies.removeCookie("JSESSIONID");
					DashboardLoginPage.clearAuthentication();
				}
				else {
					SocatUploadDashboard.showMessage(REQUEST_FAILED_MSG);
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(REQUEST_FAILED_MSG, ex);
			}
		});
	}

	/**
	 * Shows the logout page in the RootLayoutPanel.
	 * Does not attempt to logout the user.
	 * 
	 * @param addToHistory 
	 * 		if true, adds this page to the page history 
	 */
	static void redisplayPage(boolean addToHistory) {
		// Allow this succeed even if never called before
		if ( singleton == null )
			singleton = new DashboardLogoutPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		if ( addToHistory )
			History.newItem(PagesEnum.LOGOUT.name(), false);
	}

	@UiHandler("reloginButton")
	void loginOnClick(ClickEvent event) {
		DashboardLoginPage.showPage(true);
	}

}
