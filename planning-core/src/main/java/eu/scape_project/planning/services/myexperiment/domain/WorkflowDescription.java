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
package eu.scape_project.planning.services.myexperiment.domain;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Installation.Dependency;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Port.PredefinedParameter;

/**
 * Description of a workflow of a myExperiment REST API response.
 */
@XmlRootElement(name = "workflow")
public class WorkflowDescription extends WorkflowInfo {

    private static final String SEMANTIC_ANNOTATION_LANG = "N3";

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowDescription.class);

    /**
     * Type of a workflow.
     */
    @XmlRootElement(name = "type")
    public static class Type extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Uploader of a workflow.
     */
    @XmlRootElement(name = "uploader")
    public static class Uploader extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String content) {
            this.name = content;
        }
    }

    /**
     * License type of a workflow.
     */
    @XmlRootElement(name = "license-type")
    public static class LicenseType extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Tag of a workflow.
     */
    @XmlRootElement(name = "tag")
    public static class Tag extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * License type of a workflow.
     */
    @XmlRootElement(name = "rating")
    public static class Rating extends ResourceDescription {
        @XmlValue
        private String rating;

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }

    /**
     * Migration path.
     */
    public static class MigrationPath {
        private String sourceMimetype;
        private String targetMimetype;

        /**
         * Empty constructor needed for JAXB.
         */
        public MigrationPath() {
        }

        /**
         * Creates a new migration path.
         * 
         * @param sourceMimetype
         *            the source mimetype
         * @param targetMimetype
         *            the target mimetype
         */
        public MigrationPath(String sourceMimetype, String targetMimetype) {
            this.sourceMimetype = sourceMimetype;
            this.targetMimetype = targetMimetype;
        }

        public String getSourceMimetype() {
            return sourceMimetype;
        }

        public String getTargetMimetype() {
            return targetMimetype;
        }
    }

    /**
     * Accepted mimetypes.
     */
    public static class AcceptedMimetypes {
        private String leftMimetype;
        private String rightMimetype;

        /**
         * Empty constructor needed for JAXB.
         */
        public AcceptedMimetypes() {
        }

        /**
         * Creates a new accepted mimetypes.
         * 
         * @param leftMimetype
         *            the left mimetype
         * @param rightMimetype
         *            the right mimetype
         */
        public AcceptedMimetypes(String leftMimetype, String rightMimetype) {
            this.leftMimetype = leftMimetype;
            this.rightMimetype = rightMimetype;
        }

        public String getLeftMimetype() {
            return leftMimetype;
        }

        public String getRightMimetype() {
            return rightMimetype;
        }
    }

    /**
     * Installation.
     */
    public static class Installation {

        /**
         * Dependency.
         */
        public static class Dependency {
            private String name;
            private String version;
            private String license;

            /**
             * Empty constructor needed for JAXB.
             */
            public Dependency() {
            }

            /**
             * Creates a new dependency.
             * 
             * @param name
             *            the dependency name
             * @param version
             *            the version
             * @param license
             *            the license
             */
            public Dependency(String name, String version, String license) {
                this.name = name;
                this.version = version;
                this.license = license;
            }

            public String getName() {
                return name;
            }

            public String getVersion() {
                return version;
            }

            public String getLicense() {
                return license;
            }
        }

        private List<Dependency> dependencies;
        private String environment;

        /**
         * Empty constructor needed for JAXB.
         */
        public Installation() {
        }

        /**
         * Creates a new installation.
         * 
         * @param dependencies
         *            the dependencies
         * @param environment
         *            the environment
         */
        public Installation(List<Dependency> dependencies, String environment) {
            this.dependencies = dependencies;
            this.environment = environment;
        }

        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public String getEnvironment() {
            return environment;
        }
    }

    /**
     * Port.
     */
    public static class Port {

        /**
         * Predefined parameters.
         */
        public static class PredefinedParameter {
            private String value;
            private String description;

            /**
             * Empty constructor needed for JAXB.
             */
            public PredefinedParameter() {
            }

            /**
             * Creates a new predefined parameter.
             * 
             * @param value
             *            the value of the parameter
             * @param description
             *            the description of the parameter
             */
            public PredefinedParameter(String value, String description) {
                this.value = value;
                this.description = description;
            }

            public String getValue() {
                return value;
            }

            public String getDescription() {
                return description;
            }

        }

        private String name;

        private String description;

        private String value;

        private String relatedObject;

        private List<PredefinedParameter> predefinedParameters;

        /**
         * Empty constructor needed for JAXB.
         */
        public Port() {
        }

        /**
         * Creates a new port.
         * 
         * @param name
         *            the port name
         * @param description
         *            the port description
         */
        public Port(String name, String description) {
            this.name = name;
            this.description = description;
        }

        /**
         * Creates a new port.
         * 
         * @param name
         *            the port name
         * @param description
         *            the port description
         * @param value
         *            port type
         */
        public Port(String name, String description, String value) {
            this(name, description);
            this.value = value;
        }

        /**
         * Creates a new port.
         * 
         * @param name
         *            the port name
         * @param description
         *            the port description
         * @param value
         *            port type
         * @param relatedObject
         *            the related object
         */
        public Port(String name, String description, String value, String relatedObject) {
            this(name, description, value);
            this.relatedObject = relatedObject;
        }

        /**
         * Checks if this port is a parameter port.
         * 
         * @return true if this port is a parameter port, false otherwise
         */
        public boolean isParameterPort() {
            return ComponentConstants.VALUE_PARAMETER.equals(value) || predefinedParameters != null;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getValue() {
            return value;
        }

        public String getRelatedObject() {
            return relatedObject;
        }

        public List<PredefinedParameter> getPredefinedParameters() {
            return predefinedParameters;
        }

        private void setPredefinedParameters(List<PredefinedParameter> predefinedParameters) {
            this.predefinedParameters = predefinedParameters;
        }
    }

    @XmlElement
    private WorkflowDescription.Type type;

    @XmlElement
    private WorkflowDescription.Uploader uploader;

    private String preview;

    private String svg;

    @XmlElement(name = "license-type")
    private WorkflowDescription.LicenseType licenseType;

    @XmlElementWrapper
    @XmlElement(name = "tag")
    private List<Tag> tags;

    @XmlElementWrapper
    @XmlElement(name = "rating")
    private List<Rating> ratings;

    @XmlAnyElement
    private List<Element> components;

    private String profile = null;
    private List<MigrationPath> migrationPaths = null;
    private List<String> acceptedMimetype = null;
    private List<AcceptedMimetypes> acceptedMimetypes = null;
    private List<Installation> installations = null;
    private List<Port> inputPorts = null;
    private List<Port> outputPorts = null;
    private String dataflowId = null;

    /**
     * Reads additional metadata making it available via the getter methods.
     */
    public void readMetadata() {
        readDataflowId();
        readProfile();
        readInputPorts();
        readOutputPorts();
        readInstallations();
        readMigrationPaths();
        readAcceptedMimetype();
        readAcceptedMimetypes();
    }

    /**
     * Reads the dataflow id of the top dataflow.
     * 
     * @throws XPathExpressionException
     * @throws IOException
     */
    public void readDataflowId() {

        dataflowId = "";
        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    dataflowId = (String) xPath.evaluate("/components//dataflow[@role='top']/@id",
                        doc.getDocumentElement(), XPathConstants.STRING);
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting dataflow id from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Reads the profile of the top dataflow.
     * 
     * @throws XPathExpressionException
     * @throws IOException
     */
    public void readProfile() {

        profile = "";
        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate(
                        "/components//dataflow[@role='top']/semantic_annotation/content", doc.getDocumentElement(),
                        XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); ++i) {
                        Element pel = (Element) nodes.item(i);

                        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
                        String semanticAnnotation = pel.getTextContent();
                        semanticAnnotation = semanticAnnotation.replaceAll("<>", "_:wf");
                        Reader reader = new StringReader(semanticAnnotation);
                        model = model.read(reader, null, SEMANTIC_ANNOTATION_LANG);
                        reader.close();

                        // @formatter:off
                        String statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX dpc: <http://purl.org/DP/components#> " 
                            + "SELECT ?profile WHERE { "
                            + "?wf dpc:fits ?profile }";
                        // @formatter:on

                        Query q = QueryFactory.create(statement, Syntax.syntaxARQ);
                        QueryExecution qe = QueryExecutionFactory.create(q, model);
                        ResultSet results = qe.execSelect();
                        try {
                            if ((results != null) && (results.hasNext())) {
                                QuerySolution orgQs = results.next();
                                profile = orgQs.getResource("profile").getURI();
                            }
                        } finally {
                            qe.close();
                        }
                    }
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting profile from myExperiment response", e);
                } catch (IOException e) {
                    LOG.warn("Error reading profile annotation from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Reads the migration paths of the top dataflow.
     */
    public void readMigrationPaths() {
        migrationPaths = new ArrayList<MigrationPath>();

        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate(
                        "/components//dataflow[@role='top']/semantic_annotation/content", doc.getDocumentElement(),
                        XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); ++i) {
                        Element pel = (Element) nodes.item(i);

                        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
                        String semanticAnnotation = pel.getTextContent();
                        semanticAnnotation = semanticAnnotation.replaceAll("<>", "_:wf");
                        Reader reader = new StringReader(semanticAnnotation);
                        model = model.read(reader, null, SEMANTIC_ANNOTATION_LANG);
                        reader.close();

                        // @formatter:off
                        String statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX dpc: <http://purl.org/DP/components#> "
                            + "SELECT ?sourceMimetype ?targetMimetype WHERE { "
                            + "?migrationPath rdf:type dpc:MigrationPath ."
                            + "?migrationPath dpc:sourceMimetype ?sourceMimetype ."
                            + "?migrationPath dpc:targetMimetype ?targetMimetype } ";
                        // @formatter:on

                        Query q = QueryFactory.create(statement, Syntax.syntaxARQ);
                        QueryExecution qe = QueryExecutionFactory.create(q, model);
                        ResultSet results = qe.execSelect();
                        try {
                            while ((results != null) && (results.hasNext())) {
                                QuerySolution orgQs = results.next();
                                String sourceMimetype = orgQs.getLiteral("sourceMimetype").getString();
                                String targetMimetype = orgQs.getLiteral("targetMimetype").getString();

                                MigrationPath m = new MigrationPath(sourceMimetype, targetMimetype);
                                migrationPaths.add(m);
                            }
                        } finally {
                            qe.close();
                        }

                    }
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting migration paths from myExperiment response", e);
                } catch (IOException e) {
                    LOG.warn("Error reading migration path annotations from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Reads the handled mimetype of the top dataflow.
     */
    public void readAcceptedMimetype() {
        acceptedMimetype = new ArrayList<String>();

        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate(
                        "/components//dataflow[@role='top']/semantic_annotation/content", doc.getDocumentElement(),
                        XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); ++i) {
                        Element pel = (Element) nodes.item(i);

                        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
                        String semanticAnnotation = pel.getTextContent();
                        semanticAnnotation = semanticAnnotation.replaceAll("<>", "_:wf");
                        Reader reader = new StringReader(semanticAnnotation);
                        model = model.read(reader, null, SEMANTIC_ANNOTATION_LANG);
                        reader.close();

                        // @formatter:off
                        String statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX dpc: <http://purl.org/DP/components#> "
                            + "SELECT ?mimetype WHERE { "
                            + "?wf dpc:handlesMimetype ?mimetype }";
                        // @formatter:on

                        Query q = QueryFactory.create(statement, Syntax.syntaxARQ);
                        QueryExecution qe = QueryExecutionFactory.create(q, model);
                        ResultSet results = qe.execSelect();
                        try {
                            while ((results != null) && (results.hasNext())) {
                                QuerySolution orgQs = results.next();
                                String mimetype = orgQs.getLiteral("mimetype").getString();

                                acceptedMimetype.add(mimetype);
                            }
                        } finally {
                            qe.close();
                        }

                    }
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting accepted mimetype from myExperiment response", e);
                } catch (IOException e) {
                    LOG.warn("Error reading accepted mimetype annotations from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Reads the handled mimetype of the top dataflow.
     */
    public void readAcceptedMimetypes() {
        acceptedMimetypes = new ArrayList<AcceptedMimetypes>();

        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate(
                        "/components//dataflow[@role='top']/semantic_annotation/content", doc.getDocumentElement(),
                        XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); ++i) {
                        Element pel = (Element) nodes.item(i);

                        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
                        String semanticAnnotation = pel.getTextContent();
                        semanticAnnotation = semanticAnnotation.replaceAll("<>", "_:wf");
                        Reader reader = new StringReader(semanticAnnotation);
                        model = model.read(reader, null, SEMANTIC_ANNOTATION_LANG);
                        reader.close();

                        // @formatter:off
                        String statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX dpc: <http://purl.org/DP/components#> "
                            + "SELECT ?leftMimetype ?rightMimetype WHERE { "
                            + "?acceptedMimetypes rdf:type dpc:AcceptedMimetypes ."
                            + "?acceptedMimetypes dpc:handlesLeftMimetype ?leftMimetype ."
                            + "?acceptedMimetypes dpc:handlesRightMimetype ?rightMimetype } ";
                        // @formatter:on

                        Query q = QueryFactory.create(statement, Syntax.syntaxARQ);
                        QueryExecution qe = QueryExecutionFactory.create(q, model);
                        ResultSet results = qe.execSelect();
                        try {
                            while ((results != null) && (results.hasNext())) {
                                QuerySolution orgQs = results.next();
                                String leftMimetype = orgQs.getLiteral("leftMimetype").getString();
                                String rightMimetype = orgQs.getLiteral("rightMimetype").getString();

                                AcceptedMimetypes m = new AcceptedMimetypes(leftMimetype, rightMimetype);
                                acceptedMimetypes.add(m);
                            }
                        } finally {
                            qe.close();
                        }

                    }
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting accepted mimetypes from myExperiment response", e);
                } catch (IOException e) {
                    LOG.warn("Error reading accepted mimetypes annotations from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Reads the found installations paths of all workflows.
     */
    public void readInstallations() {
        installations = new ArrayList<Installation>();

        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate("/components//processor/semantic_annotation/content",
                        doc.getDocumentElement(), XPathConstants.NODESET);

                    StringBuilder combinedAnnotations = new StringBuilder();
                    for (int i = 0; i < nodes.getLength(); ++i) {
                        Element pel = (Element) nodes.item(i);
                        combinedAnnotations.append(pel.getTextContent());
                    }
                    String semanticAnnotations = replaceBlankNodes(combinedAnnotations.toString());

                    Model model = ModelFactory.createMemModelMaker().createDefaultModel();
                    Reader reader = new StringReader(semanticAnnotations);
                    model = model.read(reader, null, SEMANTIC_ANNOTATION_LANG);
                    reader.close();

                    // @formatter:off
                    String statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX dpc: <http://purl.org/DP/components#> "
                        + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                        + "PREFIX cc: <http://creativecommons.org/ns#> "
                        + "SELECT ?installation ?environment ?depTitle ?depVersion ?depLicense WHERE { "
                        + "?installation rdf:type dpc:Installation ."
                        + "OPTIONAL { ?installation dpc:hasEnvironment ?environment } ."
                        + "OPTIONAL { ?installation dpc:dependsOn ?dependency } ."
                        + "OPTIONAL { ?dependency skos:prefLabel ?depTitle } ."
                        + "OPTIONAL { ?dependency dpc:dependencyVersion ?depVersion } ."
                        + "OPTIONAL { ?dependency cc:license ?depLicense } . } " + "ORDER BY ?installation";
                    // @formatter:on

                    Query query = QueryFactory.create(statement, Syntax.syntaxARQ);
                    QueryExecution qe = QueryExecutionFactory.create(query, model);
                    ResultSet results = qe.execSelect();

                    try {
                        Resource prevInst = null;
                        List<Dependency> dependencies = new ArrayList<Dependency>();
                        String environment = "";
                        while ((results != null) && (results.hasNext())) {
                            QuerySolution qs = results.next();
                            Resource inst = qs.getResource("installation");

                            if (prevInst != null && prevInst != inst) {
                                Installation installation = new Installation(dependencies, environment);
                                installations.add(installation);
                                environment = "";
                                dependencies = new ArrayList<Dependency>();
                            }

                            environment = qs.getResource("environment") == null ? null : qs.getResource("environment")
                                .getURI();

                            String dependencyTitle = qs.getLiteral("depTitle") == null ? null : qs.getLiteral(
                                "depTitle").getString();
                            String dependencyVersion = qs.getLiteral("depVersion") == null ? null : qs.getLiteral(
                                "depVersion").getString();
                            String dependencyLicense = qs.getResource("depLicense") == null ? null : qs.getResource(
                                "depLicense").getURI();

                            dependencies.add(new Dependency(dependencyTitle, dependencyVersion, dependencyLicense));

                            prevInst = inst;
                        }
                        if (prevInst != null) {
                            Installation installation = new Installation(dependencies, environment);
                            installations.add(installation);
                        }
                    } finally {
                        qe.close();
                    }
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting installations from myExperiment response", e);
                } catch (IOException e) {
                    LOG.warn("Error reading installation annotation from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Reads the input ports of the top dataflow.
     */
    public void readInputPorts() {
        inputPorts = new ArrayList<Port>();

        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate("/components//dataflow[@role='top']/sources/source",
                        doc.getDocumentElement(), XPathConstants.NODESET);

                    for (int i = 0; i < nodes.getLength(); ++i) {
                        Element pel = (Element) nodes.item(i);
                        Port p = parsePort(pel);
                        if (p != null) {
                            inputPorts.add(p);
                        }
                    }
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting input ports from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Reads the output ports of the top dataflow.
     */
    public void readOutputPorts() {
        outputPorts = new ArrayList<Port>();

        for (Element el : components) {
            if (el.getNodeName().equals("components")) {
                try {
                    Document doc = el.getOwnerDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate("/components//dataflow[@role='top']/sinks/sink",
                        doc.getDocumentElement(), XPathConstants.NODESET);

                    for (int i = 0; i < nodes.getLength(); ++i) {
                        Element pel = (Element) nodes.item(i);
                        Port p = parsePort(pel);
                        if (p != null) {
                            outputPorts.add(p);
                        }
                    }
                } catch (XPathExpressionException e) {
                    LOG.warn("Error extracting ouput ports from myExperiment response", e);
                }
            }
        }
    }

    /**
     * Parses the provided element and creates a ParameterPort.
     * 
     * @param element
     *            the element to parse
     * @return the parameter port or null if the element is not parameter port
     */
    private Port parsePort(Element element) {
        Port port = null;
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            String portName = (String) xPath.evaluate("name", element, XPathConstants.STRING);
            String portDescription = (String) xPath
                .evaluate("descriptions/description", element, XPathConstants.STRING);

            String semanticAnnotations = replaceBlankNodes((String) xPath.evaluate("semantic_annotation/content",
                element, XPathConstants.STRING));

            Model model = ModelFactory.createMemModelMaker().createDefaultModel();
            Reader reader = new StringReader(semanticAnnotations);
            model = model.read(reader, null, SEMANTIC_ANNOTATION_LANG);

            // @formatter:off
            String statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX dpc: <http://purl.org/DP/components#> "
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "PREFIX cc: <http://creativecommons.org/ns#> " 
                + "SELECT ?port ?value ?relatedObject WHERE { { { "
                + "  ?port dpc:accepts ?value ." 
                + "} UNION { "
                + "  ?port dpc:provides ?value ."
                + "} } "
                + "OPTIONAL { ?port dpc:relatesTo ?relatedObject }"
                + "FILTER (!isBlank(?value))"
                + "}";
            // @formatter:on

            Query query = QueryFactory.create(statement, Syntax.syntaxARQ);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results = qe.execSelect();

            try {
                if (results != null && results.hasNext()) {
                    while (results != null && results.hasNext()) {
                        QuerySolution qs = results.next();
                        Resource value = qs.getResource("value");
                        port = new Port(portName, portDescription, value.getURI());
                        Resource relatedObject = qs.getResource("relatedObject");
                        if (relatedObject != null) {
                            port.relatedObject = relatedObject.getURI();
                        }
                    }
                } else {
                    port = new Port(portName, portDescription);
                }
            } finally {
                qe.close();
            }

            // Predefined parameters
            addPredefinedParameters(model, port);
        } catch (XPathExpressionException e) {
            LOG.warn("Error extracting port definition from myExperiment response", e);
        }

        return port;
    }

    /**
     * Parses predefined parameters of the provided model to the port.
     * 
     * @param model
     *            the model containing the measures
     * @param port
     *            the port
     */
    private void addPredefinedParameters(Model model, Port port) {
        // @formatter:off
        String statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX dpc: <http://purl.org/DP/components#> " 
            + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
            + "PREFIX cc: <http://creativecommons.org/ns#> " 
            + "SELECT ?port ?value ?description WHERE { "
            + "  ?port dpc:accepts ?parameter ." 
            + "  ?parameter dpc:parameterValue ?value ."
            + "  ?parameter dpc:parameterDescription ?description }";
        // @formatter:on

        Query query = QueryFactory.create(statement, Syntax.syntaxARQ);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        try {
            if (results.hasNext()) {
                List<PredefinedParameter> predefinedParameters = new ArrayList<PredefinedParameter>();
                while ((results != null) && (results.hasNext())) {
                    QuerySolution qs = results.next();
                    Literal value = qs.getLiteral("value");
                    Literal description = qs.getLiteral("description");
                    if (value != null && description != null) {
                        predefinedParameters.add(new PredefinedParameter(value.getString(), description.getString()));
                    }
                }
                port.setPredefinedParameters(predefinedParameters);
            }
        } finally {
            qe.close();
        }
    }

    /**
     * Replaces the blank node representation in semantic annotations with
     * unique variables.
     * 
     * @param semanticAnnotations
     *            the semantic annotations
     * @return the semantic annotations with variables as blank nodes
     */
    private String replaceBlankNodes(String semanticAnnotations) {
        Pattern p = Pattern.compile("<>");
        Matcher m = p.matcher(semanticAnnotations);
        int i = 0;
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "_:p" + i);
            i++;
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Returns the average rating of the workflow or 0 if the workflow was not
     * rated.
     * 
     * @return the average rating
     */
    public Double getAverageRating() {
        if (ratings == null || ratings.size() == 0) {
            return 0.0d;
        }
        int i = 0;
        for (Rating r : ratings) {
            i += Integer.parseInt(r.getRating());
        }
        return (double) i / ratings.size();
    }

    @Override
    public URI getResource() {
        try {
            return new URI(super.getResource() + "/versions/" + getVersion());
        } catch (URISyntaxException e) {
            LOG.warn("Error creating resource URI with version", e);
        }
        return super.getResource();
    }

    /**
     * Returns the input port with the provided name.
     * 
     * @param name
     *            the port name
     * @return the port or null if no port was found
     */
    public Port getInputPort(String name) {
        for (Port p : inputPorts) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Checks whether the provided workflow accepts the left and right mimetype.
     * 
     * @param leftMimetype
     *            left mimetype to check
     * @param rightMimetype
     *            right mimetype to check
     * @return true if the workflow accepts the mimetypes, false otherwise
     */
    public boolean acceptsMimetypes(final String leftMimetype, final String rightMimetype) {
        String leftWildcard = getMimetypeWildcard(leftMimetype);
        String rightWildcard = getMimetypeWildcard(leftMimetype);

        if ((leftMimetype == null || acceptedMimetype.contains(leftMimetype) || acceptedMimetype.contains(leftWildcard))
            && (rightMimetype == null || acceptedMimetype.contains(rightMimetype) || acceptedMimetype
                .contains(rightWildcard))) {
            return true;
        }

        for (AcceptedMimetypes m : acceptedMimetypes) {
            if ((leftMimetype == null || m.getLeftMimetype().equals(leftMimetype) || m.getLeftMimetype().equals(
                leftWildcard))
                && (rightMimetype == null || m.getRightMimetype().equals(rightMimetype) || m.getRightMimetype().equals(
                    rightWildcard))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the left part of the provided workflow can handle the mimetype.
     * 
     * @param mimetype
     *            the mimetype
     * @return true if the left part can handle the mimetype, false otherwise
     */
    public boolean acceptsLeftMimetype(final String mimetype) {
        if (mimetype == null) {
            return true;
        }

        String wildcardMimetype = getMimetypeWildcard(mimetype);

        if (acceptedMimetype.contains(mimetype) || acceptedMimetype.contains(wildcardMimetype)) {
            return true;
        }

        for (AcceptedMimetypes m : acceptedMimetypes) {
            if (m.getLeftMimetype().equals(mimetype) || m.getLeftMimetype().equals(wildcardMimetype)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the right part of the provided workflow can handle the
     * mimetype.
     * 
     * @param mimetype
     *            the mimetype
     * @return true if the right part can handle the mimetype, false otherwise
     */
    public boolean acceptsRightMimetype(final String mimetype) {
        if (mimetype == null) {
            return true;
        }

        String wildcardMimetype = getMimetypeWildcard(mimetype);

        if (acceptedMimetype.contains(mimetype) || acceptedMimetype.contains(wildcardMimetype)) {
            return true;
        }

        for (AcceptedMimetypes m : acceptedMimetypes) {
            if (m.getRightMimetype().equals(mimetype) || m.getRightMimetype().equals(wildcardMimetype)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the provided workflow can handle the mimetype.
     * 
     * @param mimetype
     *            the mimetype
     * @return true if the left part can handle the mimetype, false otherwise
     */
    public boolean handlesMimetype(final String mimetype) {
        if (mimetype == null) {
            return true;
        }

        String wildcardMimetype = getMimetypeWildcard(mimetype);

        if (acceptedMimetype.contains(mimetype) || acceptedMimetype.contains(wildcardMimetype)) {
            return true;
        }
        return false;
    }

    /**
     * Creates a wildcard mimetype by using the type of the provided mimetype
     * and '*' as subtype.
     * 
     * @param mimetype
     *            the base mimetype
     * @return the wildcard mimetype
     */
    private String getMimetypeWildcard(String mimetype) {
        if (mimetype == null) {
            return null;
        } else if ("".equals(mimetype)) {
            return "";
        }

        int position = mimetype.indexOf('/');
        return mimetype.substring(0, position >= 0 ? position : mimetype.length()) + "/*";
    }

    // ---------- getter/setter ----------
    public WorkflowDescription.Type getType() {
        return type;
    }

    public WorkflowDescription.Uploader getUploader() {
        return uploader;
    }

    public String getPreview() {
        return preview;
    }

    public String getSvg() {
        return svg;
    }

    public WorkflowDescription.LicenseType getLicenseType() {
        return licenseType;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public List<Element> getComponents() {
        return components;
    }

    public String getDataflowId() {
        return dataflowId;
    }

    public String getProfile() {
        return profile;
    }

    public List<MigrationPath> getMigrationPaths() {
        return migrationPaths;
    }

    public List<String> getAcceptedMimetype() {
        return acceptedMimetype;
    }

    public List<AcceptedMimetypes> getAcceptedMimetypes() {
        return acceptedMimetypes;
    }

    public List<Installation> getInstallations() {
        return installations;
    }

    public List<Port> getInputPorts() {
        return inputPorts;
    }

    public List<Port> getOutputPorts() {
        return outputPorts;
    }
}
