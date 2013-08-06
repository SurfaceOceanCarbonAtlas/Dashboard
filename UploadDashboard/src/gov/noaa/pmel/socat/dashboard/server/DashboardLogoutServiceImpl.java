/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardLogoutService;

import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

/**
 * Server side implementation of the DashboardLogoutService
 * 
 * @author Karl Smith
 */
public class DashboardLogoutServiceImpl extends XsrfProtectedServiceServlet
		implements DashboardLogoutService {

	private static final long serialVersionUID = 2761027587192306870L;

	@Override
	public void logoutUser() {
		getServletContext().removeAttribute("JSESSIONID");
	}

}
