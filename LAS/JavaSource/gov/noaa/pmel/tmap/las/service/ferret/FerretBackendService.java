package gov.noaa.pmel.tmap.las.service.ferret;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.service.BackendService;

import java.util.Date;
import java.util.logging.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FerretBackendService extends BackendService {
	// This object could be persisted between requests if this
	// were packaged in a servlet.
	private static Logger log = LoggerFactory.getLogger(FerretBackendService.class
			.getName());

	private final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(FerretBackendService.class.getName());

	public String getProduct(String backendRequestXML, String cacheFileName) throws Exception {  
       
        LASBackendRequest lasBackendRequest = new LASBackendRequest();      
        JDOMUtils.XML2JDOM(backendRequestXML, lasBackendRequest);
        log.info(new Date().toString()+ ":"+"START: "+lasBackendRequest.getServiceAction());
        log.debug(new Date().toString()+ ":"+"Starting request: "+lasBackendRequest.toCompactString());
  
        LASBackendResponse lasBackendResponse = new LASBackendResponse();    
        
		String logMessage = new Date().toString()+
				":Thread " + Thread.currentThread().getId() + 
				" is working on "+lasBackendRequest.getCancelFile();
		System.err.println(logMessage);
        log.debug(logMessage);
        logger.info(logMessage);
        
        if ( lasBackendRequest.isCancelRequest()) {           
            lasBackendResponse.setError("Ferret backend request canceled.");
            log.debug(new Date().toString()+ ":"+"Ferret backend request canceled: "+lasBackendRequest.toCompactString());
            return lasBackendResponse.toString();
        }
        FerretTool ferretTool = new FerretTool();
        lasBackendResponse = ferretTool.run(lasBackendRequest);
        if ( lasBackendResponse.getError() != null && !lasBackendResponse.getError().equals("") ) {
          log.error(new Date().toString()+ ":"+"Ferret backend request failed: "+lasBackendResponse.getError());
        }
        log.debug(new Date().toString()+ ":"+"Ending request: "+lasBackendRequest.toCompactString());
        log.info(new Date().toString()+ ":"+"END:   "+lasBackendRequest.getServiceAction());
        return lasBackendResponse.toString();
    }
}
