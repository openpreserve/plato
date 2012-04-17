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
package at.tuwien.minimee.migration.runners;

import at.tuwien.minimee.model.ToolConfig;

public class SingletonRunner extends DefaultRunner {
    
    private ToolConfig config;

    public SingletonRunner(ToolConfig config) {
        this.config = config;
    }
    
    @Override
    public RunInfo run()  {

        synchronized (config) {

            return super.run();
            
        }
    }
}
