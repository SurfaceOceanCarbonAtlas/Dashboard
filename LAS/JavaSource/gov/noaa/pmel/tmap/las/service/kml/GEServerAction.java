package gov.noaa.pmel.tmap.las.service.kml;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.server.LASAction;
import gov.noaa.pmel.tmap.las.product.server.LASConfigPlugIn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *@author Jing Yang Li
 *
 */
public final class GEServerAction extends LASAction {
    private static Logger log = LoggerFactory.getLogger(GEServerAction.class.getName());

    public String execute() 
    throws ServletException, IOException{

        log.info("START: GEServerAction");
        LASConfig lasConfig = (LASConfig) contextAttributes.get(LASConfigPlugIn.LAS_CONFIG_KEY);

        //use Google Earth Mime type
        response.setContentType("application/keyhole");
        PrintWriter out = response.getWriter();

        //create the ground overlay KML for a single plot
        String kmlString = genPlotOverlayKML(request,lasConfig);

        //return back the KML string
        if( (kmlString != "") && (kmlString != null)){
            out.println(kmlString);
        }else{
            //should ouput an error message to GE
            log.error("error while generting the plot kml");
        }
        log.info("END: GEServerAction");
        return null;
    }

    /**
     * Check if it's dynamic GE overlay 
     * @param lasInitRequestXML The initail LAS UI request
     */
    private boolean isDynamic(HttpServletRequest request)
        throws ServletException, IOException
    {
        String lasInitRequestXML = request.getParameter("xml");
        LASUIRequest lasUIRequest = new LASUIRequest();

        try{
            JDOMUtils.XML2JDOM(lasInitRequestXML, lasUIRequest);
        } catch (Exception e){
            log.error("error while create LASUIRequest: " + e.toString());
        }

        if(lasUIRequest.getProperty("ferret", "ge_overlay_style").equals("dynamic")){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Check the GE overlay style
     * @param request The HttpServletRequest
     * @return lasUIRequest the LAS UI request
     */
    private LASUIRequest getLASUIRequest(HttpServletRequest request)
        throws ServletException, IOException
    {
        String lasInitRequestXML = request.getParameter("xml");
        LASUIRequest lasUIRequest = new LASUIRequest();
        try{
            JDOMUtils.XML2JDOM(lasInitRequestXML, lasUIRequest);
        } catch (Exception e){
            log.error("error while create LASUIRequest: " + e.toString());
        }
        return lasUIRequest;
    }

    /**
     * Retrieve the BBOX coming from Google Earth
     * @param request the HttpServletRequest
     * @return bbox the bounding box
     */
    private float[] getBBOX(HttpServletRequest request)
        throws ServletException, IOException 
    {
        //get BBOX coming from GE
        String coords = request.getParameter("BBOX");

            //BBOX is defined as [longitude_west, latitude_south, longitude_east, latitude_north]
            if (coords==null || coords==""){
                log.warn("There is no BBOX information in this request.");
                return null;
            }
            String[] coParts= coords.split(",");

            float userlon;
            float userlat;//view center
            float[] bbox = new float[4];//view boundary

            //process bbox info
            try{
                userlon = ((Float.parseFloat(coParts[2]) - Float.parseFloat(coParts[0]))/2) + Float.parseFloat(coParts[0]);
                userlat = ((Float.parseFloat(coParts[3]) - Float.parseFloat(coParts[1]))/2) + Float.parseFloat(coParts[1]);
                bbox[0] = Float.parseFloat(coParts[0]);
                bbox[2] = Float.parseFloat(coParts[2]);
                bbox[1] = Float.parseFloat(coParts[1]);
                bbox[3] = Float.parseFloat(coParts[3]);
             }catch(NumberFormatException e){
                log.error("Error in processing BBOX from Google Earth: " + e.toString());
                return null;
            }
        return bbox;
    }

    /**
    * Generate a KML file for a XY plot as ground overlay 
    * and grid points as place marks
    * @param request the HttpServletRequest
    * @param lasConfig the LASConfig object for this LAS server
    * @return kmlString the KML String
    */
    public String genPlotOverlayKML(HttpServletRequest request, LASConfig lasConfig)
        throws ServletException, IOException 
    {
        LASUIRequest lasUIRequest = getLASUIRequest(request);
        boolean isDynamic = isDynamic(request);

        String requestURL ="";
        String placemarkRequestURL ="";

        String kmlString ="";
        String serverURL = getServerURL(lasConfig);

        try{
            //update XY range for dynamic overlay
            if(isDynamic){
                float[] bbox = getBBOX(request);

                //make sure latitudes are inside the data's range
                ArrayList vars = lasUIRequest.getVariables();
                String yHi = lasConfig.getHi("y",vars.get(0).toString());//dataset's max y 
                String yLo = lasConfig.getLo("y",vars.get(0).toString());//dataset's min y
                float fHi = Float.valueOf(yHi.trim()).floatValue();
                float fLo = Float.valueOf(yLo.trim()).floatValue();
                if(bbox[1] < fLo){ bbox[1]=fLo;}
                if(bbox[3] > fHi){ bbox[3]=fHi;}

                Float minlon = new Float(bbox[0]);
                Float maxlon = new Float(bbox[2]);
                Float minlat = new Float(bbox[1]);
                Float maxlat = new Float(bbox[3]);
                lasUIRequest.setRange("x", minlon.toString(),maxlon.toString());
                lasUIRequest.setRange("y", minlat.toString(),maxlat.toString());
            }

            //update operation
            //the KML for non-vector plot contains a screen overlay of colorbar
            //the KML for vector plot doesn't contain a screen overlay of colorbar
            String op = lasUIRequest.getOperationXPath();
            if(op.contains("Plot_GE_Overlay") && !op.contains("Traj")){
                lasUIRequest.changeOperation("Plot_GE_kml");
                //build the request for plot overlay
                requestURL = serverURL+"?xml="+lasUIRequest.toEncodedURLString();
                //build the request for placemarks
                lasUIRequest.changeOperation("Grid_GE_kml");
                placemarkRequestURL = serverURL+"?xml="+lasUIRequest.toEncodedURLString();
            } else if(op.contains("Plot_GE_Overlay") && op.contains("Traj")){
                lasUIRequest.changeOperation("Plot_GE_Traj_kml");
                //build the request for plot overlay
                requestURL = serverURL+"?xml="+lasUIRequest.toEncodedURLString();
                //build the request for placemarks
//                lasUIRequest.changeOperation("Grid_GE_kml");
//                placemarkRequestURL = serverURL+"?xml="+lasUIRequest.toEncodedURLString();
            }else if(op.contains("Vector_GE_Overlay")){
                lasUIRequest.changeOperation("Vector_GE_kml");
                //build the request for plot overlay
                requestURL = serverURL+"?xml="+lasUIRequest.toEncodedURLString();
            }
        } catch (Exception e){
            log.error("error while building LAS Request: " + e.toString());
        }

        if( (requestURL != "") && (requestURL != null)){
            kmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                     +"<kml xmlns=\"http://earth.google.com/kml/2.1\">"
                     +"<Folder>"
                     +"<NetworkLink>"
                     +"<name>Plot</name>"
                     +"<Link>"
                     +"<href>"
                     +requestURL
                     +"</href>"
                     +"</Link>"
                     +"</NetworkLink>";
             if(placemarkRequestURL != ""){
                 kmlString += "<NetworkLink>"
                     +"<name>Grid Points</name>"
                     +"<Link>"
                     +"<href>"
                     +placemarkRequestURL
                     +"</href>"
                     +"</Link>"
                     +"</NetworkLink>";
             }
             kmlString += "</Folder>"
                          +"</kml>";
         }
         return kmlString;
    }

    /**
    * Get the product server URL
    * @param request the HttpServletRequest
    * @return serverURL URL of the LAS product server
    */
    private String getServerURL(LASConfig lasConfig)
    throws ServletException, IOException
    {
        String serverURL="";
        try {
            serverURL = lasConfig.getServerURL();
        } catch (JDOMException e) {
            log.error("Eror getting product server URL");
        }
        return serverURL;
    }
}
