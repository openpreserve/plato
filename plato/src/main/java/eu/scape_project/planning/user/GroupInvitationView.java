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

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import eu.scape_project.planning.utils.FacesMessages;

/**
 * View bean for group invitation.
 */
@Named("groupInvitation")
@RequestScoped
public class GroupInvitationView implements Serializable {

    private static final long serialVersionUID = -2162378700125807065L;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private Groups groups;

    /**
     * Initialize the object for usage.
     */
    public void init() {
        checkInvitation();
    }

    /**
     * Accept the invitation.
     * 
     * @return the navigation target
     */
    public String acceptInvitation() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                groups.acceptInvitation(actionToken);
                facesMessages.addInfo("You have successfully accepted the invitation");
                return "/user/groups.jsf";
            } catch (GroupNotFoundException e) {
                facesMessages.addError("The group you were invited to does not exist anymore");
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            } catch (AlreadyGroupMemberException e) {
                facesMessages.addError("You are already a member of this group");
            }
        } else {
            facesMessages.addError("No action token provided");
        }

        return null;
    }

    /**
     * Decline the invitation.
     * 
     * @return the navigation target
     */
    public String declineInvitation() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                groups.declineInvitation(actionToken);
                facesMessages.addInfo("You have declined the invitation");
                return "/user/groups.jsf";
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }

        return null;

    }

    /**
     * Returns the name of the group of the invitation.
     * 
     * @return the name of the group
     */
    public String getInvitationGroupName() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                return groups.getInvitationGroupName(actionToken);
            } catch (GroupNotFoundException e) {
                facesMessages.addError("The group you were invited to does not exist anymore");
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }

        return "";
    }

    /**
     * Check if the invitation is valid.
     * 
     * @return true if valid, false otherwise
     */
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
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks and invitation and fills facesmessages.
     */
    public void checkInvitation() {

        // Fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            try {
                groups.getInvitationGroupName(actionToken);
            } catch (GroupNotFoundException e) {
                facesMessages.addError("Group could not be found");
            } catch (TokenNotFoundException e) {
                facesMessages.addError("The provided action token is not valid");
            }
        } else {
            facesMessages.addError("No action token provided");
        }
    }

}
