package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class SocatUploadDashboard implements EntryPoint {

	/**
	 *  Entry point for the SOCAT Upload Dashboard.
	 */
	public void onModuleLoad() {
		DashboardLogin loginPage = DashboardPageFactory.getPage(DashboardLogin.class);
		RootLayoutPanel.get().add(loginPage);
		loginPage.clearLoginData(true);
	}

}
