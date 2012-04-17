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


/**
 * an interface to be used in several places in Plato
 * where longer-running processes should provide feedback
 * on the go.
 * @author cb
 * @see RunExperimentsAction#run(Object)
 * @see EvaluateExperimentsAction#evaluateAll()
 */
public interface IStatusListener {
    public void updateStatus(String msg);
}
