package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Represents a simple UTC date and time as year, month, day, hour, minute, second
 * with minimal support.
 */
public final class Datestamp implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -3033185888931897459L;

    private static final int FIRST_VALID_YEAR = 1900;
    private static final int LAST_VALID_YEAR = 2099;
    public static final Datestamp MIN_VALID_DATESTAMP = new Datestamp(FIRST_VALID_YEAR, 1, 1, 0, 0, 0);
    public static final Datestamp DEFAULT_TODAY_DATESTAMP = new Datestamp(LAST_VALID_YEAR, 12, 31, 23, 59, 59);

    /**
     * Integer assigned for invalid year, month, day, hour, minute, or second
     */
    public static final int INVALID = -1;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    /**
     * Create with invalid values ({@link #INVALID}) for year, month, day, hour, minute, and second.
     */
    public Datestamp() {
        year = INVALID;
        month = INVALID;
        day = INVALID;
        hour = INVALID;
        minute = INVALID;
        second = INVALID;
    }

    /**
     * Create with the given year, month, day, hour, minute, and second integer values.
     * No check is made as to whether this is a valid date.
     */
    public Datestamp(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Create with the given year, month, day, hour, minute, and second integer string values
     * {@link #INVALID} is assigned for any values that are not integer strings.
     * No check is made as to whether this is a valid date.
     */
    public Datestamp(String year, String month, String day, String hour, String minute, String second) {
        this();
        try {
            this.year = Integer.parseInt(year);
        } catch ( NullPointerException | NumberFormatException ex ) {
            // leave as INVALID
        }
        try {
            this.month = Integer.parseInt(month);
        } catch ( NullPointerException | NumberFormatException ex ) {
            // leave as INVALID
        }
        try {
            this.day = Integer.parseInt(day);
        } catch ( NullPointerException | NumberFormatException ex ) {
            // leave as INVALID
        }
        try {
            this.hour = Integer.parseInt(hour);
        } catch ( NullPointerException | NumberFormatException ex ) {
            // leave as INVALID
        }
        try {
            this.minute = Integer.parseInt(minute);
        } catch ( NullPointerException | NumberFormatException ex ) {
            // leave as INVALID
        }
        try {
            this.second = Integer.parseInt(second);
        } catch ( NullPointerException | NumberFormatException ex ) {
            // leave as INVALID
        }
    }

    /**
     * Checks if this Datestmap represents a valid date and, if given, time.
     * Any time components that are {@link #INVALID} are assumed to be zero for this check.
     *
     * @param today
     *         a Datestamp representing the current day; if null, {@link #DEFAULT_TODAY_DATESTAMP} is used
     *
     * @return if the date is valid, not earlier than {{@link #MIN_VALID_DATESTAMP}}
     *         and not later than the given current day
     */
    public boolean isValid(Datestamp today) {
        // If hour, minute, or second was not specified, assume zero instead of being invalid
        int adjHour = (hour == INVALID) ? 0 : hour;
        int adjMinute = (minute == INVALID) ? 0 : minute;
        int adjSecond = (second == INVALID) ? 0 : second;
        if ( (month < 1) || (month > 12) || (day < 1) || (day > 31) || (adjHour < 0) ||
                (adjHour > 23) || (adjMinute < 0) || (adjMinute > 59) || (adjSecond < 0) || (adjSecond > 59) )
            return false;
        if ( (year < MIN_VALID_DATESTAMP.year) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month < MIN_VALID_DATESTAMP.month)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day < MIN_VALID_DATESTAMP.day)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day == MIN_VALID_DATESTAMP.day)
                        && (adjHour < MIN_VALID_DATESTAMP.hour)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day == MIN_VALID_DATESTAMP.day)
                        && (adjHour == MIN_VALID_DATESTAMP.hour) && (adjMinute < MIN_VALID_DATESTAMP.minute)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day == MIN_VALID_DATESTAMP.day)
                        && (adjHour == MIN_VALID_DATESTAMP.hour) && (adjMinute == MIN_VALID_DATESTAMP.minute) && (adjSecond < MIN_VALID_DATESTAMP.second)) )
            return false;
        if ( today == null )
            today = DEFAULT_TODAY_DATESTAMP;
        if ( (year > today.year) ||
                ((year == today.year) && (month > today.month)) ||
                ((year == today.year) && (month == today.month) && (day > today.day)) ||
                ((year == today.year) && (month == today.month) && (day == today.day)
                        && (adjHour > today.hour)) ||
                ((year == today.year) && (month == today.month) && (day == today.day)
                        && (adjHour == today.hour) && (adjMinute > today.minute)) ||
                ((year == today.year) && (month == today.month) && (day == today.day)
                        && (adjHour == today.hour) && (adjMinute == today.minute) && (adjSecond > today.second)) )
            return false;
        if ( (day > 30) && ((month == 4) || (month == 6) || (month == 9) || (month == 11)) )
            return false;
        if ( (day > 29) && (month == 2) )
            return false;
        if ( (day > 28) && (month == 2) && (((year % 4) != 0) || (((year % 100) == 0) && ((year % 400) != 0))) )
            return false;
        return true;
    }

    /**
     * @return the date string as yyyy-MM-dd
     *
     * @throws IllegalArgumentException
     *         if the Datestamp is invalid
     */
    public String dateString() throws IllegalArgumentException {
        if ( !isValid(null) )
            throw new IllegalArgumentException("invalid date");
        String stamp = "" + year;
        if ( month < 10 )
            stamp += "-0" + month;
        else
            stamp += "-" + month;
        if ( day < 10 )
            stamp += "-0" + day;
        else
            stamp += "-" + day;
        return stamp;
    }

    /**
     * @return the time string as:
     *         HH (if minute is {@link #INVALID}) or
     *         HH:mm (if second is {@link #INVALID}) or
     *         HH:mm:ss
     *
     * @throws IllegalArgumentException
     *         if the Datestamp is invalid or
     *         if the hour is invalid
     */
    public String timeString() throws IllegalArgumentException {
        if ( !isValid(null) )
            throw new IllegalArgumentException("invalid date");
        // hour, minute, or second could be INVALID
        // but require a valid hour for a time string
        if ( hour == INVALID )
            throw new IllegalArgumentException("invalid hour");
        String stamp;
        if ( hour < 10 )
            stamp = "0" + hour;
        else
            stamp = "" + hour;
        if ( minute != INVALID ) {
            if ( minute < 10 )
                stamp += ":0" + minute;
            else
                stamp += ":" + minute;
            if ( second != INVALID ) {
                if ( second < 10 )
                    stamp += ":0" + second;
                else
                    stamp += ":" + second;
            }
        }
        return stamp;
    }

    /**
     * @return a date/time stamp of the form yyyy-MM-dd HH:mm:ss up to the point where an invalid entry is encountered.
     *         So Datetime(2000,2,30,12,24,36) will return "2000-02" since the day is invalid for the year and month,
     *         and Datetime(2000,2,28,12,24,-1) will return "2000-02-28 12:24".  If the year is invalid (before
     *         the year of {@link #MIN_VALID_DATESTAMP} or after the year of {@link #DEFAULT_TODAY_DATESTAMP}),
     *         an empty string is returned.
     */
    public String fullOrPartialString() {
        String stamp = "";
        if ( (year < FIRST_VALID_YEAR) || (year > LAST_VALID_YEAR) )
            return stamp;
        stamp += year;

        if ( (month < 1) || (month > 12) )
            return stamp;
        if ( month < 10 )
            stamp += "-0" + month;
        else
            stamp += "-" + month;

        if ( day < 1 )
            return stamp;
        if ( (day > 30) && ((month == 4) || (month == 6) || (month == 9) || (month == 11)) )
            return stamp;
        if ( (day > 29) && (month == 2) )
            return stamp;
        if ( (day > 28) && (month == 2) && (((year % 4) != 0) || (((year % 100) == 0) && ((year % 400) != 0))) )
            return stamp;
        if ( day < 10 )
            stamp += "-0" + day;
        else
            stamp += "-" + day;

        if ( (hour < 0) || (hour > 23) )
            return stamp;
        if ( hour < 10 )
            stamp += " 0" + hour;
        else
            stamp += " " + hour;

        if ( (minute < 0) || (minute > 59) )
            return stamp;
        if ( minute < 10 )
            stamp += ":0" + minute;
        else
            stamp += ":" + minute;

        if ( (second < 0) || (second > 59) )
            return stamp;
        if ( second < 10 )
            stamp += ":0" + second;
        else
            stamp += ":" + second;

        return stamp;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year
     *         assign as the year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month
     *         assign as the month
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @param day
     *         assign as the day
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return the hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * @param hour
     *         assign as the hour
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * @return the minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * @param minute
     *         assign as the minute
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * @return the second
     */
    public int getSecond() {
        return second;
    }

    /**
     * @param second
     *         assign as the second
     */
    public void setSecond(int second) {
        this.second = second;
    }

    /**
     * Compares the date specified in this Datestamp to the date specified in the given Datestamp.
     * Time comparisons are only made until the an invalid hour, minute, or second in either
     * this Datestamp or the given Datestamp; for example, minutes are compared if hours are
     * valid in both and minutes are valid in both.  Datestamps that have dates and whatever part
     * of time is compared that are identical are considered equal and, thus, will return false.
     *
     * @param other
     *         Datestamp to compare to
     *
     * @return true if this Datestamp specifies a date before to the given Datestamp;
     *         otherwise false
     *
     * @throws IllegalArgumentException
     *         if either this Datestamp or the given Datestamp to is invalid
     */
    public boolean before(Datestamp other) throws IllegalArgumentException {
        if ( !isValid(null) )
            throw new IllegalArgumentException("before method being called on an invalid date");
        if ( !other.isValid(null) )
            throw new IllegalArgumentException("before method being called with an invalid date");

        if ( year < other.year )
            return true;
        if ( year > other.year )
            return false;

        if ( month < other.month )
            return true;
        if ( month > other.month )
            return false;

        if ( day < other.day )
            return true;
        if ( day > other.day )
            return false;

        // If an hour is invalid, do not compare time.  Dates are identical, so false.
        if ( (hour == INVALID) || (other.hour == INVALID) )
            return false;
        if ( hour < other.hour )
            return true;
        if ( hour > other.hour )
            return false;

        // If a minute is invalid, do not compare minutes and seconds.  Dates and hours are identical, so false.
        if ( (minute == INVALID) || (other.minute == INVALID) )
            return false;
        if ( minute < other.minute )
            return true;
        if ( minute > other.minute )
            return false;

        // If a second is invalid, do not compare seconds.  Dates, hours, and minutes are identical, so false.
        if ( (second == INVALID) || (other.second == INVALID) )
            return false;
        if ( second < other.second )
            return true;
        if ( second > other.second )
            return false;

        // Equal dates and times so not before
        return false;
    }

    /**
     * Compares the date specified in this Datestamp to the date specified in the given Datestamp.
     * Time comparisons are only made until the an invalid hour, minute, or second in either
     * this Datestamp or the given Datestamp; for example, minutes are compared if hours are
     * valid in both and minutes are valid in both.  Datestamps that have dates and whatever part
     * of time is compared that are identical are considered equal and, thus, will return false.
     *
     * @param other
     *         Datestamp to compare to
     *
     * @return true if this Datestamp specifies a date after to the given Datestamp;
     *         otherwise false
     *
     * @throws IllegalArgumentException
     *         if either this Datestamp or the given Datestamp to is invalid
     */
    public boolean after(Datestamp other) throws IllegalArgumentException {
        if ( !isValid(null) )
            throw new IllegalArgumentException("after method being called on an invalid date");
        if ( !other.isValid(null) )
            throw new IllegalArgumentException("after method being called with an invalid date");

        if ( year > other.year )
            return true;
        if ( year < other.year )
            return false;

        if ( month > other.month )
            return true;
        if ( month < other.month )
            return false;

        if ( day > other.day )
            return true;
        if ( day < other.day )
            return false;

        // If an hour is invalid, do not compare time.  Dates are identical, so false.
        if ( (hour == INVALID) || (other.hour == INVALID) )
            return false;
        if ( hour > other.hour )
            return true;
        if ( hour < other.hour )
            return false;

        // If a minute is invalid, do not compare minutes and seconds.  Dates and hours are identical, so false.
        if ( (minute == INVALID) || (other.minute == INVALID) )
            return false;
        if ( minute > other.minute )
            return true;
        if ( minute < other.minute )
            return false;

        // If a second is invalid, do not compare seconds.  Dates, hours, and minutes are identical, so false.
        if ( (second == INVALID) || (other.second == INVALID) )
            return false;
        if ( second > other.second )
            return true;
        if ( second < other.second )
            return false;

        // Equal dates and times so not after
        return false;
    }

    @Override
    public Object duplicate(Object dup) {
        Datestamp stamp;
        if ( dup == null )
            stamp = new Datestamp();
        else
            stamp = (Datestamp) dup;
        stamp.year = year;
        stamp.month = month;
        stamp.day = day;
        stamp.hour = hour;
        stamp.minute = minute;
        stamp.second = second;
        return stamp;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Datestamp) )
            return false;

        Datestamp datestamp = (Datestamp) obj;

        if ( year != datestamp.year )
            return false;
        if ( month != datestamp.month )
            return false;
        if ( day != datestamp.day )
            return false;
        if ( hour != datestamp.hour )
            return false;
        if ( minute != datestamp.minute )
            return false;
        if ( second != datestamp.second )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = Integer.hashCode(year);
        result = result * prime + Integer.hashCode(month);
        result = result * prime + Integer.hashCode(day);
        result = result * prime + Integer.hashCode(hour);
        result = result * prime + Integer.hashCode(minute);
        result = result * prime + Integer.hashCode(second);
        return result;
    }

    @Override
    public String toString() {
        return "Datestamp{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';
    }

}
