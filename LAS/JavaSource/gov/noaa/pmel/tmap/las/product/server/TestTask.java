package gov.noaa.pmel.tmap.las.product.server;

import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.test.LASTest;
import gov.noaa.pmel.tmap.las.test.LASTestOptions;

import java.io.UnsupportedEncodingException;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jdom.JDOMException;

public class TestTask extends TimerTask {
	ServletContext context;
	private static Logger log = LoggerFactory.getLogger(TestTask.class.getName());
	public TestTask(ServletContext context) {
		this.context = context;
	}
	@Override
	public void run() {
		LASConfig lasConfig = (LASConfig) context.getAttribute(LASConfigPlugIn.LAS_CONFIG_KEY);
        // If this is null, we're lazy starting so we'll skip it for now.
		if ( lasConfig != null ) {
			
			
			LASTestOptions l = lasConfig.getTestOptions();
            if ( l != null ) {
            	// If tests were configured, run them...
            	LASTest lasTest = new LASTest(l, lasConfig);
    			lasTest.runTest(l, true);
            }
			
		}
	}
}
