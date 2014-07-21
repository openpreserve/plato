package eu.scape_project.planning.repository;

import java.io.InputStream;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import eu.scape_project.planning.api.RepositoryConnectorApi;
import eu.scape_project.planning.utils.RepositoryConnectorException;

/**
 * Client for repositories implementing the SCAPE Dataconnector API.
 * 
 * 
 * @author Michael Kraxner
 *
 */
public class SCAPEDataConnectorClient implements RepositoryConnectorApi {
    
    private Client client;
    private WebResource endpoint;
    
    /**
     * Creates the client for the given endpoint, with the given credentials for HTTP basic authentication.
     * 
     * @param endpoint
     * @param user
     * @param password
     */
    public SCAPEDataConnectorClient(final String endpoint, final String user, final String password){
        ClientConfig cc = new DefaultClientConfig();

        this.client = Client.create(cc); 
        this.client.addFilter(new HTTPBasicAuthFilter(user, password));
        this.endpoint = this.client.resource(endpoint);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepositoryIdentifier() {
        return this.endpoint.toString();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream downloadFile(String identifier) throws RepositoryConnectorException{
        // /file/<entity-id>/<representation-id>/<file-id>/<version-id>
        try {
        	String internalIdentifier = endpoint.toString() + "file/";
        
        	if (identifier.startsWith(internalIdentifier)) {
        		internalIdentifier = identifier.substring(internalIdentifier.length());
        	} else {
        		internalIdentifier = identifier;
        	}
            return endpoint.path("file/" + internalIdentifier).get(InputStream.class);
        } catch (Exception e) {
            throw new RepositoryConnectorException(e);            
        }
    }


}
