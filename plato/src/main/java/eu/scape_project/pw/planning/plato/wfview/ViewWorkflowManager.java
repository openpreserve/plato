package eu.scape_project.pw.planning.plato.wfview;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.pw.planning.manager.PlanManager;
import eu.scape_project.pw.planning.utils.LoggerFactory;

/**
 * Class responsible for executing all administrative worfklow tasks
 * like starting and ending a viewWorkflow in a proper way.
 *
 * @author Markus Hamm, Michael Kraxner
 */
@ConversationScoped
@Named("viewWorkflowManager")
public class ViewWorkflowManager implements Serializable{
	private static final long serialVersionUID = 1L;

	@Inject private	Logger log;

	@Inject
	private Conversation conversation;

	@Inject
	private PlanManager planManager;
	
	private Plan plan;

	@Inject private ViewWorkflow viewWorkflow;
	
	@Inject private ViewWorkflowFactory viewWorkflowFactory;
	
	@Inject private ViewWorkflowMenu workflowMenu;
	
	
	/**
	 * Method responsible for starting a viewWorkflow for a given plan.
	 * 
	 * @param planPropertiesId PlanPropertiesId of the plan to start the viewWorkflow for.
	 * @return Outcome-string of the current viewWorkflow page to show. 
	 */
	public String startWorkflow(Integer planPropertiesId) {
		// load plan
		plan = null;
		try {
			plan = planManager.load(planPropertiesId);
		} catch (PlanningException e) {
			log.warn("could not load plan with planPropertiesId " + planPropertiesId, e);
			// stay on the same page
			return null;
		}
		return startWorkflow(plan);
	}

	/**
	 * Method responsible for starting a viewWorkflow for a given plan.
	 * 
	 * @param plan Plan to start the viewWorkflow for.
	 * @return Outcome-string of the current viewWorkflow page to show. 
	 */
	public String startWorkflow(Plan plan) {
		// set plan
		this.plan = plan;
		
		startConversation();
		log.info("Started viewWorkflow conversation with id " + conversation.getId());

		// construct and initialize viewWorkflow-steps
		List<AbstractView> workflowSteps = viewWorkflowFactory.constructWorkflowSteps(plan);
		try {
			viewWorkflow.init(plan, workflowSteps);
			workflowMenu.init(workflowSteps);
		} catch (PlanningException e1) {
			log.error("Failed to initialize workflow.", e1);
		}

		// redirect to workflows current state view-url
		String outcome = null;
		try {
			outcome = viewWorkflow.showCurrentView();

			if (outcome != null) {
				return outcome  + "?faces-redirect=true";
			}
			
		} catch (PlanningException e) {
			log.warn("could not determine current view for plan with id " + plan.getId() + " : " + e.getMessage(), e);
		}
		
		return null;
	}	
	
	/**
	 * Method responsible for ending the current started/running viewWorkflow.
	 * 
	 * @return URL to redirect after closing.
	 */
	public String endWorkflow() {
		planManager.unlockPlan(viewWorkflow.getPlan().getPlanProperties().getId());
		plan = null;
		conversation.end();
		log.info("Ended viewWorkflow conversation");
		return "/index.jsf" + "?faces-redirect=true";
	}
	
	/**
	 * Method responsible for indicating if a viewWorkflow currently started/running.
	 * 
	 * @return True, if a viewWorkflow is currently started/running. False otherwise.
	 */
	public boolean isActive() {
		return !conversation.isTransient() && (plan != null);
	}
	
	// --------------- getter/setter ---------------
	
	/**
	 * FIXME: the viewWorkflow manager provides the plan, really? - would'nt that fit better to the ViewWorkflow ? 
	 * @return
	 */
	public Plan getPlan() {
		return viewWorkflow.getPlan();
	}
	
	private void startConversation(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		int sessionTimeoutInSeconds = session.getMaxInactiveInterval();
		
		conversation.begin();
		
		// Conversations should stay alive as long as the session.
		// (conversation timeout is set in milliseconds)
		conversation.setTimeout(sessionTimeoutInSeconds * 1000);

		log.debug("Started conversation with id " + conversation.getId());
		log.info("Converation timeout: " + conversation.getTimeout() + " ms");
		log.debug("Session timeout: " + sessionTimeoutInSeconds * 1000 + " ms");
	}
}
