<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:strip-space elements="*" />
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	 <xsl:variable name="newline" select="'&#xD;'"/>
	<xsl:template match="@*|node()">
		<xsl:if test=". != '' or ./@* != ''">
			<xsl:copy>
				<xsl:apply-templates select="@*|node()" />
			</xsl:copy>

		</xsl:if>
	</xsl:template>	
	 <xsl:template match="text()">
        <xsl:value-of select="translate(., $newline,'')"/>
    </xsl:template>
</xsl:stylesheet>