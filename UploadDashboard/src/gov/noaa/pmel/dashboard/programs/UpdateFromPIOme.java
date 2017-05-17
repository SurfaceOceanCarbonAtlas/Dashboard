/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

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

		Pattern commaPat = Pattern.compile(",");
		Pattern spdotPat = Pattern.compile("[\\s\\.]+");
		Pattern punctPat = Pattern.compile("\\p{Punct}+");
		Pattern spacePat = Pattern.compile("\\s+");

		Pattern atmosPat1 = Pattern.compile("Atmosphere");
		Pattern atmosPat2 = Pattern.compile("Atmospheric");
		Pattern envPat1   = Pattern.compile("Environmental");
		Pattern envPat2   = Pattern.compile("Environment");
		Pattern instPat   = Pattern.compile("Institute");
		Pattern labPat    = Pattern.compile("Laboratory");
		Pattern univPat   = Pattern.compile("University");

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
			String platformName = "";
			String platformType = "";
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

			if ( platformName.startsWith("MS ") || 
				 platformName.startsWith("MV ") || 
				 platformName.startsWith("RS ") || 
				 platformName.startsWith("RV ") ) {
				platformName = platformName.substring(3);
			}
			else if ( platformName.startsWith("M/S ") || 
					  platformName.startsWith("M/V ") ||
					  platformName.startsWith("R/S ") ||
					  platformName.startsWith("R/V ") ) {
				platformName = platformName.substring(4);
			}
			else if ( platformName.startsWith("ARSV ") ) {
				platformName = platformName.substring(5);
			}

			if ( "SAAgulhasII".equals(platformName) ) {
				platformName = "S.A. Agulhas II";
			}

			File dashOmeFile = mdataHandler.getMetadataFile(expocode, DashboardUtils.OME_FILENAME);
			OmeMetadata dashOme = new OmeMetadata(expocode);
			try {
				Document omeDoc = (new SAXBuilder()).build(dashOmeFile);
				dashOme.assignFromOmeXmlDoc(omeDoc);
				if ( ! platformName.isEmpty() ) {
					System.err.println("Platform name = '" + platformName + "'");
					dashOme.replaceValue(OmeMetadata.VESSEL_NAME_STRING, platformName, -1);
				}
				if ( ! platformType.isEmpty() ) {
					System.err.println("Platform type = '" + platformType + "'");
					dashOme.replaceValue(OmeMetadata.PLATFORM_TYPE_STRING, platformType, -1);
				}
				dashOme.clearCompositeValueList(OmeMetadata.INVESTIGATOR_COMP_NAME);
				for (int k = 0; k < piNames.size(); k++) {
					String last = null;
					String[] firsts = null;
					int numFirsts = 0;
					System.err.println("PI name = '" + piNames.get(k) + "'");
					String[] pieces = commaPat.split(piNames.get(k), 2);
					if ( pieces.length > 1 ) {
						last = pieces[0].trim();
						firsts = spdotPat.split(pieces[1].trim());
						numFirsts = firsts.length;
					}
					else {
						firsts = spdotPat.split(pieces[0].trim());
						numFirsts = firsts.length - 1;
						if ( (numFirsts > 1) && 
							 ( "van".equalsIgnoreCase(firsts[numFirsts-1]) ||
							   "von".equalsIgnoreCase(firsts[numFirsts-1]) ) ) {
							numFirsts--;
							last = firsts[numFirsts] + " " + firsts[numFirsts+1];
							
						}
						else {
							last = firsts[numFirsts];
						}
					}
					String lastFI = last + ", ";
					for (int j = 0; j < numFirsts; j++) {
						String name = firsts[j];
						if ( name.isEmpty() ||
							 "Dr".equalsIgnoreCase(name) ||
							 "Pf".equalsIgnoreCase(name) ||
							 "Prof".equalsIgnoreCase(name) )
							continue;
						lastFI += firsts[j].substring(0, 1) + ".";
					}

					// Clean up the organization name
					String org = piOrgs.get(k);
					if ( "Cooperative Institute for Research in Environmental Sciences/UCB".equals(org) ) {
						org = "NOAA ESRL CIRES UCB";
					}
					else if ( "CSIRO".equals(org) ) {
						org = "CSIRO Oceans and Atmos";
					}
					else if ( "CSIR SOCO".equals(org) ) {
						org = "CSIR SOCCO";
					}
					else if ( "Department of Atmospheric and Oceanic Sciences and Institute of Arctic and Alpine Research".equals(org) ) {
						org = "IAAR Univ of Colorado";
					}
					else if ( "Meteorological Research Institute".equals(org) ) {
						org = "MRI Japan";
					}
					else if ( "National Institute for Environmental Studie".equals(org) ||
							  "National Institute for Environmental Studies".equals(org)) {
						org = "NIES Japan";
					}
					else if ( "NOAA/Atlantic Oceanographic & Meteorological Laboratory".equals(org) ) {
						org = "NOAA AOML";
					}
					else {
						// Replace all punctuation with spaces and remove all extra spaces
						org = punctPat.matcher(org).replaceAll(" ");
						org = spacePat.matcher(org).replaceAll(" ").trim();
						// Abbreviate a few common long words
						org = atmosPat1.matcher(org).replaceAll("Atmos");
						org = atmosPat2.matcher(org).replaceAll("Atmos");
						org = envPat1.matcher(org).replaceAll("Env");
						org = envPat2.matcher(org).replaceAll("Env");
						org = instPat.matcher(org).replaceAll("Inst");
						org = labPat.matcher(org).replaceAll("Lab");
						org = univPat.matcher(org).replaceAll("Univ");
					}

					System.err.println("Last name and first initial = '" + lastFI + "'");
					System.err.println("Organization = '" + org + "'");
					Properties props = new Properties();
					props.setProperty(OmeMetadata.NAME_ELEMENT_NAME, lastFI);
					props.setProperty(OmeMetadata.ORGANIZATION_ELEMENT_NAME, org);
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
