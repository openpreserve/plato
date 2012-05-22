package eu.scape_project.pw.idp.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

import org.slf4j.Logger;

import eu.scape_project.pw.idp.UserManager;
import eu.scape_project.pw.idp.model.IdpRole;
import eu.scape_project.pw.idp.model.IdpUser;

@ManagedBean(name = "addUser")
@ViewScoped
public class AddUserBean {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

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

    public AddUserBean() {
        user = new IdpUser();

        reCaptcha = ReCaptchaFactory.newReCaptcha("6Lclf9ASAAAAAJE2REWGZ7chcFgndfWIhAY01v_n",
            "6Lclf9ASAAAAAGMQlB8N-6a-UeKKXH6eMuB1hnEH", false);
        addUserSuccessful = false;
        activateUserSuccessful = false;
    }

    public void addUser() {
        // create user
        userManager.addUser(user);
        addUserSuccessful = true;

        // send user activation mail
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
            .getRequest();
        String serverString = request.getServerName() + ":" + request.getServerPort();
        userManager.sendActivationMail(user, serverString);
    }

    /**
     * Method responsible for triggering the activation of an already created
     * user
     * 
     * @return outcome-string of the page to display.
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
