/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import java.util.HashMap;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.ui.Composite;


/**
 * Returns singleton instances of the various pages displayed 
 * by the dashboard so pages do not have to be completely 
 * rebuilt each time they are to be displayed.
 * 
 * @author Karl Smith
 */
public class DashboardPageFactory {

	private static HashMap<Class<?>,Composite> pagesMap = null;
	private static XsrfToken token;
	private static String username;
	private static String userhash;
	private static String passhash;

	/**
	 * Do not create an instance of this factory.
	 * Use the static method {@link #getPage(Class)} to obtain desired page.
	 */
	private DashboardPageFactory() {
	}

	/**
	 * @return 
	 * 		the previously set XSRF token to use when communicating with the server
	 */
	static XsrfToken getToken() {
		return token;
	}

	/**
	 * @param token 
	 * 		the XSRF token to use when communicating with the server
	 */
	static void setToken(XsrfToken token) {
		DashboardPageFactory.token = token;
	}

	/**
	 * @return 
	 * 		the previously set username to use when communicating with the server
	 */
	static String getUsername() {
		return username;
	}

	/**
	 * @param token 
	 * 		the username to use when communicating with the server
	 */
	static void setUsername(String username) {
		DashboardPageFactory.username = username;
	}

	/**
	 * @return
	 * 		the previously set userhash to use when communication with the server
	 */
	static String getUserhash() {
		return userhash;
	}

	/**
	 * @return
	 * 		the previously set passhash to use when communication with the server
	 */
	static String getPasshash() {
		return passhash;
	}

	/**
	 * @param hashes
	 * 		The userhash and passhash to use when communicating with the server
	 */
	static void setHashes(String[] hashes) {
		userhash = hashes[0];
		passhash = hashes[1];
	}

	/**
	 * Remove all authentication tokens held in this class
	 */
	static void clearAuthentication() {
		token = null;
		username = null;
		userhash = null;
		passhash = null;
	}

	/**
	 * Return a singleton instance of the desired page for display 
	 * by the dashboard.  This page will need to be updated with 
	 * the appropriate data from the server prior to display.
	 * 
	 * @param clazz
	 * 		class of the desired page
	 * @return
	 * 		uninitialized page, or null if not known
	 */
	@SuppressWarnings("unchecked")
	static <T extends Composite> T getPage(Class<T> clazz) {
		/* 
		// Start of code only for running directly from eclipse
		if ( clazz == DashboardLogin.class ) {
			if ( Cookies.getCookie("JSESSIONID") == null ) {
				Cookies.setCookie("JSESSIONID", Double.toString(Math.random()));
			}
		}
		// End of code only for running directly from eclipse
		 */

		// When first called, create a hash map with just the login and logout pages
		if ( pagesMap == null ) {
			pagesMap = new HashMap<Class<?>,Composite>();
			pagesMap.put(DashboardLogin.class, new DashboardLogin());
			pagesMap.put(DashboardLogout.class, new DashboardLogout());
		}

		// Check if the page already exists
		T page = (T) pagesMap.get(clazz);
		if ( page == null ) {
			// No page; create and save it if known
			if ( clazz == DashboardCruiseListPage.class ) {
				page = (T) new DashboardCruiseListPage();
			}
			else if ( clazz == DashboardCruiseUploadPage.class ) {
				page = (T) new DashboardCruiseUploadPage();
			}
			else {
				throw new RuntimeException("Unknown page class: " + clazz);
			}
			pagesMap.put(clazz, page);
		}
		return page;
	}

}
