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

<xsl:template match="plato:measurement">
<xsl:element name="measurement">
	<xsl:attribute name="measureId">
		<xsl:value-of select="plato:property/@name"></xsl:value-of>
	</xsl:attribute>
	<xsl:apply-templates select="*[local-name() != 'property' ]"/>
</xsl:element>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#equal']">
<measure ID="http://scape-project.eu/pw/vocab/measures/2">
<name>image content is equal</name>
<description>true iff two images are identical, false otherwise</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/1">
<name>image content</name>
<description>The accuracy of image content preservation</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/tiff/data#retained']">
<measure ID="http://scape-project.eu/pw/vocab/measures/251">
<name>EXIF: all tiff data retained</name>
<description/>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/2">
<name>image EXIF metadata</name>
<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/imageHeight#retained']">
<measure ID="http://scape-project.eu/pw/vocab/measures/294">
<name>image height metadata element retained</name>
<description>Image height metadata element has been retained.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/5">
<name>image metadata</name>
<description>The accuracy of metadata of an image.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/imageWidth#retained']">
<measure ID="http://scape-project.eu/pw/vocab/measures/293">
<name>image width metadata element retained</name>
<description>Image width metadata element has been retained.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/5">
<name>image metadata</name>
<description>The accuracy of metadata of an image.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>


<xsl:template match="plato:criterion[@ID='outcome://object/image/bitsPerSample#equal']">
<measure ID="http://scape-project.eu/pw/vocab/measures/169">
<name>bits per sample equal</name>
<description>Bits per component (channel) for each pixel</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/61">
<name>colour depth</name>
<description>Bits per component (channel) for each pixel</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/ocrText#equal']">
<measure ID="http://scape-project.eu/pw/vocab/measures/168">
<name>OCR text retained</name>
<description>Indicates whether the OCR text extracted from an image has been retained.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/60">
<name>OCR Text</name>
<description>OCR text extracted from an image.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adoption/toolSupport/ocrPossible']">
<measure ID="http://scape-project.eu/pw/vocab/measures/157">
<name>format supported by OCR software</name>
<description>Indicates whether tools exists which can apply optical character recognition directly to the outcome format</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/57">
<name>format viewing features support</name>
<description>Are specific features in principle supported by the format when viewed in common viewers? (e.g. easy PDF creation, zoom etc)</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>


<xsl:template match="plato:criterion[@ID='outcome://object/image/iccProfile#equal']">
<measure ID="http://scape-project.eu/pw/vocab/measures/58">
<name>colour profile retained</name>
<description>Indicates whether the colour profile has been retained.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/21">
<name>colour profile of a digital image</name>
<description>The colour profile associated with the digital image.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

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
<positiveIntegerScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveIntegerScale>
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

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/stability/standardization']">
<measure ID="http://scape-project.eu/pw/vocab/measures/161">
<name>format standardization</name>
<description>Standardization of the outcome format.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/49">
<name>format stability</name>
<description>Degree of stability of a format and its versions</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="international standard/de facto standard/none">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/business/licenceCostsForUse']">
<measure ID="http://scape-project.eu/pw/vocab/measures/155">
<name>format licence costs incurred</name>
<description>Indicates whether licence costs occur at using the outcome format.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/58">
<name>format licence costs</name>
<description>Which licence costs are incurred when using a certain format?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_business_factors" scope="ALTERNATIVE_ACTION">
<name>Format business factors</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>


