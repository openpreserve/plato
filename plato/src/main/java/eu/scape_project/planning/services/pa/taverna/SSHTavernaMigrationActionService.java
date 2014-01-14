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
package eu.scape_project.planning.services.pa.taverna;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.interfaces.actions.IMigrationAction;
import eu.scape_project.planning.services.myexperiment.MyExperimentRESTClient;
import eu.scape_project.planning.services.myexperiment.domain.ComponentConstants;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Port;
import eu.scape_project.planning.taverna.executor.SSHInMemoryTempFile;
import eu.scape_project.planning.taverna.executor.SSHTavernaExecutor;
import eu.scape_project.planning.taverna.executor.TavernaExecutorException;
import eu.scape_project.planning.utils.FileUtils;

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

        HashMap<String, Object> inputData = new HashMap<String, Object>();

        WorkflowDescription workflowDescription = MyExperimentRESTClient.getWorkflow(action.getDescriptor());
        workflowDescription.readMetadata();
        if (!workflowDescription.getProfile().equals("http://purl.org/DP/components#MigrationAction")) {
            result.setSuccessful(false);
            result.setReport("The workflow " + action.getUrl() + " is no MigrationAction.");
            return result;
        }

        List<Port> inputPorts = workflowDescription.getInputPorts();

        for (Port p : inputPorts) {
            if (ComponentConstants.VALUE_SOURCE_OBJECT.equals(p.getValue())) {
                inputData.put(p.getName(),
                    tavernaExecutor.new ByteArraySourceFile(FileUtils.makeFilename(digitalObject.getFullname()),
                        digitalObject.getData().getData()));
            } else if (ComponentConstants.VALUE_PARAMETER.equals(p.getValue()) || p.getValue() == null) {
                String value = action.getParamByName(p.getName());
                if (value == null) {
                    value = "";
                }
                inputData.put(p.getName(), value);
            } else {
                result.setSuccessful(false);
                result.setReport("The workflow " + action.getUrl() + " has unsupported port " + p.getName()
                    + " that accepts " + p.getValue());
                return result;
            }
        }
        tavernaExecutor.setInputData(inputData);

        // Workflow
        tavernaExecutor.setWorkflowUrl(action.getUrl());

        // Output ports to receive
        List<Port> outputPorts = workflowDescription.getOutputPorts();
        Set<String> outputPortNames = new HashSet<String>(outputPorts.size());
        String targetPathPort = null;
        for (Port p : outputPorts) {
            outputPortNames.add(p.getName());

            if (ComponentConstants.VALUE_TARGET_OBJECT.equals(p.getValue())) {
                targetPathPort = p.getName();
            }
        }
        tavernaExecutor.setOutputPorts(outputPortNames);

        // Output files
        if (targetPathPort == null) {
            result.setSuccessful(false);
            result.setReport("The workflow " + action.getUrl() + " has not target port.");
            return result;
        }
        HashMap<String, SSHInMemoryTempFile> requestedFiles = new HashMap<String, SSHInMemoryTempFile>(1);
        SSHInMemoryTempFile tempFile = new SSHInMemoryTempFile();
        requestedFiles.put(targetPathPort, tempFile);
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

        Map<String, ?> outputFiles = tavernaExecutor.getOutputFiles();

        DigitalObject u = new DigitalObject();
        for (Entry<String, ?> entry : outputFiles.entrySet()) {
            SSHInMemoryTempFile resultFile = (SSHInMemoryTempFile) entry.getValue();
            u.getData().setData(resultFile.getData());
            u.setFullname(action.getShortname() + " - " + digitalObject.getFullname());
        }
        FormatInfo tFormat = new FormatInfo();
        result.setTargetFormat(tFormat);
        result.setMigratedObject(u);

        lastResult = result;
        return result;
    }

    @Override
    public MigrationResult getLastResult() {
        return lastResult;
    }

}
