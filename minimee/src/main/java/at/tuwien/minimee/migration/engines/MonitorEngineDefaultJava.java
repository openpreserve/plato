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
 * default engine measuring time, but for executing java programs.
 * @author cbu
 * @see MiniMeeDefaultMigrationEngine
 */
public class MonitorEngineDefaultJava extends MiniMeeDefaultMigrationEngine {
@Override
protected String prepareCommand(ToolConfig config, String params, String inputFile, String outputFile, long time) throws Exception {
    return "java -jar "+super.prepareCommand(config, params, inputFile, outputFile, time);
}
}
