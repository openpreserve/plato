package eu.scape_project.pw.idp;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import eu.scape_project.pw.idp.model.IdpUser;

@Stateless
public class UserManager {
  @Inject
  private EntityManager em;
  
  @Inject
  private Logger log;

  /**
   * Method responsible for adding a new user.
   * 
   * @param user User to add.
   */
  public void addUser(IdpUser user) {
    em.persist(user);
    log.info("Added user with username " + user.getUsername());
  }
}
