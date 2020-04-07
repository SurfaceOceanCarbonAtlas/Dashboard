package gov.noaa.pmel.socatmetadata.shared.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents and works with a date (without time)
 */
public final class Datestamp implements Cloneable, Serializable {

    private static final long serialVersionUID = 3044668752341242678L;

    /**
     * Integer returned for invalid year, month, or day
     */
    public static final Integer INVALID = 0;

    private static final DateFormat TIMESTAMP_PARSER;

    static {
        TIMESTAMP_PARSER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TIMESTAMP_PARSER.setTimeZone(TimeZone.getTimeZone("UTC"));
        TIMESTAMP_PARSER.setLenient(false);
    }

    private Integer year;
    private Integer month;
    private Integer day;

    /**
     * Create with invalid values ({@link #INVALID}) for year, month, and day.
     */
    public Datestamp() {
        year = INVALID;
        month = INVALID;
        day = INVALID;
    }

    /**
     * Create with the given integer string values for year, month, and day.
     *
     * @throws IllegalArgumentException
     *         if any values are null or not integer strings.
     */
    public Datestamp(String year, String month, String day) throws IllegalArgumentException {
        try {
            this.year = Integer.valueOf(year);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid year '" + year + "': " + ex.getMessage());
        }
        try {
            this.month = Integer.valueOf(month);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid month '" + month + "': " + ex.getMessage());
        }
        try {
            this.day = Integer.valueOf(day);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Invalid day '" + day + "': " + ex.getMessage());
        }
    }

    /**
     * @return Date corresponding to the earliest time (00:00:00) of the datestamp.
     *
     * @throws IllegalStateException
     *         if the date is not valid,
     *         is earlier than 1900-01-01, or
     *         is later than the current date.
     */
    public Date getEarliestTime() throws IllegalStateException {
        if ( (year < 1900) || (year > 9999) )
            throw new IllegalStateException("invalid year: " + year);
        if ( (month < 1) || (month > 12) )
            throw new IllegalStateException("invalid month: " + month);
        if ( (day < 1) || (day > 31) )
            throw new IllegalStateException("invalid day: " + day);
        String timestamp = String.format("%04d-%02d-%02d 00:00:00", year, month, day);
        Date value;
        try {
            value = TIMESTAMP_PARSER.parse(timestamp);
        } catch ( Exception ex ) {
            throw new IllegalStateException("invalid timestamp '" + timestamp + "': " + ex.getMessage());
        }
        if ( value.after(new Date()) )
            throw new IllegalStateException("'" + timestamp + "' is in the future");
        return value;
    }

    /**
     * @return the date string in "yyyy-MM-dd" format
     *
     * @throws IllegalStateException
     *         if the date is not valid,
     *         is earlier than 1900-01-01, or
     *         is later than the current date.
     */
    public String stampString() throws IllegalStateException {
        // validate the date, allowing exceptions to propagate if invalid
        getEarliestTime();
        // return the date stamp
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    /**
     * @return if the date is valid, not earlier than 1900-01-01 and not later than the current date.
     */
    public boolean isValid() {
        try {
            getEarliestTime();
        } catch ( IllegalStateException ex ) {
            return false;
        }
        return true;
    }

    /**
     * @return the year; never null but may be invalid ({@link #INVALID}) if not assigned
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year
     *         assign as the year; if null, an invalid value ({@link #INVALID}) is assigned
     */
    public void setYear(Integer year) {
        this.year = (year != null) ? year : INVALID;
    }

    /**
     * @return the month; never null but may be invalid ({@link #INVALID}) if not assigned
     */
    public Integer getMonth() {
        return month;
    }

    /**
     * @param month
     *         assign as the month; if null, an invalid value ({@link #INVALID}) is assigned
     */
    public void setMonth(Integer month) {
        this.month = (month != null) ? month : INVALID;
    }

    /**
     * @return the day; never null but may be invalid ({@link #INVALID}) if not assigned
     */
    public Integer getDay() {
        return day;
    }

    /**
     * @param day
     *         assign as the day; if null, an invalid value ({@link #INVALID}) is assigned
     */
    public void setDay(Integer day) {
        this.day = (day != null) ? day : INVALID;
    }

    @Override
    public Datestamp clone() {
        Datestamp dup;
        try {
            dup = (Datestamp) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.year = year;
        dup.month = month;
        dup.day = day;
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

        if ( !year.equals(datestamp.year) )
            return false;
        if ( !month.equals(datestamp.month) )
            return false;
        if ( !day.equals(datestamp.day) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = year.hashCode();
        result = result * prime + month.hashCode();
        result = result * prime + day.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Datestamp{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }

}
