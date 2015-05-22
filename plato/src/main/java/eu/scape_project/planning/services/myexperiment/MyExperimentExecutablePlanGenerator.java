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
package eu.scape_project.planning.services.myexperiment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.services.IServiceInfo;
import eu.scape_project.planning.services.myexperiment.domain.ComponentConstants;
import eu.scape_project.planning.services.myexperiment.domain.Port;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator.InputSource;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator.RelatedObject;
import eu.scape_project.planning.utils.FileUtils;

/**
 * Plan generator based on recommended components from myExperiment.
 */
public class MyExperimentExecutablePlanGenerator {

    @Inject
    private Logger log;

    private T2FlowExecutablePlanGenerator generator;

    private String name;

    /**
     * Constructs a new plan generator with the provided parameters for the
     * executable plan.
     * 
     * @param name
     *            the plan name
     * @param author
     *            the plan author
     */
    public MyExperimentExecutablePlanGenerator(final String name, final String author) {
        this.name = name;
        generator = new T2FlowExecutablePlanGenerator(name, author);
        generator.addSourcePort();
        generator.addTargetPort();
    }

    /**
     * Generate the executable plan.
     * 
     * @return the executable plan as digital object
     * @throws PlanningException
     *             if an error occurred during generation
     */
    public DigitalObject generateExecutablePlan() throws PlanningException {
        DigitalObject workflow = new DigitalObject();

        // Create digital object
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);

        try {
            generator.generate(writer);
        } catch (IOException e) {
            log.warn("An error occured generating the executable plan.", e.getMessage());
            throw new PlanningException("An error occured generating the executable plan.", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.warn("An error occured closing the executable plan generator writer.", e.getMessage());
                    throw new PlanningException("An error occured closing the executable plan generator writer.", e);
                }
            }
        }

        byte[] data = out.toByteArray();
        ByteStream bsData = new ByteStream();
        bsData.setData(data);

        workflow.setContentType("application/vnd.taverna.t2flow+xml");
        workflow.setData(bsData);
        workflow.setFullname(FileUtils.makeFilename(name + ".t2flow"));

        return workflow;
    }

    /**
     * Sets the migration action of the executable plan.
     * 
     * @param migrationAction
     *            the migration action to set
     * @param parameters
     *            parameters for the migration action
     * @throws PlanningException
     *             if an error occurred while setting the migration action
     */
    public void setMigrationAction(IServiceInfo migrationAction, Map<String, String> parameters)
        throws PlanningException {
        WorkflowDescription wf = null;
        try {
            wf = MyExperimentRESTClient.getWorkflow(migrationAction.getDescriptor());
        } catch (Exception e) {
            throw new PlanningException("An error occurred querying myExperiment migration component", e);
        }
        if (wf == null) {
            throw new PlanningException("Could not retrieve workflow of migration component.");
        }
        try {
            wf.readMetadata();
            String workflowContent = MyExperimentRESTClient.getWorkflowContent(wf);
            generator.setMigrationComponent(wf, workflowContent, parameters);
        } catch (Exception e) {
            throw new PlanningException("An error occurred querying myExperiment migration component", e);
        }
    }

    /**
     * Adds the recommended components to the executable plan.
     * 
     * @param recommendedComponents
     *            the recommended components to add
     */
    public void addQaComponent(List<RecommendedComponent> recommendedComponents) {
        addQaComponent(recommendedComponents.toArray(new RecommendedComponent[recommendedComponents.size()]));
    }

    /**
     * Adds the recommended components to the executable plan.
     * 
     * @param recommendedComponents
     *            the recommended components to add
     */
    public void addQaComponent(RecommendedComponent... recommendedComponents) {
        for (RecommendedComponent recommendedComponent : recommendedComponents) {
            String workflowContent = MyExperimentRESTClient.getWorkflowContent(recommendedComponent.workflow);
            if (ComponentConstants.PROFILE_OBJECT_QA.equals(recommendedComponent.workflow.getProfile())) {
                generator.addQaComponent(recommendedComponent.workflow, workflowContent,
                    recommendedComponent.leftSource, recommendedComponent.rightSource, new HashMap<String, String>(0),
                    recommendedComponent.measures, recommendedComponent.relatedObject);
            } else if (ComponentConstants.PROFILE_CC.equals(recommendedComponent.workflow.getProfile())) {
                generator.addCcComponent(recommendedComponent.workflow, workflowContent,
                    new HashMap<String, String>(0), recommendedComponent.measures);
            } else {
                log.debug("Component search returned component with invalid or no profile.");
            }
        }
    }

    /**
     * Creates a component recommendation for the provided component based on
     * the required measures and target mimetype.
     * 
     * @param component
     *            the component to recommend
     * @param measures
     *            a list of measures required
     * @param targetMimetype
     *            the target mimetype
     * @return a component recommendation, or null if component could not be found
     */
    public static RecommendedComponent recommendComponent(IServiceInfo component, List<String> measures,
        String targetMimetype) {
        RecommendedComponent recommendedComponent = null;

        WorkflowDescription wfd = MyExperimentRESTClient.getWorkflow(component.getDescriptor());
        if (wfd == null) {
            return null;
        }
        wfd.readMetadata();
        List<Port> outputPorts = wfd.getOutputPorts();

        List<String> leftMeasures = new ArrayList<String>();
        List<String> rightMeasures = new ArrayList<String>();

        for (Port port : outputPorts) {
            if (measures.contains(port.getValue())) {
                if (port.getRelatedObject() == null) {
                    leftMeasures.add(port.getValue());
                    rightMeasures.add(port.getValue());
                } else if (ComponentConstants.VALUE_LEFT_OBJECT.equals(port.getRelatedObject())) {
                    leftMeasures.add(port.getValue());
                } else if (ComponentConstants.VALUE_RIGHT_OBJECT.equals(port.getRelatedObject())) {
                    rightMeasures.add(port.getValue());
                }
            }
        }

        boolean acceptsLeftMimetype = wfd.acceptsLeftMimetype(targetMimetype);
        boolean acceptsRightMimetype = wfd.acceptsRightMimetype(targetMimetype);
        if (acceptsLeftMimetype && acceptsRightMimetype) {
            if (leftMeasures.size() > rightMeasures.size()) {
                recommendedComponent = new RecommendedComponent(wfd, leftMeasures, InputSource.TARGET_OBJECT,
                    InputSource.SOURCE_OBJECT, RelatedObject.LEFT_OBJECT);
            } else {
                recommendedComponent = new RecommendedComponent(wfd, rightMeasures, InputSource.SOURCE_OBJECT,
                    InputSource.TARGET_OBJECT, RelatedObject.RIGHT_OBJECT);
            }
        } else if (acceptsLeftMimetype) {
            recommendedComponent = new RecommendedComponent(wfd, leftMeasures, InputSource.TARGET_OBJECT,
                InputSource.SOURCE_OBJECT, RelatedObject.LEFT_OBJECT);
        } else if (acceptsRightMimetype) {
            recommendedComponent = new RecommendedComponent(wfd, rightMeasures, InputSource.SOURCE_OBJECT,
                InputSource.TARGET_OBJECT, RelatedObject.RIGHT_OBJECT);
        }

        return recommendedComponent;
    }
}
