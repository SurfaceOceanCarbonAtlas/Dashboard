/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.*;
import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * Tests for {@link gov.noaa.pmel.dashboard.server.DashDataType}
 * 
 * @author Karl Smith
 */
public class DashDataTypeTest {

	private static final String DISPLAY_NAME = "xCO2 atm dry";
	private static final String VAR_NAME = "xCO2_atm_dry";
	private static final String DATA_CLASS_NAME = "Double";
	private static final String DESCRIPTION = "mole fraction of carbon dioxide";
	private static final String STANDARD_NAME = "mole_fraction_co2";
	private static final String CATEGORY_NAME = "CO2";
	private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("umol/mol", "mmol/mol"));

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#DataType(java.lang.String, 
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Collection)} 
	 * as well as
	 * @link gov.noaa.pmel.dashboard.server.DashDataType#getVarName()},
	 * {@link gov.noaa.pmel.dashboard.server.DashDataType#getDataClassName()},
	 * {@link gov.noaa.pmel.dashboard.server.DashDataType#getDescription()},
	 * {@link gov.noaa.pmel.dashboard.server.DashDataType#getStandardName()},
	 * {@link gov.noaa.pmel.dashboard.server.DashDataType#getCategoryName()}, and
	 * {@link gov.noaa.pmel.dashboard.server.DashDataType#getUnits()}.
	 */
	@Test
	public void testStdDataColumnType() {
		DashDataType stdtype = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( UNITS == stdtype.getUnits() );
		assertEquals(UNITS, stdtype.getUnits());
		assertEquals(CATEGORY_NAME, stdtype.getCategoryName());
		assertEquals(STANDARD_NAME, stdtype.getStandardName());
		assertEquals(DESCRIPTION, stdtype.getDescription());
		assertEquals(DATA_CLASS_NAME, stdtype.getDataClassName());
		assertEquals(VAR_NAME, stdtype.getVarName());
		assertEquals(DISPLAY_NAME, stdtype.getDisplayName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(java.lang.String)}
	 * {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.shared.DataColumnType)},
	 * and {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.server.DashDataType)}.
	 */
	@Test
	public void testTypeNameEquals() {
		DashDataType stdtype = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);

		assertTrue( stdtype.typeNameEquals(DISPLAY_NAME.toUpperCase()) );
		assertTrue( stdtype.typeNameEquals(DISPLAY_NAME.toLowerCase()) );
		assertTrue( stdtype.typeNameEquals(VAR_NAME.toUpperCase()) );
		assertTrue( stdtype.typeNameEquals(VAR_NAME.toLowerCase()) );
		assertFalse( stdtype.typeNameEquals(DATA_CLASS_NAME) );
		assertFalse( stdtype.typeNameEquals(DESCRIPTION) );
		assertFalse( stdtype.typeNameEquals(STANDARD_NAME) );
		assertFalse( stdtype.typeNameEquals(CATEGORY_NAME) );

		DashDataType other;
		other = new DashDataType("Blob", VAR_NAME.toLowerCase(), 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType("Blob", VAR_NAME.toUpperCase(), 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType(DISPLAY_NAME.toLowerCase(), "Blob", 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType(DISPLAY_NAME.toUpperCase(), "Blob", 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType("Blob", DISPLAY_NAME.toLowerCase(), 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType("Blob", DISPLAY_NAME.toUpperCase(), 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType(VAR_NAME.toLowerCase(), "Blob", 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType(VAR_NAME.toUpperCase(), "Blob", 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		assertTrue( other.typeNameEquals(stdtype) );

		other = new DashDataType(null, "Blob", DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.typeNameEquals(other) );
		assertFalse( other.typeNameEquals(stdtype) );

		DataColumnType another;
		another = new DataColumnType(DISPLAY_NAME.toLowerCase(), "Blob",
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(another) );

		another = new DataColumnType("Blob", VAR_NAME.toUpperCase(),
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(another) );

		another = new DataColumnType("Blob", DISPLAY_NAME.toUpperCase(),
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(another) );

		another = new DataColumnType(VAR_NAME.toLowerCase(), "Blob", 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(another) );

		another = new DataColumnType(null, "Blob", 
				"Integer", null, null, null, DashboardUtils.NO_UNITS);
		assertFalse( stdtype.typeNameEquals(another) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#duplicate()}.
	 */
	@Test
	public void testDuplicate() {
		DashDataType stdtype = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		DataColumnType dup = stdtype.duplicate();
		DataColumnType other = new DataColumnType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertEquals(other, dup);
		assertFalse( dup.getUnits() == stdtype.getUnits() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#toPropertyValue()}
	 * and {@link gov.noaa.pmel.dashboard.server.DashDataType#fromPropertyValue(java.lang.String,java.lang.String)}.
	 */
	@Test
	public void testToFromPropertyValue() {
		DashDataType stdtype = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		String jsonDesc = stdtype.toPropertyValue();
		assertFalse( jsonDesc.contains(VAR_NAME) );
		assertTrue( jsonDesc.contains("display_name") );
		assertTrue( jsonDesc.contains(DISPLAY_NAME) );
		assertTrue( jsonDesc.contains("data_class") );
		assertTrue( jsonDesc.contains(DATA_CLASS_NAME) );
		assertTrue( jsonDesc.contains("description") );
		assertTrue( jsonDesc.contains(DESCRIPTION) );
		assertTrue( jsonDesc.contains("standard_name") );
		assertTrue( jsonDesc.contains(STANDARD_NAME) );
		assertTrue( jsonDesc.contains("category_name") );
		assertTrue( jsonDesc.contains(CATEGORY_NAME) );
		assertTrue( jsonDesc.contains("units") );
		for ( String val : UNITS )
			assertTrue( jsonDesc.contains(val) );

		DashDataType other = DashDataType.fromPropertyValue(VAR_NAME, jsonDesc);
		assertEquals(stdtype, other);

		stdtype = new DashDataType(DISPLAY_NAME, VAR_NAME, DATA_CLASS_NAME, 
				null, null, null, null);
		jsonDesc = stdtype.toPropertyValue();
		assertFalse( jsonDesc.contains(VAR_NAME) );
		assertTrue( jsonDesc.contains("display_name") );
		assertTrue( jsonDesc.contains(DISPLAY_NAME) );
		assertTrue( jsonDesc.contains("data_class") );
		assertTrue( jsonDesc.contains(DATA_CLASS_NAME) );
		assertFalse( jsonDesc.contains("description") );
		assertFalse( jsonDesc.contains("standard_name") );
		assertFalse( jsonDesc.contains("category_name") );
		assertFalse( jsonDesc.contains("units") );

		other = DashDataType.fromPropertyValue(VAR_NAME, jsonDesc);
		assertEquals(stdtype, other);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.server.DashDataType#equals(java.lang.Object)}
	 * as well as 
	 * {@link gov.noaa.pmel.dashboard.server.DashDataType#DataType(gov.noaa.pmel.dashboard.shared.DataColumnType)},
	 */
	@Test
	public void testHashCodeEquals() {
		DashDataType stdtype = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.equals(null) );
		assertFalse( stdtype.equals(VAR_NAME) );

		DashDataType other = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertTrue( stdtype.hashCode() == other.hashCode() );
		assertTrue( stdtype.equals(other) );

		other = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				"Integer", DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, null, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, null, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, null, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(DISPLAY_NAME, VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, DashboardUtils.NO_UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		DataColumnType another = new DataColumnType(DISPLAY_NAME, VAR_NAME, 
				DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.equals(another) );

		other = new DashDataType(another);
		assertTrue( stdtype.hashCode() == other.hashCode() );
		assertEquals(stdtype, other);
	}


}
