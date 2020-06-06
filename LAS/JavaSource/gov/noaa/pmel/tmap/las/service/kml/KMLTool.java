/**
 * This software was developed by the Thermal Modeling and Analysis
 * Project(TMAP) of the National Oceanographic and Atmospheric
 * Administration's (NOAA) Pacific Marine Environmental Lab(PMEL),
 * hereafter referred to as NOAA/PMEL/TMAP.
 *
 * Access and use of this software shall impose the following
 * obligations and understandings on the user. The user is granted the
 * right, without any fee or cost, to use, copy, modify, alter, enhance
 * and distribute this software, and any derivative works thereof, and
 * its supporting documentation for any purpose whatsoever, provided
 * that this entire notice appears in all copies of the software,
 * derivative works and supporting documentation. Further, the user
 * agrees to credit NOAA/PMEL/TMAP in any publications that result from
 * the use of this software or in any product that includes this
 * software. The names TMAP, NOAA and/or PMEL, however, may not be used
 * in any advertising or publicity to endorse or promote any products
 * or commercial entity unless specific written permission is obtained
 * from NOAA/PMEL/TMAP. The user also understands that NOAA/PMEL/TMAP
 * is not obligated to provide the user with any support, consulting,
 * training or assistance of any kind with regard to the use, operation
 * and performance of this software nor to provide the user with any
 * updates, revisions, new versions or "bug fixes".
 *
 * THIS SOFTWARE IS PROVIDED BY NOAA/PMEL/TMAP "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL NOAA/PMEL/TMAP BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTUOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE
 */
package gov.noaa.pmel.tmap.las.service.kml;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASKMLBackendConfig;
import gov.noaa.pmel.tmap.las.jdom.LASMapScale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.noaa.pmel.tmap.las.service.TemplateTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.VelocityContext;

/**
 * @author Roland Schweitzer and Jing Y. Li
 *
 */
public class KMLTool extends TemplateTool  {

    private final static Logger log = LoggerFactory.getLogger(KMLTool.class.getName());
    LASKMLBackendConfig kmlBackendConfig;

    /**
     * @throws IOException
     *
     */
    public KMLTool() throws LASException, IOException {

        super("kml", "KMLBackendConfig.xml");

        kmlBackendConfig = new LASKMLBackendConfig();

        try {
            JDOMUtils.XML2JDOM(getConfigFile(), kmlBackendConfig);
        } catch (Exception e) {
            throw new LASException("Could not parse Ferret config file: " + e.toString());
        }
    }

    /**
     * @param serviceName
     * @param configFileName
     * @throws LASException
     * @throws IOException
     */
    public KMLTool(String serviceName, String configFileName)
            throws LASException, IOException {
        super(serviceName, configFileName);
        // TODO Auto-generated constructor stub
    }

    public LASBackendResponse run( LASBackendRequest lasBackendRequest) throws Exception {
        //System.out.println("entering run");
        VelocityContext context = new VelocityContext(getToolboxContext());
        Properties p = new Properties();
        InputStream is = null;
        is = this.getClass().getClassLoader().getResourceAsStream("resources/kml/velocity.properties");
        String template = JDOMUtils.getResourcePath(this, "resources/kml/templates");
        if (is == null) {
            if ( template != null ) {
                // Can't find properties file.  Set where we look for templates.
                log.debug("Setting template path to default "+template);
                p.setProperty("file.resource.loader.path", template);
            } else {
                throw new LASException("Cannot find kml backend templates directory.");
            }
        } else {
            try {
				p.load(is);
			} catch (Exception e) {
				throw e;
			} finally {
				if ( is != null ) {
					is.close();
				}
			}
        }
        if ( p.getProperty("file.resource.loader.path") == null ) {
            if ( template != null ) {
                // Can't find properties file.  Set where we look for templates.
                log.warn("Template path not found in properties file.  Setting to default"+template);
                p.setProperty("file.resource.loader.path", template);
            } else {
                throw new LASException("Cannot find kml templates directory.");
            }
        }

        try {
            ve.init(p);
        } catch (Exception e) {
            throw new LASException("Cannot initialize the velocity engine.");
        }
        ve.init();

        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        String kml = lasBackendRequest.getServiceAction();
        //make overlay KML
        if (kml.contains("overlay")){
            //System.out.println("do overlay?");
            lasBackendResponse = makeOverlayKML(lasBackendRequest, context);
        }

        //make placemark KML
        if (kml.contains("placemarks")){
            lasBackendResponse = makePlacemarksKML(lasBackendRequest, context);
        }

        return lasBackendResponse;
    }

