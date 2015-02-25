/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.util.HashSet;

/**
 * User authentication and privileges
 * 
 * @author Karl Smith
 */
public class DashboardUserInfo {

	private static final String MEMBER_NAME_TAG = "MemberOf";
	private static final String MANAGER_NAME_TAG = "ManagerOf";
	private static final String ADMIN_NAME_TAG = "Admin";

	private String username;
	// List of group numbers this user belongs to
	private HashSet<Integer> memberNums;
	// List of group numbers this user manages
	private HashSet<Integer> managerNums;
	// Is this user an admin?
	private boolean admin;

	/**
	 * Creates a user with no group memberships; thus,
	 * can only work with cruises this user uploaded.
	 * 
	 * @param username
	 * 		username to use
	 * @throws IllegalArgumentException
	 * 		if username is invalid (null or too short)
	 */
	public DashboardUserInfo(String username) 
			throws IllegalArgumentException {
		if ( (username == null) || (username.trim().length() < 4) )
			throw new IllegalArgumentException("User name too short");
		this.username = username;
		memberNums = new HashSet<Integer>();
		managerNums = new HashSet<Integer>();
		admin = false;
	}

	/**
	 * Add privileges to this user from the roles specified
	 * @param rolesString
	 * 		comma/semicolon/space separated list of user roles
	 * @throws IllegalArgumentException
	 * 		if a role cannot be interpreted
	 */
	public void addUserRoles(String rolesString) throws IllegalArgumentException {
		String[] roles = rolesString.split("[,;\\s]+", -1);
		for (int k = 0; k < roles.length; k++) {
			if ( (roles[k]).startsWith(MEMBER_NAME_TAG) ) {
				int groupNum;
				try {
					groupNum = Integer.parseInt(
						roles[k].substring(MEMBER_NAME_TAG.length()) );
					if ( groupNum <= 0 )
						throw new NumberFormatException();
				} catch ( NumberFormatException ex ) {
					throw new IllegalArgumentException("Invalid " + 
							MEMBER_NAME_TAG + " number in " + roles[k]);
				}
				memberNums.add(groupNum);
			}
			else if ( (roles[k]).startsWith(MANAGER_NAME_TAG) ) {
				int groupNum;
				try {
					groupNum = Integer.parseInt(
						roles[k].substring(MANAGER_NAME_TAG.length()) );
					if ( groupNum <= 0 )
						throw new NumberFormatException();
				} catch ( NumberFormatException ex ) {
					throw new IllegalArgumentException("Invalid " + 
							MANAGER_NAME_TAG + " number in " + roles[k]);
				}
				memberNums.add(groupNum);
				managerNums.add(groupNum);
			}
			else if ( (roles[k]).equals(ADMIN_NAME_TAG) ) {
				admin = true;
			}
			else if ( ! (roles[k]).isEmpty() ) {
				throw new IllegalArgumentException("Unknown role " + roles[k]);
			}
		}
	}

	/**
	 * @return
	 * 		true is this user is an admin or a manager of a group
	 * 		(regardless of whether there is anyone else in the group)
	 */
	public boolean isManager() {
		if ( admin || (managerNums.size() > 0) )
			return true;
		return false;
	}

	/**
	 * @return
	 * 		true is this user is an admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * Determines if this user has manager privilege over a user. 
	 * This can be from this user being an administrator, a manager
	 * of a group the other user belongs to, or actually being the
	 * same user (same username) as the other user.
	 * 
	 * @param other
	 * 		the other user info; can be null
	 * @return
	 * 		true if this user has manager privilege over the other user;
	 * 		if other is null, returns true.
	 */
	public boolean managesOver(DashboardUserInfo other) {
		// Admin manages over everyone
		if ( admin )
			return true;
		if ( other == null )
			return true;
		// User manages over himself
		if ( username.equals(other.username) )
			return true;
		// Check groups this user manages
		for ( Integer groupNum : managerNums )
			if ( other.memberNums.contains(groupNum) )
				return true;
		// Not a manager over other
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Boolean.valueOf(admin).hashCode();
		result = result * prime + username.hashCode();
		result = result * prime + memberNums.hashCode();
		result = result * prime + managerNums.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DashboardUserInfo) )
			return false;
		DashboardUserInfo other = (DashboardUserInfo) obj;

		if ( admin != other.admin )
			return false;
		if ( ! username.equals(other.username) )
			return false;
		if ( ! managerNums.equals(other.managerNums) )
			return false;
		if ( ! memberNums.equals(other.memberNums) )
			return false;

		return true;
	}

}
