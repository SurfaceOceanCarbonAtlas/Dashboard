/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Updates the QC flags in the full-data and decimated-data DSG files for cruises
 * to the flag obtained from the database.
 * 
 * @author Karl Smith
 */
public class UpdateQCFlags {

	/**
	 * @param args
	 * 		ExpocodesFile - update dashboard status of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Updates the QC flags in the full-data and decimated-data DSG ");
			System.err.println("files for cruises specified in ExpocodesFile to the flag obtained ");
			System.err.println("from the database.  The default dashboard configuration is ");
			System.err.println("used for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

		final String username = "karl.smith";

		SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date oldQCTime = null;
		try {
			oldQCTime = dateParser.parse("2013-12-31 12:00:00");
		} catch (ParseException ex) {
			System.err.println("Unexpected error parsing 2013-12-31 12:00:00");
			ex.printStackTrace();
			System.exit(1);
		}

		boolean success = true;
		boolean updated = false;

		// Get the default dashboard configuration
		DashboardDataStore dataStore = null;		
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			// Get the expocode of the cruises to update
			TreeSet<String> allExpocodes = new TreeSet<String>();
			try {
				BufferedReader expoReader = 
						new BufferedReader(new FileReader(expocodesFilename));
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

			DatabaseRequestHandler dbHandler = dataStore.getDatabaseRequestHandler();
			DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				Character qcFlag;
				try {
					qcFlag = dbHandler.getQCFlag(expocode);
				} catch (Exception ex) {
					System.err.println("Error getting the database QC flag for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				char oldFlag;
				try {
					oldFlag = dsgHandler.getQCFlag(expocode);
				} catch (Exception ex) {
					System.err.println("Error reading the current DSG QC flag for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				try {
					if ( ! qcFlag.equals(oldFlag) ) {
						CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
						dsgFile.read(true);
						String version = dsgFile.getMetadata().getSocatVersion();
						if ( "1.3".equals(version)  || "1.4".equals(version)  || "2.0".equals(version) || 
							 "1.30".equals(version) || "1.40".equals(version) || "2.00".equals(version) ) {
							// Add an old global QC flag with the flag in the DSG file
							SocatQCEvent qcEvent = new SocatQCEvent();
							qcEvent.setExpocode(expocode);
							qcEvent.setFlag(oldFlag);
							qcEvent.setFlagDate(oldQCTime);
							qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
							qcEvent.setSocatVersion("2.0");
							qcEvent.setUsername(username);
							qcEvent.setComment("Adding global QC flag to that assigned in v2 to fix unresolved conflicts");
							try {
								dbHandler.addQCEvent(qcEvent);
								System.err.println("Add old global QC flag of '" + oldFlag + 
										"' to fix unresolved conflicts in " + expocode);
							} catch (Exception ex) {
								System.err.println("Failed to add old global QC flag of '" + oldFlag + 
										"' to fix unresolved conflicts for " + expocode);
								success = false;
							}
						}
						else if ( "3.0".equals(version) || "3.00".equals(version) ) {
							// Update the QC flag in the DSG files
							dsgHandler.getDsgNcFile(expocode).updateQCFlag(qcFlag);
							dsgHandler.getDecDsgNcFile(expocode).updateQCFlag(qcFlag);
							System.err.println("Updated QC flag for " + 
									expocode + " from '" + oldFlag + "' to '" + qcFlag + "'");
							updated = true;
						}
						else {
							System.err.println("Unknown SOCAT version number \"" + 
									String.valueOf(version) + "\" for " + expocode);
							success = false;
						}
					}
				} catch (Exception ex) {
					System.err.println("Error updating the QC flag in the DSG files for " + 
							expocode + " : " + ex.getMessage());
					success = false;
				}
			}
			if ( updated ) {
				dsgHandler.flagErddap(true, true);
			}
		} finally {
			dataStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
