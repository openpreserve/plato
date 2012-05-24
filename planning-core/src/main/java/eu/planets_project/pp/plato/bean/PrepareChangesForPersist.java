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

package eu.planets_project.pp.plato.bean;

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
