package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import org.junit.Test;

public class DashboardUtilsTest {

	@Test
	public void testHashesFromPlainText() {
		String passhash = DashboardUtils.passhashFromPlainText("username", "password");
		assertNotNull( passhash );
		assertTrue( passhash.length() >= 32 );

		String otherhash = DashboardUtils.passhashFromPlainText("username", "otherpass");
		assertNotNull( otherhash );
		assertTrue( otherhash.length() >= 32 );
		assertFalse( passhash.equals(otherhash) );

		otherhash = DashboardUtils.passhashFromPlainText("otheruser", "password");
		assertNotNull( otherhash );
		assertTrue( otherhash.length() >= 32 );
		assertFalse( passhash.equals(otherhash) );

		passhash = DashboardUtils.passhashFromPlainText("me", "password");
		assertNotNull( passhash );
		assertEquals(0, passhash.length());

		passhash = DashboardUtils.passhashFromPlainText("username", "pass");
		assertNotNull( passhash );
		assertEquals(0, passhash.length());
	}

	@Test
	public void testDecodeByteArray() {
		String expectedStr = "[ 3, 6, 9, 15, 0, 12 ]";
		byte[] expectedArray = new byte[] {3, 6, 9, 15, 0, 12};
		String oneStr = "[ 8 ]";
		byte[] oneArray = new byte[] { 8 };
		String emptyStr = "[  ]";
		byte[] emptyArray = new byte[0];

		byte[] byteArray = DashboardUtils.decodeByteArray(expectedStr);
		assertArrayEquals(expectedArray, byteArray);

		byteArray = DashboardUtils.decodeByteArray(oneStr);
		assertArrayEquals(oneArray, byteArray);

		byteArray = DashboardUtils.decodeByteArray(emptyStr);
		assertArrayEquals(emptyArray, byteArray);
	}

}
