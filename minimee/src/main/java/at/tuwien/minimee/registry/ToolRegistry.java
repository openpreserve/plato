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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.migration.engines.IMigrationEngine;
import at.tuwien.minimee.migration.engines.MultipleMonitoringMigrationEngine;
import at.tuwien.minimee.migration.evaluators.IMinimeeEvaluator;
import at.tuwien.minimee.model.ExperienceBase;
import at.tuwien.minimee.model.Machine;
import at.tuwien.minimee.model.Tool;
import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.registry.xml.EngineFactory;
import at.tuwien.minimee.registry.xml.EvaluatorFactory;
import at.tuwien.minimee.util.StrictErrorHandler;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.measurement.ToolExperience;
import eu.scape_project.planning.model.values.INumericValue;

/**
 * This is the internal side of MiniMEE - it contains all tools and
 * configurations that are exposed as services through {@link MiniMeeRegistry}
 * 
 * @author cbu
 * 
 */
public class ToolRegistry {
    private static Logger log = LoggerFactory.getLogger(ToolRegistry.class);

    private HashMap<String, ToolConfig> allToolConfigs = new HashMap<String, ToolConfig>();
    private HashMap<String, IMigrationEngine> allEngines = new HashMap<String, IMigrationEngine>();
    private HashMap<String, Machine> allMachines = new HashMap<String, Machine>();
    private HashMap<String, IMinimeeEvaluator> allEvaluators = new HashMap<String, IMinimeeEvaluator>();

    private HashMap<String, ToolConfig> benchmarkConfigs = new HashMap<String, ToolConfig>();

    private List<Long> timepads = new LinkedList<Long>();
    private ExperienceBase eb = new ExperienceBase();

    private double benchmarkScore = 1.0;

    private List<Tool> tools = new ArrayList<Tool>();

    private static ToolRegistry me;


    private ToolRegistry(String configFile, boolean useAbsoluteLoader) {
        load(configFile, useAbsoluteLoader);
    }

    private ToolRegistry(String configFile) throws IllegalArgumentException {
        load(configFile, true);
    }


    public static ToolRegistry getInstance() {
        // me = null;
        if (me == null) {
            me = new ToolRegistry("data/services/default-tool-config.xml");
            me.init();
        }
        return me;
    }

    private void init() {
        for (Tool t : tools) {
            for (ToolConfig c : t.getConfigs()) {
                allToolConfigs.put(c.getUrl(), c);
                if (c.isBenchmark()) {
                    benchmarkConfigs.put(c.getUrl(), c);
                }
            }
        }
        for (IMigrationEngine e : allEngines.values()) {
            if (e instanceof MultipleMonitoringMigrationEngine) {
                ((MultipleMonitoringMigrationEngine) e).initEngines();
            }
        }
        List<Measure> props = me.getMeasures();
        log.info("MINIMEE: loaded tools, configs, engines, machines, measurableProperties: " + tools.size()
            + " tools, " + allToolConfigs.size() + " configs, " + benchmarkConfigs.size()
            + " of which are used for the benchmark, " + allEngines.size() + " engines, " + allMachines.size()
            + " machines, " + props.size() + " properties.");
        log.debug("Listing measurable properties...");
        for (Measure p : props) {
            log.info(p.getName() + " - " + p.getUri());
        }

    }

    public static void reload() {
        reload("data/services/default-tool-config.xml", true);
    }

    public static void reload(String absolutePath) {
        reload(absolutePath, false);
    }

    public static void reload(String localPath, boolean loadFromResources) {
        me = new ToolRegistry(localPath, loadFromResources);
        me.init();
    }


    public void addTool(Tool t) {
        tools.add(t);
    }

