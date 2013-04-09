<!-- 
   ==========================================================
   Stylesheet for migration of Plato XML format: V3.9.9 to V4.0.1
   ==========================================================
   Changes:
      * measure and attributes based on the new quality model 
        replace criterion, measureableproperty, and metric
      * remove eprintsPlan 
      * new vocabulary for quality model, based on PURLs 
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
	<plans xsi:schemaLocation="http://ifs.tuwien.ac.at/dp/plato plato-V4.xsd" version="4.0.1">
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

<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/tiff/data#retained']">
<measure ID="http://purl.org/DP/quality/measures#251">
<name>EXIF: all tiff data retained</name>
<description/>
<attribute ID="http://purl.org/DP/quality/attributes#2">
<name>image EXIF metadata</name>
<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
<name>Functional correctness: Transformation Independent Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>


<xsl:template match="plato:criterion[@ID='outcome://object/format/validAndWellformed']">
<measure ID="http://purl.org/DP/quality/measures#120">
<name>validity</name>
<description>Indicates whether the format of the object is valid.</description>
<attribute ID="http://purl.org/DP/quality/attributes#40">
<name>format conformity</name>
<description>Does the digital object conform to its specification?</description>
<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
<name>Functional correctness: Representation Instance Property</name>
</category>
</attribute>
<booleanScale unit="" restriction="Yes/No">
<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
</booleanScale>
</measure>
</xsl:template>

<xsl:template match="plato:criterion[@ID='outcome://object/document/content/font/textMarks#equal']">
<measure ID="http://purl.org/DP/quality/measures#359">
	<name>document font text marks</name>
	<description>Indicates if font text marks exist in the document.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#64">
		<name>document content</name>
		<description>Information on content stored in document</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- end of manually created mappings -->

<!-- no old property for measure /235 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/dataDictionary/quality']">
<measure ID="http://purl.org/DP/quality/measures#236">
	<name>quality of data dictionary</name>
	<description>Quality of the present data dictionary.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#83">
		<name>available metadata for database</name>
		<description>Different metadata available for database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not available/good/acceptable/bad" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/dataModel/present']">
<measure ID="http://purl.org/DP/quality/measures#237">
	<name>presence of data model</name>
	<description>Indicates whether a data model is present in the database.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#83">
		<name>available metadata for database</name>
		<description>Different metadata available for database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/dataModel/quality']">
<measure ID="http://purl.org/DP/quality/measures#238">
	<name>quality of data model</name>
	<description>Quality of the present data model.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#83">
		<name>available metadata for database</name>
		<description>Different metadata available for database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not available/good/acceptable/bad" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/dataModel/syntax']">
<measure ID="http://purl.org/DP/quality/measures#239">
	<name>syntax of data model</name>
	<description>Syntax used in the present data model.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#83">
		<name>available metadata for database</name>
		<description>Different metadata available for database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not available/ERD/UML/other" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/originalDatabaseProductPresent']">
<measure ID="http://purl.org/DP/quality/measures#240">
	<name>original database product</name>
	<description>Indicates whether the original database product is documented.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#83">
		<name>available metadata for database</name>
		<description>Different metadata available for database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /242 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/dataDictionary/present']">
<measure ID="http://purl.org/DP/quality/measures#243">
	<name>data dictionary is present</name>
	<description>Indicates whether the (external) data dictionary is present.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#83">
		<name>available metadata for database</name>
		<description>Different metadata available for database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/elementMetadataOrDataDictionaryPresent']">
<measure ID="http://purl.org/DP/quality/measures#244">
	<name>either metadata for each element or data dictionary present</name>
	<description>Indicates whether metadata is present for each element. This means that either the element metadata or the (external) data dictionary must be present.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#83">
		<name>available metadata for database</name>
		<description>Different metadata available for database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfFreeNotOpenSourceTools']">
<measure ID="http://purl.org/DP/quality/measures#138">
	<name>number of free tools that are not open source</name>
	<description>Indicator for the adoption of a format, expressed as the number of free tools that are not open source that support a format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#48">
		<name>format adoption</name>
		<description>Degree of adoption of a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfFreeOpenSourceTools']">
<measure ID="http://purl.org/DP/quality/measures#139">
	<name>number of free tools that are open source</name>
	<description>Indicator for the adoption of a format, expressed as the number of free tools that are open source  that support a format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#48">
		<name>format adoption</name>
		<description>Degree of adoption of a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfProprietaryTools']">
<measure ID="http://purl.org/DP/quality/measures#140">
	<name>number of proprietary tools</name>
	<description>Indicator for the adoption of a format, expressed as the number of proprietary tools that support a format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#48">
		<name>format adoption</name>
		<description>Degree of adoption of a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adaption/toolSupport/nrOfTools']">
<measure ID="http://purl.org/DP/quality/measures#141">
	<name>number of tools</name>
	<description>Indicator for the adoption of a format, expressed as the total number of all tools that support a format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#48">
		<name>format adoption</name>
		<description>Degree of adoption of a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adoption/toolSupport/nativeBrowserSupport']">
<measure ID="http://purl.org/DP/quality/measures#156">
	<name>format supported by web browsers</name>
	<description>Indicates whether the format can be natively displayed by browsers.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#48">
		<name>format adoption</name>
		<description>Degree of adoption of a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adoption/ubiquity']">
<measure ID="http://purl.org/DP/quality/measures#162">
	<name>format ubiquity</name>
	<description>Ubiquity or popularity of the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#48">
		<name>format adoption</name>
		<description>Degree of adoption of a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="ubiquitous/widespread/specialised/obsolete" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://reliability/maturity/sustainabilityIndicators/manufacturerStatus']">
<measure ID="http://purl.org/DP/quality/measures#107">
	<name>status of software vendor</name>
	<description>Business status of the action vendor</description>
	<attribute ID="http://purl.org/DP/quality/attributes#34">
		<name>software vendor</name>
		<description>Assessment of the vendor of an action (active, out of business, about to go bankrupt, unreliable...)</description>
		<category ID="http://purl.org/DP/quality/categories#action_maintenance" scope="ALTERNATIVE_ACTION">
			<name>Action maintenance</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="in business/no longer in same business/out of business" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/rights']">
<measure ID="http://purl.org/DP/quality/measures#159">
	<name>format IPR protection</name>
	<description>Existence of rights applying to the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#59">
		<name>format rights</name>
		<description>This covers the question of openness and rights restrictions on a format: Do known IPR issues exist? Is the format copyrighted by one company? Are there known patents or other rights?</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="open/ipr_protected/proprietary" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://effect/automatedQAPossible']">
<measure ID="http://purl.org/DP/quality/measures#134">
	<name>automated QA supported</name>
	<description>Indicates whether results of an action support automated QA with current means</description>
	<attribute ID="http://purl.org/DP/quality/attributes#45">
		<name>automated quality assurance support</name>
		<description>Indicators whether the outcome of an action supports automated QA</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /55 -->
<xsl:template match="plato:criterion[@ID='outcome://object/image/colourSpace#equal']">
<measure ID="http://purl.org/DP/quality/measures#56">
	<name>colour model preserved</name>
	<description>Indicates whether the colour model has been preserved.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#20">
		<name>colour space of a digital image</name>
		<description>The designated colour space of the decompressed image data</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /340 -->
<!-- no old property for measure /341 -->
<!-- no old property for measure /342 -->
<!-- no old property for measure /343 -->
<!-- no old property for measure /344 -->
<xsl:template match="plato:criterion[@ID='outcome://object/document/structure/numberOfPages#equal']">
<measure ID="http://purl.org/DP/quality/measures#345">
	<name>number of pages equal</name>
	<description>Indicates whether the documents page count is equal</description>
	<attribute ID="http://purl.org/DP/quality/attributes#65">
		<name>document structure</name>
		<description>Indicators for how well the structure of a multi-page document has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /346 -->
<!-- no old property for measure /347 -->
<!-- no old property for measure /348 -->
<!-- no old property for measure /349 -->
<!-- no old property for measure /350 -->
<!-- no old property for measure /393 -->
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/stability/backwardsCompatibility']">
<measure ID="http://purl.org/DP/quality/measures#142">
	<name>format backwards compatbility</name>
	<description>Indicates whether a format is backwards compatible to previous versions.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#49">
		<name>format stability</name>
		<description>Degree of stability of a format and its versions</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/stability/speedOfChange']">
<measure ID="http://purl.org/DP/quality/measures#160">
	<name>format speed of change</name>
	<description>Stability of the outcome format with respect to changes.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#49">
		<name>format stability</name>
		<description>Degree of stability of a format and its versions</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="less than 1 year/1-2 years/3-5 years/more than 5 years" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/stability/standardization']">
<measure ID="http://purl.org/DP/quality/measures#161">
	<name>format standardization</name>
	<description>Standardization of the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#49">
		<name>format stability</name>
		<description>Degree of stability of a format and its versions</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="international standard/de facto standard/none" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /132 -->
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/gps/data#retained']">
<measure ID="http://purl.org/DP/quality/measures#250">
	<name>EXIF: all gps data retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/tiff/data#retained']">
<measure ID="http://purl.org/DP/quality/measures#251">
	<name>EXIF: all tiff data retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/apertureValue#retained']">
<measure ID="http://purl.org/DP/quality/measures#252">
	<name>EXIF: aperture value retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/artist#retained']">
<measure ID="http://purl.org/DP/quality/measures#253">
	<name>EXIF: artist retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/bitsPerSample#retained']">
<measure ID="http://purl.org/DP/quality/measures#254">
	<name>EXIF: bits per sample retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/brightnessValue#retained']">
<measure ID="http://purl.org/DP/quality/measures#255">
	<name>EXIF: brightness value retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/cfaPattern#retained']">
<measure ID="http://purl.org/DP/quality/measures#256">
	<name>EXIF: cfa pattern retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/compression#retained']">
<measure ID="http://purl.org/DP/quality/measures#257">
	<name>EXIF: compression retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/contrast#retained']">
<measure ID="http://purl.org/DP/quality/measures#258">
	<name>EXIF: contrast retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/exposureBiasValue#retained']">
<measure ID="http://purl.org/DP/quality/measures#259">
	<name>EXIF: exposure bias value retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/exposureIndex#retained']">
<measure ID="http://purl.org/DP/quality/measures#260">
	<name>EXIF: exposure index retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/exposureMode#retained']">
<measure ID="http://purl.org/DP/quality/measures#261">
	<name>EXIF: exposure mode retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/exposureProgram#retained']">
<measure ID="http://purl.org/DP/quality/measures#262">
	<name>EXIF: exposure program retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/exposureTime#retained']">
<measure ID="http://purl.org/DP/quality/measures#263">
	<name>EXIF: exposure time retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /264 -->
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/flash#retained']">
<measure ID="http://purl.org/DP/quality/measures#265">
	<name>EXIF: flash retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/focalLenghtIn35mmFilm']">
<measure ID="http://purl.org/DP/quality/measures#266">
	<name>EXIF: focal lenght in 35mm film retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/focalLength#retained']">
<measure ID="http://purl.org/DP/quality/measures#267">
	<name>EXIF: focal length retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/imageDescription#retained']">
<measure ID="http://purl.org/DP/quality/measures#268">
	<name>EXIF: image description retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/imageHeight#retained']">
<measure ID="http://purl.org/DP/quality/measures#269">
	<name>EXIF: image height retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/imageWidth#retained']">
