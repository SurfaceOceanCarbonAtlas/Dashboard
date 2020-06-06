package gov.noaa.pmel.tmap.las.client.lastest;

import gov.noaa.pmel.tmap.las.client.serializable.TestSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class TestResultsDisplay implements EntryPoint {

	@Override
	public void onModuleLoad() {
		Util.getRPCService().getTestResults(null, testCallback);
	}
    AsyncCallback<TestSerializable[]> testCallback = new AsyncCallback<TestSerializable[]> () {

		@Override
		public void onFailure(Throwable caught) {
			HTML fail = new HTML("No test results found.");
			RootPanel.get(TestConstants.KEY_TEST_DIRECT_OPENDAP).add(fail);
		}

		@Override
		public void onSuccess(TestSerializable[] tests) {
			for (int i = 0; i < tests.length; i++) {
				TestResultsWidget widget = new TestResultsWidget(tests[i]);
				RootPanel.get(tests[i].getID()).add(widget);
			}		
		}
    };
}
