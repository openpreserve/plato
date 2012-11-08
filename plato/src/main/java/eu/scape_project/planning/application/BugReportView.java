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
package eu.scape_project.planning.application;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import eu.scape_project.planning.LoadedPlan;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.utils.FacesMessages;

import org.slf4j.Logger;

/**
 * View bean for bug report.
 */
@ManagedBean(name = "bugReport")
@ViewScoped
public class BugReportView implements Serializable {
    private static final long serialVersionUID = 2018521013047511583L;

    @Inject
    private Logger log;

    @Inject
    private BugReport bugReport;

    @Inject
    private User user;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    @LoadedPlan
    private Plan plan;

    private Throwable exception;

    private String exceptionPage;

    private String userEmail;

    private String userDescription;

    /**
     * Initialises the class.
     */
    @PostConstruct
    public void init() {
        Object passedException = FacesContext.getCurrentInstance().getExternalContext().getFlash().get("exception");
        Object originatingPage = FacesContext.getCurrentInstance().getExternalContext().getFlash().get("exceptionPage");

        if (passedException != null) {
            // store excecption for further use in this scope
            exception = (Throwable) passedException;

            // store exceptionPage for further use in this scope
            exceptionPage = (originatingPage == null) ? "unknown" : (String) originatingPage;

            addExceptionToMessages(exception);
        }

        // Prefill email
        userEmail = user.getEmail();
    }

    /**
     * Method responsible for adding a throwable to the Plato messages.
     * 
     * @param t
     *            Throwable to add.
     */
    private void addExceptionToMessages(Throwable t) {
        String sessionId = "";
        try {
            sessionId = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
                .getSession().getId();
        } catch (RuntimeException ex) {
            log.debug("Unable to retrieve session-id");
        }

        String currentPage = exceptionPage;

        if (plan != null) {
            bugReport.addExeptionToMessages(t, sessionId, currentPage, plan);
        } else {
            bugReport.addExeptionToMessages(t, sessionId, currentPage);
        }
    }

    /**
     * Method responsible for sending a bug-report per mail based on the last
     * reported error.
     */
    public void sendBugReport() {
        String location = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
            .getLocalName();
        location += ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
            .getContextPath();

        try {
            if (plan != null) {
                bugReport.sendBugReport(userEmail, userDescription, exception, location, "Plato", plan);
            } else {
                bugReport.sendBugReport(userEmail, userDescription, exception, location, "Plato");
            }
            log.debug("Bugreport sent from user " + user.getUsername() + " with email " + userEmail);
            facesMessages
                .addInfo("Bugreport sent. Thank you for your feedback. We will try to analyse and resolve the issue as soon as possible.");
        } catch (MailException e) {
            log.error("Error sending bugreport from user " + user.getUsername() + " with email " + userEmail);
            facesMessages.addError("Sorry, there was an error sending your report.");
        }
    }

    // --------------- getter/setter ---------------
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }
}
