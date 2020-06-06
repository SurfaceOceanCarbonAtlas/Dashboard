/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.product.server;

import java.util.Map;

import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;


/**
 * This action takes in a blob of request XML and stores the variable definition in the session scope.  The definition of the variable
 * includes an F-TDS URL that will create the necessary JNL file and virtual variables when it is first used by an OPeNDAP client.
 * @author rhs
 *
 */
public class LASAction extends ActionSupport implements ServletRequestAware, ServletResponseAware, ApplicationAware {
	public HttpServletRequest request;
	public HttpServletResponse response;
	public Map<String, Object> contextAttributes;
	ProgressForm progress = new ProgressForm();
	private static Logger log = LoggerFactory.getLogger(LASAction.class.getName());
	   public static void logerror(HttpServletRequest request) {
	        LASBackendResponse error = (LASBackendResponse) request.getSession().getAttribute("las_response");
	        log.error(error.getResult("las_message"));
	        log.error(error.getResult("exception_message"));
	    }
	    
	    public static void logerror(HttpServletRequest request, String las_message, String exception_message) {
	        LASBackendResponse lasBackendResponse = new LASBackendResponse();
	        lasBackendResponse.setError("las_message", las_message);
	        lasBackendResponse.addError("exception_message", exception_message);
	        request.setAttribute("las_response", lasBackendResponse);
	        log.error(las_message);
	        log.error(exception_message);         
	    }
	    
	    public static void logerror(HttpServletRequest request, String las_message, Exception e) {
	        LASBackendResponse lasBackendResponse = new LASBackendResponse();
	        lasBackendResponse.setError("las_message", las_message);
	        lasBackendResponse.addError("exception_message", e.toString());
	        request.setAttribute("las_response", lasBackendResponse);
	        StackTraceElement[] trace =  e.getStackTrace();
	        log.error(las_message);
	        log.error(e.toString());
	        if ( trace.length > 0 ) {
	            log.error(trace[0].toString());
	        }           
	    }

		@Override
		public void setServletRequest(HttpServletRequest request) {
			this.request = request;
		}

		@Override
		public void setServletResponse(HttpServletResponse response) {
			this.response = response;
		}
		
		@Override
		public void setApplication(Map<String, Object> servletContextAttributes) {
			this.contextAttributes = servletContextAttributes;
		}

		public void setProgressForm(ProgressForm progress) {
			this.progress = progress;
		}
		
}
