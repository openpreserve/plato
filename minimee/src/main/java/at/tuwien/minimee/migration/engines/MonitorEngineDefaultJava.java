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
