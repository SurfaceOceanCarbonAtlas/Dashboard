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

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Unit tests for {@link gov.noaa.pmel.dashboard.shared.DataColumnType}
 * 
 * @author Karl Smith
 */
public class DataColumnTypeTest {

	private static final String VAR_NAME = "xCO2_atm_dry";
	private static final Double SORT_ORDER = 3.14159;
	private static final String DISPLAY_NAME = "xCO2 air dry";
	private static final String DATA_CLASS_NAME = "Double";
	private static final String DESCRIPTION = "mole fraction of carbon dioxide";
	private static final String STANDARD_NAME = "mole_fraction_co2";
	private static final String CATEGORY_NAME = "CO2";
	private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("umol/mol", "mmol/mol"));
	private static final Integer DEFAULT_UNIT_INDEX = Integer.valueOf(0);
	private static final Integer SELECTED_UNIT_INDEX = Integer.valueOf(1);
	private static final String SELECTED_MISSING_VALUE = "NaN";

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getVarName()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setVarName(java.lang.String)}.
	 */
	@Test
	public void testGetSetVarName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setVarName(VAR_NAME);
		assertEquals(VAR_NAME, dtype.getVarName());
		dtype.setVarName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getSortOrder()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setSortOrder(java.lang.String)}.
	 */
	@Test
	public void testGetSetSortOrder() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		dtype.setSortOrder(SORT_ORDER);
		assertEquals(SORT_ORDER, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setSortOrder(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getDisplayName()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setDisplayName(java.lang.String)}.
	 */
	@Test
	public void testGetSetDisplayName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		dtype.setDisplayName(DISPLAY_NAME);
		assertEquals(DISPLAY_NAME, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setDisplayName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getDataClassName()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setDataClassName(java.lang.String)}.
	 */
	@Test
	public void testGetSetDataClassName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
		dtype.setDataClassName(DATA_CLASS_NAME);
		assertEquals(DATA_CLASS_NAME, dtype.getDataClassName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setDataClassName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getDescription()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setDescription(java.lang.String)}.
	 */
	@Test
	public void testGetSetDescription() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDescription());
		dtype.setDescription(DESCRIPTION);
		assertEquals(DESCRIPTION, dtype.getDescription());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setDescription(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDescription());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getStandardName()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setStandardName(java.lang.String)}.
	 */
	@Test
	public void testGetSetStandardName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getStandardName());
		dtype.setStandardName(STANDARD_NAME);
		assertEquals(STANDARD_NAME, dtype.getStandardName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDescription());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setStandardName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getStandardName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getCategoryName()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setCategoryName(java.lang.String)}.
	 */
	@Test
	public void testGetSetCategoryName() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getCategoryName());
		dtype.setCategoryName(CATEGORY_NAME);
		assertEquals(CATEGORY_NAME, dtype.getCategoryName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getStandardName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDescription());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setCategoryName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getCategoryName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getUnits()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setUnits(java.util.Collection)}.
	 */
	@Test
	public void testGetSetUnits() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.NO_UNITS, dtype.getUnits());
		dtype.setUnits(UNITS);
		assertEquals(UNITS, dtype.getUnits());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getCategoryName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getStandardName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDescription());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setUnits(null);
		assertEquals(DashboardUtils.NO_UNITS, dtype.getUnits());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getSelectedUnitIndex()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setSelectedUnitIndex(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetSelectedUnitIndex() {
		DataColumnType dtype = new DataColumnType();
		dtype.setUnits(UNITS);
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());
		dtype.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		assertEquals(SELECTED_UNIT_INDEX, dtype.getSelectedUnitIndex());
		assertEquals(UNITS, dtype.getUnits());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getCategoryName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getStandardName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDescription());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());

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
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#getSelectedMissingValue()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#setSelectedMissingValue(java.lang.String)}.
	 */
	@Test
	public void testGetSetSelectedMissingValue() {
		DataColumnType dtype = new DataColumnType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getSelectedMissingValue());
		dtype.setSelectedMissingValue(SELECTED_MISSING_VALUE);
		assertEquals(SELECTED_MISSING_VALUE, dtype.getSelectedMissingValue());
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());
		assertEquals(DashboardUtils.NO_UNITS, dtype.getUnits());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getCategoryName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getStandardName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDescription());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDataClassName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getDisplayName());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, dtype.getSortOrder());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getVarName());
		dtype.setSelectedMissingValue(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getSelectedMissingValue());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#DataColumnType(
	 * 		java.lang.String, java.lang.Double, java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String, java.util.Collection)}.
	 */
	@Test
	public void testDataColumnType() {
		DataColumnType dtype = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, dtype.getSelectedMissingValue());
		assertEquals(DEFAULT_UNIT_INDEX, dtype.getSelectedUnitIndex());
		assertEquals(UNITS, dtype.getUnits());
		assertEquals(CATEGORY_NAME, dtype.getCategoryName());
		assertEquals(STANDARD_NAME, dtype.getStandardName());
		assertEquals(DESCRIPTION, dtype.getDescription());
		assertEquals(DATA_CLASS_NAME, dtype.getDataClassName());
		assertEquals(DISPLAY_NAME, dtype.getDisplayName());
		assertEquals(SORT_ORDER, dtype.getSortOrder());
		assertEquals(VAR_NAME, dtype.getVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#assignDataUnit(
	 * gov.noaa.pmel.dashboard.shared.DataColumnType, java.lang.String)}.
	 */
	@Test
	public void testAssignDataUnit() {
		DataColumnType dtype = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, null);
		assertTrue( dtype.setSelectedUnit("") );
		assertEquals(Integer.valueOf(0), dtype.getSelectedUnitIndex());
		assertFalse( dtype.setSelectedUnit(null) );
		dtype = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertTrue( dtype.setSelectedUnit(UNITS.get(1).toUpperCase()) );
		assertEquals(Integer.valueOf(1), dtype.getSelectedUnitIndex());
		assertTrue( dtype.setSelectedUnit(UNITS.get(1).toLowerCase()) );
		assertEquals(Integer.valueOf(1), dtype.getSelectedUnitIndex());
		assertFalse(dtype.setSelectedUnit("") );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#typeNameEquals(java.lang.String)}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#typeNameEquals(gov.noaa.pmel.dashboard.shared.DataColumnType)}..
	 */
	@Test
	public void testTypeNameEquals() {
		DataColumnType dtype = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);

		assertTrue( dtype.typeNameEquals(DISPLAY_NAME.toUpperCase()) );
		assertTrue( dtype.typeNameEquals(DISPLAY_NAME.toLowerCase()) );
		assertTrue( dtype.typeNameEquals(DISPLAY_NAME.replaceAll(" ", "; ")) );
		assertTrue( dtype.typeNameEquals(VAR_NAME.toUpperCase()) );
		assertTrue( dtype.typeNameEquals(VAR_NAME.toLowerCase()) );
		assertTrue( dtype.typeNameEquals(VAR_NAME.replaceAll("_", "==")) );
		assertFalse( dtype.typeNameEquals(DATA_CLASS_NAME) );
		assertFalse( dtype.typeNameEquals(DESCRIPTION) );
		assertFalse( dtype.typeNameEquals(STANDARD_NAME) );
		assertFalse( dtype.typeNameEquals(CATEGORY_NAME) );

		DataColumnType other = new DataColumnType();
		other.setDisplayName(DISPLAY_NAME.toUpperCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );
		other.setDisplayName(DISPLAY_NAME.toLowerCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );

		other = new DataColumnType();
		other.setVarName(VAR_NAME.toUpperCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );
		other.setVarName(VAR_NAME.toLowerCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );

		other = new DataColumnType();
		other.setDisplayName(VAR_NAME.toUpperCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );
		other.setDisplayName(VAR_NAME.toLowerCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );

		other = new DataColumnType();
		other.setVarName(DISPLAY_NAME.toUpperCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );
		other.setVarName(DISPLAY_NAME.toLowerCase());
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );

		other.setDataClassName("Blob");
		assertTrue( dtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(dtype) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#isQCType()},
	 * {@link gov.noaa.pmel.dashboard.shared.DataColumnType#isCommentType()}.
	 * {@link gov.noaa.pmel.dashboard.shared.DataColumnType#isCommentTypeFor(gov.noaa.pmel.dashboard.shared.DataColumnType)}.
	 */
	@Test
	public void testIsQCTypeIsComment() {
		DataColumnType dtype1 = new DataColumnType("WOCE_flag", 100.0, "WOCE flag", 
				DashboardUtils.CHAR_DATA_CLASS_NAME, null, null, 
				DashboardUtils.QUALITY_CATEGORY, null);
		assertTrue( dtype1.isQCType() );
		assertFalse( dtype1.isCommentType() );

		DataColumnType dtype2 = new DataColumnType("qc_O2", 100.0, "Oxygen Quality Flag", 
				DashboardUtils.CHAR_DATA_CLASS_NAME, null, null, 
				DashboardUtils.QUALITY_CATEGORY, null);
		assertTrue( dtype2.isQCType() );

		DataColumnType ctype = new DataColumnType("Comment_woce_flag", 101.0, "comment for WOCE flag",
				DashboardUtils.STRING_DATA_CLASS_NAME, null, null, null, null);
		assertTrue( ctype.isCommentType() );
		assertTrue( ctype.isCommentTypeFor(dtype1) );
		assertFalse( ctype.isCommentTypeFor(dtype2) );

		ctype = new DataColumnType("QC_O2_comment", 101.0, "Oxygen Quality Flag Comment",
				DashboardUtils.STRING_DATA_CLASS_NAME, null, null, null, null);
		assertFalse( ctype.isQCType() );
		assertTrue( ctype.isCommentType() );
		assertFalse( ctype.isCommentTypeFor(dtype1) );
		assertTrue( ctype.isCommentTypeFor(dtype2) );

		ctype = new DataColumnType("Comment_woce_flag", 101.0, "WOCE Comment invalid class name",
				DashboardUtils.INT_DATA_CLASS_NAME, null, null, null, null);
		assertFalse( ctype.isCommentType() );
		assertFalse( ctype.isCommentTypeFor(dtype1) );

		dtype2 = new DataColumnType("QC", 100.0, "QC flag", 
				DashboardUtils.CHAR_DATA_CLASS_NAME, null, null, 
				DashboardUtils.QUALITY_CATEGORY, DashboardUtils.NO_UNITS);
		assertTrue( dtype2.isQCType() );

		ctype = new DataColumnType("Comment", 101.0, "General Comment",
				DashboardUtils.STRING_DATA_CLASS_NAME, null, null, null, null);
		assertTrue( ctype.isCommentType() );
		assertFalse( ctype.isCommentTypeFor(dtype2) );

		dtype2 = new DataColumnType("WOCE_flag", 100.0, "WOCE flag invalid class name", 
				DashboardUtils.INT_DATA_CLASS_NAME, null, null, 
				DashboardUtils.QUALITY_CATEGORY, null);
		assertFalse( dtype2.isQCType() );

		dtype2 = new DataColumnType("WOCE_flag", 100.0, "WOCE flag invalid category", 
				DashboardUtils.CHAR_DATA_CLASS_NAME, null, null, 
				DashboardUtils.IDENTIFIER_CATEGORY, null);
		assertFalse( dtype2.isQCType() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#compareTo(
	 * gov.noaa.pmel.dashboard.shared.DataColumnType)}.
	 */
	@Test
	public void testCompareTo() {
		DataColumnType dtype = new DataColumnType();
		DataColumnType other = new DataColumnType();
		assertEquals(0, dtype.compareTo(other));
		assertEquals(0, other.compareTo(dtype));
		dtype.setSortOrder(SORT_ORDER);
		other.setSortOrder(SORT_ORDER + 10.0);
		assertTrue( dtype.compareTo(other) < 0 );
		assertTrue( other.compareTo(dtype) > 0 );
		other.setSortOrder(SORT_ORDER - 10.0);
		assertTrue( dtype.compareTo(other) > 0 );
		assertTrue( other.compareTo(dtype) < 0 );
		dtype.setVarName("avar");
		other.setVarName("zvar");
		assertTrue( dtype.compareTo(other) > 0 );
		assertTrue( other.compareTo(dtype) < 0 );
		other.setSortOrder(SORT_ORDER + 10.0);
		assertTrue( dtype.compareTo(other) < 0 );
		assertTrue( other.compareTo(dtype) > 0 );
		other.setSortOrder(SORT_ORDER);
		assertTrue( dtype.compareTo(other) < 0 );
		assertTrue( other.compareTo(dtype) > 0 );
		other.setVarName("avar");
		assertEquals(0, dtype.compareTo(other));
		assertEquals(0, other.compareTo(dtype));
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataColumnType#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		DataColumnType dtype = new DataColumnType();
		assertFalse( dtype.equals(null) );
		assertFalse( dtype.equals(DashboardUtils.STRING_MISSING_VALUE) );

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

		dtype.setSortOrder(SORT_ORDER);
		// floating point values ignored for the hash code
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setSortOrder(SORT_ORDER);
		assertTrue( dtype.hashCode() == other.hashCode() );
		assertTrue( dtype.equals(other) );

		dtype.setDisplayName(DISPLAY_NAME);
		assertFalse( dtype.hashCode() == other.hashCode() );
		assertFalse( dtype.equals(other) );
		other.setDisplayName(DISPLAY_NAME);
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

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataColumnType#duplicate()}.
	 */
	@Test
	public void testDuplicate() {
		DataColumnType dtype = new DataColumnType(DISPLAY_NAME, SORT_ORDER, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		dtype.setSelectedUnitIndex(SELECTED_UNIT_INDEX);
		dtype.setSelectedMissingValue(SELECTED_MISSING_VALUE);
		DataColumnType other = dtype.duplicate();
		assertFalse( dtype == other );
		assertEquals(dtype, other);
		assertFalse( dtype.getUnits() == other.getUnits() );
	}

}