<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/image/pdfCreationPossible']">
<measure ID="http://scape-project.eu/pw/vocab/measures/167">
<name>format facilitates PDF creation</name>
<description>Indicates whether it is commonly easy to create a pdf from the format.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/57">
<name>format viewing features support</name>
<description>Are specific features in principle supported by the format when viewed in common viewers? (e.g. easy PDF creation, zoom etc)</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://business/costs/setup/staffExternalExpertiseNeeded']">
<measure ID="http://scape-project.eu/pw/vocab/measures/27">
<name>Costs of external expertise for action setup</name>
<description>Cost for any external expertise required to setup an action (in €)</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/8">
<name>setup costs of a preservation action</name>
<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time license fees, installation and configuration, etc.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/action_costs" scope="ALTERNATIVE_ACTION">
<name>Action costs</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://business/costs/setup/hardwareInitial']">
<measure ID="http://scape-project.eu/pw/vocab/measures/22">
<name>Initial hardware costs</name>
<description>Initial hardware costs arising when deploying an action (e.g. new hardware needs to be acquired to get software running) measured in €</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/8">
<name>setup costs of a preservation action</name>
<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time license fees, installation and configuration, etc.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/action_costs" scope="ALTERNATIVE_ACTION">
<name>Action costs</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://business/costs/setup/softwareLicenceInitial']">
<measure ID="http://scape-project.eu/pw/vocab/measures/26">
<name>Initial software licence costs</name>
<description>Initial software licence costs of an action, in €</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/8">
<name>setup costs of a preservation action</name>
<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time license fees, installation and configuration, etc.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/action_costs" scope="ALTERNATIVE_ACTION">
<name>Action costs</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://business/costs/execution/softwareLicenceRunning']">
<measure ID="http://scape-project.eu/pw/vocab/measures/18">
<name>Running licence costs per object</name>
<description>Software licence costs for running an action on one object (in € per object)</description>
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

<xsl:template match="plato:criterion[@ID='action://business/costs/execution/softwareDevelopNewFeatures']">
<measure ID="http://scape-project.eu/pw/vocab/measures/16">
<name>feature development costs</name>
<description>Costs for developing new features which are not available in the software but required for successful operations (€)</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/8">
<name>setup costs of a preservation action</name>
<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time license fees, installation and configuration, etc.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/action_costs" scope="ALTERNATIVE_ACTION">
<name>Action costs</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://business/costs/setup/other']">
<measure ID="http://scape-project.eu/pw/vocab/measures/23">
<name>Other initial setup costs</name>
<description>Setup costs of an action which do not fit to any other given setup costs, in €</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/8">
<name>setup costs of a preservation action</name>
<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time license fees, installation and configuration, etc.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/action_costs" scope="ALTERNATIVE_ACTION">
<name>Action costs</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/retainFilename']">
<measure ID="http://scape-project.eu/pw/vocab/measures/66">
<name>retain original file name</name>
<description>Indicates if an action is able to retain the filename of the input-file for the output-file.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/26">
<name>file name handling</name>
<description>Indicators for how the preservation action handles file names</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_completeness" scope="ALTERNATIVE_ACTION">
<name>Functional completeness</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://compatibility/interoperability/interfaces/batchProcessingSupport']">
<measure ID="http://scape-project.eu/pw/vocab/measures/33">
<name>batchprocessing supported</name>
<description>Is there support for batch processing?</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/12">
<name>Software interface of a preservation action</name>
<description>The question of how to interface a preservation action tool, which can be a key factor limiting technical adoption in a given environment, but also drive or limit the scalability of operations where this action is involved (e.g. when no batch interface is provided)</description>
<category ID="http://scape-project.eu/pw/vocab/categories/interoperability" scope="ALTERNATIVE_ACTION">
<name>Interoperability</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://portability/installability/easeOfIntegrationInCurrentEnvironment']">
<measure ID="http://scape-project.eu/pw/vocab/measures/99">
<name>ease of integration</name>
<description>Assessment of how easy it is to integrate an action into a particular server environment.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/32">
<name>server environment integration</name>
<description>Feasibility of integration of an action into a server environment</description>
<category ID="http://scape-project.eu/pw/vocab/categories/installability" scope="ALTERNATIVE_ACTION">
<name>Installability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="good/acceptable/poor/not possible">
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

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adoption/toolSupport/nativeBrowserSupport']">
<measure ID="http://scape-project.eu/pw/vocab/measures/156">
<name>format supported by web browsers</name>
<description>Indicates whether the format can be natively displayed by browsers.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/48">
<name>format adoption</name>
<description>Degree of adoption of a format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adoption/ubiquity']">
<measure ID="http://scape-project.eu/pw/vocab/measures/162">
<name>format ubiquity</name>
<description>Ubiquity or popularity of the outcome format.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/48">
<name>format adoption</name>
<description>Degree of adoption of a format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="ubiquitous/widespread/specialised/obsolete">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>


<xsl:template match="plato:criterion[@ID='outcome://object/compression']">
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

