package gov.noaa.pmel.tmap.las.ui;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;
import org.json.XML;

import com.opensymphony.xwork2.ActionSupport;

public class ConfigService extends ActionSupport implements ServletRequestAware, ServletResponseAware, ApplicationAware {
	HttpServletResponse response;
	HttpServletRequest request;
	Map<String, Object> contextAttributes;
	private static Logger log = LoggerFactory.getLogger(ConfigService.class.getName());
	public static void sendError(HttpServletResponse response, String container, String format, String message) {
		PrintWriter respout;
		String xml = "<"+container+"><status>error</status><error>"+message+"</error></"+container+">";
		try {
			if ( format.equals("xml") ) {
				respout = response.getWriter();
			} else {
				respout = response.getWriter();
				JSONObject json_response = XML.toJSONObject(xml);
				json_response.write(respout);
			}
		} catch (Exception e) {
			// At this point we're toast and no error message will be generated.
			log.error("Error in JSON response: "+e.getMessage());
		} 
	}
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	@Override
	public void setApplication(Map<String, Object> contextAttributes) {
		this.contextAttributes = contextAttributes;
	}

}
