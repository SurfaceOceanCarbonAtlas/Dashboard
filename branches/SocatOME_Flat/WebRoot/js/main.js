jQuery(document)
		.ready(
				function() {
					var $ = jQuery;

					open();

					$("#mainform").validate({

						errorPlacement : function(error, element) {
							error.insertBefore(element);

						},
						rules : {

							'field_title' : {
								required : true
							}

						}
					});
					$.validator.addClassRules("conflicts", {
						required : true
					});

					$(".addfilter2").click(

					function() {
						// common//
						$("#field_ownername").rules("add", {
							"required" : true
						});
						$("#field_username").rules("add", {
							"required" : true
						});
						$("#field_user_organizationame").rules("add", {
							"required" : true
						});
						$("#field_user_adress").rules("add", {
							"required" : true
						});
						$("#field_user_telephonenumber").rules("add", {
							"required" : true
						});
						$("#field_user_email").rules("add", {
							"required" : true
						});
						$("#field_ownername").rules("add", {
							"required" : true
						});
						$("#field_organizationame").rules("add", {
							"required" : true
						});
						$("#field_owneraddress").rules("add", {
							"required" : true
						});
						$("#field_telephonenumber").rules("add", {
							"required" : true
						});
						$("#field_email").rules("add", {
							"required" : true
						});
						$("#field_vessel_id").rules("add", {
							"required" : true
						});
						$("#field_start_date").rules("add", {
							"required" : true
						});
						$("#field_platform_type").rules("add", {
							"required" : true
						});
						$("#field_co2_instr_type").rules("add", {
							"required" : true
						});
						$("#field_survey_type").rules("add", {
							"required" : true
						});
						$("#field_experiment_name").rules("add", {
							"required" : true
						});
						$("#field_depth_seawater_intake").rules("add", {
							"required" : true
						});
						$("#field_location_seawater_intake").rules("add", {
							"required" : true
						});
						$("#field_water_flow_rate").rules("add", {
							"required" : true
						});
						$("#field_SST_location").rules("add", {
							"required" : true
						});
						$("#field_SST_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_SST_model").rules("add", {
							"required" : true
						});
						$("#field_SST_accuracy").rules("add", {
							"required" : true
						});
						$("#field_SST_precision").rules("add", {
							"required" : true
						});
						$("#field_SST_calibration").rules("add", {
							"required" : true
						});
						$("#field_equilibrator_type").rules("add", {
							"required" : true
						});
						$("#field_drying_method").rules("add", {
							"required" : true
						});
						$("#field_measurement_method").rules("add", {
							"required" : true
						});
						$("#field_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_model").rules("add", {
							"required" : true
						});
						$("#field_measured_co2_params").rules("add", {
							"required" : true
						});
						$("#field_frequency").rules("add", {
							"required" : true
						});
						$("#field_uncertainity").rules("add", {
							"required" : true
						});
						$("#field_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_resolution").rules("add", {
							"required" : true
						});
						$("#field_uncertainity_air").rules("add", {
							"required" : true
						});
						$("#field_resolution_air").rules("add", {
							"required" : true
						});
						$("#field_sensor_calibration").rules("add", {
							"required" : true
						});
						$("#field_calibration").rules("add", {
							"required" : true
						});
						$("#field_manufacturer_calibration").rules("add", {
							"required" : true
						});
						$("#field_Tequ_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_Tequ_model").rules("add", {
							"required" : true
						});
						$("#field_Tequ_accuracy").rules("add", {
							"required" : true
						});
						$("#field_Tequ_precision").rules("add", {
							"required" : true
						});
						$("#field_Tequ_calibration").rules("add", {
							"required" : true
						});
						$("#field_Pequ_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_Pequ_model").rules("add", {
							"required" : true
						});
						$("#field_Pequ_accuracy").rules("add", {
							"required" : true
						});
						$("#field_Pequ_precision").rules("add", {
							"required" : true
						});
						$("#field_Pequ_calibration").rules("add", {
							"required" : true
						});
						// end of common//

						// for filter 2//
						$("#field_equilibration_volume").rules("remove", {
							"required" : true
						});
						$("#field_gas_flow_rate").rules("remove", {
							"required" : true
						});
						$("#field_vented").rules("remove", {
							"required" : true
						});
						$("#field_Patm_sensor").rules("remove", {
							"required" : true
						});
						$("#field_Patm_normalized").rules("remove", {
							"required" : true
						});
						$("#field_Patm_manufacturer").rules("remove", {
							"required" : true
						});
						$("#field_Patm_model").rules("remove", {
							"required" : true
						});
						$("#field_Patm_accutacy").rules("remove", {
							"required" : true
						});
						$("#field_Patm_precision").rules("remove", {
							"required" : true
						});
						$("#field_Patm_calibration").rules("remove", {
							"required" : true
						});

						// for filter 1//
						$("#field_detail_sensing").rules("add", {
							"required" : true
						});
						$("#field_method_references").rules("add", {
							"required" : true
						});

					});

					$(".addfilter1").click(function() {
						// common//
						$("#field_ownername").rules("add", {
							"required" : true
						});
						$("#field_username").rules("add", {
							"required" : true
						});
						$("#field_organizationame").rules("add", {
							"required" : true
						});
						$("#field_user_adress").rules("add", {
							"required" : true
						});
						$("#field_user_telephonenumber").rules("add", {
							"required" : true
						});
						$("#field_user_email").rules("add", {
							"required" : true
						});
						$("#field_ownername").rules("add", {
							"required" : true
						});
						$("#field_organizationname").rules("add", {
							"required" : true
						});
						$("#field_owneraddress").rules("add", {
							"required" : true
						});
						$("#field_telephonenumber").rules("add", {
							"required" : true
						});
						$("#field_email").rules("add", {
							"required" : true
						});
						$("#field_vessel_id").rules("add", {
							"required" : true
						});
						$("#field_start_date").rules("add", {
							"required" : true
						});
						$("#field_platform_type").rules("add", {
							"required" : true
						});
						$("#field_co2_instr_type").rules("add", {
							"required" : true
						});
						$("#field_survey_type").rules("add", {
							"required" : true
						});
						$("#field_experiment_name").rules("add", {
							"required" : true
						});
						$("#field_depth_seawater_intake").rules("add", {
							"required" : true
						});
						$("#field_location_seawater_intake").rules("add", {
							"required" : true
						});
						$("#field_water_flow_rate").rules("add", {
							"required" : true
						});
						$("#field_SST_location").rules("add", {
							"required" : true
						});
						$("#field_SST_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_SST_model").rules("add", {
							"required" : true
						});
						$("#field_SST_accuracy").rules("add", {
							"required" : true
						});
						$("#field_SST_precision").rules("add", {"required":true});
						$("#field_SST_calibration").rules("add", {
							"required" : true
						});
						$("#field_equilibrator_type").rules("add", {
							"required" : true
						});
						$("#field_drying_method").rules("add", {
							"required" : true
						});
						$("#field_measurement_method").rules("add", {
							"required" : true
						});
						$("#field_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_model").rules("add", {
							"required" : true
						});
						$("#field_measured_co2_params").rules("add", {
							"required" : true
						});
						$("#field_frequency").rules("add", {
							"required" : true
						});
						$("#field_uncertainity").rules("add", {
							"required" : true
						});
						$("#field_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_resolution").rules("add", {
							"required" : true
						});
						$("#field_uncertainity_air").rules("add", {
							"required" : true
						});
						$("#field_resolution_air").rules("add", {
							"required" : true
						});
						$("#field_sensor_calibration").rules("add", {
							"required" : true
						});
						$("#field_calibration").rules("add", {
							"required" : true
						});
						$("#field_manufacturer_calibration").rules("add", {
							"required" : true
						});
						$("#field_Tequ_location").rules("add", {
							"required" : true
						});
						$("#field_Tequ_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_Tequ_model").rules("add", {
							"required" : true
						});
						$("#field_Tequ_accuracy").rules("add", {
							"required" : true
						});
						$("#field_Tequ_precision").rules("add", {
							"required" : true
						});
						$("#field_Tequ_calibration").rules("add", {
							"required" : true
						});
						$("#field_Pequ_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_Pequ_model").rules("add", {
							"required" : true
						});
						$("#field_Pequ_accuracy").rules("add", {
							"required" : true
						});
						$("#field_Pequ_precision").rules("add", {
							"required" : true
						});
						$("#field_Pequ_calibration").rules("add", {
							"required" : true
						});

						// end of common//

						// for filter 1
						$("#field_equilibration_volume").rules("add", {
							"required" : true
						});
						$("#field_gas_flow_rate").rules("add", {
							"required" : true
						});
						$("#field_vented").rules("add", {
							"required" : true
						});
						$("#field_Patm_sensor").rules("add", {
							"required" : true
						});
						$("#field_Patm_normalized").rules("add", {
							"required" : true
						});
						$("#field_Patm_manufacturer").rules("add", {
							"required" : true
						});
						$("#field_Patm_model").rules("add", {
							"required" : true
						});
						$("#field_Patm_accutacy").rules("add", {
							"required" : true
						});
						$("#field_Patm_precision").rules("add", {
							"required" : true
						});
						$("#field_Patm_calibration").rules("add", {
							"required" : true
						});

						// for filter 2
						$("#field_detail_sensing").rules("remove", {
							"required" : true
						});
						$("#field_method_references").rules("remove", {
							"required" : true
						});

					});

					$(".select2").change(function() {

						var val = $(this).val();

						if (val.trim() == 'Others') {
							$(this).next("input").css("display", "block");
							$(this).next("input").val("");
						} else {
							$(this).next("input").val(val);
							$(this).next("input").css("display", "none");

						}
					});

					var elems = $(".select2");
					var options = new Array();
					var optionsVal = new Array();
					$.each(elems, function(i, elm) {
						var nextElem = $(elm).next("input");
						options = $(elm).children();
						$.each(options, function(i, option) {
							optionsVal.push($(option).text().trim());

						});
						var val1 = $(nextElem).val();

						if (optionsVal.indexOf(val1) != -1) {
							$(elm).val(val1);
						} else {

							$(elm).val("Others");
							$(elm).next("input").css("display", "block");
						}

					});
					$(".select2").select2();
					if ($("#field_conflicts").val() != null
							&& $("#field_conflicts").val() != '') {
						var conflictedFields = $("#field_conflicts").val()
								.split(",");
						var html, i;
						for (i = 0; i < conflictedFields.length; i++) {
							html = "<div class='alert alert-error'><p>There is a conflict. Please select one of the following: </p>";
							var options = $("#" + conflictedFields[i]).val()
									.split("#");

							var i;
							for (j = 0; j < options.length; j++) {
								var option = options[j];
								/*
								 * if (option.indexOf("@@CONFLICT@@")!=-1) html =
								 * html + "<input type='radio' name='conflict" +
								 * i + "' onclick='selectRadio(\"" +
								 * conflictedFields[i] + "\",\"" + options +
								 * "\")' class='conflicts' value='" + options + "'
								 * checked> preserve conflict</input><br>";
								 * else
								 */
								html = html
										+ "<input type='radio' name='conflict"
										+ i + "' onclick='selectRadio(\""
										+ conflictedFields[i] + "\",\""
										+ option
										+ "\")' class='conflicts' value='"
										+ option + "'> " + option
										+ "</input><br>";
							}

							html = html + "</div>";
							$("#" + conflictedFields[i]).css('display', 'none');
							$("#" + conflictedFields[i]).before(html);
						}
						// $("#" + conflictedFields[i]).val('');
					}
					filterReq();

					/*
					 * $("a.fancy").fancybox({ 'width' : 970, 'height' : 920,
					 * 'transitionIn' : 'elastic', 'speedIn' : 600, 'speedOut' :
					 * 200, 'showCloseButton' : true, 'type' : 'iframe'
					 * 
					 * });
					 */

				});

