package gov.noaa.pmel.tmap.las.product.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InitThread extends Thread {
	private ServletContext context;
	private static Logger log = LoggerFactory.getLogger(InitThread.class.getName());
	public InitThread(ServletContext context) {
		this.context = context;
	}
	public void run() {
		LASConfigPlugIn plugin = new LASConfigPlugIn();
		try {
			log.info("START: Server initialization.");
			plugin.reinit(context);
		} catch (ServletException e) {
			log.error("Error in LAS init thread.");
		}
		context.removeAttribute(LASConfigPlugIn.LAS_LAZY_START_RUNNING_KEY);
		context.removeAttribute(LASConfigPlugIn.LAS_LAZY_START_KEY);
		log.info("END: Initialization complete.  Server ready for requests.");
	}
}
