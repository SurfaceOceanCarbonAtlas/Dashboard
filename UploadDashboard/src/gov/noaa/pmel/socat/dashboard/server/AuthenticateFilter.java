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


/**
 * Assigns the "user_name" attribute to a session, which comes from the name
 * in the user Principal of an authenticated user.
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

		// Check that there is a session and a username from authenticating
		String username = "";
		if ( request.getSession(false) != null ) {
			try {
				username = request.getUserPrincipal().getName().trim();
			} catch ( Exception ex ) {
				// Probably null pointer exception - leave username empty
			}
		}
		if ( username.isEmpty() ) {
			response.sendRedirect("socatlogin.html");
			return;
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() { 
	}

}

