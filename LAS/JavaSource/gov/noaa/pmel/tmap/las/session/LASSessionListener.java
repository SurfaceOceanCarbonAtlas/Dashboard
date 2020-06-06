package gov.noaa.pmel.tmap.las.session;

import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class LASSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		// At the moment, nothing to do here...
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		ServletContext context = event.getSession().getServletContext();
		LASConfig lasConfig = (LASConfig) context.getAttribute(LASConfigPlugIn.LAS_CONFIG_KEY);
		context.setAttribute(LASConfigPlugIn.LAS_LOCK_KEY, "true");
        String JSESSIONID = event.getSession().getId();       
        if ( lasConfig != null ) {
        	lasConfig.removeRemoteVariables(JSESSIONID);
        }
        context.removeAttribute(LASConfigPlugIn.LAS_LOCK_KEY);
	}

}
