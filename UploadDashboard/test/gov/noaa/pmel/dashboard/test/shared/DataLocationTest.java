/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.QCEvent;

import java.util.Date;

import org.junit.Test;

/**
 * Unit tests for DataLocation methods
 * 
 * @author Karl Smith
 */
public class DataLocationTest {

	private static final Integer MY_ROW_NUMBER = 345;
	private static final Date MY_DATA_DATE = new Date(3458139048000L);
	private static final Double MY_LONGITUDE = -179.45;
	private static final Double MY_LATITUDE = -2.65;
	private static final Double MY_DATA_VALUE = 1002.97;
	private static final Double MY_DEPTH = 15.0;

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataLocation#getRowNumber()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataLocation#setRowNumber(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetRowNumber() {
		DataLocation myflag = new DataLocation();
		assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setRowNumber(MY_ROW_NUMBER);
		assertEquals(MY_ROW_NUMBER, myflag.getRowNumber());
		myflag.setRowNumber(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataLocation#getDataDate()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataLocation#setDataDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetDataDate() {
		DataLocation myflag = new DataLocation();
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
		myflag.setDataDate(MY_DATA_DATE);
		assertEquals(MY_DATA_DATE, myflag.getDataDate());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setDataDate(null);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataLocation#getLongitude()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataLocation#setLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLongitude() {
		DataLocation myflag = new DataLocation();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
		myflag.setLongitude(MY_LONGITUDE);
		assertEquals(MY_LONGITUDE, myflag.getLongitude());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setLongitude(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataLocation#getLatitude()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataLocation#setLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLatitude() {
		DataLocation myflag = new DataLocation();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLatitude());
		myflag.setLatitude(MY_LATITUDE);
		assertEquals(MY_LATITUDE, myflag.getLatitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setLatitude(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLatitude());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataLocation#getDepth()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataLocation#setDepth(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDepth() {
		DataLocation myflag = new DataLocation();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getDepth());
		myflag.setDepth(MY_DEPTH);
		assertEquals(MY_DEPTH, myflag.getDepth());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLatitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setDepth(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getDepth());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataLocation#getDataValue()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataLocation#setDataValue(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDataValue() {
		DataLocation myflag = new DataLocation();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getDataValue());
		myflag.setDataValue(MY_DATA_VALUE);
		assertEquals(MY_DATA_VALUE, myflag.getDataValue());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getDepth());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLatitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
		myflag.setDataValue(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getDataValue());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataLocation#hashCode()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataLocation#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		DataLocation myflag = new DataLocation();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(new QCEvent()) );

		DataLocation otherflag = new DataLocation();
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

		myflag.setDepth(MY_DEPTH);
		// latitude is ignored in the hash code
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDepth(MY_DEPTH);
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
