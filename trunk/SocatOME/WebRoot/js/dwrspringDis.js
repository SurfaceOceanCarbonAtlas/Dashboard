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
	} else {
		dwr.util.setValue("field_variable_description0", "");
	}
}

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
	} else {
		dwr.util.setValue("field_variable_description1", "");
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
	} else {
		dwr.util.setValue("field_variable_description2", "");
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
	} else {
		dwr.util.setValue("field_variable_description3", "");
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
	} else {
		dwr.util.setValue("field_variable_description4", "");
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
	} else {
		dwr.util.setValue("field_variable_description5", "");
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
	} else {
		dwr.util.setValue("field_variable_description6", "");
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
	} else {
		dwr.util.setValue("field_variable_description7", "");
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
	} else {
		dwr.util.setValue("field_variable_description8", "");
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
	} else {
		dwr.util.setValue("field_variable_description9", "");
	}
}

function checkVariables10()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable10"),
			updateVariableGroup10);
}

function updateVariableGroup10(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description10", comString[0]);
	} else {
		dwr.util.setValue("field_variable_description10", "");
	}
}

function checkVariables11()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable11"),
			updateVariableGroup11);
}

function updateVariableGroup11(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description11", comString[0]);
	} else {
		dwr.util.setValue("field_variable_description11", "");
	}
}

function checkVariables12()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable12"),
			updateVariableGroup12);
}

function updateVariableGroup12(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description12", comString[0]);
	} else {
		dwr.util.setValue("field_variable_description12", "");
	}
}

function checkVariables13()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable13"),
			updateVariableGroup13);
}

function updateVariableGroup13(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description13", comString[0]);
	} else {
		dwr.util.setValue("field_variable_description13", "");
	}
}


function checkVariables14()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable14"),
			updateVariableGroup14);
}

function updateVariableGroup14(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description14", comString[0]);
	} else {
		dwr.util.setValue("field_variable_description14", "");
	}
}

function checkVariables15()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable15"),
			updateVariableGroup15);
}

function updateVariableGroup15(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description15", comString[0]);
		dwr.util.setValue("field_variable_sensor_model15", comString[1]);
		dwr.util.setValue("field_variable_calibration15", comString[2]);
		dwr.util.setValue("field_variable_accuracy15", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description15", "");
		dwr.util.setValue("field_variable_sensor_model15","");
		dwr.util.setValue("field_variable_calibration15", "");
		dwr.util.setValue("field_variable_accuracy15", "");
	}
}
function checkVariables16()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable16"),
			updateVariableGroup16);
}

function updateVariableGroup16(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description16", comString[0]);
		dwr.util.setValue("field_variable_sensor_model16", comString[1]);
		dwr.util.setValue("field_variable_calibration16", comString[2]);
		dwr.util.setValue("field_variable_accuracy16", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description16", "");
		dwr.util.setValue("field_variable_sensor_model16","");
		dwr.util.setValue("field_variable_calibration16", "");
		dwr.util.setValue("field_variable_accuracy16", "");
	}
}

function checkVariables17()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable17"),
			updateVariableGroup17);
}

function updateVariableGroup17(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description17", comString[0]);
		dwr.util.setValue("field_variable_sensor_model17", comString[1]);
		dwr.util.setValue("field_variable_calibration17", comString[2]);
		dwr.util.setValue("field_variable_accuracy17", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description17", "");
		dwr.util.setValue("field_variable_sensor_model17","");
		dwr.util.setValue("field_variable_calibration17", "");
		dwr.util.setValue("field_variable_accuracy17", "");
	}
}

function checkVariables18()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable18"),
			updateVariableGroup18);
}

function updateVariableGroup18(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description18", comString[0]);
		dwr.util.setValue("field_variable_sensor_model18", comString[1]);
		dwr.util.setValue("field_variable_calibration18", comString[2]);
		dwr.util.setValue("field_variable_accuracy18", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description18", "");
		dwr.util.setValue("field_variable_sensor_model18","");
		dwr.util.setValue("field_variable_calibration18", "");
		dwr.util.setValue("field_variable_accuracy18", "");
	}
}

function checkVariables19()
{
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable19"),
			updateVariableGroup19);
}

function updateVariableGroup19(data)
{
	comString = data.split("||");
	if (comString[0] != "" && comString[0] != null) {
		if(comString[1]==null)
			comString[1] = "";
		if(comString[2]==null)
			comString[3] = "";
		if(comString[3]==null)
			comString[3] = "";
		
		dwr.util.setValue("field_variable_description19", comString[0]);
		dwr.util.setValue("field_variable_sensor_model19", comString[1]);
		dwr.util.setValue("field_variable_calibration19", comString[2]);
		dwr.util.setValue("field_variable_accuracy19", comString[3]);
	} else {
		dwr.util.setValue("field_variable_description19", "");
		dwr.util.setValue("field_variable_sensor_model19","");
		dwr.util.setValue("field_variable_calibration19", "");
		dwr.util.setValue("field_variable_accuracy19", "");
	}
}
