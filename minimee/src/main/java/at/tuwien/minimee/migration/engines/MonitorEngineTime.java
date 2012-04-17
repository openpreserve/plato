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
package at.tuwien.minimee.migration.engines;

import java.io.File;

import org.slf4j.LoggerFactory;

import at.tuwien.minimee.migration.parser.TIME_Parser;
import at.tuwien.minimee.model.ToolConfig;
import eu.planets_project.pp.plato.model.beans.MigrationResult;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.Measurement;
import eu.planets_project.pp.plato.model.values.PositiveFloatValue;

/**
 * This migration engine uses the Unix tool <em>time</em> to monitor
 * migration processes on Unix/Linux environments.
 * 
 * It gets more detailed execution time information by using the linux tool
 * /usr/bin/time. 
 * 
 * Using time, we determine the following performance information:
 *
 * <ul>
 *   <li>
 *       Percentage of the CPU that this job got.  This is just user + system times 
 *       divided by the total running time. It also prints a percentage sign.
 *   </li>
 *   <li>
 *       Total number of CPU-seconds used by the system on behalf of the process 
 *       (in kernel mode), in seconds.
 *   </li>
 *   <li>
 *       Total number of CPU-seconds that the process used directly (in user mode), in seconds.
 *   </li>
 *   <li>
 *       Elapsed real (wall clock) time used by the process, in seconds.
 *   </li>
 *   <li>
 *   <li>
 *       Exit status of the command.
 *   </li>
 * </ul>
 *  
 * @author kulovits
 */
public class MonitorEngineTime extends MiniMeeDefaultMigrationEngine {
    
    @Override
    protected void cleanup(long time, String inputFile, String outputFile) {
        super.cleanup(time, inputFile, outputFile);
        new File(outputFile+".time").delete();
    }
    
    
    @Override
    protected String prepareCommand(ToolConfig config, String params, 
            String inputFile, String outputFile, long time) throws Exception {

        String outputTimeFile=outputFile+".time";
        
        // it's important to not just call 'time' as the shell then takes
        // its own implementation of time. we have to call /usr/bin/time
        String timeCommand = "/usr/bin/time -f \"pCpu:%P,sys:%S,user:%U,real:%e,exit:%x\" -o " + outputTimeFile;
        
        String command = timeCommand + " " + config.getTool().getExecutablePath() + " " + config.getParams() + " "+ inputFile;
        
        if (!config.isNoOutFile()) {
            command += (" " + outputFile);
        }
        
        return command;
    }
    
    @Override
    protected void collectData(ToolConfig config, long time, MigrationResult result) {
        
        TIME_Parser p = new TIME_Parser();
        p.parse(makeOutputFilename(config, time)+".time");
        
        String dbg = "real:" + p.getReal()+",user:"+p.getUser()+",sys:"+p.getSys();
        LoggerFactory.getLogger(this.getClass()).debug("TIME measured: " + dbg);
        
        for (MeasurableProperty property: getMeasurableProperties()) {
            Measurement m = new Measurement();
            m.setProperty(property);
            PositiveFloatValue v = (PositiveFloatValue) property.getScale().createValue();
            
            if (property.getName().equals(MigrationResult.MIGRES_USED_TIME)) {
                v.setValue((p.getUser()+p.getSys())*1000);
            }
            
            m.setValue(v);
            result.getMeasurements().put(property.getName(), m);
        }
    }    
}
