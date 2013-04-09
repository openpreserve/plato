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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.interfaces.actions;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;

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
     * returns a list of preservation actions which can handle objects of the
     * given sourceFormat
     * 
     * @param sourceFormat
     * @return null, if nothing is found
     * @throws RemoteException
     */
    List<IPreservationActionInfo> getAvailableActions(FormatInfo sourceFormat) throws PlatoException;

    String getLastInfo();

    String getToolIdentifier(String url);

    String getToolParameters(String url);

}
