/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,  
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

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.SampleObject;

/**
 * A preservation action capable of performing the action on a sample object.
 */
public interface IPreservationAction {

    /**
     * Perform the preservation action.
     * 
     * @param alternative
     *            the alternative
     * @param sampleObject
     *            the sample object to perform the action on
     * @return true if the action succeeded, false otherwise
     * @throws PlatoException
     *             if an exception occurred while performing the action
     */
    boolean perform(Alternative alternative, SampleObject sampleObject) throws PlatoException;
}
