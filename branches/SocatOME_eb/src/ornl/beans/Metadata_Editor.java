/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.beans;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Biva Shrestha Class for implementing a bean which knows the names of
 *         fields defined in the merge.xml (XPATH values), so that Spring can
 *         populate this object. Also, provides a hashmap of values which is
 *         populated as they are set. Used by the xml generator code along with
 *         the XPATH to fill in the template.
 */
public class Metadata_Editor {
	
	
	private HashMap<String, String> lhm = new HashMap<String, String>();
	public HashMap<String, String> getLhm() {
		return lhm;
	}

	private String field_filestatus;
	private String field_form_type;
	private String field_record_id;
	private String field_filename;
	// User Information
	private String field_username;
	private String field_user_organizationame;
	private String field_user_adress;
	private String field_user_telephonenumber;
	private String field_user_email;

	// Investigator
	private String field_ownername;
	private String field_organizationame;
	private String field_owneraddress;
	private String field_telephonenumber;
	private String field_email;

	private String field_ownername2;
	private String field_organizationame2;
	private String field_owneraddress2;
	private String field_telephonenumber2;
	private String field_email2;

	private String field_ownername3;
	private String field_organizationame3;
	private String field_owneraddress3;
	private String field_telephonenumber3;
	private String field_email3;
	
	private String field_ownername4;
	private String field_organizationame4;
	private String field_owneraddress4;
	private String field_telephonenumber4;
	private String field_email4;
	private String field_ownername5;
	private String field_organizationame5;
	private String field_owneraddress5;
	private String field_telephonenumber5;
	private String field_email5;	
	public String getField_ownername4() {
		return field_ownername4;
	}

	public void setField_ownername4(String field_ownername4) {
		this.field_ownername4 = field_ownername4;
	}

	public String getField_organizationame4() {
		return field_organizationame4;
	}

	public void setField_organizationame4(String field_organizationame4) {
		this.field_organizationame4 = field_organizationame4;
	}

	public String getField_owneraddress4() {
		return field_owneraddress4;
	}

	public void setField_owneraddress4(String field_owneraddress4) {
		this.field_owneraddress4 = field_owneraddress4;
	}

	public String getField_telephonenumber4() {
		return field_telephonenumber4;
	}

	public void setField_telephonenumber4(String field_telephonenumber4) {
		this.field_telephonenumber4 = field_telephonenumber4;
	}

	public String getField_email4() {
		return field_email4;
	}

	public void setField_email4(String field_email4) {
		this.field_email4 = field_email4;
	}

	public String getField_ownername5() {
		return field_ownername5;
	}

	public void setField_ownername5(String field_ownername5) {
		this.field_ownername5 = field_ownername5;
	}

	public String getField_organizationame5() {
		return field_organizationame5;
	}

	public void setField_organizationame5(String field_organizationame5) {
		this.field_organizationame5 = field_organizationame5;
	}

	public String getField_owneraddress5() {
		return field_owneraddress5;
	}

	public void setField_owneraddress5(String field_owneraddress5) {
		this.field_owneraddress5 = field_owneraddress5;
	}

	public String getField_telephonenumber5() {
		return field_telephonenumber5;
	}

	public void setField_telephonenumber5(String field_telephonenumber5) {
		this.field_telephonenumber5 = field_telephonenumber5;
	}

	public String getField_email5() {
		return field_email5;
	}

	public void setField_email5(String field_email5) {
		this.field_email5 = field_email5;
	}

	// Dataset Info
	private String field_title;
	private String field_initial_submission;
	private String field_revised_submission;

	// Cruise info;
	private String field_experiment_name;
	//private String field_mooring_id;
	private String field_co2_instr_type;
	private String field_survey_type;
	private String field_cruise_id;
	private String field_cruise_info;
	private String field_funding_info;
	private String field_section;
	private String field_geographical_area;
	private String field_west;
	private String field_west_degree;
	private String field_west_minute;
	private String field_west_second;
	private String field_west_EW;
	private String field_east;
	private String field_east_degree;
	private String field_east_minute;
	private String field_east_second;
	private String field_east_EW;
	private String field_north;
	private String field_north_degree;
	private String field_north_minute;
	private String field_north_second;
	private String field_north_NS;
	private String field_south;
	private String field_south_degree;
	private String field_south_minute;
	private String field_south_second;
	private String field_south_NS;
	private String field_start_date;
	private String field_end_date;
	private String field_start_date_dup;
	private String field_end_date_dup;
	private String field_port_of_call;
	private String field_vessel_name;
	private String field_vessel_id;
	private String field_vessel_country;
	private String field_vessel_owner;

	// varaible info
	private String field_variable0;
	private String field_variable_description0;
	private String field_variable_unit0;
	private String field_variable_sensor_model0;
	private String field_variable1;
	private String field_variable_description1;
	private String field_variable_unit1;
	private String field_variable2;
	private String field_variable_description2;
	private String field_variable_unit2;
	private String field_variable3;
	private String field_variable_description3;
	private String field_variable_unit3;
	private String field_variable4;
	private String field_variable_description4;
	private String field_variable_unit4;
	private String field_variable5;
	private String field_variable_description5;
	private String field_variable_unit5;
	private String field_variable6;
	private String field_variable_description6;
	private String field_variable_unit6;
	private String field_variable7;
	private String field_variable_description7;
	private String field_variable_unit7;
	private String field_variable8;
	private String field_variable_description8;
	private String field_variable_unit8;
	private String field_variable9;
	private String field_variable_description9;
	private String field_variable_unit9;
	private String field_variable10;
	private String field_variable_description10;
	private String field_variable_unit10;
	private String field_variable11;
	private String field_variable_description11;
	private String field_variable_unit11;
	private String field_variable12;
	private String field_variable_description12;
	private String field_variable_unit12;
	private String field_variable13;
	private String field_variable_description13;
	private String field_variable_unit13;
	private String field_variable14;
	private String field_variable_description14;
	private String field_variable_unit14;
	
	private String field_variable15;
	private String field_variable_description15;
	private String field_variable_unit15;
	private String field_variable16;
	private String field_variable_description16;
	private String field_variable_unit16;
	private String field_variable17;
	private String field_variable_description17;
	private String field_variable_unit17;
	private String field_variable18;
	private String field_variable_description18;
	private String field_variable_unit18;
	private String field_variable19;
	private String field_variable_description19;
	private String field_variable_unit19;
	private String field_variable20;
	private String field_variable_description20;
	private String field_variable_unit20;
	private String field_variable21;
	private String field_variable_description21;
	private String field_variable_unit21;
	public String getField_variable15() {
		return field_variable15;
	}

	public void setField_variable15(String field_variable15) {
		this.field_variable15 = field_variable15;
	}

	public String getField_variable_description15() {
		return field_variable_description15;
	}

	public void setField_variable_description15(String field_variable_description15) {
		this.field_variable_description15 = field_variable_description15;
	}

	public String getField_variable16() {
		return field_variable16;
	}

	public void setField_variable16(String field_variable16) {
		this.field_variable16 = field_variable16;
	}

	public String getField_variable_description16() {
		return field_variable_description16;
	}

	public void setField_variable_description16(String field_variable_description16) {
		this.field_variable_description16 = field_variable_description16;
	}

	public String getField_variable17() {
		return field_variable17;
	}

	public void setField_variable17(String field_variable17) {
		this.field_variable17 = field_variable17;
	}

	public String getField_variable_description17() {
		return field_variable_description17;
	}

	public void setField_variable_description17(String field_variable_description17) {
		this.field_variable_description17 = field_variable_description17;
	}

	public String getField_variable18() {
		return field_variable18;
	}

	public void setField_variable18(String field_variable18) {
		this.field_variable18 = field_variable18;
	}

	public String getField_variable_description18() {
		return field_variable_description18;
	}

	public void setField_variable_description18(String field_variable_description18) {
		this.field_variable_description18 = field_variable_description18;
	}

	public String getField_variable19() {
		return field_variable19;
	}

	public void setField_variable19(String field_variable19) {
		this.field_variable19 = field_variable19;
	}

	public String getField_variable_description19() {
		return field_variable_description19;
	}

	public void setField_variable_description19(String field_variable_description19) {
		this.field_variable_description19 = field_variable_description19;
	}

	public String getField_variable20() {
		return field_variable20;
	}

	public void setField_variable20(String field_variable20) {
		this.field_variable20 = field_variable20;
	}

	public String getField_variable_description20() {
		return field_variable_description20;
	}

	public void setField_variable_description20(String field_variable_description20) {
		this.field_variable_description20 = field_variable_description20;
	}

	public String getField_variable21() {
		return field_variable21;
	}

	public void setField_variable21(String field_variable21) {
		this.field_variable21 = field_variable21;
	}

	public String getField_variable_description21() {
		return field_variable_description21;
	}

	public void setField_variable_description21(String field_variable_description21) {
		this.field_variable_description21 = field_variable_description21;
	}
	
	
	
	
/*
	public String getField_mooring_id() {
		return field_mooring_id;
	}

	public void setField_mooring_id(String field_mooring_id) {
		this.field_mooring_id = field_mooring_id;
	}*/
	

	
	public String getField_variable_unit0() {
		return field_variable_unit0;
	}

	public void setField_variable_unit0(String field_variable_unit0) {
		this.field_variable_unit0 = field_variable_unit0;
	}

	public String getField_variable_unit1() {
		return field_variable_unit1;
	}

	public void setField_variable_unit1(String field_variable_unit1) {
		this.field_variable_unit1 = field_variable_unit1;
	}

	public String getField_variable_unit2() {
		return field_variable_unit2;
	}

	public void setField_variable_unit2(String field_variable_unit2) {
		this.field_variable_unit2 = field_variable_unit2;
	}

	public String getField_variable_unit3() {
		return field_variable_unit3;
	}

	public void setField_variable_unit3(String field_variable_unit3) {
		this.field_variable_unit3 = field_variable_unit3;
	}

	public String getField_variable_unit4() {
		return field_variable_unit4;
	}

	public void setField_variable_unit4(String field_variable_unit4) {
		this.field_variable_unit4 = field_variable_unit4;
	}

	public String getField_variable_unit5() {
		return field_variable_unit5;
	}

	public void setField_variable_unit5(String field_variable_unit5) {
		this.field_variable_unit5 = field_variable_unit5;
	}

	public String getField_variable_unit6() {
		return field_variable_unit6;
	}

	public void setField_variable_unit6(String field_variable_unit6) {
		this.field_variable_unit6 = field_variable_unit6;
	}

	public String getField_variable_unit7() {
		return field_variable_unit7;
	}

	public void setField_variable_unit7(String field_variable_unit7) {
		this.field_variable_unit7 = field_variable_unit7;
	}

	public String getField_variable_unit8() {
		return field_variable_unit8;
	}

	public void setField_variable_unit8(String field_variable_unit8) {
		this.field_variable_unit8 = field_variable_unit8;
	}

	public String getField_variable_unit9() {
		return field_variable_unit9;
	}

	public void setField_variable_unit9(String field_variable_unit9) {
		this.field_variable_unit9 = field_variable_unit9;
	}

	public String getField_variable_unit10() {
		return field_variable_unit10;
	}

	public void setField_variable_unit10(String field_variable_unit10) {
		this.field_variable_unit10 = field_variable_unit10;
	}

	public String getField_variable_unit11() {
		return field_variable_unit11;
	}

	public void setField_variable_unit11(String field_variable_unit11) {
		this.field_variable_unit11 = field_variable_unit11;
	}

	public String getField_variable_unit12() {
		return field_variable_unit12;
	}

	public void setField_variable_unit12(String field_variable_unit12) {
		this.field_variable_unit12 = field_variable_unit12;
	}

	public String getField_variable_unit13() {
		return field_variable_unit13;
	}

	public void setField_variable_unit13(String field_variable_unit13) {
		this.field_variable_unit13 = field_variable_unit13;
	}

	public String getField_variable_unit14() {
		return field_variable_unit14;
	}

	public void setField_variable_unit14(String field_variable_unit14) {
		this.field_variable_unit14 = field_variable_unit14;
	}

	public String getField_variable_unit15() {
		return field_variable_unit15;
	}

	public void setField_variable_unit15(String field_variable_unit15) {
		this.field_variable_unit15 = field_variable_unit15;
	}

	public String getField_variable_unit16() {
		return field_variable_unit16;
	}

	public void setField_variable_unit16(String field_variable_unit16) {
		this.field_variable_unit16 = field_variable_unit16;
	}

	public String getField_variable_unit17() {
		return field_variable_unit17;
	}

	public void setField_variable_unit17(String field_variable_unit17) {
		this.field_variable_unit17 = field_variable_unit17;
	}

	public String getField_variable_unit18() {
		return field_variable_unit18;
	}

	public void setField_variable_unit18(String field_variable_unit18) {
		this.field_variable_unit18 = field_variable_unit18;
	}

	public String getField_variable_unit19() {
		return field_variable_unit19;
	}

	public void setField_variable_unit19(String field_variable_unit19) {
		this.field_variable_unit19 = field_variable_unit19;
	}

	public String getField_variable_unit20() {
		return field_variable_unit20;
	}

	public void setField_variable_unit20(String field_variable_unit20) {
		this.field_variable_unit20 = field_variable_unit20;
	}

	public String getField_variable_unit21() {
		return field_variable_unit21;
	}

	public void setField_variable_unit21(String field_variable_unit21) {
		this.field_variable_unit21 = field_variable_unit21;
	}

	public String getField_co2_instr_type() {
		return field_co2_instr_type;
	}

	public void setField_co2_instr_type(String field_co2_instr_type) {
		this.field_co2_instr_type = field_co2_instr_type;
	}
	
	
	public String getField_measured_co2_params() {
		return field_measured_co2_params;
	}

	public void setField_measured_co2_params(String field_measured_co2_params) {
		this.field_measured_co2_params = field_measured_co2_params;
	}

	private String field_variable22;
	private String field_variable_description22;
	private String field_variable_unit22;
	private String field_variable23;
	private String field_variable_description23;
	private String field_variable_unit23;
	private String field_variable24;
	private String field_variable_description24;
	private String field_variable_unit24;
	
	private String field_variable25;
	private String field_variable_description25;
	private String field_variable_unit25;
	private String field_variable26;
	private String field_variable_description26;
	private String field_variable_unit26;
	private String field_variable27;
	private String field_variable_description27;
	private String field_variable_unit27;
	private String field_variable28;
	private String field_variable_description28;
	private String field_variable_unit28;
	private String field_variable29;
	private String field_variable_description29;
	private String field_variable_unit29;
	private String field_variable30;
	private String field_variable_description30;
	private String field_variable_unit30;
	
	
	public String getField_variable22() {
		return field_variable22;
	}

	public void setField_variable22(String field_variable22) {
		this.field_variable22 = field_variable22;
	}

	public String getField_variable_description22() {
		return field_variable_description22;
	}

	public void setField_variable_description22(String field_variable_description22) {
		this.field_variable_description22 = field_variable_description22;
	}

	public String getField_variable23() {
		return field_variable23;
	}

	public void setField_variable23(String field_variable23) {
		this.field_variable23 = field_variable23;
	}

