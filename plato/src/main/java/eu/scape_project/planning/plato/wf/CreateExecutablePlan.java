/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
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
/**
 * 
 */
package eu.scape_project.planning.plato.wf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.taverna.parser.T2FlowParser;
import eu.scape_project.planning.taverna.parser.T2FlowParserFallback;
import eu.scape_project.planning.taverna.parser.TavernaParserException;
import eu.scape_project.planning.utils.FileUtils;
import eu.scape_project.planning.xml.PreservationActionPlanGenerator;

import org.dom4j.io.OutputFormat;
import org.slf4j.Logger;

/**
 * Workflow step to configure the executable plan.
 * 
 * @author Michael Kraxner
 */
@Stateful
@ConversationScoped
public class CreateExecutablePlan extends AbstractWorkflowStep {
    private static final long serialVersionUID = -971490825722362606L;

    @Inject
    private Logger log;

    @Inject
    private PreservationActionPlanGenerator generator;

    /**
     * Default constructor.
     */
    public CreateExecutablePlan() {
        requiredPlanState = PlanState.ANALYSED;
        correspondingPlanState = PlanState.EXECUTEABLE_PLAN_CREATED;
    }

    @Override
    public void init(Plan p) {
        super.init(p);

        // If we don't have tool parameters, we copy them from the chosen
        // alternative's config settings:
        if (plan.getExecutablePlanDefinition().getToolParameters() == null
            || "".equals(plan.getExecutablePlanDefinition().getToolParameters())) {
            plan.getExecutablePlanDefinition().setToolParameters(
                plan.getRecommendation().getAlternative().getExperiment().getSettings());
        }
    }

    @Override
    protected void saveStepSpecific() {
        saveEntity(plan.getExecutablePlanDefinition());
        saveEntity(plan.getPreservationActionPlanDefinition());
    }

    /**
     * Reads an executable plan from the provided inputstream and stores it.
     * 
     * @param stream
     *            the executable plan
     * @throws PlanningException
     *             if an error occured
     * @throws TavernaParserException
     *             if the executable plan could not be parsed
     */
    public void readT2flowExecutablePlan(InputStream stream) throws PlanningException, TavernaParserException {
        ByteStream bsData = this.convertToByteStream(stream);
        if (bsData == null) {
            throw new PlanningException("An error occurred while storing the executable plan");
        }

        T2FlowParser parser = T2FlowParserFallback.createParser(new ByteArrayInputStream(bsData.getData()));

        if (!parser.getProfile().equals(T2FlowParser.ComponentProfile.ExecutablePlan)) {
            // TODO: Throw exception, this is commented for test purposes
            // throw new
            // PlanningException("The provided profile does not adhere to the Executable Plan Profile.");
        }

        String name = parser.getName();
        storeExecutablePlan(FileUtils.makeFilename(name) + ".t2flow", bsData);
    }

    /**
     * Converts the input stream object to a {@link ByteStream} wrapper.
     * 
     * @param stream
     *            the stream to wrap.
     * @return the new {@link ByteStream} or null if an error occurred.
     */
    private ByteStream convertToByteStream(InputStream stream) {
        ByteStream bsData = null;
        byte[] bytes = null;
        try {
            bytes = FileUtils.inputStreamToBytes(stream);
            bsData = new ByteStream();
            bsData.setData(bytes);
            bsData.setSize(bytes.length);
        } catch (IOException e) {
            log.error("An error occurred while converting the stream: {}", e.getMessage());
        }

        return bsData;
    }

    /**
     * Stores the provided executable plan in the executable plan definition.
     * 
     * @param name
     *            the name of the plan
     * @param executablePlan
     *            the executable plan as t2flow
     * @throws PlanningException
     *             if an error occurred during storing
     */
    private void storeExecutablePlan(String name, ByteStream executablePlan) throws PlanningException {
        DigitalObject object = new DigitalObject();
        object.setContentType("application/vnd.taverna.t2flow+xml");
        object.setFullname(name);
        object.setData(executablePlan);

        try {
            digitalObjectManager.moveDataToStorage(object);
            plan.getExecutablePlanDefinition().setT2flowExecutablePlan(object);
            addedBytestreams.add(object.getPid());
        } catch (StorageException e) {
            log.error("An error occurred while storing the executable plan: {}", e.getMessage());
            throw new PlanningException("An error occurred while storing the profile", e);
        }
    }

    /**
     * Generates the preservation action plan other plan information and stores
     * it in the plan.
     * 
     * @throws PlanningException
     *             if an error occurred
     */
    public void generatePreservationActionPlan() throws PlanningException {

        generator.setCollectionProfile(plan.getSampleRecordsDefinition().getCollectionProfile());
        generator.setExecutablePlanDefinition(plan.getExecutablePlanDefinition());
        generator.setOutputFormat(OutputFormat.createPrettyPrint());

        try {
            DigitalObject object = generator.generatePreservationActionPlan();

            digitalObjectManager.moveDataToStorage(object);

            if (plan.getPreservationActionPlanDefinition().getPreservationActionPlan() != null
                && plan.getPreservationActionPlanDefinition().getPreservationActionPlan().isDataExistent()) {
                bytestreamsToRemove
                    .add(plan.getPreservationActionPlanDefinition().getPreservationActionPlan().getPid());
            }

            plan.getPreservationActionPlanDefinition().setPreservationActionPlan(object);
            addedBytestreams.add(object.getPid());

        } catch (UnsupportedEncodingException e) {
            log.error("Error generating preservation action plan {}.", e.getMessage());
            throw new PlanningException("Error generating preservation action plan.", e);
        } catch (StorageException e) {
            log.error("An error occurred while storing the executable plan: {}", e.getMessage());
            throw new PlanningException("An error occurred while storing the profile", e);
        }
    }
}
