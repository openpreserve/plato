<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:plato="http://ifs.tuwien.ac.at/dp/plato">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:preserve-space elements="*" />

	<xsl:param name="tempDir" select="''" />

	<xsl:template match="*|text()|@*">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates select="*|text()" />
		</xsl:copy>
	</xsl:template>

	<!-- COPY BASE64-ENCODED DATA FROM TEMP FILES INTO THE TARGET XML -->
	<xsl:template match="//plato:executablePlan[@type='t2flow']">
		<xsl:element name="executablePlan" xmlns="http://ifs.tuwien.ac.at/dp/plato">
			<xsl:copy-of select="@*" />
			<xsl:variable name="id">
				<xsl:value-of select="." />
			</xsl:variable>
			<xsl:value-of select="document(concat($tempDir,$id,'.xml'))/." />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>