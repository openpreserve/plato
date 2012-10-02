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
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

/**
 * An experiment is where all configuration is, or rather will be stored
 * concerning the actual execution of PLANETS services like PA migration
 * services, etc. At the moment it is only a textual description, but this will
 * be complimented by (probably quite complicated ;) service description objects
 * also containing parameter settings and the like.
 *
 * @author Christoph Becker
 */
@Entity
public class Experiment implements Serializable, ITouchable {

    private static final long serialVersionUID = 4935423597314567268L;

    @Id
    @GeneratedValue
    private int id;

    /**
     * standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 2000000)
    @Column(length = 2000000)
    private String description;

    @Lob
    private String settings;
    

    /**
     * Experiment result files, e.g. migration result. Each SampleObject can have one
     * result file.
     */
      @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
      @Fetch(FetchMode.SUBSELECT)
      @ForeignKey(name="FK_EXP_RESULTS")
      @Cascade(value={org.hibernate.annotations.CascadeType.ALL} )
//      @JoinTable(name="Exp_DO",
//       joinColumns = @JoinColumn(name="EXPERIMENT_ID"),
//       inverseJoinColumns = @JoinColumn(name="DIGITALOBJECT_ID"))
      private Map<SampleObject, DigitalObject> results = new HashMap<SampleObject, DigitalObject>();

    /**
     * detailed experiment info
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @ForeignKey(name="FK_EXP_DETAILEDINFO")
    @Cascade(value=org.hibernate.annotations.CascadeType.ALL)
    private Map<SampleObject, DetailedExperimentInfo> detailedInfo = new HashMap<SampleObject, DetailedExperimentInfo>();

    
    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Transient
    public String getDetailedRunDescription() {
//        if(runDescription==null) {
//            return null;
//        }
//        
//        if(runDescription.length() != 0) {
//            return runDescription;
//        }
        
        StringBuffer returnCompleteOutput=new StringBuffer();
        for(DetailedExperimentInfo info : detailedInfo.values()){
            returnCompleteOutput.append(" - ").append(info.getProgramOutput()+"\r\n");
        }
        return returnCompleteOutput.toString();       
    }
    
/**
 * Get the runDescription if initialized: it means that some errors have been thrown,
 * else returns all programOutputs of all samplerecords of this experiment
 *  
 * @author riccardo
 * @return 
 */
//    public String getRunDescription() {
//        
//        return runDescription;
//    }
//
//    public void setRunDescription(String runDescription) {
//        this.runDescription = runDescription;
//    }

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
        for (DigitalObject u : results.values()){
            u.handleChanges(h);
        }
    }

    public void removeRecord(int i){
        this.results.remove(i);
    }

    public void addRecord(SampleObject record){
        this.results.put(record, new DigitalObject());
    }

    public Map<SampleObject, DigitalObject> getResults() {
        return results;
    }

    public void setResults(Map<SampleObject, DigitalObject> uploads) {
        this.results = uploads;
    }

    public boolean containsUpload(SampleObject record){
        return results.containsKey(record);
    }

    /**
     * Checks if any Sample Record has an upload
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

    public Map<SampleObject, DetailedExperimentInfo> getDetailedInfo() {
        return detailedInfo;
    }

    public void setDetailedInfo(
            Map<SampleObject, DetailedExperimentInfo> detailedInfo) {
        this.detailedInfo = detailedInfo;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }
}
