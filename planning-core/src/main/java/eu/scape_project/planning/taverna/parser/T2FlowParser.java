/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.taverna.parser;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.planning.xml.ProjectImporter;
import eu.scape_project.planning.xml.SchemaResolver;
import eu.scape_project.planning.xml.ValidatingParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.jaxen.NamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Parser for t2flow files.
 */
public class T2FlowParser {
    private static Logger log = LoggerFactory.getLogger(T2FlowParser.class);

    protected static final String SCHEMA_LOCATION = "data/schemas/";

    protected static final NamespaceContext T2FLOW_NAMESPACE_CONTEXT = new T2FlowNamespaceContext();

    /**
     * Component profiles.
     */
    public enum ComponentProfile {

        MigrationAction("http://scape-project.eu/component/profile/migrationaction"), Characterisation(
            "http://scape-project.eu/component/profile/characterisation"), QA(
            "http://scape-project.eu/component/profile/qa"), ExecutablePlan(
            "http://scape-project.eu/component/profile/executableplan"), NoProfile("");

        private final String uri;

        /**
         * Creates a component profile based on the provided URI.
         * 
         * @param uri
         *            the uri of the profile
         */
        ComponentProfile(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return uri;
        }

        /**
         * Returns the ComponentProfile corresponding to the provided text.
         * 
         * @param text
         *            the text of the ComponentProfile
         * @return the ComponentProfile
         */
        public static ComponentProfile fromString(String text) {
            if (text != null) {
                for (ComponentProfile b : ComponentProfile.values()) {
                    if (text.equalsIgnoreCase(b.toString())) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }

    }

    /**
     * The parsed document.
     */
    private Document doc = null;

    /**
     * Creates a T2FlowParser from the inputstream.
     * 
     * @param t2flow
     *            the inputstream to parse
     * @return the parser
     * @throws TavernaParserException
     */
    public static T2FlowParser createParser(InputStream t2flow) throws TavernaParserException {
        T2FlowParser parser = new T2FlowParser();
        try {
            parser.initialise(t2flow);
        } catch (Exception e) {
            log.error("Error initialising T2FlowParser: {}", e.getMessage());
            throw new TavernaParserException("Error initialising T2FlowParser", e);
        }
        return parser;
    }

    /**
     * Initialises the parser by reading the the t2flow from the input stream
     * and parsing it.
     * 
     * @param t2flow
     *            the t2flow
     * @throws DocumentException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    protected void initialise(InputStream t2flow) throws DocumentException, SAXException, ParserConfigurationException {

        log.debug("Parsing inputstream");

        ValidatingParserFactory vpf = new ValidatingParserFactory();

        SAXParser parser = vpf.getValidatingParser();
        parser.setProperty(ValidatingParserFactory.JAXP_SCHEMA_SOURCE, ProjectImporter.TAVERNA_SCHEMA_URI);

        SAXReader reader = new SAXReader(parser.getXMLReader());
        reader.setValidation(true);

        SchemaResolver schemaResolver = new SchemaResolver();
        schemaResolver.addSchemaLocation(ProjectImporter.TAVERNA_SCHEMA_URI, SCHEMA_LOCATION
            + ProjectImporter.TAVERNA_SCHEMA);
        reader.setEntityResolver(schemaResolver);

        doc = reader.read(t2flow);
    }

    /**
     * Reads the profile annotation of the t2flow.
     * 
     * @return the profile the workflow adheres to
     * @throws TavernaParserException
     */
    public ComponentProfile getProfile() throws TavernaParserException {

        log.debug("Extracting profile");

        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Profile']/uri");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);

        String profileUri = "";
        if (node != null) {
            profileUri = node.getText();
        }

        // TODO: Is this a good idea?
        return ComponentProfile.fromString(profileUri);
    }

    /**
     * Reads the profile version annotation of the t2flow.
     * 
     * @return the profile version the workflow adheres to
     * @throws TavernaParserException
     */
    public String getProfileVersion() throws TavernaParserException {

        log.debug("Extracting profile version");

        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.ProfileVersion']/value");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Reads the ID annotation of the t2flow.
     * 
     * @return the id the workflow adheres to
     * @throws TavernaParserException
     */
    public String getId() throws TavernaParserException {

        log.debug("Extracting profile ID");

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper.createXPath("/t2f:workflow/t2f:dataflow[@role='top']/@id");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Reads the version annotation of the t2flow.
     * 
     * @return the version the workflow adheres to
     * @throws TavernaParserException
     */
    public String getVersion() throws TavernaParserException {

        log.debug("Extracting workflow version");

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Version']/value");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Reads the name annotation of the t2flow.
     * 
     * @return the name of the workflow
     * @throws TavernaParserException
     */
    public String getName() throws TavernaParserException {

        log.debug("Extracting workflow name");

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.DescriptiveTitle']/text");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Reads the description annotation of the t2flow.
     * 
     * @return the description of the workflow
     * @throws TavernaParserException
     */
    public String getDescription() throws TavernaParserException {

        log.debug("Extracting workflow description");

        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.FreeTextDescription']/text");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Reads the author annotation of the t2flow.
     * 
     * @return the author of the workflow
     * @throws TavernaParserException
     */
    public String getAuthor() throws TavernaParserException {

        log.debug("Extracting workflow author");

        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Author']/text");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Reads the owner annotation of the t2flow.
     * 
     * @return the owner of the workflow
     * @throws TavernaParserException
     */
    public String getOwner() throws TavernaParserException {

        log.debug("Extracting workflow owner");

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Owner']/text");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Reads the license annotation of the t2flow.
     * 
     * @return the license of the workflow
     * @throws TavernaParserException
     */
    public String getLicense() throws TavernaParserException {

        log.debug("Extracting workflow license");

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.License']/text");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    /**
     * Returns the input ports of the document.
     * 
     * @return
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getInputPorts() throws TavernaParserException {

        XPath xpath = DocumentHelper.createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:inputPorts/t2f:port");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        @SuppressWarnings("unchecked")
        List<Element> inputPortNodes = xpath.selectNodes(doc);

        return createPorts(inputPortNodes);
    }

    /**
     * Returns the input ports of the document.
     * 
     * @return a set of taverna ports
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getInputPorts(URI uri) throws TavernaParserException {

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:inputPorts/t2f:port[t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.URI']/uri/text()='"
                + uri.toString() + "']");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        @SuppressWarnings("unchecked")
        List<Element> inputPortNodes = xpath.selectNodes(doc);

        return createPorts(inputPortNodes);
    }

    /**
     * Returns the input ports of the document.
     * 
     * @return
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getOutputPorts() throws TavernaParserException {

        XPath xpath = DocumentHelper.createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:outputPorts/t2f:port");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        @SuppressWarnings("unchecked")
        List<Element> inputPortNodes = xpath.selectNodes(doc);

        return createPorts(inputPortNodes);
    }

    /**
     * Returns the input ports of the document.
     * 
     * @return
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getOutputPorts(URI uri) throws TavernaParserException {

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:outputPorts/t2f:port[t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.URI']/uri/text()='"
                + uri.toString() + "']");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        @SuppressWarnings("unchecked")
        List<Element> inputPortNodes = xpath.selectNodes(doc);

        return createPorts(inputPortNodes);
    }

    /**
     * Creates a set of Taverna ports for the provided nodes.
     * 
     * @param nodes
     *            the port nodes
     * @return the ports
     * @throws TavernaParserException
     */
    private Set<TavernaPort> createPorts(List<Element> nodes) throws TavernaParserException {

        Set<TavernaPort> ports = new HashSet<TavernaPort>(nodes.size());
        for (Element node : nodes) {
            ports.add(createPort(node));
        }
        return ports;
    }

    /**
     * Returns the port name of the provided port element.
     * 
     * @param portElement
     *            the port element
     * @return the name of the port
     * @throws TavernaParserException
     */
    private TavernaPort createPort(Element portElement) throws TavernaParserException {

        TavernaPort port = new TavernaPort();

        // Get name
        Element nameElement = portElement.element("name");
        port.setName(nameElement.getText());

        // Get depth
        Element depthElement = portElement.element("depth");
        if (depthElement != null) {
            Integer depth = Integer.parseInt(depthElement.getText());
            port.setDepth(depth);
        }

        // Get URIs
        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        XPath xpath = DocumentHelper
            .createXPath("t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.URI']/uri");

        xpath.setNamespaceContext(T2FLOW_NAMESPACE_CONTEXT);
        @SuppressWarnings("unchecked")
        List<Element> uriElements = xpath.selectNodes(portElement);

        try {
            Set<URI> uris = new HashSet<URI>();
            for (Element uriElement : uriElements) {
                URI uri = new URI(uriElement.getText());
                uris.add(uri);
            }
            port.setUris(uris);
        } catch (URISyntaxException e) {
            throw new TavernaParserException("URI element contains invalid URI", e);
        }

        return port;
    }

    // -------- Getter/Setter --------
    public Document getDoc() {
        return doc;
    }

}
