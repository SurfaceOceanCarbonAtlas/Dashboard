package gov.noaa.pmel.tmap.las.service.database;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.noaa.pmel.tmap.las.service.BackendService;

import java.io.IOException;

import org.jdom.JDOMException;


public class DatabaseBackendService extends BackendService {
    private final static Logger log = LoggerFactory.getLogger(DatabaseBackendService.class.getName());
    public String getProduct(String backendRequestXML, String outputFileName) throws IOException, JDOMException, LASException {
        LASBackendRequest lasBackendRequest = new LASBackendRequest();      
        JDOMUtils.XML2JDOM(backendRequestXML, lasBackendRequest);
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
             
        if ( lasBackendRequest.isCancelRequest()) {
            lasBackendResponse.setError("Database backend request canceled.");
            log.debug("Database backend request canceled: "+lasBackendRequest.toCompactString());
            return lasBackendResponse.toString();
        }
        DatabaseTool databaseTool = new DatabaseTool();
        log.debug("Running database tool.");
        lasBackendResponse = databaseTool.run(lasBackendRequest);
        if ( lasBackendResponse.getError() != null && !lasBackendResponse.getError().equals("") ) {
            log.error("Database backend request failed: "+lasBackendResponse.getError());
          } 
        return lasBackendResponse.toString();
    }
}
