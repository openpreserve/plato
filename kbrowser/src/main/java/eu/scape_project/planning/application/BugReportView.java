package eu.scape_project.planning.application;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import eu.scape_project.planning.model.User;
import eu.scape_project.planning.utils.FacesMessages;

@ManagedBean(name = "bugReport")
@ViewScoped
public class BugReportView implements Serializable {
    private static final long serialVersionUID = 2018521013047511583L;

    @Inject
    private Logger log;

    @Inject
    BugReport bugReport;

    @Inject
    private User user;

    // Does not work because conversation is not propagated
    // @Inject @LoadedPlan Plan plan;

    private Throwable exception;

    private String exceptionPage;

    private String userEmail;

    private String userDescription;

    @Inject
    private FacesMessages facesMessages;

    public BugReportView() {
        exception = null;
    }

    /**
     * Method called each time after the bean is created. This bean is generated
     * based on the JSF2 ViewState scope.
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
     * Method reponsible for adding a exception to the Plato messages array.
     * 
     * @param e
     *            Exception to add.
     */
    private void addExceptionToMessages(Throwable e) {
        String errorType = e.getClass().getCanonicalName();
        String errorMessage = e.getMessage();
        String sessionId = "";
        try {
            sessionId = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
                .getSession().getId();
        } catch (RuntimeException ex) {
            log.debug("Unable to retrieve session-id");
        }

        String userName = (user == null) ? "Unknown" : user.getUsername();
        String currentPage = exceptionPage;

        bugReport.addExeptionToMessages(errorType, errorMessage, sessionId, userName, currentPage);
    }

    /**
     * Method responsible for sending a bug-report per mail based on the last
     * reported error.
     */
    public void sendBugReport() {
        String host = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
            .getLocalName();

        boolean success = bugReport.sendBugReport(exception, userDescription, userEmail, host);

        if (success) {
            log.debug("Bugreport sent from user " + user.getUsername() + " with email " + userEmail);
            facesMessages
                .addInfo("sendBugReport",
                    "Bugreport sent. Thank you for your feedback. We will try to analyse and resolve the issue as soon as possible.");
        } else {
            log.error("Error sending bugreport from user " + user.getUsername() + " with email " + userEmail);
            facesMessages
                .addError(
                    "sendBugReport",
                    "Bugreport couldn't be sent."
                        + "Because of an internal error your bug report couldn't be sent. We apologise for this and hope you are willing to inform us about this so we can fix the problem. "
                        + "Please send an email to plato@ifs.tuwien.ac.at with a "
                        + "description of what you have been doing at the time of the error." + "Thank you very much!");
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
