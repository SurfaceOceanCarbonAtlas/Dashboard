/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * Unit test for methods of SocatWoceFlag
 * 
 * @author Karl Smith
 */
public class SocatWoceFlagTest {

	private static final Character MY_QC_FLAG = '3';
	private static final String MY_EXPOCODE = "26NA20140427";
	private static final Double MY_SOCAT_VERSION = 3.0;
	private static final Character MY_REGION_ID = 'T';
	private static final Integer MY_ROW_NUMBER = 345;
	private static final Double MY_LONGITUDE = -179.45;
	private static final Double MY_LATITUDE = -2.65;
	private static final Date MY_DATA_DATE;
	static {
		try {
			MY_DATA_DATE = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")).parse("1998-06-17 23:33:52 UTC");
		} catch (ParseException ex) {
			throw new RuntimeException("unexpected error generating a date");
		}
	}
	private static final DataColumnType MY_DATA_TYPE = DataColumnType.SEA_LEVEL_PRESSURE;
	private static final String MY_COLUMN_NAME = "P_atm";
	private static final Double MY_DATA_VALUE = 1002.97;
	private static final Date MY_FLAG_DATE = new Date();
	private static final String MY_USERNAME = "Karl.Smith";
	private static final String MY_REALNAME = "Karl M. Smith";
	private static final String MY_COMMENT = "from SocatWoceFlag unit test";

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#getRowNumber()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#setRowNumber(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetRowNumber() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setRowNumber(MY_ROW_NUMBER);
		assertEquals(MY_ROW_NUMBER, myflag.getRowNumber());
		assertEquals("", myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setRowNumber(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#getLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#setLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLongitude() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		myflag.setLongitude(MY_LONGITUDE);
		assertEquals(MY_LONGITUDE, myflag.getLongitude());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals("", myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setLongitude(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#getLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#setLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLatitude() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
		myflag.setLatitude(MY_LATITUDE);
		assertEquals(MY_LATITUDE, myflag.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals("", myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setLatitude(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#getDataDate()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#setDataDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetDataDate() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		myflag.setDataDate(MY_DATA_DATE);
		assertEquals(MY_DATA_DATE, myflag.getDataDate());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals("", myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setDataDate(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#getDataType()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#setDataType(gov.noaa.pmel.socat.dashboard.shared.DataColumnType)}.
	 */
	@Test
	public void testGetSetDataType() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertEquals(DataColumnType.UNKNOWN, myflag.getDataType());
		myflag.setDataType(MY_DATA_TYPE);
		assertEquals(MY_DATA_TYPE, myflag.getDataType());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals("", myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setDataType(null);
		assertEquals(DataColumnType.UNKNOWN, myflag.getDataType());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#getColumnName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#setColumnName(java.lang.String)}.
	 */
	@Test
	public void testGetSetColumnName() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertEquals("", myflag.getColumnName());
		myflag.setColumnName(MY_COLUMN_NAME);
		assertEquals(MY_COLUMN_NAME, myflag.getColumnName());
		assertEquals(DataColumnType.UNKNOWN, myflag.getDataType());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals("", myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setColumnName(null);
		assertEquals("", myflag.getColumnName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#getDataValue()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#setDataValue(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDataValue() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getDataValue());
		myflag.setDataValue(MY_DATA_VALUE);
		assertEquals(MY_DATA_VALUE, myflag.getDataValue());
		assertEquals("", myflag.getColumnName());
		assertEquals(DataColumnType.UNKNOWN, myflag.getDataType());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals("", myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setDataValue(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getDataValue());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		SocatWoceFlag myflag = new SocatWoceFlag();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(new SocatQCFlag()) );

		SocatWoceFlag otherflag = new SocatWoceFlag();
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setFlag(MY_QC_FLAG);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setFlag(MY_QC_FLAG);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setExpocode(MY_EXPOCODE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setExpocode(MY_EXPOCODE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setSocatVersion(MY_SOCAT_VERSION);
		// socatVersion is ignored in the hash code
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setSocatVersion(MY_SOCAT_VERSION);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setRegionID(MY_REGION_ID);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setRegionID(MY_REGION_ID);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setFlagDate(MY_FLAG_DATE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setFlagDate(MY_FLAG_DATE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setUsername(MY_USERNAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setUsername(MY_USERNAME);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setRealname(MY_REALNAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setRealname(MY_REALNAME);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setComment(MY_COMMENT);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setComment(MY_COMMENT);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setRowNumber(MY_ROW_NUMBER);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setRowNumber(MY_ROW_NUMBER);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setLongitude(MY_LONGITUDE);
		// longitude is ignored in the hash code
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setLongitude(MY_LONGITUDE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setLatitude(MY_LATITUDE);
		// latitude is ignored in the hash code
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setLatitude(MY_LATITUDE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setDataDate(MY_DATA_DATE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDataDate(MY_DATA_DATE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setDataType(MY_DATA_TYPE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDataType(MY_DATA_TYPE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setColumnName(MY_COLUMN_NAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setColumnName(MY_COLUMN_NAME);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setDataValue(MY_DATA_VALUE);
		// dataValue is ignored in the hash code
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDataValue(MY_DATA_VALUE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );
	}

}
