/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

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
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#getColumnTypes()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs#setColumnTypes(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetColumnTypes() {
		String[] userNames = {"time", "longitude", "latitude",
				"sal", "SST", "T_equ", "P_atm", "P_equ", "xCO2_equ"};
		int[] stdColNums = {
				DashboardUtils.TIMESTAMP_STD_COLUMN_NUM, 
				DashboardUtils.LONGITUDE_STD_COLUMN_NUM, 
				DashboardUtils.LATITUDE_STD_COLUMN_NUM, 
				DashboardUtils.SAMPLE_SAL_STD_COLUMN_NUM, 
				DashboardUtils.SST_STD_COLUMN_NUM, 
				DashboardUtils.TEQU_STD_COLUMN_NUM, 
				DashboardUtils.PPPP_STD_COLUMN_NUM, 
				DashboardUtils.PEQU_STD_COLUMN_NUM, 
				DashboardUtils.XCO2_EQU_STD_COLUMN_NUM
		};
		String[] units = {"YYYY-MM-DD HH:MM", "dec deg E", "dec deg N",
				"PSU", "deg C", "deg C", "mbar", "mbar", "umol/mol"};
		String[] descriptions = {
				"date and time of the measurement",
				"longitude",
				"latitude",
				"salinity", 
				"sea temperature",
				"equilibrator temperature",
				"atmospheric pressure",
				"equilibrator pressure",
				"measured xCO2 at equilibrator temperature (dry air)"				
		};
		
		ArrayList<CruiseDataColumnType> columnTypes = 
				new ArrayList<CruiseDataColumnType>(stdColNums.length);
		for (int k = 0; k < stdColNums.length; k++) {
			CruiseDataColumnType colType = new CruiseDataColumnType();
			colType.setUserColumnNum(k+1);
			colType.setStdColumnNum(stdColNums[k]);
			colType.setDataType(DashboardUtils.STD_DATA_TYPES.get(stdColNums[k]));
			colType.setStdHeaderName(DashboardUtils.STD_DATA_HEADER_NAMES.get(stdColNums[k]));
			colType.setUserHeaderName(userNames[k]);
			colType.setUnit(units[k]);
			colType.setDescription(descriptions[k]);
			columnTypes.add(colType);
		}

		CruiseDataColumnSpecs specs = new CruiseDataColumnSpecs();
		assertEquals(0, specs.getColumnTypes().size());
		specs.setColumnTypes(columnTypes);
		assertEquals(columnTypes, specs.getColumnTypes());
		assertEquals(0, specs.getNumRowsTotal());
		assertEquals("", specs.getExpocode());
		specs.setColumnTypes(null);
		assertEquals(0, specs.getColumnTypes().size());		
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
		assertEquals(0, specs.getColumnTypes().size());		
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

		String[] userNames = {"time", "longitude", "latitude",
				"sal", "SST", "T_equ", "P_atm", "P_equ", "xCO2_equ"};
		int[] stdColNums = {
				DashboardUtils.TIMESTAMP_STD_COLUMN_NUM, 
				DashboardUtils.LONGITUDE_STD_COLUMN_NUM, 
				DashboardUtils.LATITUDE_STD_COLUMN_NUM, 
				DashboardUtils.SAMPLE_SAL_STD_COLUMN_NUM, 
				DashboardUtils.SST_STD_COLUMN_NUM, 
				DashboardUtils.TEQU_STD_COLUMN_NUM, 
				DashboardUtils.PPPP_STD_COLUMN_NUM, 
				DashboardUtils.PEQU_STD_COLUMN_NUM, 
				DashboardUtils.XCO2_EQU_STD_COLUMN_NUM
		};
		String[] units = {"YYYY-MM-DD HH:MM", "dec deg E", "dec deg N",
				"PSU", "deg C", "deg C", "mbar", "mbar", "umol/mol"};
		String[] descriptions = {
				"date and time of the measurement",
				"longitude",
				"latitude",
				"salinity", 
				"sea temperature",
				"equilibrator temperature",
				"atmospheric pressure",
				"equilibrator pressure",
				"measured xCO2 at equilibrator temperature (dry air)"				
		};
		
		ArrayList<CruiseDataColumnType> columnTypes = 
				new ArrayList<CruiseDataColumnType>(stdColNums.length);
		for (int k = 0; k < stdColNums.length; k++) {
			CruiseDataColumnType colType = new CruiseDataColumnType();
			colType.setUserColumnNum(k+1);
			colType.setStdColumnNum(stdColNums[k]);
			colType.setDataType(DashboardUtils.STD_DATA_TYPES.get(stdColNums[k]));
			colType.setStdHeaderName(DashboardUtils.STD_DATA_HEADER_NAMES.get(stdColNums[k]));
			colType.setUserHeaderName(userNames[k]);
			colType.setUnit(units[k]);
			colType.setDescription(descriptions[k]);
			columnTypes.add(colType);
		}

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
		assertFalse( firstSpecs.equals(columnTypes) );
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

		firstSpecs.setColumnTypes(columnTypes);
		assertTrue( firstSpecs.hashCode() != secondSpecs.hashCode() );
		assertFalse( firstSpecs.equals(secondSpecs) );
		secondSpecs.setColumnTypes(columnTypes);
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
