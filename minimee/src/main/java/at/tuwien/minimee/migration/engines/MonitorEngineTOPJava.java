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
