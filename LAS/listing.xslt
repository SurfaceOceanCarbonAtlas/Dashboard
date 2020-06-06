<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">
	<xsl:output method="html" encoding="UTF-8" indent="no" doctype-system="about:legacy-compat" />

	<xsl:template match="listing">
		<html>
			<head>
				<title>Directory Listing For <xsl:value-of select="@directory" /></title>
				<script language="JavaScript">
					function setListingBackgrounds() {
						var tblRows = document.getElementsByTagName("tr");
						var numTblRows = tblRows.length;
						var lastExpocode = "";
						var expoNum = 0;
						var filename;
						var slashIdx;
						var underscoreIdx;
						var expocode;
						for (var k = 0; k != numTblRows; k++) {
							if ( (k % 2) == 0 ) {
								tblRows[k].style.background = "white";
							}
							else {
								tblRows[k].style.background = "#DDEEFF";
							}
						}
					}
					window.addEventListener("load", setListingBackgrounds, false);
				</script>
				<style> 
					body { margin: 0; font-family: sans-serif,Arial,Tahoma; 
							color: black; background-color: white; }
					h1 { padding: 0.25em 0.75em; margin: 0; color: white; background-color: #0086b2; }
					h3 { padding: 0.25em 0.75em; margin: 0; color: white; background-color : #0086b2; }
					pre { margin: 0 }
					a { color: black; }
					table { border-spacing: 0; }
					th.filename { padding: 0.5em 1.5em 0.25em; text-align: left; }
					th.filesize { padding: 0.5em 1.5em 0.25em; text-align: right; }
					th.filedate { padding: 0.5em 1.5em 0.25em; text-align: center; }
					td.filename { padding: 0.0em 1.5em 0.25em; text-align: left; }
					td.filesize { padding: 0.0em 1.5em 0.25em; text-align: right; }
					td.filedate { padding: 0.0em 1.5em 0.25em; text-align: center; }
				</style>
			</head>
			<body>
				<xsl:choose>
					<xsl:when test="contains(@directory,'MetadataDocs')">
						<h1>Directory Listing For <xsl:value-of select="@directory" /></h1>
						<table>
							<tr>
								<th class="filename">Filename</th>
								<th class="filesize">Size</th>
								<th class="filedate">Last Modified</th>
							</tr>
							<xsl:apply-templates select="entries" />
						</table>
						<xsl:apply-templates select="readme" />
						<h3>SOCAT Data Management System</h3>
					</xsl:when>
					<xsl:otherwise>
						<h2>Directory listing not allowed.</h2>
						<h3>SOCAT Data Management System</h3>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="entries">
		<xsl:apply-templates select="entry" />
	</xsl:template>

	<xsl:template match="readme">
		<pre><xsl:apply-templates /></pre>
	</xsl:template>

	<xsl:template match="entry">
		<xsl:choose>
			<xsl:when test="contains(@urlPath,'OME.xml')">
			</xsl:when>
			<xsl:when test="contains(@urlPath,'.properties')">
			</xsl:when>
			<xsl:when test="contains(@urlPath,'.svn')">
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="urlPath" select="@urlPath" />
				<tr id="{$urlPath}">
					<td class="filename">
						<a href="{$urlPath}">
							<pre><xsl:apply-templates /></pre>
						</a>
					</td>
					<td class="filesize">
						<pre><xsl:value-of select="@size" /></pre>
					</td>
					<td class="filedate">
						<pre><xsl:value-of select="@date" /></pre>
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>

