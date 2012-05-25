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
 ******************************************************************************/
package at.tuwien.minimee.registry;

import java.io.Serializable;
import java.util.List;

import eu.scape_project.planning.model.FormatInfo;

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
