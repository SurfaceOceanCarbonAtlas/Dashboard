/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

// TODO: remove this class used for debugging

/**
 * Class to allow debugging with a XsrfProtectedServiceServlet 
 */
public class ForceSessionCreationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
					FilterChain filterChain) throws IOException, ServletException {
		// This will force the creation of a session instance for the user, 
		// if one doesn't already exist.  Part of the creation includes the 
		// creation of a session variable name "JSESSIONID".
		((HttpServletRequest) servletRequest).getSession(true);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {        
	}

	@Override
	public void destroy () {
	}

}
