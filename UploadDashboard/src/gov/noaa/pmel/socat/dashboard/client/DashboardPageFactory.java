/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Composite;
// used when debugging from Eclipse


/**
 * Returns singleton instances of the various pages displayed 
 * by the dashboard so pages do not have to be completely 
 * rebuilt each time they are to be displayed.
 * 
 * @author Karl Smith
 */
public class DashboardPageFactory {

	private static HashMap<Class<?>,Composite> pagesMap = null;
	private static String username;
	private static String passhash;

	/**
	 * Do not create an instance of this factory.
	 * Use the static method {@link #getPage(Class)} to obtain desired page.
	 */
	private DashboardPageFactory() {
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
	 * 		the previously set password hash to use when communication with the server
	 */
	static String getPasshash() {
		return passhash;
	}

	/**
	 * @param passhash
	 * 		The password hash to use when communicating with the server
	 */
	static void setPasshash(String passhash) {
		DashboardPageFactory.passhash = passhash;
	}

	/**
	 * Remove all authentication tokens held in this class
	 */
	static void clearAuthentication() {
		username = null;
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
