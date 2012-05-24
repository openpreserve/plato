/**
 * 
 */
package eu.scape_project.planning.plato.wf;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.validation.ValidationError;

/**
 * @author Michael Kraxner
 *
 */
@Stateful
@ConversationScoped
public class ValidatePlan extends AbstractWorkflowStep {
	private static final long serialVersionUID = 7862746302624511130L;
	
	@Inject private Logger log;
	
	public ValidatePlan() {
    	requiredPlanState = PlanState.PLAN_DEFINED;
    	correspondingPlanState = PlanState.PLAN_VALIDATED;
	}
	
	public void init(Plan p){
		super.init(p);
        for (Leaf l : plan.getTree().getRoot().getAllLeaves()) {
            l.initTransformer(); 
        }
	}
	
	/**
	 * Method responsible for approving the current plan.
	 */
	public void approvePlan() {
		List<ValidationError> validationErrors = new ArrayList<ValidationError>();
		// proceed planstate to PLAN_VALIDATED
		boolean success = proceed(validationErrors);
		
		if (success) {
			log.info("Approved plan with id " + plan.getId());
		}
		else {
			log.warn("Approvement of plan with id " + plan.getId() + " failed");
		}
	}

	/**
	 * Method responsible for revising the current approved plan.
	 */
	public void revisePlan() {
		// save does the reset of planstate to PLAN_DEFINED for us
		save();
		log.info("Revised plan with id " + plan.getId());
	}

	@Override
	protected void saveStepSpecific() {
		// no custom save operation is needed here
	}

}
