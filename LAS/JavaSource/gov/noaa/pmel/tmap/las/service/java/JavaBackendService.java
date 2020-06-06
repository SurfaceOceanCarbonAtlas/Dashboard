/**
 * 
 */
package gov.noaa.pmel.tmap.las.service.java;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASMapScale;
import gov.noaa.pmel.tmap.las.jdom.LASRegionIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jdom.JDOMException;

/**
 * @author Roland Schweitzer
 *
 */
public class JavaBackendService {
    private static Logger log = LoggerFactory.getLogger(JavaBackendService.class.getName());
    
    public String fiveMinutes(String backendRequestXML, String cacheFileName) throws JDOMException, IOException, Exception {
        LASBackendRequest lasBackendRequest = new LASBackendRequest();    
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        JDOMUtils.XML2JDOM(backendRequestXML, lasBackendRequest);
        
        String cancel = lasBackendRequest.getResultAsFile("cancel");
        File cf = null;
        if ( cancel != null && !cancel.equals("") ) {
            cf = new File(cancel);
        }
        
        // If it's the cancel job, set the file and return.
        if ( lasBackendRequest.isCancelRequest() && cf != null ) {
            lasBackendResponse.setError("Java backend request canceled.");
           
            try {
                cf.createNewFile();
            } catch (Exception e) {
                lasBackendResponse.setError("fiveMinutes backend failed to cancel request. ", e);
            }
            log.debug("Java backend request canceled: "+lasBackendRequest.toCompactString());
            return lasBackendResponse.toString();
        }
        // If it's not the cancel job, sleep for 30 then look for the cancel job.  Repeat for 5 minutes.
        for ( int i = 0; i < 6; i++ ) {
            try {
                Thread.currentThread().sleep(1000*20);
            } catch (InterruptedException e) {
                lasBackendResponse.setError("fiveMinutes had trouble sleeping. ", e);
            }
            if ( cf != null && cf.exists() ) {
                lasBackendResponse.setError("Java backend request canceled.");
                if ( !cf.delete() ) {
                    lasBackendResponse.setError("Could not remove cancel file.");
                }
                return lasBackendResponse.toString();
            }
        }
        
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        return lasBackendResponse.toString();
        
    }
    public String makeRegionIndex(String backendRequestXML, String cacheFileName) throws IOException, JDOMException {
        LASBackendRequest lasBackendRequest = new LASBackendRequest();      
        JDOMUtils.XML2JDOM(backendRequestXML, lasBackendRequest);
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        
        String sample = JDOMUtils.getResourcePath(this, "resources/java/xml/region_index.xml");
        String output = lasBackendRequest.getResultAsFile("index");
        copyBytes(sample, output);
        // Create the map scale XML file if requested.
        String index_filename = lasBackendRequest.getResultAsFileByType("index");
        if ( index_filename != null && !index_filename.equals("") ) {
            File index = new File(index_filename);
            LASRegionIndex lasRegionIndex = new LASRegionIndex(index);
            lasRegionIndex.write(index);
        }
                
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        return lasBackendResponse.toString();
    }
    public String copyImage(String backendRequestXML, String cacheFileName) throws IOException, JDOMException  {       
        LASBackendRequest lasBackendRequest = new LASBackendRequest();      
        JDOMUtils.XML2JDOM(backendRequestXML, lasBackendRequest);
        LASBackendResponse lasBackendResponse = new LASBackendResponse();    

        String sample = JDOMUtils.getResourcePath(this, "resources/java/images/sample.gif");
        String output = lasBackendRequest.getResultAsFile("plot_image");
        if ( output != null && !output.equals("") ) {
            copyBytes(sample, output);
        }

        sample = JDOMUtils.getResourcePath(this, "resources/java/images/sample.gif");
        output = lasBackendRequest.getResultAsFile("colorbar");
        if ( output != null && !output.equals("") ) {
            copyBytes(sample, output);
        }

        sample = JDOMUtils.getResourcePath(this, "resources/java/xml/map_scale.xml");
        output = lasBackendRequest.getResultAsFile("map_scale");
        if ( output != null && !output.equals("") ) {
           copyBytes(sample, output);
        }

        sample = JDOMUtils.getResourcePath(this, "resources/java/images/ref_map.gif");
        output = lasBackendRequest.getResultAsFile("ref_map");
        
        if ( output != null && !output.equals("") ) {
            copyBytes(sample, output);
        }        
        HashMap<String, String> symbols = new HashMap<String, String>();
        
        // Translate the results symbols for use by Ferret.
       
        int count = lasBackendRequest.getResultCount();
        symbols.put("result_count", Integer.valueOf(count).toString());
        for (int index=0; index < count; index++ ) {
            String ID = lasBackendRequest.getResultID(index);
            String filename = lasBackendRequest.getResultFileName(index);
            String type = lasBackendRequest.getResultType(index);
            symbols.put("result_"+ID+"_filename", filename);
            symbols.put("result_"+ID+"_type", type);
            symbols.put("result_"+ID+"_ID", ID);
        }
        
        // Global properties
        
        symbols.putAll(lasBackendRequest.getSymbols());
        
        // Data Objects.
        
        // Data symbols
        symbols.putAll(lasBackendRequest.getDataSymbols());
        
        // Add a count symbol
        symbols.put("data_count", String.valueOf(lasBackendRequest.getDataCount()));
        
        // Data regions
        symbols.putAll(lasBackendRequest.getRegionsAsSymbols());
        
        // Data objects
        symbols.putAll(lasBackendRequest.getDataAsSymbols());
        
        // Constraints       
        symbols.putAll(lasBackendRequest.getConstraintsAsSymbols());
        
        output = lasBackendRequest.getResultAsFile("debug");
        
        PrintWriter debug;
        
        if ( output != null && !output.equals("") ) {
            File debug_file = new File(output);
            debug = new PrintWriter(debug_file);
            debug.println("Symbols:");
            Vector<String> v = new Vector<String>(symbols.keySet());
            Collections.sort(v);
            for (Iterator symIt = v.iterator(); symIt.hasNext();) {
                String key = (String) symIt.next();
                debug
                        .println("DEFINE SYMBOL " + key + " = "
                                + symbols.get(key));
            }
            debug.close();
        }        
             
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        return lasBackendResponse.toString();
    }
    private void copyBytes (String inputFile, String outputFile) throws IOException {
    	FileInputStream in = null;
		FileOutputStream out = null;
    	try {
    		in = new FileInputStream(inputFile);
    		out = new FileOutputStream(outputFile);
    		int c;

    		while ((c = in.read()) != -1)
    			out.write(c);

    		in.close();
    		out.close();
    	} catch (Exception e) {
    		
    	} finally {
    		if ( in != null ) 
    			in.close();
    		if ( out != null ) 
    		    out.close();
    	}
    }
}
