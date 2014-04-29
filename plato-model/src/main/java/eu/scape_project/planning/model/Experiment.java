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
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;

/**
 * An experiment is where all configuration is stored.
 * 
 * @author Christoph Becker
 */
@Entity
public class Experiment implements Serializable, ITouchable {

    private static final long serialVersionUID = 4935423597314567268L;

    @Id
    @GeneratedValue
    private int id;

    @Lob
    private String description;

    @Lob
    private String settings;

    @OneToOne(cascade = CascadeType.ALL)
    private DigitalObject workflow;

    private String workflowUri;

    /**
     * Experiment result files, e.g. migration result. Each SampleObject can
     * have one result file.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Experiment_Result")
    @ForeignKey(name = "FK_EXP_RESULTS")
    @Fetch(FetchMode.SUBSELECT)
    private Map<SampleObject, DigitalObject> results = new HashMap<SampleObject, DigitalObject>();

    /**
     * Detailed experiment info
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Experiment_DetailedInfo")
    @ForeignKey(name = "FK_EXP_DETAILEDINFOS")
    @Fetch(FetchMode.SUBSELECT)
    private Map<SampleObject, DetailedExperimentInfo> detailedInfo = new HashMap<SampleObject, DetailedExperimentInfo>();

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    /**
     * Creates a description of the experiment output.
     * 
     * @return the output description
     */
    @Transient
    public String getDetailedRunDescription() {
        StringBuffer returnCompleteOutput = new StringBuffer();
        for (DetailedExperimentInfo info : detailedInfo.values()) {
            returnCompleteOutput.append(" - ").append(info.getProgramOutput() + "\r\n");
        }
        return returnCompleteOutput.toString();
    }

    /**
     * @see ITouchable#isChanged()
     */
    @Override
    public boolean isChanged() {
        return changeLog.isAltered();
    }

    /**
     * @see ITouchable#touch()
     */
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
        for (DigitalObject u : results.values()) {
            u.handleChanges(h);
        }
    }

    /**
     * @see ITouchable#getChangeLog()
     */
    @Override
    public ChangeLog getChangeLog() {
        return changeLog;
    }

    /**
     * Removes the result record with the provided index.
     * 
     * @param i
     *            index of the record to remove.
     */
    public void removeRecord(int i) {
        this.results.remove(i);
    }

    /**
     * Adds a result record.
     * 
     * @param record
     *            the record to add
     */
    public void addRecord(SampleObject record) {
        this.results.put(record, new DigitalObject());
    }

    /**
     * Checks if this experiment contains the provided record.
     * 
     * @param record
     *            the record to search
     * @return true if the record is in this experiment, false otherwise
     */
    public boolean containsUpload(SampleObject record) {
        return results.containsKey(record);
    }

    /**
     * Checks if any Sample Record has an upload
     * 
     * @return true if any record has an upload
     */
    public boolean isRecordUploaded() {
        boolean existent = false;
        for (DigitalObject upload : results.values()) {
            if (!upload.getFullname().equals("")) {
                existent = true;
                break;
            }
        }
        return existent;
    }

    // ********** getter/setter **********
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public DigitalObject getWorkflow() {
        return workflow;
    }

    public void setWorkflow(DigitalObject workflow) {
        this.workflow = workflow;
    }

    public String getWorkflowUri() {
        return workflowUri;
    }

    public void setWorkflowUrl(String workflowUri) {
        this.workflowUri = workflowUri;
    }

    public Map<SampleObject, DigitalObject> getResults() {
        return results;
    }

    public void setResults(Map<SampleObject, DigitalObject> uploads) {
        this.results = uploads;
    }

    public Map<SampleObject, DetailedExperimentInfo> getDetailedInfo() {
        return detailedInfo;
    }

    public void setDetailedInfo(Map<SampleObject, DetailedExperimentInfo> detailedInfo) {
        this.detailedInfo = detailedInfo;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

}
