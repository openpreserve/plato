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
package eu.planets_project.pp.plato.evaluation;

public class EvaluatorException extends Exception {

	private static final long serialVersionUID = 1L;

	public EvaluatorException() {
    }

    public EvaluatorException(String arg0) {
        super(arg0);
    }

    public EvaluatorException(Throwable arg0) {
        super(arg0);
    }

    public EvaluatorException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
