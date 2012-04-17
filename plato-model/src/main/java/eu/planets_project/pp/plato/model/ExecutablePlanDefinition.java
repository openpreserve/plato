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

package eu.planets_project.pp.plato.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.Length;

/**
 * Entity bean storing additional information about an Evaluation-Step.
 *
 * @author Mark Guttenbrunner
 */
@Entity
public class ExecutablePlanDefinition implements Serializable, ITouchable {

    private static final long serialVersionUID = 8385664635418556928L;

    @Id @GeneratedValue
    private int id;
    
    private String objectPath;
    
    @Lob
    private String toolParameters;

    @Lob
    private String triggersConditions;

    @Lob
    private String validateQA;
    
    @Length(max = 2000000)
    @Column(length = 2000000)
    protected String executablePlan;
    
    @Length(max = 2000000)
    @Column(length = 2000000)
    protected String eprintsExecutablePlan;
    
    public String getExecutablePlan() {
        return executablePlan;
    }

    public void setExecutablePlan(String executablePlan) {
        this.executablePlan = executablePlan;
    }
    
    public String getEprintsExecutablePlan() {
        return eprintsExecutablePlan;
    }

    public void setEprintsExecutablePlan(String eprintsExecutablePlan) {
        this.eprintsExecutablePlan = eprintsExecutablePlan;
    }

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChangeLog getChangeLog() {
        return this.changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }
    public boolean isChanged() {
        return changeLog.isAltered();
    }
    public void touch() {
        getChangeLog().touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h){
        h.visit(this);
    }

    public String getObjectPath() {
        return objectPath;
    }

    public void setObjectPath(String objectPath) {
        this.objectPath = objectPath;
    }

    public String getTriggersConditions() {
        return triggersConditions;
    }

    public void setTriggersConditions(String triggersConditions) {
        this.triggersConditions = triggersConditions;
    }

    public String getValidateQA() {
        return validateQA;
    }

    public void setValidateQA(String validateQA) {
        this.validateQA = validateQA;
    }

    public String getToolParameters() {
        return toolParameters;
    }

    public void setToolParameters(String toolParameters) {
        this.toolParameters = toolParameters;
    }
}
