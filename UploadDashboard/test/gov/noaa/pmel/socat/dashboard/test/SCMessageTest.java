/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity;
import org.junit.Test;

/**
 * Unit tests for methods in {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage}
 * 
 * @author Karl Smith
 */
public class SCMessageTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getSeverity()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setSeverity(gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity)}.
	 */
	@Test
	public void testGetSetSeverity() {
		final SCMsgSeverity mySeverity = SCMsgSeverity.ERROR;
		SCMessage msg = new SCMessage();
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
		msg.setSeverity(mySeverity);
		assertEquals(mySeverity, msg.getSeverity());
		msg.setSeverity(null);
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getRowNumber()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setRowNumber(int)}.
	 */
	@Test
	public void testGetSetRowNumber() {
		final int myRowNum = 25;
		SCMessage msg = new SCMessage();
		assertEquals(-1, msg.getRowNumber());
		msg.setRowNumber(myRowNum);
		assertEquals(myRowNum, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getTimestamp()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setTimestamp(java.lang.String)}.
	 */
	@Test
	public void testGetSetTimestamp() {
		final String myTimestamp = "2014-02-06 11:35";
		SCMessage msg = new SCMessage();
		assertEquals("", msg.getTimestamp());
		msg.setTimestamp(myTimestamp);
		assertEquals(myTimestamp, msg.getTimestamp());
		assertEquals(-1, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
		msg.setTimestamp(null);
		assertEquals("", msg.getTimestamp());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getLongitude()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setLongitude(double)}.
	 */
	@Test
	public void testGetSetLongitude() {
		final double myLongitude = -120.35;
		SCMessage msg = new SCMessage();
		assertTrue( Double.isNaN(msg.getLongitude()) );
		msg.setLongitude(myLongitude);
		assertEquals(myLongitude, msg.getLongitude(), 1.0E-6);
		assertEquals("", msg.getTimestamp());
		assertEquals(-1, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
		msg.setLongitude( Double.NEGATIVE_INFINITY );
		assertTrue( Double.isNaN(msg.getLongitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getLatitude()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setLatitude(double)}.
	 */
	@Test
	public void testGetSetLatitude() {
		final double myLatitude = 46.25;
		SCMessage msg = new SCMessage();
		assertTrue( Double.isNaN(msg.getLatitude()) );
		msg.setLatitude(myLatitude);
		assertEquals(myLatitude, msg.getLatitude(), 1.0E-6);
		assertTrue( Double.isNaN(msg.getLongitude()) );
		assertEquals("", msg.getTimestamp());
		assertEquals(-1, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
		msg.setLatitude( Double.POSITIVE_INFINITY );
		assertTrue( Double.isNaN(msg.getLatitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getColNumber()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setColNumber(int)}.
	 */
	@Test
	public void testGetSetColNumber() {
		final int myColNum = 8;
		SCMessage msg = new SCMessage();
		assertEquals(-1, msg.getColNumber());
		msg.setColNumber(myColNum);
		assertEquals(myColNum, msg.getColNumber());
		assertTrue( Double.isNaN(msg.getLatitude()) );
		assertTrue( Double.isNaN(msg.getLongitude()) );
		assertEquals("", msg.getTimestamp());
		assertEquals(-1, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getColName()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setColName(java.lang.String)}.
	 */
	@Test
	public void testGetSetColName() {
		final String myColName = "P_atm";
		SCMessage msg = new SCMessage();
		assertEquals("", msg.getColName());
		msg.setColName(myColName);
		assertEquals(myColName, msg.getColName());
		assertEquals(-1, msg.getColNumber());
		assertTrue( Double.isNaN(msg.getLatitude()) );
		assertTrue( Double.isNaN(msg.getLongitude()) );
		assertEquals("", msg.getTimestamp());
		assertEquals(-1, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
		msg.setColName(null);
		assertEquals("", msg.getColName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getGeneralComment()} and
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setGeneralComment(java.lang.String)}.
	 */
	@Test
	public void testGetSetGeneralComment() {
		final String myExplanation = "value exceeds the upper limit of questionable values";
		SCMessage msg = new SCMessage();
		assertEquals("", msg.getGeneralComment());
		msg.setGeneralComment(myExplanation);
		assertEquals(myExplanation, msg.getGeneralComment());
		assertEquals("", msg.getColName());
		assertEquals(-1, msg.getColNumber());
		assertTrue( Double.isNaN(msg.getLatitude()) );
		assertTrue( Double.isNaN(msg.getLongitude()) );
		assertEquals("", msg.getTimestamp());
		assertEquals(-1, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
		msg.setGeneralComment(null);
		assertEquals("", msg.getGeneralComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#getDetailedComment()} and
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#setDetailedComment(java.lang.String)}.
	 */
	@Test
	public void testGetSetDetailedComment() {
		final String myExplanation = "value exceeds the upper limit of questionable values";
		SCMessage msg = new SCMessage();
		assertEquals("", msg.getDetailedComment());
		msg.setDetailedComment(myExplanation);
		assertEquals(myExplanation, msg.getDetailedComment());
		assertEquals("", msg.getGeneralComment());
		assertEquals("", msg.getColName());
		assertEquals(-1, msg.getColNumber());
		assertTrue( Double.isNaN(msg.getLatitude()) );
		assertTrue( Double.isNaN(msg.getLongitude()) );
		assertEquals("", msg.getTimestamp());
		assertEquals(-1, msg.getRowNumber());
		assertEquals(SCMsgSeverity.UNKNOWN, msg.getSeverity());
		msg.setDetailedComment(null);
		assertEquals("", msg.getDetailedComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#hashCode()}, 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#equals(java.lang.Object)}, 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#woceTypeComparator},
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#severityComparator},
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#rowNumComparator},
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#timestampComparator},
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#longitudeComparator},
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#latitudeComparator},
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#colNumComparator},
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#colNameComparator}, and
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessage#explanationComparator}.
	 */
	@Test
	public void testHashCodeEquals() {
		final SCMsgSeverity mySeverity = SCMsgSeverity.ERROR;
		final int myRowNum = 25;
		final String myTimestamp = "2014-02-06 11:35";
		final double myLongitude = -120.35;
		final double myLatitude = 46.25;
		final int myColNum = 8;
		final String myColName = "P_atm";
		final String myGeneralComment = "value exceeds the upper limit of questionable values";
		final String myExplanation = "Sea-level atmospheric pressure 1205 value exceeds 1200";

		SCMessage msg = new SCMessage();
		assertFalse( msg.equals(null) );
		assertFalse( msg.equals(myExplanation) );
		assertTrue( SCMessage.severityComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.rowNumComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.timestampComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.longitudeComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.latitudeComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.colNumComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.colNameComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.explanationComparator.compare(msg, null) > 0 );
		assertTrue( SCMessage.severityComparator.compare(null, msg) < 0 );
		assertTrue( SCMessage.rowNumComparator.compare(null, msg) < 0 );
		assertTrue( SCMessage.timestampComparator.compare(null, msg) < 0 );
		assertTrue( SCMessage.longitudeComparator.compare(null, msg) < 0 );
		assertTrue( SCMessage.latitudeComparator.compare(null, msg) < 0 );
		assertTrue( SCMessage.colNumComparator.compare(null, msg) < 0 );
		assertTrue( SCMessage.colNameComparator.compare(null, msg) < 0 );
		assertTrue( SCMessage.explanationComparator.compare(null, msg) < 0 );


		SCMessage other = new SCMessage();
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setSeverity(mySeverity);
		assertFalse( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertTrue( SCMessage.severityComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.severityComparator.compare(other, msg) < 0 );
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		other.setSeverity(mySeverity);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setRowNumber(myRowNum);
		assertFalse( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertTrue( SCMessage.rowNumComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.rowNumComparator.compare(other, msg) < 0 );
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		other.setRowNumber(myRowNum);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setTimestamp(myTimestamp);
		assertFalse( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertTrue( SCMessage.timestampComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.timestampComparator.compare(other, msg) < 0 );
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		other.setTimestamp(myTimestamp);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setLongitude(myLongitude);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertTrue( SCMessage.longitudeComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.longitudeComparator.compare(other, msg) < 0 );
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		other.setLongitude(myLongitude);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setLatitude(myLatitude);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertTrue( SCMessage.latitudeComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.latitudeComparator.compare(other, msg) < 0 );
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		other.setLatitude(myLatitude);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setColNumber(myColNum);
		assertFalse( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertTrue( SCMessage.colNumComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.colNumComparator.compare(other, msg) < 0 );
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		other.setColNumber(myColNum);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setColName(myColName);
		assertFalse( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertTrue( SCMessage.colNameComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.colNameComparator.compare(other, msg) < 0 );
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		other.setColName(myColName);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setGeneralComment(myGeneralComment);
		assertFalse( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
		assertEquals(0,  SCMessage.explanationComparator.compare(other, msg));
		other.setGeneralComment(myGeneralComment);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));

		msg.setDetailedComment(myExplanation);
		assertFalse( msg.hashCode() == other.hashCode() );
		assertFalse( msg.equals(other) );
		assertFalse( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertTrue( SCMessage.explanationComparator.compare(msg, other) > 0 );
		assertTrue( SCMessage.explanationComparator.compare(other, msg) < 0 );
		other.setDetailedComment(myExplanation);
		assertTrue( msg.hashCode() == other.hashCode() );
		assertTrue( msg.equals(other) );
		assertTrue( other.equals(msg) );
		assertEquals(0, SCMessage.severityComparator.compare(msg, other));
		assertEquals(0, SCMessage.rowNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.timestampComparator.compare(msg, other));
		assertEquals(0, SCMessage.longitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.latitudeComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNumComparator.compare(msg, other));
		assertEquals(0, SCMessage.colNameComparator.compare(msg, other));
		assertEquals(0, SCMessage.explanationComparator.compare(msg, other));
	}

}
