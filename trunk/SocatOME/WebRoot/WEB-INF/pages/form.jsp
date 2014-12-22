<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" import="java.util.*"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page session="true"%>
<html>
<head>
<title>Online Metadata Editor</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="bootstrap/css/bootstrap.css" rel="stylesheet">
		<link href="bootstrap/css/bootstrap-responsive.css" rel="stylesheet">

			<link type="text/css" rel="stylesheet" media="all"
				href="css/autocomplete.css" />
			<link rel="stylesheet" type="text/css"
				href="css/jquery.fancybox-1.3.4.css" media="screen" />
			<!--  <link rel="stylesheet" type="text/css" href="css/jquery-ui.css" />-->
			<link rel="stylesheet" type="text/css"
				href="http://code.jquery.com/ui/1.8.6/themes/base/jquery-ui.css" />
			<link rel="stylesheet" type="text/css" href="css/main.css" />
			<link rel="stylesheet" type="text/css" href="css/select2.css" />

			<script type="text/javascript" src="js/jquery-1.7.1.js"></script>

			<script type="text/javascript" src="js/jquery.validate.js"></script>
		<!-- 	<script type="text/javascript" src="js/jquery-ui.min.js"></script>

			<script type="text/javascript" src="js/jquery.fancybox-1.3.4.js"></script> -->

			<script type='text/javascript'
				src='/SocatOME/dwr/interface/AutoCompleteService.js'></script>
			<script type='text/javascript' src='/SocatOME/dwr/engine.js'></script>
			<script type='text/javascript' src='/SocatOME/dwr/util.js'></script>
			<!--<script type='text/javascript' src='/SocatOME/dwr/interface/Place1.js'></script>-->
			<script type='text/javascript' src="js/dwrspring.js"></script>
			<script type="text/javascript" src="js/prototype/prototype.js"></script>
			<script type="text/javascript" src="js/script.aculo.us/effects.js"></script>
			<script type="text/javascript" src="js/script.aculo.us/controls.js"></script>
			<script type='text/javascript' src="js/autocomplete.js"></script>
			<script src="js/select2.js"></script>
			<script type="text/javascript" src="js/main.js"></script> 
</head>
<body data-spy="scroll" data-target=".bs-docs-sidebar">


	<%@include file="header.jsp"%>
	<div class="container">
		<form:form name="UnderwayForm" id="mainform" action="saveForm.htm"
			commandName="fgdcMap" enctype="multipart/form-data">
			<c:if test="${editor.med.field_filestatus =='draft'}">
				<div class="row-fluid">
					<div class="offset2 span10">
						<div class="alert alert-success">*This record has been
							previously saved as a working draft, but its not yet submitted</div>
					</div>
				</div>
			</c:if>
			<c:if test="${editor.med.field_filestatus =='approved'}">
				<div class="row-fluid">
					<div class="offset2 span10">
						<div class="alert alert-success">*This record has been
							approved</div>
					</div>
				</div>
			</c:if>
			<c:if test="${editor.med.field_filestatus =='submit'}">
				<div class="row-fluid">
					<div class="offset2 span10">
						<div class="alert alert-success">*This record has been
							submitted, but awaiting approval</div>
					</div>
				</div>
			</c:if>
			<div class="row-fluid">
				<span class="offset2 span2"> <input type="button" id="back"
					value="Back" class="btn" onclick="returnback()" />

				</span> <span class="offset3 span1"> <span
					class="label label-important"> * = required </span>
				</span> <span class="offset3 span1"> <a
					href="<c:url value="j_spring_security_logout" />"> Logout</a>
				</span>

			</div>
			<div class="row-fluid">
				<c:forEach items="${editor.med.field_conflicts}" begin="1"
					var="conflicts" varStatus="status">
						${conflict}
				</c:forEach>
			</div>
			<div class="row-fluid">
				<input type="hidden" id="VarShown" value="0"> <input
					type="hidden" id="InvShown" value="1"> <input type="hidden"
						id="SensorShown" value="0"> <form:input type="hidden"
								value="" path="field_record_id" /> <form:input type="hidden"
								value="" path="field_filename" /> <form:input type="hidden"
								value="" path="field_conflicts" /> <input type="hidden"
							id="Files" value="0"> <input type="hidden"
								name="MAX_FILE_SIZE" value="20000000"> <form:input
										path="field_form_type" class="form_type" type="hidden"
										value="underway" />
			</div>
			<div class="row-fluid">
				<div class="span2 bs-docs-sidebar">
					<ul class="nav nav-list bs-docs-sidenav affix-top">
						<li><a href="#contact"><i class="icon-chevron-right"></i>
								Metadata Creator</a></li>

						<li><a href="#investigators"><i
								class="icon-chevron-right"></i> Investigators</a></li>

						<li><a href="#datasetinfo"><i class="icon-chevron-right"></i>
								Dataset Info</a></li>

						<li><a href="#cruiseinfo"><i class="icon-chevron-right"></i>
								Cruise Info</a></li>
						<li><a href="#variablesinfo"><i
								class="icon-chevron-right"></i> Variables Info</a></li>
						<li><a href="#generaldescription"><i
								class="icon-chevron-right"></i> General Description</a></li>
						<li><a href="#surfacewaterdescription"><i
								class="icon-chevron-right"></i> Surface Water Co2 Method
								Description</a></li>
						<li><a href="#others"><i class="icon-chevron-right"></i>
								Additonal Information</a></li>

					</ul>

				</div>
				<div class="span10">

					<%@include file="contact.jsp"%>
					<%@include file="investigators.jsp"%>

					<%@include file="datasetinfo.jsp"%>
					<%@ include file="cruiseinfo.jsp"%>

					<%@ include file="variablesinfo.jsp"%>
					<%@ include file="generaldescription.jsp"%>
					<%@ include file="surfacewaterdescription.jsp"%>
					<%@ include file="others.jsp"%>

					<div class="req-filter1">
						<input class="btn btn-info cancel" type="submit"
							value="Save Locally" name="savelocally" /> <input type="submit"
							class="btn btn-info" name="draft"
							value="Save as Draft" /> <input name="submit"
							class="addfilter1 btn btn-success submit" value="Submit" type="submit" />
					</div>
					<div class="req-filter2">
						<input type="submit" class="btn btn-info cancel"
							value="Save Locally" name="savelocally" /> <input type="submit"
							class="btn btn-info" name="draft"
							value="Save as Draft" /> <input name="submit"
							class="addfilter2 btn btn-success submit" value="Submit" type="submit" />

					</div>
					<c:if test="${editor.adminUser =='true'}">
						<input type="submit" class="btn btn-warning" name="approve"
							value="Approve Record" style="background: orange" />
						<input type="submit" class="btn btn-danger" name="delete"
							value="Delete Record" />

					</c:if>

				</div>
			</div>

		</form:form>

	</div>



	<script src="bootstrap/js/bootstrap.js" />
	<script>
	$('.bs-docs-sidebar').scrollspy()
	</script>
</body>
</html>
