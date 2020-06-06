package gov.noaa.pmel.tmap.las.service.kml;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTime;
import org.joda.time.Period;

import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;

import java.util.HashMap;
import java.io.IOException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * A point that represents an insitu observation
 * @author Jing Yang Li
 */
public class LASInsituPoint{

    private static Logger log = LoggerFactory.getLogger(LASInsituPoint.class);

    private String lat;
    private String lon;
    private String time;

    public LASInsituPoint(String lat, String lon){
        this.lat   = lat;
        this.lon   = lon;
    }

    public LASInsituPoint(String lat, String lon, String time){
        this.lat   = lat;
        this.lon   = lon;
        this.time  = time;
    }

    public String getLon(){
        return lon;
    }

    public String getLat(){
        return lat;
    }


    public String getTime(){
	    return time;
    }

    public void setLon(String lon){
        this.lon = lon;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public void setTime(String time){
        this.time = time;
    }

    public boolean equals(Object p){

        if(this == p) {  // Step 1: Perform an == test
            return true;
        }

        if(!(p instanceof LASInsituPoint)) {  // Step 2: Instance of check
            return false;
        }

        //step 3: cast p
        LASInsituPoint gp = (LASInsituPoint) p;

        //Step 4: For each important field, check to see if they are equal
        return ( lon.equals(gp.getLon()) && lat.equals(gp.getLat()) );
    }

    /*
     * compute hash code
     */
    public int hashCode(){
        return lon.hashCode() + lat.hashCode();
    }
}
