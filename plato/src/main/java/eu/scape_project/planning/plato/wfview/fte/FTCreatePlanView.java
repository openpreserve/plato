package eu.scape_project.planning.plato.wfview.fte;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.plato.wfview.ViewWorkflowManager;

/**
 * Backing bean to create new plans following the full workflow.
 * Note, that it is not part of the workflow itself.
 * 
 * @author Michael Kraxner
 *
 */
@Named("createFTPlan")
@ConversationScoped
public class FTCreatePlanView implements Serializable{
	private static final long serialVersionUID = -1177232137288031358L;

	/**
	 * Used to store a created plan, before passing it on to the {@link #viewWorkflowManager}  
	 */
	@Inject private PlanManager planManager;

	/**
	 * Used to edit a created plan 
	 */
	@Inject private ViewWorkflowManager viewWorkflowManager;
	
	@Inject
	private Conversation conversation;
	
	@Inject private User user;
	
	private Plan plan;
	
    /**
     * This flag is used in views to prevent navigation per menu when there are unsaved changes.
     * This property preserves the changed-state during requests like adding a node
     * and can be used to reset the changed-state i.e. after save and discard
     */
	protected String changed;

	
	public FTCreatePlanView(){
	}
	
	/**
	 * Creates a new plan and prepares it for the full workflow.
	 * 
	 */
	public String createPlan(){
		conversation.begin();
		
		plan = new Plan();
		
		plan.getPlanProperties().setAuthor(user.getFullName());
		plan.getPlanProperties().setPrivateProject(true);
		plan.getPlanProperties().setOwner(user.getUsername());
		
        // set Fast Track properties
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-kkmmss");
        String timestamp = format.format(new Date(System.currentTimeMillis()));
        String identificationCode = Plan.fastTrackEvaluationPrefix + timestamp;
        plan.getProjectBasis().setIdentificationCode(identificationCode);
		

        // We have to prevent the user from navigating to the step 'Load plan'
        // because the user wouldn't be able to leave this step: Going to 'Define
        // Basis' is not possible as the project hasn't been saved so far.
        //
        // We 'activate' the changed flag so that the user is asked to either
        // save the project or discard changes.
        changed = "T";
        return "/fte/createFTE.jsf"+ "?faces-redirect=true";
	}
	
	/**
	 * Stores the plan and starts the workflow 
	 * 
	 * @return
	 */
	public String savePlan() {
		planManager.save(plan, PlanState.FTE_INITIALISED, plan);
		conversation.end();
		return viewWorkflowManager.startWorkflow(plan.getPlanProperties().getId());
	}
	
	/**
	 * Discards the plan by simply reseting the changed flag and redirecting to the index page.
	 * 
	 * @return
	 */
	public String discardPlan(){
		conversation.end();
		changed = null;
		return "/index.jsf" + "?faces-redirect=true";
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public String getChanged() {
		return changed;
	}

	public void setChanged(String changed) {
		this.changed = changed;
	}

}
