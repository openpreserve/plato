package eu.scape_project.planning.taverna.executor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.password.PasswordFinder;
import net.schmizz.sshj.userauth.password.Resource;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.InMemorySourceFile;
import net.sf.taverna.t2.baclava.DataThing;
import net.sf.taverna.t2.baclava.factory.DataThingFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.pw.planning.utils.PropertiesLoader;

public class SSHTavernaExecutor {

    private static Logger log = LoggerFactory.getLogger(SSHTavernaExecutor.class);

    private static final String SSH_PROPERTIES = "config/tavernaserverssh";

    private static final String INPUT_DOC_FILENAME = "input_data.xml";
    private static final String OUTPUT_DOC_FILENAME = "output_data.xml";

    private static final String TAVERNA_COMMAND = "$TAVERNA_HOME/executeworkflow.sh -inputdoc %%inputdoc%% -outputdoc %%outputdoc%% %%workflow%%";

    private static final Namespace namespace = Namespace.getNamespace("b",
        "http://org.embl.ebi.escience/baclava/0.1alpha");

    private Properties sshProperties;

    private String workflowUri;

    private File workflowFile;

    private String tempDir;
    private SSHClient ssh;

    private Map<TavernaPort, Object> inputData = new HashMap<TavernaPort, Object>();

    private Map<TavernaPort, Object> outputData = new HashMap<TavernaPort, Object>();;

    private String inputDocPath;
    private String outputDocPath;
    private String workflowPath;

    public void init() {
        try {
            sshProperties = PropertiesLoader.loadProperties(SSH_PROPERTIES);
        } catch (IOException e) {
            log.error("Error loading properties " + SSH_PROPERTIES, e);
        }
    }

    public void execute() throws IOException {

        prepareClient();
        KeyProvider kp = getKeyProvider();

        try {
            ssh.connect(sshProperties.getProperty("host"));
            ssh.authPublickey(sshProperties.getProperty("user"), kp);

            tempDir = createTmpDir();
            prepare();
            executeWorkflow();

        } finally {
            ssh.disconnect();
        }

    }

    private void prepareClient() throws IOException {
        ssh = new SSHClient();

        ssh.addHostKeyVerifier(sshProperties.getProperty("fingerprint"));
        ssh.useCompression();
    }

    private KeyProvider getKeyProvider() throws IOException {
        // KeyProvider kp = ssh.loadKeys("/home/plangg/.ssh/id_dsa");
        KeyProvider kp = ssh.loadKeys(sshProperties.getProperty("private.key"),
            sshProperties.getProperty("public.key"), new PasswordFinder() {
                @Override
                public char[] reqPassword(Resource<?> resource) {
                    if (sshProperties.getProperty("password") == null) {
                        return null;
                    } else {
                        return sshProperties.getProperty("password").toCharArray();
                    }
                }

                @Override
                public boolean shouldRetry(Resource<?> resource) {
                    return false;
                }
            });

        return kp;
    }

    private void prepare() throws IOException {
        outputDocPath = tempDir + File.separator + OUTPUT_DOC_FILENAME;
        inputDocPath = prepareInputs();
        workflowPath = prepareWorkflow();
    }

    private String prepareInputs() throws IOException {
        Element rootElement = new Element("dataThingMap", namespace);
        Document document = new Document(rootElement);

        for (Entry<TavernaPort, Object> entry : inputData.entrySet()) {
            TavernaPort port = entry.getKey();

            Object dereferencedInput = null;

            Object value = entry.getValue();
            if (value instanceof File) {
                dereferencedInput = addInput(port, (File) value);
            } else if (value instanceof Collection) {
                dereferencedInput = addInput(port, (Collection) value);
            } else {
                dereferencedInput = addInput(port, value);
            }

            DataThing thing = DataThingFactory.bake(dereferencedInput);

            Element dataThingElement = new Element("dataThing", namespace);
            dataThingElement.setAttribute("key", port.getName());
            dataThingElement.addContent(thing.getElement());
            rootElement.addContent(dataThingElement);
        }

        XMLOutputter xo = new XMLOutputter(Format.getPrettyFormat());
        // PrintWriter out = new PrintWriter(new FileWriter(inputFile));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        xo.output(document, out);

        InputStream in = new ByteArrayInputStream(out.toByteArray());

        return uploadFile(new InputBaclavaFile(INPUT_DOC_FILENAME, in, out.toByteArray().length), tempDir);
    }

