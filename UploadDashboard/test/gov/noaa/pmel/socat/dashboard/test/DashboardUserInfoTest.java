/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.*;
import gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class DashboardUserInfoTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo#DashboardUserInfo(java.lang.String, java.lang.String)}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo#getAuthorizationHash()}.
	 */
	@Test
	public void testDashboardUserInfoGetAuthorizationHash() {
		String username = "socatuser";
		String authHash = "0123456789ABCDEF0123456789ABCDEF";

		DashboardUserInfo userInfo = new DashboardUserInfo(username, authHash);
		assertEquals(authHash, userInfo.getAuthorizationHash());

		boolean errMissed = false;
		try {
			userInfo = new DashboardUserInfo("me", authHash);
			errMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Expected result
			;
		}
		assertFalse(errMissed);

		try {
			userInfo = new DashboardUserInfo(username, "0123456789ABCDEF");
			errMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Expected result
			;
		}
		assertFalse(errMissed);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo#addUserRoles(java.lang.String)}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo#managesOver(gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo)}.
	 */
	@Test
	public void testAddUserRolesManagesOver() {
		String firstuser = "firstuser";
		String seconduser = "seconduser";
		String authHash = "0123456789ABCDEF0123456789ABCDEF";

		DashboardUserInfo firstInfo = new DashboardUserInfo(firstuser, authHash);
		assertTrue(firstInfo.managesOver(firstInfo));
		firstInfo.addUserRoles("MemberOf1,MemberOf2");
		DashboardUserInfo secondInfo = new DashboardUserInfo(seconduser, authHash);
		assertFalse(secondInfo.managesOver(firstInfo));
		secondInfo.addUserRoles("MemberOf1 ,,\t;; MemberOf2");
		assertFalse(secondInfo.managesOver(firstInfo));
		secondInfo.addUserRoles("ManagerOf1");
		assertTrue(secondInfo.managesOver(firstInfo));

		secondInfo = new DashboardUserInfo(seconduser, authHash);
		assertFalse(secondInfo.managesOver(firstInfo));
		secondInfo.addUserRoles("ManagerOf1");
		assertTrue(secondInfo.managesOver(firstInfo));

		firstInfo = new DashboardUserInfo(firstuser, authHash);
		assertFalse(firstInfo.managesOver(secondInfo));
		assertFalse(secondInfo.managesOver(firstInfo));
		firstInfo.addUserRoles("ManagerOf1");
		assertTrue(firstInfo.managesOver(secondInfo));
		assertTrue(secondInfo.managesOver(firstInfo));

		secondInfo = new DashboardUserInfo(seconduser, authHash);
		assertFalse(secondInfo.managesOver(firstInfo));
		secondInfo.addUserRoles("Admin");
		assertTrue(secondInfo.managesOver(firstInfo));

		boolean errMissed = false;
		try {
			firstInfo.addUserRoles("MemberOf");
			errMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Expected result
			;
		}
		assertFalse(errMissed);

		try {
			firstInfo.addUserRoles("ManagerOf");
			errMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Expected result
			;
		}
		assertFalse(errMissed);
		
		try {
			firstInfo.addUserRoles("Deity");
			errMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Expected result
			;
		}
		assertFalse(errMissed);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo#equals(java.lang.Object)}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo#hashCode()}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		String username = "socatuser";
		String authHash = "0123456789ABCDEF0123456789ABCDEF";
		String userRole = "MemberOf1";
		String managerRole = "ManagerOf1";
		String adminRole = "Admin";

		DashboardUserInfo userInfo = new DashboardUserInfo(username, authHash);
		assertFalse( userInfo.equals(null) );
		assertFalse( userInfo.equals(username) );
		DashboardUserInfo otherInfo = new DashboardUserInfo(username, authHash);
		assertEquals(userInfo.hashCode(), otherInfo.hashCode());
		assertEquals(userInfo, otherInfo);

		userInfo.addUserRoles(userRole);
		assertFalse( userInfo.hashCode() == otherInfo.hashCode() );
		assertFalse( userInfo.equals(otherInfo) );
		otherInfo.addUserRoles(userRole);
		assertEquals(userInfo.hashCode(), otherInfo.hashCode());
		assertEquals(userInfo, otherInfo);

		userInfo.addUserRoles(userRole);
		assertEquals(userInfo.hashCode(), otherInfo.hashCode());
		assertEquals(userInfo, otherInfo);

		userInfo.addUserRoles(managerRole);
		assertFalse( userInfo.hashCode() == otherInfo.hashCode() );
		assertFalse( userInfo.equals(otherInfo) );
		otherInfo.addUserRoles(managerRole);
		assertEquals(userInfo.hashCode(), otherInfo.hashCode());
		assertEquals(userInfo, otherInfo);

		userInfo.addUserRoles(adminRole);
		assertFalse( userInfo.hashCode() == otherInfo.hashCode() );
		assertFalse( userInfo.equals(otherInfo) );
		otherInfo.addUserRoles(adminRole);
		assertEquals(userInfo.hashCode(), otherInfo.hashCode());
		assertEquals(userInfo, otherInfo);

		userInfo = new DashboardUserInfo(username, authHash);
		userInfo.addUserRoles(userRole);
		userInfo.addUserRoles(managerRole);
		otherInfo = new DashboardUserInfo(username, authHash);
		otherInfo.addUserRoles(managerRole);
		assertEquals(userInfo.hashCode(), otherInfo.hashCode());
		assertEquals(userInfo, otherInfo);
	}

}
