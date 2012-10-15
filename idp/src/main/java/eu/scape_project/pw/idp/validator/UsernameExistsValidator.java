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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.persistence.EntityManager;

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

        Long userUsingUsername = (Long) em
            .createQuery("SELECT COUNT(u) FROM IdpUser u WHERE u.username = :desiredUsername")
            .setParameter("desiredUsername", desiredUsername).getSingleResult();

        if (userUsingUsername > 0) {
            throw new ValidatorException(new FacesMessage("The username is already taken. Please choose another one."));
        }
    }

    // Method used to make this class Unit-testable
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
