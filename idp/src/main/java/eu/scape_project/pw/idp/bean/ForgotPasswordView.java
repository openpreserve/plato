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
import eu.scape_project.pw.idp.excpetions.CannotSendMailException;
import eu.scape_project.pw.idp.excpetions.UserNotFoundExeception;
import eu.scape_project.pw.idp.utils.FacesMessages;

/**
 * Viewbean to add a new user.
 */
@ManagedBean(name = "forgotPassword")
@ViewScoped
public class ForgotPasswordView {

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private UserManager userManager;

    private String userIdentifier;

    /**
     * Resets the password of the user identified by userIdentifier.
     */
    public void resetPassword() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String serverString = request.getServerName() + ":" + request.getServerPort();

        try {
            userManager.initiateResetPassword(userIdentifier, serverString);
            facesMessages
                .addInfo("A mail with password recovery information has been sent to the email address provided when you created the account.");
        } catch (UserNotFoundExeception e) {
            facesMessages.addError("No user with this username or email address found.");
        } catch (CannotSendMailException e) {
            facesMessages.addError("Error sending the password reset mail.");
        }
    }

    // ---------- getter/setter ----------
    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }
}
