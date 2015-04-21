<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<link rel="stylesheet" type="text/css"
	href="bootstrap/css/bootstrap.css"></link>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<title>Online Metadata Editor</title>
</head>
<div id="wrapper">
	<%@include file="header.jsp"%>

	<form name='f' action="<c:url value='j_spring_security_check' />"
		method='POST'>
		<div class="container">

			<div class="form-signin">
				<center>
					<div>Welcome to the Online Metadata Editor, an easy-to-use
						tool to help you document your data. Guest users could simply
						create new Metadata or view a existing metdata just by clicking
						the button below.</div>
					<br />

					<div>
						<input type='hidden' name='j_username' value='guest' size="30px">

						<input type='hidden' name='j_password' value='guest' size="30px" />

						<input class="btn-large btn-info" name="submit" type="submit"
							value="Create/Upload Metadata" /> <input type='hidden'
							name='_spring_security_remember_me'
							id="_spring_security_remember_me" value="true" />
					</div>
				</center>
			</div>

<!-- 			<div class="request-signin"> -->
<%-- 				<a href="<c:url value="/login.htm"/>">Admin Login</a> --%>
<!-- 			</div> -->


		</div>
	</form>

</div>



</div>
<script src="bootstrap/js/bootstrap.js" />
</body>
</html>
