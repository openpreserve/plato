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
package eu.scape_project.planning.validation;
///*******************************************************************************
// * Copyright (c) 2006-2010 Vienna University of Technology, 
// * Department of Software Technology and Interactive Systems
// *
// * All rights reserved. This program and the accompanying
// * materials are made available under the terms of the
// * Apache License, Version 2.0 which accompanies
// * this distribution, and is available at
// * http://www.apache.org/licenses/LICENSE-2.0 
// *******************************************************************************/
//package eu.scape_project.planning.validation;
//
//import javax.faces.application.FacesMessage;
//import javax.faces.component.UIComponent;
//import javax.faces.context.FacesContext;
//import javax.faces.validator.Validator;
//import javax.faces.validator.ValidatorException;
//
///**
// * Implements a JSF validator that checks the respective value for being not null and
// * between 0.0 and 5.0. This validator serves a special purpose namely the validation of
// * target value input in the workflow step Transform Measured Values.
// *
// * @author Hannes Kulovits
// */
//public class TargetValueValidator implements javax.validation.Validator{
//
//    public TargetValueValidator() {
//    }
//
//    /**
//     * Overrides {@link javax.faces.validator.Validator#validate(FacesContext, UIComponent, Object)}
//     */
//    public void validate(FacesContext context, UIComponent component, Object value)
//        throws ValidatorException {
//
//        if (value == null) {
//            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation error.", "Target value must be between 0.0 and 5.0."));
//        }
//        // The converter has taken care of conversion... hopefully.
//        // Otherwise we will notice immediately :)
//        Double doubleValue = (Double)value;
//
//        if (! (doubleValue >= 0.0 && doubleValue <= 5.0)) {
//            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation error.", "Target value must be between 0.0 and 5.0."));
//        }
//    }
//}
