/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginServiceAsync;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite providing an XSRF protected login.
 * 
 * @author Karl Smith
 */
public class DashboardLogin extends Composite {

	protected static String welcomeMsg = 
			"Welcome to the SOCAT data contribution site.  In order " +
			"to contribute data, you will need to be an authorized " +
			"contributor.  If you are not an authorized contributor, " +
			"contact socat.support@noaa.gov for approval and " +
			"authorization credentials.";
	protected static String usernamePrompt = "Username:";
	protected static String passwordPrompt = "Password:";
	protected static String loginText = "Log In";
	protected static String noCredErrorMsg = "You must provide a username and password";
	protected static String loginErrorMsg = "Sorry, your login failed.";

	interface DashboardLoginUiBinder extends UiBinder<Widget, DashboardLogin> {
	}

	private static DashboardLoginUiBinder uiBinder = 
			GWT.create(DashboardLoginUiBinder.class);

	@UiField Label welcomeLabel;
	@UiField InlineLabel nameLabel;
	@UiField TextBox nameText;
	@UiField InlineLabel passLabel;
	@UiField PasswordTextBox passText;
	@UiField Button loginButton;
	private String username;

	public DashboardLogin() {
		initWidget(uiBinder.createAndBindUi(this));
		welcomeLabel.setText(welcomeMsg);
		nameLabel.setText(usernamePrompt);
		passLabel.setText(passwordPrompt);
		loginButton.setText(loginText);
		nameText.setFocus(true);
		username = "";
	}

	/**
	 * Clears the contents of the username and password text boxes
	 */
	void clearLogin() {
		nameText.setText("");
		passText.setText("");
		username = "";
	}

	@UiHandler("loginButton")
	void loginButtonOnClick(ClickEvent event) {
		username = nameText.getValue().trim();
		if ( (username.length() > 0) && 
			 (passText.getValue().trim().length() > 0) ) {
			XsrfTokenServiceAsync xsrf = GWT.create(XsrfTokenService.class);
			((ServiceDefTarget) xsrf).setServiceEntryPoint(
					GWT.getModuleBaseURL() + "xsrf");
			xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>() {
				@Override
				public void onSuccess(XsrfToken token) {
					DashboardLoginServiceAsync service = 
							GWT.create(DashboardLoginService.class);
					((HasRpcToken) service).setRpcToken(token);
					// Rudimentary encryption which someone can see, but at least
					// the plain-text password is not passed over the wire.
					String[] hashes = DashboardUtils.hashesFromPlainText(
							username, passText.getValue());
					service.authenticateUser(hashes[0], hashes[1], 
							createDashboardForUser);
				}
				@Override
				public void onFailure(Throwable ex) {
					Window.alert(SafeHtmlUtils.htmlEscape(
							loginErrorMsg + " (" + ex.getMessage() + ")"));
				}
			});
		}
		else {
			Window.alert(noCredErrorMsg);
		}
	}

	final AsyncCallback<DashboardCruiseListing> createDashboardForUser = 
			new AsyncCallback<DashboardCruiseListing>() {
		@Override
		public void onSuccess(DashboardCruiseListing cruises) {
			if ( username.equals(cruises.getUsername()) ) {
				clearLogin();
				DashboardCruiseListPage cruiseListPage = 
						DashboardPageFactory.getPage(DashboardCruiseListPage.class);
				cruiseListPage.updateCruises(cruises);
				RootPanel.get().remove(DashboardLogin.this);
				RootPanel.get().add(cruiseListPage);
			}
			else {
				Window.alert(loginErrorMsg);
			}
		}
		@Override
		public void onFailure(Throwable ex) {
			Window.alert(SafeHtmlUtils.htmlEscape(
					loginErrorMsg + " (" + ex.getMessage() + ")"));
		}
	};

}
