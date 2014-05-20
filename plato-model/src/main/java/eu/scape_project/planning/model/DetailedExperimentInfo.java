/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,  
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
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import eu.scape_project.planning.model.measurement.Measurement;

/**
 * Describes the outcome of an experiment.
 */
@Entity
public class DetailedExperimentInfo implements Serializable, ITouchable {
    private static final long serialVersionUID = 1482455823876161765L;

    @Id
    @GeneratedValue
    private int id;

    private Boolean successful = false;

    @Lob
    private String programOutput;

    /**
     * Outcome of last automated comparison, one per SampleObject.
     */
    private String cpr;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "detailedInfo_values")
    private Map<String, Measurement> measurements = new HashMap<String, Measurement>();

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    /**
     * Puts measurement {@code m} to the measurements map with the measure ID as
     * key.
     * 
     * @param m
     *            measurement to add
     */
    public void put(Measurement m) {
        if (m != null) {
            measurements.put(m.getMeasureId(), m);
        }
    }

    /**
     * Puts all measurements of the provided experiment info to the measurements
     * map.
     * 
     * @param info
     *            the detailed experiment info to use
     */
    public void put(DetailedExperimentInfo info) {
        measurements.putAll(info.getMeasurements());
    }

    /**
     * Clears the data of this object.
     */
    public void clear() {
        measurements.clear();
        programOutput = "";
        cpr = "";
    }

    @Override
    public ChangeLog getChangeLog() {
        return changeLog;
    }

    @Override
    public boolean isChanged() {
        return changeLog.isAltered();
    }

    @Override
    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    @Override
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
    }

    // ---------- getter/setter ----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public Map<String, Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Map<String, Measurement> measurements) {
        this.measurements = measurements;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }
}
