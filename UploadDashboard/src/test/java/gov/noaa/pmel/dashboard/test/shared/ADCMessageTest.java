/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link ADCMessage} and
 * {@link java.util.Comparator<ADCMessage>} methods in {@link DashboardUtils}.
 *
 * @author Karl Smith
 */
public class ADCMessageTest {

    /**
     * Test method for {@link ADCMessage#getSeverity()} and {@link ADCMessage#setSeverity(Severity)}.
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
     * Test method for {@link ADCMessage#getRowNumber()} and {@link ADCMessage#setRowNumber(Integer)}.
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
     * Test method for {@link ADCMessage#getLongitude()} and {@link ADCMessage#setLongitude(Double)}.
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
     * Test method for {@link ADCMessage#getLatitude()} and {@link ADCMessage#setLatitude(Double)}.
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
     * Test method for {@link ADCMessage#getDepth()} and {@link ADCMessage#setDepth(Double)}.
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
     * Test method for {@link ADCMessage#getTimestamp()} and {@link ADCMessage#setTimestamp(String)}.
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
     * Test method for {@link ADCMessage#getColNumber()} and {@link ADCMessage#setColNumber(Integer)}.
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
     * Test method for {@link ADCMessage#getColName()} and {@link ADCMessage#setColName(String)}.
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
     * Test method for {@link ADCMessage#getGeneralComment()} and {@link ADCMessage#setGeneralComment(String)}.
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
     * Test method for {@link ADCMessage#getDetailedComment()} and {@link ADCMessage#setDetailedComment(String)}.
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
     * Test method for {@link ADCMessage#hashCode()}, {@link ADCMessage#equals(Object)},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#severityComparator},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#rowNumComparator},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#longitudeComparator},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#latitudeComparator},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#depthComparator},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#timestampComparator},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#colNumComparator},
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#colNameComparator}, and
     * {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#explanationComparator}.
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
        assertFalse(msg.equals(null));
        assertFalse(msg.equals(myExplanation));
        assertTrue(DashboardUtils.severityComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.rowNumComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.longitudeComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.latitudeComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.depthComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.timestampComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.colNumComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.colNameComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.explanationComparator.compare(msg, null) > 0);
        assertTrue(DashboardUtils.severityComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.rowNumComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.timestampComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.longitudeComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.latitudeComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.depthComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.colNumComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.colNameComparator.compare(null, msg) < 0);
        assertTrue(DashboardUtils.explanationComparator.compare(null, msg) < 0);


        ADCMessage other = new ADCMessage();
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setSeverity(mySeverity);
        assertNotEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertTrue(DashboardUtils.severityComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.severityComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setSeverity(mySeverity);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setRowNumber(myRowNum);
        assertNotEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertTrue(DashboardUtils.rowNumComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.rowNumComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setRowNumber(myRowNum);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setLongitude(myLongitude);
        // hashcode ignores floating point values
        assertEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertTrue(DashboardUtils.longitudeComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.longitudeComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setLongitude(myLongitude);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setLatitude(myLatitude);
        // hashcode ignores floating point values
        assertEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertTrue(DashboardUtils.latitudeComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.latitudeComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setLatitude(myLatitude);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setDepth(myDepth);
        // hashcode ignores floating point values
        assertEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertTrue(DashboardUtils.depthComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.depthComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setDepth(myDepth);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setTimestamp(myTimestamp);
        assertNotEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertTrue(DashboardUtils.timestampComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.timestampComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setTimestamp(myTimestamp);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setColNumber(myColNum);
        assertNotEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertTrue(DashboardUtils.colNumComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.colNumComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setColNumber(myColNum);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setColName(myColName);
        assertNotEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertTrue(DashboardUtils.colNameComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.colNameComparator.compare(other, msg) < 0);
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        other.setColName(myColName);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setGeneralComment(myGeneralComment);
        assertNotEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(other, msg));
        other.setGeneralComment(myGeneralComment);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));

        msg.setDetailedComment(myExplanation);
        assertNotEquals(msg.hashCode(), other.hashCode());
        assertFalse(msg.equals(other));
        assertFalse(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertTrue(DashboardUtils.explanationComparator.compare(msg, other) > 0);
        assertTrue(DashboardUtils.explanationComparator.compare(other, msg) < 0);
        other.setDetailedComment(myExplanation);
        assertEquals(msg.hashCode(), other.hashCode());
        assertTrue(msg.equals(other));
        assertTrue(other.equals(msg));
        assertEquals(0, DashboardUtils.severityComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.rowNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.longitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.latitudeComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.depthComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.timestampComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNumComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.colNameComparator.compare(msg, other));
        assertEquals(0, DashboardUtils.explanationComparator.compare(msg, other));
    }

}
