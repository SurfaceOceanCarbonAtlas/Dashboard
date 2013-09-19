/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * Test methods for CruiseDataColumnSpecs
 * 
 * @author Karl Smith
 */
public class CruiseDataColumnSpecsTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#getExpocode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testSetGetExpocode() {
		String myExpocode = "AGSK20031205";
		CruiseDataColumnSpecs specs = new CruiseDataColumnSpecs();
		assertEquals("", specs.getExpocode());
		specs.setExpocode(myExpocode);
		assertEquals(myExpocode, specs.getExpocode());
		specs.setExpocode(null);
		assertEquals("", specs.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#getNumRowsTotal()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#setNumRowsTotal(int)}.
	 */
	@Test
	public void testSetGetNumRowsTotal() {
		int numRows = 235;
		CruiseDataColumnSpecs specs = new CruiseDataColumnSpecs();
		assertEquals(0, specs.getNumRowsTotal());
		specs.setNumRowsTotal(numRows);
		assertEquals(numRows, specs.getNumRowsTotal());
		assertEquals("", specs.getExpocode());
		boolean missedError = false;
		try {
			specs.setNumRowsTotal(-5);
			missedError = true;
		} catch (IllegalArgumentException ex) {
			// expected result
			;
		}
		if ( missedError )
			fail("negative NumRowsTotal did not throw an exception");
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#getFirstDataRowIndex()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#setFirstDataRowIndex(int)}.
	 */
	@Test
	public void testSetGetFirstDataRowIndex() {
		int rowIndex = 23;
		CruiseDataColumnSpecs specs = new CruiseDataColumnSpecs();
		assertEquals(0, specs.getFirstDataRowIndex());
		specs.setFirstDataRowIndex(rowIndex);
		assertEquals(rowIndex, specs.getFirstDataRowIndex());
		assertEquals(0, specs.getNumRowsTotal());
		assertEquals("", specs.getExpocode());
		boolean missedError = false;
		try {
			specs.setFirstDataRowIndex(-15);
			missedError = true;
		} catch (IllegalArgumentException ex) {
			// expected result
			;
		}
		if ( missedError )
			fail("negative FirstDataRowIndex did not throw an exception");
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#getColumnNames()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#setColumnNames(java.util.ArrayList)}.
	 */
	@Test
	public void testGetColumnNames() {
		ArrayList<String> colNames = new ArrayList<String>(Arrays.asList(
				new String[] { 
						"obs. time [UTC]", "longitude", "latitude", "sal [PSU]",
						"SST [C]", "Tequ [C]", "PPPP [hPa]", "Pequ [hPa]",
						"xCO2water_equ_dry [umol/mol]" 
				}));
		CruiseDataColumnSpecs specs = new CruiseDataColumnSpecs();
		assertEquals(0, specs.getColumnNames().size());
		specs.setColumnNames(colNames);
		assertEquals(colNames, specs.getColumnNames());
		assertEquals(0, specs.getFirstDataRowIndex());
		assertEquals(0, specs.getNumRowsTotal());
		assertEquals("", specs.getExpocode());
		specs.setColumnNames(null);
		assertEquals(0, specs.getColumnNames().size());		
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#getDataValues()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#setDataValues(java.util.ArrayList)}.
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

		CruiseDataColumnSpecs specs = new CruiseDataColumnSpecs();
		assertEquals(0, specs.getDataValues().size());
		specs.setDataValues(dataValues);
		assertEquals(dataValues, specs.getDataValues());
		assertEquals(0, specs.getColumnNames().size());		
		assertEquals(0, specs.getFirstDataRowIndex());
		assertEquals(0, specs.getNumRowsTotal());
		assertEquals("", specs.getExpocode());
		specs.setDataValues(null);
		assertEquals(0, specs.getDataValues().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#equals(java.lang.Object)}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#hashCode()}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		String myExpocode = "AGSK20031205";
		int numRows = 235;
		int rowIndex = 23;
		ArrayList<String> colNames = new ArrayList<String>(Arrays.asList(
				new String[] { 
						"obs. time [UTC]", "longitude", "latitude", "sal [PSU]",
						"SST [C]", "Tequ [C]", "PPPP [hPa]", "Pequ [hPa]",
						"xCO2water_equ_dry [umol/mol]" 
				}));
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

		CruiseDataColumnSpecs firstSpecs = new CruiseDataColumnSpecs();
		assertFalse( firstSpecs.equals(null) );
		assertFalse( firstSpecs.equals(colNames) );
		CruiseDataColumnSpecs secondSpecs = new CruiseDataColumnSpecs();
		assertEquals(firstSpecs.hashCode(), secondSpecs.hashCode());
		assertEquals(firstSpecs, secondSpecs);

		firstSpecs.setExpocode(myExpocode);
		assertTrue( firstSpecs.hashCode() != secondSpecs.hashCode() );
		assertFalse( firstSpecs.equals(secondSpecs) );
		secondSpecs.setExpocode(myExpocode);
		assertEquals(firstSpecs.hashCode(), secondSpecs.hashCode());
		assertEquals(firstSpecs, secondSpecs);

		firstSpecs.setNumRowsTotal(numRows);
		assertTrue( firstSpecs.hashCode() != secondSpecs.hashCode() );
		assertFalse( firstSpecs.equals(secondSpecs) );
		secondSpecs.setNumRowsTotal(numRows);
		assertEquals(firstSpecs.hashCode(), secondSpecs.hashCode());
		assertEquals(firstSpecs, secondSpecs);

		firstSpecs.setFirstDataRowIndex(rowIndex);
		assertTrue( firstSpecs.hashCode() != secondSpecs.hashCode() );
		assertFalse( firstSpecs.equals(secondSpecs) );
		secondSpecs.setFirstDataRowIndex(rowIndex);
		assertEquals(firstSpecs.hashCode(), secondSpecs.hashCode());
		assertEquals(firstSpecs, secondSpecs);

		firstSpecs.setColumnNames(colNames);
		assertTrue( firstSpecs.hashCode() != secondSpecs.hashCode() );
		assertFalse( firstSpecs.equals(secondSpecs) );
		secondSpecs.setColumnNames(colNames);
		assertEquals(firstSpecs.hashCode(), secondSpecs.hashCode());
		assertEquals(firstSpecs, secondSpecs);

		firstSpecs.setDataValues(dataValues);
		assertTrue( firstSpecs.hashCode() != secondSpecs.hashCode() );
		assertFalse( firstSpecs.equals(secondSpecs) );
		secondSpecs.setDataValues(dataValues);
		assertEquals(firstSpecs.hashCode(), secondSpecs.hashCode());
		assertEquals(firstSpecs, secondSpecs);
	}

}
