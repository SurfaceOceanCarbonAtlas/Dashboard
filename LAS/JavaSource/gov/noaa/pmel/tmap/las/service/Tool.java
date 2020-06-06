package gov.noaa.pmel.tmap.las.service;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;

import java.net.URISyntaxException;
import java.net.URL;

public class Tool {
    public String getResourcePath(String resource) {
        
        return JDOMUtils.getResourcePath(this, resource);
        
    }
}
