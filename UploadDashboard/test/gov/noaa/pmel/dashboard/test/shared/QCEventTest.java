/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCEvent;

/**
 * Tests for methods in QCEvent
 * @author Karl Smith
 */
public class QCEventTest {

	private static final Long DEFAULT_QC_ID = 0L;
	private static final Long MY_QC_ID = 123L;
	private static final Date MY_FLAGDATE = new Date();
	private static final String MY_FLAGNAME = "QC_O2";
	private static final Character MY_FLAGVALUE = 'B';
	private static final String MY_DATASETID = "26NA20140427";
	private static final String MY_VERSION = "3.0";
	private static final String MY_USERNAME = "Karl.Smith";
	private static final String MY_REALNAME = "Karl M. Smith";
	private static final String MY_COMMENT = "from QCEvent unit test";

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getId()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setId(java.lang.Long)}.
	 */
	@Test
	public void testGetSetId() {
		QCEvent myflag = new QCEvent();
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setId(MY_QC_ID);
		assertEquals(MY_QC_ID, myflag.getId());
		myflag.setId(null);
		assertEquals(DEFAULT_QC_ID, myflag.getId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getFlagDate()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setFlagDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetFlagDate() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		myflag.setFlagDate(MY_FLAGDATE);
		assertEquals(MY_FLAGDATE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlagDate(null);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getFlagName()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setFlagName(java.lang.String)}.
	 */
	@Test
	public void testGetSetFlagName() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		myflag.setFlagName(MY_FLAGNAME);
		assertEquals(MY_FLAGNAME, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlagName(null);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getFlagValue()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setFlagValue(java.lang.Character)}.
	 */
	@Test
	public void testGetSetFlagValue() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		myflag.setFlagValue(MY_FLAGVALUE);
		assertEquals(MY_FLAGVALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlagValue(null);
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getDatasetId()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setDatasetId(java.lang.String)}.
	 */
	@Test
	public void testGetSetDatasetId() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
		myflag.setDatasetId(MY_DATASETID);
		assertEquals(MY_DATASETID, myflag.getDatasetId());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setDatasetId(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getVersion()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setVersion(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatVersion() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		myflag.setVersion(MY_VERSION);
		assertEquals(MY_VERSION, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setVersion(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getUsername()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setUsername(java.lang.String)}.
	 */
	@Test
	public void testGetSetUserName() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		myflag.setUsername(MY_USERNAME);
		assertEquals(MY_USERNAME, myflag.getUsername());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setUsername(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getRealname()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setRealname(java.lang.String)}.
	 */
	@Test
	public void testGetSetRealName() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
		myflag.setRealname(MY_REALNAME);
		assertEquals(MY_REALNAME, myflag.getRealname());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setRealname(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getComment()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setComment(java.lang.String)}.
	 */
	@Test
	public void testGetSetComment() {
		QCEvent myflag = new QCEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
		myflag.setComment(MY_COMMENT);
		assertEquals(MY_COMMENT, myflag.getComment());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, myflag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setComment(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		QCEvent myflag = new QCEvent();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(MY_FLAGVALUE) );

		QCEvent otherflag = new QCEvent();
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
	}

}
