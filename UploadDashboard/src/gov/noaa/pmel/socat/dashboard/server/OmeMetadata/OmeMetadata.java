/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server.OmeMetadata;

import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains user-provided values needed by the SOCAT 
 * database.  
 *  
 * @author Karl Smith
 */
public class OmeMetadata extends DashboardMetadata {

	private static final long serialVersionUID = 5352480159918565845L;

	private static final SimpleDateFormat DATE_PARSER = 
			new SimpleDateFormat("yyyyMMdd HH:mm");
	private static final SimpleDateFormat DATE_FORMATTER =
			new SimpleDateFormat("yyyyMMdd");
	static {
		TimeZone gmtTz = TimeZone.getTimeZone("GMT");
		DATE_PARSER.setTimeZone(gmtTz);
		DATE_FORMATTER.setTimeZone(gmtTz);
	}

	// data values from the OME metadata 
	
	/*
	 * The following are inherited from DashboardMetadata:
	 * 
	 * 	expocode
	 *  filename
	 *  uploadTimestamp
	 *  owner
	 *  
	 *  The inherited Owner maps to <User><Name> in the OME XML.
	 */
	
	// <User>
	private OMEVariable userName = null;
	private OMEVariable userOrganization = null;
	private OMEVariable userAddress = null;
	private OMEVariable userPhone = null;
	private OMEVariable userEmail = null;
	
	// <Investigator>
	private List<OMECompositeVariable> investigators = new ArrayList<OMECompositeVariable>();
	
	// <Dataset Info>
	private OMEVariable datasetID = null;
	private OMEVariable fundingInfo = null;
	
	// <DatasetInfo><Submission_Dates>
	private OMEVariable initialSubmission = null;
	private OMEVariable revisedSubmission = null;
	
	// <Cruise_Info><Experiment>
	private OMEVariable experimentName = null;
	private OMEVariable experimentType = null;
	private OMEVariable platformType = null;
	private OMEVariable co2InstrumentType = null;
	private OMEVariable mooringId = null;
	
	// <Cruise_Info><Experiment><Cruise>
	private OMEVariable cruiseID = null;
	private OMEVariable cruiseInfo = null;
	private OMEVariable section = null;

	// These two come after Temporal_Coverage in the XML
	private OMEVariable cruiseStartDate = null;
	private OMEVariable cruiseEndDate = null;
	
	// Cruise_Info><Experiment><Cruise><Geographical_Coverage>
	private OMEVariable geographicalRegion = null;
	
	// <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
	private OMEVariable westmostLongitude = null;
	private OMEVariable eastmostLongitude = null;
	private OMEVariable northmostLatitude = null;
	private OMEVariable southmostLatitude = null;
	
	// <Cruse_Info><Experiment><Cruise><Temporal_Coverage>
	private OMEVariable temporalCoverageStartDate = null;
	private OMEVariable temporalCoverageEndDate = null;
	
	
	// <Cruise_Info><Vessel>
	private OMEVariable vesselName = null;
	private OMEVariable vesselID = null;
	private OMEVariable country = null;
	private OMEVariable vesselOwner = null;
	
	// <Variables_Info>
	List<OMECompositeVariable> variablesInfo = new ArrayList<OMECompositeVariable>();
	
	// Units stuff: <CO2_Data_Info><xxx><Unit>
	private OMEVariable xCO2WaterEquDryUnit = null;
	private OMEVariable xCO2WaterSSTDryUnit = null;
	private OMEVariable pCO2WaterEquWetUnit = null;
	private OMEVariable pCO2WaterSSTWetUnit = null;
	private OMEVariable fCO2WaterEquWetUnit = null;
	private OMEVariable fCO2WaterSSTWetUnit = null;
	private OMEVariable xCO2AirDryUnit = null;
	private OMEVariable pCO2AirWetUnit = null;
	private OMEVariable fCO2AirWetUnit = null;
	private OMEVariable xCO2AirDryInterpolatedUnit = null;
	private OMEVariable pCO2AirWetInterpolatedUnit = null;
	private OMEVariable fCO2AirWetInterpolatedUnit = null;
	
	// <Method_Description><Equilibrator_Design>
	private OMEVariable depthOfSeaWaterIntake = null;
	private OMEVariable locationOfSeaWaterIntake = null;
	private OMEVariable equilibratorType = null;
	private OMEVariable equilibratorVolume = null;
	private OMEVariable waterFlowRate = null;
	private OMEVariable headspaceGasFlowRate = null;
	private OMEVariable vented = null;
	private OMEVariable dryingMethodForCO2InWater = null;
	private OMEVariable equAdditionalInformation = null;
	
	// <Method_Description><CO2_in_Marine_Air>
	private OMEVariable co2InMarineAirMeasurement = null;
	private OMEVariable co2InMarineAirLocationAndHeight = null;
	private OMEVariable co2InMarineAirDryingMethod = null;
	
	// <Method_Description><CO2_Sensors><CO2_Sensor>
	private OMEVariable co2MeasurementMethod = null;
	private OMEVariable co2Manufacturer = null;
	private OMEVariable co2Model = null;
	private OMEVariable co2Frequency = null;
	private OMEVariable co2ResolutionWater = null;
	private OMEVariable co2UncertaintyWater = null;
	private OMEVariable co2ResolutionAir = null;
	private OMEVariable co2UncertaintyAir = null;
	private OMEVariable co2ManufacturerOfCalibrationGas = null;
	private OMEVariable co2SensorCalibration = null;
	private OMEVariable co2EnvironmentalControl = null;
	private OMEVariable co2MethodReferences = null;
	private OMEVariable detailsOfCO2Sensing = null;
	private OMEVariable analysisOfCO2Comparison = null;
	private OMEVariable measuredCO2Params = null;
	
	// <Method_Description><Sea_Surface_Temperature>
	private OMEVariable sstLocation = null;
	private OMEVariable sstManufacturer = null;
	private OMEVariable sstModel = null;
	private OMEVariable sstAccuracy = null;
	private OMEVariable sstPrecision = null;
	private OMEVariable sstCalibration = null;
	private OMEVariable sstOtherComments = null;
	
	// <Method_Description><Equilibrator_Temperature>
	private OMEVariable eqtLocation = null;
	private OMEVariable eqtManufacturer = null;
	private OMEVariable eqtModel = null;
	private OMEVariable eqtAccuracy = null;
	private OMEVariable eqtPrecision = null;
	private OMEVariable eqtCalibration = null;
	private OMEVariable eqtWarming = null;
	private OMEVariable eqtOtherComments = null;

