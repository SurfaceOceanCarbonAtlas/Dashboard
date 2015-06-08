/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class DashboardCruiseWithDataTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData#DashboardCruiseWithData()}.
	 */
	@Test
	public void testDashboardCruiseWithData() {
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertNotNull( cruiseData );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getOwner()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setOwner(java.lang.String)}.
	 */
	@Test
	public void testSetGetOwner() {
		String myUsername = "SocatUser";
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals("", cruiseData.getOwner());
		cruiseData.setOwner(myUsername);
		assertEquals(myUsername, cruiseData.getOwner());
		cruiseData.setOwner(null);
		assertEquals("", cruiseData.getOwner());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getUploadFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setUploadFilename(java.lang.String)}.
	 */
	@Test
	public void testSetGetFilename() {
		String myFilename = "agsk20031205_revised.tsv";
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals("", cruiseData.getUploadFilename());
		cruiseData.setUploadFilename(myFilename);
		assertEquals(myFilename, cruiseData.getUploadFilename());
		assertEquals("", cruiseData.getOwner());
		cruiseData.setUploadFilename(null);
		assertEquals("", cruiseData.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getVersion()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setVersion(java.lang.String)}.
	 */
	@Test
	public void testSetGetVersion() {
		String myVersion = "3.0";
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals("", cruiseData.getVersion());
		cruiseData.setVersion(myVersion);
		assertEquals(myVersion, cruiseData.getVersion());
		assertEquals("", cruiseData.getUploadFilename());
		assertEquals("", cruiseData.getOwner());
		cruiseData.setVersion(null);
		assertEquals("", cruiseData.getVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getExpocode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testSetGetExpocode() {
		String myExpocode = "AGSK20031205";
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals("", cruiseData.getExpocode());
		cruiseData.setExpocode(myExpocode);
		assertEquals(myExpocode, cruiseData.getExpocode());
		assertEquals("", cruiseData.getVersion());
		assertEquals("", cruiseData.getUploadFilename());
		assertEquals("", cruiseData.getOwner());
		cruiseData.setExpocode(null);
		assertEquals("", cruiseData.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData#getPreamble()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData#setPreamble(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetPreamble() {
		ArrayList<String> myPreamble = new ArrayList<String>(Arrays.asList(
				"Cruise Expocode: AGSK20031205",
				"Cruise Name: SKO313",
				"Ship/Vessel Name: Skogafoss",
				"Principal Investigator(s): Rik Wanninkhof"
		));
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals(0, cruiseData.getPreamble().size());
		cruiseData.setPreamble(myPreamble);
		assertEquals(myPreamble, cruiseData.getPreamble());
		assertEquals("", cruiseData.getExpocode());
		assertEquals("", cruiseData.getVersion());
		assertEquals("", cruiseData.getUploadFilename());
		assertEquals("", cruiseData.getOwner());
		cruiseData.setPreamble(null);
		assertEquals(0, cruiseData.getPreamble().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData#getDataValues()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData#setDataValues(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetDataValues() {
		String[][] observations = {
				{ "2003-12-05 22:12", "337.28101", "64.10700", "26.910", 
					"5.410", "5.700", null, "1026.500", "373.740" },
				{ "2003-12-05 22:18", "337.23901", "64.09700", "28.360", 
					"5.390", "5.680", null, "1026.100", "374.390" },
				{ "2003-12-05 22:24", "337.20499", "64.08300", "28.700", 
					"5.440", "5.730", null, "1026.100", "374.510" },
				{ "2003-12-05 22:30", "337.17499", "64.06900", "28.690", 
					"5.630", "5.920", null, "1025.800", "372.710" },
				{ "2003-12-05 22:36", "337.14499", "64.05500", "28.750", 
					"5.710", "6.000", null, "1025.900", "370.480" }
		};
		ArrayList<ArrayList<String>> dataValues = 
				new ArrayList<ArrayList<String>>(observations.length);
		for (int k = 0; k < observations.length; k++)
			dataValues.add(new ArrayList<String>(Arrays.asList(observations[k])));

		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals(0, cruiseData.getDataValues().size());
		cruiseData.setDataValues(dataValues);
		assertEquals(dataValues, cruiseData.getDataValues());
		assertEquals(0, cruiseData.getPreamble().size());
		assertEquals("", cruiseData.getExpocode());
		assertEquals("", cruiseData.getVersion());
		assertEquals("", cruiseData.getUploadFilename());
		assertEquals("", cruiseData.getOwner());
		cruiseData.setDataValues(null);
		assertEquals(0, cruiseData.getDataValues().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getWoceThreeRowIndices()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setWoceThreeRowIndices(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetWoceThreeRowIndices() {
		Integer[][] woceThreeIndices = {
				{ 2, 6 },
				{ },
				{ 6 },
				{ 3, 0, 6 },
				{ }
		};
		ArrayList<HashSet<Integer>> woceThreeRowSets = 
				new ArrayList<HashSet<Integer>>(woceThreeIndices.length);
		for (int k = 0; k < woceThreeIndices.length; k++)
			woceThreeRowSets.add(new HashSet<Integer>(Arrays.asList(woceThreeIndices[k])));

		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals(0, cruiseData.getWoceThreeRowIndices().size());
		cruiseData.setWoceThreeRowIndices(woceThreeRowSets);
		assertEquals(woceThreeRowSets, cruiseData.getWoceThreeRowIndices());
		assertEquals(0, cruiseData.getDataValues().size());
		assertEquals(0, cruiseData.getPreamble().size());
		assertEquals("", cruiseData.getExpocode());
		assertEquals("", cruiseData.getVersion());
		assertEquals("", cruiseData.getUploadFilename());
		assertEquals("", cruiseData.getOwner());
		cruiseData.setWoceThreeRowIndices(null);
		assertEquals(0, cruiseData.getWoceThreeRowIndices().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getWoceFourRowIndices()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setWoceFourRowIndices(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetWoceFourRowIndices() {
		Integer[][] woceFourIndices = {
				{ },
				{ 1, 4 },
				{ 5, 2, 8 },
				{ 3 },
				{ }
		};
		ArrayList<HashSet<Integer>> woceFourRowSets = 
				new ArrayList<HashSet<Integer>>(woceFourIndices.length);
		for (int k = 0; k < woceFourIndices.length; k++)
			woceFourRowSets.add(new HashSet<Integer>(Arrays.asList(woceFourIndices[k])));

		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		assertEquals(0, cruiseData.getWoceFourRowIndices().size());
		cruiseData.setWoceFourRowIndices(woceFourRowSets);
		assertEquals(woceFourRowSets, cruiseData.getWoceFourRowIndices());
		assertEquals(0, cruiseData.getWoceThreeRowIndices().size());
		assertEquals(0, cruiseData.getDataValues().size());
		assertEquals(0, cruiseData.getPreamble().size());
		assertEquals("", cruiseData.getExpocode());
		assertEquals("", cruiseData.getVersion());
		assertEquals("", cruiseData.getUploadFilename());
		assertEquals("", cruiseData.getOwner());
		cruiseData.setWoceFourRowIndices(null);
		assertEquals(0, cruiseData.getWoceFourRowIndices().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myOwner = "SocatUser";
		String myFilename = "agsk20031205_revised.tsv";
		String myVersion = "3.0";
		String myExpocode = "AGSK20031205";
		ArrayList<String> myPreamble = new ArrayList<String>(Arrays.asList(
				"Cruise Expocode: AGSK20031205",
				"Cruise Name: SKO313",
				"Ship/Vessel Name: Skogafoss",
				"Principal Investigator(s): Rik Wanninkhof"
		));
		String[][] observations = {
				{ "2003-12-05 22:12", "337.28101", "64.10700", "26.910", 
					"5.410", "5.700", null, "1026.500", "373.740" },
				{ "2003-12-05 22:18", "337.23901", "64.09700", "28.360", 
					"5.390", "5.680", null, "1026.100", "374.390" },
				{ "2003-12-05 22:24", "337.20499", "64.08300", "28.700", 
					"5.440", "5.730", null, "1026.100", "374.510" },
				{ "2003-12-05 22:30", "337.17499", "64.06900", "28.690", 
					"5.630", "5.920", null, "1025.800", "372.710" },
				{ "2003-12-05 22:36", "337.14499", "64.05500", "28.750", 
					"5.710", "6.000", null, "1025.900", "370.480" }
		};
		ArrayList<ArrayList<String>> dataValues = 
				new ArrayList<ArrayList<String>>(observations.length);
		for (int k = 0; k < observations.length; k++)
			dataValues.add(new ArrayList<String>(Arrays.asList(observations[k])));
		Integer[][] woceThreeIndices = {
				{ 2, 6 },
				{ },
				{ 6 },
				{ 3, 0, 6 },
				{ }
		};
		ArrayList<HashSet<Integer>> woceThreeRowSets = 
				new ArrayList<HashSet<Integer>>(woceThreeIndices.length);
		for (int k = 0; k < woceThreeIndices.length; k++)
			woceThreeRowSets.add(new HashSet<Integer>(Arrays.asList(woceThreeIndices[k])));
		Integer[][] woceFourIndices = {
				{ },
				{ 1, 4 },
				{ 5, 2, 8 },
				{ 3 },
				{ }
		};
		ArrayList<HashSet<Integer>> woceFourRowSets = 
				new ArrayList<HashSet<Integer>>(woceFourIndices.length);
		for (int k = 0; k < woceFourIndices.length; k++)
			woceFourRowSets.add(new HashSet<Integer>(Arrays.asList(woceFourIndices[k])));

		DashboardCruiseWithData firstData = new DashboardCruiseWithData();
		assertFalse( firstData.equals(null) );
		assertFalse( firstData.equals(myPreamble) );
		DashboardCruiseWithData secondData = new DashboardCruiseWithData();
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setOwner(myOwner);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setOwner(myOwner);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setUploadFilename(myFilename);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setUploadFilename(myFilename);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setVersion(myVersion);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setVersion(myVersion);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setExpocode(myExpocode);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setExpocode(myExpocode);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setPreamble(myPreamble);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setPreamble(myPreamble);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setDataValues(dataValues);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setDataValues(dataValues);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setWoceThreeRowIndices(woceThreeRowSets);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setWoceThreeRowIndices(woceThreeRowSets);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setWoceFourRowIndices(woceFourRowSets);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setWoceFourRowIndices(woceFourRowSets);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);
	}
}