<xsl:template match="plato:criterion[@ID='outcome://object/format/validAndWellformed']">
<measure ID="http://scape-project.eu/pw/vocab/measures/120">
<name>validity</name>
<description>Indicates whether the format of the object is valid.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/40">
<name>format conformity</name>
<description>Does the digital object conform to its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/rights']">
<measure ID="http://scape-project.eu/pw/vocab/measures/159">
<name>format IPR protection</name>
<description>Existence of rights applying to the outcome format.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/59">
<name>format rights</name>
<description>This covers the question of openness and rights restrictions on a format: Do known IPR issues exist? Is the format copyrighted by one company? Are there known patents or other rights?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="open/ipr_protected/proprietary">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/stability/speedOfChange']">
<measure ID="http://scape-project.eu/pw/vocab/measures/160">
<name>format speed of change</name>
<description>Stability of the outcome format with respect to changes.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/49">
<name>format stability</name>
<description>Degree of stability of a format and its versions</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="less than 1 year/1-2 years/3-5 years/more than 5 years">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfFreeOpenSourceTools']">
<measure ID="http://scape-project.eu/pw/vocab/measures/139">
<name>number of free tools that are open source</name>
<description>Indicator for the adoption of a format, expressed as the number of free tools that are open source  that support a format</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/48">
<name>format adoption</name>
<description>Degree of adoption of a format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<positiveIntegerScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveIntegerScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfFreeNotOpenSourceTools']">
<measure ID="http://scape-project.eu/pw/vocab/measures/138">
<name>number of free tools that are not open source</name>
<description>Indicator for the adoption of a format, expressed as the number of free tools that are not open source that support a format</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/48">
<name>format adoption</name>
<description>Degree of adoption of a format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<positiveIntegerScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveIntegerScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfProprietaryTools']">
<measure ID="http://scape-project.eu/pw/vocab/measures/140">
<name>number of proprietary tools</name>
<description>Indicator for the adoption of a format, expressed as the number of proprietary tools that support a format</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/48">
<name>format adoption</name>
<description>Degree of adoption of a format</description>
<category ID="http://scape-project.eu/pw/vocab/categories/format_sustainability" scope="ALTERNATIVE_ACTION">
<name>Format sustainability</name>
</category>
</attribute>
<positiveIntegerScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveIntegerScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/xmp/validExif']">
<measure ID="http://scape-project.eu/pw/vocab/measures/128">
<name>valid EXIF metadata</name>
<description>Indicates if XMP-metadata contains valid EXIF data.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/44">
<name>validity of XMP metadata</name>
<description>Does XMP metadata conform to its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/xmp/validIptc']">
<measure ID="http://scape-project.eu/pw/vocab/measures/130">
<name>valid IPTC metadata</name>
<description>Indicates if  XMP-metadata contains valid IPTC data.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/44">
<name>validity of XMP metadata</name>
<description>Does XMP metadata conform to its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/xmp/validDc']">
<measure ID="http://scape-project.eu/pw/vocab/measures/131">
<name>valid Doublin Core (DC) metadata</name>
<description>Indicates if XMP-metadata contains valid Dublin Core data.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/44">
<name>validity of XMP metadata</name>
<description>Does XMP metadata conform to its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/METS/validDcMods']">
<measure ID="http://scape-project.eu/pw/vocab/measures/126">
<name>valid DC/MODS</name>
<description>Indicates if  METS-metadata contains valid DC/MODS data.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/43">
<name>validity of METS metadata</name>
<description>Does METS metadata conform to its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/METS/validMix']">
<measure ID="http://scape-project.eu/pw/vocab/measures/127">
<name>valid MIX metadata</name>
<description>Indicates if  METS-metadata contains valid MIX data.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/43">
<name>validity of METS metadata</name>
<description>Does METS metadata conform to its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/METS/validPremis']">
<measure ID="http://scape-project.eu/pw/vocab/measures/129">
<name>valid PREMIS metadata</name>
<description>Indicates if  METS-metadata contains valid PREMIS data.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/43">
<name>validity of METS metadata</name>
<description>Does METS metadata conform to its specification?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/image/zoomPossible']">
<measure ID="http://scape-project.eu/pw/vocab/measures/154">
<name>format facilitates zooming</name>
<description>Indicates where the format provides specific support for zooming</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/57">
<name>format viewing features support</name>
<description>Are specific features in principle supported by the format when viewed in common viewers? (e.g. easy PDF creation, zoom etc)</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://effect/costs/archivalStorage']">
<measure ID="http://scape-project.eu/pw/vocab/measures/135">
<name>comparative archival storage costs</name>
<description>Effects of a preservation action on archival storage costs: Do they increase or decrease?</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/46">
<name>archival storage costs</name>
<description>Effects of a preservation action on archival storage costs</description>
<category ID="http://scape-project.eu/pw/vocab/categories/outcome_effect" scope="ALTERNATIVE_ACTION">
<name>Outcome effect</name>
</category>
</attribute>
<ordinalScale unit="" restriction="increase/unchanged/decrease">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://effect/costs/preservationWatchAndPlanning']">
<measure ID="http://scape-project.eu/pw/vocab/measures/136">
<name>comparative preservation watch and planning costs</name>
<description>Effects of a preservation action on preservation watch and planning costs: Do they increase or decrease?</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/47">
<name>preservation watch and planning costs</name>
<description>Effects of a preservation action on preservation watch and planning costs</description>
<category ID="http://scape-project.eu/pw/vocab/categories/outcome_effect" scope="ALTERNATIVE_ACTION">
<name>Outcome effect</name>
</category>
</attribute>
<ordinalScale unit="" restriction="increase/unchanged/decrease">
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

