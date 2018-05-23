package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.CommentedQCFlag;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link CommentedQCFlag}
 */
public class CommentedQCFlagTest {

    public static final String MY_FLAG_NAME = "WOCE_CO2_atm";
    public static final String MY_FLAG_VALUE = "4";
    public static final QCFlag.Severity MY_SEVERITY = QCFlag.Severity.ERROR;
    public static final Integer MY_COLUMN_INDEX = 5;
    public static final Integer MY_ROW_INDEX = 25;
    public static final String MY_COMMENT = "This is a weird looking value";

    public void testCommentedQCFlag() {
        CommentedQCFlag commqc = new CommentedQCFlag();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getFlagName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getFlagValue());
        assertEquals(QCFlag.Severity.UNASSIGNED, commqc.getSeverity());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, commqc.getColumnIndex());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, commqc.getRowIndex());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getComment());

        commqc = new CommentedQCFlag(MY_FLAG_NAME, MY_FLAG_VALUE,
                MY_SEVERITY, MY_COLUMN_INDEX, MY_ROW_INDEX, MY_COMMENT);
        assertEquals(MY_FLAG_NAME, commqc.getFlagName());
        assertEquals(MY_FLAG_VALUE, commqc.getFlagValue());
        assertEquals(MY_SEVERITY, commqc.getSeverity());
        assertEquals(MY_COLUMN_INDEX, commqc.getColumnIndex());
        assertEquals(MY_ROW_INDEX, commqc.getRowIndex());
        assertEquals(MY_COMMENT, commqc.getComment());

        commqc = new CommentedQCFlag(null, null, QCFlag.Severity.UNASSIGNED, null, null, null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getFlagName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getFlagValue());
        assertEquals(QCFlag.Severity.UNASSIGNED, commqc.getSeverity());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, commqc.getColumnIndex());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, commqc.getRowIndex());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getComment());

        QCFlag qcflag = new QCFlag(MY_FLAG_NAME, MY_FLAG_VALUE, MY_SEVERITY, MY_COLUMN_INDEX, MY_ROW_INDEX);
        commqc = new CommentedQCFlag(qcflag, MY_COMMENT);
        assertEquals(MY_FLAG_NAME, commqc.getFlagName());
        assertEquals(MY_FLAG_VALUE, commqc.getFlagValue());
        assertEquals(MY_SEVERITY, commqc.getSeverity());
        assertEquals(MY_COLUMN_INDEX, commqc.getColumnIndex());
        assertEquals(MY_ROW_INDEX, commqc.getRowIndex());
        assertEquals(MY_COMMENT, commqc.getComment());
    }

    /**
     * Test of {@link CommentedQCFlag}
     */
    @Test
    public void testGetSetComment() {
        CommentedQCFlag commqc = new CommentedQCFlag();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getComment());
        commqc.setComment(MY_COMMENT);
        assertEquals(MY_COMMENT, commqc.getComment());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, commqc.getRowIndex());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, commqc.getColumnIndex());
        assertEquals(QCFlag.Severity.UNASSIGNED, commqc.getSeverity());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getFlagName());
        commqc.setComment(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, commqc.getComment());
    }

    @Test
    public void testHashCodeEquals() {
        CommentedQCFlag first = new CommentedQCFlag();
        assertFalse(first.equals(null));
        assertFalse(first.equals(DashboardUtils.STRING_MISSING_VALUE));

        // CommentedQCFlag without a comment equals its superclass QCFlag
        QCFlag qcflag = new QCFlag();
        assertTrue(first.equals(qcflag));
        // but the superclass QCFlag object must match
        qcflag = new QCFlag(MY_FLAG_NAME, MY_FLAG_VALUE, MY_SEVERITY, MY_COLUMN_INDEX, MY_ROW_INDEX);
        assertFalse(first.equals(qcflag));

        CommentedQCFlag second = new CommentedQCFlag();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFlagName(MY_FLAG_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFlagName(MY_FLAG_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFlagValue(MY_FLAG_VALUE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFlagValue(MY_FLAG_VALUE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSeverity(MY_SEVERITY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSeverity(MY_SEVERITY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setColumnIndex(MY_COLUMN_INDEX);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setColumnIndex(MY_COLUMN_INDEX);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setRowIndex(MY_ROW_INDEX);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRowIndex(MY_ROW_INDEX);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        // CommentedQCFlag without a comment equals its superclass QCFlag
        qcflag = new QCFlag(MY_FLAG_NAME, MY_FLAG_VALUE, MY_SEVERITY, MY_COLUMN_INDEX, MY_ROW_INDEX);
        assertEquals(first.hashCode(), qcflag.hashCode());
        assertTrue(first.equals(qcflag));
        // but the superclass QCFlag object must match
        qcflag = new QCFlag();
        assertNotEquals(first.hashCode(), qcflag.hashCode());
        assertFalse(first.equals(qcflag));

        first.setComment(MY_COMMENT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setComment(MY_COMMENT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

    @Test
    public void testCompareTo() {
        QCFlag firstFlag = new QCFlag(MY_FLAG_NAME, MY_FLAG_VALUE, MY_SEVERITY, MY_COLUMN_INDEX, MY_ROW_INDEX);
        CommentedQCFlag first = new CommentedQCFlag(firstFlag, null);
        CommentedQCFlag second = new CommentedQCFlag("WOCE_CO2_water", "2", QCFlag.Severity.ACCEPTABLE,
                4, 15, "And this as well");

        // A CommentedQCFlag with no comment is the same as its superclass QCFlag
        assertEquals(0, first.compareTo(firstFlag));
        assertEquals(0, firstFlag.compareTo(first));
        first.setComment(MY_COMMENT);
        assertTrue(first.compareTo(firstFlag) > 0);
        // But of course, QCFlag.compareTo ignores the comment
        assertEquals(0, firstFlag.compareTo(first));

        // flag name first comparison
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setFlagName("WOCE_Patm");
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setFlagName(MY_FLAG_NAME);

        // flag value next comparison
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setFlagValue("9");
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setFlagValue(MY_FLAG_VALUE);

        // severity next comparison
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setSeverity(QCFlag.Severity.CRITICAL);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setSeverity(MY_SEVERITY);

        // column index next comparison
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setColumnIndex(6);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setColumnIndex(MY_COLUMN_INDEX);

        // row index next comparison
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setRowIndex(35);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setRowIndex(MY_ROW_INDEX);

        // comment last comparison
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setComment("What a weird value");
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setComment(MY_COMMENT);

        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));
    }

}