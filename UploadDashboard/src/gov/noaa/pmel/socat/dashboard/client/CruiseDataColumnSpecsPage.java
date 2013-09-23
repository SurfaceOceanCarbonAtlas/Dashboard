/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;

/**
 * Page for specifying the data column types in a DashboardCruiseWithData.
 * A new page needs to be created each time since the number of data columns
 * can vary with each cruise. 
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnSpecsPage extends Composite {

	protected static String welcomeIntro = "Logged in as: ";
	protected static String logoutText = "Logout";
	protected static String submitText = "Submit Column Types";
	protected static String cancelText = "Return to Cruise List";

	interface CruiseDataColumnSpecsPageUiBinder extends	
			UiBinder<Widget, CruiseDataColumnSpecsPage> {
	}

	private static CruiseDataColumnSpecsPageUiBinder uiBinder = 
			GWT.create(CruiseDataColumnSpecsPageUiBinder.class);

	@UiField Label userInfoLabel;
	@UiField Button logoutButton;
	@UiField Label cruiseLabel;
	@UiField DataGrid<CruiseDataColumnSpecs> dataGrid;
	@UiField SimplePager gridPager;
	@UiField Button submitButton;
	@UiField Button cancelButton;

	AsyncDataProvider<CruiseDataColumnSpecs> asyncCruiseDataProvider;

	CruiseDataColumnSpecsPage(CruiseDataColumnSpecs cruiseData) {
		initWidget(uiBinder.createAndBindUi(this));

		userInfoLabel.setText(welcomeIntro + DashboardPageFactory.getUsername());
		logoutButton.setText(logoutText);
		cruiseLabel.setText(cruiseData.getExpocode());

		// TODO: build table with drop-down column type and unit specifications
		// TODO: create the async data provider
		// TODO: configure the pager
	}

}
