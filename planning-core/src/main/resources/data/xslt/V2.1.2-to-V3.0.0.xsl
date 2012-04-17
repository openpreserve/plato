<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V2.1.2 to V3.0.0
   ==========================================================
   Changes:
      * updates version number to 3.0.0
      * turns values of results from attributes into elements
      * information on automatic measurement of criteria is now stored in measurementInfo 
        it is not possible to restore this, due to differently mapped scales
        information on the previously used measurement is stored in the descriptions of the corresponding leaves
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
    xmlns:plato="http://www.planets-project.eu/plato"
    xsi:schemaLocation="http://www.planets-project.eu/plato ../schemas/plato-2.1.xsd"
    xmlns:wdt="http://www.planets-project.eu/wdt"
    exclude-result-prefixes="java xalan plato">

<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:preserve-space elements="*"/>

<xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<xsl:template match="/plato:plans">
    <plans xsi:schemaLocation="http://www.planets-project.eu/plato plato-3.0.xsd" xmlns:wdt="http://www.planets-project.eu/wdt" version="3.0.0">
        <xsl:apply-templates/>
    </plans>
</xsl:template>

<!-- information about measurement of criteria is now stored in measurementInfo, it is not possible to restore this, due to differently mapped scales
     therefore information on the previously used mapping is stored in description of the leaf -->
<xsl:template match="plato:leaf/plato:description">
	<description>
		<xsl:value-of select="."/>
		<xsl:if test="../plato:measurementMapping/plato:objectProperty and ../plato:measurementMapping/plato:metric">
		   <xsl:value-of select="concat('-- was measured as: outcome://object/xcl/', ../plato:measurementMapping/plato:objectProperty/@name, '#', ../plato:measurementMapping/plato:metric/@name, '. - description of property: ', ../plato:measurementMapping/plato:objectProperty/plato:description, '. - description of metric: ', ../plato:measurementMapping/plato:metric/plato:description)"/>
		</xsl:if>
	</description>
</xsl:template>

<xsl:template match="plato:measurementMapping">
</xsl:template>

<!-- turns values of results from attributes into elements -->
<xsl:template match="plato:leaf/plato:evaluation/plato:alternative/*">
	<xsl:element name="{local-name()}">
		<xsl:copy-of select="@*[name() != 'value']"/>
		<value><xsl:value-of select="@value"/></value>
		<xsl:apply-templates select="*|text()"/>
	</xsl:element>
</xsl:template>

</xsl:stylesheet>