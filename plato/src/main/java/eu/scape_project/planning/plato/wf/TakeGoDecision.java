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

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;

import eu.scape_project.planning.model.PlanState;

/**
 * Business logic for workflow step Take Go Decision
 * 
 * @author Markus Hamm, Michael Kraxner
 * 
 */
@Stateful
@ConversationScoped
public class TakeGoDecision extends AbstractWorkflowStep {
    private static final long serialVersionUID = 2756349796229463787L;

    public TakeGoDecision() {
        requiredPlanState = PlanState.ALTERNATIVES_DEFINED;
        correspondingPlanState = PlanState.GO_CHOSEN;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.scape_project.planning.plato.wf.AbstractWorkflowStep#saveStepSpecific
     * ()
     */
    @Override
    protected void saveStepSpecific() {
        prepareChangesForPersist.prepare(plan);
        // alternatives might have been discarded
        saveEntity(plan.getAlternativesDefinition());
        saveEntity(plan.getDecision());
    }

}
