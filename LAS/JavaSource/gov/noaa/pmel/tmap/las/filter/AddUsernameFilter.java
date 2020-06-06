package gov.noaa.pmel.tmap.las.filter;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Assigns the "user_name" attribute to a session, which comes from the name
 * in the user Principal of an authenticated user.
 */
public class AddUsernameFilter implements Filter {
	private static Logger log = LoggerFactory.getLogger(AddUsernameFilter.class.getName());
	private static final String ATTRIBUTE_NAME = "user_name";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
			FilterChain chain) throws IOException, ServletException {
		log.debug("Entering AddUsernameFilter");
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession(false);
		if ( session == null ) {
			log.debug("AddUsernameFilter - no session; go login");
			response.sendRedirect("/socatlogin.html");
			return;
		}

		// Check if user_name is already assigned for this session 
		String username;
		try {
			username = session.getAttribute(ATTRIBUTE_NAME).toString().trim();
		} catch ( Exception ex ) {
			// Probably null pointer exception
			username = "";
		}
		if ( ! username.isEmpty() ) {
			log.debug("AddUsernameFilter - found existing session attribute " +
					ATTRIBUTE_NAME + "=" + username);
			chain.doFilter(request, response);
			return;
		}

		// Check if there is a username from authenticating
		try {
			username = request.getUserPrincipal().getName().trim();
		} catch ( Exception ex ) {
			// Probably null pointer exception - leave username empty
		}
		if ( ! username.isEmpty() ) {
			session.setAttribute(ATTRIBUTE_NAME, username);
			log.debug("AddUsernameFilter - added session attribute " + 
						ATTRIBUTE_NAME + "=" + username);
			chain.doFilter(request, response);
			return;
		}

		log.debug("AddUsernameFilter - user name is empty; go login");
		response.sendRedirect("/socatlogin.html");
		return;
	}

	@Override
	public void destroy() { }

}

