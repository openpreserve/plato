/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.plato.wfview.full;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.plato.bean.IServiceLoader;
import eu.scape_project.planning.plato.bean.ServiceInfoDataModel;
import eu.scape_project.planning.plato.bean.TavernaServices;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DefineAlternatives;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.services.IServiceInfo;
import eu.scape_project.planning.services.PlanningServiceException;
import eu.scape_project.planning.services.action.IActionInfo;
import eu.scape_project.planning.services.pa.PreservationActionRegistryDefinition;
import eu.scape_project.planning.utils.FacesMessages;
import eu.scape_project.planning.validation.ValidationError;

import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;

/**
 * Class used as backing-bean for the view definealternatives.xhtml.
 * 
 * @author Markus Hamm
 */
@Named("defineAlternatives")
@ConversationScoped
public class DefineAlternativesView extends AbstractView {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private DefineAlternatives defineAlternatives;

    @Inject
    private TavernaServices tavernaServices;

    /**
     * List of defined alternatives.
     */
    private List<Alternative> alternatives;

    /**
     * Alternative variable used to add new or edit existing alternatives.
     */
    private Alternative editableAlternative;

    /**
     * Name of the editable alternative which cannot be set directly in the
     * editableAlternative because alternative renaming is a complex process.
     */

    @NotNull
    @Length(min = 1, max = 30)
    private String editableAlternativeName;

    /**
     * A list of all currently defined preservation service registries.
     */
    private List<PreservationActionRegistryDefinition> availableRegistries;

    private List<IActionInfo> availableActions;

    private Map<PreservationActionRegistryDefinition, Boolean> registrySelection;

    private Map<IActionInfo, Boolean> actionSelection;

    /**
     * Datamodel for services.
     */
    private ServiceInfoDataModel serviceInfoData;

    private Map<String, IServiceLoader> serviceLoaders;

    /**
     * Creates a new view object.
     */
    public DefineAlternativesView() {
        currentPlanState = PlanState.TREE_DEFINED;
        name = "Define Alternatives";
        viewUrl = "/plan/definealternatives.jsf";
        group = "menu.evaluateAlternatives";

        editableAlternative = null;

        availableRegistries = new ArrayList<PreservationActionRegistryDefinition>();
        availableActions = new ArrayList<IActionInfo>();
        registrySelection = new HashMap<PreservationActionRegistryDefinition, Boolean>();
        actionSelection = new HashMap<IActionInfo, Boolean>();
        serviceLoaders = new HashMap<String, IServiceLoader>();
    }

    /**
     * Initialises the view with plandata.
     */
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

        serviceLoaders.put("myExperiment", tavernaServices);
    }

    /**
     * Removes the provided alternative from the plan.
     * 
     * @param alternative
     *            alternative to delete
     */
    public void removeAlternative(Alternative alternative) {

        if (plan.isGivenAlternativeTheCurrentRecommendation(alternative)) {
            facesMessages.addInfo("You have removed the action which was chosen as the recommended alternative.");
        }

        plan.removeAlternative(alternative);

        if (alternative == editableAlternative) {
            editableAlternative = null;
        }

        return;
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
                // the call of this method at this stage is allowed because the
                // Alternative is not yet created -
                // so I don't have to use the complex rename function instead.
                editableAlternative.setName(editableAlternativeName);
                plan.addAlternative(editableAlternative);
            } catch (PlanningException e) {
                facesMessages.addError(e.getMessage());
                return;
            }
        }

        // else if it is an existing one - the big part of properties have
        // already been set,
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
     * Method responsible for preparing and showing the add new alternatives
     * window.
     */
    public void showAddNewAlternative() {
        editableAlternative = Alternative.createAlternative();
        editableAlternativeName = "";
    }

    /**
     * Method responsible for preparing and showing the edit alternatives
     * window.
     * 
     * @param alternative
     *            the alternative to show
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

    @Override
    protected boolean tryProceed(List<ValidationError> errors) {
        // view-specific validation
        if (editableAlternative != null) {
            errors
                .add(new ValidationError(
                    "You are currently editing an Alternative. Please finish editing first before you proceed to the next step."));
        }

        // general validation
        boolean result = defineAlternatives.proceed(errors);
        return result && errors.isEmpty();
    }

    /**
     * Determines if there is at least one sample with format info.
     * 
     * @return true if a sample with format info is available
     */
    public boolean isFormatInfoAvailable() {
        return plan.getSampleRecordsDefinition().getFirstSampleWithFormat() != null;
    }

    /**
     * Returns a sample with attached format info - at the moment this is the
     * first sample with format info found.
     * 
     * @return a sample object with format info
     */
    public SampleObject getSampleWithFormat() {
        return plan.getSampleRecordsDefinition().getFirstSampleWithFormat();
    }

    /**
     * Retrieves the list of services available in the given registry, for the
     * current sample with format info.
     * 
     * @param registry
     *            the registry to query
     */
    public void showPreservationServices(PreservationActionRegistryDefinition registry) {
        log.debug("Loading preservation action services from registry [{}]", registry.getShortname());
        availableActions.clear();
        actionSelection.clear();
        tavernaServices.clear();
        try {
            registrySelection.clear();
            registrySelection.put(registry, true);
            availableActions.addAll(defineAlternatives.queryRegistry(getSampleWithFormat().getFormatInfo(), registry));
            serviceInfoData = new ServiceInfoDataModel(availableActions, serviceLoaders);
            for (IActionInfo actionInfo : availableActions) {
                actionSelection.put(actionInfo, false);
            }
        } catch (PlatoException e) {
            facesMessages.addError("Failed to query the registry: " + registry.getShortname() + " - " + e.getMessage());
            log.error("Failed to query the registry: " + registry.getShortname(), e);
        }
    }

    /**
     * Returns the number of available actions.
     * 
     * @return the number of actions
     */
    public int getNumOfAvailableActions() {
        if (availableActions == null) {
            return 0;
        }
        return availableActions.size();
    }

    /**
     * Creates alternatives from selected preservation action infos.
     */
    public void createAlternativesForPreservationActions() {
        List<IServiceInfo> selectedActions = new ArrayList<IServiceInfo>();
        for (IServiceInfo selectedAction : availableActions) {
            if (actionSelection.get(selectedAction)) {
                selectedActions.add(selectedAction);
            }
        }
        defineAlternatives.createAlternativesForPreservationActions(selectedActions);
    }

    public void addPreservationAction(IActionInfo actionInfo) {
        defineAlternatives.createAlternative(actionInfo);
    }

    // --------------- getter/setter ---------------

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
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

    public List<IActionInfo> getAvailableActions() {
        return availableActions;
    }

    public Map<PreservationActionRegistryDefinition, Boolean> getRegistrySelection() {
        return registrySelection;
    }

    public Map<IActionInfo, Boolean> getActionSelection() {
        return actionSelection;
    }

    public ServiceInfoDataModel getServiceInfoData() {
        return serviceInfoData;
    }

    public TavernaServices getTavernaServices() {
        return tavernaServices;
    }

}
