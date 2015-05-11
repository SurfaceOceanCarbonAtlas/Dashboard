var jq = jQuery.noConflict();
jq(document).ready(function() {

	jq("#mainform").validate({
		rules : {
			'field_ownername' : {
				required : true
			},
			'field_ownername2' : {
				required : true
			},
			'field_ownername3' : {
				required : true
			},
			'field_title' : {
				required : true
			},
			'field_user_email' : {
				required : true,
				email : true
			}

		}
	});
	jq("#field_initial_submission").datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange : '1900:2030',
		numberOfMonths : 1,
		dateFormat : 'yy/mm/dd'
	});
	jq("#field_revised_submission").datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange : '1900:2030',
		numberOfMonths : 1,
		dateFormat : 'yy/mm/dd'
	});
	jq("#field_start_date").datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange : '1900:2030',
		numberOfMonths : 1,
		dateFormat : 'yy/mm/dd'
	});
	jq("#field_end_date").datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange : '1900:2030',
		numberOfMonths : 1,
		dateFormat : 'yy/mm/dd'
	});
	displayDates();
	open();

});

function displayDates() {
	var west = document.getElementById("field_west").value;
	if (west.indexOf("-") != -1) {
		west = west.replace("-", "");

		document.getElementById("field_west_EW2").checked = true;
	}
	var d = Math.floor(west);
	var minfloat = (west - d) * 60;
	var m = Math.floor(minfloat);
	var secfloat = (minfloat - m) * 60;
	var s = Math.round(secfloat);
	document.getElementById("field_west_degree").value = d;
	document.getElementById("field_west_minute").value = m;
	document.getElementById("field_west_second").value = s;

	var east = document.getElementById("field_east").value;
	if (east.indexOf("-") != -1) {
		east = east.replace("-", "");

		document.getElementById("field_east_EW2").checked = true;
	}
	d = Math.floor(east);
	minfloat = (east - d) * 60;
	m = Math.floor(minfloat);
	secfloat = (minfloat - m) * 60;
	s = Math.round(secfloat);
	document.getElementById("field_east_degree").value = d;
	document.getElementById("field_east_minute").value = m;
	document.getElementById("field_east_second").value = s;
	// north
	var north = document.getElementById("field_north").value;
	if (north.indexOf("-") != -1) {
		north = north.replace("-", "");

		document.getElementById("field_north_NS2").checked = true;
	}
	d = Math.floor(north);
	minfloat = (north - d) * 60;
	m = Math.floor(minfloat);
	secfloat = (minfloat - m) * 60;
	s = Math.round(secfloat);
	document.getElementById("field_north_degree").value = d;
	document.getElementById("field_north_minute").value = m;
	document.getElementById("field_north_second").value = s;

	// south
	var south = document.getElementById("field_south").value;
	if (south.indexOf("-") != -1) {
		south = south.replace("-", "");

		document.getElementById("field_south_NS2").checked = true;
	}
	d = Math.floor(south);
	minfloat = (south - d) * 60;
	m = Math.floor(minfloat);
	secfloat = (minfloat - m) * 60;
	s = Math.round(secfloat);
	document.getElementById("field_south_degree").value = d;
	document.getElementById("field_south_minute").value = m;
	document.getElementById("field_south_second").value = s;

}


var signE = "";
var signW = "";
var signN = "";
var signS = "";
function InvAdd() {
	var FirstInvisInv = 5;
	var InvNumber = parseInt(document.DiscreteForm.InvAddNumb.value);
	if (InvNumber == document.DiscreteForm.InvShown.value)
		return;
	for ( var i = 1; i < 6; i++) {
		if (document.getElementById("Investigator" + i).style.display == "none") {
			FirstInvisInv = i;
			break;
		}
	}
	
	if (InvNumber > document.DiscreteForm.InvShown.value) {
		for (i = FirstInvisInv; i < InvNumber; i++) {
			document.getElementById("Investigator" + i).style.display = "block";
		}
		document.DiscreteForm.InvShown.value = InvNumber;
		// alert (VarNumber);
	} else {
		for (i = FirstInvisInv; i >= InvNumber; i--) {
			document.getElementById("Investigator" + i).style.display = "none";
		}
		document.DiscreteForm.InvShown.value = InvNumber;
	}
}
function VarAdd() {
	var FirstInvisVar = 28;
	var VarNumber = parseInt(document.DiscreteForm.VarAddNumb.value);
	if (VarNumber == document.DiscreteForm.VarShown.value)
		return;
	for ( var i = 0; i < 29; i++) {
		if (document.getElementById("divVar" + i).style.display == "none") {
			FirstInvisVar = i;
			break;
		}
	}
	if (VarNumber > document.DiscreteForm.VarShown.value) {
		for (i = FirstInvisVar; i < VarNumber; i++) {
			document.getElementById("divVar" + i).style.display = "block";
		}
		document.DiscreteForm.VarShown.value = VarNumber;
		// alert (VarNumber);
	} else {
		for (i = FirstInvisVar; i >= VarNumber; i--) {
			document.getElementById("divVar" + i).style.display = "none";
		}
		document.DiscreteForm.VarShown.value = VarNumber;
	}
}

function addFile() {
	var f = parseInt(document.DiscreteForm.Files.value) + 1;
	if (f > 3) {

		return;
	}
	{
		document.getElementById("divUserfile" + f).style.display = "block";
		if (f == 3)
			document.getElementById("AttMoFi").style.display = "none";
		document.DiscreteForm.Files.value = f;
	}
}

var RecaptchaOptions = {
	theme : 'custom',
	lang : 'en',
	custom_theme_widget : 'recaptcha_widget'
};

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

function open() {
	var i = 0;
	for (i = 2; i < 6; i++) {
		
		val = document.getElementById("field_ownername"+i).value;
		if (val != null && val != "") {
			document.getElementById("Investigator"+i).style.display = "block";
			document.DiscreteForm.InvAddNumb[i -1].selected = "selected";
		}		
	}

	var i = 0;
	for (i = 0; i < 19; i++) {
		val = document.getElementById("field_variable" + (i + 1)).value;
		if (val != null && val != "") {
			document.getElementById("divVar" + i).style.display = "block";
			document.DiscreteForm.VarAddNumb[i + 1].selected = "selected";
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
function displayFile(divID1,divID2)
{
	var val = document.getElementById(divID1).value;
	document.getElementById(divID2).value = val;
}
