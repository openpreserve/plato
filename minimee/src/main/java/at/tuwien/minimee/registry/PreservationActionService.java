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
package at.tuwien.minimee.registry;

import java.io.Serializable;
import java.util.List;

import eu.planets_project.pp.plato.model.FormatInfo;

/**
 * Models a MiniMEE preservation action service.
 * 
 * @author Michael Kraxner
 *
 */
public class PreservationActionService implements Serializable {

    private static final long serialVersionUID = -2302709960282587765L;
    
    /** 
     * service name 
     */
    private String name;
    

    public String getDescriptor() {
        return descriptor;
    }
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
    /** 
     * service name 
     */
    private String descriptor;
    
    /** 
     * textual description of the service
     */
    private String description;
    /** 
     * A list of accepted source formats
     */
    private List<FormatInfo> sourceFormats;
    /** 
     * Target format (only for migration services)
     */
    private FormatInfo targetFormat;
    /** 
     * A list of URLs to web-sites which provide with more information
     */  
    private List<String> externalInfo;
    /**
     * Address of the webservice
     */
    private String url;
    /**
     * Tells, if this service is a migration or emulation service
     */
    private boolean migration = true;
    
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public boolean isMigration() {
        return migration;
    }
    public void setMigration(boolean migration) {
        this.migration = migration;
    }
    public List<FormatInfo> getSourceFormats() {
        return sourceFormats;
    }
    public void setSourceFormats(List<FormatInfo> sourceFormats) {
        this.sourceFormats = sourceFormats;
    }
    public FormatInfo getTargetFormat() {
        return targetFormat;
    }
    public void setTargetFormat(FormatInfo targetFormat) {
        this.targetFormat = targetFormat;
    }
    public List<String> getExternalInfo() {
        return externalInfo;
    }
    public void setExternalInfo(List<String> externalInformation) {
        this.externalInfo = externalInformation;
    }    

}
