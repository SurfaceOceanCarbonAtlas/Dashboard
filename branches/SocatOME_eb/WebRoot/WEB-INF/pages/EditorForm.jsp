<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
</head>
<head>
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

				<span class="span3"> <input type="submit" name="expand-all"
					id="back" value="Back" class="btn" onclick="backI()" /></span> <span
					class="span9"> <form:form method="POST" commandName="editor">

						<form:errors path="*" cssClass="errorblock" element="div" />

					</form:form>
				</span>
			</div>
			
			<div class="section">
				<div class="row">
					<div class="span5">
						<h5 class="alert alert-info">Create new record using editor</h5>
					</div>
					<div class="span6">
						<h5 class="alert alert-info">Upload metadata file from your
							computer </h5>
					</div>

				</div>
				<div class="row">

					<span class="offset1 span4"> <form:form method="POST"
							action="newForm.htm" commandName="editor">
							<input class="btn btn-success" value="Create New" type="submit" />
						</form:form>
					</span> 
					
					<span class="offset1 span5"> 
					
						<form:form action="editor.htm"
							commandName="FORM" enctype="multipart/form-data" method="POST">

							<form:input type="file" path="file" />

							<input type="submit" class="btn btn-info"
								value="Open in Editor" />


							<form:input type="hidden" value="underway" path="form_type" />

						</form:form>
					</span>

				</div>
			</div>
		</div>

		<script src="bootstrap/js/bootstrap.js" />
</body>
</html>