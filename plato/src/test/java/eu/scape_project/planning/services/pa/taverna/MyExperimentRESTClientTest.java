package eu.scape_project.planning.services.pa.taverna;

import eu.scape_project.planning.services.pa.taverna.model.ResourceDescription;

import org.junit.Before;
import org.junit.Test;

public class MyExperimentRESTClientTest {
    private MyExperimentRESTClient client;

    @Before
    public void setup() {
        client = new MyExperimentRESTClient();
    }

    @Test
    public void test() throws Exception {

        ResourceDescription test = client.getWorkflow("3137");
    }

}
