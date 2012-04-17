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
package at.tuwien.minimee.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A tool is a component to be monitored in action, such as ImageMagick 
 * or any other tool that can be used for conversion. 
 * @author cbu
 */
public class Tool {
    private String name;
    /**
     * default initialisation dir that can be overwritten per toolconfig
     * {@link ToolConfig#getInitialisationDir()}
     */
    private String initialisationDir;
    
    private String identifier;

    
    private List<ToolConfig> configs = new ArrayList<ToolConfig>();
    /**
     * full path to executable
     */
    private String executablePath;
    
    public void addConfig(ToolConfig config) {
        configs.add(config);
        config.setTool(this);
    }

    public List<ToolConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ToolConfig> configs) {
        this.configs = configs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(String executablePath) {
        this.executablePath = executablePath;
    }

    public String getInitialisationDir() {
        return initialisationDir;
    }

    public void setInitialisationDir(String initialisationDir) {
        this.initialisationDir = initialisationDir;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
