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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.model.ToolConfig;

/**
 * This engine uses the unix tool TOP to monitor native Java migration
 * processes on Linux/Unix environments 
 */
public class MonitorEngineTOPJava extends MonitorEngineTOPDefault {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    protected String prepareCommand(ToolConfig config, String params, String inputFile, String outputFile, long time) throws Exception {
        
        prepareWorkingDirectory(time);
        
        String monitoringCmd = prepareMonitoringCommand(time, 60);
        
        String command = monitoringCmd + " \"java -jar " + config.getTool().getExecutablePath() + " " + config.getParams() + " "+ inputFile;
        
        // SPECIAL STUFF, UNLIKELY TO REMAIN HERE:
        if (!config.isNoOutFile()) {
            command = command + " " + outputFile;
        }
        
        command += "\"";
        
        log.debug("TOPJava MONITORING COMMAND: "+command);
        return command;        
        
    }
}
