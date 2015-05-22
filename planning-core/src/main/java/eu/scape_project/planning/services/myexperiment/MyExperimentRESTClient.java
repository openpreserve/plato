/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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
package eu.scape_project.planning.services.myexperiment;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.apache.commons.configuration.Configuration;
import org.apache.jena.atlas.io.IndentedLineBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.expr.E_StrContains;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.serializer.FormatterElement;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.scape_project.planning.services.PlanningServiceException;
import eu.scape_project.planning.services.myexperiment.domain.SearchResult;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowInfo;
import eu.scape_project.planning.utils.ConfigurationLoader;

/**
 * Client to access the REST interface of a myExperiment instance.
 */
public class MyExperimentRESTClient implements Serializable {

    private static final long serialVersionUID = -569647899543358671L;

    private static final Logger LOG = LoggerFactory.getLogger(MyExperimentRESTClient.class);

    /**
     * Elements to request when querying.
     */
    private static final String QUERY_ELEMENTS = "id,title,description,content-uri,content-type";

    /**
     * Elements to request for workflow details.
     */
    private static final String WORKFLOW_ELEMENTS = "id,title,description,type,uploader,preview,svg,license-type,content-uri,content-type,tags,ratings,components";

    private static final int WORKFLOW_URL_GROUP = 1;

    /**
     * Pattern for guessing descriptor URL.
     */
    private static final Pattern WORKFLOW_DL_PATTERN = Pattern
        .compile("(.+\\:\\/\\/.+)workflows\\/(\\d+)(\\.html|/download)(/.+?)?([?&]version=(\\d+))?");

    /**
     * Pattern group number for id.
     */
    private static final int WORKFLOW_PATH_ID_GROUP = 2;

    /**
     * Pattern group number for version.
     */
    private static final int WORKFLOW_PATH_VERSION_GROUP = 4;

    /**
     * Pattern for guessing descriptor URL.
     */
    private static final Pattern WORKFLOW_PATH_PATTERN = Pattern
        .compile("(.+\\:\\/\\/.+)workflows\\/(\\d+)(/versions/(\\d+))?/?");

    /**
     * Pattern group number for id.
     */
    private static final int WORKFLOW_DL_ID_GROUP = 2;

    /**
     * Pattern group number for version.
     */
    private static final int WORKFLOW_DL_VERSION_GROUP = 6;

    /**
     * Describes a query for components using the myExperiment REST endpoint.
     */
    public final class ComponentQuery {
        private static final String PREFIX_NAME = "prefixes";
        private static final String QUERY_NAME = "query";

        private static final String ONTOLOGY_IRI = "http://purl.org/DP/components#";
        private static final String ONTOLOGY_PREFIX = "dpc";

        private static final String WFDESC_IRI = "http://purl.org/wf4ever/wfdesc#";
        private static final String WFDESC_PREFIX = "wfdesc";

        private static final String SKOS_IRI = "http://www.w3.org/2004/02/skos/core#";
        private static final String SKOS_LABEL = SKOS_IRI + "prefLabel";

        private static final String RDF_IRI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        private static final String RDF_PREFIX = "rdf";
        private static final String TYPE_IRI = RDF_IRI + "type";

        private static final String RDFS_IRI = "http://www.w3.org/2000/01/rdf-schema#";
        private static final String RDFS_PREFIX = "rdfs";

        private WebResource resource = null;
        private String prefixes = "";
        private PrefixMapping prefixMapping = PrefixMapping.Factory.create();
        private Node wfNode;

        private String migrationPathTargetPattern;
        private String dependencyLabelPattern;

        private ElementGroup query = new ElementGroup();
        private ElementUnion handlesMimetypes = new ElementUnion();
        private ElementUnion migrationPathSource = new ElementUnion();

