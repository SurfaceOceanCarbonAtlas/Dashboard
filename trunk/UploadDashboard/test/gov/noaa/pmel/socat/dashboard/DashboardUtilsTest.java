/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class DashboardUtilsTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#passhashFromPlainText()}.
	 */
	@Test
	public void testPasshashFromPlainText() {
		String passhash = DashboardUtils.passhashFromPlainText("username", "password");
		assertTrue( passhash.length() >= 32 );

		String otherhash = DashboardUtils.passhashFromPlainText("username", "otherpass");
		assertTrue( otherhash.length() >= 32 );
		assertFalse( passhash.equals(otherhash) );

		otherhash = DashboardUtils.passhashFromPlainText("otheruser", "password");
		assertTrue( otherhash.length() >= 32 );
		assertFalse( passhash.equals(otherhash) );

		passhash = DashboardUtils.passhashFromPlainText("me", "password");
		assertEquals(0, passhash.length());

		passhash = DashboardUtils.passhashFromPlainText("username", "pass");
		assertEquals(0, passhash.length());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#decodeByteArray(java.lang.String)}.
	 */
	@Test
	public void testDecodeByteArray() {
		String expectedStr = "[ 3, 6, -9, 15, 0, 12 ]";
		byte[] expectedArray = new byte[] {3, 6, -9, 15, 0, 12};
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

		boolean exceptionCaught = false;
		try {
			byteArray = DashboardUtils.decodeByteArray("3, 6, -9, 15, 0, 12");
		} catch ( IllegalArgumentException ex ) {
			exceptionCaught = true;
		}
		assertTrue( exceptionCaught );

		exceptionCaught = false;
		try {
			byteArray = DashboardUtils.decodeByteArray("[ 3, 6, -9, 1115, 0, 12 ]");
		} catch ( IllegalArgumentException ex ) {
			exceptionCaught = true;
		}
		assertTrue( exceptionCaught );
	}

}
