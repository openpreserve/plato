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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.migration.parser.HPROF_Parser;
import at.tuwien.minimee.model.ToolConfig;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.values.PositiveFloatValue;

/**
 * This engine uses the HPROF profiler to monitor native Java 
 * migration processes
 * @author cbu
 *
 */
public class MonitorEngineHPROF extends MiniMeeDefaultMigrationEngine {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    protected void cleanup(long time, String inputFile, String outputFile) {
        super.cleanup(time, inputFile, outputFile);
        new File(outputFile+".hprof").delete();
    }
    
    @Override
    protected String prepareCommand(ToolConfig config, String params, String inputFile, String outputFile, long time) throws Exception {
        String outputTXTFile=outputFile+".hprof ";
        String monitoringCmd="java -agentlib:hprof=format=a,verbose=n,heap=sites,depth=0,file="+outputTXTFile+" -jar ";
        String command = monitoringCmd + " " + config.getTool().getExecutablePath() + " " + config.getParams() + " "+ inputFile + " " + outputFile;
        log.info("Command HPROF: "+command);
        return command;
    }
    
    @Override
    protected void collectData(ToolConfig config, long time, MigrationResult result) {
        HPROF_Parser p = new HPROF_Parser();
        p.parse(makeOutputFilename(config, time)+".hprof");
        
        for (MeasurableProperty property: getMeasurableProperties()) {
            Measurement m = new Measurement();
            m.setProperty(property);
            PositiveFloatValue v = (PositiveFloatValue) property.getScale().createValue();
            
//            if (property.getName().equals(MigrationResult.MIGRES_MEMORY_GROSS)) {
//                v.setValue(p.getTotal_allocated());
//            } 
//            if (property.getName().equals(MigrationResult.MIGRES_MEMORY_NET)) {
//                v.setValue(p.getTotal_virtual());
//            } 

            if (property.getName().equals(MigrationResult.MIGRES_MEMORY_GROSS)) {
                v.setValue(p.getTotal_allocated());
            } 

            /**
             * this is NOT the total virtual memory used during execution
             * it's the virtual memory still allocated when HProf collects information
             * - if garbage collector was called, this value is lower than the actual v-memory consumption 
             */
            if (property.getName().equals("performance:totalVirtualMemory")) {
                v.setValue(p.getTotal_virtual());
            } 
            if (property.getName().equals("performance:totalAllocatedMemory")) {
                v.setValue(p.getTotal_allocated());
            }
            m.setValue(v);
            result.getMeasurements().put(property.getName(), m);
        }
    }
}
