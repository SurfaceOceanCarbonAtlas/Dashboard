<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Upload Confirmation Page</title>
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
		<h3 style="color: green;">File has been uploaded successfully.</h3>
		<br> File Name : ${FORM.name}.

	</div>
</body>
</html>
