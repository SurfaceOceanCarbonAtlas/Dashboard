/**
 * 
 */
package uk.ac.uea.socat.metadata.OmeMetadata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains user-provided values needed by the SOCAT 
 * database.  
 * 
 * @author Steve Jones
 */
public class OmeMetadata {

	public static final String CONFLICT_STRING = "%%CONFLICT%%";
	
	private static final SimpleDateFormat DATE_PARSER = 
			new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	static {
		DATE_PARSER.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private static final String ROOT_ELEMENT_NAME = "x_tags";
	private static final String STATUS_ELEMENT_NAME = "status";
	private static final String DRAFT_ELEMENT_NAME = "draft";

	/**
	 * Paths for simple variables
	 */
	private static final String USER_ELEMENT_NAME = "User";
	private static final Path USER_PATH = new Path(null, USER_ELEMENT_NAME);
	private static final Path USER_NAME_PATH = new Path(USER_PATH, "Name");
	private static final Path USER_ORGANIZATION_PATH = new Path(USER_PATH, "Organization");
	private static final Path USER_ADDRESS_PATH = new Path(USER_PATH, "Address");
	private static final Path USER_PHONE_PATH = new Path(USER_PATH, "Phone");
	private static final Path USER_EMAIL_PATH = new Path(USER_PATH, "Email");

	private static final String DATASET_INFO_ELEMENT_NAME = "Dataset_Info";
	private static final Path DATASET_INFO_PATH = new Path(null, DATASET_INFO_ELEMENT_NAME);
	private static final Path DATASET_ID_PATH = new Path(DATASET_INFO_PATH, "Dataset_ID");
	private static final Path FUNDING_INFO_PATH = new Path(DATASET_INFO_PATH, "Funding_Info");

	private static final String SUBMISSION_DATES_ELEMENT_NAME = "Submission_Dates";
	private static final Path SUBMISSION_DATES_PATH = new Path(DATASET_INFO_PATH, SUBMISSION_DATES_ELEMENT_NAME);
	private static final Path INITIAL_SUBMISSION_PATH = new Path(SUBMISSION_DATES_PATH, "Initial_Submission");
	private static final Path REVISED_SUBMISSION_PATH = new Path(SUBMISSION_DATES_PATH, "Revised_Submission");
	
	private static final String CRUISE_INFO_ELEMENT_NAME = "Cruise_Info";
	private static final Path CRUISE_INFO_PATH = new Path(null, CRUISE_INFO_ELEMENT_NAME);
	private static final String EXPERIMENT_ELEMENT_NAME = "Experiment";
	private static final Path EXPERIMENT_PATH = new Path(CRUISE_INFO_PATH, EXPERIMENT_ELEMENT_NAME);
	private static final Path EXPERIMENT_NAME_PATH = new Path(EXPERIMENT_PATH, "Experiment_Name");
	private static final Path EXPERIMENT_TYPE_PATH = new Path(EXPERIMENT_PATH, "Experiment_Type");
	private static final Path PLATFORM_TYPE_PATH = new Path(EXPERIMENT_PATH, "Platform_Type");
	private static final Path CO2_INSTRUMENT_TYPE_PATH = new Path(EXPERIMENT_PATH, "Co2_Instrument_type");
	private static final Path MOORING_ID_PATH = new Path(EXPERIMENT_PATH, "Mooring_ID");
	
	private static final String CRUISE_ELEMENT_NAME = "Cruise";
	private static final Path CRUISE_PATH = new Path(EXPERIMENT_PATH, CRUISE_ELEMENT_NAME);
	private static final String CRUISE_ID_ELEMENT_NAME = "Cruise_ID";
	private static final Path CRUISE_ID_PATH = new Path(CRUISE_PATH, CRUISE_ID_ELEMENT_NAME);
	private static final Path SUB_CRUISE_INFO_PATH = new Path(CRUISE_PATH, "Cruise_Info");
	private static final Path SECTION_PATH = new Path(CRUISE_PATH, "Section");

	private static final String GEO_COVERAGE_ELEMENT_NAME = "Geographical_Coverage";
	private static final Path GEO_COVERAGE_PATH = new Path(CRUISE_PATH, GEO_COVERAGE_ELEMENT_NAME);
	private static final Path GEO_REGION_PATH = new Path(GEO_COVERAGE_PATH, "Geographical_Region");
	private static final String BOUNDS_ELEMENT_NAME = "Bounds";
	private static final Path BOUNDS_PATH = new Path(GEO_COVERAGE_PATH, BOUNDS_ELEMENT_NAME);
	private static final Path WEST_BOUND_PATH = new Path(BOUNDS_PATH, "Westernmost_Longitude");
	private static final Path EAST_BOUND_PATH = new Path(BOUNDS_PATH, "Easternmost_Longitude");
	private static final Path NORTH_BOUND_PATH = new Path(BOUNDS_PATH, "Northernmost_Latitude");
	private static final Path SOUTH_BOUND_PATH = new Path(BOUNDS_PATH, "Southernmost_Latitude");

	private static final String TEMP_COVERAGE_ELEMENT_NAME = "Temporal_Coverage";
	private static final Path TEMP_COVERAGE_PATH = new Path(CRUISE_PATH, TEMP_COVERAGE_ELEMENT_NAME);
	private static final Path TEMP_START_DATE_PATH = new Path(TEMP_COVERAGE_PATH, "Start_Date");
	private static final Path TEMP_END_DATE_PATH = new Path(TEMP_COVERAGE_PATH, "End_Date");
	private static final Path START_DATE_PATH = new Path(CRUISE_PATH, "Start_Date");
	private static final Path END_DATE_PATH = new Path(CRUISE_PATH, "End_Date");

	private static final String VESSEL_ELEMENT_NAME = "Vessel";
	private static final Path VESSEL_PATH = new Path(CRUISE_INFO_PATH, VESSEL_ELEMENT_NAME);
	private static final Path VESSEL_NAME_PATH = new Path(VESSEL_PATH, "Vessel_Name");
	private static final Path VESSEL_ID_PATH = new Path(VESSEL_PATH, "Vessel_ID");
	private static final Path COUNTRY_PATH = new Path(VESSEL_PATH, "Country");
	private static final Path OWNER_PATH = new Path(VESSEL_PATH, "Vessel_Owner");

	private static final String CO2_DATA_INFO_ELEMENT_NAME = "CO2_Data_Info";
	private static final Path CO2_DATA_INFO_PATH = new Path(null, CO2_DATA_INFO_ELEMENT_NAME);
	private static final String UNIT_ELEMENT_NAME = "Unit";
	private static final String XCO2_WATER_EQU_DRY_ELEMENT_NAME = "xCO2water_equ_dry";
	private static final Path XCO2_WATER_EQU_DRY_PATH = new Path(CO2_DATA_INFO_PATH, XCO2_WATER_EQU_DRY_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String XCO2_WATER_SST_DRY_ELEMENT_NAME = "xCO2water_SST_dry";
	private static final Path XCO2_WATER_SST_DRY_PATH = new Path(CO2_DATA_INFO_PATH, XCO2_WATER_SST_DRY_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String PCO2_WATER_EQU_WET_ELEMENT_NAME = "pCO2water_equ_wet";
	private static final Path PCO2_WATER_EQU_WET_PATH = new Path(CO2_DATA_INFO_PATH, PCO2_WATER_EQU_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String PCO2_WATER_SST_WET_ELEMENT_NAME = "pCO2water_SST_wet";
	private static final Path PCO2_WATER_SST_WET_PATH = new Path(CO2_DATA_INFO_PATH, PCO2_WATER_SST_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String FCO2_WATER_EQU_WET_ELEMENT_NAME = "fCO2water_equ_wet";
	private static final Path FCO2_WATER_EQU_WET_PATH = new Path(CO2_DATA_INFO_PATH, FCO2_WATER_EQU_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String FCO2_WATER_SST_WET_ELEMENT_NAME = "fCO2water_SST_wet";
	private static final Path FCO2_WATER_SST_WET_PATH = new Path(CO2_DATA_INFO_PATH, FCO2_WATER_SST_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String XCO2_AIR_DRY_ELEMENT_NAME = "xCO2air_dry";
	private static final Path XCO2_AIR_DRY_PATH = new Path(CO2_DATA_INFO_PATH, XCO2_AIR_DRY_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String PCO2_AIR_WET_ELEMENT_NAME = "pCO2air_wet";
	private static final Path PCO2_AIR_WET_PATH = new Path(CO2_DATA_INFO_PATH, PCO2_AIR_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String FCO2_AIR_WET_ELEMENT_NAME = "fCO2air_wet";
	private static final Path FCO2_AIR_WET_PATH = new Path(CO2_DATA_INFO_PATH, FCO2_AIR_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String XCO2_AIR_DRY_INTERP_ELEMENT_NAME = "xCO2air_dry_interpolated";
	private static final Path XCO2_AIR_DRY_INTERP_PATH = new Path(CO2_DATA_INFO_PATH, XCO2_AIR_DRY_INTERP_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String PCO2_AIR_WET_INTERP_ELEMENT_NAME = "pCO2air_wet_interpolated";
	private static final Path PCO2_AIR_WET_INTERP_PATH = new Path(CO2_DATA_INFO_PATH, PCO2_AIR_WET_INTERP_ELEMENT_NAME, UNIT_ELEMENT_NAME);
	private static final String FCO2_AIR_WET_INTERP_ELEMENT_NAME = "fCO2air_wet_interpolated";
	private static final Path FCO2_AIR_WET_INTERP_PATH = new Path(CO2_DATA_INFO_PATH, FCO2_AIR_WET_INTERP_ELEMENT_NAME, UNIT_ELEMENT_NAME);

	private static final String METHOD_DESCRIPTION_ELEMENT_NAME = "Method_Description";
	private static final Path METHOD_DESCRIPTION_PATH = new Path(null, METHOD_DESCRIPTION_ELEMENT_NAME);
	private static final String EQUILIBRATOR_DESIGN_ELEMENT_NAME = "Equilibrator_Design";
	private static final Path EQUILIBRATOR_DESIGN_PATH = new Path(METHOD_DESCRIPTION_PATH, EQUILIBRATOR_DESIGN_ELEMENT_NAME);
	private static final Path INTAKE_DEPTH_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Depth_of_Sea_Water_Intake");
	private static final Path INTAKE_LOCATION_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Location_of_Sea_Water_Intake");
	private static final Path EQUI_TYPE_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Equilibrator_Type");
	private static final Path EQUI_VOLUME_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Equilibrator_Volume");
	private static final Path WATER_FLOW_RATE_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Water_Flow_Rate");
	private static final Path GAS_FLOW_RATE_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Headspace_Gas_Flow_Rate");
	private static final Path VENTED_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Vented");
	private static final Path DRYING_METHOD_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Drying_Method_for_CO2_in_water");
	private static final Path EQUI_ADDITIONAL_INFO_PATH = new Path(EQUILIBRATOR_DESIGN_PATH, "Additional_Information");

	private static final String CO2_MARINE_AIR_ELEMENT_NAME = "CO2_in_Marine_Air";
	private static final Path CO2_MARINE_AIR_PATH = new Path(METHOD_DESCRIPTION_PATH, CO2_MARINE_AIR_ELEMENT_NAME);
	private static final Path MARINE_AIR_MEASUREMENT_PATH = new Path(CO2_MARINE_AIR_PATH, "Measurement");
	private static final Path MARINE_AIR_LOCATION_PATH = new Path(CO2_MARINE_AIR_PATH, "Location_and_Height");
	private static final Path MARINE_AIR_DRYING_PATH = new Path(CO2_MARINE_AIR_PATH, "Drying_Method");

	private static final String CO2_SENSORS_ELEMENT_NAME = "CO2_Sensors";
	private static final Path CO2_SENSORS_PATH = new Path(METHOD_DESCRIPTION_PATH, CO2_SENSORS_ELEMENT_NAME);
	private static final Path CO2_SENSOR_PATH = new Path(CO2_SENSORS_PATH, "CO2_Sensor");
	private static final Path CO2_MEASUREMENT_METHOD_PATH = new Path(CO2_SENSOR_PATH, "Measurement_Method");
	private static final Path CO2_MANUFACTURER_PATH = new Path(CO2_SENSOR_PATH, "Manufacturer");
	private static final Path CO2_MODEL_PATH = new Path(CO2_SENSOR_PATH, "Model");
	private static final Path CO2_FREQUENCY_PATH = new Path(CO2_SENSOR_PATH, "Frequency");
	private static final Path CO2_WATER_RES_PATH = new Path(CO2_SENSOR_PATH, "Resolution_Water");
	private static final Path CO2_WATER_UNC_PATH = new Path(CO2_SENSOR_PATH, "Uncertainty_Water");
	private static final Path CO2_AIR_RES_PATH = new Path(CO2_SENSOR_PATH, "Resolution_Air");
	private static final Path CO2_AIR_UNC_PATH = new Path(CO2_SENSOR_PATH, "Uncertainty_Air");
	private static final Path CO2_CALIBRATION_MANUFACTURER_PATH = new Path(CO2_SENSOR_PATH, "Manufacturer_of_Calibration_Gas");
	private static final Path CO2_SENSOR_CALIBRATION_PATH = new Path(CO2_SENSOR_PATH, "CO2_Sensor_Calibration");
	private static final Path ENVIRONMENTAL_CONTROL_PATH = new Path(CO2_SENSOR_PATH, "Environmental_Control");
	private static final Path METHOD_REFS_PATH = new Path(CO2_SENSOR_PATH, "Method_References");
	private static final Path DETAILS_OF_CO2_SENSING_PATH = new Path(CO2_SENSOR_PATH, "Details_Co2_Sensing");
	private static final Path ANALYSIS_OF_COMPARISON_PATH = new Path(CO2_SENSOR_PATH, "Analysis_of_Co2_Comparision");
	private static final Path MEASURED_CO2_PARAMS_PATH = new Path(CO2_SENSOR_PATH, "Measured_Co2_Params");

	private static final String SST_ELEMENT_NAME = "Sea_Surface_Temperature";
	private static final Path SST_PATH = new Path(METHOD_DESCRIPTION_PATH, SST_ELEMENT_NAME);
	private static final Path SST_LOCATION_PATH = new Path(SST_PATH, "Location");
	private static final Path SST_MANUFACTURER_PATH = new Path(SST_PATH, "Manufacturer");
	private static final Path SST_MODEL_PATH = new Path(SST_PATH, "Model");
	private static final Path SST_ACCURACY_PATH = new Path(SST_PATH, "Accuracy");
	private static final Path SST_PRECISION_PATH = new Path(SST_PATH, "Precision");
	private static final Path SST_CALIBRATION_PATH = new Path(SST_PATH, "Calibration");
	private static final Path SST_COMMENTS_PATH = new Path(SST_PATH, "Other_Comments");

	private static final String EQU_TEMP_ELEMENT_NAME = "Equilibrator_Temperature";
	private static final Path EQU_TEMP_PATH = new Path(METHOD_DESCRIPTION_PATH, EQU_TEMP_ELEMENT_NAME);
	private static final Path EQT_LOCATION_PATH = new Path(EQU_TEMP_PATH, "Location");
	private static final Path EQT_MANUFACTURER_PATH = new Path(EQU_TEMP_PATH, "Manufacturer");
	private static final Path EQT_MODEL_PATH = new Path(EQU_TEMP_PATH, "Model");
	private static final Path EQT_ACCURACY_PATH = new Path(EQU_TEMP_PATH, "Accuracy");
	private static final Path EQT_PRECISION_PATH = new Path(EQU_TEMP_PATH, "Precision");
	private static final Path EQT_CALIBRATION_PATH = new Path(EQU_TEMP_PATH, "Calibration");
	private static final Path EQT_WARMING_PATH = new Path(EQU_TEMP_PATH, "Warming");
	private static final Path EQT_COMMENTS_PATH = new Path(EQU_TEMP_PATH, "Other_Comments");	

	private static final String EQU_PRESSURE_ELEMENT_NAME = "Equilibrator_Pressure";
	private static final Path EQU_PRESSURE_PATH = new Path(METHOD_DESCRIPTION_PATH, EQU_PRESSURE_ELEMENT_NAME);
	private static final Path EQP_LOCATION_PATH = new Path(EQU_PRESSURE_PATH, "Location");
	private static final Path EQP_MANUFACTURER_PATH = new Path(EQU_PRESSURE_PATH, "Manufacturer");
	private static final Path EQP_MODEL_PATH = new Path(EQU_PRESSURE_PATH, "Model");
	private static final Path EQP_ACCURACY_PATH = new Path(EQU_PRESSURE_PATH, "Accuracy");
	private static final Path EQP_PRECISION_PATH = new Path(EQU_PRESSURE_PATH, "Precision");
	private static final Path EQP_CALIBRATION_PATH = new Path(EQU_PRESSURE_PATH, "Calibration");
	private static final Path EQP_COMMENTS_PATH = new Path(EQU_PRESSURE_PATH, "Other_Comments");
	private static final Path EQP_NORMALIZED_PATH = new Path(EQU_PRESSURE_PATH, "Normalized");

	private static final String ATM_PRESSURE_ELEMENT_NAME = "Atmospheric_Pressure";
	private static final Path ATM_PRESSURE_PATH = new Path(METHOD_DESCRIPTION_PATH, ATM_PRESSURE_ELEMENT_NAME);
	private static final Path ATM_LOCATION_PATH = new Path(ATM_PRESSURE_PATH, "Location");
	private static final Path ATM_MANUFACTURER_PATH = new Path(ATM_PRESSURE_PATH, "Manufacturer");
	private static final Path ATM_MODEL_PATH = new Path(ATM_PRESSURE_PATH, "Model");
	private static final Path ATM_ACCURACY_PATH = new Path(ATM_PRESSURE_PATH, "Accuracy");
	private static final Path ATM_PRECISION_PATH = new Path(ATM_PRESSURE_PATH, "Precision");
	private static final Path ATM_CALIBRATION_PATH = new Path(ATM_PRESSURE_PATH, "Calibration");
	private static final Path ATM_COMMENTS_PATH = new Path(ATM_PRESSURE_PATH, "Other_Comments");

	private static final String SSS_ELEMENT_NAME = "Sea_Surface_Salinity";
	private static final Path SSS_PATH = new Path(METHOD_DESCRIPTION_PATH, SSS_ELEMENT_NAME);
	private static final Path SSS_LOCATION_PATH = new Path(SSS_PATH, "Location");
	private static final Path SSS_MANUFACTURER_PATH = new Path(SSS_PATH, "Manufacturer");
	private static final Path SSS_MODEL_PATH = new Path(SSS_PATH, "Model");
	private static final Path SSS_ACCURACY_PATH = new Path(SSS_PATH, "Accuracy");
	private static final Path SSS_PRECISION_PATH = new Path(SSS_PATH, "Precision");
	private static final Path SSS_CALIBRATION_PATH = new Path(SSS_PATH, "Calibration");
	private static final Path SSS_COMMENTS_PATH = new Path(SSS_PATH, "Other_Comments");

	private static final Path DATA_SET_REFS_PATH = new Path(null, "Data_set_References");
	private static final Path ADD_INFO_PATH = new Path(null, "Additional_Information");
	private static final Path CITATION_PATH = new Path(null, "Citation");
	private static final Path MEAS_CALIB_REPORT_PATH = new Path(null, "Measurement_and_Calibration_Report");
	private static final Path PRELIM_QC_PATH = new Path(null, "Preliminary_Quality_control");
	private static final Path FORM_TYPE_PATH = new Path(null, "form_type");
	private static final Path RECORD_ID_PATH = new Path(null, "record_id");
	
	/**
	 * Names of fields in the data file headers
	 */
	public static final String USER_NAME_STRING = "user_name";
	public static final String USER_ORGANIZATION_STRING = "user_organization";
	public static final String USER_ADDRESS_STRING = "user_address";
	public static final String USER_PHONE_STRING = "user_phone";
	public static final String USER_EMAIL_STRING = "user_email";
	
	public static final String DATASET_ID_STRING = "dataset_id";
	public static final String FUNDING_INFO_STRING = "funding_info";
	
	public static final String EXPERIMENT_NAME_STRING = "experiment_name";
	public static final String EXPERIMENT_TYPE_STRING = "experiment_type";
	public static final String PLATFORM_TYPE_STRING = "platform_type";
	public static final String CO2_INSTRUMENT_TYPE_STRING = "co2_instrument_type";
	public static final String MOORING_ID_STRING = "mooring_id";
	
	public static final String EXPO_CODE_STRING = "expocode";
	public static final String CRUISE_ID_STRING = "cruise_id";
	public static final String SUB_CRUISE_INFO_STRING = "cruise_info";
	public static final String SECTION_STRING = "section";
	
	public static final String GEO_REGION_STRING = "geographical_region";
	public static final String WEST_BOUND_STRING = "westernmost_longitude";
	public static final String EAST_BOUND_STRING = "easternmost_longitude";
	public static final String NORTH_BOUND_STRING = "northernmost_latitude";
	public static final String SOUTH_BOUND_STRING = "southernmost_latitude";
	
	public static final String TEMP_START_DATE_STRING = "temporal_coverage_start_date";
	public static final String TEMP_END_DATE_STRING = "temporal_coverage_end_date";
	public static final String START_DATE_STRING = "cruise_start_date";
	public static final String END_DATE_STRING = "cruise_end_date";
	
	public static final String VESSEL_NAME_STRING = "vessel_name";
	public static final String VESSEL_ID_STRING = "vessel_id";
	public static final String COUNTRY_STRING = "country";
	public static final String OWNER_STRING = "vessel_owner";
	
	public static final String XCO2_WATER_EQU_DRY_STRING = "xco2_water_equ_dry_unit";
	public static final String XCO2_WATER_SST_DRY_STRING = "xco2_water_sst_dry_unit";
	public static final String PCO2_WATER_EQU_WET_STRING = "pco2_water_equ_wet_unit";
	public static final String PCO2_WATER_SST_WET_STRING = "pco2_water_sst_wet_unit";
	public static final String FCO2_WATER_EQU_WET_STRING = "fco2_water_equ_wet_unit";
	public static final String FCO2_WATER_SST_WET_STRING = "fco2_water_sst_wet_unit";
	public static final String XCO2_AIR_DRY_STRING = "xco2_air_dry_unit";
	public static final String PCO2_AIR_WET_STRING = "pco2_air_wet_unit";
	public static final String FCO2_AIR_WET_STRING = "fco2_air_wet_unit";
	public static final String XCO2_AIR_DRY_INTERP_STRING = "xco2_air_dry_interpolated_unit";
	public static final String PCO2_AIR_WET_INTERP_STRING = "pco2_air_wet_interpolated_unit";
	public static final String FCO2_AIR_WET_INTERP_STRING = "fco2_air_wet_interpolated_unit";
	
	public static final String INTAKE_DEPTH_STRING = "depth_of_seawater_intake";
	public static final String INTAKE_LOCATION_STRING = "location_of_seawater_intake";
	public static final String EQUI_TYPE_STRING = "equilibrator_type";
	public static final String EQUI_VOLUME_STRING = "equilibrator_volume";
	public static final String WATER_FLOW_RATE_STRING = "water_flow_rate";
	public static final String GAS_FLOW_RATE_STRING = "headspace_gas_flow_rate";
	public static final String VENTED_STRING = "vented";
	public static final String DRYING_METHOD_STRING = "drying_method_for_co2_in_water";
	public static final String EQUI_ADDITIONAL_INFO_STRING = "equilibrator_additional_information";
	
	public static final String MARINE_AIR_MEASUREMENT_STRING = "co2_in_marine_air_measurement";
	public static final String MARINE_AIR_LOCATION_STRING = "co2_in_marine_air_location_and_height";
	public static final String MARINE_AIR_DRYING_STRING = "co2_in_marine_air_drying_method";
	
	public static final String CO2_MEASUREMENT_METHOD_STRING = "co2_measurement_method";
	public static final String CO2_MANUFACTURER_STRING = "co2_manufacturer";
	public static final String CO2_MODEL_STRING = "co2_model";
	public static final String CO2_FREQUENCY_STRING = "co2_frequency";
	public static final String CO2_WATER_RES_STRING = "co2_resolution_water";
	public static final String CO2_WATER_UNC_STRING = "co2_uncertainty_water";
	public static final String CO2_AIR_RES_STRING = "co2_resolution_air";
	public static final String CO2_AIR_UNC_STRING = "co2_uncertainty_air";
	public static final String CO2_CALIBRATION_MANUFACTURER_STRING = "co2_manufacturer_of_calibration_gas";
	public static final String CO2_SENSOR_CALIBRATION_STRING = "co2_sensor_calibration";
	public static final String ENVIRONMENTAL_CONTROL_STRING = "co2_environmental_control";
	public static final String METHOD_REFS_STRING = "co2_method_references";
	public static final String DETAILS_OF_CO2_SENSING_STRING = "details_of_co2_sensing";
	public static final String ANALYSIS_OF_COMPARISON_STRING = "analyswesternmost_longitudeis_of_co2_comparison";
	public static final String MEASURED_CO2_PARAMS_STRING = "measured_co2_params";
	
	public static final String SST_LOCATION_STRING = "sst_location";
	public static final String SST_MANUFACTURER_STRING = "sst_manufacturer";
	public static final String SST_MODEL_STRING = "sst_model";
	public static final String SST_ACCURACY_STRING = "sst_accuracy";
	public static final String SST_PRECISION_STRING = "sst_precision";
	public static final String SST_CALIBRATION_STRING = "sst_calibration";
	public static final String SST_COMMENTS_STRING = "sst_other_comments";
	
	public static final String EQT_LOCATION_STRING = "equ_temperature_location";
	public static final String EQT_MANUFACTURER_STRING = "equ_temperature_manufacturer";
	public static final String EQT_MODEL_STRING = "equ_temperature_model";
	public static final String EQT_ACCURACY_STRING = "equ_temperature_accuracy";
	public static final String EQT_PRECISION_STRING = "equ_temperature_precision";
	public static final String EQT_CALIBRATION_STRING = "equ_temperature_calibration";
	public static final String EQT_WARMING_STRING = "equ_temperature_warming";
	public static final String EQT_COMMENTS_STRING = "equ_temperature_other_comments";	
	
	public static final String EQP_LOCATION_STRING = "equ_pressure_location";
	public static final String EQP_MANUFACTURER_STRING = "equ_pressure_manufacturer";
	public static final String EQP_MODEL_STRING = "equ_pressure_model";
	public static final String EQP_ACCURACY_STRING = "equ_pressure_accuracy";
	public static final String EQP_PRECISION_STRING = "equ_pressure_precision";
	public static final String EQP_CALIBRATION_STRING = "equ_pressure_calibration";
	public static final String EQP_COMMENTS_STRING = "equ_pressure_other_comments";
	public static final String EQP_NORMALIZED_STRING = "equ_pressure_normalized";
		
	public static final String ATM_LOCATION_STRING = "atm_pressure_location";
	public static final String ATM_MANUFACTURER_STRING = "atm_pressure_manufacturer";
	public static final String ATM_MODEL_STRING = "atm_pressure_model";
	public static final String ATM_ACCURACY_STRING = "atm_pressure_accuracy";
	public static final String ATM_PRECISION_STRING = "atm_pressure_precision";
	public static final String ATM_CALIBRATION_STRING = "atm_pressure_calibration";
	public static final String ATM_COMMENTS_STRING = "atm_pressure_other_comments";
	
	public static final String SSS_LOCATION_STRING = "sss_location";
	public static final String SSS_MANUFACTURER_STRING = "sss_manufacturer";
	public static final String SSS_MODEL_STRING = "sss_model";
	public static final String SSS_ACCURACY_STRING = "sss_accuracy";
	public static final String SSS_PRECISION_STRING = "sss_precision";
	public static final String SSS_CALIBRATION_STRING = "sss_calibration";
	public static final String SSS_COMMENTS_STRING = "sss_other_comments";

	public static final String DATA_SET_REFS_STRING = "data_set_references";
	public static final String ADD_INFO_STRING = "additional_information";
	public static final String CITATION_STRING = "citation";
	public static final String MEAS_CALIB_REPORT_STRING = "measurement_and_calibration_report";
	public static final String PRELIM_QC_STRING = "preliminary_quality_control";
	
	public static final String INVESTIGATOR_NAME = "name";
	public static final String INVESTIGATOR_ORGANIZATION = "organization";
	public static final String INVESTIGATOR_ADDRESS = "address";
	public static final String INVESTIGATOR_PHONE = "phone";
	public static final String INVESTIGATOR_EMAIL = "email";
	
	public static final String VARIABLES_NAME = "variable_name";
	public static final String VARIABLES_DESCRIPTION = "description_of_variable";
	
	public static final String OTHER_SENSORS_MANUFACTURER = "manufacturer";
	public static final String OTHER_SENSORS_MODEL = "model";
	public static final String OTHER_SENSORS_ACCURACY = "accuracy";
	public static final String OTHER_SENSORS_RESOLUTION = "resolution";
	public static final String OTHER_SENSORS_CALIBRATION = "calibration";
	public static final String OTHER_SENSORS_COMMENTS = "other_comments";
	
	/**
	 * Variables holding info about composite values
	 */
	private static final String INVESTIGATOR_COMP_NAME = "investigator";
	private static final String INVESTIGATOR_ELEMENT_NAME = "Investigator";
	private static final Path INVESTIGATORS_PATH = new Path(null, INVESTIGATOR_ELEMENT_NAME);
	private static final ArrayList<String> INVESTIGATOR_ENTRIES = 
			new ArrayList<String>(Arrays.asList(INVESTIGATOR_NAME, 
												INVESTIGATOR_ORGANIZATION, 
												INVESTIGATOR_ADDRESS, 
												INVESTIGATOR_PHONE, 
												INVESTIGATOR_EMAIL));
	private static final ArrayList<String> INVESTIGATOR_ID_LIST = 
			new ArrayList<String>(Arrays.asList(INVESTIGATOR_NAME, 
												INVESTIGATOR_EMAIL));
	
	private static final String VARIABLE_COMP_NAME = "variable";
	private static final String VARIABLES_INFO_ELEMENT_NAME = "Variables_Info";
	private static final String VARIABLE_ELEMENT_NAME = "Variable";
	private static final Path VARIABLES_INFO_PATH = new Path(null, VARIABLES_INFO_ELEMENT_NAME, VARIABLE_ELEMENT_NAME);
	private static final ArrayList<String> VARIABLES_INFO_ENTRIES =
			new ArrayList<String>(Arrays.asList(VARIABLES_NAME, 
												VARIABLES_DESCRIPTION));
	private static final ArrayList<String> VARIABLES_INFO_ID_LIST =
			new ArrayList<String>(Arrays.asList(VARIABLES_NAME));
	
	private static final String OTHER_SENSOR_COMP_NAME = "other_sensor";
	private static final String OTHER_SENSORS_ELEMENT_NAME = "Other_Sensors";
	private static final String SENSOR_ELEMENT_NAME = "Sensor";
	private static final Path OTHER_SENSORS_PATH = 
			new Path(new Path(null, METHOD_DESCRIPTION_ELEMENT_NAME, OTHER_SENSORS_ELEMENT_NAME), SENSOR_ELEMENT_NAME);
	private static final ArrayList<String> OTHER_SENSORS_ENTRIES = 
			new ArrayList<String>(Arrays.asList(OTHER_SENSORS_MANUFACTURER,
												OTHER_SENSORS_MODEL,
												OTHER_SENSORS_ACCURACY,
												OTHER_SENSORS_RESOLUTION,
												OTHER_SENSORS_CALIBRATION,
												OTHER_SENSORS_COMMENTS));
	private static final ArrayList<String> OTHER_SENSORS_ID_LIST = 
			new ArrayList<String>(Arrays.asList(OTHER_SENSORS_MANUFACTURER,
												OTHER_SENSORS_MODEL));
	
	/**
	 * The EXPO Code that this OmeMetadata object is related to.
	 */
	private String itsExpoCode;

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
	private boolean itIsDraft;
	
	// <User>
	private OMEVariable userName;
	private OMEVariable userOrganization;
	private OMEVariable userAddress;
	private OMEVariable userPhone;
	private OMEVariable userEmail;
	
	// <Investigator>
	private List<OMECompositeVariable> investigators;
	
	// <Dataset Info>
	private OMEVariable datasetID;
	private OMEVariable fundingInfo;
	
	// <DatasetInfo><Submission_Dates>
	private OMEVariable initialSubmission;
	private OMEVariable revisedSubmission;
	
	// <Cruise_Info><Experiment>
	private OMEVariable experimentName;
	private OMEVariable experimentType;
	private OMEVariable platformType;
	private OMEVariable co2InstrumentType;
	private OMEVariable mooringId;
	
	// <Cruise_Info><Experiment><Cruise>
	private OMEVariable cruiseID;
	private OMEVariable cruiseInfo;
	private OMEVariable section;

	// These two come after Temporal_Coverage in the XML
	private OMEVariable cruiseStartDate;
	private OMEVariable cruiseEndDate;
	
	// Cruise_Info><Experiment><Cruise><Geographical_Coverage>
	private OMEVariable geographicalRegion;
	
	// <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
	private OMEVariable westmostLongitude;
	private OMEVariable eastmostLongitude;
	private OMEVariable northmostLatitude;
	private OMEVariable southmostLatitude;
	
	// <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
	private OMEVariable temporalCoverageStartDate;
	private OMEVariable temporalCoverageEndDate;
	
	
	// <Cruise_Info><Vessel>
	private OMEVariable vesselName;
	private OMEVariable vesselID;
	private OMEVariable country;
	private OMEVariable vesselOwner;
	
	// <Variables_Info>
	private ArrayList<OMECompositeVariable> variablesInfo;
	
	// Units stuff: <CO2_Data_Info><xxx><Unit>
	private OMEVariable xCO2WaterEquDryUnit;
	private OMEVariable xCO2WaterSSTDryUnit;
	private OMEVariable pCO2WaterEquWetUnit;
	private OMEVariable pCO2WaterSSTWetUnit;
	private OMEVariable fCO2WaterEquWetUnit;
	private OMEVariable fCO2WaterSSTWetUnit;
	private OMEVariable xCO2AirDryUnit;
	private OMEVariable pCO2AirWetUnit;
	private OMEVariable fCO2AirWetUnit;
	private OMEVariable xCO2AirDryInterpolatedUnit;
	private OMEVariable pCO2AirWetInterpolatedUnit;
	private OMEVariable fCO2AirWetInterpolatedUnit;
	
	// <Method_Description><Equilibrator_Design>
	private OMEVariable depthOfSeaWaterIntake;
	private OMEVariable locationOfSeaWaterIntake;
	private OMEVariable equilibratorType;
	private OMEVariable equilibratorVolume;
	private OMEVariable waterFlowRate;
	private OMEVariable headspaceGasFlowRate;
	private OMEVariable vented;
	private OMEVariable dryingMethodForCO2InWater;
	private OMEVariable equAdditionalInformation;
	
	// <Method_Description><CO2_in_Marine_Air>
	private OMEVariable co2InMarineAirMeasurement;
	private OMEVariable co2InMarineAirLocationAndHeight;
	private OMEVariable co2InMarineAirDryingMethod;
	
	// <Method_Description><CO2_Sensors><CO2_Sensor>
	private OMEVariable co2MeasurementMethod;
	private OMEVariable co2Manufacturer;
	private OMEVariable co2Model;
	private OMEVariable co2Frequency;
	private OMEVariable co2ResolutionWater;
	private OMEVariable co2UncertaintyWater;
	private OMEVariable co2ResolutionAir;
	private OMEVariable co2UncertaintyAir;
	private OMEVariable co2ManufacturerOfCalibrationGas;
	private OMEVariable co2SensorCalibration;
	private OMEVariable co2EnvironmentalControl;
	private OMEVariable co2MethodReferences;
	private OMEVariable detailsOfCO2Sensing;
	private OMEVariable analysisOfCO2Comparison;
	private OMEVariable measuredCO2Params;
	
	// <Method_Description><Sea_Surface_Temperature>
	private OMEVariable sstLocation;
	private OMEVariable sstManufacturer;
	private OMEVariable sstModel;
	private OMEVariable sstAccuracy;
	private OMEVariable sstPrecision;
	private OMEVariable sstCalibration;
	private OMEVariable sstOtherComments;
	
	// <Method_Description><Equilibrator_Temperature>
	private OMEVariable eqtLocation;
	private OMEVariable eqtManufacturer;
	private OMEVariable eqtModel;
	private OMEVariable eqtAccuracy;
	private OMEVariable eqtPrecision;
	private OMEVariable eqtCalibration;
	private OMEVariable eqtWarming;
	private OMEVariable eqtOtherComments;

	// <Method_Description><Equilibrator_Pressure>
	private OMEVariable eqpLocation;
	private OMEVariable eqpManufacturer;
	private OMEVariable eqpModel;
	private OMEVariable eqpAccuracy;
	private OMEVariable eqpPrecision;
	private OMEVariable eqpCalibration;
	private OMEVariable eqpOtherComments;
	private OMEVariable eqpNormalized;
	
	// <Method_Description><Atmospheric_Pressure>
	private OMEVariable atpLocation;
	private OMEVariable atpManufacturer;
	private OMEVariable atpModel;
	private OMEVariable atpAccuracy;
	private OMEVariable atpPrecision;
	private OMEVariable atpCalibration;
	private OMEVariable atpOtherComments;
	
	// <Method_Description><Sea_Surface_Salinity>
	private OMEVariable sssLocation;
	private OMEVariable sssManufacturer;
	private OMEVariable sssModel;
	private OMEVariable sssAccuracy;
	private OMEVariable sssPrecision;
	private OMEVariable sssCalibration;
	private OMEVariable sssOtherComments;
	
	// <Method_Description><Other_Sensors>
	private ArrayList<OMECompositeVariable> otherSensors;
	
	// Root element
	private OMEVariable dataSetReferences;
	private OMEVariable additionalInformation;
	private OMEVariable citation;
	private OMEVariable measurementAndCalibrationReport;
	private OMEVariable preliminaryQualityControl;
	
	private OMEVariable form_type;
	private OMEVariable recordID;

	/**
	 * Creates an empty OME metadata document with the given expocode.
	 * @param expoCode expocode to use
	 */
	public OmeMetadata(String expoCode) {
		// Use assignFromOmeXmlDoc to initialize all the OMEVariables with empty values
		// This avoids having to do an insane amount of null-checking everywhere
		itsExpoCode = expoCode.toUpperCase();
		Element cruiseIdElem = new Element(CRUISE_ID_ELEMENT_NAME).setText(itsExpoCode);
		Element cruiseElem = new Element(CRUISE_ELEMENT_NAME).addContent(cruiseIdElem);
		Element experimentElem = new Element(EXPERIMENT_ELEMENT_NAME).addContent(cruiseElem);
		Element cruiseInfoElem = new Element(CRUISE_INFO_ELEMENT_NAME).addContent(experimentElem);
		Element rootElem = new Element(ROOT_ELEMENT_NAME).addContent(cruiseInfoElem);
		Document emptyDoc = new Document(rootElem);
		try {
			assignFromOmeXmlDoc(emptyDoc);
		} catch (BadEntryNameException ex) {
			// Should never happen, so throw a RunTime exception if it does
			throw new RuntimeException(ex);
		}

		// Use setExpocode to also assign any fields associated with the expocode
		setExpocode(itsExpoCode);
	}
	
	/**
	 * Determines whether or not this OME Metadata is in draft status
	 * @return {@code true} if it is in draft status; {@code false} otherwise
	 */
	public boolean isDraft() {
		return itIsDraft;
	}
	
	/**
	 * Sets the draft flag
	 * @param draft The value for the draft flag
 	 */
	public void setDraft(boolean draft) {
		itIsDraft = draft;
	}
	
	/**
	 * @return the experiment name
	 */
	public String getExperimentName() {
		return experimentName.getValue();
	}

	/**
	 * @return the west-most longitude
	 */
	public String getWestmostLongitude() {
		return westmostLongitude.getValue();
	}

	/**
	 * @return the east-most longitude
	 */
	public String getEastmostLongitude() {
		return eastmostLongitude.getValue();
	}

	/**
	 * @return the south-most latitude
	 */
	public String getSouthmostLatitude() {
		return southmostLatitude.getValue();
	}

	/**
	 * @return the north-most latitude
	 */
	public String getNorthmostLatitude() {
		return northmostLatitude.getValue();
	}

	/**
	 * @return the start date
	 */
	public String getTemporalCoverageStartDate() {
		return temporalCoverageStartDate.getValue();
	}

	/**
	 * @return the end date
	 */
	public String getTemporalCoverageEndDate() {
		return temporalCoverageEndDate.getValue();
	}

	/**
	 * @return the vessel name
	 */
	public String getVesselName() {
		return vesselName.getValue();
	}

	/**
	 * @return the list of investigator names
	 */
	public ArrayList<String> getInvestigators() {
		ArrayList<String> investigatorsList = new ArrayList<String>(investigators.size());
		for ( OMECompositeVariable invst : investigators ) {
			investigatorsList.add(invst.getValue(INVESTIGATOR_NAME));
		}
		return investigatorsList;
	}

	/**
	 * @return the list of investigator organizations
	 */
	public ArrayList<String> getOrganizations() {
		ArrayList<String> organizationsList = new ArrayList<String>(investigators.size());
		for ( OMECompositeVariable invst : investigators ) {
			organizationsList.add(invst.getValue(INVESTIGATOR_ORGANIZATION));
		}
		return organizationsList;
	}

	
	public void assignFromHeaderText(String header) throws OmeMetadataException, BadEntryNameException {
		String[] lines = header.split("\n");
		
		String compositeName = null;
		OMECompositeVariable compositeVar = null;
		String name = null;
		String value = null;
		
		// Run through each line in turn
		int lineCount = 0;
		for (String inputLine: lines) {
			
			lineCount++;
			HeaderLine line = new HeaderLine(inputLine);
			
			// If the line doesn't contain any delimiter characters...
			if (line.doesntContain("=[]")) {
				
				// Lines with no delimiters belong with the previous line.
				// If there isn't one, then we have a bad format.
				if (null == name) {
					throw new OmeMetadataException("Orphaned line " + lineCount + ": \"" + line + "\"");
				} else {
					// Add it to the existing value
					value = value + "\n" + line.toString();
				}
			} else {
				
				// The line contains a delimiter.
				
				if (line.contains("]")) {
					if (line.length() != 1) {
						// The ] delimiter can only exist on its own
						throw new OmeMetadataException(lineCount, "Lines with special character ']' cannot contain anything else \"" + line + "\"");
					} else if (null == compositeName) {
						// If there's no composite name, we're trying to end a composite value without starting one!
						throw new OmeMetadataException(lineCount, "Ending bracket ']' without a starting bracket");
					} else {
						
						compositeVar.addEntry(name, value);
						
						// Add the composite variable to the object's data
						if (compositeName.equalsIgnoreCase(INVESTIGATOR_COMP_NAME)) {
							investigators.add(compositeVar);
						} else if (compositeName.equalsIgnoreCase(VARIABLE_COMP_NAME)) {
							variablesInfo.add(compositeVar);
						} else if (compositeName.equalsIgnoreCase(OTHER_SENSOR_COMP_NAME)) {
							otherSensors.add(compositeVar);
						} else {
							// Unrecognised composite name
							throw new OmeMetadataException(lineCount, "End of unrecognized composite entry '" + compositeName + "'");
						}
						
						compositeName = null;
						compositeVar = null;
						name = null;
						value = null;
					}
					
				} else if (line.contains("[")) {
					
					// This is the start of a composite. If the line also contains = or ], it's bad
					if (line.contains("=]")) {
						throw new OmeMetadataException(lineCount, "Composite identifier '[' cannot be mixed with '=' or ']'");
					} else if (line.containsMultiple("[")) {
						throw new OmeMetadataException(lineCount, "Composite identifier '[' cannot be mixed with '=' or ']'");
					} else if (line.getCharIndex("[") != line.length() - 1) {
						throw new OmeMetadataException(lineCount, "Composite identifier '[' must be at the end of the line");
					} else if (null != compositeName) {
						throw new OmeMetadataException(lineCount, "Cannot nest composite values");
					} else {
						
						// We are beginning a new composite. Store the existing name and value,
						// and record the composite name
						
						if (null != name) {
							storeValue(name, value, lineCount);
							name = null;
							value = null;
						}
						
						compositeName = line.getBefore("[");
						
						if (compositeName.equalsIgnoreCase(INVESTIGATOR_COMP_NAME)) {
							compositeVar = new OMECompositeVariable(INVESTIGATORS_PATH, INVESTIGATOR_ENTRIES, INVESTIGATOR_ID_LIST);
						} else if (compositeName.equalsIgnoreCase(VARIABLE_COMP_NAME)) {
							compositeVar = new OMECompositeVariable(VARIABLES_INFO_PATH, VARIABLES_INFO_ENTRIES, VARIABLES_INFO_ID_LIST);
						} else if (compositeName.equalsIgnoreCase(OTHER_SENSOR_COMP_NAME)) {
							compositeVar = new OMECompositeVariable(OTHER_SENSORS_PATH, OTHER_SENSORS_ENTRIES, OTHER_SENSORS_ID_LIST);
						}
					}
				} else if (line.contains("=")) {
					
					if (line.containsMultiple("=")) {
						throw new OmeMetadataException(lineCount, "Cannot have more than one '=' on a line");
					} else {
						
						// New name/value. Store the existing ones.
						if (null != name) {
							if (null != compositeVar) {
								compositeVar.addEntry(name, value);
							} else {
								storeValue(name, value, lineCount);
							}
						}
						
						name = line.getBefore("=");
						value = line.getAfter("=");
					}
				}
			}
		}
		
		// Tidy up
		if (null != compositeVar) {
			throw new OmeMetadataException("Header ends in the middle of a composite");
		} else {
			storeValue(name, value, lineCount);
		}
	}
	
	public void storeCompositeValue(String name, Properties values, int line) throws OmeMetadataException, BadEntryNameException {
		
		OMECompositeVariable compositeVar = null;
		
		if (name.equalsIgnoreCase(INVESTIGATOR_COMP_NAME)) {
			compositeVar = new OMECompositeVariable(INVESTIGATORS_PATH, INVESTIGATOR_ENTRIES, INVESTIGATOR_ID_LIST);
		} else if (name.equalsIgnoreCase(VARIABLE_COMP_NAME)) {
			compositeVar = new OMECompositeVariable(VARIABLES_INFO_PATH, VARIABLES_INFO_ENTRIES, VARIABLES_INFO_ID_LIST);
		} else if (name.equalsIgnoreCase(OTHER_SENSOR_COMP_NAME)) {
			compositeVar = new OMECompositeVariable(OTHER_SENSORS_PATH, VARIABLES_INFO_ENTRIES, OTHER_SENSORS_ID_LIST);
		}

		for (String propName : values.stringPropertyNames()) {
			compositeVar.addEntry(propName, values.getProperty(propName));
		}
		
		
		// Add the composite variable to the object's data
		if (name.equalsIgnoreCase(INVESTIGATOR_COMP_NAME)) {
			investigators.add(compositeVar);
		} else if (name.equalsIgnoreCase(VARIABLE_COMP_NAME)) {
			variablesInfo.add(compositeVar);
		} else if (name.equalsIgnoreCase(OTHER_SENSOR_COMP_NAME)) {
			otherSensors.add(compositeVar);
		} else {
			// Unrecognised composite name
			throw new OmeMetadataException(line, "End of unrecognized composite entry '" + name + "'");
		}

	}

	public void replaceValue(String name, String value, int lineCount) throws OmeMetadataException {
		storeValue(name, value, lineCount, true);
	}
	
	public void storeValue(String name, String value, int lineCount) throws OmeMetadataException {
		storeValue(name, value, lineCount, false);
	}
	
	private void storeValue(String name, String value, int lineCount, boolean replace) throws OmeMetadataException {
		
		// Reject composites
		if ( name.equalsIgnoreCase(INVESTIGATOR_COMP_NAME) || 
			 name.equalsIgnoreCase(VARIABLE_COMP_NAME) || 
			 name.equalsIgnoreCase(OTHER_SENSOR_COMP_NAME)) {
			throw new OmeMetadataException(lineCount, "This name is reserved for composite values");
		}

		switch (name.toLowerCase()) {
		case USER_NAME_STRING:
		{
			userName = setValue(userName, USER_NAME_PATH, name, value, lineCount, replace);
			break;
		}
		case USER_ORGANIZATION_STRING:
		{
			userOrganization = setValue(userOrganization, USER_ORGANIZATION_PATH, name, value, lineCount, replace);
			break;
		}
		case USER_ADDRESS_STRING:
		{
			userAddress = setValue(userAddress, USER_ADDRESS_PATH, name, value, lineCount, replace);
			break;
		}
		case USER_PHONE_STRING:
		{
			userPhone = setValue(userPhone, USER_PHONE_PATH, name, value, lineCount, replace);
			break;
		}
		case USER_EMAIL_STRING:
		{
			userEmail = setValue(userEmail, USER_EMAIL_PATH, name, value, lineCount, replace);
			break;
		}
		case DATASET_ID_STRING:
		{
			datasetID = setValue(datasetID, DATASET_ID_PATH, name, value, lineCount, replace);
			break;
		}
		case FUNDING_INFO_STRING:
		{
			fundingInfo = setValue(fundingInfo, FUNDING_INFO_PATH, name, value, lineCount, replace);
			break;
		}
		case EXPERIMENT_NAME_STRING:
		{
			experimentName = setValue(experimentName, EXPERIMENT_NAME_PATH, name, value, lineCount, replace);
			break;
		}
		case EXPERIMENT_TYPE_STRING:
		{
			experimentType = setValue(experimentType, EXPERIMENT_TYPE_PATH, name, value, lineCount, replace);
			break;
		}
		case PLATFORM_TYPE_STRING:
		{
			platformType = setValue(platformType, PLATFORM_TYPE_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_INSTRUMENT_TYPE_STRING:
		{
			co2InstrumentType = setValue(co2InstrumentType, CO2_INSTRUMENT_TYPE_PATH, name, value, lineCount, replace);
			break;
		}
		case MOORING_ID_STRING:
		{
			mooringId = setValue(mooringId, MOORING_ID_PATH, name, value, lineCount, replace);
			break;
		}
		case EXPO_CODE_STRING:
		case CRUISE_ID_STRING:
		{
			itsExpoCode = value;
			cruiseID = setValue(cruiseID, CRUISE_ID_PATH, name, value, lineCount, replace);
			break;
		}
		case SUB_CRUISE_INFO_STRING:
		{
			cruiseInfo = setValue(cruiseInfo, SUB_CRUISE_INFO_PATH, name, value, lineCount, replace);
			break;
		}
		case SECTION_STRING:
		{
			section = setValue(section, SECTION_PATH, name, value, lineCount, replace);
			break;
		}
		case WEST_BOUND_STRING:
		{
			westmostLongitude = setValue(westmostLongitude, WEST_BOUND_PATH, name, value, lineCount, replace);
			break;
		}
		case EAST_BOUND_STRING:
		{
			eastmostLongitude = setValue(eastmostLongitude, EAST_BOUND_PATH, name, value, lineCount, replace);
			break;
		}
		case NORTH_BOUND_STRING:
		{
			northmostLatitude = setValue(northmostLatitude, NORTH_BOUND_PATH, name, value, lineCount, replace);
			break;
		}
		case SOUTH_BOUND_STRING:
		{
			southmostLatitude = setValue(southmostLatitude, SOUTH_BOUND_PATH, name, value, lineCount, replace);
			break;
		}
		case START_DATE_STRING:
		{
			cruiseStartDate = setValue(cruiseStartDate, START_DATE_PATH, name, value, lineCount, replace);
			break;
		}
		case END_DATE_STRING:
		{
			cruiseEndDate = setValue(cruiseEndDate, END_DATE_PATH, name, value, lineCount, replace);
			break;
		}
		case TEMP_START_DATE_STRING:
		{
			temporalCoverageStartDate = setValue(temporalCoverageStartDate, TEMP_START_DATE_PATH, name, value, lineCount, replace);
			break;
		}
		case TEMP_END_DATE_STRING:
		{
			temporalCoverageEndDate = setValue(temporalCoverageEndDate, TEMP_END_DATE_PATH, name, value, lineCount, replace);
			break;
		}
		case GEO_REGION_STRING:
		{
			geographicalRegion = setValue(geographicalRegion, GEO_REGION_PATH, name, value, lineCount, replace);
			break;
		}
		case VESSEL_NAME_STRING:
		{
			vesselName = setValue(vesselName, VESSEL_NAME_PATH, name, value, lineCount, replace);
			break;
		}
		case VESSEL_ID_STRING:
		{
			vesselID = setValue(vesselID, VESSEL_ID_PATH, name, value, lineCount, replace);
			break;
		}
		case COUNTRY_STRING:
		{
			country = setValue(country, COUNTRY_PATH, name, value, lineCount, replace);
			break;
		}
		case OWNER_STRING:
		{
			vesselOwner = setValue(vesselOwner, OWNER_PATH, name, value, lineCount, replace);
			break;
		}
		case XCO2_WATER_EQU_DRY_STRING:
		{
			xCO2WaterEquDryUnit = setValue(xCO2WaterEquDryUnit, XCO2_WATER_EQU_DRY_PATH, name, value, lineCount, replace);
			break;
		}
		case XCO2_WATER_SST_DRY_STRING:
		{
			xCO2WaterSSTDryUnit = setValue(xCO2WaterSSTDryUnit, XCO2_WATER_SST_DRY_PATH, name, value, lineCount, replace);
			break;
		}
		case PCO2_WATER_EQU_WET_STRING:
		{
			pCO2WaterEquWetUnit = setValue(pCO2WaterEquWetUnit, PCO2_WATER_EQU_WET_PATH, name, value, lineCount, replace);
			break;
		}
		case PCO2_WATER_SST_WET_STRING:
		{
			pCO2WaterSSTWetUnit = setValue(pCO2WaterSSTWetUnit, PCO2_WATER_SST_WET_PATH, name, value, lineCount, replace);
			break;
		}
		case FCO2_WATER_EQU_WET_STRING:
		{
			fCO2WaterEquWetUnit = setValue(fCO2WaterEquWetUnit, FCO2_WATER_EQU_WET_PATH, name, value, lineCount, replace);
			break;
		}
		case FCO2_WATER_SST_WET_STRING:
		{
			fCO2WaterSSTWetUnit = setValue(fCO2WaterSSTWetUnit, FCO2_WATER_SST_WET_PATH, name, value, lineCount, replace);
			break;
		}
		case XCO2_AIR_DRY_STRING:
		{
			xCO2AirDryUnit = setValue(xCO2AirDryUnit, XCO2_AIR_DRY_PATH, name, value, lineCount, replace);
			break;
		}
		case PCO2_AIR_WET_STRING:
		{
			pCO2AirWetUnit = setValue(pCO2AirWetUnit, PCO2_AIR_WET_PATH, name, value, lineCount, replace);
			break;
		}
		case FCO2_AIR_WET_STRING:
		{
			fCO2AirWetUnit = setValue(fCO2AirWetUnit, FCO2_AIR_WET_PATH, name, value, lineCount, replace);
			break;
		}
		case XCO2_AIR_DRY_INTERP_STRING:
		{
			xCO2AirDryInterpolatedUnit = setValue(xCO2AirDryInterpolatedUnit, XCO2_AIR_DRY_INTERP_PATH, name, value, lineCount, replace);
			break;
		}
		case PCO2_AIR_WET_INTERP_STRING:
		{
			pCO2AirWetInterpolatedUnit = setValue(pCO2AirWetInterpolatedUnit, PCO2_AIR_WET_INTERP_PATH, name, value, lineCount, replace);
			break;
		}
		case FCO2_AIR_WET_INTERP_STRING:
		{
			fCO2AirWetInterpolatedUnit = setValue(fCO2AirWetInterpolatedUnit, FCO2_AIR_WET_INTERP_PATH, name, value, lineCount, replace);
			break;
		}
		case INTAKE_DEPTH_STRING:
		{
			depthOfSeaWaterIntake = setValue(depthOfSeaWaterIntake, INTAKE_DEPTH_PATH, name, value, lineCount, replace);
			break;
		}
		case INTAKE_LOCATION_STRING:
		{
			locationOfSeaWaterIntake = setValue(locationOfSeaWaterIntake, INTAKE_LOCATION_PATH, name, value, lineCount, replace);
			break;
		}
		case EQUI_TYPE_STRING:
		{
			equilibratorType = setValue(equilibratorType, EQUI_TYPE_PATH, name, value, lineCount, replace);
			break;
		}
		case EQUI_VOLUME_STRING:
		{
			equilibratorVolume = setValue(equilibratorVolume, EQUI_VOLUME_PATH, name, value, lineCount, replace);
			break;
		}
		case WATER_FLOW_RATE_STRING:
		{
			waterFlowRate = setValue(waterFlowRate, WATER_FLOW_RATE_PATH, name, value, lineCount, replace);
			break;
		}
		case GAS_FLOW_RATE_STRING:
		{
			headspaceGasFlowRate = setValue(headspaceGasFlowRate, GAS_FLOW_RATE_PATH, name, value, lineCount, replace);
			break;
		}
		case VENTED_STRING:
		{
			vented = setValue(vented, VENTED_PATH, name, value, lineCount, replace);
			break;
		}
		case DRYING_METHOD_STRING:
		{
			dryingMethodForCO2InWater = setValue(dryingMethodForCO2InWater, DRYING_METHOD_PATH, name, value, lineCount, replace);
			break;
		}
		case EQUI_ADDITIONAL_INFO_STRING:
		{
			equAdditionalInformation = setValue(equAdditionalInformation, EQUI_ADDITIONAL_INFO_PATH, name, value, lineCount, replace);
			break;
		}
		case MARINE_AIR_MEASUREMENT_STRING:
		{
			co2InMarineAirMeasurement = setValue(co2InMarineAirMeasurement, MARINE_AIR_MEASUREMENT_PATH, name, value, lineCount, replace);
			break;
		}
		case MARINE_AIR_LOCATION_STRING:
		{
			co2InMarineAirLocationAndHeight = setValue(co2InMarineAirLocationAndHeight, MARINE_AIR_LOCATION_PATH, name, value, lineCount, replace);
			break;
		}
		case MARINE_AIR_DRYING_STRING:
		{
			co2InMarineAirDryingMethod = setValue(co2InMarineAirDryingMethod, MARINE_AIR_DRYING_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_MEASUREMENT_METHOD_STRING:
		{
			co2MeasurementMethod = setValue(co2MeasurementMethod, CO2_MEASUREMENT_METHOD_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_MANUFACTURER_STRING:
		{
			co2Manufacturer = setValue(co2Manufacturer, CO2_MANUFACTURER_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_MODEL_STRING:
		{
			co2Model = setValue(co2Model, CO2_MODEL_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_FREQUENCY_STRING:
		{
			co2Frequency = setValue(co2Frequency, CO2_FREQUENCY_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_WATER_RES_STRING:
		{
			co2ResolutionWater = setValue(co2ResolutionWater, CO2_WATER_RES_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_WATER_UNC_STRING:
		{
			co2UncertaintyWater = setValue(co2UncertaintyWater, CO2_WATER_UNC_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_AIR_RES_STRING:
		{
			co2ResolutionAir = setValue(co2ResolutionAir, CO2_AIR_RES_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_AIR_UNC_STRING:
		{
			co2UncertaintyAir = setValue(co2UncertaintyAir, CO2_AIR_UNC_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_CALIBRATION_MANUFACTURER_STRING:
		{
			co2ManufacturerOfCalibrationGas = setValue(co2ManufacturerOfCalibrationGas, CO2_CALIBRATION_MANUFACTURER_PATH, name, value, lineCount, replace);
			break;
		}
		case CO2_SENSOR_CALIBRATION_STRING:
		{
			co2SensorCalibration = setValue(co2SensorCalibration, CO2_SENSOR_CALIBRATION_PATH, name, value, lineCount, replace);
			break;
		}
		case ENVIRONMENTAL_CONTROL_STRING:
		{
			co2EnvironmentalControl = setValue(co2EnvironmentalControl, ENVIRONMENTAL_CONTROL_PATH, name, value, lineCount, replace);
			break;
		}
		case METHOD_REFS_STRING:
		{
			co2MethodReferences = setValue(co2MethodReferences, METHOD_REFS_PATH, name, value, lineCount, replace);
			break;
		}
		case DETAILS_OF_CO2_SENSING_STRING:
		{
			detailsOfCO2Sensing = setValue(detailsOfCO2Sensing, DETAILS_OF_CO2_SENSING_PATH, name, value, lineCount, replace);
			break;
		}
		case ANALYSIS_OF_COMPARISON_STRING:
		{
			analysisOfCO2Comparison = setValue(analysisOfCO2Comparison, ANALYSIS_OF_COMPARISON_PATH, name, value, lineCount, replace);
			break;
		}
		case MEASURED_CO2_PARAMS_STRING:
		{
			measuredCO2Params = setValue(measuredCO2Params, MEASURED_CO2_PARAMS_PATH, name, value, lineCount, replace);
			break;
		}
		case SST_LOCATION_STRING:
		{
			sstLocation = setValue(sstLocation, SST_LOCATION_PATH, name, value, lineCount, replace);
			break;
		}
		case SST_MANUFACTURER_STRING:
		{
			sstManufacturer = setValue(sstManufacturer, SST_MANUFACTURER_PATH, name, value, lineCount, replace);
			break;
		}
		case SST_MODEL_STRING:
		{
			sstModel = setValue(sstModel, SST_MODEL_PATH, name, value, lineCount, replace);
			break;
		}
		case SST_ACCURACY_STRING:
		{
			sstAccuracy = setValue(sstAccuracy, SST_ACCURACY_PATH, name, value, lineCount, replace);
			break;
		}
		case SST_PRECISION_STRING:
		{
			sstPrecision = setValue(sstPrecision, SST_PRECISION_PATH, name, value, lineCount, replace);
			break;
		}
		case SST_CALIBRATION_STRING:
		{
			sstCalibration = setValue(sstCalibration, SST_CALIBRATION_PATH, name, value, lineCount, replace);
			break;
		}
		case SST_COMMENTS_STRING:
		{
			sstOtherComments = setValue(sstOtherComments, SST_COMMENTS_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_LOCATION_STRING:
		{
			eqtLocation = setValue(eqtLocation, EQT_LOCATION_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_MANUFACTURER_STRING:
		{
			eqtManufacturer = setValue(eqtManufacturer, EQT_MANUFACTURER_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_MODEL_STRING:
		{
			eqtModel = setValue(eqtModel, EQT_MODEL_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_ACCURACY_STRING:
		{
			eqtAccuracy = setValue(eqtAccuracy, EQT_ACCURACY_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_PRECISION_STRING:
		{
			eqtPrecision = setValue(eqtPrecision, EQT_PRECISION_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_CALIBRATION_STRING:
		{
			eqtCalibration = setValue(eqtCalibration, EQT_CALIBRATION_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_WARMING_STRING:
		{
			eqtWarming = setValue(eqtWarming, EQT_WARMING_PATH, name, value, lineCount, replace);
			break;
		}
		case EQT_COMMENTS_STRING:
		{
			eqtOtherComments = setValue(eqtOtherComments, EQT_COMMENTS_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_LOCATION_STRING:
		{
			eqpLocation = setValue(eqpLocation, EQP_LOCATION_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_MANUFACTURER_STRING:
		{
			eqpManufacturer = setValue(eqpManufacturer, EQP_MANUFACTURER_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_MODEL_STRING:
		{
			eqpModel = setValue(eqpModel, EQP_MODEL_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_ACCURACY_STRING:
		{
			eqpAccuracy = setValue(eqpAccuracy, EQP_ACCURACY_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_PRECISION_STRING:
		{
			eqpPrecision = setValue(eqpPrecision, EQP_PRECISION_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_CALIBRATION_STRING:
		{
			eqpCalibration = setValue(eqpCalibration, EQP_CALIBRATION_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_COMMENTS_STRING:
		{
			eqpOtherComments = setValue(eqpOtherComments, EQP_COMMENTS_PATH, name, value, lineCount, replace);
			break;
		}
		case EQP_NORMALIZED_STRING:
		{
			eqpNormalized = setValue(eqpNormalized, EQP_NORMALIZED_PATH, name, value, lineCount, replace);
			break;
		}
		case ATM_LOCATION_STRING:
		{
			atpLocation = setValue(atpLocation, ATM_LOCATION_PATH, name, value, lineCount, replace);
			break;
		}
		case ATM_MANUFACTURER_STRING:
		{
			atpManufacturer = setValue(atpManufacturer, ATM_MANUFACTURER_PATH, name, value, lineCount, replace);
			break;
		}
		case ATM_MODEL_STRING:
		{
			atpModel = setValue(atpModel, ATM_MODEL_PATH, name, value, lineCount, replace);
			break;
		}
		case ATM_ACCURACY_STRING:
		{
			atpAccuracy = setValue(atpAccuracy, ATM_ACCURACY_PATH, name, value, lineCount, replace);
			break;
		}
		case ATM_PRECISION_STRING:
		{
			atpPrecision = setValue(atpPrecision, ATM_PRECISION_PATH, name, value, lineCount, replace);
			break;
		}
		case ATM_CALIBRATION_STRING:
		{
			atpCalibration = setValue(atpCalibration, ATM_CALIBRATION_PATH, name, value, lineCount, replace);
			break;
		}
		case ATM_COMMENTS_STRING:
		{
			atpOtherComments = setValue(atpOtherComments, ATM_COMMENTS_PATH, name, value, lineCount, replace);
			break;
		}
		case SSS_LOCATION_STRING:
		{
			sssLocation = setValue(sssLocation, SSS_LOCATION_PATH, name, value, lineCount, replace);
			break;
		}
		case SSS_MANUFACTURER_STRING:
		{
			sssManufacturer = setValue(sssManufacturer, SSS_MANUFACTURER_PATH, name, value, lineCount, replace);
			break;
		}
		case SSS_MODEL_STRING:
		{
			sssModel = setValue(sssModel, SSS_MODEL_PATH, name, value, lineCount, replace);
			break;
		}
		case SSS_ACCURACY_STRING:
		{
			sssAccuracy = setValue(sssAccuracy, SSS_ACCURACY_PATH, name, value, lineCount, replace);
			break;
		}
		case SSS_PRECISION_STRING:
		{
			sssPrecision = setValue(sssPrecision, SSS_PRECISION_PATH, name, value, lineCount, replace);
			break;
		}
		case SSS_CALIBRATION_STRING:
		{
			sssCalibration = setValue(sssCalibration, SSS_CALIBRATION_PATH, name, value, lineCount, replace);
			break;
		}
		case SSS_COMMENTS_STRING:
		{
			sssOtherComments = setValue(sssOtherComments, SSS_COMMENTS_PATH, name, value, lineCount, replace);
			break;
		}
		case DATA_SET_REFS_STRING:
		{
			dataSetReferences = setValue(dataSetReferences, DATA_SET_REFS_PATH, name, value, lineCount, replace);
			break;
		}
		case ADD_INFO_STRING:
		{
			additionalInformation = setValue(additionalInformation, ADD_INFO_PATH, name, value, lineCount, replace);
			break;
		}
		case CITATION_STRING:
		{
			citation = setValue(citation, CITATION_PATH, name, value, lineCount, replace);
			break;
		}
		case MEAS_CALIB_REPORT_STRING:
		{
			measurementAndCalibrationReport = setValue(measurementAndCalibrationReport, MEAS_CALIB_REPORT_PATH, name, value, lineCount, replace);
			break;
		}
		case PRELIM_QC_STRING:
		{
			preliminaryQualityControl = setValue(preliminaryQualityControl, PRELIM_QC_PATH, name, value, lineCount, replace);
			break;
		}
		default:
		{
			throw new OmeMetadataException(lineCount, "Unrecognised variable name '" + name + "'");
		}
		}
	}
	
	public String getValue(String name) throws OmeMetadataException {

		// Reject composites
		if ( name.equalsIgnoreCase(INVESTIGATOR_COMP_NAME) || 
			 name.equalsIgnoreCase(VARIABLE_COMP_NAME) || 
			 name.equalsIgnoreCase(OTHER_SENSOR_COMP_NAME)) {
			throw new OmeMetadataException("Cannot retrieve data from composite values");
		} 

		String result;
		switch (name.toLowerCase()) {
		case USER_NAME_STRING:
			result = userName.getValue();
			break;
		case USER_ORGANIZATION_STRING:
			result = userOrganization.getValue();
			break;
		case USER_ADDRESS_STRING:
			result = userAddress.getValue();
			break;
		case USER_PHONE_STRING:
			result = userPhone.getValue();
			break;
		case USER_EMAIL_STRING:
			result = userEmail.getValue();
			break;
		case DATASET_ID_STRING:
			result = datasetID.getValue();
			break;
		case FUNDING_INFO_STRING:
			result = fundingInfo.getValue();
			break;
		case EXPERIMENT_NAME_STRING:
			result = experimentName.getValue();
			break;
		case EXPERIMENT_TYPE_STRING:
			result = experimentType.getValue();
			break;
		case PLATFORM_TYPE_STRING:
			result = platformType.getValue();
			break;
		case CO2_INSTRUMENT_TYPE_STRING:
			result = co2InstrumentType.getValue();
			break;
		case MOORING_ID_STRING:
			result = mooringId.getValue();
			break;
		case EXPO_CODE_STRING:
		case CRUISE_ID_STRING:
			result = itsExpoCode;
			break;
		case SUB_CRUISE_INFO_STRING:
			result = cruiseInfo.getValue();
			break;
		case SECTION_STRING:
			result = section.getValue();
			break;
		case START_DATE_STRING:
			result = cruiseStartDate.getValue();
			break;
		case END_DATE_STRING:
			result = cruiseEndDate.getValue();
			break;
		case TEMP_START_DATE_STRING:
			result = temporalCoverageStartDate.getValue();
			break;
		case TEMP_END_DATE_STRING:
			result = temporalCoverageEndDate.getValue();
			break;
		case GEO_REGION_STRING:
			result = geographicalRegion.getValue();
			break;
		case VESSEL_NAME_STRING:
			result = vesselName.getValue();
			break;
		case VESSEL_ID_STRING:
			result = vesselID.getValue();
			break;
		case COUNTRY_STRING:
			result = country.getValue();
			break;
		case OWNER_STRING:
			result = vesselOwner.getValue();
			break;
		case XCO2_WATER_EQU_DRY_STRING:
			result = xCO2WaterEquDryUnit.getValue();
			break;
		case XCO2_WATER_SST_DRY_STRING:
			result = xCO2WaterSSTDryUnit.getValue();
			break;
		case PCO2_WATER_EQU_WET_STRING:
			result = pCO2WaterEquWetUnit.getValue();
			break;
		case PCO2_WATER_SST_WET_STRING:
			result = pCO2WaterSSTWetUnit.getValue();
			break;
		case FCO2_WATER_EQU_WET_STRING:
			result = fCO2WaterEquWetUnit.getValue();
			break;
		case FCO2_WATER_SST_WET_STRING:
			result = fCO2WaterSSTWetUnit.getValue();
			break;
		case XCO2_AIR_DRY_STRING:
			result = xCO2AirDryUnit.getValue();
			break;
		case PCO2_AIR_WET_STRING:
			result = pCO2AirWetUnit.getValue();
			break;
		case FCO2_AIR_WET_STRING:
			result = fCO2AirWetUnit.getValue();
			break;
		case XCO2_AIR_DRY_INTERP_STRING:
			result = xCO2AirDryInterpolatedUnit.getValue();
			break;
		case PCO2_AIR_WET_INTERP_STRING:
			result = pCO2AirWetInterpolatedUnit.getValue();
			break;
		case FCO2_AIR_WET_INTERP_STRING:
			result = pCO2AirWetInterpolatedUnit.getValue();
			break;
		case INTAKE_DEPTH_STRING:
			result = depthOfSeaWaterIntake.getValue();
			break;
		case INTAKE_LOCATION_STRING:
			result = locationOfSeaWaterIntake.getValue();
			break;
		case EQUI_TYPE_STRING:
			result = equilibratorType.getValue();
			break;
		case EQUI_VOLUME_STRING:
			result = equilibratorVolume.getValue();
			break;
		case WATER_FLOW_RATE_STRING:
			result = waterFlowRate.getValue();
			break;
		case GAS_FLOW_RATE_STRING:
			result = headspaceGasFlowRate.getValue();
			break;
		case VENTED_STRING:
			result = vented.getValue();
			break;
		case DRYING_METHOD_STRING:
			result = dryingMethodForCO2InWater.getValue();
			break;
		case EQUI_ADDITIONAL_INFO_STRING:
			result = equAdditionalInformation.getValue();
			break;
		case MARINE_AIR_MEASUREMENT_STRING:
			result = co2InMarineAirMeasurement.getValue();
			break;
		case MARINE_AIR_LOCATION_STRING:
			result = co2InMarineAirLocationAndHeight.getValue();
			break;
		case MARINE_AIR_DRYING_STRING:
			result = co2InMarineAirDryingMethod.getValue();
			break;
		case CO2_MEASUREMENT_METHOD_STRING:
			result = co2MeasurementMethod.getValue();
			break;
		case CO2_MANUFACTURER_STRING:
			result = co2Manufacturer.getValue();
			break;
		case CO2_MODEL_STRING:
			result = co2Model.getValue();
			break;
		case CO2_FREQUENCY_STRING:
			result = co2Frequency.getValue();
			break;
		case CO2_WATER_RES_STRING:
			result = co2ResolutionWater.getValue();
			break;
		case CO2_WATER_UNC_STRING:
			result = co2UncertaintyWater.getValue();
			break;
		case CO2_AIR_RES_STRING:
			result = co2ResolutionAir.getValue();
			break;
		case CO2_AIR_UNC_STRING:
			result = co2UncertaintyAir.getValue();
			break;
		case CO2_CALIBRATION_MANUFACTURER_STRING:
			result = co2ManufacturerOfCalibrationGas.getValue();
			break;
		case CO2_SENSOR_CALIBRATION_STRING:
			result = co2SensorCalibration.getValue();
			break;
		case ENVIRONMENTAL_CONTROL_STRING:
			result = co2EnvironmentalControl.getValue();
			break;
		case METHOD_REFS_STRING:
			result = co2MethodReferences.getValue();
			break;
		case DETAILS_OF_CO2_SENSING_STRING:
			result = detailsOfCO2Sensing.getValue();
			break;
		case ANALYSIS_OF_COMPARISON_STRING:
			result = analysisOfCO2Comparison.getValue();
			break;
		case MEASURED_CO2_PARAMS_STRING:
			result = measuredCO2Params.getValue();
			break;
		case SST_LOCATION_STRING:
			result = sstLocation.getValue();
			break;
		case SST_MANUFACTURER_STRING:
			result = sstManufacturer.getValue();
			break;
		case SST_MODEL_STRING:
			result = sstModel.getValue();
			break;
		case SST_ACCURACY_STRING:
			result = sstAccuracy.getValue();
			break;
		case SST_PRECISION_STRING:
			result = sstPrecision.getValue();
			break;
		case SST_CALIBRATION_STRING:
			result = sstCalibration.getValue();
			break;
		case SST_COMMENTS_STRING:
			result = sstOtherComments.getValue();
			break;
		case EQT_LOCATION_STRING:
			result = eqtLocation.getValue();
			break;
		case EQT_MANUFACTURER_STRING:
			result = eqtManufacturer.getValue();
			break;
		case EQT_MODEL_STRING:
			result = eqtModel.getValue();
			break;
		case EQT_ACCURACY_STRING:
			result = eqtAccuracy.getValue();
			break;
		case EQT_PRECISION_STRING:
			result = eqtPrecision.getValue();
			break;
		case EQT_CALIBRATION_STRING:
			result = eqtCalibration.getValue();
			break;
		case EQT_WARMING_STRING:
			result = eqtWarming.getValue();
			break;
		case EQT_COMMENTS_STRING:
			result = eqtOtherComments.getValue();
			break;
		case EQP_LOCATION_STRING:
			result = eqpLocation.getValue();
			break;
		case EQP_MANUFACTURER_STRING:
			result = eqpManufacturer.getValue();
			break;
		case EQP_MODEL_STRING:
			result = eqpModel.getValue();
			break;
		case EQP_ACCURACY_STRING:
			result = eqpAccuracy.getValue();
			break;
		case EQP_PRECISION_STRING:
			result = eqpPrecision.getValue();
			break;
		case EQP_CALIBRATION_STRING:
			result = eqpCalibration.getValue();
			break;
		case EQP_COMMENTS_STRING:
			result = eqpOtherComments.getValue();
			break;
		case EQP_NORMALIZED_STRING:
			result = eqpNormalized.getValue();
			break;
		case ATM_LOCATION_STRING:
			result = atpLocation.getValue();
			break;
		case ATM_MANUFACTURER_STRING:
			result = atpManufacturer.getValue();
			break;
		case ATM_MODEL_STRING:
			result = atpModel.getValue();
			break;
		case ATM_ACCURACY_STRING:
			result = atpAccuracy.getValue();
			break;
		case ATM_PRECISION_STRING:
			result = atpPrecision.getValue();
			break;
		case ATM_CALIBRATION_STRING:
			result = atpCalibration.getValue();
			break;
		case ATM_COMMENTS_STRING:
			result = atpOtherComments.getValue();
			break;
		case SSS_LOCATION_STRING:
			result = sssLocation.getValue();
			break;
		case SSS_MANUFACTURER_STRING:
			result = sssManufacturer.getValue();
			break;
		case SSS_MODEL_STRING:
			result = sssModel.getValue();
			break;
		case SSS_ACCURACY_STRING:
			result = sssAccuracy.getValue();
			break;
		case SSS_PRECISION_STRING:
			result = sssPrecision.getValue();
			break;
		case SSS_CALIBRATION_STRING:
			result = sssCalibration.getValue();
			break;
		case SSS_COMMENTS_STRING:
			result = sssOtherComments.getValue();
			break;
		case DATA_SET_REFS_STRING:
			result = dataSetReferences.getValue();
			break;
		case ADD_INFO_STRING:
			result = additionalInformation.getValue();
			break;
		case CITATION_STRING:
			result = citation.getValue();
			break;
		case MEAS_CALIB_REPORT_STRING:
			result = measurementAndCalibrationReport.getValue();
			break;
		case PRELIM_QC_STRING:
			result = preliminaryQualityControl.getValue();
			break;
		default:
			throw new OmeMetadataException("Unrecognised variable name '" + name + "'");
		}

		return result;
	}

	
	private OMEVariable setValue(OMEVariable target, Path path, String name, String value, int lineCount, boolean replace) throws OmeMetadataException {
		
		OMEVariable result = null;
		
		if (replace || null == target) {
			result = new OMEVariable(path, value);
		} else {
			target.addValue(value);
			result = target;
		}
		
		return result;
	}
	
	/**
	 * Validates that the expocode given for this metadata object matches the 
	 * expocode given in the given OME XML document, then assigns the fields
	 * in this object from this document.
	 * 
	 * @param omeDoc
	 * 		OME XML Document to use
	 */
	public void assignFromOmeXmlDoc(Document omeDoc) throws BadEntryNameException {
		
		Element rootElem = omeDoc.getRootElement();

		/*
		 * First we extract the EXPO Code, which is the Cruise_ID. If we don't have this
		 * then we can't get anywhere.
		 * 
		 * This is the only element that's accessed out of order.
		 * All the others are done in the order they appear in the XML.
		 */
		Element cruiseInfoElem = rootElem.getChild(CRUISE_INFO_ELEMENT_NAME);
		if ( cruiseInfoElem == null )
			throw new BadEntryNameException(
					"No Cruise_Info element in the OME XML contents");

		Element experimentElem = cruiseInfoElem.getChild(EXPERIMENT_ELEMENT_NAME);
		if ( experimentElem == null )
			throw new BadEntryNameException(
					"No Cruise_Info->Experiment element in the OME XML contents");
		
		Element cruiseElem = experimentElem.getChild(CRUISE_ELEMENT_NAME);
		if ( cruiseElem == null )
			throw new BadEntryNameException(
					"No Cruise_Info->Experiment->Cruise " +
					"element in the OME XML contents");
		
		String cruiseIDText = cruiseElem.getChildTextTrim(CRUISE_ID_ELEMENT_NAME);
		if ( cruiseIDText == null )
			throw new BadEntryNameException(
					"No Cruise_Info->Experiment->Cruise->Cruise_ID " +
					"element in the OME XML contents");
		
		if ( itsExpoCode.length() == 0) {
			itsExpoCode = cruiseIDText.toUpperCase();
		} else if ( ! itsExpoCode.equals(cruiseIDText.toUpperCase()) )
			throw new BadEntryNameException("Expocode of cruise (" + 
					itsExpoCode + ") does not match that the Cruise ID in " +
					"the OME document (" + cruiseID + ")");
		
		cruiseID = new OMEVariable(CRUISE_ID_PATH, cruiseIDText);
		
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
		Element statusElem = rootElem.getChild(STATUS_ELEMENT_NAME);
		if (null != statusElem) {
			String statusString = statusElem.getTextTrim();
			if (statusString.equalsIgnoreCase(DRAFT_ELEMENT_NAME)) {
				itIsDraft = true;
			}
		}
		
		// <User>
		Element userElem = rootElem.getChild(USER_ELEMENT_NAME);
			
		userName = new OMEVariable(USER_NAME_PATH, userElem);
		userOrganization = new OMEVariable(USER_ORGANIZATION_PATH, userElem);
		userAddress = new OMEVariable(USER_ADDRESS_PATH, userElem);
		userPhone = new OMEVariable(USER_PHONE_PATH, userElem);
		userEmail = new OMEVariable(USER_EMAIL_PATH, userElem);
		
		// End <User>

		// <Investigator> (repeating element)
		investigators = new ArrayList<OMECompositeVariable>();
		for (Element invElem : rootElem.getChildren(INVESTIGATOR_ELEMENT_NAME)) {
			OMECompositeVariable invDetails = new OMECompositeVariable(INVESTIGATORS_PATH, INVESTIGATOR_ENTRIES, INVESTIGATOR_ID_LIST);
			invDetails.addEntry(INVESTIGATOR_NAME, invElem);
			invDetails.addEntry(INVESTIGATOR_ORGANIZATION, invElem);
			invDetails.addEntry(INVESTIGATOR_ADDRESS, invElem);
			invDetails.addEntry(INVESTIGATOR_PHONE, invElem);
			invDetails.addEntry(INVESTIGATOR_EMAIL, invElem);
			
			investigators.add(invDetails);
		}
		// End <Investigator>
		
		// <DataSet_Info>
		Element dataSetInfoElem = rootElem.getChild(DATASET_INFO_ELEMENT_NAME);
		
		datasetID = new OMEVariable(DATASET_ID_PATH, dataSetInfoElem);
		fundingInfo = new OMEVariable(FUNDING_INFO_PATH, dataSetInfoElem);
		
		// <DataSet_Info><Submission_Dates>
		Element submissionDatesElem = null;
		if (null != dataSetInfoElem) {
			submissionDatesElem = dataSetInfoElem.getChild(SUBMISSION_DATES_ELEMENT_NAME);
		}
		
		initialSubmission = new OMEVariable(INITIAL_SUBMISSION_PATH, submissionDatesElem);
		revisedSubmission = new OMEVariable(REVISED_SUBMISSION_PATH, submissionDatesElem);

		// End <DataSet_Info></Submission_Dates<
		
		// End <DataSet_Info>
		
		// <Cruise_Info>
		// <Cruise_Info><Experiment>
		
		// The Cruise_Info and Experiment elements were created above to get the EXPO code
		// We know they exist, otherwise we wouldn't have got this far.
		
		experimentName = new OMEVariable(EXPERIMENT_NAME_PATH, experimentElem);
		experimentType = new OMEVariable(EXPERIMENT_TYPE_PATH, experimentElem);
		platformType = new OMEVariable(PLATFORM_TYPE_PATH, experimentElem);
		co2InstrumentType = new OMEVariable(CO2_INSTRUMENT_TYPE_PATH, experimentElem);
		mooringId = new OMEVariable(MOORING_ID_PATH, experimentElem);
		
		// <Cruise_Info><Experiment><Cruise>
		
		// CruiseID has already been assigned above
		cruiseInfo = new OMEVariable(SUB_CRUISE_INFO_PATH, cruiseElem);
		section = new OMEVariable(SECTION_PATH, cruiseElem);
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		Element geogCoverageElem = cruiseElem.getChild(GEO_COVERAGE_ELEMENT_NAME);
		
		geographicalRegion = new OMEVariable(GEO_REGION_PATH, geogCoverageElem);
			
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		Element boundsElem = null;
		if (null != geogCoverageElem) {
			boundsElem = geogCoverageElem.getChild(BOUNDS_ELEMENT_NAME);
		}
		westmostLongitude = new OMEVariable(WEST_BOUND_PATH, boundsElem);
		eastmostLongitude = new OMEVariable(EAST_BOUND_PATH, boundsElem);
		northmostLatitude = new OMEVariable(NORTH_BOUND_PATH, boundsElem);
		southmostLatitude = new OMEVariable(SOUTH_BOUND_PATH, boundsElem);
	
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		
		// <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		Element tempCoverageElem = cruiseElem.getChild(TEMP_COVERAGE_ELEMENT_NAME);
		
		temporalCoverageStartDate = new OMEVariable(TEMP_START_DATE_PATH, tempCoverageElem);
		temporalCoverageEndDate = new OMEVariable(TEMP_END_DATE_PATH, tempCoverageElem);
		// End <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		
		cruiseStartDate = new OMEVariable(START_DATE_PATH, cruiseElem);
		cruiseEndDate = new OMEVariable(END_DATE_PATH, cruiseElem);
		
		// End <Cruise_Info><Experiment><Cruise>
		
		// End <Cruise_Info><Experiment>
		
		// <Cruise_Info><Vessel>
		
		Element vesselElem = cruiseInfoElem.getChild(VESSEL_ELEMENT_NAME);
		
		vesselName = new OMEVariable(VESSEL_NAME_PATH, vesselElem);
		vesselID = new OMEVariable(VESSEL_ID_PATH, vesselElem);
		country = new OMEVariable(COUNTRY_PATH, vesselElem);
		vesselOwner = new OMEVariable(OWNER_PATH, vesselElem);
		
		// <Variables_Info>
		
		// The contents of this are a repeating sub-element, so live in a list of OMECompositeVariables.
		variablesInfo = new ArrayList<OMECompositeVariable>();
		Element varsInfoElem = rootElem.getChild(VARIABLES_INFO_ELEMENT_NAME);
		if (null != varsInfoElem) {
			for (Element variableElem : varsInfoElem.getChildren(VARIABLE_ELEMENT_NAME)) {
				
				OMECompositeVariable varDetails = new OMECompositeVariable(VARIABLES_INFO_PATH, VARIABLES_INFO_ENTRIES, VARIABLES_INFO_ID_LIST);
				varDetails.addEntry(VARIABLES_NAME, variableElem);
				varDetails.addEntry(VARIABLES_DESCRIPTION, variableElem);
				
				variablesInfo.add(varDetails);
			}
		}
		
		// End <Variables_Info>
		
		// <CO2_Data_Info>
		Element co2DataInfoElem = rootElem.getChild(CO2_DATA_INFO_ELEMENT_NAME);
		
		// If the co2DataInfoElem is null, this is handled by extractSubElement
		xCO2WaterEquDryUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, XCO2_WATER_EQU_DRY_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		xCO2WaterSSTDryUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, XCO2_WATER_SST_DRY_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		pCO2WaterEquWetUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, PCO2_WATER_EQU_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		pCO2WaterSSTWetUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, PCO2_WATER_SST_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		fCO2WaterEquWetUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, FCO2_WATER_EQU_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		fCO2WaterSSTWetUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, FCO2_WATER_SST_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		xCO2AirDryUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, XCO2_AIR_DRY_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		pCO2AirWetUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, PCO2_AIR_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		fCO2AirWetUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, FCO2_AIR_WET_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		xCO2AirDryInterpolatedUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, XCO2_AIR_DRY_INTERP_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		pCO2AirWetInterpolatedUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, PCO2_AIR_WET_INTERP_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		fCO2AirWetInterpolatedUnit = extractSubElement(CO2_DATA_INFO_PATH, co2DataInfoElem, FCO2_AIR_WET_INTERP_ELEMENT_NAME, UNIT_ELEMENT_NAME);
		
		// End <CO2_Data_Info>
		
		// <Method_Description>
		Element methodDescriptionElem = rootElem.getChild(METHOD_DESCRIPTION_ELEMENT_NAME);

		// <Method_Description><Equilibrator_Design>
		Element equDesignElement = null;
		if (null != methodDescriptionElem) {
			equDesignElement = methodDescriptionElem.getChild(EQUILIBRATOR_DESIGN_ELEMENT_NAME);
		}

		depthOfSeaWaterIntake = new OMEVariable(INTAKE_DEPTH_PATH, equDesignElement);
		locationOfSeaWaterIntake = new OMEVariable(INTAKE_LOCATION_PATH, equDesignElement);
		equilibratorType = new OMEVariable(EQUI_TYPE_PATH, equDesignElement);
		equilibratorVolume = new OMEVariable(EQUI_VOLUME_PATH, equDesignElement);
		waterFlowRate = new OMEVariable(WATER_FLOW_RATE_PATH, equDesignElement);
		headspaceGasFlowRate = new OMEVariable(GAS_FLOW_RATE_PATH, equDesignElement);
		vented = new OMEVariable(VENTED_PATH, equDesignElement);
		dryingMethodForCO2InWater = new OMEVariable(DRYING_METHOD_PATH, equDesignElement);
		equAdditionalInformation = new OMEVariable(EQUI_ADDITIONAL_INFO_PATH, equDesignElement);
		
		// End <Method_Description><Equilibrator_Design>

		// <Method_Description><CO2_in_Marine_Air>
		Element co2MarineAirElem = null;
		if (null != methodDescriptionElem) {
			co2MarineAirElem = methodDescriptionElem.getChild(CO2_MARINE_AIR_ELEMENT_NAME);
		}

		co2InMarineAirMeasurement = new OMEVariable(MARINE_AIR_MEASUREMENT_PATH, co2MarineAirElem);
		co2InMarineAirLocationAndHeight = new OMEVariable(MARINE_AIR_LOCATION_PATH, co2MarineAirElem);
		co2InMarineAirDryingMethod = new OMEVariable(MARINE_AIR_DRYING_PATH, co2MarineAirElem);
		
		// End <Method_Description><CO2_in_Marine_Air>

		// <Method_Description><CO2_Sensors>
		Element co2SensorsElem = null;

		if (null != methodDescriptionElem) {
			co2SensorsElem = methodDescriptionElem.getChild(CO2_SENSORS_ELEMENT_NAME);
		}
		
		// <Method_Description><CO2_Sensors><CO2_Sensor>
		Element co2SensorElem = null;
		if (null != co2SensorsElem) {
			co2SensorElem = co2SensorsElem.getChild("CO2_Sensor");
		}

		co2MeasurementMethod = new OMEVariable(CO2_MEASUREMENT_METHOD_PATH, co2SensorElem);
		co2Manufacturer = new OMEVariable(CO2_MANUFACTURER_PATH, co2SensorElem);
		co2Model = new OMEVariable(CO2_MODEL_PATH, co2SensorElem);
		co2Frequency = new OMEVariable(CO2_FREQUENCY_PATH, co2SensorElem);
		co2ResolutionWater = new OMEVariable(CO2_WATER_RES_PATH, co2SensorElem);
		co2UncertaintyWater = new OMEVariable(CO2_WATER_UNC_PATH, co2SensorElem);
		co2ResolutionAir = new OMEVariable(CO2_AIR_RES_PATH, co2SensorElem);
		co2UncertaintyAir = new OMEVariable(CO2_AIR_UNC_PATH, co2SensorElem);
		co2ManufacturerOfCalibrationGas = new OMEVariable(CO2_CALIBRATION_MANUFACTURER_PATH, co2SensorElem);
		co2SensorCalibration = new OMEVariable(CO2_SENSOR_CALIBRATION_PATH, co2SensorElem);
		co2EnvironmentalControl = new OMEVariable(ENVIRONMENTAL_CONTROL_PATH, co2SensorElem);
		co2MethodReferences = new OMEVariable(METHOD_REFS_PATH, co2SensorElem);
		detailsOfCO2Sensing = new OMEVariable(DETAILS_OF_CO2_SENSING_PATH, co2SensorElem);
		analysisOfCO2Comparison = new OMEVariable(ANALYSIS_OF_COMPARISON_PATH, co2SensorElem);
		measuredCO2Params = new OMEVariable(MEASURED_CO2_PARAMS_PATH, co2SensorElem);

		// End <Method_Description><CO2_Sensors><CO2_Sensor>
		// End <Method_Description><CO2_Sensors>
		
		// <Method_Description><Sea_Surface_Temperature>
		Element sstElem = null;
		if (null != methodDescriptionElem) {
			sstElem = methodDescriptionElem.getChild(SST_ELEMENT_NAME);
		}
		
		sstLocation = new OMEVariable(SST_LOCATION_PATH, sstElem);
		sstManufacturer = new OMEVariable(SST_MANUFACTURER_PATH, sstElem);
		sstModel = new OMEVariable(SST_MODEL_PATH, sstElem);
		sstAccuracy = new OMEVariable(SST_ACCURACY_PATH, sstElem);
		sstPrecision = new OMEVariable(SST_PRECISION_PATH, sstElem);
		sstCalibration = new OMEVariable(SST_CALIBRATION_PATH, sstElem);
		sstOtherComments = new OMEVariable(SST_COMMENTS_PATH, sstElem);
		
		// End <Method_Description><Sea_Surface_Temperature>
		
		// <Method_Description><Equilibrator_Temperature>
		Element eqtElem = null;
		if (null != methodDescriptionElem) {
			eqtElem = methodDescriptionElem.getChild(EQU_TEMP_ELEMENT_NAME);
		}
				
		eqtLocation = new OMEVariable(EQT_LOCATION_PATH, eqtElem);
		eqtManufacturer = new OMEVariable(EQT_MANUFACTURER_PATH, eqtElem);
		eqtModel = new OMEVariable(EQT_MODEL_PATH, eqtElem);
		eqtAccuracy = new OMEVariable(EQT_ACCURACY_PATH, eqtElem);
		eqtPrecision = new OMEVariable(EQT_PRECISION_PATH, eqtElem);
		eqtCalibration = new OMEVariable(EQT_CALIBRATION_PATH, eqtElem);
		eqtWarming = new OMEVariable(EQT_WARMING_PATH, eqtElem);
		eqtOtherComments = new OMEVariable(EQT_COMMENTS_PATH, eqtElem);

		// End <Method_Description><Equilibrator_Temperature>
		
		// <Method_Description><Equilibrator_Pressure>
		Element eqpElem = null;
		if (null != methodDescriptionElem) {
			eqpElem = methodDescriptionElem.getChild(EQU_PRESSURE_ELEMENT_NAME);
		}
				
		eqpLocation = new OMEVariable(EQP_LOCATION_PATH, eqpElem);
		eqpManufacturer = new OMEVariable(EQP_MANUFACTURER_PATH, eqpElem);
		eqpModel = new OMEVariable(EQP_MODEL_PATH, eqpElem);
		eqpAccuracy = new OMEVariable(EQP_ACCURACY_PATH, eqpElem);
		eqpPrecision = new OMEVariable(EQP_PRECISION_PATH, eqpElem);
		eqpCalibration = new OMEVariable(EQP_CALIBRATION_PATH, eqpElem);
		eqpOtherComments = new OMEVariable(EQP_COMMENTS_PATH, eqpElem);
		eqpNormalized = new OMEVariable(EQP_NORMALIZED_PATH, eqpElem);
		
		// End <Method_Description><Equilibrator_Pressure>
		
		// <Method_Description><Atmospheric_Pressure>
		Element atpElem = null;
		if (null != methodDescriptionElem) {
			atpElem = methodDescriptionElem.getChild(ATM_PRESSURE_ELEMENT_NAME);
		}
				
		atpLocation = new OMEVariable(ATM_LOCATION_PATH, atpElem);
		atpManufacturer = new OMEVariable(ATM_MANUFACTURER_PATH, atpElem);
		atpModel = new OMEVariable(ATM_MODEL_PATH, atpElem);
		atpAccuracy = new OMEVariable(ATM_ACCURACY_PATH, atpElem);
		atpPrecision = new OMEVariable(ATM_PRECISION_PATH, atpElem);
		atpCalibration = new OMEVariable(ATM_CALIBRATION_PATH, atpElem);
		atpOtherComments = new OMEVariable(ATM_COMMENTS_PATH, atpElem);

		// End <Method_Description><Atmospheric_Pressure>
		
		// <Method_Description><Sea_Surface_Salinity>
		Element sssElem = null;
		if (null != methodDescriptionElem) {
			sssElem = methodDescriptionElem.getChild(SSS_ELEMENT_NAME);
		}
				
		sssLocation = new OMEVariable(SSS_LOCATION_PATH, sssElem);
		sssManufacturer = new OMEVariable(SSS_MANUFACTURER_PATH, sssElem);
		sssModel = new OMEVariable(SSS_MODEL_PATH, sssElem);
		sssAccuracy = new OMEVariable(SSS_ACCURACY_PATH, sssElem);
		sssPrecision = new OMEVariable(SSS_PRECISION_PATH, sssElem);
		sssCalibration = new OMEVariable(SSS_CALIBRATION_PATH, sssElem);
		sssOtherComments = new OMEVariable(SSS_COMMENTS_PATH, sssElem);

		// End <Method_Description><Sea_Surface_Salinity>
		
		// <Method_Description><Other_Sensors>
		otherSensors = new ArrayList<OMECompositeVariable>();
		Element otherSensorsElem = null;
		if (null != methodDescriptionElem) {
			otherSensorsElem = methodDescriptionElem.getChild(OTHER_SENSORS_ELEMENT_NAME);
		}
		if (null != otherSensorsElem) {
			for (Element sensorElem : otherSensorsElem.getChildren(SENSOR_ELEMENT_NAME)) {
				OMECompositeVariable sensorDetails = new OMECompositeVariable(OTHER_SENSORS_PATH, OTHER_SENSORS_ENTRIES, OTHER_SENSORS_ID_LIST);
				sensorDetails.addEntry(OTHER_SENSORS_MANUFACTURER, sensorElem);
				sensorDetails.addEntry(OTHER_SENSORS_MODEL, sensorElem);
				sensorDetails.addEntry(OTHER_SENSORS_ACCURACY, sensorElem);
				sensorDetails.addEntry(OTHER_SENSORS_RESOLUTION, sensorElem);
				sensorDetails.addEntry(OTHER_SENSORS_CALIBRATION, sensorElem);
				sensorDetails.addEntry(OTHER_SENSORS_COMMENTS, sensorElem);
				
				otherSensors.add(sensorDetails);
			}
		}
		
		// End <Method_Description><Other_Sensors>
		// End <Method_Description>
		
		
		// Miscellaneous tags under the root element
		
		dataSetReferences = new OMEVariable(DATA_SET_REFS_PATH, rootElem);
		additionalInformation = new OMEVariable(ADD_INFO_PATH, rootElem);
		citation = new OMEVariable(CITATION_PATH, rootElem);
		measurementAndCalibrationReport = new OMEVariable(MEAS_CALIB_REPORT_PATH, rootElem);
		preliminaryQualityControl = new OMEVariable(PRELIM_QC_PATH, rootElem);
		
		// More miscellaneous root tags
		form_type = new OMEVariable(FORM_TYPE_PATH, rootElem);
		recordID = new OMEVariable(RECORD_ID_PATH, rootElem);
		
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
		
		Element rootElem = new Element(ROOT_ELEMENT_NAME);
		ConflictElement conflictElem = new ConflictElement();
		
		// <Status>
		if (itIsDraft) {
			Element statusElem = new Element(STATUS_ELEMENT_NAME);
			statusElem.setText(DRAFT_ELEMENT_NAME);
			rootElem.addContent(statusElem);
		}
		
		// <User>
		Element userElem = new Element(USER_ELEMENT_NAME);

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
		Element datasetInfoElem = new Element(DATASET_INFO_ELEMENT_NAME);
		datasetID.generateXMLContent(datasetInfoElem,  conflictElem);
		fundingInfo.generateXMLContent(datasetInfoElem,  conflictElem);
		
		// <Dataset_Info><Submission_Dates>
		Element submissionElem = new Element(SUBMISSION_DATES_ELEMENT_NAME);
		initialSubmission.generateXMLContent(submissionElem, conflictElem);
		revisedSubmission.generateXMLContent(submissionElem, conflictElem);
		datasetInfoElem.addContent(submissionElem);
		
		// End <Dataset_Info><Submission_Dates>
		
		rootElem.addContent(datasetInfoElem);
		// End <Dataset_Info>
		
		// <Cruise_Info>
		Element cruiseInfoElem = new Element(CRUISE_INFO_ELEMENT_NAME);
		
		// <Cruise_Info><Experiment>
		Element experimentElem = new Element(EXPERIMENT_ELEMENT_NAME);
		
		experimentName.generateXMLContent(experimentElem, conflictElem);
		experimentType.generateXMLContent(experimentElem, conflictElem);
		platformType.generateXMLContent(experimentElem, conflictElem);
		co2InstrumentType.generateXMLContent(experimentElem, conflictElem);
		mooringId.generateXMLContent(experimentElem, conflictElem);
		
		// <Cruise_Info><Experiment><Cruise>
		Element cruiseElem = new Element(CRUISE_ELEMENT_NAME);
		
		cruiseID.generateXMLContent(cruiseElem, conflictElem);
		cruiseInfo.generateXMLContent(cruiseElem, conflictElem);
		section.generateXMLContent(cruiseElem, conflictElem);
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		Element geoCoverageElem = new Element(GEO_COVERAGE_ELEMENT_NAME);
		
		geographicalRegion.generateXMLContent(geoCoverageElem, conflictElem);
		
		// <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		Element boundsElem = new Element(BOUNDS_ELEMENT_NAME);
		westmostLongitude.generateXMLContent(boundsElem, conflictElem);
		eastmostLongitude.generateXMLContent(boundsElem, conflictElem);
		northmostLatitude.generateXMLContent(boundsElem, conflictElem);
		southmostLatitude.generateXMLContent(boundsElem, conflictElem);
		
		
		geoCoverageElem.addContent(boundsElem);
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		
		cruiseElem.addContent(geoCoverageElem);
		// End <Cruise_Info><Experiment><Cruise><Geographical_Coverage>
		
		// <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		Element tempCoverageElem = new Element(TEMP_COVERAGE_ELEMENT_NAME);
		
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
		Element vesselElem = new Element(VESSEL_ELEMENT_NAME);
		
		vesselName.generateXMLContent(vesselElem, conflictElem);
		vesselID.generateXMLContent(vesselElem, conflictElem);
		country.generateXMLContent(vesselElem, conflictElem);
		vesselOwner.generateXMLContent(vesselElem, conflictElem);
		
		cruiseInfoElem.addContent(vesselElem);
		// End <Cruise_Info><Vessel>
		
		rootElem.addContent(cruiseInfoElem);
		// End <Cruise_Info>

		// <Variables_Info>
		Element varsInfoElem = new Element(VARIABLES_INFO_ELEMENT_NAME);
		for (OMECompositeVariable varInfo : variablesInfo) {
			varInfo.generateXMLContent(varsInfoElem, conflictElem);
		}
		
		rootElem.addContent(varsInfoElem);
		// End <Variables_Info>
		
		
		// <CO2_Data_Info>
		Element co2DataInfoElem = new Element(CO2_DATA_INFO_ELEMENT_NAME);
		
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
		Element methodDescElem = new Element(METHOD_DESCRIPTION_ELEMENT_NAME);
		
		// <Method_Description><Equilibrator_Design>
		Element eqDesignElem = new Element(EQUILIBRATOR_DESIGN_ELEMENT_NAME);
		
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
		Element co2MarineAirElem = new Element(CO2_MARINE_AIR_ELEMENT_NAME);

		co2InMarineAirMeasurement.generateXMLContent(co2MarineAirElem, conflictElem);
		co2InMarineAirLocationAndHeight.generateXMLContent(co2MarineAirElem, conflictElem);
		co2InMarineAirDryingMethod.generateXMLContent(co2MarineAirElem, conflictElem);

		methodDescElem.addContent(co2MarineAirElem);
		// End <Method_Description><CO2_in_Marine_Air>
		
		// <Method_Description><CO2_Sensors>
		Element co2SensorsElem = new Element(CO2_SENSORS_ELEMENT_NAME);
		
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
		Element sstElem = new Element(SST_ELEMENT_NAME);
		
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
		Element eqtElem = new Element(EQU_TEMP_ELEMENT_NAME);
		
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
		Element eqpElem = new Element(EQU_PRESSURE_ELEMENT_NAME);
		
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
		Element atpElem = new Element(ATM_PRESSURE_ELEMENT_NAME);
		
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
		Element sssElem = new Element(SSS_ELEMENT_NAME);
		
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
		Element otherSensorsElem = new Element(OTHER_SENSORS_ELEMENT_NAME);
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
		form_type.generateXMLContent(rootElem, conflictElem);
		recordID.generateXMLContent(rootElem, conflictElem);
		
		// Add the CONFLICT element
		if (conflictElem.conflictsExist()) {
			rootElem.addContent(conflictElem);
		}
		return new Document(rootElem);
	}

	/**
	 * @return
	 * 		the expocode for this OME metadata
	 */
	public String getExpocode() {
		return itsExpoCode;
	}

	/**
	 * Assigns the expocode for this OME metadata.
	 * 
	 * @param expocode
	 * 		expocode to assign
	 */
	public void setExpocode(String expocode) {
		itsExpoCode = expocode.toUpperCase();
		String nodcCode;
		try {
			if ( Character.isLetter(itsExpoCode.charAt(4)) ) {
				nodcCode = itsExpoCode.substring(0, 5);
			}
			else {
				nodcCode = itsExpoCode.substring(0, 4);
			}
		} catch ( IndexOutOfBoundsException ex ) {
			nodcCode = "";
		}
		vesselID = new OMEVariable(vesselID.getPath(), nodcCode);
		cruiseID = new OMEVariable(cruiseID.getPath(), itsExpoCode);
		datasetID = new OMEVariable(datasetID.getPath(), itsExpoCode);
	}

	/**
	 * Checks the current contents to see if there is any conflicts in 
	 * any elements or if there is any missing content that is absolutely 
	 * required.  This ignores the draft flag and is intended to be a way 
	 * of updating the draft flag after merging.
	 * 
	 * @return
	 * 		true if there are no conflicts and all absolutely-required 
	 * 		content is present
	 */
	public boolean isAcceptable() {

		if ( cruiseID.hasConflict() ) {
			return false;
		}

		if ( userName.hasConflict() ||
			 userOrganization.hasConflict() ||
			 userAddress.hasConflict() ||
			 userPhone.hasConflict() ||
			 userEmail.hasConflict() ) {
			return false;
		}

		for (OMECompositeVariable compVar : investigators) {
			 if ( compVar.hasConflict() ) {
				 return false;
			 }
		}

		if ( datasetID.hasConflict() ||
			 fundingInfo.hasConflict() ) {
			return false;
		}

		if ( initialSubmission.hasConflict() ||
			 revisedSubmission.hasConflict() ) {
			return false;
		}

		if ( experimentName.hasConflict() ||
			 experimentType.hasConflict() ||
			 platformType.hasConflict() ||
			 co2InstrumentType.hasConflict() ||
			 mooringId.hasConflict() ) {
			return false;
		}

		if ( cruiseInfo.hasConflict() ||
			 section.hasConflict() ) {
			return false;
		}

		if ( geographicalRegion.hasConflict() ) {
			return false;
		}

		if ( westmostLongitude.hasConflict() ||
			 eastmostLongitude.hasConflict() ||
			 northmostLatitude.hasConflict() ||
			 southmostLatitude.hasConflict() ) {
			return false;
		}

		if ( temporalCoverageStartDate.hasConflict() ||
			 temporalCoverageEndDate.hasConflict() ) {
			return false;
		}

		if ( cruiseStartDate.hasConflict() ||
			 cruiseEndDate.hasConflict() ) {
			return false;
		}

		if ( vesselName.hasConflict() ||
			 vesselID.hasConflict() ||
			 country.hasConflict() ||
			 vesselOwner.hasConflict() ) {
			return false;
		}

		for (OMECompositeVariable compVar : variablesInfo) {
			 if ( compVar.hasConflict() ) {
				 return false;
			 }
		}

		if ( xCO2WaterEquDryUnit.hasConflict() ||
			 xCO2WaterSSTDryUnit.hasConflict() ||
			 pCO2WaterEquWetUnit.hasConflict() ||
			 pCO2WaterSSTWetUnit.hasConflict() ||
			 fCO2WaterEquWetUnit.hasConflict() ||
			 fCO2WaterSSTWetUnit.hasConflict() ||
			 xCO2AirDryUnit.hasConflict() ||
			 pCO2AirWetUnit.hasConflict() ||
			 fCO2AirWetUnit.hasConflict() ||
			 xCO2AirDryInterpolatedUnit.hasConflict() ||
			 pCO2AirWetInterpolatedUnit.hasConflict() ||
			 fCO2AirWetInterpolatedUnit.hasConflict() ) {
			return false;
		}

		if ( depthOfSeaWaterIntake.hasConflict() ||
			 locationOfSeaWaterIntake.hasConflict() ||
			 equilibratorType.hasConflict() ||
			 equilibratorVolume.hasConflict() ||
			 waterFlowRate.hasConflict() ||
			 headspaceGasFlowRate.hasConflict() ||
			 vented.hasConflict() ||
			 dryingMethodForCO2InWater.hasConflict() ||
			 equAdditionalInformation.hasConflict() ) {
			return false;
		}

		if ( co2InMarineAirMeasurement.hasConflict() ||
			 co2InMarineAirLocationAndHeight.hasConflict() ||
			 co2InMarineAirDryingMethod.hasConflict() ) {
			return false;
		}

		if ( co2MeasurementMethod.hasConflict() ||
			 co2Manufacturer.hasConflict() ||
			 co2Model.hasConflict() ||
			 co2Frequency.hasConflict() ||
			 co2ResolutionWater.hasConflict() ||
			 co2UncertaintyWater.hasConflict() ||
			 co2ResolutionAir.hasConflict() ||
			 co2UncertaintyAir.hasConflict() ||
			 co2ManufacturerOfCalibrationGas.hasConflict() ||
			 co2SensorCalibration.hasConflict() ||
			 co2EnvironmentalControl.hasConflict() ||
			 co2MethodReferences.hasConflict() ||
			 detailsOfCO2Sensing.hasConflict() ||
			 analysisOfCO2Comparison.hasConflict() ||
			 measuredCO2Params.hasConflict() ) {
			return false;
		}

		if ( sstLocation.hasConflict() ||
			 sstManufacturer.hasConflict() ||
			 sstModel.hasConflict() ||
			 sstAccuracy.hasConflict() ||
			 sstPrecision.hasConflict() ||
			 sstCalibration.hasConflict() ||
			 sstOtherComments.hasConflict() ) {
			return false;
		}

		if ( eqtLocation.hasConflict() ||
			 eqtManufacturer.hasConflict() ||
			 eqtModel.hasConflict() ||
			 eqtAccuracy.hasConflict() ||
			 eqtPrecision.hasConflict() ||
			 eqtCalibration.hasConflict() ||
			 eqtWarming.hasConflict() ||
			 eqtOtherComments.hasConflict() ) {
			return false;
		}

		if ( eqpLocation.hasConflict() ||
			 eqpManufacturer.hasConflict() ||
			 eqpModel.hasConflict() ||
			 eqpAccuracy.hasConflict() ||
			 eqpPrecision.hasConflict() ||
			 eqpCalibration.hasConflict() ||
			 eqpOtherComments.hasConflict() ||
			 eqpNormalized.hasConflict() ) {
			return false;
		}

		if ( atpLocation.hasConflict() ||
			 atpManufacturer.hasConflict() ||
			 atpModel.hasConflict() ||
			 atpAccuracy.hasConflict() ||
			 atpPrecision.hasConflict() ||
			 atpCalibration.hasConflict() ||
			 atpOtherComments.hasConflict() ) {
			return false;
		}

		if ( sssLocation.hasConflict() ||
			 sssManufacturer.hasConflict() ||
			 sssModel.hasConflict() ||
			 sssAccuracy.hasConflict() ||
			 sssPrecision.hasConflict() ||
			 sssCalibration.hasConflict() ||
			 sssOtherComments.hasConflict() ) {
			return false;
		}

		for (OMECompositeVariable compVar : otherSensors) {
			 if ( compVar.hasConflict() ) {
				 return false;
			 }
		}

		if ( dataSetReferences.hasConflict() ||
			 additionalInformation.hasConflict() ||
			 citation.hasConflict() ||
			 measurementAndCalibrationReport.hasConflict() ||
			 preliminaryQualityControl.hasConflict() ) {
			return false;
		}

		if ( form_type.hasConflict() ||
			 recordID.hasConflict() ) {
			return false;
		}

		// Check for absolutely required content; at this time only
		// what is required for the UploadDashboard submission 
		// (expocode, at least one investigator name, vessel name)
		if ( cruiseID.getAllValues().isEmpty() ||
			 investigators.isEmpty() ||
			 vesselName.getAllValues().isEmpty() ) {
			return false;
		}

		return true;
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
		
		Path newPath = new Path(path, subElementName);
		return new OMEVariable(newPath, subElement);
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
	
	
	/**
	 * Returns the contents of this Metadata object as a text header to
	 * be inserted into the top of a data file.
	 * 
	 * Some values will not be included in the header. These are
	 * values that are calculated automatically by the system.
	 * 
	 * Some values should not be copied between cruises (e.g. start and
	 * end dates). For these values, empty placeholder values will be
	 * included in the header.
	 * 
	 * The header text will not contain the surrounding {@code \/*...*\/} markers
	 * @return The header text
	 */
	public String getHeaderText() throws OmeMetadataException {
		StringBuffer output = new StringBuffer();

		output.append(getSingleHeaderString(userName, USER_NAME_STRING));
		output.append(getSingleHeaderString(userOrganization, USER_ORGANIZATION_STRING));
		output.append(getSingleHeaderString(userAddress, USER_ADDRESS_STRING));
		output.append(getSingleHeaderString(userPhone, USER_PHONE_STRING));
		output.append(getSingleHeaderString(userEmail, USER_EMAIL_STRING));
		
		for (OMECompositeVariable investigator : investigators) {
			output.append(getCompositeHeaderString(investigator, INVESTIGATOR_COMP_NAME, INVESTIGATOR_ENTRIES));
		}
		
		output.append(getSingleHeaderString(datasetID, DATASET_ID_STRING));
		output.append(getSingleHeaderString(fundingInfo, FUNDING_INFO_STRING));
		
		// initialSubmission and revisedSubmission not included
		
		output.append(getSingleHeaderString(experimentName, EXPERIMENT_NAME_STRING));
		output.append(getSingleHeaderString(experimentType, EXPERIMENT_TYPE_STRING));
		output.append(getSingleHeaderString(platformType, PLATFORM_TYPE_STRING));
		output.append(getSingleHeaderString(co2InstrumentType, CO2_INSTRUMENT_TYPE_STRING));
		output.append(getSingleHeaderString(mooringId, MOORING_ID_STRING));
		output.append(getEmptyHeaderString(CRUISE_ID_STRING));
		output.append(getSingleHeaderString(cruiseInfo, SUB_CRUISE_INFO_STRING));
		output.append(getSingleHeaderString(section, SECTION_STRING));
		output.append(getEmptyHeaderString(START_DATE_STRING));
		output.append(getEmptyHeaderString(END_DATE_STRING));
		output.append(getSingleHeaderString(geographicalRegion, GEO_REGION_STRING));
		
		// Geographical bounds not included
		
		output.append(getSingleHeaderString(vesselName, VESSEL_NAME_STRING));
		output.append(getSingleHeaderString(vesselID, VESSEL_ID_STRING));
		output.append(getSingleHeaderString(country, COUNTRY_STRING));
		output.append(getSingleHeaderString(vesselOwner, OWNER_STRING));
		
		for (OMECompositeVariable variable : variablesInfo) {
			output.append(getCompositeHeaderString(variable, VARIABLE_COMP_NAME, VARIABLES_INFO_ENTRIES));
		}
		
		output.append(getSingleHeaderString(xCO2WaterEquDryUnit, XCO2_WATER_EQU_DRY_STRING));
		output.append(getSingleHeaderString(xCO2WaterSSTDryUnit, XCO2_WATER_SST_DRY_STRING));
		output.append(getSingleHeaderString(pCO2WaterEquWetUnit, PCO2_WATER_EQU_WET_STRING));
		output.append(getSingleHeaderString(pCO2WaterSSTWetUnit, PCO2_WATER_SST_WET_STRING));
		output.append(getSingleHeaderString(fCO2WaterEquWetUnit, FCO2_WATER_EQU_WET_STRING));
		output.append(getSingleHeaderString(fCO2WaterSSTWetUnit, FCO2_WATER_SST_WET_STRING));
		output.append(getSingleHeaderString(xCO2AirDryUnit, XCO2_AIR_DRY_STRING));
		output.append(getSingleHeaderString(pCO2AirWetUnit, PCO2_AIR_WET_STRING));
		output.append(getSingleHeaderString(fCO2AirWetUnit, FCO2_AIR_WET_STRING));
		output.append(getSingleHeaderString(xCO2AirDryInterpolatedUnit, XCO2_AIR_DRY_INTERP_STRING));
		output.append(getSingleHeaderString(pCO2AirWetInterpolatedUnit, PCO2_AIR_WET_INTERP_STRING));
		output.append(getSingleHeaderString(fCO2AirWetInterpolatedUnit, FCO2_AIR_WET_INTERP_STRING));

		output.append(getSingleHeaderString(depthOfSeaWaterIntake, INTAKE_DEPTH_STRING));
		output.append(getSingleHeaderString(locationOfSeaWaterIntake, INTAKE_LOCATION_STRING));
		output.append(getSingleHeaderString(equilibratorType, EQUI_TYPE_STRING));
		output.append(getSingleHeaderString(equilibratorVolume, EQUI_VOLUME_STRING));
		output.append(getSingleHeaderString(waterFlowRate, WATER_FLOW_RATE_STRING));
		output.append(getSingleHeaderString(headspaceGasFlowRate, GAS_FLOW_RATE_STRING));
		output.append(getSingleHeaderString(vented, VENTED_STRING));
		output.append(getSingleHeaderString(dryingMethodForCO2InWater, DRYING_METHOD_STRING));
		output.append(getSingleHeaderString(equAdditionalInformation, EQUI_ADDITIONAL_INFO_STRING));
		
		output.append(getSingleHeaderString(co2InMarineAirMeasurement, MARINE_AIR_MEASUREMENT_STRING));
		output.append(getSingleHeaderString(co2InMarineAirLocationAndHeight, MARINE_AIR_LOCATION_STRING));
		output.append(getSingleHeaderString(co2InMarineAirDryingMethod, MARINE_AIR_DRYING_STRING));
		output.append(getSingleHeaderString(co2MeasurementMethod, CO2_MEASUREMENT_METHOD_STRING));
		output.append(getSingleHeaderString(co2Manufacturer, CO2_MANUFACTURER_STRING));
		output.append(getSingleHeaderString(co2Model, CO2_MODEL_STRING));
		output.append(getSingleHeaderString(co2Frequency, CO2_FREQUENCY_STRING));
		output.append(getSingleHeaderString(co2ResolutionWater, CO2_WATER_RES_STRING));
		output.append(getSingleHeaderString(co2UncertaintyWater, CO2_WATER_UNC_STRING));
		output.append(getSingleHeaderString(co2ResolutionAir, CO2_AIR_RES_STRING));
		output.append(getSingleHeaderString(co2UncertaintyAir, CO2_AIR_UNC_STRING));
		output.append(getSingleHeaderString(co2ManufacturerOfCalibrationGas, CO2_CALIBRATION_MANUFACTURER_STRING));
		output.append(getSingleHeaderString(co2SensorCalibration, CO2_SENSOR_CALIBRATION_STRING));
		output.append(getSingleHeaderString(co2EnvironmentalControl, ENVIRONMENTAL_CONTROL_STRING));
		output.append(getSingleHeaderString(co2MethodReferences, METHOD_REFS_STRING));
		output.append(getSingleHeaderString(detailsOfCO2Sensing, DETAILS_OF_CO2_SENSING_STRING));
		output.append(getSingleHeaderString(analysisOfCO2Comparison, ANALYSIS_OF_COMPARISON_STRING));
		output.append(getSingleHeaderString(measuredCO2Params, MEASURED_CO2_PARAMS_STRING));

		output.append(getSingleHeaderString(sstLocation, SST_LOCATION_STRING));
		output.append(getSingleHeaderString(sstManufacturer, SST_MANUFACTURER_STRING));
		output.append(getSingleHeaderString(sstModel, SST_MODEL_STRING));
		output.append(getSingleHeaderString(sstAccuracy, SST_ACCURACY_STRING));
		output.append(getSingleHeaderString(sstPrecision, SST_PRECISION_STRING));
		output.append(getSingleHeaderString(sstCalibration, SST_CALIBRATION_STRING));
		output.append(getSingleHeaderString(sstOtherComments, SST_COMMENTS_STRING));

		output.append(getSingleHeaderString(eqtLocation, EQT_LOCATION_STRING));
		output.append(getSingleHeaderString(eqtManufacturer, EQT_MANUFACTURER_STRING));
		output.append(getSingleHeaderString(eqtModel, EQT_MODEL_STRING));
		output.append(getSingleHeaderString(eqtAccuracy, EQT_ACCURACY_STRING));
		output.append(getSingleHeaderString(eqtPrecision, EQT_PRECISION_STRING));
		output.append(getSingleHeaderString(eqtCalibration, EQT_CALIBRATION_STRING));
		output.append(getSingleHeaderString(eqtWarming, EQT_WARMING_STRING));
		output.append(getSingleHeaderString(eqtOtherComments, EQT_COMMENTS_STRING));

		output.append(getSingleHeaderString(eqpLocation, EQP_LOCATION_STRING));
		output.append(getSingleHeaderString(eqpManufacturer, EQP_MANUFACTURER_STRING));
		output.append(getSingleHeaderString(eqpModel, EQP_MODEL_STRING));
		output.append(getSingleHeaderString(eqpAccuracy, EQP_ACCURACY_STRING));
		output.append(getSingleHeaderString(eqpPrecision, EQP_PRECISION_STRING));
		output.append(getSingleHeaderString(eqpCalibration, EQP_CALIBRATION_STRING));
		output.append(getSingleHeaderString(eqpOtherComments, EQP_COMMENTS_STRING));
		output.append(getSingleHeaderString(eqpNormalized, EQP_NORMALIZED_STRING));
		
		output.append(getSingleHeaderString(atpLocation, ATM_LOCATION_STRING));
		output.append(getSingleHeaderString(atpManufacturer, ATM_MANUFACTURER_STRING));
		output.append(getSingleHeaderString(atpModel, ATM_MODEL_STRING));
		output.append(getSingleHeaderString(atpAccuracy, ATM_ACCURACY_STRING));
		output.append(getSingleHeaderString(atpPrecision, ATM_PRECISION_STRING));
		output.append(getSingleHeaderString(atpCalibration, ATM_CALIBRATION_STRING));
		output.append(getSingleHeaderString(atpOtherComments, ATM_COMMENTS_STRING));
		
		output.append(getSingleHeaderString(sssLocation, SSS_LOCATION_STRING));
		output.append(getSingleHeaderString(sssManufacturer, SSS_MANUFACTURER_STRING));
		output.append(getSingleHeaderString(sssModel, SSS_MODEL_STRING));
		output.append(getSingleHeaderString(sssAccuracy, SSS_ACCURACY_STRING));
		output.append(getSingleHeaderString(sssPrecision, SSS_PRECISION_STRING));
		output.append(getSingleHeaderString(sssCalibration, SSS_CALIBRATION_STRING));
		output.append(getSingleHeaderString(sssOtherComments, SSS_COMMENTS_STRING));
		
		for (OMECompositeVariable otherSensor : otherSensors) {
			output.append(getCompositeHeaderString(otherSensor, OTHER_SENSOR_COMP_NAME, OTHER_SENSORS_ENTRIES));
		}

		output.append(getSingleHeaderString(dataSetReferences, DATA_SET_REFS_STRING));
		output.append(getSingleHeaderString(additionalInformation, ADD_INFO_STRING));
		output.append(getSingleHeaderString(citation, CITATION_STRING));
		output.append(getSingleHeaderString(measurementAndCalibrationReport, MEAS_CALIB_REPORT_STRING));
		output.append(getSingleHeaderString(preliminaryQualityControl, PRELIM_QC_STRING));
		
		return output.toString();
	}
	
	private String getEmptyHeaderString(String name) {
		return name + "=\n";
	}
	
	private String getCompositeHeaderString(OMECompositeVariable variable, String name, List<String> entryNames) throws OmeMetadataException {
		StringBuffer out = new StringBuffer();
		
		if (null != variable) {
			if (variable.hasConflict()) {
				throw new OmeMetadataException("Cannot generate header: entry '" + name + "' has conflict");
			}
			out.append(name);
			out.append("[\n");

			List<OMECompositeVariableEntry> entryList = variable.getAllEntries();
			
			for (String entryName : entryNames) {
				for (OMECompositeVariableEntry entry : entryList) {
					if (entry.getName().equalsIgnoreCase(entryName)) {
						
						String value = escapeValue(entry.getValue());
						if (value.length() > 0) {
							out.append(entryName);
							out.append('=');
							out.append(value);
							out.append('\n');
						}
						break;
					}
				}
			}
			
			out.append("]\n");
		}
		
		return out.toString();
	}
	
	private String getSingleHeaderString(OMEVariable variable, String name) throws OmeMetadataException {
		StringBuffer out = new StringBuffer();
		
		if (null != variable) {
			if (variable.hasConflict()) {
				throw new OmeMetadataException("Cannot generate header: entry '" + name + "' has conflict");
			}
			
			String value = escapeValue(variable.getValue());
			if (value.length() > 0) {
				out.append(name);
				out.append('=');
				out.append(value);
				out.append('\n');
			}
		}
		
		return out.toString();
	}
	
	/**
	 * Trim a value and escape special characters in a value:
	 * '\' -> '\\'
	 * '=' -> '\='
	 * '[' -> '\['
	 * ']' -> '\]'
	 * 
	 * @param value The value to be escaped
	 * @return The escaped value
	 */
	private String escapeValue(String value) {
		String output = value.trim().replaceAll("\\\\", "\\\\\\\\");
		output = output.replaceAll("=", "\\\\=");
		output = output.replaceAll("\\[", "\\\\[");
		output = output.replaceAll("\\]", "\\\\]");
		return output;
	}
	
	public static OmeMetadata merge(OmeMetadata... metadatas) throws BadEntryNameException {
		OmeMetadata merged = (OmeMetadata) metadatas[0].clone();
		for (int i = 1; i < metadatas.length; i++) {
			copyValuesIn(merged, metadatas[i]);
		}
		merged.setDraft( ! merged.isAcceptable() );

		return merged;
	}

	private static void copyValuesIn(OmeMetadata dest, OmeMetadata newValues) throws BadEntryNameException {
		
		// The first thing to copy is the cruise ID (aka EXPO Code).
		// If these are different, it implies that we have metadata from
		// different cruises so they should not be merged.
		dest.cruiseID.addValues(newValues.cruiseID.getAllValues());
		if (dest.cruiseID.hasConflict()) {
			throw new BadEntryNameException("Cruise IDs do not match - cannot merge");
		}

		// draft value is dealt with after merging

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

		dest.temporalCoverageStartDate.addValues(newValues.temporalCoverageStartDate.getAllValues());
		dest.temporalCoverageEndDate.addValues(newValues.temporalCoverageEndDate.getAllValues());

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

		dest.form_type.addValues(newValues.form_type.getAllValues());
		dest.recordID.addValues(newValues.recordID.getAllValues());
	}

	@Override
	public Object clone() {
		OmeMetadata clone = new OmeMetadata(itsExpoCode);
		clone.setDraft(isDraft());

		clone.userName = (OMEVariable) userName.clone();
		clone.userOrganization = (OMEVariable) userOrganization.clone();
		clone.userAddress = (OMEVariable) userAddress.clone();
		clone.userEmail = (OMEVariable) userEmail.clone();

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

		for (OMECompositeVariable varInfo : otherSensors) {
			clone.otherSensors.add((OMECompositeVariable) varInfo.clone());
		}

		clone.dataSetReferences = (OMEVariable) dataSetReferences.clone();
		clone.additionalInformation = (OMEVariable) additionalInformation.clone();
		clone.citation = (OMEVariable) citation.clone();
		clone.measurementAndCalibrationReport = (OMEVariable) measurementAndCalibrationReport.clone();
		clone.preliminaryQualityControl = (OMEVariable) preliminaryQualityControl.clone();

		clone.form_type = (OMEVariable) form_type.clone();
		clone.recordID = (OMEVariable) recordID.clone();

		return clone;
	}
}

