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
		dwr.util.setValue("field_telephonenumber", "");
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
		dwr.util.setValue("field_telephonenumber2", "");
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
		dwr.util.setValue("field_telephonenumber3", "");
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
		dwr.util.setValue("field_telephonenumber4", "");
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
		dwr.util.setValue("field_telephonenumber5", "");
		dwr.util.setValue("field_email5", "");
	}

}

function nameValueSelector(tag) {
	return tag;
}

function surveyNames(autocompleter, token) {
	AutoCompleteService.getSurveyNames(token, function(data) {
		autocompleter.setChoices(data);
	});
}

function vesselNames(autocompleter, token) {
	AutoCompleteService.getVesselNames(token, function(data) {
		autocompleter.setChoices(data);
	});
}

function checkVessel() {
	AutoCompleteService.findVesselDetails(dwr.util
			.getValue("field_vessel_name"), updateVesselGroup);
}

function updateVesselGroup(data) {
	comString = data.split("||");
	if (comString[0] != "") {
		dwr.util.setValue("field_vessel_id", comString[0]);
		dwr.util.setValue("field_vessel_country", comString[1]);
		dwr.util.setValue("field_vessel_owner", comString[2]);
	} else {
		dwr.util.setValue("field_vessel_id", "");
		dwr.util.setValue("field_vessel_country", "");
		dwr.util.setValue("field_vessel_owner", "");
	}
}

function variableNames(autocompleter, token) {
	AutoCompleteService.getVariableNames(token, function(data) {
		autocompleter.setChoices(data);
	});
}

function checkVariables(index) {
	AutoCompleteService.findVaraibleDetails(dwr.util.getValue("field_variable"
			+ index), updateVariableGroup);
	function updateVariableGroup(data) {
		
		if (data != "") {
			dwr.util.setValue("field_variable_unit" + (index), data);
		} else {
			dwr.util.setValue("field_variable_unit" + (index), "");
		}
	}
}
