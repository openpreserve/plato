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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * This class provides information about the format of a specific
 * {@link eu.scape_project.planning.model.SampleObject}.
 *
 * @author Hannes Kulovits
 */
@Entity
public class FormatInfo implements Serializable, ITouchable {

    private static final long serialVersionUID = 5077497602212637772L;

    @Id
    @GeneratedValue
    private int id;

    /**
     * PRONOM-UID
     */
    private String puid;

    /**
     * The human readable name of this file format
     */
    private String name;

    /**
     * Each format has a specific format, e.g. 1.4 for PDF.
     */
    private String version;

    /**
     * Mime-type of the file.
     */
    private String mimeType;

    /**
     * Default extension of the format, e.g. pdf.
     */
    private String defaultExtension;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();
    
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    @Transient
    public String getLongName() {
        if ((name != null) &&
            (!"".equals(name))) {
            String versionString = (version != null && !"".equals(version)) ? ", version "+version : "";
            return name + versionString;
        }
        return "";
    }

    public String getDefaultExtension() {
        return defaultExtension;
    }

    public void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }

    /**
     * assigns all values but the ID
     */
    public void assignValues(FormatInfo source){
        this.defaultExtension = source.getDefaultExtension();
        this.mimeType = source.getMimeType();
        this.name = source.getName();
        this.puid = source.getPuid();
        this.version = source.getVersion();
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

    /**
     * we need at least either the PUID or name+version.
     * If neither is available, the format is undefined.
     * @return I have a puid OR i have name+version
     */
    public boolean isDefined() {
        return (puid != null && !"".equals(puid)) ||
                !"".equals(getLongName().trim());        
    }
    
}
