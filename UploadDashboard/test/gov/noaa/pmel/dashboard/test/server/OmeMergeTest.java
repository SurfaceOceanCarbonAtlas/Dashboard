/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import java.io.IOException;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import org.junit.Test;

/**
 * Test the merging of two OME XML metadata documents
 * 
 * @author Karl Smith
 */
public class OmeMergeTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashboardOmeMetadata#mergeModifiable(gov.noaa.pmel.dashboard.server.DashboardOmeMetadata)}.
	 * Uses the default Dashboard configuration file and OME XML documents for some of the datasets.
	 */
	@Test
	public void testMergeModifiable() throws IOException {
		final String previousExpocode = "32HQ20110517";
		final String activeExpocode = "Z2HQ20110517";

		String home = System.getenv("HOME");
		System.setProperty("CATALINA_BASE", home);
		DashboardConfigStore configStore = DashboardConfigStore.get(false);
		MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
		// Read the OME XML contents for previousExpocode 
		DashboardMetadata mdata = metadataHandler.getMetadataInfo(previousExpocode, DashboardUtils.OME_FILENAME);
		DashboardOmeMetadata updatedOmeMData = new DashboardOmeMetadata(mdata, metadataHandler);
		// Reset the expocode and related fields to that for activeExpocode 
		updatedOmeMData.changeExpocode(activeExpocode);
		// Read the OME XML contents currently saved for activeExpocode
		mdata = metadataHandler.getMetadataInfo(activeExpocode, DashboardUtils.OME_FILENAME);
		DashboardOmeMetadata origOmeMData = new DashboardOmeMetadata(mdata, metadataHandler);
		// Create the merged OME and save the results
		DashboardOmeMetadata mergedOmeMData = origOmeMData.mergeModifiable(updatedOmeMData);
		metadataHandler.saveAsOmeXmlDoc(mergedOmeMData, "Merged OME of " + previousExpocode + 
														" into OME of " + activeExpocode);
	}

}
