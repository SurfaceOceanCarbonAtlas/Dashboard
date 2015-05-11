function viewFDGCStyle()
{
	var val = document.getElementById("fgdcButton").value;
	if( val == "Apply FGDC Stylesheet" )
		{
		document.getElementById("fgdcButton").value = "Remove FGDC Stylesheet";
		document.getElementById("removeStyleSheet").style.display = "none";
		document.getElementById("applyStyleSheet").style.display = "block";
		}
	else
		{
		document.getElementById("fgdcButton").value = "Apply FGDC Stylesheet";
		document.getElementById("removeStyleSheet").style.display = "block";
		document.getElementById("applyStyleSheet").style.display = "none";
		}
}
function backI(){
	window.location.href = "/SocatOME";
}
