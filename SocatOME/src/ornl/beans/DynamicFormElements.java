package ornl.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * Biva Shrestha
 *  * 
 */
public class DynamicFormElements {
	private final Map<String, String> variablesMap = createVaraiblesMap();
	private final Map<String, String> investigatorsMap = createInvestigatorsMap();
	private final Map<String, String> userfileMap = createUserFileMap();
	private final String[] investigators2 = { "field_ownername2",
			"field_organizationame2", "field_owneraddress2",
			"field_telephonenumber2", "field_email2" };
	private final String[] investigators3 = { "field_ownername3",
			"field_organizationame3", "field_owneraddress3",
			"field_telephonenumber3", "field_email3" };
	private final String[] variables = { "field_variable1","field_variable_description1",
			"field_variable_sensor_model1","field_variable_calibration1","field_variable_accuracy1",
			 "field_variable2",	"field_variable_description2",		
			"field_variable_sensor_model2","field_variable_calibration2","field_variable_accuracy2",
			 "field_variable3",	"field_variable_description3",
			"field_variable_sensor_model3","field_variable_calibration3","field_variable_accuracy3",
			"field_variable4","field_variable_description4",
			"field_variable_sensor_model4","field_variable_calibration4","field_variable_accuracy4",			
			 "field_variable5","field_variable_description5",
			"field_variable_sensor_model5","field_variable_calibration5","field_variable_accuracy5",			
			 "field_variable6","field_variable_description6",
			"field_variable_sensor_model6","field_variable_calibration6","field_variable_accuracy6",
			 "field_variable7","field_variable_description7",
			"field_variable_sensor_model7","field_variable_calibration7","field_variable_accuracy7",
			 "field_variable8","field_variable_description8",
			"field_variable_sensor_model8","field_variable_calibration8","field_variable_accuracy8",
			 "field_variable9","field_variable_description9",
			"field_variable_sensor_model9","field_variable_calibration9","field_variable_accuracy9",
			 "field_variable10","field_variable_description10",
			"field_variable_sensor_model10","field_variable_calibration10","field_variable_accuracy10",
			 "field_variable11","field_variable_description11",
			"field_variable_sensor_model11","field_variable_calibration11","field_variable_accuracy11",
			 "field_variable12","field_variable_description12",
			"field_variable_sensor_model2","field_variable_calibration12","field_variable_accuracy12",
			 "field_variable13","field_variable_description13",
			"field_variable_sensor_model13","field_variable_calibration13","field_variable_accuracy13",
			 "field_variable14","field_variable_description14",	
			"field_variable_sensor_model14","field_variable_calibration14","field_variable_accuracy14"};		
			
	private final String[] userfiles = {"field_userfile0","field_userfile1",
			"field_userfile2","field_userfile3"};

	private Map<String, String> createVaraiblesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("field_variable1", "Variable_Name");
		map.put("field_variable_description1", "Description_of_Variable");

		map.put("field_variable2", "Variable_Name");
		map.put("field_variable_description2", "Description_of_Variable");

		map.put("field_variable3", "Variable_Name");
		map.put("field_variable_description3", "Description_of_Variable");

		map.put("field_variable4", "Variable_Name");
		map.put("field_variable_description4", "Description_of_Variable");

		map.put("field_variable5", "Variable_Name");
		map.put("field_variable_description5", "Description_of_Variable");

		map.put("field_variable6", "Variable_Name");
		map.put("field_variable_description6", "Description_of_Variable");

		map.put("field_variable7", "Variable_Name");
		map.put("field_variable_description7", "Description_of_Variable");

		map.put("field_variable8", "Variable_Name");
		map.put("field_variable_description8", "Description_of_Variable");

		map.put("field_variable9", "Variable_Name");
		map.put("field_variable_description9", "Description_of_Variable");

		map.put("field_variable10", "Variable_Name");
		map.put("field_variable_description10", "Description_of_Variable");

		map.put("field_variable11", "Variable_Name");
		map.put("field_variable_description11", "Description_of_Variable");

		map.put("field_variable12", "Variable_Name");
		map.put("field_variable_description12", "Description_of_Variable");

		map.put("field_variable13", "Variable_Name");
		map.put("field_variable_description13", "Description_of_Variable");

		map.put("field_variable14", "Variable_Name");
		map.put("field_variable_description14", "Description_of_Variable");
		
		map.put("field_variable_sensor_model1", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration1", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy1", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model2", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration2", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy2", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model3", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration3", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy3", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model4", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration4", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy4", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model5", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration5", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy5", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model6", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration6", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy6", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model7", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration7", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy7", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model8", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration8", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy8", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model9", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration9", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy9", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model10", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration10", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy10", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model11", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration11", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy11", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model12", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration12", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy12", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model13", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration13", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy13", "Variable_Sensor_Accuracy");
		
		map.put("field_variable_sensor_model14", "Variable_Sensor_Manufacturer_Model");
		map.put("field_variable_calibration14", "Variable_Sensor_Calibration");
		map.put("field_variable_accuracy14", "Variable_Sensor_Accuracy");
		
		return Collections.unmodifiableMap(map);
	}

	private Map<String, String> createInvestigatorsMap() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("field_ownername2", "Name");
		map.put("field_organizationame2", "Organization");
		map.put("field_owneraddress2", "Address");
		map.put("field_telephonenumber2", "Phone");
		map.put("field_email2", "Email");

		map.put("field_ownername3", "Name");
		map.put("field_organizationame3", "Organization");
		map.put("field_owneraddress3", "Address");
		map.put("field_telephonenumber3", "Phone");
		map.put("field_email3", "Email");

		return Collections.unmodifiableMap(map);
	}
	private Map<String, String> createUserFileMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("field_userfile0", "URL");
		map.put("field_userfile1", "URL");
		map.put("field_userfile2", "URL");
		map.put("field_userfile3", "URL");
		return Collections.unmodifiableMap(map);
	}

	public Map<String, String> getVariablesMap() {
		return variablesMap;
	}

	public Map<String, String> getInvestigatorsMap() {
		return investigatorsMap;
	}

	public String[] getInvestigators2() {
		return investigators2;
	}

	public String[] getInvestigators3() {
		return investigators3;
	}

	public String[] getVariables() {
		return variables;
	}

	public Map<String, String> getUserfileMap() {
		return userfileMap;
	}

	public String[] getUserfiles() {
		return userfiles;
	}

}
