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
