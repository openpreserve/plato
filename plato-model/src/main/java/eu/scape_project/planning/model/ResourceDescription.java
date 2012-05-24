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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

/**
 * Contains fields for describing the resources needed for an {@link Alternative}.
 *
 * @author Hannes Kulovits
 */
@Entity
public class ResourceDescription implements Serializable, ITouchable {

    private static final long serialVersionUID = 8652623297851734108L;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String reasonForConsidering;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String configSettings;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String necessaryResources;

    @Id @GeneratedValue
    private int id;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getConfigSettings() {
        return configSettings;
    }
    public void setConfigSettings(String desirable) {
        this.configSettings = desirable;
    }
    public String getNecessaryResources() {
        return necessaryResources;
    }
    public void setNecessaryResources(String necessaryResources) {
        this.necessaryResources = necessaryResources;
    }
    public String getReasonForConsidering() {
        return reasonForConsidering;
    }
    public void setReasonForConsidering(String reasonForConsidering) {
        this.reasonForConsidering = reasonForConsidering;
    }
    public ChangeLog getChangeLog() {
        return changeLog;
    }
    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }
    public boolean isChanged(){
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
    }
}
