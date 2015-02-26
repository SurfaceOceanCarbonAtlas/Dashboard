/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Logout page with buttons to login again and to go to socat.info
 * 
 * @author Karl Smith
 */
public class DashboardLogoutPage extends CompositeWithUsername {

	private static final String GOODBYE_TITLE = 
			"Thank you for contributing data to SOCAT.";
	private static final String RELOGIN_TEXT = "Log in again";
	private static final String RELOGIN_HREF = "SocatUploadDashboard.html";
	private static final String REQUEST_FAILED_MSG = 
			"Sorry, an error occurred with your logout request";

	interface DashboardLogoutPageUiBinder extends UiBinder<Widget, DashboardLogoutPage> {
	}

	private static DashboardLogoutPageUiBinder uiBinder = 
			GWT.create(DashboardLogoutPageUiBinder.class);

	private static DashboardListServiceAsync service = 
			GWT.create(DashboardListService.class);

	@UiField HTML goodbyeTitle;
	@UiField Anchor reloginAnchor;

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

		goodbyeTitle.setHTML(GOODBYE_TITLE);
		reloginAnchor.setText(RELOGIN_TEXT);
		reloginAnchor.setHref(RELOGIN_HREF);
	}

	/**
	 * Shows the logout page in the RootLayoutPanel and logs out the user.  
	 * Adds this page to the page history.
	 */
	static void showPage(String username) {
		if ( singleton == null )
			singleton = new DashboardLogoutPage();
		singleton.setUsername(username);
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.LOGOUT.name(), false);
		SocatUploadDashboard.showWaitCursor();
		service.logoutUser(username, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean success) {
				if ( success ) {
					Cookies.removeCookie("JSESSIONID");
				}
				else {
					SocatUploadDashboard.showMessage(REQUEST_FAILED_MSG);
				}
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				SocatUploadDashboard.showFailureMessage(REQUEST_FAILED_MSG, ex);
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

	/**
	 * Shows the logout page in the RootLayoutPanel.
	 * Does not attempt to logout the user.
	 */
	static void redisplayPage(String username) {
		// Allow this succeed even if never called before
		if ( singleton == null )
			singleton = new DashboardLogoutPage();
		singleton.setUsername(username);
		SocatUploadDashboard.updateCurrentPage(singleton);
	}

}