<measure ID="http://purl.org/DP/quality/measures#270">
	<name>EXIF: image width retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/isoSpeed#retained']">
<measure ID="http://purl.org/DP/quality/measures#271">
	<name>EXIF: iso-speed value retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/lightSource#retained']">
<measure ID="http://purl.org/DP/quality/measures#272">
	<name>EXIF: light source retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/make#retained']">
<measure ID="http://purl.org/DP/quality/measures#273">
	<name>EXIF: make retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/meteringMode#retained']">
<measure ID="http://purl.org/DP/quality/measures#274">
	<name>EXIF: metering mode retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/model#retained']">
<measure ID="http://purl.org/DP/quality/measures#275">
	<name>EXIF: model retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/oECF#retained']">
<measure ID="http://purl.org/DP/quality/measures#276">
	<name>EXIF: oECF retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/photometricInterpretation#retained']">
<measure ID="http://purl.org/DP/quality/measures#277">
	<name>EXIF: photometric interpretation retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/resolutionUnit#retained']">
<measure ID="http://purl.org/DP/quality/measures#278">
	<name>EXIF: resolution unit retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/samplesPerPixel#retained']">
<measure ID="http://purl.org/DP/quality/measures#279">
	<name>EXIF: samples per pixel retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/saturation#retained']">
<measure ID="http://purl.org/DP/quality/measures#280">
	<name>EXIF: saturation retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/sensingMethod#retained']">
<measure ID="http://purl.org/DP/quality/measures#281">
	<name>EXIF: sensing method retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/sharpness#retained']">
<measure ID="http://purl.org/DP/quality/measures#282">
	<name>EXIF: sharpness retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/shutterSpeedValue#retained']">
<measure ID="http://purl.org/DP/quality/measures#283">
	<name>EXIF: shutter speed value retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/ifd0/software#retained']">
<measure ID="http://purl.org/DP/quality/measures#284">
	<name>EXIF: software retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/spectralSensitivity#retained']">
<measure ID="http://purl.org/DP/quality/measures#285">
	<name>EXIF: spectral sensivity retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/subjectDistance#retained']">
<measure ID="http://purl.org/DP/quality/measures#286">
	<name>EXIF: subject distance retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/exifVersion#retained']">
<measure ID="http://purl.org/DP/quality/measures#287">
	<name>EXIF: version retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/exif/pictureTakingConditions/whiteBalance#retained']">
<measure ID="http://purl.org/DP/quality/measures#288">
	<name>EXIF: white balance retained</name>
	<description>Indicates whether the specific type of EXIF metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#2">
		<name>image EXIF metadata</name>
		<description>The accuracy of Exchangeable Image File Format (EXIF) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /289 -->
<!-- no old property for measure /390 -->
<!-- no old property for measure /391 -->
<!-- no old property for measure /392 -->
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/image/zoomPossible']">
<measure ID="http://purl.org/DP/quality/measures#154">
	<name>format facilitates zooming</name>
	<description>Indicates where the format provides specific support for zooming</description>
	<attribute ID="http://purl.org/DP/quality/attributes#57">
		<name>format viewing features support</name>
		<description>Are specific features in principle supported by the format when viewed in common viewers? (e.g. easy PDF creation, zoom etc)</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/adoption/toolSupport/ocrPossible']">
<measure ID="http://purl.org/DP/quality/measures#157">
	<name>format supported by OCR software</name>
	<description>Indicates whether tools exists which can apply optical character recognition directly to the outcome format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#57">
		<name>format viewing features support</name>
		<description>Are specific features in principle supported by the format when viewed in common viewers? (e.g. easy PDF creation, zoom etc)</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/technicalProtectionMechanisms/usageRestrictionsPossible']">
<measure ID="http://purl.org/DP/quality/measures#163">
	<name>format supports usage restrictions</name>
	<description>Indicates whether usage restrictions can be applied to the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#57">
		<name>format viewing features support</name>
		<description>Are specific features in principle supported by the format when viewed in common viewers? (e.g. easy PDF creation, zoom etc)</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/image/pdfCreationPossible']">
<measure ID="http://purl.org/DP/quality/measures#167">
	<name>format facilitates PDF creation</name>
	<description>Indicates whether it is commonly easy to create a pdf from the format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#57">
		<name>format viewing features support</name>
		<description>Are specific features in principle supported by the format when viewed in common viewers? (e.g. easy PDF creation, zoom etc)</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /62 -->
<xsl:template match="plato:criterion[@ID='outcome://object/image/orientation#equal']">
<measure ID="http://purl.org/DP/quality/measures#63">
	<name>image orientation preserved</name>
	<description>Indicates whether orientation of the image has been preserved</description>
	<attribute ID="http://purl.org/DP/quality/attributes#23">
		<name>image orientation</name>
		<description>This specifies the orientation of the image in relation to the placement of its rows and columns when it was saved to disk i.e. it denotes whether the image has been rotated or flipped.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#ssimSimple']">
<measure ID="http://purl.org/DP/quality/measures#1">
	<name>image distance SSIM</name>
	<description>Distance between two images measured using SSIM. See ....</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<floatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</floatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#equal']">
<measure ID="http://purl.org/DP/quality/measures#2">
	<name>image content is equal</name>
	<description>true iff two images are identical, false otherwise</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#rmse']">
<measure ID="http://purl.org/DP/quality/measures#3">
	<name>image distance RMSE</name>
	<description>Distance between two images measured using RMSE. See ...</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#ae']">
<measure ID="http://purl.org/DP/quality/measures#4">
	<name>image distance absolute error AE</name>
	<description>number of different pixels</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#mae']">
<measure ID="http://purl.org/DP/quality/measures#5">
	<name>image distance mean absolute error MAE</name>
	<description>average channel error distance</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#mse']">
<measure ID="http://purl.org/DP/quality/measures#6">
	<name>image distance mean error squared MSE</name>
	<description>average of the channel error squared</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#pae']">
<measure ID="http://purl.org/DP/quality/measures#7">
	<name>image distance peak absolute error PAE</name>
	<description>The highest difference of any single pixel</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/similarity#psnr']">
<measure ID="http://purl.org/DP/quality/measures#8">
	<name>image peak signal to noise ratio PSNR</name>
	<description>The ratio of mean square difference to the maximum mean square that can exist between any two images</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/aspectRatio#equal']">
<measure ID="http://purl.org/DP/quality/measures#9">
	<name>image aspect ratio retained</name>
	<description>Indicates whether the aspect ratio of an image has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#1">
		<name>image content</name>
		<description>The accuracy of image content preservation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/captureDevice#retained']">
<measure ID="http://purl.org/DP/quality/measures#291">
	<name>capture device metadata element retained</name>
	<description>Device used to create the image data has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#5">
		<name>image metadata</name>
		<description>The accuracy of metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/creationDateTime#retained']">
<measure ID="http://purl.org/DP/quality/measures#292">
	<name>date and time of creation metadata element retained</name>
	<description>Date and time of creation of image has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#5">
		<name>image metadata</name>
		<description>The accuracy of metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/imageWidth#retained']">
<measure ID="http://purl.org/DP/quality/measures#293">
	<name>image width metadata element retained</name>
	<description>Image width metadata element has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#5">
		<name>image metadata</name>
		<description>The accuracy of metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/imageHeight#retained']">
<measure ID="http://purl.org/DP/quality/measures#294">
	<name>image height metadata element retained</name>
	<description>Image height metadata element has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#5">
		<name>image metadata</name>
		<description>The accuracy of metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/orientation#retained']">
<measure ID="http://purl.org/DP/quality/measures#295">
	<name>image orientation metadata element retained</name>
	<description>Orientation metadata element has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#5">
		<name>image metadata</name>
		<description>The accuracy of metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/producer#retained']">
<measure ID="http://purl.org/DP/quality/measures#296">
	<name>producer metadata element retained</name>
	<description>Producer metadata element has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#5">
		<name>image metadata</name>
		<description>The accuracy of metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/software#retained']">
<measure ID="http://purl.org/DP/quality/measures#297">
	<name>software metadata element retained</name>
	<description>Metadata element on software used to write the image has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#5">
		<name>image metadata</name>
		<description>The accuracy of metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /298 -->
<!-- no scale for measure /299 -->
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/inputObjectValidation']">
<measure ID="http://purl.org/DP/quality/measures#46">
	<name>Input object validation performed</name>
	<description>Indicates if the action validates the input-object before processing.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#17">
		<name>input object validation</name>
		<description>Does the action perform validation of the input?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://maintainability/modularity/modularDesign']">
<measure ID="http://purl.org/DP/quality/measures#94">
	<name>modular design</name>
	<description>Assessment of the action-tool design in terms of modularity.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#29">
		<name>modular design</name>
		<description>Indicators for the modularity of the system design of an action, which is an aspect of maintainability</description>
		<category ID="http://purl.org/DP/quality/categories#modularity" scope="ALTERNATIVE_ACTION">
			<name>Modularity</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="yes/no/not observable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://maintainability/modularity/plugInSystem']">
<measure ID="http://purl.org/DP/quality/measures#95">
	<name>plugin system</name>
	<description>Indicates if and in what terms the action-tool implements plug-ins.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#29">
		<name>modular design</name>
		<description>Indicators for the modularity of the system design of an action, which is an aspect of maintainability</description>
		<category ID="http://purl.org/DP/quality/categories#modularity" scope="ALTERNATIVE_ACTION">
			<name>Modularity</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="yes - open specification/yes - closed specification/no/not observable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/execution/other']">
<measure ID="http://purl.org/DP/quality/measures#14">
	<name>other yearly execution costs</name>
	<description>Execution costs of an action which do not fit to a regular running cost category, measured in €/year</description>
	<attribute ID="http://purl.org/DP/quality/attributes#9">
		<name>operational costs of a preservation action</name>
		<description>These are the operational costs of running a preservation action.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/execution/hardwareRunning']">
<measure ID="http://purl.org/DP/quality/measures#15">
	<name>Running hardware costs</name>
	<description>Running hardware costs arising when using an action, measured in €/year (e.g. server rental costs).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#9">
		<name>operational costs of a preservation action</name>
		<description>These are the operational costs of running a preservation action.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/execution/softwareLicenceRunning']">
<measure ID="http://purl.org/DP/quality/measures#17">
	<name>Yearly running licence costs</name>
	<description>Running software licence costs of an action in € per year.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#9">
		<name>operational costs of a preservation action</name>
		<description>These are the operational costs of running a preservation action.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<!-- no old property for measure /18 -->
<xsl:template match="plato:criterion[@ID='action://business/costs/execution/staffRunning']">
<measure ID="http://purl.org/DP/quality/measures#19">
	<name>Running personell costs</name>
	<description>Running costs for operating an action per year (in €)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#9">
		<name>operational costs of a preservation action</name>
		<description>These are the operational costs of running a preservation action.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<!-- no old property for measure /20 -->
<xsl:template match="plato:criterion[@ID='action://business/costs/execution/totalPerObject']">
<measure ID="http://purl.org/DP/quality/measures#21">
	<name>Running costs per object</name>
	<description>Running operational costs of an action in € per object.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#9">
		<name>operational costs of a preservation action</name>
		<description>These are the operational costs of running a preservation action.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/compression']">
