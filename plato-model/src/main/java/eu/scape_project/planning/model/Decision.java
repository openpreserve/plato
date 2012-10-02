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