<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/activityLoggingAmount']">
<measure ID="http://scape-project.eu/pw/vocab/measures/37">
<name>Amount of logging</name>
<description>Number of characters of activity log produced upon running an action on one content object</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/13">
<name>Activity logging</name>
<description>This represents the common requirement of knowing as precisely as possible what a preservation action tool did and how, i.e. how much reporting happens on the side of a preservation action about the activities that have taken place. This includes both the amount of information procuded as well as the format (e.g. whether this is human-readable, machine-processible or in PREMIS format)</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_completeness" scope="ALTERNATIVE_ACTION">
<name>Functional completeness</name>
</category>
</attribute>
<positiveIntegerScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveIntegerScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://maintainability/modifiability/customization']">
<measure ID="http://scape-project.eu/pw/vocab/measures/93">
<name>ease of customization</name>
<description>How easy is it to customize an action?</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/28">
<name>customization</name>
<description>Indicators for the degree to which it is possible to customize the action-tool according to specific user needs</description>
<category ID="http://scape-project.eu/pw/vocab/categories/modifiability" scope="ALTERNATIVE_ACTION">
<name>Modifiability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="good/acceptable/poor/not possible">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://usability/operability/easeOfUse']">
<measure ID="http://scape-project.eu/pw/vocab/measures/115">
<name>ease of use</name>
<description>Assessment of how easy it is to use an action in operations</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/38">
<name>ease of operations</name>
<description>How easy are operations with this action?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/operability" scope="ALTERNATIVE_ACTION">
<name>Operability</name>
</category>
</attribute>
<ordinalScale unit="" restriction="good/acceptable/poor">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</ordinalScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/outputFileFormatVerification']">
<measure ID="http://scape-project.eu/pw/vocab/measures/64">
<name>output object validation performed</name>
<description>Indicates if the action validates the output-object after processing.</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/24">
<name>output object validation</name>
<description>Does the action perform validation of the output?</description>
<category ID="http://scape-project.eu/pw/vocab/categories/functional_completeness" scope="ALTERNATIVE_ACTION">
<name>Functional completeness</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='action://business/costs/setup/softwareIntegration']">
<measure ID="http://scape-project.eu/pw/vocab/measures/25">
<name>Initial integration costs</name>
<description>Costs for integrating an action in the current environment, in €</description>
<attribute ID="http://scape-project.eu/pw/vocab/attributes/8">
<name>setup costs of a preservation action</name>
<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time license fees, installation and configuration, etc.</description>
<category ID="http://scape-project.eu/pw/vocab/categories/action_costs" scope="ALTERNATIVE_ACTION">
<name>Action costs</name>
</category>
</attribute>
<positiveFloatScale unit="" restriction="">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</positiveFloatScale>
</measure>
</xsl:template>

</xsl:stylesheet>