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

    public GroupsView() {
    }

    public String init() {
        groups.init();
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
        return "/index.jsp";
    }

    /**
     * Invite users by their email addresses
     */
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

        Iterator<String> inviteMailsIter = inviteMails.iterator();
        while (inviteMailsIter.hasNext()) {
            String inviteMail = inviteMailsIter.next();
            try {
                groups.inviteUser(inviteMail, serverString);
                inviteMailsIter.remove();
                facesMessages.addInfo(inviteMail + " invited");
            } catch (AlreadyGroupMemberException e) {
                inviteMailsIter.remove();
                facesMessages.addWarning(inviteMail + " is already a member of your group");
            } catch (InvitationMailException e) {
                facesMessages.addError(inviteMail + " could not be invited");
            }
        }

        // Get missing mails
        StringBuffer failedMailsBuffer = new StringBuffer();
        Iterator<String> failedMailsIter = inviteMails.iterator();
        while (failedMailsIter.hasNext()) {
            failedMailsBuffer.append(failedMailsIter.next());

            if (failedMailsIter.hasNext()) {
                failedMailsBuffer.append(", ");
            }
        }
        inviteMailsString = failedMailsBuffer.toString();
    }

    /**
     * Remove a user from the group
     * 
     * @param user
     */
    public void removeUser(User user) {
        groups.removeUser(user);
    }

    /**
     * Leave a group
     */
    public void leaveGroup() {
        groups.leaveGroup();
    }

    // --------------- getter/setter ---------------
    public User getUser() {
        return user;
    }

    public List<User> getGroupUsers() {
        return groups.getGroupUsers();
    }

    // public Groups getGroups() {
    // return groups;
    // }

    public String getInviteMailsString() {
        return inviteMailsString;
    }

    public void setInviteMailsString(String inviteMailsString) {
        this.inviteMailsString = inviteMailsString;
    }

}
