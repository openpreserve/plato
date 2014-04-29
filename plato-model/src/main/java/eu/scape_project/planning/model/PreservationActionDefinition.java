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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.constraints.Length;

/**
 * Definition of a preservation action.
 * 
 * @author Michael Kraxner
 */
@Entity
public class PreservationActionDefinition implements Serializable, ITouchable {
    private static final long serialVersionUID = 4825419755334685518L;

    @Id
    @GeneratedValue
    private int id;

    /**
     * Short name of the alternative.
     */
    private String shortname;

    /**
     * references to a descriptor, which could be a PCDL, a RDF URI etc.. This
     * should become a URI ASAP!
     */
    private String descriptor;

    /**
     * Additional information about the service.
     */
    @Lob
    private String info;

    /**
     * Action identifier which is used to find the matching stub and invoke the
     * action.
     */
    private String actionIdentifier;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "padef_fk")
    @IndexColumn(name = "indexcol", base = 1)
    private List<Parameter> params = new LinkedList<Parameter>();

    /**
     * Information about the parameters, possible values, default values, ...
     */
    @Lob
    private String parameterInfo;

    /**
     * URL to the service.
     */
    @Length(max = 2000)
    @Column(length = 2000)
    private String url;

    @OneToOne(cascade = CascadeType.ALL)
    private FormatInfo targetFormatInfo;

    private String targetFormat;

    /**
     * This is currently only used by the MM, so no need e.g. to export/import
     * This value is set by the service registry upon querying
     */
    private boolean emulated = false;

    // most actiondefinitions are executable (by now)
    private boolean executable = true;

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    @Transient
    private boolean selected = false;

    @Transient
    private boolean execute = false;

    /**
     * Returns a link to an experience base TODO experience base need to be
     * defined first
     */
    public String getExperienceBase() {
        return null;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public FormatInfo getTargetFormatInfo() {
        return targetFormatInfo;
    }

    public void setTargetFormatInfo(FormatInfo targetFormatInfo) {
        this.targetFormatInfo = targetFormatInfo;
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean mayExecute) {
        this.execute = mayExecute;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public List<Parameter> getParams() {
        return params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }

    public void invoke() {
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getActionIdentifier() {
        return actionIdentifier;
    }

    public void setActionIdentifier(String actionIdentifier) {
        this.actionIdentifier = actionIdentifier;
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
    }

    public String getParamByName(String name) {
        for (Parameter param : params) {
            if (name.equals(param.getName()))
                return param.getValue();
        }
        return null;
    }

    public void setParamByName(String name, String value) {
        Parameter p = null;
        for (Parameter param : params) {
            if (name.equals(param.getName()))
                p = param;
        }
        if (p == null) {
            params.add(new Parameter(name, value));
        } else {
            p.setValue(value);
        }

    }

    public boolean isEmulated() {
        return emulated;
    }

    public void setEmulated(boolean emulation) {
        this.emulated = emulation;
    }

    public String getParameterInfo() {
        return parameterInfo;
    }

    public void setParameterInfo(String parameterInfo) {
        this.parameterInfo = parameterInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
