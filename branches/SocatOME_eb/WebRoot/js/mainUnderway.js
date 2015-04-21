var jq = jQuery.noConflict();
jq(document).ready(function() {
	
	jq("#mainform").validate({
		rules:
			{
			'field_ownername' : {
				required : true
			},
			'field_organizationame' : {
				required : true
			},
			'field_owneraddress' : {
				required : true
			},
			'field_telephonenumber' : {
				required : true
			},
			'field_email' : {
				required : true,
				email: true
			}
			}
	});
});

var InvShown = 1;
var VarShown = 1;

function Investigator()
{
	var Inv2 = document.getElementById("Investigator2");
	var Inv3 = document.getElementById("Investigator3");
	if (InvShown == 3 && Inv3.style.display == "block")
	{
		Inv3.style.display = "none";
		return;
	}
	if (InvShown == 3 && Inv2.style.display == "block" )
	{
		Inv2.style.display = "none";
		InvShown = 1;
		return;
	}
	if (InvShown == 2)
	{
		Inv3.style.display = "block";
		InvShown = 3;
	}
	if (InvShown == 1)
	{
		Inv2.style.display = "block";
		InvShown = 2;
	}
}

function VarAdd()
{
	var FirstInvisVar = 13;
	var VarNumber = parseInt(document.DiscreteForm.VarAddNumb.value);
	if (VarNumber == document.DiscreteForm.VarShown.value) return;
	for (var i = 0; i < 14 ; i++)
	{
		if (document.getElementById("divVar" + i).style.display == "none") 
		{
			FirstInvisVar = i;
			break;
		}
	}
	if (VarNumber > document.DiscreteForm.VarShown.value)
	{
		for (i = FirstInvisVar; i < VarNumber; i++)
		{
			document.getElementById("divVar" + i).style.display = "block";
		}
		document.DiscreteForm.VarShown.value = VarNumber;
		// alert (VarNumber);
	} 
	else
	{
		for (i = FirstInvisVar; i >= VarNumber; i--)
		{
			document.getElementById("divVar" + i).style.display = "none";
		}  
		document.DiscreteForm.VarShown.value = VarNumber;
	}
}

function addFile()
{
	var f = parseInt(document.DiscreteForm.Files.value) + 1;
	if (f > 3)
	{
		
		return;
	}
	{
		document.getElementById("divUserfile" + f).style.display = "block";
		if (f == 3) document.getElementById("AttMoFi").style.display = "none";
		document.DiscreteForm.Files.value = f;
	}
}

var RecaptchaOptions = {
		theme: 'custom',
		lang: 'en',
		custom_theme_widget: 'recaptcha_widget'
};

function getRawObject(obj) {
	var theObj;
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