	public String getField_variable_description23() {
		return field_variable_description23;
	}

	public void setField_variable_description23(String field_variable_description23) {
		this.field_variable_description23 = field_variable_description23;
	}

	public String getField_variable24() {
		return field_variable24;
	}

	public void setField_variable24(String field_variable24) {
		this.field_variable24 = field_variable24;
	}

	public String getField_variable_description24() {
		return field_variable_description24;
	}

	public void setField_variable_description24(String field_variable_description24) {
		this.field_variable_description24 = field_variable_description24;
	}

	public String getField_variable25() {
		return field_variable25;
	}

	public void setField_variable25(String field_variable25) {
		this.field_variable25 = field_variable25;
	}

	public String getField_variable_description25() {
		return field_variable_description25;
	}

	public void setField_variable_description25(String field_variable_description25) {
		this.field_variable_description25 = field_variable_description25;
	}

	public String getField_variable26() {
		return field_variable26;
	}

	public void setField_variable26(String field_variable26) {
		this.field_variable26 = field_variable26;
	}

	public String getField_variable_description26() {
		return field_variable_description26;
	}

	public void setField_variable_description26(String field_variable_description26) {
		this.field_variable_description26 = field_variable_description26;
	}

	public String getField_variable27() {
		return field_variable27;
	}

	public void setField_variable27(String field_variable27) {
		this.field_variable27 = field_variable27;
	}

	public String getField_variable_description27() {
		return field_variable_description27;
	}

	public void setField_variable_description27(String field_variable_description27) {
		this.field_variable_description27 = field_variable_description27;
	}

	public String getField_variable28() {
		return field_variable28;
	}

	public void setField_variable28(String field_variable28) {
		this.field_variable28 = field_variable28;
	}

	public String getField_variable_description28() {
		return field_variable_description28;
	}

	public void setField_variable_description28(String field_variable_description28) {
		this.field_variable_description28 = field_variable_description28;
	}

	public String getField_variable29() {
		return field_variable29;
	}

	public void setField_variable29(String field_variable29) {
		this.field_variable29 = field_variable29;
	}

	public String getField_variable_description29() {
		return field_variable_description29;
	}

	public void setField_variable_description29(String field_variable_description29) {
		this.field_variable_description29 = field_variable_description29;
	}

	public String getField_variable30() {
		return field_variable30;
	}

	public void setField_variable30(String field_variable30) {
		this.field_variable30 = field_variable30;
	}

	public String getField_variable_description30() {
		return field_variable_description30;
	}

	public void setField_variable_description30(String field_variable_description30) {
		this.field_variable_description30 = field_variable_description30;
	}
	
	public String getField_variable_unit22() {
		return field_variable_unit22;
	}

	public void setField_variable_unit22(String field_variable_unit22) {
		this.field_variable_unit22 = field_variable_unit22;
	}

	public String getField_variable_unit23() {
		return field_variable_unit23;
	}

	public void setField_variable_unit23(String field_variable_unit23) {
		this.field_variable_unit23 = field_variable_unit23;
	}

	public String getField_variable_unit24() {
		return field_variable_unit24;
	}

	public void setField_variable_unit24(String field_variable_unit24) {
		this.field_variable_unit24 = field_variable_unit24;
	}

	public String getField_variable_unit25() {
		return field_variable_unit25;
	}

	public void setField_variable_unit25(String field_variable_unit25) {
		this.field_variable_unit25 = field_variable_unit25;
	}

	public String getField_variable_unit26() {
		return field_variable_unit26;
	}

	public void setField_variable_unit26(String field_variable_unit26) {
		this.field_variable_unit26 = field_variable_unit26;
	}

	public String getField_variable_unit27() {
		return field_variable_unit27;
	}

	public void setField_variable_unit27(String field_variable_unit27) {
		this.field_variable_unit27 = field_variable_unit27;
	}

	public String getField_variable_unit28() {
		return field_variable_unit28;
	}

	public void setField_variable_unit28(String field_variable_unit28) {
		this.field_variable_unit28 = field_variable_unit28;
	}

	public String getField_variable_unit29() {
		return field_variable_unit29;
	}

	public void setField_variable_unit29(String field_variable_unit29) {
		this.field_variable_unit29 = field_variable_unit29;
	}

	public String getField_variable_unit30() {
		return field_variable_unit30;
	}

	public void setField_variable_unit30(String field_variable_unit30) {
		this.field_variable_unit30 = field_variable_unit30;
	}

	private String field_co2_data_type0;
	private String field_co2_data_unit0;
	private String field_co2_data_type1;
	private String field_co2_data_unit1;
	private String field_co2_data_type2;
	private String field_co2_data_unit2;
	private String field_co2_data_type3;
	private String field_co2_data_unit3;
	private String field_co2_data_type4;
	private String field_co2_data_unit4;
	private String field_co2_data_type5;
	private String field_co2_data_unit5;
	private String field_co2_data_type6;
	private String field_co2_data_unit6;
	private String field_co2_data_type7;
	private String field_co2_data_unit7;
	private String field_co2_data_type8;
	private String field_co2_data_unit8;
	private String field_co2_data_type9;
	private String field_co2_data_unit9;
	private String field_co2_data_type10;
	private String field_co2_data_unit10;
	private String field_co2_data_type11;
	private String field_co2_data_unit11;

	// Method Description
	private String field_depth_seawater_intake;
	private String field_location_seawater_intake;
	private String field_equilibrator_type;
	private String field_equilibration_volume;
	private String field_water_flow_rate;
	private String field_gas_flow_rate;
	private String field_vented;
	private String field_drying_method;
	private String field_equilibrium_comments;
	private String field_co2_measurement;
	private String field_marine_airtake;
	private String field_co2_drying;
	private String field_measurement_method;
	private String field_manufacturer_calibration;
	private String field_manufacturer;
	private String field_model;
	private String field_environmental_control;
	private String field_frequency;
	private String field_resolution_air;
	private String field_uncertainity_air;
	private String field_resolution;
	private String field_calibration;
	private String field_no_of_non_zero_gas_stds;
	private String field_uncertainity;
	private String field_system_manufacturer;	

	public String getField_no_of_non_zero_gas_stds() {
		return field_no_of_non_zero_gas_stds;
	}

	public void setField_no_of_non_zero_gas_stds(
			String field_no_of_non_zero_gas_stds) {
		this.field_no_of_non_zero_gas_stds = field_no_of_non_zero_gas_stds;
	}

	public String getField_system_manufacturer() {
		return field_system_manufacturer;
	}

	public void setField_system_manufacturer(String field_system_manufacturer) {
		this.field_system_manufacturer = field_system_manufacturer;
	}

	// method description II
	private String field_TCO2_analyis_method;
	private String field_technique_description;
	private String field_sample_volume;
	private String field_correction_magnitude;
	private String field_batch_number;
	private String field_CRM_analysis_info;
	private String field_replicate_info;
	private String field_poisoning_correction_description;
	private String field_poison_volume;
	private String field_accuracy_CO2_info;
	private String field_CO2_method_reference;
	private String field_detail_sensing;
	private String field_measured_co2_params;
	
	// method description II-->alkalinity
	private String field_curve_fitting_method;
	private String field_type_of_titration;
	private String field_description_of_other_titration;
	private String field_cell_type;
	private String field_CRM_scale;
	private String field_alkalinity_sample_volume;
	private String field_black_correction;
	private String field_alkalinity_accuracy_info;
	private String field_alkalinity_method_references;
	// method description II-->PCO2 Data
	private String field_pCO2_analysis_method;
	private String field_PCO2_sample_volume;
	private String field_headspace_volume;
	private String field_measurement_temperature;
	private String field_temperature_normalization;
	private String field_temperature_correction_method;
	private String field_variable_reported;
	private String field_gas;
	private String field_gas_concentrations;
	private String field_frequesncy_of_standardization;
	private String field_PCO2_replicate_info;
	private String field_PCO2_storage_method;
	private String field_PCO2_accuracy_info;
	private String field_PCO2_method_references;
	// method description II-->pH Data
	private String field_ph_scale;
	private String field_ph_analysis_method;
	private String field_calibration_description;
	private String field_in_situ_temperature;
	private String field_temperature_of_analysis;
	private String field__ph_temperature_normalization;
	private String field_in_situ_pressure;
	private String field_accuracy_info;
	private String field_ph_method_references;

	// Sea Surface Temperature
	private String field_SST_location;
	private String field_SST_manufacturer;
	private String field_SST_model;
	private String field_SST_accuracy;
	private String field_SST_precision;
	private String field_SST_calibration;
	private String field_SST_comments;

	// Equilibrator Temperature
	private String field_Tequ_location;
	private String field_Tequ_manufacturer;
	private String field_Tequ_model;
	private String field_Tequ_accuracy;
	private String field_Tequ_precision;
	private String field_Tequ_calibration;
	private String field_Tequ_warming;
	private String field_Tequ_comments;

	// Equilibrator Pressure
	private String field_Pequ_sensor;
	private String field_Pequ_manufacturer;
	private String field_Pequ_model;
	private String field_Pequ_accuracy;
	private String field_Pequ_precision;
	private String field_Pequ_calibration;
	private String field_Pequ_comments;

	// Atmospheric Pressure
	private String field_Patm_sensor;
	private String field_Patm_manufacturer;
	private String field_Patm_model;
	private String field_Patm_accuracy;
	private String field_Patm_precision;
	private String field_Patm_calibration;
	private String field_Patm_comments;

	// Sea Surface Salinity
	private String field_SSS_sensor;
	private String field_SSS_manufacturer;
	private String field_SSS_model;
	private String field_SSS_accuracy;
	private String field_SSS_precision;
	private String field_SSS_calibration;
	private String field_SSS_comments;

	// others
	private String field_manufaturer_other;
	private String field_sensor_desciption;
	private String field_accuracy_other;
	private String field_model_other;
	private String field_resolution_other;
	private String field_calibration_other;
	private String field_comment_other;
	private String field_method_references;

	private String field_manufaturer_other1;
	private String field_sensor_desciption1;
	private String field_accuracy_other1;
	private String field_model_other1;
	private String field_resolution_other1;
	private String field_calibration_other1;
	private String field_comment_other1;
	private String field_method_references1;

	private String field_manufaturer_other2;
	private String field_sensor_desciption2;
	private String field_accuracy_other2;
	private String field_model_other2;
	private String field_resolution_other2;
	private String field_calibration_other2;
	private String field_comment_other2;
	private String field_method_references2;
	
	private String field_manufaturer_other3;
	private String field_sensor_desciption3;
	private String field_accuracy_other3;
	private String field_model_other3;
	private String field_resolution_other3;
	private String field_calibration_other3;
	private String field_comment_other3;
	private String field_method_references3;
	
	private String field_manufaturer_other4;
	private String field_sensor_desciption4;
	private String field_accuracy_other4;
	private String field_model_other4;
	private String field_resolution_other4;
	private String field_calibration_other4;
	private String field_comment_other4;
	private String field_method_references4;
	
	private String field_manufaturer_other5;
	private String field_sensor_desciption5;
	private String field_accuracy_other5;
	private String field_model_other5;
	private String field_resolution_other5;
	private String field_calibration_other5;
	private String field_comment_other5;
	private String field_method_references5;
	
	private String field_manufaturer_other6;
	private String field_sensor_desciption6;
	private String field_accuracy_other6;
	private String field_model_other6;
	private String field_resolution_other6;
	private String field_calibration_other6;
	private String field_comment_other6;
	private String field_method_references6;
	
	private String field_manufaturer_other7;
	private String field_sensor_desciption7;
	private String field_accuracy_other7;
	private String field_model_other7;
	private String field_resolution_other7;
	private String field_calibration_other7;
	private String field_comment_other7;
	private String field_method_references7;
	
	private String field_manufaturer_other8;
	private String field_sensor_desciption8;
	private String field_accuracy_other8;
	private String field_model_other8;
	private String field_resolution_other8;
	private String field_calibration_other8;
	private String field_comment_other8;
	private String field_method_references8;
	
	private String field_manufaturer_other9;
	private String field_sensor_desciption9;
	private String field_accuracy_other9;
	private String field_model_other9;
	private String field_resolution_other9;
	private String field_calibration_other9;
	private String field_comment_other9;
	private String field_method_references9;
	
		// Additional Information
	private String field_additional_info;

	// Dataset References
	private String field_data_set_references;

	// Citation
	private String field_citation;

	// Data Set Link
	private String field_dataset_url;
	private String field_dataset_label;
	private String field_dataset_link_note;

	// Data files
	private String field_userfile0;
	private String field_userfile1;
	private String field_userfile2;
	private String field_userfile3;

	private String field_additional_measurements;

	private String field_challenge;
	private String field_response;

	public String getField_response() {
		return field_response;
	}

	public void setField_response(String field_response) {
		this.field_response = field_response;
	}

	public String getField_challenge() {
		return field_challenge;
	}

	public void setField_challenge(String field_challenge) {
		this.field_challenge = field_challenge;
	}
    private String field_sensor_calibration;
	private String field_comparision_co2_analysis;
	private String field_preliminary_quality_control;
    private String field_platform_type;
	private String field_Patm_normalized;

	public String getField_Patm_normalized() {
		return field_Patm_normalized;
	}

	public void setField_Patm_normalized(String field_Patm_normalized) {
		this.field_Patm_normalized = field_Patm_normalized;
	}

	public String getField_platform_type() {
		return field_platform_type;
	}

	public void setField_platform_type(String field_platform_type) {
		this.field_platform_type = field_platform_type;
	}

	public String getField_comparision_co2_analysis() {
		return field_comparision_co2_analysis;
	}

	public void setField_comparision_co2_analysis(
			String field_comparision_co2_analysis) {
		this.field_comparision_co2_analysis = field_comparision_co2_analysis;
	}

	public String getField_sensor_calibration() {
		return field_sensor_calibration;
	}

	public void setField_sensor_calibration(String field_sensor_calibration) {
		this.field_sensor_calibration = field_sensor_calibration;
	}
	
	public String getField_preliminary_quality_control() {
		return field_preliminary_quality_control;
	}

	public void setField_preliminary_quality_control(
			String field_preliminary_quality_control) {
		this.field_preliminary_quality_control = field_preliminary_quality_control;
	}
	private ArrayList<String> field_conflicts;	

	public ArrayList<String> getField_conflicts() {
		return field_conflicts;
	}

	public void setField_conflicts(ArrayList<String> field_conflicts) {
		this.field_conflicts = field_conflicts;
	}

