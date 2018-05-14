/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

/**
 * Unit tests for methods in {@link gov.noaa.pmel.dashboard.shared.ADCMessage}
 *
 * @author Karl Smith
 */
public class ADCMessageTest {

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getSeverity()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setSeverity(gov.noaa.pmel.dashboard.shared.ADCMessage.Severity)}.
     */
    @Test
    public void testGetSetSeverity() {
        final Severity mySeverity = Severity.ERROR;
        ADCMessage msg = new ADCMessage();
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setSeverity(mySeverity);
        assertEquals(mySeverity, msg.getSeverity());
        msg.setSeverity(null);
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getRowNumber()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setRowNumber(java.lang.Integer)}.
     */
    @Test
    public void testGetSetRowNumber() {
        final Integer myRowNum = 25;
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        msg.setRowNumber(myRowNum);
        assertEquals(myRowNum, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setRowNumber(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        msg.setRowNumber(-12);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        msg.setRowNumber(1234567);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getLongitude()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setLongitude(java.lang.Double)}.
     */
    @Test
    public void testGetSetLongitude() {
        final Double myLongitude = -120.35;
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        msg.setLongitude(myLongitude);
        assertEquals(myLongitude, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setLongitude(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        msg.setLongitude(Double.NaN);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        msg.setLongitude(Double.NEGATIVE_INFINITY);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        msg.setLongitude(Double.POSITIVE_INFINITY);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        msg.setLongitude(-987.0);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        msg.setLongitude(987.0);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getLatitude()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setLatitude(java.lang.Double)}.
     */
    @Test
    public void testGetSetLatitude() {
        final Double myLatitude = 46.25;
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        msg.setLatitude(myLatitude);
        assertEquals(myLatitude, msg.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setLatitude(Double.NaN);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        msg.setLatitude(Double.NEGATIVE_INFINITY);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        msg.setLatitude(Double.POSITIVE_INFINITY);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        msg.setLatitude(-98.0);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        msg.setLatitude(98.0);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getDepth()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setDepth(java.lang.Double)}.
     */
    @Test
    public void testGetSetDepth() {
        final Double myDepth = 205.3;
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getDepth());
        msg.setDepth(myDepth);
        assertEquals(myDepth, msg.getDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setDepth(Double.NaN);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getDepth());
        msg.setDepth(Double.NEGATIVE_INFINITY);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getDepth());
        msg.setDepth(Double.POSITIVE_INFINITY);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getDepth());
        msg.setDepth(-98.0);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getDepth());
        msg.setDepth(98765.0);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getDepth());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getTimestamp()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setTimestamp(java.lang.String)}.
     */
    @Test
    public void testGetSetTimestamp() {
        final String myTimestamp = "2014-02-06 11:35";
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getTimestamp());
        msg.setTimestamp(myTimestamp);
        assertEquals(myTimestamp, msg.getTimestamp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setTimestamp(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getTimestamp());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getColNumber()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setColNumber(java.lang.Integer)}.
     */
    @Test
    public void testGetSetColNumber() {
        final Integer myColNum = 8;
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
        msg.setColNumber(myColNum);
        assertEquals(myColNum, msg.getColNumber());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getTimestamp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setColNumber(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
        msg.setColNumber(-12);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
        msg.setColNumber(1234);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getColName()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setColName(java.lang.String)}.
     */
    @Test
    public void testGetSetColName() {
        final String myColName = "P_atm";
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getColName());
        msg.setColName(myColName);
        assertEquals(myColName, msg.getColName());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getTimestamp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setColName(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getColName());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getGeneralComment()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setGeneralComment(java.lang.String)}.
     */
    @Test
    public void testGetSetGeneralComment() {
        final String myExplanation = "value exceeds the upper limit of questionable values";
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getGeneralComment());
        msg.setGeneralComment(myExplanation);
        assertEquals(myExplanation, msg.getGeneralComment());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getColName());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getTimestamp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setGeneralComment(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getGeneralComment());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#getDetailedComment()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#setDetailedComment(java.lang.String)}.
     */
    @Test
    public void testGetSetDetailedComment() {
        final String myExplanation = "Sea-level atmospheric pressure 1234.5 exceeds 1200.0";
        ADCMessage msg = new ADCMessage();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getDetailedComment());
        msg.setDetailedComment(myExplanation);
        assertEquals(myExplanation, msg.getDetailedComment());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getGeneralComment());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getColName());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getTimestamp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, msg.getLongitude());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getRowNumber());
        assertEquals(Severity.UNASSIGNED, msg.getSeverity());
        msg.setDetailedComment(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msg.getDetailedComment());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessage#hashCode()},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#equals(java.lang.Object)},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#woceTypeComparator},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#severityComparator},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#rowNumComparator},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#timestampComparator},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#longitudeComparator},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#latitudeComparator},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#colNumComparator},
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#colNameComparator}, and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessage#explanationComparator}.
     */
    @Test
    public void testHashCodeEquals() {
        final Severity mySeverity = Severity.ERROR;
        final Integer myRowNum = 25;
        final Double myLongitude = -120.35;
        final Double myLatitude = 46.25;
        final Double myDepth = 205.3;
        final String myTimestamp = "2014-02-06 11:35";
        final Integer myColNum = 8;
        final String myColName = "P_atm";
        final String myGeneralComment = "value exceeds the upper limit of questionable values";
        final String myExplanation = "Sea-level atmospheric pressure 1234.5 exceeds 1200.0";

        ADCMessage msg = new ADCMessage();
        assertFalse( msg.equals(null) );
        assertFalse( msg.equals(myExplanation) );
        assertTrue( ADCMessage.severityComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.rowNumComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.longitudeComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.latitudeComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.depthComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.timestampComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.colNumComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.colNameComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.explanationComparator.compare(msg, null) > 0 );
        assertTrue( ADCMessage.severityComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.rowNumComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.timestampComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.longitudeComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.latitudeComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.depthComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.colNumComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.colNameComparator.compare(null, msg) < 0 );
        assertTrue( ADCMessage.explanationComparator.compare(null, msg) < 0 );


        ADCMessage other = new ADCMessage();
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setSeverity(mySeverity);
        assertFalse( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertTrue( ADCMessage.severityComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.severityComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setSeverity(mySeverity);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setRowNumber(myRowNum);
        assertFalse( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertTrue( ADCMessage.rowNumComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.rowNumComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setRowNumber(myRowNum);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setLongitude(myLongitude);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertTrue( ADCMessage.longitudeComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.longitudeComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setLongitude(myLongitude);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setLatitude(myLatitude);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertTrue( ADCMessage.latitudeComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.latitudeComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setLatitude(myLatitude);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setDepth(myDepth);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertTrue( ADCMessage.depthComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.depthComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setDepth(myDepth);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setTimestamp(myTimestamp);
        assertFalse( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertTrue( ADCMessage.timestampComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.timestampComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setTimestamp(myTimestamp);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setColNumber(myColNum);
        assertFalse( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertTrue( ADCMessage.colNumComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.colNumComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setColNumber(myColNum);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setColName(myColName);
        assertFalse( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertTrue( ADCMessage.colNameComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.colNameComparator.compare(other, msg) < 0 );
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        other.setColName(myColName);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setGeneralComment(myGeneralComment);
        assertFalse( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
        assertEquals(0,  ADCMessage.explanationComparator.compare(other, msg));
        other.setGeneralComment(myGeneralComment);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));

        msg.setDetailedComment(myExplanation);
        assertFalse( msg.hashCode() == other.hashCode() );
        assertFalse( msg.equals(other) );
        assertFalse( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertTrue( ADCMessage.explanationComparator.compare(msg, other) > 0 );
        assertTrue( ADCMessage.explanationComparator.compare(other, msg) < 0 );
        other.setDetailedComment(myExplanation);
        assertTrue( msg.hashCode() == other.hashCode() );
        assertTrue( msg.equals(other) );
        assertTrue( other.equals(msg) );
        assertEquals(0, ADCMessage.severityComparator.compare(msg, other));
        assertEquals(0, ADCMessage.rowNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.longitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.latitudeComparator.compare(msg, other));
        assertEquals(0, ADCMessage.depthComparator.compare(msg, other));
        assertEquals(0, ADCMessage.timestampComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNumComparator.compare(msg, other));
        assertEquals(0, ADCMessage.colNameComparator.compare(msg, other));
        assertEquals(0, ADCMessage.explanationComparator.compare(msg, other));
    }

}
