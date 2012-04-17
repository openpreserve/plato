package eu.scape_project.pw.planning.plato.wfview.full;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;

import eu.planets_project.pp.plato.exception.PlanningException;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.PlatoException;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.services.PlanningServiceException;
import eu.planets_project.pp.plato.validation.ValidationError;
import eu.scape_project.pw.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.pw.planning.plato.wf.DefineAlternatives;
import eu.scape_project.pw.planning.plato.wfview.AbstractView;
import eu.scape_project.pw.planning.services.preservationaction.PreservationActionRegistryDefinition;
import eu.scape_project.pw.planning.utils.FacesMessages;

/**
 * Class used as backing-bean for the view definealternatives.xhtml
 * 
 * @author Markus Hamm
 */
@Named("defineAlternatives")
@ConversationScoped
public class DefineAlternativesView extends AbstractView {
	private static final long serialVersionUID = 1L;

	@Inject private Logger log;
	
	@Inject private FacesMessages facesMessages;
	
	@Inject private DefineAlternatives defineAlternatives;
	
	@Inject private User user;
		
	/**
	 * List of defined alternatives
	 */
	private List<Alternative> alternatives;
	
	/**
	 * Alternative variable used to add new or edit existing alternatives. 
	 */
	private Alternative editableAlternative;
	
	/**
	 * Name of the editable alternative which cannot be set directly in the editableAlternative because alternative renaming is a complex process.
	 */
	
	@NotNull
	@Length(min=1, max=30)
	private String editableAlternativeName;
	
	/**
	 * Id of the alternative which is allowed to be remove (at the second attempt/confirmation by the user).
	 */
	private int alternativeIdAllowedToRemove;
	
    /**
     * A list of all currently defined preservation service registries
     */
    private List<PreservationActionRegistryDefinition> availableRegistries;
    
    private List<PreservationActionDefinition> availableActions;
    
    private Map<PreservationActionRegistryDefinition, Boolean> registrySelection;
	
		
	public DefineAlternativesView() {
    	currentPlanState = PlanState.TREE_DEFINED;
    	name = "Define Alternatives";
    	viewUrl = "/plan/definealternatives.jsf";
    	group = "menu.evaluateAlternatives";

    	//alternatives = new ArrayList<Alternative>();
    	alternativeIdAllowedToRemove = -1;
    	editableAlternative = null;
    	
    	registrySelection = new HashMap<PreservationActionRegistryDefinition, Boolean>();
    	availableRegistries = new ArrayList<PreservationActionRegistryDefinition>();
    	availableActions = new ArrayList<PreservationActionDefinition>();
	}
	
	public void init(Plan plan) {
    	super.init(plan);
    	alternatives = plan.getAlternativesDefinition().getAlternatives();

    	availableRegistries.clear();
    	availableActions.clear();
    	registrySelection.clear();
    	try {
			availableRegistries.addAll(defineAlternatives.getPreservationActionRegistries());
	        for (PreservationActionRegistryDefinition registry : availableRegistries) {
	            registrySelection.put(registry, false);
	        }
		} catch (PlanningServiceException e) {
			log.error("Failed to retrieve registries", e);
			facesMessages.addError("Could not find any preservation action registries.");
		}
	}
	
	/**
	 * Method responsible for removing a given alternative.
	 * At the first attempt just a allowed-flag is set.
	 * After the second attempt (confirmed by the user) the alternative is deleted.
	 * 
	 * @param alternative alternative to delete.
	 */
	public void tryRemoveAlternative(Alternative alternative) {
		// at the first attempt just set the allowed flag 
		if (alternative.getId() != alternativeIdAllowedToRemove) {
			alternativeIdAllowedToRemove = alternative.getId();
			log.debug("Allowed to remove alternative with id " + alternative.getId());
			return;
		}
		
		// at the second attempt (user confirmed) delete alternative
		if (alternative.getId() == alternativeIdAllowedToRemove) {
			// if the alternative to delete is the current recommended alternative - show an info message
			if (plan.isGivenAlternativeTheCurrentRecommendation(alternative)) {
				facesMessages.addInfo("You have removed the action which was chosen as the recommended alternative.");
			}
			
			plan.removeAlternative(alternative);
			alternativeIdAllowedToRemove = -1;
			
			return;
		}
	}
	
