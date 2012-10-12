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

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import eu.scape_project.pw.idp.UserManager;
import eu.scape_project.pw.idp.model.IdpUser;
import eu.scape_project.pw.idp.utils.FacesMessages;
import eu.scape_project.pw.idp.utils.PropertiesLoader;

/**
 * Viewbean to add a new user.
 */
@ManagedBean(name = "addUser")
@ViewScoped
public class AddUserView {

    @Inject
    private FacesMessages facesMessages;

    private IdpUser user;

    private ReCaptcha reCaptcha;

    private Boolean addUserSuccessful;

    private Boolean activateUserSuccessful;

    /**
     * Temporary (dummy) variable only required to map the recaptchaHelper
     * jsf-input-tag to an value (which is mandatory). Thus, this field has no
     * impact on this class functionality.
     */
    private String recaptchaHelper;

    @Inject
    private UserManager userManager;

    /**
     * Initializes the instance for use.
     */
    @PostConstruct
    public void init() {
        user = new IdpUser();
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        Properties idpProperties = propertiesLoader.load("idp.properties");

        reCaptcha = ReCaptchaFactory.newReCaptcha(idpProperties.getProperty("recaptcha.publickey"),
            idpProperties.getProperty("recaptcha.privatekey"), false);
        addUserSuccessful = false;
        activateUserSuccessful = false;
    }

    /**
     * Adds the user.
     */
    public void addUser() {
        // create user
        userManager.addUser(user);
        addUserSuccessful = true;

        // send user activation mail
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String serverString = request.getServerName() + ":" + request.getServerPort();
        if (!userManager.sendActivationMail(user, serverString)) {
            facesMessages.addError("Could not send activation mail.");
        }
    }

    /**
     * Method responsible for triggering the activation of an already created
     * user.
     */
    public void activateUser() {
        // fetch the token from URL request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String actionToken = request.getParameter("uid");

        if (actionToken != null) {
            activateUserSuccessful = userManager.activateUser(actionToken);
        } else {
            activateUserSuccessful = false;
        }
    }

    // ---------- getter/setter ----------

    public IdpUser getUser() {
        return user;
    }

    public void setUser(IdpUser user) {
        this.user = user;
    }

    public ReCaptcha getReCaptcha() {
        return reCaptcha;
    }

    public void setReCaptcha(ReCaptcha reCaptcha) {
        this.reCaptcha = reCaptcha;
    }

    public Boolean getActivateUserSuccessful() {
        return activateUserSuccessful;
    }

    public void setActivateUserSuccessful(Boolean activateUserSuccessful) {
        this.activateUserSuccessful = activateUserSuccessful;
    }

    public String getRecaptchaHelper() {
        return recaptchaHelper;
    }

    public void setRecaptchaHelper(String recaptchaHelper) {
        this.recaptchaHelper = recaptchaHelper;
    }

    public Boolean getAddUserSuccessful() {
        return addUserSuccessful;
    }

    public void setAddUserSuccessful(Boolean addUserSuccessful) {
        this.addUserSuccessful = addUserSuccessful;
    }
}
