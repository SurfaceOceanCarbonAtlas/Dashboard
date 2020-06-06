package gov.noaa.pmel.tmap.las.service.kml;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.regex.Pattern;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * For generating a collection of place marks for LAS data
 *
 * @author Jing Yang Li
 */
public class LASRegularPlacemarks implements LASPlacemarks{

    private static Logger log = LoggerFactory.getLogger(LASRegularPlacemarks.class.getName());

    String gridFileName;
    HashMap<String, String> initLASReq;
    ArrayList placemarks;
    String lookAtLon, lookAtLat;//for creating the <LookAt> tag in KML
    String baseURL;

    public LASRegularPlacemarks(String fname, HashMap<String, String> lasReq, String url){
        gridFileName = fname;
        initLASReq   = lasReq;
        placemarks   = new ArrayList();
        lookAtLon    = "0.0";
        lookAtLat    = "0.0";
        baseURL      = url;
    }

    public ArrayList getPlacemarks(){
		createPlacemarks();
		setDescriptions();
        setIconStyle("http://maps.google.com/mapfiles/kml/pal4/icon22.png");
        return placemarks;
    }

    public String getLookAtLon(){
        return lookAtLon;
    }

    public String getLookAtLat(){
        return lookAtLat;
    }

    /**
     * create the place marks for the regular grid points listed in gridFileName;
     * each place mark is just a point without any description and style
     */
    public void createPlacemarks(){
        File gridListFile = new File(gridFileName);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        String gridLon="";
        String gridLat="";
        //String gridTime="";
        try {
            fis = new FileInputStream(gridListFile);

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            // dis.available() returns 0 if the file does not have more lines.
            while (dis.available() != 0) {
                String gridPair = dis.readLine();
                GEPlacemark pl;

                // check if the file content is valid; when there is no valid grid points
                // Ferret writes ***** to the file that is supposed to contain the list of grid points
                if(!gridPair.contains("*")){
                    gridPair = gridPair.trim();
                    Pattern p = Pattern.compile("\\s+");
                    String[] gp = gridPair.split(p.pattern());
                    gridLon = gp[0];
                    gridLat = gp[1];
                    //gridTime= gp[2];
                    //System.out.println("gridlon"+gridLon);
                    //System.out.println("gridlat"+gridLat);
                    pl = new GEPlacemark(gridLat,gridLon);
                    placemarks.add(pl);
                }
            }

            if(gridLon != "" && gridLon != null){lookAtLon = gridLon;}
            if(gridLat != "" && gridLat != null){lookAtLat = gridLat;}

            fis.close();
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            log.warn("noplacemarks");
        } finally {
        	if ( fis != null ) {
        		try {
					fis.close();
				} catch (IOException e) {
					log.error("Close failed."+e.getMessage());
				}
        	}
        }
    }

    /**
     * set placemark descriptions for regular gridded data
     */
    //public void setRegularDescriptions(HashMap<String, String> initLAS, LASBackendRequest lasBackendReq, String baseURL){
    public void setDescriptions(){
        ListIterator itr = placemarks.listIterator();
        while (itr.hasNext()) {
            GEPlacemark gp = (GEPlacemark)itr.next();
            String lon = gp.getPointLon();
            String lat = gp.getPointLat();
            //GELASDescription gld = new GELASDescription(lon,lat,initLAS,lasBackendReq,baseURL);
            GELASDescription gld = new GELASDescription(lon,lat,initLASReq,baseURL);
            //System.out.println(gld.toString());
            gp.setDescription(gld.toString());
        }
    }

    /*
     * set icon style for place marks
     *
     */
    public void setIconStyle(String iconURL){
       String istyle = "<Style>"
                       + "<IconStyle>"
                       + "<Icon>"
                       + iconURL
                       + "</Icon>"
                       + "</IconStyle>"
                       + "</Style>";


        //set icon style for each place mark
        ListIterator itr = placemarks.listIterator();
        while (itr.hasNext()) {
            GEPlacemark gp = (GEPlacemark)itr.next();
            gp.setStyle(istyle);
        }
    }
}
