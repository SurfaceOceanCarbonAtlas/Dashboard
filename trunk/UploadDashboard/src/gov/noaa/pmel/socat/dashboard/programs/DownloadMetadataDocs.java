/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
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
 * Expocodes without links or NULL for the link are ignored.  Expocodes that have been 
 * updated in the current version of SOCAT are ignored.  Metadata files that already exist
 * are not overwritten.
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if ( args.length != 5 ) {
			System.err.println();
			System.err.println("arguments:  ExpocodeLinksFile  RenamedExpocodesFile  MetadataFilesDir  CruiseDataFilesDir  SVNUsername");
			System.err.println("where:");
			System.err.println("    ExpocodesLinksFile - list of expocode, tab, and metadata ");
			System.err.println("        file links; multiple links separated by space-semicolon-space");
			System.err.println("    RenamedExpocodesdFile - list of renamed expocodes in the form: ");
			System.err.println("        \"Rename from <old_expocode> to <new_expocode>\"");
			System.err.println("    MetadataFilesDir - version-controlled metadata documents directory");
			System.err.println("    CruiseDataFilesDir - version-controlled cruise data documents directory");
			System.err.println("    SVNUsername - username for version control");
			System.err.println();
			System.err.println("Downloads the metadata files at given links and associates with the ");
			System.err.println("corresponding cruises.  Expocodes without links or NULL for the link ");
			System.err.println("are ignored.  Expocodes that have been updated in version \"3.0\" ");
			System.err.println("of SOCAT are ignored.  Metadata files that already exist are not overwritten.");
			System.err.println();
			System.exit(1);
		}

		String expoLinksFilename = args[0];
		String renamesFilename = args[1];
		String metadataDirname = args[2];
		String cruiseDataDirname = args[3];
		String svnUsername = args[4];
		String svnPassword = "";

		// Read the expocodes and their links
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new FileReader(expoLinksFilename));
		} catch (Exception ex) {
			System.err.println("Problems opening the expocode and links file for reading:");
			ex.printStackTrace();
			System.exit(1);
		}

		// Map of link to set of expocode using that link
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
						expos.add(expocode);
						linkExposMap.put(link, expos);
					}
				}
				dataline = buffReader.readLine();
			}
		} finally {
			buffReader.close();
		}

		// Read the expocode renames
		try {
			buffReader = new BufferedReader(new FileReader(renamesFilename));
		} catch (Exception ex) {
			System.err.println("Problems opening the renames file for reading:");
			ex.printStackTrace();
			System.exit(1);
		}

		// Map of old to new expocodes
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

		CruiseFileHandler cruiseHandler = null;
		try {
			cruiseHandler = new CruiseFileHandler(cruiseDataDirname, svnUsername, svnPassword);
		} catch (Exception ex) {
			System.err.println("Problems with the cruise data documents directory");
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			// Correct the list of expocodes by renaming and removing updated cruises
			TreeMap<String,TreeSet<String>> linkNewExposMap = new TreeMap<String,TreeSet<String>>();
			for ( String link : linkExposMap.keySet() ) {
				TreeSet<String> newExpoSet = new TreeSet<String>();
				for ( String expo : linkExposMap.get(link) ) {
					String newExpo = renames.get(expo);
					if ( newExpo == null )
						newExpo = expo;
					try {
						DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(newExpo);
						if ( cruise == null )
							throw new IllegalArgumentException("info file does not exist");
						if ( ! cruise.getVersion().equals("3.0") ) {
							newExpoSet.add(newExpo);
						}
					} catch (Exception ex) {
						System.err.println("Problems reading the info file for cruise " + newExpo);
						ex.printStackTrace();
						System.exit(1);
					}
				}
				if ( newExpoSet.size() > 0 ) {
					linkNewExposMap.put(link, newExpoSet);
				}
			}
			linkExposMap = linkNewExposMap;
		} finally {
			cruiseHandler.shutdown();
		}

		boolean success = true;

		MetadataFileHandler metaHandler = null;
		try {
			metaHandler = new MetadataFileHandler(metadataDirname, svnUsername, svnPassword);
		} catch (Exception ex) {
			System.err.println("Problems with the metadata documents directory");
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			for ( String link : linkExposMap.keySet() ) {
				try {
					DashboardMetadata mdata = null;
					for ( String expocode : linkExposMap.get(link) ) {
						if ( mdata == null ) {
							// First expocode - read the link contents and save to file
							mdata = metaHandler.saveMetadataURL(expocode, "", link, true);
							System.err.println(expocode + " - read " + link);
						}
						else {
							// Copy the already-downloaded metadata file
							metaHandler.copyMetadataFile(expocode, mdata);
							System.err.println(expocode + " - copied previous " + link);
						}
					}
				} catch (Exception ex) {
					System.err.println("failed link: " + link + " :: " + ex.getMessage());
					success = false;
				}
			}
		} finally {
			metaHandler.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
