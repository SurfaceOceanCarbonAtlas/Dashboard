package gov.noaa.pmel.tmap.las.ui;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import gov.noaa.pmel.tmap.addxml.JDOMUtils;
import gov.noaa.pmel.tmap.las.client.serializable.TestSerializable;
import gov.noaa.pmel.tmap.las.client.lastest.TestConstants;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASTestResults;
import gov.noaa.pmel.tmap.las.product.server.LASAction;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;
import gov.noaa.pmel.tmap.las.test.LASTest;

public class GetTestResults extends LASAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String test = request.getParameter("test");
		LASTestResults testResults = new LASTestResults();
		LASConfig lasConfig = (LASConfig) servlet.getServletContext().getAttribute(LASConfigPlugIn.LAS_CONFIG_KEY);
		if ( lasConfig == null ) {
			return mapping.findForward("ui");
		}
		String test_output_file = lasConfig.getOutputDir()+File.separator+TestConstants.TEST_RESULTS_FILE;
		File c = new File(test_output_file);
		if ( c.exists() ) {
			JDOMUtils.XML2JDOM(new File(test_output_file), testResults);
		}
		// If there is no argument, then go to the failure only test results page.
		if ( test == null ) {
			request.setAttribute("testResults", testResults);
			return mapping.findForward("test_results");
		} else if ( test.equals("full") ) { 
			request.setAttribute("testResults", testResults);
			return mapping.findForward("full_test_results");
		} else if ( test.contains("full") ) {
			TestSerializable testS = testResults.getTest(test);
			request.setAttribute("test", testS);
			return mapping.findForward("test_result_full");
		} else {
			TestSerializable testS = testResults.getTest(test);
			request.setAttribute("test", testS);
			return mapping.findForward("test_result");
		}
	}

}