	public void setLhm(HashMap lhm) {
		this.field_challenge = (String)  lhm.get("field_challenge");
		this.field_response = (String) lhm.get("field_response");
		this.field_form_type = (String) lhm.get("field_form_type");
		this.field_filestatus = (String) lhm.get("field_filestatus");
		this.field_record_id = (String) lhm.get("field_record_id");
		this.field_filename = (String) lhm.get("field_filename");
		// method description II
		this.field_TCO2_analyis_method = (String) lhm.get("field_TCO2_analyis_method");
		this.field_technique_description = (String) lhm
				.get("field_technique_description");
		this.field_sample_volume = (String) lhm.get("field_sample_volume");
		this.field_correction_magnitude = (String) lhm.get("field_correction_magnitude");
		this.field_batch_number = (String) lhm.get("field_batch_number");
		this.field_CRM_analysis_info = (String) lhm.get("field_CRM_analysis_info");
		this.field_replicate_info = (String) lhm.get("field_replicate_info");
		this.field_poisoning_correction_description = (String) lhm
				.get("field_poisoning_correction_description");
		this.field_poison_volume = (String) lhm.get("field_poison_volume");
		this.field_accuracy_CO2_info = (String) lhm.get("field_accuracy_CO2_info");
		this.field_CO2_method_reference = (String) lhm.get("field_CO2_method_reference");
		this.field_detail_sensing = (String)lhm.get("field_detail_sensing");
		// method description II-->alkalinity
		this.field_curve_fitting_method = (String) lhm.get("field_curve_fitting_method");
		this.field_type_of_titration = (String) lhm.get("field_type_of_titration");
		this.field_description_of_other_titration = (String) lhm
				.get("field_description_of_other_titration");
		this.field_cell_type = (String) lhm.get("field_cell_type");
		this.field_CRM_scale = (String) lhm.get("field_CRM_scale");
		this.field_alkalinity_sample_volume = (String) lhm
				.get("field_alkalinity_sample_volume");
		this.field_black_correction = (String) lhm.get("field_black_correction");
		this.field_alkalinity_accuracy_info = (String) lhm
				.get("field_alkalinity_accuracy_info");
		this.field_alkalinity_method_references = (String) lhm
				.get("field_alkalinity_method_references");
		// method description II-->PCO2 Data
		this.field_pCO2_analysis_method = (String) lhm.get("field_pCO2_analysis_method");
		this.field_PCO2_sample_volume = (String) lhm.get("field_PCO2_sample_volume");
		this.field_headspace_volume = (String) lhm.get("field_headspace_volume");
		this.field_measurement_temperature = (String) lhm
				.get("field_measurement_temperature");
		this.field_temperature_normalization = (String) lhm
				.get("field_temperature_normalization");
		this.field_temperature_correction_method = (String) lhm
				.get("field_temperature_correction_method");
		this.field_variable_reported = (String) lhm.get("field_variable_reported");
		this.field_gas = (String) lhm.get("field_gas");
		this.field_gas_concentrations = (String) lhm.get("field_gas_concentrations");
		this.field_frequesncy_of_standardization = (String) lhm
				.get("field_frequesncy_of_standardization");
		this.field_PCO2_replicate_info = (String) lhm.get("field_PCO2_replicate_info");
		this.field_PCO2_storage_method = (String) lhm.get("field_PCO2_storage_method");
		this.field_PCO2_accuracy_info = (String) lhm.get("field_PCO2_accuracy_info");
		this.field_PCO2_method_references = (String) lhm
				.get("field_PCO2_method_references");
		// method description II-->pH Data
		this.field_ph_scale = (String) lhm.get("field_ph_scale");
		this.field_ph_analysis_method = (String) lhm.get("field_ph_analysis_method");
		this.field_calibration_description = (String) lhm
				.get("field_calibration_description");
		this.field_in_situ_temperature = (String) lhm.get("field_in_situ_temperature");
		this.field_temperature_of_analysis = (String) lhm
				.get("field_temperature_of_analysis");
		this.field__ph_temperature_normalization = (String) lhm
				.get("field__ph_temperature_normalization");
		this.field_in_situ_pressure = (String) lhm.get("field_in_situ_pressure");
		this.field_accuracy_info = (String) lhm.get("field_accuracy_info");
		this.field_ph_method_references = (String) lhm.get("field_ph_method_references");

		// User Information
		this.field_username = (String) lhm.get("field_username");
		this.field_user_organizationame = (String) lhm.get("field_user_organizationame");
		this.field_user_adress = (String) lhm.get("field_user_adress");
		this.field_user_telephonenumber = (String) lhm.get("field_user_telephonenumber");
		this.field_user_email = (String) lhm.get("field_user_email");
		// Investigator
		this.field_ownername = (String) lhm.get("field_ownername");
		this.field_organizationame = (String) lhm.get("field_organizationame");
		this.field_owneraddress = (String) lhm.get("field_owneraddress");
		this.field_telephonenumber = (String) lhm.get("field_telephonenumber");
		this.field_email = (String) lhm.get("field_email");

		this.field_ownername2 = (String) lhm.get("field_ownername2");
		this.field_organizationame2 = (String) lhm.get("field_organizationame2");
		this.field_owneraddress2 = (String) lhm.get("field_owneraddress2");
		this.field_telephonenumber2 = (String) lhm.get("field_telephonenumber2");
		this.field_email2 = (String) lhm.get("field_email2");

		this.field_ownername3 = (String) lhm.get("field_ownername3");
		this.field_organizationame3 = (String) lhm.get("field_organizationame3");
		this.field_owneraddress3 = (String) lhm.get("field_owneraddress3");
		this.field_telephonenumber3 = (String) lhm.get("field_telephonenumber3");
		this.field_email3 = (String) lhm.get("field_email3");
		
		this.field_ownername4 = (String) lhm.get("field_ownername4");
		this.field_organizationame4 = (String) lhm.get("field_organizationame4");
		this.field_owneraddress4 = (String) lhm.get("field_owneraddress4");
		this.field_telephonenumber4 = (String) lhm.get("field_telephonenumber4");
		this.field_email4 = (String) lhm.get("field_email4");
		
		this.field_ownername5 = (String) lhm.get("field_ownername5");
		this.field_organizationame5 = (String) lhm.get("field_organizationame5");
		this.field_owneraddress5 = (String) lhm.get("field_owneraddress5");
		this.field_telephonenumber5 = (String) lhm.get("field_telephonenumber5");
		this.field_email5 = (String) lhm.get("field_email5");

		// Dataset Info
		this.field_title = (String) lhm.get("field_title");
		this.field_funding_info = (String) lhm.get("field_funding_info");
		this.field_initial_submission = (String) lhm.get("field_initial_submission");
		this.field_revised_submission = (String) lhm.get("field_revised_submission");
		//this.field_mooring_id = (String)lhm.get("field_mooring_id");
		this.field_co2_instr_type = (String) lhm.get("field_co2_instr_type");

		// Cruise Info
		this.field_experiment_name = (String) lhm.get("field_experiment_name");
		this.field_survey_type = (String) lhm.get("field_survey_type");
		this.field_cruise_id = (String) lhm.get("field_cruise_id");
		this.field_cruise_info = (String) lhm.get("field_cruise_info");
		this.field_section = (String) lhm.get("field_section");
		this.field_geographical_area = (String) lhm.get("field_geographical_area");
		this.field_west = (String) lhm.get("field_west");
		this.field_west_degree = (String) lhm.get("field_west_degree");
		this.field_west_minute = (String) lhm.get("field_west_minute");
		this.field_west_second = (String) lhm.get("field_west_second");
		this.field_west_EW = (String) lhm.get("field_west_EW");
		this.field_east = (String) lhm.get("field_east");
		this.field_east_degree = (String) lhm.get("field_east_degree");
		this.field_east_minute = (String) lhm.get("field_east_minute");
		this.field_east_second = (String) lhm.get("field_east_second");
		this.field_east_EW = (String) lhm.get("field_east_EW");
		this.field_north = (String) lhm.get("field_north");
		this.field_north_degree = (String) lhm.get("field_north_degree");
		this.field_north_minute = (String) lhm.get("field_north_minute");
		this.field_north_second = (String) lhm.get("field_north_second");
		this.field_north_NS = (String) lhm.get("field_north_NS");
		this.field_south = (String) lhm.get("field_south");
		this.field_south_degree = (String) lhm.get("field_south_degree");
		this.field_south_minute = (String) lhm.get("field_south_minute");
		this.field_south_second = (String) lhm.get("field_south_second");
		this.field_south_NS = (String) lhm.get("field_south_NS");
		this.field_start_date = (String) lhm.get("field_start_date");
		this.field_end_date = (String) lhm.get("field_end_date");
		this.field_start_date_dup = (String) lhm.get("field_start_date_dup");
		this.field_end_date_dup = (String) lhm.get("field_end_date_dup");
		this.field_port_of_call = (String) lhm.get("field_port_of_call");
		this.field_vessel_name = (String) lhm.get("field_vessel_name");
		this.field_vessel_id = (String) lhm.get("field_vessel_id");
		this.field_vessel_country = (String) lhm.get("field_vessel_country");
		this.field_vessel_owner = (String) lhm.get("field_vessel_owner");

		// variable info
		this.field_variable0 = (String) lhm.get("field_variable0");
		this.field_variable1 = (String) lhm.get("field_variable1");
		this.field_variable2 = (String) lhm.get("field_variable2");
		this.field_variable3 = (String) lhm.get("field_variable3");
		this.field_variable4 = (String) lhm.get("field_variable4");
		this.field_variable5 = (String) lhm.get("field_variable5");
		this.field_variable6 = (String) lhm.get("field_variable6");
		this.field_variable7 = (String) lhm.get("field_variable7");
		this.field_variable8 = (String) lhm.get("field_variable8");
		this.field_variable9 = (String) lhm.get("field_variable9");
		this.field_variable10 = (String) lhm.get("field_variable10");
		this.field_variable11 = (String) lhm.get("field_variable11");
		this.field_variable12 = (String) lhm.get("field_variable12");
		this.field_variable13 = (String) lhm.get("field_variable13");
		this.field_variable14 = (String) lhm.get("field_variable14");
		this.field_variable15 = (String) lhm.get("field_variable15");
		this.field_variable16 = (String) lhm.get("field_variable16");
		this.field_variable17 = (String) lhm.get("field_variable17");
		this.field_variable18 = (String) lhm.get("field_variable18");
		this.field_variable19 = (String) lhm.get("field_variable19");
		
		this.field_variable20 = (String) lhm.get("field_variable20");
		this.field_variable22 = (String) lhm.get("field_variable22");
		this.field_variable22 = (String) lhm.get("field_variable22");
		this.field_variable23 = (String) lhm.get("field_variable23");
		this.field_variable24 = (String) lhm.get("field_variable24");
		this.field_variable25 = (String) lhm.get("field_variable25");
		this.field_variable26 = (String) lhm.get("field_variable26");
		this.field_variable27 = (String) lhm.get("field_variable27");
		this.field_variable28 = (String) lhm.get("field_variable28");
		this.field_variable29 = (String) lhm.get("field_variable29");
		
		this.field_variable_description0 = (String) lhm
				.get("field_variable_description0");
		this.field_variable_description1 = (String) lhm
				.get("field_variable_description1");
		this.field_variable_description2 = (String) lhm
				.get("field_variable_description2");
		this.field_variable_description3 = (String) lhm
				.get("field_variable_description3");
		this.field_variable_description4 = (String) lhm
				.get("field_variable_description4");
		this.field_variable_description5 = (String) lhm
				.get("field_variable_description5");
		this.field_variable_description6 = (String) lhm
				.get("field_variable_description6");
		this.field_variable_description7 = (String) lhm
				.get("field_variable_description7");
		this.field_variable_description8 = (String) lhm
				.get("field_variable_description8");
		this.field_variable_description8 = (String) lhm
				.get("field_variable_description9");
		
		
		this.field_variable_description10 = (String) lhm
				.get("field_variable_description10");
		this.field_variable_description11 = (String) lhm
				.get("field_variable_description11");
		this.field_variable_description12 = (String) lhm
				.get("field_variable_description12");
		this.field_variable_description13 = (String) lhm
				.get("field_variable_description13");
		this.field_variable_description14 = (String) lhm
				.get("field_variable_description14");		
		this.field_variable_description15 = (String) lhm
		.get("field_variable_description15");
		this.field_variable_description16 = (String) lhm
		.get("field_variable_description16");
		this.field_variable_description17 = (String) lhm
		.get("field_variable_description17");
		this.field_variable_description18 = (String) lhm
		.get("field_variable_description18");
		this.field_variable_description19 = (String) lhm
		.get("field_variable_description19");
		

		this.field_variable_description20 = (String) lhm
				.get("field_variable_description20");
		this.field_variable_description21 = (String) lhm
				.get("field_variable_description21");
		this.field_variable_description22 = (String) lhm
				.get("field_variable_description22");
		this.field_variable_description23 = (String) lhm
				.get("field_variable_description23");
		this.field_variable_description24 = (String) lhm
				.get("field_variable_description24");		
		this.field_variable_description25 = (String) lhm
		.get("field_variable_description25");
		this.field_variable_description26 = (String) lhm
		.get("field_variable_description26");
		this.field_variable_description27 = (String) lhm
		.get("field_variable_description27");
		this.field_variable_description28 = (String) lhm
		.get("field_variable_description28");
		this.field_variable_description29 = (String) lhm
		.get("field_variable_description29");
		
		
		this.field_co2_data_type0 = (String) (String) lhm.get("field_co2_data_type0");
		this.field_co2_data_unit0 = (String) lhm.get("field_co2_data_unit0");
		this.field_co2_data_type1 = (String) lhm.get("field_co2_data_type1");
		this.field_co2_data_unit1 = (String) lhm.get("field_co2_data_unit1");
		this.field_co2_data_type2 = (String) lhm.get("field_co2_data_type2");
		this.field_co2_data_unit2 = (String) lhm.get("field_co2_data_unit2");
		this.field_co2_data_type3 = (String) lhm.get("field_co2_data_type3");
		this.field_co2_data_unit3 = (String) lhm.get("field_co2_data_unit3");
		this.field_co2_data_type4 = (String) lhm.get("field_co2_data_type4");
		this.field_co2_data_unit4 = (String) lhm.get("field_co2_data_unit4");
		this.field_co2_data_type5 = (String) lhm.get("field_co2_data_type5");
		this.field_co2_data_unit5 = (String) lhm.get("field_co2_data_unit5");
		this.field_co2_data_type6 = (String) lhm.get("field_co2_data_type6");
		this.field_co2_data_unit6 = (String) lhm.get("field_co2_data_unit6");
		this.field_co2_data_type7 = (String) lhm.get("field_co2_data_type7");
		this.field_co2_data_unit7 = (String) lhm.get("field_co2_data_unit7");
		this.field_co2_data_type8 = (String) lhm.get("field_co2_data_type8");
		this.field_co2_data_unit8 = (String) lhm.get("field_co2_data_unit8");
		this.field_co2_data_type9 = (String) lhm.get("field_co2_data_type9");
		this.field_co2_data_unit9 = (String) lhm.get("field_co2_data_unit9");
		this.field_co2_data_type10 = (String) lhm.get("field_co2_data_type10");
		this.field_co2_data_unit10 = (String) lhm.get("field_co2_data_unit10");
		this.field_co2_data_type11 = (String) lhm.get("field_co2_data_type11");
		this.field_co2_data_unit11 = (String) lhm.get("field_co2_data_unit11");

		// method description
		this.field_depth_seawater_intake = (String) lhm
				.get("field_depth_seawater_intake");
		this.field_location_seawater_intake = (String) lhm
				.get("field_location_seawater_intake");
		this.field_equilibrator_type = (String) lhm.get("field_equilibrator_type");
		this.field_equilibration_volume = (String) lhm.get("field_equilibration_volume");
		this.field_water_flow_rate = (String) lhm.get("field_water_flow_rate");
		this.field_gas_flow_rate = (String) lhm.get("field_gas_flow_rate");
		this.field_vented = (String) lhm.get("field_vented");
		this.field_drying_method = (String) lhm.get("field_drying_method");
		this.field_equilibrium_comments = (String) lhm.get("field_equilibrium_comments");
		this.field_co2_measurement = (String) lhm.get("field_co2_measurement");
		this.field_marine_airtake = (String) lhm.get("field_marine_airtake");
		this.field_co2_drying = (String) lhm.get("field_co2_dryings");
		this.field_measurement_method = (String) lhm.get("field_measurement_method");
		this.field_manufacturer_calibration = (String) lhm
				.get("field_manufacturer_calibration");
		this.field_manufacturer = (String) (String) lhm.get("field_manufacturer");
		this.field_model = (String) lhm.get("field_model");
		this.field_environmental_control = (String)lhm
				.get("field_environmental_control");

		this.field_frequency = (String) lhm.get("field_frequency");
		this.field_resolution = (String) lhm.get("field_resolution");
		this.field_uncertainity = (String) lhm.get("field_uncertainity");
		this.field_calibration = (String) lhm.get("field_calibration");

		this.field_uncertainity_air = (String) lhm.get("field_uncertainity_air");
		this.field_resolution_air = (String) lhm.get("field_resolution_air");
		this.field_measured_co2_params = (String) lhm.get("field_measured_co2_params");

		this.field_manufaturer_other = (String) lhm.get("field_manufaturer_other");
		this.field_model_other =  (String) lhm.get("field_model_other");
		this.field_resolution_other = (String) (String) lhm.get("field_resolution_other");
		this.field_calibration_other = (String) (String) lhm.get("field_calibration_other");
		this.field_accuracy_other = (String) lhm.get("field_accuracy_other");
		this.field_comment_other = (String) lhm.get("field_comment_other");
		this.field_method_references = (String) lhm.get("field_method_references");

		this.field_manufaturer_other1 = (String) lhm.get("field_manufaturer_other1");
		this.field_model_other1 = (String) lhm.get("field_model_other1");
		this.field_resolution_other1 = (String) lhm.get("field_resolution_other1");
		this.field_calibration_other1 = (String) lhm.get("field_calibration_other1");
		this.field_accuracy_other1 = (String) lhm.get("field_accuracy_other1");
		this.field_comment_other1 = (String) lhm.get("field_comment_other1");
		this.field_method_references1 = (String) lhm.get("field_method_references1");

		this.field_manufaturer_other2 = (String) lhm.get("field_manufaturer_other2");
		this.field_model_other2 = (String) lhm.get("field_model_other2");
		this.field_resolution_other2 = (String) lhm.get("field_resolution_other2");
		this.field_calibration_other2 = (String) lhm.get("field_calibration_other2");
		this.field_accuracy_other2 = (String) lhm.get("field_accuracy_other2");
		this.field_comment_other2 = (String) lhm.get("field_comment_other2");
		this.field_method_references2 = (String) lhm.get("field_method_references2");
		
		this.field_manufaturer_other3= (String) lhm.get("field_manufaturer_other3");
		this.field_accuracy_other3= (String) lhm.get("field_accuracy_other3");
		this.field_model_other3= (String) lhm.get("field_model_other3");
		this.field_resolution_other3= (String) lhm.get("field_resolution_other3");
		this.field_calibration_other3= (String) lhm.get("field_calibration_other3");
		this.field_comment_other3= (String) lhm.get("field_comment_other3");
		this.field_method_references3= (String) lhm.get("field_method_references3");
		
		this.field_manufaturer_other4= (String) lhm.get("field_manufaturer_other4");
		this.field_accuracy_other4= (String) lhm.get("field_accuracy_other4");
		this.field_model_other4= (String) lhm.get("field_model_other4");
		this.field_resolution_other4= (String) lhm.get("field_resolution_other4");
		this.field_calibration_other4= (String) lhm.get("field_calibration_other4");
		this.field_comment_other4= (String) lhm.get("field_comment_other4");
		this.field_method_references4= (String) lhm.get("field_method_references4");
		
		this.field_manufaturer_other5= (String) lhm.get("field_manufaturer_other5");
		this.field_accuracy_other5= (String) lhm.get("field_accuracy_other5");
		this.field_model_other5= (String) lhm.get("field_model_other5");
		this.field_resolution_other5= (String) lhm.get("field_resolution_other5");
		this.field_calibration_other5= (String) lhm.get("field_calibration_other5");
		this.field_comment_other5= (String) lhm.get("field_comment_other5");
		this.field_method_references5= (String) lhm.get("field_method_references5");
		
		this.field_manufaturer_other6= (String) lhm.get("field_manufaturer_other6");
		this.field_accuracy_other6= (String) lhm.get("field_accuracy_other6");
		this.field_model_other6= (String) lhm.get("field_model_other6");
		this.field_resolution_other6= (String) lhm.get("field_resolution_other6");
		this.field_calibration_other6= (String) lhm.get("field_calibration_other6");
		this.field_comment_other6= (String) lhm.get("field_comment_other6");
		this.field_method_references6= (String) lhm.get("field_method_references6");
		
		this.field_manufaturer_other7= (String) lhm.get("field_manufaturer_other7");
		this.field_accuracy_other7= (String) lhm.get("field_accuracy_other7");
		this.field_model_other7= (String) lhm.get("field_model_other7");
		this.field_resolution_other7= (String) lhm.get("field_resolution_other7");
		this.field_calibration_other7= (String) lhm.get("field_calibration_other7");
		this.field_comment_other7= (String) lhm.get("field_comment_other7");
		this.field_method_references7= (String) lhm.get("field_method_references7");
		
		this.field_manufaturer_other8= (String) lhm.get("field_manufaturer_other8");
		this.field_accuracy_other8= (String) lhm.get("field_accuracy_other8");
		this.field_model_other8= (String) lhm.get("field_model_other8");
		this.field_resolution_other8= (String) lhm.get("field_resolution_other8");
		this.field_calibration_other8= (String) lhm.get("field_calibration_other8");
		this.field_comment_other8= (String) lhm.get("field_comment_other8");
		this.field_method_references8= (String) lhm.get("field_method_references8");
		
		this.field_manufaturer_other9= (String) lhm.get("field_manufaturer_other9");
		this.field_accuracy_other9= (String) lhm.get("field_accuracy_other9");
		this.field_model_other9= (String) lhm.get("field_model_other9");
		this.field_resolution_other9= (String) lhm.get("field_resolution_other9");
		this.field_calibration_other9= (String) lhm.get("field_calibration_other9");
		this.field_comment_other9= (String) lhm.get("field_comment_other9");
		this.field_method_references9= (String) lhm.get("field_method_references9");
		
		
		// method description

		// Sea Surface Temperature
		this.field_SST_location = (String) lhm.get("field_SST_location");
		this.field_SST_manufacturer = (String) lhm.get("field_SST_manufacturer");
		this.field_SST_model = (String) lhm.get("field_SST_model");
		this.field_SST_accuracy = (String) lhm.get("field_SST_accuracy");
		this.field_SST_precision = (String) lhm.get("field_SST_precision");
		this.field_SST_calibration = (String) lhm.get("field_SST_calibration");
		this.field_SST_comments = (String) lhm.get("field_SST_comments");

		// Equilibrator Temperature
		this.field_Tequ_location = (String) lhm.get("field_Tequ_location");
		this.field_Tequ_manufacturer = (String) lhm.get("field_Tequ_manufacturer");
		this.field_Tequ_model = (String) lhm.get("field_Tequ_model");
		this.field_Tequ_accuracy = (String) lhm.get("field_Tequ_accuracy");
		this.field_Tequ_precision = (String) lhm.get("field_Tequ_precision");
		this.field_Tequ_calibration = (String) lhm.get("field_Tequ_calibration");
		this.field_Tequ_warming = (String) lhm.get("field_Tequ_warming");
		this.field_Tequ_comments = (String) lhm.get("field_Tequ_comments");

		// Equilibrator Pressure
		this.field_Pequ_sensor = (String) lhm.get("field_Pequ_sensor");
		this.field_Pequ_manufacturer = (String) lhm.get("field_Pequ_manufacturer");
		this.field_Pequ_model = (String) lhm.get("field_Pequ_model");
		this.field_Pequ_accuracy = (String) lhm.get("field_Pequ_accuracy");
		this.field_Pequ_precision = (String) lhm.get("field_Pequ_precision");
		this.field_Pequ_calibration = (String) lhm.get("field_Pequ_calibration");
		this.field_Pequ_comments = (String) lhm.get("field_Pequ_comments");

		// Atmospheric Pressure
		this.field_Patm_sensor = (String) lhm.get("field_Patm_sensor");
		this.field_Patm_manufacturer = (String) lhm.get("field_Patm_manufacturer");
		this.field_Patm_model = (String) lhm.get("field_Patm_model");
		this.field_Patm_accuracy = (String) lhm.get("field_Patm_accuracy");
		this.field_Patm_precision = (String) lhm.get("field_Patm_precision");
		this.field_Patm_calibration = (String) lhm.get("field_Patm_calibration");
		this.field_Patm_comments = (String) lhm.get("field_Patm_comments");

		// Sea Surface Salinity
		this.field_SSS_sensor = (String) lhm.get("field_SSS_sensor");
		this.field_SSS_manufacturer = (String) lhm.get("field_SSS_manufacturer");
		this.field_SSS_model = (String) lhm.get("field_SSS_model");
		this.field_SSS_accuracy = (String) lhm.get("field_SSS_accuracy");
		this.field_SSS_precision = (String) lhm.get("field_SSS_precision");
		this.field_SSS_calibration = (String) lhm.get("field_SSS_calibration");
		this.field_SSS_comments = (String) lhm.get("field_SSS_comments");

		// Additional Information
		this.field_additional_info = (String) lhm.get("field_additional_info");

		// Dataset Information
		this.field_data_set_references = (String) lhm.get("field_data_set_references");

		// Citation
		this.field_citation = (String) lhm.get("field_citation");

		// Data Set Link
		this.field_dataset_url = (String) lhm.get("field_dataset_url");
		this.field_dataset_label = (String) lhm.get("field_dataset_label");
		this.field_dataset_link_note = (String) lhm.get("field_dataset_link_note");
		this.field_userfile0 = (String) lhm.get("field_userfile0");
		this.field_userfile1 = (String) lhm.get("field_userfile1");
		this.field_userfile2 = (String) lhm.get("field_userfile2");
		this.field_userfile3 = (String) lhm.get("field_userfile3");
		this.field_additional_measurements = (String) lhm
				.get("field_additional_measurements");
		this.field_sensor_calibration = (String) lhm.get("field_sensor_calibration");
		this.field_comparision_co2_analysis =(String) lhm.get("field_comparision_co2_analysis");
		this.field_preliminary_quality_control = (String) lhm.get("field_preliminary_quality_control");
		this.field_platform_type = (String) lhm.get("field_platform_type");
		this.field_Patm_normalized = (String) lhm.get("private String field_Patm_normalized");
		this.field_conflicts = (ArrayList<String>) lhm.get("field_conflicts");
		this.field_system_manufacturer = (String) lhm.get("field_system_manufacturer");
		this.field_no_of_non_zero_gas_stds = (String) lhm.get("field_no_of_non_zero_gas_stds");
		this.field_variable_unit0 = (String) lhm.get("field_variable_unit0");
		this.field_variable_unit1 = (String) lhm.get("field_variable_unit1");
		this.field_variable_unit2 = (String) lhm.get("field_variable_unit2");
		this.field_variable_unit3 = (String) lhm.get("field_variable_unit3");
		this.field_variable_unit4 = (String) lhm.get("field_variable_unit4");
		this.field_variable_unit5 = (String) lhm.get("field_variable_unit5");
		this.field_variable_unit6 = (String) lhm.get("field_variable_unit6");
		this.field_variable_unit7 = (String) lhm.get("field_variable_unit7");
		this.field_variable_unit8 = (String) lhm.get("field_variable_unit8");
		this.field_variable_unit9 = (String) lhm.get("field_variable_unit9");
		this.field_variable_unit10 = (String) lhm.get("field_variable_unit10");
		this.field_variable_unit11 = (String) lhm.get("field_variable_unit11");
		this.field_variable_unit12 = (String) lhm.get("field_variable_unit12");
		this.field_variable_unit13 = (String) lhm.get("field_variable_unit13");
		this.field_variable_unit14 = (String) lhm.get("field_variable_unit14");
		this.field_variable_unit15 = (String) lhm.get("field_variable_unit15");
		this.field_variable_unit16 = (String) lhm.get("field_variable_unit16");
		this.field_variable_unit17 = (String) lhm.get("field_variable_unit17");
		this.field_variable_unit18 = (String) lhm.get("field_variable_unit18");
		this.field_variable_unit19 = (String) lhm.get("field_variable_unit19");
		this.field_variable_unit20 = (String) lhm.get("field_variable_unit20");
		this.field_variable_unit21 = (String) lhm.get("field_variable_unit21");
		this.field_variable_unit22 = (String) lhm.get("field_variable_unit22");
		this.field_variable_unit23 = (String) lhm.get("field_variable_unit23");
		this.field_variable_unit24 = (String) lhm.get("field_variable_unit24");
		this.field_variable_unit25 = (String) lhm.get("field_variable_unit25");
		this.field_variable_unit26 = (String) lhm.get("field_variable_unit26");
		this.field_variable_unit27 = (String) lhm.get("field_variable_unit27");
		this.field_variable_unit28 = (String) lhm.get("field_variable_unit28");
		this.field_variable_unit29 = (String) lhm.get("field_variable_unit29");
		this.field_variable_unit30 = (String) lhm.get("field_variable_unit30");
		
		this.field_sensor_desciption = (String) lhm.get("field_sensor_desciption");
		this.field_sensor_desciption1 = (String) lhm.get("field_sensor_desciption1");
		this.field_sensor_desciption2 = (String) lhm.get("field_sensor_desciption2");
		this.field_sensor_desciption3 = (String) lhm.get("field_sensor_desciption3");
		this.field_sensor_desciption4 = (String) lhm.get("field_sensor_desciption4");
		this.field_sensor_desciption5 = (String) lhm.get("field_sensor_desciption5");
		this.field_sensor_desciption6 = (String) lhm.get("field_sensor_desciption6");
		this.field_sensor_desciption7 = (String) lhm.get("field_sensor_desciption7");
		this.field_sensor_desciption8 = (String) lhm.get("field_sensor_desciption8");
		this.field_sensor_desciption9 = (String) lhm.get("field_sensor_desciption9");
	}

