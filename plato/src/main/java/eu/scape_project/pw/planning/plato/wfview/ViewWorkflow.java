package eu.scape_project.pw.planning.plato.wfview;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.exception.PlanningException;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;

/**
 * Class representing a viewWorkflow which consist of several steps.
 * This class is responsible for correct navigation between these steps.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@Named("viewWorkflow")
@ConversationScoped
public class ViewWorkflow implements Serializable {
	private static final long serialVersionUID = -8304259709178234866L;
	
	@Inject private Logger log;
	
	private List<AbstractView> steps;
	
	private AbstractView currentView;
	
	private Plan plan;
	
	/**
	 * Method responsible for providing the object with all required information(dependencies) to operate.
	 * (Because this is a managed bean and created by the container this cannot be passed via the constructor)
	 * 
	 * @param steps All steps of the viewWorkflow
	 */
	public void init(Plan plan, List<AbstractView> steps) throws PlanningException{
		if (steps == null || steps.isEmpty()) {
			throw new PlanningException("No views defined!");
		}
		
		this.plan = plan;
		this.steps = steps;

		currentView = getViewForPlanState(plan.getPlanProperties().getState());		
	}
	
	/**
	 * Proceed to the next step and return the view-url of the next page to show.
	 * 
	 * @return View-url of the next page to show.
	 * @throws PlanningException If the viewWorkflow is in an unknown stage (should not happen during normal operation)
	 */
	public String proceed() throws PlanningException{
		boolean mayProceed = "success".equals(currentView.proceed()); 
		// because of storing the plan, the state might have changed
		plan = currentView.getPlan();
		if (mayProceed) {
			currentView = this.getViewForPlanState(plan.getPlanProperties().getState());
			return showCurrentView();
		} else {
			return null;
		}
	}
	
	/**
	 * Saves the currently open changes.
	 * (the actual save is delegated to the view)
	 * 
	 * @throws PlanningException if no currentView is set
	 */
	public void save() throws PlanningException{
		if (currentView != null) {
			currentView.save();
			// the plan instance might have changed 
			plan = currentView.getPlan();
		} else {
			throw new PlanningException("Invalid viewWorkflow-state, no current view available.");
		}
 	}

	/**
	 * Discards unsaved changes.
	 * (the actual discard is delegated to the view)
	 * 
	 * @throws PlanningException if no currentView is set
	 */
	public String discard() throws PlanningException {
		if (currentView != null) {
			currentView.discard();
			// the plan instance might have changed 
			plan = currentView.getPlan();
			// return the current-view-url to recreate the view 
			// (populate the view with updated model values - after immediate event changing the underlying model)
			return currentView.getViewUrl();
		} else {
			throw new PlanningException("Invalid viewWorkflow-state, no current view available.");
		}
	}
		
	/**
	 * Will display the current view:
	 * - initialization of the view
	 * - Returns the views-url dependent of the current plan-state.
	 * 
	 * @return view-url of the current page to show.
	 * @throws PlanningException If the viewWorkflow is in an unknown stage (should not happen during normal operation)
	 */
	public String showCurrentView() throws PlanningException {
		if (currentView != null) {
			currentView.init(plan);
			return currentView.getViewUrl();
		} else {
			throw new PlanningException("Invalid viewWorkflow-state, no current view available.");
		}
	}
	

	/**
	 * Returns the view URL for the given plan state (if reachable).
	 * 
	 * @param state ViewWorkflow-state to get the view-url for
	 * @return View URL corresponding to the given state, or null if the plan has not progressed this far (and thus the wanted stage is not yet reachable).
	 */
	public String goToStep(PlanState state) throws PlanningException{
		AbstractView result = getViewForPlanState(state);

		if (result != null) {
			currentView = result;
			return showCurrentView();
		}
		
		return null;
	}
		
	/**
	 * Method responsible for returning if the viewWorkflow state is reachable (the viewWorkflow has progressed so far).
	 * 
	 * @param planstate ViewWorkflow-state to chech for reachability.
	 * @return True if the viewWorkflow has progressed so far that this viewWorkflow-step is reachable, null otherwise.
	 */
	public boolean reachable(PlanState planstate)  {
		int wfStartStateValue = steps.get(0).getCurrentPlanState().getValue();
		int currentPlanStateValue = plan.getPlanProperties().getState().getValue();

		// reachable steps are all steps between start- and current-state.
		if (planstate.getValue() < wfStartStateValue || planstate.getValue() > currentPlanStateValue) {
			return false;
		}
				
		return true;
	}
	
	/**
	 * Returns the appropriate view for the given plan state.
	 * The appropriate view is the view with the highest plan-state which is <= current plan-state.
	 * 
	 * @param state Plan state to to find the appropriate view for.
	 * @return View to show for the given plan state.
	 * 		   Null if no appropriate view corresponding to the given state can be identifies.
	 * 		   Null if the plan has not progressed this far (and thus the wanted stage is not yet reachable/allowed).
	 */
	private AbstractView getViewForPlanState(PlanState state) {
		if (!reachable(state)) {
			return null;
		}
		
		AbstractView result = null;
		
		Iterator<AbstractView> viewIterator = steps.iterator();
		AbstractView view = null;
		do {
			view = viewIterator.next();

			// check if the iterated view is "better" than the current result
			if (result == null || result.getCurrentPlanState().getValue() < view.getCurrentPlanState().getValue()) {
				result = view;
			}
		} while (viewIterator.hasNext() && view.getCurrentPlanState().getValue() < state.getValue());
		
		return result;
	}	
	
	// ------------------------- getter/setter -------------------------
	
	public List<AbstractView> getSteps() {
		return steps;
	}

	public void setSteps(List<AbstractView> steps) {
		this.steps = steps;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public AbstractView getCurrentView() {
		return currentView;
	}

	public void setCurrentView(AbstractView currentView) {
		this.currentView = currentView;
	}
}