	// <Method_Description><Equilibrator_Pressure>
	private OMEVariable eqpLocation = null;
	private OMEVariable eqpManufacturer = null;
	private OMEVariable eqpModel = null;
	private OMEVariable eqpAccuracy = null;
	private OMEVariable eqpPrecision = null;
	private OMEVariable eqpCalibration = null;
	private OMEVariable eqpOtherComments = null;
	private OMEVariable eqpNormalized = null;
	
	// <Method_Description><Atmospheric_Pressure>
	private OMEVariable atpLocation = null;
	private OMEVariable atpManufacturer = null;
	private OMEVariable atqpModel = null;
	private OMEVariable atpAccuracy = null;
	private OMEVariable atpPrecision = null;
	private OMEVariable atpCalibration = null;
	private OMEVariable atpOtherComments = null;
	
	// <Method_Description><Sea_Surface_Salinity>
	private OMEVariable sssLocation = null;
	private OMEVariable sssManufacturer = null;
	private OMEVariable sssModel = null;
	private OMEVariable sssAccuracy = null;
	private OMEVariable sssPrecision = null;
	private OMEVariable sssCalibration = null;
	private OMEVariable sssOtherComments = null;
	
	// <Method_Description><Other_Sensors>
	private List<OMECompositeVariable> otherSensors = new ArrayList<OMECompositeVariable>();
	
	// Root element
	private OMEVariable dataSetReferences = null;
	private OMEVariable additionalInformation = null;
	private OMEVariable citation = null;
	private OMEVariable measurementAndCalibrationReport = null;
	private OMEVariable preliminaryQualityControl = null;
	
	private List<OMECompositeVariable> dataSetLinks = new ArrayList<OMECompositeVariable>();
	
	private OMEVariable status = null;
	private OMEVariable form_type = null;
	private OMEVariable recordID = null;
	

	/**
	 * Creates an empty OME metadata document; 
	 * only the standard OME filename is assigned.
	 */
	public OmeMetadata() {
		super();
		filename = OME_FILENAME;
	}

	/**
	 * Creates from the contents of the OME XML file specified in the 
	 * DashboardMetadata given. 
	 * 
	 * @param mdata
	 * 		OME XML file to read.  The expocode, filename, upload timestamp 
	 * 		and owner are copied from this object, and the file specified is 
	 * 		read to populate the fields of this object.
	 * @throws IllegalArgumentException
	 * 		if mdata is null, or
	 * 		if the information in the DashboardMetadata is invalid, or
	 * 		if the contents of the metadata document are not valid
	 */
	public OmeMetadata(DashboardMetadata mdata) {
		// Initialize to an empty OME metadata document with the standard OME filename
		this();

		if ( mdata == null )
			throw new IllegalArgumentException("No metadata file given");

		// Copy the expocode, uploadTimestamp, and owner 
		// from the given DashboardMetadata object
		expocode = mdata.getExpocode();
		uploadTimestamp = mdata.getUploadTimestamp();
		owner = mdata.getOwner();

		// Read the metadata document as an XML file
		MetadataFileHandler mdataHandler;
		try {
			mdataHandler = DashboardDataStore.get().getMetadataFileHandler();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the metadata handler");
		}
		File mdataFile = mdataHandler.getMetadataFile(expocode, mdata.getFilename());
		Document omeDoc;
		try {
			omeDoc = (new SAXBuilder()).build(mdataFile);
		} catch (JDOMException | IOException ex ) {
			throw new IllegalArgumentException("Problems interpreting " +
					"the OME XML contents in " + mdataFile.getPath() + 
					":\n    " + ex.getMessage());
		}

		// Verify expocode and assign from the OME XML contents
		try {
			assignFromOmeXmlDoc(omeDoc);
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalArgumentException(
					ex.getMessage() + " in " + mdataFile.getPath(), ex);
		}
	}

