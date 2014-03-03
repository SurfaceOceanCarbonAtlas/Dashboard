/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Client resources used by the SOCAT upload dashboard
 * 
 * @author Karl Smith
 */
public interface DashboardResources extends ClientBundle {

	@Source("socat_cat.png")
	ImageResource getSocatCatPng();

}
