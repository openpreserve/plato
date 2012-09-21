package eu.scape_project.planning.taverna.migrationaction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.interfaces.actions.IMigrationAction;
import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.planning.taverna.executor.SSHInMemoryTempFile;
import eu.scape_project.planning.taverna.executor.SSHTavernaExecutor;
import eu.scape_project.planning.taverna.executor.TavernaExecutorException;
import eu.scape_project.planning.taverna.parser.T2FlowParser;
import eu.scape_project.planning.taverna.parser.T2FlowParserFallback;
import eu.scape_project.planning.taverna.parser.TavernaParserException;
import eu.scape_project.planning.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migration action service for executing migration actions using taverna on a
 * remote server via SSH.
 */
public class SSHTavernaMigrationActionService implements IMigrationAction {
    private static Logger log = LoggerFactory.getLogger(SSHTavernaMigrationActionService.class);

    private MigrationResult lastResult;

    @Override
    public boolean perform(PreservationActionDefinition action, SampleObject sampleObject) throws PlatoException {
        // TODO: Copied from
        // at.tuwien.minimee.migration.MiniMeeMigrationService.
        // Always return true?
        migrate(action, sampleObject);
        return true;
    }

    @Override
    public MigrationResult migrate(PreservationActionDefinition action, DigitalObject digitalObject)
        throws PlatoException {

        SSHTavernaExecutor tavernaExecutor = new SSHTavernaExecutor();
        tavernaExecutor.init();

        MigrationResult result = new MigrationResult();

        String targetExtension = action.getParamByName("settings");

        if (targetExtension == null || targetExtension.equals("")) {
            result.setSuccessful(false);
            result.setReport("Target extension not specified in settings.");
            return result;
        }

        try {
            URL url = new URL(action.getUrl());
            InputStream is = url.openStream();
            try {
                HashMap<TavernaPort, Object> inputData = new HashMap<TavernaPort, Object>();

                T2FlowParser t2flowParser = T2FlowParserFallback.createParser(is);
                if (!t2flowParser.getProfile().equals(T2FlowParser.ComponentProfile.MigrationAction)) {
                    result.setSuccessful(false);
                    result.setReport("The workflow " + action.getUrl() + " is no MigrationAction.");
                    return result;
                }

                // Input from path
                Set<TavernaPort> inputFromPorts =
                    t2flowParser.getInputPorts(new URI(T2FlowParserFallback.FROM_OBJECT_PATH_URI));
                if (inputFromPorts.size() != 1) {
                    result.setSuccessful(false);
                    result.setReport("The number of from ports of workflow " + action.getUrl() + " is "
                        + inputFromPorts.size() + ".");
                    return result;
                }

                for (TavernaPort inputFromPort : inputFromPorts) {
                    inputData.put(inputFromPort,
                        tavernaExecutor.new ByteArraySourceFile(FileUtils.makeFilename(digitalObject.getFullname()),
                            digitalObject.getData().getData()));
                }

                // Input to path
                Set<TavernaPort> inputToPorts =
                    t2flowParser.getInputPorts(new URI(T2FlowParserFallback.TO_OBJECT_PATH_URI));
                if (inputToPorts.size() != 1) {
                    result.setSuccessful(false);
                    result.setReport("The number of to ports of workflow " + action.getUrl() + " is "
                        + inputToPorts.size() + ".");
                    return result;
                }

                SSHInMemoryTempFile tempFile = new SSHInMemoryTempFile();
                tempFile.setName("result." + FileUtils.makeFilename(digitalObject.getFullname()) + "."
                    + targetExtension);
                for (TavernaPort inputToPort : inputToPorts) {
                    inputData.put(inputToPort, tempFile);
                }

                tavernaExecutor.setInputData(inputData);
                // Workflow
                tavernaExecutor.setWorkflowUrl(action.getUrl());
                // Output ports to recieve
                Set<TavernaPort> outputPorts = t2flowParser.getOutputPorts();
                tavernaExecutor.setOutputPorts(outputPorts);

                // Output files
                Set<TavernaPort> outputToPorts =
                    t2flowParser.getOutputPorts(new URI(T2FlowParserFallback.TO_OBJECT_PATH_URI));
                if (outputToPorts.size() != 1) {
                    result.setSuccessful(false);
                    result.setReport("The number of to ports of workflow " + action.getUrl() + " is "
                        + outputToPorts.size() + ".");
                    return result;
                }

                HashMap<TavernaPort, SSHInMemoryTempFile> requestedFiles =
                    new HashMap<TavernaPort, SSHInMemoryTempFile>(1);
                for (TavernaPort outputToPort : outputToPorts) {
                    requestedFiles.put(outputToPort, tempFile);
                }
                tavernaExecutor.setOutputFiles(requestedFiles);

                // Execute
                try {
                    tavernaExecutor.execute();
                } catch (IOException e) {
                    result.setSuccessful(false);
                    result.setReport("Error connecting to execution server");
                    log.error("Error connecting to execution server", e);
                    return result;
                } catch (TavernaExecutorException e) {
                    result.setSuccessful(false);
                    result.setReport("Error executing taverna workflow");
                    log.error("Error executing taverna workflow", e);
                    return result;
                }

                result.setSuccessful(true);
                result.setReport(tavernaExecutor.getOutputDoc());

                // Map<TavernaPort, ?> outputData =
                // tavernaExecutor.getOutputData();
                Map<TavernaPort, ?> outputFiles = tavernaExecutor.getOutputFiles();

                DigitalObject u = new DigitalObject();
                for (Entry<TavernaPort, ?> entry : outputFiles.entrySet()) {
                    u.getData().setData(((SSHInMemoryTempFile) entry.getValue()).getData());
                }
                FormatInfo tFormat = new FormatInfo();
                tFormat.setDefaultExtension(targetExtension);
                result.setTargetFormat(tFormat);
                result.setMigratedObject(u);

            } catch (TavernaParserException e) {
                log.error("Error parsing workflow", e);
                throw new PlatoException("Error parsing workflow", e);
            } catch (URISyntaxException e) {
                log.error("Invalid URI", e);
                throw new PlatoException("Invalid URI", e);
            } finally {
                is.close();
            }
        } catch (MalformedURLException e) {
            log.error("Malformed action URL " + action.getUrl(), e);
            throw new PlatoException("Malformed action URL " + action.getUrl(), e);
        } catch (IOException e) {
            log.error("Error downloading workflow " + action.getUrl(), e);
            throw new PlatoException("Error downloading workflow " + action.getUrl(), e);
        }

        lastResult = result;
        return result;
    }

    @Override
    public MigrationResult getLastResult() {
        return lastResult;
    }

}
