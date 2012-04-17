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

package eu.planets_project.pp.plato.model.interfaces.actions;

import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.PlatoException;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.beans.MigrationResult;

public interface IMigrationAction extends IPreservationAction {
    MigrationResult migrate(PreservationActionDefinition action, DigitalObject digitalObject) throws PlatoException ;
    MigrationResult getLastResult();
}
