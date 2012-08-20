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
package eu.scape_project.planning.plato.wf;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.model.PlanState;

/**
 * Business logic for workflow step Define Basis
 * 
 * @author Michael Kraxner, Markus Hamm
 */
@Stateful
@ConversationScoped
public class DefineBasis extends AbstractWorkflowStep {
    private static final long serialVersionUID = -2269973220973705568L;

    @Inject
    private Logger log;

    public DefineBasis() {
        this.requiredPlanState = PlanState.INITIALISED;
        this.correspondingPlanState = PlanState.BASIS_DEFINED;
    }

    @Override
    protected void saveStepSpecific() {
        prepareChangesForPersist.prepare(plan);

        saveEntity(plan.getProjectBasis());
    }

    // /**
    // * Method responsible for removing a policy-tree from a plan.
    // */
    // public void removePolicyTree() {
    // plan.getProjectBasis().getPolicyTree().setRoot(null);
    // plan.getProjectBasis().touch();
    // }
}
