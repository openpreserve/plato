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
 	xmlns:exsl="http://exslt.org/common"
    extension-element-prefixes="exsl"    
    xsi:schemaLocation="http://ifs.tuwien.ac.at/dp/plato plato-V4.xsd"
    exclude-result-prefixes="java xalan exsl">

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

<!-- remove eprintsPlan -->
<xsl:template match="plato:eprintsPlan">
<xsl:comment>
	<xsl:copy-of select="."></xsl:copy-of> 
</xsl:comment>
</xsl:template>


<xsl:template match="plato:criterion" >
<xsl:comment>
	<xsl:copy-of select="*"></xsl:copy-of> 
</xsl:comment>

<!-- 
    <xsl:variable name="critID" select="./@ID"/>
    criterion: <xsl:value-of select="$critID"></xsl:value-of>
	<xsl:copy-of xmlns="http://ifs.tuwien.ac.at/dp/plato" select="exsl:node-set($criteriaText)/lookup:criteria/lookup:criterion[@ID=$critID]/*" />
	
 -->	
</xsl:template>

    <xsl:variable name="formatter"
       select="java:java.text.SimpleDateFormat.new('yyyy-MM-ddHH:mm:ss')"/>

    <xsl:variable name="timestamp" 
        select="java:java.util.Date.new()" />

    <xsl:variable name="timestampstr" select="java:format($formatter, $timestamp)"/>

    <xsl:variable name="nowAsISO_8601str"
        select="concat(substring($timestampstr,1,10), 'T', substring($timestampstr,11,18))"/>


<xsl:template match="plato:criterion[@ID='outcome://object/image/height#equal']">
<measure ID="http://scape-project.eu/pw/vocab/measures/53">
<name>image height preserved</name>
<description>true iff image height has been preserved.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/19">
<name>image size</name>
<description>Dimensions of a digital image</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/width#equal']">
<measure ID="http://scape-project.eu/pw/vocab/measures/51">
<name>image width preserved</name>
<description>true iff image width has been preserved.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/19">
<name>image size</name>
<description>Dimensions of a digital image</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/colourSpace#equal']">
<measure ID="http://scape-project.eu/pw/vocab/measures/56">
<name>colour model preserved</name>
<description>Indicates whether the colour model has been preserved.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/20">
<name>colour space of a digital image</name>
<description>The designated colour space of the decompressed image data</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://effect/costs/archivalStorageQuantitative']">
<measure ID="http://scape-project.eu/pw/vocab/measures/137">
<name>quantitative archival storage costs</name>
<description>Effects of a preservation action on archival storage costs, expressed as archival storage costs per object over a time period of 10 years, measured in €.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/46">
<name>archival storage costs</name>
<description>Effects of a preservation action on archival storage costs</description>
<category ID="http://scape-project.eu/pw/vocab/categories/outcome_effect" scope="ALTERNATIVE_ACTION">
<name>Outcome effect</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://business/costs/execution/totalPerObject']">
<measure ID="http://scape-project.eu/pw/vocab/measures/21">
<name>Running costs per object</name>
<description>Running operational costs of an action in € per object.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/9">
<name>operational costs of a preservation action</name>
<description>These are the operational costs of running a preservation action.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/action_costs" scope="ALTERNATIVE_ACTION">
<name>Action costs</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://effect/automatedQAPossible']">
<measure ID="http://scape-project.eu/pw/vocab/measures/134">
<name>automated QA supported</name>
<description>Indicates whether results of an action support automated QA with current means</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/45">
<name>automated quality assurance support</name>
<description>Indicators whether the outcome of an action supports automated QA</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://performanceEfficiency/timeBehaviour/timePerMB']">
<measure ID="http://scape-project.eu/pw/vocab/measures/10">
<name>elapsed time per MB</name>
<description>elapsed processing time per Megabyte of input data, measured in milliseconds</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/6">
<name>processing time</name>
<description>processing time</description>
<category ID="http://scape-project.eu/pw/vocab/categories/time_behaviour" scope="ALTERNATIVE_ACTION">
<name>Time behaviour</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfTools']">
<measure ID="http://scape-project.eu/pw/vocab/measures/141">
<name>number of tools</name>
<description>Indicator for the adoption of a format, expressed as the total number of all tools that support a format</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/48">
<name>format adoption</name>
<description>Degree of adoption of a format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/disclosure/documentation/availability']">
<measure ID="http://scape-project.eu/pw/vocab/measures/147">
<name>format documentation availability</name>
<description>Availability of the documentation for a format</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/52">
<name>format documentation</name>
<description>Indicators for the documentation that is available for a format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="yes-free/yes-pay/no">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/transparency/compression']">
<measure ID="http://scape-project.eu/pw/vocab/measures/117">
<name>compression type</name>
<description>Type of compression used in the outcome object</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/39">
<name>compression</name>
<description>Compression used in outcome object.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<ordinalScale unit="" restriction="none/lossless/lossy">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/disclosure/identificationPossibilities']">
<measure ID="http://scape-project.eu/pw/vocab/measures/153">
<name>identification possibilities</name>
<description>Possibilies to identify the outcome format (cf. PRONOM)</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/55">
<name>format disclosure</name>
<description>Disclosure of the format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="automatic_specific/automatic_generic/automatic_tentative/manual/none">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/transparency/complexity']">
<measure ID="http://scape-project.eu/pw/vocab/measures/143">
<name>format complexity</name>
<description>Indicator of the complexity of a format as judged by experts in the domain</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/50">
<name>format transparency</name>
<description>How transparent is the format and its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="high/medium/low">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/relativeFileSize']">
<measure ID="http://scape-project.eu/pw/vocab/measures/123">
<name>comparative file size</name>
<description>Factor for relative output file size, calculated as: (size of output file / size of input file)</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/41">
<name>file size</name>
<description>What happened to the size of the file?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://reliability/maturity/stability']">
<measure ID="http://scape-project.eu/pw/vocab/measures/108">
<name>stability judgement</name>
<description>Judgement of the stability of an action </description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/35">
<name>stability</name>
<description>Stability of an action under normal operations</description>
<category ID="http://scape-project.eu/pw/vocab/categories/maturity" scope="ALTERNATIVE_ACTION">
<name>Maturity</name>
</category>
</attribute>
<ordinalScale unit="" restriction="stable/largely stable/unstable">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>


</xsl:stylesheet>