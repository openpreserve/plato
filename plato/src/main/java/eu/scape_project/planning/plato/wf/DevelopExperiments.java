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
package eu.scape_project.planning.plato.wf;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PlanState;

/**
 * Workflow step to define configuration for experiments.
 * 
 * @author Michael Kraxner, Markus Hamm
 */
@Stateful
@ConversationScoped
public class DevelopExperiments extends AbstractWorkflowStep {

    private static final long serialVersionUID = 3224826109130261298L;

    @Inject
    private Logger log;

    /**
     * Default constructor.
     */
    public DevelopExperiments() {
        requiredPlanState = PlanState.GO_CHOSEN;
        correspondingPlanState = PlanState.EXPERIMENT_DEFINED;
    }

    @Override
    protected void saveStepSpecific() {
        saveEntity(plan.getTree());
        saveEntity(plan.getAlternativesDefinition());
    }

    /**
     * Sets the experiment workflow to the alternative.
     * 
     * @param alternative
     *            the alternative
     * @param workflow
     *            the workflow to add
     * @throws PlanningException
     *             if an error occurred during storage
     */
    public void setAlternativeWorkflow(final Alternative alternative, final DigitalObject workflow)
        throws PlanningException {
        try {
            digitalObjectManager.moveDataToStorage(workflow);

            DigitalObject oldWorkflow = alternative.getExperiment().getWorkflow();
            if (oldWorkflow != null && oldWorkflow.isDataExistent()) {
                bytestreamsToRemove.add(oldWorkflow.getPid());
            }

            plan.setAlternativeWorkflow(alternative, workflow);
            addedBytestreams.add(workflow.getPid());
        } catch (StorageException e) {
            log.error("An error occurred while storing the executable plan: {}", e.getMessage());
            throw new PlanningException("An error occurred while storing the profile", e);
        }
    }

    /**
     * Sets the experiment workflow URI to the alternative.
     * 
     * @param alternative
     *            the alternative
     * @param workflowUri
     *            the workflow URI to set
     */
    public void setAlternativeWorkflowUri(final Alternative alternative, final String workflowUri) {
        plan.setAlternativeWorkflowUri(alternative, workflowUri);
    }

}
