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
