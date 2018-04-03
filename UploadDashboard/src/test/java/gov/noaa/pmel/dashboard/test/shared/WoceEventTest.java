/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.shared.DashboardEvent;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

/**
 * Unit test for methods of SocatWoceFlag
 * 
 * @author Karl Smith
 */
public class WoceEventTest {

	private static final Long DEFAULT_QC_ID = 0L;
	private static final Long MY_QC_ID = 123L;
	private static final String MY_WOCE_NAME = "WOCE_PO4_water";
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
	private static final String MY_COMMENT = "from WoceEvent unit test";

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getID()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setId(java.lang.Long)}.
	 */
	@Test
	public void testGetSetId() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setId(MY_QC_ID);
		assertEquals(MY_QC_ID, myflag.getId());
		myflag.setId(null);
		assertEquals(DEFAULT_QC_ID, myflag.getId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceEvent#getWoceName()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.WoceEvent#setWoceName(java.lang.String)}.
	 */
	@Test
	public void testGetSetWoceName() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		myflag.setWoceName(MY_WOCE_NAME);
		assertEquals(MY_WOCE_NAME, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setWoceName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceEvent#getFlag()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.WoceEvent#setFlag(java.lang.Character)}.
	 */
	@Test
	public void testGetSetFlag() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		myflag.setFlag(MY_WOCE_FLAG);
		assertEquals(MY_WOCE_FLAG, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlag(null);
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getExpocode()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		myflag.setExpocode(MY_EXPOCODE);
		assertEquals(MY_EXPOCODE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setExpocode(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getVersion()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setVersion(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSocatVersion() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		myflag.setVersion(MY_SOCAT_VERSION);
		assertEquals(MY_SOCAT_VERSION, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setVersion(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceEvent#getVarName()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.WoceEvent#setVarName(java.lang.String)}.
	 */
	@Test
	public void testGetSetColumnName() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		myflag.setVarName(MY_DATA_VAR_NAME);
		assertEquals(MY_DATA_VAR_NAME, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setVarName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceEvent#getLocations()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.WoceEvent#setLocations(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetLocations() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(0, myflag.getLocations().size());
		myflag.setLocations(MY_LOCATIONS);
		assertEquals(MY_LOCATIONS, myflag.getLocations());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setLocations(null);
		assertEquals(0, myflag.getLocations().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getFlagDate()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setFlagDate(java.util.Date)}.
	 */
	@Test
	public void testGetSetFlagDate() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		myflag.setFlagDate(MY_FLAG_DATE);
		assertEquals(MY_FLAG_DATE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setFlagDate(null);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getUsername()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setUsername(java.lang.String)}.
	 */
	@Test
	public void testGetSetUsername() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		myflag.setUsername(MY_USERNAME);
		assertEquals(MY_USERNAME, myflag.getUsername());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setUsername(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getRealname()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setRealname(java.lang.String)}.
	 */
	@Test
	public void testGetSetRealname() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
		myflag.setRealname(MY_REALNAME);
		assertEquals(MY_REALNAME, myflag.getRealname());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setRealname(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getComment()} 
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setComment(java.lang.String)}.
	 */
	@Test
	public void testGetSetComment() {
		WoceEvent myflag = new WoceEvent();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
		myflag.setComment(MY_COMMENT);
		assertEquals(MY_COMMENT, myflag.getComment());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
		assertEquals(0, myflag.getLocations().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
		assertEquals(DashboardUtils.WOCE_NOT_CHECKED, myflag.getFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getWoceName());
		assertEquals(DEFAULT_QC_ID, myflag.getId());
		myflag.setComment(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceEvent#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.WoceEvent#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		WoceEvent myflag = new WoceEvent();
		assertFalse( myflag.equals(null) );
		assertFalse( myflag.equals(new DashboardEvent()) );

		WoceEvent otherflag = new WoceEvent();
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		// ID ignored for hashCode and equals
		myflag.setId(MY_QC_ID);
		assertFalse( myflag.getId().equals(otherflag.getId()) );
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setWoceName(MY_WOCE_NAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setWoceName(MY_WOCE_NAME);
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

		myflag.setVersion(MY_SOCAT_VERSION);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setVersion(MY_SOCAT_VERSION);
		assertTrue( myflag.hashCode() == otherflag.hashCode() );
		assertTrue( myflag.equals(otherflag) );

		myflag.setVarName(MY_DATA_VAR_NAME);
		assertFalse( myflag.hashCode() == otherflag.hashCode() );
		assertFalse( myflag.equals(otherflag) );
		otherflag.setVarName(MY_DATA_VAR_NAME);
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
