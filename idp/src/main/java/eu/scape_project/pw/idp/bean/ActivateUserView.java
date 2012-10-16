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
package eu.scape_project.pw.idp.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import eu.scape_project.pw.idp.UserManager;
import eu.scape_project.pw.idp.excpetions.UserNotFoundExeception;
import eu.scape_project.pw.idp.model.IdpUser;
import eu.scape_project.pw.idp.utils.FacesMessages;

/**
 * View bean to add a new user.
 */
@ManagedBean(name = "activateUser")
@ViewScoped
public class ActivateUserView {

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private UserManager userManager;

    private boolean activateUserSuccessful = false;

    private IdpUser user;

    /**
     * Reads the action token and processes it.
     */
    public void processActionToken() {
        if (!activateUserSuccessful) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();
            String actionToken = request.getParameter("uid");

            try {
                user = userManager.getUserByActionToken(actionToken);
            } catch (UserNotFoundExeception e) {
                facesMessages.addError("Action token not valid.");
                user = null;
            }
        }
    }

    /**
     * Method responsible for triggering the activation of an already created
     * user.
     */
    public void activateUser() {
        // Fetch the token from URL request

        if (user != null) {
            try {
                userManager.activateUser(user);
                activateUserSuccessful = true;
            } catch (UserNotFoundExeception e) {
                facesMessages.addError("User could not be found.");
                activateUserSuccessful = false;
            }
        } else {
            activateUserSuccessful = false;
        }

    }

    // ---------- getter/setter ----------

    public IdpUser getUser() {
        return user;
    }

    public boolean isActivateUserSuccessful() {
        return activateUserSuccessful;
    }

    public void setActivateUserSuccessful(boolean activateUserSuccessful) {
        this.activateUserSuccessful = activateUserSuccessful;
    }

    public void setUser(IdpUser user) {
        this.user = user;
    }

}
