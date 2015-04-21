<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page session="true"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>File Upload</title>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1251">
</head>
<body>
	<div id="main-content">
		<div id="logo">
			<table style="width: 100%;">
				<tr>
					<td>
					<img src="images/socat_icon.png"></img>
					</td>
					<td id='header'><span>Metadata Collection Form</span></td>
				</tr>
			</table>
		</div>
		<form:form commandName="FORM" enctype="multipart/form-data"
			method="POST">

			<fieldset>
				<legend id="upload_header">Select Local File:</legend>
				<table>
					<tr>
						<td colspan="2" style="color: red;"><form:errors path="*"
								cssStyle="color : red;" /> ${errors}</td>
					</tr>
					<tr>
						<td>Name :</td>
						<td><form:input type="file" path="file" />
						</td>
					</tr>
					<tr>
						<td colspan="2"><input type="submit"
							value="Transfer to USGS server and open in form editor " />
						</td>
					</tr>
				</table>
			</fieldset>
		</form:form>
	</div>
</body>
</html>