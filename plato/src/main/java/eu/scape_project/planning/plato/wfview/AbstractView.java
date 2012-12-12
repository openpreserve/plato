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
package eu.scape_project.planning.plato.wfview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.utils.FacesMessages;
import eu.scape_project.planning.validation.ValidationError;

/**
 * This class represents the core/base-functionality of each viewWorkflow step.
 * This is modifying the plan and saving it.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
public abstract class AbstractView implements Serializable {
    private static final long serialVersionUID = 4032545177598725457L;

    @Inject
    private Logger log;

    @Inject
    protected FacesMessages facesMessages;

    /**
     * The plan to modify/work on
     */
    protected Plan plan;

    /**
     * Planstate this viewWorkflow step is responsible for
     */
    protected PlanState currentPlanState;

    protected String name;

    protected String viewUrl;

    protected String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * This flag is used in views to prevent navigation per menu when there are
     * unsaved changes. This property preserves the changed-state during
     * requests like adding a node and can be used to reset the changed-state
     * i.e. after save and discard
     */
    protected String changed;

    public AbstractView() {
        this.currentPlanState = PlanState.CREATED;
        this.name = "abstract step";
        this.viewUrl = "nonExistent";
    }

    /**
     * Initializes the view with the given plan. This includes initializing the
     * corresponding BL
     * 
     * @param plan
     */
    public void init(Plan plan) {
        this.plan = plan;

        getWfStep().init(plan);
    }

    /**
     * Stores the plan changes in database if validation was successful.
     */
    public void save() {
        try {
            getWfStep().save();
            changed = "";
            facesMessages.addInfo("Your changes have been saved.");
        } catch (Exception e) {
            facesMessages.addError("Failed to save Your changes");
            log.error("failed to save changes:", e);
        }
    }

    /**
     * Discards all changes which have not been persisted so far. Derived views
     * may use this also to undo changes which do not concern the plan, like
     * deleting created files...
     */
    public void discard() {
        try {
            getWfStep().discard();
            plan = getWfStep().getPlan();
            // reset the view to its initial
            init(plan);
            changed = "";
        } catch (PlanningException e) {
            log.error("Failed to discard changes.", e);
            facesMessages.addError("Failed to dicard changes.");
        }
    }

    /**
     * Finishes the current step - this includes validating if the plan has
     * progressed enough - if there are problems, they are displayed as
     * FacesMessages
     * 
     * @return "success", if the plan state could be advanced, <code>null</code>
     *         otherwise
     */
    public String proceed() {
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();

        if (tryProceed(errors)) {
            changed = "";
            return "success";
        } else {
            for (ValidationError e : errors) {
                facesMessages.addError(e.getMessage());
            }
            return null;
        }
    }

    /**
     * Does the actual proceed, for internal use only. Any occurring errors
     * should be added to the provided list.
     * 
     * @param errors
     * @return true, if plan state could be advanced
     */
    protected boolean tryProceed(List<ValidationError> errors) {
        return getWfStep().proceed(errors);
    }

    protected abstract AbstractWorkflowStep getWfStep();

    // --------------- getter/setter ---------------

    public PlanState getCurrentPlanState() {
        return currentPlanState;
    }

    public void setCurrentPlanState(PlanState currentPlanState) {
        this.currentPlanState = currentPlanState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public Plan getPlan() {
        return plan;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    /**
     * Touches the given object. Used for a4j:ajax listener, which can not cope
     * with complex method el expressions
     * 
     * @param object
     */
    public void touch(Object object) {
        if (object instanceof ITouchable) {
            ((ITouchable) object).touch();
        }
    }
}
