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

package eu.scape_project.planning.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
import org.slf4j.LoggerFactory;


/**
 * Definition of a preservation action service
 *
 * @author Michael Kraxner
 *
 */
@Entity
public class PreservationActionDefinition implements Serializable, ITouchable{
    private static final long serialVersionUID = 4825419755334685518L;
    
    private static String planetsTestbedLink = "";
    
    /**
     * Returns a link to an experience base - at the moment, 
     * this only works for Planets services. Generates a link to the testbed
     * showing experiment data collected by the Planets Testbed.
     * E.g. http://testbed.planets-project.eu/testbed/public/service_inspector.faces?serviceName=ImageMagickMigrate
     * @return Link to Testbed  
     */
    public String getExperienceBase() {

        if (planetsTestbedLink == null || "".equals(planetsTestbedLink)) {

            Properties platoProperties = new Properties();

            try {
                InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("plato.properties");

                platoProperties.load(in);

                planetsTestbedLink = platoProperties.getProperty("planets.testbed");
                
                in.close();

                if (planetsTestbedLink == null || "".equals(planetsTestbedLink)) {
                    return "";
                }

            } catch (IOException e) {
                LoggerFactory.getLogger(this.getClass()).error("Error retrieving plato.properties file. " + e.getMessage());
                return "";
            }
        }
        
        if (! actionIdentifier.contains("Planets")) {
            return "";
        }

        // For a Planets service the url looks like this
        // http://localhost:8080/pserv-pa-imagemagick/ImageMagickMigrate?wsdl
        
        int endIndex = url.indexOf("?wsdl");
        
        if (endIndex == -1) {
            return "";
        }
       
        int startIndex =  url.lastIndexOf("/", endIndex);
        
        if (startIndex == -1) {
            return "";
        }
        
        String actionName = url.substring(startIndex+1, endIndex);
        
        
        return planetsTestbedLink + actionName;
    }
    
    // most actiondefinitions are executable (by now)
    private boolean executable = true;
    
    
    public boolean isExecutable() {
        return executable;
    }
    public void setExecutable(boolean executable) {
        this.executable = executable;
    }
    @OneToOne(cascade=CascadeType.ALL)
    private FormatInfo targetFormatInfo;
    
    public FormatInfo getTargetFormatInfo() {
        return targetFormatInfo;
    }
    public void setTargetFormatInfo(FormatInfo targetFormatInfo) {
        this.targetFormatInfo = targetFormatInfo;
    }
    @Transient
    private boolean execute = false;

    public boolean isExecute() {
        return execute;
    }
    public void setExecute(boolean mayExecute) {
        this.execute = mayExecute;
    }
    /**
     * Short name of the alternative.
     */
    private String shortname;
    
    /**
     * references to a descriptor, which could be a PCDL, a RDF URI etc..
     * This should become a URI ASAP!
     */
    private String descriptor;

    public String getDescriptor() {
        return descriptor;
    }
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "padef_fk")
    @IndexColumn(name="indexcol",base=1)
    private List<Parameter> params = new LinkedList<Parameter>();

    /**
     * URL to the service.
     */
    @Length(max = 2000)
    @Column(length = 2000)
    private String url;

    /**
     * Additional information about the service.
     */
    @Lob
    private String info;
    
    /**
     * Information about the parameters, possible values, default values, ...
     */
    @Lob
    private String parameterInfo;

    /**
     * Action identifier which is used to find the matching stub and invoke the action.
     */
    private String actionIdentifier;

    @Transient
    private boolean selected = false;

    private String targetFormat;
    
    
    /**
     * this is only used currently by  the MM, so no need e.g. to export/import
     * This value is set by the service registry upon querying 
     */
    private boolean emulated = false;

    @Id
    @GeneratedValue
    private int id;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();
    
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
