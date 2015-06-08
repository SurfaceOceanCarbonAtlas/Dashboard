/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.util.Date;

import org.junit.Test;

/**
 * Unit tests for DataLocation methods
 * 
 * @author Karl Smith
 */
public class DataLocationTest {

	private static final Character MY_REGION_ID = 'T';
	private static final Integer MY_ROW_NUMBER = 345;
	private static final Date MY_DATA_DATE = new Date(3458139048000L);
	private static final Double MY_LONGITUDE = -179.45;
	private static final Double MY_LATITUDE = -2.65;
	private static final Double MY_DATA_VALUE = 1002.97;

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#getRegionID()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#setRegionID(java.lang.Character)}.
	 */
	@Test
	public void testGetSetRegionID() {
		DataLocation myflag = new DataLocation();
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		myflag.setRegionID(MY_REGION_ID);
		assertEquals(MY_REGION_ID, myflag.getRegionID());
		myflag.setRegionID(null);
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#getRowNumber()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#setRowNumber(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetRowNumber() {
		DataLocation myflag = new DataLocation();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setRowNumber(MY_ROW_NUMBER);
		assertEquals(MY_ROW_NUMBER, myflag.getRowNumber());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		myflag.setRowNumber(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#getDataDate()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#setDataDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetDataDate() {
		DataLocation myflag = new DataLocation();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		myflag.setDataDate(MY_DATA_DATE);
		assertEquals(MY_DATA_DATE, myflag.getDataDate());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		myflag.setDataDate(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#getLongitude()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#setLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLongitude() {
		DataLocation myflag = new DataLocation();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		myflag.setLongitude(MY_LONGITUDE);
		assertEquals(MY_LONGITUDE, myflag.getLongitude());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		myflag.setLongitude(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#getLatitude()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#setLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLatitude() {
		DataLocation myflag = new DataLocation();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
		myflag.setLatitude(MY_LATITUDE);
		assertEquals(MY_LATITUDE, myflag.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		myflag.setLatitude(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#getDataValue()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#setDataValue(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDataValue() {
		DataLocation myflag = new DataLocation();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getDataValue());
		myflag.setDataValue(MY_DATA_VALUE);
		assertEquals(MY_DATA_VALUE, myflag.getDataValue());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, myflag.getRowNumber());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		myflag.setDataValue(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, myflag.getDataValue());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#hashCode()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DataLocation#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		DataLocation myflag = new DataLocation();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(new SocatQCEvent()) );

		DataLocation otherflag = new DataLocation();
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setRegionID(MY_REGION_ID);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setRegionID(MY_REGION_ID);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setRowNumber(MY_ROW_NUMBER);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setRowNumber(MY_ROW_NUMBER);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setDataDate(MY_DATA_DATE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDataDate(MY_DATA_DATE);
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

		myflag.setDataValue(MY_DATA_VALUE);
		// dataValue is ignored in the hash code
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDataValue(MY_DATA_VALUE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );
	}

}