function selectRadio(inputName, opt) {
	// alert(inputName+":"+ opt);
	document.getElementById(inputName).value = opt;
}

function returnback() {
	var warnmessage = 'You are exiting this page.\n'
			+ 'All unsaved changes will be lost.\n'
			+ 'Are you sure you want to exit?\n';
	var response = confirm(warnmessage);
	if (response) {
		window.location.href = "/SocatOME/editor.htm";
	}
}

var signE = "";
var signW = "";
var signN = "";
var signS = "";
function InvAdd() {
	var FirstInvisInv = 5;
	var InvNumber = parseInt(document.UnderwayForm.InvAddNumb.value);
	if (InvNumber == document.UnderwayForm.InvShown.value)
		return;
	for (var i = 1; i < 6; i++) {
		if (document.getElementById("Investigator" + i).style.display == "none") {
			FirstInvisInv = i;
			break;
		}
	}

	if (InvNumber > document.UnderwayForm.InvShown.value) {
		for (i = FirstInvisInv; i < InvNumber; i++) {
			document.getElementById("Investigator" + i).style.display = "block";
		}
		document.UnderwayForm.InvShown.value = InvNumber;
		// alert (VarNumber);
	} else {
		for (i = FirstInvisInv; i >= InvNumber; i--) {
			document.getElementById("Investigator" + i).style.display = "none";
		}
		document.UnderwayForm.InvShown.value = InvNumber;
	}
}

