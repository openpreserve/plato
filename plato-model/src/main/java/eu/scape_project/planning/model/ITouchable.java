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
 * Classes implementing this interface allow to update their changed status stored in
 * {@link eu.scape_project.planning.model.ChangeLog}.
 *
 * @author Hannes Kulovits
 */
public interface ITouchable {

    /**
     * Tell the object that it has been changed by calling this method.
     */
    public void touch();

    /**
     * @return true if the object has been changed at some time.
     */
    public boolean isChanged();

    /**
     * Returns the {@link ChangeLog} of the object, so a changes handler can access it.
     *
     * @return ChangeLog
     */
    public ChangeLog getChangeLog();

    /**
     * This function ensures that a {@link IChangesHandler} can visit every touchable object in the model.
     *
     * The object has to pass itself to the changes handler <code>h</code> via {@link IChangesHandler#visit(ITouchable)}.
     * And also all aggregated children which implement ITouchable themselves, by calling their
     * handleChanges() method.
     *
     * @param h
     */
    public void handleChanges(IChangesHandler h);
}
