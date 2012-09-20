<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V3.9.0 to V3.9.9
   ==========================================================
   Changes:
      * changes default namespace to ifs.tuwien.ac.at/dp/plato/
      * criterion has now an ID, other content is only there for documentation
      * rename subject to evaluationScope
      * remove empty value elements of *Result
      
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
    exclude-result-prefixes="java xalan oldplato oldwdt"
    >

<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:preserve-space elements="*"/>

<xsl:template match="text()|@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<!-- changes default namespace to ifs.tuwien.ac.at/dp/plato/ -->
<!-- [namespace-uri() = 'http://www.planets-project.eu/plato'] -->
 <xsl:template match="oldplato:*">
     <xsl:element name="{local-name()}" namespace="http://ifs.tuwien.ac.at/dp/plato" >
       <xsl:apply-templates select="@* | node()"/>
     </xsl:element>
 </xsl:template>

<xsl:template match="oldplato:plans">
	<xsl:element name="{local-name()}" xmlns="http://ifs.tuwien.ac.at/dp/plato" namespace="http://ifs.tuwien.ac.at/dp/plato" >
		<xsl:attribute name="xsi:schemaLocation">http://ifs.tuwien.ac.at/dp/plato plato-3.9.9.xsd</xsl:attribute>
		<xsl:attribute name="version">3.9.9</xsl:attribute>
    	<xsl:apply-templates/>
    	
    </xsl:element>
</xsl:template>
<!--  
 <xsl:template match="@xsi:schemaLocation">
 	<xsl:attribute name="xsi:schemaLocation">http://ifs.tuwien.ac.at/dp/plato plato-4.0.0.xsd</xsl:attribute>
 </xsl:template>
 -->
 <!-- 
<xsl:template match="oldplato:plans/@version">
    <xsl:attribute name="version">4.0.0</xsl:attribute>
</xsl:template>
-->
<!-- criterion has now an ID, other content is only there for documentation -->
<xsl:template match="oldplato:criterion">
	<xsl:if test="./oldplato:property/oldplato:category/text()">
	<xsl:element name="{local-name()}" namespace="http://ifs.tuwien.ac.at/dp/plato" >
	    <xsl:variable name="schema" select="substring-before(./oldplato:property/oldplato:category/text(), ':')"/>
	    <xsl:variable name="part">
	       <xsl:call-template name="append_non_empty">
	       		<xsl:with-param name="content" select="substring-after(./oldplato:property/oldplato:category/text(),':')"/>
	       		<xsl:with-param name="suffix" select="'/'"/>
	       </xsl:call-template>
	    </xsl:variable>
	    <xsl:variable name="metric">
	    	<xsl:call-template name="append_non_empty">
	    		<xsl:with-param name="content" select="./oldplato:metric/oldplato:metricId/text()"/>
	    		<xsl:with-param name="prefix" select="'#'"/>
	    	</xsl:call-template>
	    </xsl:variable>
        <xsl:attribute name="ID"> <xsl:value-of select="concat($schema, '://', $part, ./oldplato:property/oldplato:propertyId/text(), $metric)"/> </xsl:attribute>
		   	
      <xsl:apply-templates select="*"/>
	</xsl:element>
	</xsl:if>
</xsl:template>

<!-- util: adds prefix and suffix, only if content is not empty  -->
<xsl:template name="append_non_empty" >
	<xsl:param name="content" select="''"/>
	<xsl:param name="prefix" select="''"/>
	<xsl:param name="suffix" select="''"/>
	<xsl:if test="$content != ''">
		<xsl:value-of select="concat($prefix, $content, $suffix)"></xsl:value-of>
	</xsl:if>
</xsl:template>

<!-- rename subject to evaluationScope -->
<xsl:template match="oldplato:subject">
	<xsl:if test="text() and text() != 'null'">
		<xsl:element name="evaluationScope" namespace="http://ifs.tuwien.ac.at/dp/plato">
				<xsl:apply-templates />
		</xsl:element>
	</xsl:if>
</xsl:template>

<!-- remove empty value elements of *Result --> 
<xsl:template match="*[substring(name(), string-length(name())-5 ) = 'Result']/oldplato:value" >
	<xsl:if test="text()">
		<xsl:element name="value" namespace="http://ifs.tuwien.ac.at/dp/plato">
			<xsl:value-of select="text()"></xsl:value-of>
		</xsl:element>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>