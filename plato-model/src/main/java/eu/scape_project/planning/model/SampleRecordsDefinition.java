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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * This entity bean contains all information defined in the workflow step
 * 'Define Sample Records'.
 * 
 * @author Hannes Kulovits
 */
@Entity
public class SampleRecordsDefinition implements Serializable, ITouchable {

    private static final long serialVersionUID = 2022932652305694008L;

    @Id
    @GeneratedValue
    private int id;

    /**
     * Hibernate note: standard length for a string column is 255 validation is
     * broken because we use facelet templates (issue resolved in Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String samplesDescription;

    public SampleObject getFirstSampleWithFormat() {
        for (SampleObject sample : records) {
            if (sample.isFormatDefined()) {
                return sample;
            }
        }
        return null;
    }

    /**
     * The list of representative samples.
     * 
     * Note: 
     *  - retaining the order of these samples is critical, as each value in {@link Values}
     *    correspond to the sample with the same index
     *  - Per default Hibernate uses the id of the objects to determine the position.   
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleRecordsDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(value = FetchMode.SELECT)
    private List<SampleObject> records = new ArrayList<SampleObject>();

    @OneToOne(cascade = CascadeType.ALL)
    private CollectionProfile collectionProfile = new CollectionProfile();

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    public String getSamplesDescription() {
        return samplesDescription;
    }

    public void setSamplesDescription(String samplesDescription) {
        this.samplesDescription = samplesDescription;
    }

    /**
     * Remember Hibernate might call a getter method multiple times during a
     * session. Also, make sure that a call to an accessor method couldn't do
     * anything weird ... like initialize a lazy collection or proxy. For some
     * classes it can be worthwhile to provide two get/set pairs for certain
     * properties - one pair for business code and one for Hibernate.
     */
    public List<SampleObject> getRecords() {
        return records;
    }

    public void setRecords(List<SampleObject> records) {
        this.records = records;
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

    public boolean isChanged() {
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
        // call handleChanges of all child elementss
        for (SampleObject record : records) {
            record.handleChanges(h);
        }
    }

    /**
     * Adds the given record to the list of SampleRecords. Used for importing by
     * the digester.
     * 
     * We have to ensure referential integrity!
     * 
     * @param record
     */
    public void addRecord(SampleObject record) {
        // to ensure referential integrity
        record.setSampleRecordsDefinition(this);
        records.add(record);
    }

    public void removeRecord(SampleObject record) {
        records.remove(record);
    }

    public CollectionProfile getCollectionProfile() {
        return collectionProfile;
    }

    public void setCollectionProfile(CollectionProfile collectionProfile) {
        this.collectionProfile = collectionProfile;
    }

    public String getPuids() {
        ArrayList<String> puids = new ArrayList<String>();

        for (SampleObject r : records) {

            if (r.getFormatInfo().getPuid() == null || "".equals(r.getFormatInfo().getPuid())) {
                continue;
            }

            String puid = r.getFormatInfo().getPuid();

            if (!puids.contains(puid)) {
                puids.add(puid);
            }
        }

        StringBuffer puidsBuffer = new StringBuffer();
        for (String puid : puids) {
            puidsBuffer.append(puid).append(":");
        }
        return puidsBuffer.toString();
    }
}
