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
package eu.scape_project.planning.model;

/**
 * A class implementing this interface may visit objects implementing
 * {@link eu.scape_project.planning.model.ITouchable}. By calling visit the
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
