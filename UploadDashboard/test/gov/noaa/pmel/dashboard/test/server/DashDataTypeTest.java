/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.*;
import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * @author ksmith
 *
 */
public class DashDataTypeTest {

	private static final String VAR_NAME = "xCO2";
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
		DashDataType stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( UNITS == stdtype.getUnits() );
		assertEquals(UNITS, stdtype.getUnits());
		assertEquals(CATEGORY_NAME, stdtype.getCategoryName());
		assertEquals(STANDARD_NAME, stdtype.getStandardName());
		assertEquals(DESCRIPTION, stdtype.getDescription());
		assertEquals(DATA_CLASS_NAME, stdtype.getDataClassName());
		assertEquals(VAR_NAME, stdtype.getVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.server.DashDataType)}
	 * and {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.shared.DataColumnType)}.
	 */
	@Test
	public void testTypeNameEquals() {
		DashDataType stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		DashDataType other = new DashDataType(VAR_NAME.toLowerCase(), "Integer", 
				null, null, null, DataColumnType.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
		other = new DashDataType("my_var", DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.typeNameEquals(other) );

		DataColumnType another = new DataColumnType(VAR_NAME.toLowerCase(), 
				"Integer", null, null, null, DataColumnType.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(another) );
		another.setVarName("my_var");
		assertFalse( stdtype.typeNameEquals(another) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#typeEquals(gov.noaa.pmel.dashboard.server.DashDataType)}
	 * and {@link gov.noaa.pmel.dashboard.server.DashDataType#typeEquals(gov.noaa.pmel.dashboard.shared.DataColumnType)}.
	 */
	@Test
	public void testTypeEquals() {
		DashDataType stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		DashDataType other = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				null, null, null, DataColumnType.NO_UNITS);
		assertTrue( stdtype.typeEquals(other) );
		other = new DashDataType(VAR_NAME.toLowerCase(), DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.typeEquals(other) );
		other = new DashDataType(VAR_NAME, DATA_CLASS_NAME.toLowerCase(), 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.typeEquals(other) );

		DataColumnType another = new DataColumnType(VAR_NAME.toLowerCase(), 
				DATA_CLASS_NAME, null, null, null, DataColumnType.NO_UNITS);
		assertFalse( stdtype.typeEquals(another) );
		another.setVarName(VAR_NAME);
		assertTrue( stdtype.typeEquals(another) );
		another.setDataClassName(DATA_CLASS_NAME.toLowerCase());
		assertFalse( stdtype.typeEquals(another) );
	}


	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.shared.DataColumnType)}.
	 */
	@Test
	public void testTypeEqualsDataColumnType() {
		DashDataType stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		DataColumnType other = new DataColumnType(VAR_NAME, "Integer", 
				null, null, null, DataColumnType.NO_UNITS);
		assertTrue( stdtype.typeNameEquals(other) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#duplicate()}.
	 */
	@Test
	public void testDuplicate() {
		DashDataType stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		DataColumnType dup = stdtype.duplicate();
		DataColumnType other = new DataColumnType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertEquals(other, dup);
		assertFalse( dup.getUnits() == stdtype.getUnits() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#toPropertyValue()}
	 * and {@link gov.noaa.pmel.dashboard.server.DashDataType#fromPropertyValue(java.lang.String,java.lang.String)}.
	 */
	@Test
	public void testToFromPropertyValue() {
		DashDataType stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		String jsonDesc = stdtype.toPropertyValue();
		assertTrue( jsonDesc.contains("dataClassName") );
		assertTrue( jsonDesc.contains(DATA_CLASS_NAME) );
		assertTrue( jsonDesc.contains("description") );
		assertTrue( jsonDesc.contains(DESCRIPTION) );
		assertTrue( jsonDesc.contains("standardName") );
		assertTrue( jsonDesc.contains(STANDARD_NAME) );
		assertTrue( jsonDesc.contains("categoryName") );
		assertTrue( jsonDesc.contains(CATEGORY_NAME) );
		assertTrue( jsonDesc.contains("units") );
		for ( String val : UNITS )
			assertTrue( jsonDesc.contains(val) );

		DashDataType other = DashDataType.fromPropertyValue(VAR_NAME, jsonDesc);
		assertEquals(stdtype, other);

		stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, null, null, null, null);
		jsonDesc = stdtype.toPropertyValue();
		assertTrue( jsonDesc.contains("dataClassName") );
		assertTrue( jsonDesc.contains(DATA_CLASS_NAME) );
		assertFalse( jsonDesc.contains("description") );
		assertFalse( jsonDesc.contains("standardName") );
		assertFalse( jsonDesc.contains("categoryName") );
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
		DashDataType stdtype = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.equals(null) );
		assertFalse( stdtype.equals(VAR_NAME) );

		DashDataType other = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertTrue( stdtype.hashCode() == other.hashCode() );
		assertTrue( stdtype.equals(other) );

		other = new DashDataType(VAR_NAME, "Integer", 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				null, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, null, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, null, UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		other = new DashDataType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, DataColumnType.NO_UNITS);
		assertFalse( stdtype.hashCode() == other.hashCode() );
		assertFalse( stdtype.equals(other) );

		DataColumnType another = new DataColumnType(VAR_NAME, DATA_CLASS_NAME, 
				DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
		assertFalse( stdtype.equals(another) );

		other = new DashDataType(another);
		assertTrue( stdtype.hashCode() == other.hashCode() );
		assertEquals(stdtype, other);
	}


}
