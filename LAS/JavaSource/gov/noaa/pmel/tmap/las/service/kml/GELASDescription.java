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
 * The description for a regular grid point
 * @author Jing Yang Li
 */
public class GELASDescription{

    private static Logger log = LoggerFactory.getLogger(GELASDescription.class);

    String point_lat;
    String point_lon;

    HashMap<String, String> initLASInfo;
    String dataset_ID;
    String var_ID;

    String ferret_view;
    String ferret_deg_min_sec;
    String ferret_dep_axis_scale;
    String ferret_expression;
    String ferret_fill_type;
    String ferret_interpolate_data;
    String ferret_line_color;
    String ferret_line_or_sym;
    String ferret_line_thickness;
    String ferret_margins;
    String ferret_size;
    String ferret_use_graticules;
    String ferret_use_ref_map;

    String thi;
    String tlo;
    String zhi;
    String zlo;
    String t_user;
    String z_user;

    String dsIntervals;

    String base_URL;

    String style = "";
    String description ="";
    //LASBackendRequest lasBackendRequest;

    //public GELASDescription(String lat, String lon, HashMap<String, String> initLAS, LASBackendRequest lasBackendReq, String baseURL){
    public GELASDescription(String lon, String lat, HashMap<String, String> initLAS,String baseURL){

        point_lat   = lat;
        point_lon   = lon;
        initLASInfo = initLAS;
        //lasBackendRequest = lasBackendReq;
        base_URL = baseURL;
        init();
        checkPointLon();
    }

