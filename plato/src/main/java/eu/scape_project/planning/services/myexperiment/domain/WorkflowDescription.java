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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Installation.Dependency;

/**
 * Description of a workflow of a myExperiment REST API response.
 */
@XmlRootElement
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
        private String fromMimetype;
        private String toMimetype;

        /**
         * Empty constructor needed for JAXB.
         */
        public MigrationPath() {
        }

        /**
         * Creates a new migration path.
         * 
         * @param fromMimetype
         *            the from mimetype
         * @param toMimetype
         *            the to mimetype
         */
        public MigrationPath(String fromMimetype, String toMimetype) {
            this.fromMimetype = fromMimetype;
            this.toMimetype = toMimetype;
        }

        public String getFromMimetype() {
            return fromMimetype;
        }

        public String getToMimetype() {
            return toMimetype;
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
    private List<Installation> installations = null;

    /**
     * Parses the components and returns the found migration paths of the top
     * workflow.
     * 
     * @return a list of migration paths
     * @throws XPathExpressionException
     * @throws IOException
     */
    public String getProfile() {

        if (profile == null) {
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
                            String statement = 
                                  "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                                + "PREFIX comp: <http://purl.org/DP/components#> "
                                + "SELECT ?profile WHERE { "
                                + "?wf comp:fits ?profile }"; 
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

        return profile;
    }

    /**
     * Parses the components and returns the found migration paths of the top
     * workflow.
     * 
     * @return a list of migration paths
     * @throws XPathExpressionException
     * @throws IOException
     */
    public List<MigrationPath> getMigrationPaths() {

        if (migrationPaths == null) {
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
                            String statement = 
                                  "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                                + "PREFIX comp: <http://purl.org/DP/components#> "
                                + "SELECT ?fromMimetype ?toMimetype WHERE { "
                                + "?migrationPath rdf:type comp:MigrationPath ." 
                                + "?migrationPath comp:fromMimetype ?fromMimetype ." 
                                + "?migrationPath comp:toMimetype ?toMimetype } ";
                            // @formatter:on

                            Query q = QueryFactory.create(statement, Syntax.syntaxARQ);
                            QueryExecution qe = QueryExecutionFactory.create(q, model);
                            ResultSet results = qe.execSelect();
                            try {
                                if ((results != null) && (results.hasNext())) {
                                    QuerySolution orgQs = results.next();
                                    String fromMimetype = orgQs.getLiteral("fromMimetype").getString();
                                    String toMimetype = orgQs.getLiteral("toMimetype").getString();

                                    MigrationPath m = new MigrationPath(fromMimetype, toMimetype);
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

        return migrationPaths;
    }

    /**
     * Parses the components and returns the found migration paths of the top
     * workflow.
     * 
     * @return a list of migration paths
     * @throws XPathExpressionException
     * @throws IOException
     */
    public List<Installation> getInstallations() {

        if (installations == null) {
            installations = new ArrayList<Installation>();

            for (Element el : components) {
                if (el.getNodeName().equals("components")) {
                    try {
                        Document doc = el.getOwnerDocument();
                        XPath xPath = XPathFactory.newInstance().newXPath();
                        NodeList nodes = (NodeList) xPath.evaluate(
                            "/components//processor/semantic_annotation/content", doc.getDocumentElement(),
                            XPathConstants.NODESET);

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
                        String statement = 
                              "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX comp: <http://purl.org/DP/components#> "
                            + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                            + "PREFIX cc: <http://creativecommons.org/ns#> "
                            + "SELECT ?installation ?environment ?depTitle ?depVersion ?depLicense WHERE { "
                            + "?installation rdf:type comp:Installation ." 
                            + "OPTIONAL { ?installation comp:hasEnvironment ?environment } ."
                            + "OPTIONAL { ?installation comp:dependsOn ?dependency } ."
                            + "OPTIONAL { ?dependency skos:prefLabel ?depTitle } ."
                            + "OPTIONAL { ?dependency comp:dependencyVersion ?depVersion } ."
                            + "OPTIONAL { ?dependency cc:license ?depLicense } . } "
                            + "ORDER BY ?installation";
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

                                environment = qs.getResource("environment") == null ? null : qs.getResource(
                                    "environment").getURI();

                                String dependencyTitle = qs.getLiteral("depTitle") == null ? null : qs.getLiteral(
                                    "depTitle").getString();
                                String dependencyVersion = qs.getLiteral("depVersion") == null ? null : qs.getLiteral(
                                    "depVersion").getString();
                                String dependencyLicense = qs.getResource("depLicense") == null ? null : qs
                                    .getResource("depLicense").getURI();

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
        return installations;
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
}
