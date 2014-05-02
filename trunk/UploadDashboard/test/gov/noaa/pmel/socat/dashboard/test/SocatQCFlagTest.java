/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag;

import java.util.Date;

import org.junit.Test;

/**
 * Tests for methods in SocatQCFlag
 * @author Karl Smith
 */
public class SocatQCFlagTest {

	private static final Character MY_QC_FLAG = 'B';
	private static final String MY_EXPOCODE = "26NA20140427";
	private static final Double MY_SOCAT_VERSION = 3.0;
	private static final Character MY_REGION_ID = 'T';
	private static final Date MY_FLAG_DATE = new Date();
	private static final String MY_REVIEWER = "Karl.Smith";
	private static final String MY_COMMENT = "from SocatQCFlag unit test";

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#getQcFlag()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#setQcFlag(java.lang.String)}.
	 */
	@Test
	public void testGetSetQcFlag() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setFlag(MY_QC_FLAG);
		assertEquals(MY_QC_FLAG, myflag.getFlag());
		myflag.setFlag(null);
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#getExpocode()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertEquals("", myflag.getExpocode());
		myflag.setExpocode(MY_EXPOCODE);
		assertEquals(MY_EXPOCODE, myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setExpocode(null);
		assertEquals("", myflag.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#getSocatVersion()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#setSocatVersion(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSocatVersion() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		myflag.setSocatVersion(MY_SOCAT_VERSION);
		assertEquals(MY_SOCAT_VERSION, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setSocatVersion(null);
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#getRegionID()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#setRegionID(java.lang.String)}.
	 */
	@Test
	public void testGetSetRegionID() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		myflag.setRegionID(MY_REGION_ID);
		assertEquals(MY_REGION_ID, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setRegionID(null);
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#getFlagDate()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#setFlagDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetFlagDate() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		myflag.setFlagDate(MY_FLAG_DATE);
		assertEquals(MY_FLAG_DATE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setFlagDate(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#getReviewer()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#setReviewer(java.lang.String)}.
	 */
	@Test
	public void testGetSetReviewer() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertEquals("", myflag.getReviewer());
		myflag.setReviewer(MY_REVIEWER);
		assertEquals(MY_REVIEWER, myflag.getReviewer());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setReviewer(null);
		assertEquals("", myflag.getReviewer());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#getComment()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#setComment(java.lang.String)}.
	 */
	@Test
	public void testGetSetComment() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertEquals("", myflag.getComment());
		myflag.setComment(MY_COMMENT);
		assertEquals(MY_COMMENT, myflag.getComment());
		assertEquals("", myflag.getReviewer());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getRegionID());
		assertEquals(0.0, myflag.getSocatVersion(), 1.0E-6);
		assertEquals("", myflag.getExpocode());
		assertEquals(SocatCruiseData.CHAR_MISSING_VALUE, myflag.getFlag());
		myflag.setComment(null);
		assertEquals("", myflag.getComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		SocatQCFlag myflag = new SocatQCFlag();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(MY_QC_FLAG) );
		assertTrue( myflag.equals(new SocatWoceFlag()) );

		SocatQCFlag otherflag = new SocatQCFlag();
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

		myflag.setReviewer(MY_REVIEWER);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setReviewer(MY_REVIEWER);
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
