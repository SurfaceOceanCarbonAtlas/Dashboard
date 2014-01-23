/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

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
		byte[] expectedArray = {3, 6, -9, 15, 0, 12};
		String oneStr = "[ 8 ]";
		byte[] oneArray = { 8 };
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

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#decodeIntegerArrayList(java.lang.String)}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#encodeIntegerArrayList(java.util.ArrayList)}.
	 */
	@Test
	public void testEncodeDecodeIntegerArrayList() {
		ArrayList<Integer> myList = new ArrayList<Integer>(
				Arrays.asList(234, 4563, 90312, -2349));
		String encoded = DashboardUtils.encodeIntegerArrayList(myList);
		ArrayList<Integer> decodedList = 
				DashboardUtils.decodeIntegerArrayList(encoded);
		assertEquals(myList, decodedList);
		decodedList = DashboardUtils.decodeIntegerArrayList("[ ]");
		assertEquals(new ArrayList<Integer>(), decodedList);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#decodeStringArrayList(java.lang.String)}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#encodeStringArrayList(java.util.ArrayList)}.
	 */
	@Test
	public void testEncodeDecodeStringArrayList() {
		ArrayList<String> myList = new ArrayList<String>(
				Arrays.asList("one", "two", "", "four, five, and six", "", ""));
		String encoded = DashboardUtils.encodeStringArrayList(myList);
		ArrayList<String> decodedList = 
				DashboardUtils.decodeStringArrayList(encoded);
		assertEquals(myList, decodedList);
		decodedList = DashboardUtils.decodeStringArrayList("[ ]");
		assertEquals(new ArrayList<String>(), decodedList);
		decodedList = DashboardUtils.decodeStringArrayList("[ \"\" ]");
		assertEquals(new ArrayList<String>(Arrays.asList("")), decodedList);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#decodeSetsArrayList(java.lang.String)}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardUtils#encodeSetsArrayList(java.util.ArrayList)}.
	 */
	@Test
	public void testEncodeDecodeSetsArrayList() {
		ArrayList<HashSet<Integer>> myList = new ArrayList<HashSet<Integer>>();
		myList.add(new HashSet<Integer>(Arrays.asList(1,5,9,12)));
		myList.add(new HashSet<Integer>(Arrays.asList(6,9,12,21)));
		String encoded = DashboardUtils.encodeSetsArrayList(myList);
		ArrayList<HashSet<Integer>> decodedList = 
				DashboardUtils.decodeSetsArrayList(encoded);
		assertEquals(myList, decodedList);
		decodedList = DashboardUtils.decodeSetsArrayList("[ ]");
		assertEquals(new ArrayList<HashSet<Integer>>(), decodedList);
		myList = new ArrayList<HashSet<Integer>>();
		myList.add(new HashSet<Integer>());
		decodedList = DashboardUtils.decodeSetsArrayList("[ [ ] ]");
		assertEquals(myList, decodedList);
	}

}
