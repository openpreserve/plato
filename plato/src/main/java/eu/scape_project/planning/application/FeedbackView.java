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

import org.slf4j.Logger;

import eu.scape_project.planning.model.User;
import eu.scape_project.planning.utils.FacesMessages;

@ManagedBean(name = "feedback")
@ViewScoped
public class FeedbackView implements Serializable {
    private static final long serialVersionUID = -6886808166752517894L;

    @Inject
    private Logger log;

    @Inject
    private Feedback feedback;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private User user;

    private String userEmail;

    private String userComments;

    @PostConstruct
    public void setEmail() {
        // Prefill Email address
        userEmail = user.getEmail();
    }

    /**
     * Method responsible for sending user feedback per mail.
     */
    public void sendFeedback() {
        String host = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
            .getLocalName();

        boolean success = feedback.sendFeedback(userEmail, userComments, host);

        if (success) {
            log.debug("Feedback send from user " + user.getUsername() + " with email " + userEmail);
            facesMessages.addInfo("sendFeedback", "Thank you! Your feedback has been sent.");
        } else {
            log.warn("Error sending feedback from user " + user.getUsername() + " with email " + userEmail);
            facesMessages
                .addError(
                    "sendFeedback",
                    "Feedback couldn't be sent."
                        + "Because of an internal error your feedback couldn't be sent. We apologise for this and hope you are willing to inform us about this so we can fix the problem. "
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

    public String getUserComments() {
        return userComments;
    }

    public void setUserComments(String userComments) {
        this.userComments = userComments;
    }
}
