package gov.noaa.pmel.tmap.las.service.tabledap;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.service.BackendService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TabledapBackendService extends BackendService {
    private final static Logger log = LoggerFactory.getLogger(TabledapBackendService.class.getName());
    public String getProduct(String backendRequestXML, String outputFileName) throws Exception {
        try {
            LASBackendRequest lasBackendRequest = new LASBackendRequest();      
            JDOMUtils.XML2JDOM(backendRequestXML, lasBackendRequest);
            LASBackendResponse lasBackendResponse = new LASBackendResponse();

            if ( lasBackendRequest.isCancelRequest()) {
                lasBackendResponse.setError("error", "Database backend request canceled.");
                log.debug("Database backend request canceled: "+lasBackendRequest.toCompactString());
                return lasBackendResponse.toString();
            }
            
            String erddap_server = lasBackendRequest.getProperty("tabledap_access", "server");
            if ( erddap_server != null && !erddap_server.equals("") ) {
                TabledapTool tool = new TabledapTool();
                lasBackendResponse = tool.run(lasBackendRequest);
                return lasBackendResponse.toString();
            }
            lasBackendResponse.setError("Error", "Extraction service type not found or not supported.");
            return lasBackendResponse.toString();
        } catch (Exception e) {
            throw new LASException(e.getMessage());
        }
    }
}