<measure ID="http://purl.org/DP/quality/measures#117">
	<name>compression type</name>
	<description>Type of compression used in the outcome object</description>
	<attribute ID="http://purl.org/DP/quality/attributes#39">
		<name>compression</name>
		<description>Compression used in outcome object.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="none/lossless/lossy" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /118 -->
<!-- no old property for measure /119 -->
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/retainFilename']">
<measure ID="http://purl.org/DP/quality/measures#66">
	<name>retain original file name</name>
	<description>Indicates if an action is able to retain the filename of the input-file for the output-file.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#26">
		<name>file name handling</name>
		<description>Indicators for how the preservation action handles file names</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://compatibility/interoperability/interfaces/API/.Net']">
<measure ID="http://purl.org/DP/quality/measures#32">
	<name>.Net API supported</name>
	<description>Is a .Net API natively supported?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#12">
		<name>Software interface of a preservation action</name>
		<description>The question of how to interface a preservation action tool, which can be a key factor limiting technical adoption in a given environment, but also drive or limit the scalability of operations where this action is involved (e.g. when no batch interface is provided)</description>
		<category ID="http://purl.org/DP/quality/categories#interoperability" scope="ALTERNATIVE_ACTION">
			<name>Interoperability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://compatibility/interoperability/interfaces/batchProcessingSupport']">
<measure ID="http://purl.org/DP/quality/measures#33">
	<name>batchprocessing supported</name>
	<description>Is there support for batch processing?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#12">
		<name>Software interface of a preservation action</name>
		<description>The question of how to interface a preservation action tool, which can be a key factor limiting technical adoption in a given environment, but also drive or limit the scalability of operations where this action is involved (e.g. when no batch interface is provided)</description>
		<category ID="http://purl.org/DP/quality/categories#interoperability" scope="ALTERNATIVE_ACTION">
			<name>Interoperability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://compatibility/interoperability/interfaces/directoryWatch']">
<measure ID="http://purl.org/DP/quality/measures#34">
	<name>directory watch supported</name>
	<description>Is directory watch supported, i.e. can the software automatically monitor the content of a certain folder in the file system and automatically process anything appearing there?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#12">
		<name>Software interface of a preservation action</name>
		<description>The question of how to interface a preservation action tool, which can be a key factor limiting technical adoption in a given environment, but also drive or limit the scalability of operations where this action is involved (e.g. when no batch interface is provided)</description>
		<category ID="http://purl.org/DP/quality/categories#interoperability" scope="ALTERNATIVE_ACTION">
			<name>Interoperability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://compatibility/interoperability/interfaces/API/java']">
<measure ID="http://purl.org/DP/quality/measures#35">
	<name>Java API supported</name>
	<description>Is a Java native API supported?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#12">
		<name>Software interface of a preservation action</name>
		<description>The question of how to interface a preservation action tool, which can be a key factor limiting technical adoption in a given environment, but also drive or limit the scalability of operations where this action is involved (e.g. when no batch interface is provided)</description>
		<category ID="http://purl.org/DP/quality/categories#interoperability" scope="ALTERNATIVE_ACTION">
			<name>Interoperability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://compatibility/interoperability/interfaces/API/other']">
<measure ID="http://purl.org/DP/quality/measures#36">
	<name>Other API supported</name>
	<description>Are any other APIs supported?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#12">
		<name>Software interface of a preservation action</name>
		<description>The question of how to interface a preservation action tool, which can be a key factor limiting technical adoption in a given environment, but also drive or limit the scalability of operations where this action is involved (e.g. when no batch interface is provided)</description>
		<category ID="http://purl.org/DP/quality/categories#interoperability" scope="ALTERNATIVE_ACTION">
			<name>Interoperability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /39 -->
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/document/handlingOfNotAllowedImageFormats']">
<measure ID="http://purl.org/DP/quality/measures#40">
	<name>handling of not allowed image formats</name>
	<description>The way the action handles illegal image formats</description>
	<attribute ID="http://purl.org/DP/quality/attributes#14">
		<name>handling of illegal content elements</name>
		<description>How does the action handle illegal (i.e. out-of-spec) elements inside content?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="convert/remove/keep" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/document/handlingOfNotAllowedJavaScript']">
<measure ID="http://purl.org/DP/quality/measures#41">
	<name>handling of not allowed JavaScript</name>
	<description>The way the action handles illegal Javascript elements</description>
	<attribute ID="http://purl.org/DP/quality/attributes#14">
		<name>handling of illegal content elements</name>
		<description>How does the action handle illegal (i.e. out-of-spec) elements inside content?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="convert/remove/keep" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/document/handlingOfNotAllowedMovingImages']">
<measure ID="http://purl.org/DP/quality/measures#42">
	<name>handling of not allowed moving images</name>
	<description>The way the action handles illegal video elements</description>
	<attribute ID="http://purl.org/DP/quality/attributes#14">
		<name>handling of illegal content elements</name>
		<description>How does the action handle illegal (i.e. out-of-spec) elements inside content?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="convert/remove/keep" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCGamingHardware/controllerFeedback/audible']">
<measure ID="http://purl.org/DP/quality/measures#213">
	<name>PC gaming hardware audible controller feedback</name>
	<description>Indicates personal computer gaming hardware audible controller feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#76">
		<name>PC gaming hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCGamingHardware/controllerFeedback/force']">
<measure ID="http://purl.org/DP/quality/measures#214">
	<name>PC gaming hardware force controller feedback</name>
	<description>Indicates personal computer gaming hardware controller force feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#76">
		<name>PC gaming hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supporte" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCGamingHardware/originalFeel']">
<measure ID="http://purl.org/DP/quality/measures#215">
	<name>PC gaming hardware original feel</name>
	<description>Indicates if and in which way standard pc gaming hardware exists which resembles the feel of the original.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#76">
		<name>PC gaming hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="controllers resembling original controller not available/controllers resembling original controller not supported/controllers resembling original controller supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCGamingHardware/originalLook']">
<measure ID="http://purl.org/DP/quality/measures#216">
	<name>PC gaming hardware original look</name>
	<description>Indicates if and in which way standard pc gaming hardware exists which resembles the look of the original.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#76">
		<name>PC gaming hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="controllers resembling original controller not available/controllers resembling original controller not supported/controllers resembling original controller supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCGamingHardware/responseDelay']">