	/**
	 * Method responsible for add/edit a new/existing alternative.
	 */
	@SuppressWarnings("deprecation")
	public void editAlternative() {
		// If the entered alternative is a new alternative - add it
		if ((editableAlternative.getId() == 0) && (!alternatives.contains(editableAlternative))) {						
			// add new alternative - this has to be done via
			try {
				// the call of this method at this stage is allowed because the Alternative is not yet created -
				// so I don't have to use the complex rename function instead.
				editableAlternative.setName(editableAlternativeName);
				plan.addAlternative(editableAlternative);
			}
			catch (PlanningException e) {
				facesMessages.addError(e.getMessage());
				return;
			}
		}
		// else if it is an existing one - the big part of properties have already been set, 
		// but the complex renaming procedure is done here.
		else {
			editableAlternative.touch();
			
			try {
				plan.renameAlternative(editableAlternative, editableAlternativeName);
			} catch (PlanningException e) {
				facesMessages.addError(e.getMessage());
				return;
			}
		}
		
		// reset
		editableAlternative = null;
	}
	
	/**
	 * Method responsible for preparing and showing the add new alternatives window.
	 */
	public void showAddNewAlternative() {
		editableAlternative = Alternative.createAlternative();
		editableAlternativeName = "";
	}

	/**
	 * Method responsible for preparing and showing the edit alternatives window.
	 */
	public void showEditAlternative(Alternative alternative) {
		editableAlternative = alternative;
		editableAlternativeName = alternative.getName();
	}
	
	/**
	 * Method responsible for causing the add/edit-alternative window to close.
	 */
	public void closeEditArea() {
		editableAlternative = null;
	}
	
	protected boolean tryProceed(List<ValidationError> errors) {
		// view-specific validation
		if (editableAlternative != null) {
			errors.add(new ValidationError("You are currently editing an Alternative. Please finish editing first before you proceed to the next step."));
		}
		
		// general validation
		boolean result = defineAlternatives.proceed(errors);  
		return result && errors.isEmpty();
	}
	/**
	 * Determines if there is at least one sample with format info
	 *  
	 * @return
	 */
	public boolean isFormatInfoAvailable(){
		return plan.getSampleRecordsDefinition().getFirstSampleWithFormat() != null;
	}
	
	/**
	 * Returns a sample with attached format info
	 * - at the moment this is the first sample with format info found. 
	 * 
	 * @return
	 */
	public SampleObject getSampleWithFormat() {
		return plan.getSampleRecordsDefinition().getFirstSampleWithFormat();
	}

	/**
	 * Retrieves the list of services available in the given registry, for the current sample with format info
	 * 
	 * @param registry
	 */
	public void showPreservationServices(PreservationActionRegistryDefinition registry) {
		availableActions.clear();
		try {
			registrySelection.clear();
			registrySelection.put(registry, true);
			availableActions.addAll(defineAlternatives.queryRegistry(getSampleWithFormat().getFormatInfo(), registry));
		} catch (PlatoException e) {
			facesMessages.addError("Failed to query the registry: " + registry.getShortname());
			log.error("Failed to query the registry: " + registry.getShortname(), e);
		}
	}
	
	public int getNumOfAvailableActions() {
		if (availableActions == null) {
			return 0;
		}
		return availableActions.size();
	}
	public void createAlternativesForPreservationActions(){
		List<PreservationActionDefinition> selectedActions = new ArrayList<PreservationActionDefinition>();
		for (PreservationActionDefinition selectedAction : availableActions) {
			if (selectedAction.isSelected()) {
				selectedActions.add(selectedAction);
			}
		}
		defineAlternatives.createAlternativesForPreservationActions(selectedActions);
	}
	
	// --------------- getter/setter ---------------
	
	
	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<Alternative> alternatives) {
		this.alternatives = alternatives;
	}

	public int getAlternativeIdAllowedToRemove() {
		return alternativeIdAllowedToRemove;
	}

	public void setAlternativeIdAllowedToRemove(int alternativeIdAllowedToRemove) {
		this.alternativeIdAllowedToRemove = alternativeIdAllowedToRemove;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public Alternative getEditableAlternative() {
		return editableAlternative;
	}

	public void setEditableAlternative(Alternative editableAlternative) {
		this.editableAlternative = editableAlternative;
	}

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return defineAlternatives;
	}

	public String getEditableAlternativeName() {
		return editableAlternativeName;
	}

	public void setEditableAlternativeName(String editableAlternativeName) {
		this.editableAlternativeName = editableAlternativeName;
	}

	public List<PreservationActionRegistryDefinition> getAvailableRegistries() {
		return availableRegistries;
	}

	public List<PreservationActionDefinition> getAvailableActions() {
		return availableActions;
	}

	public Map<PreservationActionRegistryDefinition, Boolean> getRegistrySelection() {
		return registrySelection;
	}
}
