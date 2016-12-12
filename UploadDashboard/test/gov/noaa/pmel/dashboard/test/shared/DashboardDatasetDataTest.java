/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * @author Karl Smith
 */
public class DashboardDatasetDataTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getOwner()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setOwner(java.lang.String)}.
	 */
	@Test
	public void testSetGetOwner() {
		String myUsername = "SocatUser";
		DashboardDatasetData cruiseData = new DashboardDatasetData();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getOwner());
		cruiseData.setOwner(myUsername);
		assertEquals(myUsername, cruiseData.getOwner());
		cruiseData.setOwner(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getOwner());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getUploadFilename()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setUploadFilename(java.lang.String)}.
	 */
	@Test
	public void testSetGetFilename() {
		String myFilename = "agsk20031205_revised.tsv";
		DashboardDatasetData cruiseData = new DashboardDatasetData();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getUploadFilename());
		cruiseData.setUploadFilename(myFilename);
		assertEquals(myFilename, cruiseData.getUploadFilename());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getOwner());
		cruiseData.setUploadFilename(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getVersion()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setVersion(java.lang.String)}.
	 */
	@Test
	public void testSetGetVersion() {
		String myVersion = "3.0";
		DashboardDatasetData cruiseData = new DashboardDatasetData();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getVersion());
		cruiseData.setVersion(myVersion);
		assertEquals(myVersion, cruiseData.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getUploadFilename());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getOwner());
		cruiseData.setVersion(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getDatasetId()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setDatasetId(java.lang.String)}.
	 */
	@Test
	public void testSetGetExpocode() {
		String myExpocode = "AGSK20031205";
		DashboardDatasetData cruiseData = new DashboardDatasetData();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getDatasetId());
		cruiseData.setDatasetId(myExpocode);
		assertEquals(myExpocode, cruiseData.getDatasetId());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getUploadFilename());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getOwner());
		cruiseData.setDatasetId(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getDatasetId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetData#getRowNums()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetData#setRowNums(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetRowNums() {
		ArrayList<Integer> myRowNums = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 6));
		DashboardDatasetData cruiseData = new DashboardDatasetData();
		assertEquals(0, cruiseData.getRowNums().size());
		cruiseData.setRowNums(myRowNums);
		assertEquals(myRowNums, cruiseData.getRowNums());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getDatasetId());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getUploadFilename());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getOwner());
		cruiseData.setRowNums(null);
		assertEquals(0, cruiseData.getRowNums().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetData#getDataValues()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetData#setDataValues(java.util.ArrayList)}.
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

		DashboardDatasetData cruiseData = new DashboardDatasetData();
		assertEquals(0, cruiseData.getDataValues().size());
		cruiseData.setDataValues(dataValues);
		assertEquals(dataValues, cruiseData.getDataValues());
		assertEquals(0, cruiseData.getRowNums().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getDatasetId());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getUploadFilename());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseData.getOwner());
		cruiseData.setDataValues(null);
		assertEquals(0, cruiseData.getDataValues().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetData#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetData#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myOwner = "SocatUser";
		String myFilename = "agsk20031205_revised.tsv";
		String myVersion = "3.0";
		String myExpocode = "AGSK20031205";
		ArrayList<Integer> myRowNums = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 5, 6));
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

		DashboardDatasetData firstData = new DashboardDatasetData();
		assertFalse( firstData.equals(null) );
		assertFalse( firstData.equals(dataValues) );
		DashboardDatasetData secondData = new DashboardDatasetData();
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

		firstData.setDatasetId(myExpocode);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setDatasetId(myExpocode);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setRowNums(myRowNums);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setRowNums(myRowNums);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);

		firstData.setDataValues(dataValues);
		assertTrue( firstData.hashCode() != secondData.hashCode() );
		assertFalse( firstData.equals(secondData) );
		secondData.setDataValues(dataValues);
		assertEquals(firstData.hashCode(), secondData.hashCode());
		assertEquals(firstData, secondData);
	}
}
