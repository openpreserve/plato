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
package at.tuwien.minimee.controller;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import at.tuwien.minimee.migration.engines.IMigrationEngine;
import at.tuwien.minimee.model.Tool;
import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.registry.ToolRegistry;
import at.tuwien.minimee.util.FileUtils;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.ToolExperience;
import eu.scape_project.planning.utils.FacesMessages;

/**
 * provides administrative utilities for the MiniMEE engines and registry
 * @author Christoph Becker
 */
@Named("miniMeeAdmin")
@ApplicationScoped
@Singleton
public class MiniMeeAdminAction implements Serializable {
    private static final long serialVersionUID = -1L;

    @Inject private Logger log;

    @PersistenceContext
    private EntityManager em;

    
    private HashMap<String, String> initResults = new HashMap<String, String>();

    private List<String> configs = new ArrayList<String>();
    
	@Inject FacesMessages facesMessages;
    
	private String localPath;
	
    
    /**
     * performs a verification test that checks all configured components
     * and configurations for proper operation.
     * Output is logged, not returned anywhere else.
     */
    public void verifySetup() {
        log.info("INITIALISING AND VERIFYING TOOL REGISTRY NOW...");
        initResults.clear();
        configs.clear();

        for (Tool tool : ToolRegistry.getInstance().getTools()) {
            for (ToolConfig config : tool.getConfigs()) {
                log.info("*** CHECKING CONFIG: " + config.getUrl());
                configs.add(config.getUrl());
                String initDir = config.getInitialisationDir();

                IMigrationEngine engine = ToolRegistry.getInstance()
                        .getAllEngines().get(config.getEngine());

                if (initDir == null) {
                    log.error("No initDir for " + config.getUrl());
                    initResults.put(config.getUrl(), "no init directory specified");
                    continue;
                }
                File directory = new File(initDir);
                if (directory.isDirectory()) {
                    try {
                        for (File f : directory.listFiles()) {
                            log.debug("testing " + config.getUrl() + " with file "
                                    + f.getName());
                            byte[] data = FileUtils.getBytesFromFile(f);
                            MigrationResult r = engine.migrate(data, "minimee/"+ config.getUrl(), "");
                            if (!r.isSuccessful()) {
                                log.warn(r.getReport());
                                initResults.put(config.getUrl(), "FAILED: "
                                        + r.getReport());
                            } else {
                                initResults.put(config.getUrl(), "SUCCESS");
                            }
                        }
                    } catch (Exception e) {
                        log.error("ERROR IN CONFIG!" + e.getMessage(),e);
                        initResults.put(config.getUrl(), "FAILED: " + e.getMessage());
                    }
                } else {
                    log.warn("Init dir " + directory + " is not a directory.");
                }
            }
        }
        for (ToolConfig c: ToolRegistry.getInstance().getAllToolConfigs().values()) {
            ToolExperience ex = ToolRegistry.getInstance().getEb().getToolExperience(c.getName());
            log.debug("Startup time of " +c.getName()+": "+ex.getStartupTime());
        }
    }

    /**
     * does what it seems - it reloads the MiniMEE tool registry from
     * the configuration file
     * @link {@link ToolRegistry#reload()}
     */
    public void reloadRegistry() {
        ToolRegistry.reload();
    }


    /**
     * performs a benchmark calculation, reports it to the UI
     * and sets it in the {@link ToolRegistry}
     * {@link ToolRegistry#calculateBenchmarkScore()}
     */
    public void benchmark() {
        double score = ToolRegistry.getInstance().calculateBenchmarkScore();
        ToolRegistry.getInstance().setBenchmarkScore(score);
        facesMessages.addInfo("Ladies and gentlemen, this registry has a score of " + score);
    }

    /**
     * reloads the {@link ToolRegistry} from {@link #localPath}
     * 
     */
    public void reloadRegistryFromPath() {
        if (localPath == null || "".equals(localPath)) {
        	facesMessages.addError("Please provide a local path name to the XML file");
        } else {
            ToolRegistry.reload(localPath);
        }
    }

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public HashMap<String, String> getInitResults() {
		return initResults;
	}

	public List<String> getConfigs() {
		return configs;
	}
}
