package gov.noaa.pmel.tmap.las.service.kml;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTime;
import org.joda.time.Period;

/*
 * converts a Ferret date to Google Earth date
 */
public class GEDate{


/*
    public GEDate(String date,String resolution){
        geDateString=null;
        makeGEDate(date, resolution);
    }
*/
    public static String toGEDate(String date, String resolution){
        String geDateString="";

        if(date.length() == 8){
            //date is in format of MMM-yyyy
            String ferretFormat = "MMM-yyyy";
            DateTimeFormatter ferretFMT = DateTimeFormat.forPattern(ferretFormat).withZone(DateTimeZone.UTC);

            //parse ferret date
            DateTime ferretDT = ferretFMT.parseDateTime(date);
             //GE format
            if(resolution.equalsIgnoreCase("years")){
                DateTimeFormatter geFMT = DateTimeFormat.forPattern("yyyy").withZone(DateTimeZone.UTC);
                //convert to GE date string
                geDateString = ferretDT.toString(geFMT);
            }else if(resolution.equalsIgnoreCase("months")){
                DateTimeFormatter geFMT = DateTimeFormat.forPattern("yyyy-MM").withZone(DateTimeZone.UTC);
                //convert to GE date string
                geDateString = ferretDT.toString(geFMT);
            }
        }

        if(date.length() == 11){
            //date is in format of dd-MMM-yyyy
            String ferretFormat = "dd-MMM-yyyy";
            DateTimeFormatter ferretFMT = DateTimeFormat.forPattern(ferretFormat).withZone(DateTimeZone.UTC);

            //parse ferret date
            DateTime ferretDT = ferretFMT.parseDateTime(date);

            //GE format
            if(resolution.equalsIgnoreCase("years")){
                DateTimeFormatter geFMT = DateTimeFormat.forPattern("yyyy").withZone(DateTimeZone.UTC);
                //convert to GE date string
                geDateString = ferretDT.toString(geFMT);
            }else if(resolution.equalsIgnoreCase("months")){
                DateTimeFormatter geFMT = DateTimeFormat.forPattern("yyyy-MM").withZone(DateTimeZone.UTC);
                //convert to GE date string
                geDateString = ferretDT.toString(geFMT);
            }else if(resolution.equalsIgnoreCase("days")){
                DateTimeFormatter geFMT = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC);
                //convert to GE date string
                geDateString = ferretDT.toString(geFMT);
            }
        }

        if(date.length() == 20){
		    //date is in format of dd-MMM-yyyy
            String ferretFormat = "dd-MMM-yyyy HH:mm:ss";
            DateTimeFormatter ferretFMT = DateTimeFormat.forPattern(ferretFormat).withZone(DateTimeZone.UTC);

            //parse ferret date
            DateTime ferretDT = ferretFMT.parseDateTime(date);

	        if(resolution.equalsIgnoreCase("seconds")){
	            DateTimeFormatter geFMT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
	            //convert to GE date string
                geDateString = ferretDT.toString(geFMT);
		    }
		}

        if(date.length() == 14){
            String tmp[]=date.split(" ");
            //no year for climatology, e.g., coads_climatology
            if(tmp[0].length() == 6){
                //date is in format of "dd-MMM      hh"
                String ferretFormat = "dd-MMM";
                DateTimeFormatter ferretFMT = DateTimeFormat.forPattern(ferretFormat).withZone(DateTimeZone.UTC);
                //GE format
                DateTimeFormatter geFMT = DateTimeFormat.forPattern("MM-dd").withZone(DateTimeZone.UTC);
                //parse ferret date
                DateTime ferretDT = ferretFMT.parseDateTime(tmp[0]);
                //convert to GE date string
                geDateString = ferretDT.toString(geFMT);
                geDateString = "0000-"+geDateString;
            }
        }

        return geDateString;
    }

/*
    public String toString(){
        return geDateString;
    }
*/
}
