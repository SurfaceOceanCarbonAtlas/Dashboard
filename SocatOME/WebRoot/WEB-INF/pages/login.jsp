<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link href="bootstrap/css/bootstrap.css" rel="stylesheet"></link>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>



<script type="text/javascript" src="js/jquery.min.js"></script>
<script language="javascript" src="js/jquery.placeholder.1.3.min.js"></script>
<title>Ocean Metadata Editor Login Page</title>

</head>


<div id="wrapper">
	<%@include file="header.jsp"%>



	<form name='f' action="<c:url value='j_spring_security_check' />"
		method="post">

		<div class="container">

			<div class="form-signin">
				<h4 class="form-signin-heading">
					Sign in <strong></strong>
				</h4>
				<input type="text" class="input-block-level" placeholder="Username"
					name='j_username' /> <input type="password"
					class="input-block-level" placeholder="Password" name='j_password' />
				<label class="checkbox"> <input type="checkbox" value="true"
					checked="checked" name='_spring_security_remember_me' /> Stay
					signed in
				</label>
				<c:if test="${not empty error}">
					<div class="alert alert-error">Login failed due to
						${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
				</c:if>
				<div>
					<button class="btn btn-large btn-info" name="submit">Sign
						in</button>
				</div>
			</div>			
		</div>


	</form>


</div>
<script src="bootstrap/js/bootstrap.js" />

</body>
</html>