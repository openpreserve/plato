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
/**
 * 
 */
package eu.planets_project.pp.plato.evaluation;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.pw.planning.utils.FileUtils;

/**
 * Base class for evaluators.
 * - Can loads possible measurements from a measurement description file
 *   
 * @author kraxner
 *
 */
public class EvaluatorBase implements IEvaluator {
	private static Logger log = LoggerFactory.getLogger(EvaluatorBase.class);
    
    protected MeasurementsDescriptor descriptor;
    protected String descriptorStr;
    
    /**
     * @see eu.planets_project.pp.plato.evaluation.IEvaluator#getPossibleMeasurements()
     */
    public String getPossibleMeasurements() {
        return descriptorStr;
    }
    
    /**
     * loads measurements description from the given file.
     * populates descriptor and descriptor String
     * 
     * @param filename
     * @return
     */
    protected boolean loadMeasurementsDescription(String filename) {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
            if (in != null) {
                descriptorStr = new String(FileUtils.inputStreamToBytes(in), "UTF-8");
                descriptor= new MeasurementsDescriptor();
                descriptor.addCriteria(new StringReader(descriptorStr));
                return true;
            }
        } catch (Exception e) {
            log.error("failed to load measurements description from " + filename, e);
        }
        return false;
    }

}
