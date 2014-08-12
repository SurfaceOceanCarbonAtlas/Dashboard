/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * For resubmitting cruises automatically to update DSG files 
 * and SanityChecker messages after changes are made.  Cruises
 * that had never been submitted are only rechecked by the 
 * SanityChecker.
 * 
 * @author Karl Smith
 */
public class CruiseResubmitter {

	/**
	 * Rechecks the data of all cruises.  If a cruise had been submitted 
	 * at some point, it is resubmitted and the QC status is set to 'N'
	 * (new).  Requests to "send to CDIAC immediately" are not re-sent.
	 * 
	 * @param expocode
	 * 		expocode of the cruise to check/resubmit
	 * @param username
	 * 		user performing this submit
	 * @param dataStore
	 * 		data configuration for the dashboard
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * 		if problems access the cruise information or data file,
	 * 		if problems updating the cruise information file,
	 * 		if problems submitting the cruise for QC
	 */
	public static void resubmitCruise(String expocode, String username,
			DashboardDataStore dataStore) throws IllegalArgumentException {
		// Get the information for this cruise
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
		String qcStatus = cruise.getQcStatus();
		if ( qcStatus.equals(SocatQCEvent.QC_STATUS_NOT_SUBMITTED) ) {
			// Only check (do not submit) if the cruise has never been submitted
			DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);
			dataStore.getDashboardCruiseChecker().checkCruise(cruiseData);
			cruiseHandler.saveCruiseInfoToFile(cruiseData, 
					"Cruise data column types, units, and missing values for " + 
					cruiseData.getExpocode() + " updated by " + username);
		}
		else {
			// Un-submit the cruise but do not bother committing the change
			cruise.setQcStatus(SocatQCEvent.QC_STATUS_NOT_SUBMITTED);
			cruiseHandler.saveCruiseInfoToFile(cruise, null);
			// Submit the cruise for QC
			HashSet<String> expocodeSet = new HashSet<String>(Arrays.asList(expocode));
			String timestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date());
			dataStore.getDashboardCruiseSubmitter()
					 .submitCruises(expocodeSet, cruise.getArchiveStatus(), 
									timestamp, false, username, null, null);
			// Note that the cruise will now have a QC status of 'N' (new)
		}
	}

	/**
	 * Rechecks all cruises, and resubmits all cruises that had been submitted, 
	 * in the default dashboard configuration.  Resubmitted cruises will have a
	 * QC status of 'N' (new).
	 * 
	 * @param args
	 * 		Username - name of the dashboard (admin) user requesting this update.
	 */
	public static void main(String[] args) {
		if ( (args.length < 1) || (args.length > 2) ) {
			System.err.println("Arguments:  Username  [ ExpocodesFile ]");
			System.err.println();
			System.err.println("Rechecks all cruises, or those specified in ExpocodesFile if given. ");
			System.err.println("Resubmits all cruises that had been submitted.  Resubmitted cruises ");
			System.err.println("will have a QC status of 'N' (new).  The default dashboard ");
			System.err.println("configuration is used for this recheck and resubmit process. ");
			System.err.println();
			System.err.println("Username is the dashboard admin requesting this update.");
			System.err.println();
			System.exit(1);
		}

		String username = args[0];
		String expocodesFilename;
		if ( args.length > 1 )
			expocodesFilename = args[1];
		else
			expocodesFilename = null;

		boolean success = true;

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
			// Get the expocode of the cruises to resubmit
			TreeSet<String> allExpocodes = null; 
			if ( expocodesFilename != null ) {
				allExpocodes = new TreeSet<String>();
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
			} 
			else {
				try {
					allExpocodes = new TreeSet<String>(
							dataStore.getCruiseFileHandler().getMatchingExpocodes("*"));
				} catch (Exception ex) {
					System.err.println("Error getting all expocodes: " + ex.getMessage());
					ex.printStackTrace();
					System.exit(1);
				}
			}

			// Recheck, and possibly resubmit, each of these cruises
			for ( String expocode : allExpocodes ) {
				try {
					resubmitCruise(expocode, username, dataStore);
				} catch (Exception ex) {
					System.err.println("Error updating " + expocode + " : " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
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
