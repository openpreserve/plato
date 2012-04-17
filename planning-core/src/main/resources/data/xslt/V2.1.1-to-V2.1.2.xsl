<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V2.1.1 to V2.1.2
   ==========================================================
   Changes:
      * update version number to 2.1.2
      * changed prefix for executable plan to wdt 

      * rename experiment/uploads/upload to experiment/results/result
      * xcdlDescriptions/xcdlDescription moved to corresponding results/result
      * rename xclMapping to measurementMapping
      * turn description of node and leaf into an element
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
    xsi:schemaLocation="http://www.planets-project.eu/plato ../schemas/plato-2.1.xsd"
    xmlns:wdt="http://www.planets-project.eu/wdt"
    exclude-result-prefixes="java xalan">

<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
<xsl:preserve-space elements="*"/>

<!-- default rule: copy all -->
<xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<xsl:template match="/plans">
    <plans xsi:schemaLocation="http://www.planets-project.eu/plato plato-2.1.xsd" xmlns:wdt="http://www.planets-project.eu/wdt" version="2.1.2">
        <xsl:apply-templates/>
    </plans>
</xsl:template>

<!-- according to planets_wdt schema V1.0 this must not be null, remove it if it is empty  -->
<xsl:template match="planWorkflow">
    <xsl:choose>
        <xsl:when test="not(string(workflowConf))">
            <!-- remove this empty node: according to planets_wdt schema V1.0 this must not be null -->
        </xsl:when>
        <xsl:otherwise>
            <planWorkflow>
            	<workflowConf xmlns="http://www.planets-project.eu/wdt">
                	<xsl:apply-templates/>
                </workflowConf>
            </planWorkflow>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- sort elements of digitalObjectType 
     report, applyingPoliciesUpload, designatedCommunityUpload, mandateUpload, xcdlDescription, record, experimentResult, upload -->
<xsl:template match="report|applyingPoliciesUpload|designatedCommunityUpload|mandateUpload|xcdlDescription|record">
	<xsl:variable name="name" select="local-name()" />
    <xsl:element name="{$name}">
    	<!-- do not copy attribute key of xcdlDescription -->
	    <xsl:copy-of select="@*[not(($name = 'xcdlDescription') and (local-name() = 'key'))]"/>
        <xsl:apply-templates select="data"/>
        <xsl:apply-templates select="xcdlDescription"/>
        <xsl:apply-templates select="jhoveXML"/>
        <xsl:apply-templates select="formatInfo"/>
        <xsl:apply-templates select="changelog"/>
        <xsl:apply-templates select="*[not(local-name() = 'data') and not(local-name() = 'formatInfo') and not(local-name() = 'xcdlDescription') and not(local-name() = 'jhoveXML') and not(local-name() = 'changelog')]"/>
    </xsl:element>
</xsl:template>

<!-- rename experiment/uploads/upload to experiment/results/result and sort elements -->
<xsl:template match="experiment/uploads">
    <xsl:element name="results">
        <xsl:for-each select="upload">
		    <xsl:element name="result">
			    <xsl:apply-templates select="@*"/>
			    <xsl:apply-templates select="data"/>
    	    	<xsl:apply-templates select="../../xcdlDescriptions/xcdlDescription[@key = current()/@key]"></xsl:apply-templates>
			    <xsl:apply-templates select="jhoveXML"/>
			    <xsl:apply-templates select="formatInfo"/>
			    <xsl:apply-templates select="changelog"/>
		    </xsl:element>
	    </xsl:for-each>
    </xsl:element>
</xsl:template>

<!-- do not copy experiment/xcdlDescriptions, they have moved to experiment/results -->
<xsl:template match="experiment/xcdlDescriptions"/>

<!-- rename xclMapping to measurementMapping -->
<xsl:template match="xclMapping">
	<measurementMapping>
		<xsl:apply-templates />
	</measurementMapping>
</xsl:template>

<!-- legacy rule: serviceLocator => actionIdentifier 
     transformation are applied in V2.0-to-V2.1.xsl, too
     but plans exported with version 2.1 had still this out dated attribute  -->
<xsl:template match="action/@servicelocator">
	<xsl:choose>  
		<xsl:when test="not(@actionIdentifier)">
		<xsl:attribute name="actionIdentifier">
			<xsl:value-of select="(.)" />
		</xsl:attribute>
		</xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template match="leaf|node">
	<xsl:element name="{local-name()}">
		<xsl:copy-of select="@*[not (local-name() = 'description')]"/>
		<xsl:choose>
		<xsl:when test="not(string(description))">
			<description>
				<xsl:value-of select="@description"/>
			</description>
		</xsl:when>
		</xsl:choose>
		<xsl:apply-templates select="*|text()"/>
	</xsl:element>
</xsl:template>

</xsl:stylesheet>