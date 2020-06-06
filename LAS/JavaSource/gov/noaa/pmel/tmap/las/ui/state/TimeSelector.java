package gov.noaa.pmel.tmap.las.ui.state;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeSelector {
    
    /** lo property the lo value of the range that is allowed for this widget. */
    private String lo;
    
    /** hi property the hi value of the range that is allowed for this widget. */
    private String hi;   
    
    /** current_lo property contains the current lo value for this widget. */  
    private String current_lo;
    
    /** current_hi property contains the current hi value for this widget. */
    private String current_hi;
    
    /** Save all this values as DateTime objects so we can easily make comparisons. */
    
    /** lo_dt property the lo value of the range allowed for this widget as a DateTime object. */
    private DateTime lo_dt;
    
    /** hi_dt property the hi value of the range allowed for this widget as a DateTime object. */
    private DateTime hi_dt;
    
    /** current_lo_dt the current selected time as a DateTime object. */
    private DateTime current_lo_dt;
    
    /** current_hi_dt the current selected time as a DateTime object. */
    private DateTime current_hi_dt;
    
    /** type property contains the type for this widget. */
    private String type;
    
    /** yearNeeded property says whether or not to show the year selector. */    
    private boolean yearNeeded;
    
    /** monthNeeded property says whether or not to show the month selector. */
    private boolean monthNeeded;
    
    /** dayNeeded property says whether or not to show the day selector. */
    private boolean dayNeeded;
    
    /** hourNeeded property says whether or not to show the hour selector. */
    private boolean hourNeeded;
    
    /** minuteInterval property number of minutes between time steps on an hour time axis. */
    private double minuteInterval;
    
    /** lo_items property if type=menu then these are the menu items for the lo selector. */
    private StateNameValueList lo_items;
    
    /** hi_items property if type=menu then these are the menu items for the hi selector. */
    private StateNameValueList hi_items;

    public TimeSelector() {
        
    }
    /** Constructor sets initial and current values. */
    public TimeSelector(String value) {
        setCurrent_lo(value);
        setCurrent_hi(value);
    }
    /**
     * @return Returns the dayNeeded.
     */
    public boolean isDayNeeded() {
        return dayNeeded;
    }

    /**
     * @param dayNeeded The dayNeeded to set.
     */
    public void setDayNeeded(boolean dayNeeded) {
        this.dayNeeded = dayNeeded;
    }


    /**
     * @return Returns the monthNeeded.
     */
    public boolean isMonthNeeded() {
        return monthNeeded;
    }

    /**
     * @param monthNeeded The monthNeeded to set.
     */
    public void setMonthNeeded(boolean monthNeeded) {
        this.monthNeeded = monthNeeded;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the yearNeeded.
     */
    public boolean isYearNeeded() {
        return yearNeeded;
    }

    /**
     * @param yearNeeded The yearNeeded to set.
     */
    public void setYearNeeded(boolean yearNeeded) {
        this.yearNeeded = yearNeeded;
    }
    /**
     * @return Returns the hourNeeded.
     */
    public boolean isHourNeeded() {
        return hourNeeded;
    }
    /**
     * @param hourNeeded The hourNeeded to set.
     */
    public void setHourNeeded(boolean hourNeeded) {
        this.hourNeeded = hourNeeded;
    }
    /**
     * @return Returns the hi.
     */
    public String getHi() {
        return hi;
    }
    /**
     * @param hi The hi to set.
     */
    public void setHi(String hi) {
        this.hi = hi;
    }
    /**
     * @return Returns the lo.
     */
    public String getLo() {
        return lo;
    }
    /**
     * @param lo The lo to set.
     */
    public void setLo(String lo) {
        this.lo = lo;
    }
    /**
     * @return Returns the minuteInterval.
     */
    public double getMinuteInterval() {
        return minuteInterval;
    }
    /**
     * @param minuteInterval The minuteInterval to set.
     */
    public void setMinuteInterval(double minuteInterval) {
        this.minuteInterval = minuteInterval;
    }
    /**
     * @return Returns the current_hi.
     */
    public String getCurrent_hi() {
        return current_hi;
    }
    /**
     * @param current_hi The current_hi to set.
     */
    public void setCurrent_hi(String current_hi) {
        this.current_hi = current_hi;
        // If it's not a list menu then set the hi DateTime object.
        if (!getType().equals("menu")) {
            DateTimeFormatter longfmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
            setCurrent_hi_dt(longfmt.parseDateTime(current_hi));
        }
    }
    /**
     * @return Returns the current_lo.
     */
    public String getCurrent_lo() {
        return current_lo;
    }
    /**
     * @param current_lo The current_lo to set.
     */
    public void setCurrent_lo(String current_lo) {
        this.current_lo = current_lo;
        // If it's not a list menu then set the lo DateTime object.
        if (!getType().equals("menu")) {
            DateTimeFormatter longfmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
            setCurrent_lo_dt(longfmt.parseDateTime(current_lo));
        }
    }
    /**
     * @return Returns the hi_items.
     */
    public StateNameValueList getHi_items() {
        return hi_items;
    }
    /**
     * @param hi_items The hi_items to set.
     */
    public void setHi_items(StateNameValueList hi_items) {
        this.current_hi = hi_items.getCurrent();
        this.hi_items = hi_items;
    }
    /**
     * @return Returns the lo_items.
     */
    public StateNameValueList getLo_items() {
        return lo_items;
    }
    /**
     * @param lo_items The lo_items to set.
     */
    public void setLo_items(StateNameValueList lo_items) {
        this.current_lo = lo_items.getCurrent();
        this.lo_items = lo_items;
    }
    /**
     * @return Returns the current_hi_dt.
     */
    public DateTime getCurrent_hi_dt() {
        return current_hi_dt;
    }
    /**
     * @param current_hi_dt The current_hi_dt to set.
     */
    public void setCurrent_hi_dt(DateTime current_hi_dt) {
        this.current_hi_dt = current_hi_dt;
    }
    /**
     * @return Returns the current_lo_dt.
     */
    public DateTime getCurrent_lo_dt() {
        return current_lo_dt;
    }
    /**
     * @param current_lo_dt The current_lo_dt to set.
     */
    public void setCurrent_lo_dt(DateTime current_lo_dt) {
        this.current_lo_dt = current_lo_dt;
    }
    /**
     * @return Returns the hi_dt.
     */
    public DateTime getHi_dt() {
        return hi_dt;
    }
    /**
     * @param hi_dt The hi_dt to set.
     */
    public void setHi_dt(DateTime hi_dt) {
        this.hi_dt = hi_dt;
    }
    /**
     * @return Returns the lo_dt.
     */
    public DateTime getLo_dt() {
        return lo_dt;
    }
    /**
     * @param lo_dt The lo_dt to set.
     */
    public void setLo_dt(DateTime lo_dt) {
        this.lo_dt = lo_dt;
    }
}
