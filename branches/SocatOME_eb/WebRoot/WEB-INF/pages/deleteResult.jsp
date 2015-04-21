<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<script type="text/javascript" src="js/main.js"></script>
</head>
<body>
	<div id="main-content">
		<div id="logo">
			<table style="width: 100%;">
				<tr>
					<td>
					<img src="images/socat_icon.png"></img>
					</td>
					<td id='header'><span>Metadata Collection Form</span>
					</td>
				</tr>
			</table>
		</div>
		<table width="980">
			<tr>
				<td align="right"><c:if test="${editor.adminUser =='true'}">
						<a href="<c:url value="j_spring_security_logout"/>"> Logout</a>
					</c:if></td>
			</tr>
		</table>
		<div style="text-align: left">
			<input type="submit" name="expand-all" id="back" value="Back"
				class="form-submit" onClick="back()" />

		</div>

		<fieldset>
			<legend id="result-header">Success!</legend>
			Record was deleted sucesfully!!
			<br /> <br />

		</fieldset>


	</div>
</body>