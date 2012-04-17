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
package at.tuwien.minimee;

import at.tuwien.minimee.migration.MigrationService;
import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.registry.ToolRegistry;

/**
 * This is the generic class for action services in MiniMEE -
 * @see MigrationService
 * @author cbu
 *
 */
public class ActionService {
    /**
     * @param toolID
     */
    protected ToolConfig getToolConfig(String toolID) {
        String toolIdentifier = ToolRegistry.getToolKey(toolID);
        return ToolRegistry.getInstance().getToolConfig(toolIdentifier);
    }

 
}
