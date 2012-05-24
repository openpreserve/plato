/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.constraints.Length;

import eu.scape_project.planning.model.measurement.Measurement;


@Entity
public class DetailedExperimentInfo implements Serializable, ITouchable {
    private static final long serialVersionUID = 1482455823876161765L;

    @Id
    @GeneratedValue
    private int id;
    
    private Boolean successful=false;
    
    @Length(max = 2000000)
    @Column(length = 2000000)
    private String programOutput;

    /**
     * Outcome of last automated comparision, one per SampleObject.
     */
    private String cpr;

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @IndexColumn(name = "key_prop")
    @JoinTable(name = "detailedInfo_values")
    private Map<String,Measurement> measurements = new HashMap<String, Measurement>();
    
    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    
    /**
     * Puts measurement m to map measurements, uses the name of m's property as key
     *  
     * @param m
     */
    public void put(Measurement m) {
        if ((m == null)||(m.getProperty()== null))
            throw new IllegalArgumentException("No property defined for measurement.");
        measurements.put(m.getProperty().getName(), m);
    }

    public Map<String, Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Map<String, Measurement> measurements) {
        this.measurements = measurements;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void clear(){
        measurements.clear();
    }
    public void put(DetailedExperimentInfo info) {
        measurements.putAll(info.getMeasurements());
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getProgramOutput() {
        return programOutput;
    }

    public void setProgramOutput(String programOutput) {
        this.programOutput = programOutput;
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
