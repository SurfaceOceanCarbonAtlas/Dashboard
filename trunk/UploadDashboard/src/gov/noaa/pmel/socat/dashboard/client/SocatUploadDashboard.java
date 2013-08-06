package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class SocatUploadDashboard implements EntryPoint {

	/**
	 *  Entry point for the SOCAT Upload Dashboard.
	 */
	public void onModuleLoad() {
		DashboardLogin loginPage = DashboardPageFactory.getPage(DashboardLogin.class);
		loginPage.clearLogin();
		RootPanel.get().add(loginPage);
	}

}
