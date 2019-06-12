package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DatasetQCFlag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DatasetQCFlagTest {

    private static final DatasetQCFlag.Status DEFAULT_FLAG = DatasetQCFlag.Status.NOT_GIVEN;
    private static final DatasetQCFlag.Status ACTUAL_FLAG = DatasetQCFlag.Status.UPDATED_AWAITING_QC;
    private static final DatasetQCFlag.Status PI_FLAG = DatasetQCFlag.Status.ACCEPTED_A;
    private static final DatasetQCFlag.Status AUTO_FLAG = DatasetQCFlag.Status.ACCEPTED_B;

    /**
     * Test of {@link DatasetQCFlag#getActualFlag()} and {@link DatasetQCFlag#setActualFlag(DatasetQCFlag.Status)}
     */
    @Test
    public void testGetSetActualFlag() {
        DatasetQCFlag flag = new DatasetQCFlag();
        assertEquals(DEFAULT_FLAG, flag.getActualFlag());
        flag.setActualFlag(ACTUAL_FLAG);
        assertEquals(ACTUAL_FLAG, flag.getActualFlag());
        flag.setActualFlag(null);
        assertEquals(DEFAULT_FLAG, flag.getActualFlag());
    }

    /**
     * Test of {@link DatasetQCFlag#getPiFlag()} and {@link DatasetQCFlag#setPiFlag(DatasetQCFlag.Status)}
     */
    @Test
    public void testGetSetPiFlag() {
        DatasetQCFlag flag = new DatasetQCFlag();
        assertEquals(DEFAULT_FLAG, flag.getPiFlag());
        flag.setPiFlag(PI_FLAG);
        assertEquals(PI_FLAG, flag.getPiFlag());
        assertEquals(DEFAULT_FLAG, flag.getActualFlag());
        flag.setPiFlag(null);
        assertEquals(DEFAULT_FLAG, flag.getPiFlag());
    }

    /**
     * Test of {@link DatasetQCFlag#getAutoFlag()} and {@link DatasetQCFlag#setAutoFlag(DatasetQCFlag.Status)}
     */
    @Test
    public void testGetSetAutoFlag() {
        DatasetQCFlag flag = new DatasetQCFlag();
        assertEquals(DEFAULT_FLAG, flag.getAutoFlag());
        flag.setAutoFlag(AUTO_FLAG);
        assertEquals(AUTO_FLAG, flag.getAutoFlag());
        assertEquals(DEFAULT_FLAG, flag.getPiFlag());
        assertEquals(DEFAULT_FLAG, flag.getActualFlag());
        flag.setAutoFlag(null);
        assertEquals(DEFAULT_FLAG, flag.getAutoFlag());
    }

    /**
     * Test of {@link DatasetQCFlag#flagString()} and {@link DatasetQCFlag#statusString()}.
     */
    @Test
    public void testFlagStatusStrings() {
        DatasetQCFlag flag = new DatasetQCFlag();
        assertEquals("", flag.flagString());
        assertEquals("", flag.statusString());

        flag.setPiFlag(DatasetQCFlag.Status.ACCEPTED_A);
        flag.setAutoFlag(DatasetQCFlag.Status.ACCEPTED_B);
        boolean caughtError;
        try {
            flag.flagString();
            caughtError = false;
        } catch ( IllegalArgumentException ex ) {
            caughtError = true;
        }
        if ( !caughtError )
            fail("IllegalArgumentException not thrown in flagString() with actual flag not given");
        try {
            flag.statusString();
            caughtError = false;
        } catch ( IllegalArgumentException ex ) {
            caughtError = true;
        }
        if ( !caughtError )
            fail("IllegalArgumentException not thrown in statusString() with actual flag not given");

        flag.setActualFlag(DatasetQCFlag.Status.UPDATED_AWAITING_QC);
        assertEquals("U-piA-autoB", flag.flagString());
        assertEquals("Submitted update; PI suggested Flag A; automation suggested Flag B", flag.statusString());

        flag.setActualFlag(DatasetQCFlag.Status.ACCEPTED_A);
        try {
            flag.flagString();
            caughtError = false;
        } catch ( IllegalArgumentException ex ) {
            caughtError = true;
        }
        if ( !caughtError )
            fail("IllegalArgumentException not thrown in flagString() with flag A");
        try {
            flag.statusString();
            caughtError = false;
        } catch ( IllegalArgumentException ex ) {
            caughtError = true;
        }
        if ( !caughtError )
            fail("IllegalArgumentException not thrown in statusString() with flag A");

        flag.setPiFlag(null);
        flag.setAutoFlag(null);
        assertEquals("A", flag.flagString());
        assertEquals("Flag A", flag.statusString());
    }

    /**
     * Test of {@link DatasetQCFlag#fromString(String)}
     */
    @Test
    public void testFromFlagString() {
        DatasetQCFlag flag = new DatasetQCFlag(ACTUAL_FLAG);
        String flagString = flag.flagString();
        DatasetQCFlag other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);

        flag.setPiFlag(PI_FLAG);
        flagString = flag.flagString();
        other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);

        flag.setAutoFlag(AUTO_FLAG);
        flagString = flag.flagString();
        other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);

        flag.setPiFlag(null);
        flagString = flag.flagString();
        other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);
        flagString = flag.statusString();
        other = DatasetQCFlag.fromString(flagString);
        assertEquals(flag, other);
    }

    /**
     * Test of {@link DatasetQCFlag#isAcceptable()}, {@link DatasetQCFlag#isAwaitingQC()},
     * {@link DatasetQCFlag#isCommentFlag()}, {@link DatasetQCFlag#isConflicted()},
     * {@link DatasetQCFlag#isEditable()}, {@link DatasetQCFlag#isNewAwaitingQC()},
     * {@link DatasetQCFlag#isRenameFlag()}, {@link DatasetQCFlag#isUnsubmitted()},
     * and {@link DatasetQCFlag#isUpdatedAwaitingQC()}
     */
    @Test
    public void testBooleans() {
        DatasetQCFlag flag = new DatasetQCFlag();
        // Values of PI and automated flags should not affect any of these
        flag.setPiFlag(PI_FLAG);
        flag.setAutoFlag(AUTO_FLAG);

        flag.setActualFlag(DatasetQCFlag.Status.NOT_GIVEN);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertTrue(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertTrue(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.SUSPENDED);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertTrue(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.EXCLUDED);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertTrue(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.NEW_AWAITING_QC);
        assertFalse(flag.isAcceptable());
        assertTrue(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertTrue(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.UPDATED_AWAITING_QC);
        assertFalse(flag.isAcceptable());
        assertTrue(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertTrue(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.CONFLICTED);
        assertFalse(flag.isAcceptable());
        assertTrue(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertTrue(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.ACCEPTED_A);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.ACCEPTED_B);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.ACCEPTED_C);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.ACCEPTED_D);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.ACCEPTED_E);
        assertTrue(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.COMMENT);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertTrue(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertFalse(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());

        flag.setActualFlag(DatasetQCFlag.Status.RENAMED);
        assertFalse(flag.isAcceptable());
        assertFalse(flag.isAwaitingQC());
        assertFalse(flag.isCommentFlag());
        assertFalse(flag.isConflicted());
        assertFalse(flag.isEditable());
        assertFalse(flag.isNewAwaitingQC());
        assertTrue(flag.isRenameFlag());
        assertFalse(flag.isUnsubmitted());
        assertFalse(flag.isUpdatedAwaitingQC());
    }


    /**
     * Test of {@link DatasetQCFlag#compareTo(DatasetQCFlag)}
     */
    @Test
    public void testCompareTo() {
        DatasetQCFlag first = new DatasetQCFlag();
        DatasetQCFlag second = new DatasetQCFlag();
        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));

        first.setActualFlag(ACTUAL_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        first.setPiFlag(PI_FLAG);
        first.setAutoFlag(AUTO_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.setActualFlag(ACTUAL_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.setPiFlag(PI_FLAG);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.setAutoFlag(AUTO_FLAG);
        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));

        first.setActualFlag(DatasetQCFlag.Status.SUSPENDED);
        second.setPiFlag(DatasetQCFlag.Status.NOT_GIVEN);
        second.setAutoFlag(DatasetQCFlag.Status.NOT_GIVEN);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
    }

    /**
     * Test of {@link DatasetQCFlag#clone()}
     */
    @Test
    public void testClone() {
        DatasetQCFlag flag = new DatasetQCFlag(ACTUAL_FLAG);
        flag.setPiFlag(PI_FLAG);
        flag.setAutoFlag(AUTO_FLAG);

        DatasetQCFlag dup = flag.clone();
        assertNotSame(flag, dup);
        assertEquals(flag, dup);
    }

    /**
     * Test of {@link DatasetQCFlag#hashCode()} and {@link DatasetQCFlag#equals(Object)}
     */
    @Test
    public void testHashCodeEquals() {
        DatasetQCFlag first = new DatasetQCFlag();
        assertFalse(first.equals(DatasetQCFlag.Status.NOT_GIVEN));
        assertFalse(first.equals(null));

        DatasetQCFlag second = new DatasetQCFlag();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setActualFlag(ACTUAL_FLAG);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setActualFlag(ACTUAL_FLAG);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPiFlag(PI_FLAG);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPiFlag(PI_FLAG);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAutoFlag(AUTO_FLAG);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAutoFlag(AUTO_FLAG);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}