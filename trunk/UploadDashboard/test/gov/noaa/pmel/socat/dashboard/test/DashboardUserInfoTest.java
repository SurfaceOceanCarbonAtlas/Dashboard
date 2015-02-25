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
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserInfo#DashboardUserInfo(java.lang.String)}
	 */
	@Test
	public void testDashboardUserInfoGetAuthorizationHash() {
		String goodUsername = "socatuser";
		String badUsername = "me";

		DashboardUserInfo userInfo = new DashboardUserInfo(goodUsername);
		assertNotNull(userInfo);

		boolean errMissed = false;
		try {
			userInfo = new DashboardUserInfo(badUsername);
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

		DashboardUserInfo firstInfo = new DashboardUserInfo(firstuser);
		assertTrue(firstInfo.managesOver(firstInfo));
		firstInfo.addUserRoles("MemberOf1,MemberOf2");
		DashboardUserInfo secondInfo = new DashboardUserInfo(seconduser);
		assertFalse(secondInfo.managesOver(firstInfo));
		secondInfo.addUserRoles("MemberOf1 ,,\t;; MemberOf2");
		assertFalse(secondInfo.managesOver(firstInfo));
		secondInfo.addUserRoles("ManagerOf1");
		assertTrue(secondInfo.managesOver(firstInfo));

		secondInfo = new DashboardUserInfo(seconduser);
		assertFalse(secondInfo.managesOver(firstInfo));
		secondInfo.addUserRoles("ManagerOf1");
		assertTrue(secondInfo.managesOver(firstInfo));

		firstInfo = new DashboardUserInfo(firstuser);
		assertFalse(firstInfo.managesOver(secondInfo));
		assertFalse(secondInfo.managesOver(firstInfo));
		firstInfo.addUserRoles("ManagerOf1");
		assertTrue(firstInfo.managesOver(secondInfo));
		assertTrue(secondInfo.managesOver(firstInfo));

		secondInfo = new DashboardUserInfo(seconduser);
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
		String userRole = "MemberOf1";
		String managerRole = "ManagerOf1";
		String adminRole = "Admin";

		DashboardUserInfo userInfo = new DashboardUserInfo(username);
		assertFalse( userInfo.equals(null) );
		assertFalse( userInfo.equals(username) );
		DashboardUserInfo otherInfo = new DashboardUserInfo(username);
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

		userInfo = new DashboardUserInfo(username);
		userInfo.addUserRoles(userRole);
		userInfo.addUserRoles(managerRole);
		otherInfo = new DashboardUserInfo(username);
		otherInfo.addUserRoles(managerRole);
		assertEquals(userInfo.hashCode(), otherInfo.hashCode());
		assertEquals(userInfo, otherInfo);
	}

}
