<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V3.9.9 to V4.0.1
   ==========================================================
   Changes:
      * resets Fast Track Evaluation plans to state 0
      * removes planType FTE 
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
    xmlns="http://ifs.tuwien.ac.at/dp/plato"
    xmlns:plato="http://ifs.tuwien.ac.at/dp/plato"
 	xmlns:exsl="http://exslt.org/common"
    extension-element-prefixes="exsl"    
    xsi:schemaLocation="http://ifs.tuwien.ac.at/dp/plato plato-V4.xsd"
    exclude-result-prefixes="java xalan exsl">

<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:preserve-space elements="*"/>

<xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<xsl:template match="plato:plans">
	<plans xsi:schemaLocation="http://ifs.tuwien.ac.at/dp/plato plato-V4.xsd" version="4.0.2">
    	<xsl:apply-templates/>
    </plans>
</xsl:template>

<xsl:template match="plato:properties/@planType">
</xsl:template>

<xsl:template match="plato:properties/plato:state">
	<xsl:choose>
	<xsl:when test="../@plato:planType = 'FTE'">
	<state value="0"></state>
	</xsl:when>
	<xsl:otherwise>
	<xsl:copy-of select="."/>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>