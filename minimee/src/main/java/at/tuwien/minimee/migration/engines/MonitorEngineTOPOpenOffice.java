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

import at.tuwien.minimee.migration.runners.IRunner;
import at.tuwien.minimee.migration.runners.SingletonRunner;
import at.tuwien.minimee.model.ToolConfig;

/**
 * This engine uses the unix tool TOP to monitor OpenOffice migration
 * processes on Linux/Unix environments.
 * It should be possible to adapt this to comparable C/S migration architectures
 */
public class MonitorEngineTOPOpenOffice extends MonitorEngineTOPDefault {

    private String monitorScript = "topmonitoropenoffice.sh";
    
    protected String prepareMonitoringCommand(long time) {
        return makeWorkingDirName(time) + "/" + monitorScript + " " + makeWorkingDirName(time);
    }
    
    @Override
    protected String prepareWorkingDirectory (long time) throws Exception {
        String workingDirectory = super.prepareWorkingDirectory(time);
        
        //
        // copy script files
        //
        
        String from, to;
        
        //
        // copy script: monitorcall.sh
        //
        from = "data/scripts/" + monitorScript; 
        to = workingDirectory + "/" + monitorScript;

        copyFile(from, to, workingDirectory);
        
        //
        // copy script: monitorcall.sh
        //
        from = "data/scripts/OpenOfficeDocumentConverter.py"; 
        to = workingDirectory + "/OpenOfficeDocumentConverter.py";

        copyFile(from, to, workingDirectory);
        
        //
        // copy script: monitorcall.sh
        //
        from = "data/scripts/openOfficeConvert.sh"; 
        to = workingDirectory + "/openOfficeConvert.sh";

        copyFile(from, to, workingDirectory);        
        return workingDirectory;
    }
    
    @Override
    protected String prepareCommand(ToolConfig config, String params, 
            String inputFile, String outputFile, long time) throws Exception {
        
        prepareWorkingDirectory(time);
        
        String monitoringCmd = prepareMonitoringCommand(time);
        
        String command = monitoringCmd + " " + makeWorkingDirName(time) + "/" + config.getTool().getExecutablePath() + " \"" + config.getParams() + " "+ inputFile;
        
        // SPECIAL STUFF, UNLIKELY TO REMAIN HERE:
        if (!config.isNoOutFile()) {
            command = command + " " + outputFile;
        }
        
        command += "\"";
        
        return command;
    }

    @Override
    protected IRunner makeRunner(String command, ToolConfig config) {
        
        SingletonRunner r = new SingletonRunner(config);
        r.setCommand(command);
        return r;
    }

}
