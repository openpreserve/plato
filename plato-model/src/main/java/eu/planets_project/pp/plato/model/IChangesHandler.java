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

package eu.planets_project.pp.plato.model;

/**
 * A class implementing this interface may visit objects implementing
 * {@link eu.planets_project.pp.plato.model.ITouchable}. By calling visit the
 * object intends to get information about having been changed (who changed it, when)
 *
 * @author Hannes Kulovits
 */
public interface IChangesHandler {

    /**
     * Objects which implement {@link ITouchable} call this function themselve so
     * the changes handler can take some actions.
     *
     * @param t
     */
    void visit(ITouchable t);
}
