//author names
function AuthNames(autocompleter, token) {
	AutoCompleteService.getMatchedWord(token, function(data) {
		autocompleter.setChoices(data);
	});
}


function CheckOrg1() {
	AutoCompleteService.findauthDetails(dwr.util.getValue("field_ownername"),
			updateValues1);
}
function updateValues1(data) {
	comString = data.split("||");
	if (comString[0] != "") {
		dwr.util.setValue("field_organizationame", comString[0]);
		dwr.util.setValue("field_owneraddress", comString[1]);
		dwr.util.setValue("field_telephonenumber", comString[2]);
		dwr.util.setValue("field_email", comString[3]);
	} else {
		dwr.util.setValue("field_organizationame", "");
		dwr.util.setValue("field_owneraddress", "");
		dwr.util.setValue("field_telephonenumber","");
		dwr.util.setValue("field_email", "");
	}

}
function CheckOrg2() {
	AutoCompleteService.findauthDetails(dwr.util.getValue("field_ownername2"),
			updateValues2);
}
function updateValues2(data) {
	comString = data.split("||");
	if (comString[0] != "") {
		dwr.util.setValue("field_organizationame2", comString[0]);
		dwr.util.setValue("field_owneraddress2", comString[1]);
		dwr.util.setValue("field_telephonenumber2", comString[2]);
		dwr.util.setValue("field_email2", comString[3]);
		
	} else {
		dwr.util.setValue("field_organizationame2", "");
		dwr.util.setValue("field_owneraddress2", "");
		dwr.util.setValue("field_telephonenumber2","");
		dwr.util.setValue("field_email2", "");
	}

}
function CheckOrg3() {
	AutoCompleteService.findauthDetails(dwr.util.getValue("field_ownername3"),
			updateValues3);
}
function updateValues3(data) {
	comString = data.split("||");
	if (comString[0] != "") {
		dwr.util.setValue("field_organizationame3", comString[0]);
		dwr.util.setValue("field_owneraddress3", comString[1]);
		dwr.util.setValue("field_telephonenumber3", comString[2]);
		dwr.util.setValue("field_email3", comString[3]);
	} else {
		dwr.util.setValue("field_organizationame3", "");
		dwr.util.setValue("field_owneraddress3", "");
		dwr.util.setValue("field_telephonenumber3","");
		dwr.util.setValue("field_email3", "");		
	}

}
function CheckOrg4() {
	AutoCompleteService.findauthDetails(dwr.util.getValue("field_ownername4"),
			updateValues4);
}
function updateValues4(data) {
	comString = data.split("||");
	if (comString[0] != "") {
		dwr.util.setValue("field_organizationame4", comString[0]);
		dwr.util.setValue("field_owneraddress4", comString[1]);
		dwr.util.setValue("field_telephonenumber4", comString[2]);
		dwr.util.setValue("field_email4", comString[3]);
	} else {
		dwr.util.setValue("field_organizationame4", "");
		dwr.util.setValue("field_owneraddress4", "");
		dwr.util.setValue("field_telephonenumber4","");
		dwr.util.setValue("field_email4", "");		
	}

}
function CheckOrg5() {
	AutoCompleteService.findauthDetails(dwr.util.getValue("field_ownername5"),
			updateValues5);
}
function updateValues5(data) {
	comString = data.split("||");
	if (comString[0] != "") {
		dwr.util.setValue("field_organizationame5", comString[0]);
		dwr.util.setValue("field_owneraddress5", comString[1]);
		dwr.util.setValue("field_telephonenumber5", comString[2]);
		dwr.util.setValue("field_email5", comString[3]);
	} else {
		dwr.util.setValue("field_organizationame5", "");
		dwr.util.setValue("field_owneraddress5", "");
		dwr.util.setValue("field_telephonenumber5","");
		dwr.util.setValue("field_email5", "");		
	}

}



function nameValueSelector(tag){  
     return tag;
}


