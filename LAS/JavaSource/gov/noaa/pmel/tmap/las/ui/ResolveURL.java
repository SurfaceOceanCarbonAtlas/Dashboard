package gov.noaa.pmel.tmap.las.ui;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;

import java.io.PrintWriter;

public class ResolveURL extends ConfigService {

	@Override
	public String execute() throws Exception {
		LASConfig lasConfig = (LASConfig) contextAttributes.get(LASConfigPlugIn.LAS_CONFIG_KEY); 
		String xml = request.getParameter("xml");
		LASUIRequest lasRequest = new LASUIRequest();
		JDOMUtils.XML2JDOM(xml, lasRequest);
		String resolvedRequest = lasConfig.resolveURLS(lasRequest);
		PrintWriter respout = response.getWriter();
		response.setContentType("application/xml");
		respout.print(resolvedRequest);
		return null;
	}

}
