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
package eu.scape_project.planning.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import eu.scape_project.planning.model.User;
import eu.scape_project.planning.utils.FacesMessages;

@Named("groups")
@ConversationScoped
public class GroupsView implements Serializable {

    private static final long serialVersionUID = -2162378700125807065L;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private Groups groups;

    @Inject
    private User user;
    private String inviteMailsString = "";

    public GroupsView() {
    }

    /**
     * Method responsible for initializing all properties with proper values -
     * so the page can be displayed correctly.
     * 
     * @return OutcomeString which navigates to this page.
     */
    @PostConstruct
    public String init() {
        
        isInvitationValid();
        
        return "user/groups.jsf";
    }

    /**
     * Method responsible for saving the made changes
     * 
     * @return Outcome String redirecting to start page.
     */
    public String save() {
        groups.save();
        return "/index.jsp";
    }

    /**
     * Method responsible for discarding the made changes
     * 
     * @return Outcome String redirecting to start page.
     */
    public String discard() {
        groups.discard();
        init();
        return "/index.jsp";
    }

    public void inviteUsers() {

        if (inviteMailsString.equals("")) {
            facesMessages.addWarning("No email addresses of people to invite were entered");
            return;
        }

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String serverString = request.getServerName() + ":" + request.getServerPort();

        ArrayList<String> inviteMails = new ArrayList<String>(Arrays.asList(inviteMailsString.split("\\s*[,;\\s]\\s*")));

        // Invite users
        List<String> invitedMails = groups.inviteUsers(inviteMails, serverString);

        // Get invited Mails
        StringBuffer invitedMailsBuffer = new StringBuffer();
        Iterator<String> invitedMailsIter = invitedMails.iterator();
        while (invitedMailsIter.hasNext()) {
            String invitedMail = invitedMailsIter.next();
            invitedMailsBuffer.append(invitedMail);

            if (invitedMailsIter.hasNext()) {
                invitedMailsBuffer.append(", ");
            }
        }

        inviteMails.removeAll(invitedMails);

        // Get missing mails
        StringBuffer inviteMailsBuffer = new StringBuffer();
        Iterator<String> inviteMailsIter = inviteMails.iterator();
        while (inviteMailsIter.hasNext()) {
            String inviteMail = inviteMailsIter.next();
            inviteMailsBuffer.append(inviteMail);

            if (inviteMailsIter.hasNext()) {
                inviteMailsBuffer.append(", ");
            }
        }
        inviteMailsString = inviteMailsBuffer.toString();

        if (inviteMails.size() == 0) {
            facesMessages.addInfo("User(s) " + invitedMailsBuffer.toString() + " invited");
        } else if (invitedMails.size() > 0) {
            facesMessages.addWarning("User(s) " + invitedMailsBuffer.toString()
                + " invited but not all users could be invited");
        } else {
            facesMessages.addError("Error inviting user(s)");
        }

    }

    public void acceptInvitation() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                groups.acceptInvitation(actionToken);
                facesMessages.addInfo("You have successfully accepted the invitation");
            } catch (GroupNotFoundException e) {
                facesMessages.addError("Group could not be found");
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }
    }

    public void acceptInvitation(String actionToken) {

        if (actionToken != null) {
            try {
                groups.acceptInvitation(actionToken);
                facesMessages.addInfo("You have successfully accepted the invitation");
            } catch (GroupNotFoundException e) {
                facesMessages.addError("Group could not be found");
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }

    }

    public void declineInvitation() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                groups.declineInvitation(actionToken);
                facesMessages.addInfo("You have successfully accepted the invitation");
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }
    }

    public String getInvitationGroupName() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                return groups.getInvitationGroupName(actionToken);
            } catch (GroupNotFoundException e) {
                facesMessages.addError("Group could not be found");
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }

        return "";
    }

    public boolean isInvitationValid() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                groups.getInvitationGroupName(actionToken);
                return true;
            } catch (GroupNotFoundException e) {
                return false;
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }

        return false;
    }

    public void removeUser(User user) {
        groups.switchGroup(user);
    }

    public void leaveGroup() {
        groups.switchGroup();
    }

    // --------------- getter/setter ---------------
    public User getUser() {
        return user;
    }

    public Groups getGroups() {
        return groups;
    }

    public String getInviteMailsString() {
        return inviteMailsString;
    }

    public void setInviteMailsString(String inviteMailsString) {
        this.inviteMailsString = inviteMailsString;
    }

}