    private void load(String configFile, boolean loadFromResources) throws IllegalArgumentException {
        if (loadFromResources) {
            load(Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile));
        } else {
            InputStream in;
            try {
                in = new FileInputStream(new File(configFile));
                load(in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e.getMessage() + " - maybe caused by wrong filename? check: "
                    + configFile);
            }
        }
    }

    private void load(InputStream config) throws IllegalArgumentException {
        Digester digester = new Digester();
        digester.setValidating(true);
        digester.setErrorHandler(new StrictErrorHandler());

        digester.push(this);
        digester.addObjectCreate("*/tool", Tool.class);
        digester.addSetProperties("*/tool");

        digester.addObjectCreate("*/config", ToolConfig.class);
        digester.addSetProperties("*/config");
        digester.addBeanPropertySetter("*/config/name", "name");
        digester.addBeanPropertySetter("*/config/executablePath", "executablePath");
        digester.addBeanPropertySetter("*/config/engineName", "engine");
        digester.addBeanPropertySetter("*/config/params", "params");
        digester.addBeanPropertySetter("*/config/inEnding", "inEnding");
        digester.addBeanPropertySetter("*/config/outEnding", "outEnding");
        digester.addBeanPropertySetter("*/config/noOutFile", "noOutFile");
        digester.addBeanPropertySetter("*/config/initialisationDir", "initialisationDir");
        digester.addCallMethod("*/config/evaluators/evaluator", "addEvaluator", 0);

        digester.addSetNext("*/config", "addConfig");
        digester.addSetNext("*/tool", "addTool");

        digester.addFactoryCreate("*/evaluators/evaluator", EvaluatorFactory.class);
        digester.addSetProperties("*/evaluators/evaluator");
        digester.addSetNext("*/evaluators/evaluator", "addEvaluator");

        digester.addFactoryCreate("*/engine", EngineFactory.class);
        digester.addSetProperties("*/engine");
        digester.addObjectCreate("*/measure", Measure.class);
        digester.addSetProperties("*/measure", "ID", "uri");
        digester.addSetProperties("*/measure", "name", "name");

        digester.addSetNext("*/measure", "addMeasure");
        digester.addCallMethod("*/includedEngine", "addEngineName", 0);
        digester.addCallMethod("*/nextEngine", "setNextEngineName", 0);

        digester.addSetNext("*/engine", "addEngine");

        digester.addObjectCreate("*/machine", Machine.class);
        digester.addSetProperties("*/machine");
        digester.addSetNext("*/machine", "addMachine");
        try {

            digester.setUseContextClassLoader(true);
            digester.setValidating(false);
            digester.parse(config);

            // config =
            // Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);
            // engineDigester.setValidating(false);
            // engineDigester.setUseContextClassLoader(true);
            // engineDigester.parse(config);

        } catch (Exception e) {
            log.error("Error in config file! ", e);
        }
    }

    public List<Tool> getTools() {
        return tools;
    }

    public List<IMigrationEngine> getMigrationEngines() {
        return null;
    }

    public void addMachine(Machine m) {
        allMachines.put(m.getId(), m);
    }

    public void addEngine(IMigrationEngine e) {
        allEngines.put(e.getName(), e);
    }

    public void addEvaluator(IMinimeeEvaluator e) {
        allEvaluators.put(e.getName(), e);
    }

    public HashMap<String, IMigrationEngine> getAllEngines() {
        return allEngines;
    }

    public void setAllEngines(HashMap<String, IMigrationEngine> allEngines) {
        this.allEngines = allEngines;
    }

    public HashMap<String, ToolConfig> getAllToolConfigs() {
        return allToolConfigs;
    }

    public void setAllToolConfigs(HashMap<String, ToolConfig> allTools) {
        this.allToolConfigs = allTools;
    }

    public ToolConfig getToolConfig(String id) {
        return allToolConfigs.get(id);
    }

    public IMigrationEngine getEngine(String name) {
        return allEngines.get(name);
    }

    public Machine getMachine(String name) {
        return allMachines.get(name);
    }

    public IMinimeeEvaluator getEvaluator(String name) {
        return allEvaluators.get(name);
    }

    public List<Measure> getMeasures() {
        List<Measure> l = new ArrayList<Measure>();
        for (IMigrationEngine e : allEngines.values()) {
            for (Measure p : e.getMeasures()) {
                if (!l.contains(p)) {
                    l.add(p);
                }
            }
        }
        return l;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public double getBenchmarkScore() {
        return benchmarkScore;
    }

    public void setBenchmarkScore(double benchmarkScore) {
        this.benchmarkScore = benchmarkScore;
    }

    public ExperienceBase getEb() {
        return eb;
    }
    public void addTimePad(long time) {
        timepads.add(time);
    }
    
    /**
     * @param toolID
     * @return
     */
    public static String getToolKey(String toolID) {
        String key = "minimee/";
        String toolIdentifier = toolID.substring(toolID.indexOf(key) + key.length());
        return toolIdentifier;
    }
    
    /**
     * checks the timepad for being valid - if so, the measurement is added to
     * the experience base. Currently not exposed as a web service since minimee
     * has been integrated with Plato.
     */
    public synchronized boolean addExperience(long otp, String toolID, Measurement m) {
        if (!timepads.contains(otp)) {
            return false;
        } else {
            addExperience(toolID, m);
        }
        
        return true;
    }
    
    public Measurement addExperience(String config, Measurement m) {
        eb.addExperience(config, m);
        return eb.getAverage(config, m);
    }
    
    public double calculateBenchmarkScore() {
        if (benchmarkConfigs.size() == 0)
            return 1.0;
        double score = 0.0;
        for (String config : benchmarkConfigs.keySet()) {
            ToolExperience ex = eb.getToolExperience(config);

            Measurement m = ex.getAverage(MigrationResult.MIGRES_ELAPSED_TIME_PER_MB);
            if (m != null) {
                INumericValue v = (INumericValue) m.getValue();
                score += v.value();
            }
        }
        return score / benchmarkConfigs.size() / 4000;
    }    
}