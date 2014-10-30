<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<html>
<head>
<style>
.error {
	color: #ff0000;
}

.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
<title>Metadata Creation Toolkit</title>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<script type='text/javascript' src="js/customer_form.js"></script>

<link rel="stylesheet" type="text/css"
	href="bootstrap/css/bootstrap.css"></link>
</head>
<c:if test="${not empty error}">
	<div class="errorblock">
		The file could not be uploaded!! Please check the mime type or file
		size. <br />
	</div>
</c:if>
<body>
	<div id="wrapper">
		<%@include file="header.jsp"%>
		<div class="container">

			<div class="row">
				<span class="span3"> <form:form method="POST"
						commandName="editor" action="editor.htm">
						<input class="btn btn-info" name="statistics" type="submit"
							value="Statistics" />
					</form:form></span> <span class="span6"><div class="alert alert-success">You
						are logged in as an Administrator !</div></span> <span class="offset2 span1">
					<a href="<c:url value="j_spring_security_logout" />"> Logout</a>
				</span>

			</div>
			<div class="section">
				<form:form method="POST" commandName="editor" action="editor.htm">
					<form:hidden path="homePath" />
					<div class="row">
						<span class="span11"><form:errors path="*" cssClass="errorblock" element="div" />
							<h5 class="alert alert-info">Edit Files on the Server:</h5></span>
						
					</div>
					<div class="row">
						<span class="span3">Select Users :</span> <span class="span4"><form:select
								path="profile" items="${users}" id="mdFile" multiple="false" /></span>
						<span class="span4"> <input class="btn" type="submit"
							name="openUser" value="See Files" /></span>

					</div>
					<div class="row">
						<span class="span3">Select Files :</span> <span class="span4">
							<form:select path="mdFile" items="${files}" multiple="false" />
						</span> <span class="span4"><input class="btn" name="loader"
							type="submit" value="Load XML" /></span>

					</div>


				</form:form>

				<div class="row">
					<form:form method="POST" commandName="editor">

						<form:errors path="*" cssClass="errorblock" element="div" />

					</form:form>
				</div>
			</div>
			<div class="section">
				<div class="row">
					<div class="span5">
						<h5 class="alert alert-info">Create new record using editor</h5>
					</div>
					<div class="span6">
						<h5 class="alert alert-info">Upload metadata file from your
							computer</h5>
					</div>

				</div>
				<div class="row">

					<span class="offset1 span4"> <form:form method="POST"
							action="newForm.htm" commandName="editor">
							<input class="btn btn-success" value="Create New" type="submit" />
						</form:form>
					</span> <span class="span6"> <form:form action="editor.htm"
							commandName="FORM" enctype="multipart/form-data" method="POST">

							<form:input type="file" path="file" />

							<input type="submit" class="btn btn-info" value="Open in Editor" />


							<form:input type="hidden" value="underway" path="form_type" />

						</form:form>
					</span>

				</div>

			</div>

		</div>
	</div>
	<script src="bootstrap/js/bootstrap.js" />
</body>
</html>