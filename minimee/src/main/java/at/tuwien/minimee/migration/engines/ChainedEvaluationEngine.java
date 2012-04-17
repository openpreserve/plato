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

import eu.planets_project.pp.plato.model.beans.MigrationResult;
import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.registry.ToolRegistry;

/**
 * this is not used at the moment anymore, maybe in the future though.
 * 
 * @author Christoph Becker
 *
 */
public abstract class ChainedEvaluationEngine extends MiniMeeDefaultMigrationEngine
implements IMigrationEngine {
    private IMigrationEngine nextEngine;
    private String nextEngineName;
    
    public void initEngines() {
         nextEngine = ToolRegistry.getInstance().getEngine(nextEngineName);
    }
    
    public IMigrationEngine getNextEngine() {
        return nextEngine;
    }

    public void setNextEngine(IMigrationEngine nextEngine) {
        this.nextEngine = nextEngine;
    }

    public String getNextEngineName() {
        return nextEngineName;
    }

    public void setNextEngineName(String nextEngineName) {
        this.nextEngineName = nextEngineName;
    }

    @Override
    protected void collectData(ToolConfig config, long time, MigrationResult result) {
        super.collectData(config, time, result);
        evaluate(config,time,result);
    }
    
    @Override
    public boolean migrate(byte[] data, ToolConfig config, String params, MigrationResult result) {
        return nextEngine.migrate(data, config, params, result);
    }    
 
    protected abstract void evaluate(ToolConfig config,long time, MigrationResult r);


}
