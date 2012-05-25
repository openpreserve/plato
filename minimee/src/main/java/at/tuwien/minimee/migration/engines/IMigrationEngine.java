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

import java.util.List;

import at.tuwien.minimee.model.ToolConfig;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.MeasurableProperty;

/**
 * generic interface for a migration engine, to be implemented by any engine.
 * @author cbu
 *
 */
public interface IMigrationEngine {
    /**
     * migrates a bytestream
     * @param data bytestream to be converted
     * @param toolID identifier pointing to a {@link ToolConfig} that specifies component+configuration to be used for migration
     * @param params to be passed onto the migration tool
     * @return {@link MigrationResult}
     */
    public MigrationResult migrate(byte[] data, String toolID, String params);
    
    /**
     * like {@link #migrate(byte[], String, String)} but returns a boolean
     * success indicator and takes a {@link ToolConfig} instead of the identifier
     * @param data
     * @param config {@link ToolConfig} to be used
     * @param params
     * @param result
     * @return true if the migration was succesful
     */
    public boolean migrate(byte[] data, ToolConfig config, String params,
            MigrationResult result);
    
    /**
     * self description: which properties am I able to measure?
     * @return
     */
    public List<MeasurableProperty> getMeasurableProperties();
    
    public String getName();
    
    /**
     * returns the name of the machine on which I am deployed.
     * @return machine identifier
     */
    public String getMachine();
}
