package gov.noaa.pmel.tmap.las.filter;

import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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



public class AuthenticationFilter implements Filter {
	private static Logger log = LoggerFactory.getLogger(AuthenticationFilter.class.getName());
	private String databaseUrl;
	private String selectUser;
	private String selectPass;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("AuthenticationFilter: initializing");
		// Register the SQL driver - no harm if already registered
		String driverName = filterConfig.getInitParameter("driverName");
		if ( driverName == null )
			throw new ServletException("driverName not defined in the AuthenticationFilter configuration");
		try {
			Class.forName(driverName).newInstance();
		} catch (Exception ex) {
			throw new ServletException("Unable to register the SQL driver " + 
					driverName + ": " + ex.getMessage());
		}
		//Get the rest of the info for making database queries
		databaseUrl = filterConfig.getInitParameter("databaseURL");
		if ( databaseUrl == null )
			throw new ServletException("databaseURL not defined in the AuthenticationFilter configuration");
		selectUser = filterConfig.getInitParameter("selectUsername");
		if ( selectUser == null ) 
			throw new ServletException("selectUsername not defined in the AuthenticationFilter configuration");
		selectPass = filterConfig.getInitParameter("selectPassword");
		if ( selectPass == null ) 
			throw new ServletException("selectPassword not defined in the AuthenticationFilter configuration");
		Connection catConn = null;
		try {
			catConn = DriverManager.getConnection(databaseUrl, selectUser, selectPass);
			if ( catConn == null )
				throw new ServletException("unable to make a connection to the database for AuthenticationFilter: null returned");
			catConn.close();
		} catch (SQLException ex) {
			throw new ServletException("Unable to make a connection to the database for AuthenticationFilter: " + ex.getMessage());
		} finally {
			if ( catConn !=  null ) {
				try {
					catConn.close();
				} catch (SQLException e) {
					log.error("Connection failed to close.");
				}
			}
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
			FilterChain chain) throws IOException, ServletException {
		log.info("Authentication request received");
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession(true);

		// Check if we have already authenticated this user
		try {
			String username = session.getAttribute("user_name").toString();
			if ( ! username.trim().isEmpty() ) {
				log.info("Already authenticated user " + username);
				chain.doFilter(request, response);
				return;
			}
		} catch ( Exception ex ) {
			// Probably null pointer exception - continue on
			;
		}

		// Check if already authenticated by Tomcat
		Principal principal = request.getUserPrincipal();
		if ( principal != null) {
			String username = principal.getName();
			if ( (username != null) && ! username.trim().isEmpty() ) {
				session.setAttribute("user_name", username);
				log.info("Tomcat authenticated user " + username);
				chain.doFilter(request, response);
				return;
			}
		}

		// Check if the username has already been requested
		String username;
		try {
			username = request.getAttribute("user_name").toString();
			if ( username.trim().isEmpty() )
				throw new NullPointerException();
		} catch ( Exception ex ) {
			// TODO: Make a request for the username, assigning it to attribute "user_name"
			log.info("TODO: Request username");
			return;
		}

		int reviewerId = -1;
		try {
			Connection catConn = DriverManager.getConnection(databaseUrl, selectUser, selectPass);
			if ( catConn == null ) {
				log.error("Unable to make a connection to the database for authentication of " + username);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "database access failure");
				return;
			}
			PreparedStatement prepStmt = catConn.prepareStatement(
					"SELECT `reviewer_id` FROM `Reviewers` WHERE `username` = ?");
			prepStmt.setString(1, username);
			ResultSet results = prepStmt.executeQuery();
			while ( results.next() ) {
				if ( reviewerId > 0 ) {
					log.error("More than one reviewer matches the username " + username);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "database configuration errors");
					return;
				}
				reviewerId = results.getInt(1);
			}
			results.close();
			catConn.close();
		} catch ( SQLException ex ) {
			log.error("SQLException when trying to authenticate " + username + ": " + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "database access errors");
			return;
		}
		if ( reviewerId <= 0 ) {
			// Unknown username, so return a unauthorized response
			log.info("Unauthorized: unknown username " + username);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unknown username");
			return;
		}
		// Set the user_name attribute so subsequent requests in this session can bypass the SQL check
		session.setAttribute("user_name", username);
		log.info("Authenticated user " + username);
	}

	@Override
	public void destroy() { 
		; // Nothing to do
	}

}