	public String getField_filestatus() {
		return field_filestatus;
	}

	public void setField_filestatus(String field_filestatus) {
		this.field_filestatus = field_filestatus;
	}

	public String getField_form_type() {
		return field_form_type;
	}

	public void setField_form_type(String field_form_type) {
		this.field_form_type = field_form_type;
	}

	public String getField_record_id() {
		return field_record_id;
	}

	public void setField_record_id(String field_record_id) {
		this.field_record_id = field_record_id;
	}

	public String getField_filename() {
		return field_filename;
	}

	public void setField_filename(String field_filename) {
		this.field_filename = field_filename;
	}

	// User Information

	public String getField_user_email() {
		return field_user_email;
	}

	public String getField_username() {
		return field_username;
	}

	public void setField_username(String field_username) {
		this.field_username = field_username;
	}

	public String getField_user_organizationame() {
		return field_user_organizationame;
	}

	public void setField_user_organizationame(String field_user_organizationame) {
		this.field_user_organizationame = field_user_organizationame;
	}

	public String getField_user_adress() {
		return field_user_adress;
	}

	public void setField_user_adress(String field_user_adress) {
		this.field_user_adress = field_user_adress;
	}

	public String getField_user_telephonenumber() {
		return field_user_telephonenumber;
	}

	public void setField_user_telephonenumber(String field_user_telephonenumber) {
		this.field_user_telephonenumber = field_user_telephonenumber;
	}

