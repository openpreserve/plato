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

import java.util.List;

import at.tuwien.minimee.model.ToolConfig;
import eu.planets_project.pp.plato.model.beans.MigrationResult;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;

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
