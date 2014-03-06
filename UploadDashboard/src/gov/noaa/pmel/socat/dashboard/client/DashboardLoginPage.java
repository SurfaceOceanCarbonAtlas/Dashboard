/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite providing login.
 * 
 * @author Karl Smith
 */
public class DashboardLoginPage extends Composite {

	private static final String WELCOME_TITLE = 
			"Welcome to the SOCAT data contribution site.";
	private static final String WELCOME_MSG =
			"In order to contribute data, you will need to be an " +
			"authorized contributor.  If you are not an authorized " +
			"contributor, contact socat.support@noaa.gov for approval " +
			"and authorization credentials.";
	private static final String USERNAME_PROMPT = "Username:";
	private static final String PASSWORD_PROMPT = "Password:";
	private static final String LOGIN_TEXT = "Login";
	private static final String NO_CREDENTIALS_ERROR_MSG = 
			"You must provide a username and password";

	interface DashboardLoginPageUiBinder extends UiBinder<Widget, DashboardLoginPage> {
	}

	private static DashboardLoginPageUiBinder uiBinder = 
			GWT.create(DashboardLoginPageUiBinder.class);

	@UiField HTML welcomeTitle;
	@UiField HTML welcomeHtml;
	@UiField Label nameLabel;
	@UiField TextBox nameText;
	@UiField Label passLabel;
	@UiField PasswordTextBox passText;
	@UiField Button loginButton;

	private String username;
	private String passhash;

	// The singleton instance of this page
	private static DashboardLoginPage singleton = null;

	/**
	 * Creates a login page.  Do not call this constructor; 
	 * instead use the showPage static method to show the 
	 * singleton instance of this page with all authentication 
	 * cleared.  Use the static methods getUsername and 
	 * getPasshash to obtain current authentication values. 
	 */
	DashboardLoginPage() {
		// Create the page and set the static strings
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		welcomeTitle.setHTML(WELCOME_TITLE);
		welcomeHtml.setHTML(WELCOME_MSG);
		nameLabel.setText(USERNAME_PROMPT);
		passLabel.setText(PASSWORD_PROMPT);
		loginButton.setText(LOGIN_TEXT);
	}

	/**
	 * Clears all authentication information.
	 */
	static void clearAuthentication() {
		if ( singleton != null ) {
			singleton.nameText.setText("");
			singleton.passText.setText("");
			singleton.nameText.setFocus(true);
			singleton.nameText.selectAll();
			singleton.username = "";
			singleton.passhash = "";
		}
	}

	/**
	 * Clears any stored authentication information and 
	 * shows the login page in the RootLayoutPanel.
	 * 
	 * @param addToHistory 
	 * 		if true, adds this page to the page history.
	 */
	static void showPage(boolean addToHistory) {
		if ( singleton == null )
			singleton = new DashboardLoginPage();
		clearAuthentication();
		SocatUploadDashboard.updateCurrentPage(singleton);
		if ( addToHistory )
			History.newItem(PagesEnum.LOGIN.name(), false);
	}

	/**
	 * @return 
	 * 		username to use when communicating with the server;
	 * 		never null, but may be empty
	 */
	static String getUsername() {
		if ( singleton == null )
			return "";
		return singleton.username;
	}

	/**
	 * @param token 
	 * 		username to use when communicating with the server;
	 * 		if null, an empty string is assigned
	 */
	private void setUsername(String username) {
		if ( username == null )
			this.username = "";
		else
			this.username = username.trim();
	}

	/**
	 * @return
	 * 		password hash to use when communication with the server;
	 * 		never null, but may be empty
	 */
	static String getPasshash() {
		if ( singleton == null )
			return "";
		return singleton.passhash;
	}

	/**
	 * @param passhash
	 * 		password hash to use when communicating with the server;
	 * 		if null, an empty string is assigned
	 */
	private void setPasshash(String passhash) {
		if ( passhash == null )
			this.passhash = "";
		else
			this.passhash = passhash.trim();
	}

	@UiHandler("nameText")
	void nameTextOnKeyDown(KeyDownEvent event) {
		// Pressing enter from nameText takes you to passText
		if ( (event != null) && (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) ) {
			passText.setFocus(true);
			passText.selectAll();
		}
	}
	
	@UiHandler("passText")
	void passTextOnKeyDown(KeyDownEvent event) {
		// Pressing enter from passText pressing the login button
		if ( (event != null) && (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) ) {
			loginButton.click();
		}
	}

	@UiHandler("loginButton")
	void loginButtonOnClick(ClickEvent event) {
		if ( (nameText.getValue().trim().length() > 0) && 
			 (passText.getValue().trim().length() > 0) ) {
			setUsername(nameText.getValue().trim());
			// Rudimentary encryption which someone can see, but at least
			// the plain-text password is not passed over the wire or stored.
			setPasshash(DashboardUtils.passhashFromPlainText(
						nameText.getValue().trim(), passText.getValue()));
			passText.setText("");
			CruiseListPage.showPage(true);
		}
		else {
			SocatUploadDashboard.showMessage(NO_CREDENTIALS_ERROR_MSG);
		}
	}

}
