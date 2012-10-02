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
 * Entity bean storing additional information about an Evaluation-Step.
 *
 * @author Mark Guttenbrunner
 */
@Entity
public class PlanDefinition implements Serializable, ITouchable {

    private static final long serialVersionUID = 8385664635418556928L;

    @Id @GeneratedValue
    private int id;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    @OneToOne(cascade=CascadeType.ALL)
    private TriggerDefinition triggers = new TriggerDefinition();
    
    private String currency = new String("EUR");

    private String costsIG;
 
    private String costsPE;

    private String costsQA;
    
    private String costsRM;
    
    private String costsPA;

    private String costsREI;

    private String costsTCO;

    @Lob
    private String costsRemarks;

    private String responsibleExecution;

    private String responsibleMonitoring;
    
    public int getId() {
        return id;
    }

    public String getResponsibleMonitoring() {
        return responsibleMonitoring;
    }

    public void setResponsibleMonitoring(String responsibleMonitoring) {
        this.responsibleMonitoring = responsibleMonitoring;
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
    

    public String getCostsIG() {
        return costsIG;
    }

    public void setCostsIG(String costsIG) {
        this.costsIG = costsIG;
    }

    public String getCostsPA() {
        return costsPA;
    }

    public void setCostsPA(String costsPA) {
        this.costsPA = costsPA;
    }

    public String getCostsPE() {
        return costsPE;
    }

    public void setCostsPE(String costsPE) {
        this.costsPE = costsPE;
    }

    public String getCostsQA() {
        return costsQA;
    }

    public void setCostsQA(String costsQA) {
        this.costsQA = costsQA;
    }

    public String getCostsREI() {
        return costsREI;
    }

    public void setCostsREI(String costsREI) {
        this.costsREI = costsREI;
    }

    public String getCostsRM() {
        return costsRM;
    }

    public void setCostsRM(String costsRM) {
        this.costsRM = costsRM;
    }

    public String getCostsRemarks() {
        return costsRemarks;
    }

    public void setCostsRemarks(String costsRemarks) {
        this.costsRemarks = costsRemarks;
    }

    public String getCostsTCO() {
        return costsTCO;
    }

    public void setCostsTCO(String costsTCO) {
        this.costsTCO = costsTCO;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getResponsibleExecution() {
        return responsibleExecution;
    }

    public void setResponsibleExecution(String responsibleExecutor) {
        this.responsibleExecution = responsibleExecutor;
    }

    public TriggerDefinition getTriggers() {
        return triggers;
    }

    public void setTriggers(TriggerDefinition triggers) {
        this.triggers = triggers;
    }

}
