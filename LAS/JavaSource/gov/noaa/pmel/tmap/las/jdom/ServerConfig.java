/**
 * 
 */
package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.util.NameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jdom.Element;

/**
 * @author Roland Schweitzer
 *
 */
public class ServerConfig extends LASDocument {
    
	public ArrayList<NameValuePair> getServiceNamesAndURLs() {
		ArrayList<NameValuePair> services = new ArrayList<NameValuePair>();
		List serviceElements = this.getRootElement().getChild("servers").getChildren("service");
        for (Iterator sIt = serviceElements.iterator(); sIt.hasNext();) {
            Element service = (Element) sIt.next();
            String name = service.getAttributeValue("name");
            String url = service.getAttributeValue("url");
            if ( url == null || url.equals("local") ) {
                url = "Local Service";
            }
            NameValuePair s = new NameValuePair(name, url);
            services.add(s);
        }
        return services;
	}
    public Element getServer(String name) {
        List services = this.getRootElement().getChild("servers").getChildren("service");
        for (Iterator sIt = services.iterator(); sIt.hasNext();) {
            Element service = (Element) sIt.next();
            if (name.equals(service.getAttributeValue("name"))) {
                return service;
            }
        }
        return null;
    }
    
    public String getServerURL(String name) {
        Element server = getServer(name);
        if ( server != null ) {
            return server.getAttributeValue("url");
        } else {
            return null;
        }
    }

    public int getCacheSize() {
       Element cache = this.getRootElement().getChild("cache");
       if ( cache != null ) {
           String size = cache.getAttributeValue("size");
           if (size != null) {
               return Integer.valueOf(size).intValue();
           } else {
               return 10000;
           }
       }
       return 10000;
    }

    public File getCacheFile() {
        Element cache = this.getRootElement().getChild("cache");
        if ( cache != null ) {
            String cacheFileName = cache.getAttributeValue("file");
            if (cacheFileName != null) {
                return new File(cacheFileName);
            } else {
                return null;
            }
        }
        return null;

    }

    public String getMethodName(String serverName) {
        Element server = getServer(serverName);
        if (server != null) {
            return server.getAttributeValue("method");
        }
        return null;
    }
    /**
     * @param serviceName
     * @return
     */
    public boolean isRemote(String serviceName) {
        Element server = getServer(serviceName);
        if (server != null) {
            String remote = server.getAttributeValue("remote");
            if ( remote != null && remote.equals("true") ) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    /**
     * @return
     */
    public String getSMTPHost() {
        Element smtp = getRootElement().getChild("smtp");
        String host = smtp.getAttributeValue("host");
        if ( host != null && !host.equals("") ) {
            return host;
        }
        // Try localhost if none is defined.
        return "localhost";
    }
    
    /**
     * @return
     */
    public String getFTDSBase() {
        Element ftds = getRootElement().getChild("ftds");
        String base_url = ftds.getAttributeValue("base_url");
        if ( base_url != null && !base_url.equals("") ) {
            if ( !base_url.endsWith("/") ) {
                base_url = base_url+"/";
            }
            return base_url;
        }
        return "";
    }
    public String getFTDSDir() {
        Element ftds = getRootElement().getChild("ftds");
        String data_dir = ftds.getAttributeValue("data_dir");
        if ( data_dir != null && !data_dir.equals("") ) {
            if ( !data_dir.endsWith("/") ) {
                data_dir = data_dir+"/";
            }
            return data_dir;
        }
        return "";
    }
    public long getCacheMaxBytes() {
    	Element cache = this.getRootElement().getChild("cache");
    	if ( cache != null ) {
    		String size = cache.getAttributeValue("bytes");
    		if (size != null) {
    			if ( size.toLowerCase(Locale.ENGLISH).contains("mb")) {
    				size = size.substring(0, size.toLowerCase(Locale.ENGLISH).indexOf("mb"));
    				return Long.valueOf(size).longValue() * (long)(Math.pow(2, 20));  // number of megabytes * 1 megabyte :-)
    			}
    			return Long.valueOf(size).longValue();
    		} else {
    			return 500*(long)(Math.pow(2, 20));
    		}
    	}
    	return 500*(long)(Math.pow(2, 20));
    }
}
