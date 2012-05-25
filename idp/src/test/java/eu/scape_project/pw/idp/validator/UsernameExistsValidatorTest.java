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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;

public class UsernameExistsValidatorTest {
  private UsernameExistsValidator usernameExValidator;

  public UsernameExistsValidatorTest() {
    usernameExValidator = new UsernameExistsValidator();
  }

  @Test
  public void validate_stopValidationIfNoUsernameIsPassed() {
    usernameExValidator.validate(null, null, "");

    assertTrue(true);
  }

  @Test
  public void validate_ok_userNameNotExistsInDatabase() {
    EntityManager em = mock(EntityManager.class);
    Query query = mock(Query.class);
    Query parameterQuery = mock(Query.class);
    when(em.createQuery(anyString())).thenReturn(query);
    when(query.setParameter(anyString(), anyObject())).thenReturn(parameterQuery);
    when(parameterQuery.getSingleResult()).thenReturn(0L);

    usernameExValidator.setEntityManager(em);

    usernameExValidator.validate(null, null, "nonExistingUser");

    assertTrue(true);
  }

  @Test(expected = ValidatorException.class)
  public void validate_notOk_userNameExistsInDatabase() {
    EntityManager em = mock(EntityManager.class);
    Query query = mock(Query.class);
    Query parameterQuery = mock(Query.class);
    when(em.createQuery(anyString())).thenReturn(query);
    when(query.setParameter(anyString(), anyObject())).thenReturn(parameterQuery);
    when(parameterQuery.getSingleResult()).thenReturn(1L);

    usernameExValidator.setEntityManager(em);

    usernameExValidator.validate(null, null, "nonExistingUser");
  }
}
