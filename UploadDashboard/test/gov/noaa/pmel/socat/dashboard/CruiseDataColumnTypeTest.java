/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType;

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
		assertEquals(0, spec.getStdColumnNum());
		spec.setStdColumnNum(stdColumnNum);
		assertEquals(stdColumnNum, spec.getStdColumnNum());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getFullName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setFullName(java.lang.String)}.
	 */
	@Test
	public void testSetGetFullName() {
		String fullName = "Atmospheric Pressure";
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals("", spec.getFullName());
		spec.setFullName(fullName);
		assertEquals(fullName, spec.getFullName());
		assertEquals(0, spec.getStdColumnNum());
		spec.setFullName(null);
		assertEquals("", spec.getFullName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#getLabelName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#setLabelName(java.lang.String)}.
	 */
	@Test
	public void testSetGetLabelName() {
		String labelName = "PPPP";
		CruiseDataColumnType spec = new CruiseDataColumnType();
		assertEquals("", spec.getLabelName());
		spec.setLabelName(labelName);
		assertEquals(labelName, spec.getLabelName());
		assertEquals("", spec.getFullName());
		assertEquals(0, spec.getStdColumnNum());
		spec.setLabelName(null);
		assertEquals("", spec.getLabelName());
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
		assertEquals("", spec.getLabelName());
		assertEquals("", spec.getFullName());
		assertEquals(0, spec.getStdColumnNum());
		spec.setDataType(null);
		assertEquals("", spec.getDataType());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#DataColumnSpec(
	 * int,java.lang.String,java.lang.String,java.lang.String)}.
	 */
	@Test
	public void testDataColumnSpecStdColumnNumDataTypeLabelNameFullName() {
		int sdtdColumnNum = 4;
		String fullName = "Atmospheric Pressure";
		String labelName = "PPPP";
		String dataType = "pressure";
		CruiseDataColumnType spec = new CruiseDataColumnType(sdtdColumnNum, dataType, labelName, fullName);
		assertEquals(sdtdColumnNum, spec.getStdColumnNum());
		assertEquals(dataType, spec.getDataType());
		assertEquals(labelName, spec.getLabelName());
		assertEquals(fullName, spec.getFullName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#equals(java.lang.Object)}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType#hashCode()}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		int priority = 4;
		String fullName = "Atmospheric Pressure";
		String labelName = "PPPP";
		String dataType = "pressure";

		CruiseDataColumnType firstSpec = new CruiseDataColumnType();
		assertFalse( firstSpec.equals(null) );
		assertFalse( firstSpec.equals(fullName) );
		CruiseDataColumnType secondSpec = new CruiseDataColumnType();
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setStdColumnNum(priority);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setStdColumnNum(priority);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setFullName(fullName);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setFullName(fullName);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setLabelName(labelName);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setLabelName(labelName);
		assertEquals(firstSpec.hashCode(), secondSpec.hashCode());
		assertEquals(firstSpec, secondSpec);

		firstSpec.setDataType(dataType);
		assertTrue( firstSpec.hashCode() != secondSpec.hashCode() );
		assertFalse( firstSpec.equals(secondSpec) );
		secondSpec.setDataType(dataType);
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
		String firstLabelName = "PPPP";
		String firstFullName = "Atmospheric Pressure";

		int secondStdColNum = 2;
		String secondDataType = "temperature";
		String secondLabelName = "SST";
		String secondFullName = "Sea Surface Temperature";

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

		firstSpec.setLabelName(firstLabelName);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setLabelName(secondLabelName);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setLabelName(firstLabelName);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		firstSpec.setFullName(firstFullName);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
		secondSpec.setFullName(secondFullName);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );
		secondSpec.setFullName(firstFullName);
		assertEquals(0, firstSpec.compareTo(secondSpec));
		assertEquals(0, secondSpec.compareTo(firstSpec));

		secondSpec.setFullName(firstFullName);
		secondSpec.setDataType(secondDataType);
		secondSpec.setLabelName(secondLabelName);
		assertTrue( firstSpec.compareTo(secondSpec) < 0 );
		assertTrue( secondSpec.compareTo(firstSpec) > 0 );

		secondSpec.setStdColumnNum(secondStdColNum);
		assertTrue( firstSpec.compareTo(secondSpec) > 0 );
		assertTrue( secondSpec.compareTo(firstSpec) < 0 );
	}

}
