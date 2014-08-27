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
    xmlns:plato="http://ifs.tuwien.ac.at/dp/plato"
    exclude-result-prefixes="java xalan xsi sparql plato">

<xsl:output method="html" indent="yes" encoding="UTF-8" />


<xsl:template match="plato:plans">
<html>
<head>
	<title>Preservation Plan</title>
	<link rel="stylesheet" href="bootstrap.min.css"/>
</head>
<body>
<div class="container">
	<xsl:apply-templates/>
</div>	
</body>

</html>	
</xsl:template>


<xsl:template match="*|text()|@*">
</xsl:template>


<xsl:template match="plato:plan">
	<h1>Plan: <xsl:value-of select="plato:properties/@name"/></h1>
	<h2>Basis</h2>
	<fieldset>
		<label>Identification Code:</label> <xsl:value-of select="plato:basis/@identificationCode"/> <br/>
		<label>Repository Identifier: </label> <xsl:value-of select="plato:properties/@repositoryIdentifier"/> <br/>
		<label>Description:</label> <p><xsl:value-of select="plato:properties/plato:description"/></p>
		<hr/>
		<label>Responsible Planners: </label> <xsl:value-of select="plato:properties/@author"/> <br/>
		<label>Organisation: </label> <xsl:value-of select="plato:properties/@organization"/> <br/>
	</fieldset>
	<xsl:apply-templates/>

</xsl:template>

<xsl:template match="plato:sampleRecords">
	<h2>Collection</h2>
	<fieldset>
		<label>Collection ID:</label> <xsl:value-of select="plato:collectionProfile/plato:collectionID"/> <br/>
		<label>Description:</label> <p><xsl:value-of select="plato:samplesDescription"/></p>
		<label>Type of Objects:</label> <p><xsl:value-of select="plato:collectionProfile/plato:typeOfObjects"/></p>
	</fieldset>
</xsl:template>


<xsl:template match="plato:alternatives">
	<h2>Alternatives</h2>
	<xsl:apply-templates/>	
</xsl:template>
<xsl:template match="plato:alternative[@name = ../../plato:recommendation/@alternativeName]">
	<fieldset>
		<legend><b>Selected Alternative: </b> <xsl:value-of select="@name"/></legend>
		<label>Description:</label> <p><xsl:value-of select="plato:description"/></p>
		<hr/>
		<label>Reasoning:</label> <p><xsl:value-of select="../../plato:recommendation/plato:reasoning"/></p>
		<label>Effects of applying recommended solution:</label> <p><xsl:value-of select="../../plato:recommendation/plato:effects"/></p>
	</fieldset>

	<hr/>
</xsl:template>

<xsl:template match="plato:alternative[@name != ../../plato:recommendation/@alternativeName]">
	<fieldset>
		<legend>Alternative: <xsl:value-of select="@name"/> <xsl:if test="@discarded ='true'"><b> - discarded</b> </xsl:if>   </legend>
		<label>Description:</label> <p><xsl:value-of select="plato:description"/></p>
	</fieldset>

</xsl:template>



</xsl:stylesheet>