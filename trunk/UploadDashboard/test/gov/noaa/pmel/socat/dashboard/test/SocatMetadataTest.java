/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.nc.SocatMetadata;

import java.util.Date;

import org.junit.Test;

/**
 * Unit test for methods in gov.noaa.pmel.socat.dashboard.nc.SocatMetadata.
 * 
 * @author Karl Smith
 */
public class SocatMetadataTest {

	static final String EXPOCODE = "XXXX20140113";
	static final String CRUISE_NAME = "My Cruise";
	static final String VESSEL_NAME = "My Vessel";
	static final Double WESTMOST_LONGITUDE = -160.0;
	static final Double EASTMOST_LONGITUDE = -135.0;
	static final Double SOUTHMOST_LATITUDE = 15.0;
	static final Double NORTHMOST_LATITUDE = 50.0;
	static final Date BEGIN_TIME = new Date();
	static final Date END_TIME = new Date(BEGIN_TIME.getTime() + 1000000L);
	static final String SCIENCE_GROUP = "My Science Group; Another Science Group";
	static final String ORIGINAL_DOI = "doi:cdiac12345";
	static final String METADATA_HREF = "http://www.socat.info/metadata/xxxx.html";
	static final String SOCAT_DOI = "doi:pangaea12345";
	static final String SOCAT_DOI_HREF = "http://www.socat.info/doi/xxxx20140113.html";
	static final String CRUISE_FLAG = "C";

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getExpocode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getExpocode());
		mdata.setExpocode(EXPOCODE);
		assertEquals(EXPOCODE, mdata.getExpocode());
		mdata.setExpocode(null);
		assertEquals("", mdata.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getCruiseName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setCruiseName(java.lang.String)}.
	 */
	@Test
	public void testGetSetCruiseName() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getCruiseName());
		mdata.setCruiseName(CRUISE_NAME);
		assertEquals(CRUISE_NAME, mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setCruiseName(null);
		assertEquals("", mdata.getCruiseName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getVesselName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setVesselName(java.lang.String)}.
	 */
	@Test
	public void testGetSetVesselName() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getVesselName());
		mdata.setVesselName(VESSEL_NAME);
		assertEquals(VESSEL_NAME, mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setVesselName(null);
		assertEquals("", mdata.getVesselName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getWestmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setWestmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWestmostLongitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertEquals(WESTMOST_LONGITUDE, mdata.getWestmostLongitude());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setWestmostLongitude(null);
		assertTrue( mdata.getWestmostLongitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getEastmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setEastmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEastmostLongitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertEquals(EASTMOST_LONGITUDE, mdata.getEastmostLongitude());
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setEastmostLongitude(null);
		assertTrue( mdata.getEastmostLongitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getSouthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setSouthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSouthmostLatitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertEquals(SOUTHMOST_LATITUDE, mdata.getSouthmostLatitude());
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setSouthmostLatitude(null);
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getNorthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setNorthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNorthmostLatitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertEquals(NORTHMOST_LATITUDE, mdata.getNorthmostLatitude());
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setNorthmostLatitude(null);
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getBeginTime()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setBeginTime(java.util.Date)}.
	 */
	@Test
	public void testSetBeginTime() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		mdata.setBeginTime(BEGIN_TIME);
		assertEquals(BEGIN_TIME, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setBeginTime(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getEndTime()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setEndTime(java.util.Date)}.
	 */
	@Test
	public void testGetSetEndTime() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		mdata.setEndTime(END_TIME);
		assertEquals(END_TIME, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setEndTime(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getScienceGroup()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setScienceGroup(java.lang.String)}.
	 */
	@Test
	public void testGetSetScienceGroup() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getScienceGroup());
		mdata.setScienceGroup(SCIENCE_GROUP);
		assertEquals(SCIENCE_GROUP, mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setScienceGroup(null);
		assertEquals("", mdata.getScienceGroup());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getOrigDOI()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setOrigDOI(java.lang.String)}.
	 */
	@Test
	public void testGetSetOrigDOI() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getOrigDOI());
		mdata.setOrigDOI(ORIGINAL_DOI);
		assertEquals(ORIGINAL_DOI, mdata.getOrigDOI());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setOrigDOI(null);
		assertEquals("", mdata.getOrigDOI());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getMetadataHRef()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setMetadataHRef(java.lang.String)}.
	 */
	@Test
	public void testGetSetMetadataHRef() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getMetadataHRef());
		mdata.setMetadataHRef(METADATA_HREF);
		assertEquals(METADATA_HREF, mdata.getMetadataHRef());
		assertEquals("", mdata.getOrigDOI());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setMetadataHRef(null);
		assertEquals("", mdata.getMetadataHRef());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getSocatDOI()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setSocatDOI(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatDOI() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getSocatDOI());
		mdata.setSocatDOI(SOCAT_DOI);
		assertEquals(SOCAT_DOI, mdata.getSocatDOI());
		assertEquals("", mdata.getMetadataHRef());
		assertEquals("", mdata.getOrigDOI());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setSocatDOI(null);
		assertEquals("", mdata.getSocatDOI());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getSocatDOIHRef()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setSocatDOIHRef(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatDOIHRef() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getSocatDOIHRef());
		mdata.setSocatDOIHRef(SOCAT_DOI_HREF);
		assertEquals(SOCAT_DOI_HREF, mdata.getSocatDOIHRef());
		assertEquals("", mdata.getSocatDOI());
		assertEquals("", mdata.getMetadataHRef());
		assertEquals("", mdata.getOrigDOI());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setSocatDOIHRef(null);
		assertEquals("", mdata.getSocatDOIHRef());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#getCruiseFlag()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#setFlag(java.lang.String)}.
	 */
	@Test
	public void testGetSetFlag() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getCruiseFlag());
		mdata.setCruiseFlag(CRUISE_FLAG);
		assertEquals(CRUISE_FLAG, mdata.getCruiseFlag());
		assertEquals("", mdata.getSocatDOIHRef());
		assertEquals("", mdata.getSocatDOI());
		assertEquals("", mdata.getMetadataHRef());
		assertEquals("", mdata.getOrigDOI());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setCruiseFlag(null);
		assertEquals("", mdata.getCruiseFlag());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatMetadata#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		SocatMetadata mdata = new SocatMetadata();
		assertFalse( mdata.equals(null) );
		assertFalse( mdata.equals(EXPOCODE) );

		SocatMetadata other = new SocatMetadata();
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setExpocode(EXPOCODE);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setExpocode(EXPOCODE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setCruiseName(CRUISE_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setCruiseName(CRUISE_NAME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setVesselName(VESSEL_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setVesselName(VESSEL_NAME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setBeginTime(BEGIN_TIME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setBeginTime(BEGIN_TIME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setEndTime(END_TIME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setEndTime(END_TIME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setScienceGroup(SCIENCE_GROUP);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setScienceGroup(SCIENCE_GROUP);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setOrigDOI(ORIGINAL_DOI);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setOrigDOI(ORIGINAL_DOI);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setMetadataHRef(METADATA_HREF);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setMetadataHRef(METADATA_HREF);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setSocatDOI(SOCAT_DOI);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setSocatDOI(SOCAT_DOI);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setSocatDOIHRef(SOCAT_DOI_HREF);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setSocatDOIHRef(SOCAT_DOI_HREF);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode and equals ignores the cruise cruiseFlag
		mdata.setCruiseFlag(CRUISE_FLAG);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setCruiseFlag(CRUISE_FLAG);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );
	}

}
