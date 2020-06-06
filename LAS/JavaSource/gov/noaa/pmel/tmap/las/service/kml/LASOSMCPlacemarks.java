package gov.noaa.pmel.tmap.las.service.kml;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTime;
import org.joda.time.Period;

import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Enumeration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * For generating a collection of place marks for OSMC data
 *
 * @author Jing Yang Li
 */
public class LASOSMCPlacemarks implements LASPlacemarks{

    private static Logger log = LoggerFactory.getLogger(LASOSMCPlacemarks.class);

    String gridFileName;
    HashMap<String, String> initLASReq;
    ArrayList placemarks;
    String lookAtLon, lookAtLat;//for creating the <LookAt> tag in KML
    Hashtable uniquePoints;
    String baseURL;
    boolean allParameter;

    public LASOSMCPlacemarks(String fname, HashMap<String, String> las, String url){
        gridFileName = fname;
        initLASReq   = las;
        placemarks   = new ArrayList();
        lookAtLon    = "0.0";
        lookAtLat    = "0.0";
        uniquePoints =  new Hashtable();
        baseURL      = url;
    }

    public ArrayList getPlacemarks(){
        createPlacemarks();
        return placemarks;
    }

    public String getLookAtLon(){
        return lookAtLon;
    }

    public String getLookAtLat(){
        return lookAtLat;
    }

    /**
     * create place marks for the grid points listed in uniquePoints
     */
    public void createPlacemarks(){

        //extract points from the file
        extractPoints();

        Enumeration keys = uniquePoints.keys();

        //System.out.println("Number of unique points = " + uniquePoints.size());

        while(keys.hasMoreElements()){

            //*** OSMC
            OSMCInsituPoint gp = (OSMCInsituPoint)(keys.nextElement());
            GEPlacemark pl = new GEPlacemark(gp.getLat(),gp.getLon());
            pl.setPointTime(GEDate.toGEDate(gp.getTime(),"seconds"));//TODO: set to pass date unit

            //set name
            pl.setName(gp.getCountry()+", "+gp.getPlatformID());

            //set icon
            String colorBy = initLASReq.get("colorBy");//plotform or country
            String iconURL = gp.getIconURL(baseURL, colorBy);
			String istyle = "<Style>"
			                + "<IconStyle>"
			                + "<Icon>"
			                + iconURL
			                + "</Icon>"
			                + "</IconStyle>"
                            + "</Style>";

            pl.setStyle(istyle);

            //set description for this placemark

            String desp = "<description><![CDATA[";

            //check if it's for all parameters (varID == ID)
            //if a variable was selected; create a time series link
            if( !(initLASReq.get("varID").equalsIgnoreCase("ID")) ){
                String lasReq =  makeTSPlotRequest(gp.getLon(),gp.getLat(), gp.getPlatformID());
                desp += "<a href='" + lasReq + "'><h2>Time Series</h2></a>";
		    }

            desp += "Time: " + gp.getTime() + "<br/>";
            desp += "Location: "+ gp.getLon() +", "+gp.getLat() +"<br/>";
			desp += "Type: " + gp.getPlatformType() + "<br/>";
			desp += "<br/>]]>" + "</description>";
            pl.setDescription(desp);

            placemarks.add(pl);
        }
    }


