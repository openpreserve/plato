package eu.scape_project.pw.idp.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ConfirmPasswordValidator")
public class ConfirmPasswordValidator implements Validator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String password = (String) value;
    String confirm = (String) component.getAttributes().get("pwConfirm");

    // Just ignore and let required="true" do its job.
    if (password == null || confirm == null || password.length() == 0 || confirm.length() == 0) {
      return;
    }

    if (!password.equals(confirm)) {
      throw new ValidatorException(new FacesMessage("Passwords are not equal."));
    }
  }
}
