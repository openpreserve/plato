package eu.scape_project.planning.services.pa.taverna;

import eu.scape_project.planning.services.taverna.MyExperimentRESTClient;

import org.junit.Before;

public class MyExperimentRESTClientTest {
    private MyExperimentRESTClient client;

    @Before
    public void setup() {
        client = new MyExperimentRESTClient();
    }

    // @Test
    // public void test() throws Exception {
    // ResourceDescription test = client.getWorkflow("3304");
    // System.out.println(test.getUri());
    // // ResourceDescription full = client.getFullWorkflow("3304");
    // }
    //
    // @Test
    // public void searchTest() throws Exception {
    // List<Workflow> test = client.listWorkflows("gene");
    //
    // for (SearchResult.Workflow w : test) {
    // WorkflowDescription wd = client.getWorkflow(w.getId());
    //
    // System.out.println(wd.getId());
    // }
    //
    // }
    //
    // @Test
    // public void queryTest() throws Exception {
    // ComponentQuery query = client.createComponentQuery();
    //
    // query.addInputPortType(PortType.FromURIPort);
    //
    // List<Workflow> test = client.searchComponents(query);
    //
    // System.out.println(test.toString());
    // }

}
