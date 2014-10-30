<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@taglib prefix="xtags"
	uri="http://jakarta.apache.org/taglibs/xtags-1.0"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<title>USGS Core Science Metadata Editor</title>
<link rel="stylesheet" type="text/css" href="css/customer_form.css"></link>

<div id="main-content">
	<div id="logo">
		<table style="width: 100%;">
			<tr>
				<td>
					<img src="images/socat_icon.png"></img>
					</td>
				<td id='header'><span>Metadata Collection Form</span></td>
			</tr>
		</table>
	</div>
	<table width="950">
		<tr>
			<td align="right"><a
				href="<c:url value="j_spring_security_logout" />"> Logout</a>
			</td>
		</tr>
	</table>
	<div id="content">
		<table width="100%">
			<tr>
				<td width="40%" style="text-align: right"><input type="submit"
					name="expand-all" id="back" value="Back" class="form-submit"
					onClick="javascript:history.back();" />
				</td>
			</tr>
		</table>
		<c:import url="./FGDCPlus.xsl" var="xslt"></c:import>
		<x:transform xml="${td.htmltext}" xslt="${xslt}"></x:transform>
	</div>
</div>