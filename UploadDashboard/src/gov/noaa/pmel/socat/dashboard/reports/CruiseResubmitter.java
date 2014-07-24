/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.reports;

import gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardCruiseSubmitter;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import uk.ac.uea.socat.sanitychecker.Output;

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
	 * at some point, it is resubmitted and the QC status is set to 'U'
	 * (updated).  Requests to "send to CDIAC immediately" are not re-sent.
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
			Output output = dataStore.getDashboardCruiseChecker().checkCruise(cruiseData);
			cruiseHandler.saveCruiseMessages(cruiseData.getExpocode(), output);
			cruiseHandler.saveCruiseInfoToFile(cruiseData, 
					"Cruise data column types, units, and missing values for " + 
					cruiseData.getExpocode() + " updated by " + username);
		}
		else {
			// Suspend the cruise but do not bother committing the change
			cruise.setQcStatus(SocatQCEvent.QC_STATUS_SUSPENDED);
			cruiseHandler.saveCruiseInfoToFile(cruise, null);
			// Submit the cruise for QC
			DashboardCruiseSubmitter submitter = new DashboardCruiseSubmitter(dataStore);
			HashSet<String> expocodeSet = new HashSet<String>(Arrays.asList(expocode));
			String timestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date());
			submitter.submitCruises(expocodeSet, cruise.getArchiveStatus(), 
									timestamp, false, username, null, null);
			// Note that the cruise will now have a QC status of 'U' (updated)
		}
	}

	/**
	 * Rechecks all cruises, and resubmits all cruises that had been submitted, 
	 * in the default dashboard configuration.  Resubmitted cruises will have a
	 * QC status of 'U' (updated).
	 * 
	 * @param args
	 * 		Username - name of the dashboard (admin) user requesting this update.
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  Username");
			System.err.println();
			System.err.println("Rechecks all cruises, and resubmits all cruises that had been submitted, ");
			System.err.println("in the default dashboard configuration.  Resubmitted cruises will have a ");
			System.err.println("QC status of 'U' (updated). ");
			System.err.println();
			System.err.println("Username is the name of the dashboard (admin) user requesting this update.");
			System.err.println();
			System.exit(1);
		}

		String username = args[0];
		// Get the expocodes of all cruises in the default dashboard
		DashboardDataStore dataStore = null;		
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		TreeSet<String> allExpocodes = null; 
		try {
			allExpocodes = new TreeSet<String>(
					dataStore.getCruiseFileHandler().getMatchingExpocodes("*"));
		} catch (Exception ex) {
			System.err.println("Error getting all expocodes: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		// Resubmit each of these cruises that had been submitted
		boolean success = true;
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
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
