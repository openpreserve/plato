package eu.scape_project.planning.services.notification;

import java.io.StringWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.scape_project.planning.annotation.IntegrationTest;

@Category(IntegrationTest.class)
public class NotificationResourceIT {
    
    /**
     * adds a test notification.
     * - to user scape.pw@gmail.com
     * - the server must be running on localhost:8080
     *  
     * @throws Exception
     */
    @Test
    public void addNotificationTest() throws Exception{
        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        WatchNotification n = new WatchNotification(null, "test message", null, null, "scape.pw@gmail.com");
        
        StringWriter writer = new StringWriter();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(writer, n);
                
        HttpPut putRequest = new HttpPut("http://localhost:8080/plato/rest/notification");
        putRequest.addHeader("content-type", "application/json");
        
        putRequest.setEntity(new StringEntity(writer.getBuffer().toString()));

        try {
            HttpResponse response = httpClient.execute(putRequest);
    
            int statusCode = response.getStatusLine().getStatusCode();
            
            Assert.assertEquals(200, statusCode);

        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        
    }

}
