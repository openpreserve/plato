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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import eu.scape_project.planning.LoadedPlan;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.User;

/**
 * View bean for bug report.
 */
@ManagedBean(name = "bugReport")
@ViewScoped
public class BugReportView implements Serializable {
    private static final long serialVersionUID = -4163926008026409933L;

    @Inject
    private Logger log;

    @Inject
    private BugReport bugReport;

    @Inject
    private User user;

    @Inject
    @LoadedPlan
    private Plan plan;
    
    private Throwable throwable;

    private String errorRequestUri;
    private String userEmail;

    private String userDescription;

    /**
     * Initialises the class.
     */
    @PostConstruct
    public void init() {

        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        throwable = (Throwable) sessionMap.get(RequestDispatcher.ERROR_EXCEPTION);
        sessionMap.remove(RequestDispatcher.ERROR_EXCEPTION);
        errorRequestUri = (String) sessionMap.get(RequestDispatcher.ERROR_REQUEST_URI);
        sessionMap.remove(RequestDispatcher.ERROR_REQUEST_URI);
        sessionMap.remove(RequestDispatcher.ERROR_STATUS_CODE);
        sessionMap.remove(RequestDispatcher.ERROR_EXCEPTION_TYPE);
        sessionMap.remove(RequestDispatcher.ERROR_MESSAGE);

        if (throwable != null) {
            addExceptionToMessages(throwable);
        }

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

        if (plan != null) {
            bugReport.addExeptionToMessages(t, sessionId, errorRequestUri, plan);
        } else {
            bugReport.addExeptionToMessages(t, sessionId, errorRequestUri);
        }
    }

    /**
     * Method responsible for sending a bug-report per mail based on the last
     * reported error.
     */
    public String sendBugReport() {
        String location = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
            .getLocalName();
        location += ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
            .getContextPath();

        try {
            if (plan != null) {
                bugReport
                    .sendBugReport(userEmail, userDescription, throwable, errorRequestUri, location, "Plato", plan);
            } else {
                bugReport.sendBugReport(userEmail, userDescription, throwable, errorRequestUri, location, "Plato");
            }

        } catch (MailException e) {
            log.error("Error sending bugreport from user " + user.getUsername() + " with email " + userEmail, e);
        }
        return "/index.jsf" + "?faces-redirect=true&GLO=true";
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
