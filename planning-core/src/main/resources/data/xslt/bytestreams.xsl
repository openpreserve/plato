<!-- 
   ==============================================================================
   Stylesheet for plan-export: collecting bytestreams for temp-files into the XML
   ==============================================================================
   Changes:

      * 
   ==========================================================
   
   Plato: Planning Tool
   developed within the EU IST FP6 project PLANETS: Preservation and Long-term Access through Networked Services,
   Contract number 033789, June 2006-May 2009.
   Subproject: PP - Preservation Planning
   Workpackage: PP4 - Preservation Plan Decision Support
   Responsible partner:  TUWIEN - Vienna University of Technology, Department of Software Technology  and Interactive Systems
   Further information:
   www.planets-project.eu
   www.ifs.tuwien.ac.at/dp
--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:plato="http://www.planets-project.eu/plato" xmlns:wdt="http://www.planets-project.eu/wdt">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" />
  <xsl:preserve-space elements="*"/>
  
  <xsl:param name="tempDir" select="''"/>

 <xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

 
<!-- COPY BASE64-ENCODED DATA FROM TEMP FILES INTO THE TARGET XML -->
<xsl:template match="*/plato:data[@hasData='true']">
	<xsl:element name="data" xmlns="http://www.planets-project.eu/plato">
		<xsl:copy-of select="@*"/>
		<xsl:variable name="id">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:value-of select="document(concat($tempDir,$id,'.xml'))/data"/>
	</xsl:element>
</xsl:template>


</xsl:stylesheet>