<!-- 
   ==========================================================
   Stylesheet for extracting a template file from  Plan V3.0.1
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
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns="http://www.planets-project.eu/plato"
    xmlns:plato="http://www.planets-project.eu/plato"
    xsi:schemaLocation="http://www.planets-project.eu/plato ../schemas/plato-3.0.xsd"
    xmlns:fits="http://hul.harvard.edu/ois/xml/ns/fits/fits_output"
    xmlns:wdt="http://www.planets-project.eu/wdt"
    exclude-result-prefixes="java xalan fits wdt xsi">

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

<!-- remove all unsed namespaces 
     found here: http://stackoverflow.com/questions/4593326/xsl-how-to-remove-unused-namespaces-from-source-xml -->
<xsl:template match="node()|@*" priority="-2">
     <xsl:copy>
       <xsl:apply-templates select="node()|@*"/>
     </xsl:copy>
 </xsl:template>

 <xsl:template match="*">
  <xsl:element name="{name()}" namespace="{namespace-uri()}">
   <xsl:variable name="vtheElem" select="."/>

   <xsl:for-each select="namespace::*">
     <xsl:variable name="vPrefix" select="name()"/>

     <xsl:if test=
      "$vtheElem/descendant::*
              [namespace-uri()=current()
             and
              substring-before(name(),':') = $vPrefix
             or
              @*[substring-before(name(),':') = $vPrefix]
              ]
      ">
      <xsl:copy-of select="."/>
     </xsl:if>
   </xsl:for-each>
   <xsl:apply-templates select="node()|@*"/>
  </xsl:element>
 </xsl:template>
 
<xsl:template match="plato:plans">
<xsl:element name="templates">
    <xsl:apply-templates  select="plato:plan/plato:tree"/>
</xsl:element>
</xsl:template>

<xsl:template name="objective-tree" match="plato:tree">
<xsl:element name="template">
        <xsl:attribute name="name"><xsl:value-of select="plato:node/@name" /></xsl:attribute>
        <xsl:apply-templates select="plato:node/plato:node"/>
</xsl:element>
</xsl:template>

<xsl:template match="plato:evaluation"/>

</xsl:stylesheet>