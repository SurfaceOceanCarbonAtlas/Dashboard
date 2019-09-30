package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DatasetQCStatusTest {

    private static final DatasetQCStatus.Status DEFAULT_FLAG = DatasetQCStatus.Status.PRIVATE;
    private static final DatasetQCStatus.Status ACTUAL_FLAG = DatasetQCStatus.Status.UPDATED_AWAITING_QC;
    private static final DatasetQCStatus.Status PI_FLAG = DatasetQCStatus.Status.ACCEPTED_A;
    private static final DatasetQCStatus.Status AUTO_FLAG = DatasetQCStatus.Status.ACCEPTED_B;
    private static final String COMMENT_STRING = "This is a comment about the QC flag";

    /**
     * Test of {@link DatasetQCStatus#getActual()} and {@link DatasetQCStatus#setActual(DatasetQCStatus.Status)}
     */
    @Test
    public void testGetSetActualFlag() {
        DatasetQCStatus flag = new DatasetQCStatus();
        assertEquals(DEFAULT_FLAG, flag.getActual());
        flag.setActual(ACTUAL_FLAG);
        assertEquals(ACTUAL_FLAG, flag.getActual());
        flag.setActual(null);
        assertEquals(DEFAULT_FLAG, flag.getActual());
    }

    /**
     * Test of {@link DatasetQCStatus#getPiSuggested()} and {@link DatasetQCStatus#setPiSuggested(DatasetQCStatus.Status)}
     */
    @Test
    public void testGetSetPiFlag() {
        DatasetQCStatus flag = new DatasetQCStatus();
        assertEquals(DEFAULT_FLAG, flag.getPiSuggested());
        flag.setPiSuggested(PI_FLAG);
        assertEquals(PI_FLAG, flag.getPiSuggested());
        assertEquals(DEFAULT_FLAG, flag.getActual());
        flag.setPiSuggested(null);
        assertEquals(DEFAULT_FLAG, flag.getPiSuggested());
    }

    /**
     * Test of {@link DatasetQCStatus#getAutoSuggested()} and {@link DatasetQCStatus#setAutoSuggested(DatasetQCStatus.Status)}
     */
    @Test
    public void testGetSetAutoFlag() {
        DatasetQCStatus flag = new DatasetQCStatus();
        assertEquals(DEFAULT_FLAG, flag.getAutoSuggested());
        flag.setAutoSuggested(AUTO_FLAG);
        assertEquals(AUTO_FLAG, flag.getAutoSuggested());
        assertEquals(DEFAULT_FLAG, flag.getPiSuggested());
        assertEquals(DEFAULT_FLAG, flag.getActual());
        flag.setAutoSuggested(null);
        assertEquals(DEFAULT_FLAG, flag.getAutoSuggested());
    }

    /**
     * Test of {@link DatasetQCStatus#getComment()} and {@link DatasetQCStatus#setComment(String)}
     */
    @Test
    public void testGetSetComment() {
        DatasetQCStatus flag = new DatasetQCStatus();
        assertEquals("", flag.getComment());
        flag.setComment(COMMENT_STRING);
        assertEquals(COMMENT_STRING, flag.getComment());
        assertEquals(DEFAULT_FLAG, flag.getAutoSuggested());
        assertEquals(DEFAULT_FLAG, flag.getPiSuggested());
        assertEquals(DEFAULT_FLAG, flag.getActual());
        flag.setComment(null);
        assertEquals("", flag.getComment());
    }

    /**
     * Test of {@link DatasetQCStatus#flagString()} and {@link DatasetQCStatus#statusString()}.
     */
    @Test
    public void testFlagStatusStrings() {
        DatasetQCStatus flag = new DatasetQCStatus();
        assertEquals("P", flag.flagString());
        assertEquals("Private", flag.statusString());

        flag.setPiSuggested(DatasetQCStatus.Status.NEW_AWAITING_QC);
        flag.setAutoSuggested(DatasetQCStatus.Status.ACCEPTED_B);
        boolean caughtError;
        try {
            flag.flagString();
            caughtError = false;
        } catch ( IllegalArgumentException ex ) {
            caughtError = true;
        }
        if ( !caughtError )
            fail("IllegalArgumentException not thrown in flagString() with invalid PI-suggested status");
        flag.setPiSuggested(DatasetQCStatus.Status.ACCEPTED_A);
        flag.setAutoSuggested(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
        try {
            flag.statusString();
            caughtError = false;
        } catch ( IllegalArgumentException ex ) {
            caughtError = true;
        }
        if ( !caughtError )
            fail("IllegalArgumentException not thrown in statusString() with invalid automation-suggested status");

        flag.setActual(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
        flag.setPiSuggested(DatasetQCStatus.Status.ACCEPTED_A);
        flag.setAutoSuggested(DatasetQCStatus.Status.ACCEPTED_B);
        assertEquals("U-piA-autoB", flag.flagString());
        assertEquals("Submitted update; PI suggested Flag A; automation suggested Flag B", flag.statusString());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_A);
        flag.setPiSuggested(null);
        flag.setAutoSuggested(null);
        assertEquals("A", flag.flagString());
        assertEquals("Flag A", flag.statusString());
    }

    /**
     * Test of {@link DatasetQCStatus.Status#fromString(String)} and {@link DatasetQCStatus#fromString(String)}
     */
    @Test
    public void testFromString() {
        assertEquals(ACTUAL_FLAG, DatasetQCStatus.Status.fromString(ACTUAL_FLAG.toString()));
        assertEquals(PI_FLAG, DatasetQCStatus.Status.fromString(PI_FLAG.toString()));
        assertEquals(AUTO_FLAG, DatasetQCStatus.Status.fromString(AUTO_FLAG.toString()));

        DatasetQCStatus flag = new DatasetQCStatus(ACTUAL_FLAG, "");
        String flagString = flag.flagString();
        DatasetQCStatus other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);

        flag.setPiSuggested(PI_FLAG);
        flagString = flag.flagString();
        other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);

        flag.setAutoSuggested(AUTO_FLAG);
        flagString = flag.flagString();
        other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);

        flag.setPiSuggested(null);
        flagString = flag.flagString();
        other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCStatus.fromString(flagString);
        assertEquals(flag, other);
    }

    /**
     * Test of {@link DatasetQCStatus.Status#isAcceptable()}, {@link DatasetQCStatus#isAcceptable()},
     * {@link DatasetQCStatus#isAwaitingQC()}, {@link DatasetQCStatus#isCommentFlag()},
     * {@link DatasetQCStatus#isConflicted()}, {@link DatasetQCStatus#isEditable()},
     * {@link DatasetQCStatus#isNewAwaitingQC()}, {@link DatasetQCStatus#isRenameFlag()},
     * {@link DatasetQCStatus#isPrivate()}, and {@link DatasetQCStatus#isUpdatedAwaitingQC()}
     */
    @Test
    public void testBooleans() {
        assertFalse(DatasetQCStatus.Status.isAcceptable(null));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.PRIVATE));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.SUSPENDED));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.EXCLUDED));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.NEW_AWAITING_QC));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.UPDATED_AWAITING_QC));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.CONFLICTED));
        assertTrue(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.ACCEPTED_A));
        assertTrue(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.ACCEPTED_B));
        assertTrue(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.ACCEPTED_C));
        assertTrue(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.ACCEPTED_D));
        assertTrue(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.ACCEPTED_E));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.COMMENT));
        assertFalse(DatasetQCStatus.Status.isAcceptable(DatasetQCStatus.Status.RENAMED));

        DatasetQCStatus flag = new DatasetQCStatus();
        // Values of PI and automated flags should not affect any of these
        flag.setPiSuggested(PI_FLAG);
        flag.setAutoSuggested(AUTO_FLAG);

        flag.setActual(DatasetQCStatus.Status.PRIVATE);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertTrue(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertTrue(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.SUSPENDED);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertTrue(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.EXCLUDED);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertTrue(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.NEW_AWAITING_QC);
        assertFalse(flag.isAcceptable());
        assertTrue(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertTrue(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
        assertFalse(flag.isAcceptable());
        assertTrue(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertTrue(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.CONFLICTED);
        assertFalse(flag.isAcceptable());
        assertTrue(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertTrue(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_A);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_B);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_C);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_D);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_E);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.COMMENT);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertTrue(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActual(DatasetQCStatus.Status.RENAMED);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertTrue(flag.isRenameFlag());
        assertFalse(flag.isPrivate());
        assertFalse(flag.isUpdatedAwaitingQC());
    }

    /**
     * Test of {@link DatasetQCStatus#compareTo(DatasetQCStatus)}
     */
    @Test
    public void testCompareTo() {
        DatasetQCStatus first = new DatasetQCStatus();
        DatasetQCStatus second = new DatasetQCStatus();
        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));

        first.setActual(ACTUAL_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        first.setPiSuggested(PI_FLAG);
        first.setAutoSuggested(AUTO_FLAG);
        first.setComment(COMMENT_STRING);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.setActual(ACTUAL_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.setPiSuggested(PI_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.setAutoSuggested(AUTO_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.setComment(COMMENT_STRING);
        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));

        first.setActual(DatasetQCStatus.Status.SUSPENDED);
        second.setPiSuggested(DatasetQCStatus.Status.PRIVATE);
        second.setAutoSuggested(DatasetQCStatus.Status.PRIVATE);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
    }

    /**
     * Test of {@link DatasetQCStatus#hashCode()} and {@link DatasetQCStatus#equals(Object)}
     */
    @Test
    public void testHashCodeEquals() {
        DatasetQCStatus first = new DatasetQCStatus();
        assertFalse(first.equals(DatasetQCStatus.Status.PRIVATE));
        assertFalse(first.equals(null));

        DatasetQCStatus second = new DatasetQCStatus();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setActual(ACTUAL_FLAG);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setActual(ACTUAL_FLAG);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPiSuggested(PI_FLAG);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPiSuggested(PI_FLAG);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAutoSuggested(AUTO_FLAG);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAutoSuggested(AUTO_FLAG);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setComment(COMMENT_STRING);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setComment(COMMENT_STRING);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

    /**
     * Test of {@link DatasetQCStatus#DatasetQCStatus(DatasetQCStatus.Status, String)}
     * and {@link DatasetQCStatus#DatasetQCStatus(DatasetQCStatus)}
     */
    @Test
    public void testDatasetQCFlag() {
        DatasetQCStatus flag = new DatasetQCStatus();
        DatasetQCStatus other = new DatasetQCStatus((DatasetQCStatus.Status) null, "");
        assertEquals(flag, other);
        other = new DatasetQCStatus((DatasetQCStatus) null);
        assertEquals(flag, other);

        flag.setActual(ACTUAL_FLAG);
        flag.setComment(COMMENT_STRING);
        other = new DatasetQCStatus(ACTUAL_FLAG, COMMENT_STRING);
        assertEquals(flag, other);

        flag.setPiSuggested(PI_FLAG);
        flag.setAutoSuggested(AUTO_FLAG);
        other = new DatasetQCStatus(flag);
        assertEquals(flag, other);
    }

}
