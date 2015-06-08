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
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * @author Karl Smith
 */
public class SocatCruiseReporter {

	private static final String SOCAT_ENHANCED_DOI_TAG = "SOCATENHANCEDDOI";
	private static final String SOCAT_ENHANCED_HREF_TAG = "SOCATENHANCEDHREF";

	// SOCAT main DOI, DOI HRef, and publication citation
	private static final String SOCAT_MAIN_DOI = "doi:10.1594/PANGAEA.811776";
	private static final String SOCAT_MAIN_DOI_HREF = "http://doi.pangaea.de/10.1594/PANGAEA.811776";
	private static final String[] SOCAT_MAIN_CITATION = {
		"B. Pfeil, A. Olsen, D. C. E. Bakker, et. al. \"A uniform, quality controlled",
		"Surface Ocean CO2 Atlas (SOCAT)\" Earth Syst. Sci. Data, 5, 125-143, 2013",
		"doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/",
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
	static {
		TIMESTAMPER.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private CruiseFileHandler cruiseHandler;
	private MetadataFileHandler metadataHandler;
	private DsgNcFileHandler dsgFileHandler;
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
		ArrayList<String> unknownVars = dsgFile.read(false);
		if ( unknownVars.size() > 0 ) {
			String msg = "Unknown variables: ";
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

		// Generate the report
		PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
		try {
			ArrayList<String> msgs = printMetadataPreamble(omeMeta, socatVersion, qcFlag, addlDocs, report);
			warnMsgs.addAll(msgs);
			printDataTableHeader(report, false);
			for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
				report.println(dataReportString(dataVals, upperExpo, socatVersion, qcFlag, false));
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
	 * 		report only data for the region with this ID; 
	 * 		if null, no region restriction is made on the data
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
	public ArrayList<String> generateReport(TreeSet<String> expocodes, Character regionID, 
			File reportFile) throws IllegalArgumentException, IOException {
		ArrayList<String> upperExpoList = new ArrayList<String>();
		ArrayList<String> socatVersionList = new ArrayList<String>();
		ArrayList<String> qcFlagList = new ArrayList<String>();
		for ( String expo : expocodes ) {
			// Get the expocodes, SOCAT version, and QC flags 
			// of the datasets to report (checking region IDs, if appropriate)
			String upperExpo = DashboardServerUtils.checkExpocode(expo);
			CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(expo);
			boolean inRegion;
			if ( regionID != null ) {
				dsgFile.read(false);
				inRegion = false;
				for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
					if ( regionID.equals(dataVals.getRegionID()) ) {
						inRegion = true;
						break;
					}
				}
			}
			else {
				dsgFile.read(true);
				inRegion = true;
			}
			if ( inRegion ) {
				SocatMetadata socatMeta = dsgFile.getMetadata();
				socatVersionList.add(socatMeta.getSocatVersion());
				qcFlagList.add(socatMeta.getQcFlag());
				upperExpoList.add(upperExpo);
			}
		}
		ArrayList<String> warnMsgs = new ArrayList<String>();

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
					socatVersionList, qcFlagList, addlDocsList, report);
			warnMsgs.addAll(msgs);
			printDataTableHeader(report, true);
			// Read and report the data for one cruise at a time
			for (int k = 0; k < upperExpoList.size(); k++) {
				String upperExpo = upperExpoList.get(k);
				String socatVersion = socatVersionList.get(k);
				String qcFlag = qcFlagList.get(k);
				CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
				ArrayList<String> unknownVars = dsgFile.read(false);
				if ( unknownVars.size() > 0 ) {
					String msg = upperExpo + " unknown variables: ";
					for (String var : unknownVars)
						msg += var + "; ";
					warnMsgs.add(msg);
				}
				for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
					if ( (regionID != null) && ! regionID.equals(dataVals.getRegionID()) )
						continue;
					if ( SocatCruiseData.FP_MISSING_VALUE.equals(dataVals.getfCO2Rec()) )
						continue;
					Character woceFlag = dataVals.getWoceCO2Water();
					if ( woceFlag.equals(SocatWoceEvent.WOCE_GOOD) || 
						 woceFlag.equals(SocatWoceEvent.WOCE_NOT_CHECKED) ) {
						report.println(dataReportString(dataVals, upperExpo, socatVersion, qcFlag, true));
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
	 * @param socatVersion
	 * 		SOCAT version to report in the preamble
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
			String socatVersion, String qcFlag, TreeSet<String> addlDocs, PrintWriter report) {
		String upperExpo = omeMeta.getExpocode();
		ArrayList<String> warnMsgs = new ArrayList<String>();

		report.println("SOCAT data report created: " + TIMESTAMPER.format(new Date()));
		report.println("Expocode: " + upperExpo);
		report.println("version: " + socatVersion);
		report.println("Cruise/Dataset Name: " + omeMeta.getCruiseName());
		report.println("Ship/Vessel Name: " + omeMeta.getVesselName());
		report.println("Principal Investigator(s): " + omeMeta.getScienceGroup());
		report.println("Reference for the original data: " + omeMeta.getOrigDataRef());
		report.println("DOI of this SOCAT-enhanced data: " + SOCAT_ENHANCED_DOI_TAG);
		report.println("    or see: " + SOCAT_ENHANCED_HREF_TAG);
		report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
		report.println("    or see: " + SOCAT_MAIN_DOI_HREF);
		report.println();

		// Additional references - add expocode suffix for clarity
		report.println("Supplemental documentation reference(s):");
		for (String filename : addlDocs) {
			report.println("    " + upperExpo + "_" + filename);
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
			warnMsgs.add("Cruise was marked as having artificial hours, minutes, and seconds");
		}
		report.println();
		report.println("Cruise QC flag: " + qcFlag + " (see below)");
		report.println();

		return warnMsgs;
	}

	/**
	 * Prints the metadata preamble for a multi-cruise report.  
	 * If successful, any warnings about the generated preamble
	 * are returned.
	 * 
	 * @param metaList
	 * 		list of metadata values to report in the preamble
	 * @param report
	 * 		print with this PrintWriter
	 * @return
	 * 		list of warnings about the generated preamble;
	 * 		never null but may be empty
	 */
	private static ArrayList<String> printMetadataPreamble(String regionName, 
			ArrayList<DashboardOmeMetadata> omeMetaList, ArrayList<String> socatVersionList, 
			ArrayList<String> qcFlagList, ArrayList<TreeSet<String>> addlDocsList, 
			PrintWriter report) {
		ArrayList<String> warnMsgs = new ArrayList<String>();

		report.println("SOCAT data report created: " + TIMESTAMPER.format(new Date()));
		report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
		report.println("    or see: " + SOCAT_MAIN_DOI_HREF);
		if ( regionName == null )
			report.println("SOCAT cruise data for the following cruises:");
		else
			report.println("SOCAT cruise data in SOCAT region \"" + 
					regionName + "\" for the following cruises:");
		report.println("Expocode\t" +
					   "version\t" +
					   "Cruise/Dataset Name\t" +
					   "Ship/Vessel Name\t" +
					   "PI(s)\t" +
					   "Original Data DOI\t" +
					   "Westmost Longitude\t" +
					   "Eastmost Longitude\t" +
					   "Southmost Latitude\t" +
					   "Northmost Latitude\t" +
					   "Start Time\t" +
					   "End Time\t" +
					   "QC Flag\t" +
					   "Additional Metadata Reference(s)");
		boolean needsFakeHoursMsg = false;
		for (int k = 0; k < omeMetaList.size(); k++) {
			DashboardOmeMetadata omeMeta = omeMetaList.get(k);
			String upperExpo = omeMeta.getExpocode();
			String socatVersion = socatVersionList.get(k);
			String qcFlag = qcFlagList.get(k);
			TreeSet<String> addlDocs = addlDocsList.get(k);

			report.print(upperExpo);
			report.print("\t");

			report.print(socatVersion);
			report.print("\t");

			report.print(omeMeta.getCruiseName());
			report.print("\t");

			report.print(omeMeta.getVesselName());
			report.print("\t");

			report.print(omeMeta.getScienceGroup());
			report.print("\t");

			report.print(omeMeta.getOrigDataRef());
			report.print("\t");

			report.print(SOCAT_ENHANCED_DOI_TAG);
			report.print("\t");

			report.print(SOCAT_ENHANCED_HREF_TAG);
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
			report.print("Note for cruise(s): ");
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
		report.println("The quality assessments given by the Cruise QC flag and fCO2rec_flag only apply");
		report.println("to the fCO2rec value.  For more information about the recomputed fCO2 value and");
		report.println("the meaning of the Cruise QC flag, fCO2rec_src, and fCO2rec_flag values, see:");
		for (int k = 0; k < SOCAT_MAIN_CITATION.length; k++)
			report.println(SOCAT_MAIN_CITATION[k]);
		if ( multicruise ) {
			report.println();
			report.println("This is a report of only cruise data points with recomputed fCO2 values");
			report.println("which were deemed acceptable (WOCE flag 2). ");
		}
		else {
			report.println();
			report.println("This is a report of all cruise data points, including those with missing");
			report.println("recomputed fCO2 values and those with a WOCE flag indicating questionable (3)");
			report.println("or bad (4) recomputed fCO2 values.");
		}
		report.println();
		report.println("The data use policy can be found at http://www.socat.info/DataUsePolicy.htm");
		if ( ! multicruise ) {
			report.println();
			report.println("All standard SOCAT version 3 data columns are reported in this file,");
			report.println("even if all values are missing ('NaN') for this cruise.");
		}
		report.println();

		if ( multicruise )
			report.println(MULTI_CRUISE_DATA_REPORT_HEADER);			
		else
			report.println(SINGLE_CRUISE_DATA_REPORT_HEADER);
	}

	/**
	 * Explanation lines for the data columns given by the header string 
	 * SINLGE_CRUISE DATA_REPORT_HEADER and data string returned by singleCruiseDataReportString().
	 */
	private static final String[] SINGLE_CRUISE_DATA_REPORT_EXPLANATIONS = {
		"Expocode: unique identifier for the cruise from which this data was obtained",
		"version: version of SOCAT where this enhanced cruise data first appears",
		"QC_Flag: Cruise QC flag",
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
		"    World Ocean Atlas 2005 (see: //http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)",
		"NCEP_SLP: sea level pressure in hectopascals interpolated from the NCEP/NCAR 40-Year",
		"    Reanalysis Project (see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)",
		"ETOPO2_depth: bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded ",
		"    Global Relief Data (see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)",
		"dist_to_land: estimated distance to major land mass in kilometers (up to 1000 km)",
		"GVCO2: atmospheric xCO2 in micromole per mole interpolated from GlobalView-CO2, 2012 ",
		"    1979-01-01 to 2012-01-01 data (see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html)",
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
	 * tab-separated data column names for the data String returned by singleCruiseDataReportString()
	 */
	private static final String SINGLE_CRUISE_DATA_REPORT_HEADER = 
			"Expocode\t" +
			"version\t" +
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
	 * MULTI_CRUISE_DATA_REPORT_HEADER and data string returned by multiCruiseDataReportString().
	 */
	private static final String[] MULTI_CRUISE_DATA_REPORT_EXPLANATIONS = {
		"Expocode: unique identifier for the cruise from which this data was obtained",
		"version: version of SOCAT where this enhanced cruise data first appears",
		"QC_Flag: Cruise QC flag",
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
		"    World Ocean Atlas 2005 (see: //http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)",
		"NCEP_SLP: sea level pressure in hectopascals interpolated from the NCEP/NCAR 40-Year",
		"    Reanalysis Project (see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)",
		"ETOPO2_depth: bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded ",
		"    Global Relief Data (see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)",
		"dist_to_land: estimated distance to major land mass in kilometers (up to 1000 km)",
		"GVCO2: atmospheric xCO2 in micromole per mole interpolated from GlobalView-CO2, 2012 ",
		"    1979-01-01 to 2012-01-01 data (see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html)",
		"fCO2rec: fCO2 in microatmospheres recomputed from the raw data (see below)",
		"fCO2rec_src: algorithm for generating fCO2rec from the raw data (0:not generated; 1-14, see below)",
		"fCO2rec_flag: WOCE flag for this fCO2rec value (2:good, 3:questionable, 4:bad, 9:not generated; see below)",
		"",
		"Missing values are indicated by 'NaN'"
	};

	/**
	 * tab-separated data column names for the data String returned by multiCruiseDataReportString()
	 */
	private static final String MULTI_CRUISE_DATA_REPORT_HEADER = 
			"Expocode\t" + 
			"version\t" +
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
	 * @param expocode
	 * 		expocode for the cruise data report string
	 * @param socatVersion
	 * 		SOCAT Version for the cruise data report string
	 * @param cruiseQCFlag
	 * 		cruise QC flag for the cruise
	 * @param multicruise
	 * 		create the multi-cruise data string
	 * 		(no original-data CO2 measurements) ?
	 * @return 
	 * 		tab-separated data values for SOCAT data reporting.
	 */
	private static String dataReportString(SocatCruiseData dataVals, 
			String expocode, String socatVersion, String cruiseQCFlag, 
			boolean multicruise) throws IllegalArgumentException {
		// Generate the string for this data point
		Formatter fmtr = new Formatter();
		fmtr.format("%s\t", expocode);
		fmtr.format("%s\t", socatVersion);
		fmtr.format("%s\t", cruiseQCFlag);

		Integer intVal = dataVals.getYear();
		if ( SocatCruiseData.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%04d\t", intVal);

		intVal = dataVals.getMonth();
		if ( SocatCruiseData.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		intVal = dataVals.getDay();
		if ( SocatCruiseData.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		intVal = dataVals.getHour();
		if ( SocatCruiseData.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		intVal = dataVals.getMinute();
		if ( SocatCruiseData.INT_MISSING_VALUE.equals(intVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%02d\t", intVal);

		Double dblVal = dataVals.getSecond();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#03.0f\t", dblVal);

		dblVal = dataVals.getLongitude();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) ) {
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
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.5f\t", dblVal);

		dblVal = dataVals.getSampleDepth();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.0f\t", dblVal);

		dblVal = dataVals.getSalinity();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getSst();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.gettEqu();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getSlp();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getpEqu();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getWoaSss();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getNcepSlp();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		dblVal = dataVals.getEtopo2Depth();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.0f\t", dblVal);

		dblVal = dataVals.getDistToLand();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.0f\t", dblVal);

		dblVal = dataVals.getGvCO2();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		if ( ! multicruise ) {
			dblVal = dataVals.getxCO2WaterTEquDry();
			if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getxCO2WaterSstDry();
			if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getpCO2WaterTEquWet();
			if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getpCO2WaterSstWet();
			if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getfCO2WaterSstWet();
			if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);

			dblVal = dataVals.getfCO2WaterSstWet();
			if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
				fmtr.format("NaN\t");
			else
				fmtr.format("%#.3f\t", dblVal);
		}

		dblVal = dataVals.getfCO2Rec();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("NaN\t");
		else
			fmtr.format("%#.3f\t", dblVal);

		// if fCO2_rec not given, always set source to zero
		intVal = dataVals.getfCO2Source();
		if ( SocatCruiseData.INT_MISSING_VALUE.equals(intVal) || 
			 SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
			fmtr.format("0\t");
		else
			fmtr.format("%d\t", intVal);

		// if fCO2_rec not given, always set to nine ("bottle not sampled");
		// otherwise if missing or zero, it is presumed to be good (two)
		Character charVal = dataVals.getWoceCO2Water();
		if ( SocatCruiseData.FP_MISSING_VALUE.equals(dblVal) )
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
	public void printCruiseSummary(String expocode, PrintStream out) 
											throws IllegalArgumentException {
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

		out.println(expocode + "\t" + 
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
			   addlDocs);
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
	 * Prints the summary header to the given PrintStream
	 * @param out
	 * 		print the summary to here
	 */
	public void printSummaryHeader(PrintStream out) {
		out.println(CRUISE_SUMMARY_HEADER);
	}

}
