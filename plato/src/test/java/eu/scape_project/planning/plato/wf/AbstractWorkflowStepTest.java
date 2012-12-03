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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import eu.scape_project.planning.bean.PrepareChangesForPersist;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;

import org.junit.Test;
import org.mockito.Mockito;

public class AbstractWorkflowStepTest {

    @Test
    public void save_planStateSetCorrectly() {
        // Mock objects
        AbstractWorkflowStep step = mock(AbstractWorkflowStep.class);
        Mockito.doCallRealMethod().when(step).save();

        step.requiredPlanState = PlanState.CREATED;
        Plan p = new Plan();
        p.getPlanProperties().setState(PlanState.PLAN_VALIDATED);
        step.plan = p;

        PrepareChangesForPersist prepareChangesForPersist = mock(PrepareChangesForPersist.class);
        step.prepareChangesForPersist = prepareChangesForPersist;

        EntityManager em = mock(EntityManager.class);
        when(em.merge(p)).thenReturn(p);
        step.em = em;

        // Execute test
        step.save();

        // Verify results
        Assert.assertTrue(p.getPlanProperties().getState() == PlanState.CREATED);
    }

}