function VarAdd() {
	var FirstInvisVar = 28;
	var VarNumber = parseInt(document.UnderwayForm.VarAddNumb.value);
	if (VarNumber == document.UnderwayForm.VarShown.value)
		return;
	for (var i = 0; i < 29; i++) {
		if (document.getElementById("divVar" + i).style.display == "none") {
			FirstInvisVar = i;
			break;
		}
	}
	if (VarNumber > document.UnderwayForm.VarShown.value) {
		for (i = FirstInvisVar; i < VarNumber; i++) {
			document.getElementById("divVar" + i).style.display = "block";
		}
		document.UnderwayForm.VarShown.value = VarNumber;
		// alert (VarNumber);
	} else {
		for (i = FirstInvisVar; i >= VarNumber; i--) {
			document.getElementById("divVar" + i).style.display = "none";
		}
		document.UnderwayForm.VarShown.value = VarNumber;
	}
}

function SensorAdd() {
	var FirstInvisSensor = 8;
	var SensorNumber = parseInt(document.UnderwayForm.SensorAddNumb.value);
	if (SensorNumber == document.UnderwayForm.SensorShown.value)
		return;
	for (var i = 0; i < 9; i++) {
		if (document.getElementById("sensor" + i).style.display == "none") {
			FirstInvisSensor = i;
			break;
		}
	}
	if (SensorNumber > document.UnderwayForm.SensorShown.value) {
		for (i = FirstInvisSensor; i < SensorNumber; i++) {
			document.getElementById("sensor" + i).style.display = "block";
		}
		document.UnderwayForm.SensorShown.value = SensorNumber;
		// alert (VarNumber);
	} else {
		for (i = FirstInvisSensor; i >= SensorNumber; i--) {
			document.getElementById("sensor" + i).style.display = "none";
		}
		document.UnderwayForm.SensorShown.value = SensorNumber;
	}
}
function addFile() {
	var f = parseInt(document.UnderwayForm.Files.value) + 1;
	if (f > 3) {

		return;
	}
	{
		document.getElementById("divUserfile" + f).style.display = "block";
		if (f == 3)
			document.getElementById("AttMoFi").style.display = "none";
		document.UnderwayForm.Files.value = f;
	}
}

