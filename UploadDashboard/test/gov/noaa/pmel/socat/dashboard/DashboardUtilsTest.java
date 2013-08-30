package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import org.junit.Test;

public class DashboardUtilsTest {

	@Test
	public void testHashesFromPlainText() {
		String[] hashes = DashboardUtils.hashesFromPlainText("username", "password");
		assertEquals(2, hashes.length);
		assertNotNull( hashes[0] );
		assertTrue( hashes[0].length() >= 16 );
		assertNotNull( hashes[1] );
		assertTrue( hashes[1].length() >= 16 );

		hashes = DashboardUtils.hashesFromPlainText("too", "short");
		assertEquals(2, hashes.length);
		assertNotNull( hashes[0] );
		assertEquals(0, hashes[0].length());
		assertNotNull( hashes[1] );
		assertEquals(0, hashes[1].length());
	}

	@Test
	public void testEncodeDecodeByteArray() {
		byte[] expectedArray = new byte[] {3, 6, 9, 15, 0, 12};
		String expectedStr = "[ 3, 6, 9, 15, 0, 12 ]";
		byte[] oneArray = new byte[] { 8 };
		String oneStr = "[ 8 ]";
		byte[] emptyArray = new byte[0];
		String emptyStr = "[  ]";

		String byteStr = DashboardUtils.encodeByteArray(expectedArray);
		assertEquals(expectedStr, byteStr);
		byte[] byteArray = DashboardUtils.decodeByteArray(expectedStr);
		assertArrayEquals(expectedArray, byteArray);

		byteStr = DashboardUtils.encodeByteArray(oneArray);
		assertEquals(oneStr, byteStr);
		byteArray = DashboardUtils.decodeByteArray(oneStr);
		assertArrayEquals(oneArray, byteArray);

		byteStr = DashboardUtils.encodeByteArray(emptyArray);
		assertEquals(emptyStr, byteStr);
		byteArray = DashboardUtils.decodeByteArray(emptyStr);
		assertArrayEquals(emptyArray, byteArray);
	}

	@Test
	public void testEncodeDecodeStrStrMap() {
		LinkedHashMap<String,String> expectedMap = new LinkedHashMap<String,String>();
		expectedMap.put("first \\\"of\\\" five", "one");
		expectedMap.put("second", "two");
		expectedMap.put("third", "three");
		expectedMap.put("fourth", "four");
		expectedMap.put("fifth", "five");
		String expectedStr = "{ \"first \\\"of\\\" five\":\"one\", \"second\":\"two\", " +
				"\"third\":\"three\", \"fourth\":\"four\", \"fifth\":\"five\" }";
		LinkedHashMap<String,String> oneMap = new LinkedHashMap<String,String>();
		oneMap.put("first \\\"and\\\" only", "one");
		String oneStr = "{ \"first \\\"and\\\" only\":\"one\" }";
		LinkedHashMap<String,String> emptyMap = new LinkedHashMap<String,String>();
		String emptyStr = "{  }";

		String mapStr = DashboardUtils.encodeStrStrMap(expectedMap);
		assertEquals(expectedStr, mapStr);
		Map<String,String> mapping = DashboardUtils.decodeStrStrMap(expectedStr);
		assertEquals(expectedMap, mapping);

		mapStr = DashboardUtils.encodeStrStrMap(oneMap);
		assertEquals(oneStr, mapStr);
		mapping = DashboardUtils.decodeStrStrMap(oneStr);
		assertEquals(oneMap, mapping);

		mapStr = DashboardUtils.encodeStrStrMap(emptyMap);
		assertEquals(emptyStr, mapStr);
		mapping = DashboardUtils.decodeStrStrMap(emptyStr);
		assertEquals(emptyMap, mapping);
	}

}
