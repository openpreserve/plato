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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.digester3.Digester;
import org.apache.commons.lang.StringUtils;
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
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.MeasureConstants;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.measurement.ToolExperience;
import eu.scape_project.planning.model.values.INumericValue;
import eu.scape_project.planning.utils.ConfigurationLoader;

/**
 * This is the internal side of MiniMEE - it contains all tools and
 * configurations that are exposed as services through {@link MiniMeeRegistry}
 * 
 * @author cbu
 * 
 */
public class ToolRegistry {
    private static Logger log = LoggerFactory.getLogger(ToolRegistry.class);
    
    private static final String MINIMEE_HOME = "minimee.home"; 
    private static final String TOOL_CONFIG = "tool-config.xml";

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


    private ToolRegistry() {
    }

    public static ToolRegistry getInstance() {
        // me = null;
        if (me == null) {
            me = new ToolRegistry();
            me.reload();
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

    public void addTool(Tool t) {
        tools.add(t);
    }

    private void reload() {
        clear();
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.load();
        if (configuration == null) {
            log.error("An error occurred while reading the properties file.");
            return;
        }
        String home = configuration.getString(MINIMEE_HOME);
        if (StringUtils.isEmpty(home)) {
            log.error("minimee.home is not defined. cannot initialize ToolRegistry.");
            return;
        }
        File configFile = new File(home + File.separator + TOOL_CONFIG);
        if (!configFile.exists()) {
            log.error("Could not find " + configFile.getAbsolutePath() + ". Cannot initialize ToolRegistry.");
        } else {
            try {
                parseConfig(new FileInputStream(configFile));
                init();
            } catch (Exception e) {
                log.error("Failed to reload minimee-config.", e);
            }
        }
    }
    
    private void parseConfig(InputStream config) throws IllegalArgumentException {
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

            Measurement m = ex.getAverage(MeasureConstants.ELAPSED_TIME_PER_MB);
            if (m != null) {
                INumericValue v = (INumericValue) m.getValue();
                score += v.value();
            }
        }
        return score / benchmarkConfigs.size() / 4000;
    }
    private void clear(){
        allToolConfigs.clear();
        allEngines.clear();
        allMachines.clear();
        allEvaluators.clear();
        benchmarkConfigs.clear();
        timepads.clear();
        tools.clear();
        eb = new ExperienceBase();
    }
    
}