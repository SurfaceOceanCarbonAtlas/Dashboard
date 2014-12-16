/**
 * 
 */
package uk.ac.uea.socat.metadata.OmeMetadata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains user-provided values needed by the SOCAT 
 * database.  
 * 
 * @author Steve Jones
 * @author Karl Smith
 */
public class OmeMetadata {

	private static final long serialVersionUID = 7764440573920810989L;

	public static final String CONFLICT_STRING = "%%CONFLICT%%";

	private static final SimpleDateFormat DATE_PARSER = 
			new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	static {
		DATE_PARSER.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	/**
	 * The EXPO Code that this OmeMetadata object is related to.
	 */
	private String itsExpoCode = null;

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
	
	// <Draft>
	private boolean itIsDraft = false;
	
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
	private OMEVariable atpModel = null;
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
	
	private OMEVariable status = null;
	private OMEVariable form_type = null;
	private OMEVariable recordID = null;

	/**
	 * Creates an empty OME metadata document; 
	 * only the standard OME filename is assigned.
	 */
	public OmeMetadata(String expoCode) {
		itsExpoCode = expoCode;
	}
	
	/**
	 * Determines whether or not this OME Metadata is in draft status
	 * @return {@code true} if it is in draft status; {@code false} otherwise
	 */
	public boolean isDraft() {
		return itIsDraft;
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

		/*
		 * First we extract the EXPO Code, which is the Cruise_ID. If we don't have this
		 * then we can't get anywhere.
		 * 
		 * This is the only element that's accessed out of order.
		 * All the others are done in the order they appear in the XML.
		 */
		Element cruiseInfoElem = rootElem.getChild("Cruise_Info");
		Path cruiseInfoPath = new Path(null, "Cruise_Info");
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
		
		if ( itsExpoCode.length() == 0) {
			itsExpoCode = cruiseIDText.toUpperCase();
		} else if ( ! itsExpoCode.equals(cruiseIDText.toUpperCase()) )
			throw new IllegalArgumentException("Expocode of cruise (" + 
					itsExpoCode + ") does not match that the Cruise ID in " +
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
		
		// <Status>
		itIsDraft = false;
		Element statusElem = rootElem.getChild("status");
		if (null != statusElem) {
			String statusString = statusElem.getTextTrim();
			if (statusString.equalsIgnoreCase("draft")) {
				itIsDraft = true;
			}
		}
		
		// <User>
		Element userElem = rootElem.getChild("User");
		Path userPath = new Path(null, "User");
			
		userName = new OMEVariable(userPath, userElem, "Name");
		userOrganization = new OMEVariable(userPath, userElem, "Organization");
		userAddress = new OMEVariable(userPath, userElem, "Address");
		userPhone = new OMEVariable(userPath, userElem, "Phone");
		userEmail = new OMEVariable(userPath, userElem, "Email");
		
		// End <User>

		// <Investigator> (repeating element)
		Path investigatorPath = new Path(null, "Investigator");
		List<String> invIdList = new ArrayList<String>(2);
		invIdList.add("Name");
		invIdList.add("Email");
		for (Element invElem : rootElem.getChildren("Investigator")) {
			
			OMECompositeVariable invDetails = new OMECompositeVariable(investigatorPath, invIdList);
			invDetails.addEntry("Name", invElem);
			invDetails.addEntry("Organization", invElem);
			invDetails.addEntry("Address", invElem);
			invDetails.addEntry("Phone", invElem);
			invDetails.addEntry("Email", invElem);
			
			investigators.add(invDetails);
		}
		// End <Investigator>
		
		// <DataSet_Info>
		Element dataSetInfoElem = rootElem.getChild("Dataset_Info");
		
		Path dataSetInfoPath = new Path(null, "Dataset_Info");
		
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
			Path varsInfoPath = new Path(null, "Variables_Info");
			
			Path variablePath = new Path(varsInfoPath, "Variable");
			for (Element variableElem : varsInfoElem.getChildren("Variable")) {
				
				OMECompositeVariable varDetails = new OMECompositeVariable(variablePath, "Variable_Name");
				varDetails.addEntry("Variable_Name", variableElem);
				varDetails.addEntry("Description_of_Variable", variableElem);
				
				variablesInfo.add(varDetails);
			}
		}
		
		// End <Variables_Info>
		
		// <CO2_Data_Info>
		Element co2DataInfoElem = rootElem.getChild("CO2_Data_Info");
		Path co2DataInfoPath = new Path(null, "CO2_Data_Info");
		
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
		Path methodDescriptionPath = new Path(null, "Method_Description");

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
		atpModel = new OMEVariable(atpPath, atpElem, "Model");
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
				
				List<String> sensorIdList = new ArrayList<String>(2);
				sensorIdList.add("Manufacturer");
				sensorIdList.add("Model");
				OMECompositeVariable sensorDetails = new OMECompositeVariable(sensorPath, sensorIdList);
				sensorDetails.addEntry("Manufacturer", sensorElem);
				sensorDetails.addEntry("Accuracy", sensorElem);
				sensorDetails.addEntry("Model", sensorElem);
				sensorDetails.addEntry("Resolution", sensorElem);
				sensorDetails.addEntry("Calibration", sensorElem);
				sensorDetails.addEntry("Other_Comments", sensorElem);
				
				otherSensors.add(sensorDetails);
			}
		}
		
		// End <Method_Description><Other_Sensors>
		// End <Method_Description>
		
		
		// Miscellaneous tags under the root element
		
