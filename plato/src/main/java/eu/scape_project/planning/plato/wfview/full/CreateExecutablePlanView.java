package eu.scape_project.planning.plato.wfview.full;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.CreateExecutablePlan;
import eu.scape_project.planning.plato.wfview.AbstractView;

@Named("createExecutablePlan")
@ConversationScoped
public class CreateExecutablePlanView extends AbstractView {
	private static final long serialVersionUID = 1L;
	
	@Inject private CreateExecutablePlan createExecutablePlan;

	public CreateExecutablePlanView() {
    	currentPlanState = PlanState.ANALYSED;
    	name = "Create Executable Plan";
    	viewUrl = "/plan/createexecutableplan.jsf";
    	group="menu.buildPreservationPlan";
	}
	
	@Override
	protected AbstractWorkflowStep getWfStep() {
		return createExecutablePlan;
	}
}
