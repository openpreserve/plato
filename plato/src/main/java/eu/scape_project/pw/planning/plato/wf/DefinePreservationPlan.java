/**
 * 
 */
package eu.scape_project.pw.planning.plato.wf;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;

import eu.scape_project.planning.model.PlanState;

/**
 * Class containing business logic for workflow-step Define Preservation Plan.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@Stateful
@ConversationScoped
public class DefinePreservationPlan extends AbstractWorkflowStep {
	private static final long serialVersionUID = -1393530549080374281L;


	public DefinePreservationPlan() {
    	requiredPlanState = PlanState.EXECUTEABLE_PLAN_CREATED;
    	correspondingPlanState = PlanState.PLAN_DEFINED;
	}
	
	@Override
	protected void saveStepSpecific() {
		saveEntity(plan.getPlanDefinition());
	}
}
