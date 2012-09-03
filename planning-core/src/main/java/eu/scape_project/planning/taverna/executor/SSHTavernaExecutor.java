package eu.scape_project.planning.taverna.executor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import net.sf.taverna.t2.baclava.factory.DataThingXMLFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.pw.planning.utils.PropertiesLoader;

public class SSHTavernaExecutor implements TavernaExecutor {

	private static Logger log = LoggerFactory
			.getLogger(SSHTavernaExecutor.class);

	/**
	 * Name of the executor properties
	 */
	private static final String SSH_PROPERTIES = "config/tavernaserverssh";
	/**
	 * Filename of input data document
	 */
	private static final String INPUT_DOC_FILENAME = "input_data.xml";
	/**
	 * Filename of output data document
	 */
	private static final String OUTPUT_DOC_FILENAME = "output_data.xml";
	/**
	 * Taverna command
	 */
	private static final String TAVERNA_COMMAND = "$TAVERNA_HOME/executeworkflow.sh -inputdoc %%inputdoc%% -outputdoc %%outputdoc%% %%workflow%%";

	private static final Namespace namespace = Namespace.getNamespace("b",
			"http://org.embl.ebi.escience/baclava/0.1alpha");

	/*
	 * Properties
	 */
	private Properties sshProperties;
	private Integer commandTimeout;

	/*
	 * Executor parameters
	 */
	private String workflowUri;
	private File workflowFile;
	private Map<TavernaPort, Object> inputData = new HashMap<TavernaPort, Object>();
	HashSet<TavernaPort> outputPorts = new HashSet<TavernaPort>();
	HashMap<TavernaPort, Object> outputFiles = new HashMap<TavernaPort, Object>();

	private Map<TavernaPort, Object> outputData = new HashMap<TavernaPort, Object>();;

	/*
	 * Cache of created directories on the server
	 */
	private HashSet<String> createdDirsCache = new HashSet<String>();

	/*
	 * Taverna call stuff
	 */
	private SSHClient ssh;
	private String tempDir;

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
		try {
			sshProperties = PropertiesLoader.loadProperties(SSH_PROPERTIES);
			commandTimeout = Integer.parseInt(sshProperties
					.getProperty("command.timeout"));
		} catch (IOException e) {
			log.error("Error loading properties " + SSH_PROPERTIES, e);
		}
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
		KeyProvider kp = getKeyProvider();

