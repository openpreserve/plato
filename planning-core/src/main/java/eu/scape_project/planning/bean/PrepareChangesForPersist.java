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
 ******************************************************************************/
package eu.scape_project.planning.bean;

import java.io.Serializable;

import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;

/**
 * This class provides a mechanism to update the user name of changed entities.
 */
public class PrepareChangesForPersist implements IChangesHandler, Serializable {

    private static final long serialVersionUID = 6891938566086489955L;
    private String user;

    public PrepareChangesForPersist(){
        user = null;
    }

    public PrepareChangesForPersist(String user){
        this.user = user;
    }

    /**
     * Tells <code>entity</code> to prepare for persisting, if it implements {@link ITouchable}.
     * The object will do a call back to {@link #visit(ITouchable)}.
     *
     * @param entity
     */
    public void prepare(Object entity) {
        if (entity instanceof ITouchable) {
            ((ITouchable)entity).handleChanges(this);
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Sets the currently logged in user for every touchable which was modified,
     * and resets the modified flag.
     * Resetting the modified flag is ok: If persisting fails the user was already set.
     *
     */
    public void visit(ITouchable t) {
        ChangeLog log = t.getChangeLog();
        // maybe this object was just created
        String createdBy = log.getCreatedBy();
        if (createdBy == null || "".equals(createdBy))
            log.setCreatedBy(user);
        // set user name and reset dirty flag
        if (log.isDirty()) {
            if (user != null) {
               log.setChangedBy(user);
            }
            log.setDirty(false);
        }
    }

}
