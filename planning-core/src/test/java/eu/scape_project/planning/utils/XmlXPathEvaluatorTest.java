/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.xml.sax.SAXException;

import eu.scape_project.planning.utils.XmlXPathEvaluator;

public class XmlXPathEvaluatorTest {

	private XmlXPathEvaluator xPathEvaluator;
	
	public XmlXPathEvaluatorTest() {
		xPathEvaluator = new XmlXPathEvaluator();
	}
	
	@Test(expected=Exception.class)
	public void setXmlToParseTest_passedNullStringThrowsException() throws SAXException, IOException, ParserConfigurationException {
		xPathEvaluator.setXmlToParse(null);
	}
	
	@Test(expected=Exception.class)
	public void setXmlToParseTest_passedEmptyStringThrowsException() throws SAXException, IOException, ParserConfigurationException {
		xPathEvaluator.setXmlToParse("");
	}

	@Test(expected=Exception.class)
	public void setXmlToParseTest_passedInvalidXmlThrowsException() throws SAXException, IOException, ParserConfigurationException {
		xPathEvaluator.setXmlToParse("This is not a valid xml. Thus, an exception should be thrown.");
	}

	@Test
	public void setXmlToParseTest_passedValidXmlReturnsWithoutErrors() throws SAXException, IOException, ParserConfigurationException {
		xPathEvaluator.setXmlToParse(exampleXml);
		assertTrue(true);
	}
	
	@Test
	public void extractValue_extractTagValueWorks() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		xPathEvaluator.setXmlToParse(exampleXml);
		String externalIdentifier = xPathEvaluator.extractValue("/fits/identification/identity/externalIdentifier");
		String version = xPathEvaluator.extractValue("/fits/identification/identity/version[@toolname='Jhove']");
		String bitsPerSample = xPathEvaluator.extractValue("/fits/metadata/image/bitsPerSample");
		
		assertEquals("fmt/44", externalIdentifier);
		assertEquals("1.02", version);
		assertEquals("8 8 8", bitsPerSample);
	}
	
	@Test
	public void extractValue_extractAttributeWorks() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		xPathEvaluator.setXmlToParse(exampleXml);
		String identityFormat = xPathEvaluator.extractValue("/fits/identification/identity/attribute::format");
		String externalIdentifierType = xPathEvaluator.extractValue("/fits/identification/identity/externalIdentifier/attribute::type");
		
		assertEquals("JPEG File Interchange Format", identityFormat);
		assertEquals("puid", externalIdentifierType);
	}

	
	// --------------- example xml string ---------------
	
	private String exampleXml =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"0.5.0\" timestamp=\"02.02.12 10:19\">" +
		"  <identification>" +
		"    <identity format=\"JPEG File Interchange Format\" mimetype=\"image/jpeg\">" +
		"      <tool toolname=\"Jhove\" toolversion=\"1.5\" />" +
		"      <tool toolname=\"file utility\" toolversion=\"5.03\" />" +
		"      <tool toolname=\"Exiftool\" toolversion=\"7.74\" />" +
		"      <tool toolname=\"Droid\" toolversion=\"3.0\" />" +
		"      <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.4GA\" />" +
		"      <version toolname=\"Jhove\" toolversion=\"1.5\">1.02</version>" +
		"      <externalIdentifier toolname=\"Droid\" toolversion=\"3.0\" type=\"puid\">fmt/44</externalIdentifier>" +
		"    </identity>" +
		"  </identification>" +
		"  <fileinfo>" +
		"    <size toolname=\"Jhove\" toolversion=\"1.5\">879394</size>" +
		"    <lastmodified toolname=\"Exiftool\" toolversion=\"7.74\" status=\"SINGLE_RESULT\">2012:02:02 10:19:15+01:00</lastmodified>" +
		"    <created toolname=\"Exiftool\" toolversion=\"7.74\" status=\"CONFLICT\">2008:03:14 11:31:48.98-07:00</created>" +
		"    <created toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.4GA\" status=\"CONFLICT\">2009:03:12 13:46:42</created>" +
		"    <filename toolname=\"OIS File Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">C:\\Users\\markus\\AppData\\Local\\Temp\\digitalobjects28353034298492856173259873</filename>" +
		"    <md5checksum toolname=\"OIS File Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">076e3caed758a1c18c91a0e9cae3368f</md5checksum>" +
		"    <fslastmodified toolname=\"OIS File Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">1328174355110</fslastmodified>" +
		"  </fileinfo>" +
		"  <filestatus>" +
		"    <well-formed toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">true</well-formed>" +
		"    <valid toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">true</valid>" +
		"    <message toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">Unknown TIFF IFD tag: 18246 offset=42</message>" +
		"    <message toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">Unknown TIFF IFD tag: 18249 offset=54</message>" +
		"    <message toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">Unknown TIFF IFD tag: 40093 offset=0</message>" +
		"    <message toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">Unknown TIFF IFD tag: 59932 offset=0</message>" +
		"    <message toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">Tag 34665 out of sequence offset=82</message>" +
		"    <message toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">Value offset not word-aligned: 191 offset=135</message>" +
		"    <message toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">IFD offset not word-aligned: 231</message>" +
		"  </filestatus>" +
		"  <metadata>" +
		"    <image>" +
		"      <byteOrder toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">big endian</byteOrder>" +
		"      <compressionScheme toolname=\"Jhove\" toolversion=\"1.5\">JPEG (old-style)</compressionScheme>" +
		"      <imageWidth toolname=\"Jhove\" toolversion=\"1.5\">1024</imageWidth>" +
		"      <imageHeight toolname=\"Jhove\" toolversion=\"1.5\">768</imageHeight>" +
		"      <colorSpace toolname=\"Jhove\" toolversion=\"1.5\" status=\"CONFLICT\">YCbCr</colorSpace>" +
		"      <colorSpace toolname=\"Exiftool\" toolversion=\"7.74\" status=\"CONFLICT\">RGB</colorSpace>" +
		"      <YCbCrSubSampling toolname=\"Exiftool\" toolversion=\"7.74\" status=\"SINGLE_RESULT\">1 1</YCbCrSubSampling>" +
		"      <orientation toolname=\"Exiftool\" toolversion=\"7.74\" status=\"SINGLE_RESULT\">normal*</orientation>" +
		"      <samplingFrequencyUnit toolname=\"Jhove\" toolversion=\"1.5\">in.</samplingFrequencyUnit>" +
		"      <xSamplingFrequency toolname=\"Jhove\" toolversion=\"1.5\" status=\"CONFLICT\">96</xSamplingFrequency>" +
		"      <xSamplingFrequency toolname=\"Exiftool\" toolversion=\"7.74\" status=\"CONFLICT\">72</xSamplingFrequency>" +
		"      <ySamplingFrequency toolname=\"Jhove\" toolversion=\"1.5\" status=\"CONFLICT\">96</ySamplingFrequency>" +
		"      <ySamplingFrequency toolname=\"Exiftool\" toolversion=\"7.74\" status=\"CONFLICT\">72</ySamplingFrequency>" +
		"      <bitsPerSample toolname=\"Jhove\" toolversion=\"1.5\">8 8 8</bitsPerSample>" +
		"      <samplesPerPixel toolname=\"Jhove\" toolversion=\"1.5\">3</samplesPerPixel>" +
		"      <exifVersion toolname=\"Exiftool\" toolversion=\"7.74\" status=\"SINGLE_RESULT\">0221</exifVersion>" +
		"      <lightSource toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.4GA\" status=\"SINGLE_RESULT\">unknown</lightSource>" +
		"    </image>" +
		"  </metadata>" +
		"</fits>";
}