    /**
     *  extract non-repeated points from the listing file for in-situ data
     */
    private void extractPoints(){
        System.out.println("enter extractPoints");

        File gridListFile = new File(gridFileName);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        String gridLon  = "";
        String gridLat  = "";

        int i = 0;
        try {
            fis = new FileInputStream(gridListFile);

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            // dis.available() returns 0 if the file does not have more lines.
            while (dis.available() != 0) {
            //if (dis.available() != 0) {
            //System.out.println("get first point");
                String gridPair = dis.readLine();
                GEPlacemark pl;

                //*** OSMC
                // check if the file content is valid; when there is no valid grid points
                // Ferret writes ***** to the file that is supposed to contain the list of grid points
                if(!gridPair.contains("*")){
                    gridPair = gridPair.trim();
                    /*
                    Pattern p = Pattern.compile("\\s+");
                    String[] gp = gridPair.split(p.pattern());
                    gridLon = gp[0]; //lon
                    gridLat = gp[1]; //lat
                    gridTime= gp[2].substring(1) + " " + gp[3].substring(0,gp[3].length()-1);//time

                    //time
                    OSMCInsituPoint pt = new OSMCInsituPoint(gridLat,gridLon);
                    pt.setTime(gridTime);

                    //platform ID
					pt.setPlatformID(gp[4].substring(1,gp[4].length()-1));

                    //country
                    pt.setCountry(gp[5].substring(1,gp[5].length()-1));

                    //platform type
                    if(gp.length > 8){
					    pt.setPlatformType(gp[6].substring(1)+" "+gp[7]+" "+gp[8].substring(0,gp[8].length()-1));
					}else if(gp.length > 7){
                        pt.setPlatformType(gp[6].substring(1)+" "+gp[7].substring(0,gp[7].length()-1));
                    }else{
						pt.setPlatformType(gp[6].substring(1,gp[6].length()-1));
					}
					*/
					Pattern p = Pattern.compile("\"");
                    String[] gp = gridPair.split(p.pattern());

                    //lon and lat
                    String lonlat = gp[0];
                    Pattern p1 = Pattern.compile("\\s+");
                    String[] location = lonlat.split(p1.pattern());
                    gridLon = location[0];
                    gridLat = location[1];

                    OSMCInsituPoint pt = new OSMCInsituPoint(gridLat,gridLon);

                    //time
                    pt.setTime(gp[1]);

                    //platform ID
					pt.setPlatformID(gp[3]);

                    //country
                    pt.setCountry(gp[5]);

                    //platform type
					pt.setPlatformType(gp[7]);

                    if(!uniquePoints.containsKey(pt)){
                        uniquePoints.put(pt, new Integer(0));
                    }
                }
            }

            if(gridLon != "" && gridLon != null){lookAtLon = gridLon;}
            if(gridLat != "" && gridLat != null){lookAtLat = gridLat;}

            fis.close();
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            log.error("no placemarks");
        } catch (IOException e) {
            log.error("no placemarks");
        } catch (Exception e){
            log.warn("noplacemarks");
        } finally {
        	if ( bis != null )
				try {
					bis.close();
				} catch (IOException e) {
		            log.error("Close error " + e.getMessage());
				}
        	if ( dis != null )
				try {
					dis.close();
				} catch (IOException e) {
		            log.error("Close error " + e.getMessage());
				}
        }

        System.out.println("finish extractPoints");
    }


    /**
     * create the LAS request URL that generates table values for in-situ data
     */
    private String makeTableValueRequest(String lon, String lat){
        String lasReq = "";

        LASUIRequest lasUIRequest = new LASUIRequest();

        //set datset and variable
        String dataset_ID = initLASReq.get("dsID");
        String var_ID = initLASReq.get("varID");
        lasUIRequest.addVariable(dataset_ID, var_ID);

        //set operation
        lasUIRequest.setOperation("Insitu_extract_data");

        //set regions
        HashMap<String, HashMap<String,String[]>> region = new HashMap<String, HashMap<String,String[]>>();
        HashMap<String, String[]> points = new HashMap<String, String[]>();
        HashMap<String, String[]> intervals = new HashMap<String, String[]>();

        double lonDouble = Double.parseDouble(lon);
        String lon1 = Double.toString(lonDouble - 0.01);
        String lon2 = Double.toString(lonDouble + 0.01);
        double latDouble = Double.parseDouble(lat);
        String lat1 = Double.toString(latDouble - 0.01);
        String lat2 = Double.toString(latDouble + 0.01);

        String[] xp = new String[] {lon1, lon2};
        String[] yp = new String[] {lat1, lat2};
        intervals.put("x", xp);
        intervals.put("y", yp);

        String tlo = initLASReq.get("tlo");
        String thi = initLASReq.get("thi");
        if(tlo != null && thi != null){
            String[] ti = new String[] {tlo,thi};
            intervals.put("t",ti);
		}

        String zlo = initLASReq.get("zlo");
		String zhi = initLASReq.get("zhi");
		if(zlo != null && zhi != null){
	    	String[] zi = new String[] {zlo,zhi};
            intervals.put("z",zi);
	    }

        region.put("points", points);//must do this even points is empty
        region.put("intervals",intervals);
        lasUIRequest.setRegion(region);
        lasUIRequest.setProperty("ferret", "view", "xy");
        lasUIRequest.setProperty("ferret", "format", "text");
        //lasUIRequest.setProperty("constraint","type","SHIPS (GENERIC)");

        String serverURL = baseURL.replaceFirst("output","ProductServer");
        try{
            lasReq = serverURL+".do?xml=" + lasUIRequest.toEncodedURLString();
        }catch(Exception e){
            //
        }

        return lasReq;
    }

