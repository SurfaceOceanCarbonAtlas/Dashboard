/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.DataQCEvent;
import gov.noaa.pmel.dashboard.shared.QCEvent;

/**
 * Unit test for methods of {@link gov.noaa.pmel.dashboard.shared.DataQCEvent}.
 * 
 * @author Karl Smith
 */
public class DataQCEventTest {

	private static final Long DEFAULT_QC_ID = 0L;
	private static final Long MY_QC_ID = 123L;
	private static final Date MY_FLAGDATE = new Date();
	private static final String MY_FLAGNAME = "QC_O2";
	private static final Character MY_FLAGVALUE = 'B';
	private static final String MY_DATASETID = "26NA20140427";
	private static final String MY_VERSION = "3.0";
	private static final String MY_USERNAME = "Karl.Smith";
	private static final String MY_REALNAME = "Karl M. Smith";
	private static final String MY_COMMENT = "from DataQCEvent unit test";
	private static final String MY_VARNAME = "O2";
	private static final ArrayList<DataLocation> MY_LOCATIONS;
	static {
		MY_LOCATIONS = new ArrayList<DataLocation>(2);
		DataLocation loc = new DataLocation();
		loc.setRowNumber(345);
		loc.setDataDate(new Date(3458139048000L));
		loc.setLongitude(-179.5);
		loc.setLatitude(3.5);
		loc.setDepth(15.0);
		loc.setDataValue(1105.450);
		MY_LOCATIONS.add(loc);
		loc = new DataLocation();
		loc.setRowNumber(346);
		loc.setDataDate(new Date(3458139203000L));
		loc.setLongitude(-179.6);
		loc.setLatitude(3.4);
		loc.setDepth(25.0);
		loc.setDataValue(1105.453);
		MY_LOCATIONS.add(loc);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataQCEvent#getVarName()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataQCEvent#setVarName(java.lang.String)}.
	 */
	@Test
	public void testGetSetVarName() {
		DataQCEvent myflag = new DataQCEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		myflag.setVarName(MY_VARNAME);
		assertEquals(MY_VARNAME, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setVarName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataQCEvent#getLocations()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DataQCEvent#setLocations(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetLocations() {
		DataQCEvent myflag = new DataQCEvent();
		assertEquals(0, myflag.getLocations().size());
		myflag.setLocations(MY_LOCATIONS);
		assertEquals(MY_LOCATIONS, myflag.getLocations());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setLocations(null);
		assertEquals(0, myflag.getLocations().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DataQCEvent#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DataQCEvent#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		DataQCEvent myflag = new DataQCEvent();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(new QCEvent()) );

		DataQCEvent otherflag = new DataQCEvent();
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		// ID ignored for hashCode and equals
		myflag.setId(MY_QC_ID);
		assertFalse( myflag.getId().equals(otherflag.getId()) );
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setFlagDate(MY_FLAGDATE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setFlagDate(MY_FLAGDATE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setFlagName(MY_FLAGNAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setFlagName(MY_FLAGNAME);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setFlagValue(MY_FLAGVALUE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setFlagValue(MY_FLAGVALUE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setDatasetId(MY_DATASETID);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDatasetId(MY_DATASETID);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setVersion(MY_VERSION);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setVersion(MY_VERSION);
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

		myflag.setVarName(MY_VARNAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setVarName(MY_VARNAME);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setLocations(MY_LOCATIONS);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setLocations(MY_LOCATIONS);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );
	}

}
