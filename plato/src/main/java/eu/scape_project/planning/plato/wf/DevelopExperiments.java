package eu.scape_project.planning.plato.wf;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;

import eu.scape_project.planning.model.PlanState;

/**
 * 
 * @author Michael Kraxner, Markus Hamm
 *
 */
@Stateful
@ConversationScoped
public class DevelopExperiments extends AbstractWorkflowStep {

	private static final long serialVersionUID = 3224826109130261298L;
	
	public DevelopExperiments() {
    	requiredPlanState = PlanState.GO_CHOSEN;
    	correspondingPlanState = PlanState.EXPERIMENT_DEFINED;
	}
	
	@Override
	protected void saveStepSpecific() {
		// TODO Auto-generated method stub
	}

}