	public void setField_user_email(String field_user_email) {
		this.field_user_email = field_user_email;
	}

	// Investigator
	public String getField_ownername() {
		return field_ownername;
	}

	public void setField_ownername(String field_ownername) {
		this.field_ownername = field_ownername;
	}

	public String getField_organizationame() {
		return field_organizationame;
	}

	public void setField_organizationame(String field_organizationame) {
		this.field_organizationame = field_organizationame;
	}

	public String getField_owneraddress() {
		return field_owneraddress;
	}

	public void setField_owneraddress(String field_owneraddress) {
		this.field_owneraddress = field_owneraddress;
	}

	public String getField_telephonenumber() {
		return field_telephonenumber;
	}

	public void setField_telephonenumber(String field_telephonenumber) {
		this.field_telephonenumber = field_telephonenumber;
	}

	public String getField_email() {
		return field_email;
	}

	public void setField_email(String field_email) {
		this.field_email = field_email;
	}

	public String getField_ownername2() {
		return field_ownername2;
	}

	public void setField_ownername2(String field_ownername2) {
		this.field_ownername2 = field_ownername2;
	}

	public String getField_organizationame2() {
		return field_organizationame2;
	}

	public void setField_organizationame2(String field_organizationame2) {
		this.field_organizationame2 = field_organizationame2;
	}

	public String getField_owneraddress2() {
		return field_owneraddress2;
	}

	public void setField_owneraddress2(String field_owneraddress2) {
		this.field_owneraddress2 = field_owneraddress2;
	}

	public String getField_telephonenumber2() {
		return field_telephonenumber2;
	}

	public void setField_telephonenumber2(String field_telephonenumber2) {
		this.field_telephonenumber2 = field_telephonenumber2;
	}

	public String getField_email2() {
		return field_email2;
	}

	public void setField_email2(String field_email2) {
		this.field_email2 = field_email2;
	}

	public String getField_ownername3() {
		return field_ownername3;
	}

	public void setField_ownername3(String field_ownername3) {
		this.field_ownername3 = field_ownername3;
	}

	public String getField_organizationame3() {
		return field_organizationame3;
	}

	public void setField_organizationame3(String field_organizationame3) {
		this.field_organizationame3 = field_organizationame3;
	}

	public String getField_owneraddress3() {
		return field_owneraddress3;
	}

	public void setField_owneraddress3(String field_owneraddress3) {
		this.field_owneraddress3 = field_owneraddress3;
	}

	public String getField_telephonenumber3() {
		return field_telephonenumber3;
	}

	public void setField_telephonenumber3(String field_telephonenumber3) {
		this.field_telephonenumber3 = field_telephonenumber3;
	}

	public String getField_email3() {
		return field_email3;
	}

	public void setField_email3(String field_email3) {
		this.field_email3 = field_email3;
	}

	// Dataset Info
	public String getField_title() {
		return field_title;
	}

	public void setField_title(String field_title) {
		this.field_title = field_title;
	}

	public String getField_funding_info() {
		return field_funding_info;
	}

	public void setField_funding_info(String field_funding_info) {
		this.field_funding_info = field_funding_info;
	}

	public String getField_initial_submission() {
		return field_initial_submission;
	}

	public void setField_initial_submission(String field_initial_submission) {
		this.field_initial_submission = field_initial_submission;
	}

	public String getField_revised_submission() {
		return field_revised_submission;
	}

	public void setField_revised_submission(String field_revised_submission) {
		this.field_revised_submission = field_revised_submission;
	}

	// cruise info
	public String getField_experiment_name() {
		return field_experiment_name;
	}

	public void setField_experiment_name(String field_experiment_name) {
		this.field_experiment_name = field_experiment_name;
	}

	public String getField_survey_type() {
		return field_survey_type;
	}

	public void setField_survey_type(String field_survey_type) {
		this.field_survey_type = field_survey_type;
	}

	public String getField_cruise_id() {
		return field_cruise_id;
	}

	public void setField_cruise_id(String field_cruise_id) {
		this.field_cruise_id = field_cruise_id;
	}

	public String getField_cruise_info() {
		return field_cruise_info;
	}

	public void setField_cruise_info(String field_cruise_info) {
		this.field_cruise_info = field_cruise_info;
	}

	public String getField_section() {
		return field_section;
	}

	public void setField_section(String field_section) {
		this.field_section = field_section;
	}

	public String getField_geographical_area() {
		return field_geographical_area;
	}

	public void setField_geographical_area(String field_geographical_area) {
		this.field_geographical_area = field_geographical_area;
	}

	public String getField_west() {
		return field_west;
	}

	public void setField_west(String field_west) {
		this.field_west = field_west;
	}

	public String getField_west_degree() {
		return field_west_degree;
	}

	public void setField_west_degree(String field_west_degree) {
		this.field_west_degree = field_west_degree;
	}

	public String getField_west_minute() {
		return field_west_minute;
	}

	public void setField_west_minute(String field_west_minute) {
		this.field_west_minute = field_west_minute;
	}

	public String getField_west_second() {
		return field_west_second;
	}

	public void setField_west_second(String field_west_second) {
		this.field_west_second = field_west_second;
	}

	public String getField_west_EW() {
		return field_west_EW;
	}

	public void setField_west_EW(String field_west_EW) {
		this.field_west_EW = field_west_EW;
	}

	public String getField_east() {
		return field_east;
	}

	public void setField_east(String field_east) {
		this.field_east = field_east;
	}

	public String getField_east_degree() {
		return field_east_degree;
	}

	public void setField_east_degree(String field_east_degree) {
		this.field_east_degree = field_east_degree;
	}

	public String getField_east_minute() {
		return field_east_minute;
	}

	public void setField_east_minute(String field_east_minute) {
		this.field_east_minute = field_east_minute;
	}

	public String getField_east_second() {
		return field_east_second;
	}

	public void setField_east_second(String field_east_second) {
		this.field_east_second = field_east_second;
	}

	public String getField_east_EW() {
		return field_east_EW;
	}

	public void setField_east_EW(String field_east_EW) {
		this.field_east_EW = field_east_EW;
	}

	public String getField_north() {
		return field_north;
	}

	public void setField_north(String field_north) {
		this.field_north = field_north;
	}

	public String getField_north_degree() {
		return field_north_degree;
	}

	public void setField_north_degree(String field_north_degree) {
		this.field_north_degree = field_north_degree;
	}

	public String getField_north_minute() {
		return field_north_minute;
	}

	public void setField_north_minute(String field_north_minute) {
		this.field_north_minute = field_north_minute;
	}

	public String getField_north_second() {
		return field_north_second;
	}

	public void setField_north_second(String field_north_second) {
		this.field_north_second = field_north_second;
	}

	public String getField_north_NS() {
		return field_north_NS;
	}

	public void setField_north_NS(String field_north_NS) {
		this.field_north_NS = field_north_NS;
	}

	public String getField_south() {
		return field_south;
	}

	public void setField_south(String field_south) {
		this.field_south = field_south;
	}

	public String getField_south_degree() {
		return field_south_degree;
	}

	public void setField_south_degree(String field_south_degree) {
		this.field_south_degree = field_south_degree;
	}

	public String getField_south_minute() {
		return field_south_minute;
	}

	public void setField_south_minute(String field_south_minute) {
		this.field_south_minute = field_south_minute;
	}

	public String getField_south_second() {
		return field_south_second;
	}

	public void setField_south_second(String field_south_second) {
		this.field_south_second = field_south_second;
	}

	public String getField_south_NS() {
		return field_south_NS;
	}

	public void setField_south_NS(String field_south_NS) {
		this.field_south_NS = field_south_NS;
	}

	public String getField_start_date() {
		return field_start_date;
	}

	public void setField_start_date(String field_start_date) {
		this.field_start_date = field_start_date;
	}

	public String getField_end_date() {
		return field_end_date;
	}

	public void setField_end_date(String field_end_date) {
		this.field_end_date = field_end_date;
	}

	public String getField_port_of_call() {
		return field_port_of_call;
	}

	public void setField_port_of_call(String field_port_of_call) {
		this.field_port_of_call = field_port_of_call;
	}

	public String getField_vessel_name() {
		return field_vessel_name;
	}

	public void setField_vessel_name(String field_vessel_name) {
		this.field_vessel_name = field_vessel_name;
	}

	public String getField_vessel_id() {
		return field_vessel_id;
	}

	public void setField_vessel_id(String field_vessel_id) {
		this.field_vessel_id = field_vessel_id;
	}

	public String getField_vessel_country() {
		return field_vessel_country;
	}

	public void setField_vessel_country(String field_vessel_country) {
		this.field_vessel_country = field_vessel_country;
	}

	public String getField_vessel_owner() {
		return field_vessel_owner;
	}

	public void setField_vessel_owner(String field_vessel_owner) {
		this.field_vessel_owner = field_vessel_owner;
	}

	// variable description

	public String getField_variable_description0() {
		return field_variable_description0;
	}

	public String getField_variable0() {
		return field_variable0;
	}

	public void setField_variable0(String field_variable0) {
		this.field_variable0 = field_variable0;
	}

	public void setField_variable_description0(
			String field_variable_description0) {
		this.field_variable_description0 = field_variable_description0;
	}

	public String getField_variable1() {
		return field_variable1;
	}

	public void setField_variable1(String field_variable1) {
		this.field_variable1 = field_variable1;
	}

	public String getField_variable_description1() {
		return field_variable_description1;
	}

	public void setField_variable_description1(
			String field_variable_description1) {
		this.field_variable_description1 = field_variable_description1;
	}

	public String getField_variable2() {
		return field_variable2;
	}

	public void setField_variable2(String field_variable2) {
		this.field_variable2 = field_variable2;
	}

	public String getField_variable_description2() {
		return field_variable_description2;
	}

	public void setField_variable_description2(
			String field_variable_description2) {
		this.field_variable_description2 = field_variable_description2;
	}

	public String getField_variable3() {
		return field_variable3;
	}

	public void setField_variable3(String field_variable3) {
		this.field_variable3 = field_variable3;
	}

	public String getField_variable_description3() {
		return field_variable_description3;
	}

	public void setField_variable_description3(
			String field_variable_description3) {
		this.field_variable_description3 = field_variable_description3;
	}

	public String getField_variable4() {
		return field_variable4;
	}

	public void setField_variable4(String field_variable4) {
		this.field_variable4 = field_variable4;
	}

	public String getField_variable_description4() {
		return field_variable_description4;
	}

	public void setField_variable_description4(
			String field_variable_description4) {
		this.field_variable_description4 = field_variable_description4;
	}

	public String getField_variable5() {
		return field_variable5;
	}

	public void setField_variable5(String field_variable5) {
		this.field_variable5 = field_variable5;
	}

	public String getField_variable_description5() {
		return field_variable_description5;
	}

	public void setField_variable_description5(
			String field_variable_description5) {
		this.field_variable_description5 = field_variable_description5;
	}

	public String getField_variable6() {
		return field_variable6;
	}

	public void setField_variable6(String field_variable6) {
		this.field_variable6 = field_variable6;
	}

	public String getField_variable_description6() {
		return field_variable_description6;
	}

	public void setField_variable_description6(
			String field_variable_description6) {
		this.field_variable_description6 = field_variable_description6;
	}

	public String getField_variable7() {
		return field_variable7;
	}

	public void setField_variable7(String field_variable7) {
		this.field_variable7 = field_variable7;
	}

	public String getField_variable_description7() {
		return field_variable_description7;
	}

	public void setField_variable_description7(
			String field_variable_description7) {
		this.field_variable_description7 = field_variable_description7;
	}

	public String getField_variable8() {
		return field_variable8;
	}

	public void setField_variable8(String field_variable8) {
		this.field_variable8 = field_variable8;
	}

	public String getField_variable_description8() {
		return field_variable_description8;
	}

	public void setField_variable_description8(
			String field_variable_description8) {
		this.field_variable_description8 = field_variable_description8;
	}

	public String getField_variable9() {
		return field_variable9;
	}

	public void setField_variable9(String field_variable9) {
		this.field_variable9 = field_variable9;
	}

	public String getField_variable_description9() {
		return field_variable_description9;
	}

	public void setField_variable_description9(
			String field_variable_description9) {
		this.field_variable_description9 = field_variable_description9;
	}

	public String getField_variable10() {
		return field_variable10;
	}

	public void setField_variable10(String field_variable10) {
		this.field_variable10 = field_variable10;
	}

	public String getField_variable_description10() {
		return field_variable_description10;
	}

	public void setField_variable_description10(
			String field_variable_description10) {
		this.field_variable_description10 = field_variable_description10;
	}

	public String getField_variable11() {
		return field_variable11;
	}

	public void setField_variable11(String field_variable11) {
		this.field_variable11 = field_variable11;
	}

	public String getField_variable_description11() {
		return field_variable_description11;
	}

	public void setField_variable_description11(
			String field_variable_description11) {
		this.field_variable_description11 = field_variable_description11;
	}

	public String getField_variable12() {
		return field_variable12;
	}

	public void setField_variable12(String field_variable12) {
		this.field_variable12 = field_variable12;
	}

	public String getField_variable_description12() {
		return field_variable_description12;
	}

	public void setField_variable_description12(
			String field_variable_description12) {
		this.field_variable_description12 = field_variable_description12;
	}

	public String getField_variable13() {
		return field_variable13;
	}

	public void setField_variable13(String field_variable13) {
		this.field_variable13 = field_variable13;
	}

	public String getField_variable_description13() {
		return field_variable_description13;
	}

	public void setField_variable_description13(
			String field_variable_description13) {
		this.field_variable_description13 = field_variable_description13;
	}

	public String getField_variable14() {
		return field_variable14;
	}

	public void setField_variable14(String field_variable14) {
		this.field_variable14 = field_variable14;
	}

	public String getField_variable_description14() {
		return field_variable_description14;
	}

	public void setField_variable_description14(
			String field_variable_description14) {
		this.field_variable_description14 = field_variable_description14;
	}

	public String getField_variable_sensor_model0() {
		return field_variable_sensor_model0;
	}