function surveyNames(autocompleter, token){		
	AutoCompleteService.getSurveyNames(token, function(data) { autocompleter.setChoices(data);});
}

function vesselNames(autocompleter, token){		
	AutoCompleteService.getVesselNames(token, function(data) { autocompleter.setChoices(data);});
}

function checkVessel()
{
	AutoCompleteService.findVesselDetails(dwr.util.getValue("field_vessel_name"),
			updateVesselGroup);
}

function updateVesselGroup(data)
{
	comString = data.split("||");
	if (comString[0] != "") {
		dwr.util.setValue("field_vessel_id", comString[0]);
		dwr.util.setValue("field_vessel_country", comString[1]);
		dwr.util.setValue("field_vessel_owner", comString[2]);
	} else {
		dwr.util.setValue("field_vessel_id", "");
		dwr.util.setValue("field_vessel_country", "");
		dwr.util.setValue("field_vessel_owner","");
	}
}

function variableNames(autocompleter, token){		
	AutoCompleteService.getVariableNames(token, function(data) { autocompleter.setChoices(data);});
}

function checkVariables()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable0"),
			updateVariableGroup);
}
/*
function updateVariableGroup(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description0", comString[0]);
		dwr.util.setValue("field_variable_sensor_model0", comString[1]);
		dwr.util.setValue("field_variable_calibration0", comString[2]);
		dwr.util.setValue("field_variable_accuracy0", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description0", "");
		dwr.util.setValue("field_variable_sensor_model0","");
		dwr.util.setValue("field_variable_calibration0", "");
		dwr.util.setValue("field_variable_accuracy0", "");
	}
}*/

function checkVariables1()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable1"),
			updateVariableGroup1);
}

function updateVariableGroup1(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description1", comString[0]);
		dwr.util.setValue("field_variable_sensor_model1", comString[1]);
		dwr.util.setValue("field_variable_calibration1", comString[2]);
		dwr.util.setValue("field_variable_accuracy1", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description1", "");
		dwr.util.setValue("field_variable_sensor_model1","");
		dwr.util.setValue("field_variable_calibration1", "");
		dwr.util.setValue("field_variable_accuracy1", "");
	}
}

function checkVariables2()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable2"),
			updateVariableGroup2);
}

function updateVariableGroup2(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description2", comString[0]);
		dwr.util.setValue("field_variable_sensor_model2", comString[1]);
		dwr.util.setValue("field_variable_calibration2", comString[2]);
		dwr.util.setValue("field_variable_accuracy2", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description2", "");
		dwr.util.setValue("field_variable_sensor_model2","");
		dwr.util.setValue("field_variable_calibration2", "");
		dwr.util.setValue("field_variable_accuracy2", "");
	}
}

function checkVariables3()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable3"),
			updateVariableGroup3);
}

function updateVariableGroup3(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description3", comString[0]);
		dwr.util.setValue("field_variable_sensor_model3", comString[1]);
		dwr.util.setValue("field_variable_calibration3", comString[2]);
		dwr.util.setValue("field_variable_accuracy3", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description3", "");
		dwr.util.setValue("field_variable_sensor_model3","");
		dwr.util.setValue("field_variable_calibration3", "");
		dwr.util.setValue("field_variable_accuracy3", "");
	}
}

function checkVariables4()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable4"),
			updateVariableGroup4);
}

function updateVariableGroup4(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description4", comString[0]);
		dwr.util.setValue("field_variable_sensor_model4", comString[1]);
		dwr.util.setValue("field_variable_calibration4", comString[2]);
		dwr.util.setValue("field_variable_accuracy4", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description4", "");
		dwr.util.setValue("field_variable_sensor_model4","");
		dwr.util.setValue("field_variable_calibration4", "");
		dwr.util.setValue("field_variable_accuracy4", "");
	}
}

function checkVariables5()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable5"),
			updateVariableGroup5);
}

