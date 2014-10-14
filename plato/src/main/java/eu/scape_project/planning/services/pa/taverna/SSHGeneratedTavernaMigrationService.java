/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.services.pa.taverna;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.interfaces.actions.IMigrationAction;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.services.taverna.executor.SSHInMemoryTempFile;
import eu.scape_project.planning.services.taverna.executor.SSHTavernaExecutor;
import eu.scape_project.planning.services.taverna.executor.TavernaExecutorException;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator;
import eu.scape_project.planning.utils.FileUtils;

/**
 * Migration action that executes generated Taverna executable plans.
 * 
 * @see T2FlowExecutablePlanGenerator
 */
public class SSHGeneratedTavernaMigrationService implements IMigrationAction {
    private static Logger log = LoggerFactory.getLogger(SSHGeneratedTavernaMigrationService.class);

    private CriteriaManager cm;

    /**
     * Constructs a new migration service.
     */
    public SSHGeneratedTavernaMigrationService() {
        cm = new CriteriaManager();
        cm.init();
    }

    @Override
    public boolean perform(Alternative alternative, SampleObject sampleObject) throws PlatoException {
        MigrationResult result = migrate(alternative, sampleObject);
        return result.isSuccessful();
    }

    @Override
    public MigrationResult migrate(Alternative alternative, DigitalObject digitalObject) throws PlatoException {
        DigitalObject workflow = alternative.getExperiment().getWorkflow();
        MigrationResult result = new MigrationResult();
        result.setSourceFormat(digitalObject.getFormatInfo());
        result.setSuccessful(false);

        // Prepare executor
        SSHTavernaExecutor tavernaExecutor = new SSHTavernaExecutor();
        tavernaExecutor.init();
        SSHTavernaExecutor.ByteArraySourceFile workflowFile = tavernaExecutor.new ByteArraySourceFile(
            FileUtils.makeFilename(workflow.getFullname()), workflow.getData().getData());
        tavernaExecutor.setWorkflow(workflowFile);

        // Input
        HashMap<String, Object> inputData = new HashMap<String, Object>();
        SSHTavernaExecutor.ByteArraySourceFile sourceFile = tavernaExecutor.new ByteArraySourceFile(
            FileUtils.makeFilename(digitalObject.getFullname()), digitalObject.getData().getData());
        inputData.put("source", sourceFile);
        tavernaExecutor.setInputData(inputData);

        // Outputs
        tavernaExecutor.setOutputPorts(null);
        HashMap<String, SSHInMemoryTempFile> requestedFiles = new HashMap<String, SSHInMemoryTempFile>(1);
        SSHInMemoryTempFile tempFile = new SSHInMemoryTempFile();
        requestedFiles.put("target", tempFile);
        tavernaExecutor.setOutputFiles(requestedFiles);

        // Execute
        try {
            tavernaExecutor.execute();
            result.setReport(tavernaExecutor.getOutputDoc());

            // Migrated file
            Map<String, ?> outputFiles = tavernaExecutor.getOutputFiles();

            DigitalObject migrated = new DigitalObject();
            for (Entry<String, ?> entry : outputFiles.entrySet()) {
                SSHInMemoryTempFile resultFile = (SSHInMemoryTempFile) entry.getValue();
                migrated.getData().setData(resultFile.getData());
                migrated.setFullname(alternative.getAction().getShortname() + " - " + digitalObject.getFullname());
            }
            result.setMigratedObject(migrated);
            result.setTargetFormat(migrated.getFormatInfo());
            result.setSuccessful(migrated.isDataExistent());

            // Measures
            Map<String, ?> outputData = tavernaExecutor.getOutputData();
            Map<String, Measurement> measurements = new HashMap<String, Measurement>();
            for (Entry<String, ?> outputEntry : outputData.entrySet()) {
                Measurement measurement = createMeasurement(outputEntry.getKey(), outputEntry.getValue());
                if (measurement != null) {
                    measurements.put(measurement.getMeasureId(), measurement);
                }
            }
            result.setMeasurements(measurements);
        } catch (IOException e) {
            throw new PlatoException("Error communicating with the server.", e);
        } catch (TavernaExecutorException e) {
            throw new PlatoException("Error executing taverna workflow", e);
        }
        return result;
    }

    /**
     * Creates a measurement from the provided ID and value.
     * 
     * If a {@link Measure} can be created, uses the value from the measure.
     * Otherwise the string representation of the {@code value} is used.
     * 
     * @param measureId
     *            the ID of the measure
     * @param value
     *            the measured value
     * @return a measurement
     */
    private Measurement createMeasurement(String measureId, Object value) {
        Measurement measurement = null;
        String measureUri = T2FlowExecutablePlanGenerator.guessMeasureUrl(measureId);
        if (measureUri != null) {
            Measure measure = cm.getMeasure(measureUri);
            if (measure != null) {
                Value v = measure.getScale().createValue();
                try {
                    v.parse(value.toString());
                    // The measurement value does not need a scale
                    v.setScale(null);
                    measurement = new Measurement();
                    measurement.setMeasureId(measureUri);
                    measurement.setValue(v);
                } catch (Exception e) {
                    // Catch parsing exceptions
                    log.debug("Error parsing measure value", e);
                }
            }
        }

        return measurement;
    }
}
