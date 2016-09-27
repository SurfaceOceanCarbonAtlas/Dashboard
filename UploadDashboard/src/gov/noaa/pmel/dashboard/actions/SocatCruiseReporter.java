/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.CruiseDsgNcFile;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.SocatCruiseData;
import gov.noaa.pmel.dashboard.server.SocatMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.QCEvent;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Karl Smith
 */
public class SocatCruiseReporter {

	private static final String NOT_AVAILABLE_TAG = "N/A";
	private static final String SOCAT_ENHANCED_DOI_TAG = "SOCATENHANCEDDOI";
	private static final String SOCAT_ENHANCED_HREF_PREFIX = "http://doi.pangaea.de/";

	// SOCAT main DOI, DOI HRef, and publication citation
	private static final String SOCAT_MAIN_DOI = "SOCATMAINDOI";
	private static final String[] SOCAT_MAIN_CITATION = {
		"  SOCATMAINHREF",
		"Also see: ",
		"  D.C.E. Bakker, B. Pfeil, C.S. Landa, et. al.  \"A multi-decade record ",
		"of high quality fCO2 data in version 3 of the Surface Ocean CO2 Atlas ",
		"(SOCAT)\"  Under review in Earth System Science Data Discussions, 2016, ",
		"55 pp.  doi:10.5194/essd-2016-15; ",
		"  D.C.E. Bakker, B. Pfeil, K. Smith, et. al.  \"An update to the Surface ",
		"Ocean CO2 Atlas (SOCAT version 2)\" Earth Syst. Sci. Data, 6, 69-90, 2014 ",
		"doi:10.5194/essd-6-69-2014  http://www.earth-syst-sci-data.net/6/69/2014/ ",
	};

    // Expocodes for cruises missing hours and minutes (hour:minute time is 00:00)
	private static final TreeSet<String> DAY_RESOLUTION_CRUISE_EXPOCODES = 
			new TreeSet<String>(Arrays.asList(new String[] {
					"06AQ19911114",
					"06AQ19911210",
					"06MT19920510",
					"06MT19970106",
					"06P119910616",
					"06P119950901",
					"316N19971005",
			}));

	private static final SimpleDateFormat TIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
	private static final SimpleDateFormat DATETIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/** Jan 1, 1940 - reasonable lower limit on data dates */
	private static final Date EARLIEST_DATE;
	static {
		TimeZone utc = TimeZone.getTimeZone("UTC");
		TIMESTAMPER.setTimeZone(utc);
		DATETIMESTAMPER.setTimeZone(utc);
		try {
			EARLIEST_DATE = DATETIMESTAMPER.parse("1940-01-01 00:00:00");
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Class for collecting and sorting time/lat/lon/fco2rec data
	 */
	private static class DataPoint implements Comparable<DataPoint> {
		final Date datetime;
		final Double latitude;
		final Double longitude;
		final Double fco2rec;

		/**
		 * @param expocode
		 * 		dataset expocode, only used for error message when raising exceptions
		 * @param sectime
		 * 		measurement time in seconds since Jan 1, 1970 00:00:00
		 * @param latitude
		 * 		measurement latitude in decimal degrees north
		 * @param longitude
		 * 		measurment longitude in decimal degrees east in the range [-180,180]
		 * @param fco2rec
		 * 		measurement recommended fCO2
		 * @throws IllegalArgumentException
		 * 		if the sectime, latitude, longitude, or fco2rec values are invalid
		 */
		DataPoint(String expocode, Double sectime, Double latitude, 
				Double longitude, Double fco2rec) throws IllegalArgumentException {
			if ( sectime == null )
				throw new IllegalArgumentException("null time for " + expocode);
			this.datetime = new Date(Math.round(sectime * 1000.0));
			if ( this.datetime.before(EARLIEST_DATE) || this.datetime.after(new Date()) )
				throw new IllegalArgumentException("invalid time of " + 
						this.datetime.toString() + " for " + expocode);

			if ( latitude == null )
				throw new IllegalArgumentException("null latitude for " + expocode);
			if ( (latitude < -90.0) || (latitude > 90.0) )
				throw new IllegalArgumentException("invalid latitude of " + 
						Double.toString(latitude) + " for " + expocode);
			this.latitude = latitude;

			if ( longitude == null )
				throw new IllegalArgumentException("null longitude for " + expocode);
			if ( (longitude < -180.0) || (longitude > 180.0) )
				throw new IllegalArgumentException("invalid longitude of " + 
						Double.toString(longitude) + " for " + expocode);
			this.longitude = longitude;

			if ( fco2rec == null )
				throw new IllegalArgumentException("null fco2rec for " + expocode);
			if ( ( ! DashboardUtils.FP_MISSING_VALUE.equals(fco2rec) ) && 
				 ( (fco2rec < 0.0) || (fco2rec > 100000.0) ) )
				throw new IllegalArgumentException("invalid fCO2rec of " + 
						Double.toString(fco2rec) + " for " + expocode);
			this.fco2rec = fco2rec;
		}

		@Override
		public int compareTo(DataPoint other) {
			// the primary sort must be on datetime
			int result = this.datetime.compareTo(other.datetime);
			if ( result != 0 )
				return result;
			result = this.latitude.compareTo(other.latitude);
			if ( result != 0 )
				return result;
			result = this.longitude.compareTo(other.longitude);
			if ( result != 0 )
				return result;
			result = this.fco2rec.compareTo(other.fco2rec);
			if ( result != 0 )
				return result;
			return 0;
		}

		@Override
		public int hashCode() {
			final int prime = 37;
			int result = 1;
			result = prime * result + datetime.hashCode();
			result = prime * result + latitude.hashCode();
			result = prime * result + longitude.hashCode();
			result = prime * result + fco2rec.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) 
				return true;
			if ( obj == null ) 
				return false;
			if ( ! (obj instanceof DataPoint) )
				return false;
			DataPoint other = (DataPoint) obj;
			if ( ! datetime.equals(other.datetime) )
				return false;
			if ( ! latitude.equals(other.latitude) ) 
				return false;
			if ( ! longitude.equals(other.longitude) ) 
				return false;
			if ( ! fco2rec.equals(other.fco2rec) ) 
				return false;
			return true;
		}

		@Override
		public String toString() {
			return  "[ datetime=" + DATETIMESTAMPER.format(datetime) + 
					", latitude=" + String.format("%#.6f", latitude) + 
					", longitude=" + String.format("%#.6f", longitude) + 
					", fco2rec=" + String.format("%#.6f", fco2rec) + 
					" ]";
		}

	}

	private CruiseFileHandler cruiseHandler;
	private MetadataFileHandler metadataHandler;
	private DsgNcFileHandler dsgFileHandler;
	private KnownDataTypes knownMetadataTypes;
	private KnownDataTypes knownDataFileTypes;
	private DatabaseRequestHandler databaseHandler;

	/**
	 * For generating cruise reports from the data provided 
	 * by the given dashboard configuration. 
	 * DsgNcFileHandler.
	 * 
	 * @param configStore
	 * 		dashboard configuration to use
	 */
	public SocatCruiseReporter(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getCruiseFileHandler();
		metadataHandler = configStore.getMetadataFileHandler();
		dsgFileHandler = configStore.getDsgNcFileHandler();
		knownMetadataTypes = configStore.getKnownMetadataTypes();
		knownDataFileTypes = configStore.getKnownDataFileTypes();
		databaseHandler = configStore.getDatabaseRequestHandler();
	}

	/**
	 * Generates a single-cruise-format data report (includes 
	 * WOCE-3 and WOCE-4 data and original-data CO2 measurements).
	 * If successful, any warnings about the generated report
	 * are returned.
	 * 
	 * @param expocode
	 * 		report the data of the cruise with this expocode 
	 * @param reportFile
	 * 		print the report to this file
	 * @return
	 * 		list of warnings about the generated report;
	 * 		never null but may be empty
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * @throws IOException 
	 * 		if unable to read the DSG NC file, or
	 * 		if unable to create the cruise report file
	 */
	public ArrayList<String> generateReport(String expocode, File reportFile) 
			throws IllegalArgumentException, IOException {
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		ArrayList<String> warnMsgs = new ArrayList<String>();

		// Get the metadata and data from the DSG file
		CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
		ArrayList<String> unknownVars = dsgFile.readMetadata(knownMetadataTypes);
		if ( unknownVars.size() > 0 ) {
			String msg = "Unassigned metadata variables: ";
			for (String var : unknownVars)
				msg += var + "; ";
			warnMsgs.add(msg);
		}
		unknownVars = dsgFile.readData(knownDataFileTypes);
		if ( unknownVars.size() > 0 ) {
			String msg = "Unassigned data variables: ";
			for (String var : unknownVars)
				msg += var + "; ";
			warnMsgs.add(msg);
		}

		// Get the SOCAT version and QC flag from the DSG metadata
		SocatMetadata socatMeta = dsgFile.getMetadata();
		String socatVersion = socatMeta.getSocatVersion();
		String qcFlag = socatMeta.getQcFlag();

		// Get the rest of the metadata info from the OME XML
		DashboardMetadata metadata = 
				metadataHandler.getMetadataInfo(upperExpo, DashboardMetadata.OME_FILENAME);
		DashboardOmeMetadata omeMeta = new DashboardOmeMetadata(metadata, metadataHandler);

		// Get the list of additional document filenames associated with this cruise.
		// Use what the QC-ers see - the directory listing.
		TreeSet<String> addlDocs = new TreeSet<String>();
		for ( DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo) ) {
			if ( ! mdata.getFilename().equals(DashboardMetadata.OME_FILENAME) ) {
				addlDocs.add(mdata.getFilename());
			}
		}

		// Get the original and SOCAT-enhanced data document DOIs for this cruise 
		DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(upperExpo);
		String origDOI = cruise.getOrigDoi();
		if ( origDOI.isEmpty() )
			origDOI = NOT_AVAILABLE_TAG;
		String socatDOI = cruise.getSocatDoi();
		if ( socatDOI.isEmpty() )
			socatDOI = SOCAT_ENHANCED_DOI_TAG;

		// Get the computed values of time in seconds since 1970-01-01 00:00:00
		double[] sectimes = dsgFile.readDoubleVarDataValues(KnownDataTypes.TIME.getVarName());

		// Generate the report
		PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
		try {
			ArrayList<String> msgs = printMetadataPreamble(omeMeta, 
					socatVersion, origDOI, socatDOI, qcFlag, addlDocs, report);
			warnMsgs.addAll(msgs);
			printDataTableHeader(report, false);
			int j = -1;
			for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
				j++;
				// Reported WOCE-4 might be duplicates, so do not check for duplicates
				String tsvdat = dataReportString(dataVals, sectimes[j], upperExpo, 
						socatVersion, socatDOI, qcFlag, false, null);
				if ( ! tsvdat.isEmpty() ) {
					report.println(tsvdat);
				}
			}
		} finally {
			report.close();
		}

