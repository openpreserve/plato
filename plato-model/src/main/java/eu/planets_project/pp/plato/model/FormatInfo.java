/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * This class provides information about the format of a specific
 * {@link eu.planets_project.pp.plato.model.SampleObject}.
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
