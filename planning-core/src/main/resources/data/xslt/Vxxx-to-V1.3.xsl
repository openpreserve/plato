<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V1.0 to V1.3
   ==========================================================
   Changes:

      * text properties which can hold more than one line of text are now stored as XML-elements
      * base64 encoded binary data is now stored as content of the data element
      * update version number to 1.3
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
  <xsl:preserve-space elements="*"/>
 
<xsl:template match="*|text()">
  <xsl:copy>
    <!-- It is not possible to add an attribute after elements were added to the parent node, so: -->
    <!-- First select all attributes, which are not transformed to XML-elements -->
    <xsl:apply-templates select="@*[(name() = 'identificationCode') or (name() = 'contentType') or (name() = 'alternativeName')]" />
    <!-- Then process all attributes which should be transormed -->
    <xsl:apply-templates select="@*[not(name() = 'identificationCode') and not(name() = 'contentType') and not (name() = 'alternativeName')]" />
    <!-- Finally copy the rest -->
    <xsl:apply-templates select="*|text()" />
  </xsl:copy>
</xsl:template>

<!-- The resulting XML file has format Plato 1.3 -->
<xsl:template match="project/@version|project/@version">
  <xsl:attribute name="{name()}">
    <xsl:value-of select="1.3" />
  </xsl:attribute>
</xsl:template>

<xsl:template match="@*">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
  </xsl:copy>
</xsl:template>

<!-- base64 encoded binary data is now stored as content of the data element -->
<xsl:template match="data/@value">
   <xsl:value-of select="." />
</xsl:template>

<!-- Turn all attributes that correspond to text properties which can hold more than one line of text to elements  -->
<xsl:template match="@description|@runDescription|@samplesDescription|@collectionID|@typeOfObjects|@numberOfObjects|@expectedGrowthRate|@reason|@actionNeeded|@comment|@documentTypes|@mandate|@reasoning|@reasonForConsidering|@configSettings|@necessaryResources|@planningPurpose|@designatedCommunity|@applyingPolicies|@organisationalProcedures|@preservationRights|@referenceToAgreements|@planRelations|@originalTechnicalEnvironment">
 <xsl:element name="{name()}">
   <xsl:value-of select="." />
</xsl:element>
</xsl:template>

</xsl:stylesheet>
