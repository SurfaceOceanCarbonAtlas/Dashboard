/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardLogoutService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLogoutServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Logout page with button to login again or to go to socat.info
 * @author Karl Smith
 */
public class DashboardLogout extends Composite {

	protected static String goodbyeMsg = 
			"Thank you for contributing cruise data to SOCAT.  Please consider, " +
			"if you are not already a member, joining our group of reviewers as " +
			"well as joining the SOCAT E-mail list (Google group).  Contact " +
			"socat.support@noaa.gov for more information on joining these groups. " +
			"<br /><br />" +
			"If you experienced problems working with this product, please contact " +
			"socat.support@noaa.gov with details explaining what you were trying to " +
			"accomplish and the problem you encountered. " +
			"<br /><br />" +
			"Goodbye.";
	protected static String reloginText = "Log in again";
	protected static String socatInfoText = "Return to socat.info";
	protected static String socatInfoLink = "http://www.socat.info";
	protected static String requestFailedMsg = 
			"Sorry, an error occurred with your logout request";

	interface DashboardLogoutUiBinder extends UiBinder<Widget, DashboardLogout> {
	}

	private static DashboardLogoutUiBinder uiBinder = GWT
			.create(DashboardLogoutUiBinder.class);

	@UiField HTML goodbyeHTML;
	@UiField Button reloginButton;
	@UiField Anchor socatInfoAnchor;

	DashboardLogout() {
		initWidget(uiBinder.createAndBindUi(this));
		goodbyeHTML.setHTML(goodbyeMsg);
		reloginButton.setText(reloginText);
		socatInfoAnchor.setText(socatInfoText);
		socatInfoAnchor.setHref(socatInfoLink);
	}

	/**
	 * Tell the server to invalidate this session.
	 */
	void doLogout() {
		DashboardLogoutServiceAsync service = 
				GWT.create(DashboardLogoutService.class);
		service.logoutUser(DashboardPageFactory.getUsername(),
						   DashboardPageFactory.getPasshash(),
						   new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean okay) {
				if ( okay ) {
					Cookies.removeCookie("JSESSIONID");
					DashboardPageFactory.clearAuthentication();
				}
				else {
					Window.alert(requestFailedMsg);
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(requestFailedMsg + " (" + ex.getMessage() + ")");
			}
		});
	}

	@UiHandler("reloginButton")
	void loginOnClick(ClickEvent event) {
		DashboardPageFactory.clearAuthentication();
		RootLayoutPanel.get().remove(DashboardLogout.this);
		DashboardLogin loginPage = DashboardPageFactory.getPage(DashboardLogin.class);
		RootLayoutPanel.get().add(loginPage);
		loginPage.clearLoginData();
	}

}
