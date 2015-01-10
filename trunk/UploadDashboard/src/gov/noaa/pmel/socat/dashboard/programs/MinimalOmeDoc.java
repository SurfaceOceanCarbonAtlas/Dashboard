/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * Class for generating a minimal OME XML Document for use in 
 * generating an OmeMetadata object. 
 *  
 * @author Karl Smith
 */
public class MinimalOmeDoc {

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
	static {
		DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	String expocode;
	String cruiseName;
	String vesselName;
	ArrayList<String> investigators;
	ArrayList<String> organizations;
	Double westmostLongitude;
	Double eastmostLongitude;
	Double southmostLatitude;
	Double northmostLatitude;
	Date startDate;
	Date endDate;
	String origDataRef;

	/**
	 * Creates an empty OME metadata document; 
	 * only the standard OME filename is assigned.
	 */
	public MinimalOmeDoc() {
		expocode = "";
		cruiseName = "";
		vesselName = "";
		investigators = new ArrayList<String>();
		organizations = new ArrayList<String>();
		westmostLongitude = Double.NaN;
		eastmostLongitude = Double.NaN;
		southmostLatitude = Double.NaN;
		northmostLatitude = Double.NaN;
		startDate = SocatMetadata.DATE_MISSING_VALUE;
		endDate = SocatMetadata.DATE_MISSING_VALUE;
		origDataRef = "";
	}

	/**
	 * Generated an pseudo-OME XML document that contains the contents
	 * of the fields read by {@link #assignFromOmeXmlDoc(Document)}.
	 * Fields not read by that method are not saved in the document
	 * produced by this method.
	 * 
	 * @return
	 * 		the generated pseudo-OME XML document
	 */
	public Document createMinimalOmeXmlDoc() {
		/*
		 * Example contents of the OME XML file:
		 * ------------------------------------------------
		 * <?xml version="1.0" encoding="UTF-8"?>
		 * <x_tags>
		 *   ...
		 *   <Investigator>
 		 *     <Name>Last, First M.</Name>
		 *     <Organization>...</Organization>
		 *     <Address>...</Address>
		 *     <Phone>...</Phone>
		 *     <Email>...</Email>
		 *   </Investigator>
		 *   ... (more Investigator elements) ...
		 *   <Cruise_Info>
		 *     <Experiment>
		 *       <Experiment_Name>SH1201</Experiment_Name>
		 *       <Experiment_Type>VOS Lines</Experiment_Type>
		 *       <Cruise>
		 *         <Cruise_ID>332220120220</Cruise_ID>
		 *         <Geographical_Coverage>
		 *           <Geographical_Region>North American West Coast</Geographical_Region>
		 *           <Bounds>
		 *             <Westernmost_Longitude>-125.702</Westernmost_Longitude>
		 *             <Easternmost_Longitude>-122.978</Easternmost_Longitude>
		 *             <Northernmost_Latitude>49.027</Northernmost_Latitude>
		 *             <Southernmost_Latitude>48.183</Southernmost_Latitude>
		 *           </Bounds>
		 *         </Geographical_Coverage>
		 *         <Temporal_Coverage>
		 *           <Start_Date>20120220</Start_Date>
		 *           <End_Date>20120229</End_Date>
		 *         </Temporal_Coverage>
		 *       </Cruise>
		 *     </Experiment>
		 *     <Vessel>
		 *       <Vessel_Name>Bell M. Shimada</Vessel_Name>
		 *       <Vessel_ID>3322</Vessel_ID>
		 *       <Vessel_Owner>NOAA</Vessel_Owner>
		 *     </Vessel>
		 *   </Cruise_Info>
		 *   <Variables_Info>
		 *     ... 
		 *   </Variables_Info>
		 *   <Method_Description>
		 *     ...
		 *   </Method_Description>
		 *   <Citation>...</Citation>
		 *   <Data_Set_Link>
		 *     <URL>www.pmel.noaa.gov/co2/</URL>
		 *     <Label>PMEL Underway pCO2 data</Label>
		 *   </Data_Set_Link>
		 *   <Data_Link>
		 *     <URL>SH1201.csv</URL>
		 *   </Data_Link>
		 *   <form_type>underway</form_type>
		 * </x_tags>
		 * ------------------------------------------------
		 */

		Element cruiseElem = new Element("Cruise");

		// expocode goes in <Cruise_Info><Experiment><Cruise><Cruise_ID>
		Element cruiseIdElem = new Element("Cruise_ID");
		cruiseIdElem.setText(expocode);
		cruiseElem.addContent(cruiseIdElem);

		// Bounds on longitude and latitude go in <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		Element boundsElem = new Element("Bounds");
		boolean somethingAdded = false;
		if ( ! westmostLongitude.isNaN() ) { 
			Element westElem = new Element("Westernmost_Longitude");
			westElem.setText(String.format("%#.3f", westmostLongitude));
			boundsElem.addContent(westElem);
			somethingAdded = true;
		}
		if ( ! eastmostLongitude.isNaN() ) { 
			Element eastElem = new Element("Easternmost_Longitude");
			eastElem.setText(String.format("%#.3f", eastmostLongitude));
			boundsElem.addContent(eastElem);
			somethingAdded = true;
		}
		if ( ! southmostLatitude.isNaN() ) { 
			Element eastElem = new Element("Southernmost_Latitude");
			eastElem.setText(String.format("%#.3f", southmostLatitude));
			boundsElem.addContent(eastElem);
			somethingAdded = true;
		}
		if ( ! northmostLatitude.isNaN() ) { 
			Element eastElem = new Element("Northernmost_Latitude");
			eastElem.setText(String.format("%#.3f", northmostLatitude));
			boundsElem.addContent(eastElem);
			somethingAdded = true;
		}
		if ( somethingAdded ) {
			Element geoElem = new Element("Geographical_Coverage");
			geoElem.addContent(boundsElem);
			cruiseElem.addContent(geoElem);
		}

		// Start and end date go in <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		Element timeElem = new Element("Temporal_Coverage");
		somethingAdded = false;
		if ( ! startDate.equals(SocatMetadata.DATE_MISSING_VALUE) ) {
			Element startElem = new Element("Start_Date");
			startElem.addContent(DATE_FORMATTER.format(startDate));
			timeElem.addContent(startElem);
			somethingAdded = true;
		}
		if ( ! endDate.equals(SocatMetadata.DATE_MISSING_VALUE) ) {
			Element endElem = new Element("End_Date");
			endElem.addContent(DATE_FORMATTER.format(endDate));
			timeElem.addContent(endElem);
			somethingAdded = true;
		}
		if ( somethingAdded ) {
			cruiseElem.addContent(timeElem);
		}

		Element experimentElem = new Element("Experiment");
		experimentElem.addContent(cruiseElem);

		// cruiseName goes in <Cruise_Info><Experiment><Experiment_Name>
		Element experimentNameElem = new Element("Experiment_Name");
		experimentNameElem.setText(cruiseName);
		experimentElem.addContent(experimentNameElem);

		Element cruiseInfoElem = new Element("Cruise_Info");
		cruiseInfoElem.addContent(experimentElem);

		// vesselName goes in <Cruise_Info><Vessel><Vessel_Name>
		Element vesselNameElem = new Element("Vessel_Name");
		vesselNameElem.setText(vesselName);
		Element vesselElem = new Element("Vessel");
		vesselElem.addContent(vesselNameElem);
		cruiseInfoElem.addContent(vesselElem);
		
		Element rootElem = new Element("x_tags");
		rootElem.addContent(cruiseInfoElem);

		// names in investigators and organizations go in <Name> and <Organization>
		// under separate <Investigator> elements
		for (int k = 0; k < investigators.size(); k++) {
			String piName = investigators.get(k);
			String orgName;
			try {
				orgName = organizations.get(k);
			} catch ( IndexOutOfBoundsException ex) {
				orgName = "";
			}

			Element nameElem = new Element("Name");
			nameElem.setText(piName);
			Element orgElem = new Element("Organization");
			orgElem.setText(orgName);

			Element investigatorElem = new Element("Investigator");
			investigatorElem.addContent(nameElem);
			investigatorElem.addContent(orgElem);

			rootElem.addContent(investigatorElem);
		}

		// Put origDataRef, if there is one, in <Data_Set_Link><URL>
		if ( ! origDataRef.isEmpty() ) {
			Element urlElem = new Element("URL");
			urlElem.setText(origDataRef);
			Element dataLinkElem = new Element("Data_Set_Link");
			dataLinkElem.addContent(urlElem);

			rootElem.addContent(dataLinkElem);
		}

		// Return the document created from the root element
		return new Document(rootElem);
	}

	/**
	 * @return 
	 * 		the cruise expocode; never null, but may be empty
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the cruise expocode to set; if null, an empty string is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode != null )
			this.expocode = expocode;
		else
			this.expocode = "";
	}

	/**
	 * @return 
	 * 		the cruise name; never null but could be empty
	 */
	public String getCruiseName() {
		return cruiseName;
	}

	/**
	 * @param cruiseName 
	 * 		the cruise name to set;
	 * 		if null, an empty string is assigned
	 */
	public void setCruiseName(String cruiseName) {
		if ( cruiseName == null )
			this.cruiseName = "";
		else
			this.cruiseName = cruiseName;
	}

	/**
	 * @return 
	 * 		the vessel name; never null but could be empty
	 */
	public String getVesselName() {
		return vesselName;
	}

	/**
	 * @param vesselName 
	 * 		the vessel name to set;
	 * 		if null, an empty string is assigned
	 */
	public void setVesselName(String vesselName) {
		if ( vesselName == null )
			this.vesselName = "";
		else
			this.vesselName = vesselName;
	}

	/**
	 * @return 
	 * 		the list of PIs; never null but could be empty.
	 * 		The actual list in this instance is returned.
	 */
	public ArrayList<String> getInvestigators() {
		return investigators;
	}

	/**
	 * @param investigators 
	 * 		the list of PIs to set;
	 * 		if null, an empty list is assigned
	 */
	public void setInvestigators(ArrayList<String> investigators) {
		this.investigators.clear();
		if ( investigators != null )
			this.investigators.addAll(investigators);
	}

	/**
	 * @return 
	 * 		the list of organizations/institutions;
	 * 		never null but could be empty if not assigned.
	 * 		The actual list in this instance is returned.
	 */
	public ArrayList<String> getOrganizations() {
		return organizations;
	}

	/**
	 * @param organizations 
	 * 		the list of organizations/institutions to set;
	 * 		if null, an empty list is assigned.
	 */
	public void setOrganizations(ArrayList<String> organizations) {
		this.organizations.clear();
		if ( organizations != null )
			this.organizations.addAll(organizations);
	}

	/**
	 * @return
	 * 		the west-most longitude for the cruise;
	 * 		never null could be Double.NaN if not assigned.
	 */
	public Double getWestmostLongitude() {
		return westmostLongitude;
	}

	/**
	 * @param westmostLongitude 
	 * 		the west-most longitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setWestmostLongitude(Double westmostLongitude) {
		if ( westmostLongitude == null )
			this.westmostLongitude = Double.NaN;
		else 
			this.westmostLongitude = westmostLongitude;
	}

	/**
	 * @return
	 * 		the east-most longitude for the cruise;
	 * 		never null but could be Double.NaN if not assigned.
	 */
	public Double getEastmostLongitude() {
		return eastmostLongitude;
	}

	/**
	 * @param eastmostLongitude 
	 * 		the east-most longitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setEastmostLongitude(Double eastmostLongitude) {
		if ( eastmostLongitude == null )
			this.eastmostLongitude = Double.NaN;
		else
			this.eastmostLongitude = eastmostLongitude;
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be Double.NaN if not assigned.
	 */
	public Double getSouthmostLatitude() {
		return southmostLatitude;
	}

	/**
	 * @param southmostLatitude 
	 * 		the south-most latitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setSouthmostLatitude(Double southmostLatitude) {
		if ( southmostLatitude == null )
			this.southmostLatitude = Double.NaN;
		else
			this.southmostLatitude = southmostLatitude;
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be Double.NaN if not assigned.
	 */
	public Double getNorthmostLatitude() {
		return northmostLatitude;
	}

	/**
	 * @param northmostLatitude 
	 * 		the north-most latitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setNorthmostLatitude(Double northmostLatitude) {
		if ( northmostLatitude == null )
			this.northmostLatitude = Double.NaN;
		else
			this.northmostLatitude = northmostLatitude;
	}

	/**
	 * @return
	 * 		the start date for the cruise;
	 * 		never null but could be {@link SocatMetadata#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate 
	 * 		the start date for the cruise to set;
	 * 		if null, {@link SocatMetadata#DATE_MISSING_VALUE} is assigned
	 */
	public void setStartDate(Date startDate) {
		if ( startDate == null )
			this.startDate = SocatMetadata.DATE_MISSING_VALUE;
		else 
			this.startDate = startDate;
	}

	/**
	 * @return
	 * 		the ending date for the cruise;
	 * 		never null but could be {@link SocatMetadata#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate 
	 * 		the ending date for the cruise to set;
	 * 		if null, {@link SocatMetadata#DATE_MISSING_VALUE} is assigned
	 */
	public void setEndDate(Date endDate) {
		if ( endDate == null )
			this.endDate = SocatMetadata.DATE_MISSING_VALUE;
		else 
			this.endDate = endDate;
	}

	/**
	 * @return 
	 * 		the DOI of the original cruise data; never null but could be empty
	 */
	public String getOrigDataRef() {
		return origDataRef;
	}

	/**
	 * @param origDataRef 
	 * 		the DOI of the original cruise data to set;
	 * 		if null, an empty string is assigned
	 */
	public void setOrigDataRef(String origDataRef) {
		if ( origDataRef == null )
			this.origDataRef = "";
		else
			this.origDataRef = origDataRef;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = expocode.hashCode();
		result = result * prime + cruiseName.hashCode();
		result = result * prime + vesselName.hashCode();
		result = result * prime + investigators.hashCode();
		result = result * prime + organizations.hashCode();
		result = result * prime + startDate.hashCode();
		result = result * prime + endDate.hashCode();
		result = result * prime + origDataRef.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! ( obj instanceof MinimalOmeDoc ) )
			return false;
		MinimalOmeDoc other = (MinimalOmeDoc) obj;

		if ( ! expocode.equals(other.expocode) )
			return false;
		// Date comparisons
		if ( ! startDate.equals(other.startDate) )
			return false;
		if ( ! endDate.equals(other.endDate) )
			return false;
		// String comparisons
		if ( ! cruiseName.equals(other.cruiseName) )
			return false;
		if ( ! vesselName.equals(other.vesselName) )
			return false;
		if ( ! origDataRef.equals(other.origDataRef) )
			return false;
		// ArrayList<String> comparisons
		if ( ! investigators.equals(other.investigators) )
			return false;
		if ( ! organizations.equals(other.organizations) )
			return false;
		// Double comparisons
		if ( ! DashboardUtils.longitudeCloseTo(westmostLongitude, 
				other.westmostLongitude, 0.0, 1.0E-4) )
			return false;
		if ( ! DashboardUtils.longitudeCloseTo(eastmostLongitude, 
				other.eastmostLongitude, 0.0, 1.0E-4) )
			return false;
		if ( ! DashboardUtils.closeTo(southmostLatitude, 
				other.southmostLatitude, 0.0, 1.0E-4) )
			return false;
		if ( ! DashboardUtils.closeTo(northmostLatitude, 
				other.northmostLatitude, 0.0, 1.0E-4) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "OmeMetadata[ expocode=" + expocode + 
				",\n    cruiseName=" + cruiseName + 
				",\n    vesselName=" + vesselName + 
				",\n    investigators=" + investigators.toString() +
				",\n    organizations=" + organizations.toString() + 
				",\n    westmostLongitude=" + westmostLongitude.toString() + 
				",\n    eastmostLongitude=" + eastmostLongitude.toString() + 
				",\n    southmostLatitude=" + southmostLatitude.toString() + 
				",\n    northmostLatitude=" + northmostLatitude.toString() + 
				",\n    startDate=" + startDate.toString() + 
				",\n    endDate=" + endDate.toString() + 
				",\n    origDataRef=" + origDataRef + 
				" ]";
	}

	/**
	 * Creates a DashboardOmeMetadata object from a line of tab-separated values 
	 * from Benjamin's metadata spreadsheet TSV table.  The owner of the 
	 * metadata is left empty so anyone can take possession of it.
	 * 
	 * @param colNames 
	 * 		array of column names for the metadata in dataLine
	 * @param metadataLine 
	 * 		metadata for a cruise as a string of tab-separated values
	 * @param timestamp
	 * 		upload time-stamp string to use 
	 * @return
	 * 		the OmeMetadata object assigned from the provided metadata
	 * @throws IllegalArgumentException
	 * 		if the number of metadata values does not match the number 
	 * 		of column names or if the expocode is not defined
	 */
	public static DashboardOmeMetadata createDashboardOmeMetadata(String[] colNames, 
			String metadataLine, String timestamp) {
		MinimalOmeDoc mdata = new MinimalOmeDoc();

		// Parse the metadata string - split on tabs
		String[] metaVals = metadataLine.split("\\t", -1);
		if ( metaVals.length != colNames.length ) {
			throw new IllegalArgumentException("Number of metadata values (" + 
					Integer.toString(metaVals.length) + 
					") does not match the number of column names (" + 
					Integer.toString(colNames.length) + ")");
		}

		// Directly assign the investigator and organization lists in the OmeMetadata object
		ArrayList<String> investigators = mdata.getInvestigators();
		ArrayList<String> organizations = mdata.getOrganizations();

		// Interpret the data based on the column names
		for (int k = 0; k < metaVals.length; k++) {
			metaVals[k] = metaVals[k].trim();
			// Treat blank entries the same as "NaN" entries
			if ( metaVals[k].isEmpty() )
				metaVals[k] = "NaN";
			if ( "Expocode created".equals(colNames[k]) ||
				 "Expocode".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					mdata.setExpocode(metaVals[k]);
			}
			else if ( "Cruise Label".equals(colNames[k]) ||
					  "Cruise".equals(colNames[k]) ||
					  "Cruise_name".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					mdata.setCruiseName(metaVals[k]);
			}
			else if ( "ship/platform".equals(colNames[k]) ||
					  "Vessel".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					mdata.setVesselName(metaVals[k]);
			}
			else if ( "PI".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					investigators.add(metaVals[k]);
					organizations.add("");
				}
			}
			else if ( "PI_2".equals(colNames[k]) ||
					  "PI 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					investigators.add(metaVals[k]);
					organizations.add("");
				}
			}
			else if ( "PI_3".equals(colNames[k]) ||
					  "PI 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					investigators.add(metaVals[k]);
					organizations.add("");
				}
			}
			else if ( "doi".equals(colNames[k]) ||
					  "orig_doi".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					mdata.setOrigDataRef(metaVals[k]);
			}
			// otherwise ignore the data in this column
			// longitude/latitude/time limits are derived from the data
		}

		if ( mdata.getExpocode().isEmpty() )
			throw new IllegalArgumentException("Expocode is not given");

		return new DashboardOmeMetadata(mdata.getExpocode(), timestamp, 
										mdata.createMinimalOmeXmlDoc());
	}

	/**
	 * Creates minimal OME XML Metadata files from a file of tab-separated metadata values
	 * from Benjamin's metadata spreadsheet TSV table.  The owner of these metadata files 
	 * is left empty so anyone can take possession of it.
	 * Uses the default dashboard configuration.
	 *  
	 * @param args TSVMetadata
	 * 		TSVMetadata: name of the file of tab-separated metadata values
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("\nArguments:  TSV_Metadata\n");
			System.exit(1);
		}
		DashboardDataStore dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			System.err.println("Unable to read the default dashboard configuration: " + 
					ex.getMessage());
			System.exit(1);
		}
		try {
			MetadataFileHandler mdataHandler = dataStore.getMetadataFileHandler();
			// Open the file containing the spreadsheet TSV table 
			File tsvFile = new File(args[0]);
			String timestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm"))
					.format(new Date(tsvFile.lastModified()));
			String dataline = "";
			BufferedReader tsvIn = null;
			try {
				tsvIn = new BufferedReader(new FileReader(tsvFile));
			} catch (FileNotFoundException ex) {
				System.err.println("Unable to open " + tsvFile.getPath() + 
						"\n    " + ex.getMessage());
				System.exit(1);
			}
			try {
				try {
					// Read the headers
					dataline = tsvIn.readLine();
					if ( dataline == null )
						throw new IOException("No header line given");
					String[] headers = dataline.split("\t", -1);
					if ( headers.length < 4 )
						throw new IOException("Invalid header line\n    " + dataline);
					// Get the first metadata line
					dataline = tsvIn.readLine();
					while ( dataline != null ) {
						// Get the Dashboard OME metadata from the metadata line
						DashboardOmeMetadata omeMData = createDashboardOmeMetadata(headers, dataline, timestamp);
						// Create the info file for this metadata file; not checked in.
						// This creates the parent directory if it does not exist.
						mdataHandler.saveMetadataInfo(omeMData, null);
						// Save the minimal OME XML metadata file; not checked in 
						mdataHandler.saveAsOmeXmlDoc(omeMData, null);
						// Get the next metadata line
						dataline = tsvIn.readLine();
					}
				} finally {
					tsvIn.close();
				}
			} catch (IOException ex) {
				System.err.println("IO Problems: " + ex.getMessage());
				System.exit(1);
			} catch (IllegalArgumentException ex) {
				System.err.println("Problems with: " + dataline + 
						"\n    " + ex.getMessage());
				System.exit(1);
			}
		}
		finally {
			dataStore.shutdown();
		}
		// Success
		System.exit(0);
	}

}
