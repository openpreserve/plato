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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TriggerDefinition implements Serializable{
    

    /**
     * 
     */
    private static final long serialVersionUID = 3477679926993171004L;

    @OneToOne(cascade = CascadeType.ALL)
    private Trigger newCollection = new Trigger(TriggerType.NEW_COLLECTION);
    
    @OneToOne(cascade = CascadeType.ALL)
    private Trigger periodicReview = new Trigger(TriggerType.PERIODIC_REVIEW);
    
    @OneToOne(cascade = CascadeType.ALL)
    private Trigger changedEnvironment = new Trigger(TriggerType.CHANGED_ENVIRONMENT);
    
    @OneToOne(cascade = CascadeType.ALL)
    private Trigger changedObjective = new Trigger(TriggerType.CHANGED_OBJECTIVE);
    
    @OneToOne(cascade = CascadeType.ALL)
    private Trigger changedCollectionProfile = new Trigger(TriggerType.CHANGED_COLLECTION_PROFILE);
    
    @Id
    @GeneratedValue
    private int id;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTrigger(Trigger t) {
        if (t.getType() == TriggerType.NEW_COLLECTION) {
            setNewCollection(t);
        } else if (t.getType() == TriggerType.PERIODIC_REVIEW) {
            setPeriodicReview(t);
        } else if (t.getType() == TriggerType.CHANGED_ENVIRONMENT) {
            setChangedEnvironment(t);
        } else if (t.getType() == TriggerType.CHANGED_OBJECTIVE) {
            setChangedObjective(t);
        } else {
            setChangedCollectionProfile(t);
        }
    }
    
    public Trigger getChangedCollectionProfile() {
        return changedCollectionProfile;
    }
    public void setChangedCollectionProfile(Trigger changedCollectionProfile) {
        this.changedCollectionProfile = changedCollectionProfile;
    }
    public Trigger getChangedEnvironment() {
        return changedEnvironment;
    }
    public void setChangedEnvironment(Trigger changedEnvironment) {
        this.changedEnvironment = changedEnvironment;
    }
    public Trigger getChangedObjective() {
        return changedObjective;
    }
    public void setChangedObjective(Trigger changedObjective) {
        this.changedObjective = changedObjective;
    }
    public Trigger getNewCollection() {
        return newCollection;
    }
    public void setNewCollection(Trigger newCollection) {
        this.newCollection = newCollection;
    }
    public Trigger getPeriodicReview() {
        return periodicReview;
    }
    public void setPeriodicReview(Trigger periodicReview) {
        this.periodicReview = periodicReview;
    }
}