		return warnMsgs;
	}

	/**
	 * Generates a multi-cruise-format data report (does not include 
	 * WOCE-3 and WOCE-4 data nor original-data CO2 measurements).
	 * If successful, any warnings about the generated report
	 * are returned.
	 * 
	 * @param expocodes
	 * 		report the data for the cruises with these expocodes
	 * @param regionID
	 * 		report only data in the region with this ID; 
	 * 		if null, no region restriction is made on the data
	 * @param reportFile
	 * 		print the report to this file
	 * @return
	 * 		list of warnings about the generated report;
	 * 		never null but may be empty
	 * @throws IllegalArgumentException
	 * 		if an expocode is invalid,
	 * @throws IOException 
	 * 		if unable to read a DSG NC file, or
	 * 		if unable to create the cruise report file
	 */
	public ArrayList<String> generateReport(TreeSet<String> expocodes, Character regionID, 
			File reportFile) throws IllegalArgumentException, IOException {
		int numDatasets = expocodes.size();
		ArrayList<String> upperExpoList = new ArrayList<String>(numDatasets);
		ArrayList<String> socatVersionList = new ArrayList<String>(numDatasets);
		ArrayList<String> origDOIList = new ArrayList<String>(numDatasets);
		ArrayList<String> socatDOIList = new ArrayList<String>(numDatasets);
		ArrayList<String> qcFlagList = new ArrayList<String>(numDatasets);
		ArrayList<String> warnMsgs = new ArrayList<String>(numDatasets);

		for ( String expo : expocodes ) {
			// Get the expocodes, SOCAT version, and QC flags 
			// of the datasets to report (checking region IDs, if appropriate)
			String upperExpo = DashboardServerUtils.checkExpocode(expo);
			CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
			ArrayList<String> unknownVars = dsgFile.readMetadata(knownMetadataTypes);
			if ( unknownVars.size() > 0 ) {
				String msg = upperExpo + " unknown metadata variables: ";
				for (String var : unknownVars)
					msg += var + "; ";
				warnMsgs.add(msg);
			}
			boolean inRegion;
			if ( regionID != null ) {
				inRegion = false;
				dsgFile.readData(knownDataFileTypes);
				for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
					if ( regionID.equals(dataVals.getRegionID()) ) {
						Character woceFlag = dataVals.getWoceCO2Water();
						// Ignore WOCE-3 and WOCE-4 as they are not reported 
						// and may be indicating invalid locations for this cruise
						if ( WoceEvent.WOCE_GOOD.equals(woceFlag) || 
							 WoceEvent.WOCE_NOT_CHECKED.equals(woceFlag) ) {
							inRegion = true;
							break;
						}
					}
				}
			}
			else {
				inRegion = true;
			}
			if ( inRegion ) {
				SocatMetadata socatMeta = dsgFile.getMetadata();
				socatVersionList.add(socatMeta.getSocatVersion());
				qcFlagList.add(socatMeta.getQcFlag());
				DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(upperExpo);
				String origDOI = cruise.getOrigDoi();
				if ( origDOI.isEmpty() )
					origDOI = NOT_AVAILABLE_TAG;
				origDOIList.add(origDOI);
				String socatDOI = cruise.getSocatDoi();
				if ( socatDOI.isEmpty() )
					socatDOI = SOCAT_ENHANCED_DOI_TAG;
				socatDOIList.add(socatDOI);
				upperExpoList.add(upperExpo);
			}
		}

		// Get the rest of the metadata info from the OME XML
		ArrayList<DashboardOmeMetadata> omeMetaList = new ArrayList<DashboardOmeMetadata>();
		for ( String upperExpo : upperExpoList ) {
			DashboardMetadata metadata = 
					metadataHandler.getMetadataInfo(upperExpo, DashboardMetadata.OME_FILENAME);
			omeMetaList.add(new DashboardOmeMetadata(metadata, metadataHandler));
		}

		// Get the list of additional document filenames associated with this cruise.
		// Use what the QC-ers see - the directory listing.
		ArrayList<TreeSet<String>> addlDocsList = new ArrayList<TreeSet<String>>();
		for ( String upperExpo : upperExpoList ) {
			TreeSet<String> addlDocs = new TreeSet<String>();
			for ( DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo) ) {
				if ( ! mdata.getFilename().equals(DashboardMetadata.OME_FILENAME) ) {
					addlDocs.add(mdata.getFilename());
				}
			}
			addlDocsList.add(addlDocs);
		}

		String regionName;
		if ( regionID != null )
			regionName = DataLocation.REGION_NAMES.get(regionID);
		else
			regionName = null;

		PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
		try {
			ArrayList<String> msgs = printMetadataPreamble(regionName, omeMetaList, 
					socatVersionList, origDOIList, socatDOIList, qcFlagList, addlDocsList, report);
			warnMsgs.addAll(msgs);
			printDataTableHeader(report, true);
			// Read and report the data for one cruise at a time
			for (int k = 0; k < upperExpoList.size(); k++) {
				String upperExpo = upperExpoList.get(k);
				String socatVersion = socatVersionList.get(k);
				String qcFlag = qcFlagList.get(k);
				String socatDOI = cruiseHandler.getCruiseFromInfoFile(upperExpo).getSocatDoi();
				if ( socatDOI.isEmpty() )
					socatDOI = SOCAT_ENHANCED_DOI_TAG;
				CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
				ArrayList<String> unknownVars = dsgFile.readData(knownDataFileTypes);
				if ( unknownVars.size() > 0 ) {
					String msg = upperExpo + " unknown data variables: ";
					for (String var : unknownVars)
						msg += var + "; ";
					warnMsgs.add(msg);
				}
				// Get the computed values of time in seconds since 1970-01-01 00:00:00
				double[] sectimes = dsgFile.readDoubleVarDataValues(KnownDataTypes.TIME.getVarName());
				// Create the set for holding previous lon/lat/time/fCO2_rec data
				TreeSet<DataPoint> prevDatPts = new TreeSet<DataPoint>();
				int j = -1;
				for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
					j++;
					if ( (regionID != null) && ! regionID.equals(dataVals.getRegionID()) )
						continue;
					if ( DashboardUtils.FP_MISSING_VALUE.equals(dataVals.getfCO2Rec()) )
						continue;
					Character woceFlag = dataVals.getWoceCO2Water();
					if ( woceFlag.equals(WoceEvent.WOCE_GOOD) || 
						 woceFlag.equals(WoceEvent.WOCE_NOT_CHECKED) ) {
						String tsvdat = dataReportString(dataVals, sectimes[j], upperExpo, 
								socatVersion, socatDOI, qcFlag, true, prevDatPts);
						if ( ! tsvdat.isEmpty() ) {
							report.println(tsvdat);
						}
					}
				}
			}
		} finally {
			report.close();
		}

		return warnMsgs;
	}

	/**
	 * Prints the metadata preamble for a single-cruise report.  
	 * If successful, any warnings about the generated preamble
	 * are returned.
	 * 
	 * @param omeMeta
	 * 		OME XML document with metadata values to report in the preamble
	 * @param version
	 * 		SOCAT version to report in the preamble
	 * @param origDOI
	 * 		DOI for the original data file
	 * @param socatDOI
	 * 		DOI for this SOCAT-enhanced data file
	 * @param qcFlag
	 * 		QC flag to report in the preamble
	 * @param addlDocs
	 * 		filenames of additional documents to report in the preamble
	 * @param report
	 * 		print with this PrintWriter
	 * @return
	 * 		list of warnings about the generated preamble;
	 * 		never null but may be empty
	 */
	private static ArrayList<String> printMetadataPreamble(DashboardOmeMetadata omeMeta, 
									String socatVersion, String origDOI, String socatDOI, 
									String qcFlag, TreeSet<String> addlDocs, PrintWriter report) {
		String upperExpo = omeMeta.getExpocode();
		ArrayList<String> warnMsgs = new ArrayList<String>();

		report.println("SOCAT data report created: " + TIMESTAMPER.format(new Date()));
		report.println("Expocode: " + upperExpo);
		report.println("version: " + socatVersion);
		report.println("Cruise/Dataset Name: " + omeMeta.getCruiseName());
		report.println("Ship/Vessel Name: " + omeMeta.getVesselName());
		report.println("Principal Investigator(s): " + omeMeta.getScienceGroup());
		report.println("DOI for the original data: " + origDOI);
		report.println("    or see: " + omeMeta.getOrigDataRef());
		report.println("DOI of this SOCAT-enhanced data: " + socatDOI);
		report.println("    or see: " + SOCAT_ENHANCED_HREF_PREFIX + socatDOI);
		report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
		report.println("    or see: " + SOCAT_ENHANCED_HREF_PREFIX + SOCAT_MAIN_DOI);
		report.println();

		// Additional references - add expocode suffix for clarity
		report.println("Supplemental documentation reference(s):");
		for (String filename : addlDocs) {
			report.println("    " + filename);
		}
		report.println();

		// Longitude range in [180W,180E]
		double westLon;
		double eastLon;
		try {
			westLon = omeMeta.getWestmostLongitude();
			eastLon = omeMeta.getEastmostLongitude();
			if ( westLon < 0.0 )
				report.format("Longitude range: %#.2fW", -1.0 * westLon);
			else
				report.format("Longitude range: %#.2fE", westLon);
			if ( eastLon < 0.0 )
				report.format(" to %#.2fW", -1.0 * eastLon);
			else
				report.format(" to %#.2fE", eastLon);
			report.println();
		} catch (Exception ex) {
			warnMsgs.add(ex.getMessage());
		}

		// Latitude range in [90S,90N]
		double southLat;
		double northLat;
		try {
			southLat = omeMeta.getSouthmostLatitude();
			northLat = omeMeta.getNorthmostLatitude();
			if ( southLat < 0.0 )
				report.format("Latitude range: %#.2fS", -1.0 * southLat);
			else
				report.format("Latitude range: %#.2fN", southLat);
			if ( northLat < 0.0 )
				report.format(" to %#.2fS", -1.0 * northLat);
			else
				report.format(" to %#.2fN", northLat);
			report.println();
		} catch (Exception ex) {
			warnMsgs.add(ex.getMessage());
		}

		// Time range
		String startDatestamp;
		String endDatestamp;
		try {
			startDatestamp = omeMeta.getBeginDatestamp();
			endDatestamp = omeMeta.getEndDatestamp();
			report.println("Time range: " + startDatestamp + " to " + endDatestamp);
		} catch (Exception ex) {
			warnMsgs.add(ex.getMessage());
		}

		// Check if this is a day-resolution cruise whose hour, minutes, and seconds 
		// were approximated and reset in the database
		if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(upperExpo) ) {
			report.println();
			report.println("Observation times were not provided to a resolution of hours;");
			report.println("the hours, minutes, and seconds given are artificially generated values");
			warnMsgs.add("Data set was marked as having artificial hours, minutes, and seconds");
		}
		report.println();
		report.println("Data set QC flag: " + qcFlag + " (see below)");
		report.println();

		return warnMsgs;
	}

	/**
	 * Prints the metadata preamble for a multi-cruise report.  
	 * If successful, any warnings about the generated preamble
	 * are returned.
	 * 
	 * @param regionName
	 * 		name of the region being reported; use null for no region
	 * @param omeMetaList
	 * 		list of metadata values, one per dataset, to use for information to report
	 * @param socatVersionList
	 * 		list of SOCAT version numbers, one per dataset, to report
	 * @param origDOIList
	 * 		list of DOIs, one per dataset, for the original data files
	 * @param socatDOIList
	 * 		list of DOIs, one per dataset, for the SOCAT-enhanced data files
	 * @param qcFlagList
	 * 		list of QC flags, one per dataset, to report
	 * @param addlDocsList
	 * 		list of additional documents, one set of names per dataset, to report
	 * @param report
	 * 		print with this PrintWriter
	 * @return
	 * 		list of warnings about the generated preamble;
	 * 		never null but may be empty
	 */
	private static ArrayList<String> printMetadataPreamble(String regionName, 
			ArrayList<DashboardOmeMetadata> omeMetaList, ArrayList<String> socatVersionList, 
			ArrayList<String> origDOIList, ArrayList<String> socatDOIList, ArrayList<String> qcFlagList, 
			ArrayList<TreeSet<String>> addlDocsList, PrintWriter report) {
		ArrayList<String> warnMsgs = new ArrayList<String>();

		report.println("SOCAT data report created: " + TIMESTAMPER.format(new Date()));
		report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
		report.println("    or see: " + SOCAT_ENHANCED_HREF_PREFIX + SOCAT_MAIN_DOI);
		if ( regionName == null )
			report.println("SOCAT data for the following data sets:");
		else
			report.println("SOCAT data in SOCAT region \"" + 
					regionName + "\" for the following data sets:");
		report.println("Expocode\t" +
					   "version\t" +
					   "Cruise/Dataset Name\t" +
					   "Ship/Vessel Name\t" +
					   "PI(s)\t" +
					   "Original Data DOI\t" +
					   "Original Data Reference\t" +
					   "SOCAT DOI\t" +
					   "SOCAT Reference\t" +
					   "Westmost Longitude\t" +
					   "Eastmost Longitude\t" +
					   "Southmost Latitude\t" +
					   "Northmost Latitude\t" +
					   "Start Time\t" +
					   "End Time\t" +
					   "QC Flag\t" +
					   "Additional Metadata Document(s)");
		boolean needsFakeHoursMsg = false;
		for (int k = 0; k < omeMetaList.size(); k++) {
			DashboardOmeMetadata omeMeta = omeMetaList.get(k);
			String upperExpo = omeMeta.getExpocode();
			String socatVersion = socatVersionList.get(k);
			String cruiseName = omeMeta.getCruiseName();
			if ( cruiseName.isEmpty() )
				cruiseName = NOT_AVAILABLE_TAG;
			String qcFlag = qcFlagList.get(k);
			TreeSet<String> addlDocs = addlDocsList.get(k);
			String origDOI = origDOIList.get(k);
			String origHRef = omeMeta.getOrigDataRef();
			if ( origHRef.isEmpty() )
				origHRef = NOT_AVAILABLE_TAG;
			String socatDOI = socatDOIList.get(k);
			String socatHRef = SOCAT_ENHANCED_HREF_PREFIX + socatDOI;

			report.print(upperExpo);
			report.print("\t");

			report.print(socatVersion);
			report.print("\t");

			report.print(cruiseName);
			report.print("\t");

			report.print(omeMeta.getVesselName());
			report.print("\t");

			report.print(omeMeta.getScienceGroup());
			report.print("\t");

			report.print(origDOI);
			report.print("\t");

			report.print(origHRef);
			report.print("\t");

			report.print(socatDOI);
			report.print("\t");

			report.print(socatHRef);
			report.print("\t");

			try {
				double westLon = omeMeta.getWestmostLongitude();
				if ( westLon < 0.0 ) {
					report.format("%#.2fW", -1.0 * westLon);
				}
				else {
					report.format("%#.2fE", westLon);
				}
			} catch (Exception ex) {
				// Leave blank
				warnMsgs.add("Invalid west-most longitude for " + upperExpo);
			}
			report.print("\t");

			try {
				double eastLon = omeMeta.getEastmostLongitude();
				if ( eastLon < 0.0 ) {
					report.format("%#.2fW", -1.0 * eastLon);
				}
				else {
					report.format("%#.2fE", eastLon);
				}
			} catch (Exception ex) {
				// Leave blank
				warnMsgs.add("Invalid east-most longitude for " + upperExpo);
			}
			report.print("\t");

			try {
				double southLat = omeMeta.getSouthmostLatitude();
				if ( southLat < 0.0 ) {
					report.format("%#.2fS", -1.0 * southLat);
				}
				else {
					report.format("%#.2fN", southLat);
				}
			} catch (Exception ex) {
				// Leave blank
				warnMsgs.add("Invalid south-most latitude for " + upperExpo);
			}
			report.print("\t");

			try {
				double northLat = omeMeta.getNorthmostLatitude();
				if ( northLat < 0.0 ) {
					report.format("%#.2fS", -1.0 * northLat);
				}
				else {
					report.format("%#.2fN", northLat);
				}
			} catch (Exception ex) {
				// Leave blank
				warnMsgs.add("Invalid north-most latitude for " + upperExpo);
			}
			report.print("\t");

			try {
				String beginDatestamp = omeMeta.getBeginDatestamp();
				report.print(beginDatestamp);
			} catch (Exception ex) {
				// Leave blank
				warnMsgs.add("Invalid beginning date for " + upperExpo);
			}
			report.print("\t");

			try {
				String endDatestamp = omeMeta.getEndDatestamp();
				report.print(endDatestamp);
			} catch (Exception ex) {
				// Leave blank
				warnMsgs.add("Invalid ending date for " + upperExpo);
			}
			report.print("\t");

			report.print(qcFlag);
			report.print("\t");

			String docs = "";
			boolean isFirst = true;
			for (String filename : addlDocs ) {
				if ( isFirst )
					isFirst = false;
				else
					docs += "; ";
				docs += filename;
			}
			report.print(docs);

			report.println();

			if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(upperExpo) )
				needsFakeHoursMsg = true;
		}

		if ( needsFakeHoursMsg ) {
			boolean isFirst = true;
			report.print("Note for data set(s): ");
			for ( DashboardOmeMetadata omeMeta : omeMetaList ) {
				String upperExpo = omeMeta.getExpocode();
				if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(upperExpo) ) {
					if ( isFirst )
						isFirst = false;
					else
						report.print("; ");
					report.print(upperExpo);
					warnMsgs.add(upperExpo + " was marked as having artificial hours, minutes, and seconds");
				}
			}
			report.println("    Observation times were not provided to a resolution of hours;");
			report.println("    the hours, minutes, and seconds given are artificially generated values");
		}

		report.println();

		return warnMsgs;
	}

	/**
	 * Prints the data table header.  This includes the explanation of the data columns
	 * as well as the SOCAT reference and, if appropriate, the salinity used if WOA_SSS
	 * is needed but missing.
	 * 
	 * @param report
	 * 		print to this PrintWriter
	 * @param multicruise
	 * 		is this header for a multi-cruise report 
	 * 		(only data with fCO2_rec given and WOCE-flag 2 or not given, 
	 * 		 do not include original-data CO2 measurement columns) ?
	 */
	private static void printDataTableHeader(PrintWriter report, boolean multicruise) {
		report.println("Explanation of data columns:");
		if ( multicruise ) {
			for (int k = 0; k < MULTI_CRUISE_DATA_REPORT_EXPLANATIONS.length; k++)
				report.println(MULTI_CRUISE_DATA_REPORT_EXPLANATIONS[k]);
		}
		else {
			for (int k = 0; k < SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS.length; k++)
				report.println(SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS[k]);
		}
		report.println();
		report.println("The quality assessments given by the QC flag and fCO2rec_flag only apply to");
		report.println("the fCO2rec value.  For more information about the recomputed fCO2 value and");
		report.println("the meaning of the QC flag, fCO2rec_src, and fCO2rec_flag values, see:");
		for (int k = 0; k < SOCAT_MAIN_CITATION.length; k++)
			report.println(SOCAT_MAIN_CITATION[k]);
		if ( multicruise ) {
			report.println();
			report.println("This is a report of only data points with recomputed fCO2 values");
			report.println("which were deemed acceptable (WOCE flag 2). ");
		}
		else {
			report.println();
			report.println("This is a report of all data points, including those with missing");
			report.println("recomputed fCO2 values and those with a WOCE flag indicating questionable (3)");
			report.println("or bad (4) recomputed fCO2 values.");
		}
		report.println();
		report.println("Terms of use of this data are given in the SOCAT Fair Data Use Statement ");
		report.println("http://www.socat.info/SOCAT_fair_data_use_statement.htm");
		if ( ! multicruise ) {
			report.println();
			report.println("All standard SOCAT data columns are reported in this file,");
			report.println("even if all values are missing ('NaN') for this data set.");
		}
		report.println();

		if ( multicruise )
			report.println(MULTI_CRUISE_DATA_REPORT_HEADER);			
		else
			report.println(SINGLE_CRUISE_DATA_REPORT_HEADER);
	}

	/**
	 * Explanation lines for the data columns given by the header string 
	 * {@link #SINGLE_CRUISE_DATA_REPORT_HEADER} 
	 * and data strings returned by the single-cruise version of 
	 * {@link #dataReportString}
	 */
	private static final String[] SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS = {
		"Expocode: unique identifier for the data set from which this data was obtained",
		"version: version of SOCAT where this enhanced data first appears",
		"SOCAT_DOI: DOI for this SOCAT-enhanced data",
		"QC_Flag: Data set QC flag",
		"yr: 4-digit year of the time (UTC) of the measurement",
		"mon: month of the time (UTC) of the measurement",
		"day: day of the time (UTC) of the measurement",
		"hh: hour of the time (UTC) of the measurement",
		"mm: minute of the time (UTC) of the measurement",
		"ss: second of the time (UTC) of the measurement (may include decimal places)",
		"longitude: measurement longitude, from zero to 360, in decimal degrees East",
		"latitude: measurement latitude in decimal degrees North",
		"sample_depth: water sampling depth in meters",
		"sal: measured sea surface salinity on the Practical Salinity Scale",
		"SST: measured sea surface temperature in degrees Celcius",
		"Tequ: equilibrator chamber temperature in degrees Celcius",
		"PPPP: measured atmospheric pressure in hectopascals",
		"Pequ: equilibrator chamber pressure in hectopascals",
		"WOA_SSS: sea surface salinity on the Practical Salinity Scale interpolated from the",
		"    World Ocean Atlas 2005 (see: http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)",
		"NCEP_SLP: sea level pressure in hectopascals interpolated from the NCEP/NCAR Reanalysis Project",
		"    (see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)",
		"ETOPO2_depth: bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded ",
		"    Global Relief Data (see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)",
		"dist_to_land: estimated distance to major land mass in kilometers (up to 1000 km)",
		"GVCO2: atmospheric xCO2 in micromole per mole interpolated from GlobalView-CO2, 2014 ",
		"    1979-01-01 to 2014-01-01 data (see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html)",
		"xCO2water_equ_dry: measured xCO2 (water) in micromole per mole at equilibrator temperature (dry air)",
		"xCO2water_SST_dry: measured xCO2 (water) in micromole per mole at sea surface temperature (dry air)",
		"pCO2water_equ_wet: measured pCO2 (water) in microatmospheres at equilibrator temperature (wet air)",
		"pCO2water_SST_wet: measured pCO2 (water) in microatmospheres at sea surface temperature (wet air)",
		"fCO2water_equ_wet: measured fCO2 (water) in microatmospheres at equilibrator temperature (wet air)",
		"fCO2water_SST_wet: measured fCO2 (water) in microatmospheres at sea surface temperature (wet air)",
		"fCO2rec: fCO2 in microatmospheres recomputed from the measured CO2 data (see below)",
		"fCO2rec_src: algorithm for generating fCO2rec from the measured CO2 data (0:not generated; 1-14, see below)",
		"fCO2rec_flag: WOCE flag for this fCO2rec value (2:good, 3:questionable, 4:bad, 9:not generated; see below)",
		"",
		"Missing values are indicated by 'NaN'"
	};

	/**
	 * Tab-separated data column names for the data Strings returned by the single-cruise version of 
	 * {@link #dataReportString}
	 */
	private static final String SINGLE_CRUISE_DATA_REPORT_HEADER = 
			"Expocode\t" +
			"version\t" +
			"SOCAT_DOI\t" +
			"QC_Flag\t" +
			"yr\t" +
			"mon\t" +
			"day\t" +
			"hh\t" +
			"mm\t" +
			"ss\t" +
			"longitude [dec.deg.E]\t" +
			"latitude [dec.deg.N]\t" +
			"sample_depth [m]\t" +
			"sal\t" +
			"SST [deg.C]\t" +
			"Tequ [deg.C]\t" +
			"PPPP [hPa]\t" +
			"Pequ [hPa]\t" +
			"WOA_SSS\t" +
			"NCEP_SLP [hPa]\t" +
			"ETOPO2_depth [m]\t" +
			"dist_to_land [km]\t" +
			"GVCO2 [umol/mol]\t" +
			"xCO2water_equ_dry [umol/mol]\t" +
			"xCO2water_SST_dry [umol/mol]\t" +
			"pCO2water_equ_wet [uatm]\t" +
			"pCO2water_SST_wet [uatm]\t" +
			"fCO2water_equ_wet [uatm]\t" +
			"fCO2water_SST_wet [uatm]\t" +
			"fCO2rec [uatm]\t" +
			"fCO2rec_src\t" +
			"fCO2rec_flag";

	/**
	 * Explanation lines for the data columns given by the header string 
	 * {@link #MULTI_CRUISE_DATA_REPORT_HEADER} 
	 * and data strings returned by the multi-cruise version of 
	 * {@link #dataReportString}
	 */
	private static final String[] MULTI_CRUISE_DATA_REPORT_EXPLANATIONS = {
		"Expocode: unique identifier for the data set from which this data was obtained",
		"version: version of SOCAT where this enhanced data first appears",
		"SOCAT_DOI: DOI for this SOCAT-enhanced data",
		"QC_Flag: Data set QC flag",
		"yr: 4-digit year of the time (UTC) of the measurement",
		"mon: month of the time (UTC) of the measurement",
		"day: day of the time (UTC) of the measurement",
		"hh: hour of the time (UTC) of the measurement",
		"mm: minute of the time (UTC) of the measurement",
		"ss: second of the time (UTC) of the measurement (may include decimal places)",
		"longitude: measurement longitude, from zero to 360, in decimal degrees East",
		"latitude: measurement latitude in decimal degrees North",
		"sample_depth: water sampling depth in meters",
		"sal: measured sea surface salinity on the Practical Salinity Scale",
		"SST: measured sea surface temperature in degrees Celcius",
		"Tequ: equilibrator chamber temperature in degrees Celcius",
		"PPPP: measured atmospheric pressure in hectopascals",
		"Pequ: equilibrator chamber pressure in hectopascals",
		"WOA_SSS: sea surface salinity on the Practical Salinity Scale interpolated from the",
		"    World Ocean Atlas 2005 (see: http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)",
		"NCEP_SLP: sea level pressure in hectopascals interpolated from the NCEP/NCAR Reanalysis Project",
		"    (see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)",
		"ETOPO2_depth: bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded ",
		"    Global Relief Data (see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)",
		"dist_to_land: estimated distance to major land mass in kilometers (up to 1000 km)",
		"GVCO2: atmospheric xCO2 in micromole per mole interpolated from GlobalView-CO2, 2014 ",
		"    1979-01-01 to 2014-01-01 data (see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html)",
		"fCO2rec: fCO2 in microatmospheres recomputed from the raw data (see below)",
		"fCO2rec_src: algorithm for generating fCO2rec from the raw data (0:not generated; 1-14, see below)",
		"fCO2rec_flag: WOCE flag for this fCO2rec value (2:good, 3:questionable, 4:bad, 9:not generated; see below)",
		"",
		"Missing values are indicated by 'NaN'"
	};

	/**
	 * Tab-separated data column names for the data Strings returned by the multi-cruise version of 
	 * {@link #dataReportString}
	 */
	private static final String MULTI_CRUISE_DATA_REPORT_HEADER = 
			"Expocode\t" + 
			"version\t" +
			"SOCAT_DOI\t" +
			"QC_Flag\t" +
			"yr\t" +
			"mon\t" +
			"day\t" +
			"hh\t" +
			"mm\t" +
			"ss\t" +
			"longitude [dec.deg.E]\t" +
			"latitude [dec.deg.N]\t" +
			"sample_depth [m]\t" +
			"sal\t" +
			"SST [deg.C]\t" +
			"Tequ [deg.C]\t" +
			"PPPP [hPa]\t" +
			"Pequ [hPa]\t" +
			"WOA_SSS\t" +
			"NCEP_SLP [hPa]\t" +
			"ETOPO2_depth [m]\t" +
			"dist_to_land [km]\t" +
			"GVCO2 [umol/mol]\t" +
			"fCO2rec [uatm]\t" +
			"fCO2rec_src\t" +
			"fCO2rec_flag";

	/**
	 * @param dataVals
	 * 		data point values to report
	 * @param sectime
	 * 		date/time of this data point in seconds since 01-JAN-1970 00:00:00 UTC
	 * @param expocode
	 * 		expocode for the cruise data report string
	 * @param version
	 * 		SOCAT Version for the cruise data report string
	 * @param socatDOI
	 * 		SOCAT DOI for the cruise data report string
	 * @param cruiseQCFlag
	 * 		cruise QC flag for the cruise
	 * @param multicruise
	 * 		create the multi-cruise data string
	 * 		(no original-data CO2 measurements) ?
	 * @param prevDatPts
	 * 		if not null, a set of DataPoint objects for previous datapoints 
	 * 		in this dataset.  This method will add the current datapoint, 
	 * 		if not a duplicate, to this set.
	 * @return 
	 * 		tab-separated data values for SOCAT data reporting, or
	 * 		an empty string if this lon/lat/time/fCO2_rec duplicates
	 * 		that for a previous datapoint.
	 */
	private static String dataReportString(SocatCruiseData dataVals, 
			Double sectime, String expocode, String socatVersion, 
			String socatDOI, String cruiseQCFlag, boolean multicruise, 
			TreeSet<DataPoint> prevDatPts) throws IllegalArgumentException {

		if ( prevDatPts != null ) {
			// Add this lon/lat/time/fCO2_rec datapoint to the set; checking for duplicates
			DataPoint datpt = new DataPoint(expocode, sectime, dataVals.getLatitude(), 
											dataVals.getLongitude(), dataVals.getfCO2Rec());
			if ( ! prevDatPts.add(datpt) ) {
				System.err.println("Ignored duplicate datapoint for " + expocode + ": " + datpt.toString());
				return "";
			}
		}

		// Generate the string for this data point
		Formatter fmtr = new Formatter();
		fmtr.format("%s\t", expocode);
		fmtr.format("%s\t", socatVersion);
		fmtr.format("%s\t", socatDOI);
		fmtr.format("%s\t", cruiseQCFlag);

		Integer intVal = dataVals.getYear();
		if ( DashboardUtils.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%04d\t", intVal);

		intVal = dataVals.getMonth();
		if ( DashboardUtils.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		intVal = dataVals.getDay();
		if ( DashboardUtils.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		intVal = dataVals.getHour();
		if ( DashboardUtils.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		intVal = dataVals.getMinute();
		if ( DashboardUtils.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		Double dblVal = dataVals.getSecond();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#03.0f\t", dblVal);

		dblVal = dataVals.getLongitude();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) ) {
			fmtr.format("NaN\t");
		}
		else {
			while ( dblVal < 0.0 )
				dblVal += 360.0;
			while ( dblVal >= 360.0 )
				dblVal -= 360.0;
			fmtr.format("%#.5f\t", dblVal);
		}

		dblVal = dataVals.getLatitude();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.5f\t", dblVal);

		dblVal = dataVals.getSampleDepth();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.0f\t", dblVal);

		dblVal = dataVals.getSalinity();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getSst();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getTEqu();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getPAtm();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getPEqu();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getWoaSalinity();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getNcepSlp();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getEtopo2Depth();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.0f\t", dblVal);

		dblVal = dataVals.getDistToLand();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.0f\t", dblVal);

		dblVal = dataVals.getGvCO2();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		if ( ! multicruise ) {
			dblVal = dataVals.getXCO2WaterTEquDry();
			if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getXCO2WaterSstDry();
			if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getPCO2WaterTEquWet();
			if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getPCO2WaterSstWet();
			if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getFCO2WaterTEquWet();
			if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getFCO2WaterSstWet();
			if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);
		}

		dblVal = dataVals.getfCO2Rec();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		// if fCO2_rec not given, always set source to zero
		intVal = dataVals.getFCO2Source();
		if ( DashboardUtils.INT_MISSING_VALUE.equals(intVal) || 
			 DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("0\t");
		else
			fmtr.format("%d\t", intVal);

		// if fCO2_rec not given, always set to nine ("bottle not sampled");
		// otherwise if missing or zero, it is presumed to be good (two)
		Character charVal = dataVals.getWoceCO2Water();
		if ( DashboardUtils.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("9");
		else if ( Character.valueOf(' ').equals(charVal) || 
				  Character.valueOf('0').equals(charVal) )
			fmtr.format("2");
		else
			fmtr.format("%c", charVal);

		String repStr = fmtr.toString();
		fmtr.close();
		return repStr;
	}

	/**
	 * Print the summary line for a cruise.  The line contains tab-separated 
	 * values in the order corresponding to the tab-separated titles in the 
	 * header line printed by {@link #printSummaryHeader(PrintStream)}.
	 * 
	 * @param expocode
	 * 		print the summary for the cruise with this expocode
	 * @param out
	 * 		print the summary to here
	 * @throws IllegalArgumentException
	 * 		if there are problems generating the summary for the cruise
	 */
	public void printCruiseSummary(String expocode, PrintWriter out) 
											throws IllegalArgumentException {
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);

		String dsgQCFlag;
		// String databaseQCFlag;
		String socatVersionStatus;
		String oldExpocode;
		String regions;
		String numRows;
		String numMissRows;
		String numErrRows;
		String numWarnRows;
		String numOkayRows;
		String vesselName;
		String pis;

		DashboardCruise cruiseInfo = cruiseHandler.getCruiseFromInfoFile(upperExpo);
		if ( cruiseInfo == null )
			throw new IllegalArgumentException("No cruise data for " + upperExpo);
		String qcStatus = cruiseInfo.getQcStatus();
		if ( QCEvent.QC_STATUS_NOT_SUBMITTED.equals(qcStatus) )
			throw new IllegalArgumentException(upperExpo + " has not been submitted for QC");

		DashboardOmeMetadata omeMetadata = new DashboardOmeMetadata(
				metadataHandler.getMetadataInfo(upperExpo, DashboardMetadata.OME_FILENAME), metadataHandler);
		vesselName = omeMetadata.getVesselName();
		pis = omeMetadata.getScienceGroup();

		CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
		if ( ! dsgFile.exists() )
			throw new IllegalArgumentException("DSG file does not exist for " + upperExpo);
		try {
			dsgFile.readMetadata(knownMetadataTypes);
			dsgFile.readData(knownDataFileTypes);
		} catch (IOException ex) {
			throw new IllegalArgumentException("Problems reading the DSG file for " + 
					upperExpo + ": " + ex.getMessage());
		}

		ArrayList<SocatCruiseData> dataList = dsgFile.getDataList();
		numRows = Integer.toString(dataList.size());
		int numMissing = 0;
		int numWoceOkay = 0;
		int numWoceBad = 0;
		int numWoceWarn = 0;
		TreeSet<String> regionNames = new TreeSet<String>();
		for ( SocatCruiseData data : dataList ) {
			Character woceCO2Water = data.getWoceCO2Water();
			if ( DashboardUtils.FP_MISSING_VALUE.equals(data.getfCO2Rec()) ) {
				numMissing++;
			}
			else if ( woceCO2Water.equals(WoceEvent.WOCE_BAD) ) {
				numWoceBad++;
			}
			else if ( woceCO2Water.equals(WoceEvent.WOCE_QUESTIONABLE) ) {
				numWoceWarn++;
			}
			else { 
				numWoceOkay++;
			}
			regionNames.add(DataLocation.REGION_NAMES.get(data.getRegionID()));
		}
		numMissRows = Integer.toString(numMissing);
		numErrRows = Integer.toString(numWoceBad);
		numWarnRows = Integer.toString(numWoceWarn);
		numOkayRows = Integer.toString(numWoceOkay);
		regionNames.remove(DataLocation.REGION_NAMES.get(DataLocation.GLOBAL_REGION_ID));
		regions = "";
		for ( String name : regionNames )
			regions += "; " + name;
		regions = regions.substring(2);

		SocatMetadata socatMetadata = dsgFile.getMetadata();
		socatVersionStatus = socatMetadata.getSocatVersion();
		dsgQCFlag = socatMetadata.getQcFlag();
		String westmost = String.format("%#.2f", socatMetadata.getWestmostLongitude());
		String eastmost = String.format("%#.2f", socatMetadata.getEastmostLongitude());
		String southmost = String.format("%#.2f", socatMetadata.getSouthmostLatitude());
		String northmost = String.format("%#.2f", socatMetadata.getNorthmostLatitude());
		String firsttime = String.format("%1$tF %1$tR", socatMetadata.getBeginTime());
		String lasttime = String.format("%1$tF %1$tR", socatMetadata.getEndTime());

		/*
		 * try {
		 * 	databaseQCFlag = databaseHandler.getQCFlag(upperExpo).toString();
		 * } catch (SQLException ex) {
		 * 	throw new IllegalArgumentException("Problems generating \"the\" database QC flag for " +
		 * 			upperExpo + ": " + ex.getMessage());
		 * }
		 */

		ArrayList<QCEvent> qcEvents;
		try {
			qcEvents = databaseHandler.getQCEvents(upperExpo);
		} catch (SQLException ex) {
			throw new IllegalArgumentException("Problems reading database QC events for " +
					upperExpo + ": " + ex.getMessage());
		}
		oldExpocode = "-";
		for ( QCEvent evt : qcEvents ) {
			if ( QCEvent.QC_RENAMED_FLAG.equals(evt.getFlag()) ) {
				// Get the old expocode for this rename
				String msg = evt.getComment();
				String[] msgWords = msg.split("\\s+");
				if ( ! ( (msgWords.length >= 5) &&
						"Rename".equalsIgnoreCase(msgWords[0]) &&
						"from".equalsIgnoreCase(msgWords[1]) &&
						"to".equalsIgnoreCase(msgWords[3]) ) )
					throw new IllegalArgumentException("Unexpected comment for rename: " + msg);
				if ( upperExpo.equals(msgWords[4]) )
					oldExpocode = msgWords[2];
			}
		}

		out.println(
			upperExpo + "\t" +
			dsgQCFlag + "\t" +
			socatVersionStatus + "\t" +
			oldExpocode + "\t" +
			numRows + "\t" +
			numOkayRows + "\t" +
			numWarnRows + "\t" +
			numErrRows + "\t" +
			numMissRows + "\t" +
			regions + "\t" +
			westmost + "\t" +
			eastmost + "\t" +
			southmost + "\t" + 
			northmost + "\t" +
			firsttime + "\t" +
			lasttime + "\t" +
			vesselName + "\t" +
			pis);
	}

	/**
	 * Tab-separated data column names used by {@link #printSummaryHeader(PrintWriter)}
	 * for the data Strings printed by {@link #printCruiseSummary(String, PrintWriter)}.
	 */
	private static final String CRUISE_SUMMARY_HEADER = 
			"Expocode\t" +
			"QC_Flag\t" +
			"SOCAT_Version\t" +
			"Renamed_From\t" +
			"Num_Observations\t" +
			"Num_WOCE-2_fCO2Rec\t" +
			"Num_WOCE-3_fCO2Rec\t" +
			"Num_WOCE-4_fCO2Rec\t" +
			"Num_Missing_fCO2Rec\t" +
			"Regions\t" +
			"Westmost\t" +
			"Eastmost\t" +
			"Southmost\t" + 
			"Northmost\t" +
			"Firsttime\t" + 
			"Lasttime\t" + 
			"Vessel\t" +
			"Investigators";

	/**
	 * Prints the summary header to the given PrintStream
	 * @param out
	 * 		print the summary to here
	 */
	public void printSummaryHeader(PrintWriter out) {
		out.println(CRUISE_SUMMARY_HEADER);
	}

	/**
	 * Tab-separated data column names for the data printed by 
	 * {@link #generateDataFileForGrids(TreeSet, File)}
	 */
	private static final String GENERATE_DATA_FILE_FOR_GRIDS_HEADER = 
			"data_id\t" +
			"latitude\t" +
			"longitude\t" +
			"datetime\t" +
			"expocode\t" + 
			"fCO2rec";
	/**
	 * Print the data needed to generate the gridded-data NetCDF files.
	 * Only WOCE-2 data with valid fCO2rec values are printed for the
	 * given datasets.  Data is printed in order of expocodes as they
	 * are given and the in increasing time order.  Only one copy of 
	 * any data points in a dataset with identical valid values for 
	 * latitude, longitude, time, fCO2rec, and WOCE flag are printed. 
	 * 
	 * @param expocodes
	 * 		use the data for the datasets with these expocodes
	 * @param outputFile
	 * 		print the data to this File
	 * @throws IllegalArgumentException
	 * 		if an expocode is invalid, or 
	 * 		if the full-data DSG file for a dataset is invalid
	 * @throws IOException
	 * 		if creating or writing to the output file throws one, or
	 * 		if reading from a DSG file throws one
	 */
	public void generateDataFileForGrids(TreeSet<String> expocodes, File outputFile) 
			throws IllegalArgumentException, IOException {
		PrintWriter report = new PrintWriter(outputFile);
		report.println(GENERATE_DATA_FILE_FOR_GRIDS_HEADER);
		long dataID = 0L;
		try {
			// Read and report the data for one cruise at a time
			for ( String expo : expocodes ) {
				// Read the data for this cruise
				String upperExpo = DashboardServerUtils.checkExpocode(expo);
				CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
				ArrayList<String> unknownVars = dsgFile.readData(knownDataFileTypes);
				if ( unknownVars.size() > 0 ) {
					String msg = upperExpo + " unassigned data variables: ";
					for (String var : unknownVars)
						msg += var + "; ";
					throw new IllegalArgumentException(msg);
				}
				// Get the computed values of time in seconds since 1970-01-01 00:00:00
				double[] sectimes = dsgFile.readDoubleVarDataValues(KnownDataTypes.TIME.getVarName());
				// Collect and sort the acceptable data for this cruise
				// Any duplicates are eliminated in this process
				TreeSet<DataPoint> datSet = new TreeSet<DataPoint>();
				int j = -1;
				for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
					j++;
					Double fco2rec = dataVals.getfCO2Rec();
					if ( DashboardUtils.FP_MISSING_VALUE.equals(fco2rec) )
						continue;
					Character woceFlag = dataVals.getWoceCO2Water();
					if ( woceFlag.equals(WoceEvent.WOCE_GOOD) || 
						 woceFlag.equals(WoceEvent.WOCE_NOT_CHECKED) ) {
						DataPoint datpt = new DataPoint(upperExpo, sectimes[j], 
								dataVals.getLatitude(), dataVals.getLongitude(), fco2rec);
						if ( ! datSet.add(datpt) ) {
							System.err.println("Ignored duplicate datapoint for " + upperExpo + ": " + datpt.toString());
						}
					}
				}
				// Print the sorted data for this cruise
				for ( DataPoint datPt : datSet ) {
					dataID++;
					String datetime = DATETIMESTAMPER.format(datPt.datetime);
					report.format("%d\t%.6f\t%.6f\t%s\t%s\t%.6f\n", 
							Long.valueOf(dataID), datPt.latitude, datPt.longitude, 
							datetime, upperExpo, datPt.fco2rec);
				}
			}
		} finally {
			report.close();
		}
	}

	/**
	 * Print a count of valid fCO2_rec data points in a collection 
	 * of data sets.  Data points with a WOCE-3 or WOCE-4 flag, 
	 * as well as data points without fCO2_rec are ignored.
	 * Data point counts reported are grouped by year and QC flag.
	 * 
	 * @param expocodes
	 * 		print a summary of the data for data sets with these expocodes
	 * @param regionID
	 * 		consider only data in the region with this ID; 
	 * 		if null, no region restriction is made on the data
	 * @param reportFile
	 * 		print the report to this file
	 * @return
	 * 		list of warnings about the generated report;
	 * 		never null but may be empty
	 * @throws IllegalArgumentException
	 * 		if an expocode is invalid,
	 * @throws IOException 
	 * 		if unable to read a DSG NC file, or
	 * 		if unable to create the cruise report file
	 */
	public ArrayList<String> printDataCounts(TreeSet<String> expocodes, Character regionID, 
			File reportFile) throws IllegalArgumentException, IOException {
		// Map with Year + QC Flag as the keys, and number of data points as the values. 
		TreeMap<String,Integer> counts = new TreeMap<String,Integer>();
		// List of warning messages to return
		ArrayList<String> warnMsgs = new ArrayList<String>();

		PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
		try {
			if ( regionID != null )
				report.println(DataLocation.REGION_NAMES.get(regionID) + " region data point counts for data sets:");
			else
				report.println("Global data point counts for data sets:");
			int k = 0;
			for ( String expo : expocodes ) {
				String upperExpo = DashboardServerUtils.checkExpocode(expo);
				report.print("\t" + expo);
				k++;
				if ( ( k % 5 ) == 0 )
					report.println();
				CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
				ArrayList<String> unknownVars = dsgFile.readMetadata(knownMetadataTypes);
				if ( unknownVars.size() > 0 ) {
					String msg = upperExpo + " unknown metadata variables: ";
					for (String var : unknownVars)
						msg += var + "; ";
					warnMsgs.add(msg);
				}
				// QC flag part of the map key string
				String qcFlag = "\t" + dsgFile.getMetadata().getQcFlag();

				unknownVars = dsgFile.readData(knownDataFileTypes);
				if ( unknownVars.size() > 0 ) {
					String msg = upperExpo + " unknown data variables: ";
					for (String var : unknownVars)
						msg += var + "; ";
					warnMsgs.add(msg);
				}
				for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
					// Check that fCO2_rec is given
					if ( DashboardUtils.FP_MISSING_VALUE.equals(dataVals.getfCO2Rec()) )
						continue;
					// Check WOCE flag for fCO2_rec
					Character woceFlag = dataVals.getWoceCO2Water();
					if ( ! ( WoceEvent.WOCE_GOOD.equals(woceFlag) || 
							WoceEvent.WOCE_NOT_CHECKED.equals(woceFlag) ) )
						continue;
					// Check region, if appropriate
					if ( (regionID != null) && 
							( ! regionID.equals(dataVals.getRegionID()) ) )
						continue;
					// Increment the count for this year + QC flag.  This could be made 
					// much more efficient since year is monotonically increasing, but 
					// since this method is rarely called, it is not worth the effort.
					String key = dataVals.getYear().toString() + qcFlag;
					Integer value = counts.get(key);
					if ( value == null ) {
						value = 1;
					}
					else {
						value++;
					}
					counts.put(key, value);
				}
			}

			if ( ( k % 5 ) != 0 )
				report.println();
			report.println();

			// Print out all the counts in the map (ordered by year then QC flag)
			for ( Entry<String,Integer> entry : counts.entrySet() ) {
				report.println(entry.getKey() + "\t" + entry.getValue().toString());
			}
		} finally {
			report.close();
		}

		return warnMsgs;
	}

}
