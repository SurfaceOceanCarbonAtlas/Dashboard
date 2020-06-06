package gov.noaa.pmel.tmap.las.ui;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.noaa.pmel.tmap.addxml.JDOMUtils;
import gov.noaa.pmel.tmap.las.client.lastest.TestConstants;
import gov.noaa.pmel.tmap.las.client.serializable.TestSerializable;

import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASTestResults;
import gov.noaa.pmel.tmap.las.product.server.LASAction;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;

public class GetTestResults extends LASAction {

	private static String UI = "ui";
	private static String TEST_RESULTS = "test_results";
	private static String FULL_TEST_RESULTS = "full_test_results";
	private static String TEST_RESULT_FULL = "test_result_full";
	private static String TEST_RESULT = "test_result";
	@Override
	public String execute()
			throws Exception {
		String test = request.getParameter("test");
		LASTestResults testResults = new LASTestResults();
		LASConfig lasConfig = (LASConfig) contextAttributes.get(LASConfigPlugIn.LAS_CONFIG_KEY);
		if ( lasConfig == null ) {
			return UI;
		}
		String test_output_file = lasConfig.getOutputDir()+File.separator+TestConstants.TEST_RESULTS_FILE;
		File c = new File(test_output_file);
		if ( c.exists() ) {
			JDOMUtils.XML2JDOM(new File(test_output_file), testResults);
		}
		// If there is no argument, then go to the failure only test results page.
		if ( test == null ) {
			request.setAttribute("testResults", testResults);
			return TEST_RESULTS;
		} else if ( test.equals("full") ) { 
			request.setAttribute("testResults", testResults);
			return FULL_TEST_RESULTS;
		} else if ( test.contains("full") ) {
			TestSerializable testS = testResults.getTest(test);
			request.setAttribute("test", testS);
			return TEST_RESULT_FULL;
		} else {
			TestSerializable testS = testResults.getTest(test);
			request.setAttribute("test", testS);
			return TEST_RESULT;
		}
	}

}
