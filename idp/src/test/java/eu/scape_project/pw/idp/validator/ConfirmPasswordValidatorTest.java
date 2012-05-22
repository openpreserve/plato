package eu.scape_project.pw.idp.validator;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.validator.ValidatorException;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConfirmPasswordValidatorTest {

  private ConfirmPasswordValidator confirmPwValidator;

  public ConfirmPasswordValidatorTest() {
    confirmPwValidator = new ConfirmPasswordValidator();
  }

  @Test
  public void validate_stopValidationIfPasswordOrConfirmationIsEmpty() {
    UIComponent uiComponent = new UIInput();
    Map<String, Object> attributesMap = uiComponent.getAttributes();

    uiComponent.getAttributes().put("pwConfirm", "");
    confirmPwValidator.validate(null, uiComponent, "");

    uiComponent.getAttributes().put("pwConfirm", "");
    confirmPwValidator.validate(null, uiComponent, "pass");

    uiComponent.getAttributes().put("pwConfirm", "confirmPass");
    confirmPwValidator.validate(null, uiComponent, "");

    assertTrue(true);
  }

  @Test
  public void validate_equalPasswordsAreAccepted() {
    UIComponent uiComponent = new UIInput();
    Map<String, Object> attributesMap = uiComponent.getAttributes();

    uiComponent.getAttributes().put("pwConfirm", "EqualPassword");
    confirmPwValidator.validate(null, uiComponent, "EqualPassword");

    assertTrue(true);
  }

  @Test(expected = ValidatorException.class)
  public void validate_unEqualPasswordsAreNotAccepted() {
    UIComponent uiComponent = new UIInput();
    Map<String, Object> attributesMap = uiComponent.getAttributes();

    uiComponent.getAttributes().put("pwConfirm", "Password");
    confirmPwValidator.validate(null, uiComponent, "WrongConfirmedPassword");
  }
}
