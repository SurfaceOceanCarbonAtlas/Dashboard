/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseChecker;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * Correct the XML, as well as any PI and vessel names, in the OME XML files.
 * 
 * @author Karl Smith
 */
public class FixOmeMetadata {

	/**
	 * @param args
	 * 		ExpocodesFile - fix the OME XML files for these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Corrects the XML, as well as any PI and vessel names, "); 
			System.err.println("in the OME XML files for cruises specified in ExpocodesFile.  ");
			System.err.println("The default dashboard configuration is used for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			// Get the expocode of the cruises to update
			TreeSet<String> allExpocodes = new TreeSet<String>();
			try {
				BufferedReader expoReader = new BufferedReader(new FileReader(expocodesFilename));
				try {
					String dataline = expoReader.readLine();
					while ( dataline != null ) {
						dataline = dataline.trim();
						if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
							allExpocodes.add(dataline);
						dataline = expoReader.readLine();
					}
				} finally {
					expoReader.close();
				}
			} catch (Exception ex) {
				System.err.println("Error getting expocodes from " + 
						expocodesFilename + ": " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
			CruiseChecker cruiseChecker = configStore.getDashboardCruiseChecker();
			MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				try {

					// Read the original data for this cruise and sanity check 
					// to correct the XML and update with the lon/lat/time bounds
					DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);
					if ( ! cruiseChecker.checkCruise(cruiseData) )
						throw new IllegalArgumentException("Sanity check failed");

					// Fix the vessel and PI names
					DashboardOmeMetadata omeMData = new DashboardOmeMetadata(
							metaHandler.getMetadataInfo(expocode, DashboardMetadata.OME_FILENAME), metaHandler);
					// Kludgey - working with the XML instead of OmeMetadata methods
					Document omeXmlDoc = omeMData.createOmeXmlDoc();
					boolean changed = false;

					Element rootElem = omeXmlDoc.getRootElement();
					for ( Element invElem : rootElem.getChildren("Investigator") ) {
						Element invNameElem = invElem.getChild("Name");
						if ( invNameElem == null )
							throw new IllegalArgumentException("Investigator element without a Name");
						String name = invNameElem.getTextTrim();
						String stdName = SocatMetadata.PI_RENAME_MAP.get(name);
						if ( stdName == null )
							stdName = name;
						String correctName = SocatMetadata.PI_NAME_CORRECTIONS.get(stdName);
						if ( correctName == null )
							correctName = stdName;
						if ( ! correctName.equals(name) ) {
							invNameElem.setText(correctName);
							changed = true;
						}
					}

					Element vesselNameElem;
					try {
						vesselNameElem = rootElem.getChild("Cruise_Info").getChild("Vessel").getChild("Vessel_Name");
					} catch ( NullPointerException ex ) {
						throw new IllegalArgumentException("No vessel name element");
					}
					String name = vesselNameElem.getTextTrim();
					String stdName = SocatMetadata.VESSEL_RENAME_MAP.get(name);
					if ( stdName == null )
						stdName = name;
					String correctName = SocatMetadata.VESSEL_NAME_CORRECTIONS.get(stdName);
					if ( correctName == null )
						correctName = stdName;
					if ( ! correctName.equals(name) ) {
						vesselNameElem.setText(correctName);
						changed = true;
					}

					if ( changed ) {
						omeMData = new DashboardOmeMetadata(expocode, omeMData.getUploadTimestamp(), omeXmlDoc);
						metaHandler.saveAsOmeXmlDoc(omeMData, "Correction of PI names and/or vessel name");
						System.out.println("PI and/or vessel name updated in the OME for " + expocode);
					}
					else {
						System.out.println("No PI or vessel name changes in the OME for " + expocode);
					}

				} catch ( Exception ex ) {
					System.err.println("Problems working with the data for " + expocode + ": " + ex.getMessage());
					success = false;
					continue;
				}
			}
		} finally {
			configStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
