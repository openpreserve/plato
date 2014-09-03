package eu.scape_project.planning.services.notification;

import java.io.StringWriter;

import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.HttpPut;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import eu.scape_project.planning.annotation.IntegrationTest;

@Category(IntegrationTest.class)
public class NotificationResourceIT {

    /**
     * adds a test notification. 
     * - the server must be running on localhost:8080
     * - credentials for notification.rest : scout/pass
     * - notification is added for user: scape.pw@gmail.com 
     * 
     * @throws Exception
     */
    @Test
    public void addNotificationTest() throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        Client client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter("scout", "pass"));
        WebResource endpoint = client.resource("http://localhost:8080/plato/rest/");

        // prepare the json load
        WatchNotification n = new WatchNotification(null, "test message", null, null, "scape.pw@gmail.com");
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(writer, n);

        HttpPut putRequest = new HttpPut("http://localhost:8080/plato/rest/notification");
        putRequest.addHeader("content-type", "application/json");

        WebResource fileResource = endpoint.path("notification");
        ClientResponse response = fileResource.type(MediaType.APPLICATION_JSON).put(ClientResponse.class,
            writer.toString());

        Assert.assertEquals(200, response.getStatus());
    }
}
