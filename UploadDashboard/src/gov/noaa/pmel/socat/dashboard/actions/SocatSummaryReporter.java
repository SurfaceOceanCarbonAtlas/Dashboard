/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Generates a summary of cruises in SOCAT
 * 
 * @author Karl Smith
 */
public class SocatSummaryReporter {

	private CruiseFileHandler cruiseHandler;
	private MetadataFileHandler metadataHandler;
	private DsgNcFileHandler dsgFileHandler;
	private DatabaseRequestHandler databaseHandler;

	/**
	 * Generate summary data using the handlers from a data store.
	 * @param configStore
	 * 		use handlers in the data store
	 */
	public SocatSummaryReporter(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getCruiseFileHandler();
		metadataHandler = configStore.getMetadataFileHandler();
		dsgFileHandler = configStore.getDsgNcFileHandler();
		databaseHandler = configStore.getDatabaseRequestHandler();
	}

	/**
	 * Generate the summary line for a cruise.  The line contains tab-separated 
	 * values in the order corresponding to the tab-separated titles in the 
	 * header line returned by {@link #getCruiseSummaryHeader()}.
	 * 
	 * @param expocode
	 * 		generate the summary for the cruise with this expocode
	 * @return
	 * 		the summary line for the cruise
	 * @throws IllegalArgumentException
	 * 		if there are problems generating the summary for the cruise
	 */
	public String getCruiseSummary(String expocode) throws IllegalArgumentException {
		String datasetName;
		String dsgQCFlag;
		String databaseQCFlag;
		String socatVersion;
		String oldExpocode;
		String regions;
		String numRows;
		String numErrRows;
		String numWarnRows;
		String pis;
		String addlDocs;

		DashboardCruise cruiseInfo = cruiseHandler.getCruiseFromInfoFile(expocode);
		if ( cruiseInfo == null )
			throw new IllegalArgumentException("No cruise data for " + expocode);
		String qcStatus = cruiseInfo.getQcStatus();
		if ( SocatQCEvent.QC_STATUS_NOT_SUBMITTED.equals(qcStatus) ||
			 SocatQCEvent.QC_STATUS_PREVIEW.equals(qcStatus) ) {
			// No official DSG file - get what we can from the dashboard cruise data and OME metadata
			numRows = Integer.toString(cruiseInfo.getNumDataRows());
			if ( cruiseInfo.getDataCheckStatus().isEmpty() ) {
				numErrRows = "-";
				numWarnRows = "-";
			}
			else {
				numErrRows = Integer.toString(cruiseInfo.getNumErrorRows());
				numWarnRows = Integer.toString(cruiseInfo.getNumWarnRows());
			}
			regions = "-";
			DashboardMetadata metadata = metadataHandler.getMetadataInfo(expocode, DashboardMetadata.OME_FILENAME);
			if ( metadata == null )
				throw new IllegalArgumentException("No OME metadata for " + expocode);
			DashboardOmeMetadata omeMeta = new DashboardOmeMetadata(metadata, metadataHandler);
			SocatMetadata socatMetadata = omeMeta.createSocatMetadata(null, null, null);
			datasetName = socatMetadata.getCruiseName();
			socatVersion = "-";
			pis = socatMetadata.getScienceGroup();
			addlDocs = socatMetadata.getAddlDocs();
			dsgQCFlag = "-";
			databaseQCFlag = "-";
			oldExpocode = "-";
		}
		else {
			CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(expocode);
			if ( ! dsgFile.exists() )
				throw new IllegalArgumentException("DSG file does not exist for " + expocode);
			try {
				dsgFile.read(false);
			} catch (IOException ex) {
				throw new IllegalArgumentException("Problems reading the metdata from the DSG file for " + 
						expocode + ": " + ex.getMessage());
			}
			ArrayList<SocatCruiseData> dataList = dsgFile.getDataList();
			numRows = Integer.toString(dataList.size());
			int numWoceBad = 0;
			int numWoceWarn = 0;
			TreeSet<String> regionNames = new TreeSet<String>();
			for ( SocatCruiseData data : dataList ) {
				Character woceCO2Water = data.getWoceCO2Water();
				if ( woceCO2Water.equals(SocatWoceEvent.WOCE_BAD) )
					numWoceBad++;
				else if ( woceCO2Water.equals(SocatWoceEvent.WOCE_QUESTIONABLE) )
					numWoceWarn++;
				regionNames.add(DataLocation.REGION_NAMES.get(data.getRegionID()));
			}
			numErrRows = Integer.toString(numWoceBad);
			numWarnRows = Integer.toString(numWoceWarn);
			regionNames.remove(DataLocation.REGION_NAMES.get(DataLocation.GLOBAL_REGION_ID));
			regions = "";
			for ( String name : regionNames )
				regions += "; " + name;
			regions = regions.substring(2);
			SocatMetadata socatMetadata = dsgFile.getMetadata();
			datasetName = socatMetadata.getCruiseName();
			socatVersion = socatMetadata.getSocatVersion();
			pis = socatMetadata.getScienceGroup();
			addlDocs = socatMetadata.getAddlDocs();
			dsgQCFlag = socatMetadata.getQcFlag();
			try {
				databaseQCFlag = databaseHandler.getQCFlag(expocode).toString();
			} catch (SQLException ex) {
				throw new IllegalArgumentException("Problems generating \"the\" database QC flag for " +
						expocode + ": " + ex.getMessage());
			}
			ArrayList<SocatQCEvent> qcEvents;
			try {
				qcEvents = databaseHandler.getQCEvents(expocode);
			} catch (SQLException ex) {
				throw new IllegalArgumentException("Problems reading database QC events for " +
						expocode + ": " + ex.getMessage());
			}
			oldExpocode = "-";
			for ( SocatQCEvent evt : qcEvents ) {
				if ( ! SocatQCEvent.QC_RENAMED_FLAG.equals(evt.getFlag()) )
					continue;
				String msg = evt.getComment();
				String[] msgWords = msg.split("\\s+");
				if ( ! ( (msgWords.length >= 5) &&
						 "Rename".equalsIgnoreCase(msgWords[0]) && 
						 "from".equalsIgnoreCase(msgWords[1]) && 
						 "to".equalsIgnoreCase(msgWords[3]) ) )
					throw new IllegalArgumentException("Unexpected comment for rename: " + msg);
				if ( expocode.equals(msgWords[4]) )
					oldExpocode = msgWords[2];
			}
		}
		// Add any supplemental documents found in the documents directory
		// (just to be safe) since this is what the reviewers will see
		ArrayList<DashboardMetadata> cruiseDocs = metadataHandler.getMetadataFiles(expocode);
		TreeSet<String> addlDocNames = new TreeSet<String>();
		for ( DashboardMetadata mdata : cruiseDocs )
			addlDocNames.add(mdata.getFilename());
		for ( String name : addlDocs.split(SocatMetadata.NAMES_SEPARATOR) )
			addlDocNames.add(name);
		addlDocNames.remove(DashboardMetadata.OME_FILENAME);
		addlDocs = "";
		for ( String name : addlDocNames ) {
			addlDocs += "; " + name;
		}
		addlDocs = addlDocs.substring(2);

		// Clean up the listing of PIs
		String[] pisArray = pis.split(SocatMetadata.NAMES_SEPARATOR);
		pis = "";
		for ( String name : pisArray ) {
			pis += "; " + name;
		}
		pis = pis.substring(2);

		return expocode + "\t" + 
			   datasetName + "\t" +
			   dsgQCFlag + "\t" + 
			   databaseQCFlag + "\t" +
			   socatVersion + "\t" +
			   oldExpocode + "\t" +
			   regions + "\t" +
			   numRows + "\t" +
			   numErrRows + "\t" +
			   numWarnRows + "\t" + 
			   pis + "\t" +
			   addlDocs;
	}

	private static final String CRUISE_SUMMARY_HEADER = 
			"Expocode\t" + 
			"Dataset Name\t" + 
			"QC (DSG)\t" + 
			"QC (Database)\t" + 
			"Socat Version\t" +
			"Renamed From\t" + 
			"Regions\t" + 
			"Num Data Pts\t" + 
			"Num Err Pts\t" + 
			"Num Warn Pts\t" +
			"PIs\t" +
			"Addl Docs";

	/**
	 * @return
	 * 		the header for the cruise summary lines
	 */
	public String getCruiseSummaryHeader() {
		return CRUISE_SUMMARY_HEADER;
	}

}
