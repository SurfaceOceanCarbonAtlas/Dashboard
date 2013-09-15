/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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
public class DashboardLogin extends Composite {

	protected static String welcomeMsg = 
			"Welcome to the SOCAT data contribution site.  In order " +
			"to contribute data, you will need to be an authorized " +
			"contributor.  If you are not an authorized contributor, " +
			"contact socat.support@noaa.gov for approval and " +
			"authorization credentials.";
	protected static String usernamePrompt = "Username:";
	protected static String passwordPrompt = "Password:";
	protected static String loginText = "Login";
	protected static String noCredErrorMsg = "You must provide a username and password";
	protected static String loginErrorMsg = "Sorry, your login failed.";

	interface DashboardLoginUiBinder extends UiBinder<Widget, DashboardLogin> {
	}

	private static DashboardLoginUiBinder uiBinder = 
			GWT.create(DashboardLoginUiBinder.class);

	@UiField HTML welcomeHTML;
	@UiField Label nameLabel;
	@UiField TextBox nameText;
	@UiField Label passLabel;
	@UiField PasswordTextBox passText;
	@UiField Button loginButton;

	DashboardLogin() {
		initWidget(uiBinder.createAndBindUi(this));
		welcomeHTML.setHTML(welcomeMsg);
		nameLabel.setText(usernamePrompt);
		passLabel.setText(passwordPrompt);
		loginButton.setText(loginText);
	}

	/**
	 * Clears the contents of the username and password text boxes.
	 */
	void clearLoginData() {
		nameText.setText("");
		passText.setText("");
		nameText.setFocus(true);
		nameText.selectAll();
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
			DashboardPageFactory.setUsername(nameText.getValue().trim());
			// Rudimentary encryption which someone can see, but at least
			// the plain-text password is not passed over the wire.
			DashboardPageFactory.setPasshash(
					DashboardUtils.passhashFromPlainText(
							nameText.getValue().trim(), passText.getValue()));
			clearLoginData();

			DashboardCruiseListPage.showCruiseListPage(
					DashboardLogin.this, loginErrorMsg);
		}
		else {
			Window.alert(noCredErrorMsg);
		}
	}

}
