/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseChecker;
import gov.noaa.pmel.socat.dashboard.actions.CruiseSubmitter;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Rechecks cruises, and resubmits cruises that had been submitted.  Uses 
 * the default dashboard configuration.  If a submitted cruise does not 
 * have a DSG file, 'N' (new) will be assigned as the QC status; otherwise,
 * 'U' (updated) will be assigned.  Requests to "send to CDIAC immediately" 
 * are not re-sent.  The default dashboard configuration is used for this 
 * recheck and resubmit process.
 * 
 * @author Karl Smith
 */
public class ResubmitCruises {

	CruiseFileHandler cruiseHandler;
	CruiseChecker cruiseChecker;
	CruiseSubmitter cruiseSubmitter;
	DsgNcFileHandler dsgHandler;
	DatabaseRequestHandler databaseHandler;
	String socatVersion;

	/**
	 * @param configStore
	 * 		create using the CruiseFileHandler, CruiseChecker,
	 * 		and CruiseSubmitter given in this DashboardConfigStore
	 */
	public ResubmitCruises(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getCruiseFileHandler();
		cruiseChecker = configStore.getDashboardCruiseChecker();
		cruiseSubmitter = configStore.getDashboardCruiseSubmitter();
		dsgHandler = configStore.getDsgNcFileHandler();
		databaseHandler = configStore.getDatabaseRequestHandler();
		socatVersion = configStore.getSocatUploadVersion();
	}

	/**
	 * Rechecks the data of the given cruise.  If the cruise had been submitted 
	 * at some point, it is resubmitted.  If a submitted cruise does not have a 
	 * DSG file, 'N' (new) will be assigned as the QC flag; otherwise 'U' (updated) 
	 * will be assigned.  Requests to "send to CDIAC immediately" are not re-sent.
	 * 
	 * @param expocode
	 * 		expocode of the cruise to check/resubmit
	 * @param username
	 * 		user performing this submit
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * 		if problems access the cruise information or data file,
	 * 		if problems updating the cruise information file,
	 * 		if problems submitting the cruise for QC
	 */
	public void resubmitCruise(String expocode, String username) 
											throws IllegalArgumentException {
		// Get the information for this cruise
		DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
		String qcStatus = cruise.getQcStatus();

		if ( qcStatus.equals(SocatQCEvent.QC_STATUS_NOT_SUBMITTED) ) {
			// Only check (do not submit) if the cruise in not submitted
			DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);
			// If the DSG file exists, this is an unsubmitted update, so mark as suspended
			if ( dsgHandler.getDsgNcFile(expocode).exists() ) {
				cruiseData.setQcStatus(SocatQCEvent.QC_STATUS_SUSPENDED);

				SocatQCEvent qcEvent = new SocatQCEvent();
				qcEvent.setExpocode(expocode);
				qcEvent.setFlag(SocatQCEvent.QC_SUSPEND_FLAG);
				qcEvent.setFlagDate(new Date());
				qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
				qcEvent.setSocatVersion(socatVersion);
				qcEvent.setUsername(username);
				qcEvent.setComment("Suspending cruise for pending update");
				try {
					databaseHandler.addQCEvent(qcEvent);
					// This is a global 'S' flag, so this is the flag for the DSG file
					dsgHandler.updateQCFlag(qcEvent);
				} catch (Exception ex) {
					throw new IllegalArgumentException("Unexpected error " +
							"suspending the cruise " + expocode);
				}
			}
			cruiseChecker.checkCruise(cruiseData);
			cruiseHandler.saveCruiseInfoToFile(cruiseData, 
					"Cruise data column types, units, and missing values for " + 
					cruiseData.getExpocode() + " updated by " + username);
		}
		else {
			// Un-submit the cruise but do not bother committing the change at this time
			if ( dsgHandler.getDsgNcFile(expocode).exists() )
				cruise.setQcStatus(SocatQCEvent.QC_STATUS_SUSPENDED);
			else
				cruise.setQcStatus(SocatQCEvent.QC_STATUS_NOT_SUBMITTED);
			cruiseHandler.saveCruiseInfoToFile(cruise, null);
			// Submit the cruise for QC
			HashSet<String> expocodeSet = new HashSet<String>(Arrays.asList(expocode));
			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm Z").format(new Date());
			cruiseSubmitter.submitCruises(expocodeSet, cruise.getArchiveStatus(), 
										  timestamp, false, username);
			// The cruise will now have a QC status of 'N' (new) if it was 
			// QC_STATUS_NOT_SUBMITTED, or 'U' (updated) if it was QC_STATUS_SUSPENDED
		}
	}

	/**
	 * @param args
	 * 		Username - name of the dashboard admin user requesting this update.
	 * 		ExpocodesFile - file of expocodes to recheck/resubmit; if not given,
	 * 		                all cruises are rechecked/resubmitted.
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  ExpocodesFile  Username");
			System.err.println();
			System.err.println("Rechecks cruises specified in ExpocodesFile.  Resubmits all cruises ");
			System.err.println("that had been submitted.  If a submitted cruise does not have a DSG ");
			System.err.println("file, 'N' (new) will be assigned as the QC status; otherwise, 'U' ");
			System.err.println("(updated) will be assigned.  Requests to \"send to CDIAC immediately\" ");
			System.err.println("are not re-sent.  The default dashboard configuration is used for this ");
			System.err.println("recheck and resubmit process. ");
			System.err.println();
			System.err.println("Username is the dashboard admin requesting this update.");
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];
		String username = args[1];

		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			if ( ! configStore.isAdmin(username) ) {
				System.err.println(username + " is not an admin for the dashboard");
				System.exit(1);				
			}
			ResubmitCruises resubmitter = new ResubmitCruises(configStore);

			// Get the expocode of the cruises to resubmit
			TreeSet<String> allExpocodes = null; 
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

			// Recheck, and possibly resubmit, each of these cruises
			for ( String expocode : allExpocodes ) {
				try {
					resubmitter.resubmitCruise(expocode, username);
				} catch (Exception ex) {
					System.err.println("Error updating " + expocode + " : " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
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
