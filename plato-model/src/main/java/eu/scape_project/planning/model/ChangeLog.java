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

package eu.scape_project.planning.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * This class provides information about the object it is a member of. Most importantly:
 * <ul>
 * <li>who created it and when</li>
 * <li>who changed it last and when</li>
 * </ul>
 *
 * @author Hannes Kulovits
 */
@Entity
public class ChangeLog implements Serializable {

    private static final long serialVersionUID = 1490303678803904382L;

    @Id
    @GeneratedValue
    private int id;

    private long created = System.currentTimeMillis();
    private long changed = System.currentTimeMillis();

    /**
     * Indicates whether an object was altered. Is not persisted: if an object is
     * persisted, it is not dirty any more!
     */
    @Transient
    private boolean dirty;

    @Transient
    private static DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

    /**
     * User who created the object lastly.
     * Note: will resolve to some userid in future
     */
    private String createdBy;

    /**
     * User who changed the object lastly.
     * Note: will resolve to some userid in future
     */
    private String changedBy;

    /**
     * Default constructor (for Hibernate etc)
     */
    public ChangeLog() {
    }

    public ChangeLog(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getChanged() {
        return changed;
    }

    public void setChanged(long changed) {
        this.changed = changed;
    }

    public String getChangedString() {
        return ChangeLog.dateFormat.format(new Date(this.changed));
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public long getCreated() {
        return created;
    }

    public String getCreatedString() {
        return ChangeLog.dateFormat.format(new Date(this.created));
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * We have this function here because
     * isChanged can come in conflict with public long getChanged(),
     * e.g. the digester uses this function instead of getChanged
     */
    public boolean isAltered() {
        return changed > created;
    }

    public void touch(String username) {
        this.touch();
        setChangedBy(username);
    }

    public void touch(){
        setChanged(System.currentTimeMillis());
        this.dirty = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean value) {
        dirty = value;
    }
}