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
package eu.scape_project.planning.model;

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

    @Id
    @GeneratedValue
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

    @OneToOne(cascade = CascadeType.ALL)
    protected DigitalObject t2flowExecutablePlan;

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

    @OneToOne(cascade = CascadeType.ALL)
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
    public void handleChanges(IChangesHandler h) {
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

    public DigitalObject getT2flowExecutablePlan() {
        return t2flowExecutablePlan;
    }

    public void setT2flowExecutablePlan(DigitalObject t2flowExecutablePlan) {
        this.t2flowExecutablePlan = t2flowExecutablePlan;
    }

}
