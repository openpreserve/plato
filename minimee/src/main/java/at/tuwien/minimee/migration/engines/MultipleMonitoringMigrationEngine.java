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
package at.tuwien.minimee.migration.engines;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.minimee.registry.ToolRegistry;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Measurement;

/**
 * This engine combines the output of several other engines, i.e.
 * it calls the configured engines and returns the set of all measurements.
 * It does not perform any measurements itself 
 * @author Christoph Becker
 *
 */
public class MultipleMonitoringMigrationEngine extends MiniMeeDefaultMigrationEngine {
    private List<IMigrationEngine> engines = new ArrayList<IMigrationEngine>();
    
    private List<String> engineNames = new ArrayList<String>();
    
    public void addEngineName(String name) {
        engineNames.add(name);
    }
    
    public void initEngines() {
        for (String name: engineNames) {
                engines.add(ToolRegistry.getInstance().getEngine(name));
        }
    }

    /**
     * forwards the migration request to all configured engines
     * and collects their results.
     * Forwarding is done sequentially, one AFTER the other.
     */
    public MigrationResult migrate(byte[] data, String toolID, String params) {
        
        MigrationResult result = new MigrationResult();
        StringBuffer report = new StringBuffer();
        
        for (IMigrationEngine engine: engines) {
            // execute tool on all migration engines (ideally in parallel)
            report.append("migrating with engine "+engine.getName()+":\n");
            MigrationResult r = engine.migrate(data, toolID, params);
            report.append(r.getReport()).append("\n------------------- ------------\n");

            // get all performance data and put them together in the order the engines are defined
            for (MeasurableProperty p : engine.getMeasurableProperties()) {
                result.getMeasurements().put(p.getName(),r.getMeasurements().get(p.getName()));
            }
            for (Measurement m : r.getMeasurements().values()) {
                if (m.getProperty().getName().contains(":normalised")) {
                    result.getMeasurements().put(m.getProperty().getName(),m);
                }
            }// TODO define proper models and IDs for these measurements
            
            
            // Let's be nice for now - we can still get more defensive later on
            // and check consistency, i.e. identity of the produced byte arrays, etc.
            if (r.isSuccessful()) {
                result.setMigratedObject(r.getMigratedObject());
                result.setTargetFormat(r.getTargetFormat());
                result.setSuccessful(true);
            }
        }
        normaliseMeasurements(result, toolID);
        result.setReport(report.toString());
        return result;
    }
    
    public List<MeasurableProperty> getMeasurableProperties() {
        List<MeasurableProperty> props = new ArrayList<MeasurableProperty>();
        for (IMigrationEngine e: engines) {
            props.addAll(e.getMeasurableProperties());
        }
        return props;
        // TODO avoid duplicates!
    }
    
    
}
