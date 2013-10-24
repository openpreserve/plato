package eu.scape_project.planning.repository;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * Client for repositories implementing the SCAPE Plan Management API. 
 * 
 * Used to reserve plan identifiers and deploy plans.
 * 
 * @author Michael Kraxner
 *
 */
public class SCAPEPlanManagementClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SCAPEPlanManagementClient.class); 
    
    private Client client;
    private WebResource endpoint;
    
    /**
     * Creates the client configured for the given endpoint, with the given credentials for HTTP basic authentication.
     * 
     * @param endpoint
     * @param user
     * @param password
     */
    public SCAPEPlanManagementClient(final String endpoint, final String user, final String password) {
        ClientConfig cc = new DefaultClientConfig();
        this.client = Client.create(cc); 
        this.client.addFilter(new HTTPBasicAuthFilter(user, password));
        this.endpoint = this.client.resource(endpoint);
    }

    /**
     * Reserves a plan identifier in the repository.
     *  
     * @return
     * @throws Exception
     */
    public String reservePlanIdentifier() throws Exception {
        return endpoint.path("plan-id/reserve")
            .accept(MediaType.TEXT_PLAIN_TYPE)
            .get(String.class);
    }
    
    /**
     * Deploys the given plan to the repository
     *  
     * @param identifier
     *            identifier of the plan in the repository
     * @param plan
     *            plan to store
     * @return
     * @throws Exception
     */
    public boolean deployPlan(final String identifier, InputStream plan) throws Exception {
        WebResource fileResource = endpoint.path("plan/" + identifier);
        String sContentDisposition = "attachment; filename=\"" + "test name" +"\"";
        
        ClientResponse response = fileResource.type(MediaType.APPLICATION_XML)
            .header("Content-Disposition", sContentDisposition)
            .put(ClientResponse.class, plan);
        LOGGER.debug("Deploy plan [{}] response: [{}]", identifier, response.getStatus());
        return (response.getStatus() == 201);
    }
    
    /**
     * Retrieves the plan with the given identifier from the repository.
     * 
     * @param identifier
     * @return
     * @throws Exception
     */
    public InputStream retrievePlan(final String identifier) throws Exception {
        return endpoint.path("plan/" + identifier)
        .accept(MediaType.APPLICATION_XML_TYPE)
        .get(InputStream.class);
    }

}
