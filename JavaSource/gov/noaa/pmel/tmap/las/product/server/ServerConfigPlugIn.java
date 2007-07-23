/**
 * 
 */
package gov.noaa.pmel.tmap.las.product.server;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.ServerConfig;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/**
 * @author Roland Schweitzer
 *
 */
public class ServerConfigPlugIn implements PlugIn {
    
    /**  Servlet context key server config is stored under */
    public final static String SERVER_CONFIG_KEY = "server_config";
    
    /**  Servlet context key server config is stored under */
    public final static String CACHE_KEY = "cache";
    
    private ServletContext context;
    
    /** File name from plug-in config **/
    private String configFileName=null;
    
    public String getConfigFileName() {
        return configFileName;
    }
    
    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }
    
    /**
     *  Initialize the ServerConfigPlugIn
     *
     *@param  servlet               The ActionServlet for this web application
     *@param  config                The ModuleConfig for our owning module
     * @throws Exception 
     */
    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
        
        ServletContext context = servlet.getServletContext();
        
        if ((configFileName == null || configFileName.length() == 0)) {
            throw new ServletException("No LAS configuration file specified.");
        }
        
        File configFile = new File(configFileName);
        ServerConfig serverConfig = new ServerConfig();
        
        try {
            JDOMUtils.XML2JDOM(configFile, serverConfig);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        context.setAttribute(SERVER_CONFIG_KEY, serverConfig);
        
        // Create the Cache
        Cache cache = new Cache(serverConfig.getCacheSize());
        
        // Read the Cache if it exists
        File cacheFile = serverConfig.getCacheFile();
        if ( cacheFile != null) {
            try {
                cache.loadCacheFromStore(cacheFile);
            } catch (Exception e) {
                // TODO Log cache file store not set and go on.
            }
        }
        
        // Store the cache in the context.
        context.setAttribute(CACHE_KEY, cache);
        
    }
    
    public void destroy() {
        
        // ???? Factory.getContinuationsManager().destroy();
        
        context.removeAttribute(SERVER_CONFIG_KEY);
        context.removeAttribute(CACHE_KEY);
        context = null;
        
    }
}