    /**
     * create the LAS request URL that generates TS plot
     * @param myID the ID of the platform
     */
    private String makeTSPlotRequest(String lon, String lat, String myID){
        String lasReq = "";

        LASUIRequest lasUIRequest = new LASUIRequest();

        //set datset and variable
        String dataset_ID = initLASReq.get("dsID");
        String var_ID = initLASReq.get("varID");
        lasUIRequest.addVariable(dataset_ID, var_ID);

        //set operation
        lasUIRequest.setOperation("osmc_ts_plot");

        //set regions
        HashMap<String, HashMap<String,String[]>> region = new HashMap<String, HashMap<String,String[]>>();
        HashMap<String, String[]> points = new HashMap<String, String[]>();
        HashMap<String, String[]> intervals = new HashMap<String, String[]>();


        double lonDouble = Double.parseDouble(lon);
        //String lon1 = Double.toString(lonDouble - 0.5);
        //String lon2 = Double.toString(lonDouble + 0.5);
        String lon1 = Double.toString(-180.0);
        String lon2 = Double.toString(180.0);
        double latDouble = Double.parseDouble(lat);
        //String lat1 = Double.toString(latDouble - 0.5);
        //String lat2 = Double.toString(latDouble + 0.5);
        String lat1 = Double.toString(-90.0);
        String lat2 = Double.toString(89.0);

        String[] xp = new String[] {lon1, lon2};
        String[] yp = new String[] {lat1, lat2};
        intervals.put("x", xp);
        intervals.put("y", yp);

        String tlo = initLASReq.get("tlo");
        String thi = initLASReq.get("thi");
        if(tlo != null && thi != null){
            String[] ti = new String[] {tlo,thi};
            intervals.put("t",ti);
		}
/*
        String zlo = initLASReq.get("zlo");
		String zhi = initLASReq.get("zhi");
		if(zlo != null && zhi != null){
	    	String[] zi = new String[] {zlo,zhi};
            intervals.put("z",zi);
	    }
*/
        region.put("points", points);//must do this even points is empty
        region.put("intervals",intervals);
        lasUIRequest.setRegion(region);
        //lasUIRequest.setProperty("ferret", "view", "xy");
        //lasUIRequest.setProperty("ferret", "format", "text");
        //lasUIRequest.setProperty("ferret", "use_ref_map", "true");
        //lasUIRequest.setProperty("constraint","type","SHIPS (GENERIC)");

        //add constraint
        lasUIRequest.addTextConstraint("p.platform_code", "=", myID, "Platform Code");

        String serverURL = baseURL.replaceFirst("output","ProductServer");
        try{
            lasReq = serverURL+".do?xml=" + lasUIRequest.toEncodedURLString();
        }catch(Exception e){
            //
        }

        return lasReq;
    }

    /**
     * set <Region> for each place marks
     */
    public void setRegion(){
        String region ="";

        ListIterator itr = placemarks.listIterator();
        while (itr.hasNext()) {
        //for(int i=0;i<placemarks.size();i++){
            //GEPlacemark gpl = (GEPlacemark)placemarks.get(i);
            GEPlacemark gpl = (GEPlacemark)itr.next();
            //gp.setName(gp.getPointTime());

            //String lasReq =  makeTableValueRequest(gp.getPointLon(),gp.getPointLat(),initLAS,lasBackendReq, baseURL);
            //compute the las/lon box
            double lonDouble = Double.parseDouble(gpl.getPointLon());
            double interval = 0.5;
            String lon1 = Double.toString(lonDouble - interval);
            String lon2 = Double.toString(lonDouble + interval);
            double latDouble = Double.parseDouble(gpl.getPointLat());
            String lat1 = Double.toString(latDouble - interval);
            String lat2 = Double.toString(latDouble + interval);

            region = "";
            region += "<Region><LatLonAltBox>";
            region += "<north>"+lat2+"</north>";
            region += "<south>"+lat1+"</south>";
            region += "<east>"+lon2+"</east>";
            region += "<west>"+lon1+"</west>";
            region += "<minAltitude>0</minAltitude>";
			region += "<maxAltitude>2000</maxAltitude>";
		    region += "</LatLonAltBox>";
			region += "<Lod>";

			int minLod = (int)(Math.random()*29)+1;
		    region += "<minLodPixels>"+Integer.toString(minLod)+"</minLodPixels>";
		    region += "<maxLodPixels>-1</maxLodPixels>";
			region += "<minFadeExtent>1</minFadeExtent>";
	        region += "<maxFadeExtent>1</maxFadeExtent>";
            region +=  "</Lod></Region>";

            gpl.setRegion(region);
        }
    }
}
