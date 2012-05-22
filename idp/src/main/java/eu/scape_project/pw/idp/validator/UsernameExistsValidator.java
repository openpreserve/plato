package eu.scape_project.pw.idp.validator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Class responsible for validate if a username already exists in database or
 * not. This cannot be done in the form of a FacesValidator class because
 * injection (in this case the EntityManager) is needed which only works in
 * managed beans.
 * 
 * @author Markus Hamm
 */
@ManagedBean(name = "UsernameExistsValidator")
@RequestScoped
public class UsernameExistsValidator implements Validator {

  @Inject
  private EntityManager em;

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String desiredUsername = (String) value;

    // Just ignore and let required="true" do its job.
    if (desiredUsername == null || desiredUsername.length() == 0) {
      return;
    }

    Long userUsingUsername = (Long) em.createQuery("SELECT COUNT(u) FROM IdpUser u WHERE u.username = :desiredUsername")
      .setParameter("desiredUsername", desiredUsername).getSingleResult();

    if (userUsingUsername > 0) {
      throw new ValidatorException(new FacesMessage("Username already assigned. Please choose another one."));
    }
  }

  // Method used to make this class Unit-testable
  public void setEntityManager(EntityManager em) {
    this.em = em;
  }
}
