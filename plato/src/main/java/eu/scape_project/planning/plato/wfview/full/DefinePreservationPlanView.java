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
package eu.scape_project.planning.plato.wfview.full;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DefinePreservationPlan;
import eu.scape_project.planning.plato.wfview.AbstractView;

@Named("definePreservationPlan")
@ConversationScoped
public class DefinePreservationPlanView extends AbstractView {
	private static final long serialVersionUID = -7567036818122833461L;
	
	@Inject private DefinePreservationPlan definePreservationPlan;

	public DefinePreservationPlanView() {
    	currentPlanState = PlanState.EXECUTEABLE_PLAN_CREATED;
    	name = "Define Preservation Plan";
    	viewUrl = "/plan/definepreservationplan.jsf";
    	group="menu.buildPreservationPlan";
	}

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return definePreservationPlan;
	}
}
