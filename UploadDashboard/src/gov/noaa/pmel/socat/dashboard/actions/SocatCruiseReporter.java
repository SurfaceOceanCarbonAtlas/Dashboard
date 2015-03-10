/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

	// SOCAT main DOI, DOI HRef, and publication citation
	private static final String SOCAT_MAIN_DOI = "(SOCAT v3 unpublished data)";
	// private static final String SOCAT_MAIN_DOI = "doi:10.1594/PANGAEA.811776";
	private static final String SOCAT_MAIN_DOI_HREF = "(SOCAT v3 unpublished data)";
	// private static final String SOCAT_MAIN_DOI_HREF = "http://doi.pangaea.de/10.1594/PANGAEA.811776";
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

	private DsgNcFileHandler dsgFileHandler;

	/**
	 * For generating cruise reports from the full-data discrete
	 * sampling geometry (DSG) netCDF file provided by the given 
	 * DsgNcFileHandler.
	 * 
	 * @param dsgFileHandler
	 * 		get full-data DsgNcFiles using this file handler
	 */
	public SocatCruiseReporter(DsgNcFileHandler dsgFileHandler) {
		this.dsgFileHandler = dsgFileHandler;
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
	 * 		if unable to read the DSG NC 
	 */
	public ArrayList<String> generateReport(String expocode, File reportFile) 
								throws IllegalArgumentException, IOException {
		ArrayList<String> warnMsgs = new ArrayList<String>();
		// Get the metadata and data from the DSG file
		CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(expocode);
		ArrayList<String> unknownVars = dsgFile.read(false);
		if ( unknownVars.size() > 0 ) {
			String msg = "Unknown variables: ";
			for (String var : unknownVars)
				msg += var + "; ";
			warnMsgs.add(msg);
		}

		PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
		try {
			SocatMetadata metadata = dsgFile.getMetadata();
			metadata.correctSpellings();
			warnMsgs.addAll(printMetadataPreamble(metadata, report));
			printDataTableHeader(report, false);
			for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
				report.println(dataReportString(dataVals, metadata.getExpocode(), 
						metadata.getSocatVersion(), metadata.getSocatDOI(), 
						metadata.getQcFlag(), false));
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
	 * 		if unable to read the DSG NC 
	 */
	public ArrayList<String> generateReport(TreeSet<String> expocodes, 
			Character regionID, File reportFile) 
								throws IllegalArgumentException, IOException {
		ArrayList<String> warnMsgs = new ArrayList<String>();
		
		// Get all the metadata the DSG files (but not data lists - too large)
		ArrayList<SocatMetadata> metaList = new ArrayList<SocatMetadata>();
		for ( String expo : expocodes ) {
			CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(expo);
			dsgFile.read(true);
			// Wait until later to report all unknown variables
			SocatMetadata metadata = dsgFile.getMetadata();
			metadata.correctSpellings();
			metaList.add(metadata);
		}

		PrintWriter report = new PrintWriter(reportFile, "ISO-8859-1");
		try {
			String regionName;
			if ( regionID != null )
				regionName = DataLocation.REGION_NAMES.get(regionID);
			else
				regionName = null;
			warnMsgs.addAll(printMetadataPreamble(regionName, metaList, report));
			printDataTableHeader(report, true);
			// Read and report the data for one cruise at a time
			for ( String expo : expocodes ) {
				CruiseDsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(expo);
				ArrayList<String> unknownVars = dsgFile.read(false);
				if ( unknownVars.size() > 0 ) {
					String msg = expo + " unknown variables: ";
					for (String var : unknownVars)
						msg += var + "; ";
					warnMsgs.add(msg);
				}
				SocatMetadata metadata = dsgFile.getMetadata();
				metadata.correctSpellings();
				boolean dataFound = false;
				for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
					if ( (regionID != null) && ! regionID.equals(dataVals.getRegionID()) )
						continue;
					if ( SocatCruiseData.FP_MISSING_VALUE.equals(dataVals.getfCO2Rec()) )
						continue;
					Character woceFlag = dataVals.getWoceCO2Water();
					if ( woceFlag.equals('3') || woceFlag.equals('4') )
						continue;
					report.println(dataReportString(dataVals, metadata.getExpocode(), 
							metadata.getSocatVersion(), metadata.getSocatDOI(), 
							metadata.getQcFlag(), true));
					dataFound = true;
				}
				if ( ! dataFound ) 
					warnMsgs.add(metadata.getExpocode() + " no data found for region ID " + regionID);
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
	 * @param metadata
	 * 		metadata values to report in the preamble
	 * @param report
	 * 		print with this PrintWriter
	 * @return
	 * 		list of warnings about the generated preamble;
	 * 		never null but may be empty
	 */
	private static ArrayList<String> printMetadataPreamble(SocatMetadata metadata, PrintWriter report) {
		ArrayList<String> warnMsgs = new ArrayList<String>();

		report.println("SOCAT data report created: " + TIMESTAMPER.format(new Date()));
		report.println("Expocode: " + metadata.getExpocode());
		report.println("version: " + metadata.getSocatVersion());
		report.println("Cruise/Dataset Name: " + metadata.getCruiseName());
		report.println("Ship/Vessel Name: " + metadata.getVesselName());
		report.println("Principal Investigator(s): " + metadata.getScienceGroup());
		report.println("Reference for the original data: " + metadata.getOrigDataRef());
		report.println("DOI of this SOCAT-enhanced data: " + metadata.getSocatDOI());
		report.println("    or see: " + metadata.getSocatDOIHRef());
		report.println("DOI of the entire SOCAT collection: " + SOCAT_MAIN_DOI);
		report.println("    or see: " + SOCAT_MAIN_DOI_HREF);
		report.println();

		// Additional references
		report.println("Supplemental documentation reference(s):");
		for (String ref : metadata.getAddlDocs().split(SocatMetadata.NAMES_SEPARATOR)) {
			String name = DashboardMetadata.splitAddlDocsTitle(ref)[0];
			// TODO: this needs to be changed to an http or doi reference 
			warnMsgs.add("Supplemental documentation is not a reference: " + name);
			report.println("    " + name);
		}
		report.println();

		// Longitude range in [180W,180E]
		Double westLon = metadata.getWestmostLongitude();
		Double eastLon = metadata.getEastmostLongitude();
		if ( (westLon < -540.0) || (westLon > 540.0) ||
			 (eastLon < -540.0) || (eastLon > 540.0) ) {
			warnMsgs.add("Invalid west-most and/or east-most longitude");
		}
		else {
			if ( westLon < 0.0 )
				report.format("Longitude range: %#.2fW", -1.0 * westLon);
			else
				report.format("Longitude range: %#.2fE", westLon);
			if ( eastLon < 0.0 )
				report.format(" to %#.2fW", -1.0 * eastLon);
			else
				report.format(" to %#.2fE", eastLon);
			report.println();
		}

		// Latitude range in [90S,90N]
		Double southLat = metadata.getSouthmostLatitude();
		Double northLat = metadata.getNorthmostLatitude();
		if ( (southLat < -90.0) || (southLat > 90.0) ||
			 (northLat < -90.0) || (northLat > 90.0) ) {
			warnMsgs.add("Invalid south-most and/or north-most latitude");
		}
		else {
			if ( southLat < 0.0 )
				report.format("Latitude range: %#.2fS", -1.0 * southLat);
			else
				report.format("Latitude range: %#.2fN", southLat);
			if ( northLat < 0.0 )
				report.format(" to %#.2fS", -1.0 * northLat);
			else
				report.format(" to %#.2fN", northLat);
			report.println();
		}

		// Time range
		Date startTime = metadata.getBeginTime();
		Date endTime = metadata.getEndTime();
		if ( SocatMetadata.DATE_MISSING_VALUE.equals(startTime) || 
			 SocatMetadata.DATE_MISSING_VALUE.equals(endTime) ) {
			warnMsgs.add("Invalid start time and/or end time");
		}
		else {
			report.println("Time range: " + TIMESTAMPER.format(startTime) + 
					" to " + TIMESTAMPER.format(endTime));
		}
		// Check if this is a day-resolution cruise whose hour, minutes, and seconds 
		// were approximated and reset in the database
		if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(metadata.getExpocode()) ) {
			report.println();
			report.println("Observation times were not provided to a resolution of hours;");
			report.println("the hours, minutes, and seconds given are artificially generated values");
			warnMsgs.add("Cruise was marked as having artificial hours, minutes, and seconds");
		}
		report.println();
		report.println("Cruise QC flag: " + metadata.getQcFlag() + " (see below)");
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
			ArrayList<SocatMetadata> metaList, PrintWriter report) {
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
					   "SOCAT DOI\t" +
					   "SOCAT DOI link\t" +
					   "Westmost Longitude\t" +
					   "Eastmost Longitude\t" +
					   "Southmost Latitude\t" +
					   "Northmost Latitude\t" +
					   "Start Time\t" +
					   "End Time\t" +
					   "QC Flag\t" +
					   "Additional Metadata Reference(s)");
		boolean needsFakeHoursMsg = false;
		for ( SocatMetadata metadata : metaList ) {
			String expocode = metadata.getExpocode();
			report.print(expocode);
			report.print("\t");

			report.print(metadata.getSocatVersion());
			report.print("\t");

			report.print(metadata.getCruiseName());
			report.print("\t");

			report.print(metadata.getVesselName());
			report.print("\t");

			report.print(metadata.getScienceGroup());
			report.print("\t");

			report.print(metadata.getOrigDataRef());
			report.print("\t");

			report.print(metadata.getSocatDOI());
			report.print("\t");

			report.print(metadata.getSocatDOIHRef());
			report.print("\t");

			Double dblVal = metadata.getWestmostLongitude();
			if ( (dblVal < -540.0) || (dblVal > 540.0) ) {
				warnMsgs.add("Invalid west-most longitude for " + expocode);
			}
			else if ( dblVal < 0.0 ) {
				report.format("%#.2fW", -1.0 * dblVal);
			}
			else {
				report.format("%#.2fE", dblVal);
			}
			report.print("\t");

			dblVal = metadata.getEastmostLongitude();
			if ( (dblVal < -540.0) || (dblVal > 540.0) ) {
				warnMsgs.add("Invalid east-most longitude for " + expocode);
			}
			else if ( dblVal < 0.0 ) {
				report.format("%#.2fW", -1.0 * dblVal);
			}
			else {
				report.format("%#.2fE", dblVal);
			}
			report.print("\t");

			dblVal = metadata.getSouthmostLatitude();
			if ( (dblVal < -90.0) || (dblVal > 90.0) ) {
				warnMsgs.add("Invalid south-most latitude for " + expocode);
			}
			else if ( dblVal < 0.0 ) {
				report.format("%#.2fS", -1.0 * dblVal);
			}
			else {
				report.format("%#.2fN", dblVal);
			}
			report.print("\t");

			dblVal = metadata.getNorthmostLatitude();
			if ( (dblVal < -90.0) || (dblVal > 90.0) ) {
				warnMsgs.add("Invalid north-most latitude for " + expocode);
			}
			else if ( dblVal < 0.0 ) {
				report.format("%#.2fS", -1.0 * dblVal);
			}
			else {
				report.format("%#.2fN", dblVal);
			}
			report.print("\t");

			if ( SocatMetadata.DATE_MISSING_VALUE.equals(metadata.getBeginTime()) ) {
				warnMsgs.add("Invalid beginning time for " + expocode);
			}
			else {
				report.print(TIMESTAMPER.format(metadata.getBeginTime()));
			}
			report.print("\t");

			if ( SocatMetadata.DATE_MISSING_VALUE.equals(metadata.getEndTime()) ) {
				warnMsgs.add("Invalid ending time for " + expocode);
			}
			else {
				report.print(TIMESTAMPER.format(metadata.getEndTime()));
			}
			report.print("\t");

			report.print(metadata.getQcFlag());
			report.print("\t");

			String suppRefs = "";
			boolean first = true;
			for (String ref : metadata.getAddlDocs().split(SocatMetadata.NAMES_SEPARATOR) ) {
				String name = DashboardMetadata.splitAddlDocsTitle(ref)[0];
				// TODO: this needs to be changed to an http or doi reference 
				warnMsgs.add(expocode + " supplemental documentation is not a reference: " + name);
				if ( ! first )
					suppRefs += " ; ";
				else
					first = false;
				suppRefs += name;
			}
			report.print(suppRefs);

			report.println();

			if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(expocode) )
				needsFakeHoursMsg = true;
		}

		if ( needsFakeHoursMsg ) {
			boolean first = true;
			report.print("Note for cruise(s): ");
			for ( SocatMetadata metadata : metaList ) {
				String expocode = metadata.getExpocode();
				if ( DAY_RESOLUTION_CRUISE_EXPOCODES.contains(expocode) ) {
					if ( ! first )
						report.print("; ");
					else
						first = false;
					report.print(expocode);
					warnMsgs.add(expocode + " was marked as having artificial hours, minutes, and seconds");
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
			report.println("All standard SOCAT version 2 data columns are reported in this file,");
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
		"SOCAT_DOI: DOI for this SOCAT-enhanced cruise data",
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
	 * MULTI_CRUISE_DATA_REPORT_HEADER and data string returned by multiCruiseDataReportString().
	 */
	private static final String[] MULTI_CRUISE_DATA_REPORT_EXPLANATIONS = {
		"Expocode: unique identifier for the cruise from which this data was obtained",
		"version: version of SOCAT where this enhanced cruise data first appears",
		"SOCAT_DOI: DOI for this SOCAT-enhanced cruise data",
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
	 * @param expocode
	 * 		expocode for the cruise data report string
	 * @param socatDOI
	 * 		SOCAT DOI for the cruise data report string
	 * @param cruiseQCFlag
	 * 		cruise QC flag for the cruise
	 * @param multicruise
	 * 		create the multi-cruise data string
	 * 		(no original-data CO2 measurements) ?
	 * @return 
	 * 		tab-separated data values for SOCAT data reporting.
	 */
	private static String dataReportString(SocatCruiseData dataVals, 
			String expocode, String socatVersion, String socatDOI, 
			String cruiseQCFlag, boolean multicruise) throws IllegalArgumentException {
		// Generate the string for this data point
		Formatter fmtr = new Formatter();
		fmtr.format("%s\t", expocode);
		fmtr.format("%s\t", socatVersion);
		fmtr.format("%s\t", socatDOI);
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

}
