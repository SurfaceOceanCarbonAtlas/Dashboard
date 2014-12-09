/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;

import org.junit.Test;

/**
 * Tests of methods in {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList}.
 * 
 * @author Karl Smith
 */
public class SCMessageListTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#getUsername()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#setUsername(java.lang.String)}.
	 */
	@Test
	public void testGetSetUsername() {
		final String myUsername = "SocatUser";
		SCMessageList msgList = new SCMessageList();
		assertEquals("", msgList.getUsername());
		msgList.setUsername(myUsername);
		assertEquals(myUsername, msgList.getUsername());
		msgList.setUsername(null);
		assertEquals("", msgList.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#getExpocode()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		final String myExpocode = "XXXX20140204";
		SCMessageList msgList = new SCMessageList();
		assertEquals("", msgList.getExpocode());
		msgList.setExpocode(myExpocode);
		assertEquals(myExpocode, msgList.getExpocode());
		assertEquals("", msgList.getUsername());
		msgList.setExpocode(null);
		assertEquals("", msgList.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#getSummaries()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#setSummaries(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetSummaries() {
		final ArrayList<String> mySummaries = new ArrayList<String>(Arrays.asList(
				"3 errors of type: P_equ unreasonably large", 
				"5 errors of type: calculated ship speed unreasonably large",
				"20 warnings of type: P_equ unusually large"));
		SCMessageList msgList = new SCMessageList();
		assertEquals(0, msgList.getSummaries().size());
		msgList.setSummaries(mySummaries);
		assertEquals(mySummaries, msgList.getSummaries());
		assertEquals("", msgList.getExpocode());
		assertEquals("", msgList.getUsername());
		msgList.setSummaries(null);
		assertEquals(0, msgList.getSummaries().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#hashCode()} and 
	 * {@link gov.noaa.pmel.socat.dashboard.shared.SCMessageList#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		final String myUsername = "SocatUser";
		final String myExpocode = "XXXX20140204";
		final ArrayList<String> mySummaries = new ArrayList<String>(Arrays.asList(
				"3 errors of type: P_equ unreasonably large", 
				"5 errors of type: calculated ship speed unreasonably large",
				"20 warnings of type: P_equ unusually large"));
		final SCMsgSeverity mySeverity = SCMsgSeverity.ERROR;
		final int myRowNum = 25;
		final int myColNum = 8;
		final String myColName = "P_atm";
		final String myExplanation = "value exceeds the upper limit of questionable values";

		SCMessage myMsg = new SCMessage();
		myMsg.setSeverity(mySeverity);
		myMsg.setRowNumber(myRowNum);
		myMsg.setColNumber(myColNum);
		myMsg.setColName(myColName);
		myMsg.setGeneralComment(myExplanation);
		myMsg.setDetailedComment(myExplanation);

		SCMessage otherMsg = new SCMessage();
		otherMsg.setSeverity(mySeverity);
		otherMsg.setRowNumber(myRowNum);
		otherMsg.setColNumber(myColNum);
		otherMsg.setColName(myColName);
		otherMsg.setGeneralComment(myExplanation);
		otherMsg.setDetailedComment(myExplanation);
		assertEquals(myMsg, otherMsg);

		SCMessageList msgList = new SCMessageList();
		assertFalse( msgList.equals(null) );
		assertFalse( msgList.equals(myExpocode) );

		SCMessageList other = new SCMessageList();
		assertTrue( msgList.hashCode() == other.hashCode() );
		assertTrue( msgList.equals(other) );

		msgList.setUsername(myUsername);
		assertFalse( msgList.hashCode() == other.hashCode() );
		assertFalse( msgList.equals(other) );
		other.setUsername(myUsername);
		assertTrue( msgList.hashCode() == other.hashCode() );
		assertTrue( msgList.equals(other) );

		msgList.setExpocode(myExpocode);
		assertFalse( msgList.hashCode() == other.hashCode() );
		assertFalse( msgList.equals(other) );
		other.setExpocode(myExpocode);
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
