<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V3.9.9 to V4.0.0
   ==========================================================
   Changes:
      * measure and attributes based on the new quality model 
        replace criterion, measureableproperty, and metric
      * remove eprintsPlan 
      
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
    xmlns:lookup="http://lookup"
 	xmlns:exsl="http://exslt.org/common"
    extension-element-prefixes="exsl"    
    xsi:schemaLocation="http://ifs.tuwien.ac.at/dp/plato plato-V4.xsd"
    exclude-result-prefixes="java xalan exsl lookup">

<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:preserve-space elements="*"/>

<xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<xsl:template match="plato:plans">
	<plans xsi:schemaLocation="http://ifs.tuwien.ac.at/dp/plato plato-V4.xsd" version="4.0.0">
    	<xsl:apply-templates/>
    </plans>
</xsl:template>

<xsl:template match="plato:properties">
	<xsl:element name="properties" namespace="http://ifs.tuwien.ac.at/dp/plato">
	<xsl:apply-templates select="@*"/>
	<xsl:attribute name="planType" >
	<xsl:choose>
		<xsl:when test="./plato:state/@value > 15">FTE</xsl:when>
		<xsl:otherwise>FULL</xsl:otherwise>
	</xsl:choose>	
	</xsl:attribute>
	<xsl:apply-templates select="*|text()"></xsl:apply-templates>
	</xsl:element> 
</xsl:template>
<xsl:template match="plato:properties/plato:state">
	<xsl:choose>
	<xsl:when test="@value = 16">
	<state value="0"></state>
	</xsl:when>
	<xsl:when test="@value = 17">
	<state value="4"></state>
	</xsl:when>
	<xsl:when test="@value = 18">
	<state value="9"></state>
	</xsl:when>
	<xsl:when test="@value = 19">
	<state value="12"></state>
	</xsl:when>
	<xsl:otherwise>
	<xsl:copy-of select="."/>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<!--  
<xsl:variable name="criteriaText">
<lookup:criteria xmlns="http://ifs.tuwien.ac.at/dp/plato">
	<lookup:criterion ID="outcome://object/image/similarity#equal">
		<measure ID="http://ifs.tuwien.ac.at/dp/plato/similarity-equal">
		</measure>
	</lookup:criterion>
	<lookup:criterion ID="outcome://object/image/similarity#ssimSimple">
		<measure ID="http://ifs.tuwien.ac.at/dp/plato/similarity-simm">
		</measure>
	</lookup:criterion>
</lookup:criteria>
</xsl:variable>
-->
<xsl:template match="plato:criterion">
<xsl:comment>
	<xsl:copy-of select="."></xsl:copy-of> 
</xsl:comment>

<!-- 
    <xsl:variable name="critID" select="./@ID"/>
    criterion: <xsl:value-of select="$critID"></xsl:value-of>
	<xsl:copy-of xmlns="http://ifs.tuwien.ac.at/dp/plato" select="exsl:node-set($criteriaText)/lookup:criteria/lookup:criterion[@ID=$critID]/*" />
	
 -->	
</xsl:template>

<!-- remove eprintsPlan -->
<xsl:template match="plato:eprintsPlan">
<xsl:comment>
	<xsl:copy-of select="."></xsl:copy-of> 
</xsl:comment>
</xsl:template>

</xsl:stylesheet>