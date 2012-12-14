package eu.scape_project.planning.services.pa.taverna;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.scape_project.planning.services.pa.taverna.model.WorkflowDescription;
import eu.scape_project.planning.taverna.executor.SSHTavernaExecutor;
import eu.scape_project.planning.utils.ConfigurationLoader;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyExperimentRESTClient {

    private static final Logger LOG = LoggerFactory.getLogger(SSHTavernaExecutor.class);

    private static final String WORKFLOW_PATH = "workflow.xml";

    private String myExperimentUri;

    private WebResource resource;

    public MyExperimentRESTClient() {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration config = configurationLoader.load();
        // myExperimentUri = config.getString("myexperiment.rest.uri");
        myExperimentUri = "http://www.myexperiment.org/";
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client client = Client.create(cc);

        resource = client.resource(myExperimentUri);
    }

    public WorkflowDescription getWorkflow(String id) {
        GenericType<JAXBElement<WorkflowDescription>> planetType = new GenericType<JAXBElement<WorkflowDescription>>() {
        };
        return resource.path(WORKFLOW_PATH).queryParam("id", id).accept(MediaType.APPLICATION_XML_TYPE).get(planetType)
            .getValue();
    }
}
