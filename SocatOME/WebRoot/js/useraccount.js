

var jq = jQuery.noConflict();
jq(document).ready(function() {

	
	jq("#myform").validate({
		rules : {
			'username':{
				required:true
			},
			'password':{
				required:true
			},
			'newpassword':{
				required:true
			},
			'confirmPassword':{
				equalTo:'#newpassword',
					required:true,
					
			}
			
		}
		});
		
});

function verifypass()
{
	val1 = document.getElementById("password").value;
	val2 = document.getElementById("confirmPassword").value;
	if (val1!=val2)
		document.getElementById("error-mssg").style.display = "block";
	else
		document.getElementById("error-mssg").style.display = "none";
}