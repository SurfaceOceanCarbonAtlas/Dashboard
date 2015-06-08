/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import java.io.IOException;

import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import org.junit.Test;

/**
 * Test the merging of two OME XML metadata documents
 * 
 * @author Karl Smith
 */
public class OmeMergeTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata#mergeModifiable(gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata)}.
	 * Uses the default Dashboard configuration file and OME XML documents for some of the datasets.
	 */
	@Test
	public void testMergeModifiable() throws IOException {
		final String previousExpocode = "32HQ20110517";
		final String activeExpocode = "Z2HQ20110517";

		DashboardConfigStore configStore = DashboardConfigStore.get();
		MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
		// Read the OME XML contents for previousExpocode 
		DashboardMetadata mdata = metadataHandler.getMetadataInfo(previousExpocode, DashboardMetadata.OME_FILENAME);
		DashboardOmeMetadata updatedOmeMData = new DashboardOmeMetadata(mdata, metadataHandler);
		// Reset the expocode and related fields to that for activeExpocode 
		updatedOmeMData.changeExpocode(activeExpocode);
		// Read the OME XML contents currently saved for activeExpocode
		mdata = metadataHandler.getMetadataInfo(activeExpocode, DashboardMetadata.OME_FILENAME);
		DashboardOmeMetadata origOmeMData = new DashboardOmeMetadata(mdata, metadataHandler);
		// Create the merged OME and save the results
		DashboardOmeMetadata mergedOmeMData = origOmeMData.mergeModifiable(updatedOmeMData);
		metadataHandler.saveAsOmeXmlDoc(mergedOmeMData, "Merged OME of " + previousExpocode + 
														" into OME of " + activeExpocode);
	}

}
