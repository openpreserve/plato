<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V2.0 to V2.1
   ==========================================================
   Changes:

      * rename servicelocator -> actionIdentifier
      * update version number to 2.1
      
      
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
   xmlns="http://www.planets-project.eu/plato"
   xsi:noNamespaceSchemaLocation="http://www.ifs.tuwien.ac.at/dp/plato/schemas/plato-2.0.xsd"
   exclude-result-prefixes="java xalan">

  <xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
  <xsl:preserve-space elements="*"/>

 <xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<xsl:template match="plans">
	<plans version="2.1">
		<xsl:apply-templates select="*|text()" />
	</plans>
</xsl:template>


<xsl:template match="action/@serviceLocator">
   <xsl:attribute name="actionIdentifier">
      <xsl:value-of select="(.)" />
   </xsl:attribute>
</xsl:template>

<xsl:template match="threshold">
    <xsl:variable name="tname" select="concat('threshold',@target+1)"/>
	<xsl:element name="{$tname}">
      <xsl:value-of select="@value" />
      </xsl:element>
</xsl:template>

<!-- The resulting XML file has format Plato 2.1 
<xsl:template match="plans/@version|plan/@version">
  <xsl:attribute name="{name()}">
    <xsl:value-of select="2.1" />
  </xsl:attribute>
</xsl:template>
-->
</xsl:stylesheet>