    private LASBackendResponse makeOverlayKML(LASBackendRequest lasBackendRequest, VelocityContext context)
    throws Exception {
        LASBackendResponse lasBackendResponse = new LASBackendResponse();

        String output = lasBackendRequest.getResultAsFile("kml");
        String kmz = lasBackendRequest.getResultAsFile("kmz");
        String map_scale_file = lasBackendRequest.getChainedDataFile("map_scale");
        String map_scale_URL = kmlBackendConfig.getHttpBaseURL() + "/" + map_scale_file.substring(map_scale_file.lastIndexOf(File.separator), map_scale_file.length());
        String plot_image_file = lasBackendRequest.getChainedDataFile("plot_image");
        String plot_image_URL = kmlBackendConfig.getHttpBaseURL() + "/" + plot_image_file.substring(plot_image_file.lastIndexOf(File.separator), plot_image_file.length());

        if ( output.equals("") ) {
        	output = kmz.replaceAll("kmz", "kml");
        }
        
        String kml = lasBackendRequest.getServiceAction();
        if (!kml.endsWith(".vm")) {
            kml = kml+".vm";
        }
        String colorbar_file="";
        String colorbar_URL="";

        LASMapScale map_scale = new LASMapScale();
        try {
            JDOMUtils.XML2JDOM(new File(map_scale_file), map_scale);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        float xll = Float.valueOf(map_scale.getXAxisLowerLeft()).floatValue();
        float xur = Float.valueOf(map_scale.getXAxisUpperRight()).floatValue();
        float xmid = (xur + xll)/2.0f;
        float yll = Float.valueOf(map_scale.getYAxisLowerLeft()).floatValue();
        float yur = Float.valueOf(map_scale.getYAxisUpperRight()).floatValue();
        float ymid = (yur + yll)/2.0f;
        context.put("longitude_center", xmid);
        context.put("latitude_center", ymid);
        context.put("las_response", lasBackendResponse);
        context.put("map_scale", map_scale);
        context.put("map_scale_file", map_scale_file);
        context.put("map_scale_URL", map_scale_URL);
        context.put("plot_image_file", plot_image_file);
        context.put("plot_image_URL", plot_image_URL);

        if(!kml.contains("vector")){
            try{
                colorbar_file = lasBackendRequest.getChainedDataFile("colorbar");
                colorbar_URL = kmlBackendConfig.getHttpBaseURL() + "/" + colorbar_file.substring(colorbar_file.lastIndexOf(File.separator), colorbar_file.length());
            }catch(NullPointerException e){
                log.warn("color bar is either not defined in operation or not created correctly");
            }

            if(colorbar_file != null && colorbar_file != ""){
                context.put("colorbar_file", colorbar_file);
            }

            if(colorbar_URL != null && colorbar_URL != ""){
                context.put("colorbar_URL", colorbar_URL);
            }
        }

        PrintWriter kmlWriter = null;
        try {
            kmlWriter = new PrintWriter(new FileOutputStream(new File(output)));
        }
        catch(Exception e) {
            // We need to package these and send them back to the UI.
        }
        ve.mergeTemplate(kml,"ISO-8859-1", context, kmlWriter);
        if ( kmlWriter != null )
        	kmlWriter.close();
        writeKMZ(kmz, output);
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        return lasBackendResponse;
    }

    /**
      * make place marks for either regular gridded data or insitu data
      */
    private LASBackendResponse makePlacemarksKML(LASBackendRequest lasBackendRequest,VelocityContext context)
    throws Exception {
        log.debug("enter makePlacemarksKML");

        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        String output = lasBackendRequest.getResultAsFile("kml");
        String kmz = lasBackendRequest.getResultAsFile("kmz");
        String baseURL = kmlBackendConfig.getHttpBaseURL();
        

        if ( output.equals("") ) {
        	output = kmz.replaceAll("kmz", "kml");
        }
        

        //get the files written by the Ferret backend service (last step of the compound operation)
        String ferret_listing_file = lasBackendRequest.getChainedDataFile("ferret_listing");
        String las_req_info_file = lasBackendRequest.getChainedDataFile("las_request_info");

        //get file name of the KML template
        String kml = lasBackendRequest.getServiceAction();
        if (!kml.endsWith(".vm")) {
            kml = kml+".vm";
        }

        boolean isInsitu = false;
        if(kml.contains("insitu") || kml.contains("osmc")){
			isInsitu=true;
		}

        ArrayList allPlacemarks;
        HashMap<String, String> initLASReq = new HashMap<String, String>();

        LASPlacemarks lps;

        //osmc
        if(kml.contains("osmc")){
			initLASReq = LASReqInfoOSMC.getLASReqInfo(las_req_info_file);
	        lps = new LASOSMCPlacemarks(ferret_listing_file, initLASReq, baseURL);
	    }else{
			//regular grid
			initLASReq = LASReqInfoRegular.getLASReqInfo(las_req_info_file);
	        lps = new LASRegularPlacemarks(ferret_listing_file, initLASReq, baseURL);
	    }

        allPlacemarks = lps.getPlacemarks();

        //output to velocity context
        context.put("gridLon",lps.getLookAtLon());
        context.put("gridLat",lps.getLookAtLat());
        context.put("dsID",initLASReq.get("dsID"));
        //if(kml.contains("osmc") && (initLASReq.get("varID")).equalsIgnoreCase("ID")){
		//	log.info("all parameters");
	    //    context.put("varID","all parameters");
		//}else{
            context.put("varID",initLASReq.get("varID"));
	    //}
        if(!isInsitu){
            //System.out.println("xstride value is "+initLASReq.get("xstride"));
            context.put("xstride",initLASReq.get("xstride"));
            context.put("ystride",initLASReq.get("ystride"));
            context.put("xstride_coord",initLASReq.get("xstride_coord"));
            context.put("ystride_coord",initLASReq.get("ystride_coord"));
        }
        context.put("allPlacemarks", allPlacemarks);

        log.debug("finish creating allPlacemarks");

        PrintWriter kmlWriter = null;
        try {
            kmlWriter = new PrintWriter(new FileOutputStream(new File(output)));
        }catch(Exception e) {
            // We need to package these and send them back to the UI.
        }
        ve.mergeTemplate(kml,"ISO-8859-1", context, kmlWriter);
        if ( kmlWriter != null )
        	kmlWriter.close();
        
        writeKMZ(kmz, output);
        
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);

        return lasBackendResponse;
    }
    private void writeKMZ(String kmz, String kml) {
    	final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];
        if ( kmz != null ) {
        	try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(kmz));
				FileInputStream in = new FileInputStream(kml);
				File file = new File(kml);
				String entry = file.getName();
				out.putNextEntry(new ZipEntry(entry));
				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(data)) > 0) {
					out.write(data, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();

				// Complete the ZIP file
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