    private void init(){
        dataset_ID = initLASInfo.get("dsID");
        var_ID = initLASInfo.get("varID");
        dsIntervals = initLASInfo.get("dsIntervals");

        if(dsIntervals.contains("t")){
            tlo = initLASInfo.get("tlo");
            thi = initLASInfo.get("thi");
        }

        if(dsIntervals.contains("z")){
            zlo = initLASInfo.get("zlo");
            zhi = initLASInfo.get("zhi");
        }

        ferret_view             = initLASInfo.get("ferret_view");
        ferret_deg_min_sec      = initLASInfo.get("ferret_deg_min_sec");
        ferret_dep_axis_scale   = initLASInfo.get("ferret_dep_axis_scale");
        ferret_expression       = initLASInfo.get("ferret_expression");
        ferret_fill_type        = initLASInfo.get("ferret_fill_type");
        ferret_interpolate_data = initLASInfo.get("ferret_interpolate_data");
        ferret_line_color       = initLASInfo.get("ferret_line_color");
        ferret_line_or_sym      = initLASInfo.get("ferret_line_or_sym");
        ferret_line_thickness   = initLASInfo.get("ferret_line_thickness");
        ferret_margins          = initLASInfo.get("ferret_margins");
        ferret_size             = initLASInfo.get("ferret_size");
        ferret_use_graticules   = initLASInfo.get("ferret_use_graticules");
        ferret_use_ref_map      = initLASInfo.get("ferret_use_ref_map");

        if(dsIntervals.equals("xyzt") && ferret_view.equals("xy")){
            t_user = initLASInfo.get("t_user");
            z_user = initLASInfo.get("z_user");
        }

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
            //east
            }else{
                point_lon = Double.toString(glon360);
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

    /**
     * set description
     */
    private void setDescription(){

        description = "<description>"
                      + "<![CDATA[";

        if(dsIntervals.equals("xy")){
            description += "No Time Series or Vertical Profile available for this dataset";
        }

        if(dsIntervals.equals("xyt")){
            description += "<a href='"
                           + make1DPlotRequest("t",null,null,tlo,thi)
                           + "'><h2>Time Series Plot</h2></a><br />";
        }

        if(dsIntervals.equals("xyz")){
            description += "<a href='"
                           + make1DPlotRequest("z",zlo,zhi,null,null)
                           + "'><h2>Vertical Profile Plot</h2></a><br />";
        }

        if(dsIntervals.equals("xyzt")){
            if(ferret_view.equals("xy")){
                //use user selected t, t_user
                description += "<a href='"
                               + make1DPlotRequest("z",zlo,zhi,t_user,t_user)
                               + "'><h2>Vertical Profile Plot</h2></a>(Time: " + t_user+")";

                //use user selected z, z_user
                description += "<a href='"
                               + make1DPlotRequest("t",z_user,z_user,tlo,thi)
                               + "'><h2>Time Series Plot</h2></a>(Depth(m): " + z_user+")";
            }

            if(ferret_view.equals("xyz")){
                description +=  "<a href='"
                                + make1DPlotRequest("z",zlo,zhi,tlo,thi)
                                + "'><h2>Vertical Profile Plot</h2></a><br />";
            }

            if(ferret_view.equals("xyt")){
                description += "<a href='"
                               + make1DPlotRequest("t",zlo,zhi,tlo,thi)
                               + "'><h2>Time Series Plot</h2></a><br />";
            }

        }

        description +=  "<br />]]>" + "</description>";
    }

    /**
     * Create the URL that makes LAS 1D plot
     */
    private String make1DPlotRequest(String ferret1DView, String z_lo, String z_hi, String t_lo, String t_hi )
    {
        String lasReq = "";

        LASUIRequest lasUIRequest = new LASUIRequest();

        //set datset and variable
        lasUIRequest.addVariable(dataset_ID, var_ID);
        //set operation
        lasUIRequest.setOperation("Plot_1D");
        //set region
        HashMap<String, HashMap<String,String[]>> region = new HashMap<String, HashMap<String,String[]>>();
        HashMap<String, String[]> points = new HashMap<String, String[]>();
        HashMap<String, String[]> intervals = new HashMap<String, String[]>();
        String[] xp = new String[] {point_lon};
        String[] yp = new String[] {point_lat};
        points.put("x", xp);
        points.put("y", yp);

        if(t_lo != null && t_hi != null){
            String[] ti = new String[] {t_lo,t_hi};
            intervals.put("t",ti);
        }

        if(z_lo != null && z_hi != null){
            String[] zi = new String[] {z_lo,z_hi};
            intervals.put("z",zi);
        }

        region.put("points", points);
        region.put("intervals",intervals);

        lasUIRequest.setRegion(region);

        lasUIRequest.setProperty("ferret", "view", ferret1DView);

        lasUIRequest.setProperty("ferret", "format", "line");
        lasUIRequest.setProperty("ferret", "image_format", "default");


        if(ferret_deg_min_sec != null && ferret_deg_min_sec != ""){
            lasUIRequest.setProperty("ferret", "deg_min_sec", ferret_deg_min_sec);
        }

        if(ferret_dep_axis_scale != null && ferret_dep_axis_scale != ""){
            lasUIRequest.setProperty("ferret", "dep_axis_scale", ferret_dep_axis_scale);
        }

        if(ferret_expression != null && ferret_expression != ""){
            lasUIRequest.setProperty("ferret", "expression", ferret_expression);
        }

        if(ferret_fill_type != null && ferret_fill_type != ""){
            lasUIRequest.setProperty("ferret", "fill_type", ferret_fill_type);
        }

        if(ferret_interpolate_data != null && ferret_interpolate_data != ""){
            lasUIRequest.setProperty("ferret", "interpolate_data", ferret_interpolate_data);
        }

        if(ferret_line_color != null && ferret_line_color != ""){
            lasUIRequest.setProperty("ferret", "line_color", ferret_line_color);
        }

        if(ferret_line_or_sym != null && ferret_line_or_sym != ""){
            lasUIRequest.setProperty("ferret", "line_or_sym", ferret_line_or_sym);
        }

        if(ferret_line_thickness != null && ferret_line_thickness != ""){
            lasUIRequest.setProperty("ferret", "line_thickness", ferret_line_thickness);
        }

        if(ferret_margins != null && ferret_margins != ""){
            lasUIRequest.setProperty("ferret", "margins", ferret_margins);
        }

        if(ferret_size != null && ferret_size != ""){
            lasUIRequest.setProperty("ferret", "size", ferret_size);
        }else{
            lasUIRequest.setProperty("ferret", "size", "0.5");
        }

        if(ferret_use_graticules != null && ferret_use_graticules != ""){
            lasUIRequest.setProperty("ferret", "use_graticules", ferret_use_graticules);
        }

        if(ferret_use_ref_map != null && ferret_use_ref_map != ""){
            lasUIRequest.setProperty("ferret", "use_ref_map", ferret_use_ref_map);
        }

        String serverURL = base_URL.replaceFirst("output","ProductServer");

        try{
            lasReq = serverURL+".do?xml=" + lasUIRequest.toEncodedURLString();
        }catch(Exception e){
        }

        return lasReq;
    }

    public String toString(){
        setDescription();
        return description;
    }
}
