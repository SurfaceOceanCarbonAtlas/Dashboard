/**
 * 
 */
package gov.noaa.pmel.tmap.las.service;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.view.XMLToolboxManager;

/**
 * @author Roland Schweitzer
 *
 */
public class ProductService {
    protected LASBackendRequest lasBackendRequest;
    protected String serverURL;
    protected String methodName;
    protected String outputFileName;
    protected int timeout = 120*1000;
    protected String responseXML;
    public VelocityEngine ve = new VelocityEngine();
    Map toolboxContext;
    
    private final static Logger log = LoggerFactory.getLogger(ProductService.class.getName());
    
    public ProductService (LASBackendRequest lasBackendRequest, String serverURL, String methodName, String outputFileName) throws LASException, IOException {
        this.lasBackendRequest = lasBackendRequest;
        this.serverURL = serverURL;
        this.methodName = methodName;
        this.outputFileName = outputFileName;
        this.responseXML = null;

        Properties p = new Properties();
        InputStream is;
        String resourcePath = "resources/local";
        is = this.getClass().getClassLoader().getResourceAsStream(resourcePath+"/velocity.properties");
        String template = JDOMUtils.getResourcePath(this, resourcePath+"/templates");
        if (is == null) {
            if ( template != null ) {
                // Can't find properties file.  Set where we look for templates.
                p.setProperty("file.resource.loader.path", template);
            } else {
                throw new LASException("Cannot find the local service templates directory.");
            }
        } else {
           p.load(is);
        }  
        if ( p.getProperty("file.resource.loader.path") == null ) {
            if ( template != null ) {
                // Can't find properties file.  Set where we look for templates.
                log.debug("Template path not found in properties file.  Setting to default: "+template);
                p.setProperty("file.resource.loader.path", template);
            } else {
                throw new LASException("Cannot find database templates directory.");
            }
        }
        if ( p.getProperty("runtime.log") == null ) {
        	log.debug("Setting runtime velocity log to /dev/null.");
        	p.setProperty("runtime.log", "/dev/null");
        }
        try {
            log.debug("Setting runtime log to /dev/null and loader path to: "+p.getProperty("file.resource.loader.path"));
            ve.init(p);
        } catch (Exception e) {
            throw new LASException("Cannot initialize the velocity engine.");
        }
        
        XMLToolboxManager toolboxManager = new XMLToolboxManager();
        InputStream tbis = null;
        try {
            tbis = this.getClass().getClassLoader().getResourceAsStream("resources/services/toolbox.xml");
            if ( tbis == null ) {
                String toolboxPath = JDOMUtils.getResourcePath(this, "resources/services/toolbox.xml");
                tbis = new FileInputStream(toolboxPath);
                throw new LASException("The Velocity toolbox configuration toolbox.xml not found. ");
            }
            toolboxManager.load(tbis);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new LASException("The Velocity toolbox configuration toolbox.xml not found. "+e.toString());
        } finally {
        	if ( tbis != null ) 
        		tbis.close();
        }
        toolboxContext = toolboxManager.getToolbox(null);
        
    }
    
    protected String error_response(String message, Exception e) {
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        lasBackendResponse.setError(message, e);
        return lasBackendResponse.toString();
    }
    protected String error_response(String message) {
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        lasBackendResponse.setError("Invocation of the backend service failed.", message);
        return lasBackendResponse.toString();
    }
    /**
     * @return Returns the responseXML.
     */
    public String getResponseXML() {
        return responseXML;
    }

    /**
     * @param responseXML The responseXML to set.
     */
    public void setResponseXML(String responseXML) {
        this.responseXML = responseXML;
    }
    public Map getToolboxContext() {
        return toolboxContext;
    }
    public void setTimeout(int t) {
    	timeout = t;
    }
}
