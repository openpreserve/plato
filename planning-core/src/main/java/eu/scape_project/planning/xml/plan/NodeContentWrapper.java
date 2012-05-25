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
package eu.scape_project.planning.xml.plan;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Digester helper class for {@link eu.scape_project.planning.xml.ProjectImporter}:
 * 
 * For parsing XML data contained in a Node, and pass it on as string.
 * Currently only used for definition of executable plan.
 *    
 * @author Michael Kraxner
 *
 */
public class NodeContentWrapper implements Serializable{
    
    private static final long serialVersionUID = 981504451654217666L;

    Element node = null;    

    /**
     * @param value
     */
    public void setNode(Object value) {
        if (value instanceof Element) {
            node = (Element)value;
        } else {
            node = null;
        }
    }
    
    public void setNodeContentEPrintsPlan(Object target, String setter) {
        if ((target == null) || ((setter == null) || "".equals(setter))) {
            return;
        }
        try {
            String value = "";
            if (node != null) {
                StringWriter stringWriter = new StringWriter();
                try {
                    // node is a w3c element - create a w3c document                    
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    // add missing xsi namespace
                    Document w3cDoc = builder.newDocument();
                    Node dup = w3cDoc.importNode(node, true);
                    w3cDoc.appendChild(dup);
                    // print document
                    OutputFormat xmlFormat = new OutputFormat("xml","ISO-8859-1", true);
                    xmlFormat.setOmitXMLDeclaration(true);
                    XMLSerializer serializer = new XMLSerializer(stringWriter, xmlFormat);
                    serializer.serialize(w3cDoc);
                    
                    value = stringWriter.toString();
                } catch (ParserConfigurationException e) { 
                    e.printStackTrace();
                }   catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            // finally: call setter on target object
            Method setData = target.getClass().getMethod(setter, String.class);
            setData.invoke(target, new Object[]{value});
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
    }
    
    
    public void setNodeContent(Object target, String setter) {
        if ((target == null) || ((setter == null) || "".equals(setter))) {
            return;
        }
        try {
            String value = "";
            if (node != null) {
                StringWriter stringWriter = new StringWriter();
                try {
                    // node is a w3c element - create a w3c document                    
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    // add missing xsi namespace
                    node.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.planets-project.eu/plato");
                    node.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                    node.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wdt", "http://www.planets-project.eu/wdt");
                    normalizeChildPrefixes(node, "wdt");
                    Document w3cDoc = builder.newDocument();
                    Node dup = w3cDoc.importNode(node, true);
                    w3cDoc.appendChild(dup);
                    // print document
                    OutputFormat xmlFormat = new OutputFormat("xml","ISO-8859-1", true);
                    xmlFormat.setOmitXMLDeclaration(true);
                    XMLSerializer serializer = new XMLSerializer(stringWriter, xmlFormat);
                    serializer.serialize(w3cDoc);
                    
                    value = stringWriter.toString();
                } catch (ParserConfigurationException e) { 
                    e.printStackTrace();
                }   catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            // finally: call setter on target object
            Method setData = target.getClass().getMethod(setter, String.class);
            setData.invoke(target, new Object[]{value});
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    private void normalizeChildPrefixes(Element node, String prefix) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                ((Element)child).setPrefix(prefix);
                normalizeChildPrefixes((Element)child, prefix);
            }
            
        }
    }
    /**
     * Invokes the function setter on <code>object</code> via reflection 
     * - with content of previously stored node as parameter.  
     *      
     * @param object
     */
//    public void setContent(Object object) {
//        try {
//            String value = "";
//            if (node != null) {
//                StringWriter stringWriter = new StringWriter();
//                XMLWriter writer = new XMLWriter(stringWriter, ProjectExporter.prettyFormat);
//                try {
//                    // node is a w3c element - create a w3c document                    
//                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                    factory.setNamespaceAware(true);
//                    DocumentBuilder builder = factory.newDocumentBuilder();
//
//                    node.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//                    Document w3cDoc = builder.newDocument();
//                    Node dup = w3cDoc.importNode(node, true);
//                    w3cDoc.appendChild(dup); 
////                    w3cDoc.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//                    OutputFormat xmlFormat = new OutputFormat("xml","ISO-8859-1", true);
//                    xmlFormat.setOmitXMLDeclaration(true);
//                    XMLSerializer serializer = new XMLSerializer(stringWriter, xmlFormat);
//                    serializer.serialize(w3cDoc);
//                    
////                    // Convert w3c document to dom4j document
////                    DOMReader reader = new DOMReader();
////                    org.dom4j.Document dom4jDoc = reader.read( w3cDoc);
////                    // and write dom4j document
////                    writer.write(dom4jDoc);
////                    writer.close();
//                    value = stringWriter.toString();
//                } catch (ParserConfigurationException e) { 
//                    e.printStackTrace();
//                }   catch (IOException e) {
//                    e.printStackTrace();
//                } 
//            }
//            Method setData = object.getClass().getMethod(setter, String.class);
//            setData.invoke(object, new Object[]{value});
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }

}
