<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<link rel="stylesheet" type="text/css"
	href="bootstrap/css/bootstrap.css"></link>
<script type="text/javascript" src="js/main.js"></script>
</head>
<body>
	<div id="wrapper">
		<%@include file="header.jsp"%>
		<div class="container">
			<div class="row">

				<span class="span2"> <input type="submit" name="expand-all"
					id="back" value="Back" class="btn " onClick="back()" />

				</span> <span class="offset8 span2 "> <a
					href="<c:url value="j_spring_security_logout"/>"> Logout</a>
				</span>
			</div>

			<div class="section">
				<h4>Success!</h4>
				File has been Approved: <a href="${elinks.filepath}" target="blank">${elinks.filename}</a><br>
			</div>
		</div>
</body>