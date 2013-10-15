package eu.scape_project.planning.services.myexperiment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import eu.scape_project.planning.services.myexperiment.domain.SearchResult;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowInfo;
import eu.scape_project.planning.utils.ConfigurationLoader;

/**
 * Client to access the REST interface of a myExperiment instance.
 */
public class MyExperimentRESTClient {

    private static final Logger LOG = LoggerFactory.getLogger(MyExperimentRESTClient.class);

    /**
     * Elements to request when querying.
     */
    private static final String QUERY_ELEMENTS = "id,title,description,content-uri,content-type";

    /**
     * Elements to request for workflow details.
     */
    private static final String WORKFLOW_ELEMENTS = "id,title,description,type,uploader,preview,svg,license-type,content-uri,content-type,tags,ratings,components";

    /**
     * Describes a query for components using the myExperiment REST endpoint.
     */
    public final class ComponentQuery {

        private static final String PREFIX_NAME = "prefixes";
        private static final String QUERY_NAME = "query";

        private static final String ONTOLOGY_IRI = "http://purl.org/DP/components#";

        private static final String WFDESC_IRI = "http://purl.org/wf4ever/wfdesc#";

        private static final String SKOS_IRI = "http://www.w3.org/2004/02/skos/core#";
        private static final String SKOS_LABEL = SKOS_IRI + "prefLabel";

        private static final String RDFS_IRI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        private static final String TYPE_IRI = RDFS_IRI + "type";

        private WebResource resource = null;
        private String prefixes = "";
        private PrefixMapping prefixMapping = PrefixMapping.Factory.create();
        private Node wfNode;

        private String migrationPathTargetPattern;
        private String dependencyLabelPattern;

        private ElementGroup query = new ElementGroup();
        private ElementUnion handlesMimetypes = new ElementUnion();

