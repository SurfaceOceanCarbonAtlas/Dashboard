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
import javax.servlet.http.HttpSession;


/**
 * Authenticates a user for a session
 */
public class AuthenticateFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession(false);
		if ( session == null ) {
			// Session has expired
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "session timed out - refresh page to login again");
			return;
		}

		if ( session.isNew() ) {
			// New session - go login
			response.sendRedirect("socatlogin.html");
			return;
		}

		// Check the user name used by the servers
		String username = "";
		try {
			username = request.getUserPrincipal().getName().trim();
		} catch ( Exception ex ) {
			// No user principal or name - leave username empty
		}
		if ( username.isEmpty() ) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not logged in - refresh page to login");
			return;
		}

		// All is well - continue on
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() { 
	}

}

