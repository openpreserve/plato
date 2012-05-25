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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.activity.InvalidActivityException;
import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Class responsible for evaluating XPath expressions on a given xml-document/string.
 * 
 * @author Markus Hamm
 */
public class XmlXPathEvaluator implements Serializable {
	private static final long serialVersionUID = -5184499592773830923L;
	
	/**
	 * xml data represented as document.
	 */
	private Document xmlDocument;
	
	public XmlXPathEvaluator() {
		this.xmlDocument = null;
	}
	
	/**
	 * Method responsible for setting the xml to query.
	 * 
	 * @param xml String representation of xml data.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
 
	 */
	public void setXmlToParse(String xml) throws SAXException, IOException, ParserConfigurationException {
		// Create a parsable document out of the xml string
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(false);

		DocumentBuilder builder = documentFactory.newDocumentBuilder();
		xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes()));
	}
	
	/**
	 * Method responsible for extracting a XPath value from the current xml-document.
	 * 
	 * @param xPathString
	 * @return XPath value extracted from current xml-document
	 * @throws InvalidActivityException
	 * @throws XPathExpressionException
	 */
	public String extractValue(String xPathString) throws InvalidActivityException, XPathExpressionException {
		if (xmlDocument == null) {
			throw new InvalidActivityException("No xml document set to parse.");
		}
		
		// Query the xml document by several XPath queries
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		
		XPathExpression expression = xPath.compile(xPathString);
		Object xx = expression.evaluate(xmlDocument, XPathConstants.STRING);
		String result = (String) expression.evaluate(xmlDocument, XPathConstants.STRING);
		
		return result;
	}
}
