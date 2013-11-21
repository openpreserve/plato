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
package eu.scape_project.planning.plato.wfview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.plato.wfview.ViewWorkflow;
import eu.scape_project.planning.validation.ValidationError;

public class ViewWorkflowTest {

	private ViewWorkflow viewWorkflow;
	
	// FIXME: This tests are out-dated and need an update!
	/*
	@Before
	public void initializeDependencies() {
		// step 1
		AbstractView step1 = mock(AbstractView.class);
		when(step1.getCurrentPlanState()).thenReturn(PlanState.CREATED);
		when(step1.getViewUrl()).thenReturn("step1");
		when(step1.proceed()).thenReturn("success");
//		when(step1.tryProceed(List<ValidationError> errors)).thenReturn(true);

		// step 2
		AbstractView step2 = mock(AbstractView.class);
		when(step2.getCurrentPlanState()).thenReturn(PlanState.BASIS_DEFINED);
		when(step2.getViewUrl()).thenReturn("step2");
		when(step2.proceed()).thenReturn("success");
		
		// step 3
		AbstractView step3 = mock(AbstractView.class);
		when(step3.getCurrentPlanState()).thenReturn(PlanState.RECORDS_CHOSEN);
		when(step3.getViewUrl()).thenReturn("step3");
		when(step3.proceed()).thenReturn("success");
		
		List<AbstractView> steps = new ArrayList<AbstractView>();
		steps.add(step1);
		steps.add(step2);
		steps.add(step3);

		PlanProperties planProperties = mock(PlanProperties.class);
		when(planProperties.getState()).thenReturn(PlanState.CREATED);
		
		Plan plan = mock(Plan.class);
		when(plan.getPlanProperties()).thenReturn(planProperties);
		
		viewWorkflow = new ViewWorkflow();
		try {
			viewWorkflow.init(plan, steps);
		} catch (PlanningException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Test
	public void proceed_updatesCurrentStateCorrectly() throws PlanningException {
		 assertEquals(PlanState.CREATED, viewWorkflow.getCurrentState());
		 viewWorkflow.proceed();
		 assertEquals(PlanState.BASIS_DEFINED, viewWorkflow.getCurrentState());
		 viewWorkflow.proceed();
		 assertEquals(PlanState.RECORDS_CHOSEN, viewWorkflow.getCurrentState());
	}

	@Test
	public void proceed_returnsCorrectOutcomeStrings() throws PlanningException {
		 String outcomeStr;
		 outcomeStr = viewWorkflow.proceed();
		 assertEquals("step2", outcomeStr);
		 outcomeStr = viewWorkflow.proceed();
		 assertEquals("step3", outcomeStr);
	}

	@Test
	public void proceed_stopsProceedingAtTheEndOfWorkflowSteps() throws PlanningException {
		 viewWorkflow.proceed();
		 viewWorkflow.proceed();
		 viewWorkflow.proceed();
		 viewWorkflow.proceed();
		 viewWorkflow.proceed();
		 assertEquals(PlanState.RECORDS_CHOSEN, viewWorkflow.getCurrentState());
	}

	// FIXME: I don't really get this test - it should not be possible to set this plan state anyway....
	@Ignore
	@Test(expected=PlanningException.class)
	public void proceed_ThrowsExceptionAtInvalidState() throws PlanningException {
		viewWorkflow.goToStep(PlanState.EXPERIMENT_PERFORMED);
		viewWorkflow.proceed();
	}
	
	@Test
	public void getCurrentViewUrl_returnsCorrectView() throws PlanningException {
		viewWorkflow.goToStep(PlanState.CREATED); 
		assertEquals("step1", viewWorkflow.showCurrentView());
		// this step is not correct, the plan is at state CREATED, so goToStep SHOULD fail !!!
		// we have to proceed to step BASIS_DEFINED first!
		viewWorkflow.proceed();
		viewWorkflow.goToStep(PlanState.BASIS_DEFINED); 
		assertEquals("step2", viewWorkflow.showCurrentView());		 
	}

	// we dont want to throw an exception here, but rather stay at the current page 
	@Ignore
	@Test(expected=PlanningException.class)
	public void getCurrentViewUrl_throwsExeptionAtInvalidState() throws PlanningException {
		viewWorkflow.goToStep(PlanState.EXPERIMENT_DEFINED); 
		assertEquals("step1", viewWorkflow.showCurrentView());
	}
	
	@Test
	public void goToStep_returnsCorrectView() throws PlanningException{
		viewWorkflow.goToStep(PlanState.RECORDS_CHOSEN);
		String viewURL = viewWorkflow.goToStep(PlanState.CREATED);
		assertEquals("step1", viewURL);
		
		// same here, if the plan is in step CREATED, it should not be possible to go to this state
		viewWorkflow.proceed();
		viewWorkflow.proceed();
		viewWorkflow.proceed();
		viewURL = viewWorkflow.goToStep(PlanState.CREATED);
		
		viewURL = viewWorkflow.goToStep(PlanState.RECORDS_CHOSEN);
		assertEquals("step3", viewURL);

		viewURL = viewWorkflow.goToStep(PlanState.BASIS_DEFINED);
		assertEquals("step2", viewURL);
		
	}
	
	@Test
	public void goToStep_returnsNullOnTooAdvancedStep() throws PlanningException {
		String viewURL = viewWorkflow.goToStep(PlanState.BASIS_DEFINED);
		assertNull(viewURL);
	}
	
	@Test
	public void reachable_valid() throws PlanningException{
		// this test was not correct: the plan is in state CREATED, so it should never be possible to go to step RECORDS_CHOSEN !!!
		// we have to advance to this step first!
		viewWorkflow.proceed();
		viewWorkflow.proceed();
		viewWorkflow.proceed();
		
		viewWorkflow.goToStep(PlanState.RECORDS_CHOSEN);
		
		assert(viewWorkflow.reachable(PlanState.CREATED));
		assert(viewWorkflow.reachable(PlanState.RECORDS_CHOSEN));
		assert(viewWorkflow.reachable(PlanState.BASIS_DEFINED));
	}
	
	@Test 
	public void reachable_invalid() {
		assert(!viewWorkflow.reachable(PlanState.RECORDS_CHOSEN));
		assert(!viewWorkflow.reachable(PlanState.BASIS_DEFINED));
	}
	*/
}
