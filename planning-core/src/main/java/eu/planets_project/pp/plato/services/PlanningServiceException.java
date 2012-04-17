/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.services;

public class PlanningServiceException extends Exception {

    private static final long serialVersionUID = -3107662456923200745L;

    public PlanningServiceException() {
    }

    public PlanningServiceException(String arg0) {
        super(arg0);
    }

    public PlanningServiceException(Throwable arg0) {
        super(arg0);
    }

    public PlanningServiceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
