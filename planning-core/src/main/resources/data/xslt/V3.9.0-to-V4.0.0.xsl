<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V3.9.0 to V4.0.0
   ==========================================================
   Changes:
      * changes default namespace to ifs.tuwien.ac.at/dp/plato/
      
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
    xsi:schemaLocation="http://ifs.tuwien.ac.at/dp/plato plato-4.0.0.xsd"
    xmlns:oldwdt="http://www.planets-project.eu/wdt"
    xmlns:oldplato="http://www.planets-project.eu/plato"
    exclude-result-prefixes="java xalan ">

<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:preserve-space elements="*"/>

<xsl:template match="text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>
<!-- [namespace-uri() = 'http://www.planets-project.eu/plato'] -->
 <xsl:template match="oldplato:*">
     <xsl:element name="{local-name()}" namespace="http://ifs.tuwien.ac.at/dp/plato" >
       <xsl:apply-templates select="@* | node()"/>
     </xsl:element>
 </xsl:template>
 
 <xsl:template match="@xsi:schemaLocation">
 	<xsl:attribute name="xsi:schemaLocation">http://ifs.tuwien.ac.at/dp/plato plato-4.0.0.xsd</xsl:attribute>
 </xsl:template>
 
<xsl:template match="oldplato:plans/@version">
    <xsl:attribute name="version">4.0.0</xsl:attribute>
</xsl:template>

<xsl:template match="oldplato:criterion">
	<xsl:element name="{local-name()}" namespace="http://ifs.tuwien.ac.at/dp/plato" >
	    <xsl:variable name="schema" select="substring-before(concat(oldplato:property/oldplato:category, ':'),':')"/>
	    <xsl:variable name="part">
	       <xsl:call-template name="append_non_empty">
	       		<xsl:with-param name="content" select="substring-after(oldplato:property/oldplato:category,':')"/>
	       		<xsl:with-param name="suffix" select="'/'"/>
	       </xsl:call-template>
	    </xsl:variable>
	    <xsl:variable name="metric">
	    	<xsl:call-template name="append_non_empty">
	    		<xsl:with-param name="content" select="oldplato:metric/oldplato:metricId"/>
	    		<xsl:with-param name="prefix" select="'#'"/>
	    	</xsl:call-template>
	    </xsl:variable>

        <xsl:attribute name="ID"> <xsl:value-of select="concat($schema, '://', $part, oldplato:property/oldplato:propertyId, $metric)"/> </xsl:attribute>
		   	
      <xsl:apply-templates select="*"/>
	</xsl:element>
</xsl:template>

<xsl:template name="append_non_empty" >
	<xsl:param name="content" select="''"/>
	<xsl:param name="prefix" select="''"/>
	<xsl:param name="suffix" select="''"/>
	<xsl:if test="$content != ''">
		<xsl:value-of select="concat($prefix, $content, $suffix)"></xsl:value-of>
	</xsl:if>
</xsl:template>
 


</xsl:stylesheet>