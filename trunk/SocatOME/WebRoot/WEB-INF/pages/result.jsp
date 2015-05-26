<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<link rel="stylesheet" type="text/css"
	href="bootstrap/css/bootstrap.css"></link>
<script>
function closeWindow()
{
	window.close();
}
</script>
</head>
<body>
	<div id="wrapper">
		<%@include file="header.jsp"%>
		<div class="container">
			<div class="row">
 				<span class="span2"> 
 					<input type="submit" value="Close Window" class="btn" onclick="closeWindow()" />
 				</span>
			</div>

			<div class="section">
				<h4>Success</h4>
				Your file has been validated and saved. <br />  
				It will be included with your data. <br />
				You can close this window. <br />
				<br />
			</div>
		</div>
	</div>


</body>
</html>
