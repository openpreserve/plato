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
package eu.scape_project.planning.evaluation.evaluators;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.values.Value;

/**
 * This is a generic helper class that takes an XPath expression and uses
 * it to search a specified XML and extract a @link {@link Value} 
 * @author cb
 *
 */
public class XmlExtractor implements Serializable {
	private static final long serialVersionUID = -8844757294292266759L;

	private static Logger log = LoggerFactory.getLogger(XmlExtractor.class);
			
    private NamespaceContext namespaceContext;
    
    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public void setNamespaceContext(NamespaceContext context) {
        this.namespaceContext = context;
    }

    public Value extractValue(Document xml, Scale scale, String xpath, String commentXPath) {
        try {
            Document pcdlDoc = xml;
            String text = extractTextInternal(pcdlDoc,xpath);
            Value v = scale.createValue();
            v.parse(text);
            if (commentXPath != null) {
                String comment = extractTextInternal(pcdlDoc,commentXPath);
                v.setComment(comment);
            }
            return v;
            
        } catch (Exception e) {
            log.error(
                    "Could not parse XML " +
                    " searching for path "+xpath+
                    ": "+e.getMessage(),
                    e);  
            return null;
        } 
    }
    
    public String extractText(Document xml, String xpath) {
        try {
            Document pcdlDoc = xml;
            String text = extractTextInternal(pcdlDoc,xpath);
            return text;
        } catch (Exception e) {
            log.error(
                    "Could not parse XML " +
                    " searching for path "+xpath+
                    ": "+e.getMessage(),
                    e);  
            return null;
        }         
    }
    

    public Document getDocument(InputSource xml)
            throws ParserConfigurationException, SAXException, IOException {
        // extract value via XPath
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document pcdlDoc = builder.parse(xml);
        return pcdlDoc;
    }
    
    public Value extractAttributeValue() {
        return null;
    }
    
    /**
     * very useful: {@link http://www.ibm.com/developerworks/library/x-javaxpathapi.html}
     * @param doc
     * @param path
     * @param scale
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    private  String extractTextInternal(Document doc, String path) 
    throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
    {
       
        XPathFactory factory = XPathFactory.newInstance();
        
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(namespaceContext);
        XPathExpression expr = xpath.compile(path);
        try {
            String result = (String) expr.evaluate(doc, XPathConstants.STRING);
            return result;
        } catch (Exception e) {
            log.error("XML extraction for path "+path+" failed: "+e.getMessage(),e);
            return "XML extraction for path "+path+" failed: "+e.getMessage();
        }
    }

    public HashMap<String, String> extractValues(Document xml, String path) {
        try {
            HashMap<String, String> resultMap = new HashMap<String, String>();
            
            XPathFactory factory = XPathFactory.newInstance();
            
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(namespaceContext);
            XPathExpression expr = xpath.compile(path);
            
            NodeList list = (NodeList) expr.evaluate(xml,  XPathConstants.NODESET);
            if (list != null) {
                for (int i = 0; i < list.getLength(); i++) {
                    Node n = list.item(i);
                    String content = n.getTextContent();
                    if (content != null) {
                        resultMap.put(n.getLocalName(), content);    
                    }
                }
            }
            return resultMap;
        } catch (Exception e) {
            log.error(
                    "Could not parse XML " +
                    " searching for path "+path+
                    ": "+e.getMessage(),
                    e);  
            return null;
        } 
    }
    
}
