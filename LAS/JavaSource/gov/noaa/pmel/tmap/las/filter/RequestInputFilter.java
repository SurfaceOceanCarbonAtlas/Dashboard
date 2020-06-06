package gov.noaa.pmel.tmap.las.filter;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.server.LASAction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestInputFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(RequestInputFilter.class.getName());
    /*
     * For the record these values could have been passed in via parameters in the web.xml...
     * 
     * If this proves to be too difficult to maintain we can move them there...
     * 
     * Start by defining groups of parameters...
     *   <init-param>
     *      <param-name>boolean</param-name>
     *      <param-value>'cancel,debug,stream'</param-value>
     *   </init-param>
     *   <init-param>
     *       <param-name>id</param-name>
     *       <param-value>catitem,dsid,JSESSIONID,opid,plot,stream_ID,varid</param-value>
     *   </init-param>
     *   <init-param>
     *       <param-name>BBOX</param-name>
     *       <param-value>BBOX</param-name>
     *   </init-param>
     *   
     *   
     *   then by convention we would use a regular expression to evaluate the parameter if we found a foo_regex to associate with a
     *   group of parameters.
     *   <init-param>
     *       <param-name>id_regex</param-name>
     *       <param-value>[A-Za-z0-9._-]</param-value>
     *   </init-param>
     *   
     *   or by convention we could use a list to enumerate the possible values
     *   
     *   <init-param>
     *       <param-name>boolean_values</param-name>
     *       <param-value>true,false</param-value>
     *   </init-param>
     *   
     *   the xml parameter would be a special case.
     *   
     *   Seems more trouble than it's worth at the moment...
     *   
     *   For now any parameter should go in the "p" list.
     *   Any to be evaluated as a boolean with values of "true" or "false" should go in the pb list.
     *   Any to be evaluated to see if it is a valid LAS ID (only contains numbers, letters, . _ and - should to in the lp list.
     *   Anything else is a special case.
     *
     */
    // All LAS query parameters.
    private final static String[] p = {
    	"auto",
	    "auth_url",    
	    "BBOX", 
	    "stream_id",
        "stream",
    	"cancel",
    	"cancel_button",
    	"catid", 
    	"catitem", 
    	"clean",
    	"cleanbutton",
    	"cleands",
    	"data_url",
    	"debug",
    	"dojo.preventCache",
    	"dsid",
    	"dsID",
    	"email",
    	"end",
    	"embutton",
    	"format",
    	"file",
    	"image",
        "initXHi",
        "initXLo",
        "initYHi",
        "initYLo",
    	"JSESSIONID",
    	"key",
    	"lasxmldoc",
		"log_level",
		"opendap",
		"openid",
		"opid",
		"plot",
		"reinit", 
		"REQUEST", 
		"start",
		"stream",
		"stream_ID", 
		"template",
		"test",
		"varid",
		"varID",
		"view",
		"xml",
		"xlo",
		"xhi",
		"ylo",
                "yhi",
                "zlo",
                "zhi",
                "tlo",
                "thi",
                "tid",
		"xpath",
		"gwt.codesvr",
		"provider",
		"code",
		"state",
		"remove_url"
    };
    private final static Set<String> LAS_PARAMETERS = new HashSet<String>(Arrays.asList(p));
	
    // Parameters that should be either "true" or "false"
    private final static String[] bp = {
    	"cancel",
    	"debug",
    	"stream",
    	"error",
    	"auto"
    };
    private final static Set<String> LAS_BOOLEAN_PARAMETERS = new HashSet<String>(Arrays.asList(bp));
    
    // Every parameter that accepts an LAS ID (just letters, numbers, -, ., _  (cancel_button has value Cancel.  This will filter code in that button
    private static final String[] lp = {
    	"catid", 
    	"catitem", 
    	"dsid",
    	"JSESSIONID",
		"opid",
		"plot",
		"stream_ID", 
		"cancel_button",
		"varid"
    };
	
    private final static Set<String> LAS_ID_PARAMETERS = new HashSet<String>(Arrays.asList(lp));
    private final static Pattern ID_PATTERN = Pattern.compile("[/A-Za-z0-9._-]+");
    // Match 4 floating point numbers separated by a comma
    private final static Pattern BBOX_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+");
    
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		 if ( ! ( servletRequest instanceof HttpServletRequest ) )
		    {
		      log.error( "doFilter(): Not an HTTP request! How did this filter get here?" );
		      filterChain.doFilter( servletRequest, servletResponse );
		      return;
		    }
		    HttpServletRequest request = (HttpServletRequest) servletRequest;
		    HttpServletResponse response = (HttpServletResponse) servletResponse;
            if ( ! validateParameters(request) ) {
            	LASAction.logerror(request, "Illegal request parameter.", "Request contains a parameter that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal query parameter.");
            	return;
            }
            if ( !validateTemplates(request) ) {
				LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal query parameter value.");
				return;
			}
            if ( ! validBooleanValues(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal boolean query parameter value.");
            	return;
            }
            if ( ! validIds(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal LAS ID query parameter value.");
            	return;
            }
            if ( !validBBOX(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal BBOX query parameter value.");
            	return;
            }
            if ( !validFormat(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal format query parameter value.");
            	return;
            }
            if ( !validWMSRequest(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal WMS query parameter value.");
            	return;
            }
            if ( !validView(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal view query parameter value.");
            	return;
            }
            if ( !validReinit(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal reinit query parameter value.");
            	return;
            }
            if ( !validLogLevel(request) ) {
            	LASAction.logerror(request, "Illegal request parameter value.", "Request contains a parameter value that is not allowed.");
            	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal log_level query parameter value.");
            	return;
            }
            String requestXML = request.getParameter("xml");
            if ( requestXML == null ) {
              requestXML = request.getParameter("lasxmldoc");
            }
            if ( (requestXML != null && !requestXML.equals("")) ) {
            	try {
            		requestXML = JDOMUtils.decode(requestXML, "UTF-8");
            	} catch (UnsupportedEncodingException e) {
            		LASAction.logerror(request, "Error decoding the XML request query string.", e);
            		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal xml query parameter value.");
            		return;
            	}

            	// Create a lasRequest object.
            	LASUIRequest lasRequest = new LASUIRequest();
            	try {
            		JDOMUtils.XML2JDOM(requestXML, lasRequest);
            		// Set the lasRequest object in the HttpServletRequest so the product server does not have to rebuild it.
            		request.setAttribute("las_request", lasRequest);
            	} catch (Exception e) {
            		LASAction.logerror(request, "Error parsing the request XML. ", e);
            		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request contains an illegal xml query parameter value.");
            		return;
            	}
            }
		    filterChain.doFilter( servletRequest, servletResponse );
		    return;
	}
	public boolean validateTemplates(HttpServletRequest request) {
		String value[] = request.getParameterValues("template");
		if ( value != null ) {
            for (int i = 0; i < value.length; i++) {
                String v = value[i];
                if (v.toLowerCase().contains(">") || v.toLowerCase().contains("<") || v.toLowerCase().contains("script")) {
                    return false;
                }
            }
        }
		return true;
	}
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	private boolean validLogLevel(HttpServletRequest request) {
		String value[] = request.getParameterValues("log_level");
		if ( value == null || value.length == 0 ) {
			return true;
		}
		if ( value.length > 1 ) return false;
		
		if ( !value[0].trim().toLowerCase(Locale.ENGLISH).equals("debug") && 
			 !value[0].trim().toLowerCase(Locale.ENGLISH).equals("info") && 
			 !value[0].trim().toLowerCase(Locale.ENGLISH).equals("warn") && 
			 !value[0].trim().toLowerCase(Locale.ENGLISH).equals("error") &&
			 !value[0].trim().toLowerCase(Locale.ENGLISH).equals("fatal")
			 ) return false;
		return true;
	}
	private boolean validReinit(HttpServletRequest request ) {
		String value[] = request.getParameterValues("reinit");
		if ( value == null || value.length == 0 ) {
			return true;
		}
		if ( value.length > 1 ) return false;
		if ( !value[0].equals("wait") && !value[0].equals("force") ) return false;
		return true;
	}
	private boolean validView(HttpServletRequest request) {
		String value[] = request.getParameterValues("view");
		if ( value == null || value.length == 0 ) {
			return true;
		}
		if ( value.length > 1 ) return false;
		
		if ( !value[0].equals("x") && 
			 !value[0].equals("y") && 
			 !value[0].equals("z") && 
			 !value[0].equals("t") && 
			 !value[0].equals("xy") && 
			 !value[0].equals("xz") && 
			 !value[0].equals("xt") && 
			 !value[0].equals("yx") && 
			 !value[0].equals("yz") && 
			 !value[0].equals("yt") && 
			 !value[0].equals("zt") && 
			 !value[0].equals("xyz") && 
			 !value[0].equals("xyt") && 
			 !value[0].equals("xzt") && 
			 !value[0].equals("yzt") && 
			 !value[0].equals("xyzt")
			 ) return false;
		return true;
	}
	private boolean validWMSRequest(HttpServletRequest request) {
		String value[] = request.getParameterValues("REQUEST");
		if ( value == null || value.length == 0 ) {
			return true;
		}
		if ( value.length > 1 ) return false;
		if ( !value[0].equals("GETMAP") && !value[0].equals("GETCAPABILITES") && !value[0].equals("GETFEATUREINFO") ) return false;
		return true;
	}
	private boolean validFormat(HttpServletRequest request) {
		String value[] = request.getParameterValues("format");
		if ( value == null || value.length == 0 ) {
			return true;
		}
		if ( value.length > 1 ) return false;
		if ( !value[0].equals("json") && !value[0].equals("xml") &&  !value[0].equals("full")) return false;
		return true;
	}
	private boolean validTest(HttpServletRequest request) {
		String value[] = request.getParameterValues("test");
		if ( value == null || value.length == 0 ) {
			return true;
		}
		if ( value.length > 1 ) return false;
		if ( !value[0].equals("ftds") && !value[0].equals("opendap") && !value[0].equals("product") && 
			 !value[0].equals("ftds_full") && !value[0].equals("opendap_full") && !value[0].equals("product_full") &&
			 !value[0].equals("full") && !value.equals("all") ) return false;
		return true;
	}
	private boolean validBBOX(HttpServletRequest request) {
		String value[] = request.getParameterValues("BBOX");
		if ( value == null || value.length == 0 ) {
			return true;
		}
		if ( value.length > 1 ) return false; // only allow 1
		Matcher m = BBOX_PATTERN.matcher(value[0]);
		return m.matches();
	}
	// email address    "^[A-Za-z0-9._-]+@[[A-Za-z0-9.-]+$"
	// LAS ID "^[A-Za-z0-9._-]"
	private boolean validateParameters(HttpServletRequest request) {
		Set<String> parameters = request.getParameterMap().keySet();
		for (Iterator pIt = parameters.iterator(); pIt.hasNext();) {
			String name = (String) pIt.next();
			log.debug("Found parameter: "+name);
		}
		return LAS_PARAMETERS.containsAll(parameters);
	}
    private boolean validBooleanValues(HttpServletRequest request) {
    	boolean valid = true;
    	for (Iterator keyIt = LAS_BOOLEAN_PARAMETERS.iterator(); keyIt.hasNext();) {
			String key = (String) keyIt.next();
			String[] value = request.getParameterValues(key);
			valid = valid && validateBooleanValue(value);			
		}
    	return valid;
    }
    private boolean validateBooleanValue(String[] value) {

		if ( value == null || value.length == 0 ) return true;
		
		if ( value.length > 1 ) return false;
		
		if ( !value[0].equals("true") && !value[0].equals("false") ) return false;
		
		return true;
    }

	private boolean validIds(HttpServletRequest request) {
		boolean valid = true;
    	for (Iterator keyIt = LAS_ID_PARAMETERS.iterator(); keyIt.hasNext();) {
			String key = (String) keyIt.next();
			String[] value = request.getParameterValues(key);
			valid = valid && validateIdValue(value);			
		}
    	return valid;
	}

	private boolean validateIdValue(String[] value) {
		if ( value == null || value.length == 0 ) return true;
		// More than one is allowed so don't test the length
		Matcher m = ID_PATTERN.matcher(value[0]);
		return m.matches();
	}
}
