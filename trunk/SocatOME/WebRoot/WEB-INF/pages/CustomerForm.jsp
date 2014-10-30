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
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
</head>

<body>
	<div id="main-content">
		<div id="logo">
			<table style="width: 100%;">
					<tr>
						<td>
						<img src="images/socat_icon.png"></img>
						</td>
						<td id='header'>
							<span>Metadata Collection Form</span>
						</td>
					</tr>
				</table>
		</div>
		<h2>Select Server side file for editing</h2>

		<div>
			<form:form method="POST" commandName="customer">

				<form:errors path="*" cssClass="errorblock" element="div" />

				<table>
					<tr>
						<td>UserName :</td>
						<td><form:input path="userName" />
						</td>
						<td><form:errors path="userName" cssClass="error" />
						</td>
					</tr>
					<tr>
					<tr>
						<td>Metadata Files :</td>
						<td><form:select path="mdFile" items="${files}"
								multiple="false" />
						</td>

					</tr>

					<form:hidden path="homePath" />

					<tr>
						<td colspan="3"><input type="submit" value="Edit" />
						</td>

					</tr>

				</table>
			</form:form>
		</div>
		</br> </br>
		<div>
			<form:form method="POST" action="newForm.htm" commandName="customer">
				<table>
					<tr>
						<td colspan="3"><input id="newForm" type="submit"
							value="Create New Form for editing" />
						</td>

					</tr>

				</table>
			</form:form>

		</div>
		</br> </br>
		<div>


			<form:form method="POST" action="serverForm.htm">
				<ul>
					<li><a href="<c:url value="/FileUploadForm.htm"/>">Select
							local file for editing on USGS server</a>
					</li>
				</ul>


			</form:form>

		</div>
	</div>

</body>
</html>