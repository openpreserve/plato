package eu.scape_project.planning.services.taverna;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.services.taverna.model.SearchResult;
import eu.scape_project.planning.services.taverna.model.SearchResult.Workflow;
import eu.scape_project.planning.services.taverna.model.WorkflowDescription;
import eu.scape_project.planning.taverna.PortType;
import eu.scape_project.planning.taverna.executor.SSHTavernaExecutor;
import eu.scape_project.planning.utils.ConfigurationLoader;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client to access the REST interface of a myExperiment instance.
 */
public class MyExperimentRESTClient {

    /**
     * Describes a query for components using the myExperiment REST endpoint.
     */
    public final class ComponentQuery {

        private static final String ONTOLOGY_PREFIX = "http://purl.org/DP/components";

        private WebResource resource = null;

        private int inputParamIndex = 0;
        private int outputParamIndex = 0;
        private int workflowParamIndex = 0;

        /**
         * Creates a new component query for the provided web resource.
         * 
         * @param resource
         *            a web resource
         */
        private ComponentQuery(WebResource resource) {
            this.resource = resource.path(COMPONENTS_PATH);
        }

        /**
         * Adds an query parameter for an input port to this query.
         * 
         * @param param
         *            the annotation to add to the query
         * @return this query
         */
        public ComponentQuery addInputPortAnnotation(final String param) {
            resource = resource.queryParam("input[" + inputParamIndex + "]", param);
            inputParamIndex++;
            return this;
        }

        /**
         * Adds an query parameter for an output port to this query.
         * 
         * @param param
         *            the annotation to add to the query
         * @return this query
         */
        public ComponentQuery addOutputPortAnnotation(final String param) {
            resource = resource.queryParam("output[" + outputParamIndex + "]", param);
            outputParamIndex++;
            return this;
        }

        /**
         * Adds the port type as query parameter for an input port to this
         * query.
         * 
         * @param portType
         *            the port type to add
         * @return this query
         */
        public ComponentQuery addInputPortType(final PortType portType) {
            return addInputPortAnnotation("\"" + ONTOLOGY_PREFIX + "#portType " + portType.toString() + "\"");
        }

        /**
         * Adds the port type as query parameter for an output port to this
         * query.
         * 
         * @param portType
         *            the port type to add
         * @return this query
         */
        public ComponentQuery addOutputPortType(final PortType portType) {
            return addInputPortAnnotation("\"" + ONTOLOGY_PREFIX + "#portType " + portType.toString() + "\"");
        }

        /**
         * Adds an accepted measures as query parameter for an input port to
         * this query.
         * 
         * @param measure
         *            the measure to add
         * @return this query
         */
        public ComponentQuery addAcceptsMeasure(final Measure measure) {
            return addInputPortAnnotation("\"" + ONTOLOGY_PREFIX + "#acceptsMeasure " + measure.getUri() + "\"");
        }

        /**
         * Adds an provided measures as query parameter for an output port to
         * this query.
         * 
         * @param measure
         *            the measure to add
         * @return this query
         */
        public ComponentQuery addProvidesMeasure(final Measure measure) {
            return addOutputPortAnnotation("\"" + ONTOLOGY_PREFIX + "#providesMeasure " + measure.getUri() + "\"");
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(SSHTavernaExecutor.class);

    private static final String WORKFLOWS_PATH = "workflows.xml";

    private static final String WORKFLOW_PATH = "workflow.xml";

    private static final String COMPONENTS_PATH = "components.xml";

    private static final int NOT_FOUND_STATUS = 404;

    private String myExperimentUri;

    private Client client;

    private WebResource myExperiment;

    /**
     * Creates a new rest client for myExperiment.
     */
    public MyExperimentRESTClient() {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration config = configurationLoader.load();
        myExperimentUri = config.getString("myexperiment.rest.uri");

        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(cc);

        myExperiment = client.resource(myExperimentUri);
    }

    /**
     * Searches for components.
     * 
     * @param query
     *            the query to use
     * @return a list of workflows
     */
    public List<Workflow> searchComponents(ComponentQuery query) {
        GenericType<JAXBElement<SearchResult>> searchResultType = new GenericType<JAXBElement<SearchResult>>() {
        };

        LOG.debug("Querying myExperiments with [{}]", query.resource.getURI());
        return query.resource.accept(MediaType.APPLICATION_XML_TYPE).get(searchResultType).getValue().getWorkflows();
    }

    /**
     * Gets the workflow description of a workflow.
     * 
     * @param id
     *            the id of the workflow
     * @return a workflow description
     */
    public WorkflowDescription getWorkflow(String id, String version) {
        GenericType<JAXBElement<WorkflowDescription>> workflowType = new GenericType<JAXBElement<WorkflowDescription>>() {
        };
        try {
            LOG.debug("Querying myExperiments for workflow id [{}]", id);
            return myExperiment.path(WORKFLOW_PATH).queryParam("id", id).queryParam("version", version)
                .accept(MediaType.APPLICATION_XML_TYPE).get(workflowType).getValue();
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == NOT_FOUND_STATUS) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Gets the workflow description of a workflow.
     * 
     * @param id
     *            the id of the workflow
     * @return a workflow description
     */
    public WorkflowDescription getWorkflow(String uri) {
        GenericType<JAXBElement<WorkflowDescription>> workflowType = new GenericType<JAXBElement<WorkflowDescription>>() {
        };

        client.setFollowRedirects(true);
        WebResource resource = client.resource(uri).queryParam("all_elements", "yes");

        try {
            LOG.debug("Querying myExperiments for workflow resource [{}]", uri);
            return resource.accept(MediaType.APPLICATION_XML_TYPE).get(workflowType).getValue();
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == NOT_FOUND_STATUS) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // [] &lt;http://scape-project.eu/pc/vocab/components#migrates&gt;
    // [ a &lt;http://scape-project.eu/pc/vocab/components#MigrationPath&gt;
    // ;
    // &lt;http://scape-project.eu/pc/vocab/profiles#fromMimetype&gt;
    // "image/*" ;
    // &lt;http://scape-project.eu/pc/vocab/profiles#toMimetype&gt;
    // "image/tiff"
    // ] .

    /**
     * Lists workflows according to the provided tag or with not tag restriction
     * if null is provided.
     * 
     * @param tag
     *            the tag to filter or null
     * @return a list of workflows
     */
    public List<Workflow> listWorkflows(String tag) {
        GenericType<JAXBElement<SearchResult>> searchResultType = new GenericType<JAXBElement<SearchResult>>() {
        };

        WebResource workflows = myExperiment.path(WORKFLOWS_PATH);
        if (tag != null) {
            workflows = workflows.queryParam("tag", tag);
        }
        LOG.debug("Querying myExperiments for workflows with tag [{}]", tag);
        return workflows.accept(MediaType.APPLICATION_XML_TYPE).get(searchResultType).getValue().getWorkflows();
    }

    /**
     * Creates a new component query.
     * 
     * @return a query object
     */
    public ComponentQuery createComponentQuery() {
        return new ComponentQuery(myExperiment);
    }
}
