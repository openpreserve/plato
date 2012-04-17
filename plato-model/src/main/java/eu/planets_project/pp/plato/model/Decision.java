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
import javax.persistence.Lob;
import javax.persistence.OneToOne;


/**
 * This is the documentation of the Go/No-Go-decision for continuing along the workflow
 * or abandoning the planning and evaluation procedure.
 *
 * @author Christoph Becker
 */
@Entity
public class Decision implements Serializable, ITouchable {

    private static final long serialVersionUID = -4501163474724678308L;

    /**
     * The possible values of the Go/No-Go decision.
     *
     * @author Christoph Becker
     *
     */
    public enum GoDecision {
        UNDEFINED,GO,PROVISIONAL_GO, DEFERRED_GO, NO_GO;
    }

    @Id @GeneratedValue
    private int id;

    private GoDecision decision = GoDecision.UNDEFINED;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String reason;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String actionNeeded;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    public String getActionNeeded() {
        return actionNeeded;
    }

    public void setActionNeeded(String actionNeeded) {
        this.actionNeeded = actionNeeded;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Decision() {

    }

    public void setDecision(GoDecision decision) {
        this.decision = decision;
    }

    public GoDecision getDecision() {
        return decision;
    }

    public boolean isGoDecision() {
        return decision == GoDecision.GO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