function getRawObject(obj) {
	var theObj = "";
	if (typeof obj == "string") {
		if (isW3C) {
			theObj = document.getElementById(obj);
		} else if (isIE4) {
			theObj = document.all(obj);
		} else if (isNN4) {
			theObj = seekLayer(document, obj);
		}
	} else {
		// pass through object reference
		theObj = obj;
	}
	return theObj;
}
function changeSign(dir, sign) {
	if (dir == 'west') {
		signW = sign;
		ddeg = document.getElementById("field_west").value;
		ddeg = ddeg.replace("-", "");
		document.getElementById("field_west").value = signW + ddeg;
	}
	if (dir == 'east') {
		signE = sign;
		ddeg = document.getElementById("field_east").value;
		ddeg = ddeg.replace("-", "");
		document.getElementById("field_east").value = signE + ddeg;
	}
	if (dir == 'north') {
		signN = sign;
		ddeg = document.getElementById("field_north").value;
		ddeg = ddeg.replace("-", "");
		document.getElementById("field_north").value = signN + ddeg;
	}
	if (dir == 'south') {
		signS = sign;
		ddeg = document.getElementById("field_south").value;
		ddeg = ddeg.replace("-", "");
		document.getElementById("field_south").value = signS + ddeg;
	}

}
function showDDegW() {
	deg = document.getElementById("field_west_degree").value;
	min = document.getElementById("field_west_minute").value;
	sec = document.getElementById("field_west_second").value;
	ddeg = calDecDeg(deg, min, sec);
	document.getElementById("field_west").value = signW + ddeg;
}
function showDDegE() {
	deg = document.getElementById("field_east_degree").value;
	min = document.getElementById("field_east_minute").value;
	sec = document.getElementById("field_east_second").value;
	ddeg = calDecDeg(deg, min, sec);
	document.getElementById("field_east").value = signE + ddeg;

}
function showDDegN() {
	deg = document.getElementById("field_north_degree").value;
	min = document.getElementById("field_north_minute").value;
	sec = document.getElementById("field_north_second").value;
	ddeg = calDecDeg(deg, min, sec);
	document.getElementById("field_north").value = signN + ddeg;
}
function showDDegS() {
	deg = document.getElementById("field_south_degree").value;
	min = document.getElementById("field_south_minute").value;
	sec = document.getElementById("field_south_second").value;
	ddeg = calDecDeg(deg, min, sec);
	document.getElementById("field_south").value = signS + ddeg;

}
function calDecDeg(deg, min, sec) {
	ddeg = deg / 1 + (min / 60) + (sec / 3600);
	return ddeg;
}
function back() {
	window.location.href = "/SocatOME/editor.htm";
}

