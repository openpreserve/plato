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

package eu.scape_project.planning.model.interfaces.actions;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;

/**
 * Interface for preservation action registries supported by plato
 * 
 * @author Michael Kraxner
 *
 */
public interface IPreservationActionRegistry {
    /**
     * Connects the registry to the remote endpoint.
     * 
     * @param URL
     * @throws ServiceException
     * @throws MalformedURLException
     */
    void connect(String URL) throws ServiceException, MalformedURLException;
    /**
     * returns a list of preservation actions which can handle objects of the given sourceFormat
     * 
     * @param sourceFormat
     * @return null, if nothing is found 
     * @throws RemoteException
     */
    List<PreservationActionDefinition> getAvailableActions(FormatInfo sourceFormat) throws PlatoException;
    String getLastInfo();
    
    String getToolIdentifier(String url);
    
    String getToolParameters(String url);
    
}
