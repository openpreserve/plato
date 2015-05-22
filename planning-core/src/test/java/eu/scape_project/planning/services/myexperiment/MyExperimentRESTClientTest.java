package eu.scape_project.planning.services.myexperiment;

import org.junit.Assert;
import org.junit.Test;

public class MyExperimentRESTClientTest {
    
    @Test
    public void testGetWorkflowNullReference() {
        Assert.assertNull(MyExperimentRESTClient.getWorkflow(null));
    }

}