    private Object addInput(TavernaPort port, Collection<? extends Object> objects) throws IOException {
        ArrayList<Object> results = new ArrayList<Object>(objects.size());
        for (Object object : objects) {
            results.add(addInput(port, object));
        }
        return results;
    }

    private Object addInput(TavernaPort port, Object object) throws IOException {
        return object.toString();
    }

    private Object addInput(TavernaPort port, File file) throws IOException {
        return uploadFile(file, tempDir);
    }

    private String createTmpDir() throws IOException {
        final Session session = ssh.startSession();
        try {
            final Command cmd = session.exec("mktemp -d plato.XXXXXXXXXXXXXXXXXXXX");
            String tempDir = IOUtils.readFully(cmd.getInputStream()).toString();
            cmd.join(5, TimeUnit.SECONDS);
            tempDir = tempDir.trim();
            log.debug("Created temporary directory " + tempDir);
            return tempDir;
        } finally {
            session.close();
        }
    }

    private String uploadFile(File file, String tempDir) throws IOException {
        String targetPath = tempDir + File.separator + file.getName();
        ssh.newSCPFileTransfer().upload(new FileSystemFile(file), targetPath);
        log.debug("Uploaded file " + file.getAbsolutePath() + " to " + targetPath);
        return targetPath;
    }

    private String uploadFile(InMemorySourceFile file, String tempDir) throws IOException {
        String targetPath = tempDir + File.separator + file.getName();
        ssh.newSCPFileTransfer().upload(file, targetPath);
        log.debug("Uploaded file " + file.getName() + " to " + targetPath);
        return targetPath;
    }

    private void executeWorkflow() throws IOException {
        final Session session = ssh.startSession();
        try {
            String command = TAVERNA_COMMAND.replace("%%inputdoc%%", inputDocPath)
                .replace("%%outputdoc%%", outputDocPath).replace("%%workflow%%", workflowPath);
            final Command cmd = session.exec(command);
            String tempDir = IOUtils.readFully(cmd.getInputStream()).toString();
            cmd.join(5, TimeUnit.SECONDS);
            log.debug("Executed workflow with command " + command);
        } finally {
            session.close();
        }
    }

    private String prepareWorkflow() throws IOException {
        if (workflowFile != null) {
            return uploadFile(workflowFile, tempDir);
        } else if (workflowUri != null && !workflowUri.equals("")) {
            return workflowUri;
        } else {
            // TODO: Exception
            return null;
        }
    }

    // --------------- getter/setter ---------------
    public String getWorkflowURI() {
        return workflowUri;
    }

    public void setWorkflowURI(String workflowURI) {
        this.workflowUri = workflowURI;
    }

    public File getWorkflowFile() {
        return workflowFile;
    }

    public void setWorkflowFile(File workflowFile) {
        this.workflowFile = workflowFile;
    }

    public Map<TavernaPort, Object> getInputData() {
        return inputData;
    }

    public void setInputData(Map<TavernaPort, Object> inputData) {
        this.inputData = inputData;
    }

    public Map<TavernaPort, Object> getOutputData() {
        return outputData;
    }

    public void setOutputData(Map<TavernaPort, Object> outputData) {
        this.outputData = outputData;
    }

    // private void downloadFile(SSHClient ssh, File file, String tempDir)
    // throws IOException {
    // ssh.newSCPFileTransfer().upload(new FileSystemFile(file), tempDir +
    // file.getName());
    // log.debug("Uploaded file " + file.getAbsolutePath() + " to " + tempDir +
    // file.getName());
    // }

    private class InputBaclavaFile extends InMemorySourceFile {

        private InputStream in;
        private long length;
        private String name;

        public InputBaclavaFile(String name, InputStream in, long length) {
            this.name = name;
            this.in = in;
            this.length = length;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getLength() {
            return length;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return in;
        }

    }

}
