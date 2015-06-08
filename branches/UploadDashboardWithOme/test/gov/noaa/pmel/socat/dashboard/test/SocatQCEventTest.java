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
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.util.Date;

import org.junit.Test;

/**
 * Tests for methods in SocatQCEvent
 * @author Karl Smith
 */
public class SocatQCEventTest {

	private static final Long DEFAULT_QC_ID = 0L;
	private static final Long MY_QC_ID = 123L;
	private static final Character MY_QC_FLAG = 'B';
	private static final String MY_EXPOCODE = "26NA20140427";
	private static final String MY_SOCAT_VERSION = "3.0";
	private static final Character MY_REGION_ID = 'T';
	private static final Date MY_FLAG_DATE = new Date();
	private static final String MY_USERNAME = "Karl.Smith";
	private static final String MY_REALNAME = "Karl M. Smith";
	private static final String MY_COMMENT = "from SocatQCEvent unit test";

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getID()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setId(java.lang.Long)}.
	 */
	@Test
	public void testGetSetId() {
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setId(MY_QC_ID);
		assertEquals(MY_QC_ID, myflag.getId());
		myflag.setId(null);
		assertEquals(DEFAULT_QC_ID, myflag.getId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent#getFlag()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent#setFlag(java.lang.String)}.
	 */
	@Test
	public void testGetSetFlag() {
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
		myflag.setFlag(MY_QC_FLAG);
		assertEquals(MY_QC_FLAG, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlag(null);
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getExpocode()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals("", myflag.getExpocode());
		myflag.setExpocode(MY_EXPOCODE);
		assertEquals(MY_EXPOCODE, myflag.getExpocode());
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
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
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals("", myflag.getSocatVersion());
		myflag.setSocatVersion(MY_SOCAT_VERSION);
		assertEquals(MY_SOCAT_VERSION, myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setSocatVersion(null);
		assertEquals("", myflag.getSocatVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent#getRegionID()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent#setRegionID(java.lang.String)}.
	 */
	@Test
	public void testGetSetRegionID() {
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		myflag.setRegionID(MY_REGION_ID);
		assertEquals(MY_REGION_ID, myflag.getRegionID());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setRegionID(null);
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#getFlagDate()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatEvent#setFlagDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetFlagDate() {
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		myflag.setFlagDate(MY_FLAG_DATE);
		assertEquals(MY_FLAG_DATE, myflag.getFlagDate());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
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
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals("", myflag.getUsername());
		myflag.setUsername(MY_USERNAME);
		assertEquals(MY_USERNAME, myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
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
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals("", myflag.getRealname());
		myflag.setRealname(MY_REALNAME);
		assertEquals(MY_REALNAME, myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
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
		SocatQCEvent myflag = new SocatQCEvent();
		assertEquals("", myflag.getComment());
		myflag.setComment(MY_COMMENT);
		assertEquals(MY_COMMENT, myflag.getComment());
		assertEquals("", myflag.getRealname());
		assertEquals("", myflag.getUsername());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DataLocation.GLOBAL_REGION_ID, myflag.getRegionID());
		assertEquals("", myflag.getSocatVersion());
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatQCEvent.QC_COMMENT, myflag.getFlag());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setComment(null);
		assertEquals("", myflag.getComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		SocatQCEvent myflag = new SocatQCEvent();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(new SocatEvent()) );

		SocatQCEvent otherflag = new SocatQCEvent();
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		// ID ignored for hashCode and equals
		myflag.setId(MY_QC_ID);
		assertFalse( myflag.getId().equals(otherflag.getId()) );
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
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
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
	}

}
