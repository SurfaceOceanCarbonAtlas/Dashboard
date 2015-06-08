/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

/**
 * Unit test for methods of SocatWoceFlag
 * 
 * @author Karl Smith
 */
public class SocatWoceEventTest {

	private static final Long DEFAULT_QC_ID = 0L;
	private static final Long MY_QC_ID = 123L;
	private static final Character MY_WOCE_FLAG = '3';
	private static final String MY_EXPOCODE = "26NA20140427";
	private static final String MY_SOCAT_VERSION = "3.0";
	private static final String MY_DATA_VAR_NAME = "Pressure_atm";
	private static final ArrayList<DataLocation> MY_LOCATIONS;
	static {
		MY_LOCATIONS = new ArrayList<DataLocation>(2);
		DataLocation loc = new DataLocation();
		loc.setRegionID('T');
		loc.setRowNumber(345);
		loc.setDataDate(new Date(3458139048000L));
		loc.setLongitude(-179.5);
		loc.setLatitude(3.5);
		loc.setDataValue(1105.450);
		MY_LOCATIONS.add(loc);
		loc = new DataLocation();
		loc.setRegionID('T');
		loc.setRowNumber(346);
		loc.setDataDate(new Date(3458139203000L));
		loc.setLongitude(-179.6);
		loc.setLatitude(3.4);
		loc.setDataValue(1105.453);
		MY_LOCATIONS.add(loc);
	}
	private static final Date MY_FLAG_DATE = new Date();
	private static final String MY_USERNAME = "Karl.Smith";
	private static final String MY_REALNAME = "Karl M. Smith";
	private static final String MY_COMMENT = "from SocatWoceEvent unit test";

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getID()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setId(java.lang.Long)}.
	 */
	@Test
	public void testGetSetId() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setId(MY_QC_ID);
		assertEquals(MY_QC_ID, myflag.getId());
		myflag.setId(null);
		assertEquals(DEFAULT_QC_ID, myflag.getId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#getFlag()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#setFlag(java.lang.String)}.
	 */
	@Test
	public void testGetSetFlag() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		myflag.setFlag(MY_WOCE_FLAG);
		assertEquals(MY_WOCE_FLAG, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlag(null);
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getExpocode()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals("", myflag.getExpocode());
		myflag.setExpocode(MY_EXPOCODE);
		assertEquals(MY_EXPOCODE, myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setExpocode(null);
		assertEquals("", myflag.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getSocatVersion()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setSocatVersion(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSocatVersion() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals("", myflag.getSocatVersion());
		myflag.setSocatVersion(MY_SOCAT_VERSION);
		assertEquals(MY_SOCAT_VERSION, myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setSocatVersion(null);
		assertEquals("", myflag.getSocatVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#getDataVarName()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#setDataVarName(java.lang.String)}.
	 */
	@Test
	public void testGetSetColumnName() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals("", myflag.getDataVarName());
		myflag.setDataVarName(MY_DATA_VAR_NAME);
		assertEquals(MY_DATA_VAR_NAME, myflag.getDataVarName());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setDataVarName(null);
		assertEquals("", myflag.getDataVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#getLocations()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#setLocations(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetLocations() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals(0, myflag.getLocations().size());
		myflag.setLocations(MY_LOCATIONS);
		assertEquals(MY_LOCATIONS, myflag.getLocations());
		assertEquals("", myflag.getDataVarName());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setLocations(null);
		assertEquals(0, myflag.getLocations().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getFlagDate()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setFlagDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetFlagDate() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		myflag.setFlagDate(MY_FLAG_DATE);
		assertEquals(MY_FLAG_DATE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals("", myflag.getDataVarName());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlagDate(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getUsername()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setUsername(java.lang.String)}.
	 */
	@Test
	public void testGetSetUsername() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals("", myflag.getUsername());
		myflag.setUsername(MY_USERNAME);
		assertEquals(MY_USERNAME, myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals("", myflag.getDataVarName());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setUsername(null);
		assertEquals("", myflag.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getRealname()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setRealname(java.lang.String)}.
	 */
	@Test
	public void testGetSetRealname() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals("", myflag.getRealname());
		myflag.setRealname(MY_REALNAME);
		assertEquals(MY_REALNAME, myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals("", myflag.getDataVarName());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setRealname(null);
		assertEquals("", myflag.getRealname());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getComment()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setComment(java.lang.String)}.
	 */
	@Test
	public void testGetSetComment() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertEquals("", myflag.getComment());
		myflag.setComment(MY_COMMENT);
		assertEquals(MY_COMMENT, myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals("", myflag.getDataVarName());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatWoceEvent.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setComment(null);
		assertEquals("", myflag.getComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		SocatWoceEvent myflag = new SocatWoceEvent();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(new SocatEvent()) );

		SocatWoceEvent otherflag = new SocatWoceEvent();
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		// ID ignored for hashCode and equals
		myflag.setId(MY_QC_ID);
		assertFalse( myflag.getId().equals(otherflag.getId()) );
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setFlag(MY_WOCE_FLAG);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setFlag(MY_WOCE_FLAG);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setExpocode(MY_EXPOCODE);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setExpocode(MY_EXPOCODE);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setSocatVersion(MY_SOCAT_VERSION);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setSocatVersion(MY_SOCAT_VERSION);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setDataVarName(MY_DATA_VAR_NAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setDataVarName(MY_DATA_VAR_NAME);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setLocations(MY_LOCATIONS);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setLocations(MY_LOCATIONS);
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
	}

}
