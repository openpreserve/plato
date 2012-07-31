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
package eu.scape_project.planning.policies;

import java.io.Serializable;
import java.util.Arrays;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import eu.scape_project.planning.model.User;
import eu.scape_project.planning.utils.FacesMessages;

@Named("groups")
@SessionScoped
public class GroupsView implements Serializable {

    private static final long serialVersionUID = -2162378700125807065L;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private Groups groups;

    @Inject
    private User user;
    private String inviteMailsString = "";

    private boolean invitationAccepted = false;

    public GroupsView() {
    }

    /**
     * Method responsible for initializing all properties with proper values -
     * so the page can be displayed correctly.
     * 
     * @return OutcomeString which navigates to this page.
     */
    public String init() {
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

        // Invite users
        groups.inviteUsers(Arrays.asList(inviteMailsString.split("[,;]")), serverString);

        facesMessages.addInfo("Users invited");

        // return "users/groups.jsf";
    }

    public void acceptInvitation() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            invitationAccepted = groups.acceptInvitation(actionToken);
        } else {
            invitationAccepted = false;
        }
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

    public boolean isInvitationAccepted() {
        return invitationAccepted;
    }

}
