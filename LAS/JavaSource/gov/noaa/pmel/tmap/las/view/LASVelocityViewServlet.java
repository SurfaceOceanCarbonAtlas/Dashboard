/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.view;

import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

/**
 * @author Roland Schweitzer
 *
 */
public class LASVelocityViewServlet extends VelocityViewServlet {
    protected void setContentType(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
    	response.setHeader("X-XSS-Protection", "0");
        String las_mime_type = (String) request.getAttribute("las_mime_type");
        if ( las_mime_type != null && !las_mime_type.equals("") ) {
            // Set it to default values to get the default encoding.
            super.setContentType(request, response);
            // Reset the content type.
            response.setContentType(las_mime_type);
            response.setHeader("Content-disposition", "inline;filename=\"las_product.kml\"");
        } else {
            super.setContentType(request, response);
        }
    }

}
