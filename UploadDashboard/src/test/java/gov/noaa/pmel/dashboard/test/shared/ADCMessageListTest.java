/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.ADCMessageList;

/**
 * Tests of methods in {@link gov.noaa.pmel.dashboard.shared.ADCMessageList}.
 *
 * @author Karl Smith
 */
public class ADCMessageListTest {

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#getUsername()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#setUsername(java.lang.String)}.
     */
    @Test
    public void testGetSetUsername() {
        final String myUsername = "SocatUser";
        ADCMessageList msgList = new ADCMessageList();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msgList.getUsername());
        msgList.setUsername(myUsername);
        assertEquals(myUsername, msgList.getUsername());
        msgList.setUsername(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msgList.getUsername());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#getDatasetId()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#setDatasetId(java.lang.String)}.
     */
    @Test
    public void testGetSetDatasetId() {
        final String myExpocode = "XXXX20140204";
        ADCMessageList msgList = new ADCMessageList();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msgList.getDatasetId());
        msgList.setDatasetId(myExpocode);
        assertEquals(myExpocode, msgList.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msgList.getUsername());
        msgList.setDatasetId(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msgList.getDatasetId());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#getSummaries()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#setSummaries(java.util.ArrayList)}.
     */
    @Test
    public void testGetSetSummaries() {
        final ArrayList<String> mySummaries = new ArrayList<String>(Arrays.asList(
                "3 errors of type: P_equ unreasonably large",
                "5 errors of type: calculated ship speed unreasonably large",
                "20 warnings of type: P_equ unusually large"));
        ADCMessageList msgList = new ADCMessageList();
        assertEquals(0, msgList.getSummaries().size());
        msgList.setSummaries(mySummaries);
        assertEquals(mySummaries, msgList.getSummaries());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msgList.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, msgList.getUsername());
        msgList.setSummaries(null);
        assertEquals(0, msgList.getSummaries().size());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#hashCode()} and
     * {@link gov.noaa.pmel.dashboard.shared.ADCMessageList#equals(java.lang.Object)}.
     */
    @Test
    public void testHashCodeEquals() {
        final String myUsername = "SocatUser";
        final String myExpocode = "XXXX20140204";
        final ArrayList<String> mySummaries = new ArrayList<String>(Arrays.asList(
                "3 errors of type: P_equ unreasonably large",
                "5 errors of type: calculated ship speed unreasonably large",
                "20 warnings of type: P_equ unusually large"));
        final Severity mySeverity = Severity.ERROR;
        final int myRowNum = 25;
        final int myColNum = 8;
        final String myColName = "P_atm";
        final String myExplanation = "value exceeds the upper limit of questionable values";

        ADCMessage myMsg = new ADCMessage();
        myMsg.setSeverity(mySeverity);
        myMsg.setRowNumber(myRowNum);
        myMsg.setColNumber(myColNum);
        myMsg.setColName(myColName);
        myMsg.setGeneralComment(myExplanation);
        myMsg.setDetailedComment(myExplanation);

        ADCMessage otherMsg = new ADCMessage();
        otherMsg.setSeverity(mySeverity);
        otherMsg.setRowNumber(myRowNum);
        otherMsg.setColNumber(myColNum);
        otherMsg.setColName(myColName);
        otherMsg.setGeneralComment(myExplanation);
        otherMsg.setDetailedComment(myExplanation);
        assertEquals(myMsg, otherMsg);

        ADCMessageList msgList = new ADCMessageList();
        assertFalse( msgList.equals(null) );
        assertFalse( msgList.equals(myExpocode) );

        ADCMessageList other = new ADCMessageList();
        assertTrue( msgList.hashCode() == other.hashCode() );
        assertTrue( msgList.equals(other) );

        msgList.setUsername(myUsername);
        assertFalse( msgList.hashCode() == other.hashCode() );
        assertFalse( msgList.equals(other) );
        other.setUsername(myUsername);
        assertTrue( msgList.hashCode() == other.hashCode() );
        assertTrue( msgList.equals(other) );

        msgList.setDatasetId(myExpocode);
        assertFalse( msgList.hashCode() == other.hashCode() );
        assertFalse( msgList.equals(other) );
        other.setDatasetId(myExpocode);
        assertTrue( msgList.hashCode() == other.hashCode() );
        assertTrue( msgList.equals(other) );

        msgList.setSummaries(mySummaries);
        assertFalse( msgList.hashCode() == other.hashCode() );
        assertFalse( msgList.equals(other) );
        other.setSummaries(mySummaries);
        assertTrue( msgList.hashCode() == other.hashCode() );
        assertTrue( msgList.equals(other) );

        msgList.add(myMsg);
        assertFalse( msgList.hashCode() == other.hashCode() );
        assertFalse( msgList.equals(other) );
        other.add(otherMsg);
        assertTrue( msgList.hashCode() == other.hashCode() );
        assertTrue( msgList.equals(other) );

        // myMsg same as otherMsg
        assertFalse( msgList.add(otherMsg) );
    }

}