function updateVariableGroup5(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description5", comString[0]);
		dwr.util.setValue("field_variable_sensor_model5", comString[1]);
		dwr.util.setValue("field_variable_calibration5", comString[2]);
		dwr.util.setValue("field_variable_accuracy5", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description5", "");
		dwr.util.setValue("field_variable_sensor_model5","");
		dwr.util.setValue("field_variable_calibration5", "");
		dwr.util.setValue("field_variable_accuracy5", "");
	}
}

function checkVariables6()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable6"),
			updateVariableGroup6);
}

function updateVariableGroup6(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description6", comString[0]);
		dwr.util.setValue("field_variable_sensor_model6", comString[1]);
		dwr.util.setValue("field_variable_calibration6", comString[2]);
		dwr.util.setValue("field_variable_accuracy6", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description6", "");
		dwr.util.setValue("field_variable_sensor_model6","");
		dwr.util.setValue("field_variable_calibration6", "");
		dwr.util.setValue("field_variable_accuracy6", "");
	}
}

function checkVariables7()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable7"),
			updateVariableGroup7);
}

function updateVariableGroup7(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description7", comString[0]);
		dwr.util.setValue("field_variable_sensor_model7", comString[1]);
		dwr.util.setValue("field_variable_calibration7", comString[2]);
		dwr.util.setValue("field_variable_accuracy7", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description7", "");
		dwr.util.setValue("field_variable_sensor_model7","");
		dwr.util.setValue("field_variable_calibration7", "");
		dwr.util.setValue("field_variable_accuracy7", "");
	}
}

function checkVariables8()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable8"),
			updateVariableGroup8);
}

function updateVariableGroup8(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description8", comString[0]);
		dwr.util.setValue("field_variable_sensor_model8", comString[1]);
		dwr.util.setValue("field_variable_calibration8", comString[2]);
		dwr.util.setValue("field_variable_accuracy8", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description8", "");
		dwr.util.setValue("field_variable_sensor_model8","");
		dwr.util.setValue("field_variable_calibration8", "");
		dwr.util.setValue("field_variable_accuracy8", "");
	}
}

function checkVariables9()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable9"),
			updateVariableGroup9);
}

function updateVariableGroup9(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description9", comString[0]);
		dwr.util.setValue("field_variable_sensor_model9", comString[1]);
		dwr.util.setValue("field_variable_calibration9", comString[2]);
		dwr.util.setValue("field_variable_accuracy9", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description9", "");
		dwr.util.setValue("field_variable_sensor_model9","");
		dwr.util.setValue("field_variable_calibration9", "");
		dwr.util.setValue("field_variable_accuracy9", "");
	}
}

function checkVariables20()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable20"),
			updateVariableGroup20);
}

function updateVariableGroup20(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description20", comString[0]);
		dwr.util.setValue("field_variable_sensor_model20", comString[1]);
		dwr.util.setValue("field_variable_calibration20", comString[2]);
		dwr.util.setValue("field_variable_accuracy20", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description20", "");
		dwr.util.setValue("field_variable_sensor_model20","");
		dwr.util.setValue("field_variable_calibration20", "");
		dwr.util.setValue("field_variable_accuracy10", "");
	}
}

function checkVariables21()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable21"),
			updateVariableGroup21);
}

function updateVariableGroup21(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description21", comString[0]);
		dwr.util.setValue("field_variable_sensor_model21", comString[1]);
		dwr.util.setValue("field_variable_calibration21", comString[2]);
		dwr.util.setValue("field_variable_accuracy21", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description21", "");
		dwr.util.setValue("field_variable_sensor_model21","");
		dwr.util.setValue("field_variable_calibration21", "");
		dwr.util.setValue("field_variable_accuracy21", "");
	}
}

function checkVariables22()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable22"),
			updateVariableGroup22);
}

function updateVariableGroup22(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description22", comString[0]);
		dwr.util.setValue("field_variable_sensor_model22", comString[1]);
		dwr.util.setValue("field_variable_calibration22", comString[2]);
		dwr.util.setValue("field_variable_accuracy22", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description22", "");
		dwr.util.setValue("field_variable_sensor_model22","");
		dwr.util.setValue("field_variable_calibration22", "");
		dwr.util.setValue("field_variable_accuracy22", "");
	}
}

