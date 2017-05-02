/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import uk.ac.uea.socat.omemetadata.OmeMetadata;

/**
 * Updates dashboard's OME.xml from values given in the PI-provided OME.xml
 * 
 * @author Karl Smith
 */
public class UpdateFromPIOme {

	/**
	 * @param args
	 * 		expocode -	expoocode of the dataset to update the OME.xml from the PI_OME.xml
	 */
	public static void main(String[] args) {
		String expocode;
		try {
			expocode = DashboardServerUtils.checkExpocode(args[0]);
		} catch ( Exception ex ) {
			expocode = null;
		}
		if ( (args.length != 1) || (expocode == null) ) {
			if ( args.length == 1 ) {
				System.err.println("");
				System.err.println("expocode not valid: " + args[0]);
			}
			System.err.println("");
			System.err.println("Arguments: expocode");
			System.err.println("");
			System.err.println("For the dataset with the given expocode, update some basic information ");
			System.err.println("in the dashboard OME.xml from the values in the PI-provided OME.xml document. ");
			System.err.println("PI names in the PI-provided OME.xml will be modified to 'lastname, firstinitial' ");
			System.err.println("as best possible.  Other values are copied as given: organization for each PI, ");
			System.err.println("platform name, and platform type. ");
			System.err.println("");
			System.exit(1);
		}

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {

			MetadataFileHandler mdataHandler = configStore.getMetadataFileHandler();
			String platformName = null;
			String platformType = null;
			ArrayList<String> piNames = null;
			ArrayList<String> piOrgs = null;
			try {
				File piOmeFile = mdataHandler.getMetadataFile(expocode, DashboardUtils.PI_OME_FILENAME);
				Document omeDoc = (new SAXBuilder()).build(piOmeFile);
				OmeMetadata piOme = new OmeMetadata(expocode);
				piOme.assignFromOmeXmlDoc(omeDoc);
				platformName = piOme.getVesselName();
				platformType = piOme.getValue(OmeMetadata.PLATFORM_TYPE_STRING);
				piNames = piOme.getInvestigators();
				piOrgs = piOme.getOrganizations();
			} catch ( Exception ex ) {
				System.err.println("Problems reading the PI-provided OME file: " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			File dashOmeFile = mdataHandler.getMetadataFile(expocode, DashboardUtils.OME_FILENAME);
			OmeMetadata dashOme = new OmeMetadata(expocode);
			try {
				Document omeDoc = (new SAXBuilder()).build(dashOmeFile);
				dashOme.assignFromOmeXmlDoc(omeDoc);
				dashOme.replaceValue(OmeMetadata.VESSEL_NAME_STRING, platformName, -1);
				dashOme.replaceValue(OmeMetadata.PLATFORM_TYPE_STRING, platformType, -1);
				dashOme.clearCompositeValueList(OmeMetadata.INVESTIGATOR_COMP_NAME);
				for (int k = 0; k < piNames.size(); k++) {
					String last = null;
					String[] firsts = null;
					int numFirsts = 0;
					String[] pieces = piNames.get(k).split(",");
					if ( pieces.length > 1 ) {
						last = pieces[0];
						firsts = pieces[1].split("\\s+");
						numFirsts = firsts.length;
					}
					else {
						firsts = pieces[0].split("\\s+");
						numFirsts = pieces.length - 1;
						last = pieces[numFirsts];
					}
					String lastFI = last + ", ";
					for (int j = 0; j < numFirsts; j++) {
						String name = firsts[j];
						if ( "Dr".equalsIgnoreCase(name) || "Dr.".equalsIgnoreCase(name) ||
							 "Pf".equalsIgnoreCase(name) || "Pf.".equalsIgnoreCase(name) )
							continue;
						lastFI += firsts[j].substring(0, 1) + ".";
					}
					Properties props = new Properties();
					props.setProperty(OmeMetadata.NAME_ELEMENT_NAME, lastFI);
					props.setProperty(OmeMetadata.ORGANIZATION_ELEMENT_NAME, piOrgs.get(k));
					dashOme.storeCompositeValue(OmeMetadata.INVESTIGATOR_COMP_NAME, props, -1);
				}
			} catch ( Exception ex ) {
				System.err.println("Problems reading (or updating) the dashboard OME file: " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			try {
				Document dashDoc = dashOme.createOmeXmlDoc();
				FileOutputStream out = new FileOutputStream(dashOmeFile);
				try {
					(new XMLOutputter(Format.getPrettyFormat())).output(dashDoc, out);
				} finally {
					out.close();
				}
			} catch (IOException ex) {
				throw new IllegalArgumentException("Problems writing the updated dashboard OME: " + ex.getMessage());
			}

		} finally {
			DashboardConfigStore.shutdown();
		}
		System.exit(0);
	}

}