        /**
         * Creates a new component query for the provided web resource.
         * 
         * @param resource
         *            a web resource
         */
        private ComponentQuery(WebResource resource) {
            this.resource = resource.path(COMPONENTS_PATH);
            wfNode = NodeFactory.createVariable("w");
            addPrefix(RDF_PREFIX, RDF_IRI);
            addPrefix(ONTOLOGY_PREFIX, ONTOLOGY_IRI);
            // Add prefixes already specified in the myExperiment API
            prefixMapping.setNsPrefix(WFDESC_PREFIX, WFDESC_IRI);
            prefixMapping.setNsPrefix(RDFS_PREFIX, RDFS_IRI);
        }

        /**
         * Adds a prefix to the query.
         * 
         * @param prefix
         *            the prefix
         * @param iri
         *            the IRI
         * @return this query
         */
        private ComponentQuery addPrefix(String prefix, String iri) {
            if (prefix != null && !prefix.isEmpty() && iri != null && !iri.isEmpty()) {
                prefixes += "PREFIX " + prefix + ":<" + iri + ">\n";
                prefixMapping.setNsPrefix(prefix, iri);
            }
            return this;
        }

        /**
         * Adds an element to the query.
         * 
         * @param element
         *            the element to add
         * @return this query
         */
        public ComponentQuery addElement(Element element) {
            query.addElement(element);
            return this;
        }

