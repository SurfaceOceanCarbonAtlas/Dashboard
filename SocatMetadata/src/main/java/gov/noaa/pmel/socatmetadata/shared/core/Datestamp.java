package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Represents a simple UTC date and time as year, month, day, hour, minute, second
 * with minimal support.
 */
public final class Datestamp implements Serializable, IsSerializable {

    private static final long serialVersionUID = -3324033462558122150L;

    public static final Datestamp MIN_VALID_DATESTAMP = new Datestamp(1900, 1, 1, 0, 0, 0);
    public static final Datestamp DEFAULT_TODAY_DATESTAMP = new Datestamp(2099, 12, 31, 23, 59, 59);

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
     * Create with invalid values ({@link #INVALID}) for year, month, and day.
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
     * Create with the given year, month, day, hour, minute, and second.
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
     * No check is made as to whether this is a valid date.
     *
     * @throws IllegalArgumentException
     *         if any values are null or not integer strings.
     */
    public Datestamp(String year, String month, String day,
            String hour, String minute, String second) throws IllegalArgumentException {
        try {
            this.year = Integer.parseInt(year);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid year '" + year + "': " + ex.getMessage());
        }
        try {
            this.month = Integer.parseInt(month);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid month '" + month + "': " + ex.getMessage());
        }
        try {
            this.day = Integer.parseInt(day);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid day '" + day + "': " + ex.getMessage());
        }
        try {
            this.hour = Integer.parseInt(hour);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid hour '" + hour + "': " + ex.getMessage());
        }
        try {
            this.minute = Integer.parseInt(minute);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid minute '" + minute + "': " + ex.getMessage());
        }
        try {
            this.second = Integer.parseInt(second);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid second '" + second + "': " + ex.getMessage());
        }
    }

    /**
     * @param today
     *         a Datestamp representing the current day; if null, {@link #DEFAULT_TODAY_DATESTAMP} is used
     *
     * @return if the date is valid, not earlier than {{@link #MIN_VALID_DATESTAMP}}
     *         and not later than the given current day
     */
    public boolean isValid(Datestamp today) {
        if ( (month < 1) || (month > 12) || (day < 1) || (day > 31) || (hour < 0) ||
                (hour > 23) || (minute < 0) || (minute > 59) || (second < 0) || (second > 59) )
            return false;
        if ( (year < MIN_VALID_DATESTAMP.year) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month < MIN_VALID_DATESTAMP.month)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day < MIN_VALID_DATESTAMP.day)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day == MIN_VALID_DATESTAMP.day)
                        && (hour < MIN_VALID_DATESTAMP.hour)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day == MIN_VALID_DATESTAMP.day)
                        && (hour == MIN_VALID_DATESTAMP.hour) && (minute < MIN_VALID_DATESTAMP.minute)) ||
                ((year == MIN_VALID_DATESTAMP.year) && (month == MIN_VALID_DATESTAMP.month) && (day == MIN_VALID_DATESTAMP.day)
                        && (hour == MIN_VALID_DATESTAMP.hour) && (minute == MIN_VALID_DATESTAMP.minute) && (second < MIN_VALID_DATESTAMP.second)) )
            return false;
        if ( today == null )
            today = DEFAULT_TODAY_DATESTAMP;
        if ( (year > today.year) ||
                ((year == today.year) && (month > today.month)) ||
                ((year == today.year) && (month == today.month) && (day > today.day)) ||
                ((year == today.year) && (month == today.month) && (day == today.day)
                        && (hour > today.hour)) ||
                ((year == today.year) && (month == today.month) && (day == today.day)
                        && (hour == today.hour) && (minute > today.minute)) ||
                ((year == today.year) && (month == today.month) && (day == today.day)
                        && (hour == today.hour) && (minute == today.minute) && (second > today.second)) )
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
     * @return the time string as HH:mm:ss
     *
     * @throws IllegalArgumentException
     *         if the Datestamp is invalid
     */
    public String timeString() throws IllegalArgumentException {
        if ( !isValid(null) )
            throw new IllegalArgumentException("invalid date");
        String stamp;
        if ( hour < 10 )
            stamp = "0" + hour;
        else
            stamp = "" + hour;
        if ( minute < 10 )
            stamp += ":0" + minute;
        else
            stamp += ":" + minute;
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
     *
     * @param other
     *         Datestamp to compare to
     *
     * @return true if this Datestamp specifies a date before to the given Datestamp; otherwise false
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
        if ( hour < other.hour )
            return true;
        if ( hour > other.hour )
            return false;
        if ( minute < other.minute )
            return true;
        if ( minute > other.minute )
            return false;
        if ( second < other.second )
            return true;
        if ( second > other.second )
            return false;
        // Equal and thus not before
        return false;
    }

    /**
     * Compares the date specified in this Datestamp to the date specified in the given Datestamp.
     *
     * @param other
     *         Datestamp to compare to
     *
     * @return true if this Datestamp specifies a date after to the given Datestamp; otherwise false
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
        if ( hour > other.hour )
            return true;
        if ( hour < other.hour )
            return false;
        if ( minute > other.minute )
            return true;
        if ( minute < other.minute )
            return false;
        if ( second > other.second )
            return true;
        if ( second < other.second )
            return false;
        // Equal and thus not before
        return false;
    }

    /**
     * Deeply copies the values in this Datestamp object to the given Datestamp object.
     *
     * @param dup
     *         the Datestamp object to copy values into;
     *         if null, a new Datestamp object is created for copying values into
     *
     * @return the updated Datestamp object
     */
    public Datestamp duplicate(Datestamp dup) {
        if ( dup == null )
            dup = new Datestamp();
        dup.year = year;
        dup.month = month;
        dup.day = day;
        dup.hour = hour;
        dup.minute = minute;
        dup.second = second;
        return dup;
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
