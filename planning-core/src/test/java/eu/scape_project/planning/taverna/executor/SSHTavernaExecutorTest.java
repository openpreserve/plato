package eu.scape_project.planning.taverna.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.planning.taverna.executor.SSHTavernaExecutor;

public class SSHTavernaExecutorTest {

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

        List<File> inList = new ArrayList<File>();
        inList.add(new File("/home/markus/rumble1.jpg"));
        inList.add(new File("/home/markus/rumble1.jpg"));
        inList.add(new File("/home/markus/rumble1.jpg"));
        inputData.put(port2, inList);

        // executor.setInputData(inputData);

        executor.setWorkflowFile(new File("/home/markus/Dropbox/Projekte/SCAPE/Taverna/Executor/OutputError.t2flow"));

        TavernaPort out1 = new TavernaPort();
        out1.setName("ErrorOut");

        HashSet<TavernaPort> outputPorts = new HashSet<TavernaPort>();
        outputPorts.add(out1);

        executor.setOutputPorts(outputPorts);

        executor.init();
        executor.execute();

        Map<TavernaPort, ?> outputData = executor.getOutputData();
    }
}
