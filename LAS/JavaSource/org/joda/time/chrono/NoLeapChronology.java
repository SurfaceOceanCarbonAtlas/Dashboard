/**
 * 
 */
package org.joda.time.chrono;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Roland Schweitzer
 *
 */
public class NoLeapChronology extends BasicGJChronology {
    
    /**
     * 
     */
    private static final long serialVersionUID = 206798718536694977L;
    
    /* 365*1971 */
    private static final int DAYS_0000_TO_1970 = 719415;
    
    private static final long MILLIS_PER_YEAR =
        (long) (365L * DateTimeConstants.MILLIS_PER_DAY);
    
    private static final long MILLIS_PER_MONTH =
        (long) ((365L * DateTimeConstants.MILLIS_PER_DAY) / 12L);
    
    /** The lowest year that can be fully supported. */
    private static final int MIN_YEAR = -292275054;

    /** Singleton instance of a UTC GregorianChronology */
    private static final NoLeapChronology INSTANCE_UTC;
    
    /** The highest year that can be fully supported. */
    private static final int MAX_YEAR = 292278993;
    
    /** Cache of zone to chronology arrays */
    private static final Map cCache = new HashMap();
    
    /**
     *  Same constructor as super class.
     */
    public NoLeapChronology(Chronology base, Object param, int minDaysInFirstWeek) {
        super(base, param, minDaysInFirstWeek);
    }
    
    
    public boolean isLeapYear(int year) {
        return false;
    }
    
    
    long calculateFirstDayOfYearMillis(int year) {
        return (year * 365L - DAYS_0000_TO_1970) * DateTimeConstants.MILLIS_PER_DAY;
    }
    
    long getAverageMillisPerYear() {
        return MILLIS_PER_YEAR;
    }
    
    long getAverageMillisPerYearDividedByTwo() {
        return MILLIS_PER_YEAR / 2L;
    }
    
    long getAverageMillisPerMonth() {
        return MILLIS_PER_MONTH;
    }
    int getMinYear() {
        return MIN_YEAR;
    }

    int getMaxYear() {
        return MAX_YEAR;
    }
    static {
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }

    /**
     * Gets an instance of the GregorianChronology.
     * The time zone of the returned instance is UTC.
     * 
     * @return a singleton UTC instance of the chronology
     */
    public static NoLeapChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }
    /**
     * Gets an instance of the GregorianChronology in the default time zone.
     * 
     * @return a chronology in the default time zone
     */
    public static NoLeapChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), 4);
    }
    
    public static NoLeapChronology getInstance(DateTimeZone zone) {
        return getInstance(zone, 4);
    }
    
    long getApproxMillisAtEpochDividedByTwo() {
        return (1970L * MILLIS_PER_YEAR) / 2;
    }
    
    //  Conversion
    //-----------------------------------------------------------------------
    /**
     * Gets the Chronology in the UTC time zone.
     * 
     * @return the chronology in UTC
     */
    public Chronology withUTC() {
        return INSTANCE_UTC;
    }

    /**
     * Gets the Chronology in a specific time zone.
     * 
     * @param zone  the zone to get the chronology in, null is default
     * @return the chronology
     */
    public Chronology withZone(DateTimeZone zone) {
        if (zone == null) {
            zone = DateTimeZone.getDefault();
        }
        if (zone == getZone()) {
            return this;
        }
        return getInstance(zone);
    }
    
    /**
     * Gets an instance of the GregorianChronology in the given time zone.
     * 
     * @param zone  the time zone to get the chronology in, null is default
     * @param minDaysInFirstWeek  minimum number of days in first week of the year; default is 4
     * @return a chronology in the specified time zone
     */
    public static NoLeapChronology getInstance(DateTimeZone zone, int minDaysInFirstWeek) {
        if (zone == null) {
            zone = DateTimeZone.getDefault();
        }
        NoLeapChronology chrono;
        synchronized (cCache) {
            NoLeapChronology[] chronos = (NoLeapChronology[]) cCache.get(zone);
            if (chronos == null) {
                chronos = new NoLeapChronology[7];
                cCache.put(zone, chronos);
            }
            try {
                chrono = chronos[minDaysInFirstWeek - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException
                    ("Invalid min days in first week: " + minDaysInFirstWeek);
            }
            if (chrono == null) {
                if (zone == DateTimeZone.UTC) {
                    chrono = new NoLeapChronology(null, null, minDaysInFirstWeek);
                } else {
                    chrono = getInstance(DateTimeZone.UTC, minDaysInFirstWeek);
                    chrono = new NoLeapChronology
                        (ZonedChronology.getInstance(chrono, zone), null, minDaysInFirstWeek);
                }
                chronos[minDaysInFirstWeek - 1] = chrono;
            }
        }
        return chrono;
    }
    public static void main(String[] args) {
        Chronology nl = NoLeapChronology.getInstance(DateTimeZone.UTC);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dt = new DateTime(2000, 2, 27, 0, 0, 0, 0, nl);
        for ( int i = 0; i < 10; i++ ) {
            System.out.println(fmt.print(dt.plusDays(i)));
        }
    }
}
