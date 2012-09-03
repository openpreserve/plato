package eu.scape_project.planning.taverna.migrationaction;

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

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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

public class TavernaMigrationActionService implements IMigrationAction {
    private static Logger log = LoggerFactory.getLogger(TavernaMigrationActionService.class);

    @Override
    public boolean perform(PreservationActionDefinition action, SampleObject sampleObject) throws PlatoException {
        // TODO: Got from at.tuwien.minimee.migration.MiniMeeMigrationService.
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

        try {
            URL url = new URL(action.getUrl());
            InputStream is = url.openStream();
            try {
                HashMap<TavernaPort, Object> inputData = new HashMap<TavernaPort, Object>();

                T2FlowParser t2flowParser = T2FlowParserFallback.createParser(is);
                if (!t2flowParser.getProfile().equals(T2FlowParser.ComponentProfile.MigrationAction)) {
                    throw new PlatoException("The workflow " + action.getUrl() + " is no MigrationAction");
                }

                // Input from path
                Set<TavernaPort> fromPorts = t2flowParser.getInputPorts(new URI(
                    T2FlowParserFallback.FROM_OBJECT_PATH_URI));
                if (fromPorts.size() != 1) {
                    log.error("Number of from ports is " + fromPorts.size());
                    throw new PlatoException("Number of from ports is " + fromPorts.size());
                }

                for (TavernaPort fromPort : fromPorts) {
                    inputData.put(fromPort, tavernaExecutor.new ByteArraySourceFile(digitalObject.getFullname(),
                        digitalObject.getData().getData()));
                }

                // Input to path
                Set<TavernaPort> toPorts = t2flowParser.getInputPorts(new URI(T2FlowParserFallback.TO_OBJECT_PATH_URI));
                if (toPorts.size() != 1) {
                    log.error("Number of to ports is " + fromPorts.size());
                    throw new PlatoException("Number of to ports is " + fromPorts.size());
                }

                SSHInMemoryTempFile tempFile = new SSHInMemoryTempFile();
                tempFile.setName("result." + digitalObject.getFullname() + "." + targetExtension);
                for (TavernaPort toPort : toPorts) {
                    inputData.put(toPort, tempFile);
                }

                // Workflow
                tavernaExecutor.setWorkflowUrl(action.getUrl());
                // Output ports to recieve
                Set<TavernaPort> outputPorts = t2flowParser.getOutputPorts();
                tavernaExecutor.setOutputPorts(outputPorts);

                // Output files
                Set<TavernaPort> outputToPorts = t2flowParser.getOutputPorts(new URI(
                    T2FlowParserFallback.TO_OBJECT_PATH_URI));
                if (toPorts.size() != 1) {
                    log.error("Number of to ports is " + fromPorts.size());
                    throw new PlatoException("Number of to ports is " + fromPorts.size());
                }

                HashMap<TavernaPort, SSHInMemoryTempFile> requestedFiles = new HashMap<TavernaPort, SSHInMemoryTempFile>(
                    1);
                for (TavernaPort outputToPort : outputToPorts) {
                    requestedFiles.put(outputToPort, tempFile);
                }
                tavernaExecutor.setOutputFiles(requestedFiles);

                // Execute
                tavernaExecutor.execute();

                result.setSuccessful(true);
                result.setReport(tavernaExecutor.getOutputDoc());

                Map<TavernaPort, ?> outputData = tavernaExecutor.getOutputData();
                Map<TavernaPort, ?> outputFiles = tavernaExecutor.getOutputFiles();

                DigitalObject u = new DigitalObject();
                for (Entry<TavernaPort, ?> entry : outputFiles.entrySet()) {
                    u.getData().setData(((SSHInMemoryTempFile) entry.getValue()).getData());
                }
                FormatInfo tFormat = new FormatInfo();
                tFormat.setDefaultExtension(targetExtension);
                result.setTargetFormat(tFormat);
                result.setMigratedObject(u);

            } catch (ParserConfigurationException e) {
                log.error("Error initializing T2Flow parser");
                throw new PlatoException("Error initializing T2Flow parser", e);
            } catch (SAXException e) {
                log.error("Error initializing T2Flow parser");
                throw new PlatoException("Error initializing T2Flow parser", e);
            } catch (TavernaParserException e) {
                log.error("Error parsing workflow");
                throw new PlatoException("Error parsing workflow", e);
            } catch (URISyntaxException e) {
                log.error("Invalid URI");
                throw new PlatoException("Invalid URI", e);
            } catch (TavernaExecutorException e) {
                log.error("Error executing taverna workflow");
                throw new PlatoException("Error executing taverna workflow", e);
            } finally {
                is.close();
            }
        } catch (MalformedURLException e) {
            log.error("Malformed action URL " + action.getUrl());
            throw new PlatoException("Malformed action URL " + action.getUrl(), e);
        } catch (IOException e) {
            log.error("Error downloading workflow " + action.getUrl());
            throw new PlatoException("Error downloading workflow " + action.getUrl(), e);
        }

        TavernaPort pathFromPort = new TavernaPort();
        pathFromPort.setName("path_from");
        pathFromPort.setDepth(1);

        return result;

    }

    @Override
    public MigrationResult getLastResult() {
        // TODO Auto-generated method stub
        return null;
    }

}
