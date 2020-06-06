<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wrs="http://java.sun.com/xml/ns/jdbc" version="1.0">
	<xsl:output method="html" indent="yes" />
	<xsl:template match="/">

		<table border="1">

			<tr bgcolor="#A9D67A">
				<th>Row</th>
				<xsl:for-each select="wrs:webRowSet/wrs:metadata/wrs:column-definition">
					<th>
						<xsl:value-of select="wrs:column-name" />
					</th>
				</xsl:for-each>
			</tr>

			<xsl:for-each select="wrs:webRowSet/wrs:data/wrs:currentRow">
				<xsl:if test="position() mod 2 = 1">
					<tr bgcolor="#F3E7C9">
						<td>
							<xsl:value-of select="position()" />
						</td>
						<xsl:for-each select="wrs:columnValue">
							<td>
								<xsl:value-of select="." />
							</td>
						</xsl:for-each>
					</tr>
				</xsl:if>
				<xsl:if test="position() mod 2 = 0">
					<tr>
						<td>
							<xsl:value-of select="position()" />
						</td>
						<xsl:for-each select="wrs:columnValue">
							<td>
								<xsl:value-of select="." />
							</td>
						</xsl:for-each>
					</tr>
				</xsl:if>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
