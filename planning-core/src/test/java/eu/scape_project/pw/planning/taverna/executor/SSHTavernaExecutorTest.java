package eu.scape_project.pw.planning.taverna.executor;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.planning.taverna.executor.SSHTavernaExecutor;

public class SSHTavernaExecutorTest {

    @Test
    public void getIdTest() throws Exception {

        SSHTavernaExecutor executor = new SSHTavernaExecutor();

        TavernaPort port1 = new TavernaPort();
        port1.setDepth(0);
        port1.setName("singleIn");

        TavernaPort port2 = new TavernaPort();
        port2.setDepth(1);
        port2.setName("listIn");

        HashMap<TavernaPort, Object> inputData = new HashMap<TavernaPort, Object>();

        inputData.put(port1, "test1");
        inputData.put(port2, new File("/home/plangg/becker.pdf"));

        executor.setInputData(inputData);

        executor.setWorkflowFile(new File("/home/plangg/Dropbox/Projekte/SCAPE/Taverna/Executor/SingeListPass.t2flow"));

        executor.init();
        executor.execute();
    }
}