	/**
	 * Validates that the expocode given for this metadata object matches the 
	 * expocode given in the given OME XML document, then assigns the fields
	 * in this object from this document.
	 * 
	 * @param omeDoc
	 * 		OME XML Document to use
	 */
	public void assignFromOmeXmlDoc(Document omeDoc) {

		
		Element rootElem = omeDoc.getRootElement();
		Path rootPath = new Path(rootElem.getName());

		/*
		 * First we extract the EXPO Code, which is the Cruise_ID. If we don't have this
		 * then we can't get anywhere.
		 * 
		 * This is the only element that's accessed out of order.
		 * All the others are done in the order they appear in the XML.
		 */
		Element cruiseInfoElem = rootElem.getChild("Cruise_Info");
		Path cruiseInfoPath = new Path(rootPath, "Cruise_Info");
		if ( cruiseInfoElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info element in the OME XML contents");

		Element experimentElem = cruiseInfoElem.getChild("Experiment");
		Path experimentPath = new Path(cruiseInfoPath, "Experiment");
		if ( experimentElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment element in the OME XML contents");
		
		Element cruiseElem = experimentElem.getChild("Cruise");
		Path cruisePath = new Path(experimentPath, "Cruise");
		if ( cruiseElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment->Cruise " +
					"element in the OME XML contents");
		
		String cruiseIDText = cruiseElem.getChildTextTrim("Cruise_ID");
		if ( cruiseIDText == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment->Cruise->Cruise_ID " +
					"element in the OME XML contents");
		
		if ( expocode.length() == 0) {
			expocode = cruiseIDText.toUpperCase();
		} else if ( ! expocode.equals(cruiseIDText.toUpperCase()) )
			throw new IllegalArgumentException("Expocode of cruise (" + 
					expocode + ") does not match that the Cruise ID in " +
					"the OME document (" + cruiseID + ")");
		
		cruiseID = new OMEVariable(new Path(cruisePath, "Cruise_ID"), cruiseIDText);
		
		/*
		 * So now we've got the EXPO code (aka Cruise_ID), we can extract everything else.
		 * We don't care if anything is missing, and we assume everything is a String.
		 * 
		 * Elements can always be missing from the XML, in which case getChild will return null.
		 * This is handled automatically by the methods that build the variable objects, so you won't see
		 * many null checks here!
		 */
		
		// <User>
		Element userElem = rootElem.getChild("User");
		Path userPath = new Path(rootPath, "User");
			
		userName = new OMEVariable(userPath, userElem, "Name");
		userOrganization = new OMEVariable(userPath, userElem, "Organization");
		userAddress = new OMEVariable(userPath, userElem, "Address");
		userPhone = new OMEVariable(userPath, userElem, "Phone");
		userEmail = new OMEVariable(userPath, userElem, "Email");
		
		// End <User>

		// <Investigator> (repeating element)
		Path investigatorPath = new Path(rootPath, "Investigator");
		for (Element invElem : rootElem.getChildren("Investigator")) {
			
			OMECompositeVariable invDetails = new OMECompositeVariable(investigatorPath, "Email");
			invDetails.addValue("Name", invElem);
			invDetails.addValue("Organization", invElem);
			invDetails.addValue("Address", invElem);
			invDetails.addValue("Phone", invElem);
			invDetails.addValue("Email", invElem);
			
			investigators.add(invDetails);
		}
		// End <Investigator>
		
		// <DataSet_Info>
		Element dataSetInfoElem = rootElem.getChild("Dataset_Info");
		
		Path dataSetInfoPath = new Path(rootPath, "Dataset_Info");
		
		datasetID = new OMEVariable(dataSetInfoPath, dataSetInfoElem, "Dataset_ID");
		fundingInfo = new OMEVariable(dataSetInfoPath, dataSetInfoElem, "Funding_Info");
		
		// <DataSet_Info><Submission_Dates>
		Element submissionDatesElem = null;
		if (null != dataSetInfoElem) {
			submissionDatesElem = dataSetInfoElem.getChild("Submission_Dates");
		}
		Path submissionDatesPath = new Path(dataSetInfoPath, "Submission_Dates");
		
		initialSubmission = new OMEVariable(submissionDatesPath, submissionDatesElem, "Initial_Submission");
		revisedSubmission = new OMEVariable(submissionDatesPath, submissionDatesElem, "Revised_Submission");

		// End <DataSet_Info></Submission_Dates<
		
		// End <DataSet_Info>
		
		// <Cruise_Info>
		// <Cruise_Info><Experiment>
		
		// The Cruise_Info and Experiment elements were created above to get the EXPO code
		// We know they exist, otherwise we wouldn't have got this far.
		
		experimentName = new OMEVariable(experimentPath, experimentElem, "Experiment_Name");
		experimentType = new OMEVariable(experimentPath, experimentElem, "Experiment_Type");
		platformType = new OMEVariable(experimentPath, experimentElem, "Platform_Type");
		co2InstrumentType = new OMEVariable(experimentPath, experimentElem, "Co2_Intstrument_type");
		mooringId = new OMEVariable(experimentPath, experimentElem, "Mooring_ID");
		
		// <Cruise_Info><Experiment><Cruise>
		
		// CruiseID has already been assigned above
		cruiseInfo = new OMEVariable(cruisePath, cruiseElem, "Cruise_Info");
		section = new OMEVariable(cruisePath, cruiseElem, "section");
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		Element geogCoverageElem = cruiseElem.getChild("Geographical_Coverage");
		Path geogCoveragePath = new Path(cruisePath, "Geographical_Coverage");
		
		geographicalRegion = new OMEVariable(geogCoveragePath, geogCoverageElem, "Geographical_Region");
			
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		if (null != geogCoverageElem) {
			Element boundsElem = geogCoverageElem.getChild("Bounds");
			Path boundsPath = new Path(geogCoveragePath, "Bounds");
			
			westmostLongitude = new OMEVariable(boundsPath, boundsElem, "Westernmost_Longitude");
			eastmostLongitude = new OMEVariable(boundsPath, boundsElem, "Easternmost_Longitude");
			northmostLatitude = new OMEVariable(boundsPath, boundsElem, "Northernmost_Latitude");
			southmostLatitude = new OMEVariable(boundsPath, boundsElem, "Southernmost_Latitude");
		}
	
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		
		// <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		Element tempCoverageElem = cruiseElem.getChild("Temporal_Coverage");
		Path tempCoveragePath = new Path(cruisePath, "Temporal_Coverage");
		
		temporalCoverageStartDate = new OMEVariable(tempCoveragePath, tempCoverageElem, "Start_Date");
		temporalCoverageEndDate = new OMEVariable(tempCoveragePath, tempCoverageElem, "End_Date");
		// End <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		
		cruiseStartDate = new OMEVariable(cruisePath, cruiseElem, "Start_Date");
		cruiseEndDate = new OMEVariable(cruisePath, cruiseElem, "End_Date");
		
		// End <Cruise_Info><Experiment><Cruise>
		
		// End <Cruise_Info><Experiment>
		
		// <Cruise_Info><Vessel>
		
		Element vesselElem = cruiseInfoElem.getChild("Vessel");
		Path vesselPath = new Path(cruiseInfoPath, "Vessel");
		
		vesselName = new OMEVariable(vesselPath, vesselElem, "Vessel_Name");
		vesselID = new OMEVariable(vesselPath, vesselElem, "Vessel_ID");
		country = new OMEVariable(vesselPath, vesselElem, "Country");
		vesselOwner = new OMEVariable(vesselPath, vesselElem, "Vessel_Owner");
		
		// <Variables_Info>
		
		// The contents of this are a repeating sub-element, so live in a list of OMECompositeVariables.
		Element varsInfoElem = rootElem.getChild("Variables_Info");
		if (null != varsInfoElem) {
			Path varsInfoPath = new Path(rootPath, "Variables_Info");
			
			Path variablePath = new Path(varsInfoPath, "Variable");
			for (Element variableElem : rootElem.getChildren("Variable")) {
				
				OMECompositeVariable varDetails = new OMECompositeVariable(variablePath, "Variable_Name");
				varDetails.addValue("Variable_Name", variableElem);
				varDetails.addValue("Description_of_Variable", variableElem);
				
				variablesInfo.add(varDetails);
			}
		}
		
		// End <Variables_Info>
		
		// <CO2_Data_Info>
		Element co2DataInfoElem = rootElem.getChild("CO2_Data_Info");
		Path co2DataInfoPath = new Path(rootPath, "CO2_Data_Info");
		
		// If the co2DataInfoElem is null, this is handled by extractSubElement
		xCO2WaterEquDryUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "xCO2water_equ_dry", "Unit");
		xCO2WaterSSTDryUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "xCO2water_SST_dry", "Unit");
		pCO2WaterEquWetUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "pCO2water_equ_wet", "Unit");
		pCO2WaterSSTWetUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "pCO2water_SST_wet", "Unit");
		fCO2WaterEquWetUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "fCO2water_equ_wet", "Unit");
		fCO2WaterSSTWetUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "fCO2water_SST_wet", "Unit");
		xCO2AirDryUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "xCO2air_dry", "Unit");
		pCO2AirWetUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "pCO2air_wet", "Unit");
		fCO2AirWetUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "fCO2air_wet", "Unit");
		xCO2AirDryInterpolatedUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "xCO2air_dry_interpolated", "Unit");
		pCO2AirWetInterpolatedUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "pCO2air_wet_interpolated", "Unit");
		fCO2AirWetInterpolatedUnit = extractSubElement(co2DataInfoPath, co2DataInfoElem, "fCO2air_wet_interpolated", "Unit");
		
		// End <CO2_Data_Info>
		
		// <Method_Description>
		Element methodDescriptionElem = rootElem.getChild("Method_Description");
		Path methodDescriptionPath = new Path(rootPath, "Method_Description");

		// <Method_Description><Equilibrator_Design>
		Element equDesignElement = null;
		Path equDesignPath = new Path(methodDescriptionPath, "Equilibrator_Design");
		if (null != methodDescriptionElem) {
			equDesignElement = methodDescriptionElem.getChild("Equilibrator_Design");
		}

		depthOfSeaWaterIntake = new OMEVariable(equDesignPath, equDesignElement, "Depth_of_Sea_Water_Intake");
		locationOfSeaWaterIntake = new OMEVariable(equDesignPath, equDesignElement, "Location_of_Sea_Water_Intake");
		equilibratorType = new OMEVariable(equDesignPath, equDesignElement, "Equilibrator_Type");
		equilibratorVolume = new OMEVariable(equDesignPath, equDesignElement, "Equilibrator_Volume");
		waterFlowRate = new OMEVariable(equDesignPath, equDesignElement, "Water_Flow_Rate");
		headspaceGasFlowRate = new OMEVariable(equDesignPath, equDesignElement, "Headspace_Gas_Flow_Rate");
		vented = new OMEVariable(equDesignPath, equDesignElement, "Vented");
		dryingMethodForCO2InWater = new OMEVariable(equDesignPath, equDesignElement, "Drying_Method_for_CO2_in_water");
		equAdditionalInformation = new OMEVariable(equDesignPath, equDesignElement, "Additional_Information");
		
		// End <Method_Description><Equilibrator_Design>

		// <Method_Description><CO2_in_Marine_Air>
		Element co2MarineAirElem = null;
		Path co2MarineAirPath = new Path(methodDescriptionPath, "CO2_in_Marine_Air");
		if (null != methodDescriptionElem) {
			co2MarineAirElem = methodDescriptionElem.getChild("CO2_in_Marine_Air");
		}

		co2InMarineAirMeasurement = new OMEVariable(co2MarineAirPath, co2MarineAirElem, "Measurement");
		co2InMarineAirLocationAndHeight = new OMEVariable(co2MarineAirPath, co2MarineAirElem, "Location_and_Height");
		co2InMarineAirDryingMethod = new OMEVariable(co2MarineAirPath, co2MarineAirElem, "Drying_Method");
		
		// End <Method_Description><CO2_in_Marine_Air>

		// <Method_Description><CO2_Sensors>
		Element co2SensorsElem = null;
		Path co2SensorsPath = new Path(methodDescriptionPath, "CO2_Sensors");

		if (null != methodDescriptionElem) {
			co2SensorsElem = methodDescriptionElem.getChild("CO2_Sensors");
		}
		
		// <Method_Description><CO2_Sensors><CO2_Sensor>
		Element co2SensorElem = null;
		Path co2SensorPath = new Path(co2SensorsPath, "CO2_Sensor");
		if (null != co2SensorsElem) {
			co2SensorElem = co2SensorsElem.getChild("CO2_Sensor");
		}

		co2MeasurementMethod = new OMEVariable(co2SensorPath, co2SensorElem, "Measurement_Method");
		co2Manufacturer = new OMEVariable(co2SensorPath, co2SensorElem, "Manufacturer");
		co2Model = new OMEVariable(co2SensorPath, co2SensorElem, "Model");
		co2Frequency = new OMEVariable(co2SensorPath, co2SensorElem, "Frequency");
		co2ResolutionWater = new OMEVariable(co2SensorPath, co2SensorElem, "Resolution_Water");
		co2UncertaintyWater = new OMEVariable(co2SensorPath, co2SensorElem, "Uncertainty_Water");
		co2ResolutionAir = new OMEVariable(co2SensorPath, co2SensorElem, "Resolution_Air");
		co2UncertaintyAir = new OMEVariable(co2SensorPath, co2SensorElem, "Uncertainty_Air");
		co2ManufacturerOfCalibrationGas = new OMEVariable(co2SensorPath, co2SensorElem, "Manufacturer_of_Calibration_Gas");
		co2SensorCalibration = new OMEVariable(co2SensorPath, co2SensorElem, "CO2_Sensor_Calibration");
		co2EnvironmentalControl = new OMEVariable(co2SensorPath, co2SensorElem, "Environmental_Control");
		co2MethodReferences = new OMEVariable(co2SensorPath, co2SensorElem, "Method_References");
		detailsOfCO2Sensing = new OMEVariable(co2SensorPath, co2SensorElem, "Details_Co2_Sensing");
		analysisOfCO2Comparison = new OMEVariable(co2SensorPath, co2SensorElem, "Analysis_of_Co2_Comparision");
		measuredCO2Params = new OMEVariable(co2SensorPath, co2SensorElem, "Measured_Co2_Params");

		// End <Method_Description><CO2_Sensors><CO2_Sensor>
		// End <Method_Description><CO2_Sensors>
		
		// <Method_Description><Sea_Surface_Temperature>
		Element sstElem = null;
		Path sstPath = new Path(methodDescriptionPath, "Sea_Surface_Temperature");
		if (null != methodDescriptionElem) {
			sstElem = methodDescriptionElem.getChild("Sea_Surface_Temperature");
		}
		
		sstLocation = new OMEVariable(sstPath, sstElem, "Location");
		sstManufacturer = new OMEVariable(sstPath, sstElem, "Manufacturer");
		sstModel = new OMEVariable(sstPath, sstElem, "Model");
		sstAccuracy = new OMEVariable(sstPath, sstElem, "Accuracy");
		sstPrecision = new OMEVariable(sstPath, sstElem, "Precision");
		sstCalibration = new OMEVariable(sstPath, sstElem, "Calibration");
		sstOtherComments = new OMEVariable(sstPath, sstElem, "Other_Comments");
		
		// End <Method_Description><Sea_Surface_Temperature>
		
		// <Method_Description><Equilibrator_Temperature>
		Element eqtElem = null;
		Path eqtPath = new Path(methodDescriptionPath, "Equilibrator_Temperature");
		if (null != methodDescriptionElem) {
			eqtElem = methodDescriptionElem.getChild("Equilibrator_Temperature");
		}
				
		eqtLocation = new OMEVariable(eqtPath, eqtElem, "Location");
		eqtManufacturer = new OMEVariable(eqtPath, eqtElem, "Manufacturer");
		eqtModel = new OMEVariable(eqtPath, eqtElem, "Model");
		eqtAccuracy = new OMEVariable(eqtPath, eqtElem, "Accuracy");
		eqtPrecision = new OMEVariable(eqtPath, eqtElem, "Precision");
		eqtCalibration = new OMEVariable(eqtPath, eqtElem, "Calibration");
		eqtWarming = new OMEVariable(eqtPath, eqtElem, "Warming");
		eqtOtherComments = new OMEVariable(eqtPath, eqtElem, "Other_Comments");

		// End <Method_Description><Equilibrator_Temperature>
		
		// <Method_Description><Equilibrator_Pressure>
		Element eqpElem = null;
		Path eqpPath = new Path(methodDescriptionPath, "Equilibrator_Pressure");

		if (null != methodDescriptionElem) {
			eqpElem = methodDescriptionElem.getChild("Equilibrator_Pressure");
		}
				
		eqpLocation = new OMEVariable(eqpPath, eqpElem, "Location");
		eqpManufacturer = new OMEVariable(eqpPath, eqpElem, "Manufacturer");
		eqpModel = new OMEVariable(eqpPath, eqpElem, "Model");
		eqpAccuracy = new OMEVariable(eqpPath, eqpElem, "Accuracy");
		eqpPrecision = new OMEVariable(eqpPath, eqpElem, "Precision");
		eqpCalibration = new OMEVariable(eqpPath, eqpElem, "Calibration");
		eqpOtherComments = new OMEVariable(eqpPath, eqpElem, "Other_Comments");
		eqpNormalized = new OMEVariable(eqpPath, eqpElem, "Normalized");
		
		// End <Method_Description><Equilibrator_Pressure>
		
		// <Method_Description><Atmospheric_Pressure>
		Element atpElem = null;
		Path atpPath = new Path(methodDescriptionPath, "Atmospheric_Pressure");
		if (null != methodDescriptionElem) {
			atpElem = methodDescriptionElem.getChild("Atmospheric_Pressure");
		}
				
		atpLocation = new OMEVariable(atpPath, atpElem, "Location");
		atpManufacturer = new OMEVariable(atpPath, atpElem, "Manufacturer");
		atqpModel = new OMEVariable(atpPath, atpElem, "Model");
		atpAccuracy = new OMEVariable(atpPath, atpElem, "Accuracy");
		atpPrecision = new OMEVariable(atpPath, atpElem, "Precision");
		atpCalibration = new OMEVariable(atpPath, atpElem, "Calibration");
		atpOtherComments = new OMEVariable(atpPath, atpElem, "Other_Comments");

		// End <Method_Description><Atmospheric_Pressure>
		
		// <Method_Description><Sea_Surface_Salinity>
		Element sssElem = null;
		Path sssPath = new Path(methodDescriptionPath, "Sea_Surface_Salinity");
		if (null != methodDescriptionElem) {
			sssElem = methodDescriptionElem.getChild("Sea_Surface_Salinity");
		}
				
		sssLocation = new OMEVariable(sssPath, sssElem, "Location");
		sssManufacturer = new OMEVariable(sssPath, sssElem, "Manufacturer");
		sssModel = new OMEVariable(sssPath, sssElem, "Model");
		sssAccuracy = new OMEVariable(sssPath, sssElem, "Accuracy");
		sssPrecision = new OMEVariable(sssPath, sssElem, "Precision");
		sssCalibration = new OMEVariable(sssPath, sssElem, "Calibration");
		sssOtherComments = new OMEVariable(sssPath, sssElem, "Other_Comments");

		// End <Method_Description><Sea_Surface_Salinity>
		
		// <Method_Description><Other_Sensors>
		Element otherSensorsElem = null;
		Path otherSensorsPath = new Path(methodDescriptionPath, "Other_Sensors");
		if (null != methodDescriptionElem) {
			otherSensorsElem = methodDescriptionElem.getChild("Other_Sensors");
		}
		
		if (null != otherSensorsElem) {
			Path sensorPath = new Path(otherSensorsPath, "Sensor");
			for (Element sensorElem : otherSensorsElem.getChildren("Sensor")) {
				
				List<String> idList = new ArrayList<String>(2);
				idList.add("Manufacturer");
				idList.add("Model");
				OMECompositeVariable sensorDetails = new OMECompositeVariable(sensorPath, idList);
				sensorDetails.addValue("Manufacturer", sensorElem);
				sensorDetails.addValue("Accuracy", sensorElem);
				sensorDetails.addValue("Model", sensorElem);
				sensorDetails.addValue("Resolution", sensorElem);
				sensorDetails.addValue("Calibration", sensorElem);
				sensorDetails.addValue("Other_Comments", sensorElem);
				
				otherSensors.add(sensorDetails);
			}
		}
		
		// End <Method_Description><Other_Sensors>
		// End <Method_Description>
		
		
		// Miscellaneous tags under the root element
		
		dataSetReferences = new OMEVariable(rootPath, rootElem, "Data_set_References");
		additionalInformation = new OMEVariable(rootPath, rootElem, "Additional_Information");
		citation = new OMEVariable(rootPath, rootElem, "Citation");
		measurementAndCalibrationReport = new OMEVariable(rootPath, rootElem, "Measurement_and_Calibration_Report");
		preliminaryQualityControl = new OMEVariable(rootPath, rootElem, "Preliminary_Quality_control");
		
		// <Data_Set_Link>s
		Path dataSetLinkPath = new Path(rootPath, "Data_Set_Link");
		for (Element dataSetLinkElem : rootElem.getChildren("Data_Set_Link")) {
			
			OMECompositeVariable dataSetLinkDetails = new OMECompositeVariable(dataSetLinkPath, "URL");
			dataSetLinkDetails.addValue("URL", dataSetLinkElem);
			dataSetLinkDetails.addValue("Label", dataSetLinkElem);
			dataSetLinkDetails.addValue("Link_Note", dataSetLinkElem);
			
			dataSetLinks.add(dataSetLinkDetails);
		}
		
		// More miscellaneous root tags
		status = new OMEVariable(rootPath, rootElem, "status");
		form_type = new OMEVariable(rootPath, rootElem, "form_type");
		recordID = new OMEVariable(rootPath, rootElem, "record_id");
		
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
		
		Element rootElem = new Element("x_tags");
		
		// <User>
		Element userElem = new Element("User");
		userElem.addContent(userName.getElement());
		userElem.addContent(userOrganization.getElement());
		userElem.addContent(userAddress.getElement());
		userElem.addContent(userPhone.getElement());
		userElem.addContent(userEmail.getElement());

		rootElem.addContent(userElem);
		// End <User>
		
		// <Investigator> (multiple)
		for (OMECompositeVariable investigator : investigators) {
			Element invElem = new Element("Investigator");
			while (investigator.hasMoreValues()) {
				invElem.addContent(investigator.getNextValueElement());
			}
			rootElem.addContent(invElem);
		}

		// End <Investigator>
		
		// <Dataset_Info>
		Element datasetInfoElem = new Element("Dataset_Info");
		datasetInfoElem.addContent(datasetID.getElement());
		datasetInfoElem.addContent(fundingInfo.getElement());
		
		// <Dataset_Info><Submission_Dates>
		Element submissionElem = new Element("Submission_Dates");
		submissionElem.addContent(initialSubmission.getElement());
		submissionElem.addContent(revisedSubmission.getElement());
		datasetInfoElem.addContent(submissionElem);
		
		// End <Dataset_Info><Submission_Dates>
		
		rootElem.addContent(datasetInfoElem);
		// End <Dataset_Info>
		
		// <Cruise_Info>
		Element cruiseInfoElem = new Element("Cruise_Info");
		
		// <Cruise_Info><Experiment>
		Element experimentElem = new Element("Experiment");
		
		experimentElem.addContent(experimentName.getElement());
		experimentElem.addContent(experimentType.getElement());
		experimentElem.addContent(platformType.getElement());
		experimentElem.addContent(co2InstrumentType.getElement());
		experimentElem.addContent(mooringId.getElement());
		
		// <Cruise_Info><Experiment><Cruise>
		Element cruiseElem = new Element("Cruise");
		
		cruiseElem.addContent(cruiseID.getElement());
		cruiseElem.addContent(cruiseInfo.getElement());
		cruiseElem.addContent(section.getElement());
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		Element geoCoverageElem = new Element("Geographical_Coverage");
		
		geoCoverageElem.addContent(geographicalRegion.getElement());
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		Element boundsElem = new Element("Bounds");
		
		boundsElem.addContent(westmostLongitude.getElement());
		boundsElem.addContent(eastmostLongitude.getElement());
		boundsElem.addContent(northmostLatitude.getElement());
		boundsElem.addContent(southmostLatitude.getElement());
		
		
		geoCoverageElem.addContent(boundsElem);
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		
		cruiseElem.addContent(geoCoverageElem);
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		
		// <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		Element tempCoverageElem = new Element("Temporal_Coverage");
		
		tempCoverageElem.addContent(temporalCoverageStartDate.getElement());
		tempCoverageElem.addContent(temporalCoverageEndDate.getElement());
		
		cruiseElem.addContent(tempCoverageElem);
		// End <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		
		cruiseElem.addContent(cruiseStartDate.getElement());
		cruiseElem.addContent(cruiseEndDate.getElement());
		
		experimentElem.addContent(cruiseElem);
		// End <Cruise_Info><Experiment><Cruise>
		
		cruiseInfoElem.addContent(experimentElem);
		// End <Cruise_Info><Experiment>
		

		// <Cruise_Info><Vessel>
		Element vesselElem = new Element("Vessel");
		
		vesselElem.addContent(vesselName.getElement());
		vesselElem.addContent(vesselID.getElement());
		vesselElem.addContent(country.getElement());
		vesselElem.addContent(vesselOwner.getElement());
		
		cruiseInfoElem.addContent(vesselElem);
		// End <Cruise_Info><Vessel>
		
		
		rootElem.addContent(cruiseInfoElem);
		// End <Cruise_Info>
		

		// <Variables_Info>
		Element varsInfoElem = new Element("Variables_Info");
		for (OMECompositeVariable varInfo : variablesInfo) {
			
			// <Variables_Info><Variable>
			Element varElement = new Element("Variable");
			
			while (varInfo.hasMoreValues()) {
				varElement.addContent(varInfo.getNextValueElement());
			}
			
			varsInfoElem.addContent(varElement);
			// End <Variables_Info><Variable>
		}
		
		rootElem.addContent(varsInfoElem);
		// End <Variables_Info>
		
		
		// <CO2_Data_Info>
		Element co2DataInfoElem = new Element("CO2_Data_Info");
		
		co2DataInfoElem.addContent(buildSubElement(xCO2WaterEquDryUnit));
		co2DataInfoElem.addContent(buildSubElement(xCO2WaterSSTDryUnit));
		co2DataInfoElem.addContent(buildSubElement(pCO2WaterEquWetUnit));
		co2DataInfoElem.addContent(buildSubElement(pCO2WaterSSTWetUnit));
		co2DataInfoElem.addContent(buildSubElement(fCO2WaterEquWetUnit));
		co2DataInfoElem.addContent(buildSubElement(fCO2WaterSSTWetUnit));
		co2DataInfoElem.addContent(buildSubElement(xCO2AirDryUnit));
		co2DataInfoElem.addContent(buildSubElement(pCO2AirWetUnit));
		co2DataInfoElem.addContent(buildSubElement(fCO2AirWetUnit));
		co2DataInfoElem.addContent(buildSubElement(xCO2AirDryInterpolatedUnit));
		co2DataInfoElem.addContent(buildSubElement(pCO2AirWetInterpolatedUnit));
		co2DataInfoElem.addContent(buildSubElement(fCO2AirWetInterpolatedUnit));
		
		rootElem.addContent(co2DataInfoElem);
		// End <CO2_Data_Info>
		
		// <Method_Description>
		Element methodDescElem = new Element("Method_Description");
		
		// <Method_Description><Equilibrator_Design>
		Element eqDesignElem = new Element("Equilibrator_Design");
		
		eqDesignElem.addContent(depthOfSeaWaterIntake.getElement());
		eqDesignElem.addContent(locationOfSeaWaterIntake.getElement());
		eqDesignElem.addContent(equilibratorType.getElement());
		eqDesignElem.addContent(equilibratorVolume.getElement());
		eqDesignElem.addContent(waterFlowRate.getElement());
		eqDesignElem.addContent(headspaceGasFlowRate.getElement());
		eqDesignElem.addContent(vented.getElement());
		eqDesignElem.addContent(dryingMethodForCO2InWater.getElement());
		eqDesignElem.addContent(equAdditionalInformation.getElement());
		
		
		methodDescElem.addContent(eqDesignElem);
		// End <Method_Description><Equilibrator_Design>
		
		// <Method_Description><CO2_in_Marine_Air>
		Element co2MarineAirElem = new Element("CO2_in_Marine_Air");
		
		co2MarineAirElem.addContent(co2InMarineAirMeasurement.getElement());
		co2MarineAirElem.addContent(co2InMarineAirLocationAndHeight.getElement());
		co2MarineAirElem.addContent(co2InMarineAirDryingMethod.getElement());

		methodDescElem.addContent(co2MarineAirElem);
		// End <Method_Description><CO2_in_Marine_Air>
		
		// <Method_Description><CO2_Sensors>
		Element co2SensorsElem = new Element("CO2_Sensors");
		
		// <Method_Description><CO2_Sensors><CO2_Sensor>
		Element co2SensorElem = new Element("CO2_Sensor");
		
		co2SensorElem.addContent(co2MeasurementMethod.getElement());
		co2SensorElem.addContent(co2Manufacturer.getElement());
		co2SensorElem.addContent(co2Model.getElement());
		co2SensorElem.addContent(co2Frequency.getElement());
		co2SensorElem.addContent(co2ResolutionWater.getElement());
		co2SensorElem.addContent(co2UncertaintyWater.getElement());
		co2SensorElem.addContent(co2ResolutionAir.getElement());
		co2SensorElem.addContent(co2UncertaintyAir.getElement());
		co2SensorElem.addContent(co2ManufacturerOfCalibrationGas.getElement());
		co2SensorElem.addContent(co2SensorCalibration.getElement());
		co2SensorElem.addContent(co2EnvironmentalControl.getElement());
		co2SensorElem.addContent(co2MethodReferences.getElement());
		co2SensorElem.addContent(detailsOfCO2Sensing.getElement());
		co2SensorElem.addContent(analysisOfCO2Comparison.getElement());
		co2SensorElem.addContent(measuredCO2Params.getElement());
		
		co2SensorsElem.addContent(co2SensorElem);
		// End <Method_Description><CO2_Sensors><CO2_Sensor>
		
		methodDescElem.addContent(co2SensorsElem);
		// End <Method_Description><CO2_Sensors>
		
		
		// <Method_Description><Sea_Surface_Temperature>
		Element sstElem = new Element("Sea_Surface_Temperature");
		
		sstElem.addContent(sstLocation.getElement());
		sstElem.addContent(sstManufacturer.getElement());
		sstElem.addContent(sstModel.getElement());
		sstElem.addContent(sstAccuracy.getElement());
		sstElem.addContent(sstPrecision.getElement());
		sstElem.addContent(sstCalibration.getElement());
		sstElem.addContent(sstOtherComments.getElement());
		
		methodDescElem.addContent(sstElem);
		// End <Method_Description><Sea_Surface_Temperature>
		
		// <Method_Description><Equilibrator_Temperature>
		Element eqtElem = new Element("Equilibrator_Temperature");
		
		eqtElem.addContent(eqtLocation.getElement());
		eqtElem.addContent(eqtManufacturer.getElement());
		eqtElem.addContent(eqtModel.getElement());
		eqtElem.addContent(eqtAccuracy.getElement());
		eqtElem.addContent(eqtPrecision.getElement());
		eqtElem.addContent(eqtCalibration.getElement());
		eqtElem.addContent(eqtWarming.getElement());
		eqtElem.addContent(eqtOtherComments.getElement());
		
		methodDescElem.addContent(eqtElem);
		// End <Method_Description><Equilibrator_Temperature>
	
		// <Method_Description><Equilibrator_Pressure>
		Element eqpElem = new Element("Equilibrator_Pressure");
		
		eqpElem.addContent(eqpLocation.getElement());
		eqpElem.addContent(eqpManufacturer.getElement());
		eqpElem.addContent(eqpModel.getElement());
		eqpElem.addContent(eqpAccuracy.getElement());
		eqpElem.addContent(eqpPrecision.getElement());
		eqpElem.addContent(eqpCalibration.getElement());
		eqpElem.addContent(eqpOtherComments.getElement());
		eqpElem.addContent(eqpNormalized.getElement());
		
		methodDescElem.addContent(eqpElem);
		// End <Method_Description><Equilibrator_Pressure>

		// <Method_Description><Atmospheric_Pressure>
		Element atpElem = new Element("Atmospheric_Pressure");
		
		atpElem.addContent(atpLocation.getElement());
		atpElem.addContent(atpManufacturer.getElement());
		atpElem.addContent(atqpModel.getElement());
		atpElem.addContent(atpAccuracy.getElement());
		atpElem.addContent(atpPrecision.getElement());
		atpElem.addContent(atpCalibration.getElement());
		atpElem.addContent(atpOtherComments.getElement());
		
		methodDescElem.addContent(atpElem);
		// End <Method_Description><Atmospheric_Pressure>
		
		// <Method_Description><Sea_Surface_Salinity>
		Element sssElem = new Element("Sea_Surface_Salinity");
		
		sssElem.addContent(sssLocation.getElement());
		sssElem.addContent(sssManufacturer.getElement());
		sssElem.addContent(sssModel.getElement());
		sssElem.addContent(sssAccuracy.getElement());
		sssElem.addContent(sssPrecision.getElement());
		sssElem.addContent(sssCalibration.getElement());
		sssElem.addContent(sssOtherComments.getElement());
		
		methodDescElem.addContent(sssElem);
		// End <Method_Description><Sea_Surface_Salinity>
		
		// <Method_Description><Other_Sensors>
		Element otherSensorsElem = new Element("Other_Sensors");
		for (OMECompositeVariable sensorInfo : otherSensors) {
			
			// <Method_Description><Other_Sensors><Sensor>
			Element sensorElem = new Element("Sensor");
			
			while (sensorInfo.hasMoreValues()) {
				sensorElem.addContent(sensorInfo.getNextValueElement());
			}
			
			otherSensorsElem.addContent(sensorElem);
			// End <Method_Description><Other_Sensors><Sensor>
		}
		
		
		methodDescElem.addContent(otherSensorsElem);
		// End <Method_Description><Other_Sensors>
		
		
		rootElem.addContent(methodDescElem);
		// End <Method_Description>
		
		// Some misc root-level elements
		rootElem.addContent(dataSetReferences.getElement());
		rootElem.addContent(additionalInformation.getElement());
		rootElem.addContent(citation.getElement());
		rootElem.addContent(measurementAndCalibrationReport.getElement());
		rootElem.addContent(preliminaryQualityControl.getElement());
		
		// <Data_Set_Link> (multiple)
		for (OMECompositeVariable dataSetLink : dataSetLinks) {
			Element dataSetLinkElem = new Element("Data_Set_Link");
			while (dataSetLink.hasMoreValues()) {
				dataSetLinkElem.addContent(dataSetLink.getNextValueElement());
			}
			rootElem.addContent(dataSetLinkElem);
		}
		
		// End <Data_Set_Link>
		
		// More misc root-level elements
		rootElem.addContent(status.getElement());
		rootElem.addContent(form_type.getElement());
		rootElem.addContent(recordID.getElement());
		
		
		return new Document(rootElem);
	}

	/**
	 * Create a SocatMetadata object from the data in this object.
	 * 
	 * @param socatVersion
	 * 		SOCAT version to assign
	 * @param addlDocs
	 * 		additional documents to assign
	 * @param qcFlag
	 * 		dataset QC flag to assign
	 * @return
	 *		created SocatMetadata object 
	 */
	public SocatMetadata createSocatMetadata(Double socatVersion, 
							Set<String> addlDocs, String qcFlag) throws IllegalArgumentException {
		
		// We cannot create a SocatMetadata object if there are conflicts
		if (isConflicted()) {
			throw new IllegalArgumentException("The Metadata contains conflicts");
		}
		
		SocatMetadata scMData = new SocatMetadata();
		
		scMData.setExpocode(expocode);
		scMData.setCruiseName(experimentName.getValue());
		scMData.setVesselName(vesselName.getValue());

		try {
			scMData.setWestmostLongitude(Double.parseDouble(westmostLongitude.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("The Westernmost_Longitude entry is not numeric", e);
		}

		try {
			scMData.setEastmostLongitude(Double.parseDouble(eastmostLongitude.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("The Easternmost_Longitude entry is not numeric", e);
		}

		try {
			scMData.setSouthmostLatitude(Double.parseDouble(southmostLatitude.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("The Westernmost_Longitude entry is not numeric", e);
		}

		try {
			scMData.setNorthmostLatitude(Double.parseDouble(northmostLatitude.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("The Westernmost_Longitude entry is not numeric", e);
		}
		
		try {
			scMData.setBeginTime(DATE_PARSER.parse(temporalCoverageStartDate.getValue() + " 00:00"));
		} catch (ParseException e) {
			throw new IllegalArgumentException("The Start Date cannot be parsed", e);
		}

		try {
			scMData.setEndTime(DATE_PARSER.parse(temporalCoverageEndDate.getValue() + " 00:00"));
		} catch (ParseException e) {
			throw new IllegalArgumentException("The End Date cannot be parsed", e);
		}
		
		String firstDataSetLink = "";
		if (dataSetLinks.size() > 0) {
			OMECompositeVariable dataSetLink = dataSetLinks.get(0);
			firstDataSetLink = dataSetLink.getValue("URL");
		}
		scMData.setOrigDataRef(firstDataSetLink);
		
		StringBuffer scienceGroup = new StringBuffer();
		List<String> usedOrganizations = new ArrayList<String>(investigators.size());
		StringBuffer orgGroup = new StringBuffer();
		
		for (OMECompositeVariable investigator : investigators) {
			if (scienceGroup.length() == 0) {
				scienceGroup.append(investigator.getValue("Name"));
			} else {
				scienceGroup.append(SocatMetadata.NAMES_SEPARATOR);
				scienceGroup.append(investigator.getValue("Name"));
			}
			
			String organization = investigator.getValue("Organization");
			if (!usedOrganizations.contains(organization)) {
				usedOrganizations.add(organization);
				
				if (orgGroup.length() == 0) {
					orgGroup.append(organization);
				} else {
					orgGroup.append(SocatMetadata.NAMES_SEPARATOR);
					orgGroup.append(organization);
				}
				
			}
		}
		
		scMData.setScienceGroup(scienceGroup.toString());
		scMData.setOrganization(orgGroup.toString());

		// Add names of any ancillary documents
		String docsString = "";
		for ( String docName : addlDocs ) {
			if ( docsString.isEmpty() )
				docsString = docName;
			else
				docsString += SocatMetadata.NAMES_SEPARATOR + docName;
		}
		scMData.setAddlDocs(docsString);

		// Add SOCAT version and QC flag
		scMData.setSocatVersion(socatVersion);
		scMData.setQcFlag(qcFlag);

		return scMData;
	}
	
	
	private OMEVariable extractSubElement(Path parentPath, Element parentElement, String elementName, String subElementName) {
		Path path = new Path(parentPath, elementName);
	
		// The OMEVariable constructor is quite happy to treat the null element
		// as an empty value. So we can leave it as null here if the parent element is also null.
		Element subElement = null;
		if (null != parentElement) {
			subElement = parentElement.getChild(elementName);
		}
		
		return new OMEVariable(path, subElement, subElementName);
	}
	
	private Element buildSubElement(OMEVariable variable) {
		Path varPath = variable.getPath();
		Element elem = new Element(varPath.getParent().getElementName());
		elem.addContent(variable.getElement());
		
		return elem;
	}
}