	public String getField_co2_data_type0() {
		return field_co2_data_type0;
	}

	public void setField_co2_data_type0(String field_co2_data_type0) {
		this.field_co2_data_type0 = field_co2_data_type0;
	}

	public String getField_co2_data_unit0() {
		return field_co2_data_unit0;
	}

	public void setField_co2_data_unit0(String field_co2_data_unit0) {
		this.field_co2_data_unit0 = field_co2_data_unit0;
	}

	public String getField_co2_data_type1() {
		return field_co2_data_type1;
	}

	public void setField_co2_data_type1(String field_co2_data_type1) {
		this.field_co2_data_type1 = field_co2_data_type1;
	}

	public String getField_co2_data_unit1() {
		return field_co2_data_unit1;
	}

	public void setField_co2_data_unit1(String field_co2_data_unit1) {
		this.field_co2_data_unit1 = field_co2_data_unit1;
	}

	public String getField_co2_data_type2() {
		return field_co2_data_type2;
	}

	public void setField_co2_data_type2(String field_co2_data_type2) {
		this.field_co2_data_type2 = field_co2_data_type2;
	}

	public String getField_co2_data_unit2() {
		return field_co2_data_unit2;
	}

	public void setField_co2_data_unit2(String field_co2_data_unit2) {
		this.field_co2_data_unit2 = field_co2_data_unit2;
	}

	public String getField_co2_data_type3() {
		return field_co2_data_type3;
	}

	public void setField_co2_data_type3(String field_co2_data_type3) {
		this.field_co2_data_type3 = field_co2_data_type3;
	}

	public String getField_co2_data_unit3() {
		return field_co2_data_unit3;
	}

	public void setField_co2_data_unit3(String field_co2_data_unit3) {
		this.field_co2_data_unit3 = field_co2_data_unit3;
	}

	public String getField_co2_data_type4() {
		return field_co2_data_type4;
	}

	public void setField_co2_data_type4(String field_co2_data_type4) {
		this.field_co2_data_type4 = field_co2_data_type4;
	}

	public String getField_co2_data_unit4() {
		return field_co2_data_unit4;
	}

	public void setField_co2_data_unit4(String field_co2_data_unit4) {
		this.field_co2_data_unit4 = field_co2_data_unit4;
	}

	public String getField_co2_data_type5() {
		return field_co2_data_type5;
	}

	public void setField_co2_data_type5(String field_co2_data_type5) {
		this.field_co2_data_type5 = field_co2_data_type5;
	}

	public String getField_co2_data_unit5() {
		return field_co2_data_unit5;
	}

	public void setField_co2_data_unit5(String field_co2_data_unit5) {
		this.field_co2_data_unit5 = field_co2_data_unit5;
	}

	public String getField_co2_data_type6() {
		return field_co2_data_type6;
	}

	public void setField_co2_data_type6(String field_co2_data_type6) {
		this.field_co2_data_type6 = field_co2_data_type6;
	}

	public String getField_co2_data_unit6() {
		return field_co2_data_unit6;
	}

	public void setField_co2_data_unit6(String field_co2_data_unit6) {
		this.field_co2_data_unit6 = field_co2_data_unit6;
	}

	public String getField_co2_data_type7() {
		return field_co2_data_type7;
	}

	public void setField_co2_data_type7(String field_co2_data_type7) {
		this.field_co2_data_type7 = field_co2_data_type7;
	}

	public String getField_co2_data_unit7() {
		return field_co2_data_unit7;
	}

	public void setField_co2_data_unit7(String field_co2_data_unit7) {
		this.field_co2_data_unit7 = field_co2_data_unit7;
	}

	public String getField_co2_data_type8() {
		return field_co2_data_type8;
	}

	public void setField_co2_data_type8(String field_co2_data_type8) {
		this.field_co2_data_type8 = field_co2_data_type8;
	}

	public String getField_co2_data_unit8() {
		return field_co2_data_unit8;
	}

	public void setField_co2_data_unit8(String field_co2_data_unit8) {
		this.field_co2_data_unit8 = field_co2_data_unit8;
	}

	public void setField_variable_sensor_model0(
			String field_variable_sensor_model0) {
		this.field_variable_sensor_model0 = field_variable_sensor_model0;
	}

	// Method Description

	public String getField_equilibrator_type() {
		return field_equilibrator_type;
	}

	public String getField_depth_seawater_intake() {
		return field_depth_seawater_intake;
	}

	public void setField_depth_seawater_intake(
			String field_depth_seawater_intake) {
		this.field_depth_seawater_intake = field_depth_seawater_intake;
	}

	public String getField_location_seawater_intake() {
		return field_location_seawater_intake;
	}

	public void setField_location_seawater_intake(
			String field_location_seawater_intake) {
		this.field_location_seawater_intake = field_location_seawater_intake;
	}

	public void setField_equilibrator_type(String field_equilibrator_type) {
		this.field_equilibrator_type = field_equilibrator_type;
	}

	public String getField_equilibration_volume() {
		return field_equilibration_volume;
	}

	public void setField_equilibration_volume(String field_equilibration_volume) {
		this.field_equilibration_volume = field_equilibration_volume;
	}

	public String getField_water_flow_rate() {
		return field_water_flow_rate;
	}

	public void setField_water_flow_rate(String field_water_flow_rate) {
		this.field_water_flow_rate = field_water_flow_rate;
	}

	public String getField_gas_flow_rate() {
		return field_gas_flow_rate;
	}

	public void setField_gas_flow_rate(String field_gas_flow_rate) {
		this.field_gas_flow_rate = field_gas_flow_rate;
	}

	public String getField_vented() {
		return field_vented;
	}

	public void setField_vented(String field_vented) {
		this.field_vented = field_vented;
	}

	public String getField_drying_method() {
		return field_drying_method;
	}

	public void setField_drying_method(String field_drying_method) {
		this.field_drying_method = field_drying_method;
	}

	public String getField_equilibrium_comments() {
		return field_equilibrium_comments;
	}

	public void setField_equilibrium_comments(String field_equilibrium_comments) {
		this.field_equilibrium_comments = field_equilibrium_comments;
	}

	public String getField_co2_measurement() {
		return field_co2_measurement;
	}

	public void setField_co2_measurement(String field_co2_measurement) {
		this.field_co2_measurement = field_co2_measurement;
	}

	public String getField_marine_airtake() {
		return field_marine_airtake;
	}

	public void setField_marine_airtake(String field_marine_airtake) {
		this.field_marine_airtake = field_marine_airtake;
	}

	public String getField_co2_drying() {
		return field_co2_drying;
	}

	public void setField_co2_drying(String field_co2_drying) {
		this.field_co2_drying = field_co2_drying;
	}

	public String getField_measurement_method() {
		return field_measurement_method;
	}

	public void setField_measurement_method(String field_measurement_method) {
		this.field_measurement_method = field_measurement_method;
	}

	public String getField_manufacturer_calibration() {
		return field_manufacturer_calibration;
	}

	public void setField_manufacturer_calibration(
			String field_manufacturer_calibration) {
		this.field_manufacturer_calibration = field_manufacturer_calibration;
	}

	public String getField_manufacturer() {
		return field_manufacturer;
	}

	public void setField_manufacturer(String field_manufacturer) {
		this.field_manufacturer = field_manufacturer;
	}

	public String getField_model() {
		return field_model;
	}

	public void setField_model(String field_model) {
		this.field_model = field_model;
	}

	public String getField_environmental_control() {
		return field_environmental_control;
	}

	public void setField_environmental_control(
			String field_environmental_control) {
		this.field_environmental_control = field_environmental_control;
	}

	public String getField_frequency() {
		return field_frequency;
	}

	public void setField_frequency(String field_frequency) {
		this.field_frequency = field_frequency;
	}

	public String getField_resolution_air() {
		return field_resolution_air;
	}

	public void setField_resolution_air(String field_resolution_air) {
		this.field_resolution_air = field_resolution_air;
	}

	public String getField_uncertainity_air() {
		return field_uncertainity_air;
	}

	public void setField_uncertainity_air(String field_uncertainity_air) {
		this.field_uncertainity_air = field_uncertainity_air;
	}

	public String getField_resolution() {
		return field_resolution;
	}

	public void setField_resolution(String field_resolution) {
		this.field_resolution = field_resolution;
	}

	public String getField_calibration() {
		return field_calibration;
	}

	public void setField_calibration(String field_calibration) {
		this.field_calibration = field_calibration;
	}

	public String getField_uncertainity() {
		return field_uncertainity;
	}

	public void setField_uncertainity(String field_uncertainity) {
		this.field_uncertainity = field_uncertainity;
	}

	public String getField_manufaturer_other() {
		return field_manufaturer_other;
	}

	public void setField_manufaturer_other(String field_manufaturer_other) {
		this.field_manufaturer_other = field_manufaturer_other;
	}

	public String getField_model_other() {
		return field_model_other;
	}

	public void setField_model_other(String field_model_other) {
		this.field_model_other = field_model_other;
	}

	public String getField_resolution_other() {
		return field_resolution_other;
	}

	public void setField_resolution_other(String field_resolution_other) {
		this.field_resolution_other = field_resolution_other;
	}

	public String getField_calibration_other() {
		return field_calibration_other;
	}

	public void setField_calibration_other(String field_calibration_other) {
		this.field_calibration_other = field_calibration_other;
	}

	public String getField_accuracy_other() {
		return field_accuracy_other;
	}

	public void setField_accuracy_other(String field_accuracy_other) {
		this.field_accuracy_other = field_accuracy_other;
	}

	public String getField_comment_other() {
		return field_comment_other;
	}

	public void setField_comment_other(String field_comment_other) {
		this.field_comment_other = field_comment_other;
	}

	public String getField_method_references() {
		return field_method_references;
	}

	public void setField_method_references(String field_method_references) {
		this.field_method_references = field_method_references;
	}

	// others

	public String getField_manufaturer_other1() {
		return field_manufaturer_other1;
	}

	public void setField_manufaturer_other1(String field_manufaturer_other1) {
		this.field_manufaturer_other1 = field_manufaturer_other1;
	}

	public String getField_accuracy_other1() {
		return field_accuracy_other1;
	}

	public void setField_accuracy_other1(String field_accuracy_other1) {
		this.field_accuracy_other1 = field_accuracy_other1;
	}

	public String getField_model_other1() {
		return field_model_other1;
	}

	public void setField_model_other1(String field_model_other1) {
		this.field_model_other1 = field_model_other1;
	}

	public String getField_resolution_other1() {
		return field_resolution_other1;
	}

	public void setField_resolution_other1(String field_resolution_other1) {
		this.field_resolution_other1 = field_resolution_other1;
	}

	public String getField_calibration_other1() {
		return field_calibration_other1;
	}

	public void setField_calibration_other1(String field_calibration_other1) {
		this.field_calibration_other1 = field_calibration_other1;
	}

	public String getField_comment_other1() {
		return field_comment_other1;
	}

	public void setField_comment_other1(String field_comment_other1) {
		this.field_comment_other1 = field_comment_other1;
	}

	public String getField_method_references1() {
		return field_method_references1;
	}

	public void setField_method_references1(String field_method_references1) {
		this.field_method_references1 = field_method_references1;
	}

	public String getField_manufaturer_other2() {
		return field_manufaturer_other2;
	}

	public void setField_manufaturer_other2(String field_manufaturer_other2) {
		this.field_manufaturer_other2 = field_manufaturer_other2;
	}

	public String getField_accuracy_other2() {
		return field_accuracy_other2;
	}

	public void setField_accuracy_other2(String field_accuracy_other2) {
		this.field_accuracy_other2 = field_accuracy_other2;
	}

	public String getField_model_other2() {
		return field_model_other2;
	}

	public void setField_model_other2(String field_model_other2) {
		this.field_model_other2 = field_model_other2;
	}

	public String getField_resolution_other2() {
		return field_resolution_other2;
	}

	public void setField_resolution_other2(String field_resolution_other2) {
		this.field_resolution_other2 = field_resolution_other2;
	}

	public String getField_calibration_other2() {
		return field_calibration_other2;
	}

	public void setField_calibration_other2(String field_calibration_other2) {
		this.field_calibration_other2 = field_calibration_other2;
	}

	public String getField_comment_other2() {
		return field_comment_other2;
	}

	public void setField_comment_other2(String field_comment_other2) {
		this.field_comment_other2 = field_comment_other2;
	}

	public String getField_method_references2() {
		return field_method_references2;
	}

	public void setField_method_references2(String field_method_references2) {
		this.field_method_references2 = field_method_references2;
	}

	// method description II
	public String getField_TCO2_analyis_method() {
		return field_TCO2_analyis_method;
	}

	public void setField_TCO2_analyis_method(String field_TCO2_analyis_method) {
		this.field_TCO2_analyis_method = field_TCO2_analyis_method;
	}

	public String getField_technique_description() {
		return field_technique_description;
	}

	public void setField_technique_description(
			String field_technique_description) {
		this.field_technique_description = field_technique_description;
	}

	public String getField_sample_volume() {
		return field_sample_volume;
	}

	public void setField_sample_volume(String field_sample_volume) {
		this.field_sample_volume = field_sample_volume;
	}

	public String getField_correction_magnitude() {
		return field_correction_magnitude;
	}

	public void setField_correction_magnitude(String field_correction_magnitude) {
		this.field_correction_magnitude = field_correction_magnitude;
	}

	public String getField_batch_number() {
		return field_batch_number;
	}

	public void setField_batch_number(String field_batch_number) {
		this.field_batch_number = field_batch_number;
	}

	public String getField_CRM_analysis_info() {
		return field_CRM_analysis_info;
	}

	public void setField_CRM_analysis_info(String field_CRM_analysis_info) {
		this.field_CRM_analysis_info = field_CRM_analysis_info;
	}

	public String getField_replicate_info() {
		return field_replicate_info;
	}

	public void setField_replicate_info(String field_replicate_info) {
		this.field_replicate_info = field_replicate_info;
	}

	public String getField_poisoning_correction_description() {
		return field_poisoning_correction_description;
	}

	public void setField_poisoning_correction_description(
			String field_poisoning_correction_description) {
		this.field_poisoning_correction_description = field_poisoning_correction_description;
	}

	public String getField_poison_volume() {
		return field_poison_volume;
	}

	public void setField_poison_volume(String field_poison_volume) {
		this.field_poison_volume = field_poison_volume;
	}

	public String getField_accuracy_CO2_info() {
		return field_accuracy_CO2_info;
	}

	public void setField_accuracy_CO2_info(String field_accuracy_CO2_info) {
		this.field_accuracy_CO2_info = field_accuracy_CO2_info;
	}

	public String getField_CO2_method_reference() {
		return field_CO2_method_reference;
	}

	public void setField_CO2_method_reference(String field_CO2_method_reference) {
		this.field_CO2_method_reference = field_CO2_method_reference;
	}
    
	public String getField_detail_sensing() {
		return field_detail_sensing;
	}

	public void setField_detail_sensing(String field_detail_sensing) {
		this.field_detail_sensing = field_detail_sensing;
	}

	// Method Description II----Alkalinity
	public String getField_curve_fitting_method() {
		return field_curve_fitting_method;
	}

