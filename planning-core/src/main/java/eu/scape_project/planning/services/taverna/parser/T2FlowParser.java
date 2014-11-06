/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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
package eu.scape_project.planning.services.taverna.parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.scape_project.planning.xml.ProjectImporter;
import eu.scape_project.planning.xml.SchemaResolver;
import eu.scape_project.planning.xml.ValidatingParserFactory;

/**
 * Parser for t2flow files.
 */
public class T2FlowParser {
    private static Logger log = LoggerFactory.getLogger(T2FlowParser.class);

    protected static final String SCHEMA_LOCATION = "data/schemas/";

    protected static final Map<String, String> T2FLOW_NAMESPACE_MAP = new HashMap<String, String>();

    /**
     * Component profiles.
     */
    public enum ComponentProfile {

        MigrationAction("http://scape-project.eu/component/profile/migrationaction"),
        Characterisation("http://scape-project.eu/component/profile/characterisation"),
        QA("http://scape-project.eu/component/profile/qa"),
        ExecutablePlan("http://scape-project.eu/component/profile/executableplan"),
        NoProfile("");

        private final String uri;

        /**
         * Creates a component profile based on the provided URI.
         * 
         * @param uri
         *            the URI of the profile
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
     *             if the parser could not be initialized
     */
    public static T2FlowParser createParser(InputStream t2flow) throws TavernaParserException {
        T2FlowParser parser = new T2FlowParser();
        parser.initialise(t2flow);
        return parser;
    }

    /**
     * Initialises the parser by reading the the t2flow from the input stream
     * and parsing it.
     * 
     * @param t2flow
     *            the t2flow
     * @throws TavernaParserException
     *             if the parser could not be initialized
     */
    protected void initialise(InputStream t2flow) throws TavernaParserException {

        log.debug("Parsing inputstream");

        T2FLOW_NAMESPACE_MAP.put("t2f", "http://taverna.sf.net/2008/xml/t2flow");

        ValidatingParserFactory vpf = new ValidatingParserFactory();
        try {
            SAXParser parser = vpf.getValidatingParser();
            parser.setProperty(ValidatingParserFactory.JAXP_SCHEMA_SOURCE, ProjectImporter.TAVERNA_SCHEMA_URI);

            SAXReader reader = new SAXReader(parser.getXMLReader());
            reader.setValidation(false);

            SchemaResolver schemaResolver = new SchemaResolver();
            schemaResolver.addSchemaLocation(ProjectImporter.TAVERNA_SCHEMA_URI, SCHEMA_LOCATION
                + ProjectImporter.TAVERNA_SCHEMA);
            reader.setEntityResolver(schemaResolver);

            doc = reader.read(t2flow);
        } catch (DocumentException e) {
            log.error("Error initialising T2FlowParser: {}", e.getMessage());
            throw new TavernaParserException("Error parsing workflow.", e);
        } catch (ParserConfigurationException e) {
            log.error("Error initialising T2FlowParser: {}", e.getMessage());
            throw new TavernaParserException("Error parsing workflow.", e);
        } catch (SAXException e) {
            log.error("Error initialising T2FlowParser: {}", e.getMessage());
            throw new TavernaParserException("Error parsing workflow.", e);
        }
    }

    /**
     * Reads the ID annotation of the t2flow.
     * 
     * @return the id the workflow adheres to
     * @throws TavernaParserException
     */
    public String getId() throws TavernaParserException {

        log.debug("Extracting profile ID");

        XPath xpath = DocumentHelper.createXPath("/t2f:workflow/t2f:dataflow[@role='top']/@id");

        xpath.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
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

        XPath xpath = DocumentHelper
            .createXPath("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.DescriptiveTitle']/text");

        xpath.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
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

        xpath.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
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

        xpath.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
        Node node = xpath.selectSingleNode(doc);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

    // -------- Getter/Setter --------
    public Document getDoc() {
        return doc;
    }
}
