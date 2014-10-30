<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@taglib prefix="xtags"
	uri="http://jakarta.apache.org/taglibs/xtags-1.0"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<title>Metadata Editor</title>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script language="javascript" src="js/jquery.dataTables.js"></script>
<link rel="stylesheet" type="text/css" href="css/demo_table.css"></link>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>
<link rel="stylesheet" type="text/css"
	href="bootstrap/css/bootstrap.css"></link>
<script>
	var jq = jQuery.noConflict();
	jq(document).ready(function() {
		jq('#example').dataTable({
			"aaSorting" : [ [ 4, "desc" ] ]
		});
	});
</script>
<head>
</head>
<body>
	<div id="wrapper">
		<%@include file="header.jsp"%>
		<div class="container">
			<table cellpadding="0" class="display" cellspacing="0" border="0"
				id="example">
				<thead>
					<tr>
						<th>File Location</th>
						<th>Status</th>

					</tr>
				</thead>
				<tbody>
					<c:forEach var="entry" items="${s.stats}">

						<tr>
							<td><a href="${entry.key}">${entry.key}</a></td>
							<td>${entry.value}</td>
						</tr>

					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<script src="bootstrap/js/bootstrap.js" />
</body>

</html>