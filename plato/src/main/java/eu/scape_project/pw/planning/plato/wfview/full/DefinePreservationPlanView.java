package eu.scape_project.pw.planning.plato.wfview.full;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.model.PlanState;
import eu.scape_project.pw.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.pw.planning.plato.wf.DefinePreservationPlan;
import eu.scape_project.pw.planning.plato.wfview.AbstractView;

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
