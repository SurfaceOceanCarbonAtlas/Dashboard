package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * Assigns the "user_name" attribute to a session, which comes from the name
 * in the user Principal of an authenticated user.
 */
public class AuthenticateFilter implements Filter {

	private Logger logger;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { 
		logger = Logger.getLogger("AuthenticateFilter");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
			FilterChain chain) throws IOException, ServletException {

		logger.debug("Entering AuthenticateFilter");
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String username = "";
		if ( request.getSession(false) != null ) {
			try {
				username = request.getUserPrincipal().getName().trim();
			} catch ( Exception ex ) {
				; // Probably null pointer exception - leave username empty
			}
		}

		// Check if there is a username from authenticating
		if ( username.isEmpty() ) {
			logger.debug("AuthenticateFilter - no login name; go login");
			response.sendRedirect("/SocatUploadDashboard/socatlogin.html");
			return;
		}

		logger.debug("AuthenticateFilter - username found: " + username);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() { 
	}

}

