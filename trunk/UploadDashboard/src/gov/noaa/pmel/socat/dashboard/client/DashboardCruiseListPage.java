/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 */
public class DashboardCruiseListPage extends Composite {

	protected static String logoutButtonText = "Log out";
	protected static String welcomeIntro = "Logged in as: ";
	protected static String requestFailedMsg = "Sorry, an error occurred";

	interface DashboardCruiseListPageUiBinder extends
			UiBinder<Widget, DashboardCruiseListPage> {
	}

	private static DashboardCruiseListPageUiBinder uiBinder = 
			GWT.create(DashboardCruiseListPageUiBinder.class);

	@UiField InlineLabel welcomeLabel;
	@UiField Button logoutButton;

	public DashboardCruiseListPage() {
		initWidget(uiBinder.createAndBindUi(this));
		logoutButton.setText(logoutButtonText);
	}

	public void updateCruises(DashboardCruiseListing cruises) {
		welcomeLabel.setText(
				SafeHtmlUtils.htmlEscape(welcomeIntro + cruises.getUsername()));
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		XsrfTokenServiceAsync xsrf = GWT.create(XsrfTokenService.class);
		((ServiceDefTarget) xsrf).setServiceEntryPoint(
				GWT.getModuleBaseURL() + "xsrf");
		xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>() {
			@Override
			public void onSuccess(XsrfToken token) {
				DashboardLogout logoutPage = 
						DashboardPageFactory.getPage(DashboardLogout.class);
				RootPanel.get().remove(DashboardCruiseListPage.this);
				RootPanel.get().add(logoutPage);
				logoutPage.doLogout(token);
			}
			@Override
			public void onFailure(Throwable ex) {
				Window.alert(SafeHtmlUtils.htmlEscape(
						requestFailedMsg + " (" + ex.getMessage() + ")"));
			}
		});
	}
}