function checkVariables23()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable23"),
			updateVariableGroup23);
}

function updateVariableGroup23(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description23", comString[0]);
		dwr.util.setValue("field_variable_sensor_model23", comString[1]);
		dwr.util.setValue("field_variable_calibration23", comString[2]);
		dwr.util.setValue("field_variable_accuracy23", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description23", "");
		dwr.util.setValue("field_variable_sensor_model23","");
		dwr.util.setValue("field_variable_calibration23", "");
		dwr.util.setValue("field_variable_accuracy23", "");
	}
}


function checkVariables24()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable24"),
			updateVariableGroup24);
}

function updateVariableGroup24(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description24", comString[0]);
		dwr.util.setValue("field_variable_sensor_model24", comString[1]);
		dwr.util.setValue("field_variable_calibration24", comString[2]);
		dwr.util.setValue("field_variable_accuracy24", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description24", "");
		dwr.util.setValue("field_variable_sensor_model24","");
		dwr.util.setValue("field_variable_calibration24", "");
		dwr.util.setValue("field_variable_accuracy24", "");
	}
}
function checkVariables25()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable25"),
			updateVariableGroup25);
}

function updateVariableGroup25(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description25", comString[0]);
		dwr.util.setValue("field_variable_sensor_model25", comString[1]);
		dwr.util.setValue("field_variable_calibration25", comString[2]);
		dwr.util.setValue("field_variable_accuracy25", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description25", "");
		dwr.util.setValue("field_variable_sensor_model25","");
		dwr.util.setValue("field_variable_calibration25", "");
		dwr.util.setValue("field_variable_accuracy25", "");
	}
}
function checkVariables26()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable26"),
			updateVariableGroup26);
}

function updateVariableGroup26(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description26", comString[0]);
		dwr.util.setValue("field_variable_sensor_model26", comString[1]);
		dwr.util.setValue("field_variable_calibration26", comString[2]);
		dwr.util.setValue("field_variable_accuracy26", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description26", "");
		dwr.util.setValue("field_variable_sensor_model26","");
		dwr.util.setValue("field_variable_calibration26", "");
		dwr.util.setValue("field_variable_accuracy26", "");
	}
}

function checkVariables27()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable27"),
			updateVariableGroup27);
}

function updateVariableGroup27(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description27", comString[0]);
		dwr.util.setValue("field_variable_sensor_model27", comString[1]);
		dwr.util.setValue("field_variable_calibration27", comString[2]);
		dwr.util.setValue("field_variable_accuracy27", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description27", "");
		dwr.util.setValue("field_variable_sensor_model27","");
		dwr.util.setValue("field_variable_calibration27", "");
		dwr.util.setValue("field_variable_accuracy27", "");
	}
}

function checkVariables28()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable28"),
			updateVariableGroup28);
}

function updateVariableGroup28(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description28", comString[0]);
		dwr.util.setValue("field_variable_sensor_model28", comString[1]);
		dwr.util.setValue("field_variable_calibration28", comString[2]);
		dwr.util.setValue("field_variable_accuracy28", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description28", "");
		dwr.util.setValue("field_variable_sensor_model28","");
		dwr.util.setValue("field_variable_calibration28", "");
		dwr.util.setValue("field_variable_accuracy28", "");
	}
}

function checkVariables29()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable29"),
			updateVariableGroup29);
}

function updateVariableGroup29(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description29", comString[0]);
		dwr.util.setValue("field_variable_sensor_model29", comString[1]);
		dwr.util.setValue("field_variable_calibration29", comString[2]);
		dwr.util.setValue("field_variable_accuracy29", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description29", "");
		dwr.util.setValue("field_variable_sensor_model29","");
		dwr.util.setValue("field_variable_calibration29", "");
		dwr.util.setValue("field_variable_accuracy29", "");
	}
}

