/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

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
	static final String ORGANIZATION_NAME = "PMEL/NOAA";
	static final Double WESTMOST_LONGITUDE = -160.0;
	static final Double EASTMOST_LONGITUDE = -135.0;
	static final Double SOUTHMOST_LATITUDE = 15.0;
	static final Double NORTHMOST_LATITUDE = 50.0;
	static final Date BEGIN_TIME = new Date();
	static final Date END_TIME = new Date(BEGIN_TIME.getTime() + 1000000L);
	static final String SCIENCE_GROUP = "My Science Group : Another Science Group";
	static final String ORIGINAL_DATA_REF = "doi:cdiac12345";
	static final String ADDL_DOCS = "MySupplementalDoc1.doc : MySupplementalDoc2.pdf";
	static final String SOCAT_DOI = "doi:pangaea12345";
	static final String SOCAT_DOI_HREF = "http://www.socat.info/doi/xxxx20140113.html";
	static final String SOCAT_VERSION = "3.0";
	static final String CRUISE_FLAG = "C";

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getExpocode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setExpocode(java.lang.String)}.
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
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getCruiseName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setCruiseName(java.lang.String)}.
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
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getVesselName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setVesselName(java.lang.String)}.
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
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getOrganization()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setOrganization(java.lang.String)}.
	 */
	@Test
	public void testGetSetOrganization() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getOrganization());
		mdata.setOrganization(ORGANIZATION_NAME);
		assertEquals(ORGANIZATION_NAME, mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setOrganization(null);
		assertEquals("", mdata.getOrganization());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getWestmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setWestmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWestmostLongitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertEquals(WESTMOST_LONGITUDE, mdata.getWestmostLongitude());
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setWestmostLongitude(null);
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getEastmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setEastmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEastmostLongitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertEquals(EASTMOST_LONGITUDE, mdata.getEastmostLongitude());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setEastmostLongitude(null);
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getSouthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setSouthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSouthmostLatitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertEquals(SOUTHMOST_LATITUDE, mdata.getSouthmostLatitude());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setSouthmostLatitude(null);
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getNorthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setNorthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNorthmostLatitude() {
		SocatMetadata mdata = new SocatMetadata();
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertEquals(NORTHMOST_LATITUDE, mdata.getNorthmostLatitude());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setNorthmostLatitude(null);
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getBeginTime()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setBeginTime(java.util.Date)}.
	 */
	@Test
	public void testSetBeginTime() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		mdata.setBeginTime(BEGIN_TIME);
		assertEquals(BEGIN_TIME, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setBeginTime(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getEndTime()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setEndTime(java.util.Date)}.
	 */
	@Test
	public void testGetSetEndTime() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		mdata.setEndTime(END_TIME);
		assertEquals(END_TIME, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setEndTime(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getScienceGroup()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setScienceGroup(java.lang.String)}.
	 */
	@Test
	public void testGetSetScienceGroup() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getScienceGroup());
		mdata.setScienceGroup(SCIENCE_GROUP);
		assertEquals(SCIENCE_GROUP, mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setScienceGroup(null);
		assertEquals("", mdata.getScienceGroup());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getOrigDataRef()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setOrigDataRef(java.lang.String)}.
	 */
	@Test
	public void testGetSetOrigDataRef() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getOrigDataRef());
		mdata.setOrigDataRef(ORIGINAL_DATA_REF);
		assertEquals(ORIGINAL_DATA_REF, mdata.getOrigDataRef());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setOrigDataRef(null);
		assertEquals("", mdata.getOrigDataRef());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getAddlDocs()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setAddlDocs(java.lang.String)}.
	 */
	@Test
	public void testGetSetAddlDocs() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getAddlDocs());
		mdata.setAddlDocs(ADDL_DOCS);
		assertEquals(ADDL_DOCS, mdata.getAddlDocs());
		assertEquals("", mdata.getOrigDataRef());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setAddlDocs(null);
		assertEquals("", mdata.getAddlDocs());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getSocatDOI()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setSocatDOI(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatDOI() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getSocatDOI());
		mdata.setSocatDOI(SOCAT_DOI);
		assertEquals(SOCAT_DOI, mdata.getSocatDOI());
		assertEquals("", mdata.getAddlDocs());
		assertEquals("", mdata.getOrigDataRef());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setSocatDOI(null);
		assertEquals("", mdata.getSocatDOI());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getSocatDOIHRef()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setSocatDOIHRef(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatDOIHRef() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getSocatDOIHRef());
		mdata.setSocatDOIHRef(SOCAT_DOI_HREF);
		assertEquals(SOCAT_DOI_HREF, mdata.getSocatDOIHRef());
		assertEquals("", mdata.getSocatDOI());
		assertEquals("", mdata.getAddlDocs());
		assertEquals("", mdata.getOrigDataRef());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setSocatDOIHRef(null);
		assertEquals("", mdata.getSocatDOIHRef());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getSocatVersion()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setSocatVersion(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatVersion() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals("", mdata.getSocatVersion());
		mdata.setSocatVersion(SOCAT_VERSION);
		assertEquals(SOCAT_VERSION, mdata.getSocatVersion());
		assertEquals("", mdata.getSocatDOIHRef());
		assertEquals("", mdata.getSocatDOI());
		assertEquals("", mdata.getAddlDocs());
		assertEquals("", mdata.getOrigDataRef());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setSocatVersion(null);
		assertEquals("", mdata.getSocatVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#getQcFlag()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#setQcFlag(java.lang.Character)}.
	 */
	@Test
	public void testGetSetQCFlag() {
		SocatMetadata mdata = new SocatMetadata();
		assertEquals(" ", mdata.getQcFlag());
		mdata.setQcFlag(CRUISE_FLAG);
		assertEquals(CRUISE_FLAG, mdata.getQcFlag());
		assertEquals("", mdata.getSocatVersion());
		assertEquals("", mdata.getSocatDOIHRef());
		assertEquals("", mdata.getSocatDOI());
		assertEquals("", mdata.getAddlDocs());
		assertEquals("", mdata.getOrigDataRef());
		assertEquals("", mdata.getScienceGroup());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( SocatCruiseData.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals("", mdata.getOrganization());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		assertEquals("", mdata.getExpocode());
		mdata.setQcFlag(null);
		assertEquals(" ", mdata.getQcFlag());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatMetadata#equals(java.lang.Object)}.
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

		mdata.setOrganization(ORGANIZATION_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setOrganization(ORGANIZATION_NAME);
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

		mdata.setOrigDataRef(ORIGINAL_DATA_REF);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setOrigDataRef(ORIGINAL_DATA_REF);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setAddlDocs(ADDL_DOCS);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setAddlDocs(ADDL_DOCS);
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

		mdata.setSocatVersion(SOCAT_VERSION);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setSocatVersion(SOCAT_VERSION);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setQcFlag(CRUISE_FLAG);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setQcFlag(CRUISE_FLAG);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );
	}

}
