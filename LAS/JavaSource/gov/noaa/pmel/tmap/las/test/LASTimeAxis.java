package gov.noaa.pmel.tmap.las.test;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class LASTimeAxis{
    
    /** style represents how time axis was defined in dataset configuration file 
        either by <v> or <arange> tag
    */
    private String style;
    
    /** unit */
    private String units;
    
    /** size */
    private double size;
    
    /** step */
    private double step;
    
    private DateTime lodt;
    private DateTime hidt;

    public void setStyle(String sy){
        style = sy;
    }

    public void setUnit(String ut){
        units = ut;
    }

    public void setSize(double sz ){
        size = sz;
    }

    public void setStep(double sp){
        step = sp;
    }
    
    public void setLoDateTime(DateTime lo){
        lodt = lo;
    }

    public void setHiDateTime(DateTime hi){
        hidt = hi;
    }

    public String getStyle(){
        return style;
    }

    public String getUnit(){
        return units;
    }

    public double getSize(){
        return size;
    }

    public double getStep(){
        return step;
    }
    public DateTime getLoDatetTime(){
        return lodt;
    }

    public DateTime getHiDateTime(){
        if(hidt == null){
        	computeHiDateTime();
        }
        return hidt;
    }
    
    public void computeHiDateTime(){
    	if(lodt != null){ 		
    		if ( units.contains("hour") ) {   
                int hours = (int) Math.round((size-1)*step);
                int minuteInterval = (int) Math.round(step*60.);              
                hidt = lodt.plus(Period.hours(hours));                
            } else if ( units.contains("day") ) {                
                int days = (int) Math.round((size-1)*step);
                hidt = lodt.plus(Period.days(days));
            } else if ( units.contains("month") ) {                
                int months = (int) Math.round((size-1)*step);
                hidt = lodt.plus(Period.months(months));               
            } else if ( units.contains("year") ) {               
                //double start = Double.valueOf(tlo).doubleValue();
                //int years = (int) Math.round(start + (size-1)*step);
            	int years = (int) Math.round((size-1)*step);
                hidt = lodt.plus(Period.years(years));
            }
    	}
    }
}