        /**
         * Creates a new component query for the provided web resource.
         * 
         * @param resource
         *            a web resource
         */
        private ComponentQuery(WebResource resource) {
            this.resource = resource.path(COMPONENTS_PATH);
            wfNode = NodeFactory.createVariable("w");
            addPrefix("dpc", ONTOLOGY_IRI);
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
            if (prefix != null && !prefix.equals("") && iri != null && !iri.equals("")) {
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
            if (profile != null && !profile.equals("")) {
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
         *            the from mimetype
         * @param targetMimetype
         *            the to mimetype
         * @return this query
         */
        public ComponentQuery addMigrationPath(String sourceMimetype, String targetMimetype) {
            if ((sourceMimetype != null && !sourceMimetype.equals(""))
                || (targetMimetype != null && !targetMimetype.equals(""))) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI + "migrates"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(TYPE_IRI), NodeFactory
                    .createURI(ONTOLOGY_IRI + "MigrationPath")));
                if (sourceMimetype != null && !sourceMimetype.equals("")) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "sourceMimetype"),
                        NodeFactory.createLiteral(sourceMimetype)));
                }
                if (targetMimetype != null && !targetMimetype.equals("")) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "targetMimetype"),
                        NodeFactory.createLiteral(targetMimetype)));
                }
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a migration path from restriction to the query.
         * 
         * @param sourceMimetype
         *            the from mimetype
         * @return this query
         */
        public ComponentQuery addMigrationPath(String sourceMimetype) {
            return addMigrationPath(sourceMimetype, null);
        }

        /**
         * Sets a migration path to pattern to the query.
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
                    elements.addTriplePattern(new Triple(wfNode, NodeFactory
                        .createURI(ONTOLOGY_IRI + "handlesMimetype"), NodeFactory.createLiteral(mimetype)));
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
         * @param mimetype1
         *            the first mimetype
         * @param mimetype2
         *            the second mimetype
         * @return this query
         */
        public ComponentQuery addHandlesMimetypes(String mimetype1, String mimetype2) {
            if (mimetype1 != null && !mimetype1.equals("") && mimetype2 != null && !mimetype2.equals("")) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(ONTOLOGY_IRI + "handlesMimetypes"),
                    node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(TYPE_IRI), NodeFactory
                    .createURI(ONTOLOGY_IRI + "AcceptedMimetypes")));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "handlesMimetype1"),
                    NodeFactory.createLiteral(mimetype1)));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "handlesMimetype2"),
                    NodeFactory.createLiteral(mimetype2)));

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
         * @param mimetype1
         *            the first mimetype
         * @param mimetype2
         *            the second mimetype
         * @return this query
         */
        public ComponentQuery addHandlesMimetypesWildcard(String mimetype1, String mimetype2) {
            if (mimetype1 != null && !mimetype1.equals("") && mimetype2 != null && !mimetype2.equals("")) {
                String wildcard1 = getMimetypeWildcard(mimetype1);
                String wildcard2 = getMimetypeWildcard(mimetype2);
                addHandlesMimetypes(mimetype1, wildcard2);
                addHandlesMimetypes(wildcard1, mimetype2);
            }
            return this;
        }

        /**
         * Adds an input port type restriction to the query.
         * 
         * @param portType
         *            the port type
         * @return this query
         */
        public ComponentQuery addInputPort(String portType) {
            if (portType != null && !portType.equals("")) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasInput"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "portType"), NodeFactory
                    .createURI(portType)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a measures input port restriction to the query.
         * 
         * @param acceptsMeasures
         *            the measure
         * @return this query
         */
        public ComponentQuery addMeasureInputPort(String... acceptsMeasures) {
            if (acceptsMeasures != null) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasInput"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "portType"), NodeFactory
                    .createURI(ONTOLOGY_IRI + "MeasurePortType")));
                for (String acceptsMeasure : acceptsMeasures) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "acceptsMeasure"),
                        NodeFactory.createURI(acceptsMeasure)));
                }
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds an output port type restriction to the query.
         * 
         * @param portType
         *            the port type
         * @return this query
         */
        public ComponentQuery addOutputPort(String portType) {
            if (portType != null && !portType.equals("")) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasOutput"), node));
                group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "portType"), NodeFactory
                    .createURI(portType)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Adds a measure output port restriction to the query.
         * 
         * @param providesMeasures
         *            the measure
         * @return this query
         */
        public ComponentQuery addMeasureOutputPort(String... providesMeasures) {
            if (providesMeasures != null) {
                Node node = NodeFactory.createAnon();
                ElementGroup group = new ElementGroup();
                group.addTriplePattern(new Triple(wfNode, NodeFactory.createURI(WFDESC_IRI + "hasOutput"), node));
                for (String providesMeasure : providesMeasures) {
                    group.addTriplePattern(new Triple(node, NodeFactory.createURI(ONTOLOGY_IRI + "providesMeasure"),
                        NodeFactory.createURI(providesMeasure)));
                }
                query.addElement(group);
            }
            return this;
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
            if (environment != null && !environment.equals("")) {
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
         * @param environmentType
         *            the environment type
         * @return this query
         */
        public ComponentQuery addInstallationEnvironmentType(String environmentType) {
            if (environmentType != null && !environmentType.equals("")) {
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
                group.addTriplePattern(new Triple(environmentNode, NodeFactory.createURI(ONTOLOGY_IRI
                    + "hasEnvironmentType"), NodeFactory.createURI(environmentType)));
                query.addElement(group);
            }
            return this;
        }

        /**
         * Finishes the migration path to filter.
         */
        private void finishMigrationPathFilter() {
            if (migrationPathTargetPattern != null && !migrationPathTargetPattern.equals("")) {
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
            if (dependencyLabelPattern != null && !dependencyLabelPattern.equals("")) {
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
         * Finishes the dependency label filter.
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
     */
    public List<WorkflowInfo> searchComponents(ComponentQuery query) {
        GenericType<JAXBElement<SearchResult>> searchResultType = new GenericType<JAXBElement<SearchResult>>() {
        };

        LOG.debug("Querying myExperiments with [{}]", query.resource.getURI());
        return query.resource.queryParam("elements", QUERY_ELEMENTS).accept(MediaType.APPLICATION_XML_TYPE)
            .get(searchResultType).getValue().getWorkflows();
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
     * @return a workflow description
     */
    public static WorkflowDescription getWorkflow(String descriptor) {
        GenericType<JAXBElement<WorkflowDescription>> workflowType = new GenericType<JAXBElement<WorkflowDescription>>() {
        };

        Client client = Client.create();
        WebResource resource = client.resource(descriptor).queryParam("elements", WORKFLOW_ELEMENTS);

        try {
            LOG.debug("Querying myExperiments for workflow resource [{}]", descriptor);
            return resource.accept(MediaType.APPLICATION_XML_TYPE).get(workflowType).getValue();
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == NOT_FOUND_STATUS) {
                LOG.debug("Workflow resource [{}] not found", descriptor);
                return null;
            } else {
                throw e;
            }
        }
    }
}
