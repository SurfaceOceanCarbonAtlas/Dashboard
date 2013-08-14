/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardLogoutService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLogoutServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Logout page with button to login again or to go to socat.info
 * @author Karl Smith
 */
public class DashboardLogout extends Composite {

	protected static String goodbyeMsg = "Thank you for contributing to SOCAT.  Goodbye.";
	protected static String reloginText = "Log in again";
	protected static String socatInfoText = "Return to socat.info";
	protected static String socatInfoLink = "http://www.socat.info";
	protected static String requestFailedMsg = "Sorry, an error occurred";

	interface DashboardLogoutUiBinder extends UiBinder<Widget, DashboardLogout> {
	}

	private static DashboardLogoutUiBinder uiBinder = GWT
			.create(DashboardLogoutUiBinder.class);

	@UiField Label goodbyeLabel;
	@UiField Button reloginButton;
	@UiField Anchor socatInfoAnchor;

	public DashboardLogout() {
		initWidget(uiBinder.createAndBindUi(this));
		goodbyeLabel.setText(goodbyeMsg);
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
		((HasRpcToken) service).setRpcToken(
				DashboardPageFactory.getToken());
		service.logoutUser(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Cookies.removeCookie("JSESSIONID");
				DashboardPageFactory.setToken(null);
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(SafeHtmlUtils.htmlEscape(
						requestFailedMsg + " (" + ex.getMessage() + ")"));
			}
		});
	}

	@UiHandler("reloginButton")
	void loginOnClick(ClickEvent event) {
		RootPanel.get().remove(this);
		DashboardLogin loginPage = DashboardPageFactory.getPage(DashboardLogin.class);
		RootPanel.get().add(loginPage);
		loginPage.clearLoginData(true);
	}

}
