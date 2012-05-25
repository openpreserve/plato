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

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DevelopExperiments;
import eu.scape_project.planning.plato.wfview.AbstractView;

/**
 * Class used as backing-bean for the view developexperiments.xhtml
 * 
 * @author Markus Hamm
 */
@Named("developExperiments")
@ConversationScoped
public class DevelopExperimentsView extends AbstractView {
	private static final long serialVersionUID = 1L;

	@Inject private Logger log;
	
	@Inject private DevelopExperiments developExperiments;
	
	private List<Alternative> alternatives;
	
	public DevelopExperimentsView() {
    	currentPlanState = PlanState.GO_CHOSEN;
    	name = "Develop Experiments";
    	viewUrl = "/plan/developexperiments.jsf";
    	group = "menu.evaluateAlternatives";    	
	}
	
	public void init(Plan plan) {
    	super.init(plan);
    	alternatives = plan.getAlternativesDefinition().getAlternatives();
	}
	
	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<Alternative> alternatives) {
		this.alternatives = alternatives;
	}

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return developExperiments;
	}
}
