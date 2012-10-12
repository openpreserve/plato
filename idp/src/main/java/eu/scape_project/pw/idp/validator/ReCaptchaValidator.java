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

import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import eu.scape_project.pw.idp.utils.PropertiesLoader;

/**
 * Validator for ReCaptcha.
 */
@FacesValidator("ReCaptchaValidator")
public class ReCaptchaValidator implements Validator {

    /**
     * IDP properties.
     */
    private Properties idpProperties;

    /**
     * Constructor.
     */
    public ReCaptchaValidator() {
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        idpProperties = propertiesLoader.load("idp.properties");
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        String remoteAddr = request.getRemoteAddr();
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(idpProperties.getProperty("recaptcha.privatekey"));

        String challenge = request.getParameter("recaptcha_challenge_field");
        String uresponse = request.getParameter("recaptcha_response_field");
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

        if (reCaptchaResponse.isValid()) {
            return;
        } else {
            throw new ValidatorException(new FacesMessage("Invalid ReCaptcha text. Please try again."));
        }
    }
}