		try {
			ssh.connect(sshProperties.getProperty("host"));
			ssh.authPublickey(sshProperties.getProperty("user"), kp);

			tempDir = createWorkingDir();
			prepareServer();
			executeWorkflow();
			getResults();

			for (Entry<TavernaPort, Object> entry : outputFiles.entrySet()) {
				getResultFiles(entry.getKey(), entry.getValue());
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
		tempDir = null;
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
	 */
	private void prepareClient() throws IOException {
		ssh = new SSHClient();

		ssh.addHostKeyVerifier(sshProperties.getProperty("fingerprint"));
		ssh.useCompression();
	}

	/**
	 * Returns an ssh key provider for the.
	 * 
	 * @return the keyprovider
	 * @throws IOException
	 */
	private KeyProvider getKeyProvider() throws IOException {
		// KeyProvider kp = ssh.loadKeys("/home/plangg/.ssh/id_dsa");
		KeyProvider kp = ssh.loadKeys(sshProperties.getProperty("private.key"),
				sshProperties.getProperty("public.key"), new PasswordFinder() {
					@Override
					public char[] reqPassword(Resource<?> resource) {
						if (sshProperties.getProperty("password") == null) {
							return null;
						} else {
							return sshProperties.getProperty("password")
									.toCharArray();
						}
					}

					@Override
					public boolean shouldRetry(Resource<?> resource) {
						return false;
					}
				});

		return kp;
	}

	/**
	 * Prepares the server for execution.
	 * 
	 * @throws IOException
	 * @throws TavernaExecutorException
	 */
	private void prepareServer() throws IOException, TavernaExecutorException {
		outputDocPath = tempDir + File.separator + OUTPUT_DOC_FILENAME;
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
		Element rootElement = new Element("dataThingMap", namespace);
		Document document = new Document(rootElement);

		for (Entry<TavernaPort, Object> entry : inputData.entrySet()) {
			TavernaPort port = entry.getKey();

			Object value = entry.getValue();
			Object dereferencedInput = dereferenceInput(port, value);

			DataThing thing = DataThingFactory.bake(dereferencedInput);

			Element dataThingElement = new Element("dataThing", namespace);
			dataThingElement.setAttribute("key", port.getName());
			dataThingElement.addContent(thing.getElement());
			rootElement.addContent(dataThingElement);
		}

		XMLOutputter xo = new XMLOutputter(Format.getPrettyFormat());
		// PrintWriter out = new PrintWriter(new FileWriter(inputFile));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			xo.output(document, out);
			return uploadFile(
					new ByteArraySourceFile(INPUT_DOC_FILENAME,
							out.toByteArray()), tempDir);
		} finally {
			out.close();
		}

	}

	/**
	 * Dereferences an input object of the provided port.
	 * 
	 * @param port
	 *            the port
	 * @param value
	 *            input object
	 * @return a derefereneced object
	 * @throws IOException
	 * @throws TavernaExecutorException
	 */
	private Object dereferenceInput(TavernaPort port, Object value)
			throws IOException, TavernaExecutorException {
		if (value instanceof File) {
			return uploadFile((File) value,
					tempDir + File.separator + port.getName());
		} else if (value instanceof Collection<?>) {
			ArrayList<Object> results = new ArrayList<Object>(
					((Collection<?>) value).size());
			for (Object object : (Collection<?>) value) {
				results.add(dereferenceInput(port, object));
			}
			return results;
		} else {
			return value;
		}
	}

	/**
	 * Creates the working directory on the server.
	 * 
	 * @return the directory
	 * @throws IOException
	 * @throws TavernaExecutorException
	 */
	private String createWorkingDir() throws IOException,
			TavernaExecutorException {
		final Session session = ssh.startSession();
		try {
			final Command cmd = session
					.exec("mktemp -d plato.XXXXXXXXXXXXXXXXXXXX");
			String tempDir = IOUtils.readFully(cmd.getInputStream()).toString();
			cmd.join(5, TimeUnit.SECONDS);
			if (cmd.getExitStatus().equals(0)) {
				tempDir = tempDir.trim();
				log.debug("Created temporary directory " + tempDir);
				return tempDir;
			} else {
				String stderr = IOUtils.readFully(cmd.getErrorStream())
						.toString();
				log.error("Error creating temporary directory " + stderr);
				throw new TavernaExecutorException(
						"Error creating temporary directory " + stderr);
			}
		} finally {
			session.close();
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
	private String uploadFile(File file, String targetDir) throws IOException,
			TavernaExecutorException {
		String targetPath = targetDir + File.separator + file.getName();
		if (!createdDirsCache.contains(targetDir)) {
			createDir(targetDir);
		}
		if (file.canRead()) {
			ssh.newSCPFileTransfer().upload(new FileSystemFile(file),
					targetPath);
			log.debug("Uploaded file " + file.getAbsolutePath() + " to "
					+ targetPath);
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
	private String uploadFile(InMemorySourceFile file, String targetDir)
			throws IOException, TavernaExecutorException {
		String targetPath = targetDir + File.separator + file.getName();
		if (!createdDirsCache.contains(targetDir)) {
			createDir(targetDir);
		}
		ssh.newSCPFileTransfer().upload(file, targetPath);
		log.debug("Uploaded file " + file.getName() + " to " + targetPath);
		return targetPath;
	}

	/**
	 * Creates a directory on the server if it does not already exist
	 * 
	 * @param dir
	 *            name of the directory to create
	 * @throws IOException
	 * @throws TavernaExecutorException
	 */
	private void createDir(String dir) throws IOException,
			TavernaExecutorException {
		final Session session = ssh.startSession();
		try {
			final Command cmd = session.exec("mkdir -p \"" + dir + "\"");
			cmd.join(5, TimeUnit.SECONDS);

			if (cmd.getExitStatus().equals(0)) {
				log.debug("Created directory " + dir);
				createdDirsCache.add(dir);
			} else {
				String stderr = IOUtils.readFully(cmd.getErrorStream())
						.toString();
				log.error("Error creating directory " + dir + ": " + stderr);
				throw new TavernaExecutorException("Error creating directory "
						+ dir + ": " + stderr);
			}

		} finally {
			session.close();
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
			String command = TAVERNA_COMMAND
					.replace("%%inputdoc%%", inputDocPath)
					.replace("%%outputdoc%%", outputDocPath)
					.replace("%%workflow%%", workflowPath);
			final Command cmd = session.exec(command);
			cmd.join(commandTimeout, TimeUnit.SECONDS);

			if (cmd.getExitStatus().equals(0)) {
				log.info("Executed workflow");
			} else {
				String stderr = IOUtils.readFully(cmd.getErrorStream())
						.toString();
				log.error("Error executing workflow: " + stderr);
				throw new TavernaExecutorException("Error executing workflow: "
						+ stderr);
			}

			log.debug("Executed workflow with command " + command);
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
	private String prepareWorkflow() throws IOException,
			TavernaExecutorException {
		if (workflowFile != null) {
			return uploadFile(workflowFile, tempDir);
		} else if (workflowUri != null && !workflowUri.equals("")) {
			return workflowUri;
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

		File outputDoc = File.createTempFile("ssh-taverna-executor-", ".xml");
		try {
			downloadFile(OUTPUT_DOC_FILENAME, outputDoc);

			SAXBuilder builder = new SAXBuilder();
			FileInputStream is = new FileInputStream(outputDoc);
			try {
				Document inputDoc = builder.build(is);
				Map<String, DataThing> outputDataThings = DataThingXMLFactory
						.parseDataDocument(inputDoc);

				for (TavernaPort port : outputPorts) {
					DataThing outputDataThing = outputDataThings.get(port
							.getName());

					if (outputDataThing == null) {
						outputData.put(port, null);
					} else {
						outputData.put(port, outputDataThing.getDataObject());
					}

				}

			} catch (JDOMException e) {
				throw new TavernaExecutorException(
						"Error reading output document", e);
			} finally {
				is.close();
			}
		} finally {
			outputDoc.delete();
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
	private Object getResultFiles(TavernaPort port, Object value)
			throws IOException, TavernaExecutorException {
		if (value instanceof File) {
			String path = tempDir + File.separator + port.getName()
					+ File.separator + ((File) value).getName();
			downloadFile(path, (File) value);
			return value;
		} else if (value instanceof Collection<?>) {
			ArrayList<Object> results = new ArrayList<Object>(
					((Collection<?>) value).size());
			for (Object object : (Collection<?>) value) {
				results.add(dereferenceInput(port, object));
			}
			return results;
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
		ssh.newSCPFileTransfer().download(tempDir + File.separator + path,
				new FileSystemFile(localFile));
		log.debug("Downloaded file " + path + " to "
				+ localFile.getAbsolutePath());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.scape_project.planning.taverna.executor.TavernaExecutor#getOutputData
	 * ()
	 */
	@Override
	public Map<TavernaPort, Object> getOutputData() {
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
	public HashMap<TavernaPort, Object> getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(HashMap<TavernaPort, Object> outputFiles) {
		this.outputFiles = outputFiles;
	}

	public HashSet<TavernaPort> getOutputPorts() {
		return outputPorts;
	}

	public void setOutputPorts(HashSet<TavernaPort> outputPorts) {
		this.outputPorts = outputPorts;
	}

	/**
	 * Implementation of in-memory-source-file that reads the data from a byte
	 * array.
	 */
	private class ByteArraySourceFile extends InMemorySourceFile {

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

}
