<!-- 
   ==========================================================
   Stylesheet to transform SPARQL results from http://rdf.myexperiment.org/sparql to 
   list of components
   ==========================================================
   ==========================================================

--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:sparql="http://www.w3.org/2005/sparql-results#"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:java="http://xml.apache.org/xalan/java"
    exclude-result-prefixes="java xalan xsi sparql">

<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:preserve-space elements="*"/>

<!--
<xsl:template match="*|text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>
-->
<xsl:template match="/sparql:sparql/sparql:results">
	<components>
		<xsl:apply-templates/>
	</components>
</xsl:template>

<xsl:template match="/sparql:sparql/sparql:results/sparql:result">
	<component>
		<xsl:apply-templates/>
	</component>
</xsl:template>

<xsl:template match="sparql:binding[@name='w']">
	<uri>
		<xsl:value-of select="./sparql:uri"></xsl:value-of>
	</uri>
</xsl:template>

<xsl:template match="sparql:binding[@name='wt']">
	<title>
		<xsl:value-of select="./sparql:literal"></xsl:value-of>
	</title>
</xsl:template>
<xsl:template match="sparql:binding[@name='wdesc']">
	<description>
		<xsl:value-of select="./sparql:literal"></xsl:value-of>
	</description>
</xsl:template>


</xsl:stylesheet>