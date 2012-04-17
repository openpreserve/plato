/**
 * 
 */
package eu.scape_project.pw.planning.plato.wf;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;

import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;

/**
 * @author Michael Kraxner
 *
 */
@Stateful
@ConversationScoped
public class CreateExecutablePlan extends AbstractWorkflowStep {
	private static final long serialVersionUID = -971490825722362606L;

	public CreateExecutablePlan() {
		requiredPlanState = PlanState.ANALYSED;
    	correspondingPlanState = PlanState.EXECUTEABLE_PLAN_CREATED;
	}
	
    public void init(Plan p){
    	super.init(p);
    	
        // If we don't have tool parameters, we copy them from the chosen alternative's config settings:
        if (plan.getExecutablePlanDefinition().getToolParameters() == null || 
        	"".equals(plan.getExecutablePlanDefinition().getToolParameters())) {
            plan.getExecutablePlanDefinition().setToolParameters(plan.getRecommendation().getAlternative().getExperiment().getSettings());
        }
    }
	
	@Override
	protected void saveStepSpecific() {
		saveEntity(plan.getExecutablePlanDefinition());
	}
}
