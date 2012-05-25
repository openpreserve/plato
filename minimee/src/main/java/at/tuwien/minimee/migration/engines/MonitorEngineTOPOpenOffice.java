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
