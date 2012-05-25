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
 * A tool configuration for a {@link Tool} models a specific way of calling it -
 * e.g. calling ImageMagick to convert an image to uncompressed TIFF.
 * @author cbu
 *
 */
public class ToolConfig {
    private String url;
    private Tool tool;
    private List<String> evaluators = new ArrayList<String>();
    
    /**
     * ID of the engine to be used for executing this tool
     */
    private String engine = "default";
    
    public void addEvaluator(String evaluator) {
        evaluators.add(evaluator);
    }
    
    /**
     * denotes whether this config is used for benchmarking
     */
    private boolean benchmark = false;
    
    /**
     * path to a directory containing files for testing this configuration
     */
    private String initialisationDir;
    
    /**
     * a name of the tool (not really used, rather for logging)
     */
    private String name;
    /**
     * call params to include (e.g. commandline options)
     */
    private String params;

    /**
     * specific file ending to apply to the temporary input file 
     * (needed by some migration tools)
     */
    private String inEnding;
    
    /**
     * file ending that the migration tool will apply to its output file
     * (some do that)
     */
    private String outEnding;
    
    /**
     * if that is true, we shall not include a filename for the out file in 
     * the call params (some tools are stubborn and just write the input file
     * name with a different ending)
     */
    private boolean noOutFile = false;
    
    public boolean isNoOutFile() {
        return noOutFile;
    }
    public void setNoOutFile(boolean noOutFile) {
        this.noOutFile = noOutFile;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String id) {
        this.url = id;
    }
    public String getParams() {
        return params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    public String getName() {
        return name;
    }
    public void setName(String toolname) {
        this.name = toolname;
    }
    public String getInEnding() {
        return inEnding;
    }
    public void setInEnding(String inEnding) {
        this.inEnding = inEnding;
    }
    public String getOutEnding() {
        return outEnding;
    }
    public void setOutEnding(String outEnding) {
        this.outEnding = outEnding;
    }
    public String getEngine() {
        return engine;
    }
    public void setEngine(String engine) {
        this.engine = engine;
    }
    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool) {
        this.tool = tool;
    }

    /**
     * @return my init dir or, if that is not set, the 
     * one of my {@link Tool}
     */
    public String getInitialisationDir() {
        return (initialisationDir == null || "".equals(initialisationDir))
        ? tool.getInitialisationDir()
        : initialisationDir;
    }
    public void setInitialisationDir(String initialisationDir) {
        this.initialisationDir = initialisationDir;
    }
    public boolean isBenchmark() {
        return benchmark;
    }
    public void setBenchmark(boolean benchmark) {
        this.benchmark = benchmark;
    }
    public List<String> getEvaluators() {
        return evaluators;
    }
    public void setEvaluators(List<String> evaluators) {
        this.evaluators = evaluators;
    }
}
