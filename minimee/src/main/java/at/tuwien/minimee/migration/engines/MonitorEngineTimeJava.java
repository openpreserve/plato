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

import at.tuwien.minimee.model.ToolConfig;

/**
 * This migration engine uses the Unix tool <em>time</em> to monitor
 * native Java migration processes on Unix/Linux environments.
 * @see MonitorEngineTime
*/
public class MonitorEngineTimeJava extends MonitorEngineTime {

    @Override
    protected String prepareCommand(ToolConfig config, String params, 
            String inputFile, String outputFile, long time) throws Exception {

        String outputTimeFile=outputFile+".time";
        
        // it's important to not just call 'time' as the shell then takes
        // its own implementation of time. we have to call /usr/bin/time
        String timeCommand = "/usr/bin/time -f \"pCpu:%P,sys:%S,user:%U,real:%e,exit:%x\" -o " + outputTimeFile;
        
        String command = timeCommand + " java -jar " + config.getTool().getExecutablePath() + " " + config.getParams() + " "+ inputFile + " " + outputFile;
        
        // we divert the output 
        // command += (" >> " + outputTimeFile);
        
        return command;
    }

}
