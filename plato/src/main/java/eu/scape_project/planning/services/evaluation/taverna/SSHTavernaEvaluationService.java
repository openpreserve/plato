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
package eu.scape_project.planning.services.evaluation.taverna;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.evaluation.EvaluatorException;
import eu.scape_project.planning.evaluation.IObjectEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.services.myexperiment.MyExperimentRESTClient;
import eu.scape_project.planning.services.myexperiment.MyExperimentRESTClient.ComponentQuery;
import eu.scape_project.planning.services.myexperiment.domain.ComponentConstants;
import eu.scape_project.planning.services.myexperiment.domain.Port;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowInfo;
import eu.scape_project.planning.services.taverna.executor.SSHTavernaExecutor;
import eu.scape_project.planning.services.taverna.executor.TavernaExecutorException;
import eu.scape_project.planning.utils.FileUtils;

/**
 * Evaluation service for executing evaluations using taverna on a remote server
 * via SSH.
 */
public class SSHTavernaEvaluationService implements IObjectEvaluator {
    private static Logger log = LoggerFactory.getLogger(SSHTavernaEvaluationService.class);

    private final CriteriaManager cm = new CriteriaManager();
    private MyExperimentRESTClient myExperiment = new MyExperimentRESTClient();

    /**
     * Creates a new SSH taverna evaluation service.
     */
    public SSHTavernaEvaluationService() {
        cm.init();
    }

    @Override
    public HashMap<String, Value> evaluate(Alternative alternative, SampleObject sample, DigitalObject result,
        List<String> measureUris, IStatusListener listener) throws EvaluatorException {
        HashMap<String, Value> results = new HashMap<String, Value>();

        if (result == null) {
            return results;
        }

        Set<String> processedMeasures = new HashSet<String>(measureUris.size());

        for (String measure : measureUris) {
            if (!processedMeasures.contains(measure)) {
                List<WorkflowInfo> wfs = queryMyExperiment(sample, result, measure);
                for (WorkflowInfo wf : wfs) {
                    Map<String, Value> wfResults = evaluate(sample, result, wf, measureUris, listener);
                    results.putAll(wfResults);
                    processedMeasures.addAll(results.keySet());
                }
            }
        }

        return results;
    }

    /**
     * Evaluates the provided digital objects using the service.
     * 
     * @param sample
     *            the sample object
     * @param result
     *            the result object
     * @param service
     *            the service used for evaluation
     * @param measureUris
     *            measures to evaluate
     * @param listener
     *            status listener
     * @return a map of measure URIs and values
     * @throws EvaluatorException
     *             if an error occurred during evaluation
     */
    private HashMap<String, Value> evaluate(DigitalObject sample, DigitalObject result, WorkflowInfo service,
        List<String> measureUris, IStatusListener listener) throws EvaluatorException {

        SSHTavernaExecutor tavernaExecutor = new SSHTavernaExecutor();
        tavernaExecutor.init();

        HashMap<String, Value> results = new HashMap<String, Value>();

        // Get description
        WorkflowDescription workflowDescription = MyExperimentRESTClient.getWorkflow(service.getDescriptor());
        workflowDescription.readMetadata();

        if (!workflowDescription.getProfile().equals("http://purl.org/DP/components#Characterisation")
            && !workflowDescription.getProfile().equals("http://purl.org/DP/components#QAObjectComparison")) {
            log.warn("The workflow {} is no CC or QA component.", service.getDescriptor());
            throw new EvaluatorException("The workflow " + service.getDescriptor() + " is no CC or QA component.");
        }

        // Input
        if (workflowDescription.getProfile().equals("http://purl.org/DP/components#Characterisation")) {
            setCCInputData(workflowDescription, result, tavernaExecutor);
        } else {
            setQAInputData(workflowDescription, sample, result, tavernaExecutor);
        }

        // Workflow
        tavernaExecutor.setWorkflow(service.getContentUri());

        // Output ports to receive
        List<Port> outputPorts = workflowDescription.getOutputPorts();
        Set<String> outputPortNames = new HashSet<String>(outputPorts.size());
        for (Port p : outputPorts) {
            outputPortNames.add(p.getName());
        }
        tavernaExecutor.setOutputPorts(outputPortNames);

        // Execute
        try {
            tavernaExecutor.execute();
        } catch (IOException e) {
            log.error("Error connecting to execution server", e);
            throw new EvaluatorException("Error connecting to execution server", e);
        } catch (TavernaExecutorException e) {
            log.error("Error executing taverna workflow", e);
            throw new EvaluatorException("Error executing taverna workflow", e);
        }

        Map<String, ?> outputData = tavernaExecutor.getOutputData();
        for (Port p : outputPorts) {
            String measure = p.getValue();
            // Ignore non-measures, measures for sample object
            if (measure != null && !measure.isEmpty() && measureUris.contains(measure)) {
                Object value = outputData.get(p.getName());
                Measure m = cm.getMeasure(measure);
                Value v = m.getScale().createValue();
                v.setComment("Evaluated by " + service.getDescriptor());
                v.parse(value.toString());
                results.put(measure, v);
            }
        }

        return results;
    }

