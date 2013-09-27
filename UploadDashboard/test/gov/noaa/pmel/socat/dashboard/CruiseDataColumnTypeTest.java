/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import org.junit.Test;

/**
 * Test methods for CruiseDataColumnType
 * @author Karl Smith
 */
public class CruiseDataColumnTypeTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getStdColumnNum()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setStdColumnNum(int)}.
	 */
	@Test
	public void testSetGetStdColumnNum() {
		int stdColumnNum = 4;
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getStdColumnNum());
		spec.setStdColumnNum(stdColumnNum);
		assertEquals(stdColumnNum, spec.getStdColumnNum());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getDescription()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setDescription(java.lang.String)}.
	 */
	@Test
	public void testSetGetDescription() {
		String description = "Atmospheric Pressure";
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals("", spec.getDescription());
		spec.setDescription(description);
		assertEquals(description, spec.getDescription());
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getStdColumnNum());
		spec.setDescription(null);
		assertEquals("", spec.getDescription());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getStdHeaderName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setStdHeaderName(java.lang.String)}.
	 */
	@Test
	public void testSetGetStdHeaderName() {
		String stdHeaderName = "PPPP";
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals("", spec.getStdHeaderName());
		spec.setStdHeaderName(stdHeaderName);
		assertEquals(stdHeaderName, spec.getStdHeaderName());
		assertEquals("", spec.getDescription());
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getStdColumnNum());
		spec.setStdHeaderName(null);
		assertEquals("", spec.getStdHeaderName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getDataType()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setDataType(java.lang.String)}.
	 */
	@Test
	public void testSetGetDataType() {
		String dataType = "pressure";
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals("", spec.getDataType());
		spec.setDataType(dataType);
		assertEquals(dataType, spec.getDataType());
		assertEquals("", spec.getStdHeaderName());
		assertEquals("", spec.getDescription());
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getStdColumnNum());
		spec.setDataType(null);
		assertEquals("", spec.getDataType());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getUnits()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setUnit(java.lang.String[])}.
	 */
	@Test
	public void testSetGetUnits() {
		String unit = "mbar";
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals("", spec.getUnit());
		spec.setUnit(unit);
		assertEquals(unit, spec.getUnit());
		assertEquals("", spec.getDataType());
		assertEquals("", spec.getStdHeaderName());
		assertEquals("", spec.getDescription());
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getStdColumnNum());
		spec.setUnit(null);
		assertEquals("", spec.getUnit());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getUserHeaderName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setUserHeaderName(java.lang.String)}.
	 */
	@Test
	public void testSetGetUserHeaderName() {
		String userHeaderName = "P_atm";
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals("", spec.getUserHeaderName());
		spec.setUserHeaderName(userHeaderName);
		assertEquals(userHeaderName, spec.getUserHeaderName());
		assertEquals("", spec.getUnit());
		assertEquals("", spec.getDataType());
		assertEquals("", spec.getStdHeaderName());
		assertEquals("", spec.getDescription());
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getStdColumnNum());
		spec.setUserHeaderName(null);
		assertEquals("", spec.getUserHeaderName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getUserColumnNum()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setUserColumnNum(int)}.
	 */
	@Test
	public void testSetGetUserColumnNum() {
		int userColumnNum = 15;
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getUserColumnNum());
		spec.setUserColumnNum(userColumnNum);
		assertEquals(userColumnNum, spec.getUserColumnNum());
		assertEquals("", spec.getUserHeaderName());
		assertEquals("", spec.getUnit());
		assertEquals("", spec.getDataType());
		assertEquals("", spec.getStdHeaderName());
		assertEquals("", spec.getDescription());
		assertEquals(DashboardUtils.UNKNOWN_DATA_STD_COLUMN_NUM, spec.getStdColumnNum());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#equals(java.lang.Object)}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#hashCode()}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		int stdColumnNum = 4;
		String description = "Atmospheric Pressure";
		String stdHeaderName = "PPPP";
		String dataType = "pressure";
		String unit = "mbar";
		String userHeaderName = "P_atm";
		int userColumnNum = 15;

		CruiseDataColumnType firstSpec = new CruiseDataColumnType();
		assertFalse( firstSpec.equals(null) );
		assertFalse( firstSpec.equals(description) );
		CruiseDataColumnType secondSpec = new CruiseDataColumnType();
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setStdColumnNum(stdColumnNum);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setStdColumnNum(stdColumnNum);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setDescription(description);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setDescription(description);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setStdHeaderName(stdHeaderName);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setStdHeaderName(stdHeaderName);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setDataType(dataType);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setDataType(dataType);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setUnit(unit);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setUnit(unit);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setUserHeaderName(userHeaderName);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setUserHeaderName(userHeaderName);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setUserColumnNum(userColumnNum);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setUserColumnNum(userColumnNum);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#compareTo(
	 * gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType)}.
	 */
	@Test
	public void testCompareToDataColumnSpec() {
		int firstStdColNum = 4;
		String firstDataType = "pressure";
		String firstStdHeaderName = "PPPP";
		String firstDescription = "Atmospheric Pressure";
		String firstUnit = "atm";
		String firstUserHeaderName = "P_atm";
		int firstUserColumnNum = 7;

		int secondStdColNum = 2;
		String secondDataType = "temperature";
		String secondStdHeaderName = "SST";
		String secondDescription = "Sea Surface Temperature";
		String secondUnit = "deg C";
		String secondUserHeaderName = "T_sea";
		int secondUserColumnNum = 9;

		CruiseDataColumnType firstSpec = new CruiseDataColumnType();
		CruiseDataColumnType secondSpec = new CruiseDataColumnType();
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		secondSpec.setStdColumnNum(secondStdColNum);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		firstSpec.setStdColumnNum(firstStdColNum);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setStdColumnNum(firstStdColNum);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		firstSpec.setDataType(firstDataType);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setDataType(secondDataType);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setDataType(firstDataType);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		firstSpec.setStdHeaderName(firstStdHeaderName);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setStdHeaderName(secondStdHeaderName);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setStdHeaderName(firstStdHeaderName);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		firstSpec.setDescription(firstDescription);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setDescription(secondDescription);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setDescription(firstDescription);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		firstSpec.setUnit(firstUnit);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setUnit(secondUnit);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setUnit(firstUnit);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		firstSpec.setUserHeaderName(firstUserHeaderName);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setUserHeaderName(secondUserHeaderName);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setUserHeaderName(firstUserHeaderName);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		firstSpec.setUserColumnNum(firstUserColumnNum);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setUserColumnNum(secondUserColumnNum);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setUserColumnNum(firstUserColumnNum);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		secondSpec.setDataType(secondDataType);
		secondSpec.setStdHeaderName(secondStdHeaderName);
		secondSpec.setDescription(firstDescription);
		secondSpec.setUnit(secondUnit);
		secondSpec.setUserHeaderName(secondUserHeaderName);
		secondSpec.setUserColumnNum(secondUserColumnNum);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );

		secondSpec.setStdColumnNum(secondStdColNum);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
	}

}
