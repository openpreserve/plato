/**
 * 
 */
package eu.scape_project.pw.planning.plato.wf;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;

import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;


/**
 * Business logic for workflow step Set Importance Factors.
 *
 * @author Michael Kraxner, Markus Hamm
 */
@Stateful
@ConversationScoped
public class SetImportanceFactors extends AbstractWorkflowStep {
	private static final long serialVersionUID = -5261160430145014006L;

	public SetImportanceFactors() {
    	requiredPlanState = PlanState.TRANSFORMATION_DEFINED;
    	correspondingPlanState = PlanState.WEIGHTS_SET;
	}
	
	public void init(Plan p){
		super.init(p);
		
        // The ObjectiveTree makes sure that weights that have already been initialized
        // aren't overwritten by this call.
        plan.getTree().initWeights();
	}
	
	/* (non-Javadoc)
	 * @see eu.scape_project.pw.planning.plato.wf.AbstractWorkflowStep#saveStepSpecific()
	 */
	@Override
	protected void saveStepSpecific() {
		saveEntity(plan.getTree());
		saveEntity(plan.getImportanceWeighting());
	}

}