		dataSetReferences = new OMEVariable(null, rootElem, "Data_set_References");
		additionalInformation = new OMEVariable(null, rootElem, "Additional_Information");
		citation = new OMEVariable(null, rootElem, "Citation");
		measurementAndCalibrationReport = new OMEVariable(null, rootElem, "Measurement_and_Calibration_Report");
		preliminaryQualityControl = new OMEVariable(null, rootElem, "Preliminary_Quality_control");
		
		// More miscellaneous root tags
		status = new OMEVariable(null, rootElem, "status");
		form_type = new OMEVariable(null, rootElem, "form_type");
		recordID = new OMEVariable(null, rootElem, "record_id");
		
	}

	/**
	 * Generated an OME XML document that contains the contents
	 * of the fields read by {@link #assignFromOmeXmlDoc(Document)}.
	 * Fields not read by that method are not saved in the document
	 * produced by this method.
	 * 
	 * @return
	 * 		the generated OME XML document
	 */
	public Document createOmeXmlDoc() {
		
		Element rootElem = new Element("x_tags");
		ConflictElement conflictElem = new ConflictElement();
		
		// <Status>
		if (itIsDraft) {
			Element statusElem = new Element("status");
			statusElem.setText("draft");
			rootElem.addContent(statusElem);
		}
		
		// <User>
		Element userElem = new Element("User");

		userName.generateXMLContent(userElem, conflictElem);
		userOrganization.generateXMLContent(userElem, conflictElem);
		userAddress.generateXMLContent(userElem, conflictElem);
		userPhone.generateXMLContent(userElem, conflictElem);
		userEmail.generateXMLContent(userElem, conflictElem);

		rootElem.addContent(userElem);
		// End <User>
		
		// <Investigator> (multiple)
		for (OMECompositeVariable investigator : investigators) {
			investigator.generateXMLContent(rootElem, conflictElem);
		}
		// End <Investigator>
		
		// <Dataset_Info>
		Element datasetInfoElem = new Element("Dataset_Info");
		datasetID.generateXMLContent(datasetInfoElem,  conflictElem);
		fundingInfo.generateXMLContent(datasetInfoElem,  conflictElem);
		
		// <Dataset_Info><Submission_Dates>
		Element submissionElem = new Element("Submission_Dates");
		initialSubmission.generateXMLContent(submissionElem, conflictElem);
		revisedSubmission.generateXMLContent(submissionElem, conflictElem);
		datasetInfoElem.addContent(submissionElem);
		
		// End <Dataset_Info><Submission_Dates>
		
		rootElem.addContent(datasetInfoElem);
		// End <Dataset_Info>
		
		// <Cruise_Info>
		Element cruiseInfoElem = new Element("Cruise_Info");
		
		// <Cruise_Info><Experiment>
		Element experimentElem = new Element("Experiment");
		
		experimentName.generateXMLContent(experimentElem, conflictElem);
		experimentType.generateXMLContent(experimentElem, conflictElem);
		platformType.generateXMLContent(experimentElem, conflictElem);
		co2InstrumentType.generateXMLContent(experimentElem, conflictElem);
		mooringId.generateXMLContent(experimentElem, conflictElem);
		
		// <Cruise_Info><Experiment><Cruise>
		Element cruiseElem = new Element("Cruise");
		
		cruiseID.generateXMLContent(cruiseElem, conflictElem);
		cruiseInfo.generateXMLContent(cruiseElem, conflictElem);
		section.generateXMLContent(cruiseElem, conflictElem);
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		Element geoCoverageElem = new Element("Geographical_Coverage");
		
		geographicalRegion.generateXMLContent(geoCoverageElem, conflictElem);
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		Element boundsElem = new Element("Bounds");
		
		if ( null != westmostLongitude )
			westmostLongitude.generateXMLContent(boundsElem, conflictElem);
		if ( null != eastmostLongitude )
			eastmostLongitude.generateXMLContent(boundsElem, conflictElem);
		if ( null != northmostLatitude )
			northmostLatitude.generateXMLContent(boundsElem, conflictElem);
		if ( null != southmostLatitude )
			southmostLatitude.generateXMLContent(boundsElem, conflictElem);
		
		
		geoCoverageElem.addContent(boundsElem);
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		
		cruiseElem.addContent(geoCoverageElem);
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		
		// <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		Element tempCoverageElem = new Element("Temporal_Coverage");
		
		temporalCoverageStartDate.generateXMLContent(tempCoverageElem, conflictElem);
		temporalCoverageEndDate.generateXMLContent(tempCoverageElem, conflictElem);
		
		cruiseElem.addContent(tempCoverageElem);
		// End <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		
		cruiseStartDate.generateXMLContent(cruiseElem, conflictElem);
		cruiseEndDate.generateXMLContent(cruiseElem, conflictElem);
		
		experimentElem.addContent(cruiseElem);
		// End <Cruise_Info><Experiment><Cruise>
		
		cruiseInfoElem.addContent(experimentElem);
		// End <Cruise_Info><Experiment>
		

		// <Cruise_Info><Vessel>
		Element vesselElem = new Element("Vessel");
		
		vesselName.generateXMLContent(vesselElem, conflictElem);
		vesselID.generateXMLContent(vesselElem, conflictElem);
		country.generateXMLContent(vesselElem, conflictElem);
		vesselOwner.generateXMLContent(vesselElem, conflictElem);
		
		cruiseInfoElem.addContent(vesselElem);
		// End <Cruise_Info><Vessel>
		
		rootElem.addContent(cruiseInfoElem);
		// End <Cruise_Info>

		// <Variables_Info>
		Element varsInfoElem = new Element("Variables_Info");
		for (OMECompositeVariable varInfo : variablesInfo) {
			varInfo.generateXMLContent(varsInfoElem, conflictElem);
		}
		
		rootElem.addContent(varsInfoElem);
		// End <Variables_Info>
		
		
		// <CO2_Data_Info>
		Element co2DataInfoElem = new Element("CO2_Data_Info");
		
		co2DataInfoElem.addContent(buildSubElement(xCO2WaterEquDryUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(xCO2WaterSSTDryUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(pCO2WaterEquWetUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(pCO2WaterSSTWetUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(fCO2WaterEquWetUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(fCO2WaterSSTWetUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(xCO2AirDryUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(pCO2AirWetUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(fCO2AirWetUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(xCO2AirDryInterpolatedUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(pCO2AirWetInterpolatedUnit, conflictElem));
		co2DataInfoElem.addContent(buildSubElement(fCO2AirWetInterpolatedUnit, conflictElem));
		
		rootElem.addContent(co2DataInfoElem);
		// End <CO2_Data_Info>
		
		// <Method_Description>
		Element methodDescElem = new Element("Method_Description");
		
		// <Method_Description><Equilibrator_Design>
		Element eqDesignElem = new Element("Equilibrator_Design");
		
		depthOfSeaWaterIntake.generateXMLContent(eqDesignElem, conflictElem);
		locationOfSeaWaterIntake.generateXMLContent(eqDesignElem, conflictElem);
		equilibratorType.generateXMLContent(eqDesignElem, conflictElem);
		equilibratorVolume.generateXMLContent(eqDesignElem, conflictElem);
		waterFlowRate.generateXMLContent(eqDesignElem, conflictElem);
		headspaceGasFlowRate.generateXMLContent(eqDesignElem, conflictElem);
		vented.generateXMLContent(eqDesignElem, conflictElem);
		dryingMethodForCO2InWater.generateXMLContent(eqDesignElem, conflictElem);
		equAdditionalInformation.generateXMLContent(eqDesignElem, conflictElem);
		
		
		methodDescElem.addContent(eqDesignElem);
		// End <Method_Description><Equilibrator_Design>
		
		// <Method_Description><CO2_in_Marine_Air>
		Element co2MarineAirElem = new Element("CO2_in_Marine_Air");

		co2InMarineAirMeasurement.generateXMLContent(co2MarineAirElem, conflictElem);
		co2InMarineAirLocationAndHeight.generateXMLContent(co2MarineAirElem, conflictElem);
		co2InMarineAirDryingMethod.generateXMLContent(co2MarineAirElem, conflictElem);

		methodDescElem.addContent(co2MarineAirElem);
		// End <Method_Description><CO2_in_Marine_Air>
		
		// <Method_Description><CO2_Sensors>
		Element co2SensorsElem = new Element("CO2_Sensors");
		
		// <Method_Description><CO2_Sensors><CO2_Sensor>
		Element co2SensorElem = new Element("CO2_Sensor");
		
		co2MeasurementMethod.generateXMLContent(co2SensorElem, conflictElem);
		co2Manufacturer.generateXMLContent(co2SensorElem, conflictElem);
		co2Model.generateXMLContent(co2SensorElem, conflictElem);
		co2Frequency.generateXMLContent(co2SensorElem, conflictElem);
		co2ResolutionWater.generateXMLContent(co2SensorElem, conflictElem);
		co2UncertaintyWater.generateXMLContent(co2SensorElem, conflictElem);
		co2ResolutionAir.generateXMLContent(co2SensorElem, conflictElem);
		co2UncertaintyAir.generateXMLContent(co2SensorElem, conflictElem);
		co2ManufacturerOfCalibrationGas.generateXMLContent(co2SensorElem, conflictElem);
		co2SensorCalibration.generateXMLContent(co2SensorElem, conflictElem);
		co2EnvironmentalControl.generateXMLContent(co2SensorElem, conflictElem);
		co2MethodReferences.generateXMLContent(co2SensorElem, conflictElem);
		detailsOfCO2Sensing.generateXMLContent(co2SensorElem, conflictElem);
		analysisOfCO2Comparison.generateXMLContent(co2SensorElem, conflictElem);
		measuredCO2Params.generateXMLContent(co2SensorElem, conflictElem);
		
		co2SensorsElem.addContent(co2SensorElem);
		// End <Method_Description><CO2_Sensors><CO2_Sensor>
		
		methodDescElem.addContent(co2SensorsElem);
		// End <Method_Description><CO2_Sensors>
		
		
		// <Method_Description><Sea_Surface_Temperature>
		Element sstElem = new Element("Sea_Surface_Temperature");
		
		sstLocation.generateXMLContent(sstElem, conflictElem);
		sstManufacturer.generateXMLContent(sstElem, conflictElem);
		sstModel.generateXMLContent(sstElem, conflictElem);
		sstAccuracy.generateXMLContent(sstElem, conflictElem);
		sstPrecision.generateXMLContent(sstElem, conflictElem);
		sstCalibration.generateXMLContent(sstElem, conflictElem);
		sstOtherComments.generateXMLContent(sstElem, conflictElem);
		
		methodDescElem.addContent(sstElem);
		// End <Method_Description><Sea_Surface_Temperature>
		
		// <Method_Description><Equilibrator_Temperature>
		Element eqtElem = new Element("Equilibrator_Temperature");
		
		eqtLocation.generateXMLContent(eqtElem, conflictElem);
		eqtManufacturer.generateXMLContent(eqtElem, conflictElem);
		eqtModel.generateXMLContent(eqtElem, conflictElem);
		eqtAccuracy.generateXMLContent(eqtElem, conflictElem);
		eqtPrecision.generateXMLContent(eqtElem, conflictElem);
		eqtCalibration.generateXMLContent(eqtElem, conflictElem);
		eqtWarming.generateXMLContent(eqtElem, conflictElem);
		eqtOtherComments.generateXMLContent(eqtElem, conflictElem);
		
		methodDescElem.addContent(eqtElem);
		// End <Method_Description><Equilibrator_Temperature>
	
		// <Method_Description><Equilibrator_Pressure>
		Element eqpElem = new Element("Equilibrator_Pressure");
		
		eqpLocation.generateXMLContent(eqpElem, conflictElem);
		eqpManufacturer.generateXMLContent(eqpElem, conflictElem);
		eqpModel.generateXMLContent(eqpElem, conflictElem);
		eqpAccuracy.generateXMLContent(eqpElem, conflictElem);
		eqpPrecision.generateXMLContent(eqpElem, conflictElem);
		eqpCalibration.generateXMLContent(eqpElem, conflictElem);
		eqpOtherComments.generateXMLContent(eqpElem, conflictElem);
		eqpNormalized.generateXMLContent(eqpElem, conflictElem);
		
		methodDescElem.addContent(eqpElem);
		// End <Method_Description><Equilibrator_Pressure>

		// <Method_Description><Atmospheric_Pressure>
		Element atpElem = new Element("Atmospheric_Pressure");
		
		atpLocation.generateXMLContent(atpElem, conflictElem);
		atpManufacturer.generateXMLContent(atpElem, conflictElem);
		atpModel.generateXMLContent(atpElem, conflictElem);
		atpAccuracy.generateXMLContent(atpElem, conflictElem);
		atpPrecision.generateXMLContent(atpElem, conflictElem);
		atpCalibration.generateXMLContent(atpElem, conflictElem);
		atpOtherComments.generateXMLContent(atpElem, conflictElem);
		
		methodDescElem.addContent(atpElem);
		// End <Method_Description><Atmospheric_Pressure>
		
		// <Method_Description><Sea_Surface_Salinity>
		Element sssElem = new Element("Sea_Surface_Salinity");
		
		sssLocation.generateXMLContent(sssElem, conflictElem);
		sssManufacturer.generateXMLContent(sssElem, conflictElem);
		sssModel.generateXMLContent(sssElem, conflictElem);
		sssAccuracy.generateXMLContent(sssElem, conflictElem);
		sssPrecision.generateXMLContent(sssElem, conflictElem);
		sssCalibration.generateXMLContent(sssElem, conflictElem);
		sssOtherComments.generateXMLContent(sssElem, conflictElem);
		
		methodDescElem.addContent(sssElem);
		// End <Method_Description><Sea_Surface_Salinity>
		
		// <Method_Description><Other_Sensors>
		Element otherSensorsElem = new Element("Other_Sensors");
		for (OMECompositeVariable sensorInfo : otherSensors) {
			sensorInfo.generateXMLContent(otherSensorsElem, conflictElem);
		}
		
		
		methodDescElem.addContent(otherSensorsElem);
		// End <Method_Description><Other_Sensors>
		
		
		rootElem.addContent(methodDescElem);
		// End <Method_Description>
		
		// Some misc root-level elements
		dataSetReferences.generateXMLContent(rootElem, conflictElem);
		additionalInformation.generateXMLContent(rootElem, conflictElem);
		citation.generateXMLContent(rootElem, conflictElem);
		measurementAndCalibrationReport.generateXMLContent(rootElem, conflictElem);
		preliminaryQualityControl.generateXMLContent(rootElem, conflictElem);
		
		// More misc root-level elements
		status.generateXMLContent(rootElem, conflictElem);
		form_type.generateXMLContent(rootElem, conflictElem);
		recordID.generateXMLContent(rootElem, conflictElem);
		
		// Add the CONFLICT element
		if (conflictElem.conflictsExist()) {
			rootElem.addContent(conflictElem);
		}
		return new Document(rootElem);
	}

	/**
	 * Assigns the expocode assigned for both the DashboardMetadata field
	 * and in the OmeMetadata OME variable.
	 * 
	 * @param expocode
	 * 		expocode to assign
	 */
	public void changeExpocode(String expocode) {
		this.itsExpoCode = expocode;
		this.cruiseID = new OMEVariable(this.cruiseID.getPath(), expocode);
	}

	
	/**
	 * Some elements of the OME XML have a single child. This is a shortcut method to
	 * extract the element and its child in one step. For example, there are a set of
	 * elements like this:
	 * 
	 * {@code 		<pCO2water_equ_wet>
			<Unit></Unit>
		</pCO2water_equ_wet>
		<pCO2water_SST_wet>
			<Unit></Unit>
		</pCO2water_SST_wet>}

	 * This method allows the parent and child elements to be processed in one call.
	 * 
	 * The two elements are known as the element ({@code <pCO2water_equ_wet>}) and
	 * the subElement ({@code <Unit>}). The parent element is the element at the level
	 * above these.
	 * 
	 * @param parentPath The Path object representing the parent element
	 * @param parentElement The XML elemenet of the parent
	 * @param elementName The name of the element
	 * @param subElementName The name of the sub-element
	 * @return The variable containing details of the extracted sub-element
	 */
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
	
	/**
	 * This method constructs an XML Element object for a variable that
	 * represents a single sub-element (see {@link #extractSubElement(Path, Element, String, String)}
	 * for details of these special elements).
	 * 
	 * @param variable The variable object
	 * @return The XML element containing its sub-element.
	 */
	private Element buildSubElement(OMEVariable variable, ConflictElement conflictElem) {
		Path varPath = variable.getPath();
		Element elem = new Element(varPath.getParent().getElementName());
		variable.generateXMLContent(elem, conflictElem);
		
		return elem;
	}
	
	
	
	public static OmeMetadata merge(OmeMetadata... metadatas) throws IllegalArgumentException {
		OmeMetadata merged = null;
		
		if (metadatas.length == 1) {
			merged = metadatas[0];
		} else {
			
			merged = (OmeMetadata) metadatas[0].clone();
			
			for (int i = 1; i < metadatas.length; i++) {
				copyValuesIn(merged, metadatas[i]);
			}
			
		}
		
		return merged;
	}
	
	private static void copyValuesIn(OmeMetadata dest, OmeMetadata newValues) throws IllegalArgumentException {
		
		// The first thing to copy is the cruise ID (aka EXPO Code).
		// If these are different, it implies that we have metadata from
		// different cruises so they should not be merged.
		dest.cruiseID.addValues(newValues.cruiseID.getAllValues());
		if (dest.cruiseID.hasConflict()) {
			throw new IllegalArgumentException("Cruise IDs do not match - cannot merge");
		}
		
		dest.userName.addValues(newValues.userName.getAllValues());
		dest.userOrganization.addValues(newValues.userOrganization.getAllValues());
		dest.userAddress.addValues(newValues.userAddress.getAllValues());
		dest.userPhone.addValues(newValues.userPhone.getAllValues());
		dest.userEmail.addValues(newValues.userEmail.getAllValues());
		
		dest.investigators = OMECompositeVariable.mergeVariables(dest.investigators, newValues.investigators);
		
		dest.datasetID.addValues(newValues.datasetID.getAllValues());
		dest.fundingInfo.addValues(newValues.fundingInfo.getAllValues());
		
		dest.initialSubmission.addValues(newValues.initialSubmission.getAllValues());
		dest.revisedSubmission.addValues(newValues.revisedSubmission.getAllValues());
		
		dest.experimentName.addValues(newValues.experimentName.getAllValues());
		dest.experimentType.addValues(newValues.experimentType.getAllValues());
		dest.platformType.addValues(newValues.platformType.getAllValues());
		dest.co2InstrumentType.addValues(newValues.co2InstrumentType.getAllValues());
		dest.mooringId.addValues(newValues.mooringId.getAllValues());

		dest.cruiseID.addValues(newValues.cruiseID.getAllValues());
		dest.cruiseInfo.addValues(newValues.cruiseInfo.getAllValues());
		dest.section.addValues(newValues.section.getAllValues());

		dest.geographicalRegion.addValues(newValues.geographicalRegion.getAllValues());
		
		dest.westmostLongitude.addValues(newValues.westmostLongitude.getAllValues());
		dest.eastmostLongitude.addValues(newValues.eastmostLongitude.getAllValues());
		dest.northmostLatitude.addValues(newValues.northmostLatitude.getAllValues());
		dest.southmostLatitude.addValues(newValues.southmostLatitude.getAllValues());

		dest.cruiseStartDate.addValues(newValues.cruiseStartDate.getAllValues());
		dest.cruiseEndDate.addValues(newValues.cruiseEndDate.getAllValues());

		dest.vesselName.addValues(newValues.vesselName.getAllValues());
		dest.vesselID.addValues(newValues.vesselID.getAllValues());
		dest.country.addValues(newValues.country.getAllValues());
		dest.vesselOwner.addValues(newValues.vesselOwner.getAllValues());

		dest.variablesInfo = OMECompositeVariable.mergeVariables(dest.variablesInfo, newValues.variablesInfo);

		dest.xCO2WaterEquDryUnit.addValues(newValues.xCO2WaterEquDryUnit.getAllValues());
		dest.xCO2WaterSSTDryUnit.addValues(newValues.xCO2WaterSSTDryUnit.getAllValues());
		dest.pCO2WaterEquWetUnit.addValues(newValues.pCO2WaterEquWetUnit.getAllValues());
		dest.pCO2WaterSSTWetUnit.addValues(newValues.pCO2WaterSSTWetUnit.getAllValues());
		dest.fCO2WaterEquWetUnit.addValues(newValues.fCO2WaterEquWetUnit.getAllValues());
		dest.fCO2WaterSSTWetUnit.addValues(newValues.fCO2WaterSSTWetUnit.getAllValues());
		dest.xCO2AirDryUnit.addValues(newValues.xCO2AirDryUnit.getAllValues());
		dest.pCO2AirWetUnit.addValues(newValues.pCO2AirWetUnit.getAllValues());
		dest.fCO2AirWetUnit.addValues(newValues.fCO2AirWetUnit.getAllValues());
		dest.xCO2AirDryInterpolatedUnit.addValues(newValues.xCO2AirDryInterpolatedUnit.getAllValues());
		dest.pCO2AirWetInterpolatedUnit.addValues(newValues.pCO2AirWetInterpolatedUnit.getAllValues());
		dest.fCO2AirWetInterpolatedUnit.addValues(newValues.fCO2AirWetInterpolatedUnit.getAllValues());

		dest.depthOfSeaWaterIntake.addValues(newValues.depthOfSeaWaterIntake.getAllValues());
		dest.locationOfSeaWaterIntake.addValues(newValues.locationOfSeaWaterIntake.getAllValues());
		dest.equilibratorType.addValues(newValues.equilibratorType.getAllValues());
		dest.equilibratorVolume.addValues(newValues.equilibratorVolume.getAllValues());
		dest.waterFlowRate.addValues(newValues.waterFlowRate.getAllValues());
		dest.headspaceGasFlowRate.addValues(newValues.headspaceGasFlowRate.getAllValues());
		dest.vented.addValues(newValues.vented.getAllValues());
		dest.dryingMethodForCO2InWater.addValues(newValues.dryingMethodForCO2InWater.getAllValues());
		dest.equAdditionalInformation.addValues(newValues.equAdditionalInformation.getAllValues());

		dest.co2InMarineAirMeasurement.addValues(newValues.co2InMarineAirMeasurement.getAllValues());
		dest.co2InMarineAirLocationAndHeight.addValues(newValues.co2InMarineAirLocationAndHeight.getAllValues());
		dest.co2InMarineAirDryingMethod.addValues(newValues.co2InMarineAirDryingMethod.getAllValues());
		
		dest.co2MeasurementMethod.addValues(newValues.co2MeasurementMethod.getAllValues());
		dest.co2Manufacturer.addValues(newValues.co2Manufacturer.getAllValues());
		dest.co2Model.addValues(newValues.co2Model.getAllValues());
		dest.co2Frequency.addValues(newValues.co2Frequency.getAllValues());
		dest.co2ResolutionWater.addValues(newValues.co2ResolutionWater.getAllValues());
		dest.co2UncertaintyWater.addValues(newValues.co2UncertaintyWater.getAllValues());
		dest.co2ResolutionAir.addValues(newValues.co2ResolutionAir.getAllValues());
		dest.co2UncertaintyAir.addValues(newValues.co2UncertaintyAir.getAllValues());
		dest.co2ManufacturerOfCalibrationGas.addValues(newValues.co2ManufacturerOfCalibrationGas.getAllValues());
		dest.co2SensorCalibration.addValues(newValues.co2SensorCalibration.getAllValues());
		dest.co2EnvironmentalControl.addValues(newValues.co2EnvironmentalControl.getAllValues());
		dest.co2MethodReferences.addValues(newValues.co2MethodReferences.getAllValues());
		dest.detailsOfCO2Sensing.addValues(newValues.detailsOfCO2Sensing.getAllValues());
		dest.analysisOfCO2Comparison.addValues(newValues.analysisOfCO2Comparison.getAllValues());
		dest.measuredCO2Params.addValues(newValues.measuredCO2Params.getAllValues());

		dest.sstLocation.addValues(newValues.sstLocation.getAllValues());
		dest.sstManufacturer.addValues(newValues.sstManufacturer.getAllValues());
		dest.sstModel.addValues(newValues.sstModel.getAllValues());
		dest.sstAccuracy.addValues(newValues.sstAccuracy.getAllValues());
		dest.sstPrecision.addValues(newValues.sstPrecision.getAllValues());
		dest.sstCalibration.addValues(newValues.sstCalibration.getAllValues());
		dest.sstOtherComments.addValues(newValues.sstOtherComments.getAllValues());

		dest.eqtLocation.addValues(newValues.eqtLocation.getAllValues());
		dest.eqtManufacturer.addValues(newValues.eqtManufacturer.getAllValues());
		dest.eqtModel.addValues(newValues.eqtModel.getAllValues());
		dest.eqtAccuracy.addValues(newValues.eqtAccuracy.getAllValues());
		dest.eqtPrecision.addValues(newValues.eqtPrecision.getAllValues());
		dest.eqtCalibration.addValues(newValues.eqtCalibration.getAllValues());
		dest.eqtWarming.addValues(newValues.eqtWarming.getAllValues());
		dest.eqtOtherComments.addValues(newValues.eqtOtherComments.getAllValues());

		dest.eqpLocation.addValues(newValues.eqpLocation.getAllValues());
		dest.eqpManufacturer.addValues(newValues.eqpManufacturer.getAllValues());
		dest.eqpModel.addValues(newValues.eqpModel.getAllValues());
		dest.eqpAccuracy.addValues(newValues.eqpAccuracy.getAllValues());
		dest.eqpPrecision.addValues(newValues.eqpPrecision.getAllValues());
		dest.eqpCalibration.addValues(newValues.eqpCalibration.getAllValues());
		dest.eqpOtherComments.addValues(newValues.eqpOtherComments.getAllValues());
		dest.eqpNormalized.addValues(newValues.eqpNormalized.getAllValues());

		dest.atpLocation.addValues(newValues.atpLocation.getAllValues());
		dest.atpManufacturer.addValues(newValues.atpManufacturer.getAllValues());
		dest.atpModel.addValues(newValues.atpModel.getAllValues());
		dest.atpAccuracy.addValues(newValues.atpAccuracy.getAllValues());
		dest.atpPrecision.addValues(newValues.atpPrecision.getAllValues());
		dest.atpCalibration.addValues(newValues.atpCalibration.getAllValues());
		dest.atpOtherComments.addValues(newValues.atpOtherComments.getAllValues());

		dest.sssLocation.addValues(newValues.sssLocation.getAllValues());
		dest.sssManufacturer.addValues(newValues.sssManufacturer.getAllValues());
		dest.sssModel.addValues(newValues.sssModel.getAllValues());
		dest.sssAccuracy.addValues(newValues.sssAccuracy.getAllValues());
		dest.sssPrecision.addValues(newValues.sssPrecision.getAllValues());
		dest.sssCalibration.addValues(newValues.sssCalibration.getAllValues());
		dest.sssOtherComments.addValues(newValues.sssOtherComments.getAllValues());

		dest.otherSensors = OMECompositeVariable.mergeVariables(dest.otherSensors, newValues.otherSensors);
		
		dest.dataSetReferences.addValues(newValues.dataSetReferences.getAllValues());
		dest.additionalInformation.addValues(newValues.additionalInformation.getAllValues());
		dest.citation.addValues(newValues.citation.getAllValues());
		dest.measurementAndCalibrationReport.addValues(newValues.measurementAndCalibrationReport.getAllValues());
		dest.preliminaryQualityControl.addValues(newValues.preliminaryQualityControl.getAllValues());

		dest.status.addValues(newValues.status.getAllValues());
		dest.form_type.addValues(newValues.form_type.getAllValues());
		dest.recordID.addValues(newValues.recordID.getAllValues());
	}
	
	public Object clone() {
		OmeMetadata clone = new OmeMetadata(itsExpoCode);
		
		clone.userName = (OMEVariable) userName.clone();
		clone.userOrganization = (OMEVariable) userOrganization.clone();
		clone.userAddress = (OMEVariable) userAddress.clone();
		clone.userPhone = (OMEVariable) userPhone.clone();
		clone.userEmail = (OMEVariable) userEmail.clone();
		
		clone.investigators = new ArrayList<OMECompositeVariable>(investigators.size());
		for (OMECompositeVariable investigator : investigators) {
			clone.investigators.add((OMECompositeVariable) investigator.clone());
		}
		
		clone.datasetID = (OMEVariable) datasetID.clone();
		clone.fundingInfo = (OMEVariable) fundingInfo.clone();
		
		clone.initialSubmission = (OMEVariable) initialSubmission.clone();
		clone.revisedSubmission = (OMEVariable) revisedSubmission.clone();
		
		clone.experimentName = (OMEVariable) experimentName.clone();
		clone.experimentType = (OMEVariable) experimentType.clone();
		clone.platformType = (OMEVariable) platformType.clone();
		clone.co2InstrumentType = (OMEVariable) co2InstrumentType.clone();
		clone.mooringId = (OMEVariable) mooringId.clone();
		
		clone.cruiseID = (OMEVariable) cruiseID.clone();
		clone.cruiseInfo = (OMEVariable) cruiseInfo.clone();
		clone.section = (OMEVariable) section.clone();

		clone.geographicalRegion = (OMEVariable) geographicalRegion.clone();
		
		clone.westmostLongitude = (OMEVariable) westmostLongitude.clone();
		clone.eastmostLongitude = (OMEVariable) eastmostLongitude.clone();
		clone.northmostLatitude = (OMEVariable) northmostLatitude.clone();
		clone.southmostLatitude = (OMEVariable) southmostLatitude.clone();
		
		clone.temporalCoverageStartDate = (OMEVariable) temporalCoverageStartDate.clone();
		clone.temporalCoverageEndDate = (OMEVariable) temporalCoverageEndDate.clone();

		clone.cruiseStartDate = (OMEVariable) cruiseStartDate.clone();
		clone.cruiseEndDate = (OMEVariable) cruiseEndDate.clone();

		clone.vesselName = (OMEVariable) vesselName.clone();
		clone.vesselID = (OMEVariable) vesselID.clone();
		clone.country = (OMEVariable) country.clone();
		clone.vesselOwner = (OMEVariable) vesselOwner.clone();
		
		clone.variablesInfo = new ArrayList<OMECompositeVariable>(variablesInfo.size());
		for (OMECompositeVariable varInfo : variablesInfo) {
			clone.variablesInfo.add((OMECompositeVariable) varInfo.clone());
		}

		clone.xCO2WaterEquDryUnit = (OMEVariable) xCO2WaterEquDryUnit.clone();
		clone.xCO2WaterSSTDryUnit = (OMEVariable) xCO2WaterSSTDryUnit.clone();
		clone.pCO2WaterEquWetUnit = (OMEVariable) pCO2WaterEquWetUnit.clone();
		clone.pCO2WaterSSTWetUnit = (OMEVariable) pCO2WaterSSTWetUnit.clone();
		clone.fCO2WaterEquWetUnit = (OMEVariable) fCO2WaterEquWetUnit.clone();
		clone.fCO2WaterSSTWetUnit = (OMEVariable) fCO2WaterSSTWetUnit.clone();
		clone.xCO2AirDryUnit = (OMEVariable) xCO2AirDryUnit.clone();
		clone.pCO2AirWetUnit = (OMEVariable) pCO2AirWetUnit.clone();
		clone.fCO2AirWetUnit = (OMEVariable) fCO2AirWetUnit.clone();
		clone.xCO2AirDryInterpolatedUnit = (OMEVariable) xCO2AirDryInterpolatedUnit.clone();
		clone.pCO2AirWetInterpolatedUnit = (OMEVariable) pCO2AirWetInterpolatedUnit.clone();
		clone.fCO2AirWetInterpolatedUnit = (OMEVariable) fCO2AirWetInterpolatedUnit.clone();

		clone.depthOfSeaWaterIntake = (OMEVariable) depthOfSeaWaterIntake.clone();
		clone.locationOfSeaWaterIntake = (OMEVariable) locationOfSeaWaterIntake.clone();
		clone.equilibratorType = (OMEVariable) equilibratorType.clone();
		clone.equilibratorVolume = (OMEVariable) equilibratorVolume.clone();
		clone.waterFlowRate = (OMEVariable) waterFlowRate.clone();
		clone.headspaceGasFlowRate = (OMEVariable) headspaceGasFlowRate.clone();
		clone.vented = (OMEVariable) vented.clone();
		clone.dryingMethodForCO2InWater = (OMEVariable) dryingMethodForCO2InWater.clone();
		clone.equAdditionalInformation = (OMEVariable) equAdditionalInformation.clone();

		clone.co2InMarineAirMeasurement = (OMEVariable) co2InMarineAirMeasurement.clone();
		clone.co2InMarineAirLocationAndHeight = (OMEVariable) co2InMarineAirLocationAndHeight.clone();
		clone.co2InMarineAirDryingMethod = (OMEVariable) co2InMarineAirDryingMethod.clone();
		
		clone.co2MeasurementMethod = (OMEVariable) co2MeasurementMethod.clone();
		clone.co2Manufacturer = (OMEVariable) co2Manufacturer.clone();
		clone.co2Model = (OMEVariable) co2Model.clone();
		clone.co2Frequency = (OMEVariable) co2Frequency.clone();
		clone.co2ResolutionWater = (OMEVariable) co2ResolutionWater.clone();
		clone.co2UncertaintyWater = (OMEVariable) co2UncertaintyWater.clone();
		clone.co2ResolutionAir = (OMEVariable) co2ResolutionAir.clone();
		clone.co2UncertaintyAir = (OMEVariable) co2UncertaintyAir.clone();
		clone.co2ManufacturerOfCalibrationGas = (OMEVariable) co2ManufacturerOfCalibrationGas.clone();
		clone.co2SensorCalibration = (OMEVariable) co2SensorCalibration.clone();
		clone.co2EnvironmentalControl = (OMEVariable) co2EnvironmentalControl.clone();
		clone.co2MethodReferences = (OMEVariable) co2MethodReferences.clone();
		clone.detailsOfCO2Sensing = (OMEVariable) detailsOfCO2Sensing.clone();
		clone.analysisOfCO2Comparison = (OMEVariable) analysisOfCO2Comparison.clone();
		clone.measuredCO2Params = (OMEVariable) measuredCO2Params.clone();

		clone.sstLocation = (OMEVariable) sstLocation.clone();
		clone.sstManufacturer = (OMEVariable) sstManufacturer.clone();
		clone.sstModel = (OMEVariable) sstModel.clone();
		clone.sstAccuracy = (OMEVariable) sstAccuracy.clone();
		clone.sstPrecision = (OMEVariable) sstPrecision.clone();
		clone.sstCalibration = (OMEVariable) sstCalibration.clone();
		clone.sstOtherComments = (OMEVariable) sstOtherComments.clone();

		clone.eqtLocation = (OMEVariable) eqtLocation.clone();
		clone.eqtManufacturer = (OMEVariable) eqtManufacturer.clone();
		clone.eqtModel = (OMEVariable) eqtModel.clone();
		clone.eqtAccuracy = (OMEVariable) eqtAccuracy.clone();
		clone.eqtPrecision = (OMEVariable) eqtPrecision.clone();
		clone.eqtCalibration = (OMEVariable) eqtCalibration.clone();
		clone.eqtWarming = (OMEVariable) eqtWarming.clone();
		clone.eqtOtherComments = (OMEVariable) eqtOtherComments.clone();

		clone.eqpLocation = (OMEVariable) eqpLocation.clone();
		clone.eqpManufacturer = (OMEVariable) eqpManufacturer.clone();
		clone.eqpModel = (OMEVariable) eqpModel.clone();
		clone.eqpAccuracy = (OMEVariable) eqpAccuracy.clone();
		clone.eqpPrecision = (OMEVariable) eqpPrecision.clone();
		clone.eqpCalibration = (OMEVariable) eqpCalibration.clone();
		clone.eqpOtherComments = (OMEVariable) eqpOtherComments.clone();
		clone.eqpNormalized = (OMEVariable) eqpNormalized.clone();

		clone.atpLocation = (OMEVariable) atpLocation.clone();
		clone.atpManufacturer = (OMEVariable) atpManufacturer.clone();
		clone.atpModel = (OMEVariable) atpModel.clone();
		clone.atpAccuracy = (OMEVariable) atpAccuracy.clone();
		clone.atpPrecision = (OMEVariable) atpPrecision.clone();
		clone.atpCalibration = (OMEVariable) atpCalibration.clone();
		clone.atpOtherComments = (OMEVariable) atpOtherComments.clone();

		clone.sssLocation = (OMEVariable) sssLocation.clone();
		clone.sssManufacturer = (OMEVariable) sssManufacturer.clone();
		clone.sssModel = (OMEVariable) sssModel.clone();
		clone.sssAccuracy = (OMEVariable) sssAccuracy.clone();
		clone.sssPrecision = (OMEVariable) sssPrecision.clone();
		clone.sssCalibration = (OMEVariable) sssCalibration.clone();
		clone.sssOtherComments = (OMEVariable) sssOtherComments.clone();

		clone.otherSensors = new ArrayList<OMECompositeVariable>(otherSensors.size());
		for (OMECompositeVariable otherSensor : otherSensors) {
			clone.otherSensors.add((OMECompositeVariable) otherSensor.clone());
		}
		
		clone.dataSetReferences = (OMEVariable) dataSetReferences.clone();
		clone.additionalInformation = (OMEVariable) additionalInformation.clone();
		clone.citation = (OMEVariable) citation.clone();
		clone.measurementAndCalibrationReport = (OMEVariable) measurementAndCalibrationReport.clone();
		clone.preliminaryQualityControl = (OMEVariable) preliminaryQualityControl.clone();
		
		clone.status = (OMEVariable) status.clone();
		clone.form_type = (OMEVariable) form_type.clone();
		clone.recordID = (OMEVariable) recordID.clone();
		
		return clone;
	}
}