        /**
         * Adds a profile restriction to the query.
         * 
         * @param profile
         *            the profile
         * @return this query
         */
        public ComponentQuery addProfile(String profile) {
            if (profile != null && !profile.isEmpty()) {
                Triple t = new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI + "fits"),
                    NodeFactory.createURI(profile));
                query.addTriplePattern(t);
            }
            return this;
        }

        /**
         * Adds a migration path restriction to the query.
         * 
         * @param sourceMimetype
         *            the source mimetype
         * @param targetMimetype
         *            the target mimetype
         * @return this query
         */
        public ComponentQuery addMigrationPath(String sourceMimetype, String targetMimetype) {
            if ((sourceMimetype != null && !sourceMimetype.isEmpty())
                || (targetMimetype != null && !targetMimetype.isEmpty())) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI + "migrates"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(TYPE_IRI), NodeFactory
                    .createURI(ONTOLOGY_IRI + "MigrationPath")));
                if (sourceMimetype != null && !sourceMimetype.isEmpty()) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "sourceMimetype"),
                        NodeFactory.createLiteral(sourceMimetype)));
                }
                if (targetMimetype != null && !targetMimetype.isEmpty()) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "targetMimetype"),
                        NodeFactory.createLiteral(targetMimetype)));
                }
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a migration path source restriction to the query.
         * 
         * Note that all mimetypes added using the methods
         * {@link #addMigrationPath(String)},
         * {@link #addMigrationPathWildcard(String)} will be concatenated using
         * UNION.
         * 
         * @param sourceMimetype
         *            the source mimetype
         * @return this query
         */
        public ComponentQuery addMigrationPath(String sourceMimetype) {
            if (sourceMimetype != null && !sourceMimetype.isEmpty()) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI + "migrates"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(TYPE_IRI), NodeFactory
                    .createURI(ONTOLOGY_IRI + "MigrationPath")));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "sourceMimetype"),
                    NodeFactory.createLiteral(sourceMimetype)));
                migrationPathSource.addElement(group);
            }
            return this;
        }

        /**
         * Adds a migration path source wildcard restriction to the query.
         * 
         * Note that all mimetypes added using the methods
         * {@link #addMigrationPath(String)},
         * {@link #addMigrationPathWildcard(String)} will be concatenated using
         * UNION.
         * 
         * @param sourceMimetype
         *            the source mimetype
         * @return this query
         */
        public ComponentQuery addMigrationPathWildcard(String sourceMimetype) {
            return addMigrationPath(getMimetypeWildcard(sourceMimetype));
        }

        /**
         * Sets a migration path pattern for the query.
         * 
         * @param pattern
         *            the pattern
         * @return this query
         */
        public ComponentQuery setMigrationPathTargetPattern(String pattern) {
            this.migrationPathTargetPattern = pattern;
            return this;
        }

        /**
         * Adds a handlesMimetype restriction to the query.
         * 
         * Note that all mimetypes added using the methods
         * {@link #addHandlesMimetype(String...)},
         * {@link #addHandlesMimetypeWildcard(String...)},
         * {@link #addHandlesMimetypes(String, String)} and
         * {@link #addHandlesMimetypesWildcard(String, String)} will be
         * concatenated using UNION.
         * 
         * @param mimetypes
         *            the mimetypes
         * @return this query
         */
        public ComponentQuery addHandlesMimetype(String... mimetypes) {
            if (mimetypes != null && mimetypes.length > 0) {
                ElementGroup elements = new ElementGroup();
                Set<String> mimeset = new HashSet<String>();
                Collections.addAll(mimeset, mimetypes);
                for (String mimetype : mimeset) {
                    if (mimetype != null) {
                        elements.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI
                            + "handlesMimetype"), NodeFactory.createLiteral(mimetype)));
                    }
                }
                handlesMimetypes.addElement(elements);
            }
            return this;
        }

        /**
         * Adds handlesMimetype wildcard restrictions based on the provided
         * mimetypes.
         * 
         * Note that all mimetypes added using the methods
         * {@link #addHandlesMimetype(String...)},
         * {@link #addHandlesMimetypeWildcard(String...)},
         * {@link #addHandlesMimetypes(String, String)} and
         * {@link #addHandlesMimetypesWildcard(String, String)} will be
         * concatenated using UNION.
         * 
         * @param mimetypes
         *            the base mimetypes
         * @return this query
         */
        public ComponentQuery addHandlesMimetypeWildcard(String... mimetypes) {
            if (mimetypes != null && mimetypes.length > 0) {
                Set<String> wildcards = new HashSet<String>(mimetypes.length);
                for (String mimetype : mimetypes) {
                    wildcards.add(getMimetypeWildcard(mimetype));

                }
                addHandlesMimetype(wildcards.toArray(new String[0]));
            }
            return this;
        }

        /**
         * Adds a handlesMimetypes restriction to the query.
         * 
         * Note that all mimetypes added using the methods
         * {@link #addHandlesMimetype(String...)},
         * {@link #addHandlesMimetypeWildcard(String...)},
         * {@link #addHandlesMimetypes(String, String)} and
         * {@link #addHandlesMimetypesWildcard(String, String)} will be
         * concatenated using UNION.
         * 
         * @param leftMimetype
         *            the left mimetype
         * @param rightMimetype
         *            the right mimetype
         * @return this query
         */
        public ComponentQuery addHandlesMimetypes(String leftMimetype, String rightMimetype) {
            if (leftMimetype != null && !leftMimetype.isEmpty() && rightMimetype != null && !rightMimetype.isEmpty()) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI + "handlesMimetypes"),
                    node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(TYPE_IRI), NodeFactory
                    .createURI(ONTOLOGY_IRI + "AcceptedMimetypes")));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "handlesLeftMimetype"),
                    NodeFactory.createLiteral(leftMimetype)));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "handlesRightMimetype"),
                    NodeFactory.createLiteral(rightMimetype)));

                handlesMimetypes.addElement(group);
            }
            return this;
        }

        /**
         * Adds handlesMimetype wildcard restrictions based on the provided
         * mimetypes.
         * 
         * Note that all mimetypes added using the methods
         * {@link #addHandlesMimetype(String...)},
         * {@link #addHandlesMimetypeWildcard(String...)},
         * {@link #addHandlesMimetypes(String, String)} and
         * {@link #addHandlesMimetypesWildcard(String, String)} will be
         * concatenated using UNION.
         * 
         * @param leftMimetype
         *            the left mimetype
         * @param rightMimetype
         *            the right mimetype
         * @return this query
         */
        public ComponentQuery addHandlesMimetypesWildcard(String leftMimetype, String rightMimetype) {
            if (leftMimetype != null && !leftMimetype.isEmpty() && rightMimetype != null && !rightMimetype.isEmpty()) {
                String leftWildcard = getMimetypeWildcard(leftMimetype);
                String rightWildcard = getMimetypeWildcard(rightMimetype);
                addHandlesMimetypes(leftMimetype, rightWildcard);
                addHandlesMimetypes(leftWildcard, rightMimetype);
                addHandlesMimetypes(leftWildcard, rightWildcard);
            }
            return this;
        }

        /**
         * Adds an input port type restriction to the query.
         * 
         * @param accepts
         *            the port type
         * @return this query
         */
        public ComponentQuery addInputPort(String accepts) {
            if (accepts != null && !accepts.isEmpty()) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasInput"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "accepts"), NodeFactory
                    .createURI(accepts)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a measures input port restriction to the query.
         * 
         * @param relatedObject
         *            the object related to the measures
         * @param measure
         *            the measure
         * @return this query
         */
        public ComponentQuery addMeasureInputPort(String relatedObject, String measure) {
            if (measure != null && !measure.isEmpty()) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasInput"), node));
                if (relatedObject != null && !relatedObject.isEmpty()) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "relatesTo"),
                        NodeFactory.createURI(ONTOLOGY_IRI + relatedObject)));
                }
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "accepts"), NodeFactory
                    .createURI(measure)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a measures input port restriction to the query.
         * 
         * @param acceptsMeasure
         *            the measures
         * @return this query
         */
        public ComponentQuery addMeasureInputPort(String acceptsMeasure) {
            return addMeasureInputPort(null, acceptsMeasure);
        }

        /**
         * Adds an output port type restriction to the query.
         * 
         * @param provides
         *            the port type
         * @return this query
         */
        public ComponentQuery addOutputPort(String provides) {
            if (provides != null && !provides.isEmpty()) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasOutput"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "provides"), NodeFactory
                    .createURI(provides)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a measure output port restriction to the query.
         * 
         * @param relatedObject
         *            the object related to the measures
         * @param measure
         *            the measure
         * @return this query
         */
        public ComponentQuery addMeasureOutputPort(String relatedObject, String measure) {
            if (measure != null && !measure.isEmpty()) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasOutput"), node));
                if (relatedObject != null && !relatedObject.isEmpty()) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "relatesTo"),
                        NodeFactory.createURI(ONTOLOGY_IRI + relatedObject)));
                }
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "provides"), NodeFactory
                    .createURI(measure)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a measure output port restriction to the query.
         * 
         * @param measure
         *            the measure
         * @return this query
         */
        public ComponentQuery addMeasureOutputPort(String measure) {
            return addMeasureOutputPort(null, measure);
        }

        /**
         * Sets the dependency label pattern for the query.
         * 
         * @param pattern
         *            the pattern for dependency label
         * @return this query
         */
        public ComponentQuery setDependencyLabelPattern(String pattern) {
            this.dependencyLabelPattern = pattern;
            return this;
        }

        /**
         * Adds an environment restriction to the query.
         * 
         * @param environment
         *            the environment
         * @return this query
         */
        public ComponentQuery addInstallationEnvironment(String environment) {
            if (environment != null && !environment.isEmpty()) {
                Node processNode = NodeFactory.createAnon();
                Node installationNode = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasSubProcess"),
                    processNode));
                group.addTriplePattern(new Triple(processNode, NodeFactory.createURI(ONTOLOGY_IRI
                    + "requiresInstallation"), installationNode));
                group.addTriplePattern(new Triple(installationNode, NodeFactory.createURI(ONTOLOGY_IRI
                    + "hasEnvironment"), NodeFactory.createURI(environment)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds an environment restriction to the query.
         * 
         * @param environmentClass
         *            the environment class
         * @return this query
         */
        public ComponentQuery addInstallationEnvironmentType(String environmentClass) {
            if (environmentClass != null && !environmentClass.isEmpty()) {
                Node processNode = NodeFactory.createAnon();
                Node installationNode = NodeFactory.createAnon();
                Node environmentNode = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasSubProcess"),
                    processNode));
                group.addTriplePattern(new Triple(processNode, NodeFactory.createURI(ONTOLOGY_IRI
                    + "requiresInstallation"), installationNode));
                group.addTriplePattern(new Triple(installationNode, NodeFactory.createURI(ONTOLOGY_IRI
                    + "hasEnvironment"), environmentNode));
                group.addTriplePattern(new Triple(environmentNode, NodeFactory.createURI(TYPE_IRI), NodeFactory
                    .createURI(environmentClass)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Finishes the migration path to filter.
         */
        private void finishMigrationPathFilter() {
            if (migrationPathTargetPattern != null && !migrationPathTargetPattern.isEmpty()) {
                Node migrationPath = NodeFactory.createAnon();
                Node toMimetype = NodeFactory.createVariable("migrationPathTarget");
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI + "migrates"),
                    migrationPath));
                group.addTriplePattern(new Triple(migrationPath, NodeFactory.createURI(TYPE_IRI), NodeFactory
                    .createURI(ONTOLOGY_IRI + "MigrationPath")));
                group.addTriplePattern(new Triple(migrationPath,
                    NodeFactory.createURI(ONTOLOGY_IRI + "targetMimetype"), toMimetype));
                query.addElement(group);
                ElementFilter filter = new ElementFilter(new E_StrContains(new ExprVar(toMimetype),
                    new NodeValueString(migrationPathTargetPattern)));
                query.addElementFilter(filter);
            }
        }

        /**
         * Finishes the dependency label filter.
         */
        private void finishDependencyLabelFilter() {
            if (dependencyLabelPattern != null && !dependencyLabelPattern.isEmpty()) {
                Node processNode = NodeFactory.createAnon();
                Node installationNode = NodeFactory.createAnon();
                Node dependencyNode = NodeFactory.createAnon();
                Node dependencyLabel = NodeFactory.createVariable("dependencyLabel");
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasSubProcess"),
                    processNode));
                group.addTriplePattern(new Triple(processNode, NodeFactory.createURI(ONTOLOGY_IRI
                    + "requiresInstallation"), installationNode));
                group.addTriplePattern(new Triple(installationNode, NodeFactory.createURI(ONTOLOGY_IRI + "dependsOn"),
                    dependencyNode));
                group.addTriplePattern(new Triple(dependencyNode, NodeFactory.createURI(SKOS_LABEL), dependencyLabel));
                query.addElement(group);
                ElementFilter filter = new ElementFilter(new E_StrContains(new ExprVar(dependencyLabel),
                    new NodeValueString(dependencyLabelPattern)));
                query.addElementFilter(filter);
            }
        }

        /**
         * Finishes the source migration path filter.
         */
        private void finishMigrationPathSource() {
            if (migrationPathSource != null && !migrationPathSource.getElements().isEmpty()) {
                if (migrationPathSource.getElements().size() > 1) {
                    query.addElement(migrationPathSource);
                } else {
                    query.addElement(migrationPathSource.getElements().get(0));
                }
            }
        }

        /**
         * Finishes the handles mimetype filter
         */
        private void finishHandlesMimetypes() {
            if (handlesMimetypes != null && !handlesMimetypes.getElements().isEmpty()) {
                if (handlesMimetypes.getElements().size() > 1) {
                    query.addElement(handlesMimetypes);
                } else {
                    query.addElement(handlesMimetypes.getElements().get(0));
                }
            }
        }

        /**
         * Finishes the query for execution.
         */
        public void finishQuery() {
            finishMigrationPathFilter();
            finishDependencyLabelFilter();
            finishMigrationPathSource();
            finishHandlesMimetypes();

            IndentedLineBuffer formatBuffer = new IndentedLineBuffer();
            FormatterElement.format(formatBuffer, new SerializationContext(prefixMapping), query);

            try {
                String encqs = URLEncoder.encode(formatBuffer.toString(), "UTF-8");
                String encpf = URLEncoder.encode(prefixes, "UTF-8");
                resource = resource.queryParam(PREFIX_NAME, encpf).queryParam(QUERY_NAME, encqs);
            } catch (UnsupportedEncodingException e) {
                LOG.error("Error encoding query", e);
            }
        }

        /**
         * Creates a wildcard mimetype by using the type of the provided
         * mimetype and '*' as subtype.
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
    }

    /**
     * Path to workflow list endpoint.
     */
    private static final String WORKFLOWS_PATH = "workflows.xml";

    /**
     * Path to workflow detail endpoint.
     */
    private static final String WORKFLOW_PATH = "workflow.xml";

    /**
     * Path to query endpoint.
     */
    private static final String COMPONENTS_PATH = "components.xml";

    private static final int NOT_FOUND_STATUS = 404;

    private String myExperimentUri;

    private WebResource myExperiment;

    /**
     * Creates a new rest client for myExperiment.
     */
    public MyExperimentRESTClient() {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration config = configurationLoader.load();
        myExperimentUri = config.getString("myexperiment.rest.uri");

        ClientConfig cc = new DefaultClientConfig();
        Client client = Client.create(cc);

        myExperiment = client.resource(myExperimentUri);
    }

    /**
     * Creates a new rest client for myExperiment.
     * 
     * @param myExperiment
     *            web resource to use for requests
     */
    public MyExperimentRESTClient(WebResource myExperiment) {
        this.myExperiment = myExperiment;
    }

    /**
     * Searches for components.
     * 
     * @param query
     *            the query to use
     * @return a list of workflows
     * @throws PlanningServiceException
     *             if the query failed
     */
    public List<WorkflowInfo> searchComponents(ComponentQuery query) throws PlanningServiceException {
        GenericType<JAXBElement<SearchResult>> searchResultType = new GenericType<JAXBElement<SearchResult>>() {
        };

        try {
            LOG.debug("Querying myExperiments with [{}]", query.resource.getURI());
            return query.resource.queryParam("elements", QUERY_ELEMENTS).accept(MediaType.APPLICATION_XML_TYPE)
                .get(searchResultType).getValue().getWorkflows();
        } catch (Exception e) {
            throw new PlanningServiceException("Querying myExperiments failed.", e);
        }
    }

    /**
     * Gets the workflow description of a workflow using the default
     * myExperiment URL.
     * 
     * @param id
     *            the id of the workflow
     * @param version
     *            the version of the workflow
     * @return a workflow description
     */
    public WorkflowDescription getWorkflow(String id, String version) {
        GenericType<JAXBElement<WorkflowDescription>> workflowType = new GenericType<JAXBElement<WorkflowDescription>>() {
        };
        try {
            LOG.debug("Querying myExperiments for workflow id [{}]", id);
            return myExperiment.path(WORKFLOW_PATH).queryParam("id", id).queryParam("version", version)
                .queryParam("elements", WORKFLOW_ELEMENTS).accept(MediaType.APPLICATION_XML_TYPE).get(workflowType)
                .getValue();
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == NOT_FOUND_STATUS) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Lists workflows according to the provided tag or with not tag restriction
     * if null is provided.
     * 
     * @param tag
     *            the tag to filter or null
     * @return a list of workflows
     */
    public List<WorkflowInfo> listWorkflows(String tag) {
        GenericType<JAXBElement<SearchResult>> searchResultType = new GenericType<JAXBElement<SearchResult>>() {
        };

        WebResource workflows = myExperiment.path(WORKFLOWS_PATH);
        if (tag != null) {
            workflows = workflows.queryParam("tag", tag);
        }
        LOG.debug("Querying myExperiments for workflows with tag [{}]", tag);
        return workflows.queryParam("elements", QUERY_ELEMENTS).accept(MediaType.APPLICATION_XML_TYPE)
            .get(searchResultType).getValue().getWorkflows();
    }

    /**
     * Lists all workflows.
     * 
     * @return a list of workflows
     */
    public List<WorkflowInfo> listWorkflows() {
        return listWorkflows(null);
    }

    /**
     * Creates a new component query.
     * 
     * @return a query object
     */
    public ComponentQuery createComponentQuery() {
        return new ComponentQuery(myExperiment);
    }

    /**
     * Gets the workflow description of a workflow using the provided workflow
     * descriptor URL.
     * 
     * @param descriptor
     *            the descriptor URL of the workflow
     * @return a workflow description, or null if the workflow could not be found 
     */
    public static WorkflowDescription getWorkflow(String descriptor) {
        GenericType<JAXBElement<WorkflowDescription>> workflowType = new GenericType<JAXBElement<WorkflowDescription>>() {
        };

        String likelyDescriptor = guessDescriptor(descriptor);
        if (likelyDescriptor == null) {
            return null;
        }
        Client client = Client.create();
        WebResource resource = client.resource(likelyDescriptor).queryParam("elements", WORKFLOW_ELEMENTS);

        try {
            LOG.debug("Querying myExperiments for workflow resource [{}]", likelyDescriptor);
            return resource.accept(MediaType.APPLICATION_XML_TYPE).get(workflowType).getValue();
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == NOT_FOUND_STATUS) {
                LOG.debug("Workflow resource [{}] not found", likelyDescriptor);
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Gets the workflow content of a workflow identified by the provided
     * workflowInfo.
     * 
     * @param workflowInfo
     *            the workflow info
     * @return the content of the workflow
     */
    public static String getWorkflowContent(WorkflowInfo workflowInfo) {
        try {
            LOG.debug("Querying myExperiment for workflow content for [{}]", workflowInfo.getContentUri());
            return Client.create().resource(workflowInfo.getContentUri()).accept(workflowInfo.getContentType())
                .get(String.class);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == NOT_FOUND_STATUS) {
                LOG.debug("Workflow resource [{}] not found", workflowInfo.getContentUri());
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Creates a descriptor URL based on the provided reference URL to a
     * workflow.
     * 
     * @param reference
     *            a URL to a workflow
     * @return the likely descriptor URL or the reference if no match was found
     */
    private static String guessDescriptor(String reference) {
        if (reference == null) {
            return reference;
        }
        if (reference.matches("(.+\\:\\/\\/.+workflow.xml)(.*[?&]id=\\d+)")) {
            return reference;
        } else {
            String myExperimentUrl = null;
            String id = null;
            String version = null;

            Matcher dlMatcher = WORKFLOW_DL_PATTERN.matcher(reference);
            if (dlMatcher.matches()) {
                myExperimentUrl = dlMatcher.group(WORKFLOW_URL_GROUP);
                id = dlMatcher.group(WORKFLOW_DL_ID_GROUP);
                version = dlMatcher.group(WORKFLOW_DL_VERSION_GROUP);
            } else {
                Matcher pathMatcher = WORKFLOW_PATH_PATTERN.matcher(reference);
                if (pathMatcher.matches()) {
                    myExperimentUrl = pathMatcher.group(WORKFLOW_URL_GROUP);
                    id = pathMatcher.group(WORKFLOW_PATH_ID_GROUP);
                    version = pathMatcher.group(WORKFLOW_PATH_VERSION_GROUP);
                }
            }

            if (myExperimentUrl != null && id != null) {
                if (version == null) {
                    version = "1";
                }
                return myExperimentUrl + "workflow.xml?id=" + id + "&version=" + version;
            }
        }
        return reference;
    }

}
