<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V1.3 to V1.9
   ==========================================================
   Changes:

      * rename "project" to "plan"
      * threshold target range is now [0..4] (was [1..5] before)
      * update version number to 1.9
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
   version="1.0"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:noNamespaceSchemaLocation="http://www.ifs.tuwien.ac.at/dp/plato/schemas/plato-1.9.xsd">
  <xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
  <xsl:preserve-space elements="*"/>

 <xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

 
<!-- Change projects to plans, the resulting XML file has format Plato 1.9 -->
<xsl:template match="projects">
  <plans version="1.9">
    <xsl:apply-templates/>
  </plans>
</xsl:template>

<!-- change threshold target range to [0..4] (was [1..5] before) -->
<xsl:template match="threshold/@target">
  <xsl:attribute name="{local-name()}">
      <xsl:value-of select=". - 1" />
  </xsl:attribute>
</xsl:template>

<!-- rename project to "plan" -->
<xsl:template match="project">
  <plan>
    <xsl:apply-templates/>
  </plan>
</xsl:template>

</xsl:stylesheet>