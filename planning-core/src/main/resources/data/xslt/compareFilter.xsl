<!-- 
   ==========================================================
   Stylesheet for sorting hashed entries
   ==========================================================
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

<!-- default rule: copy all -->
<xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

<!-- we cannot compare the executable flag, it was introduced later and is missing in old plans  -->
<xsl:template match="plato:action/@executable">
</xsl:template>


<!-- we do not export displayName  -->
<xsl:template match="@displayName">
</xsl:template>

<!-- we do not export displayName  -->
<xsl:template match="plato:measurementInfo">
<xsl:if test="plato:property">
<measurementinfo>
	<xsl:apply-templates/>
</measurementinfo>	
</xsl:if>
</xsl:template>


<!-- we do not export @single for nodes -->
<xsl:template match="plato:node/@single">
</xsl:template>


<xsl:template match="plato:leaf/plato:evaluation">
<evaluation>
	<xsl:apply-templates select="plato:alternative">
		<xsl:sort select="@key"/>
		<xsl:sort select="@fullname"/>
	</xsl:apply-templates>
</evaluation>
</xsl:template>


<xsl:template match="plato:alternative/plato:experiment/plato:results">
<results>
	<xsl:apply-templates select="plato:result">
		<xsl:sort select="@key"/>
	</xsl:apply-templates>
</results>
</xsl:template>

<xsl:template match="plato:alternative/plato:experiment/plato:detailedInfos">
<detailedInfos>
	<xsl:apply-templates select="plato:detailedInfo">
		<xsl:sort select="@key"/>
	</xsl:apply-templates>
</detailedInfos>
</xsl:template>

<xsl:template match="plato:measurements">
<measurements>
   <xsl:apply-templates select="plato:measurement">
      <xsl:sort select="plato:property/@name"/>
   </xsl:apply-templates>
</measurements>
</xsl:template>

<xsl:template match="plato:action/plato:params">
<params>
<xsl:apply-templates select="plato:param">
      <xsl:sort select="@name"/>
   </xsl:apply-templates>
</params>
</xsl:template>

</xsl:stylesheet>