<!-- 
   ==========================================================
   Stylesheet for migration of FTE template files to V3.0.1
   ==========================================================
   Changes:
   
      * MeasurementInfo -> Criterion
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="java xalan">

<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:preserve-space elements="*"/>

<xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<xsl:template match="templates">
<templates>
    <xsl:apply-templates/>
</templates>
</xsl:template>

<!-- information about measurement of criteria is now stored in measurementInfo, it is not possible to restore this, due to differently mapped scales
     therefore information on the previously used mapping is stored in description of the leaf -->
<xsl:template match="measurementInfo">
    <criterion>
        <xsl:apply-templates/>
    </criterion>
</xsl:template>
</xsl:stylesheet>