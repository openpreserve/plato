package eu.scape_project.planning.services.pa.taverna;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.services.myexperiment.MyExperimentRESTClient;
import eu.scape_project.planning.services.myexperiment.MyExperimentRESTClient.ComponentQuery;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Installation;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.MigrationPath;

public class MyExperimentRESTClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(MyExperimentRESTClient.class);

    private MyExperimentRESTClient client;

    @Before
    public void setup() {
        client = new MyExperimentRESTClient();
    }

    @Test
    public void test() throws Exception {
        ComponentQuery q = client.createComponentQuery();

        // q.addHandlesMimetype("image/tiff");
        // q.addHandlesMimetypes("pdf", "jpg");
        // q.addHandlesMimetype("pdf");
        // q.addOutputPort("http://purl.org/DP/components/FromURIPort");
        // q.addMeasureOutputPort("http://purl.org/DP/quality/measures/11",
        // "http://purl.org/DP/quality/measures/12",
        // "http://purl.org/DP/quality/measures/113");
        //
        // q.addInputPort("http://purl.org/DP/components/ToURIPort");
        // q.addMeasureInputPort("http://purl.org/DP/quality/measures/21",
        // "http://purl.org/DP/quality/measures/22",
        // "http://purl.org/DP/quality/measures/213");

        // q.addMigrationPath("image/tiff", null);

        // q.addInputPort("http://purl.org/DP/components/ToURIPort");
        //
        // q.finishQuery();
        //
        // List<Workflow> workflows = client.searchComponents(q);

        // WorkflowDescription d =
        // client.getWorkflow("http://sandbox.myexperiment.org/workflow.xml?id=3346");
        WorkflowDescription d = client.getWorkflow("http://www.myexperiment.org/workflow.xml?id=3646");
        List<MigrationPath> migrationPaths = d.getMigrationPaths();
        List<Installation> installations = d.getInstallations();

        // List<Element> elements = d.getComponents();

        // for (Element el : elements) {
        // if (el.getNodeName().equals("components")) {
        // LOG.error("found comoponents");
        // Document doc = el.getOwnerDocument();
        // XPath xPath = XPathFactory.newInstance().newXPath();
        // NodeList nodes = (NodeList)
        // xPath.evaluate("/components//dataflow[@role='top']/semantic_annotation",
        // doc.getDocumentElement(), XPathConstants.NODESET);
        // for (int i = 0; i < nodes.getLength(); ++i) {
        // Element pel = (Element) nodes.item(i);
        // LOG.info(pel.getNodeName() + ": " + pel.getTextContent());
        // }
        // }
        // }
    }
    
    //
    // @Ignore
    // @Test
    // public void scufl2Test() throws Exception {
    // WorkflowBundleIO io = new WorkflowBundleIO();
    // File scufl2File = new File("/tmp/workflow.wfbundle");
    // WorkflowBundle wfBundle = io
    // .readBundle(
    // new URL(
    // "http://sandbox.myexperiment.org/workflows/3346/download/imagemagick_convert_-_tiff2tiff_-_compression_514886.t2flow"),
    // "application/vnd.taverna.t2flow+xml");
    //
    // wfBundle.getResources();
    // io.writeBundle(wfBundle, scufl2File,
    // "application/vnd.taverna.scufl2.workflow-bundle");
    // }

}
