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
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.utils.FacesMessages;

import org.slf4j.Logger;

/**
 * Class responsible for executing all administrative worfklow tasks like
 * starting and ending a viewWorkflow in a proper way.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@ConversationScoped
@Named("viewWorkflowManager")
public class ViewWorkflowManager implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private Conversation conversation;

    @Inject
    private PlanManager planManager;

    private Plan plan;

    @Inject
    private ViewWorkflow viewWorkflow;

    @Inject
    private ViewWorkflowFactory viewWorkflowFactory;

    @Inject
    private ViewWorkflowMenu workflowMenu;

    @Inject
    private FacesMessages facesMessages;

    /**
     * Method responsible for starting a viewWorkflow for a given plan.
     * 
     * @param planPropertiesId
     *            PlanPropertiesId of the plan to start the viewWorkflow for.
     * @return Outcome-string of the current viewWorkflow page to show.
     */
    public String startWorkflow(Integer planPropertiesId) {
        plan = null;
        try {
            plan = planManager.load(planPropertiesId);
        } catch (PlanningException e) {
            log.warn("Could not load plan with planPropertiesId " + planPropertiesId, e);
            facesMessages.addError("Could not load plan: " + e.getMessage());
            return null;
        }
        return startWorkflow(plan);
    }

    /**
     * Method responsible for starting a viewWorkflow for a given plan.
     * 
     * @param plan
     *            Plan to start the viewWorkflow for.
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
        } catch (PlanningException e) {
            log.error("Failed to initialize workflow.", e);
            facesMessages.addError("Could not open the plan: " + e.getMessage());
        }

        // redirect to workflows current state view-URL
        String outcome = null;
        try {
            outcome = viewWorkflow.showCurrentView();

            if (outcome != null) {
                return outcome + "?faces-redirect=true";
            }

        } catch (PlanningException e) {
            log.warn("Could not determine current view for plan with id " + plan.getId() + " : " + e.getMessage(), e);
            facesMessages.addError("Could not determine the current workflow step: " + e.getMessage());
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
     * Logs the user out. - closes the plan and ends the conversation - does a
     * global logout
     * 
     * @return the navigation target
     */
    public String logout() {
        planManager.unlockPlan(viewWorkflow.getPlan().getPlanProperties().getId());
        plan = null;
        conversation.end();
        log.info("Ended viewWorkflow conversation - logging out");
        return "/index.jsf" + "?faces-redirect=true&GLO=true";
    }

    /**
     * Method responsible for indicating if a viewWorkflow currently
     * started/running.
     * 
     * @return True, if a viewWorkflow is currently started/running. False
     *         otherwise.
     */
    public boolean isActive() {
        return !conversation.isTransient() && (plan != null);
    }

    // --------------- getter/setter ---------------

    /**
     * FIXME: the viewWorkflow manager provides the plan, really? - would'nt
     * that fit better to the ViewWorkflow ?
     * 
     * @return
     */
    public Plan getPlan() {
        return viewWorkflow.getPlan();
    }

    /**
     * Starts a new conversation.
     */
    private void startConversation() {
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
