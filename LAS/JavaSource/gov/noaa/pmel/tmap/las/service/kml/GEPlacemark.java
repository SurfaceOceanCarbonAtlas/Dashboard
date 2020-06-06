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
 * A single Google Earth placemark
 * @author Jing Yang Li
 */
public class GEPlacemark{

    private static Logger log = LoggerFactory.getLogger(GEPlacemark.class);

    String kmlString;
    String point_lat;
    String point_lon;
    String point_time;
    String name;
    String style;
    String description;
    String timeStamp;
    String region;

    public GEPlacemark(String lat, String lon){
        point_lat   = lat;
        point_lon   = lon;
        checkPointLon();
    }

    public GEPlacemark(String lat, String lon, String sty, String des){
        point_lat   = lat;
        point_lon   = lon;
        checkPointLon();
        style = sty;
        description =des;
    }

    public String getPointLon(){
        return point_lon;
    }

    public String getPointLat(){
        return point_lat;
    }

    public void setPointTime(String time){
	    point_time = time;
	}

	public String getPointTime(){
	    return point_time;
    }

    public void setTimeStamp(String st){
	    timeStamp = st;
	}
    /**
     * make a placemark kml
     */
    private void makeKMLString(){
        kmlString = "<Placemark><visibility>1</visibility>";

        if(name != "" && name != null){
		            kmlString +="<name>"+name+"</name>";
	    }

        //set time stamp
        //we may not always want this
        //TODO: need to add an UI option: animatePlacemark
        /*
        if(point_time != "" && point_time!=null){
			kmlString +="<TimeStamp><when>"+point_time+"</when></TimeStamp>";
	    }
	    */


        if(description != "" && description != null){
            kmlString += description;
        }

        if(style != "" && style != null){
            kmlString += style;
        }

        if(region != "" && region != null){
		    kmlString += region;
		}

        kmlString += "<styleUrl>#style1_roll_over_labels_Earthwatch</styleUrl>";
        kmlString += "<Point>"
                    + "<coordinates>"+point_lon+","+point_lat+","+"0</coordinates>"
                    + "</Point>"
                    + "</Placemark>";
    }

    /**
     * set style
     * ---only icon style
     */
    public void setStyle(String st){
        style = st;
    }

    /**
     * set description
     */
    public void setDescription(String ds){
        description = ds;
    }

    public void setName(String name){
	    this.name = name;
	}

    public void setRegion(String region){
		this.region = region;
	}

    /**
     * return the place marks in kml format
     */
    public String toString(){
        makeKMLString();
        return kmlString;
    }

    /**
     * check longitude and convert it to be in [-180,180]
     */
    private void checkPointLon(){
        double glon = Double.parseDouble(point_lon);
        if(glon > 180.0){
            double glon360 = glon % 360.0;
            //west
            if(glon360 > 180.0){
                point_lon = Double.toString(glon360-360.0);
                //System.out.println(point_lon);
            //east
            }else{
                point_lon = Double.toString(glon360);
                //System.out.println(point_lon);
            }
        }
        if(glon < -180.0){
            double glon360 = glon % 360.0;
            //east
            if(glon360 < -180.0){
                point_lon = Double.toString(glon360+360.0);
            //west
            }else{
                point_lon = Double.toString(glon360);
            }
        }
    }
}
