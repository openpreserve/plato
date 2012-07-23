package eu.scape_project.planning.taverna;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.scape_project.planning.evaluation.evaluators.TavernaResultsEvaluator;

public class T2FlowParser {
    private static Logger log = LoggerFactory.getLogger(TavernaResultsEvaluator.class);

    public enum ComponentProfile {

        MigrationAction("http://scape-project.eu/component/profile/migrationaction"), Characterisation(
            "http://scape-project.eu/component/profile/characterisation"), QA(
            "http://scape-project.eu/component/profile/qa"), ExecutablePlan(
            "http://scape-project.eu/component/profile/executableplan"), NoProfile("");

        private final String uri;

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
     * The t2flow inputstream.
     */
    private InputStream t2flow = null;

    /**
     * The parsed document.
     */
    private Document doc = null;

    private T2FlowNamespaceContext nsc = new T2FlowNamespaceContext();

    /**
     * Creates a T2FlowParser from the inputstream
     * 
     * @param t2flow
     *            the inputstream to parse
     * @return the parser
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static T2FlowParser createParser(InputStream t2flow) throws ParserConfigurationException, SAXException,
        IOException {
        T2FlowParser parser = new T2FlowParser();
        parser.initialise(t2flow);
        return parser;
    }

    /**
     * Initialises the parser by reading the the t2flow from the input stream
     * and parsing it.
     * 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void initialise(InputStream t2flow) throws ParserConfigurationException, SAXException, IOException {
        this.t2flow = t2flow;
        log.debug("Parsing inputstream");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        doc = builder.parse(t2flow);
    }

    /**
     * Reads the profile annotation of the t2flow.
     * 
     * @return the profile the workflow adheres to
     * @throws TavernaParserException
     */
    public ComponentProfile getProfile() throws TavernaParserException {

        log.debug("Extracting profile");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Profile']/uri");

            String profileUri = (String) expr.evaluate(doc, XPathConstants.STRING);

            // TODO: Is this a good idea?
            return ComponentProfile.fromString(profileUri);

        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
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

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            // TODO: Fix XPath to use correct annotation class after annotation
            // is implemented in Taverna
            XPathExpression expr = xPath.compile("/t2f:workflow/t2f:dataflow[@role='top']/@id");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Reads the version annotation of the t2flow.
     * 
     * @return the version the workflow adheres to
     * @throws TavernaParserException
     */
    public String getVersion() throws TavernaParserException {

        log.debug("Extracting profile version");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            // TODO: Fix XPath to use correct annotation class after annotation
            // is implemented in Taverna
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Version']/value");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Reads the profile version annotation of the t2flow.
     * 
     * @return the profile version the workflow adheres to
     * @throws TavernaParserException
     */
    public String getProfileVersion() throws TavernaParserException {

        log.debug("Extracting profile version");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.ProfileVersion']/value");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Reads the name annotation of the t2flow.
     * 
     * @return the name of the workflow
     * @throws TavernaParserException
     */
    public String getName() throws TavernaParserException {

        log.debug("Extracting workflow name");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);
        try {
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.DescriptiveTitle']/text");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Reads the description annotation of the t2flow.
     * 
     * @return the description of the workflow
     * @throws TavernaParserException
     */
    public String getDescription() throws TavernaParserException {

        log.debug("Extracting workflow description");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);
        try {
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.FreeTextDescription']/text");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Reads the author annotation of the t2flow.
     * 
     * @return the author of the workflow
     * @throws TavernaParserException
     */
    public String getAuthor() throws TavernaParserException {

        log.debug("Extracting workflow author");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Author']/text");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Reads the owner annotation of the t2flow.
     * 
     * @return the owner of the workflow
     * @throws TavernaParserException
     */
    public String getOwner() throws TavernaParserException {

        log.debug("Extracting workflow owner");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            // TODO: Fix XPath to use correct annotation class after annotation
            // is implemented in Taverna
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Owner']/text");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Reads the license annotation of the t2flow.
     * 
     * @return the license of the workflow
     * @throws TavernaParserException
     */
    public String getLicense() throws TavernaParserException {

        log.debug("Extracting workflow license");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            // TODO: Fix XPath to use correct annotation class after annotation
            // is implemented in Taverna
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.License']/text");

            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Returns the input ports of the document
     * 
     * @return
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getInputPorts() throws TavernaParserException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            XPathExpression expr = xPath.compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:inputPorts/t2f:port");
            NodeList inputPortNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            return createPorts(inputPortNodes);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Returns the input ports of the document
     * 
     * @return
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getInputPorts(URI uri) throws TavernaParserException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            // TODO: Fix XPath to use correct annotation class after annotation
            // is implemented in Taverna
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:inputPorts/t2f:port[t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.URI']/uri/text()='"
                    + uri.toString() + "']");
            NodeList inputPortNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            return createPorts(inputPortNodes);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Returns the input ports of the document
     * 
     * @return
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getOutputPorts() throws TavernaParserException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            XPathExpression expr = xPath.compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:outputPorts/t2f:port");
            NodeList outputPortNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            return createPorts(outputPortNodes);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Returns the input ports of the document
     * 
     * @return
     * @throws TavernaParserException
     */
    public Set<TavernaPort> getOutputPorts(URI uri) throws TavernaParserException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        try {
            // TODO: Fix XPath to use correct annotation class after annotation
            // is implemented in Taverna
            XPathExpression expr = xPath
                .compile("/t2f:workflow/t2f:dataflow[@role='top']/t2f:outputPorts/t2f:port[t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.URI']/uri/text()='"
                    + uri.toString() + "']");
            NodeList outputPortNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            return createPorts(outputPortNodes);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        }
    }

    /**
     * Creates a set of Taverna ports for the provided nodes.
     * 
     * @param nodes
     *            the port nodes
     * @return the ports
     * @throws TavernaParserException
     */
    private Set<TavernaPort> createPorts(NodeList nodes) throws TavernaParserException {

        Set<TavernaPort> ports = new HashSet<TavernaPort>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            Element portNode = (Element) nodes.item(i);
            ports.add(createPort(portNode));
        }

        return ports;
    }

    /**
     * Returns the port name of the provided port element.
     * 
     * @param portElement
     *            the port element
     * @return the name of the port
     * @throws XPathExpressionException
     */
    private TavernaPort createPort(Element portElement) throws TavernaParserException {

        TavernaPort port = new TavernaPort();

        // Get name
        NodeList nameNodes = portElement.getElementsByTagNameNS(nsc.getNamespaceURI("t2f"), "name");

        if (nameNodes.getLength() != 1) {
            throw new TavernaParserException("Not a valid port element");
        }
        port.setName(((Element) nameNodes.item(0)).getTextContent());

        // Get depth
        NodeList depthNodes = portElement.getElementsByTagNameNS(nsc.getNamespaceURI("t2f"), "depth");

        if (depthNodes.getLength() != 1) {
            throw new TavernaParserException("Not a valid port element");
        }
        Integer depth = Integer.parseInt(((Element) depthNodes.item(0)).getTextContent());
        port.setDepth(depth);

        // Get URIs
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(nsc);

        XPathExpression expr;
        try {
            // TODO: Fix XPath to use correct annotation class after annotation
            // is implemented in Taverna
            expr = xPath
                .compile("t2f:annotations/*/*/*/*/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.URI']/uri");

            NodeList uriNodes = (NodeList) expr.evaluate(portElement, XPathConstants.NODESET);

            Set<URI> uris = new HashSet<URI>();
            for (int i = 0; i < uriNodes.getLength(); i++) {
                Element uriElement = (Element) uriNodes.item(i);
                URI uri = new URI(uriElement.getTextContent());
                uris.add(uri);
            }
            port.setUris(uris);
        } catch (XPathExpressionException e) {
            throw new TavernaParserException(e);
        } catch (URISyntaxException e) {
            throw new TavernaParserException("URI element contains invalid URI", e);
        }

        return port;
    }
}