	public void setField_curve_fitting_method(String field_curve_fitting_method) {
		this.field_curve_fitting_method = field_curve_fitting_method;
	}

	public String getField_type_of_titration() {
		return field_type_of_titration;
	}

	public void setField_type_of_titration(String field_type_of_titration) {
		this.field_type_of_titration = field_type_of_titration;
	}

	public String getField_description_of_other_titration() {
		return field_description_of_other_titration;
	}

	public void setField_description_of_other_titration(
			String field_description_of_other_titration) {
		this.field_description_of_other_titration = field_description_of_other_titration;
	}

	public String getField_cell_type() {
		return field_cell_type;
	}

	public void setField_cell_type(String field_cell_type) {
		this.field_cell_type = field_cell_type;
	}

	public String getField_CRM_scale() {
		return field_CRM_scale;
	}

	public void setField_CRM_scale(String field_CRM_scale) {
		this.field_CRM_scale = field_CRM_scale;
	}

	public String getField_alkalinity_sample_volume() {
		return field_alkalinity_sample_volume;
	}

	public void setField_alkalinity_sample_volume(
			String field_alkalinity_sample_volume) {
		this.field_alkalinity_sample_volume = field_alkalinity_sample_volume;
	}

	public String getField_black_correction() {
		return field_black_correction;
	}

	public void setField_black_correction(String field_black_correction) {
		this.field_black_correction = field_black_correction;
	}

	public String getField_alkalinity_accuracy_info() {
		return field_alkalinity_accuracy_info;
	}

	public void setField_alkalinity_accuracy_info(
			String field_alkalinity_accuracy_info) {
		this.field_alkalinity_accuracy_info = field_alkalinity_accuracy_info;
	}

	public String getField_alkalinity_method_references() {
		return field_alkalinity_method_references;
	}

	public void setField_alkalinity_method_references(
			String field_alkalinity_method_references) {
		this.field_alkalinity_method_references = field_alkalinity_method_references;
	}

	// Method Description II----PCO2 Data
	public String getField_pCO2_analysis_method() {
		return field_pCO2_analysis_method;
	}

	public void setField_pCO2_analysis_method(String field_pCO2_analysis_method) {
		this.field_pCO2_analysis_method = field_pCO2_analysis_method;
	}

	public String getField_PCO2_sample_volume() {
		return field_PCO2_sample_volume;
	}

	public void setField_PCO2_sample_volume(String field_PCO2_sample_volume) {
		this.field_PCO2_sample_volume = field_PCO2_sample_volume;
	}

	public String getField_headspace_volume() {
		return field_headspace_volume;
	}

	public void setField_headspace_volume(String field_headspace_volume) {
		this.field_headspace_volume = field_headspace_volume;
	}

	public String getField_measurement_temperature() {
		return field_measurement_temperature;
	}

	public void setField_measurement_temperature(
			String field_measurement_temperature) {
		this.field_measurement_temperature = field_measurement_temperature;
	}

	public String getField_temperature_normalization() {
		return field_temperature_normalization;
	}

	public void setField_temperature_normalization(
			String field_temperature_normalization) {
		this.field_temperature_normalization = field_temperature_normalization;
	}

	public String getField_temperature_correction_method() {
		return field_temperature_correction_method;
	}

	public void setField_temperature_correction_method(
			String field_temperature_correction_method) {
		this.field_temperature_correction_method = field_temperature_correction_method;
	}

	public String getField_variable_reported() {
		return field_variable_reported;
	}

	public void setField_variable_reported(String field_variable_reported) {
		this.field_variable_reported = field_variable_reported;
	}

	public String getField_gas() {
		return field_gas;
	}

	public void setField_gas(String field_gas) {
		this.field_gas = field_gas;
	}

	public String getField_gas_concentrations() {
		return field_gas_concentrations;
	}

	public void setField_gas_concentrations(String field_gas_concentrations) {
		this.field_gas_concentrations = field_gas_concentrations;
	}

	public String getField_frequesncy_of_standardization() {
		return field_frequesncy_of_standardization;
	}

	public void setField_frequesncy_of_standardization(
			String field_frequesncy_of_standardization) {
		this.field_frequesncy_of_standardization = field_frequesncy_of_standardization;
	}

	public String getField_PCO2_replicate_info() {
		return field_PCO2_replicate_info;
	}

	public void setField_PCO2_replicate_info(String field_PCO2_replicate_info) {
		this.field_PCO2_replicate_info = field_PCO2_replicate_info;
	}

	public String getField_PCO2_storage_method() {
		return field_PCO2_storage_method;
	}

	public void setField_PCO2_storage_method(String field_PCO2_storage_method) {
		this.field_PCO2_storage_method = field_PCO2_storage_method;
	}

	public String getField_PCO2_accuracy_info() {
		return field_PCO2_accuracy_info;
	}

	public void setField_PCO2_accuracy_info(String field_PCO2_accuracy_info) {
		this.field_PCO2_accuracy_info = field_PCO2_accuracy_info;
	}

	public String getField_PCO2_method_references() {
		return field_PCO2_method_references;
	}

	public void setField_PCO2_method_references(
			String field_PCO2_method_references) {
		this.field_PCO2_method_references = field_PCO2_method_references;
	}

	// method description II-->pH Data
	public String getField_ph_scale() {
		return field_ph_scale;
	}

	public void setField_ph_scale(String field_ph_scale) {
		this.field_ph_scale = field_ph_scale;
	}

	public String getField_ph_analysis_method() {
		return field_ph_analysis_method;
	}

	public void setField_ph_analysis_method(String field_ph_analysis_method) {
		this.field_ph_analysis_method = field_ph_analysis_method;
	}

	public String getField_calibration_description() {
		return field_calibration_description;
	}

	public void setField_calibration_description(
			String field_calibration_description) {
		this.field_calibration_description = field_calibration_description;
	}

	public String getField_in_situ_temperature() {
		return field_in_situ_temperature;
	}

	public void setField_in_situ_temperature(String field_in_situ_temperature) {
		this.field_in_situ_temperature = field_in_situ_temperature;
	}

	public String getField_temperature_of_analysis() {
		return field_temperature_of_analysis;
	}

	public void setField_temperature_of_analysis(
			String field_temperature_of_analysis) {
		this.field_temperature_of_analysis = field_temperature_of_analysis;
	}

	public String getField__ph_temperature_normalization() {
		return field__ph_temperature_normalization;
	}

	public void setField__ph_temperature_normalization(
			String field__ph_temperature_normalization) {
		this.field__ph_temperature_normalization = field__ph_temperature_normalization;
	}

	public String getField_in_situ_pressure() {
		return field_in_situ_pressure;
	}

	public void setField_in_situ_pressure(String field_in_situ_pressure) {
		this.field_in_situ_pressure = field_in_situ_pressure;
	}

	public String getField_accuracy_info() {
		return field_accuracy_info;
	}

	public void setField_accuracy_info(String field_accuracy_info) {
		this.field_accuracy_info = field_accuracy_info;
	}

	public String getField_ph_method_references() {
		return field_ph_method_references;
	}

	public void setField_ph_method_references(String field_ph_method_references) {
		this.field_ph_method_references = field_ph_method_references;
	}

	// Sensors

	public String getField_SST_location() {
		return field_SST_location;
	}

	public void setField_SST_location(String field_SST_location) {
		this.field_SST_location = field_SST_location;
	}

	public String getField_SST_manufacturer() {
		return field_SST_manufacturer;
	}

	public void setField_SST_manufacturer(String field_SST_manufacturer) {
		this.field_SST_manufacturer = field_SST_manufacturer;
	}

	public String getField_SST_model() {
		return field_SST_model;
	}

	public void setField_SST_model(String field_SST_model) {
		this.field_SST_model = field_SST_model;
	}

	public String getField_SST_accuracy() {
		return field_SST_accuracy;
	}

	public void setField_SST_accuracy(String field_SST_accuracy) {
		this.field_SST_accuracy = field_SST_accuracy;
	}

	public String getField_SST_precision() {
		return field_SST_precision;
	}

	public void setField_SST_precision(String field_SST_precision) {
		this.field_SST_precision = field_SST_precision;
	}

	public String getField_SST_calibration() {
		return field_SST_calibration;
	}

	public void setField_SST_calibration(String field_SST_calibration) {
		this.field_SST_calibration = field_SST_calibration;
	}

	public String getField_SST_comments() {
		return field_SST_comments;
	}

	public void setField_SST_comments(String field_SST_comments) {
		this.field_SST_comments = field_SST_comments;
	}

	public String getField_Tequ_location() {
		return field_Tequ_location;
	}

	public void setField_Tequ_location(String field_Tequ_location) {
		this.field_Tequ_location = field_Tequ_location;
	}

	public String getField_Tequ_manufacturer() {
		return field_Tequ_manufacturer;
	}

	public void setField_Tequ_manufacturer(String field_Tequ_manufacturer) {
		this.field_Tequ_manufacturer = field_Tequ_manufacturer;
	}

	public String getField_Tequ_model() {
		return field_Tequ_model;
	}

	public void setField_Tequ_model(String field_Tequ_model) {
		this.field_Tequ_model = field_Tequ_model;
	}

	public String getField_Tequ_accuracy() {
		return field_Tequ_accuracy;
	}

	public void setField_Tequ_accuracy(String field_Tequ_accuracy) {
		this.field_Tequ_accuracy = field_Tequ_accuracy;
	}

	public String getField_Tequ_precision() {
		return field_Tequ_precision;
	}

	public void setField_Tequ_precision(String field_Tequ_precision) {
		this.field_Tequ_precision = field_Tequ_precision;
	}

	public String getField_Tequ_calibration() {
		return field_Tequ_calibration;
	}

	public void setField_Tequ_calibration(String field_Tequ_calibration) {
		this.field_Tequ_calibration = field_Tequ_calibration;
	}

	public String getField_Tequ_warming() {
		return field_Tequ_warming;
	}

	public void setField_Tequ_warming(String field_Tequ_warming) {
		this.field_Tequ_warming = field_Tequ_warming;
	}

	public String getField_Tequ_comments() {
		return field_Tequ_comments;
	}

	public void setField_Tequ_comments(String field_Tequ_comments) {
		this.field_Tequ_comments = field_Tequ_comments;
	}

	public String getField_Pequ_sensor() {
		return field_Pequ_sensor;
	}

	public void setField_Pequ_sensor(String field_Pequ_sensor) {
		this.field_Pequ_sensor = field_Pequ_sensor;
	}

	public String getField_Pequ_manufacturer() {
		return field_Pequ_manufacturer;
	}

	public void setField_Pequ_manufacturer(String field_Pequ_manufacturer) {
		this.field_Pequ_manufacturer = field_Pequ_manufacturer;
	}

	public String getField_Pequ_model() {
		return field_Pequ_model;
	}

	public void setField_Pequ_model(String field_Pequ_model) {
		this.field_Pequ_model = field_Pequ_model;
	}

	public String getField_Pequ_accuracy() {
		return field_Pequ_accuracy;
	}

	public void setField_Pequ_accuracy(String field_Pequ_accuracy) {
		this.field_Pequ_accuracy = field_Pequ_accuracy;
	}

	public String getField_Pequ_precision() {
		return field_Pequ_precision;
	}

	public void setField_Pequ_precision(String field_Pequ_precision) {
		this.field_Pequ_precision = field_Pequ_precision;
	}

	public String getField_Pequ_calibration() {
		return field_Pequ_calibration;
	}

	public void setField_Pequ_calibration(String field_Pequ_calibration) {
		this.field_Pequ_calibration = field_Pequ_calibration;
	}

	public String getField_Pequ_comments() {
		return field_Pequ_comments;
	}

	public void setField_Pequ_comments(String field_Pequ_comments) {
		this.field_Pequ_comments = field_Pequ_comments;
	}

	public String getField_Patm_sensor() {
		return field_Patm_sensor;
	}

	public void setField_Patm_sensor(String field_Patm_sensor) {
		this.field_Patm_sensor = field_Patm_sensor;
	}

	public String getField_Patm_manufacturer() {
		return field_Patm_manufacturer;
	}

	public void setField_Patm_manufacturer(String field_Patm_manufacturer) {
		this.field_Patm_manufacturer = field_Patm_manufacturer;
	}

	public String getField_Patm_model() {
		return field_Patm_model;
	}

	public void setField_Patm_model(String field_Patm_model) {
		this.field_Patm_model = field_Patm_model;
	}

	public String getField_Patm_accuracy() {
		return field_Patm_accuracy;
	}

	public void setField_Patm_accuracy(String field_Patm_accuracy) {
		this.field_Patm_accuracy = field_Patm_accuracy;
	}

	public String getField_Patm_precision() {
		return field_Patm_precision;
	}

	public void setField_Patm_precision(String field_Patm_precision) {
		this.field_Patm_precision = field_Patm_precision;
	}

	public String getField_Patm_calibration() {
		return field_Patm_calibration;
	}

	public void setField_Patm_calibration(String field_Patm_calibration) {
		this.field_Patm_calibration = field_Patm_calibration;
	}

	public String getField_Patm_comments() {
		return field_Patm_comments;
	}

	public void setField_Patm_comments(String field_Patm_comments) {
		this.field_Patm_comments = field_Patm_comments;
	}

	public String getField_SSS_sensor() {
		return field_SSS_sensor;
	}

	public void setField_SSS_sensor(String field_SSS_sensor) {
		this.field_SSS_sensor = field_SSS_sensor;
	}

	public String getField_SSS_manufacturer() {
		return field_SSS_manufacturer;
	}

	public void setField_SSS_manufacturer(String field_SSS_manufacturer) {
		this.field_SSS_manufacturer = field_SSS_manufacturer;
	}

	public String getField_SSS_model() {
		return field_SSS_model;
	}

	public void setField_SSS_model(String field_SSS_model) {
		this.field_SSS_model = field_SSS_model;
	}

	public String getField_SSS_accuracy() {
		return field_SSS_accuracy;
	}

	public void setField_SSS_accuracy(String field_SSS_accuracy) {
		this.field_SSS_accuracy = field_SSS_accuracy;
	}

	public String getField_SSS_precision() {
		return field_SSS_precision;
	}

	public void setField_SSS_precision(String field_SSS_precision) {
		this.field_SSS_precision = field_SSS_precision;
	}

	public String getField_SSS_calibration() {
		return field_SSS_calibration;
	}

	public void setField_SSS_calibration(String field_SSS_calibration) {
		this.field_SSS_calibration = field_SSS_calibration;
	}

	public String getField_SSS_comments() {
		return field_SSS_comments;
	}

	public void setField_SSS_comments(String field_SSS_comments) {
		this.field_SSS_comments = field_SSS_comments;
	}

	// Additional Information
	public String getField_additional_info() {
		return field_additional_info;
	}

	public void setField_additional_info(String field_additional_info) {
		this.field_additional_info = field_additional_info;
	}

	// Data Set Information
	public String getField_data_set_references() {
		return field_data_set_references;
	}

	public void setField_data_set_references(String field_data_set_references) {
		this.field_data_set_references = field_data_set_references;
	}

