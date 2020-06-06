/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development.
 */
package gov.noaa.pmel.tmap.las.service.kml;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.service.BackendService;

/**
 * @author Roland Schweitzer
 *
 */
public class KMLBackendService extends BackendService {
    public String mapScaleToKML(String backendRequestXML, String cacheFileName) throws Exception {

        KMLTool kmlTool = new KMLTool();

        LASBackendRequest lasBackendRequest = new LASBackendRequest();      
        JDOMUtils.XML2JDOM(backendRequestXML, lasBackendRequest);

        LASBackendResponse lasBackendResponse = kmlTool.run(lasBackendRequest);

        return lasBackendResponse.toString();
    }
}
