/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.taverna.executor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.planning.utils.ConfigurationLoader;

import org.apache.commons.configuration.Configuration;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.InMemoryDestFile;
import net.schmizz.sshj.xfer.InMemorySourceFile;
import net.sf.taverna.t2.baclava.DataThing;
import net.sf.taverna.t2.baclava.factory.DataThingFactory;
import net.sf.taverna.t2.baclava.factory.DataThingXMLFactory;

/**
 * Class to execute Taverna workflows on a remote server via SSH.
 */
public class SSHTavernaExecutor implements TavernaExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SSHTavernaExecutor.class);

    /**
     * Name of the executor properties.
     */
    private static final String CONFIG_NAME = "tavernaserverssh.properties";

    /**
     * Filename of input data document.
     */
    private static final String INPUT_DOC_FILENAME = "input_data.xml";

    /**
     * Filename of output data document.
     */
    private static final String OUTPUT_DOC_FILENAME = "output_data.xml";

    /**
     * Taverna command.
     */
    private static final String TAVERNA_COMMAND = "$TAVERNA_HOME/executeworkflow.sh -inputdoc %%inputdoc%% -outputdoc %%outputdoc%% %%workflow%%";

    /**
     * Baclava XML namespace.
     */
    private static final Namespace NAMESPACE = Namespace.getNamespace("b",
        "http://org.embl.ebi.escience/baclava/0.1alpha");

    /**
     * SSH properties.
     */
    private Configuration sshConfig;

    /**
     * Timeout for remote commands.
     */
    private Integer commandTimeout;

    /*
     * Executor parameters
     */
    private String workflowUrl;
    private File workflowFile;
    private Map<TavernaPort, Object> inputData = new HashMap<TavernaPort, Object>();
    private Set<TavernaPort> outputPorts = new HashSet<TavernaPort>();
    private HashMap<TavernaPort, ?> outputFiles = new HashMap<TavernaPort, Object>();

    private Map<TavernaPort, Object> outputData = new HashMap<TavernaPort, Object>();;
    private String outputDoc;

    /*
     * Cache of created directories on the server
     */
    private HashSet<String> createdDirsCache = new HashSet<String>();

    /*
     * Cache of temp files
     */
    private HashMap<SSHTempFile, String> tempFilePaths = new HashMap<SSHTempFile, String>();

    /*
     * Taverna call stuff
     */
    private SSHClient ssh;
    private String workingDir;

    /*
     * Taverna command line arguments
     */
    private String inputDocPath;
    private String outputDocPath;
    private String workflowPath;

    /**
     * Initializes the Executor.
     */
    public void init() {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        sshConfig = configurationLoader.load(CONFIG_NAME);
        commandTimeout = sshConfig.getInt("tavernaserver.ssh.command.timeout");

        clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.scape_project.planning.taverna.executor.TavernaExecutor#execute()
     */
    @Override
    public void execute() throws IOException, TavernaExecutorException {

        clear();
        prepareClient();

        try {
            ssh.connect(sshConfig.getString("tavernaserver.ssh.host"));

            if (sshConfig.getString("tavernaserver.ssh.privatekey.location") != null
                && !"".equals(sshConfig.getString("tavernaserver.ssh.privatekey.location"))) {
                KeyProvider kp = ssh.loadKeys(sshConfig.getString("tavernaserver.ssh.privatekey.location"),
                    sshConfig.getString("tavernaserver.ssh.privatekey.password"));
                ssh.authPublickey(sshConfig.getString("tavernaserver.ssh.user"), kp);
            } else if (sshConfig.getString("tavernaserver.ssh.password") != null
                && !"".equals(sshConfig.getString("tavernaserver.ssh.password"))) {
                ssh.authPassword(sshConfig.getString("tavernaserver.ssh.user"),
                    sshConfig.getString("tavernaserver.ssh.password"));
            } else {
                ssh.authPublickey(sshConfig.getString("tavernaserver.ssh.user"));
            }

            workingDir = createWorkingDir();
            prepareServer();
            executeWorkflow();
            getResults();

            if (sshConfig.getBoolean("tavernaserver.ssh.server.cleanup")) {
                cleanupServer();
            }

        } finally {
            ssh.disconnect();
        }

    }

    /**
     * Clears temporary data used for each execute.
     */
    private void clear() {
        createdDirsCache.clear();
        tempFilePaths.clear();
        workingDir = null;
        ssh = null;
        inputDocPath = null;
        outputDocPath = null;
        workflowPath = null;
        outputData.clear();
    }

    /**
     * Prepares the ssh client.
     * 
     * @throws IOException
     *             if an error occured during setting up the client
     */
    private void prepareClient() throws IOException {
        ssh = new SSHClient();

        if (sshConfig.getString("tavernaserver.ssh.fingerprint") != null
            && !"".equals(sshConfig.getString("tavernaserver.ssh.fingerprint"))) {
            ssh.addHostKeyVerifier(sshConfig.getString("tavernaserver.ssh.fingerprint"));
        }
        ssh.useCompression();
    }

    /**
     * Prepares the server for execution.
     * 
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private void prepareServer() throws IOException, TavernaExecutorException {
        outputDocPath = workingDir + File.separator + OUTPUT_DOC_FILENAME;
        inputDocPath = prepareInputs();
        workflowPath = prepareWorkflow();
    }

    /**
     * Prepares the inputs of the workflow run.
     * 
     * @return the server path of the input document
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private String prepareInputs() throws IOException, TavernaExecutorException {
        Element rootElement = new Element("dataThingMap", NAMESPACE);
        Document document = new Document(rootElement);

        for (Entry<TavernaPort, Object> entry : inputData.entrySet()) {
            TavernaPort port = entry.getKey();

            Object value = entry.getValue();
            Object dereferencedInput = dereferenceInput(port, value);

            DataThing thing = DataThingFactory.bake(dereferencedInput);

            Element dataThingElement = new Element("dataThing", NAMESPACE);
            dataThingElement.setAttribute("key", port.getName());
            dataThingElement.addContent(thing.getElement());
            rootElement.addContent(dataThingElement);
        }

        XMLOutputter xo = new XMLOutputter(Format.getPrettyFormat());
        // PrintWriter out = new PrintWriter(new FileWriter(inputFile));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            xo.output(document, out);
            return uploadFile(new ByteArraySourceFile(INPUT_DOC_FILENAME, out.toByteArray()), "");
        } finally {
            out.close();
        }

    }

    /**
     * Dereferences an input object of the provided port recursively.
     * 
     * @param port
     *            the port
     * @param value
     *            input object
     * @return a derefereneced object
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private Object dereferenceInput(TavernaPort port, Object value) throws IOException, TavernaExecutorException {
        if (value instanceof Collection<?>) {
            ArrayList<Object> results = new ArrayList<Object>(((Collection<?>) value).size());
            for (Object object : (Collection<?>) value) {
                results.add(dereferenceInput(port, object));
            }
            return results;
        } else if (value instanceof File) {
            return uploadFile((File) value, port.getName());
        } else if (value instanceof ByteArraySourceFile) {
            return uploadFile((ByteArraySourceFile) value, port.getName());
        } else if (value instanceof SSHTempFile) {
            return registerTempPath((SSHTempFile) value, port.getName());
        } else {
            return value;
        }
    }

    /**
     * Uploads a file to the provided target directory.
     * 
     * @param file
     *            the file to upload
     * @param targetDir
     *            the target directory name
     * @return the path of the file on the server
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private String uploadFile(File file, String targetDir) throws IOException, TavernaExecutorException {

        String targetPath;
        if (targetDir.equals("")) {
            targetPath = workingDir + File.separator + file.getName();
        } else {
            targetPath = workingDir + File.separator + targetDir + File.separator + file.getName();
            createDir(targetDir);
        }

        if (file.canRead()) {
            ssh.newSCPFileTransfer().upload(new FileSystemFile(file), targetPath);
            LOG.debug("Uploaded file " + file.getAbsolutePath() + " to " + targetPath);
        } else {
            LOG.error("Cannot load file " + file.getAbsolutePath() + " for upload");
            throw new TavernaExecutorException("Cannot load file " + file.getAbsolutePath() + " for upload");
        }
        return targetPath;
    }

    /**
     * Uploads an in-memory-source-file to the provided target directory.
     * 
     * @param file
     *            the file to upload
     * @param targetDir
     *            the target directory name
     * @return the path of the file on the server
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private String uploadFile(InMemorySourceFile file, String targetDir) throws IOException, TavernaExecutorException {
        String targetPath;
        if (targetDir.equals("")) {
            targetPath = workingDir + File.separator + file.getName();
        } else {
            targetPath = workingDir + File.separator + targetDir + File.separator + file.getName();
            createDir(workingDir + File.separator + targetDir);
        }

        ssh.newSCPFileTransfer().upload(file, targetPath);
        LOG.debug("Uploaded file " + file.getName() + " to " + targetPath);
        return targetPath;
    }

    /**
     * Registers the the temporary file in the provided target directory and
     * returns the server path to it.
     * 
     * @param file
     *            the file
     * @param targetDir
     *            the target directory name
     * @return the path of the file on the server
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private String registerTempPath(SSHTempFile file, String targetDir) throws IOException, TavernaExecutorException {
        String targetPath;
        if (targetDir.equals("")) {
            targetPath = workingDir + File.separator + file.getName();
        } else {
            targetPath = workingDir + File.separator + targetDir + File.separator + file.getName();
            createDir(workingDir + File.separator + targetDir);
        }

        tempFilePaths.put(file, targetPath);

        LOG.debug("Added temporary file " + file.getName() + " to " + targetPath);
        return targetPath;
    }

    /**
     * Creates the working directory on the server.
     * 
     * @return the directory
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private String createWorkingDir() throws IOException, TavernaExecutorException {
        final Session session = ssh.startSession();
        try {
            final Command cmd = session.exec("mktemp -d -t plato.XXXXXXXXXXXXXXXXXXXX");
            String tempDir = IOUtils.readFully(cmd.getInputStream()).toString();
            cmd.join(5, TimeUnit.SECONDS);
            if (cmd.getExitStatus().equals(0)) {
                tempDir = tempDir.trim();
                LOG.debug("Created working directory " + tempDir);
                return tempDir;
            } else {
                String stderr = IOUtils.readFully(cmd.getErrorStream()).toString();
                LOG.error("Error creating working directory " + stderr);
                throw new TavernaExecutorException("Error creating working directory " + stderr);
            }
        } finally {
            session.close();
        }
    }

    /**
     * Creates a directory on the server if it does not already exist
     * 
     * @param dir
     *            name of the directory to create
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private void createDir(String dir) throws IOException, TavernaExecutorException {
        if (!createdDirsCache.contains(dir)) {
            final Session session = ssh.startSession();
            try {
                final Command cmd = session.exec("mkdir -p \"" + dir + "\"");
                cmd.join(5, TimeUnit.SECONDS);

                if (cmd.getExitStatus().equals(0)) {
                    LOG.debug("Created directory " + dir);
                    createdDirsCache.add(dir);
                } else {
                    String stderr = IOUtils.readFully(cmd.getErrorStream()).toString();
                    LOG.error("Error creating directory " + dir + ": " + stderr);
                    throw new TavernaExecutorException("Error creating directory " + dir + ": " + stderr);
                }

            } finally {
                session.close();
            }
        }
    }

    /**
     * Executes a prepared workfow.
     * 
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private void executeWorkflow() throws IOException, TavernaExecutorException {
        final Session session = ssh.startSession();
        try {
            String command = TAVERNA_COMMAND.replace("%%inputdoc%%", inputDocPath)
                .replace("%%outputdoc%%", outputDocPath).replace("%%workflow%%", workflowPath);
            final Command cmd = session.exec(command);
            cmd.join(commandTimeout, TimeUnit.SECONDS);

            if (!cmd.getExitStatus().equals(0)) {
                String stderr = IOUtils.readFully(cmd.getErrorStream()).toString();
                LOG.error("Error executing workflow: " + stderr);
                throw new TavernaExecutorException("Error executing workflow: " + stderr);
            }

            LOG.debug("Executed workflow with command " + command);
        } finally {
            session.close();
        }
    }

    /**
     * Prepares a workflow for execution.
     * 
     * @return the workflow identifier for execution
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private String prepareWorkflow() throws IOException, TavernaExecutorException {
        if (workflowFile != null) {
            return uploadFile(workflowFile, "");
        } else if (workflowUrl != null && !workflowUrl.equals("")) {
            return workflowUrl;
        } else {
            throw new TavernaExecutorException("No workflow specified");
        }
    }

    /**
     * Reads the results of ports specified in outputPorts.
     * 
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private void getResults() throws IOException, TavernaExecutorException {

        // Download data
        File outputDocFile = File.createTempFile("ssh-taverna-executor-", ".xml");
        try {
            downloadFile(OUTPUT_DOC_FILENAME, outputDocFile);

            SAXBuilder builder = new SAXBuilder();
            FileInputStream is = new FileInputStream(outputDocFile);
            try {
                Document outputDocument = builder.build(is);

                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputDoc = outputter.outputString(outputDocument);

                Map<String, DataThing> outputDataThings = DataThingXMLFactory.parseDataDocument(outputDocument);

                for (TavernaPort port : outputPorts) {
                    DataThing outputDataThing = outputDataThings.get(port.getName());

                    if (outputDataThing == null) {
                        outputData.put(port, null);
                    } else {
                        outputData.put(port, outputDataThing.getDataObject());
                    }

                }

            } catch (JDOMException e) {
                throw new TavernaExecutorException("Error reading output document", e);
            } finally {
                is.close();
            }
        } finally {
            outputDocFile.delete();
        }

        // Download files
        for (Entry<TavernaPort, ?> entry : outputFiles.entrySet()) {
            getResultFiles(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Reads the results files of the provided port
     * 
     * @param port
     *            the port
     * @param value
     *            a file or nested collection of files
     * @return a file or nested collection of files
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private Object getResultFiles(TavernaPort port, Object value) throws IOException, TavernaExecutorException {
        if (value instanceof Collection<?>) {
            ArrayList<Object> results = new ArrayList<Object>(((Collection<?>) value).size());
            for (Object object : (Collection<?>) value) {
                results.add(dereferenceInput(port, object));
            }
            return results;
        } else if (value instanceof File) {
            String path = port.getName() + File.separator + ((File) value).getName();
            downloadFile(path, (File) value);
            return value;
        } else if (value instanceof SSHInMemoryTempFile) {
            downloadFile((SSHInMemoryTempFile) value);
            return value;
        } else {
            return value;
        }
    }

    /**
     * Downloads a path to a local file.
     * 
     * @param path
     *            the server path
     * @param localFile
     *            the local file
     * @throws IOException
     */
    private void downloadFile(String path, File localFile) throws IOException {
        String sourcePath = workingDir + File.separator + path;

        ssh.newSCPFileTransfer().download(sourcePath, new FileSystemFile(localFile));
        LOG.debug("Downloaded file " + path + " to " + localFile.getPath());
    }

    /**
     * Downloads a path to a temp file.
     * 
     * @param path
     *            the server path
     * @param tempFile
     *            the temp file
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private void downloadFile(SSHInMemoryTempFile tempFile) throws IOException, TavernaExecutorException {

        String tempFilePath = tempFilePaths.get(tempFile);
        if (tempFilePath == null) {
            LOG.error("The temp file " + tempFile.getName() + " is not registerd.");
            throw new TavernaExecutorException("The temp file " + tempFile.getName() + " is not registerd.");
        }

        ByteArrayDestFile destFile = new ByteArrayDestFile();
        ssh.newSCPFileTransfer().download(tempFilePath, destFile);
        tempFile.setData(destFile.getData());
        LOG.debug("Downloaded file " + tempFilePath + " to " + tempFile.getName());
    }

    /**
     * Cleans up created resources on the server.
     * 
     * @throws IOException
     * @throws TavernaExecutorException
     */
    private void cleanupServer() throws IOException, TavernaExecutorException {
        final Session session = ssh.startSession();
        try {
            final Command cmd = session.exec("rm -rf " + workingDir);
            cmd.join(5, TimeUnit.SECONDS);

            if (!cmd.getExitStatus().equals(0)) {
                String stderr = IOUtils.readFully(cmd.getErrorStream()).toString();
                LOG.error("Error deleting working directory " + stderr);
                throw new TavernaExecutorException("Error deleting working directory " + stderr);
            }

            LOG.debug("Deleted working directory " + workingDir);
        } finally {
            session.close();
        }
    }

    // --------------- getter/setter ---------------
    public String getWorkflowUrl() {
        return workflowUrl;
    }

    public void setWorkflowUrl(String workflowUrl) {
        this.workflowUrl = workflowUrl;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.scape_project.planning.taverna.executor.TavernaExecutor#getOutputData
     * ()
     */
    @Override
    public Map<TavernaPort, ?> getOutputData() {
        return outputData;
    }

    public void setOutputData(Map<TavernaPort, Object> outputData) {
        this.outputData = outputData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.scape_project.planning.taverna.executor.TavernaExecutor#getOutputFiles
     * ()
     */
    @Override
    public HashMap<TavernaPort, ?> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(HashMap<TavernaPort, ?> outputFiles) {
        this.outputFiles = outputFiles;
    }

    public Set<TavernaPort> getOutputPorts() {
        return outputPorts;
    }

    public void setOutputPorts(Set<TavernaPort> outputPorts) {
        this.outputPorts = outputPorts;
    }

    public String getOutputDoc() {
        return outputDoc;
    }

    public void setOutputDoc(String outputDoc) {
        this.outputDoc = outputDoc;
    }

    /**
     * Implementation of in-memory-source-file that reads the data from a byte
     * array.
     */
    public class ByteArraySourceFile extends InMemorySourceFile {

        private byte[] data;
        private String name;

        public ByteArraySourceFile(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getLength() {
            return data.length;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }
    }

    /**
     * Implementation of in-memory-destination-file that writes to a byte array.
     */
    public class ByteArrayDestFile extends InMemoryDestFile {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        @Override
        public OutputStream getOutputStream() throws IOException {
            return os;
        }

        public byte[] getData() {
            return os.toByteArray();
        }
    }

}