function displayExpo() {
	val2 = document.getElementById("field_vessel_id").value;
	val3 = document.getElementById("field_start_date").value;
	val = val2 + val3;
	document.getElementById("field_cruise_id").value = val;
}
function open() {
	var val, val1, val2;
	var i = 0;
	for (i = 2; i < 6; i++) {

		val = document.getElementById("field_ownername" + i).value;
		if (val != null && val != "") {
			document.getElementById("Investigator" + i).style.display = "block";
			document.UnderwayForm.InvAddNumb[i - 1].selected = "selected";
		}
	}

	for (i = 0; i < 19; i++) {
		val = document.getElementById("field_variable" + (i + 1)).value;
		if (val != null && val != "") {
			document.getElementById("divVar" + i).style.display = "block";
			document.UnderwayForm.VarAddNumb[i + 1].selected = "selected";
		}

	}
	for (i = 0; i < 9; i++) {
		val1 = document.getElementById("field_manufaturer_other" + (i + 1)).value;
		val2 = document.getElementById("field_model_other" + (i + 1)).value;
		if (val1 != null && val1 != "") {
			document.getElementById("sensor" + i).style.display = "block";
			document.UnderwayForm.SensorAddNumb[i + 1].selected = "selected";
		} else if (val2 != null && val2 != "") {
			document.getElementById("sensor" + i).style.display = "block";
			document.UnderwayForm.SensorAddNumb[i + 1].selected = "selected";
		}

	}
	val1 = document.getElementById("field_start_date_dup").value;
	val2 = document.getElementById("field_end_date_dup").value;
	if (val1 != null && val1 != "") {
		document.getElementById("field_start_date").value = val1;
	}
	if (val2 != null && val2 != "") {
		document.getElementById("field_end_date").value = val1;
	}

}
function showDiv(divName) {
	if (document.getElementById(divName).style.display == 'block') {
		document.getElementById(divName).style.display = 'none';
		jQuery("#" + divName).find('input').removeAttr('checked');
	} else
		document.getElementById(divName).style.display = 'block';
}

