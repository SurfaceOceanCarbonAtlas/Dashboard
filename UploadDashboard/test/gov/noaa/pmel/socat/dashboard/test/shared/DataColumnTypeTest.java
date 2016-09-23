/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * Unit tests for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType}
 * 
 * @author Karl Smith
 */
public class DataColumnTypeTest {

	private static final String VAR_NAME = "xCO2";
	private static final String DATA_CLASS_NAME = "Double";
	private static final String DESCRIPTION = "mole fraction of carbon dioxide";
	private static final String STANDARD_NAME = "mole_fraction_co2";
	private static final String CATEGORY_NAME = "CO2";
	private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("umol/mol", "mmol/mol"));
	private static final Integer DEFAULT_UNIT_INDEX = Integer.valueOf(0);
	private static final Integer SELECTED_UNIT_INDEX = Integer.valueOf(1);
	private static final String SELECTED_MISSING_VALUE = "NaN";
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getVarName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setVarName(java.lang.String)}.
	 */
	@Test
	public void testGetSetVarName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals("", dtype.getVarName());
		dtype.setVarName(VAR_NAME);
		assertEquals(VAR_NAME, dtype.getVarName());
		dtype.setVarName(null);
		assertEquals("", dtype.getVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getDataClassName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setDataClassName(java.lang.String)}.
	 */
	@Test
	public void testGetSetDataClassName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals("", dtype.getDataClassName());
		dtype.setDataClassName(DATA_CLASS_NAME);
		assertEquals(DATA_CLASS_NAME, dtype.getDataClassName());
		assertEquals("", dtype.getVarName());
		dtype.setDataClassName(null);
		assertEquals("", dtype.getDataClassName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getDescription()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setDescription(java.lang.String)}.
	 */
	@Test
	public void testGetSetDescription() {
		DataColumnType dtype = new DataColumnType();
		assertEquals("", dtype.getDescription());
		dtype.setDescription(DESCRIPTION);
		assertEquals(DESCRIPTION, dtype.getDescription());
		assertEquals("", dtype.getDataClassName());
		assertEquals("", dtype.getVarName());
		dtype.setDescription(null);
		assertEquals("", dtype.getDescription());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getStandardName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setStandardName(java.lang.String)}.
	 */
	@Test
	public void testGetSetStandardName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals("", dtype.getStandardName());
		dtype.setStandardName(STANDARD_NAME);
		assertEquals(STANDARD_NAME, dtype.getStandardName());
		assertEquals("", dtype.getDescription());
		assertEquals("", dtype.getDataClassName());
		assertEquals("", dtype.getVarName());
		dtype.setStandardName(null);
		assertEquals("", dtype.getStandardName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getCategoryName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setCategoryName(java.lang.String)}.
	 */
	@Test
	public void testGetSetCategoryName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals("", dtype.getCategoryName());
		dtype.setCategoryName(CATEGORY_NAME);
		assertEquals(CATEGORY_NAME, dtype.getCategoryName());
		assertEquals("", dtype.getStandardName());
		assertEquals("", dtype.getDescription());
		assertEquals("", dtype.getDataClassName());
		assertEquals("", dtype.getVarName());
		dtype.setCategoryName(null);
		assertEquals("", dtype.getCategoryName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getUnits()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setUnits(java.util.Collection)}.
	 */
	@Test
	public void testGetSetUnits() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DataColumnType.NO_UNITS, dtype.getUnits());
		dtype.setUnits(UNITS);
		assertEquals(UNITS, dtype.getUnits());
		assertEquals("", dtype.getCategoryName());
		assertEquals("", dtype.getStandardName());
		assertEquals("", dtype.getDescription());
		assertEquals("", dtype.getDataClassName());
		assertEquals("", dtype.getVarName());
		dtype.setUnits(null);
		assertEquals(DataColumnType.NO_UNITS, dtype.getUnits());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getSelectedUnitIndex()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setSelectedUnitIndex(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetSelectedUnitIndex() {
		DataColumnType dtype = new DataColumnType();
		dtype.setUnits(UNITS);
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());
		dtype.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		assertEquals(SELECTED_UNIT_INDEX, dtype.getSelectedUnitIndex());
		assertEquals(UNITS, dtype.getUnits());
		assertEquals("", dtype.getCategoryName());
		assertEquals("", dtype.getStandardName());
		assertEquals("", dtype.getDescription());
		assertEquals("", dtype.getDataClassName());
		assertEquals("", dtype.getVarName());

		// Zero returned for null or invalid index
		dtype.setSelectedUnitIndex(null);
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());

		dtype.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		assertEquals(SELECTED_UNIT_INDEX, dtype.getSelectedUnitIndex());
		dtype.setSelectedUnitIndex(-1);
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());

		dtype.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		assertEquals(SELECTED_UNIT_INDEX, dtype.getSelectedUnitIndex());
		dtype.setSelectedUnitIndex(100);
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#getSelectedMissingValue()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#setSelectedMissingValue(java.lang.String)}.
	 */
	@Test
	public void testGetSetSelectedMissingValue() {
		DataColumnType dtype = new DataColumnType();
		assertEquals("", dtype.getSelectedMissingValue());
		dtype.setSelectedMissingValue(SELECTED_MISSING_VALUE);
		assertEquals(SELECTED_MISSING_VALUE, dtype.getSelectedMissingValue());
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());
		assertEquals(DataColumnType.NO_UNITS, dtype.getUnits());
		assertEquals("", dtype.getCategoryName());
		assertEquals("", dtype.getStandardName());
		assertEquals("", dtype.getDescription());
		assertEquals("", dtype.getDataClassName());
		assertEquals("", dtype.getVarName());
		dtype.setSelectedMissingValue(null);
		assertEquals("", dtype.getSelectedMissingValue());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#DataColumnType(
	 * 		java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, 
	 * 		java.util.Collection)}.
	 */
	@Test
	public void testDataColumnType() {
		DataColumnType dtype = new DataColumnType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertEquals("", dtype.getSelectedMissingValue());
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());
		assertEquals(UNITS, dtype.getUnits());
		assertEquals(CATEGORY_NAME, dtype.getCategoryName());
		assertEquals(STANDARD_NAME, dtype.getStandardName());
		assertEquals(DESCRIPTION, dtype.getDescription());
		assertEquals(DATA_CLASS_NAME, dtype.getDataClassName());
		assertEquals(VAR_NAME, dtype.getVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#duplicate()}.
	 */
	@Test
	public void testDuplicate() {
		DataColumnType dtype = new DataColumnType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		dtype.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		dtype.setSelectedMissingValue(SELECTED_MISSING_VALUE);
		DataColumnType other = dtype.duplicate();
		assertFalse( dtype == other );
		assertEquals(dtype, other);
		assertFalse( dtype.getUnits() == other.getUnits() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#typeEquals(gov.noaa.pmel.socat.dashboard.shared.DataColumnType)}.
	 */
	@Test
	public void testTypeEquals() {
		DataColumnType dtype = new DataColumnType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		DataColumnType other = new DataColumnType();
		other.setVarName(VAR_NAME.toUpperCase());
		assertTrue( dtype.typeEquals(other) );
		other.setVarName(VAR_NAME.toLowerCase());
		assertTrue( dtype.typeEquals(other) );
		other.setDataClassName("Blob");
		assertTrue( dtype.typeEquals(other) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataColumnType#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		DataColumnType dtype = new DataColumnType();
		assertFalse( dtype.equals(null) );
		assertFalse( dtype.equals("") );

		DataColumnType other = new DataColumnType();
		assertFalse( dtype == other );
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setVarName(VAR_NAME);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setVarName(VAR_NAME);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setDataClassName(DATA_CLASS_NAME);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setDataClassName(DATA_CLASS_NAME);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setDescription(DESCRIPTION);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setDescription(DESCRIPTION);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setStandardName(STANDARD_NAME);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setStandardName(STANDARD_NAME);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setCategoryName(CATEGORY_NAME);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setCategoryName(CATEGORY_NAME);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setUnits(UNITS);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setUnits(UNITS);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setSelectedMissingValue(SELECTED_MISSING_VALUE);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setSelectedMissingValue(SELECTED_MISSING_VALUE);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );
	}

}
