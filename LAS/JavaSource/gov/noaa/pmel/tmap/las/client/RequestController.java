/**
 * 
 */
package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.event.LASRequestEvent;
import gov.noaa.pmel.tmap.las.client.util.MultiCallback;

/**
 * @author weusijana
 *
 */
public interface RequestController {

	void done(MultiCallback multiCallback);
	void process(LASRequestEvent event);
}
