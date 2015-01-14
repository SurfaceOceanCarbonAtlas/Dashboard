/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Downloads the metadata files at given links and associates with the corresponding cruises.  
 * 
 * @author Karl Smith
 */
public class DownloadMetadataDocs {

	/**
	 * @param args
	 * 		ExpocodeLinksFile
	 * 			List of expocodes and metadata file links; multiple links separated by space-semicolon-space
	 * 		RenamedExpocodesFile
	 * 			List of renamed expocodes in the form: Rename from (old_expocode) to (new_expocode)
	 * 		AllowOverwrite
	 * 			Allow overwriting of existing metadata files?
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if ( args.length != 3 ) {
			System.err.println();
			System.err.println("arguments:  ExpocodeLinksFile  RenamedExpocodesFile AllowOverwrite");
			System.err.println("where:");
			System.err.println("    ExpocodesLinksFile - list of expocode, tab, and metadata ");
			System.err.println("        file links; multiple links separated by space-semicolon-space ");
			System.err.println("    RenamedExpocodesdFile - list of renamed expocodes in the form: ");
			System.err.println("        \"Rename from <old_expocode> to <new_expocode>\" ");
			System.err.println("    AllowOverwrite - allow overwriting of existing metadata files? ");
			System.err.println("        (\"Y\" or \"Yes\" allows; otherwise not allowed) ");
			System.err.println();
			System.err.println("Downloads the metadata files at given links and associates with the ");
			System.err.println("corresponding cruises.  Expocodes without links or NULL for the link are ");
			System.err.println("ignored.  The default dashboard configuration is used for this process. ");
			System.err.println();
			System.exit(1);
		}

		String expoLinksFilename = args[0];
		String renamesFilename = args[1];
		boolean allowOverwrite = false;
		if ( "Y".equalsIgnoreCase(args[2]) || "YES".equalsIgnoreCase(args[2]) )
			allowOverwrite = true;

		// Read the expocode renames and create the map of old to new expocodes
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new FileReader(renamesFilename));
		} catch (Exception ex) {
			System.err.println("Problems opening the renames file for reading:");
			ex.printStackTrace();
			System.exit(1);
		}
		Pattern patt = Pattern.compile("Rename from (\\S+) to (\\S+)");
		TreeMap<String,String> renames = new TreeMap<String,String>();
		try {
			String dataline = buffReader.readLine();
			while ( dataline != null ) {
				Matcher mat = patt.matcher(dataline.trim());
				if ( ! mat.matches() ) {
					System.err.println("Unexpected rename line: '" + dataline + "'");
					System.exit(1);
				}
				String fromExpo = null;
				try {
					fromExpo = DashboardServerUtils.checkExpocode(mat.group(1));
				} catch (Exception ex) {
					System.err.println("Invalid 'from' expocode in '" + dataline + "'");
					ex.printStackTrace();
					System.exit(1);
				}
				String toExpo = null;
				try {
					toExpo = DashboardServerUtils.checkExpocode(mat.group(2));
				} catch (Exception ex) {
					System.err.println("Invalid 'to' expocode in '" + dataline + "'");
					ex.printStackTrace();
					System.exit(1);
				}
				renames.put(fromExpo, toExpo);
				dataline = buffReader.readLine();
			}
		} finally {
			buffReader.close();
		}

		// Read the expocodes and their links, and create the map of links to sets of expocodes using that link
		try {
			buffReader = new BufferedReader(new FileReader(expoLinksFilename));
		} catch (Exception ex) {
			System.err.println("Problems opening the expocode and links file for reading:");
			ex.printStackTrace();
			System.exit(1);
		}
		TreeMap<String,TreeSet<String>> linkExposMap = new TreeMap<String,TreeSet<String>>();
		try {
			String dataline = buffReader.readLine();
			while ( dataline != null ) {
				String[] expoLinks = dataline.split("\t");
				if ( expoLinks.length != 2 ) {
					System.err.println("expo-link line: '" + dataline + "' ignored");
				}
				else {
					for ( String link : expoLinks[1].split(" ; ") ) {
						link = link.trim();
						if ( link.isEmpty() || link.equalsIgnoreCase("NULL") )
							continue;
						TreeSet<String> expos = linkExposMap.get(link);
						if ( expos == null )
							expos = new TreeSet<String>();
						String expocode = null;
						try {
							expocode = DashboardServerUtils.checkExpocode(expoLinks[0]);
						} catch (Exception ex) {
							System.err.println("Invalid expocode given in links line: '" + dataline + "'");
							ex.printStackTrace();
							System.exit(1);
						}
						String newExpocode = renames.get(expocode);
						if ( newExpocode == null )
							expos.add(expocode);
						else
							expos.add(newExpocode);
						linkExposMap.put(link, expos);
					}
				}
				dataline = buffReader.readLine();
			}
		} finally {
			buffReader.close();
		}

		boolean success = true;

		// Get the default dashboard configuration
		DashboardDataStore dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard "
					+ "configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			MetadataFileHandler metaHandler = dataStore.getMetadataFileHandler();
			for ( String link : linkExposMap.keySet() ) {
				DashboardMetadata mdata = null;
				for ( String expocode : linkExposMap.get(link) ) {
					if ( mdata == null ) {
						// First expocode - read the link contents and save to file
						try {
							mdata = metaHandler.saveMetadataURL(expocode, "", link, allowOverwrite);
						} catch (IllegalArgumentException ex) {
							System.err.println(expocode + " - failed to save the link: " + link + " :: " + ex.getMessage());
							success = false;
						} catch (Exception ex) {
							System.err.println("Problems with link: " + link + " :: " + ex.getMessage());
							System.err.println("    used by expocodes: " + linkExposMap.get(link).toString());
							success = false;
							break;
						}
						System.err.println(expocode + " - saved link: " + link);
					}
					else {
						// Copy the already-downloaded metadata file
						try {
							metaHandler.copyMetadataFile(expocode, mdata, allowOverwrite);
						} catch (Exception ex) {
							System.err.println(expocode + " - failed to save the link: " + link + " :: " + ex.getMessage());
							success = false;
						}
						System.err.println(expocode + " - copied link file: " + link);
					}
				}
			}
		} finally {
			dataStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