function Sensor() {
	if (document.getElementById("sensor2").style.display == 'block') {
		document.getElementById("sensor2").style.display = 'none';
	} else
		document.getElementById("sensor2").style.display = 'block';

	if (document.getElementById("sensor3").style.display == 'block') {
		document.getElementById("sensor3").style.display = 'none';
	} else
		document.getElementById("sensor3").style.display = 'block';
}
function validateCaptcha() {
	jQuery.ajax({
		type : 'POST',
		url : 'recaptcha.jsp',
		success : function(result) {
			alert(result);
		},
		dataType : "json"
	});

}
/*
 * function defaultValues() { var i=0; for (i = 0; i < 12; i++) {
 * 
 * if ( document.getElementById("field_co2_data_unit" + i).value == "") { if(i ==
 * 0 || i == 1 || i == 6 || i == 9 )
 * document.getElementById("field_co2_data_unit"+i).value = "\u00B5mol/mol";
 * else document.getElementById("field_co2_data_unit"+i).value = "matm"; } } }
 */
function showUnit(divId) {

	switch (divId) {
	case 'Co2Div0':
		document.getElementById("field_co2_data_unit" + 0).value = "\u00B5mol/mol";
		break;
	case 'Co2Div1':
		document.getElementById("field_co2_data_unit" + 1).value = "\u00B5mol/mol";
		break;
	case 'Co2Div2':
		document.getElementById("field_co2_data_unit" + 2).value = "\u00B5atm";
		break;
	case 'Co2Div3':
		document.getElementById("field_co2_data_unit" + 3).value = "\u00B5atm";
		break;
	case 'Co2Div4':
		document.getElementById("field_co2_data_unit" + 4).value = "\u00B5atm";
		break;
	case 'Co2Div5':
		document.getElementById("field_co2_data_unit" + 5).value = "\u00B5atm";
		break;
	case 'Co2Div6':
		document.getElementById("field_co2_data_unit" + 6).value = "\u00B5mol/mol";
		break;
	case 'Co2Div7':
		document.getElementById("field_co2_data_unit" + 7).value = "\u00B5atm";
		break;
	case 'Co2Div8':
		document.getElementById("field_co2_data_unit" + 8).value = "\u00B5atm";
		break;
	case 'Co2Div9':
		document.getElementById("field_co2_data_unit" + 9).value = "\u00B5mol/mol";
		break;
	case 'Co2Div10':
		document.getElementById("field_co2_data_unit" + 10).value = "\u00B5atm";
		break;
	case 'Co2Div11':
		document.getElementById("field_co2_data_unit" + 11).value = "\u00B5atm";
		break;
	default:
		break;
	}
	showDiv(divId);
}
function displayFile(divID1, divID2) {
	var val = document.getElementById(divID1).value;
	document.getElementById(divID2).value = val;
}
function filterReq() {
	
	var value = document.getElementById("select_co2_instr_type").selectedIndex;
	switch (value) {
	case 0: {
		var elements = document.querySelectorAll(".req-filter1");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "inline-block";
		}
		var elements = document.querySelectorAll(".req-filter2");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "none";
		}
		break;
	}
	case 1: {
		var elements = document.querySelectorAll(".req-filter2");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "inline-block";
		}
		var elements = document.querySelectorAll(".req-filter1");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "none";
		}
		break;
	}
	case 2: {
		var elements = document.querySelectorAll(".req-filter2");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "inline-block";
		}
		var elements = document.querySelectorAll(".req-filter1");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "none";
		}
		break;
	}
	case 3: {
		var elements = document.querySelectorAll(".req-filter2");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "inline-block";
		}
		var elements = document.querySelectorAll(".req-filter1");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "none";
		}
		break;
	}
	default: {
		var elements = document.querySelectorAll(".req-filter2");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "inline-block";
		}
		var elements = document.querySelectorAll(".req-filter1");
		for (var i = 0; i < elements.length; i++) {
			elements[i].style.display = "none";
		}
		break;
	}
	}
}
