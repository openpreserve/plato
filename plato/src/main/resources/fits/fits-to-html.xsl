<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:fits="http://hul.harvard.edu/ois/xml/ns/fits/fits_output"
 	xmlns:exsl="http://exslt.org/common"
    extension-element-prefixes="exsl"    
    exclude-result-prefixes="java xalan exsl fits xsi">

<xsl:output method="html" indent="yes" version="4.0" encoding="UTF-8" />


<xsl:template match="text()">
</xsl:template>

<xsl:template match="/">
<h1>FITS</h1>
   	<xsl:apply-templates select="fits:fits/*"/>
</xsl:template>


<xsl:template match="fits:fits/*[*]">
<h2><xsl:value-of select="concat(translate(substring(name(), 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), substring(name(), 2))"/></h2>
<ul>
	<xsl:apply-templates/>
</ul>
</xsl:template>

<xsl:template match="/fits:fits/fits:identification">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="/fits:fits/fits:identification/fits:identity">
<h2>Identification</h2>
<ul>
	<li>Format: <xsl:value-of select="@format"/>   Mimetype: <xsl:value-of select="@mimetype"/>
		<ul class="toolinfo"> <xsl:apply-templates select="fits:tool" /> </ul>
	</li>
	<li>Version: <xsl:value-of select="fits:version"/>
		<ul class="toolinfo"><li>Tool: <xsl:value-of select="fits:version/@toolname"/>	Version: <xsl:value-of select="fits:version/@toolversion"/></li></ul>
	</li>
	<li>PUID: <xsl:value-of select="fits:externalIdentifier[@type='puid']"/>
		<ul class="toolinfo"><li>Tool: <xsl:value-of select="fits:externalIdentifier[@type='puid']/@toolname"/>	Version: <xsl:value-of select="fits:externalIdentifier[@type='puid']/@toolversion"/></li></ul>
	</li>
</ul>
</xsl:template>

<xsl:template match="fits:tool">
	<li>Tool: <xsl:value-of select="@toolname"/>	Version: <xsl:value-of select="@toolversion"/></li>
</xsl:template>

<xsl:template match="//*[not(*) and text()]">
	<li><xsl:value-of select="name()"/>: <xsl:value-of select="text()"/>
		<xsl:if test="@toolname"><ul class="toolinfo"><li>Tool: <xsl:value-of select="@toolname"/>	Version: <xsl:value-of select="@toolversion"/></li></ul>
		</xsl:if>
	</li>
</xsl:template>
</xsl:stylesheet>