    /**
     * Sets the input data of the taverna executor according for CC components.
     * 
     * @param workflowDescription
     *            the workflow description
     * @param digitalObject
     *            the digital object
     * @param tavernaExecutor
     *            taverna executor
     * @throws EvaluatorException
     *             if the input could not be set
     */
    private void setCCInputData(WorkflowDescription workflowDescription, DigitalObject digitalObject,
        SSHTavernaExecutor tavernaExecutor) throws EvaluatorException {
        HashMap<String, Object> inputData = new HashMap<String, Object>();

        List<Port> inputPorts = workflowDescription.getInputPorts();

        for (Port p : inputPorts) {
            if (ComponentConstants.VALUE_SOURCE_OBJECT.equals(p.getValue())) {
                inputData.put(p.getName(),
                    tavernaExecutor.new ByteArraySourceFile(FileUtils.makeFilename(digitalObject.getFullname()),
                        digitalObject.getData().getData()));
            } else {
                log.warn("The workflow has an unsupported port {} of type {}", p.getName(), p.getValue());
                throw new EvaluatorException("The workflow has an unsupported port " + p.getName() + " that accepts "
                    + p.getValue());
            }
        }

        tavernaExecutor.setInputData(inputData);
    }

    /**
     * Sets the input data of the taverna executor according for CC components.
     * 
     * @param workflowDescription
     *            the workflow description
     * @param digitalObject1
     *            the first digital object
     * @param digitalObject2
     *            the second digital object
     * @param tavernaExecutor
     *            taverna executor
     * @throws EvaluatorException
     *             if the input could not be set
     */
    private void setQAInputData(WorkflowDescription workflowDescription, DigitalObject digitalObject1,
        DigitalObject digitalObject2, SSHTavernaExecutor tavernaExecutor) throws EvaluatorException {
        HashMap<String, Object> inputData = new HashMap<String, Object>();

        List<Port> inputPorts = workflowDescription.getInputPorts();

        for (Port p : inputPorts) {
            if (ComponentConstants.VALUE_LEFT_OBJECT.equals(p.getValue())) {
                inputData.put(p.getName(),
                    tavernaExecutor.new ByteArraySourceFile(FileUtils.makeFilename(digitalObject1.getFullname()),
                        digitalObject1.getData().getData()));
            } else {
                if (ComponentConstants.VALUE_RIGHT_OBJECT.equals(p.getValue())) {
                    inputData.put(p.getName(),
                        tavernaExecutor.new ByteArraySourceFile(FileUtils.makeFilename(digitalObject2.getFullname()),
                            digitalObject2.getData().getData()));
                } else {
                    log.warn("The workflow has an unsupported port {} of type {}", p.getName(), p.getValue());
                    throw new EvaluatorException("The workflow has an unsupported port " + p.getName()
                        + " that accepts " + p.getValue());
                }
            }
        }
        tavernaExecutor.setInputData(inputData);
    }

    /**
     * Queries myExperiment for workflows that provide the required measure for
     * the digital objects.
     * 
     * @param sample
     *            the sample object
     * @param result
     *            the result object
     * @param measure
     *            the required measure
     * @return a list of workflows
     */
    private List<WorkflowInfo> queryMyExperiment(DigitalObject sample, DigitalObject result, String measure) {
        ComponentQuery q = myExperiment.createComponentQuery();

        String sampleMimetype = sample.getFormatInfo().getMimeType();
        String resultMimetype = result.getFormatInfo().getMimeType();

        q.addHandlesMimetype(sampleMimetype, resultMimetype).addHandlesMimetypeWildcard(sampleMimetype, resultMimetype)
            .addHandlesMimetypes(sampleMimetype, resultMimetype)
            .addHandlesMimetypesWildcard(sampleMimetype, resultMimetype)
            .addInputPort(ComponentConstants.VALUE_LEFT_OBJECT).addInputPort(ComponentConstants.VALUE_RIGHT_OBJECT)
            .addMeasureOutputPort(measure).finishQuery();

        return myExperiment.searchComponents(q);
    }
}
