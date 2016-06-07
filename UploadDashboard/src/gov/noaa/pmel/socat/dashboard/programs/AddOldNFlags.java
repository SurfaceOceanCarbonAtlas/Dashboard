/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

/**
 * Add missing global N flags with an appropriate date to old (v1.x) datasets.
 * Updates the SOCAT version in the DSG files where this has caused a change.
 * 
 * @author Karl Smith
 */
public class AddOldNFlags {

	/**
	 * @param args
	 * 		ExpocodesFile - add old global N flags where missing to the datasets in this list.
	 * 				Update the SOCAT version in the DSG files where this has caused a change.
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Adds old global N flags where missing to the database.  Updates ");
			System.err.println("the SOCAT version in the full-data and decimated-data DSG files ");
			System.err.println("where this has caused a change. ");
			System.err.println("The default dashboard configuration is used for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

		boolean success = true;
		boolean updated = false;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			// Get the expocode of the datasets to update
			TreeSet<String> allExpocodes = new TreeSet<String>();
			try {
				BufferedReader expoReader = 
						new BufferedReader(new FileReader(expocodesFilename));
				try {
					String dataline = expoReader.readLine();
					while ( dataline != null ) {
						dataline = dataline.trim();
						if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) ) {
							try {
								String expocode = DashboardServerUtils.checkExpocode(dataline);
								allExpocodes.add(expocode);
							} catch (Exception ex) {
								System.err.println("Invalid expocode '" + dataline + "': " + ex.getMessage());
								ex.printStackTrace();
								System.exit(1);
							}
						}
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

			DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();
			DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

			// check and possibly update each of these datasets
			for ( String expocode : allExpocodes ) {
				ArrayList<SocatQCEvent> qcEvents;
				try {
					qcEvents = dbHandler.getQCEvents(expocode);
				} catch (Exception ex) {
					System.err.println("Error getting the database QC events for " + 
							expocode + ": " + ex.getMessage());
					success = false;
					continue;
				}

				// Check for v1.3 and v1.4 QC flags
				boolean hasV13NU = false;
				boolean hasV13QC = false;
				boolean hasV14NU = false;
				boolean hasV14QC = false;
				SocatQCEvent earliestQC = null;
				for ( SocatQCEvent evnt : qcEvents ) {
					// Ordered by qc_time descending, so earliest is last
					earliestQC = evnt;
					String socatVersion = evnt.getSocatVersion();
					if ( socatVersion.equals("1.3") ) {
						hasV13QC = true;
						Character flag = evnt.getFlag();
						Character regionID = evnt.getRegionID();
						if ( ( flag.equals(SocatQCEvent.QC_NEW_FLAG) || flag.equals(SocatQCEvent.QC_UPDATED_FLAG) ) 
								&& regionID.equals(DataLocation.GLOBAL_REGION_ID) ) {
							hasV13NU = true;
						}
					}
					else if ( socatVersion.equals("1.4") ){
						hasV14QC = true;
						Character flag = evnt.getFlag();
						Character regionID = evnt.getRegionID();
						if ( ( flag.equals(SocatQCEvent.QC_NEW_FLAG) || flag.equals(SocatQCEvent.QC_UPDATED_FLAG) ) 
								&& regionID.equals(DataLocation.GLOBAL_REGION_ID) ) {
							hasV14NU = true;
						}
					}
				}

				boolean changed = false;
				if ( hasV13QC ) {
					if ( ! hasV13NU ) {
						// Add a old global v1.3 N flag
						SocatQCEvent qcEvent = new SocatQCEvent();
						qcEvent.setExpocode(expocode);
						qcEvent.setFlag(SocatQCEvent.QC_NEW_FLAG);
						qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
						qcEvent.setSocatVersion("1.3");
						qcEvent.setUsername(SocatEvent.SANITY_CHECKER_USERNAME);
						qcEvent.setRealname(SocatEvent.SANITY_CHECKER_REALNAME);
						Date oldDate = earliestQC.getFlagDate();
						if ( oldDate.equals(SocatMetadata.DATE_MISSING_VALUE) ) {
							System.err.println("No date for oldest QC flag of " + expocode);
							success = false;
							continue;
						}
						// Set the QC time to be a minute before the oldest QC flag
						qcEvent.setFlagDate(new Date(oldDate.getTime() - 60 * 1000));
						qcEvent.setComment("Added old global N flag to v1.3 cruise with only regional QC flags");
						try {
							dbHandler.addQCEvent(qcEvent);
						} catch (Exception ex) {
							System.err.println("Unable to add an old global N flag for " + 
									expocode + ": " + ex.getMessage());
							success = false;
							continue;
						}
						changed = true;
					}
				}
				else if ( hasV14QC && ! hasV14NU ) {
					// Add a old global v1.4 N flags - only if v1.4 QC but no v1.3 QC flags
					SocatQCEvent qcEvent = new SocatQCEvent();
					qcEvent.setExpocode(expocode);
					qcEvent.setFlag(SocatQCEvent.QC_NEW_FLAG);
					qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
					qcEvent.setSocatVersion("1.4");
					qcEvent.setUsername(SocatEvent.SANITY_CHECKER_USERNAME);
					qcEvent.setRealname(SocatEvent.SANITY_CHECKER_REALNAME);
					Date oldDate = earliestQC.getFlagDate();
					if ( oldDate.equals(SocatMetadata.DATE_MISSING_VALUE) ) {
						System.err.println("No date for oldest QC flag of " + expocode);
						success = false;
						continue;
					}
					// Set the QC time to be a minute before the oldest QC flag
					qcEvent.setFlagDate(new Date(oldDate.getTime() - 60 * 1000));
					qcEvent.setComment("Added old global N flag to v1.4 cruise with only regional QC flags");
					try {
						dbHandler.addQCEvent(qcEvent);
					} catch (Exception ex) {
						System.err.println("Unable to add an old global N flag for " + 
								expocode + ": " + ex.getMessage());
						success = false;
						continue;
					}
					changed = true;
				}

				if ( changed ) {
					String dbVersionStatus;
					try {
						dbVersionStatus = dbHandler.getSocatVersionStatus(expocode);
						if ( dbVersionStatus.isEmpty() )
							throw new IllegalArgumentException("Unexpected missing global N/U QC flags");
					} catch (Exception ex) {
						System.err.println("Problems getting the SOCAT version status from the database for " + 
								expocode + ": " + ex.getMessage());
						success = false;
						continue;
					}
					CruiseDsgNcFile fullDataDsg = dsgHandler.getDsgNcFile(expocode);
					try {
						ArrayList<String> missing = fullDataDsg.readMetadata();
						if ( ! missing.isEmpty() ) 
							throw new IllegalArgumentException("unassigned fields: " + missing.toString());
					} catch (Exception ex) {
						System.err.println("Problems getting the SOCAT version status from the full-data DSG file for " + 
								expocode + ": " + ex.getMessage());
						success = false;
						continue;
					}
					SocatMetadata mdata = fullDataDsg.getMetadata();
					String dsgVersionStatus = mdata.getSocatVersion();
					if ( ! dsgVersionStatus.equals(dbVersionStatus) ) {
						try {
							fullDataDsg.updateStringVarValue(CruiseDsgNcFile.SOCAT_VERSION_NCVAR_NAME, dbVersionStatus);
						} catch (Exception ex) {
							System.err.println("Problems updating the SOCAT version status in the full-data DSG file for " + 
									expocode + ": " + ex.getMessage());
							success = false;
							continue;
						}
						CruiseDsgNcFile decDataDsg = dsgHandler.getDecDsgNcFile(expocode);
						try {
							decDataDsg.updateStringVarValue(CruiseDsgNcFile.SOCAT_VERSION_NCVAR_NAME, dbVersionStatus);
						} catch (Exception ex) {
							System.err.println("Problems updating the SOCAT version status in the decimated-data DSG file for " + 
									expocode + ": " + ex.getMessage());
							success = false;
							continue;
						}
						updated = true;
					}
				}
			}
			if ( updated ) {
				dsgHandler.flagErddap(true, true);
			}
		} finally {
			DashboardConfigStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
