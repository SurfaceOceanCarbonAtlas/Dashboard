/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;

/**
 * Page for specifying the data column types in a DashboardCruiseWithData
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnSpecsPage extends Composite {

	protected static String welcomeIntro = "Logged in as: ";
	protected static String logoutText = "Logout";

	interface CruiseDataColumnSpecsPageUiBinder extends	
			UiBinder<Widget, CruiseDataColumnSpecsPage> {
	}

	private static CruiseDataColumnSpecsPageUiBinder uiBinder = 
			GWT.create(CruiseDataColumnSpecsPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField Label cruiseLabel;
	@UiField DataGrid<CruiseDataColumnSpecs> dataGrid;

	AsyncDataProvider<CruiseDataColumnSpecs> asyncCruiseDataProvider;

	CruiseDataColumnSpecsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		logoutButton.setText(logoutText);

		// TODO: create the data grid with drop-down headers
		// TODO: create the simple pager 
		// TODO: create the async data provider
	}

	void updateCruiseData(CruiseDataColumnSpecs cruiseData) {
		// Update the username
		userInfoLabel.setText(welcomeIntro + DashboardPageFactory.getUsername());
		// TODO: update the column names and data
	}

}
