package eu.scape_project.pw.idp.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import eu.scape_project.pw.idp.UserManager;
import eu.scape_project.pw.idp.model.IdpUser;

@ManagedBean(name = "addUser")
@ViewScoped
public class AddUserBean {

  private IdpUser user;
  
  @Inject
  private UserManager userManager;

  public AddUserBean() {
    user = new IdpUser();
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
}
