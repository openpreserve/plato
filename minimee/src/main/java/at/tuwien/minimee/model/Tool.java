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
