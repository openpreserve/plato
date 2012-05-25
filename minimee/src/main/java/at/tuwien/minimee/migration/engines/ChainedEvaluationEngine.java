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

import eu.scape_project.planning.model.beans.MigrationResult;
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
