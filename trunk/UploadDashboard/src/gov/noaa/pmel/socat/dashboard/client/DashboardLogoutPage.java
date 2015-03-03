/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;

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

	interface DashboardLogoutPageUiBinder extends UiBinder<Widget, DashboardLogoutPage> {
	}

	private static DashboardLogoutPageUiBinder uiBinder = 
			GWT.create(DashboardLogoutPageUiBinder.class);

	private static DashboardServicesInterfaceAsync service = 
			GWT.create(DashboardServicesInterface.class);

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
	static void showPage() {
		if ( singleton == null )
			singleton = new DashboardLogoutPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		History.newItem(PagesEnum.LOGOUT.name(), false);
		SocatUploadDashboard.showWaitCursor();
		service.logoutUser(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void nada) {
				Cookies.removeCookie("JSESSIONID");
				SocatUploadDashboard.stopHistoryHandling();
				SocatUploadDashboard.showAutoCursor();
			}
			@Override
			public void onFailure(Throwable ex) {
				Cookies.removeCookie("JSESSIONID");
				SocatUploadDashboard.stopHistoryHandling();
				SocatUploadDashboard.showAutoCursor();
			}
		});
	}

	/**
	 * Shows the logout page in the RootLayoutPanel.
	 * Does not attempt to logout the user.
	 */
	static void redisplayPage() {
		// Allow this succeed even if never called before
		if ( singleton == null )
			singleton = new DashboardLogoutPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
	}

}
