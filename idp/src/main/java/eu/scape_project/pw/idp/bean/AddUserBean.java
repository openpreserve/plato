package eu.scape_project.pw.idp.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

import eu.scape_project.pw.idp.UserManager;
import eu.scape_project.pw.idp.model.IdpUser;

@ManagedBean(name = "addUser")
@ViewScoped
public class AddUserBean {

    private IdpUser user;

    private ReCaptcha reCaptcha;

    @Inject
    private UserManager userManager;

    public AddUserBean() {
        user = new IdpUser();
        reCaptcha = ReCaptchaFactory.newReCaptcha("6Lclf9ASAAAAAJE2REWGZ7chcFgndfWIhAY01v_n",
            "6Lclf9ASAAAAAGMQlB8N-6a-UeKKXH6eMuB1hnEH", false);
    }

    public String addUser() {
        userManager.addUser(user);
        return "login.jsf";
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
}
