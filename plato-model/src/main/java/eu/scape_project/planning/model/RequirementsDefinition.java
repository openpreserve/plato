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
import org.hibernate.annotations.IndexColumn;

/**
 * This class encapsulates properties set in the workflow step 'Identify Requirements'.
 *
 * @author Hannes Kulovits
 */
@Entity
public class RequirementsDefinition implements Serializable, ITouchable {

    private static final long serialVersionUID = 2040908987106786691L;

    @Id @GeneratedValue
    private int id;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String description;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    /**
     * List of files the user can attach to the workflow step 'Identify Requirements'.
     */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @IndexColumn(name="indexcol",base=1)
    private List<DigitalObject> uploads = new ArrayList<DigitalObject>();

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

    public List<DigitalObject> getUploads() {
        return uploads;
    }

    public void setUploads(List<DigitalObject> uploads) {
        this.uploads = uploads;
    }

    public boolean isChanged(){
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
        
        for (DigitalObject u : uploads) {
            u.handleChanges(h);
        }
    }
}
