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
package at.tuwien.minimee.migration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.ActionService;
import at.tuwien.minimee.migration.engines.IMigrationEngine;
import at.tuwien.minimee.migration.evaluators.IMinimeeEvaluator;
import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.registry.ToolRegistry;
import at.tuwien.minimee.util.OS;
import eu.planets_project.pp.plato.model.beans.MigrationResult;
import eu.planets_project.pp.plato.model.measurement.Measurement;
/**
 * Currently not exposed as a web service since miniMEE has been integrated
 * with Plato.
 * @author cb
 */
public class MigrationService extends ActionService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private HashMap<String, ToolConfig> tools = ToolRegistry.getInstance().getAllToolConfigs();

    //private static String tempDir = System.getProperty("java.io.tmpdir").replace('\\', '/');

    /**
     * This is a public client feedback mechanism.
     * With each migration service returning, the client gets a ONETIMEPAD key
     * that can be used ONCE for adding a Measurement to the ToolExperience 
     * (one measurement only at the moment ;)
     */
    public boolean addExperience(long otp,String toolID, Measurement measurement) {
        return ToolRegistry.getInstance().addExperience(otp, ToolRegistry.getToolKey(toolID), measurement);
    }
    
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    

    /**
     * THIS is the real one. Currently not exposed as a web service since miniMEE
     * has been integrated with Plato.
     * @param data
     * @param toolID
     * @param params
     * @return
     */
    public MigrationResult migrate(byte[] data, String toolID, String params) {
        ToolConfig config = getToolConfig(toolID);
        ToolRegistry reg = ToolRegistry.getInstance();

        IMigrationEngine engine = reg.getAllEngines().get(config.getEngine());
        MigrationResult r= engine.migrate(data, toolID, params);

        /* evaluate result */
        evaluate(config, data, r);
        
        long key = System.nanoTime();
        r.setFeedbackKey(key);
        reg.addTimePad(key);
        return r;
    }
    
    public MigrationResult migrate(byte[] data, String toolID) {
        return migrate(data, toolID,null);
    }
    
    protected void evaluate(ToolConfig config, byte[] data, MigrationResult result) {
        if (!result.isSuccessful() || !result.getMigratedObject().isDataExistent()) {
            return;
        }
        long time = System.nanoTime();

        // get tempdir
        String tempDir = OS.getTmpPath();
       
        // prepare inputfile
        String inputFile = tempDir + "in" + time;
        // SPECIAL STUFF, UNLIKELY TO REMAIN HERE:
        if (config.getInEnding() != null && !"".equals(config.getInEnding())) {
            inputFile = inputFile+"."+config.getInEnding();
        }
        OutputStream in;
        try {
            in = new  BufferedOutputStream (new FileOutputStream(inputFile));
            in.write(data);
            in.close();
        } catch (FileNotFoundException e) {
            log.error("Failed to prepare inputfile " + inputFile, e);
        } catch (IOException e) {
            log.error("Failed to prepare inputfile " + inputFile, e);
        }
        // prepare outputfile 
        String outputFile = tempDir + "out" + time + ((config.getOutEnding()==null)?"":"."+config.getOutEnding());
        try {
            in = new  BufferedOutputStream (new FileOutputStream(outputFile));
            in.write(result.getMigratedObject().getData().getData());
            in.close();
        } catch (FileNotFoundException e) {
            log.error("Failed to prepare outputfile " + outputFile, e);
        } catch (IOException e) {
            log.error("Failed to prepare outputfile " + outputFile, e);
        }
        try { 
            for (String evaluatorName: config.getEvaluators()) {
                IMinimeeEvaluator evaluator = ToolRegistry.getInstance().getEvaluator(evaluatorName);
                if (evaluator == null) {
                    log.error("Error in ToolConfig, could not find evaluator: " + evaluatorName);
                } else {
                    if (evaluator != null) {
                        List<Measurement> list = evaluator.evaluate(tempDir,
                                inputFile,
                                outputFile);
                        for (Measurement m: list) {
                            result.getMeasurements().put(m.getProperty().getName(), m);
                        }
                    }
                }
            }
        } finally {
            new File(inputFile).delete();
            new File(outputFile).delete();
        }
    }
    
}