<measure ID="http://purl.org/DP/quality/measures#217">
	<name>PC gaming hardware response delay</name>
	<description>Indicates personal computer gaming hardware response delay (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#76">
		<name>PC gaming hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/considerable delay/short delay/delay not noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCGamingHardware/controllerFeedback/visual']">
<measure ID="http://purl.org/DP/quality/measures#218">
	<name>PC gaming hardware visual controller feedback</name>
	<description>Indicates personal computer gaming hardware visual controller feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#76">
		<name>PC gaming hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://effect/costs/preservationWatchAndPlanning']">
<measure ID="http://purl.org/DP/quality/measures#136">
	<name>comparative preservation watch and planning costs</name>
	<description>Effects of a preservation action on preservation watch and planning costs: Do they increase or decrease?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#47">
		<name>preservation watch and planning costs</name>
		<description>Effects of a preservation action on preservation watch and planning costs</description>
		<category ID="http://purl.org/DP/quality/categories#outcome_effect" scope="ALTERNATIVE_ACTION">
			<name>Outcome effect</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="increase/unchanged/decrease" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/networkSupport/lagForNetworkPlay']">
<measure ID="http://purl.org/DP/quality/measures#232">
	<name>lag for network play</name>
	<description>Indicates the lag occuring at network play.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#80">
		<name>video game network support</name>
		<description>Degree of network support.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/considerable delay/short delay/delay not noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/networkSupport/networkProtocols']">
<measure ID="http://purl.org/DP/quality/measures#233">
	<name>supported network protocols</name>
	<description>Indicates whether network protocols are supported by the game.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#80">
		<name>video game network support</name>
		<description>Degree of network support.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/networkSupport/useOfOriginalServers']">
<measure ID="http://purl.org/DP/quality/measures#234">
	<name>possibility of using original servers</name>
	<description>Indicates it the use of the original servers is supported at network play.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#80">
		<name>video game network support</name>
		<description>Degree of network support.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /13 -->
<xsl:template match="plato:criterion[@ID='outcome://object/image/xSamplingFrequency#equal']">
<measure ID="http://purl.org/DP/quality/measures#59">
	<name>X sampling frequency preserved</name>
	<description>Indicates whether X sampling frequency has been preserved.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#22">
		<name>sampling frequency</name>
		<description>The number of captured pixels per unit distance</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/ySamplingFrequency#equal']">
<measure ID="http://purl.org/DP/quality/measures#60">
	<name>Y sampling frequency preserved</name>
	<description>Indicates whether Y sampling frequency has been preserved.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#22">
		<name>sampling frequency</name>
		<description>The number of captured pixels per unit distance</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /61 -->
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/outputFileFormatVerification']">
<measure ID="http://purl.org/DP/quality/measures#64">
	<name>output object validation performed</name>
	<description>Indicates if the action validates the output-object after processing.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#24">
		<name>output object validation</name>
		<description>Does the action perform validation of the output?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://portability/installability/easeOfIntegrationInCurrentEnvironment']">
<measure ID="http://purl.org/DP/quality/measures#99">
	<name>ease of integration</name>
	<description>Assessment of how easy it is to integrate an action into a particular server environment.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#32">
		<name>server environment integration</name>
		<description>Feasibility of integration of an action into a server environment</description>
		<category ID="http://purl.org/DP/quality/categories#installability" scope="ALTERNATIVE_ACTION">
			<name>Installability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="good/acceptable/poor/not possible" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://portability/installability/supportedPlattforms/multiplePlatformSupport']">
<measure ID="http://purl.org/DP/quality/measures#100">
	<name>multiple platform support</name>
	<description>Indicates if an action is capable of running on different platforms</description>
	<attribute ID="http://purl.org/DP/quality/attributes#32">
		<name>server environment integration</name>
		<description>Feasibility of integration of an action into a server environment</description>
		<category ID="http://purl.org/DP/quality/categories#installability" scope="ALTERNATIVE_ACTION">
			<name>Installability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://portability/installability/supportedPlattforms/linux']">
<measure ID="http://purl.org/DP/quality/measures#101">
	<name>Linux supported</name>
	<description>Indicates if an action is capable of running on Linux</description>
	<attribute ID="http://purl.org/DP/quality/attributes#32">
		<name>server environment integration</name>
		<description>Feasibility of integration of an action into a server environment</description>
		<category ID="http://purl.org/DP/quality/categories#installability" scope="ALTERNATIVE_ACTION">
			<name>Installability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://portability/installability/supportedPlattforms/macOS']">
<measure ID="http://purl.org/DP/quality/measures#102">
	<name>MacOS supported</name>
	<description>Indicates if an action is capable of running on MacOS</description>
	<attribute ID="http://purl.org/DP/quality/attributes#32">
		<name>server environment integration</name>
		<description>Feasibility of integration of an action into a server environment</description>
		<category ID="http://purl.org/DP/quality/categories#installability" scope="ALTERNATIVE_ACTION">
			<name>Installability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://portability/installability/supportedPlattforms/windows']">
<measure ID="http://purl.org/DP/quality/measures#103">
	<name>Windows supported</name>
	<description>Indicates if an action is capable of running on Windows</description>
	<attribute ID="http://purl.org/DP/quality/attributes#32">
		<name>server environment integration</name>
		<description>Feasibility of integration of an action into a server environment</description>
		<category ID="http://purl.org/DP/quality/categories#installability" scope="ALTERNATIVE_ACTION">
			<name>Installability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/selfDocumentation/metadata/embedCustomMetadata']">
<measure ID="http://purl.org/DP/quality/measures#149">
	<name>custom metadata support</name>
	<description>Indicates whether additionaly metadata can be embedded in the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#53">
		<name>format metadata support</name>
		<description>Indicators for the support that a format provides for metadata</description>
		<category ID="http://purl.org/DP/quality/categories#format_quality_and_functionality" scope="ALTERNATIVE_ACTION">
			<name>Format quality and functionality</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/selfDocumentation/metadata/embedImageMetadata']">
<measure ID="http://purl.org/DP/quality/measures#150">
	<name>image metadata support</name>
	<description>Indicates whether image metadata can be embedded in the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#53">
		<name>format metadata support</name>
		<description>Indicators for the support that a format provides for metadata</description>
		<category ID="http://purl.org/DP/quality/categories#format_quality_and_functionality" scope="ALTERNATIVE_ACTION">
			<name>Format quality and functionality</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/selfDocumentation/metadata/embedPreviewImage']">
<measure ID="http://purl.org/DP/quality/measures#151">
	<name>image previews embeddable</name>
	<description>Indicate whether a preview image can be embedded in the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#53">
		<name>format metadata support</name>
		<description>Indicators for the support that a format provides for metadata</description>
		<category ID="http://purl.org/DP/quality/categories#format_quality_and_functionality" scope="ALTERNATIVE_ACTION">
			<name>Format quality and functionality</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://usability/operability/easeOfUse']">
<measure ID="http://purl.org/DP/quality/measures#115">
	<name>ease of use</name>
	<description>Assessment of how easy it is to use an action in operations</description>
	<attribute ID="http://purl.org/DP/quality/attributes#38">
		<name>ease of operations</name>
		<description>How easy are operations with this action?</description>
		<category ID="http://purl.org/DP/quality/categories#operability" scope="ALTERNATIVE_ACTION">
			<name>Operability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="good/acceptable/poor" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://effect/costs/archivalStorage']">
<measure ID="http://purl.org/DP/quality/measures#135">
	<name>comparative archival storage costs</name>
	<description>Effects of a preservation action on archival storage costs: Do they increase or decrease?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#46">
		<name>archival storage costs</name>
		<description>Effects of a preservation action on archival storage costs</description>
		<category ID="http://purl.org/DP/quality/categories#outcome_effect" scope="ALTERNATIVE_ACTION">
			<name>Outcome effect</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="increase/unchanged/decrease" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://effect/costs/archivalStorageQuantitative']">
<measure ID="http://purl.org/DP/quality/measures#137">
	<name>quantitative archival storage costs</name>
	<description>Effects of a preservation action on archival storage costs, expressed as archival storage costs per object over a time period of 10 years, measured in  €.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#46">
		<name>archival storage costs</name>
		<description>Effects of a preservation action on archival storage costs</description>
		<category ID="http://purl.org/DP/quality/categories#outcome_effect" scope="ALTERNATIVE_ACTION">
			<name>Outcome effect</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/gamePlayable']">
<measure ID="http://purl.org/DP/quality/measures#181">
	<name>video game playable</name>
	<description>Indicates whether the game is playable.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#69">
		<name>video game</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/gameStarts']">
<measure ID="http://purl.org/DP/quality/measures#182">
	<name>video game starts</name>
	<description>Indicates whether the game starts. A game starts successfully if the start screen of the game shows up. A black screen after a start attempt is not enough to evaluate this property to true.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#69">
		<name>video game</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /183 -->
<xsl:template match="plato:criterion[@ID='action://business/costs/total']">
<measure ID="http://purl.org/DP/quality/measures#30">
	<name>TCO of action</name>
	<description>Total costs of setting up and operating an action (in €)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#10">
		<name>Total costs of ownership of a preservation action</name>
		<description>These are the total costs of a preservation action (according to some TCO model)</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/fractionOfObjectsSupported']">
<measure ID="http://purl.org/DP/quality/measures#44">
	<name>coverage of objects</name>
	<description>Objects supported by the action as percentage of overall objects of this content that need to be supported</description>
	<attribute ID="http://purl.org/DP/quality/attributes#15">
		<name>coverage of content</name>
		<description>How much of the content that needs treatment is supported by this action?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_appropriateness" scope="ALTERNATIVE_ACTION">
			<name>Functional appropriateness</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/numberInputFormats']">
<measure ID="http://purl.org/DP/quality/measures#48">
	<name>Number of input formats supported</name>
	<description>Number of input formats supported by an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#15">
		<name>coverage of content</name>
		<description>How much of the content that needs treatment is supported by this action?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_appropriateness" scope="ALTERNATIVE_ACTION">
			<name>Functional appropriateness</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/numberOutputFormats']">
<measure ID="http://purl.org/DP/quality/measures#49">
	<name>Number of output formats supported</name>
	<description>Number of output formats supported by an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#15">
		<name>coverage of content</name>
		<description>How much of the content that needs treatment is supported by this action?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_appropriateness" scope="ALTERNATIVE_ACTION">
			<name>Functional appropriateness</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/numberOfObjectsSupported']">
<measure ID="http://purl.org/DP/quality/measures#133">
	<name>number of objects supported</name>
	<description>Total number of objects supported by an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#15">
		<name>coverage of content</name>
		<description>How much of the content that needs treatment is supported by this action?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_appropriateness" scope="ALTERNATIVE_ACTION">
			<name>Functional appropriateness</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/bitsPerSample#equal']">
<measure ID="http://purl.org/DP/quality/measures#169">
	<name>bits per sample equal</name>
	<description>Bits per component (channel) for each pixel</description>
	<attribute ID="http://purl.org/DP/quality/attributes#61">
		<name>colour depth</name>
		<description>Bits per component (channel) for each pixel</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/bitsPerSample#greaterOrEqual']">
<measure ID="http://purl.org/DP/quality/measures#170">
	<name>bits per sample retained</name>
	<description>Number of bits per component (channel) equal or higher compared with original.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#61">
		<name>colour depth</name>
		<description>Bits per component (channel) for each pixel</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/transparency/complexity']">
<measure ID="http://purl.org/DP/quality/measures#143">
	<name>format complexity</name>
	<description>Indicator of the complexity of a format as judged by experts in the domain</description>
	<attribute ID="http://purl.org/DP/quality/attributes#50">
		<name>format transparency</name>
		<description>How transparent is the format and its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="high/medium/low" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/readableAsPlainText']">
<measure ID="http://purl.org/DP/quality/measures#158">
	<name>format readable as plain text</name>
	<description>Indicates whether the outcome format is human readable as plain text.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#50">
		<name>format transparency</name>
		<description>How transparent is the format and its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/representation/characterEncoding']">
<measure ID="http://purl.org/DP/quality/measures#171">
	<name>database character encoding</name>
	<description>Character encoding used in given database representation.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#63">
		<name>database representation</name>
		<description>Database representation information</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<freeTextScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</freeTextScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/representation/timeEncoding']">
<measure ID="http://purl.org/DP/quality/measures#172">
	<name>database time encoding</name>
	<description>Time encoding used in given database representation.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#63">
		<name>database representation</name>
		<description>Database representation information</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<freeTextScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</freeTextScale>
</measure>
</xsl:template>
<!-- no old property for measure /173 -->
<!-- no old property for measure /174 -->
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCStandardHardware/controllerFeedback/audible']">
<measure ID="http://purl.org/DP/quality/measures#219">
	<name>PC standard hardware audible controller feedback</name>
	<description>Indicates personal computer standard hardware audible controller feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#75">
		<name>PC standard hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCStandardHardware/controllerFeedback/force']">
<measure ID="http://purl.org/DP/quality/measures#220">
	<name>PC standard hardware force controller feedback</name>
	<description>Indicates personal computer standard hardware controller force feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#75">
		<name>PC standard hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCStandardHardware/responseDelay']">
<measure ID="http://purl.org/DP/quality/measures#221">
	<name>PC standard hardware response delay</name>
	<description>Indicates personal computer standard hardware response delay (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#75">
		<name>PC standard hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/considerable delay/short delay/delay not noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCStandardHardware/support']">
<measure ID="http://purl.org/DP/quality/measures#222">
	<name>PC standard hardware support</name>
	<description>Indicates if and in which way personal computer standard hardware is supported.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#75">
		<name>PC standard hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="supported and resemble look of original controller/supported/not supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCStandardHardware/usability']">
<measure ID="http://purl.org/DP/quality/measures#223">
	<name>PC standard hardware usability</name>
	<description>Indicates personal computer standard hardware usability (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#75">
		<name>PC standard hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not acceptable/fairly playable/well playable/perfectly playable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/PCStandardHardware/controllerFeedback/visual']">
<measure ID="http://purl.org/DP/quality/measures#224">
	<name>PC standard hardware visual controller feedback</name>
	<description>Indicates personal computer standard hardware visual controller feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#75">
		<name>PC standard hardware controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/recoverability/errorAwareness']">
<measure ID="http://purl.org/DP/quality/measures#152">
	<name>error awareness</name>
	<description>Indicates the presence of features built into the format that allow the detection of and recovery from errors (such as checksums)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#54">
		<name>format recoverability</name>
		<description>Resilience of the format aginst errors: Can errors be detected and/or recovered?</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="recoverable/detectable/none" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/format/valid']">
<measure ID="http://purl.org/DP/quality/measures#120">
	<name>validity</name>
	<description>Indicates whether the format of the object is valid.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#40">
		<name>format conformity</name>
		<description>Does the digital object conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/format/wellformed']">
<measure ID="http://purl.org/DP/quality/measures#121">
	<name>well-formedness</name>
	<description>Indicates whether the format of the object is well formed.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#40">
		<name>format conformity</name>
		<description>Does the digital object conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/format/conforms']">
<measure ID="http://purl.org/DP/quality/measures#122">
	<name>conforms</name>
	<description>Indicates whether the format of the result conforms to the output format specified by the action.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#40">
		<name>format conformity</name>
		<description>Does the digital object conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/document/features/searchability']">
<measure ID="http://purl.org/DP/quality/measures#124">
	<name>level of searchability</name>
	<description>Indicates if and in which way the document is searchable.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#42">
		<name>searchability</name>
		<description>Is it possible to search the content of the digital object</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="full/partial/no" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /125 -->
<xsl:template match="plato:criterion[@ID='outcome://format/business/licenceCostsForUse']">
<measure ID="http://purl.org/DP/quality/measures#155">
	<name>format licence costs incurred</name>
	<description>Indicates whether licence costs occur at using the outcome format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#58">
		<name>format licence costs</name>
		<description>Which licence costs are incurred when using a certain format?</description>
		<category ID="http://purl.org/DP/quality/categories#format_business_factors" scope="ALTERNATIVE_ACTION">
			<name>Format business factors</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/byteStreamMediaSupport']">
<measure ID="http://purl.org/DP/quality/measures#68">
	<name>byte-stream media support</name>
	<description>Indicator for the mode in which an action supports byte stream media</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported as stand-alone file/supported encapsulated in file" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/cdRomSupport']">
<measure ID="http://purl.org/DP/quality/measures#69">
	<name>cd-rom support</name>
	<description>Indicates whether an action is able to access and use the cd-rom drive.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysBoxArtwork']">
<measure ID="http://purl.org/DP/quality/measures#70">
	<name>displays box artwork</name>
	<description>Indicates whether an action is capable of displaying the box artwork.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysContextDetails']">
<measure ID="http://purl.org/DP/quality/measures#71">
	<name>displays context details</name>
	<description>Indicates whether an action is capable of displaying the context details.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysDescriptionOfOriginalMedia']">
<measure ID="http://purl.org/DP/quality/measures#72">
	<name>displays description of original media</name>
	<description>Indicates whether an action is capable of displaying the description of the original media.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysManual']">
<measure ID="http://purl.org/DP/quality/measures#73">
	<name>displays manual</name>
	<description>Indicates whether an action is capable of displaying the manual</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysNecessarySystemConfiguration']">
<measure ID="http://purl.org/DP/quality/measures#74">
	<name>displays necessary system configuration</name>
	<description>Indicates whether an action is capable of displaying the necessary system configuration.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysOriginalMedia']">
<measure ID="http://purl.org/DP/quality/measures#75">
	<name>displays original media</name>
	<description>Indicates whether an action is capable of displaying the original media.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysOriginallySuppliedAdditionalItems']">
<measure ID="http://purl.org/DP/quality/measures#76">
	<name>displays originally supplied additional itmes</name>
	<description>Indicates whether an action is capable of displaying the originally suplied additional items (like special controllers, accessories, etc.).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysReferenceToConsoleVideoGameSystem']">
<measure ID="http://purl.org/DP/quality/measures#77">
	<name>displays reference to console video game system</name>
	<description>Indicates whether an action is capable of displaying the reference to the console video game system.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysReferenceToPreferredEmulator']">
<measure ID="http://purl.org/DP/quality/measures#78">
	<name>displays reference to preferred emulator</name>
	<description>Indicates whether an action is capable of displaying the reference to the preferred emulator.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="yes - with system configuration/yes - without system configuration/no" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysRegionLockOutInformation']">
<measure ID="http://purl.org/DP/quality/measures#79">
	<name>displays region lock-out information</name>
	<description>Indicates whether an action is capable of displaying the region lock-out information.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysTvStandardInformation']">
<measure ID="http://purl.org/DP/quality/measures#80">
	<name>displays TV standard information</name>
	<description>Indicates whether an action is capable of displaying the TV standard information.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/displaysDisplayFrequencyInformation']">
<measure ID="http://purl.org/DP/quality/measures#81">
	<name>displays video display frequency information</name>
	<description>Indicates whether an action is capable of displaying the display frequency information.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/gameStartMode']">
<measure ID="http://purl.org/DP/quality/measures#82">
	<name>game start mode</name>
	<description>indicates in which way video games can be started by an action.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="command line/user interface/ command line and user interface" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/keyboardSupport']">
<measure ID="http://purl.org/DP/quality/measures#83">
	<name>keyboard support</name>
	<description>Indicates whether an action is able to access and use the keyboard.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/mouseSupport']">
<measure ID="http://purl.org/DP/quality/measures#84">
	<name>mouse support</name>
	<description>Indicates whether an action is able to access and use the mouse.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/necessaryAccessoriesSupported']">
<measure ID="http://purl.org/DP/quality/measures#85">
	<name>necessary accessories supported</name>
	<description>Indicates in which way necessary accessories (e.g. special controller mandatory for playing a video game) are supported by an action.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/Y/N" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/originalAccessoriesSupported']">
<measure ID="http://purl.org/DP/quality/measures#86">
	<name>original accessories supported</name>
	<description>Indicates in which way original accessories (special controller, etc.) are supported by an action.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported with special hardware/supported without special hardware" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/originalMediaSupport']">
<measure ID="http://purl.org/DP/quality/measures#87">
	<name>original media support</name>
	<description>The way an action supports original media.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported with special hardware/supported without special hardware" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/osCommunicationType']">
<measure ID="http://purl.org/DP/quality/measures#88">
	<name>OS communication type</name>
	<description>The way an action communicates with the operating system.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="native/none/via network/via virtual media" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/osPreInstalled']">
<measure ID="http://purl.org/DP/quality/measures#89">
	<name>OS pre-installed</name>
	<description>Indicates whether there is an OS preinstalled in an action (in an emulator)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/recreationLevel']">
<measure ID="http://purl.org/DP/quality/measures#90">
	<name>recreation level</name>
	<description>Indicates on which levels an action operates to be able to run the video game.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="high level and no BIOS is needed/low level and no BIOS is needed/high level and BIOS is needed/low level and BIOS is needed" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/soundSupport']">
<measure ID="http://purl.org/DP/quality/measures#91">
	<name>sound support</name>
	<description>Indicates whether sound output is supported by an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/contentSpecific/videoGames/supportedMediaTypes']">
<measure ID="http://purl.org/DP/quality/measures#92">
	<name>supported media types</name>
	<description>Media types supported by an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#27">
		<name>degree of specific feature support for video games</name>
		<description>Indicators for the specific support of particular features that are present in video games</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/neither byte-stream nor original media supported/original media but no byte stream supported/byte stream but not original media supported/byte stream and original media supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/execution/softwareDevelopNewFeatures']">
<measure ID="http://purl.org/DP/quality/measures#16">
	<name>feature development costs</name>
	<description>Costs for developing new features which are not available in the software but required for successful operations (€)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/hardwareInitial']">
<measure ID="http://purl.org/DP/quality/measures#22">
	<name>Initial hardware costs</name>
	<description>Initial hardware costs arising when deploying an action (e.g. new hardware needs to be acquired to get software running) measured in €</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/other']">
<measure ID="http://purl.org/DP/quality/measures#23">
	<name>Other initial setup costs</name>
	<description>Setup costs of an action which do not fit to any other given setup costs, in €</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/softwareCustomization']">
<measure ID="http://purl.org/DP/quality/measures#24">
	<name>Initial customization costs</name>
	<description>Costs for customizing an action (e.g. performance-tuning), in €</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/softwareIntegration']">
<measure ID="http://purl.org/DP/quality/measures#25">
	<name>Initial integration costs</name>
	<description>Costs for integrating an action in the current environment, in €</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/softwareLicenceInitial']">
<measure ID="http://purl.org/DP/quality/measures#26">
	<name>Initial software licence costs</name>
	<description>Initial software licence costs of an action, in €</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/staffExternalExpertiseNeeded']">
<measure ID="http://purl.org/DP/quality/measures#27">
	<name>Costs of external expertise for action setup</name>
	<description>Cost for any external expertise required to setup an action (in €)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/staffTraining']">
<measure ID="http://purl.org/DP/quality/measures#28">
	<name>Software training costs</name>
	<description>Costs for training staff in operating an action (in €)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://business/costs/setup/total']">
<measure ID="http://purl.org/DP/quality/measures#29">
	<name>Total setup costs</name>
	<description>Total sum of costs needed to setup an action (in €)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#8">
		<name>setup costs of a preservation action</name>
		<description>These are the initial setup costs required for preservation operations. This includes many factors such as acquisition, one-time licence fees, installation and configuration, etc.</description>
		<category ID="http://purl.org/DP/quality/categories#action_costs" scope="ALTERNATIVE_ACTION">
			<name>Action costs</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/additionalItemsSupported']">
<measure ID="http://purl.org/DP/quality/measures#197">
	<name>additional supported items</name>
	<description>Indicates if additional items (like game boards used in role-playing games) are supported by the game emulation itself (and therefore are not required physical), or if they are not supported and therefore are required to be physically present - otherwise the game cannot be played.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#72">
		<name>video game interactivity</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /210 -->
<!-- no old property for measure /211 -->
<!-- no old property for measure /212 -->
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/frameRate']">
<measure ID="http://purl.org/DP/quality/measures#184">
	<name>frame rate preserved</name>
	<description></description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/frameRate#deviationInPercent']">
<measure ID="http://purl.org/DP/quality/measures#185">
	<name>frame rate deviation</name>
	<description></description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<!-- no old property for measure /186 -->
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/2DObjectLayerPlacement']">
<measure ID="http://purl.org/DP/quality/measures#187">
	<name>quality of 2d object layer placement</name>
	<description>Indicates how good 2D object layer placement is working.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="no 2D objects displayed/not applicable/severe errors on whole image/errors noticeable but do not affect gameplay/no errors noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/2DObjectPositionPlacement']">
<measure ID="http://purl.org/DP/quality/measures#188">
	<name>quality of 2D object position placement</name>
	<description>Indicates how good 2D object position placement is working.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="no 2D objects displayed/not applicable/severe errors on whole image/errors noticeable but do not affect gameplay/no errors noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/3DObjectCalculation']">
<measure ID="http://purl.org/DP/quality/measures#189">
	<name>quality of 3D object calculation</name>
	<description>Indicates how good 3D object calculation is working.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="no 3D objects displayed/not applicable/severe errors on whole image/errors noticeable but do not affect gameplay/no errors noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/3DObjectClipping']">
<measure ID="http://purl.org/DP/quality/measures#190">
	<name>quality of 3D object clipping</name>
	<description>Indicates how good 3D object clipping is working.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="no 3D objects displayed/not applicable/severe errors on whole image/errors noticeable but do not affect gameplay/no errors noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/3DTextureQuality']">
<measure ID="http://purl.org/DP/quality/measures#191">
	<name>3D texture quality</name>
	<description>Indicates the quality of 3D texture quality.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="no 3D objects displayed/not applicable/severe errors on whole image/errors noticeable but do not affect gameplay/no errors noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/graphics/screenOverlay']">
<measure ID="http://purl.org/DP/quality/measures#192">
	<name>degree to which screen overlay is supported</name>
	<description>Indicates in which way screen overlays are supported.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#71">
		<name>video game graphics</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported manually by loading image/supported by auto-detection/supported through encapsulation" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /193 -->
<xsl:template match="plato:criterion[@ID='outcome://object/image/ocrText#equal']">
<measure ID="http://purl.org/DP/quality/measures#168">
	<name>OCR text retained</name>
	<description>Indicates whether the OCR text extracted from an image has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#60">
		<name>OCR Text</name>
		<description>OCR text extracted from an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/disclosure/documentation/availability']">
<measure ID="http://purl.org/DP/quality/measures#147">
	<name>format documentation availability</name>
	<description>Availability of the documentation for a format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#52">
		<name>format documentation</name>
		<description>Indicators for the documentation that is available for a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="yes-free/yes-pay/no" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/disclosure/documentation/quality']">
<measure ID="http://purl.org/DP/quality/measures#148">
	<name>format documentation quality</name>
	<description>Judgement of the quality of documentation available for a format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#52">
		<name>format documentation</name>
		<description>Indicators for the documentation that is available for a format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="complete/fragmentary/useless" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/macroSupport']">
<measure ID="http://purl.org/DP/quality/measures#47">
	<name>Macros supported</name>
	<description>Indicates if the action supports macros.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#18">
		<name>Support of active content</name>
		<description>Are Macros specifically supported?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/reportedErrorsContainHandlingAdvice']">
<measure ID="http://purl.org/DP/quality/measures#65">
	<name>error handling advice provided</name>
	<description>Indicate whether reported error contain a handling advice/suggestion.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#25">
		<name>error handling</name>
		<description>Indicators for how well the preservation action deals with errors</description>
		<category ID="http://purl.org/DP/quality/categories#fault_tolerance" scope="ALTERNATIVE_ACTION">
			<name>Fault tolerance</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/understandabilityOfReportedErrors']">
<measure ID="http://purl.org/DP/quality/measures#67">
	<name>error message understandability</name>
	<description>Indicates tho which degree reported errors are understandable to human users.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#25">
		<name>error handling</name>
		<description>Indicators for how well the preservation action deals with errors</description>
		<category ID="http://purl.org/DP/quality/categories#fault_tolerance" scope="ALTERNATIVE_ACTION">
			<name>Fault tolerance</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="good/acceptable/poor/no" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://reliability/faultTolerance/errorCorrection']">
<measure ID="http://purl.org/DP/quality/measures#105">
	<name>error correction</name>
	<description>Indicates whether the action-tools is able to correct errors in content</description>
	<attribute ID="http://purl.org/DP/quality/attributes#25">
		<name>error handling</name>
		<description>Indicators for how well the preservation action deals with errors</description>
		<category ID="http://purl.org/DP/quality/categories#fault_tolerance" scope="ALTERNATIVE_ACTION">
			<name>Fault tolerance</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://reliability/faultTolerance/errorTolerance']">
<measure ID="http://purl.org/DP/quality/measures#106">
	<name>error tolerance</name>
	<description>Indicates whether the action-tool is able to tolerate errors in content</description>
	<attribute ID="http://purl.org/DP/quality/attributes#25">
		<name>error handling</name>
		<description>Indicators for how well the preservation action deals with errors</description>
		<category ID="http://purl.org/DP/quality/categories#fault_tolerance" scope="ALTERNATIVE_ACTION">
			<name>Fault tolerance</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://performanceEfficiency/resourceUtilization/memoryPerMB']">
<measure ID="http://purl.org/DP/quality/measures#97">
	<name>average memory used per Megabyte</name>
	<description>Memory used per megabyte of input, measured in megabyte.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#31">
		<name>memory usage</name>
		<description>Memory used for processing of objects in an action</description>
		<category ID="http://purl.org/DP/quality/categories#resource_utilization" scope="ALTERNATIVE_ACTION">
			<name>Resource utilization</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<!-- no old property for measure /98 -->
<!-- no old property for measure /57 -->
<!-- no old property for measure /58 -->
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/specialController/controllerFeedback/audible']">
<measure ID="http://purl.org/DP/quality/measures#198">
	<name>audible feedback</name>
	<description>Indicates original special controller audible feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#73">
		<name>special controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/specialController/controllerFeedback/force']">
<measure ID="http://purl.org/DP/quality/measures#199">
	<name>force feedback</name>
	<description>Indicates original special controller force feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#73">
		<name>special controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/specialController/support']">
<measure ID="http://purl.org/DP/quality/measures#203">
	<name>special controller supported</name>
	<description>Indicates if and under what circumstances original special controller are supported.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#73">
		<name>special controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported with special hardware/supported with special software/native supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/specialController/controllerFeedback/visual']">
<measure ID="http://purl.org/DP/quality/measures#204">
	<name>special controller visual feedback</name>
	<description>Indicates original special controller visual feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#73">
		<name>special controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /194 -->
<!-- no old property for measure /195 -->
<!-- no old property for measure /196 -->
<!-- no old property for measure /351 -->
<xsl:template match="plato:criterion[@ID='outcome://object/document/metadata/author#equal']">
<measure ID="http://purl.org/DP/quality/measures#352">
	<name>creating author of document retained</name>
	<description>Indicates whether the author who created the document has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#66">
		<name>document metadata</name>
		<description>Information about the document</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /353 -->
<xsl:template match="plato:criterion[@ID='outcome://object/document/metadata/language#equal']">
<measure ID="http://purl.org/DP/quality/measures#354">
	<name>document language retained</name>
	<description>Indicates whether the language the document is written in has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#66">
		<name>document metadata</name>
		<description>Information about the document</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/document/metadata/subject#equal']">
<measure ID="http://purl.org/DP/quality/measures#355">
	<name>document subject retained</name>
	<description>Indicates whether the document's subject has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#66">
		<name>document metadata</name>
		<description>Information about the document</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/document/metadata/title#equal']">
<measure ID="http://purl.org/DP/quality/measures#356">
	<name>document title retained</name>
	<description>Indicates whether the document's title has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#66">
		<name>document metadata</name>
		<description>Information about the document</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://reliability/maturity/stability']">
<measure ID="http://purl.org/DP/quality/measures#108">
	<name>stability judgement</name>
	<description>Judgement of the stability of an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#35">
		<name>stability</name>
		<description>Stability of an action under normal operations</description>
		<category ID="http://purl.org/DP/quality/categories#maturity" scope="ALTERNATIVE_ACTION">
			<name>Maturity</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="stable/largely stable/unstable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/activityLoggingAmount']">
<measure ID="http://purl.org/DP/quality/measures#37">
	<name>Amount of logging</name>
	<description>Number of characters of activity log produced upon running an action on one content object</description>
	<attribute ID="http://purl.org/DP/quality/attributes#13">
		<name>Activity logging</name>
		<description>This represents the common requirement of knowing as precisely as possible what a preservation action tool did and how, i.e. how much reporting happens on the side of a preservation action about the activities that have taken place. This includes both the amount of information procuded as well as the format (e.g. whether this is human-readable, machine-processible or in PREMIS format)</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/activityLoggingFormat']">
<measure ID="http://purl.org/DP/quality/measures#38">
	<name>Format of logging</name>
	<description>The format of logging output produced by an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#13">
		<name>Activity logging</name>
		<description>This represents the common requirement of knowing as precisely as possible what a preservation action tool did and how, i.e. how much reporting happens on the side of a preservation action about the activities that have taken place. This includes both the amount of information procuded as well as the format (e.g. whether this is human-readable, machine-processible or in PREMIS format)</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="text/xml/premis/database/none" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/generic/errorReporting']">
<measure ID="http://purl.org/DP/quality/measures#43">
	<name>error reporting</name>
	<description>Indicates whether errors at action execution are reported.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#13">
		<name>Activity logging</name>
		<description>This represents the common requirement of knowing as precisely as possible what a preservation action tool did and how, i.e. how much reporting happens on the side of a preservation action about the activities that have taken place. This includes both the amount of information procuded as well as the format (e.g. whether this is human-readable, machine-processible or in PREMIS format)</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/standardController/controllerFeedback/audible']">
<measure ID="http://purl.org/DP/quality/measures#205">
	<name>standard controller audible feedback</name>
	<description>Indicates original standard controller audible feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#74">
		<name>standard controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/standardController/controllerFeedback/force']">
<measure ID="http://purl.org/DP/quality/measures#206">
	<name>standard controller force feedback</name>
	<description>Indicates original standard controller force feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#74">
		<name>standard controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/standardController/responseDelay']">
<measure ID="http://purl.org/DP/quality/measures#207">
	<name>standard controller response delay</name>
	<description>Indicates original standard controller response delay (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#74">
		<name>standard controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/considerable delay/short delay/delay not noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/standardController/support']">
<measure ID="http://purl.org/DP/quality/measures#208">
	<name>standard controller support</name>
	<description>Indicates if and under what circumstances original standard controller are supported.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#74">
		<name>standard controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported with special hardware/supported with special software/native supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/originalHardware/standardController/controllerFeedback/visual']">
<measure ID="http://purl.org/DP/quality/measures#209">
	<name>standard controller visual feedback</name>
	<description>Indicates original standard controller visual feedback (if applicable).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#74">
		<name>standard controller support</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not supported/supported" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/METS/validDcMods']">
<measure ID="http://purl.org/DP/quality/measures#126">
	<name>valid DC/MODS</name>
	<description>Indicates if  METS-metadata contains valid DC/MODS data.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#43">
		<name>validity of METS metadata</name>
		<description>Does METS metadata conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/METS/validMix']">
<measure ID="http://purl.org/DP/quality/measures#127">
	<name>valid MIX metadata</name>
	<description>Indicates if  METS-metadata contains valid MIX data.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#43">
		<name>validity of METS metadata</name>
		<description>Does METS metadata conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/METS/validPremis']">
<measure ID="http://purl.org/DP/quality/measures#129">
	<name>valid PREMIS metadata</name>
	<description>Indicates if  METS-metadata contains valid PREMIS data.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#43">
		<name>validity of METS metadata</name>
		<description>Does METS metadata conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /382 -->
<!-- no old property for measure /383 -->
<!-- no old property for measure /384 -->
<!-- no old property for measure /385 -->
<!-- no old property for measure /386 -->
<xsl:template match="plato:criterion[@ID='action://functionalSuitability/functionalCompleteness/image/XMPSidecarFileSupport']">
<measure ID="http://purl.org/DP/quality/measures#45">
	<name>XMP sidecar support</name>
	<description>Indicates if the action is able to handle XMP sidecar files.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#16">
		<name>image-specific features supported</name>
		<description>Are image-specific features supported by the action?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_completeness" scope="ALTERNATIVE_ACTION">
			<name>Functional completeness</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/relativeFileSize']">
<measure ID="http://purl.org/DP/quality/measures#123">
	<name>comparative file size</name>
	<description>Factor for relative output file size, calculated as: (size of output file / size of input file)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#41">
		<name>file size</name>
		<description>What happened to the size of the file?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/archivalProcess/clientMachinePresent']">
<measure ID="http://purl.org/DP/quality/measures#245">
	<name>archival client machine information documented</name>
	<description>Indicates whether documentation of the client machine which executed the archival process is present.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#84">
		<name>archival provenance information</name>
		<description>Provenance information on the archival process documented in outcome</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/archivalProcess/connectionURLPresent']">
<measure ID="http://purl.org/DP/quality/measures#246">
	<name>archival process connection url documented</name>
	<description>Indicates whether the connection-url used to connect to the database for executing the archival process is documented.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#84">
		<name>archival provenance information</name>
		<description>Provenance information on the archival process documented in outcome</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/archivalProcess/executingUserPresent']">
<measure ID="http://purl.org/DP/quality/measures#247">
	<name>archival process execution user documented</name>
	<description>Indicates whether the executing user of the archival process is documented.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#84">
		<name>archival provenance information</name>
		<description>Provenance information on the archival process documented in outcome</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/archivalProcess/executionLocationPresent']">
<measure ID="http://purl.org/DP/quality/measures#248">
	<name>archival process execution location present</name>
	<description>Indicates whether the execution location of the archival process is documented.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#84">
		<name>archival provenance information</name>
		<description>Provenance information on the archival process documented in outcome</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/archivalProcess/startTimePresent']">
<measure ID="http://purl.org/DP/quality/measures#249">
	<name>archival process start time present</name>
	<description>Indicates whether the start of the archival process is documented.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#84">
		<name>archival provenance information</name>
		<description>Provenance information on the archival process documented in outcome</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/disclosure/identificationPossibilities']">
<measure ID="http://purl.org/DP/quality/measures#153">
	<name>identification possibilities</name>
	<description>Possibilies to identify the outcome format (cf. PRONOM)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#55">
		<name>format disclosure</name>
		<description>Disclosure of the format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="automatic_specific/automatic_generic/automatic_tentative/manual/none" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/sustainability/disclosure/validationPossibilities']">
<measure ID="http://purl.org/DP/quality/measures#164">
	<name>format validation support</name>
	<description>Possibilies to validate conformance to a format.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#55">
		<name>format disclosure</name>
		<description>Disclosure of the format</description>
		<category ID="http://purl.org/DP/quality/categories#format_sustainability" scope="ALTERNATIVE_ACTION">
			<name>Format sustainability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="automatic/manual/none" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/dataOwner#retained']">
<measure ID="http://purl.org/DP/quality/measures#322">
	<name>database data owner retained</name>
	<description>Owner of the data in the database</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/databaseName#retained']">
<measure ID="http://purl.org/DP/quality/measures#323">
	<name>database name retained</name>
	<description>Name of the database</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/databasePurpose#retained']">
<measure ID="http://purl.org/DP/quality/measures#324">
	<name>database purpose retained</name>
	<description>Purpose of the database</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /325 -->
<!-- no old property for measure /326 -->
<!-- no old property for measure /327 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/privileges#retained']">
<measure ID="http://purl.org/DP/quality/measures#328">
	<name>database descriptive metadata for privileges retained</name>
	<description>Indicates whether database descriptive metadata for privileges have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /329 -->
<!-- no old property for measure /330 -->
<!-- no old property for measure /331 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/sequences#retained']">
<measure ID="http://purl.org/DP/quality/measures#332">
	<name>database descriptive metadata for sequences retained</name>
	<description>Indicates whether database descriptive metadata for sequences have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /333 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/traceability/changeHistoryAndChangelog#retained']">
<measure ID="http://purl.org/DP/quality/measures#334">
	<name>database change history and changelog retained</name>
	<description>Indicates whether database change history and changelog have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/traceability/explicitAuditTrail#retained']">
<measure ID="http://purl.org/DP/quality/measures#335">
	<name>database explicit audit trail retained</name>
	<description>Indicates whether database explicit audit trail has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/traceability/implicitAuditTrail#retained']">
<measure ID="http://purl.org/DP/quality/measures#336">
	<name>database implicit audit trail retained</name>
	<description>Indicates whether database implicit audit trail has been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/triggers#retained']">
<measure ID="http://purl.org/DP/quality/measures#337">
	<name>database descriptive metadata for triggers retained</name>
	<description>Indicates whether database descriptive metadata for triggers have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/users#retained']">
<measure ID="http://purl.org/DP/quality/measures#338">
	<name>database descriptive metadata for users retained</name>
	<description>Indicates whether database descriptive metadata for users have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/views#retained']">
<measure ID="http://purl.org/DP/quality/measures#339">
	<name>database descriptive metadata for views retained</name>
	<description>Indicates whether database descriptive metadata for views have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#62">
		<name>descriptive metadata for database elements</name>
		<description>Context information of the database</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/database/supportForColumnTypeDateTime']">
<measure ID="http://purl.org/DP/quality/measures#144">
	<name>format support for column type datetime</name>
	<description>Indicates whether the outcome format supports the database column type DateTime.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#51">
		<name>format support for specific database features</name>
		<description>Indicators for the specific support of particular features that are present in databases</description>
		<category ID="http://purl.org/DP/quality/categories#format_quality_and_functionality" scope="ALTERNATIVE_ACTION">
			<name>Format quality and functionality</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/database/supportForColumnTypeNumber']">
<measure ID="http://purl.org/DP/quality/measures#145">
	<name>format support for column type number</name>
	<description>Indicates whether the outcome format supports the database column type Number.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#51">
		<name>format support for specific database features</name>
		<description>Indicators for the specific support of particular features that are present in databases</description>
		<category ID="http://purl.org/DP/quality/categories#format_quality_and_functionality" scope="ALTERNATIVE_ACTION">
			<name>Format quality and functionality</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/database/supportForColumnTypeString']">
<measure ID="http://purl.org/DP/quality/measures#146">
	<name>format support for column type string</name>
	<description>Indicates whether the outcome format supports the database column type String.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#51">
		<name>format support for specific database features</name>
		<description>Indicators for the specific support of particular features that are present in databases</description>
		<category ID="http://purl.org/DP/quality/categories#format_quality_and_functionality" scope="ALTERNATIVE_ACTION">
			<name>Format quality and functionality</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/image/interpretedData']">
<measure ID="http://purl.org/DP/quality/measures#165">
	<name>format stores interpreted data</name>
	<description>Indicates whether the format contains interpreted data (as opposed to original raw data plus interpretation instructions)</description>
	<attribute ID="http://purl.org/DP/quality/attributes#56">
		<name>content interpretation quality</name>
		<description>This refers to the degrees of quality of content representation, such as the question whether a digital photograph is represented as interpreted data or as raw sensor data with interpretation instructions</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://format/qualityAndFunctionality/image/qualityLossAtReinterpretating']">
<measure ID="http://purl.org/DP/quality/measures#166">
	<name>format reinterpretation quality loss</name>
	<description>Indicates whether a quality loss occurs at reinterpretating the content stored in a format</description>
	<attribute ID="http://purl.org/DP/quality/attributes#56">
		<name>content interpretation quality</name>
		<description>This refers to the degrees of quality of content representation, such as the question whether a digital photograph is represented as interpreted data or as raw sensor data with interpretation instructions</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="minimal/acceptable/not acceptable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/dc/data#retained']">
<measure ID="http://purl.org/DP/quality/measures#300">
	<name>DC: all metadata retained</name>
	<description>Indicates whether all DC metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#4">
		<name>image DC metadata</name>
		<description>The accuracy of Dubling Core (DC) metadata to an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/audio/music/synchronousToVideo']">
<measure ID="http://purl.org/DP/quality/measures#225">
	<name>game music synchronicity</name>
	<description>Indicates to which degree music appears synchronous to the related video.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#78">
		<name>audio synchronicity</name>
		<description>Degree to which audio is synchronous with video</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/severe errors apparent/small error noticeable/no error noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/audio/sound/synchronousToVideo']">
<measure ID="http://purl.org/DP/quality/measures#226">
	<name>game sound synchronicity</name>
	<description>Indicates to which degree sound appears synchronous to the related video.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#78">
		<name>audio synchronicity</name>
		<description>Degree to which audio is synchronous with video</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/severe errors apparent/small error noticeable/no error noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/audio/speech/synchronousToVideo']">
<measure ID="http://purl.org/DP/quality/measures#227">
	<name>game speech synchronicity</name>
	<description>Indicates to which degree speech appears synchronous to the related video.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#78">
		<name>audio synchronicity</name>
		<description>Degree to which audio is synchronous with video</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/severe errors apparent/small error noticeable/no error noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://maintainability/modifiability/customization']">
<measure ID="http://purl.org/DP/quality/measures#93">
	<name>ease of customization</name>
	<description>How easy is it to customize an action?</description>
	<attribute ID="http://purl.org/DP/quality/attributes#28">
		<name>customization</name>
		<description>Indicators for the degree to which it is possible to customize the action-tool according to specific user needs</description>
		<category ID="http://purl.org/DP/quality/categories#modifiability" scope="ALTERNATIVE_ACTION">
			<name>Modifiability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="good/acceptable/poor/not possible" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://performanceEfficiency/capacity/batchProcessingMaxFiles']">
<measure ID="http://purl.org/DP/quality/measures#96">
	<name>maximum number of files in batch processing</name>
	<description>Maximum number of files supported at one batch run.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#30">
		<name>batch processing capacity</name>
		<description>Capacity of an action in batch processing mode</description>
		<category ID="http://purl.org/DP/quality/categories#capacity" scope="ALTERNATIVE_ACTION">
			<name>Capacity</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/width']">
<measure ID="http://purl.org/DP/quality/measures#50">
	<name>image width in pixels</name>
	<description>The horizontal width of a digital image in pixel</description>
	<attribute ID="http://purl.org/DP/quality/attributes#19">
		<name>image size</name>
		<description>Dimensions of a digital image</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/width#equal']">
<measure ID="http://purl.org/DP/quality/measures#51">
	<name>image width equal</name>
	<description>true iff image width has been preserved.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#19">
		<name>image size</name>
		<description>Dimensions of a digital image</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/height']">
<measure ID="http://purl.org/DP/quality/measures#52">
	<name>image height in pixels</name>
	<description>The vertical height of a digital image in pixel</description>
	<attribute ID="http://purl.org/DP/quality/attributes#19">
		<name>image size</name>
		<description>Dimensions of a digital image</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<positiveIntegerScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveIntegerScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/height#equal']">
<measure ID="http://purl.org/DP/quality/measures#53">
	<name>image height equal</name>
	<description>true iff image height has been preserved.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#19">
		<name>image size</name>
		<description>Dimensions of a digital image</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/resolution']">
<measure ID="http://purl.org/DP/quality/measures#54">
	<name>image resolution as string (width x height) as string</name>
	<description>Width and height of an image in number of pixels represented as string (e.g. 1024x768).</description>
	<attribute ID="http://purl.org/DP/quality/attributes#19">
		<name>image size</name>
		<description>Dimensions of a digital image</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<freeTextScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</freeTextScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/document/content/characterSet#equal']">
<measure ID="http://purl.org/DP/quality/measures#357">
	<name>document character set retained</name>
	<description>Character set used in the document.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#64">
		<name>document content</name>
		<description>Information on content stored in document</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /358 -->
<xsl:template match="plato:criterion[@ID='outcome://object/document/content/font/textMarks']">
<measure ID="http://purl.org/DP/quality/measures#359">
	<name>document font text marks</name>
	<description>Indicates if font text marks exist in the document.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#64">
		<name>document content</name>
		<description>Information on content stored in document</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /360 -->
<!-- no old property for measure /361 -->
<!-- no old property for measure /362 -->
<!-- no old property for measure /363 -->
<!-- no old property for measure /364 -->
<!-- no old property for measure /365 -->
<!-- no old property for measure /366 -->
<!-- no old property for measure /367 -->
<!-- no old property for measure /368 -->
<!-- no old property for measure /369 -->
<!-- no old property for measure /370 -->
<!-- no old property for measure /371 -->
<!-- no old property for measure /372 -->
<!-- no old property for measure /373 -->
<!-- no old property for measure /374 -->
<!-- no old property for measure /375 -->
<!-- no old property for measure /376 -->
<!-- no old property for measure /377 -->
<!-- no old property for measure /378 -->
<!-- no old property for measure /379 -->
<!-- no old property for measure /380 -->
<!-- no old property for measure /381 -->
<xsl:template match="plato:criterion[@ID='action://usability/operability/configurationMethod']">
<measure ID="http://purl.org/DP/quality/measures#111">
	<name>configuration methods</name>
	<description>Means available for configuring an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#36">
		<name>configurability</name>
		<description>Configurability of an action</description>
		<category ID="http://purl.org/DP/quality/categories#operability" scope="ALTERNATIVE_ACTION">
			<name>Operability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="internal only/external parameter/configuration file" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://usability/operability/easeOfAdministration']">
<measure ID="http://purl.org/DP/quality/measures#114">
	<name>ease of adminstration</name>
	<description>Assessment of how easy it is to administer an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#36">
		<name>configurability</name>
		<description>Configurability of an action</description>
		<category ID="http://purl.org/DP/quality/categories#operability" scope="ALTERNATIVE_ACTION">
			<name>Operability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="good/acceptable/poor" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /116 -->
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/overlay/responseDelay']">
<measure ID="http://purl.org/DP/quality/measures#200">
	<name>response delay to overlay</name>
	<description>Overlay response delay</description>
	<attribute ID="http://purl.org/DP/quality/attributes#77">
		<name>input overlay support</name>
		<description>Degree to which overlays are supported</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/considerable delay/short delay/delay not noticeable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /201 -->
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/interactivity/input/overlay/usability']">
<measure ID="http://purl.org/DP/quality/measures#202">
	<name>input overlay usable</name>
	<description>Degree to which input overlay is usable</description>
	<attribute ID="http://purl.org/DP/quality/attributes#77">
		<name>input overlay support</name>
		<description>Degree to which overlays are supported</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="not applicable/not usable/usable" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/iptc/data#retained']">
<measure ID="http://purl.org/DP/quality/measures#290">
	<name>IPTC: all metadata retained</name>
	<description>Indicates whether all IPTC metadata has been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#3">
		<name>image IPTC metadata</name>
		<description>The accuracy of International Press Telecommunications Council (IPTC) metadata of an image.</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/documentation/boxArtworkAvailable']">
<measure ID="http://purl.org/DP/quality/measures#228">
	<name>box artwork available for video game</name>
	<description>Indicates whether box artwork is available.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#79">
		<name>video game documentation</name>
		<description>Availability of different types of documentation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/documentation/manualAvailable']">
<measure ID="http://purl.org/DP/quality/measures#229">
	<name>documentation of video game available</name>
	<description>Indicates whether the manual is available.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#79">
		<name>video game documentation</name>
		<description>Availability of different types of documentation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/documentation/originalMediaAvailable']">
<measure ID="http://purl.org/DP/quality/measures#230">
	<name>documentation of original video game media available</name>
	<description>Indicates whether information about the original game media is available.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#79">
		<name>video game documentation</name>
		<description>Availability of different types of documentation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/documentation/systemRequirementsSpecificationAvailable']">
<measure ID="http://purl.org/DP/quality/measures#231">
	<name>system requirements specification available</name>
	<description>Indicates whether an actual systems requirements specification is available.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#79">
		<name>video game documentation</name>
		<description>Availability of different types of documentation</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_transformation_independent_property" scope="OBJECT">
			<name>Functional correctness: Transformation Independent Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://reliability/maturity/stabilityIndicators/activeCommunity']">
<measure ID="http://purl.org/DP/quality/measures#104">
	<name>community activity</name>
	<description>Indicates if an action is used and supported by an active community</description>
	<attribute ID="http://purl.org/DP/quality/attributes#33">
		<name>community</name>
		<description>Assessment of the user/developer community: Is this action actively supported, maintained, improved, used?</description>
		<category ID="http://purl.org/DP/quality/categories#action_maintenance" scope="ALTERNATIVE_ACTION">
			<name>Action maintenance</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /109 -->
<xsl:template match="plato:criterion[@ID='action://reliability/maturity/stabilityIndicators/underDevelopment']">
<measure ID="http://purl.org/DP/quality/measures#110">
	<name>action under development</name>
	<description>Indicates whether the action-tool is still being further developed by an active community</description>
	<attribute ID="http://purl.org/DP/quality/attributes#33">
		<name>community</name>
		<description>Assessment of the user/developer community: Is this action actively supported, maintained, improved, used?</description>
		<category ID="http://purl.org/DP/quality/categories#action_maintenance" scope="ALTERNATIVE_ACTION">
			<name>Action maintenance</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no scale for measure /31 -->
<xsl:template match="plato:criterion[@ID='action://business/licencingSchema']">
<measure ID="http://purl.org/DP/quality/measures#175">
	<name>software licence source code</name>
	<description>Indicates if and in which way the source code of the software is accessible.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#11">
		<name>software licence of a preservation action</name>
		<description>These are the licence agreements of a preservation action</description>
		<category ID="http://purl.org/DP/quality/categories#action_licensing" scope="ALTERNATIVE_ACTION">
			<name>Action licensing</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="openSource/closedSource" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/gameInstallationNecessary']">
<measure ID="http://purl.org/DP/quality/measures#179">
	<name>installation of video game necessary</name>
	<description>Indicates whether it is necessary to install the game, before being able to play it.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#68">
		<name>video game set-up</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/videoGame/gameConfiguration']">
<measure ID="http://purl.org/DP/quality/measures#180">
	<name>configuration of video game</name>
	<description>The way game configuration takes place.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#68">
		<name>video game set-up</name>
		<description></description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="manual/pre-configured/not needed" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/xmp/validExif']">
<measure ID="http://purl.org/DP/quality/measures#128">
	<name>valid EXIF metadata</name>
	<description>Indicates if XMP-metadata contains valid EXIF data.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#44">
		<name>validity of XMP metadata</name>
		<description>Does XMP metadata conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/xmp/validIptc']">
<measure ID="http://purl.org/DP/quality/measures#130">
	<name>valid IPTC metadata</name>
	<description>Indicates if  XMP-metadata contains valid IPTC data.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#44">
		<name>validity of XMP metadata</name>
		<description>Does XMP metadata conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/image/metadata/xmp/validDc']">
<measure ID="http://purl.org/DP/quality/measures#131">
	<name>valid Doublin Core (DC) metadata</name>
	<description>Indicates if XMP-metadata contains valid Dublin Core data.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#44">
		<name>validity of XMP metadata</name>
		<description>Does XMP metadata conform to its specification?</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://performanceEfficiency/timeBehaviour/timePerMB']">
<measure ID="http://purl.org/DP/quality/measures#10">
	<name>elapsed time per MB</name>
	<description>elapsed processing time per Megabyte of input data, measured in milliseconds</description>
	<attribute ID="http://purl.org/DP/quality/attributes#6">
		<name>processing time</name>
		<description>processing time</description>
		<category ID="http://purl.org/DP/quality/categories#time_behaviour" scope="ALTERNATIVE_ACTION">
			<name>Time behaviour</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://performanceEfficiency/timeBehaviour/timePerSample']">
<measure ID="http://purl.org/DP/quality/measures#11">
	<name>elapsed time per object</name>
	<description>elapsed processing time per object processed, measured in milliseconds</description>
	<attribute ID="http://purl.org/DP/quality/attributes#6">
		<name>processing time</name>
		<description>processing time</description>
		<category ID="http://purl.org/DP/quality/categories#time_behaviour" scope="ALTERNATIVE_ACTION">
			<name>Time behaviour</name>
		</category>
	</attribute>
	<positiveFloatScale unit=""  >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</positiveFloatScale>
</measure>
</xsl:template>
<!-- no old property for measure /12 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/representation/dateTimesAccordingToSQL99Standard']">
<measure ID="http://purl.org/DP/quality/measures#241">
	<name>datetime elements conform to SQL-99</name>
	<description>Indicates if datetime representations accord to SQL-99 standard.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#82">
		<name>database data representation</name>
		<description>Compliance of data fields with standards</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_information_property" scope="OBJECT">
			<name>Functional correctness: Information Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://usability/learnability/documentation/availability']">
<measure ID="http://purl.org/DP/quality/measures#112">
	<name>documentation availability</name>
	<description>Availability of documentation to support the operations of an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#37">
		<name>documentation</name>
		<description>Is there good documentation available to support operations?</description>
		<category ID="http://purl.org/DP/quality/categories#operability" scope="ALTERNATIVE_ACTION">
			<name>Operability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="yes-free/yes-pay/no" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='action://usability/learnability/documentation/quality']">
<measure ID="http://purl.org/DP/quality/measures#113">
	<name>documentation quality</name>
	<description>Quality of documentation to support the operations of an action</description>
	<attribute ID="http://purl.org/DP/quality/attributes#37">
		<name>documentation</name>
		<description>Is there good documentation available to support operations?</description>
		<category ID="http://purl.org/DP/quality/categories#operability" scope="ALTERNATIVE_ACTION">
			<name>Operability</name>
		</category>
	</attribute>
	<ordinalScale unit="" restriction="complete/fragmentary/useless" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</ordinalScale>
</measure>
</xsl:template>
<!-- no old property for measure /387 -->
<!-- no old property for measure /388 -->
<!-- no old property for measure /389 -->
<!-- no old property for measure /394 -->
<!-- no old property for measure /395 -->
<!-- no old property for measure /396 -->
<!-- no old property for measure /397 -->
<!-- no old property for measure /398 -->
<!-- no old property for measure /399 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/blobs/bitstreams#retained']">
<measure ID="http://purl.org/DP/quality/measures#301">
	<name>bitstreams of database blobs retained</name>
	<description>Indicates whether bitstreams of database binary large objects (blobs) have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/blobs/contents#retained']">
<measure ID="http://purl.org/DP/quality/measures#302">
	<name>content of database blobs retained</name>
	<description>Indicates whether content of database binary large objects (blobs) have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/blobs/formatMetadata#retained']">
<measure ID="http://purl.org/DP/quality/measures#303">
	<name>database blobs format metadata retained</name>
	<description>Indicates whether format metadata of database binary large objects (blobs) have been retained.</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /304 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/constraints/checkConstraints#retained']">
<measure ID="http://purl.org/DP/quality/measures#305">
	<name>database check constraints retained</name>
	<description>Indicates whether database check constraints have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /306 -->
<!-- no old property for measure /307 -->
<!-- no old property for measure /308 -->
<!-- no old property for measure /309 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/roles#retained']">
<measure ID="http://purl.org/DP/quality/measures#310">
	<name>database roles retained</name>
	<description>Indicates whether database roles have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/routines#retained']">
<measure ID="http://purl.org/DP/quality/measures#311">
	<name>database routines retained</name>
	<description>Indicates whether database routines have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/context/schemas#retained']">
<measure ID="http://purl.org/DP/quality/measures#312">
	<name>database schemas retained</name>
	<description>Indicates whether database schemas have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/sequences#retained']">
<measure ID="http://purl.org/DP/quality/measures#313">
	<name>database sequences retained</name>
	<description>Indicates whether database sequences have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /314 -->
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/systemTables#retained']">
<measure ID="http://purl.org/DP/quality/measures#315">
	<name>database system tables retained</name>
	<description>Indicates whether database system tables have been retained</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/columns/tableColumnCounts#equal']">
<measure ID="http://purl.org/DP/quality/measures#316">
	<name>database table column counts equal</name>
	<description>Indicates whether the total number of columns of all tables is the same</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<xsl:template match="plato:criterion[@ID='outcome://object/database/content/tableRowCounts#equal']">
<measure ID="http://purl.org/DP/quality/measures#317">
	<name>database table row counts equal</name>
	<description>Indicates whether the total number of rows of all tables is the same</description>
	<attribute ID="http://purl.org/DP/quality/attributes#85">
		<name>database content</name>
		<description>Indicators for how well the content of the database has been preserved</description>
		<category ID="http://purl.org/DP/quality/categories#functional_correctness_representation_instance_property" scope="OBJECT">
			<name>Functional correctness: Representation Instance Property</name>
		</category>
	</attribute>
	<booleanScale unit="" restriction="Yes/No" >
		<changelog created="{$nowAsISO_8601str}" createdBy="xslt-migration" changed="{$nowAsISO_8601str}" changedBy="xslt-migration"/>
	</booleanScale>
</measure>
</xsl:template>
<!-- no old property for measure /318 -->
<!-- no old property for measure /319 -->
<!-- no old property for measure /320 -->
<!-- no old property for measure /321 -->

</xsl:stylesheet>