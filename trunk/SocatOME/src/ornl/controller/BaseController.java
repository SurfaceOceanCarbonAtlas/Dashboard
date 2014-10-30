/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class BaseController {

	/**
	* Returns the domain User object for the currently logged in user, or null
	* if no User is logged in.
	* 
	* @return User object for the currently logged in user, or null if no User
	*         is logged in.
	*/
	public static User getCurrentUser() {

	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (principal instanceof User){
	    	
	    	return (User)principal;
	    }

	    // principal object is either null or represents anonymous user -
	    // neither of which our domain User object can represent - so return null
	    return null;
	}


	/**
	 * Utility method to determine if the current user is logged in /
	 * authenticated.
	 * <p>
	 * Equivalent of calling:
	 * <p>
	 * <code>getCurrentUser() != null</code>
	 * 
	 * @return if user is logged in
	 */
	public static boolean isLoggedIn() {
	    return getCurrentUser() != null;
	}

}