	public String getField_citation() {
		return field_citation;
	}

	public void setField_citation(String field_citation) {
		this.field_citation = field_citation;
	}

	// Data Set Link
	public String getField_dataset_url() {
		return field_dataset_url;
	}

	public void setField_dataset_url(String field_dataset_url) {
		this.field_dataset_url = field_dataset_url;
	}

	public String getField_dataset_label() {
		return field_dataset_label;
	}

	public void setField_dataset_label(String field_dataset_label) {
		this.field_dataset_label = field_dataset_label;
	}

	public String getField_dataset_link_note() {
		return field_dataset_link_note;
	}

	public void setField_dataset_link_note(String field_dataset_link_note) {
		this.field_dataset_link_note = field_dataset_link_note;
	}

	public String getField_userfile0() {
		return field_userfile0;
	}

	public void setField_userfile0(String field_userfile0) {
		this.field_userfile0 = field_userfile0;
	}

	public String getField_userfile1() {
		return field_userfile1;
	}

	public void setField_userfile1(String field_userfile1) {
		this.field_userfile1 = field_userfile1;
	}

	public String getField_userfile2() {
		return field_userfile2;
	}

	public void setField_userfile2(String field_userfile2) {
		this.field_userfile2 = field_userfile2;
	}

	public String getField_userfile3() {
		return field_userfile3;
	}

	public void setField_userfile3(String field_userfile3) {
		this.field_userfile3 = field_userfile3;
	}

	public String getField_additional_measurements() {
		return field_additional_measurements;
	}

	public void setField_additional_measurements(
			String field_additional_measurements) {
		this.field_additional_measurements = field_additional_measurements;
	}

	public String getField_co2_data_type9() {
		return field_co2_data_type9;
	}

	public void setField_co2_data_type9(String field_co2_data_type9) {
		this.field_co2_data_type9 = field_co2_data_type9;
	}

	public String getField_co2_data_unit9() {
		return field_co2_data_unit9;
	}

	public void setField_co2_data_unit9(String field_co2_data_unit9) {
		this.field_co2_data_unit9 = field_co2_data_unit9;
	}

	public String getField_co2_data_type10() {
		return field_co2_data_type10;
	}

	public void setField_co2_data_type10(String field_co2_data_type10) {
		this.field_co2_data_type10 = field_co2_data_type10;
	}

	public String getField_co2_data_unit10() {
		return field_co2_data_unit10;
	}

	public void setField_co2_data_unit10(String field_co2_data_unit10) {
		this.field_co2_data_unit10 = field_co2_data_unit10;
	}

	public String getField_co2_data_type11() {
		return field_co2_data_type11;
	}

	public void setField_co2_data_type11(String field_co2_data_type11) {
		this.field_co2_data_type11 = field_co2_data_type11;
	}

	public String getField_co2_data_unit11() {
		return field_co2_data_unit11;
	}

	public void setField_co2_data_unit11(String field_co2_data_unit11) {
		this.field_co2_data_unit11 = field_co2_data_unit11;
	}

	public String getField_manufaturer_other3() {
		return field_manufaturer_other3;
	}

	public void setField_manufaturer_other3(String field_manufaturer_other3) {
		this.field_manufaturer_other3 = field_manufaturer_other3;
	}

	public String getField_accuracy_other3() {
		return field_accuracy_other3;
	}

	public void setField_accuracy_other3(String field_accuracy_other3) {
		this.field_accuracy_other3 = field_accuracy_other3;
	}

	public String getField_model_other3() {
		return field_model_other3;
	}

	public void setField_model_other3(String field_model_other3) {
		this.field_model_other3 = field_model_other3;
	}

	public String getField_resolution_other3() {
		return field_resolution_other3;
	}

	public void setField_resolution_other3(String field_resolution_other3) {
		this.field_resolution_other3 = field_resolution_other3;
	}

	public String getField_calibration_other3() {
		return field_calibration_other3;
	}

	public void setField_calibration_other3(String field_calibration_other3) {
		this.field_calibration_other3 = field_calibration_other3;
	}

	public String getField_comment_other3() {
		return field_comment_other3;
	}

	public void setField_comment_other3(String field_comment_other3) {
		this.field_comment_other3 = field_comment_other3;
	}

	public String getField_method_references3() {
		return field_method_references3;
	}

	public void setField_method_references3(String field_method_references3) {
		this.field_method_references3 = field_method_references3;
	}

	public String getField_manufaturer_other4() {
		return field_manufaturer_other4;
	}

	public void setField_manufaturer_other4(String field_manufaturer_other4) {
		this.field_manufaturer_other4 = field_manufaturer_other4;
	}

	public String getField_accuracy_other4() {
		return field_accuracy_other4;
	}

	public void setField_accuracy_other4(String field_accuracy_other4) {
		this.field_accuracy_other4 = field_accuracy_other4;
	}

	public String getField_model_other4() {
		return field_model_other4;
	}

	public void setField_model_other4(String field_model_other4) {
		this.field_model_other4 = field_model_other4;
	}

	public String getField_resolution_other4() {
		return field_resolution_other4;
	}

	public void setField_resolution_other4(String field_resolution_other4) {
		this.field_resolution_other4 = field_resolution_other4;
	}

	public String getField_calibration_other4() {
		return field_calibration_other4;
	}

	public void setField_calibration_other4(String field_calibration_other4) {
		this.field_calibration_other4 = field_calibration_other4;
	}

	public String getField_comment_other4() {
		return field_comment_other4;
	}

	public void setField_comment_other4(String field_comment_other4) {
		this.field_comment_other4 = field_comment_other4;
	}

	public String getField_method_references4() {
		return field_method_references4;
	}

	public void setField_method_references4(String field_method_references4) {
		this.field_method_references4 = field_method_references4;
	}

	public String getField_manufaturer_other5() {
		return field_manufaturer_other5;
	}

	public void setField_manufaturer_other5(String field_manufaturer_other5) {
		this.field_manufaturer_other5 = field_manufaturer_other5;
	}

	public String getField_accuracy_other5() {
		return field_accuracy_other5;
	}

	public void setField_accuracy_other5(String field_accuracy_other5) {
		this.field_accuracy_other5 = field_accuracy_other5;
	}

	public String getField_model_other5() {
		return field_model_other5;
	}

	public void setField_model_other5(String field_model_other5) {
		this.field_model_other5 = field_model_other5;
	}

	public String getField_resolution_other5() {
		return field_resolution_other5;
	}

	public void setField_resolution_other5(String field_resolution_other5) {
		this.field_resolution_other5 = field_resolution_other5;
	}

	public String getField_calibration_other5() {
		return field_calibration_other5;
	}

	public void setField_calibration_other5(String field_calibration_other5) {
		this.field_calibration_other5 = field_calibration_other5;
	}

	public String getField_comment_other5() {
		return field_comment_other5;
	}

	public void setField_comment_other5(String field_comment_other5) {
		this.field_comment_other5 = field_comment_other5;
	}

	public String getField_method_references5() {
		return field_method_references5;
	}

	public void setField_method_references5(String field_method_references5) {
		this.field_method_references5 = field_method_references5;
	}

	public String getField_manufaturer_other6() {
		return field_manufaturer_other6;
	}

	public void setField_manufaturer_other6(String field_manufaturer_other6) {
		this.field_manufaturer_other6 = field_manufaturer_other6;
	}

	public String getField_accuracy_other6() {
		return field_accuracy_other6;
	}

	public void setField_accuracy_other6(String field_accuracy_other6) {
		this.field_accuracy_other6 = field_accuracy_other6;
	}

	public String getField_model_other6() {
		return field_model_other6;
	}

	public void setField_model_other6(String field_model_other6) {
		this.field_model_other6 = field_model_other6;
	}

	public String getField_resolution_other6() {
		return field_resolution_other6;
	}

	public void setField_resolution_other6(String field_resolution_other6) {
		this.field_resolution_other6 = field_resolution_other6;
	}

	public String getField_calibration_other6() {
		return field_calibration_other6;
	}

	public void setField_calibration_other6(String field_calibration_other6) {
		this.field_calibration_other6 = field_calibration_other6;
	}

	public String getField_comment_other6() {
		return field_comment_other6;
	}

	public void setField_comment_other6(String field_comment_other6) {
		this.field_comment_other6 = field_comment_other6;
	}

	public String getField_method_references6() {
		return field_method_references6;
	}

	public void setField_method_references6(String field_method_references6) {
		this.field_method_references6 = field_method_references6;
	}

	public String getField_manufaturer_other7() {
		return field_manufaturer_other7;
	}

	public void setField_manufaturer_other7(String field_manufaturer_other7) {
		this.field_manufaturer_other7 = field_manufaturer_other7;
	}

	public String getField_accuracy_other7() {
		return field_accuracy_other7;
	}

	public void setField_accuracy_other7(String field_accuracy_other7) {
		this.field_accuracy_other7 = field_accuracy_other7;
	}

	public String getField_model_other7() {
		return field_model_other7;
	}

	public void setField_model_other7(String field_model_other7) {
		this.field_model_other7 = field_model_other7;
	}

	public String getField_resolution_other7() {
		return field_resolution_other7;
	}

	public void setField_resolution_other7(String field_resolution_other7) {
		this.field_resolution_other7 = field_resolution_other7;
	}

	public String getField_calibration_other7() {
		return field_calibration_other7;
	}

	public void setField_calibration_other7(String field_calibration_other7) {
		this.field_calibration_other7 = field_calibration_other7;
	}

	public String getField_comment_other7() {
		return field_comment_other7;
	}

	public void setField_comment_other7(String field_comment_other7) {
		this.field_comment_other7 = field_comment_other7;
	}

	public String getField_method_references7() {
		return field_method_references7;
	}

	public void setField_method_references7(String field_method_references7) {
		this.field_method_references7 = field_method_references7;
	}

	public String getField_manufaturer_other8() {
		return field_manufaturer_other8;
	}

	public void setField_manufaturer_other8(String field_manufaturer_other8) {
		this.field_manufaturer_other8 = field_manufaturer_other8;
	}

	public String getField_accuracy_other8() {
		return field_accuracy_other8;
	}

	public void setField_accuracy_other8(String field_accuracy_other8) {
		this.field_accuracy_other8 = field_accuracy_other8;
	}

	public String getField_model_other8() {
		return field_model_other8;
	}

	public void setField_model_other8(String field_model_other8) {
		this.field_model_other8 = field_model_other8;
	}

	public String getField_resolution_other8() {
		return field_resolution_other8;
	}

	public void setField_resolution_other8(String field_resolution_other8) {
		this.field_resolution_other8 = field_resolution_other8;
	}

	public String getField_calibration_other8() {
		return field_calibration_other8;
	}

	public void setField_calibration_other8(String field_calibration_other8) {
		this.field_calibration_other8 = field_calibration_other8;
	}

	public String getField_comment_other8() {
		return field_comment_other8;
	}

	public void setField_comment_other8(String field_comment_other8) {
		this.field_comment_other8 = field_comment_other8;
	}

	public String getField_method_references8() {
		return field_method_references8;
	}

	public void setField_method_references8(String field_method_references8) {
		this.field_method_references8 = field_method_references8;
	}

	public String getField_manufaturer_other9() {
		return field_manufaturer_other9;
	}

	public void setField_manufaturer_other9(String field_manufaturer_other9) {
		this.field_manufaturer_other9 = field_manufaturer_other9;
	}

	public String getField_accuracy_other9() {
		return field_accuracy_other9;
	}

	public void setField_accuracy_other9(String field_accuracy_other9) {
		this.field_accuracy_other9 = field_accuracy_other9;
	}

	public String getField_model_other9() {
		return field_model_other9;
	}

	public void setField_model_other9(String field_model_other9) {
		this.field_model_other9 = field_model_other9;
	}

	public String getField_resolution_other9() {
		return field_resolution_other9;
	}

	public void setField_resolution_other9(String field_resolution_other9) {
		this.field_resolution_other9 = field_resolution_other9;
	}

	public String getField_calibration_other9() {
		return field_calibration_other9;
	}

	public void setField_calibration_other9(String field_calibration_other9) {
		this.field_calibration_other9 = field_calibration_other9;
	}

	public String getField_comment_other9() {
		return field_comment_other9;
	}

	public void setField_comment_other9(String field_comment_other9) {
		this.field_comment_other9 = field_comment_other9;
	}

	public String getField_method_references9() {
		return field_method_references9;
	}

	public void setField_method_references9(String field_method_references9) {
		this.field_method_references9 = field_method_references9;
	}

	public String getField_start_date_dup() {
		return field_start_date_dup;
	}

	public void setField_start_date_dup(String field_start_date_dup) {
		this.field_start_date_dup = field_start_date_dup;
	}

	public String getField_end_date_dup() {
		return field_end_date_dup;
	}

	public void setField_end_date_dup(String field_end_date_dup) {
		this.field_end_date_dup = field_end_date_dup;
	}

	public String getField_sensor_desciption() {
		return field_sensor_desciption;
	}

	public void setField_sensor_desciption(String field_sensor_desciption) {
		this.field_sensor_desciption = field_sensor_desciption;
	}

	public String getField_sensor_desciption1() {
		return field_sensor_desciption1;
	}

	public void setField_sensor_desciption1(String field_sensor_desciption1) {
		this.field_sensor_desciption1 = field_sensor_desciption1;
	}

	public String getField_sensor_desciption2() {
		return field_sensor_desciption2;
	}

	public void setField_sensor_desciption2(String field_sensor_desciption2) {
		this.field_sensor_desciption2 = field_sensor_desciption2;
	}

	public String getField_sensor_desciption3() {
		return field_sensor_desciption3;
	}

	public void setField_sensor_desciption3(String field_sensor_desciption3) {
		this.field_sensor_desciption3 = field_sensor_desciption3;
	}

	public String getField_sensor_desciption4() {
		return field_sensor_desciption4;
	}

	public void setField_sensor_desciption4(String field_sensor_desciption4) {
		this.field_sensor_desciption4 = field_sensor_desciption4;
	}

	public String getField_sensor_desciption5() {
		return field_sensor_desciption5;
	}

	public void setField_sensor_desciption5(String field_sensor_desciption5) {
		this.field_sensor_desciption5 = field_sensor_desciption5;
	}

	public String getField_sensor_desciption6() {
		return field_sensor_desciption6;
	}

	public void setField_sensor_desciption6(String field_sensor_desciption6) {
		this.field_sensor_desciption6 = field_sensor_desciption6;
	}

	public String getField_sensor_desciption7() {
		return field_sensor_desciption7;
	}

	public void setField_sensor_desciption7(String field_sensor_desciption7) {
		this.field_sensor_desciption7 = field_sensor_desciption7;
	}

	public String getField_sensor_desciption8() {
		return field_sensor_desciption8;
	}

	public void setField_sensor_desciption8(String field_sensor_desciption8) {
		this.field_sensor_desciption8 = field_sensor_desciption8;
	}

	public String getField_sensor_desciption9() {
		return field_sensor_desciption9;
	}

	public void setField_sensor_desciption9(String field_sensor_desciption9) {
		this.field_sensor_desciption9 = field_sensor_desciption9;
	}
	
	
	
}
