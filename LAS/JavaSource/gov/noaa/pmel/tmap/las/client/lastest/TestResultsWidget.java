package gov.noaa.pmel.tmap.las.client.lastest;

import gov.noaa.pmel.tmap.las.client.serializable.TestDataset;
import gov.noaa.pmel.tmap.las.client.serializable.TestSerializable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TestResultsWidget extends Composite {
	DisclosurePanel testPanel;
	VerticalPanel layoutPanel = new VerticalPanel();
	VerticalPanel tablePanel = new VerticalPanel();
	HorizontalPanel buttonPanel = new HorizontalPanel();
	ToggleButton failedButton = new ToggleButton("Failed Tests");
	ToggleButton allButton = new ToggleButton("All Tests");
	TestSerializable test;
	public TestResultsWidget(TestSerializable test) {
		this.test = test;
		buttonPanel.add(failedButton);
		buttonPanel.add(allButton);
		failedButton.setDown(true);
		failedButton.addClickHandler(failedClick);
		allButton.addClickHandler(allClick);
		testPanel = new DisclosurePanel(test.getName());
		testPanel.setWidth("100%");
		HTML meta = new HTML("<h3>Test from: "+test.getDateString()+"</h3>");
		layoutPanel.add(meta);
		layoutPanel.add(buttonPanel);
		layoutPanel.add(tablePanel);
		TestDataset[] ds = test.getDatasetsWithFailedResult();
		if ( test.getID().equals(TestConstants.KEY_TEST_PRODUCT)) {
			ProductTestTable testTable = new ProductTestTable(ds, true);
			tablePanel.add(testTable);
		} else {
			DataTestTable testTable = new DataTestTable(ds, true);
			tablePanel.add(testTable);
		}
		testPanel.add(layoutPanel);
		initWidget(testPanel);
	}
    ClickHandler failedClick = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			allButton.setDown(false);
			tablePanel.clear();
			TestDataset[] ds = test.getDatasetsWithFailedResult();
			if ( test.getID().equals(TestConstants.KEY_TEST_PRODUCT)) {
				ProductTestTable testTable = new ProductTestTable(ds, true);
				tablePanel.add(testTable);
			} else {
				DataTestTable testTable = new DataTestTable(ds, true);
				tablePanel.add(testTable);
			}
		}  	
    };
    ClickHandler allClick = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			failedButton.setDown(false);
			tablePanel.clear();
			TestDataset[] ds = test.getTestDatasets();
			if ( test.getID().equals(TestConstants.KEY_TEST_PRODUCT)) {
				ProductTestTable testTable = new ProductTestTable(ds, false);
				tablePanel.add(testTable);
			} else {
				DataTestTable testTable = new DataTestTable(ds, false);
				tablePanel.add(testTable);
			}
		}
    	
    };
}
